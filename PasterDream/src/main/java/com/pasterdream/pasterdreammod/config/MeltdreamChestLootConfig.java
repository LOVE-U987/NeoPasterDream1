package com.pasterdream.pasterdreammod.config;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * 融梦水晶箱战利品配置（物品池解析与默认值）。
 * <p>
 * 三个品质（普通/稀有/传说）的物品池均可由玩家在配置界面中自定义，
 * 配置项定义在 {@link PDCommonConfig}（PasterDream-Common.toml 的 "Meltdream Chest" 段）。
 * <p>
 * 条目格式（每行一个）：{@code <物品ID> [数量] [权重]}
 * <ul>
 *   <li>物品ID：注册 ID，如 {@code pasterdream:fried_egg} 或 {@code minecraft:diamond}；
 *       无命名空间时优先按 {@code minecraft:} 解析，失败再尝试 {@code pasterdream:}</li>
 *   <li>数量：可选，默认 1，范围 1~64</li>
 *   <li>权重：可选，默认 1，范围 1~9999</li>
 * </ul>
 * 解析失败的条目会被跳过（输出警告日志，不影响其他条目）。
 * 自定义开关关闭时，默认掉落由原版战利品表提供；开关开启时使用玩家配置池，
 * 留空或全部无效则回退到隐藏的内置默认池（不出现在 toml/GUI，见 {@code DEFAULT_*} 常量）。
 *
 * @author PasterDream
 */
public final class MeltdreamChestLootConfig {

    private MeltdreamChestLootConfig() {
    }

    /** 物品池条目：物品 + 权重（与 {@link com.pasterdream.pasterdreammod.block.MeltdreamChestBlock} 原有记录一致） */
    public record LootEntry(ItemStack stack, int weight) {
    }

    // ==================== 旧内置默认物品池（迁移比对用） ====================
    // 仅供一次性迁移识别"玩家未修改过"的旧默认值，不再作为实际掉落来源

    /** 旧普通品质默认物品池（迁移比对用） */
    public static final List<String> LEGACY_V1_COMMON_LOOT = List.of(
            "pasterdream:fried_egg 2 30",
            "pasterdream:candy_cane 2 25",
            "pasterdream:bubble_gum 3 25",
            "pasterdream:chocolate 2 25",
            "pasterdream:berry_buncake 2 22",
            "pasterdream:cream_buncake 2 22",
            "pasterdream:dyedream_popsicle 2 22",
            "pasterdream:gingerbread_man 2 20",
            "pasterdream:potato_buncake 2 20",
            "pasterdream:pumpkin_buncake 2 20",
            "pasterdream:jellyfish_jello 2 18",
            "pasterdream:ricecake 1 16",
            "pasterdream:swiss_roll 1 16",
            "pasterdream:bread_slice 3 15",
            "pasterdream:fig 2 14",
            "pasterdream:strawberry_heart 1 12",
            "pasterdream:wafer_biscuit 2 10"
    );

    /** 旧稀有品质默认物品池（迁移比对用） */
    public static final List<String> LEGACY_V1_RARE_LOOT = List.of(
            "pasterdream:dyedream_ingot 2 25",
            "pasterdream:titanium_ingot 2 22",
            "pasterdream:blackmetal_ingot 2 20",
            "pasterdream:white_crystal 2 18",
            "pasterdream:dreamwish 1 18",
            "pasterdream:soul_essence 2 16",
            "pasterdream:charged_amethyst 2 15",
            "pasterdream:wind_iron_ingot 2 15",
            "pasterdream:moltengold_ingot 2 15",
            "pasterdream:dream_aurorian_steel 1 12",
            "pasterdream:dyedream_sword 1 12",
            "pasterdream:titanium_sword 1 12",
            "pasterdream:pinkegg 2 10",
            "pasterdream:nightmare_fuel 2 10",
            "pasterdream:memento_item_03 1 6",
            "pasterdream:memento_item_08 1 6"
    );

    /** 旧传说品质默认物品池（迁移比对用） */
    public static final List<String> LEGACY_V1_LEGENDARY_LOOT = List.of(
            "pasterdream:meltdream_crystal_0 1 20",
            "pasterdream:shadow_erosion_sword 1 18",
            "pasterdream:allkinds_ring 1 15",
            "pasterdream:boboji_plume 1 15",
            "pasterdream:dyedream_upgrade 1 12",
            "pasterdream:titanium_upgrade 1 12",
            "pasterdream:sculk_upgrade 1 10",
            "pasterdream:dyedream_teleport_crystal 2 10",
            "pasterdream:sweetdream_disc 1 8",
            "pasterdream:dyedream_world_disc 1 8",
            "pasterdream:memento_item_03 1 8",
            "pasterdream:memento_item_08 1 8"
    );

    // ==================== 隐藏内置默认物品池（不出现在 toml/GUI） ====================
    // 按原版四池（材料/宝石/饰品/装备）重映射为三档；仅在开关开启且玩家池无效、
    // 非梦境维度、或战利品表缺失/无效时作为兜底

    /** 隐藏普通档兜底池：原版池1 材料 */
    public static final List<String> DEFAULT_COMMON_LOOT = List.of(
            "pasterdream:dyedream_ingot 1 10",
            "pasterdream:dyedream_dust 2 40",
            "pasterdream:titanium_nugget 4 30",
            "pasterdream:dream_coin_1 1 10",
            "pasterdream:titanium_ingot 1 20",
            "pasterdream:pink_slimeball 4 40",
            "pasterdream:dyedream_nugget 2 20"
    );

    /** 隐藏稀有档兜底池：原版池2 宝石 + 池3 饰品 */
    public static final List<String> DEFAULT_RARE_LOOT = List.of(
            "minecraft:diamond 1 30",
            "minecraft:gold_ingot 3 30",
            "pasterdream:dream_coin_0 2 20",
            "pasterdream:titanium_ingot 1 10",
            "minecraft:emerald 2 20",
            "pasterdream:moltengold_ingot 1 10",
            "pasterdream:charged_amethyst 1 10",
            "minecraft:netherite_scrap 1 10",
            "pasterdream:pineapple_love_sea 1 10",
            "pasterdream:embryo_ring 1 10",
            "pasterdream:embryo_necklace 1 10",
            "pasterdream:health_0_necklace 1 10",
            "pasterdream:rabbit_0_necklace 1 10",
            "pasterdream:fire_0_necklace 1 10",
            "pasterdream:red_dew_0_ring 1 10",
            "pasterdream:red_dew_1_ring 1 10",
            "pasterdream:embryo_belt 1 10",
            "pasterdream:traveler_belt 1 10",
            "pasterdream:garland 1 20",
            "pasterdream:nature_belt 1 20"
    );

    /** 隐藏传说档兜底池：原版池4 装备 + 顶级材料/饰品 */
    public static final List<String> DEFAULT_LEGENDARY_LOOT = List.of(
            "pasterdream:dyedream_armor_helmet 1 10",
            "pasterdream:dyedream_armor_chestplate 1 10",
            "pasterdream:dyedream_armor_leggings 1 10",
            "pasterdream:dyedream_armor_boots 1 10",
            "pasterdream:dyedream_sword 1 10",
            "pasterdream:dyedream_axe 1 10",
            "pasterdream:dyedream_shovel 1 10",
            "pasterdream:dyedream_hoe 1 10",
            "pasterdream:dyedream_pickaxe 1 10",
            "pasterdream:meltdream_crystal_0 1 20",
            "pasterdream:shadow_erosion_sword 1 18",
            "pasterdream:allkinds_ring 1 15",
            "pasterdream:boboji_plume 1 15",
            "pasterdream:dyedream_upgrade 1 12",
            "pasterdream:titanium_upgrade 1 12",
            "pasterdream:sculk_upgrade 1 10",
            "pasterdream:dyedream_teleport_crystal 2 10",
            "pasterdream:sweetdream_disc 1 8",
            "pasterdream:dyedream_world_disc 1 8",
            "pasterdream:memento_item_03 1 8",
            "pasterdream:memento_item_08 1 8"
    );

    // ==================== 公共入口 ====================

    /**
     * 获取普通品质物品池。
     * <p>自定义开关开启且物品池有效时使用自定义池，否则回退内置默认池。</p>
     *
     * @return 普通品质物品池数组（纯食物）
     */
    public static LootEntry[] getCommonLoot() {
        return resolvePool(PDCommonConfig.MELTDREAM_CHEST_COMMON_LOOT.get(), DEFAULT_COMMON_LOOT);
    }

    /**
     * 获取稀有品质物品池。
     *
     * @return 稀有品质物品池数组
     */
    public static LootEntry[] getRareLoot() {
        return resolvePool(PDCommonConfig.MELTDREAM_CHEST_RARE_LOOT.get(), DEFAULT_RARE_LOOT);
    }

    /**
     * 获取传说品质物品池。
     *
     * @return 传说品质物品池数组
     */
    public static LootEntry[] getLegendaryLoot() {
        return resolvePool(PDCommonConfig.MELTDREAM_CHEST_LEGENDARY_LOOT.get(), DEFAULT_LEGENDARY_LOOT);
    }

    /**
     * 获取指定品质的隐藏内置兜底物品池（忽略自定义开关与玩家配置）。
     * <p>用于非梦境维度、战利品表缺失/无效、或开关开启但玩家池全部无效时的回退。</p>
     *
     * @param quality 品质（1=普通, 2=稀有, 3=传说）
     * @return 对应档位的隐藏默认物品池数组
     */
    public static LootEntry[] getFallbackLoot(int quality) {
        return switch (quality) {
            case 2 -> parsePool(DEFAULT_RARE_LOOT);
            case 3 -> parsePool(DEFAULT_LEGENDARY_LOOT);
            default -> parsePool(DEFAULT_COMMON_LOOT);
        };
    }

    // ==================== 内部解析 ====================

    /**
     * 解析物品池：优先使用玩家自定义条目，无效/空时回退默认池。
     *
     * @param customSpecs 玩家自定义条目（来自配置）
     * @param defaultSpecs 内置默认条目
     * @return 解析后的物品池数组（保证至少 1 条）
     */
    private static LootEntry[] resolvePool(List<String> customSpecs, List<String> defaultSpecs) {
        boolean customEnabled = PDCommonConfig.MELTDREAM_CHEST_CUSTOM_LOOT_ENABLED.get();
        LootEntry[] custom = customEnabled ? parsePool(customSpecs) : new LootEntry[0];
        if (custom.length > 0) {
            return custom;
        }
        if (customEnabled) {
            // 玩家开了自定义但全部条目无效 → 回退默认并提示
            PasterDreamMod.LOGGER.warn("[MeltdreamChestLootConfig] 自定义物品池全部条目无效，回退到内置默认物品池");
        }
        return parsePool(defaultSpecs);
    }

    /**
     * 将配置字符串列表解析为物品池数组，跳过无效条目。
     *
     * @param specs 配置条目列表（每行一个）
     * @return 有效条目数组（可能为空）
     */
    private static LootEntry[] parsePool(List<String> specs) {
        List<LootEntry> entries = new ArrayList<>();
        for (String spec : specs) {
            if (spec == null || spec.isBlank()) continue;
            LootEntry entry = parseSpec(spec.trim());
            if (entry != null) {
                entries.add(entry);
            }
        }
        return entries.toArray(new LootEntry[0]);
    }

    /**
     * 解析单条物品规格：{@code <物品ID> [数量] [权重]}。
     *
     * @param spec 单条规格字符串
     * @return 解析后的物品池条目；解析失败返回 null
     */
    private static LootEntry parseSpec(String spec) {
        String[] parts = spec.trim().split("\\s+");
        if (parts.length == 0) return null;

        // 物品 ID 解析：无命名空间时先按 minecraft: 再按 pasterdream: 尝试
        Item item = null;
        String rawId = parts[0];
        ResourceLocation id = ResourceLocation.tryParse(rawId);
        if (id != null) {
            item = BuiltInRegistries.ITEM.get(id);
        }
        if ((item == null || item == Items.AIR) && !rawId.contains(":")) {
            item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("minecraft", rawId));
        }
        if ((item == null || item == Items.AIR) && !rawId.contains(":")) {
            item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("pasterdream", rawId));
        }
        if (item == null || item == Items.AIR) {
            PasterDreamMod.LOGGER.warn("[MeltdreamChestLootConfig] 跳过无效物品条目（物品不存在）：{}", spec);
            return null;
        }

        // 数量（可选，默认 1，限制 1~64）
        int count = 1;
        if (parts.length >= 2) {
            try {
                count = Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignored) {
                PasterDreamMod.LOGGER.warn("[MeltdreamChestLootConfig] 跳过无效数量（应为整数）：{}", spec);
                return null;
            }
            count = Math.max(1, Math.min(64, count));
        }

        // 权重（可选，默认 1，限制 1~9999）
        int weight = 1;
        if (parts.length >= 3) {
            try {
                weight = Integer.parseInt(parts[2]);
            } catch (NumberFormatException ignored) {
                PasterDreamMod.LOGGER.warn("[MeltdreamChestLootConfig] 跳过无效权重（应为整数）：{}", spec);
                return null;
            }
            weight = Math.max(1, Math.min(9999, weight));
        }

        return new LootEntry(new ItemStack(item, count), weight);
    }
}
