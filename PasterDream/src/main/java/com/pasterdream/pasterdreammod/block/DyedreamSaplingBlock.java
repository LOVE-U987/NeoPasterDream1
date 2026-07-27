package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.api.worldgen.decor.TreeRegistry;
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
 * 骨粉催熟与自然生长均使用原版 SaplingBlock 逻辑，单株树苗即可生长为染梦树。
 */
public class DyedreamSaplingBlock extends SaplingBlock {

    /** 生长阶段属性，复用原版 SaplingBlock 的 STAGE（0-1） */
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
     * 检查树苗是否可以种植在目标方块上。
     *
     * @param state 目标方块状态
     * @param level 世界访问器
     * @param pos   目标位置
     * @return 是否允许种植
     */
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(PDBlocks.DYEDREAM_GRASS.get())
            || state.is(PDBlocks.DYEDREAM_DIRT.get())
            || state.is(PDBlocks.DYEDREAM_SAND.get())
            || state.is(PDBlocks.DYEDREAM_BLOCK.get());
    }

    /**
     * 创建方块状态定义
     */
    @Override
    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }
}
