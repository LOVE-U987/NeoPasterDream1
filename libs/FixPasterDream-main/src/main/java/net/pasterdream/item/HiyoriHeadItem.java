
package net.pasterdream.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.pasterdream.init.PasterdreamModAttributes;



import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.UUID;

public class HiyoriHeadItem extends Item implements ICurioItem {
	public HiyoriHeadItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

    public static final UUID _UUID = UUID.fromString("7db7368f-03bf-46cb-b7ae-ca8b2777f4f7");
	public static final AttributeModifier modifier0 = new AttributeModifier(_UUID, "generic.maxHealth", 2.0F, AttributeModifier.Operation.ADDITION);
	public static final AttributeModifier modifier1 = new AttributeModifier(_UUID, "pasterdream.luck", 15.0F, AttributeModifier.Operation.ADDITION);
    public static final AttributeModifier modifier2 = new AttributeModifier(_UUID, "pasterdream.san_variability", 0.96, AttributeModifier.Operation.ADDITION);

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7d\u53F2\u8BD7 \u2605\u2605\u2605\u2605\u2605\u2605"));
		list.add(Component.literal("\u00A77\u00A7o-- \u56E0\u4E3A\u5E38\u89C4\u8131\u79BB \u6240\u4EE5creative"));
	}
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if(slotContext.entity() != null)
        {
            return CuriosApi.getCuriosInventory(slotContext.entity()).map(handler ->
                    handler.findFirstCurio(stack.getItem()).isEmpty())
                    .orElse(true);
        }
        return true;
    }
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext,
                                                                 UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> attributeModifiers = HashMultimap.create();
        attributeModifiers.put(Attributes.MAX_HEALTH,modifier0);
        attributeModifiers.put(PasterdreamModAttributes.LUCK.get(),modifier1);
        attributeModifiers.put(PasterdreamModAttributes.SAN_VARIABILITY.get(),modifier2);
        return attributeModifiers;
    }
}
