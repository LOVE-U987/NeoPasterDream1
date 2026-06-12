package com.pasterdream.pasterdreammod.api.entity.skill;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;

/**
 * 实体技能构建器 —— 采用 Builder 模式链式配置技能属性
 * <p>
 * 使用示例：
 * <pre>{@code
 * EntitySkill fireBreath = EntitySkill.builder("fire_breath")
 *     .animationName("breath")
 *     .damage(8.0f)
 *     .range(6.0f)
 *     .cooldownTicks(120)       // 6 秒冷却
 *     .particleName("flame")
 *     .soundId("pasterdream:fire_breath")
 *     .build();
 * }</pre>
 *
 * @see EntitySkill
 */
public class EntitySkillBuilder {

    private final String name;

    // 必要参数
    private String animationName;
    private float damage;
    private float range;
    private int cooldownTicks;

    // 可选参数
    private String particleName;
    private String soundId;

    /**
     * 构造技能构建器
     *
     * @param name 技能标识名称（snake_case 格式）
     */
    public EntitySkillBuilder(String name) {
        this.name = name;
        PasterDreamAPI.LOGGER.debug("[EntitySkillBuilder] 创建技能构建器: {}", name);
    }

    // ======================== 动画 ========================

    /**
     * 设置技能动画名称
     * <p>
     * 对应 GeckoLib 模型中的动画名称，播放时通过 procedure 控制器触发。
     *
     * @param animationName 动画名称（如 "dash"、"breath"、"roar"）
     * @return 当前构建器实例
     */
    public EntitySkillBuilder animationName(String animationName) {
        this.animationName = animationName;
        PasterDreamAPI.LOGGER.debug("[EntitySkillBuilder] {} → animationName={}", name, animationName);
        return this;
    }

    // ======================== 伤害 ========================

    /**
     * 设置技能基础伤害
     *
     * @param damage 伤害值
     * @return 当前构建器实例
     */
    public EntitySkillBuilder damage(float damage) {
        this.damage = damage;
        PasterDreamAPI.LOGGER.debug("[EntitySkillBuilder] {} → damage={}", name, damage);
        return this;
    }

    // ======================== 范围 ========================

    /**
     * 设置技能作用范围（单位：格）
     * <p>
     * 用于判断技能是否能命中目标。
     *
     * @param range 作用范围
     * @return 当前构建器实例
     */
    public EntitySkillBuilder range(float range) {
        this.range = range;
        PasterDreamAPI.LOGGER.debug("[EntitySkillBuilder] {} → range={}", name, range);
        return this;
    }

    // ======================== 冷却 ========================

    /**
     * 设置技能冷却时间
     *
     * @param cooldownTicks 冷却时间（tick，20 tick = 1 秒）
     * @return 当前构建器实例
     */
    public EntitySkillBuilder cooldownTicks(int cooldownTicks) {
        this.cooldownTicks = cooldownTicks;
        PasterDreamAPI.LOGGER.debug("[EntitySkillBuilder] {} → cooldownTicks={} ({}秒)", name, cooldownTicks, cooldownTicks / 20.0);
        return this;
    }

    // ======================== 粒子 ========================

    /**
     * 设置技能粒子名称
     * <p>
     * 与 {@link com.pasterdream.pasterdreammod.api.particle.ParticleAPI} 联动，
     * 技能触发时会自动生成对应的粒子效果。
     *
     * @param particleName 粒子注册名称（如 "sparkle"、"flame"）
     * @return 当前构建器实例
     */
    public EntitySkillBuilder particle(String particleName) {
        this.particleName = particleName;
        PasterDreamAPI.LOGGER.debug("[EntitySkillBuilder] {} → particle={}", name, particleName);
        return this;
    }

    // ======================== 音效 ========================

    /**
     * 设置技能音效 ID
     * <p>
     * 技能触发时会播放此音效。
     *
     * @param soundId 音效资源 ID（如 "pasterdream:dash_attack"）
     * @return 当前构建器实例
     */
    public EntitySkillBuilder sound(String soundId) {
        this.soundId = soundId;
        PasterDreamAPI.LOGGER.debug("[EntitySkillBuilder] {} → sound={}", name, soundId);
        return this;
    }

    // ======================== 构建 ========================

    /**
     * 执行构建，完成技能创建
     *
     * @return {@link EntitySkill} 不可变技能实例
     * @throws IllegalStateException 如果缺少必要参数
     */
    public EntitySkill build() {
        PasterDreamAPI.LOGGER.info("[EntitySkillBuilder] ===== 开始构建技能: {} =====", name);
        validate();

        EntitySkill skill = new EntitySkill(
                name, animationName, damage, range,
                cooldownTicks, particleName, soundId
        );

        PasterDreamAPI.LOGGER.info("[EntitySkillBuilder] ✅ 技能构建完成: {} | damage={}, range={}, cooldown={}tick, anim={}, particle={}, sound={}",
                name, damage, range, cooldownTicks, animationName,
                particleName != null ? particleName : "无",
                soundId != null ? soundId : "无");

        return skill;
    }

    /**
     * 验证必要参数是否已配置
     *
     * @throws IllegalStateException 如果缺少必要参数
     */
    private void validate() {
        if (animationName == null || animationName.isEmpty()) {
            PasterDreamAPI.LOGGER.error("[EntitySkillBuilder] ❌ 验证失败: {} → animationName 未设置", name);
            throw new IllegalStateException("EntitySkillBuilder: 缺少动画名称（animationName），请调用 .animationName() 设置");
        }
        if (damage < 0) {
            PasterDreamAPI.LOGGER.error("[EntitySkillBuilder] ❌ 验证失败: {} → damage 无效: {}", name, damage);
            throw new IllegalStateException("EntitySkillBuilder: 伤害值不能为负数");
        }
        if (range <= 0) {
            PasterDreamAPI.LOGGER.error("[EntitySkillBuilder] ❌ 验证失败: {} → range 无效: {}", name, range);
            throw new IllegalStateException("EntitySkillBuilder: 作用范围必须大于 0");
        }
        if (cooldownTicks <= 0) {
            PasterDreamAPI.LOGGER.error("[EntitySkillBuilder] ❌ 验证失败: {} → cooldownTicks 无效: {}", name, cooldownTicks);
            throw new IllegalStateException("EntitySkillBuilder: 冷却时间必须大于 0");
        }
        PasterDreamAPI.LOGGER.debug("[EntitySkillBuilder] ✅ 验证通过: {}", name);
    }
}