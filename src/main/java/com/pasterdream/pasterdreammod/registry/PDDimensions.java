package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 维度注册类
 * 使用 DeferredRegister 模式注册所有自定义维度类型和维度实例
 * <p>
 * 待实现维度（参考 STORYLINE.md）：
 * - dyedream_world（染梦维度）：第0层梦境，安全区域
 * - lamp_shadow_world（灯影世界）：更深层梦境，暗影生物巢穴
 * - shadow_dungeon（暗影地牢）：灯影世界深处的随机生成地牢
 * <p>
 * 注意：维度注册除了 Java 代码外，还需要：
 * 1. data/pasterdream/dimension/ 下的 JSON 配置文件
 * 2. data/pasterdream/dimension_type/ 下的维度类型 JSON
 * 3. 对应的生物群系和噪声设置
 */
public class PDDimensions {

    /**
     * 维度类型注册器
     * 定义维度环境属性（光照、天气、坐标缩放等）
     */
    public static final DeferredRegister<DimensionType> DIMENSION_TYPES = DeferredRegister.create(
            Registries.DIMENSION_TYPE, PasterDreamMod.MOD_ID);

    /**
     * 维度实例注册器
     * 将 DimensionType 与 ChunkGenerator 绑定，构成完整维度
     */
    public static final DeferredRegister<LevelStem> LEVEL_STEMS = DeferredRegister.create(
            Registries.LEVEL_STEM, PasterDreamMod.MOD_ID);

    // ==================== 维度类型（DimensionType） ====================

    /*
     * 染梦维度类型 (dyedream_world)
     * 温和梦境维度，类似主世界但更宁静
     * 拥有天光、无天气变化
     */
    // public static final DeferredHolder<DimensionType, DimensionType> DYEDREAM_WORLD_TYPE =
    //         DIMENSION_TYPES.register("dyedream_world", () -> new DimensionType(
    //                 OptionalLong.empty(), // 固定时间（空=跟随主世界）
    //                 false,               // 是否拥有天光
    //                 false,               // 是否有天气
    //                 false,               // 是否禁用床
    //                 1.0f,                // 坐标缩放
    //                 true,                // 是否生成自然结构
    //                 0.0f,                // 光照亮度
    //                 false,               // 是否有末地般的光照
    //                 0,                   // 最低Y
    //                 256,                 // 最高Y
    //                 0,                   // 逻辑高度
    //                 BlockTags.INFINIBURN_OVERWORLD, // 无限燃烧方块
    //                 BuiltInRegistries.DIMENSION_TYPE.wrapAsHolder( // 效果ID
    //                         BuiltInRegistries.DIMENSION_TYPE.get(DefaultDimensions.OVERWORLD_EFFECTS)),
    //                 0.0f,                // 环境雾
    //                 new DimensionType.MonsterSettings(false, false, 0, 0))); // 怪物设置

    /*
     * 灯影世界维度类型 (lamp_shadow_world)
     * 黑暗梦境维度，永夜环境
     */
    // public static final DeferredHolder<DimensionType, DimensionType> LAMP_SHADOW_WORLD_TYPE =
    //         DIMENSION_TYPES.register("lamp_shadow_world", () -> { ... });

    /*
     * 暗影地牢维度类型 (shadow_dungeon)
     * 地牢维度，完全封闭空间
     */
    // public static final DeferredHolder<DimensionType, DimensionType> SHADOW_DUNGEON_TYPE =
    //         DIMENSION_TYPES.register("shadow_dungeon", () -> { ... });

    // ==================== 维度实例（LevelStem） ====================

    /*
     * 染梦维度实例 (dyedream_world)
     * 使用染梦维度类型 + 自定义区块生成器
     */
    // public static final DeferredHolder<LevelStem, LevelStem> DYEDREAM_WORLD =
    //         LEVEL_STEMS.register("dyedream_world", () -> new LevelStem(
    //                 DYEDREAM_WORLD_TYPE.getHolder().orElseThrow(),
    //                 new NoiseBasedChunkGenerator(...)));

    // 更多维度实例依次添加...
}