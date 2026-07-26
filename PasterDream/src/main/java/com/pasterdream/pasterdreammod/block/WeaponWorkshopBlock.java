package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.WeaponWorkshopBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.util.WorkshopMultiBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 精铸工坊方块 (Weapon Workshop)
 * <p>
 * 武器工坊群核心：GeckoLib 3D 模型（整组工坊的可见外观都由本方块渲染），
 * 右键打开锻造 GUI；被玩家破坏/爆炸摧毁时连带拆除周边 7×7 内的
 * 四座卫星工位（冷却盆/锻炉/磨石/铁砧，各一次，原版 WeaponWorkshopPr2）。
 * <p>
 * {@link #placeWorkshop} 还原原版 WeaponWorkshopPr0：播放 machine0 音效并按
 * 5×5 布局在结构位铺设核心与四座卫星工位。
 */
public class WeaponWorkshopBlock extends BaseEntityBlock {

    public static final MapCodec<WeaponWorkshopBlock> CODEC = simpleCodec(WeaponWorkshopBlock::new);
    /** 动画状态（原版 0-2；动画文件含 "0" 空闲 / "1" 锻造） */
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 2);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    /** 工坊布局（5×5 展平索引 → 方块；原版 WeaponWorkshopPr0 的 map） */
    private static final Map<Integer, List<Block>> LAYOUT = new HashMap<>();

    /**
     * 构造精铸工坊方块
     *
     * @param properties 方块属性
     */
    public WeaponWorkshopBlock(Properties properties) {
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
     * 懒加载工坊布局（索引 8 核心 / 9 冷却盆 / 11 锻炉 / 15 磨石 / 19 铁砧）
     */
    private static void initLayout() {
        List<Block> layer = new ArrayList<>(Arrays.asList(new Block[25]));
        layer.set(8, PDBlocks.WEAPON_WORKSHOP.get());
        layer.set(9, PDBlocks.WORKSHOP_CAULDEON.get());
        layer.set(11, PDBlocks.WORKSHOP_BLAST.get());
        layer.set(15, PDBlocks.WORKSHOP_GRIND.get());
        layer.set(19, PDBlocks.WORKSHOP_ANVIL.get());
        LAYOUT.put(0, layer);
    }

    /**
     * 铺设整组工坊（原版 WeaponWorkshopPr0Procedure）：
     * machine0 音效 + 按玩家朝向在 (x, y-2, z) 基准铺设 5×5 布局
     *
     * @param world  世界
     * @param player 触发玩家
     * @param x      X 坐标
     * @param y      Y 坐标（调用方传入核心上方一格，与原版一致）
     * @param z      Z 坐标
     */
    public static void placeWorkshop(Level world, Player player, double x, double y, double z) {
        if (LAYOUT.isEmpty()) {
            initLayout();
        }
        if (!world.isClientSide()) {
            world.playSound(null, new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z)),
                    PDSounds.MACHINE0.get(), SoundSource.NEUTRAL, 1.0f, 1.0f);
        }
        WorkshopMultiBlock.setMultiBlock(LAYOUT, player, world,
                new BlockPos(Mth.floor(x), Mth.floor(y) - 2, Mth.floor(z)), 1, 3, player.getDirection(), 1);
    }

    /**
     * 拆除周边卫星工位（原版 WeaponWorkshopPr2Procedure）：
     * 7×7 同层扫描，四座卫星各拆一次（不掉落）
     *
     * @param world 世界
     * @param pos   核心位置
     */
    public static void destroySatellites(Level world, BlockPos pos) {
        List<Block> targets = new ArrayList<>(List.of(
                PDBlocks.WORKSHOP_CAULDEON.get(),
                PDBlocks.WORKSHOP_BLAST.get(),
                PDBlocks.WORKSHOP_GRIND.get(),
                PDBlocks.WORKSHOP_ANVIL.get()));
        for (int ox = -3; ox <= 3 && !targets.isEmpty(); ox++) {
            for (int oz = -3; oz <= 3 && !targets.isEmpty(); oz++) {
                BlockPos cell = pos.offset(ox, 0, oz);
                Block block = world.getBlockState(cell).getBlock();
                if (targets.contains(block)) {
                    world.destroyBlock(cell, false);
                    targets.remove(block);
                }
            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WeaponWorkshopBlockEntity(pos, state);
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
        // 原版逐朝向碰撞箱（含 3 格高台面与外扩底座）
        return switch (state.getValue(FACING)) {
            case NORTH -> Shapes.or(box(0, 3, -2, 22, 16, 16), box(-16, 0, -32, 64, 3, 16));
            case EAST -> Shapes.or(box(0, 3, 0, 18, 16, 22), box(0, 0, -16, 48, 3, 64));
            case WEST -> Shapes.or(box(-2, 3, -6, 16, 16, 16), box(-32, 0, -48, 16, 3, 32));
            default -> Shapes.or(box(-6, 3, 0, 16, 16, 18), box(-48, 0, 0, 32, 3, 48));
        };
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

    // ==================== 破坏联动 ====================

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player,
                                       boolean willHarvest, FluidState fluid) {
        boolean result = super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
        destroySatellites(world, pos);
        return result;
    }

    @Override
    public void wasExploded(Level world, BlockPos pos, Explosion explosion) {
        super.wasExploded(world, pos, explosion);
        destroySatellites(world, pos);
    }

    // ==================== 右键交互 ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player.isSpectator()) {
            return InteractionResult.FAIL;
        }
        if (level.getBlockEntity(pos) instanceof WeaponWorkshopBlockEntity workshop
                && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(workshop, pos);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }

    // ==================== 移除掉落 ====================

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (world.getBlockEntity(pos) instanceof WeaponWorkshopBlockEntity workshop) {
                ItemStackHandler handler = workshop.getItemHandler();
                for (int i = 0; i < handler.getSlots(); i++) {
                    Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
                }
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    // ==================== 红石比较器 ====================

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof WeaponWorkshopBlockEntity workshop) {
            return calcRedstoneFromItemHandler(workshop.getItemHandler());
        }
        return 0;
    }

    /**
     * 按 vanilla 容器公式计算比较器信号
     *
     * @param handler 物品处理器
     * @return 0-15 信号强度
     */
    static int calcRedstoneFromItemHandler(ItemStackHandler handler) {
        int filledSlots = 0;
        float fillRatio = 0.0f;
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                fillRatio += (float) stack.getCount() / Math.min(handler.getSlotLimit(slot), stack.getMaxStackSize());
                filledSlots++;
            }
        }
        fillRatio /= handler.getSlots();
        return Mth.floor(fillRatio * 14.0f) + (filledSlots > 0 ? 1 : 0);
    }
}
