package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.worldgen.feature.CaveGlowMushroomFeature;
import com.pasterdream.pasterdreammod.worldgen.feature.CrystalCaveFeature;
import com.pasterdream.pasterdreammod.worldgen.feature.FloatingIslandFeature;
import com.pasterdream.pasterdreammod.worldgen.feature.MegaCalcitePillarFeature;
import com.pasterdream.pasterdreammod.worldgen.feature.MegaMushroomFeature;
import com.pasterdream.pasterdreammod.worldgen.feature.PinkagaricClusterFeature;
import com.pasterdream.pasterdreammod.worldgen.feature.SuspendedCrystalFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
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

    /** 水晶洞穴特征 —— 在 Y=-32~0 生成椭球体水晶洞穴，壁面嵌入水晶，地面铺发光方块 */
    public static final DeferredHolder<Feature<?>, CrystalCaveFeature> CRYSTAL_CAVE =
            FEATURES.register("crystal_cave", CrystalCaveFeature::new);

    /** 悬浮水晶核心特征 —— 在大空腔中生成中央主水晶柱 + 环绕小水晶 */
    public static final DeferredHolder<Feature<?>, SuspendedCrystalFeature> SUSPENDED_CRYSTAL =
            FEATURES.register("suspended_crystal", SuspendedCrystalFeature::new);

    /** 发光菌体特征 —— 在洞穴天花板/墙壁悬挂生成粉色发光菌群 */
    public static final DeferredHolder<Feature<?>, CaveGlowMushroomFeature> CAVE_GLOW_MUSHROOM =
            FEATURES.register("cave_glow_mushroom", CaveGlowMushroomFeature::new);
}