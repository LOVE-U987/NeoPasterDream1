package com.pasterdream.pasterdreammod.util;

import com.pasterdream.pasterdreammod.registry.PDDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 梦之强化工具类 (Dream Potion Enhancer)
 *
 * 梦境炼药锅酿造药水的专属增益：坩埚产出的药水会经过本类强化后写入瓶内——
 * <ul>
 *   <li>所有效果：等级 +1</li>
 *   <li>持续型效果：额外时长变为 1.5 倍</li>
 *   <li>无限时长效果：时长保持原样（等级仍 +1）</li>
 * </ul>
 *
 * 实现说明：强化后的完整效果列表写入 {@code POTION_CONTENTS.customEffects} 并清空
 * potion 字段，避免"原版效果 + 强化效果"双重叠加；原药水名称通过
 * {@code ITEM_NAME} 组件以翻译键形式保留（显示无斜体，与原版药水名一致）；
 * 最后附加 {@code dream_enhanced} 组件标记强化来源。
 */
public final class DreamPotionEnhancer {

    /** 持续型效果时长增益倍率 */
    public static final float DURATION_MULTIPLIER = 1.5f;

    /** 即时型效果等级增益 */
    public static final int INSTANT_AMPLIFIER_BONUS = 1;

    /**
     * 私有构造器（纯静态工具类，禁止实例化）
     */
    private DreamPotionEnhancer() {
    }

    /**
     * 对药水物品应用梦之强化
     *
     * 无效果的药水（水瓶/粗制/浑浊等）不做强化也不加标记，原样返回。
     *
     * @param potion 酿造产出的药水（将被就地修改并返回）
     * @return 强化后的药水（无效果时为原物品）
     */
    public static ItemStack enhance(ItemStack potion) {
        if (potion.isEmpty()) {
            return potion;
        }
        PotionContents contents = potion.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        if (!contents.hasEffects()) {
            return potion;
        }

        // 在 potion 字段仍完好时记录原始药水名称翻译键（如 item.minecraft.potion.effect.night_vision）
        String nameKey = potion.getItem().getDescriptionId(potion);

        // 逐效果强化：所有效果等级 +1，持续型额外时长乘以增益倍率（无限时长保持原样）
        List<MobEffectInstance> enhanced = new ArrayList<>();
        for (MobEffectInstance instance : contents.getAllEffects()) {
            if (instance.getEffect().value().isInstantenous()) {
                // 即时型：等级 +1（时长无意义，保持原值）
                enhanced.add(new MobEffectInstance(instance.getEffect(), instance.getDuration(),
                        instance.getAmplifier() + INSTANT_AMPLIFIER_BONUS,
                        instance.isAmbient(), instance.isVisible(), instance.showIcon()));
            } else {
                int duration = instance.isInfiniteDuration() ? instance.getDuration()
                        : Math.max(instance.getDuration(), (int) (instance.getDuration() * DURATION_MULTIPLIER));
                enhanced.add(new MobEffectInstance(instance.getEffect(), duration,
                        instance.getAmplifier() + INSTANT_AMPLIFIER_BONUS,
                        instance.isAmbient(), instance.isVisible(), instance.showIcon()));
            }
        }

        // 清空 potion 字段（防止与强化效果双重叠加），效果全部置于 customEffects
        PotionContents enhancedContents = new PotionContents(Optional.empty(), contents.customColor(), enhanced);
        potion.set(DataComponents.POTION_CONTENTS, enhancedContents);
        // 保留原药水名称（ITEM_NAME 组件覆盖，显示无斜体）
        potion.set(DataComponents.ITEM_NAME, Component.translatable(nameKey));
        // 标记梦之强化来源
        potion.set(PDDataComponents.DREAM_ENHANCED.get(), true);
        return potion;
    }
}
