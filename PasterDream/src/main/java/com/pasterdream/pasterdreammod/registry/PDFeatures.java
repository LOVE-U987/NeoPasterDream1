package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.worldgen.feature.MegaCalcitePillarFeature;
import com.pasterdream.pasterdreammod.worldgen.feature.MegaMushroomFeature;
import com.pasterdream.pasterdreammod.worldgen.feature.PinkagaricClusterFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 自定义世界生成特征（Feature）注册类
 * <p>
 * 包含粉丁菇巨簇、巨型方解石云端柱和巨型粉丁菇等需要精细结构控制的自定义 Feature。
 * 其他通用装饰物（冰刺、冰之门、方解石柱、坠云等）通过
 * WorldDecorationAPI 的 DecorationBuilder 在 ModDecorations 中注册。
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
}