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
 * 『亚勒兹』唤星 (truest_moltengold_wand)
 * <p>
 * 还原自原版 TruestMoltengoldWandItem：右键蓄力即时发射唤星雨法球（消耗魔法石），
 * 施法冷却与炙焰金杖共用 MoltengoldWandPr1Procedure（20 + 法术冷却属性）。
 */
public class TruestMoltengoldWandItem extends AbstractChargeWandItem {

    public TruestMoltengoldWandItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal("法术技能：§6唤星雨"));
        tooltip.add(Component.literal("§7▪ §9法球攻击到敌人或落地时 如空间充足将会在原地释放唤星照明"));
        tooltip.add(Component.literal("§7▪ §9如空间充足且露天 则有50%的概率在天空释放唤星裂隙"));
        tooltip.add(Component.literal("§7▪ §9唤星裂隙以每0.5秒/次向地面发射火球 唤星裂隙持续时间10s"));
        tooltip.add(Component.literal("法术强度≥§a1§f时"));
        tooltip.add(Component.literal("§7▪ §9额外向命中目标周围散射2+法术强度个火球"));
        tooltip.add(Component.literal("§2法术伤害：10"));
        tooltip.add(Component.literal("§2法球动能：1.7"));
        tooltip.add(Component.literal("§2施法冷却：1.00秒"));
        tooltip.add(Component.literal("§2施法消耗：§f魔法石"));
    }

    @Override
    protected Item ammoItem() {
        return PDItems.MAGIC_STONE.get();
    }

    @Override
    protected AbstractArrow shootProjectile(Level level, ServerPlayer player) {
        return com.pasterdream.pasterdreammod.entity.projectile.TruestMoltengoldWandProjectileEntity
                .shoot(level, player, level.getRandom());
    }

    @Override
    protected void afterShoot(Level level, ServerPlayer player, ItemStack ammo) {
        MoltengoldWandItem.applyMoltengoldCooldown(player, this);
    }
}
