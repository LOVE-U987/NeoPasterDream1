package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDDimensions;
import net.minecraft.core.BlockPos;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.Direction;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.storage.loot.LootParams;

/**
 * 染梦裂纹方块 —— 染梦维度的入口
 * <p>
 * 当玩家接触此方块时，会被传送到染梦维度（或从染梦维度返回主世界）。
 * 方块具有发光效果，作为染梦世界的标志性传送门。
 */
public class DyedreamCrackBlock extends Block implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /** 传送冷却时间（tick），防止重复传送 —— 3秒 */
    private static final int TELEPORT_COOLDOWN = 60;

    public DyedreamCrackBlock() {
        super(BlockBehaviour.Properties.of()
            .sound(SoundType.AMETHYST)
            .strength(10f)
            .lightLevel(s -> 7)
            .noCollission()
            .noOcclusion()
            .hasPostProcess((bs, br, bp) -> true)
            .emissiveRendering((bs, br, bp) -> true)
            .isRedstoneConductor((bs, br, bp) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            default -> box(-13, 3, 7, 13, 29, 9);
            case NORTH -> box(3, 3, 7, 29, 29, 9);
            case EAST -> box(7, 3, 3, 9, 29, 29);
            case WEST -> box(7, 3, -13, 9, 29, 13);
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean flag = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, flag);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    /**
     * 当实体进入方块碰撞箱时触发 —— 实现染梦维度传送
     * <p>
     * 主世界/其他维度 → 染梦维度
     * 染梦维度 → 主世界
     *
     * @param state 当前方块状态
     * @param level 当前世界
     * @param pos   方块位置
     * @param entity 进入方块的实体
     */
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        // 只在服务器端执行，且只处理玩家
        if (level.isClientSide || !(entity instanceof ServerPlayer player)) {
            return;
        }

        // 检查传送冷却时间
        if (player.getPortalCooldown() > 0) {
            return;
        }

        // 确定目标维度
        ResourceKey<Level> targetDimension;
        if (level.dimension().equals(PDDimensions.DYEDREAM_WORLD_LEVEL_KEY)) {
            // 在染梦维度 → 回主世界
            targetDimension = Level.OVERWORLD;
        } else {
            // 主世界/其他维度 → 去染梦维度
            targetDimension = PDDimensions.DYEDREAM_WORLD_LEVEL_KEY;
        }

        ServerLevel targetWorld = player.getServer().getLevel(targetDimension);
        if (targetWorld == null) {
            return;
        }

        // 查找安全传送位置
        BlockPos targetPos = findSafePosition(targetWorld, player);

        DimensionTransition transition = new DimensionTransition(
            targetWorld,
            targetPos.getCenter(),
            player.getDeltaMovement(),
            player.getYRot(),
            player.getXRot(),
            DimensionTransition.PLAY_PORTAL_SOUND
        );

        player.changeDimension(transition);

        // 设置传送冷却时间，防止重复传送
        player.setPortalCooldown(TELEPORT_COOLDOWN);
    }

    /**
     * 在目标维度查找安全的传送位置
     * <p>
     * 优先使用玩家重生点（床），若玩家未设置重生点或重生点不在目标维度，
     * 则回退到世界出生点，并从高处向下扫描找到安全地面。
     *
     * @param world  目标世界
     * @param player 传送的玩家
     * @return 安全的传送位置
     */
    private BlockPos findSafePosition(ServerLevel world, ServerPlayer player) {
        BlockPos spawnPos;

        // 优先使用玩家重生点
        if (player.getRespawnPosition() != null && player.getRespawnDimension().equals(world.dimension())) {
            spawnPos = player.getRespawnPosition();
        } else {
            spawnPos = world.getSharedSpawnPos();
        }

        BlockPos.MutableBlockPos checkPos = spawnPos.atY(world.getMaxBuildHeight() - 1).mutable();

        // 从最高处向下扫描，找到第一个非空气方块（即地面），然后在其上空 2 格处传送
        for (int y = world.getMaxBuildHeight() - 1; y > world.getMinBuildHeight(); y--) {
            checkPos.setY(y);
            if (!world.getBlockState(checkPos).isAir()) {
                return checkPos.above(2).immutable();
            }
        }

        // 兜底：使用世界出生点上空 3 格
        return spawnPos.above(3);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }
}
