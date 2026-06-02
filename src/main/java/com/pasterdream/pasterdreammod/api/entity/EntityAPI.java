package com.pasterdream.pasterdreammod.api.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.entity.builder.EntityBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 实体注册 API —— 将繁琐的实体注册集中管理，提供高效简洁的注册方式
 * <p>
 * 采用与 {@link com.pasterdream.pasterdreammod.api.block.BlockAPI} 相似的
 * Facade 模式 + Builder 模式设计，覆盖实体注册的完整流程。
 * <p>
 * 注意：此类不包含任何客户端专属类型引用，确保服务端兼容。
 * 客户端渲染器注册请直接使用 {@code EntityRenderersEvent.RegisterRenderers} 事件。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // ====== 在 PDEntities.java 中使用 ======
 * EntityResult<ShadowGolemEntity> shadowGolem = EntityAPI.createEntity("shadow_golem")
 *     .category(MobCategory.MONSTER)
 *     .size(2.2f, 3.5f)
 *     .trackingRange(64)
 *     .entityClass(ShadowGolemEntity.class)
 *     .attributes(ShadowGolemEntity::createAttributes)
 *     .build();
 *
 * // ====== 在 ClientSetup 中注册渲染器 ======
 * EntityResult<ShadowGolemEntity> result = (EntityResult<ShadowGolemEntity>)
 *     EntityAPI.getEntityResult("shadow_golem");
 * event.registerEntityRenderer(result.entityType(), ShadowGolemRenderer::new);
 *
 * // ====== 在 PDEntityEvents 中注册属性 ======
 * EntityAPI.registerAttributes(event, shadowGolem);
 * }</pre>
 */
public final class EntityAPI {

    /**
     * API 专属的实体类型注册器。
     * 注意：需要在 {@code PasterDreamMod} 构造函数中注册到事件总线：
     * <pre>{@code
     * EntityAPI.REGISTRY.register(modEventBus);
     * }</pre>
     */
    public static final DeferredRegister<EntityType<?>> REGISTRY =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, PasterDreamMod.MOD_ID);

    // ======================== 内部缓存 ========================

    /** 已注册的实体结果缓存 —— 注册名 → EntityResult */
    private static final Map<String, EntityResult<?>> ENTITY_CACHE = new HashMap<>();

    /** 实体属性缓存 —— 注册名 → AttributeSupplier Supplier */
    private static final Map<String, Supplier<AttributeSupplier>> ATTRIBUTES_CACHE = new HashMap<>();

    /** 生成蛋颜色缓存 —— 注册名 → [背景色, 高光色] */
    private static final Map<String, int[]> SPAWN_EGG_COLORS = new HashMap<>();

    private EntityAPI() {
        throw new UnsupportedOperationException("EntityAPI 是纯静态门面类，不可实例化");
    }

    // ======================== Builder 工厂方法 ========================

    /**
     * 创建一个实体构建器
     * <p>
     * 采用链式调用配置实体的各项参数，
     * 最终通过 {@link EntityBuilder#build()} 完成注册并返回 {@link EntityResult}。
     *
     * @param name 实体注册名称（snake_case 格式，如 "shadow_golem"）
     * @return {@link EntityBuilder} 实例
     */
    public static EntityBuilder<?> createEntity(String name) {
        PasterDreamMod.LOGGER.info("[EntityAPI] 开始创建实体构建器: {}", name);
        return new EntityBuilder<>(REGISTRY, PasterDreamMod.MOD_ID, name);
    }

    // ======================== 属性注册 ========================

    /**
     * 注册实体属性（使用 Builder 中缓存的属性）
     * <p>
     * 如果在 {@link EntityBuilder#attributes(Supplier)} 中已配置属性，
     * 可直接调用此方法自动注册。
     *
     * @param event  {@link EntityAttributeCreationEvent}
     * @param result 之前由 {@link #createEntity(String)} 返回的结果
     * @throws IllegalStateException 如果在 Builder 中未配置属性
     */
    public static void registerAttributes(
            EntityAttributeCreationEvent event,
            EntityResult<?> result) {
        PasterDreamMod.LOGGER.debug("[EntityAPI] 🔍 查找实体属性: {}", result.name());
        Supplier<AttributeSupplier> supplier = ATTRIBUTES_CACHE.get(result.name());
        if (supplier == null) {
            PasterDreamMod.LOGGER.error("[EntityAPI] ❌ 未找到实体 [{}] 的属性配置", result.name());
            throw new IllegalStateException(
                    "EntityAPI: 未找到实体 [" + result.name() + "] 的属性配置，请在 Builder 中调用 .attributes()");
        }
        PasterDreamMod.LOGGER.info("[EntityAPI] 📊 注册实体属性: {} | entityType={}", result.name(), result.entityType());
        // EntityAttributeCreationEvent 要求 EntityType<? extends LivingEntity>
        event.put((EntityType<? extends net.minecraft.world.entity.LivingEntity>) result.entityType(), supplier.get());
        PasterDreamMod.LOGGER.info("[EntityAPI] ✅ 已注册实体属性: {}", result.name());
    }

    /**
     * 注册实体属性（显式指定 AttributeSupplier）
     *
     * @param event    {@link EntityAttributeCreationEvent}
     * @param result   之前由 {@link #createEntity(String)} 返回的结果
     * @param supplier {@link AttributeSupplier} 实例
     */
    public static void registerAttributes(
            EntityAttributeCreationEvent event,
            EntityResult<?> result,
            AttributeSupplier supplier) {
        PasterDreamMod.LOGGER.info("[EntityAPI] 📊 注册实体属性（显式）: {} | entityType={}", result.name(), result.entityType());
        // EntityAttributeCreationEvent 要求 EntityType<? extends LivingEntity>
        event.put((EntityType<? extends net.minecraft.world.entity.LivingEntity>) result.entityType(), supplier);
        PasterDreamMod.LOGGER.info("[EntityAPI] ✅ 已注册实体属性: {}", result.name());
    }

    /**
     * 按实体名称注册属性（使用 Builder 中缓存的属性）
     * <p>
     * 便捷重载，自动根据实体名称查找已注册的结果和属性配置。
     *
     * @param event      {@link EntityAttributeCreationEvent}
     * @param entityName 实体注册名称（与 {@link #createEntity(String)} 传入的名称一致）
     * @throws IllegalStateException 如果未找到对应的实体注册结果或属性配置
     */
    public static void registerAttributes(
            EntityAttributeCreationEvent event,
            String entityName) {
        PasterDreamMod.LOGGER.debug("[EntityAPI] 🔍 按名称查找并注册实体属性: {}", entityName);
        EntityResult<?> result = ENTITY_CACHE.get(entityName);
        if (result == null) {
            PasterDreamMod.LOGGER.error("[EntityAPI] ❌ 未找到实体 [{}] 的注册结果", entityName);
            throw new IllegalStateException("EntityAPI: 未找到实体 [" + entityName + "] 的注册结果");
        }
        Supplier<AttributeSupplier> supplier = ATTRIBUTES_CACHE.get(entityName);
        if (supplier == null) {
            PasterDreamMod.LOGGER.error("[EntityAPI] ❌ 未找到实体 [{}] 的属性配置", entityName);
            throw new IllegalStateException(
                    "EntityAPI: 未找到实体 [" + entityName + "] 的属性配置，请在 Builder 中调用 .attributes()");
        }
        PasterDreamMod.LOGGER.debug("[EntityAPI] 注册实体属性: {} | entityType={}", entityName, result.entityType());
        // EntityAttributeCreationEvent 要求 EntityType<? extends LivingEntity>
        event.put((EntityType<? extends net.minecraft.world.entity.LivingEntity>) result.entityType(), supplier.get());
        PasterDreamMod.LOGGER.info("[EntityAPI] ✅ 已注册实体属性: {}", entityName);
    }

    // ======================== 查询方法 ========================

    /**
     * 根据实体注册名称查询已注册的实体类型
     *
     * @param name 实体注册名称
     * @return {@link EntityType}，如果未找到返回 null
     */
    public static EntityType<?> getEntityType(String name) {
        EntityResult<?> result = ENTITY_CACHE.get(name);
        if (result != null) {
            PasterDreamMod.LOGGER.debug("[EntityAPI] 🔍 查询实体类型: {} → {}", name, result.entityType());
            return result.entityType();
        }
        PasterDreamMod.LOGGER.debug("[EntityAPI] 🔍 查询实体类型: {} → null（未找到）", name);
        return null;
    }

    /**
     * 根据实体注册名称查询 {@link EntityResult}
     *
     * @param name 实体注册名称
     * @return {@link EntityResult}，如果未找到返回 null
     */
    public static EntityResult<?> getEntityResult(String name) {
        EntityResult<?> result = ENTITY_CACHE.get(name);
        PasterDreamMod.LOGGER.debug("[EntityAPI] 🔍 查询实体结果: {} → {}", name, result != null ? "已找到" : "未找到");
        return result;
    }

    /**
     * 获取所有已注册实体的不可变视图
     *
     * @return 注册名到 EntityResult 的映射
     */
    public static Map<String, EntityResult<?>> getRegisteredEntities() {
        int count = ENTITY_CACHE.size();
        PasterDreamMod.LOGGER.debug("[EntityAPI] 📊 获取所有已注册实体: 共 {} 个", count);
        return Collections.unmodifiableMap(ENTITY_CACHE);
    }

    // ======================== 生成蛋颜色查询 ========================

    /**
     * 获取生成蛋颜色配置
     *
     * @param name 实体注册名称
     * @return 长度为 2 的 int 数组 [背景色, 高光色]，未配置返回 null
     */
    public static int[] getSpawnEggColors(String name) {
        int[] colors = SPAWN_EGG_COLORS.get(name);
        PasterDreamMod.LOGGER.debug("[EntityAPI] 🥚 查询生成蛋颜色: {} → {}", name, colors != null ? "已配置" : "未配置");
        return colors;
    }

    // ======================== 内部缓存方法 (Builder 调用) ========================

    /**
     * 缓存已注册的实体结果
     *
     * @param result 实体注册结果
     */
    public static void cacheEntity(EntityResult<?> result) {
        ENTITY_CACHE.put(result.name(), result);
        int total = ENTITY_CACHE.size();
        PasterDreamMod.LOGGER.info("[EntityAPI] 📦 已缓存实体: {} | 缓存总数: {} | entityClass={}", result.name(), total, result.entityClass().getSimpleName());
    }

    /**
     * 缓存实体属性 Supplier
     *
     * @param name             实体注册名称
     * @param attributeSupplier 属性 Supplier
     */
    public static void cacheAttributes(String name, Supplier<AttributeSupplier> attributeSupplier) {
        ATTRIBUTES_CACHE.put(name, attributeSupplier);
        int total = ATTRIBUTES_CACHE.size();
        PasterDreamMod.LOGGER.info("[EntityAPI] 📦 已缓存实体属性: {} | 属性缓存总数: {}", name, total);
    }

    /**
     * 缓存生成蛋颜色
     *
     * @param name            实体注册名称
     * @param backgroundColor 背景色
     * @param highlightColor  高光色
     */
    public static void cacheSpawnEgg(String name, int backgroundColor, int highlightColor) {
        SPAWN_EGG_COLORS.put(name, new int[]{backgroundColor, highlightColor});
        int total = SPAWN_EGG_COLORS.size();
        PasterDreamMod.LOGGER.info("[EntityAPI] 📦 已缓存生成蛋颜色: {} | bg=#{}, hl=#{} | 颜色缓存总数: {}",
                name, Integer.toHexString(backgroundColor), Integer.toHexString(highlightColor), total);
    }
}