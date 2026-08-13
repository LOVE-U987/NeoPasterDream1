package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.ruin.RuinAPI;
import com.pasterdream.pasterdreammod.api.ruin.RuinResult;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.worldgen.structure.AaroncosArenaPortalStructure;
import com.pasterdream.pasterdreammod.worldgen.structure.DyedreamCrackStructure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.LinkedHashMap;
import java.util.Map;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
import com.pasterdream.pasterdreammod.worldgen.structure.DyedreamCrackStructure;
/**
 * 染梦遗迹/结构注册 —— 使用 RuinAPI + JigsawStructure 注册 41 个遗迹结构
 * <p>
 * 采用 {@link RuinAPI} 的 Facade + Builder 模式，以 {@link JigsawStructure#CODEC}
 * 作为序列化编解码器，注册自定义 StructureType。所有 JSON 资源文件已在开发阶段
 * 通过 Python 脚本生成，运行时仅需注册 StructureType 和 StructureSet。
 * <p>
 * 注册的遗迹（来自旧模组 FixPasterDream）：
 * <ul>
 *   <li>{@code dream_train} — 染梦列车，Y=55 空中漂浮</li>
 *   <li>{@code dyedream_worldtree_0}/{@code dyedream_worldtree_1} — 巨型染梦树两变体（NBT {@code dyedream_worldtree} / {@code dyedream_worldtree_true}），Y=-25</li>
 *   <li>{@code pinkagaric_house_0~3} — 4 种粉红菇屋，Y=-4 地表</li>
 *   <li>{@code struct_dyedream_crack_1} — 主世界染梦裂隙入口，Y=32</li>
 *   <li>{@code desert_cottage_0} — 主世界沙漠小屋，Y=0</li>
 *   <li>{@code aaroncos_arena_portals} — 亚伦柯斯竞技场入口，Y=-4</li>
 *   <li>{@code dyedream_floating_temple} — 染梦悬浮寺庙，Y=39 地标级</li>
 *   <li>{@code dream_church_0~10} — 11 种梦想教堂，Y=32</li>
 *   <li>{@code desert_fortress_0} — 沙漠地下堡垒，Y=-33</li>
 *   <li>{@code dyedream_tower_0~1} — 2 种染梦塔，Y=32</li>
 *   <li>{@code dyedream_laboratory_0} — 染梦实验室，Y=32</li>
 *   <li>{@code dyedream_tavern} — 染梦酒馆，Y=32</li>
 *   <li>{@code dyedream_pavilion_0~2} — 3 种染梦亭子，Y=32</li>
 *   <li>{@code dyedream_campsite_0} — 染梦营地，Y=32</li>
 *   <li>{@code dream_wishingtree_0~1} — 2 种梦想许愿树，Y=32</li>
 *   <li>{@code traveler_house_0~2} — 3 种旅人小屋，Y=-13</li>
 *   <li>{@code garden_decryption_0~2} — 3 种花园解密，Y=32</li>
 *   <li>{@code picnic_basket_structure} — 野餐篮，Y=32</li>
 *   <li>{@code meltdream_liquid_well_0~1} — 2 种融梦液体井，Y=-33</li>
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
        PDDebugLogger.mainDebug("[PDRuinsRegistration] ===== 开始注册染梦遗迹结构 =====");

        registerDreamTrain();
        registerDyedreamWorldTree();
        registerPinkagaricHouses();
        // 染梦裂隙自然生成受配置控制（PDCommonConfig.DYEDREAM_CRACK_GENERATE）：
        // 类型无条件注册；配置判断下沉到生成阶段（见 DyedreamCrackStructure#findGenerationPoint），
        // 因为配置在 RegisterEvent 之后才加载，注册阶段无法安全读取
        registerDyedreamCrack();
        registerDesertCottage();
        registerAaroncosArenaPortal();
        // P0 移植新增
        registerDyedreamFloatingTemple();
        registerDreamChurches();
        registerDesertFortress();
        // P1 移植新增
        registerDyedreamTower0();
        registerDyedreamTower1();
        registerDyedreamLaboratory0();
        registerDyedreamTavern();
        registerDyedreamPavilion0();
        registerDyedreamPavilion1();
        registerDyedreamPavilion2();
        registerDyedreamCampsite0();
        registerDreamWishingtree0();
        registerDreamWishingtree1();
        registerTravelerHouse0();
        registerTravelerHouse1();
        registerTravelerHouse2();
        registerGardenDecryption0();
        registerGardenDecryption1();
        registerGardenDecryption2();
        registerPicnicBasketStructure();
        registerMeltdreamLiquidWell0();
        registerMeltdreamLiquidWell1();

        int count = REGISTERED_STRUCTURES.size();
        PDDebugLogger.mainDebug("[PDRuinsRegistration] ✅ 染梦遗迹结构注册完成: 共 {} 个"
                + "（裂隙类型无条件注册，生成与否由配置在生成阶段控制）", count);
    }

    /**
     * 构建单个遗迹结构（染梦维度快捷版，不生成 JSON，JSON 已预置）
     *
     * @param name         结构注册名
     * @param startHeight  起始高度
     * @return 注册结果
     */
    private static RuinResult buildRuin(String name, int startHeight) {
        return buildRuin(name, "pasterdream:is_dyedream", startHeight, "none");
    }

    /**
     * 构建单个遗迹结构（完整参数版，不生成 JSON，JSON 已预置）
     *
     * @param name              结构注册名
     * @param biomeTag          生物群系标签（如 "pasterdream:is_dyedream" 或 "minecraft:is_desert"）
     * @param startHeight       起始高度
     * @param terrainAdaptation 地形适应类型（"none" / "beard_thin" / "beard_box"）
     * @return 注册结果
     */
    private static RuinResult buildRuin(String name, String biomeTag,
                                         int startHeight, String terrainAdaptation) {
        RuinResult result = RuinAPI.createRuin(name)
                .biomeTag(biomeTag)
                .templatePool("pasterdream:" + name)
                .structureClass(JigsawStructure.class)
                .codec(JigsawStructure.CODEC)
                .terrainAdaptation(terrainAdaptation)
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

    /**
     * 巨型染梦树两变体 —— 与原版 {@code dyedream_worldtree_0/1} 一一对应。
     * <ul>
     *   <li>0：模板 {@code dyedream_worldtree}，spacing 156 / sep 87 / salt 1208134265</li>
     *   <li>1：模板 {@code dyedream_worldtree_true}，spacing 289 / sep 165 / salt 1208711388</li>
     * </ul>
     * JSON（structure / template_pool / structure_set）已预置；此处仅注册 StructureType 元数据。
     */
    private static void registerDyedreamWorldTree() {
        // biome 在预置 JSON 中为 biome_dyedream_0；generateJson=false，biomeTag 仅元数据
        buildRuin("dyedream_worldtree_0", "pasterdream:biome_dyedream_0", -25, "none");
        buildSet("dyedream_worldtree_0", "dyedream_worldtree_0_set", 156, 87, 1208134265);

        buildRuin("dyedream_worldtree_1", "pasterdream:biome_dyedream_0", -25, "none");
        buildSet("dyedream_worldtree_1", "dyedream_worldtree_1_set", 289, 165, 1208711388);
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
     * <p>
     * StructureType 无条件注册（不读配置）；自然生成是否启用由
     * {@link DyedreamCrackStructure#findGenerationPoint} 在生成阶段按
     * {@link PDCommonConfig#DYEDREAM_CRACK_GENERATE} 判断（配置关闭时返回空 → 不生成）。
     */
    private static void registerDyedreamCrack() {
        RuinResult result = RuinAPI.createRuin("struct_dyedream_crack_1")
                .biomeTag("minecraft:is_overworld")
                .templatePool("pasterdream:struct_dyedream_crack_1")
                .structureClass(DyedreamCrackStructure.class)
                .codec(DyedreamCrackStructure.CODEC)
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

    /**
     * 注册主世界竞技场入口结构 —— aaroncos_arena_portals
     * <p>
     * 使用 {@link AaroncosArenaPortalStructure} 自定义结构类（组合 JigsawStructure），
     * 通过 structure_set 正常随机生成，但<b>每世界仅生成一次</b>：
     * 第一个候选点生成成功后记录坐标并"关门"，后续候选点不再生成；
     * 生成时由 {@link com.pasterdream.pasterdreammod.worldgen.PDAaroncosArenaWorldgen}
     * 分帧刷写竞技场群系 {@code aaroncos_arena_biome} 并启动遗迹感染。
     * <p>
     * 结构集配置：spacing=256, separation=128, salt=901277331（预置 JSON）。
     * 灯影世界的 {@code aaroncos_arena_portal}（单数，biome_shadow_0）不受影响，照常生成。
     */
    private static void registerAaroncosArenaPortal() {
        RuinResult result = RuinAPI.createRuin("aaroncos_arena_portals")
                .biomeTag("minecraft:is_overworld")
                .templatePool("pasterdream:aaroncos_arena_portals")
                .structureClass(AaroncosArenaPortalStructure.class)
                .codec(AaroncosArenaPortalStructure.CODEC)
                .terrainAdaptation("beard_thin")
                .step("surface_structures")
                .size(1)
                .startHeight(24)
                .generateJson(false)
                .build();
        REGISTERED_STRUCTURES.put("aaroncos_arena_portals", result);

        RuinAPI.createRuinSet("aaroncos_arena_portals", "aaroncos_arena_portals_set")
                .spacing(256)
                .separation(128)
                .salt(901277331)
                .generateJson(false)
                .build();
    }

    // ======================== P0 移植新增遗迹 ========================

    /**
     * 注册染梦悬浮寺庙结构 —— dyedream_floating_temple
     * <p>
     * 在染梦维度 Y=39 悬浮生成，是染梦维度的地标级建筑。
     * NBT 结构文件为 {@code structure/dyedream_floating_temple.nbt}。
     * 结构集配置：spacing=187, separation=121, salt=1576871481
     */
    private static void registerDyedreamFloatingTemple() {
        buildRuin("dyedream_floating_temple", 39);
        buildSet("dyedream_floating_temple", "dyedream_floating_temple_set", 187, 121, 1576871481);
    }

    /**
     * 注册梦想教堂系列结构 —— dream_church_0~10（与原版 11 变体一一对应）
     * <p>
     * 数据对齐 {@code libs/FixPasterDream-main}：
     * <ul>
     *   <li>0–7：spacing/sep 134–137 / 60–61，Y=32，群系 {@code #pasterdream:dyedream_biome}</li>
     *   <li>8–9：spacing 164 / sep 81，Y=32（第三教堂变体，structure_block_12）</li>
     *   <li>10：spacing 172 / sep 95，Y=37，群系仅 {@code pasterdream:biome_dyedream_0}</li>
     * </ul>
     * NBT / structure / template_pool / structure_set JSON 均已预置；
     * 0–7 的 structure_set 文件名为 {@code *_set.json}，8–10 与原版同名无后缀。
     * <p>
     * 注意：{@code #pasterdream:dyedream_biome} 为<b>结构专用地面群系标签</b>，
     * 故意只包含 7 个陆地主群系（不含深海/蘑菇平原），使教堂等建筑仅在地面生成。
     * 与 {@code #pasterdream:is_dyedream}（9 个群系全量标签，用于特征注入）语义不同，勿混用。
     */
    private static void registerDreamChurches() {
        // 配置表：{index, spacing, separation, salt, startHeight}
        // salt/spacing 取自原版 structure_set/dream_church_*.json
        int[][] configs = {
                {0,  134, 60, 1163538860, 32},
                {1,  134, 60, 1163346486, 32},
                {2,  134, 60, 1163923609, 32},
                {3,  134, 60, 1163731235, 32},
                {4,  137, 61, 1162769362, 32},
                {5,  137, 61, 1162576988, 32},
                {6,  137, 61, 1163154111, 32},
                {7,  137, 61, 1162961737, 32},
                {8,  164, 81, 1161999865, 32},
                {9,  164, 81, 1161807490, 32},
                {10, 172, 95, 1178135507, 37},
        };
        for (int[] cfg : configs) {
            int idx = cfg[0];
            String name = "dream_church_" + idx;
            // church_10 的 biomes 在预置 JSON 中为单群系 biome_dyedream_0；
            // generateJson=false，此处 biomeTag 仅作 StructureType 元数据，与 JSON 并存。
            buildRuin(name, cfg[4]);
            // 0–7 历史文件名带 _set；8–10 与原版一致不带后缀
            String setName = idx <= 7 ? name + "_set" : name;
            buildSet(name, setName, cfg[1], cfg[2], cfg[3]);
        }
    }

    /**
     * 注册沙漠地下堡垒结构 —— desert_fortress_0
     * <p>
     * 在主世界沙漠群系（#minecraft:is_desert）Y=-33 地下生成，使用 beard_thin 地形适应。
     * 注意：NBT 文件名为 {@code desert_fortress.nbt}（无 _0 后缀），template_pool 的
     * location 字段已正确指向 {@code pasterdream:desert_fortress}。
     * 结构集配置：spacing=39, separation=27, salt=707152074
     */
    private static void registerDesertFortress() {
        buildRuin("desert_fortress_0", "minecraft:is_desert", -33, "beard_thin");
        buildSet("desert_fortress_0", "desert_fortress_0_set", 39, 27, 707152074);
    }

    // ======================== P1 移植新增遗迹 ========================

    private static void registerDyedreamTower0() {
        buildRuin("dyedream_tower_0", 32);
        buildSet("dyedream_tower_0", "dyedream_tower_0_set", 96, 45, 524960775);
    }

    private static void registerDyedreamTower1() {
        buildRuin("dyedream_tower_1", 32);
        buildSet("dyedream_tower_1", "dyedream_tower_1_set", 98, 48, 524768400);
    }

    private static void registerDyedreamLaboratory0() {
        buildRuin("dyedream_laboratory_0", "pasterdream:is_dyedream", 32, "beard_thin");
        buildSet("dyedream_laboratory_0", "dyedream_laboratory_0_set", 37, 18, 946202329);
    }

    private static void registerDyedreamTavern() {
        buildRuin("dyedream_tavern", 32);
        buildSet("dyedream_tavern", "dyedream_tavern_set", 34, 16, 1516864983);
    }

    private static void registerDyedreamPavilion0() {
        buildRuin("dyedream_pavilion_0", 32);
        buildSet("dyedream_pavilion_0", "dyedream_pavilion_0_set", 17, 7, 1818613810);
    }

    private static void registerDyedreamPavilion1() {
        buildRuin("dyedream_pavilion_1", "pasterdream:is_dyedream", 32, "beard_thin");
        buildSet("dyedream_pavilion_1", "dyedream_pavilion_1_set", 45, 21, 1819190933);
    }

    private static void registerDyedreamPavilion2() {
        buildRuin("dyedream_pavilion_2", "pasterdream:is_dyedream", 32, "beard_thin");
        buildSet("dyedream_pavilion_2", "dyedream_pavilion_2_set", 43, 24, 1818998559);
    }

    private static void registerDyedreamCampsite0() {
        buildRuin("dyedream_campsite_0", 32);
        buildSet("dyedream_campsite_0", "dyedream_campsite_0_set", 25, 12, 54397954);
    }

    private static void registerDreamWishingtree0() {
        buildRuin("dream_wishingtree_0", 32);
        buildSet("dream_wishingtree_0", "dream_wishingtree_0_set", 150, 72, 1932971913);
    }

    private static void registerDreamWishingtree1() {
        buildRuin("dream_wishingtree_1", 32);
        buildSet("dream_wishingtree_1", "dream_wishingtree_1_set", 89, 72, 1932779539);
    }

    private static void registerTravelerHouse0() {
        buildRuin("traveler_house_0", "pasterdream:is_dyedream", -13, "beard_thin");
        buildSet("traveler_house_0", "traveler_house_0_set", 99, 45, 52031349);
    }

    private static void registerTravelerHouse1() {
        buildRuin("traveler_house_1", "pasterdream:is_dyedream", -13, "beard_thin");
        buildSet("traveler_house_1", "traveler_house_1_set", 79, 42, 52608472);
    }

    private static void registerTravelerHouse2() {
        buildRuin("traveler_house_2", "pasterdream:is_dyedream", -13, "beard_thin");
        buildSet("traveler_house_2", "traveler_house_2_set", 78, 43, 52416098);
    }

    private static void registerGardenDecryption0() {
        buildRuin("garden_decryption_0", "pasterdream:is_dyedream", 32, "beard_thin");
        buildSet("garden_decryption_0", "garden_decryption_0_set", 26, 13, 1889587396);
    }

    private static void registerGardenDecryption1() {
        buildRuin("garden_decryption_1", "pasterdream:is_dyedream", 32, "beard_thin");
        buildSet("garden_decryption_1", "garden_decryption_1_set", 18, 7, 1889395022);
    }

    private static void registerGardenDecryption2() {
        buildRuin("garden_decryption_2", 32);
        buildSet("garden_decryption_2", "garden_decryption_2_set", 136, 85, 1888433149);
    }

    private static void registerPicnicBasketStructure() {
        buildRuin("picnic_basket_structure", 32);
        buildSet("picnic_basket_structure", "picnic_basket_structure_set", 20, 10, 1295591192);
    }

    private static void registerMeltdreamLiquidWell0() {
        buildRuin("meltdream_liquid_well_0", "pasterdream:is_dyedream", -33, "beard_thin");
        buildSet("meltdream_liquid_well_0", "meltdream_liquid_well_0_set", 57, 24, 1153570020);
    }

    private static void registerMeltdreamLiquidWell1() {
        buildRuin("meltdream_liquid_well_1", "pasterdream:is_dyedream", -33, "beard_thin");
        buildSet("meltdream_liquid_well_1", "meltdream_liquid_well_1_set", 56, 25, 1153377646);
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
