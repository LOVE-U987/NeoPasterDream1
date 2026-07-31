package com.pasterdream.pasterdreammod.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.entity.EquipmentSlot;

/**
 * Nature Belt Item (Curio Item)
 */
public class NatureBeltItem extends Item implements ICurioItem {

    public NatureBeltItem() {
        super(new Item.Properties().stacksTo(1).durability(200).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7f\u666E\u901A \u2605"));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 仅服务端执行物品变更，避免双端不同步
        if (slotContext.entity().level().isClientSide()) {
            return;
        }
        // 原版逻辑：随机 0.4% 触发一次损耗；耐久耗尽时 hurtAndBreak 自动销毁（等价原版 hurt 返回 true 后 shrink）
        if (Mth.nextDouble(slotContext.entity().getRandom(), 0, 1) < 0.004
                && slotContext.entity().level() instanceof ServerLevel serverLevel) {
            stack.hurtAndBreak(1, serverLevel, null,
                    item -> CuriosApi.broadcastCurioBreakEvent(slotContext));
        }
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributeModifiers = HashMultimap.create();
        // 由 Curios 传入的槽位 id + 槽位索引派生 modifier id，保证同属性多槽位装备不冲突（1.21 按 id 键控）
        ResourceLocation modifierId = id.withSuffix("/" + slotContext.index() + "_san_var");
        attributeModifiers.put(com.pasterdream.pasterdreammod.registry.PDAttributes.SAN_VARIABILITY,
                new AttributeModifier(modifierId, 0.48, AttributeModifier.Operation.ADD_VALUE));
        return attributeModifiers;
    }
}
