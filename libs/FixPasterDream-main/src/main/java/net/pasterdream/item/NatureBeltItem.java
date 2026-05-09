
package net.pasterdream.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.pasterdream.init.PasterdreamModAttributes;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.UUID;

public class NatureBeltItem extends Item implements ICurioItem {
	public NatureBeltItem() {
		super(new Item.Properties().durability(200).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7f\u666E\u901A \u2605"));
	}
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        HashMultimap<Attribute, AttributeModifier> map = HashMultimap.create();
        map.put(PasterdreamModAttributes.SAN_VARIABILITY.get(),new AttributeModifier(uuid,"pasterdream.naturebelt.san_variability",0.48, AttributeModifier.Operation.ADDITION));
        return map;
    }
	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (Mth.nextDouble(slotContext.entity().getRandom(),0,1) < 0.004) {
            if (stack.hurt(1, RandomSource.create(), null)) {
                stack.shrink(1);
                stack.setDamageValue(0);
            }
        }
	}
}
