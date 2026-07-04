package com.pasterdream.pasterdreammod.entity.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

/**
 * 可配置伤害免疫的实体基类
 * <p>
 * 所有继承此类的实体将自动获得基于 {@link DamageImmunityConfig} 的伤害免疫判断，
 * 无需在每个实体类中重复编写 {@code hurt()} 方法。
 * <p>
 * 免疫规则通过 {@link EntityImmunitySetup#setupAllImmunities()} 集中配置，
 * 使用 {@link DamageImmunityConfig#getInstance()} 获取全局单例配置。
 * <p>
 * 注意：此基类继承 {@link PathfinderMob}，适用于 {@code PathfinderMob} 和 {@code Monster} 两种实体。
 * <p>
 * 非 {@link PathfinderMob} 子类（如投射物）无法继承此类，应直接调用
 * {@link DamageImmunityConfig#isImmune(Entity, DamageSource)} 进行免疫检查。
 *
 * @see DamageImmunityConfig
 * @see EntityImmunitySetup
 */
public abstract class ConfigurableImmunityEntity extends PathfinderMob {

    /**
     * 构造可配置免疫实体
     *
     * @param entityType 实体类型
     * @param level      世界实例
     */
    protected ConfigurableImmunityEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * 统一的伤害处理逻辑
     * <p>
     * 如果 {@link DamageImmunityConfig} 中配置了该实体对当前伤害类型免疫，则返回 {@code false}，
     * 否则交给父类处理。
     * <p>
     * 使用 {@link DamageImmunityConfig#getInstance()} 获取全局单例配置，确保所有实体共享同一套免疫规则。
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (DamageImmunityConfig.getInstance().isImmune(this, source)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    /**
     * 获取全局共享的伤害免疫配置实例
     * <p>
     * 供 {@link EntityImmunitySetup} 在模组初始化时配置所有实体的免疫规则。
     *
     * @return 全局 {@link DamageImmunityConfig} 实例（等同于 {@link DamageImmunityConfig#getInstance()}）
     */
    public static DamageImmunityConfig getImmunityConfig() {
        return DamageImmunityConfig.getInstance();
    }
}
