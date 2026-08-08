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
 * 自定义开关关闭、或自定义物品池全部无效时，回退到内置默认池。
 *
 * @author PasterDream
 */
public final class MeltdreamChestLootConfig {

    private MeltdreamChestLootConfig() {
    }

    /** 物品池条目：物品 + 权重（与 {@link com.pasterdream.pasterdreammod.block.MeltdreamChestBlock} 原有记录一致） */
    public record LootEntry(ItemStack stack, int weight) {
    }

    // ==================== 内置默认物品池（与原版逻辑一致） ====================

    /** 普通品质默认物品池（纯食物） */
    public static final List<String> DEFAULT_COMMON_LOOT = List.of(
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

    /** 稀有品质默认物品池（染梦高级材料与中级装备） */
    public static final List<String> DEFAULT_RARE_LOOT = List.of(
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
            // 夜空交互纪念品：羽星占卜图录 / 星空枕（稀有档也有机会开出）
            "pasterdream:memento_item_03 1 6",
            "pasterdream:memento_item_08 1 6"
    );

    /** 传说品质默认物品池（染梦维度顶级装备与稀有材料） */
    public static final List<String> DEFAULT_LEGENDARY_LOOT = List.of(
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
            // 夜空交互纪念品：羽星占卜图录 / 星空枕（传说档高概率开出）
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
