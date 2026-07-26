package com.pasterdream.pasterdreammod.worldgen.treedecorator;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.registry.PDTreeDecorators;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

/**
 * 染梦树 0 号树叶装饰器（biome_dyedream_0_tree_leave_decorator）
 * <p>
 * 还原自原版 {@code net.pasterdream.world.features.treedecorators.BiomeDyedream0LeaveDecorator}，
 * 供 {@code data/pasterdream/worldgen/configured_feature/biome_dyedream_0_tree.json} 引用。
 * <p>
 * 行为与原版逐行一致：遍历树冠上的每一个树叶方块，在东/西/南/北四个水平方向上
 * 各以 25% 概率（{@code random.nextFloat() < 0.25f}）检查相邻位置，若为空气则从该处
 * 向下延伸最多 5 格（起点 1 格 + 下垂 4 格，遇非空气即停）写入空气方块
 * （{@code minecraft:air}）。原版作者以空气替代了垂蔓写入，等效于保留装饰器挂载点但
 * 不产生可见装饰，此处保持完全一致以确保世界生成结果与原版相同。
 * <p>
 * 1.20.1 → 1.21.1 迁移说明：原版继承 {@code LeaveVineDecorator}（super(0.25f)）并整体
 * 覆写 place，此处直接继承 {@link TreeDecorator}，行为等价；Codec 由 {@code Codec.unit}
 * 迁移为 {@link MapCodec#unit}（1.21.1 的 {@link TreeDecoratorType} 构造需要 MapCodec）。
 */
public class BiomeDyedream0LeaveDecorator extends TreeDecorator {

    /** 无参装饰器编解码器（对照原版 Codec.unit，迁移为 MapCodec.unit） */
    public static final MapCodec<BiomeDyedream0LeaveDecorator> CODEC =
            MapCodec.unit(BiomeDyedream0LeaveDecorator::new);

    /** 每个方向尝试装饰的概率（对照原版构造 super(0.25f) 及 place 内硬编码的 0.25f） */
    private static final float PROBABILITY = 0.25f;

    @Override
    protected TreeDecoratorType<?> type() {
        return PDTreeDecorators.BIOME_DYEDREAM_0_TREE_LEAVE_DECORATOR.get();
    }

    @Override
    public void place(TreeDecorator.Context context) {
        // 与原版逐行对照：每个树叶方块的四个水平方向各以 25% 概率尝试悬挂装饰
        context.leaves().forEach(blockpos -> {
            if (context.random().nextFloat() < PROBABILITY) {
                BlockPos pos = blockpos.west();
                if (context.isAir(pos)) {
                    addVine(pos, context);
                }
            }
            if (context.random().nextFloat() < PROBABILITY) {
                BlockPos pos = blockpos.east();
                if (context.isAir(pos)) {
                    addVine(pos, context);
                }
            }
            if (context.random().nextFloat() < PROBABILITY) {
                BlockPos pos = blockpos.north();
                if (context.isAir(pos)) {
                    addVine(pos, context);
                }
            }
            if (context.random().nextFloat() < PROBABILITY) {
                BlockPos pos = blockpos.south();
                if (context.isAir(pos)) {
                    addVine(pos, context);
                }
            }
        });
    }

    /**
     * 在指定位置写入装饰方块并向下延伸最多 4 格（与原版 addVine 完全一致）
     *
     * @param pos     起始位置（树叶的水平相邻空气位置）
     * @param context 树装饰器上下文
     */
    private static void addVine(BlockPos pos, TreeDecorator.Context context) {
        context.setBlock(pos, Blocks.AIR.defaultBlockState());
        int i = 4;
        for (BlockPos blockpos = pos.below(); context.isAir(blockpos) && i > 0; --i) {
            context.setBlock(blockpos, Blocks.AIR.defaultBlockState());
            blockpos = blockpos.below();
        }
    }
}
