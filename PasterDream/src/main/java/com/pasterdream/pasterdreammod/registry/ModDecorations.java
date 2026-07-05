package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.worldgen.decor.DecorationBuilder;
import com.pasterdream.pasterdreammod.worldgen.decor.DecorationRegistry;
import com.pasterdream.pasterdreammod.worldgen.decor.DecorationType;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

/**
 * 染梦装饰物注册 —— 使用 WorldDecorationAPI 注册所有多方块装饰物
 * <p>
 * 通过 {@link DecorationBuilder} 的流式 API 配置装饰物参数。
 * 所有注册会记录到 {@link DecorationRegistry} 中，
 * 用于后续的 JSON 数据文件自动生成。
 * <p>
 * 拆分结构：
 * <ul>
 *   <li>{@link IceDecorations} — 冰雪群系（biome_2）+ 方解石柱（biome_1）</li>
 *   <li>{@link OceanDecorations} — 海洋群系（biome_3）</li>
 *   <li>本文件保留跨群系装饰物（云团）</li>
 * </ul>
 */
public class ModDecorations {

    // ==================== 跨群系装饰物 ====================

    /**
     * 注册坠云密集子特征 —— 用于 random_selector 的子 feature
     * <p>
     * 对应原 CloudBlobFeature 的密集配置（cluster_size=90, radius=6），
     * 使用 API 内置的 BLOB 类型生成不规则椭球云团，
     * 开启悬空填充（fillHang）使云朵落在地面不悬空。
     */
    private static void registerCloudfallMoundDense() {
        SimpleWeightedRandomList<BlockState> cloudBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.CLOUD.get().defaultBlockState(), 80)
                .add(PDBlocks.THICK_CLOUD.get().defaultBlockState(), 15)
                .add(PDBlocks.CHISELED_DYEDREAMQUARTZ_BLOCK.get().defaultBlockState(), 5)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.BLOB)
                .body(new WeightedStateProvider(cloudBodyList))
                .clusterSize(90)
                .radius(6, 0)
                .yRadius(2)
                .irregularity(0.3f)
                .fillHang(true)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.CAVE_AIR, PDBlocks.CLOUD.get(), PDBlocks.THICK_CLOUD.get(), PDBlocks.DARK_CLOUD.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .biome("#pasterdream:is_dyedream")
                .rarity(1)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("cloudfall_mound_dense");
    }

    /**
     * 注册坠云稀疏子特征 —— 用于 random_selector 的子 feature
     * <p>
     * 对应原 CloudBlobFeature 的稀疏配置（cluster_size=55, radius=5），
     * 使用 API 内置的 BLOB 类型，不规则椭球云团的默认变体。
     */
    private static void registerCloudfallMoundSparse() {
        SimpleWeightedRandomList<BlockState> cloudBodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(PDBlocks.CLOUD.get().defaultBlockState(), 90)
                .add(PDBlocks.THICK_CLOUD.get().defaultBlockState(), 10)
                .build();

        DecorationBuilder.create()
                .type(DecorationType.BLOB)
                .body(new WeightedStateProvider(cloudBodyList))
                .clusterSize(55)
                .radius(5, 0)
                .yRadius(2)
                .irregularity(0.35f)
                .fillHang(true)
                .regionCheck(true, 0.3f)
                .replaceable(BlockPredicate.anyOf(
                    BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.CAVE_AIR, PDBlocks.CLOUD.get(), PDBlocks.THICK_CLOUD.get(), PDBlocks.DARK_CLOUD.get()),
                    BlockPredicate.matchesTag(BlockTags.REPLACEABLE)
                ))
                .biome("#pasterdream:is_dyedream")
                .rarity(1)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("cloudfall_mound_sparse");
    }

    // ==================== 调度器 ====================

    /**
     * 注册所有装饰物 —— 供模组初始化阶段调用
     * <p>
     * 依次调用各分类的注册方法：
     * <ol>
     *   <li>跨群系云团（本文件）</li>
     *   <li>平原群系装饰物（{@link DyedreamDecorations}）</li>
     *   <li>冰雪群系装饰物（{@link IceDecorations}）</li>
     *   <li>海洋群系装饰物（{@link OceanDecorations}）</li>
     * </ol>
     */
    public static void register() {
        // 跨群系云团
        registerCloudfallMoundDense();
        registerCloudfallMoundSparse();

        // 平原群系（biome_0 + biome_1 + mushroom_plains）
        DyedreamDecorations.registerDyedreamCrystalCluster();
        DyedreamDecorations.registerMeltdreamCrystalPillar();
        DyedreamDecorations.registerFloatingCloudIsland();
        DyedreamDecorations.registerCalciteCrystalGarden();
        DyedreamDecorations.registerWarmCrystalSpike();
        DyedreamDecorations.registerPinkagaricForest();

        // 冰雪群系（biome_1 + biome_2）
        IceDecorations.registerIceSpike();
        IceDecorations.registerIceGate();
        IceDecorations.registerCalcitePillar();
        IceDecorations.registerIceCrystalGarden();
        IceDecorations.registerIceCrystalSpike();
        IceDecorations.registerIcePillar();

        // 海洋群系（biome_3）—— 优化稀有度，避免视觉杂乱
        OceanDecorations.registerCoralReef();
        OceanDecorations.registerUnderwaterIceSpike();
        OceanDecorations.registerSeaIceMound();
        OceanDecorations.registerCloudBubble();
        OceanDecorations.registerFloatingIceMound();
        OceanDecorations.registerIceArch();
        OceanDecorations.registerIceArchRuined();
        OceanDecorations.registerDyedreamIcePillar();
        OceanDecorations.registerIceCrystalCluster();
        OceanDecorations.registerFrostSpike();
    }

}

