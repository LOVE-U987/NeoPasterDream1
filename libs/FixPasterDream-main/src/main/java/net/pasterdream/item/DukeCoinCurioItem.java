
package net.pasterdream.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.pasterdream.init.PasterdreamModTabs;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraftforge.common.ForgeMod;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.UUID;

public class DukeCoinCurioItem extends Item implements ICurioItem {
	public DukeCoinCurioItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	public static final UUID LUCK_UUID = UUID.fromString("da309091-9a17-45e1-8366-2b1a0bd55625");
	public static final AttributeModifier modifier0 = new AttributeModifier(LUCK_UUID, "pasterdream.luck", 7.0F, AttributeModifier.Operation.ADDITION);

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7a\u4F18\u79C0 \u2605\u2605"));
		list.add(Component.literal("\u00A77\u00A7o-- 7 Days to Die"));
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
        attributeModifiers.put(PasterdreamModAttributes.LUCK.get(),modifier0);
        attributeModifiers.put(PasterdreamModAttributes.TELEPORTATIONCD.get(),modifier0);
        return attributeModifiers;
    }
}
