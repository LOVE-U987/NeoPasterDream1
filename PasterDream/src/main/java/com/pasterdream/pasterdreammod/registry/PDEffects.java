package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.api.effect.MobEffectResult;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 状态效果（BUFF/DEBUFF）注册类
 * <p>
 * 使用 {@link MobEffectAPI} 的 Facade+Builder 模式注册，
 * 支持链式配置分类、颜色、着色器、粒子、回调等。
 * <p>
 * 首批试点移植自原模组的染梦维度相关效果：
 * <ul>
 *   <li>dreamwish_buff — 梦境祝福：进入染梦维度时获得</li>
 *   <li>dyedreamup_buff — 染梦附魔：染梦维度通用BUFF</li>
 *   <li>dyedream_perfume_buff — 染梦香水：使用香水后获得</li>
 *   <li>expup_buff — 经验提升：每 tick 概率获得经验</li>
 *   <li>goldenrod_tea_buff — 菊茶效果：每 tick 消除饥饿和反胃</li>
 * </ul>
 *
 * @see MobEffectAPI
 * @see com.pasterdream.pasterdreammod.api.effect.builder.MobEffectBuilder
 * @see com.pasterdream.pasterdreammod.api.effect.base.PasterDreamEffect
 */
public class PDEffects {

    /**
     * 状态效果旧式注册器（向后兼容）
     *
     * @deprecated 此独立注册器仅为过渡期兼容保留，将在下个主版本移除。
     *             请使用 {@link com.pasterdream.pasterdreammod.api.effect.MobEffectAPI#REGISTRY}。
     */
    @Deprecated(forRemoval = true, since = "0.0.3.2")
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(
            Registries.MOB_EFFECT, PasterDreamMod.MOD_ID);

    // ==================== 染梦维度核心效果 ====================

    /**
     * 梦境祝福 (dreamwish_buff)
     * <p>
     * 粉红色有益效果，进入染梦维度时自动获取。
     * 纯标记效果，无附加逻辑。
     */
    public static final MobEffectResult DREAMWISH_BUFF =
            MobEffectAPI.createEffect("dreamwish_buff")
                    .beneficial()
                    .color(0xFFFA8CE6)
                    .build();

    /**
     * 染梦附魔 (dyedreamup_buff)
     * <p>
     * 亮粉色有益效果，染梦维度的通用增幅状态。
     * 纯标记效果，无附加逻辑。
     */
    public static final MobEffectResult DYEDREAMUP_BUFF =
            MobEffectAPI.createEffect("dyedreamup_buff")
                    .beneficial()
                    .color(0xFFFF80B2)
                    .build();

    /**
     * 染梦香水 (dyedream_perfume_buff)
     * <p>
     * 米白色有益效果，使用染梦香水后获得。
     * 纯标记效果，无附加逻辑。
     */
    public static final MobEffectResult DYEDREAM_PERFUME_BUFF =
            MobEffectAPI.createEffect("dyedream_perfume_buff")
                    .beneficial()
                    .color(0xFFEACDBD)
                    .build();

    // ==================== 工具类效果 ====================

    /**
     * 经验提升 (expup_buff)
     * <p>
     * 淡紫色有益效果，每 tick 有 1/1000 概率给予 1 点经验。
     * 演示 {@link MobEffectAPI} 的 onTick 回调用法。
     */
    public static final MobEffectResult EXPUP_BUFF =
            MobEffectAPI.createEffect("expup_buff")
                    .beneficial()
                    .color(0xFFABABD5)
                    .onTick((entity, amplifier) -> {
                        // 每 tick 1/1000 概率给 1 点经验（原版逻辑）
                        if (Mth.nextInt(RandomSource.create(), 1, 1000) <= 10) {
                            if (entity instanceof net.minecraft.world.entity.player.Player player) {
                                player.giveExperiencePoints(1);
                            }
                        }
                    })
                    .build();

    /**
     * 菊茶效果 (goldenrod_tea_buff)
     * <p>
     * 暖橙色有益效果，饮用黄金菊茶后获得。
     * 每 tick 自动消除饥饿和反胃效果。
     */
    public static final MobEffectResult GOLDENROD_TEA_BUFF =
            MobEffectAPI.createEffect("goldenrod_tea_buff")
                    .beneficial()
                    .color(0xFFFF9F6A)
                    .onTick((entity, amplifier) -> {
                        // 每 tick 移除饥饿和反胃效果（原版逻辑）
                        entity.removeEffect(MobEffects.HUNGER);
                        entity.removeEffect(MobEffects.CONFUSION);
                    })
                    .build();
}