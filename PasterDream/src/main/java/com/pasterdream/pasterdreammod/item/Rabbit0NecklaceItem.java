package com.pasterdream.pasterdreammod.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import java.util.List;
import java.util.UUID;

/**
 * Rabbit0 Necklace Item (Curio Item)
 */
public class Rabbit0NecklaceItem extends Item implements ICurioItem {

    public Rabbit0NecklaceItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
}

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.translatable("tooltip.pasterdream.rabbit0_necklace.quality"));
        list.add(Component.translatable("tooltip.pasterdream.rabbit0_necklace.effect_1"));
}

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity == null) return;
        if (!entity.level().isClientSide) {
            entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 2, 0, false, false, true));
        }
    }
}
