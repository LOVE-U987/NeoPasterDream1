package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.entity.projectile.BoneWingFireBallProjectileEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** 骨翼火球弹药/发射器 (bone_wing_fire_ball) */
public class BoneWingFireBallItem extends Item {
    public BoneWingFireBallItem() {
        super(new Item.Properties().durability(9999).rarity(Rarity.COMMON));
    }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BOW; }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) { return 72000; }
    @Override public float getDestroySpeed(ItemStack stack, BlockState state) { return 0f; }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }
    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide() || !(entity instanceof ServerPlayer player)) return;
        BoneWingFireBallProjectileEntity projectile =
                BoneWingFireBallProjectileEntity.shoot(level, player, player.getRandom());
        if (player.getAbilities().instabuild) {
            projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        } else {
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
        }
        entity.releaseUsingItem();
    }
}
