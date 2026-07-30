package com.pasterdream.pasterdreammod.api.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.function.Consumer;

/**
 * 物品 {@link DataComponents#CUSTOM_DATA} 读写工具。
 * <p>
 * 对应 1.20 {@code ItemStack#getOrCreateTag()} 的 1.21 数据组件形态。
 * 键名语义由调用方决定（工坊 process/level 等业务键留在主模）。
 */
public final class CustomItemData {

    private CustomItemData() {
    }

    /** 读取 double（缺键为 0） */
    public static double getDouble(ItemStack stack, String key) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble(key);
    }

    /** 读取 boolean（缺键为 false） */
    public static boolean getBoolean(ItemStack stack, String key) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(key);
    }

    /** 读取 int（缺键为 0） */
    public static int getInt(ItemStack stack, String key) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt(key);
    }

    /** 读取 String（缺键为 ""） */
    public static String getString(ItemStack stack, String key) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getString(key);
    }

    /** 是否包含键 */
    public static boolean has(ItemStack stack, String key) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains(key);
    }

    /** 更新 CUSTOM_DATA（等价原版 getOrCreateTag 后写入） */
    public static void update(ItemStack stack, Consumer<CompoundTag> updater) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
    }

    public static void putDouble(ItemStack stack, String key, double value) {
        update(stack, tag -> tag.putDouble(key, value));
    }

    public static void putBoolean(ItemStack stack, String key, boolean value) {
        update(stack, tag -> tag.putBoolean(key, value));
    }

    public static void putInt(ItemStack stack, String key, int value) {
        update(stack, tag -> tag.putInt(key, value));
    }

    public static void putString(ItemStack stack, String key, String value) {
        update(stack, tag -> tag.putString(key, value));
    }

    public static void remove(ItemStack stack, String key) {
        update(stack, tag -> tag.remove(key));
    }
}
