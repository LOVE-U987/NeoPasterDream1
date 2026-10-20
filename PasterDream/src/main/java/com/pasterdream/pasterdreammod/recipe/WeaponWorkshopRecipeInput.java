package com.pasterdream.pasterdreammod.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * 精铸工坊锻造配方输入（材料槽 0-4 的 5 格只读视图）
 * <p>
 * 供 {@link WeaponWorkshopRecipe#matches} 使用的 {@link RecipeInput} 实现，
 * 仅暴露 {@code ItemStackHandler} 的前 5 个材料槽（强化石槽 5 与产物槽 6 不参与配方匹配）。
 */
public class WeaponWorkshopRecipeInput implements RecipeInput {

    /** 材料槽位数（与 {@code WeaponWorkshopBlockEntity} 的槽 0-4 一致） */
    public static final int SLOT_COUNT = 5;

    /** 5 格材料只读快照 */
    private final NonNullList<ItemStack> items;

    /**
     * 构造锻造配方输入
     *
     * @param items 材料栈列表（应恰好 {@link #SLOT_COUNT} 个）
     */
    public WeaponWorkshopRecipeInput(NonNullList<ItemStack> items) {
        this.items = items;
    }

    /**
     * 从精铸工坊库存处理器构建配方输入（取材料槽 0-4）
     *
     * @param handler 工坊 7 格库存处理器
     * @return 5 格材料输入视图
     */
    public static WeaponWorkshopRecipeInput of(ItemStackHandler handler) {
        NonNullList<ItemStack> list = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        for (int i = 0; i < SLOT_COUNT; i++) {
            list.set(i, handler.getStackInSlot(i));
        }
        return new WeaponWorkshopRecipeInput(list);
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public int size() {
        return SLOT_COUNT;
    }
}
