package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.ruin.RuinAPI;
import com.pasterdream.pasterdreammod.api.ruin.RuinResult;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 染梦遗迹/结构注册 —— 使用 RuinAPI + JigsawStructure 注册 6 个遗迹结构
 * <p>
 * 采用 {@link RuinAPI} 的 Facade + Builder 模式，以 {@link JigsawStructure#CODEC}
 * 作为序列化编解码器，注册自定义 StructureType。所有 JSON 资源文件已在开发阶段
 * 通过 Python 脚本生成，运行时仅需注册 StructureType 和 StructureSet。
 * <p>
 * 注册的遗迹（来自旧模组 FixPasterDream）：
 * <ul>
 *   <li>{@code dream_train} — 染梦列车，Y=55 空中漂浮</li>
 *   <li>{@code dyedream_worldtree} — 巨型染梦树，Y=-25 地下生长</li>
 *   <li>{@code pinkagaric_house_0~3} — 4 种粉红菇屋，Y=-4 地表</li>
 * </ul>
 *
 * @see JigsawStructure
 * @see RuinAPI
 */
public class PDRuinsRegistration {

    private static final Map<String, RuinResult> REGISTERED_STRUCTURES = new LinkedHashMap<>();

    private PDRuinsRegistration() {}

    /** 注册所有染梦遗迹结构 */
    public static void register() {
        PasterDreamMod.LOGGER.info("[PDRuinsRegistration] ===== 开始注册染梦遗迹结构 =====");

        registerDreamTrain();
        registerDyedreamWorldTree();
        registerPinkagaricHouses();
        registerDyedreamCrack();
        registerDesertCottage();

        int count = REGISTERED_STRUCTURES.size();
        PasterDreamMod.LOGGER.info("[PDRuinsRegistration] ✅ 染梦遗迹结构注册完成: 共 {} 个", count);
    }

    /**
     * 构建单个遗迹结构（不生成 JSON，JSON 已预置）
     *
     * @param name         结构注册名
     * @param startHeight  起始高度
     * @return 注册结果
     */
    private static RuinResult buildRuin(String name, int startHeight) {
        RuinResult result = RuinAPI.createRuin(name)
                .biomeTag("pasterdream:is_dyedream")
                .templatePool("pasterdream:" + name)
                .structureClass(JigsawStructure.class)
                .codec(JigsawStructure.CODEC)
                .terrainAdaptation("none")
                .step("surface_structures")
                .size(1)
                .startHeight(startHeight)
                .generateJson(false)
                .build();

        REGISTERED_STRUCTURES.put(name, result);
        return result;
    }

    /**
     * 构建单个结构集配置（不生成 JSON，JSON 已预置）
     *
     * @param ruinName   结构名
     * @param setName    结构集名
     * @param spacing    生成间距（区块）
     * @param separation 最小分离（区块）
     * @param salt       随机种子盐值
     */
    private static void buildSet(String ruinName, String setName,
                                  int spacing, int separation, int salt) {
        RuinAPI.createRuinSet(ruinName, setName)
                .spacing(spacing)
                .separation(separation)
                .salt(salt)
                .generateJson(false)
                .build();
    }

    private static void registerDreamTrain() {
        buildRuin("dream_train", 55);
        buildSet("dream_train", "dream_train_set", 258, 179, 109243324);
    }

    private static void registerDyedreamWorldTree() {
        buildRuin("dyedream_worldtree", -25);
        buildSet("dyedream_worldtree", "dyedream_worldtree_set", 289, 165, 1208711388);
    }

    private static void registerPinkagaricHouses() {
        int[] spacing  = {78, 78, 79, 77};
        int[] separ    = {42, 43, 42, 41};
        int[] salts    = {148801135, 149378258, 149185884, 148224012};

        for (int i = 0; i < 4; i++) {
            String name = "pinkagaric_house_" + i;
            buildRuin(name, -4);
            buildSet(name, name + "_set", spacing[i], separ[i], salts[i]);
        }
    }

    /**
     * 注册主世界 vs 染梦裂隙结构 —— struct_dyedream_crack_1
     * <p>
     * 在主世界 Y=32 处生成裂隙结构，包含 {@code dyedream_crack} 方块，
     * 玩家接触后可传送到染梦维度。
     */
    private static void registerDyedreamCrack() {
        RuinResult result = RuinAPI.createRuin("struct_dyedream_crack_1")
                .biomeTag("minecraft:is_overworld")
                .templatePool("pasterdream:struct_dyedream_crack_1")
                .structureClass(JigsawStructure.class)
                .codec(JigsawStructure.CODEC)
                .terrainAdaptation("none")
                .step("surface_structures")
                .size(1)
                .startHeight(32)
                .generateJson(false)
                .build();
        REGISTERED_STRUCTURES.put("struct_dyedream_crack_1", result);
        buildSet("struct_dyedream_crack_1", "struct_dyedream_crack_1_set", 37, 20, 2076406732);
    }

    /**
     * 注册沙漠小屋结构 —— desert_cottage_0
     * <p>
     * 在沙漠地表 Y=0 生成的小型沙漠建筑，全原版方块。
     */
    private static void registerDesertCottage() {
        RuinResult result = RuinAPI.createRuin("desert_cottage_0")
                .biomeTag("minecraft:is_overworld")
                .templatePool("pasterdream:desert_cottage_0")
                .structureClass(JigsawStructure.class)
                .codec(JigsawStructure.CODEC)
                .terrainAdaptation("beard_thin")
                .step("surface_structures")
                .size(1)
                .startHeight(0)
                .generateJson(false)
                .build();
        REGISTERED_STRUCTURES.put("desert_cottage_0", result);
        buildSet("desert_cottage_0", "desert_cottage_0_set", 60, 48, 1131718516);
    }

    /** 获取已注册的结构结果 */
    public static RuinResult getRegisteredStructure(String name) {
        return REGISTERED_STRUCTURES.get(name);
    }

    /** 获取所有已注册结构结果的不可变视图 */
    public static Map<String, RuinResult> getAllRegisteredStructures() {
        return Map.copyOf(REGISTERED_STRUCTURES);
    }
}
