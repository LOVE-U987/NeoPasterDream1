package com.pasterdream.pasterdreammod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * 玻璃罐饮品物品类。
 * <p>
 * 在 {@link GlassDrinkItem} 的饮用返还行为之上，额外声明合成残留：
 * 该物品作为合成材料被消耗时，返还一个空的玻璃罐。
 * 残留物在合成结算时才通过 {@code jarSupplier} 取值，
 * 因此不依赖注册期 {@code DeferredHolder} 的绑定状态，也不改变饮用返还逻辑。
 * </p>
 */
public class GlassJarDrinkItem extends GlassDrinkItem {

    /** 合成残留与饮用返还的物品供应者（通常是空玻璃罐） */
    private final Supplier<Item> jarSupplier;

    /**
     * 构造玻璃罐饮品物品。
     *
     * @param properties  物品属性，包含食物属性
     * @param jarSupplier 合成残留与饮用返还的物品供应者（如空玻璃罐）
     */
    public GlassJarDrinkItem(Properties properties, Supplier<Item> jarSupplier) {
        super(properties, jarSupplier);
        this.jarSupplier = jarSupplier;
    }

    /**
     * 声明该物品作为合成材料时存在残留物。
     *
     * @param stack 物品栈
     * @return 恒为 {@code true}
     */
    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    /**
     * 返回合成残留物，即一个空玻璃罐。
     *
     * @param stack 物品栈
     * @return 数量为 1 的玻璃罐物品栈
     */
    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return new ItemStack(this.jarSupplier.get());
    }
}
