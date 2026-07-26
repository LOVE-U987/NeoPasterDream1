package com.pasterdream.pasterdreammod.item;

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
 * 蓄力施法法杖基类 (Abstract Charge Wand)
 * <p>
 * 还原原版 MCreator “远程武器”模板（MoltengoldWandItem 等）的公共骨架：
 * <ul>
 *   <li>右键开始蓄力（弓形动画、时长 72000），首个使用 tick 即在服务端完成一次施法</li>
 *   <li>弹药查找：优先双手中的弹药，否则扫描主背包（ProjectileWeaponItem.getHeldProjectile + 遍历）</li>
 *   <li>创造模式免弹药（弹射物标记 CREATIVE_ONLY 拾取）；生存模式按原版规则消耗弹药</li>
 *   <li>施法后调用 {@link #afterShoot}（各法杖的冷却/能量 procedure），随后立即结束蓄力</li>
 * </ul>
 * 子类通过 {@link #castGate}、{@link #ammoItem}、{@link #shootProjectile}、{@link #afterShoot} 定制行为。
 */
public abstract class AbstractChargeWandItem extends Item {

    protected AbstractChargeWandItem(Properties properties) {
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
        InteractionResultHolder<ItemStack> result = InteractionResultHolder.success(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return result;
    }

    /**
     * 施法前置校验（如魂啸法杖的能量/San 门槛），默认放行
     *
     * @param player 施法者
     * @return true 表示允许施法
     */
    protected boolean castGate(ServerPlayer player) {
        return true;
    }

    /**
     * 本法杖的弹药物品（原版 PROJECTILE_ITEM）
     *
     * @return 弹药物品
     */
    protected abstract Item ammoItem();

    /**
     * 发射投射物（服务端）
     *
     * @param level  世界
     * @param player 施法者
     * @return 已加入世界的投射物
     */
    protected abstract AbstractArrow shootProjectile(Level level, ServerPlayer player);

    /**
     * 施法后的 procedure 回调（冷却、能量消耗等）
     *
     * @param level  世界
     * @param player 施法者
     * @param ammo   被消耗的弹药栈（创造模式下可能为空栈）
     */
    protected void afterShoot(Level level, ServerPlayer player, ItemStack ammo) {
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide() || !(entity instanceof ServerPlayer player)) {
            return;
        }
        if (!castGate(player)) {
            return;
        }
        // 原版弹药查找：先看双手，再扫主背包
        ItemStack ammo = ProjectileWeaponItem.getHeldProjectile(entity, s -> s.getItem() == ammoItem());
        if (ammo.isEmpty()) {
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                ItemStack candidate = player.getInventory().items.get(i);
                if (candidate != null && candidate.getItem() == ammoItem()) {
                    ammo = candidate;
                    break;
                }
            }
        }
        if (player.getAbilities().instabuild || !ammo.isEmpty()) {
            AbstractArrow projectile = shootProjectile(level, player);
            // 法杖自体损耗（原版 hurtAndBreak(1)；本模块法杖多为无耐久物品，此调用为兼容保留）
            stack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
            if (player.getAbilities().instabuild) {
                projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            } else {
                consumeAmmo(level, player, ammo);
            }
            afterShoot(level, player, ammo);
        }
        entity.releaseUsingItem();
    }

    /**
     * 按原版规则消耗一份弹药：可损耗物品扣 1 耐久（耗尽则消失），否则数量 -1
     *
     * @param level  世界（服务端）
     * @param player 施法者
     * @param ammo   弹药栈
     */
    protected static void consumeAmmo(Level level, ServerPlayer player, ItemStack ammo) {
        if (ammo.isDamageableItem()) {
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                ammo.hurtAndBreak(1, serverLevel, player, item -> {
                });
            }
            if (ammo.isEmpty()) {
                player.getInventory().removeItem(ammo);
            }
        } else {
            ammo.shrink(1);
            if (ammo.isEmpty()) {
                player.getInventory().removeItem(ammo);
            }
        }
    }
}
