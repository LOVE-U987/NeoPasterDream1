package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.worldgen.decor.DecorationBuilder;
import com.pasterdream.pasterdreammod.worldgen.decor.DecorationRegistry;
import com.pasterdream.pasterdreammod.worldgen.decor.DecorationType;
import com.pasterdream.pasterdreammod.worldgen.feature.IceArchGenerator;
import com.pasterdream.pasterdreammod.worldgen.feature.CloudBubbleGenerator;
import com.pasterdream.pasterdreammod.worldgen.feature.FloatingIceGenerator;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.tags.BlockTags;

/**
 * 海洋装饰物注册 —— 染梦海洋群系（biome_dyedream_3）的所有装饰物
 * <p>
 * 包含珊瑚礁、水下冰刺、浮冰、冰拱门、云泡泡等。
 * 由 {@link ModDecorations#register()} 统一调用，
 * 从原 {@code ModDecorations.java} 拆分以提高可维护性。
 */
public class OceanDecorations {

    /**
     * 注册珊瑚礁装饰物
     * <p>
     * 使用 API 内置的 AQUATIC 类型，在染梦海洋群系的海底生成群落状珊瑚礁。
     */
    public static void registerCoralReef() {
        SimpleWeightedRandomList<BlockState> coralBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.TUBE_CORAL_BLOCK.defaultBlockState(), 1)
                .add(Blocks.BRAIN_CORAL_BLOCK.defaultBlockState(), 1)
                .add(Blocks.BUBBLE_CORAL_BLOCK.defaultBlockState(), 1)
                .add(Blocks.FIRE_CORAL_BLOCK.defaultBlockState(), 1)
                .add(Blocks.HORN_CORAL_BLOCK.defaultBlockState(), 1)
                .build();

        SimpleWeightedRandomList<BlockState> coralFanList = SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.TUBE_CORAL_FAN.defaultBlockState(), 2)
                .add(Blocks.BRAIN_CORAL_FAN.defaultBlockState(), 2)
                .add(Blocks.BUBBLE_CORAL_FAN.defaultBlockState(), 2)
                .add(Blocks.FIRE_CORAL_FAN.defaultBlockState(), 2)
                .add(Blocks.HORN_CORAL_FAN.defaultBlockState(), 2)
                .add(Blocks.SEA_PICKLE.defaultBlockState(), 1)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.AQUATIC)
                .body(new WeightedStateProvider(coralBodyList))
                .crystal(0.2f, new WeightedStateProvider(coralFanList))
                .debris(new WeightedStateProvider(coralFanList), 12, 5)
                .height(1, 3)
                .width(3, 2)
                .waterRequired(true)
                .checkHang(false)
                .occupiedCheck(false)
                .biome("pasterdream:biome_dyedream_3")
                .rarity(1)
                .step(GenerationStep.Decoration.VEGETAL_DECORATION)
                .register("patch_coral_reef");
    }

    /**
     * 注册粉红梦幻珊瑚丛 —— 小型密集变种
     * <p>
     * 使用染梦粉色史莱姆块为主体，搭配粉色蘑菇和珊瑚扇装饰。
     */
    public static void registerCoralReefPink() {
        SimpleWeightedRandomList<BlockState> pinkBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.PINKSLIME_BLOCK.get().defaultBlockState(), 3)
                .add(Blocks.TUBE_CORAL_BLOCK.defaultBlockState(), 1)
                .add(Blocks.BRAIN_CORAL_BLOCK.defaultBlockState(), 1)
                .build();

        SimpleWeightedRandomList<BlockState> pinkDecorList = SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.BRAIN_CORAL_FAN.defaultBlockState(), 2)
                .add(PDBlocks.PINKAGARIC_0.get().defaultBlockState(), 2)
                .add(PDBlocks.PINKAGARIC_1.get().defaultBlockState(), 2)
                .add(PDBlocks.PINKAGARIC_3.get().defaultBlockState(), 1)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.AQUATIC)
                .body(new WeightedStateProvider(pinkBodyList))
                .crystal(0.3f, new WeightedStateProvider(pinkDecorList))
                .debris(new WeightedStateProvider(pinkDecorList), 8, 4)
                .height(1, 2)
                .width(2, 1)
                .waterRequired(true)
                .checkHang(false)
                .occupiedCheck(false)
                .biome("pasterdream:biome_dyedream_3")
                .rarity(1)
                .step(GenerationStep.Decoration.VEGETAL_DECORATION)
                .register("patch_coral_reef_pink");
    }

    /**
     * 注册水下冰刺装饰物 —— 高大倾斜冰尖刺
     * <p>
     * 在寒冷海洋（biome_dyedream_3）海床向上生长巨型倾斜冰刺，
     * 高度 30~60 格，可穿破水面。
     */
    public static void registerUnderwaterIceSpike() {
        SimpleWeightedRandomList<BlockState> bodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.ICE.defaultBlockState(), 50)
                .add(Blocks.PACKED_ICE.defaultBlockState(), 30)
                .add(Blocks.BLUE_ICE.defaultBlockState(), 20)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.SPIKE)
                .body(new WeightedStateProvider(bodyList))
                .crystal(0.1f, BlockStateProvider.simple(PDBlocks.ICE_BUD_0.get()))
                .height(30, 60)
                .radius(5, 1)
                .tilt(0.25f)
                .waterRequired(true)
                .checkHang(false)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.WATER, Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.SAND, Blocks.GRAVEL, PDBlocks.DYEDREAM_SAND.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .biome("pasterdream:biome_dyedream_3")
                .rarity(3)
                .step(GenerationStep.Decoration.VEGETAL_DECORATION)
                .register("underwater_ice_spike");
    }

    /**
     * 注册海冰丘装饰物
     * <p>
     * 在寒冷海洋（biome_dyedream_3）海底生成破水而出的冰山丘。
     */
    public static void registerSeaIceMound() {
        SimpleWeightedRandomList<BlockState> bodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.PACKED_ICE.defaultBlockState(), 50)
                .add(Blocks.BLUE_ICE.defaultBlockState(), 30)
                .add(Blocks.ICE.defaultBlockState(), 20)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.AQUATIC)
                .body(new WeightedStateProvider(bodyList))
                .crystal(0.15f, BlockStateProvider.simple(PDBlocks.ICE_BUD_0.get()))
                .debris(Blocks.PACKED_ICE, 6, 4)
                .height(8, 18)
                .width(4, 2)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.WATER, Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.SAND, Blocks.GRAVEL, PDBlocks.DYEDREAM_SAND.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .checkHang(true)
                .biome("pasterdream:biome_dyedream_3")
                .rarity(2)
                .step(GenerationStep.Decoration.VEGETAL_DECORATION)
                .register("sea_ice_mound");
    }

    /**
     * 注册云泡泡装饰物
     * <p>
     * 在染梦海洋海面上空生成由云朵构成的球形结构。
     */
    public static void registerCloudBubble() {
        SimpleWeightedRandomList<BlockState> cloudBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.CLOUD.get().defaultBlockState(), 75)
                .add(PDBlocks.THICK_CLOUD.get().defaultBlockState(), 20)
                .add(Blocks.WHITE_WOOL.defaultBlockState(), 5)
                .build();

        DecorationRegistry.registerCustomGenerator("cloud_bubble", new CloudBubbleGenerator());

        DecorationBuilder.create()
                .type(DecorationType.CUSTOM)
                .body(new WeightedStateProvider(cloudBodyList))
                .customGenerator("cloud_bubble")
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.CAVE_AIR, Blocks.WATER))
                .biome("pasterdream:biome_dyedream_3")
                .rarity(1)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("cloud_bubble");
    }

    /**
     * 注册浮冰结构装饰物
     * <p>
     * 在染梦海洋海面位置生成扁平碟状浮冰。
     */
    public static void registerFloatingIceMound() {
        SimpleWeightedRandomList<BlockState> bodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.DYEDREAM_PACKED_ICE.get().defaultBlockState(), 60)
                .add(PDBlocks.DYEDREAM_ICE.get().defaultBlockState(), 40)
                .build();

        DecorationRegistry.registerCustomGenerator("floating_ice_mound", new FloatingIceGenerator());

        DecorationBuilder.create()
                .type(DecorationType.CUSTOM)
                .body(new WeightedStateProvider(bodyList))
                .top(Blocks.SNOW_BLOCK)
                .customGenerator("floating_ice_mound")
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.WATER, Blocks.SNOW_BLOCK, PDBlocks.DYEDREAM_ICE.get(), PDBlocks.DYEDREAM_PACKED_ICE.get(), Blocks.SAND, Blocks.GRAVEL, PDBlocks.DYEDREAM_SAND.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .biome("pasterdream:biome_dyedream_3")
                .rarity(2)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("floating_ice_mound");
    }

    /**
     * 注册冰拱门装饰物
     * <p>
     * 在染梦海洋水底向上形成巨大的半圆拱形冰结构。
     */
    public static void registerIceArch() {
        SimpleWeightedRandomList<BlockState> pillarBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.DYEDREAM_PACKED_ICE.get().defaultBlockState(), 60)
                .add(PDBlocks.DYEDREAM_ICE.get().defaultBlockState(), 40)
                .build();

        SimpleWeightedRandomList<BlockState> topList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.DYEDREAM_PACKED_ICE.get().defaultBlockState(), 60)
                .add(PDBlocks.DYEDREAM_ICE.get().defaultBlockState(), 40)
                .build();

        DecorationRegistry.registerCustomGenerator("ice_arch", new IceArchGenerator());

        DecorationBuilder.create()
                .type(DecorationType.CUSTOM)
                .body(new WeightedStateProvider(pillarBodyList))
                .top(new WeightedStateProvider(topList))
                .customGenerator("ice_arch")
                .height(20, 35)
                .gateWidth(30, 50)
                .pillarRadius(4)
                .beamThickness(4)
                .decorationChance(0.25f)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.WATER, Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.SNOW_BLOCK, Blocks.SAND, Blocks.GRAVEL, PDBlocks.DYEDREAM_SAND.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .checkHang(false)
                .biome("pasterdream:biome_dyedream_3")
                .rarity(12)
                .step(GenerationStep.Decoration.VEGETAL_DECORATION)
                .register("ice_arch");
    }

    /**
     * 注册冰拱门变种装饰物（损坏版本）
     * <p>
     * 中间出现不同程度的损坏，形成由冰雪混合的碎冰渣。
     */
    public static void registerIceArchRuined() {
        SimpleWeightedRandomList<BlockState> pillarBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.DYEDREAM_ICE.get().defaultBlockState(), 60)
                .add(PDBlocks.DYEDREAM_PACKED_ICE.get().defaultBlockState(), 40)
                .build();

        DecorationRegistry.registerCustomGenerator("ice_arch_ruined", new IceArchGenerator());

        DecorationBuilder.create()
                .type(DecorationType.CUSTOM)
                .body(new WeightedStateProvider(pillarBodyList))
                .customGenerator("ice_arch_ruined")
                .height(15, 25)
                .gateWidth(20, 35)
                .pillarRadius(3)
                .beamThickness(3)
                .decorationChance(0.4f)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.WATER, Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.SNOW_BLOCK, Blocks.SAND, Blocks.GRAVEL, PDBlocks.DYEDREAM_SAND.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .checkHang(false)
                .biome("pasterdream:biome_dyedream_3")
                .rarity(35)
                .step(GenerationStep.Decoration.VEGETAL_DECORATION)
                .register("ice_arch_ruined");
    }

    /**
     * 注册染梦冰柱装饰物
     * <p>
     * 在染梦深海海底冒出海面的冰柱，使用染梦维度纯冰系方块。
     */
    public static void registerDyedreamIcePillar() {
        SimpleWeightedRandomList<BlockState> bodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.DYEDREAM_PACKED_ICE.get().defaultBlockState(), 55)
                .add(PDBlocks.DYEDREAM_ICE.get().defaultBlockState(), 35)
                .add(PDBlocks.ICE_BUD_0.get().defaultBlockState(), 10)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.PILLAR)
                .body(new WeightedStateProvider(bodyList))
                .top(Blocks.SNOW_BLOCK)
                .crystal(0.1f, BlockStateProvider.simple(PDBlocks.ICE_BUD_0.get()))
                .debris(PDBlocks.DYEDREAM_PACKED_ICE.get(), 8, 5)
                .height(12, 25)
                .width(3, 1)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.WATER, Blocks.SNOW_BLOCK, PDBlocks.DYEDREAM_ICE.get(), PDBlocks.DYEDREAM_PACKED_ICE.get(), Blocks.SAND, Blocks.GRAVEL, PDBlocks.DYEDREAM_SAND.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .checkHang(true)
                .biome("pasterdream:biome_dyedream_3")
                .rarity(3)
                .step(GenerationStep.Decoration.VEGETAL_DECORATION)
                .register("dyedream_ice_pillar");
    }

    /**
     * 注册海底冰晶丛装饰物
     * <p>
     * 在染梦深海海底散布小型冰晶簇。
     */
    public static void registerIceCrystalCluster() {
        SimpleWeightedRandomList<BlockState> crystalBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.ICE_BUD_0.get().defaultBlockState(), 40)
                .add(PDBlocks.DYEDREAM_ICE.get().defaultBlockState(), 35)
                .add(PDBlocks.DYEDREAM_PACKED_ICE.get().defaultBlockState(), 25)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.SCATTER)
                .body(new WeightedStateProvider(crystalBodyList))
                .clusterSize(6)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.WATER, Blocks.SAND, Blocks.GRAVEL, PDBlocks.DYEDREAM_SAND.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .checkHang(true)
                .biome("pasterdream:biome_dyedream_3")
                .rarity(2)
                .step(GenerationStep.Decoration.VEGETAL_DECORATION)
                .register("ice_crystal_cluster");
    }

    /**
     * 注册冰霜尖刺装饰物
     * <p>
     * 在染梦深海海底向上生长的小型冰尖刺，形成海底冰笋景观。
     */
    public static void registerFrostSpike() {
        SimpleWeightedRandomList<BlockState> spikeBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.DYEDREAM_PACKED_ICE.get().defaultBlockState(), 60)
                .add(PDBlocks.DYEDREAM_ICE.get().defaultBlockState(), 40)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.SPIKE)
                .body(new WeightedStateProvider(spikeBodyList))
                .top(Blocks.SNOW_BLOCK)
                .crystal(0.1f, BlockStateProvider.simple(PDBlocks.ICE_BUD_0.get()))
                .height(3, 8)
                .radius(2, 0)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.WATER, Blocks.ICE, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.SNOW_BLOCK, Blocks.SAND, Blocks.GRAVEL, PDBlocks.DYEDREAM_SAND.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .checkHang(true)
                .biome("pasterdream:biome_dyedream_3")
                .rarity(3)
                .step(GenerationStep.Decoration.VEGETAL_DECORATION)
                .register("frost_spike");
    }
}