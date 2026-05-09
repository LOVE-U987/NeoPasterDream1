
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

public class FeatherNecklaceItem extends Item implements ICurioItem {
	public FeatherNecklaceItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}


	public static final UUID TELEPORTATIONCD_UUID = UUID.fromString("c017c046-643b-4b28-a9a0-7e8134b54900");
	public static final AttributeModifier modifier0 = new AttributeModifier(TELEPORTATIONCD_UUID, "pasterdream.teleportationCd", -0.05F, AttributeModifier.Operation.ADDITION);
	public static final UUID TELEPORTATIONRANGE_UUID = UUID.fromString("53c0c497-af95-43c7-9d82-46a5916dc442");
	public static final AttributeModifier modifier1 = new AttributeModifier(TELEPORTATIONRANGE_UUID, "pasterdream.teleportationConsume", 0.2F, AttributeModifier.Operation.ADDITION);

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7a\u4F18\u79C0 \u2605\u2605"));
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
        attributeModifiers.put(PasterdreamModAttributes.TELEPORTATIONCONSUME.get(),modifier0);
        attributeModifiers.put(PasterdreamModAttributes.TELEPORTATIONRANGE.get(),modifier1);
        return attributeModifiers;
    }
}