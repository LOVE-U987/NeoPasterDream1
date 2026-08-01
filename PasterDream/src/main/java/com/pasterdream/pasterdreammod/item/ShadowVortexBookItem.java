package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.registry.PDAdvancements;
import com.pasterdream.pasterdreammod.entity.projectile.ShadowVortexBookProjectileEntity;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * 暗影旋涡 (shadow_vortex_book)
 * <p>
 * 还原自原版 ShadowVortexBookItem（松开蓄力施法的法术书）：
 * <ul>
 *   <li>施法门槛（ShadowVortexBookPr1Procedure）：创造模式直接放行；
 *       否则要求融梦能量 ≥0.01（或免消耗）且（San ≥0.05 或理智系统关闭）</li>
 *   <li>右键蓄力，<b>松开时</b>发射暗影旋涡法球（消耗魔法石）</li>
 *   <li>施法后（ShadowVortexBookPr0Procedure）：已达成 achievement_talent_shadow 成就者
 *       消耗 0.01 能量、San -0.05，全部法术物品冷却 24 + 法术冷却属性；
 *       未达成者遭到反噬——冷却 1000、受 5 点伤害并提示</li>
 * </ul>
 */
public class ShadowVortexBookItem extends Item {

    /** 暗影天赋成就 ID */
    private static final ResourceLocation TALENT_SHADOW_ADVANCEMENT = PDAdvancements.TALENT_SHADOW;

    public ShadowVortexBookItem(Properties properties) {
        super(properties.stacksTo(1));
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
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.pasterdream.shadow_vortex_book.hovertext.describe.0"));
        // 原版仅在理智系统开启时显示精神值消耗行；1.21.1 的 appendHoverText 无玩家上下文，恒定展示
        tooltip.add(Component.translatable("item.pasterdream.shadow_vortex_book.hovertext.is_san"));
        tooltip.add(Component.translatable("item.pasterdream.shadow_vortex_book.hovertext.describe.1"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        InteractionResultHolder<ItemStack> result = InteractionResultHolder.success(player.getItemInHand(hand));
        player.startUsingItem(hand);
        return result;
    }

    /**
     * 原版 ShadowVortexBookPr1Procedure：施法门槛
     */
    private static boolean castGate(ServerPlayer player) {
        // 融梦能量门槛已剥离；仅保留 SAN / 创造旁路
        boolean sanOk = PDAttachments.getSan(player).sanValue() >= 0.05
                || !PDAttachments.isSanCheckEnabled(player);
        return player.getAbilities().instabuild || sanOk;
    }

    /**
     * 判断玩家是否已达成暗影天赋成就（缺失成就时按未达成处理）
     */
    private static boolean hasShadowTalent(ServerPlayer player) {
        if (!PDAdvancements.isAdvancementLocked(player, TALENT_SHADOW_ADVANCEMENT)) {
            return true;
        }
        AdvancementHolder advancement = player.server.getAdvancements().get(TALENT_SHADOW_ADVANCEMENT);
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (level.isClientSide() || !(entity instanceof ServerPlayer player)) {
            return;
        }
        if (!castGate(player)) {
            return;
        }
        // 原版弹药查找：先看双手，再扫主背包（弹药 = 魔法石）
        ItemStack ammo = ProjectileWeaponItem.getHeldProjectile(entity,
                s -> s.getItem() == PDItems.MAGIC_STONE.get());
        if (ammo.isEmpty()) {
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                ItemStack candidate = player.getInventory().items.get(i);
                if (candidate != null && candidate.getItem() == PDItems.MAGIC_STONE.get()) {
                    ammo = candidate;
                    break;
                }
            }
        }
        if (!player.getAbilities().instabuild && ammo.isEmpty()) {
            return;
        }
        ShadowVortexBookProjectileEntity projectile =
                ShadowVortexBookProjectileEntity.shoot(level, entity, level.getRandom());
        stack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
        if (player.getAbilities().instabuild) {
            projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        } else {
            AbstractChargeWandItem.consumeAmmo(level, player, ammo);
        }
        // 原版 ShadowVortexBookPr0Procedure：天赋检定与消耗/反噬
        if (hasShadowTalent(player)) {
            // 融梦能量消耗已剥离至附属 mod
            PDAttachments.addPlayerSanWithCheck(player, -0.05);
            AttributeInstance magicCd = player.getAttribute(PDAttributes.MAGICCD);
            int ticks = (int) (24 + (magicCd != null ? magicCd.getValue() : 0));
            WandSupport.applyTaggedCooldown(player, WandSupport.MAGIC_TAG, ticks);
        } else {
            WandSupport.applyTaggedCooldown(player, WandSupport.MAGIC_TAG, 1000);
            player.hurt(level.damageSources().generic(), 5);
            player.displayClientMessage(Component.translatable("tooltip.pasterdream.shadow_vortex_book.backlash"), true);
        }
    }
}
