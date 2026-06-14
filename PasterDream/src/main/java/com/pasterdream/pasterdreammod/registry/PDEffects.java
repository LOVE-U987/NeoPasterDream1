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
import net.minecraft.world.effect.MobEffectInstance;
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

    // ==================== 防风效果 ====================

    /**
     * 防风效果 (windproof_buff)
     * <p>
     * 淡蓝色有益效果，水母系列食物提供。
     * 纯标记效果，无 tick 逻辑，可抵御风系 debuff 或环境风阻（待实装）。
     */
    public static final MobEffectResult WINDPROOF_BUFF =
            MobEffectAPI.createEffect("windproof_buff")
                    .beneficial()
                    .color(0xBBBBF6)
                    .build();

    // ==================== 烹饪增益效果 ====================

    /**
     * 烹饪增益 (cook_buff)
     * <p>
     * 玫红色有益效果，享用美食后获得的饱足感。
     * 原版效果同时增加 san_variability 属性 +1.2（san 系统尚未移植，暂为纯标记效果）。
     * 被约 13 种食物携带，持续时间 60 秒。
     */
    public static final MobEffectResult COOK_BUFF =
            MobEffectAPI.createEffect("cook_buff")
                    .beneficial()
                    .color(0xEE3373)
                    .build();

    // ==================== 实体技能相关状态效果 ====================

    /**
     * 混乱效果 (confusion_buff)
     * <p>
     * 深紫色有害效果，由恐怖尖喙咆哮、震动水晶等技能施加。
     * 每 tick 施加原版反胃（屏幕抖动）和缓慢效果，模拟眩晕/致盲的实战效果。
     */
    public static final MobEffectResult CONFUSION_BUFF =
            MobEffectAPI.createEffect("confusion_buff")
                    .harmful()
                    .color(0xFF4A0080)
                    .onTick((entity, amplifier) -> {
                        // 施加反胃效果——屏幕抖动（刷新持续防止自然消失）
                        entity.addEffect(new MobEffectInstance(
                                MobEffects.CONFUSION, 100, 0, false, false, false));
                        // 施加缓慢效果——限制移动
                        entity.addEffect(new MobEffectInstance(
                                MobEffects.MOVEMENT_SLOWDOWN, 100, 1, false, false, false));
                    })
                    .build();

    /**
     * 暗影沉默效果 (shadow_silence_buff)
     * 暗紫色有害效果，被沉默的实体无法使用技能。
     */
    public static final MobEffectResult SHADOW_SILENCE_BUFF =
            MobEffectAPI.createEffect("shadow_silence_buff")
                    .harmful()
                    .color(0xFF2A0040)
                    .build();

    /**
     * 压迫效果 (oppression_buff)
     * 深红色有害效果，压制目标行动能力。
     */
    public static final MobEffectResult OPPRESSION_BUFF =
            MobEffectAPI.createEffect("oppression_buff")
                    .harmful()
                    .color(0xFF800000)
                    .build();
}