package com.pasterdream.pasterdreammod.compat;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.compat.IdAliasTable;
import com.pasterdream.pasterdreammod.api.compat.IdCompatAPI;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

/**
 * 主模块「旧注册名 → 新注册名」别名表。
 * <p>
 * 数据来源为 Phase 0 取证清单：对比初始全量移植提交与当前 HEAD 的注册名字面量，
 * 仅收录<b>移植版自身早期曾注册、现已改名/合并</b>的旧 ID；原版模组名（如
 * {@code qym_armor_boots}）不属本表范围。
 * <p>
 * 证据与完整登记见 {@code docs/设计/存档兼容层.md}。
 * <p>
 * <b>维护约定</b>：每次对已发布内容改名时，在此追加旧→新映射，并在设计文档登记证据。
 */
public final class PDIdAliases {

    private PDIdAliases() {
        throw new UnsupportedOperationException("PDIdAliases 是常量表工具类，不可实例化");
    }

    /**
     * 构造并登记全部别名。
     * <p>
     * 必须在 {@code RegisterEvent} 之前调用（主模构造器内）。
     */
    public static void registerAll() {
        IdCompatAPI.registerAll(build());
    }

    /**
     * 构建别名表。
     *
     * @return 别名表
     */
    public static IdAliasTable build() {
        IdAliasTable table = new IdAliasTable();

        // 风之骑士唤醒台：早期移植版注册 5 个独立方块/物品（wind_knight_spawnblock_0..4），
        // 后合并为单一 wind_knight_spawnblock（样式改由方块 STAGE 属性决定）。
        // 逐条展开，便于静态工具（tools/verify_id_aliases.py）解析与审计。
        aliasBlockAndItem(table, "wind_knight_spawnblock_0", "wind_knight_spawnblock");
        aliasBlockAndItem(table, "wind_knight_spawnblock_1", "wind_knight_spawnblock");
        aliasBlockAndItem(table, "wind_knight_spawnblock_2", "wind_knight_spawnblock");
        aliasBlockAndItem(table, "wind_knight_spawnblock_3", "wind_knight_spawnblock");
        aliasBlockAndItem(table, "wind_knight_spawnblock_4", "wind_knight_spawnblock");

        // 融梦水晶灯 → 染梦水晶灯（dcb340c1：移除融梦水晶灯，替换为染梦水晶灯）
        aliasBlockAndItem(table, "meltdream_crystal_lamp", "dyedream_lantern");

        // 银狐棉花糖：拼音拼写修正（yinhul → silver_fox）
        table.add(Registries.ITEM, id("yinhul_cotton_candy"), id("silver_fox_cotton_candy"));

        // 调试装饰杖改名（水晶芽 → 芽）
        table.add(Registries.ITEM, id("debug_wand_ice_crystal_cluster"), id("debug_wand_bud_ice"));
        table.add(Registries.ITEM, id("debug_wand_ice_crystal_garden"), id("debug_wand_bud_dyedream"));

        // 风泊树叶：纹理随机化方案修正。早期移植版误用三个独立方块（windmoor_leaves_0/1/2）
        // 模拟纹理随机，现合并为单一 windmoor_leaves（blockstate 加权随机模型）；
        // 其中 _2（无碰撞悬挂变体）独立为 windmoor_hanging_vine（垂钓植被，向下生长 fig_vine）。
        aliasBlockAndItem(table, "windmoor_leaves_0", "windmoor_leaves");
        aliasBlockAndItem(table, "windmoor_leaves_1", "windmoor_leaves");
        aliasBlockAndItem(table, "windmoor_leaves_2", "windmoor_hanging_vine");

        // 暗影书架：早期移植版注册四个独立方块（shadowshelf_0..3），
        // 现 0/1/2 合并为单一 shadowshelf（blockstate 随机纹理），
        // _3（带钥匙纹理变体）独立为 shadowshelf_with_key（掉落暗影地牢钥匙）。
        aliasBlockAndItem(table, "shadowshelf_0", "shadowshelf");
        aliasBlockAndItem(table, "shadowshelf_1", "shadowshelf");
        aliasBlockAndItem(table, "shadowshelf_2", "shadowshelf");
        aliasBlockAndItem(table, "shadowshelf_3", "shadowshelf_with_key");

        return table;
    }

    /**
     * 同时登记方块与物品注册表的同一条别名（BlockItem 场景）。
     *
     * @param table 别名表
     * @param from  旧注册名
     * @param to    新注册名
     */
    private static void aliasBlockAndItem(IdAliasTable table, String from, String to) {
        table.add(Registries.BLOCK, id(from), id(to));
        table.add(Registries.ITEM, id(from), id(to));
    }

    /**
     * 构造模组命名空间下的注册名。
     *
     * @param path 注册路径
     * @return ResourceLocation
     */
    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path);
    }
}
