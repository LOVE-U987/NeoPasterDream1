package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 状态效果（BUFF/DEBUFF）注册类
 * 使用 DeferredRegister 模式注册所有自定义 MobEffect
 * <p>
 * 待实现的效果列表（参考 STORYLINE.md）：
 * - DREAMWISH_BUFF：梦境祝福效果，用于进入染梦维度
 * - San 值相关效果：精神状态指标相关
 * - 暗影侵蚀效果：灯影世界中的负面状态
 */
public class PDEffects {

    /**
     * 状态效果注册器
     */
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(
            Registries.MOB_EFFECT, PasterDreamMod.MOD_ID);

    // ==================== 占位示例（后续取消注释并替换为实际效果） ====================

    /*
     * 梦境祝福效果 (dreamwish_buff)
     * 用于进入染梦维度的前置条件
     * 效果：在夜晚睡觉时触发传送
     */
    // public static final DeferredHolder<MobEffect, MobEffect> DREAMWISH_BUFF =
    //         MOB_EFFECTS.register("dreamwish_buff",
    //                 () -> new DreamwishEffect(MobEffectCategory.BENEFICIAL, 0xFF69B4));
    //
    // 其他效果依次添加...
}