package com.pasterdream.pasterdreammod.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.function.Consumer;

/**
 * 帕斯特物品自定义数据读写工具 (Paster Item Data)
 * <p>
 * 1.20 时代的 ItemStack NBT（{@code getOrCreateTag()}）在 1.21.1 已迁移为
 * {@link DataComponents#CUSTOM_DATA} 数据组件；本工具封装武器工坊群
 * procedure 所需的同名键读写，保证键名与原版逐字一致：
 * <ul>
 *   <li>{@code process} —— 原胚工序进度（0 待煅烧 / 1 待锤炼 / 2 待淬火 / 3 待打磨）</li>
 *   <li>{@code level} —— 磨石打磨层数（0-9）</li>
 *   <li>{@code paster_attack_damage(_number)} / {@code paster_attack_speed(_number)}
 *       / {@code paster_movement_speed(_number)} / {@code paster_luck(_number)}
 *       —— 工坊强化属性（未来属性应用模块按同名键读取）</li>
 * </ul>
 */
public final class PasterItemData {

    private PasterItemData() {
    }

    /**
     * 读取物品自定义数据中的 double 值
     *
     * @param stack 物品
     * @param key   NBT 键
     * @return 值（键不存在返回 0）
     */
    public static double getDouble(ItemStack stack, String key) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getDouble(key);
    }

    /**
     * 读取物品自定义数据中的 boolean 值
     *
     * @param stack 物品
     * @param key   NBT 键
     * @return 值（键不存在返回 false）
     */
    public static boolean getBoolean(ItemStack stack, String key) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean(key);
    }

    /**
     * 更新物品自定义数据（等价原版 getOrCreateTag() 后的写入）
     *
     * @param stack   物品
     * @param updater NBT 更新回调
     */
    public static void update(ItemStack stack, Consumer<net.minecraft.nbt.CompoundTag> updater) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, updater);
    }

    /**
     * 写入 double 值
     *
     * @param stack 物品
     * @param key   NBT 键
     * @param value 值
     */
    public static void putDouble(ItemStack stack, String key, double value) {
        update(stack, tag -> tag.putDouble(key, value));
    }

    /**
     * 写入 boolean 值
     *
     * @param stack 物品
     * @param key   NBT 键
     * @param value 值
     */
    public static void putBoolean(ItemStack stack, String key, boolean value) {
        update(stack, tag -> tag.putBoolean(key, value));
    }
}
