package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.worldgen.decor.DecorationBuilder;
import com.pasterdream.pasterdreammod.worldgen.decor.DecorationRegistry;
import com.pasterdream.pasterdreammod.worldgen.decor.DecorationType;
import com.pasterdream.pasterdreammod.worldgen.feature.IceGateGenerator;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.tags.BlockTags;

/**
 * 冰雪装饰物注册 —— 染梦冰雪群系（biome_dyedream_2）和方解石柱（biome_1）
 * <p>
 * 由 {@link ModDecorations#register()} 统一调用，
 * 从原 {@code ModDecorations.java} 拆分以提高可维护性。
 */
public class IceDecorations {

    /**
     * 注册冰刺装饰物
     * <p>
     * 对应原 IceSpikeFeature + IceSpikeConfiguration，
     * 使用 API 内置的 SPIKE 类型，圆形截面锥形尖刺，
     * 开启悬空检测（checkHang）和区域重叠检测（regionCheck）。
     */
    public static void registerIceSpike() {
        SimpleWeightedRandomList<BlockState> iceBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.ICE.defaultBlockState(), 50)
                .add(Blocks.PACKED_ICE.defaultBlockState(), 35)
                .add(Blocks.BLUE_ICE.defaultBlockState(), 15)
                .build();

        SimpleWeightedRandomList<BlockState> oreList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.DYEDREAMQUARTZ_ORE.get().defaultBlockState(), 1)
                .add(PDBlocks.ICE_BUD_0.get().defaultBlockState(), 1)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.SPIKE)
                .body(new WeightedStateProvider(iceBodyList))
                .top(Blocks.SNOW_BLOCK)
                .crystal(0.2f, new WeightedStateProvider(oreList))
                .height(5, 14)
                .radius(3, 0)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.SNOW_BLOCK, Blocks.STONE, Blocks.DIRT, Blocks.GRASS_BLOCK, PDBlocks.DYEDREAM_ICE.get(), PDBlocks.DYEDREAM_PACKED_ICE.get(), PDBlocks.ICESTONE.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .checkHang(true)
                .biome("pasterdream:biome_dyedream_2")
                .rarity(2)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("ice_spike");
    }

    /**
     * 注册冰之门装饰物（完整+倒塌变种合并）
     * <p>
     * 使用 CUSTOM 类型 + IceGateGenerator 自定义生成逻辑。
     * 当两根支柱都能找到坚实地面时 → 完整的双柱+横梁冰门；
     * 当只有一根支柱能找到坚实地面时 → 倒塌变种（倾斜版或断柱版）。
     */
    public static void registerIceGate() {
        SimpleWeightedRandomList<BlockState> pillarBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.PACKED_ICE.defaultBlockState(), 40)
                .add(Blocks.ICE.defaultBlockState(), 35)
                .add(Blocks.BLUE_ICE.defaultBlockState(), 25)
                .build();

        SimpleWeightedRandomList<BlockState> topList = SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.SNOW_BLOCK.defaultBlockState(), 70)
                .add(Blocks.PACKED_ICE.defaultBlockState(), 30)
                .build();

        DecorationRegistry.registerCustomGenerator("ice_gate", new IceGateGenerator());

        DecorationBuilder.create()
                .type(DecorationType.CUSTOM)
                .body(new WeightedStateProvider(pillarBodyList))
                .top(new WeightedStateProvider(topList))
                .customGenerator("ice_gate")
                .height(20, 40)
                .gateWidth(5, 12)
                .pillarRadius(3)
                .beamThickness(3)
                .decorationChance(0.3f)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.SNOW_BLOCK, Blocks.STONE, Blocks.DIRT, Blocks.GRASS_BLOCK, PDBlocks.DYEDREAM_ICE.get(), PDBlocks.DYEDREAM_PACKED_ICE.get(), PDBlocks.ICESTONE.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .checkHang(false)
                .biome("pasterdream:biome_dyedream_2")
                .rarity(5)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("ice_gate");
    }

    /**
     * 注册方解石之柱装饰物
     * <p>
     * 对应原 CalcitePillarFeature + CalcitePillarConfiguration，
     * 使用 API 内置的 PILLAR 类型，方形截面锥形柱体。
     */
    public static void registerCalcitePillar() {
        SimpleWeightedRandomList<BlockState> crystalList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.DYEDREAMQUARTZ_ORE.get().defaultBlockState(), 30)
                .add(PDBlocks.DYEDREAMDUST_ORE.get().defaultBlockState(), 20)
                .add(PDBlocks.AMBER_CANDY_ORE.get().defaultBlockState(), 15)
                .add(PDBlocks.DYEDREAM_BUD_0.get().defaultBlockState(), 10)
                .add(PDBlocks.DYEDREAM_BUD_1.get().defaultBlockState(), 10)
                .add(PDBlocks.DYEDREAM_BUD_2.get().defaultBlockState(), 10)
                .add(Blocks.CALCITE.defaultBlockState(), 5)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.PILLAR)
                .body(Blocks.CALCITE)
                .crystal(0.3f, new WeightedStateProvider(crystalList))
                .debris(Blocks.CALCITE, 6, 3)
                .height(15, 20)
                .width(2, 1)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.CALCITE, Blocks.STONE, Blocks.DIRT, Blocks.GRASS_BLOCK, PDBlocks.DYEDREAM_BLOCK.get(), PDBlocks.DYEDREAM_DIRT.get(), PDBlocks.DYEDREAM_GRASS.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .checkHang(true)
                .biome("pasterdream:biome_dyedream_1")
                .rarity(3)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("calcite_pillar");
    }

    /**
     * 注册冰晶花园装饰物
     * <p>
     * 在寒冷染梦（biome_dyedream_2）地表散布冰晶和冰蕾。
     */
    public static void registerIceCrystalGarden() {
        SimpleWeightedRandomList<BlockState> iceCrystalBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.DYEDREAM_ICE.get().defaultBlockState(), 35)
                .add(PDBlocks.DYEDREAM_PACKED_ICE.get().defaultBlockState(), 25)
                .add(PDBlocks.ICESTONE.get().defaultBlockState(), 20)
                .add(PDBlocks.ICE_BUD_0.get().defaultBlockState(), 10)
                .add(Blocks.PACKED_ICE.defaultBlockState(), 5)
                .add(Blocks.ICE.defaultBlockState(), 5)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.SCATTER)
                .body(new WeightedStateProvider(iceCrystalBodyList))
                .clusterSize(8)
                .checkHang(true)
                .biome("pasterdream:biome_dyedream_2")
                .rarity(2)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("ice_crystal_garden");
    }

    /**
     * 注册冰晶丛装饰物
     * <p>
     * 在染梦冰雪（biome_dyedream_2）地表生成小型冰晶尖刺丛。
     */
    public static void registerIceCrystalSpike() {
        SimpleWeightedRandomList<BlockState> bodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.ICE.defaultBlockState(), 50)
                .add(Blocks.PACKED_ICE.defaultBlockState(), 30)
                .add(Blocks.BLUE_ICE.defaultBlockState(), 10)
                .add(PDBlocks.DYEDREAM_ICE.get().defaultBlockState(), 10)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.SPIKE)
                .body(new WeightedStateProvider(bodyList))
                .crystal(0.1f, BlockStateProvider.simple(PDBlocks.ICE_BUD_0.get()))
                .height(3, 6)
                .radius(2, 0)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.SNOW_BLOCK, Blocks.STONE, Blocks.DIRT, Blocks.GRASS_BLOCK, PDBlocks.DYEDREAM_ICE.get(), PDBlocks.DYEDREAM_PACKED_ICE.get(), PDBlocks.ICESTONE.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .checkHang(true)
                .biome("pasterdream:biome_dyedream_2")
                .rarity(1)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("ice_crystal_spike");
    }

    /**
     * 注册冰柱装饰物
     * <p>
     * 在染梦冰雪（biome_dyedream_2）地表生成方形截面的高大冰柱。
     */
    public static void registerIcePillar() {
        SimpleWeightedRandomList<BlockState> bodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.PACKED_ICE.defaultBlockState(), 40)
                .add(Blocks.ICE.defaultBlockState(), 30)
                .add(Blocks.BLUE_ICE.defaultBlockState(), 20)
                .add(PDBlocks.DYEDREAM_PACKED_ICE.get().defaultBlockState(), 10)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.PILLAR)
                .body(new WeightedStateProvider(bodyList))
                .crystal(0.15f, BlockStateProvider.simple(PDBlocks.ICE_BUD_0.get()))
                .debris(Blocks.PACKED_ICE, 4, 3)
                .height(8, 15)
                .width(3, 2)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.SNOW_BLOCK, Blocks.STONE, Blocks.DIRT, Blocks.GRASS_BLOCK, PDBlocks.DYEDREAM_ICE.get(), PDBlocks.DYEDREAM_PACKED_ICE.get(), PDBlocks.ICESTONE.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .checkHang(true)
                .biome("pasterdream:biome_dyedream_2")
                .rarity(3)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("ice_pillar");
    }
}