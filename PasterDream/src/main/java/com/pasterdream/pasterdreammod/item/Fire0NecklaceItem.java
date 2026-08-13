package com.pasterdream.pasterdreammod.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import java.util.List;
import java.util.UUID;

/**
 * Fire0 Necklace Item (Curio Item)
 * <p>
 * 对齐原版 {@code Fire0NecklacePr0Procedure}：佩戴时在脚下点燃火焰；
 * 自身着火时获得急迫 I（2 tick）。配置 {@code ban fire necklace} 关闭时
 * 不执行任何效果，并向玩家提示“此物品已被禁用”。
 */
public class Fire0NecklaceItem extends Item implements ICurioItem {

    public Fire0NecklaceItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
}

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.translatable("tooltip.pasterdream.fire0_necklace.quality"));
        list.add(Component.translatable("tooltip.pasterdream.fire0_necklace.effect_1"));
        list.add(Component.translatable("tooltip.pasterdream.fire0_necklace.effect_2"));
}

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity == null) return;
        // 配置禁用：不再执行任何效果，并向玩家提示（对齐原版 Fire0NecklacePr0Procedure）
        if (Boolean.TRUE.equals(PDCommonConfig.BAN_FIRE_NECKLACE.get())) {
            if (entity instanceof Player player && !player.level().isClientSide) {
                player.displayClientMessage(Component.literal("\u00A74此物品已被禁用"), true);
            }
            return;
        }
        if (!entity.level().isClientSide) {
            // 在原版逻辑中于脚下点燃火焰（对齐 Fire0NecklacePr0Procedure：脚下为空气时生成火焰）
            if (entity.level().getBlockState(entity.blockPosition().below()).isAir()) {
                entity.level().setBlock(entity.blockPosition().below(), net.minecraft.world.level.block.Blocks.FIRE.defaultBlockState(), 3);
            }
            if (entity.isOnFire()) {
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2, 0, false, false, true));
            }
        }
    }
}
