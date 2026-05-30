package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.ruin.RuinAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 遗迹/结构注册类
 * 使用 DeferredRegister 模式注册所有自定义 StructureType
 * <p>
 * 待实现结构（参考原模组和 STORYLINE.md）：
 * - 染梦遗迹（Dyedream Ruins）：染梦维度的建筑遗迹
 * - 暗影地牢（Shadow Dungeon）：灯影世界深处的随机地牢
 * - 村庄建筑（Village Buildings）：原模组中的村庄建筑扩展
 * <p>
 * 注意：结构注册除了 Java 代码外，还需要：
 * 1. data/pasterdream/worldgen/structure/ 下的结构 JSON
 * 2. data/pasterdream/worldgen/structure_set/ 下的结构集 JSON
 * 3. data/pasterdream/worldgen/template_pool/ 下的模板池 JSON
 * 4. 对应的 .nbt 结构文件（放在 data/pasterdream/structures/）
 * <p>
 * 新 API 推荐使用方式（见下文示例）：
 * <pre>{@code
 * // ====== 在 PasterDreamMod 构造函数或 ModDecoration 中使用 RuinAPI ======
 * // 1. 注册结构类型 + 自动生成 structure JSON
 * RuinResult ruins = RuinAPI.createRuin("dyedream_ruins")
 *     .biomeTag("pasterdream:is_dyedream")
 *     .templatePool("pasterdream:dyedream_ruins_pool")
 *     .structureClass(DyedreamRuinsStructure.class)
 *     .codec(DyedreamRuinsStructure.CODEC)
 *     .terrainAdaptation(TerrainAdaptation.BEARD_THIN)
 *     .build();
 *
 * // 2. 注册结构集 + 自动生成 structure_set JSON
 * RuinAPI.createRuinSet("dyedream_ruins", "dyedream_ruins_set")
 *     .spacing(32).separation(8).salt(12345)
 *     .build();
 *
 * // 3. 单独使用 TemplatePoolGenerator 生成模板池 JSON
 * new TemplatePoolGenerator("pasterdream", "dyedream_ruins_pool")
 *     .addSingleElement("pasterdream:dyedream_ruins/ruin_1", 3, "rigid", "minecraft:empty")
 *     .addSingleElement("pasterdream:dyedream_ruins/ruin_2", 2, "rigid", "minecraft:empty")
 *     .saveToFile("src/main/resources");
 * }</pre>
 */
public class PDStructures {

    /**
     * 结构类型注册器
     * <p>
     * 注意：新旧两种方式均可使用。推荐通过 {@link RuinAPI#REGISTRY} 统一注册，
     * 现有 {@code STRUCTURE_TYPES} 保留用于手动注册兼容。
     */
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(
            Registries.STRUCTURE_TYPE, PasterDreamMod.MOD_ID);

    // ==================== 结构类型（StructureType） ====================
    
    /*
     * 示例：自定义结构类型注册（传统手动方式）
     * 需要在后期创建对应的 Structure 子类
     * 
     * 新项目推荐使用 RuinAPI.createRuin() 替代下面的手动注册方式
     */
    // public static final DeferredHolder<StructureType<?>, StructureType<DyedreamRuinsStructure>> DYEDREAM_RUINS =
    //         STRUCTURE_TYPES.register("dyedream_ruins",
    //                 () -> () -> DyedreamRuinsStructure.CODEC);
    //
    // public static final DeferredHolder<StructureType<?>, StructureType<ShadowDungeonStructure>> SHADOW_DUNGEON =
    //         STRUCTURE_TYPES.register("shadow_dungeon",
    //                 () -> () -> ShadowDungeonStructure.CODEC);

    // ==================== 结构配置引用键（ResourceKey） ====================
    
    /*
     * 结构 ResourceKey 常量定义示例（传统手动方式）：
     * 
     * 新项目推荐使用 RuinResult 自动管理 ResourceKey
     *
     * public static final ResourceKey<Structure> DYEDREAM_RUINS_KEY =
     *         ResourceKey.create(Registries.STRUCTURE,
     *                 ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dyedream_ruins"));
     *
     * public static final ResourceKey<StructureSet> DYEDREAM_RUINS_SET_KEY =
     *         ResourceKey.create(Registries.STRUCTURE_SET,
     *                 ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "dyedream_ruins_set"));
     */
}