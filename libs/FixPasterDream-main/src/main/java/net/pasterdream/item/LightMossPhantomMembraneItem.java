
package net.pasterdream.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.pasterdream.procedures.LightMossPhantomMembranePr0Procedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class LightMossPhantomMembraneItem extends Item implements ICurioItem {
	public LightMossPhantomMembraneItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7a\u4F18\u79C0 \u2605\u2605"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u4E3A\u6B63\u5728\u88C5\u5907\u7684\u9798\u7FC5\u6062\u590D\u8010\u4E45"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5728\u9ED1\u6697\u7684\u73AF\u5883\u4E0B\u8010\u4E45\u6062\u590D\u901F\u5EA6\u51CF\u6162"));
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		LightMossPhantomMembranePr0Procedure.execute(slotContext.entity().level(), slotContext.entity().getX(), slotContext.entity().getY(), slotContext.entity().getZ(), slotContext.entity(), stack);
	}
}
