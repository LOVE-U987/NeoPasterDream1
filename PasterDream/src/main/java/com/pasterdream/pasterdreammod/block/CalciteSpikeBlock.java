package com.pasterdream.pasterdreammod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collections;
import java.util.List;

/**
 * 方解石笋（grass_5 / grass_6）。
 * <p>
 * 非植被的石笋装饰方块：使用方解石音效、镐挖掘、需正确工具，
 * 仅用镐破坏时掉落自身。继承普通 {@link Block}（脱离 FlowerBlock 的
 * 植被逻辑：不再要求种在染梦土壤上、不再受剪刀采集影响）。
 * <p>
 * 碰撞箱：贴地矮箱（默认底部 1 像素），带 XZ 随机偏移，实体可穿过
 * （配合 {@code noCollission()} 属性）——纯贴地装饰。
 */
public class CalciteSpikeBlock extends Block {

    /** 碰撞箱形状（相对未偏移位置，见 {@link #getShape}） */
    private final VoxelShape shape;

    /**
     * @param properties 方块属性（建议复制 {@link net.minecraft.world.level.block.Blocks#CALCITE}）
     * @param shape      贴地碰撞箱形状（如 {@code box(1, 0, 1, 15, 1, 15)}）
     */
    public CalciteSpikeBlock(Properties properties, VoxelShape shape) {
        super(properties);
        this.shape = shape;
    }

    /**
     * 石笋必须贴在坚固的向上表面，普通方块默认的无条件存活会造成浮空。
     *
     * @param state 小笋状态
     * @param level 所在世界
     * @param pos 小笋位置
     * @return 下方是否有坚固支撑
     */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    /**
     * 下方支撑消失时同步移除石笋，避免玩家挖掘后留下悬空装饰。
     *
     * @param state 当前状态
     * @param direction 变化邻居方向
     * @param neighborState 邻居状态
     * @param level 所在世界
     * @param pos 小笋位置
     * @param neighborPos 邻居位置
     * @return 更新后的状态
     */
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return direction == Direction.DOWN && !state.canSurvive(level, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    /**
     * 返回贴地矮碰撞箱（并随 XZ 随机偏移），避免默认的整高立方体框。
     *
     * @param state   方块状态
     * @param level   所在世界
     * @param pos     方块位置
     * @param context 碰撞上下文
     * @return 移动了 XZ 偏移的贴地形状
     */
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Vec3 offset = state.getOffset(level, pos);
        return this.shape.move(offset.x, offset.y, offset.z);
    }

    /**
     * 仅当使用镐类工具破坏时掉落自身；空手或其他工具破坏不掉落。
     * <p>
     * 配合方块属性 {@code requiresCorrectToolForDrops()} + {@code mineable/pickaxe} 标签实现
     * "需正确工具"的掉落拦截。
     *
     * @param state  被破坏时的方块状态
     * @param params 战利品参数（含挖掘工具 {@link LootContextParams#TOOL}）
     * @return 掉落物列表
     */
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        ItemStack tool = params.getParameter(LootContextParams.TOOL);
        if (tool != null && !tool.isEmpty() && tool.is(ItemTags.PICKAXES)) {
            return List.of(new ItemStack(this));
        }
        return Collections.emptyList();
    }
}
