
package net.pasterdream.item;

import net.pasterdream.procedures.TrueMoltengoldPickaxePr0Procedure;
import net.pasterdream.init.PasterdreamModItems;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TrueMoltengoldPickaxeItem extends PickaxeItem {
	public TrueMoltengoldPickaxeItem() {
		super(new Tier() {
			public int getUses() {
				return 1255;
			}

			public float getSpeed() {
				return 16f;
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
		}, 1, -2.6f, new Item.Properties().fireResistant());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		TrueMoltengoldPickaxePr0Procedure.execute(entity, ar.getObject());
		return ar;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u9576\u5D4C\uFF1A\u00A77\u65E0"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u53F3\u952E\u4F7F\u7528\u5C06\u6D88\u8017\u9971\u98DF\u5EA6\u6765\u83B7\u5F97\u8F83\u77ED\u65F6\u95F4\u7684\u6025\u8FEB\u6548\u679C"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u51B7\u5374\u65F6\u95F4\uFF1A8\u79D2"));
	}
}
