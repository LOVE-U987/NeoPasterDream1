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
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import java.util.List;
import java.util.UUID;

/**
 * Bright Butterfly Curio Item (Curio Item)
 */
public class BrightButterflyCurioItem extends Item implements ICurioItem {

    public BrightButterflyCurioItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
}

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A75\u4E0A\u53E4 \u2605\u2605\u2605\u2605\u2605"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u81EA\u8EAB\u5904\u4E8E\u4F4E\u4EAE\u5EA6\u73AF\u5883\u4E0B\u83B7\u5F97\u591C\u89C6"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u514D\u75AB\u9ED1\u6697\u6548\u679C"));
}

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity == null) return;
        net.minecraft.world.level.Level world = entity.level();
        if (world.isClientSide) return;
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        if (entity instanceof net.minecraft.world.entity.player.Player pl
                && world.getMaxLocalRawBrightness(net.minecraft.core.BlockPos.containing(x, y, z)) <= 7) {
            // Night Vision amp0, 240 ticks (12 seconds)
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.NIGHT_VISION, 240, 0, false, false));
            // Clear darkness
            entity.removeEffect(net.minecraft.world.effect.MobEffects.DARKNESS);
        }
    }

}
