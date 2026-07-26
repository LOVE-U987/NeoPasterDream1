package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.entity.projectile.PinkeggProjectileEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 粉蛋 (pinkegg)
 * <p>
 * 还原原版 PinkeggItem：右键蓄力后立即投掷 PinkeggProjectileEntity，
 * 命中方块时 1/5 概率孵出粉色鸡。
 */
public class PinkeggItem extends Item {

    public PinkeggItem(Properties properties) {
        super(properties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return 0f;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide() || !(entity instanceof ServerPlayer player)) {
            return;
        }
        PinkeggProjectileEntity projectile = PinkeggProjectileEntity.shoot(level, player, level.getRandom());
        if (player.getAbilities().instabuild) {
            projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        } else {
            stack.shrink(1);
            if (stack.isEmpty()) {
                player.getInventory().removeItem(stack);
            }
        }
        entity.releaseUsingItem();
    }
}
