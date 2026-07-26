package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 药水（可酿造）注册类
 * <p>
 * 注册可通过酿造台制作的药水，与 {@link PDEffects} 中的状态效果联动。
 * 首批移植自原模组的药水：
 * <ul>
 *   <li>dreamwish_potion — 梦境祝福药水（3分钟）</li>
 *   <li>expup_potion — 经验提升药水（3分钟）</li>
 * </ul>
 *
 * @see PDEffects
 */
public class PDPotions {

    /**
     * 药水注册器
     */
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(
            Registries.POTION, PasterDreamMod.MOD_ID);

    // ==================== 药水注册 ====================

    /**
     * 梦境祝福药水 (dreamwish_potion)
     * <p>
     * 提供 3 分钟（3600 ticks）的梦境祝福效果。
     */
    public static final DeferredHolder<Potion, Potion> DREAMWISH_POTION =
            POTIONS.register("dreamwish_potion",
                    () -> new Potion(new MobEffectInstance(
                            PDEffects.DREAMWISH_BUFF.holder(), 3600, 0, false, true)));

    /**
     * 经验提升药水 (expup_potion)
     * <p>
     * 提供 3 分钟（3600 ticks）的经验提升效果。
     */
    public static final DeferredHolder<Potion, Potion> EXPUP_POTION =
            POTIONS.register("expup_potion",
                    () -> new Potion(new MobEffectInstance(
                            PDEffects.EXPUP_BUFF.holder(), 3600, 0, false, true)));

    /**
     * 玄武药水 (basalt)
     * <p>
     * 缓慢 3 分钟 + 抗火 1 分钟 + 抗性提升 2 分钟（组合与时长与原版一致）。
     */
    public static final DeferredHolder<Potion, Potion> BASALT =
            POTIONS.register("basalt",
                    () -> new Potion(
                            new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 3600, 0, false, true),
                            new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0, false, true),
                            new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2400, 0, false, true)));

    /**
     * 幻想乡药水 (fondillusion_potion)
     * <p>
     * 提供 3 分钟（3600 ticks）的幻想乡效果（高空云雾中触发风之旅途传送）。
     */
    public static final DeferredHolder<Potion, Potion> FONDILLUSION_POTION =
            POTIONS.register("fondillusion_potion",
                    () -> new Potion(new MobEffectInstance(
                            PDEffects.FONDILLUSION_BUFF.holder(), 3600, 0, false, true)));
}