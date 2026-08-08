package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.worldgen.feature.CaveGlowMushroomFeature;
import com.pasterdream.pasterdreammod.worldgen.feature.FloatingIslandFeature;
import com.pasterdream.pasterdreammod.worldgen.feature.MegaCalcitePillarFeature;
import com.pasterdream.pasterdreammod.worldgen.feature.MegaMushroomFeature;
import com.pasterdream.pasterdreammod.worldgen.feature.PinkagaricClusterFeature;
import com.pasterdream.pasterdreammod.worldgen.feature.SafeLakeFeature;
import com.pasterdream.pasterdreammod.worldgen.tree.DyedreamStructureTreeFeature;
import com.pasterdream.pasterdreammod.worldgen.tree.DyedreamTreeFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 自定义世界生成特征（Feature）注册类
 * <p>
 * 包含粉丁菇巨簇、巨型方解石云端柱、巨型粉丁菇和浮空群岛等
 * 需要精细结构控制的自定义 Feature。其他通用装饰物（冰刺、冰之门、方解石柱、坠云等）
 * 通过 WorldDecorationAPI 的 DecorationBuilder 在 ModDecorations 中注册。
 */
public class PDFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, PasterDreamMod.MOD_ID);

    /** 粉丁菇巨簇特征 —— 在染梦草原地面生成丛生的粉丁菇群 */
    public static final DeferredHolder<Feature<?>, PinkagaricClusterFeature> PINKAGARIC_CLUSTER =
            FEATURES.register("pinkagaric_cluster", PinkagaricClusterFeature::new);

    /** 巨型方解石云端柱特征 —— 染梦草原的 40~50 格擎天巨柱地标 */
    public static final DeferredHolder<Feature<?>, MegaCalcitePillarFeature> MEGA_CALCITE_PILLAR =
            FEATURES.register("mega_calcite_pillar", MegaCalcitePillarFeature::new);

    /** 巨型粉丁菇特征 —— 寒冷染梦的 40~50 格擎天巨蘑地标 */
    public static final DeferredHolder<Feature<?>, MegaMushroomFeature> MEGA_MUSHROOM =
            FEATURES.register("mega_mushroom", MegaMushroomFeature::new);

    /** 浮空群岛特征 —— 在染梦世界高空 Y=160~220 生成悬浮的椭球体岛屿群 */
    public static final DeferredHolder<Feature<?>, FloatingIslandFeature> FLOATING_ISLAND =
            FEATURES.register("floating_island", FloatingIslandFeature::new);

    // ==================== 地下生态特征 ====================

    /** 发光菌体特征 —— 在洞穴天花板/墙壁悬挂生成粉色发光菌群 */
    public static final DeferredHolder<Feature<?>, CaveGlowMushroomFeature> CAVE_GLOW_MUSHROOM =
            FEATURES.register("cave_glow_mushroom", CaveGlowMushroomFeature::new);

    /** 染梦混合树特征 —— 自定义 Trunk/Foliage/Decorator 的方块树入口 */
    public static final DeferredHolder<Feature<?>, Feature<TreeConfiguration>> DYEDREAM_TREE =
            FEATURES.register("dyedream_tree", () -> new DyedreamTreeFeature(TreeConfiguration.CODEC));

    /** 结构树特征 —— 自然生成 Better Biomes 移植树（结构 NBT 直接放置） */
    public static final DeferredHolder<Feature<?>, DyedreamStructureTreeFeature> STRUCTURE_TREE =
            FEATURES.register("structure_tree", () -> new DyedreamStructureTreeFeature(DyedreamStructureTreeFeature.Config.CODEC.codec()));

    /**
     * 安全水色湖 —— 对齐 LakeFeature 形貌，但不调用 getBiome 结冰检查
     *（避免 1.21.1 WorldGenRegion OOB FATAL）。供 ground_feature_wind_journey_1 等使用。
     */
    public static final DeferredHolder<Feature<?>, SafeLakeFeature> SAFE_LAKE =
            FEATURES.register("safe_lake", SafeLakeFeature::new);
}
