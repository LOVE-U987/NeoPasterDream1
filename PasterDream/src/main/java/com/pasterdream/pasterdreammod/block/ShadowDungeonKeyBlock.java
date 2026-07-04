package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * 暗影地牢钥匙方块 (Shadow Dungeon Key Block)
 * 支持水平朝向放置，破坏后掉落暗影地牢钥匙物品（非方块自身）
 * key_0 为墙挂式（碰撞箱随朝向变化），key_1 为地置式（碰撞箱固定扁平）
 */
public class ShadowDungeonKeyBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<ShadowDungeonKeyBlock> CODEC = simpleCodec(properties -> new ShadowDungeonKeyBlock(properties, false));

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    /** 水平朝向属性 */
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    /** 是否为墙挂式（true=墙挂式，false=地置式） */
    private final boolean wallMounted;

    /** 墙挂式碰撞箱 - 各朝向 */
    private static final VoxelShape SHAPE_WALL_SOUTH = Block.box(2, 2, 0, 14, 14, 1);
    private static final VoxelShape SHAPE_WALL_NORTH = Block.box(2, 2, 15, 14, 14, 16);
    private static final VoxelShape SHAPE_WALL_EAST = Block.box(0, 2, 2, 1, 14, 14);
    private static final VoxelShape SHAPE_WALL_WEST = Block.box(15, 2, 2, 16, 14, 14);

    /** 地置式碰撞箱（所有朝向相同，1像素高） */
    private static final VoxelShape SHAPE_FLOOR = Block.box(2, 0, 2, 14, 1, 14);

    /**
     * 构造暗影地牢钥匙方块
     * @param properties 方块属性
     * @param wallMounted true=墙挂式（碰撞箱随朝向变化），false=地置式（碰撞箱固定扁平）
     */
    public ShadowDungeonKeyBlock(BlockBehaviour.Properties properties, boolean wallMounted) {
        super(properties);
        this.wallMounted = wallMounted;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    /**
     * 创建方块状态定义，注册 FACING 属性
     * @param builder 状态构建器
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    /**
     * 获取放置时的方块状态（朝向玩家反向）
     * @param context 放置上下文
     * @return 方块状态
     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    /**
     * 旋转方块状态
     * @param state 当前状态
     * @param rot 旋转方式
     * @return 旋转后的状态
     */
    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    /**
     * 镜像方块状态
     * @param state 当前状态
     * @param mirrorIn 镜像方式
     * @return 镜像后的状态
     */
    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    /**
     * 获取碰撞箱形状（墙挂式随朝向变化，地置式固定）
     * @param state 方块状态
     * @param level 世界读取器
     * @param pos 方块位置
     * @param context 碰撞上下文
     * @return 碰撞箱形状
     */
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                         @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (!wallMounted) {
            return SHAPE_FLOOR;
        }
        return switch (state.getValue(FACING)) {
            case NORTH -> SHAPE_WALL_NORTH;
            case EAST -> SHAPE_WALL_EAST;
            case WEST -> SHAPE_WALL_WEST;
            default -> SHAPE_WALL_SOUTH;
        };
    }

    /**
     * 获取掉落物 - 返回空气（不掉落方块自身，实际掉落由战利品表控制）
     * 注意：原模组中掉落 SHADOW_DUNGEON_KEY 物品，此处通过战利品表实现
     * @param state 方块状态
     * @param builder 掉落参数构建器
     * @return 掉落物列表（空列表，由战利品表接管）
     */
    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootParams.Builder builder) {
        return Collections.emptyList();
    }
}
