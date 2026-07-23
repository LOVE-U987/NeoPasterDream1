package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.worldgen.decor.TreeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import java.util.Optional;

/**
 * 染梦树苗方块
 * <p>
 * 继承 SaplingBlock，使用已有的 dyedream_tree_selector configured feature
 * 实现随机 tick 生长和骨粉催熟功能。
 * <p>
 * 树苗有4个生长阶段（0-3），阶段3时使用骨粉会立即生长为树。
 */
public class DyedreamSaplingBlock extends SaplingBlock {

    /** 生长阶段属性，0-3 */
    public static final IntegerProperty STAGE = BlockStateProperties.STAGE;

    /**
     * 染梦树生长器 —— 使用 dyedream_tree_selector 随机选择树变体
     */
    private static final TreeGrower DYEDREAM_TREE_GROWER = new TreeGrower(
            "dyedream_tree",
            Optional.of(TreeRegistry.TREE_SELECTOR),
            Optional.empty(),
            Optional.empty()
    );

    /**
     * 构造染梦树苗方块
     */
    public DyedreamSaplingBlock() {
        super(DYEDREAM_TREE_GROWER, BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .randomTicks()
                .sound(SoundType.GRASS)
                .instabreak()
                .noCollission()
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .pushReaction(PushReaction.DESTROY));
        registerDefaultState(stateDefinition.any().setValue(STAGE, 0));
    }

    /**
     * 随机 tick —— 增长生长阶段，到达阶段3时尝试生长为树
     */
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;

        // 如果阶段小于3，有75%概率进入下一阶段
        if (state.getValue(STAGE) < 3) {
            if (random.nextInt(4) == 0) {
                level.setBlock(pos, state.setValue(STAGE, state.getValue(STAGE) + 1), Block.UPDATE_CLIENTS);
            }
        } else {
            // 阶段3时，有20%概率尝试生长为树
            if (random.nextInt(5) == 0) {
                growTree(level, pos, state, random);
            }
        }
    }

    /**
     * 检查树苗是否可以在此位置生长
     */
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(PDBlocks.DYEDREAM_GRASS.get())
            || state.is(PDBlocks.DYEDREAM_DIRT.get())
            || state.is(PDBlocks.DYEDREAM_SAND.get())
            || state.is(PDBlocks.DYEDREAM_BLOCK.get());
    }

    /**
     * 使用骨粉催熟 —— 如果阶段小于3则加速生长，否则立即生长为树
     */
    public void grow(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        if (state.getValue(STAGE) < 3) {
            level.setBlock(pos, state.setValue(STAGE, 3), Block.UPDATE_CLIENTS);
        } else {
            growTree(level, pos, state, random);
        }
    }

    /**
     * 生长为树 —— 调用父类方法生成树结构
     */
    private void growTree(ServerLevel level, BlockPos pos, BlockState state, RandomSource random) {
        DYEDREAM_TREE_GROWER.growTree(level, level.getChunkSource().getGenerator(), pos, state, random);
    }

    /**
     * 创建方块状态定义
     */
    @Override
    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }
}