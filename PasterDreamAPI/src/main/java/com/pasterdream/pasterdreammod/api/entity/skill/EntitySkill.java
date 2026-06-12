package com.pasterdream.pasterdreammod.api.entity.skill;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

/**
 * 实体技能定义 —— 代表一个实体可执行的技能
 * <p>
 * 包含技能的名称、动画、伤害、范围、冷却等核心属性，
 * 以及可选的粒子效果和音效联动。
 * <p>
 * 使用 {@link EntitySkillBuilder} 进行构建：
 * <pre>{@code
 * EntitySkill dashAttack = EntitySkill.builder("dash_attack")
 *     .damage(10.0f)
 *     .range(5.0f)
 *     .cooldownTicks(100)
 *     .animationName("dash")
 *     .particleName("pasterdream:sparkle")
 *     .soundId("pasterdream:dash_attack")
 *     .build();
 * }</pre>
 *
 * @param name           技能标识名称（snake_case，如 "dash_attack"）
 * @param animationName  技能触发时播放的 GeckoLib 动画名称
 * @param damage         技能基础伤害值
 * @param range          技能作用范围（格）
 * @param cooldownTicks  技能冷却时间（tick，20 tick = 1 秒）
 * @param particleName   技能触发时生成的粒子名称（与 {@code ParticleAPI} 联动，可选）
 * @param soundId        技能触发时播放的音效 ID（如 "pasterdream:dash_attack"，可选）
 * @see EntitySkillBuilder
 * @see com.pasterdream.pasterdreammod.api.particle.ParticleAPI
 */
public record EntitySkill(
        String name,
        String animationName,
        float damage,
        float range,
        int cooldownTicks,
        @Nullable String particleName,
        @Nullable String soundId
) {

    /**
     * 创建一个技能构建器
     *
     * @param name 技能标识名称（snake_case 格式）
     * @return 技能构建器实例
     */
    public static EntitySkillBuilder builder(String name) {
        PasterDreamAPI.LOGGER.debug("[EntitySkill] 创建技能构建器: {}", name);
        return new EntitySkillBuilder(name);
    }

    /**
     * 获取技能的音效事件（如果配置了音效 ID）
     *
     * @return 音效事件，未配置返回 null
     */
    @Nullable
    public SoundEvent getSoundEvent() {
        if (soundId == null) return null;
        return SoundEvent.createVariableRangeEvent(ResourceLocation.parse(soundId));
    }

    /**
     * 判断技能对目标是否在有效范围内
     *
     * @param attacker 施法者
     * @param target   目标实体
     * @return 如果在范围内返回 true
     */
    public boolean isInRange(LivingEntity attacker, LivingEntity target) {
        boolean inRange = attacker.distanceTo(target) <= range;
        PasterDreamAPI.LOGGER.debug("[EntitySkill] 范围检查: skill={}, attacker={}, target={}, distance={}, range={}, inRange={}",
                name, attacker, target, attacker.distanceTo(target), range, inRange);
        return inRange;
    }
}