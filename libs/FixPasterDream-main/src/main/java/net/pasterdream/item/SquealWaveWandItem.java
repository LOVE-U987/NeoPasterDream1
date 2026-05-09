
package net.pasterdream.item;

import net.pasterdream.capability.SanCapability;
import net.pasterdream.procedures.SquealWaveWandPr2Procedure;
import net.pasterdream.procedures.SquealWaveWandPr1Procedure;
import net.pasterdream.entity.SquealWaveWandProjectileEntity;

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

public class SquealWaveWandItem extends Item {
	public SquealWaveWandItem() {
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
        list.add(Component.translatable("item.pasterdream.squeal_wave_wand.hovertext.describe.0"));
        if(SanCapability.IsSanCheckSystem())
        {
            list.add(Component.translatable("item.pasterdream.squeal_wave_wand.hovertext.is_san"));
        }
        list.add(Component.translatable("item.pasterdream.squeal_wave_wand.hovertext.describe.1"));

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
			double x = entity.getX();
			double y = entity.getY();
			double z = entity.getZ();
			if (SquealWaveWandPr2Procedure.execute(player)) {
				ItemStack stack = ProjectileWeaponItem.getHeldProjectile(entity, e -> e.getItem() == SquealWaveWandProjectileEntity.PROJECTILE_ITEM.getItem());
				if (stack == ItemStack.EMPTY) {
					for (int i = 0; i < player.getInventory().items.size(); i++) {
						ItemStack teststack = player.getInventory().items.get(i);
						if (teststack != null && teststack.getItem() == SquealWaveWandProjectileEntity.PROJECTILE_ITEM.getItem()) {
							stack = teststack;
							break;
						}
					}
				}
				if (player.getAbilities().instabuild || stack != ItemStack.EMPTY) {
					SquealWaveWandProjectileEntity projectile = SquealWaveWandProjectileEntity.shoot(world, entity, world.getRandom());
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
					SquealWaveWandPr1Procedure.execute(world, entity, stack);
				}
				entity.releaseUsingItem();
			}
		}
	}
}
