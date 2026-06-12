package com.pasterdream.pasterdreammod.api.entity.skill;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.particle.ParticleAPI;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体技能管理器 —— 管理实体的技能注册、冷却、触发和执行
 * <p>
 * 可在实体类中持有此管理器，在 {@code baseTick()} 中调用 {@link #tick()}
 * 以处理冷却递减，在需要时调用 {@link #tryTriggerSkill(String, LivingEntity)} 尝试触发技能。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 在实体类中：
 * private final EntitySkillManager skillManager = new EntitySkillManager(this);
 *
 * // 注册技能（构造方法中）
 * public MyEntity(EntityType<? extends Monster> type, Level level) {
 *     super(type, level);
 *     skillManager.registerSkill(EntitySkill.builder("dash_attack")
 *         .animationName("dash")
 *         .damage(10.0f).range(5.0f).cooldownTicks(100)
 *         .particle("sparkle")
 *         .sound("pasterdream:dash_attack")
 *         .build());
 * }
 *
 * // 每 tick 更新
 * @Override
 * public void baseTick() {
 *     super.baseTick();
 *     skillManager.tick();
 * }
 *
 * // 尝试触发技能
 * public void doSkill() {
 *     LivingEntity target = getTarget();
 *     if (target != null) {
 *         skillManager.tryTriggerSkill("dash_attack", target);
 *     }
 * }
 * }</pre>
 *
 * @see EntitySkill
 * @see EntitySkillBuilder
 */
public class EntitySkillManager {

    private final Mob entity;
    private final Map<String, EntitySkill> skills = new ConcurrentHashMap<>();
    private final Map<String, Integer> cooldowns = new ConcurrentHashMap<>();

    /** 当前正在执行的技能（如果有） */
    @Nullable
    private EntitySkill currentSkill = null;

    /** 技能执行阶段计时器 */
    private int skillTimer = 0;

    /** 当前客户端正在播放的procedure动画（防止重复设置） */
    private String currentlyPlayingAnim = "empty";

    // ======================== 构造 ========================

    /**
     * 创建实体技能管理器
     *
     * @param entity 持有此管理器的实体实例
     */
    public EntitySkillManager(Mob entity) {
        this.entity = entity;
        PasterDreamAPI.LOGGER.debug("[EntitySkillManager] 创建技能管理器: entity={}", entity);
    }

    // ======================== 技能注册 ========================

    /**
     * 注册一个技能到实体
     *
     * @param skill 技能定义
     * @return 当前管理器实例（方便链式调用）
     */
    public EntitySkillManager registerSkill(EntitySkill skill) {
        skills.put(skill.name(), skill);
        cooldowns.put(skill.name(), 0);
        PasterDreamAPI.LOGGER.info("[EntitySkillManager] 注册技能: {} → {} | 技能总数: {}", entity, skill.name(), skills.size());
        return this;
    }

    /**
     * 批量注册多个技能
     *
     * @param skillArray 技能数组
     * @return 当前管理器实例（方便链式调用）
     */
    public EntitySkillManager registerSkills(EntitySkill... skillArray) {
        for (EntitySkill skill : skillArray) {
            registerSkill(skill);
        }
        PasterDreamAPI.LOGGER.info("[EntitySkillManager] 批量注册技能完成: {} 个技能已注册", skillArray.length);
        return this;
    }

    // ======================== Tick 更新 ========================

    /**
     * 每 tick 调用，处理冷却递减和技能执行阶段
     * <p>
     * 需要在实体类的 {@code baseTick()} 或 {@code aiStep()} 中调用。
     */
    public void tick() {
        // 处理冷却递减
        for (Map.Entry<String, Integer> entry : cooldowns.entrySet()) {
            if (entry.getValue() > 0) {
                entry.setValue(entry.getValue() - 1);
            }
        }

        // 处理技能执行阶段
        if (skillTimer > 0) {
            tickSkillExecution();
        }
    }

    /**
     * 处理技能执行阶段的 tick
     * <p>
     * 子类可重写此方法以自定义技能执行逻辑。
     * 默认行为：在 skillTimer == 45 时执行伤害/粒子/音效。
     */
    protected void tickSkillExecution() {
        skillTimer++;

        // 默认在 skillTimer == 2 时播放音效
        if (currentSkill != null && skillTimer == 2 && currentSkill.soundId() != null) {
            playSkillSound(currentSkill);
        }

        // 默认在 skillTimer == 45 时执行技能效果
        if (currentSkill != null && skillTimer == 45) {
            executeSkillEffects(currentSkill);
        }

        // 技能执行完毕（默认 46 tick ≈ 2.3 秒）
        if (skillTimer >= 46) {
            finishCurrentSkill();
        }
    }

    // ======================== 技能触发 ========================

    /**
     * 尝试触发指定技能
     * <p>
     * 如果技能在冷却中或已有技能正在执行，则返回 false。
     *
     * @param skillName 技能名称
     * @param target    技能目标实体
     * @return 技能是否成功触发
     */
    public boolean tryTriggerSkill(String skillName, @Nullable LivingEntity target) {
        // 检查是否有技能正在执行
        if (skillTimer > 0) {
            PasterDreamAPI.LOGGER.debug("[EntitySkillManager] 技能 [{}] 触发失败: 有技能正在执行 [{}]", skillName,
                    currentSkill != null ? currentSkill.name() : "unknown");
            return false;
        }

        EntitySkill skill = skills.get(skillName);
        if (skill == null) {
            PasterDreamAPI.LOGGER.warn("[EntitySkillManager] 技能 [{}] 未注册到实体 {}", skillName, entity);
            return false;
        }

        // 检查冷却
        int remainingCooldown = cooldowns.getOrDefault(skillName, 0);
        if (remainingCooldown > 0) {
            PasterDreamAPI.LOGGER.debug("[EntitySkillManager] 技能 [{}] 冷却中: 剩余 {} tick", skillName, remainingCooldown);
            return false;
        }

        // 触发技能
        startSkill(skill, target);
        return true;
    }

    /**
     * 启动技能
     *
     * @param skill  技能定义
     * @param target 技能目标
     */
    protected void startSkill(EntitySkill skill, @Nullable LivingEntity target) {
        PasterDreamAPI.LOGGER.info("[EntitySkillManager] 🎯 技能触发: {} | 施法者={}, 目标={}", skill.name(), entity, target);

        currentSkill = skill;
        skillTimer = 1;
        cooldowns.put(skill.name(), skill.cooldownTicks());

        // 设置动画（服务端同步数据，客户端播放）
        setAnimation(skill.animationName());

        // 播放粒子（如果有配置）
        if (skill.particleName() != null && entity.level() instanceof ServerLevel serverLevel) {
            spawnSkillParticles(serverLevel, skill);
        }
    }

    /**
     * 完成当前技能
     */
    protected void finishCurrentSkill() {
        if (currentSkill != null) {
            PasterDreamAPI.LOGGER.info("[EntitySkillManager] ✅ 技能完成: {}", currentSkill.name());
        }
        skillTimer = 0;
        currentSkill = null;
        setAnimation("empty");
    }

    // ======================== 技能效果执行 ========================

    /**
     * 执行技能效果（伤害 + 击飞）
     * <p>
     * 子类可重写此方法以自定义效果逻辑。
     *
     * @param skill 技能定义
     */
    protected void executeSkillEffects(EntitySkill skill) {
        PasterDreamAPI.LOGGER.debug("[EntitySkillManager] 执行技能效果: {} | damage={}, range={}", skill.name(), skill.damage(), skill.range());

        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

        // 对范围内所有非自身的 LivingEntity 造成伤害
        AABB area = new AABB(entity.blockPosition()).inflate(skill.range());
        List<LivingEntity> targets = entity.level().getEntitiesOfClass(
                LivingEntity.class, area,
                e -> e != entity && e.isAlive()
        );

        for (LivingEntity target : targets) {
            target.hurt(entity.damageSources().mobAttack(entity), skill.damage());
            // 击飞效果
            Vec3 knockback = target.position().subtract(entity.position()).normalize()
                    .scale(1.5).add(0, 0.5, 0);
            target.setDeltaMovement(knockback);
            target.hurtMarked = true;
        }

        PasterDreamAPI.LOGGER.info("[EntitySkillManager] 💥 技能 [{}] 对 {} 个目标造成 {} 点伤害", skill.name(), targets.size(), skill.damage());
    }

    // ======================== 粒子效果 ========================

    /**
     * 生成技能触发时的粒子效果
     *
     * @param level 服务端世界
     * @param skill 技能定义
     */
    protected void spawnSkillParticles(ServerLevel level, EntitySkill skill) {
        if (skill.particleName() == null) return;

        ParticleType<?> particleType = ParticleAPI.getParticleType(skill.particleName());
        if (particleType == null) {
            PasterDreamAPI.LOGGER.warn("[EntitySkillManager] 粒子 [{}] 未找到，跳过粒子生成", skill.particleName());
            return;
        }

        // 生成一圈扩散粒子
        for (int i = 0; i < 30; i++) {
            double offsetX = (entity.getRandom().nextDouble() - 0.5) * skill.range() * 2;
            double offsetY = entity.getRandom().nextDouble() * 2;
            double offsetZ = (entity.getRandom().nextDouble() - 0.5) * skill.range() * 2;
            level.sendParticles(
                    (ParticleOptions) particleType,
                    entity.getX() + offsetX,
                    entity.getY() + offsetY,
                    entity.getZ() + offsetZ,
                    1, 0, 0, 0, 0.05
            );
        }

        PasterDreamAPI.LOGGER.debug("[EntitySkillManager] ✨ 技能 [{}] 生成粒子: {} (x30)", skill.name(), skill.particleName());
    }

    // ======================== 音效 ========================

    /**
     * 播放技能音效
     *
     * @param skill 技能定义
     */
    protected void playSkillSound(EntitySkill skill) {
        if (skill.soundId() == null) return;

        entity.level().playSound(null, entity.blockPosition(),
                skill.getSoundEvent(), SoundSource.HOSTILE, 1.0f, 1.0f);
        PasterDreamAPI.LOGGER.debug("[EntitySkillManager] 🔊 技能 [{}] 播放音效: {}", skill.name(), skill.soundId());
    }

    // ======================== 动画 ========================

    /**
     * 设置实体动画（用于同步客户端和服务端）
     * <p>
     * 实体类需要配合重写数据同步方法和 GeckoLib 动画控制器。
     *
     * @param animation 动画名称
     */
    protected void setAnimation(String animation) {
        if (entity instanceof IAnimatedEntity animated) {
            animated.setSyncedAnimation(animation);
            // 同时更新 animationprocedure 以触发 procedure 动画控制器
            if (entity instanceof IProcedureAnimatable procedureAnimatable) {
                procedureAnimatable.setAnimationProcedure(animation);
            }
        }
        PasterDreamAPI.LOGGER.debug("[EntitySkillManager] 🎬 设置动画: entity={}, animation={}", entity, animation);
    }

    // ======================== 查询方法 ========================

    /**
     * 获取已注册的技能
     *
     * @param skillName 技能名称
     * @return {@link EntitySkill}，未注册返回 null
     */
    @Nullable
    public EntitySkill getSkill(String skillName) {
        return skills.get(skillName);
    }

    /**
     * 获取所有已注册的技能
     *
     * @return 技能名称到技能定义的映射
     */
    public Map<String, EntitySkill> getSkills() {
        return Map.copyOf(skills);
    }

    /**
     * 检查技能是否在冷却中
     *
     * @param skillName 技能名称
     * @return 如果在冷却中返回 true
     */
    public boolean isOnCooldown(String skillName) {
        return cooldowns.getOrDefault(skillName, 0) > 0;
    }

    /**
     * 获取技能剩余冷却时间
     *
     * @param skillName 技能名称
     * @return 剩余冷却 tick 数，0 表示可用
     */
    public int getRemainingCooldown(String skillName) {
        return cooldowns.getOrDefault(skillName, 0);
    }

    /**
     * 当前是否有技能正在执行
     *
     * @return 如果有技能正在执行返回 true
     */
    public boolean isSkillActive() {
        return skillTimer > 0;
    }

    /**
     * 获取当前正在执行的技能
     *
     * @return 当前技能，无则返回 null
     */
    @Nullable
    public EntitySkill getCurrentSkill() {
        return currentSkill;
    }

    /**
     * 获取技能执行计时器
     *
     * @return 当前技能执行阶段的 tick 数
     */
    public int getSkillTimer() {
        return skillTimer;
    }

    /**
     * 获取当前客户端播放的 procedure 动画名称
     *
     * @return 动画名称
     */
    public String getCurrentlyPlayingAnim() {
        return currentlyPlayingAnim;
    }

    /**
     * 设置当前客户端播放的 procedure 动画名称
     *
     * @param anim 动画名称
     */
    public void setCurrentlyPlayingAnim(String anim) {
        this.currentlyPlayingAnim = anim;
    }

    // ======================== 接口定义 ========================

    /**
     * 可动画实体的接口 —— 实体类需实现此接口以支持技能管理器设置动画
     */
    public interface IAnimatedEntity {
        /**
         * 获取同步数据中的动画名称
         *
         * @return 动画名称
         */
        String getSyncedAnimation();

        /**
         * 设置同步数据中的动画名称
         *
         * @param animation 动画名称
         */
        void setSyncedAnimation(String animation);
    }

    /**
     * procedure 动画控制器接口 —— 实体类需实现此接口以支持 procedure 动画控制器
     */
    public interface IProcedureAnimatable {
        /**
         * 获取当前的 animationprocedure
         *
         * @return 动画 procedure 名称
         */
        String getAnimationProcedure();

        /**
         * 设置当前的 animationprocedure
         *
         * @param procedure 动画 procedure 名称
         */
        void setAnimationProcedure(String procedure);
    }
}