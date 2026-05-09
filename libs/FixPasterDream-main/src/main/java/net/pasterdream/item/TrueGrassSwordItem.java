
package net.pasterdream.item;

import net.pasterdream.procedures.TrueGrassSwordPr1Procedure;
import net.pasterdream.procedures.TrueGrassSwordPr0Procedure;
import net.pasterdream.init.PasterdreamModItems;

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

public class TrueGrassSwordItem extends SwordItem {
	public TrueGrassSwordItem() {
		super(new Tier() {
			public int getUses() {
				return 1311;
			}

			public float getSpeed() {
				return 1f;
			}

			public float getAttackDamageBonus() {
				return 3.5f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 16;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(PasterdreamModItems.JUNGLE_SPORE.get()));
			}
		}, 3, -2.5f, new Item.Properties());
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		TrueGrassSwordPr0Procedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity, itemstack);
		return retval;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		TrueGrassSwordPr1Procedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, ar.getObject());
		return ar;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u9576\u5D4C\uFF1A\u00A77200\u4EA1\u9B42"));
		list.add(Component.literal("\u6218\u6280\uFF1A\u00A7b\u6781\u81F4\u950B\u5229"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u9996\u6B21\u653B\u51FB\u76EE\u6807\u65F6\u89E6\u53D1"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u653B\u51FB\u9020\u62107+\u6B66\u5668\u950B\u5229\u7B49\u7EA7*\u6B66\u5668\u57FA\u7840\u9762\u677F/2\u70B9\u4F24\u5BB3"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u51B7\u5374\u65F6\u95F4\uFF1A4\u79D2"));
		list.add(Component.literal("\u88AB\u52A8\uFF1A"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u653B\u51FB\u65BD\u52A0\u66F4\u957F\u65F6\u95F4\u7684\u4E2D\u6BD2\u6548\u679C"));
	}
}
