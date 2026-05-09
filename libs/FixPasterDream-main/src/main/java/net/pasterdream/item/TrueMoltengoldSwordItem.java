
package net.pasterdream.item;

import net.pasterdream.procedures.TrueMoltengoldSwordPr0Procedure;
import net.pasterdream.init.PasterdreamModItems;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TrueMoltengoldSwordItem extends SwordItem {
	public TrueMoltengoldSwordItem() {
		super(new Tier() {
			public int getUses() {
				return 1255;
			}

			public float getSpeed() {
				return 1f;
			}

			public float getAttackDamageBonus() {
				return 3f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 23;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(PasterdreamModItems.MOLTENGOLD_INGOT.get()));
			}
		}, 3, -2.2f, new Item.Properties().fireResistant());
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		TrueMoltengoldSwordPr0Procedure.execute(entity);
		return retval;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u9576\u5D4C\uFF1A\u00A77\u65E0"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u653B\u51FB\u5C06\u4F1A\u4F7F\u76EE\u6807\u88AB\u5F15\u71C34\u79D2"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5982\u76EE\u6807\u5DF2\u88AB\u5F15\u71C3 \u5219\u4F1A\u5728\u76EE\u6807\u5F15\u71C3\u7684\u6301\u7EED\u65F6\u95F4\u4E0A\u589E\u52A02\u79D2 \u53EF\u53E0\u52A0"));
	}
}
