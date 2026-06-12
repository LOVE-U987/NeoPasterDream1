package com.pasterdream.pasterdreammod.api.entity.gen;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.FlyingAnimal;

/**
 * 实体属性生成器 —— 提供常用实体类型的属性模板工厂方法
 * <p>
 * 封装了原版 {@link Mob#createMobAttributes()} 的调用，
 * 提供怪物、动物、飞行生物等预设属性模板，
 * 配合 {@link com.pasterdream.pasterdreammod.api.entity.builder.EntityBuilder#attributes(java.util.function.Supplier)} 使用。
 * <p>
 * 使用示例：
 * <pre>{@code
 * EntityAPI.createEntity("shadow_beast")
 *     .category(MobCategory.MONSTER)
 *     .size(1.5f, 2.0f)
 *     .entityClass(ShadowBeastEntity.class)
 *     .attributes(EntityAttributesGenerator::createMonsterAttributes)
 *     .build();
 * }</pre>
 *
 * @see com.pasterdream.pasterdreammod.api.entity.builder.EntityBuilder
 */
public final class EntityAttributesGenerator {

    private EntityAttributesGenerator() {
        throw new UnsupportedOperationException("EntityAttributesGenerator 是纯静态工具类，不可实例化");
    }

    /**
     * 创建标准怪物属性模板
     * <p>
     * 基于 {@link Mob#createMobAttributes()}，额外配置：
     * <ul>
     *   <li>攻击力: 3.0（可后续覆盖）</li>
     *   <li>盔甲: 2.0</li>
     *   <li>追踪范围: 32 格</li>
     * </ul>
     *
     * @return 怪物属性构造器
     */
    public static AttributeSupplier.Builder createMonsterAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.FOLLOW_RANGE, 32);
    }

    /**
     * 创建标准动物属性模板
     * <p>
     * 基于 {@link Mob#createMobAttributes()}，额外配置：
     * <ul>
     *   <li>移动速度: 0.2</li>
     *   <li>最大生命: 10.0</li>
     *   <li>追踪范围: 16 格</li>
     * </ul>
     *
     * @return 动物属性构造器
     */
    public static AttributeSupplier.Builder createCreatureAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FOLLOW_RANGE, 16);
    }

    /**
     * 创建飞行生物属性模板
     * <p>
     * 基于 {@link Mob#createMobAttributes()} 并增加飞行相关属性：
     * <ul>
     *   <li>移动速度: 0.2</li>
     *   <li>最大生命: 10.0</li>
     *   <li>追踪范围: 24 格</li>
     *   <li>飞行速度: 0.4</li>
     * </ul>
     *
     * @return 飞行生物属性构造器
     */
    public static AttributeSupplier.Builder createFlyingAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.FLYING_SPEED, 0.4)
                .add(Attributes.FOLLOW_RANGE, 24);
    }

    /**
     * 创建水生生物属性模板
     *
     * @return 水生生物属性构造器
     */
    public static AttributeSupplier.Builder createWaterCreatureAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 16);
    }
}