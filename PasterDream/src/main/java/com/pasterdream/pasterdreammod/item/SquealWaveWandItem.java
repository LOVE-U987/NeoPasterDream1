package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.attachment.MeltDreamEnergyData;
import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 魂啸法杖 (squeal_wave_wand)
 * <p>
 * 还原自原版 SquealWaveWandItem：
 * <ul>
 *   <li>施法门槛（SquealWaveWandPr2Procedure）：创造模式或佩戴幽灵面具直接放行；
 *       否则要求融梦能量 ≥0.01（或免消耗）且（San ≥0.02 或理智系统关闭）</li>
 *   <li>右键蓄力即时发射魂啸音波（消耗魔法石）</li>
 *   <li>施法后（SquealWaveWandPr1Procedure）：非创造且未戴幽灵面具时消耗 0.01 融梦能量、
 *       San -0.02；全部法术物品冷却 18 + 法术冷却属性 tick</li>
 * </ul>
 */
public class SquealWaveWandItem extends AbstractChargeWandItem {

    public SquealWaveWandItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.pasterdream.squeal_wave_wand.hovertext.describe.0"));
        // 原版仅在理智系统开启时显示精神值消耗行；1.21.1 的 appendHoverText 无玩家上下文，
        // 客户端 San 开关无法在此可靠读取，故恒定展示该行（内容仍由语言文件控制）
        tooltip.add(Component.translatable("item.pasterdream.squeal_wave_wand.hovertext.is_san"));
        tooltip.add(Component.translatable("item.pasterdream.squeal_wave_wand.hovertext.describe.1"));
    }

    @Override
    protected boolean castGate(ServerPlayer player) {
        // 原版 SquealWaveWandPr2Procedure
        MeltDreamEnergyData energy = PDAttachments.getMeltDreamEnergy(player);
        boolean energyOk = energy.meltDreamEnergy() >= 0.01 || energy.isNoNeedConsume();
        boolean sanOk = PDAttachments.getSan(player).sanValue() >= 0.02
                || !PDAttachments.isSanCheckEnabled(player);
        boolean bypass = player.getAbilities().instabuild
                || WandSupport.hasCurioEquipped(player, PDItems.GHOST_FACE_HEAD.get());
        return bypass || (energyOk && sanOk);
    }

    @Override
    protected Item ammoItem() {
        return PDItems.MAGIC_STONE.get();
    }

    @Override
    protected AbstractArrow shootProjectile(Level level, ServerPlayer player) {
        return com.pasterdream.pasterdreammod.entity.projectile.SquealWaveWandProjectileEntity
                .shoot(level, player, level.getRandom());
    }

    @Override
    protected void afterShoot(Level level, ServerPlayer player, ItemStack ammo) {
        // 原版 SquealWaveWandPr1Procedure
        boolean bypass = player.getAbilities().instabuild
                || WandSupport.hasCurioEquipped(player, PDItems.GHOST_FACE_HEAD.get());
        if (!bypass) {
            PDAttachments.consumePlayerMeltDreamEnergy(player, 0.01);
            PDAttachments.addPlayerSanWithCheck(player, -0.02);
        }
        AttributeInstance magicCd = player.getAttribute(PDAttributes.MAGICCD);
        int ticks = (int) (18 + (magicCd != null ? magicCd.getValue() : 0));
        WandSupport.applyTaggedCooldown(player, WandSupport.MAGIC_TAG, ticks);
    }
}
