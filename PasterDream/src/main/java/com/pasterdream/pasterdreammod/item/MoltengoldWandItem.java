package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 炙焰金杖 (moltengold_wand)
 * <p>
 * 还原自原版 MoltengoldWandItem：右键蓄力即时发射炙焰法球（消耗魔法石），
 * 施法后按 MoltengoldWandPr1Procedure 统一冷却：
 * 佩戴俏皮鬼头饰（qym_head）时免冷却，否则全部法术物品冷却 20 + 法术冷却属性 tick。
 * 物品属性与原版一致：单个堆叠、普通稀有度、无耐久。
 */
public class MoltengoldWandItem extends AbstractChargeWandItem {

    public MoltengoldWandItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.pasterdream.moltengold_wand.damage"));
        tooltip.add(Component.translatable("tooltip.pasterdream.moltengold_wand.kinetic"));
        tooltip.add(Component.translatable("tooltip.pasterdream.moltengold_wand.cooldown"));
        tooltip.add(Component.translatable("tooltip.pasterdream.moltengold_wand.cost"));
    }

    @Override
    protected Item ammoItem() {
        return PDItems.MAGIC_STONE.get();
    }

    @Override
    protected AbstractArrow shootProjectile(Level level, ServerPlayer player) {
        return com.pasterdream.pasterdreammod.entity.projectile.MoltengoldWandProjectileEntity
                .shoot(level, player, level.getRandom());
    }

    @Override
    protected void afterShoot(Level level, ServerPlayer player, ItemStack ammo) {
        applyMoltengoldCooldown(player, this);
    }

    /**
     * 原版 MoltengoldWandPr1Procedure：炙焰金杖系共用的施法冷却
     * <ul>
     *   <li>佩戴俏皮鬼头饰（qym_head）→ 本法杖冷却 0（免冷却）</li>
     *   <li>否则 → 全部 pasterdream:magic 物品冷却 20 + 法术冷却属性值 tick</li>
     * </ul>
     *
     * @param player 施法者
     * @param wand   施放的法杖物品
     */
    static void applyMoltengoldCooldown(Player player, Item wand) {
        if (WandSupport.hasCurioEquipped(player, PDItems.QYM_HEAD.get())) {
            player.getCooldowns().addCooldown(wand, 0);
            return;
        }
        AttributeInstance magicCd = player.getAttribute(PDAttributes.MAGICCD);
        int ticks = (int) (20 + (magicCd != null ? magicCd.getValue() : 0));
        WandSupport.applyTaggedCooldown(player, WandSupport.MAGIC_TAG, ticks);
    }
}
