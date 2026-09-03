package com.pasterdream.pasterdreammod.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import java.util.List;
import java.util.UUID;

/**
 * Snow Vow Head Item (Curio Item)
 * <p>
 * 对齐原版 {@code SnowVowHeadPr0Procedure + SnowVowBuffMobEffect}：
 * 佩戴时持续为佩戴者刷新 {@code snow_vow_buff} 效果（幸运+3、免疫燃烧与冻结）。
 * 设计偏离说明：原版为"直径7格内玩家获得效果"的范围光环，本项目改为仅佩戴者生效。
 */
public class SnowVowHeadItem extends Item implements ICurioItem {

    public SnowVowHeadItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
}

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.translatable("tooltip.pasterdream.snow_vow_head.quality"));
        list.add(Component.translatable("tooltip.pasterdream.snow_vow_head.effect_1"));
        list.add(Component.translatable("tooltip.pasterdream.snow_vow_head.effect_2"));
}

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        entity.addEffect(new MobEffectInstance(
                PDEffects.SNOW_VOW_BUFF.holder(), 20, 0, false, false));
    }

}
