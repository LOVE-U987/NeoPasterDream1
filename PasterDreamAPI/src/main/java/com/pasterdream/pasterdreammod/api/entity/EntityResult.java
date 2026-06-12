package com.pasterdream.pasterdreammod.api.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * 实体注册结果 —— 包含实体注册后的所有引用信息
 * <p>
 * 由 {@link EntityBuilder#build()} 返回，
 * 持有实体类型 {@link Supplier} 和实体 {@link Class}，
 * 方便在属性注册、渲染器注册等后续流程中引用。
 * <p>
 * 使用示例：
 * <pre>{@code
 * EntityResult<ShadowGolemEntity> result = EntityAPI.createEntity("shadow_golem")
 *     .category(MobCategory.MONSTER)
 *     .size(2.2f, 3.5f)
 *     .entityClass(ShadowGolemEntity.class)
 *     .attributes(ShadowGolemEntity::createAttributes)
 *     .build();
 *
 * // 获取 EntityType
 * EntityType<ShadowGolemEntity> type = result.entityType();
 *
 * // 获取实体类
 * Class<ShadowGolemEntity> clazz = result.entityClass();
 * }</pre>
 *
 * @param name               实体注册名称（snake_case 格式，如 "shadow_golem"）
 * @param entityTypeSupplier 实体类型 Supplier（通常为 DeferredHolder）
 * @param entityClass        实体类对象
 * @param <T>                实体类型参数
 */
public record EntityResult<T extends Entity>(
        String name,
        Supplier<EntityType<T>> entityTypeSupplier,
        Class<T> entityClass
) {

    /**
     * 便捷获取 {@link EntityType}
     *
     * @return 已注册的实体类型
     */
    public EntityType<T> entityType() {
        return entityTypeSupplier.get();
    }

    /**
     * 获取原始的 {@link DeferredHolder}，用于需要 DeferredHolder 引用的场景
     *
     * @return DeferredHolder 实例
     * @deprecated 此方法仅为向后兼容保留，将在下个主版本移除。
     *             请直接使用 {@link #entityTypeSupplier()} 获取 Supplier 引用。
     */
    @Deprecated(forRemoval = true, since = "0.0.3.2")
    @SuppressWarnings("unchecked")
    public DeferredHolder<EntityType<?>, EntityType<T>> deferredHolder() {
        return (DeferredHolder<EntityType<?>, EntityType<T>>) entityTypeSupplier;
    }
}