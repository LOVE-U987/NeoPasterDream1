
package net.pasterdream.item;

import net.pasterdream.procedures.WhiteSwordPr0Procedure;
import net.pasterdream.init.PasterdreamModBlocks;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

import java.util.List;

public class WhiteSwordItem extends SwordItem {
	public WhiteSwordItem() {
		super(new Tier() {
			public int getUses() {
				return 1771;
			}

			public float getSpeed() {
				return 0f;
			}

			public float getAttackDamageBonus() {
				return 5f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 10;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(PasterdreamModBlocks.SHADOW_LIGHT_0.get()));
			}
		}, 3, -2.4f, new Item.Properties().fireResistant());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		WhiteSwordPr0Procedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity);
		return ar;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u6218\u6280\uFF1A\u00A7f\u767D\u5384\u5251\u96E8"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u6280\u80FD\u5F00\u542F\u65F6\u6807\u8BB0\u5728\u89C6\u70B9\u65B9\u54118\u683C\u5916\u7684\u4F4D\u7F6E"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5728\u76F4\u5F84\u4E3A7\u7684\u533A\u57DF\u91CA\u653E\u591A\u8F6E\u5251\u96E8"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u6BCF\u53D1\u5251\u96E8\u9020\u62103+\u653B\u51FB\u529B\u5C5E\u6027\u503C*0.4\u70B9\u4F24\u5BB3\u5E76\u5C06\u5176\u675F\u7F1A"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u4E14\u6BCF\u53D1\u670912%\u7684\u6982\u7387\u4F7F\u6697\u5F71\u751F\u7269\u6C89\u9ED810\u79D2"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5BF9BOSS\u7C7B\u6697\u5F71\u751F\u7269\u6982\u7387\u51CF\u534A"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u51B7\u5374\u65F6\u95F4\uFF1A4.2\u79D2"));
		list.add(Component.literal("\u00A77\u25AA \u00A74\u878D\u68A6\u80FD\u91CF\u6D88\u8017\uFF1A0.1"));
	}
}
