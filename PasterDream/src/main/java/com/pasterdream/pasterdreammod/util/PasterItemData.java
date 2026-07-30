package com.pasterdream.pasterdreammod.util;

import com.pasterdream.pasterdreammod.api.util.CustomItemData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

/**
 * 帕斯特物品自定义数据 —— 工坊/法杖等业务键的主模入口。
 * <p>
 * 底层读写委托 API {@link CustomItemData}。本类保留原版键名文档：
 * <ul>
 *   <li>{@code process} —— 原胚工序（0 煅烧 / 1 锤炼 / 2 淬火 / 3 打磨）</li>
 *   <li>{@code level} —— 磨石打磨层数（0-9）</li>
 *   <li>{@code paster_attack_damage(_number)} 等 —— 工坊强化属性</li>
 * </ul>
 */
public final class PasterItemData {

    private PasterItemData() {
    }

    public static double getDouble(ItemStack stack, String key) {
        return CustomItemData.getDouble(stack, key);
    }

    public static boolean getBoolean(ItemStack stack, String key) {
        return CustomItemData.getBoolean(stack, key);
    }

    public static void update(ItemStack stack, Consumer<CompoundTag> updater) {
        CustomItemData.update(stack, updater);
    }

    public static void putDouble(ItemStack stack, String key, double value) {
        CustomItemData.putDouble(stack, key, value);
    }

    public static void putBoolean(ItemStack stack, String key, boolean value) {
        CustomItemData.putBoolean(stack, key, value);
    }
}
