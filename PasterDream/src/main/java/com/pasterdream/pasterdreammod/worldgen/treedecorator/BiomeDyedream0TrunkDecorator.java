package com.pasterdream.pasterdreammod.worldgen.treedecorator;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.registry.PDTreeDecorators;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

/**
 * 染梦树 0 号树干装饰器（biome_dyedream_0_tree_trunk_decorator）
 * <p>
 * 还原自原版 {@code net.pasterdream.world.features.treedecorators.BiomeDyedream0TrunkDecorator}，
 * 供 {@code data/pasterdream/worldgen/configured_feature/biome_dyedream_0_tree.json} 引用。
 * <p>
 * 行为与原版逐行一致：遍历树干上的每一个原木方块，在东/西/南/北四个水平方向上
 * 各以 2/3 概率（{@code random.nextInt(3) > 0}）检查相邻位置，若为空气则写入空气方块
 * （{@code minecraft:air}）。原版作者以空气替代了藤蔓写入，等效于保留装饰器挂载点但
 * 不产生可见装饰，此处保持完全一致以确保世界生成结果与原版相同。
 * <p>
 * 1.20.1 → 1.21.1 迁移说明：原版继承 {@code TrunkVineDecorator} 并整体覆写 place，
 * 此处直接继承 {@link TreeDecorator}，行为等价；Codec 由 {@code Codec.unit}
 * 迁移为 {@link MapCodec#unit}（1.21.1 的 {@link TreeDecoratorType} 构造需要 MapCodec）。
 */
public class BiomeDyedream0TrunkDecorator extends TreeDecorator {

    /** 无参装饰器编解码器（对照原版 Codec.unit，迁移为 MapCodec.unit） */
    public static final MapCodec<BiomeDyedream0TrunkDecorator> CODEC =
            MapCodec.unit(BiomeDyedream0TrunkDecorator::new);

    @Override
    protected TreeDecoratorType<?> type() {
        return PDTreeDecorators.BIOME_DYEDREAM_0_TREE_TRUNK_DECORATOR.get();
    }

    @Override
    public void place(TreeDecorator.Context context) {
        // 与原版逐行对照：每个原木方块的四个水平方向各以 2/3 概率尝试装饰
        context.logs().forEach(blockpos -> {
            if (context.random().nextInt(3) > 0) {
                BlockPos pos = blockpos.west();
                if (context.isAir(pos)) {
                    context.setBlock(pos, Blocks.AIR.defaultBlockState());
                }
            }
            if (context.random().nextInt(3) > 0) {
                BlockPos pos = blockpos.east();
                if (context.isAir(pos)) {
                    context.setBlock(pos, Blocks.AIR.defaultBlockState());
                }
            }
            if (context.random().nextInt(3) > 0) {
                BlockPos pos = blockpos.north();
                if (context.isAir(pos)) {
                    context.setBlock(pos, Blocks.AIR.defaultBlockState());
                }
            }
            if (context.random().nextInt(3) > 0) {
                BlockPos pos = blockpos.south();
                if (context.isAir(pos)) {
                    context.setBlock(pos, Blocks.AIR.defaultBlockState());
                }
            }
        });
    }
}
