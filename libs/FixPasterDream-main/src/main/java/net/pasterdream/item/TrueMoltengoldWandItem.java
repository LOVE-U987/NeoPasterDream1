
package net.pasterdream.item;

import net.pasterdream.procedures.MoltengoldWandPr1Procedure;
import net.pasterdream.entity.TrueMoltengoldWandProjectileEntity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TrueMoltengoldWandItem extends Item {
	public TrueMoltengoldWandItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
	}

	@Override
	public UseAnim getUseAnimation(ItemStack itemstack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack itemstack) {
		return 72000;
	}

	@Override
	public float getDestroySpeed(ItemStack par1ItemStack, BlockState par2Block) {
		return 0f;
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u6CD5\u672F\u6280\u80FD\uFF1A\u00A76\u5524\u661F"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u6CD5\u7403\u653B\u51FB\u5230\u654C\u4EBA\u6216\u843D\u5730\u65F6 \u5982\u7A7A\u95F4\u5145\u8DB3\u5C06\u4F1A\u5728\u539F\u5730\u91CA\u653E\u5524\u661F\u7167\u660E"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5982\u7A7A\u95F4\u5145\u8DB3\u4E14\u9732\u5929 \u5219\u670920%\u7684\u6982\u7387\u5728\u5929\u7A7A\u91CA\u653E\u5524\u661F\u88C2\u9699"));
		list.add(Component.literal("\u00A77\u25AA \u00A79\u5524\u661F\u88C2\u9699\u4EE5\u6BCF0.5\u79D2/\u6B21\u5411\u5730\u9762\u53D1\u5C04\u706B\u7403 \u5524\u661F\u88C2\u9699\u6301\u7EED\u65F6\u95F410s"));
		list.add(Component.literal("\u00A72\u6CD5\u672F\u4F24\u5BB3\uFF1A7"));
		list.add(Component.literal("\u00A72\u6CD5\u7403\u52A8\u80FD\uFF1A1.6"));
		list.add(Component.literal("\u00A72\u65BD\u6CD5\u51B7\u5374\uFF1A1\u79D2"));
		list.add(Component.literal("\u00A72\u65BD\u6CD5\u6D88\u8017\uFF1A\u00A7f\u9B54\u6CD5\u77F3"));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = InteractionResultHolder.success(entity.getItemInHand(hand));
		entity.startUsingItem(hand);
		return ar;
	}

	@Override
	public void onUseTick(Level world, LivingEntity entity, ItemStack itemstack, int count) {
		if (!world.isClientSide() && entity instanceof ServerPlayer player) {
			ItemStack stack = ProjectileWeaponItem.getHeldProjectile(entity, e -> e.getItem() == TrueMoltengoldWandProjectileEntity.PROJECTILE_ITEM.getItem());
			if (stack == ItemStack.EMPTY) {
				for (int i = 0; i < player.getInventory().items.size(); i++) {
					ItemStack teststack = player.getInventory().items.get(i);
					if (teststack != null && teststack.getItem() == TrueMoltengoldWandProjectileEntity.PROJECTILE_ITEM.getItem()) {
						stack = teststack;
						break;
					}
				}
			}
			if (player.getAbilities().instabuild || stack != ItemStack.EMPTY) {
				TrueMoltengoldWandProjectileEntity projectile = TrueMoltengoldWandProjectileEntity.shoot(world, entity, world.getRandom());
				itemstack.hurtAndBreak(1, entity, e -> e.broadcastBreakEvent(entity.getUsedItemHand()));
				if (player.getAbilities().instabuild) {
					projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
				} else {
					if (stack.isDamageableItem()) {
						if (stack.hurt(1, world.getRandom(), player)) {
							stack.shrink(1);
							stack.setDamageValue(0);
							if (stack.isEmpty())
								player.getInventory().removeItem(stack);
						}
					} else {
						stack.shrink(1);
						if (stack.isEmpty())
							player.getInventory().removeItem(stack);
					}
				}
				MoltengoldWandPr1Procedure.execute(world, entity, stack);
			}
			entity.releaseUsingItem();
		}
	}
}
