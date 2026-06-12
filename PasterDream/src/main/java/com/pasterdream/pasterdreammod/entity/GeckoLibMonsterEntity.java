package com.pasterdream.pasterdreammod.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * GeckoLib 怪物实体基类
 * 为使用 GeckoLib 动画的怪物提供基础功能
 */
public abstract class GeckoLibMonsterEntity extends Monster implements GeoEntity {

    /**
     * 动画实例缓存
     */
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 构造 GeckoLib 怪物实体
     *
     * @param entityType 实体类型
     * @param level      世界层级
     */
    public GeckoLibMonsterEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * 注册动画控制器
     * 子类需要重写此方法以定义动画
     *
     * @param controllers 动画控制器注册器
     */
    @Override
    public abstract void registerControllers(AnimatableManager.ControllerRegistrar controllers);

    /**
     * 获取动画实例缓存
     *
     * @return 动画实例缓存
     */
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    /**
     * 创建基础属性构建器
     *
     * @return 属性构建器
     */
    public static AttributeSupplier.Builder createMonsterAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.FOLLOW_RANGE, 16);
    }
}
