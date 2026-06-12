package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.api.entity.EntityResult;
import com.pasterdream.pasterdreammod.entity.mob.PinkSlimeEntity;
import com.pasterdream.pasterdreammod.entity.mob.ShadowGolemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 实体注册类
 * <p>
 * 使用 {@link EntityAPI} 的 Facade+Builder 模式注册所有实体，
 * 同时保持 {@link #SHADOW_GOLEM} / {@link #PINK_SLIME} 常量的向后兼容性。
 */
public class PDEntities {

    /**
     * 实体类型注册器（指向 {@link EntityAPI#REGISTRY}）
     */
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = EntityAPI.REGISTRY;

    // ======================== EntityAPI 注册 ========================

    /**
     * 暗影魔像实体注册结果
     * 大型暗影主题怪物，使用 GeckoLib 动画
     * 尺寸: 2.2f x 3.5f
     */
    private static final EntityResult<ShadowGolemEntity> SHADOW_GOLEM_RESULT =
            EntityAPI.createEntity("shadow_golem")
                    .category(MobCategory.MONSTER)
                    .size(2.2f, 3.5f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(ShadowGolemEntity.class)
                    .attributes(ShadowGolemEntity::createAttributes)
                    .build();

    /**
     * 粉色史莱姆实体注册结果
     * 友好的粉色史莱姆生物，使用原版模型
     * 尺寸: 0.5f x 0.5f
     */
    private static final EntityResult<PinkSlimeEntity> PINK_SLIME_RESULT =
            EntityAPI.createEntity("pink_slime")
                    .category(MobCategory.CREATURE)
                    .size(0.5f, 0.5f)
                    .trackingRange(64)
                    .updateInterval(3)
                    .velocityUpdates(true)
                    .entityClass(PinkSlimeEntity.class)
                    .attributes(PinkSlimeEntity::createAttributes)
                    .build();

    // ======================== 向后兼容常量 ========================

    /**
     * 暗影魔像实体 (shadow_golem) — 向后兼容引用
     */
    public static final DeferredHolder<EntityType<?>, EntityType<ShadowGolemEntity>> SHADOW_GOLEM =
            SHADOW_GOLEM_RESULT.deferredHolder();

    /**
     * 粉色史莱姆实体 (pink_slime) — 向后兼容引用
     */
    public static final DeferredHolder<EntityType<?>, EntityType<PinkSlimeEntity>> PINK_SLIME =
            PINK_SLIME_RESULT.deferredHolder();
}
