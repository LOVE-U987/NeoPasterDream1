package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.WeaponTableBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.util.WorkshopMultiBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 精铸工作台方块 (Weapon Table)
 * <p>
 * 武器工坊多方块结构的激活核心（原版 WeaponTablePr0Procedure）：
 * 玩家手持"蓝图·精铸工坊"（blueprint_1）右键 → 校验 4 层 5×5 结构
 * （石砖/深板岩瓦地基 + 磨制黑石砖立柱/炼药锅/砂轮/铁砧 + 高炉×2/砖块 +
 * 泥砖台阶）→ 完整则整体清场并铺设精铸工坊群；缺蓝图/结构不完整时
 * 在快捷栏上方提示。
 */
public class WeaponTableBlock extends BaseEntityBlock {

    public static final MapCodec<WeaponTableBlock> CODEC = simpleCodec(WeaponTableBlock::new);
    /** 动画状态（动画文件仅含 "0" 空闲） */
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    /** 多方块校验布局（原版 WeaponTablePr0 的 4 层 map，null=任意方块） */
    private static final Map<Integer, List<Block>> CHECK_LAYOUT = new HashMap<>();

    /**
     * 构造精铸工作台方块
     *
     * @param properties 方块属性
     */
    public WeaponTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ANIMATION, 0)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    /**
     * 懒加载校验布局（与原版逐索引一致）
     */
    private static void initCheckLayout() {
        // 第 0 层：地基（石砖环 + 深板岩瓦中列）
        List<Block> layer0 = Arrays.asList(
                null, null, null, null, null,
                Blocks.STONE_BRICKS, Blocks.STONE_BRICKS, Blocks.STONE_BRICKS, Blocks.STONE_BRICKS, Blocks.STONE_BRICKS,
                Blocks.STONE_BRICKS, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_TILES, Blocks.DEEPSLATE_TILES, Blocks.STONE_BRICKS,
                Blocks.STONE_BRICKS, Blocks.STONE_BRICKS, Blocks.STONE_BRICKS, Blocks.STONE_BRICKS, Blocks.STONE_BRICKS,
                null, null, null, null, null);
        // 第 1 层：工位（磨制黑石砖立柱 + 工作台/炼药锅/砂轮/铁砧）
        List<Block> layer1 = new java.util.ArrayList<>(Arrays.asList(new Block[25]));
        layer1.set(5, Blocks.POLISHED_BLACKSTONE_BRICKS);
        layer1.set(6, Blocks.POLISHED_BLACKSTONE_BRICKS);
        layer1.set(8, PDBlocks.WEAPON_TABLE.get());
        layer1.set(9, Blocks.CAULDRON);
        layer1.set(10, Blocks.POLISHED_BLACKSTONE_BRICKS);
        layer1.set(11, Blocks.POLISHED_BLACKSTONE_BRICKS);
        layer1.set(15, Blocks.GRINDSTONE);
        layer1.set(19, Blocks.ANVIL);
        // 第 2 层：炉体（砖块 + 高炉×2）
        List<Block> layer2 = new java.util.ArrayList<>(Arrays.asList(new Block[25]));
        layer2.set(5, Blocks.BRICKS);
        layer2.set(6, Blocks.BLAST_FURNACE);
        layer2.set(10, Blocks.BRICKS);
        layer2.set(11, Blocks.BLAST_FURNACE);
        // 第 3 层：烟囱（泥砖台阶）
        List<Block> layer3 = new java.util.ArrayList<>(Arrays.asList(new Block[25]));
        layer3.set(5, Blocks.MUD_BRICK_SLAB);
        layer3.set(6, Blocks.MUD_BRICK_SLAB);
        layer3.set(10, Blocks.MUD_BRICK_SLAB);
        layer3.set(11, Blocks.MUD_BRICK_SLAB);
        CHECK_LAYOUT.put(0, layer0);
        CHECK_LAYOUT.put(1, layer1);
        CHECK_LAYOUT.put(2, layer2);
        CHECK_LAYOUT.put(3, layer3);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WeaponTableBlockEntity(pos, state);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        if (facing == Direction.NORTH) return box(0, 0, -2, 22, 11, 16);
        if (facing == Direction.EAST) return box(0, 0, 0, 18, 11, 22);
        if (facing == Direction.WEST) return box(-2, 0, -6, 16, 11, 16);
        return box(-6, 0, 0, 16, 11, 18);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ANIMATION, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    // ==================== 右键激活（原版 WeaponTablePr0Procedure） ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!player.getMainHandItem().is(PDItems.BLUEPRINT_1.get().asItem())) {
            player.displayClientMessage(Component.literal("缺少蓝图 请手持蓝图点击核心"), true);
            return InteractionResult.SUCCESS;
        }
        if (CHECK_LAYOUT.isEmpty()) {
            initCheckLayout();
        }
        boolean complete = WorkshopMultiBlock.checkMultiBlock(CHECK_LAYOUT, player, level,
                new BlockPos(pos.getX(), Mth.floor(pos.getY() - 1), pos.getZ()),
                1, 3, player.getDirection(), Blocks.AIR, 4);
        if (complete) {
            WeaponWorkshopBlock.placeWorkshop(level, player, pos.getX(), pos.getY() + 1, pos.getZ());
        } else {
            player.displayClientMessage(Component.literal("多方块结构不完整"), true);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }
}
