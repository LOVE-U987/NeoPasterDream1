
package net.pasterdream.item;

import net.pasterdream.procedures.IceshadowHammerPr0Procedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;

import java.util.List;

public class IceshadowHammerItem extends SwordItem {
	public IceshadowHammerItem() {
		super(new Tier() {
			public int getUses() {
				return 835;
			}

			public float getSpeed() {
				return 4f;
			}

			public float getAttackDamageBonus() {
				return 9f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 2;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of();
			}
		}, 3, -3.3f, new Item.Properties().fireResistant());
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u6218\u6280\uFF1A\u00A73\u4F9D\u8BFA\u7684\u64BC\u5730"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u4F7F\u7528\u6280\u80FD\u540E\u8D77\u8DF3\u5E76\u5BF9\u653B\u51FB\u5730\u70B9\u53D1\u52A8\u64BC\u5730"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5728\u76F4\u5F84\u4E3A5\u7684\u533A\u57DF\u5185\u9020\u6210\u6DF7\u4E71\u548C3\u6BB5\u4F24\u5BB3"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5206\u522B\u9020\u6210\u653B\u51FB\u529B*0.5/*0.7/*0.4\u70B9\u4F24\u5BB3"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u4E14\u6280\u80FD\u53D1\u52A80.5\u79D2\u540E\u5982\u679C\u8303\u56F4\u5185\u76EE\u6807\u6B63\u5728\u79FB\u52A8\u5219\u4F1A\u88AB\u64BC\u5730\u6C34\u6676\u51FB\u98DE"));
		list.add(Component.literal("\u00A77\u53F3\u952E\u5730\u9762\u4EE5\u91CA\u653E\u6280\u80FD"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u51B7\u5374\u65F6\u95F4\uFF1A4\u79D2"));
		list.add(Component.literal("\u00A77\u25AA \u00A74\u878D\u68A6\u80FD\u91CF\u6D88\u8017\uFF1A0.1"));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		IceshadowHammerPr0Procedure.execute(context.getLevel(), context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ(), context.getPlayer(), context.getItemInHand());
		return InteractionResult.SUCCESS;
	}
}
