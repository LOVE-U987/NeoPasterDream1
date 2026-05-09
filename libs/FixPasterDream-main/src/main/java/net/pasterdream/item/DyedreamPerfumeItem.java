
package net.pasterdream.item;

import net.pasterdream.procedures.DyedreamPerfumePr0Procedure;
import net.pasterdream.init.PasterdreamModItems;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;

import java.util.List;

public class DyedreamPerfumeItem extends Item {
	public DyedreamPerfumeItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(0).saturationMod(0f).build()));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.DRINK;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A77\u996E\u7528\u540E\u83B7\u5F97\u6548\u679C:"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u8BA9\u8718\u86DB\u8FDC\u79BB\u4F60 (1:00)"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u6E05\u7A7A\u672A\u7761\u7720\u7D2F\u8BA1\u91CF\u8868"));
		list.add(Component.literal("\u00A77\u00A7o\u5F53\u4F60\u5728\u601D\u8003\u4E3A\u4EC0\u4E48\u9999\u6C34\u8981\u7528\u6765\u559D\u65F6 \u6216\u8BB8\u5E94\u8BE5\u5148\u8003\u8651\u4F60\u73B0\u5728\u6B63\u5728\u505A\u68A6\uFF1F"));
	}

	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = new ItemStack(PasterdreamModItems.GLASSJAR.get());
		super.finishUsingItem(itemstack, world, entity);
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		DyedreamPerfumePr0Procedure.execute(entity);
		if (itemstack.isEmpty()) {
			return retval;
		} else {
			if (entity instanceof Player player && !player.getAbilities().instabuild) {
				if (!player.getInventory().add(retval))
					player.drop(retval, false);
			}
			return itemstack;
		}
	}
}
