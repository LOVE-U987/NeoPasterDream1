package com.pasterdream.pasterdreammod.pasterdreammeltdream.registry;

import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.api.meltdream.MeltDreamEnergyAPI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 融梦能量系统状态效果注册类。
 * <p>
 * 负责注册融梦能量增加/减少等效果。
 * 使用 {@link MobEffectAPI#REGISTRY} 注册到 pasterdream 命名空间，便于主模组运行时查找。
 *
 * @author PasterDream
 */
public class PDMeltDreamEffects {

    /** 状态效果注册器（代理至 API 统一注册表） */
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = MobEffectAPI.REGISTRY;

    /**
     * 融梦能量增加 (melt_dream_energy_increase)。
     * <p>
     * 有益效果 0xADFF2F。瞬时生效：融梦能量 +((amplifier &amp; 0xff) + 1)。
     */
    public static final DeferredHolder<MobEffect, MobEffect> MELT_DREAM_ENERGY_INCREASE =
            MobEffectAPI.REGISTRY.register("melt_dream_energy_increase",
                    () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 0xADFF2F) {
                        @Override
                        public boolean applyEffectTick(net.minecraft.world.entity.LivingEntity entity, int amplifier) {
                            if (entity instanceof ServerPlayer pl) {
                                MeltDreamEnergyAPI.addEnergy(pl, (amplifier & 0xff) + 1);
                            }
                            return true;
                        }
                    });

    /**
     * 融梦能量减少 (melt_dream_energy_decrease)。
     * <p>
     * 有害效果 0x9B4400。瞬时生效：融梦能量 -((amplifier &amp; 0xff) + 1)。
     */
    public static final DeferredHolder<MobEffect, MobEffect> MELT_DREAM_ENERGY_DECREASE =
            MobEffectAPI.REGISTRY.register("melt_dream_energy_decrease",
                    () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0x9B4400) {
                        @Override
                        public boolean applyEffectTick(net.minecraft.world.entity.LivingEntity entity, int amplifier) {
                            if (entity instanceof ServerPlayer pl) {
                                MeltDreamEnergyAPI.addEnergy(pl, -(amplifier & 0xff) - 1);
                            }
                            return true;
                        }
                    });

    private PDMeltDreamEffects() {
    }
}
