package com.pasterdream.pasterdreammod.dreamnotes;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.function.Consumer;

/**
 * 寻梦者笔记自定义数据（1.21 {@link DataComponents#CUSTOM_DATA}）。
 * <p>
 * 键名与原版 NBT 完全一致：{@code switch} / {@code x} / {@code z}。
 */
public final class DreamnotesData {

    private DreamnotesData() {
    }

    public static boolean getBoolean(ItemStack stack, String key) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(key);
    }

    public static double getDouble(ItemStack stack, String key) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble(key);
    }

    public static void update(ItemStack stack, Consumer<CompoundTag> updater) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
    }

    public static void putBoolean(ItemStack stack, String key, boolean value) {
        update(stack, tag -> tag.putBoolean(key, value));
    }

    public static void putDouble(ItemStack stack, String key, double value) {
        update(stack, tag -> tag.putDouble(key, value));
    }
}
