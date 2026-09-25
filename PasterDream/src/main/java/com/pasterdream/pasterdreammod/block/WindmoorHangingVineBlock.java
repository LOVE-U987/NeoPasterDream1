package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * 风泊悬挂藤 (Windmoor Hanging Vine)
 * <p>
 * 垂钓植被，可悬挂在石质/木制/树叶方块下表面，无碰撞体积。
 * 放置后通过随机刻或骨粉向下生长 {@link FigVineBlock}。
 * 手持/背包显示为 2D 平面纹理，方块模型使用 cross 十字植物模型。
 */
public class WindmoorHangingVineBlock extends Block implements BonemealableBlock {

    /** 单悬挂片形状：底部薄片，模拟悬挂的藤叶 */
    private static final VoxelShape SHAPE = box(2, 0, 2, 14, 10, 14);

    /** 生长阶段（0=初始, 1=可生长, 2=成熟） */
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 2);

    /** 生长概率（随机刻每 tick） */
    private static final float GROW_CHANCE = 0.05f;

    /**
     * 构造风泊悬挂藤
     *
     * @param properties 方块属性
     */
    public WindmoorHangingVineBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    // ==================== 放置条件 ====================

    /**
     * 只能放置在支持方块下表面（石质/木制/树叶）
     *
     * @param state   当前方块状态
     * @param level   世界
     * @param pos     位置
     * @return 是否可以生存
     */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);
        return canHangOn(aboveState);
    }

    /**
     * 判断目标方块是否为悬挂支撑面
     * <p>
     * 支持：石质（需正确工具挖掘）、木制（原木/木板/菌柄）、树叶
     *
     * @param state 目标方块状态
     * @return 是否可作为悬挂支撑
     */
    private static boolean canHangOn(BlockState state) {
        return state.is(BlockTags.LOGS)
                || state.is(BlockTags.PLANKS)
                || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.STONE_ORE_REPLACEABLES)
                || state.is(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
    }

    /**
     * 邻居变化时检查自身能否生存
     */
    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos,
                                    Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!canSurvive(state, level, pos)) {
            level.removeBlock(pos, false);
        }
    }

    /**
     * 获取放置状态：仅在目标方向为 UP（从下方放置到上表面）时放置
     */
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getClickedFace() == Direction.DOWN) {
            BlockPos above = context.getClickedPos().above();
            if (canHangOn(context.getLevel().getBlockState(above))) {
                return this.defaultBlockState();
            }
        }
        // 点击方块下表面或尝试从下方放置
        BlockPos checkPos = context.getClickedPos();
        BlockState aboveState = context.getLevel().getBlockState(checkPos.above());
        if (canHangOn(aboveState)) {
            return this.defaultBlockState();
        }
        return null;
    }

    // ==================== 形状与视觉 ====================

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    // ==================== 生长逻辑 ====================

    /**
     * 随机刻处理：向下生长 fig_vine
     */
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (random.nextFloat() < GROW_CHANCE) {
            tryGrowDown(level, pos, state);
        }
    }

    /**
     * 尝试向下生长 fig_vine
     *
     * @param level 服务端世界
     * @param pos   当前位置
     * @param state 当前状态
     */
    private void tryGrowDown(ServerLevel level, BlockPos pos, BlockState state) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);

        // 下方必须是可替换方块（空气、草、花等）
        if (!belowState.isAir()) return;

        // 检查悬挂藤自身数量上限：避免无限垂吊性能问题
        int length = countVineLength(level, pos);
        if (length > 12) return; // 最多 12 格

        // 放置 fig_vine
        level.setBlock(below, PDBlocks.FIG_VINE.get().defaultBlockState(), 3);

        // 推进生长阶段
        int age = state.getValue(AGE);
        if (age < 2) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 3);
        }
    }

    /**
     * 计算从当前位置往下的悬挂藤链长度
     *
     * @param level 世界
     * @param pos   起始位置
     * @return 藤链总长度（含自身）
     */
    private int countVineLength(ServerLevel level, BlockPos pos) {
        int length = 0;
        BlockPos check = pos;
        while (length < 64) {
            if (level.getBlockState(check).is(this)) {
                length++;
                check = check.below();
            } else if (level.getBlockState(check).getBlock() instanceof FigVineBlock) {
                length++;
                check = check.below();
            } else {
                break;
            }
        }
        return length;
    }

    // ==================== 骨粉催长 ====================

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, net.minecraft.util.RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, net.minecraft.util.RandomSource random, BlockPos pos, BlockState state) {
        tryGrowDown(level, pos, state);
        // 骨粉高概率额外再生长一格
        if (random.nextFloat() < 0.6f) {
            BlockPos below = pos.below();
            if (level.getBlockState(below).getBlock() instanceof FigVineBlock) {
                tryGrowDown(level, below, level.getBlockState(below));
            }
        }
    }
}