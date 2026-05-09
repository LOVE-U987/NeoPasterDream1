
package net.pasterdream.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.pasterdream.procedures.Fire0NecklacePr0Procedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class Fire0NecklaceItem extends Item implements ICurioItem {
	public Fire0NecklaceItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7f\u666E\u901A \u2605"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u884C\u8D70\u4F1A\u7559\u4E0B\u706B\u7130"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5904\u4E8E\u71C3\u70E7\u72B6\u6001\u4E0B\u83B7\u5F97\u6025\u8FEBI\u6548\u679C"));
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		Fire0NecklacePr0Procedure.execute(slotContext.entity().level(), slotContext.entity().getX(), slotContext.entity().getY(), slotContext.entity().getZ(), slotContext.entity());
	}
}
