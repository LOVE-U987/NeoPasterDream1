package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksFurniture;
import com.pasterdream.pasterdreammod.util.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 玻璃罐系列方块（ecology_glass_jar / firefly_glass_jar / light_firefly_glass_jar）
 * <p>
 * 忠实还原原版三个 MCreator 类：
 * <ul>
 *   <li>生态玻璃罐：100 tick 周期释放萤火虫粒子，无右键交互；</li>
 *   <li>萤火虫玻璃罐：20 tick 周期粒子；右键播放 bottle.fill、animation=1，
 *       8 tick 后切换到亮萤火虫玻璃罐（原 FireflyGlassJarPr1Procedure）；</li>
 *   <li>亮萤火虫玻璃罐：同上互相切换，注册属性带 light 12 / 泛光渲染。</li>
 * </ul>
 * FACING + WATERLOGGED + ANIMATION 属性、形状 (4,0,4,12,11,12)。
 */
public class GlassJarBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final MapCodec<GlassJarBlock> CODEC = simpleCodec(p -> new GlassJarBlock(Kind.ECOLOGY, p));

    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /** 玻璃罐变体 */
    public enum Kind {
        /** 生态玻璃罐（100 tick 粒子，无交互） */
        ECOLOGY(100, false),
        /** 萤火虫玻璃罐（20 tick 粒子，可切换） */
        FIREFLY(20, true),
        /** 亮萤火虫玻璃罐（20 tick 粒子，可切换，发光） */
        LIGHT_FIREFLY(20, true);

        final int tickDelay;
        final boolean toggleable;

        Kind(int tickDelay, boolean toggleable) {
            this.tickDelay = tickDelay;
            this.toggleable = toggleable;
        }
    }

    private final Kind kind;

    /**
     * 构造玻璃罐方块
     *
     * @param kind       变体
     * @param properties 方块属性
     */
    public GlassJarBlock(Kind kind, Properties properties) {
        super(properties);
        this.kind = kind;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return box(4, 0, 4, 12, 11, 12);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ANIMATION, FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean water = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, water);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!drops.isEmpty()) {
            return drops;
        }
        return Collections.singletonList(new ItemStack(this));
    }

    // ==================== tick 粒子（原 FireflyGlassJarPr0Procedure） ====================

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        level.scheduleTick(pos, this, kind.tickDelay);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        level.sendParticles(PDParticles.FIREFLY_GLASS_JAR_PARTICLE.holder().get(),
                pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5, 2, 0.1, 0.15, 0.1, 0.01);
        level.scheduleTick(pos, this, kind.tickDelay);
    }

    // ==================== 右键切换（原 FireflyGlassJarPr1Procedure） ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!kind.toggleable) {
            return super.useWithoutItem(state, level, pos, player, hitResult);
        }
        setAnimation(level, pos, 1);
        if (!level.isClientSide()) {
            level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1, 1);
        }
        ServerScheduler.schedule(8, () -> {
            Block target = level.getBlockState(pos).getBlock() == PDBlocksFurniture.FIREFLY_GLASS_JAR.get()
                    ? PDBlocksFurniture.LIGHT_FIREFLY_GLASS_JAR.get()
                    : PDBlocksFurniture.FIREFLY_GLASS_JAR.get();
            replaceKeepingProperties(level, pos, target);
        });
        return InteractionResult.SUCCESS;
    }

    /** 设置 animation 属性值 */
    private static void setAnimation(Level level, BlockPos pos, int value) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty prop
                && prop.getPossibleValues().contains(value)) {
            level.setBlock(pos, state.setValue(prop, value), 3);
        }
    }

    /** 替换方块并保留共有属性（原 procedure 的属性拷贝块） */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void replaceKeepingProperties(Level level, BlockPos pos, Block next) {
        BlockState newState = next.defaultBlockState();
        BlockState oldState = level.getBlockState(pos);
        for (Map.Entry<Property<?>, Comparable<?>> entry : oldState.getValues().entrySet()) {
            Property property = newState.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
            if (property != null && newState.getValue(property) != null) {
                try {
                    newState = newState.setValue(property, (Comparable) entry.getValue());
                } catch (Exception ignored) {
                }
            }
        }
        level.setBlock(pos, newState, 3);
    }

    // ==================== 方块实体 ====================

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new W4GeoDataBlockEntity(switch (kind) {
            case ECOLOGY -> PDBlockEntitiesFurniture.ECOLOGY_GLASS_JAR.get();
            case FIREFLY -> PDBlockEntitiesFurniture.FIREFLY_GLASS_JAR.get();
            case LIGHT_FIREFLY -> PDBlockEntitiesFurniture.LIGHT_FIREFLY_GLASS_JAR.get();
        }, pos, state);
    }
}
