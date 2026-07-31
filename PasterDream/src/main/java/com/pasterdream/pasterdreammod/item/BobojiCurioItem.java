package com.pasterdream.pasterdreammod.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

/**
 * Boboji Curio Item (Curio Item)
 */
public class BobojiCurioItem extends Item implements ICurioItem {

    public BobojiCurioItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
}


    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7c\u4F20\u8BF4 \u2605\u2605\u2605\u2605\u2605\u2605\u2605"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u88C5\u5907\u9798\u7FC5\u65F6\u4E0D\u518D\u5EF6\u957F\u77AC\u8EAB\u672F\u51B7\u5374\u65F6\u95F4"));
        list.add(Component.literal("\u00a77\u25aa \u00a79\u4f7f\u7528\u77ac\u8eab\u672f\u65f6\u53ef\u4ee5\u57280.25\u79d2\u5185\u56de\u907f\u4e00\u6b21\u4f24\u5bb3"));
        list.add(Component.literal("\u00a77\u25aa \u00a79\u5982\u9644\u8fd1\u5b58\u5728\u73a9\u5bb6\u5219\u56de\u907f\u65f6\u95f4\u5c06\u5ef6\u957f\u4e00\u500d\u5e76\u5206\u4eab\u7ed9\u9644\u8fd1\u961f\u53cb"));
        list.add(Component.literal("\u00A77\u25AA \u00A7e\u77AC\u8EAB\u672F\u589E\u52A0\u97F3\u6548\u548C\u7C92\u5B50\u62D6\u5C3E"));
        list.add(Component.literal("\u00A77\u00A7o\u4EC5\u4EE5\u6B64\u732E\u7ED9\u66FE\u966A\u4F34\u6211\u7684\u5B83"));
        list.add(Component.literal("\u00A77\u00A7o-- \u4E00\u53EA\u540D\u4E3A\u5575\u5575\u9E21\u7684\u9EC4\u7EFF\u8272\u864E\u76AE\u9E66\u9E49"));
}

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return CurioHelper.canEquipSingleton(slotContext, stack);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id, ItemStack stack) {
        Multimap<Holder<Attribute>, AttributeModifier> attributeModifiers = HashMultimap.create();
        // modifier id 由槽位 id+索引派生，避免同属性多槽位冲突
        ResourceLocation slotKey = id.withSuffix("/" + slotContext.index());
        // 原版为 -0.2（teleportationCd）与 -0.4 两个 consume 修饰符，净效果 -0.6；合并为单一 -0.6 语义等价
        attributeModifiers.put(com.pasterdream.pasterdreammod.registry.PDAttributes.TELEPORTATIONCONSUME, new AttributeModifier(slotKey.withSuffix("_tp_consume"), -0.6, AttributeModifier.Operation.ADD_VALUE));
        attributeModifiers.put(com.pasterdream.pasterdreammod.registry.PDAttributes.TELEPORTATIONRANGE, new AttributeModifier(slotKey.withSuffix("_tp_range"), 0.1, AttributeModifier.Operation.ADD_VALUE));
        attributeModifiers.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(slotKey.withSuffix("_speed"), 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        return attributeModifiers;
    }
}
