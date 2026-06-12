package com.pasterdream.pasterdreammod.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.function.Supplier;

/**
 * 玻璃杯饮品物品类
 * <p>
 * 用于染梦花茶、染梦果汁等使用玻璃杯盛装的饮品，
 * 覆盖 {@link #getUseAnimation(ItemStack)} 返回 {@link UseAnim#DRINK}（饮用动画），
 * 并在 {@link #finishUsingItem(ItemStack, Level, LivingEntity)} 中返还玻璃杯 {@link #returnItem}。
 * </p>
 */
public class GlassDrinkItem extends Item {

    /** 使用后返还的物品供应者（通常是玻璃杯） */
    private final Supplier<Item> returnItem;

    /**
     * 构造玻璃杯饮品物品
     *
     * @param properties 物品属性，包含食物属性 {@link FoodProperties}
     * @param returnItem 使用后返还的物品（如玻璃杯）的延迟供应者
     */
    public GlassDrinkItem(Properties properties, Supplier<Item> returnItem) {
        super(properties);
        this.returnItem = returnItem;
    }

    /**
     * 返回饮用动画，使玩家在食用时显示饮用动作并播放饮用音效
     *
     * @param stack 物品栈
     * @return {@link UseAnim#DRINK} 饮用动画
     */
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    /**
     * 完成食用后的逻辑
     * <p>
     * 调用父类 {@link Item#finishUsingItem(ItemStack, Level, LivingEntity)} 完成食物效果后，
     * 如果物品栈为空则直接返回返还物品，否则尝试将返还物品加入玩家背包，
     * 背包满则掉落在地上。创造模式玩家不返还物品。
     * </p>
     *
     * @param stack  当前物品栈
     * @param level  所处世界
     * @param entity 食用者
     * @return 剩余的物品栈
     */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        super.finishUsingItem(stack, level, entity);
        if (stack.isEmpty()) {
            return new ItemStack(returnItem.get());
        }
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack retval = new ItemStack(returnItem.get());
            if (!player.getInventory().add(retval)) {
                player.drop(retval, false);
            }
        }
        return stack;
    }
}