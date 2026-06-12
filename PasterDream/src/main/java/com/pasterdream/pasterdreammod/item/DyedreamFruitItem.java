package com.pasterdream.pasterdreammod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 染梦果 (Dyedream Fruit)
 * 食用后获得 5 秒生命恢复 II 效果
 * 原版稀有度: COMMON
 */
public class DyedreamFruitItem extends Item {

    public DyedreamFruitItem(Item.Properties properties) {
        super(properties.stacksTo(64)
                .food(new FoodProperties.Builder()
                        .nutrition(3)
                        .saturationModifier(0.4f)
                        .build()));
}

    @Override
    public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
        ItemStack retval = super.finishUsingItem(itemstack, world, entity);
        if (!world.isClientSide()) {
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
}
        return retval;
    }
}