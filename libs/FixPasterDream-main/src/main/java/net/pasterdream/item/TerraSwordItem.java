
package net.pasterdream.item;

import net.pasterdream.procedures.TerraSwordPr1Procedure;
import net.pasterdream.procedures.TerraSwordPr0Procedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TerraSwordItem extends SwordItem {
	public TerraSwordItem() {
		super(new Tier() {
			public int getUses() {
				return 2011;
			}

			public float getSpeed() {
				return 4f;
			}

			public float getAttackDamageBonus() {
				return 4f;
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
		}, 3, -2.4f, new Item.Properties());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		TerraSwordPr1Procedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, ar.getObject());
		return ar;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u6218\u6280\uFF1A\u00A7a\u6CF0\u62C9\u5251\u6280"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u6280\u80FD\u5F00\u542F\u540E\u5171\u53EF\u5411\u81EA\u8EAB\u671D\u5411\u6325\u51FA3\u6B21\u5251\u6C14"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5BF9\u5251\u6C14\u9014\u5F84\u8303\u56F4\u7684\u6240\u6709\u654C\u4EBA\u9020\u6210\u4F24\u5BB3\u5E76\u5C0F\u5E45\u51FB\u98DE"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5251\u6C14\u9020\u62102+\u5F53\u524D\u653B\u51FB\u529B*0.7\u70B9\u4F24\u5BB3"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u7B2C\u4E09\u6BB5\u5251\u6C14\u53D7\u5230\u7684\u7684\u5C5E\u6027\u653B\u51FB\u529B\u52A0\u6210\u7FFB\u500D"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u51B7\u5374\u65F6\u95F4\uFF1A5\u79D2"));
		list.add(Component.literal("\u00A77\u25AA \u00A74\u878D\u68A6\u80FD\u91CF\u6D88\u8017\uFF1A0.3"));
		list.add(Component.literal("\u00A77\u53F3\u952E\u4EE5\u5F3A\u5316\u4E0B3\u6B21\u653B\u51FB\u4E3A\u5251\u6C14\u653B\u51FB"));
	}

	@Override
	public boolean onEntitySwing(ItemStack itemstack, LivingEntity entity) {
		boolean retval = super.onEntitySwing(itemstack, entity);
		TerraSwordPr0Procedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity, itemstack);
		return retval;
	}
}
