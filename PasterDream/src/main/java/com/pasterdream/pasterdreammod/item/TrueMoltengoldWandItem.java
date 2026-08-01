package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 唤星者法杖 (true_moltengold_wand)
 * <p>
 * 还原自原版 TrueMoltengoldWandItem：右键蓄力即时发射唤星法球（消耗魔法石），
 * 施法冷却与炙焰金杖共用 MoltengoldWandPr1Procedure（20 + 法术冷却属性）。
 */
public class TrueMoltengoldWandItem extends AbstractChargeWandItem {

    public TrueMoltengoldWandItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.pasterdream.true_moltengold_wand.skill_name"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_moltengold_wand.illumination"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_moltengold_wand.rift_sky"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_moltengold_wand.rift_behavior"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_moltengold_wand.damage"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_moltengold_wand.kinetic"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_moltengold_wand.cooldown"));
        tooltip.add(Component.translatable("tooltip.pasterdream.true_moltengold_wand.cost"));
    }

    @Override
    protected Item ammoItem() {
        return PDItems.MAGIC_STONE.get();
    }

    @Override
    protected AbstractArrow shootProjectile(Level level, ServerPlayer player) {
        return com.pasterdream.pasterdreammod.entity.projectile.TrueMoltengoldWandProjectileEntity
                .shoot(level, player, level.getRandom());
    }

    @Override
    protected void afterShoot(Level level, ServerPlayer player, ItemStack ammo) {
        MoltengoldWandItem.applyMoltengoldCooldown(player, this);
    }
}
