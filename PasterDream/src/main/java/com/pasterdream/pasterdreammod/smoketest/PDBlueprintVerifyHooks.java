package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.data.BluePrintLoader;
import com.pasterdream.pasterdreammod.item.BlueprintItem;
import com.pasterdream.pasterdreammod.registry.PDMenusBlueprint;
import com.pasterdream.pasterdreammod.registry.items.PDItemsBlueprint;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 蓝图阅览子系统运行时校验钩子（供 PDPortingVerifyTest / 控制台调用，避免改共享测试文件）。
 * <p>
 * 用法示例：在游戏内命令或冒烟测试中调用 {@link #verifyAll()}，返回失败消息列表（空=通过）。
 */
public final class PDBlueprintVerifyHooks {

    private PDBlueprintVerifyHooks() {
    }

    /**
     * 运行全部可自动断言检查
     *
     * @return 失败说明；空列表表示全部通过
     */
    public static List<String> verifyAll() {
        List<String> failures = new ArrayList<>();
        verifyRegistry(failures);
        verifyLoadedBlueprints(failures);
        return failures;
    }

    /**
     * 校验物品/菜单是否已注册
     *
     * @param failures 失败收集
     */
    public static void verifyRegistry(List<String> failures) {
        try {
            if (PDMenusBlueprint.BLUEPRINT_GUI_0.get() == null) {
                failures.add("MenuType blueprint_gui_0 is null");
            }
        } catch (Exception e) {
            failures.add("MenuType blueprint_gui_0 not available: " + e.getMessage());
        }

        checkItem(failures, "blueprint_0", PDItemsBlueprint.BLUEPRINT_0.get());
        checkItem(failures, "blueprint_1", PDItemsBlueprint.BLUEPRINT_1.get());

        Item b0 = BuiltInRegistries.ITEM.get(ResourceLocation.parse("pasterdream:blueprint_0"));
        if (b0 == null || b0 == Items.AIR || !(b0 instanceof BlueprintItem bp0)) {
            failures.add("Registry item blueprint_0 missing or not BlueprintItem");
        } else if (!bp0.getBlueprintId().equals(ResourceLocation.parse("pasterdream:shadow_blast_furnace"))) {
            failures.add("blueprint_0 id mismatch: " + bp0.getBlueprintId());
        }

        Item b1 = BuiltInRegistries.ITEM.get(ResourceLocation.parse("pasterdream:blueprint_1"));
        if (b1 == null || b1 == Items.AIR || !(b1 instanceof BlueprintItem bp1)) {
            failures.add("Registry item blueprint_1 missing or not BlueprintItem");
        } else if (!bp1.getBlueprintId().equals(ResourceLocation.parse("pasterdream:weapon_workshop"))) {
            failures.add("blueprint_1 id mismatch: " + bp1.getBlueprintId());
        }
    }

    /**
     * 校验数据包加载后的蓝图内容
     *
     * @param failures 失败收集
     */
    public static void verifyLoadedBlueprints(List<String> failures) {
        Map<ResourceLocation, BluePrintLoader.BluePrint> all = BluePrintLoader.getAll();
        if (all.isEmpty()) {
            failures.add("BluePrintLoader map is empty (datapack not reloaded yet?)");
            return;
        }

        BluePrintLoader.BluePrint furnace = BluePrintLoader.get(
                ResourceLocation.parse("pasterdream:shadow_blast_furnace"));
        if (furnace == null) {
            failures.add("missing blueprint pasterdream:shadow_blast_furnace");
        } else if (furnace.getMaxPage() != 3) {
            failures.add("shadow_blast_furnace pages=" + furnace.getMaxPage() + " expected 3");
        } else {
            Item core = furnace.get(1).get(12);
            ResourceLocation coreId = BuiltInRegistries.ITEM.getKey(core);
            if (coreId == null || !"pasterdream:shadow_blast_furnace_core".equals(coreId.toString())) {
                failures.add("shadow_blast_furnace page1 slot12 expected core, got " + coreId);
            }
        }

        BluePrintLoader.BluePrint workshop = BluePrintLoader.get(
                ResourceLocation.parse("pasterdream:weapon_workshop"));
        if (workshop == null) {
            failures.add("missing blueprint pasterdream:weapon_workshop");
        } else if (workshop.getMaxPage() != 4) {
            failures.add("weapon_workshop pages=" + workshop.getMaxPage() + " expected 4");
        } else {
            Item table = workshop.get(1).get(8);
            ResourceLocation tableId = BuiltInRegistries.ITEM.getKey(table);
            if (tableId == null || !"pasterdream:weapon_table".equals(tableId.toString())) {
                failures.add("weapon_workshop page1 slot8 expected weapon_table, got " + tableId);
            }
        }
    }

    private static void checkItem(List<String> failures, String name, Item item) {
        if (item == null || item == Items.AIR) {
            failures.add("PDItemsBlueprint." + name + " is empty");
        }
    }
}
