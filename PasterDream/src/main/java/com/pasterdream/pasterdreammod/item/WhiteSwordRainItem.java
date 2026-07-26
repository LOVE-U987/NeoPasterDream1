package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.entity.projectile.WhiteSwordRainProjectileEntity;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 白厄剑雨 (white_sword_rain)
 * <p>
 * 还原自原版 WhiteSwordRainItem（可独立投掷的光剑物品，也是白色灾厄剑技的弹药与
 * 光剑投射物的渲染贴图来源）：
 * <ul>
 *   <li>耐久 100、三叉戟式蓄力动画</li>
 *   <li>右键蓄力，松开时消耗自身（作为弹药扣 1 耐久）掷出白厄光剑</li>
 * </ul>
 */
public class WhiteSwordRainItem extends Item {

    public WhiteSwordRainItem(Properties properties) {
        super(properties.durability(100));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
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
        InteractionResultHolder<ItemStack> result = InteractionResultHolder.success(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return result;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (level.isClientSide() || !(entity instanceof ServerPlayer player)) {
            return;
        }
        // 原版：弹药即 white_sword_rain 自身
        ItemStack ammo = ProjectileWeaponItem.getHeldProjectile(entity,
                s -> s.getItem() == PDItems.WHITE_SWORD_RAIN.get());
        if (ammo.isEmpty()) {
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                ItemStack candidate = player.getInventory().items.get(i);
                if (candidate != null && candidate.getItem() == PDItems.WHITE_SWORD_RAIN.get()) {
                    ammo = candidate;
                    break;
                }
            }
        }
        if (!player.getAbilities().instabuild && ammo.isEmpty()) {
            return;
        }
        WhiteSwordRainProjectileEntity projectile =
                WhiteSwordRainProjectileEntity.shoot(level, entity, level.getRandom());
        stack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
        if (player.getAbilities().instabuild) {
            projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        } else {
            AbstractChargeWandItem.consumeAmmo(level, player, ammo);
        }
    }
}
