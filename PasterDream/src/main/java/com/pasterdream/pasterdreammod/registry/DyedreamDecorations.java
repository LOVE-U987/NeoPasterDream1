package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.worldgen.decor.DecorationBuilder;
import com.pasterdream.pasterdreammod.worldgen.decor.DecorationType;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

/**
 * 染梦平原群系装饰物注册 —— biome_dyedream_0（平原）和 biome_dyedream_1（温暖平原）
 * <p>
 * 包含水晶簇、融梦灯柱、方解石水晶花园、暖水晶山脉等特色装饰物。
 * 由 {@link ModDecorations#register()} 统一调用。
 */
public class DyedreamDecorations {

    /**
     * 注册染梦水晶簇装饰物 —— biome_dyedream_0 地表散布的发光水晶
     * <p>
     * 使用 SCATTER 类型，在染梦平原地表散布融梦水晶灯和染梦花蕾，
     * 形成闪烁发光的水晶点缀效果。
     */
    public static void registerDyedreamCrystalCluster() {
        SimpleWeightedRandomList<BlockState> crystalBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.MELTDREAM_CRYSTAL_LAMP.get().defaultBlockState(), 40)
                .add(PDBlocks.DYEDREAM_BUD_0.get().defaultBlockState(), 30)
                .add(PDBlocks.DYEDREAM_BUD_1.get().defaultBlockState(), 20)
                .add(PDBlocks.DYEDREAM_BUD_2.get().defaultBlockState(), 10)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.SCATTER)
                .body(new WeightedStateProvider(crystalBodyList))
                .clusterSize(5)
                .checkHang(true)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.CAVE_AIR, PDBlocks.DYEDREAM_GRASS.get(), PDBlocks.DYEDREAM_DIRT.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .biome("pasterdream:biome_dyedream_0")
                .rarity(2)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("dyedream_crystal_cluster");
    }

    /**
     * 注册融梦水晶灯柱装饰物 —— biome_dyedream_0 的高大发光灯柱
     * <p>
     * 使用 PILLAR 类型，由染梦石英方块构成柱体，顶部放置融梦水晶灯，
     * 在平原上形成神秘的发光地标。
     */
    public static void registerMeltdreamCrystalPillar() {
        DecorationBuilder.create()
                .type(DecorationType.PILLAR)
                .body(PDBlocks.DYEDREAMQUARTZ_BLOCK.get())
                .top(PDBlocks.MELTDREAM_CRYSTAL_LAMP.get())
                .crystal(0.2f, BlockStateProvider.simple(PDBlocks.DYEDREAM_BUD_0.get()))
                .debris(PDBlocks.DYEDREAMQUARTZ_BLOCK.get(), 4, 2)
                .height(6, 12)
                .width(2, 1)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.CAVE_AIR, PDBlocks.DYEDREAM_GRASS.get(), PDBlocks.DYEDREAM_DIRT.get(), PDBlocks.DYEDREAM_BLOCK.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .checkHang(true)
                .biome("pasterdream:biome_dyedream_0")
                .rarity(5)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("meltdream_crystal_pillar");
    }

    /**
     * 注册浮空云岛装饰物 —— biome_dyedream_0 的浮空结构
     * <p>
     * 使用 BLOB 类型，由云朵方块构成浮空岛屿，底部挂着融梦水晶灯，
     * 形成神秘的空中漂浮景观。
     */
    public static void registerFloatingCloudIsland() {
        SimpleWeightedRandomList<BlockState> cloudBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.CLOUD.get().defaultBlockState(), 70)
                .add(PDBlocks.THICK_CLOUD.get().defaultBlockState(), 25)
                .add(PDBlocks.MELTDREAM_CRYSTAL_LAMP.get().defaultBlockState(), 5)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.BLOB)
                .body(new WeightedStateProvider(cloudBodyList))
                .clusterSize(80)
                .radius(5, 0)
                .yRadius(2)
                .irregularity(0.25f)
                .fillHang(false)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.CAVE_AIR, PDBlocks.CLOUD.get(), PDBlocks.THICK_CLOUD.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .biome("pasterdream:biome_dyedream_0")
                .rarity(4)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("floating_cloud_island");
    }

    /**
     * 注册方解石水晶花园装饰物 —— biome_dyedream_1 的温暖水晶景观
     * <p>
     * 使用 SCATTER 类型，在温暖平原地表散布方解石、粉色蘑菇和染梦花蕾，
     * 形成温暖梦幻的水晶花园效果。
     */
    public static void registerCalciteCrystalGarden() {
        SimpleWeightedRandomList<BlockState> gardenBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.CALCITE.defaultBlockState(), 35)
                .add(PDBlocks.POLISHED_CALCITE.get().defaultBlockState(), 25)
                .add(PDBlocks.PINKAGARIC_0.get().defaultBlockState(), 20)
                .add(PDBlocks.PINKAGARIC_1.get().defaultBlockState(), 15)
                .add(PDBlocks.DYEDREAM_BUD_0.get().defaultBlockState(), 5)
                .build();

        SimpleWeightedRandomList<BlockState> crystalList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.MELTDREAM_CRYSTAL_LAMP.get().defaultBlockState(), 60)
                .add(PDBlocks.DYEDREAM_BUD_1.get().defaultBlockState(), 25)
                .add(PDBlocks.DYEDREAM_BUD_2.get().defaultBlockState(), 15)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.SCATTER)
                .body(new WeightedStateProvider(gardenBodyList))
                .crystal(0.15f, new WeightedStateProvider(crystalList))
                .clusterSize(8)
                .checkHang(true)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.CAVE_AIR, PDBlocks.DYEDREAM_GRASS.get(), PDBlocks.DYEDREAM_DIRT.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .biome("pasterdream:biome_dyedream_1")
                .rarity(2)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("calcite_crystal_garden");
    }

    /**
     * 注册暖水晶山脉装饰物 —— biome_dyedream_1 的尖刺水晶山
     * <p>
     * 使用 SPIKE 类型，由方解石和染梦石英构成高大的锥形水晶山脉，
     * 在温暖平原上形成壮观的地标。
     */
    public static void registerWarmCrystalSpike() {
        SimpleWeightedRandomList<BlockState> spikeBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.CALCITE.defaultBlockState(), 50)
                .add(PDBlocks.DYEDREAMQUARTZ_BLOCK.get().defaultBlockState(), 35)
                .add(PDBlocks.SMOOTH_DYEDREAMQUARTZ_BLOCK.get().defaultBlockState(), 15)
                .build();

        SimpleWeightedRandomList<BlockState> crystalList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.DYEDREAM_BUD_0.get().defaultBlockState(), 40)
                .add(PDBlocks.DYEDREAM_BUD_1.get().defaultBlockState(), 30)
                .add(PDBlocks.PINKAGARIC_3.get().defaultBlockState(), 30)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.SPIKE)
                .body(new WeightedStateProvider(spikeBodyList))
                .top(PDBlocks.MELTDREAM_CRYSTAL_LAMP.get())
                .crystal(0.25f, new WeightedStateProvider(crystalList))
                .height(10, 20)
                .radius(3, 0)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.CAVE_AIR, Blocks.CALCITE, PDBlocks.DYEDREAM_GRASS.get(), PDBlocks.DYEDREAM_DIRT.get(), PDBlocks.DYEDREAM_BLOCK.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .checkHang(true)
                .biome("pasterdream:biome_dyedream_1")
                .rarity(4)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("warm_crystal_spike");
    }

    /**
     * 注册粉丁菇森林装饰物 —— biome_dyedream_mushroom_plains 的蘑菇景观
     * <p>
     * 使用 SCATTER 类型，在蘑菇平原上密集散布各种粉色蘑菇变种，
     * 形成梦幻的蘑菇森林效果。
     */
    public static void registerPinkagaricForest() {
        SimpleWeightedRandomList<BlockState> mushroomBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.PINKAGARIC_0.get().defaultBlockState(), 30)
                .add(PDBlocks.PINKAGARIC_1.get().defaultBlockState(), 25)
                .add(PDBlocks.PINKAGARIC_2.get().defaultBlockState(), 20)
                .add(PDBlocks.PINKAGARIC_3.get().defaultBlockState(), 15)
                .add(PDBlocks.DYEDREAM_BUD_0.get().defaultBlockState(), 10)
                .build();

        SimpleWeightedRandomList<BlockState> crystalList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.MELTDREAM_CRYSTAL_LAMP.get().defaultBlockState(), 50)
                .add(PDBlocks.DYEDREAM_BUD_1.get().defaultBlockState(), 30)
                .add(PDBlocks.DYEDREAM_BUD_2.get().defaultBlockState(), 20)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.SCATTER)
                .body(new WeightedStateProvider(mushroomBodyList))
                .crystal(0.12f, new WeightedStateProvider(crystalList))
                .clusterSize(10)
                .checkHang(true)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.CAVE_AIR, PDBlocks.DYEDREAM_GRASS.get(), PDBlocks.DYEDREAM_DIRT.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .biome("pasterdream:biome_dyedream_mushroom_plains")
                .rarity(1)
                .step(GenerationStep.Decoration.VEGETAL_DECORATION)
                .register("pinkagaric_forest");
    }
}
