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
 * 融梦水晶箱战利品配置（玩家自定义物品池解析）。
 * <p>
 * 自定义开关关闭时，默认掉落由数据包战利品表提供（维度主表 / 兜底表）；
 * 开关开启时使用玩家在配置界面（PasterDream-Common.toml 的 "Meltdream Chest" 段）填写的物品池，
 * 逐条容错，留空或全部无效则由调用方回退兜底战利品表。
 * <p>
 * 条目格式（每行一个）：{@code <物品ID> [数量] [权重]}
 * <ul>
 *   <li>物品ID：注册 ID，如 {@code pasterdream:dyedream_ingot} 或 {@code minecraft:diamond}；
 *       无命名空间时优先按 {@code minecraft:} 解析，失败再尝试 {@code pasterdream:}</li>
 *   <li>数量：可选，默认 1，范围 1~64</li>
 *   <li>权重：可选，默认 1，范围 1~9999</li>
 * </ul>
 * 解析失败的条目会被跳过（输出警告日志，不影响其他条目）。
 *
 * @author PasterDream
 */
public final class MeltdreamChestLootConfig {

    private MeltdreamChestLootConfig() {
    }

    /** 物品池条目：物品 + 权重 */
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

    // ==================== 玩家自定义池 ====================

    /**
     * 解析指定品质的玩家自定义物品池（逐条容错，跳过无效条目）。
     * <p>返回空数组表示未配置或全部无效，由调用方决定回退兜底战利品表。</p>
     *
     * @param quality 品质（1=普通, 2=稀有, 3=传说）
     * @return 解析后的物品池数组（可能为空）
     */
    public static LootEntry[] getCustomLoot(int quality) {
        List<String> specs = switch (quality) {
            case 2 -> PDCommonConfig.MELTDREAM_CHEST_RARE_LOOT.get();
            case 3 -> PDCommonConfig.MELTDREAM_CHEST_LEGENDARY_LOOT.get();
            default -> PDCommonConfig.MELTDREAM_CHEST_COMMON_LOOT.get();
        };
        return parsePool(specs);
    }

    // ==================== 内部解析 ====================

    /**
     * 将配置字符串列表解析为物品池数组，跳过无效条目。
     *
     * @param specs 配置条目列表（每行一个）
     * @return 有效条目数组（可能为空）
     */
    private static LootEntry[] parsePool(List<String> specs) {
        List<LootEntry> entries = new ArrayList<>();
        if (specs == null) {
            return entries.toArray(new LootEntry[0]);
        }
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
