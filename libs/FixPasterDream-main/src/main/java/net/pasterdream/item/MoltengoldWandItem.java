
package net.pasterdream.item;

import net.pasterdream.procedures.MoltengoldWandPr1Procedure;
import net.pasterdream.entity.MoltengoldWandProjectileEntity;

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

public class MoltengoldWandItem extends Item {
	public MoltengoldWandItem() {
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
		list.add(Component.literal("\u00A72\u6CD5\u672F\u4F24\u5BB3\uFF1A5"));
		list.add(Component.literal("\u00A72\u6CD5\u7403\u52A8\u80FD\uFF1A1.5"));
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
			ItemStack stack = ProjectileWeaponItem.getHeldProjectile(entity, e -> e.getItem() == MoltengoldWandProjectileEntity.PROJECTILE_ITEM.getItem());
			if (stack == ItemStack.EMPTY) {
				for (int i = 0; i < player.getInventory().items.size(); i++) {
					ItemStack teststack = player.getInventory().items.get(i);
					if (teststack != null && teststack.getItem() == MoltengoldWandProjectileEntity.PROJECTILE_ITEM.getItem()) {
						stack = teststack;
						break;
					}
				}
			}
			if (player.getAbilities().instabuild || stack != ItemStack.EMPTY) {
				MoltengoldWandProjectileEntity projectile = MoltengoldWandProjectileEntity.shoot(world, entity, world.getRandom());
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
