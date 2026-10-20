package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;

/**
 * 温热匕首 (thermal_dagger) — 命中音效演出
 * <p>
 * 对应原版 {@code ThermalDaggerItem}（属性与新模组既有 ItemAPI 注册逐项一致：
 * 耐久 1721 / 挖速 2 / 伤害+5.5 / 攻速 -2.3 / 附魔 2）。
 * 攻击命中播放 thermal_dagger 音效并喷发气泡粒子（原版 ThermalDaggerPr0Procedure）；
 * 修复材料为原版钛粒；按原版补 fireResistant。
 */
public class ThermalDaggerItem extends SwordItem {

    /** 命中粒子数量，原版 ×64 */
    private static final int PARTICLE_COUNT = 64;

    /** 与既有 ItemAPI 注册等值的 tier（修复材料为钛粒） */
    private static final SimpleTier TIER = new SimpleTier(
            BlockTags.INCORRECT_FOR_WOODEN_TOOL, 1721, 2.0f, 5.5f, 2,
            () -> Ingredient.of(PDItems.TITANIUM_NUGGET.get()));

    public ThermalDaggerItem(Properties properties) {
        // 原版 fireResistant；攻击伤害由 tier 承载（伤害参数 0，与 ItemAPI 方案一致）
        super(TIER, properties.fireResistant().attributes(SwordItem.createAttributes(TIER, 0, -2.3f)));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        Level level = target.level();
        if (!level.isClientSide()) {
            // 原版 ThermalDaggerPr0Procedure：命中音效 + 气泡粒子（范围 1.2/1.5/1.2，速度 0.15）
            level.playSound(null, BlockPos.containing(target.getX(), target.getY(), target.getZ()),
                    PDSounds.THERMAL_DAGGER.get(), SoundSource.PLAYERS, 1f, 1f);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.BUBBLE,
                        target.getX(), target.getY(), target.getZ(),
                        PARTICLE_COUNT, 1.2, 1.5, 1.2, 0.15);
            }
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版 appendHoverText 逐行还原
        tooltip.add(Component.translatable("tooltip.pasterdream.thermal_dagger.name_note"));
    }
}
