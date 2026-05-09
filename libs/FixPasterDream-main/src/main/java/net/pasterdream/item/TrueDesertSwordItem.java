
package net.pasterdream.item;

import net.pasterdream.procedures.TrueDesertSwordPr1Procedure;
import net.pasterdream.procedures.TrueDesertSwordPr0Procedure;
import net.pasterdream.procedures.DesertSwordPr0Procedure;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TrueDesertSwordItem extends SwordItem {
	public TrueDesertSwordItem() {
		super(new Tier() {
			public int getUses() {
				return 1561;
			}

			public float getSpeed() {
				return 1f;
			}

			public float getAttackDamageBonus() {
				return 8f;
			}

			public int getLevel() {
				return 1;
			}

			public int getEnchantmentValue() {
				return 8;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of();
			}
		}, 3, -3.1f, new Item.Properties());
	}

	@Override
	public boolean hurtEnemy(ItemStack itemstack, LivingEntity entity, LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		TrueDesertSwordPr0Procedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity, itemstack);
		return retval;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		TrueDesertSwordPr1Procedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity, ar.getObject());
		return ar;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u9576\u5D4C\uFF1A\u00A7e\u6C89\u5BC2\u5BB6\u4E66"));
		list.add(Component.literal("\u6218\u6280\uFF1A\u00A7e\u7EDD\u5730\u53CD\u51FB"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u6280\u80FD\u5F00\u542F\u65F6\u83B7\u5F97\u77ED\u6682\u7684\u751F\u547D\u5438\u6536\u6548\u679C"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u653B\u51FB\u9020\u62105+(\u5DF2\u635F\u5931\u751F\u547D\u767E\u5206\u6BD4*2+1)*\u5F53\u524D\u653B\u51FB\u529B\u70B9\u4F24\u5BB3"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u51B7\u5374\u65F6\u95F4\uFF1A10\u79D2"));
		list.add(Component.literal("\u88AB\u52A8\uFF1A"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u624B\u6301\u6B66\u5668\u65F6\u79FB\u901F-15% \u6297\u6027+20%"));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected)
			DesertSwordPr0Procedure.execute(entity);
	}
}
