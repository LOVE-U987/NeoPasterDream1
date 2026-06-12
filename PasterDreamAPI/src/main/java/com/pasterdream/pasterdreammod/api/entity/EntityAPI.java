package com.pasterdream.pasterdreammod.api.entity;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.entity.builder.EntityBuilder;
import com.pasterdream.pasterdreammod.api.entity.skill.EntitySkill;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, PasterDreamAPI.MOD_ID);

    // ======================== 内部缓存 ========================

    /** 已注册的实体结果缓存 —— 注册名 → EntityResult */
    private static final Map<String, EntityResult<?>> ENTITY_CACHE = new HashMap<>();

    /** 实体属性缓存 —— 注册名 → AttributeSupplier Supplier */
    private static final Map<String, Supplier<AttributeSupplier>> ATTRIBUTES_CACHE = new HashMap<>();

    /** 生成蛋颜色缓存 —— 注册名 → [背景色, 高光色] */
    private static final Map<String, int[]> SPAWN_EGG_COLORS = new HashMap<>();

    /** 实体技能缓存 —— 注册名 → 技能列表 */
    private static final Map<String, List<EntitySkill>> ENTITY_SKILLS = new HashMap<>();

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
        PasterDreamAPI.LOGGER.info("[EntityAPI] 开始创建实体构建器: {}", name);
        return new EntityBuilder<>(REGISTRY, PasterDreamAPI.MOD_ID, name);
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
        PasterDreamAPI.LOGGER.debug("[EntityAPI] 🔍 查找实体属性: {}", result.name());
        Supplier<AttributeSupplier> supplier = ATTRIBUTES_CACHE.get(result.name());
        if (supplier == null) {
            PasterDreamAPI.LOGGER.error("[EntityAPI] ❌ 未找到实体 [{}] 的属性配置", result.name());
            throw new IllegalStateException(
                    "EntityAPI: 未找到实体 [" + result.name() + "] 的属性配置，请在 Builder 中调用 .attributes()");
        }
        PasterDreamAPI.LOGGER.info("[EntityAPI] 📊 注册实体属性: {} | entityType={}", result.name(), result.entityType());
        // EntityAttributeCreationEvent 要求 EntityType<? extends LivingEntity>
        event.put((EntityType<? extends net.minecraft.world.entity.LivingEntity>) result.entityType(), supplier.get());
        PasterDreamAPI.LOGGER.info("[EntityAPI] ✅ 已注册实体属性: {}", result.name());
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
        PasterDreamAPI.LOGGER.info("[EntityAPI] 📊 注册实体属性（显式）: {} | entityType={}", result.name(), result.entityType());
        // EntityAttributeCreationEvent 要求 EntityType<? extends LivingEntity>
        event.put((EntityType<? extends net.minecraft.world.entity.LivingEntity>) result.entityType(), supplier);
        PasterDreamAPI.LOGGER.info("[EntityAPI] ✅ 已注册实体属性: {}", result.name());
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
        PasterDreamAPI.LOGGER.debug("[EntityAPI] 🔍 按名称查找并注册实体属性: {}", entityName);
        EntityResult<?> result = ENTITY_CACHE.get(entityName);
        if (result == null) {
            PasterDreamAPI.LOGGER.error("[EntityAPI] ❌ 未找到实体 [{}] 的注册结果", entityName);
            throw new IllegalStateException("EntityAPI: 未找到实体 [" + entityName + "] 的注册结果");
        }
        Supplier<AttributeSupplier> supplier = ATTRIBUTES_CACHE.get(entityName);
        if (supplier == null) {
            PasterDreamAPI.LOGGER.error("[EntityAPI] ❌ 未找到实体 [{}] 的属性配置", entityName);
            throw new IllegalStateException(
                    "EntityAPI: 未找到实体 [" + entityName + "] 的属性配置，请在 Builder 中调用 .attributes()");
        }
        PasterDreamAPI.LOGGER.debug("[EntityAPI] 注册实体属性: {} | entityType={}", entityName, result.entityType());
        // EntityAttributeCreationEvent 要求 EntityType<? extends LivingEntity>
        event.put((EntityType<? extends net.minecraft.world.entity.LivingEntity>) result.entityType(), supplier.get());
        PasterDreamAPI.LOGGER.info("[EntityAPI] ✅ 已注册实体属性: {}", entityName);
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
            PasterDreamAPI.LOGGER.debug("[EntityAPI] 🔍 查询实体类型: {} → {}", name, result.entityType());
            return result.entityType();
        }
        PasterDreamAPI.LOGGER.debug("[EntityAPI] 🔍 查询实体类型: {} → null（未找到）", name);
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
        PasterDreamAPI.LOGGER.debug("[EntityAPI] 🔍 查询实体结果: {} → {}", name, result != null ? "已找到" : "未找到");
        return result;
    }

    /**
     * 获取所有已注册实体的不可变视图
     *
     * @return 注册名到 EntityResult 的映射
     */
    public static Map<String, EntityResult<?>> getRegisteredEntities() {
        int count = ENTITY_CACHE.size();
        PasterDreamAPI.LOGGER.debug("[EntityAPI] 📊 获取所有已注册实体: 共 {} 个", count);
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
        PasterDreamAPI.LOGGER.debug("[EntityAPI] 🥚 查询生成蛋颜色: {} → {}", name, colors != null ? "已配置" : "未配置");
        return colors;
    }

    // ======================== 技能查询 ========================

    /**
     * 获取实体已注册的技能列表
     *
     * @param entityName 实体注册名称
     * @return 技能列表（不可变），未注册技能返回空列表
     */
    public static List<EntitySkill> getEntitySkills(String entityName) {
        List<EntitySkill> skills = ENTITY_SKILLS.get(entityName);
        if (skills != null) {
            PasterDreamAPI.LOGGER.debug("[EntityAPI] 🎯 查询实体技能: {} → {} 个技能", entityName, skills.size());
            return Collections.unmodifiableList(skills);
        }
        PasterDreamAPI.LOGGER.debug("[EntityAPI] 🎯 查询实体技能: {} → 未配置技能", entityName);
        return Collections.emptyList();
    }

    /**
     * 获取实体指定名称的技能
     *
     * @param entityName 实体注册名称
     * @param skillName  技能名称
     * @return {@link EntitySkill}，未找到返回 null
     */
    public static EntitySkill getEntitySkill(String entityName, String skillName) {
        List<EntitySkill> skills = ENTITY_SKILLS.get(entityName);
        if (skills != null) {
            for (EntitySkill skill : skills) {
                if (skill.name().equals(skillName)) {
                    PasterDreamAPI.LOGGER.debug("[EntityAPI] 🎯 查询实体技能: {}[{}] → 已找到", entityName, skillName);
                    return skill;
                }
            }
        }
        PasterDreamAPI.LOGGER.debug("[EntityAPI] 🎯 查询实体技能: {}[{}] → 未找到", entityName, skillName);
        return null;
    }

    /**
     * 检查实体是否注册了指定技能
     *
     * @param entityName 实体注册名称
     * @param skillName  技能名称
     * @return 如果实体拥有该技能返回 true
     */
    public static boolean hasEntitySkill(String entityName, String skillName) {
        return getEntitySkill(entityName, skillName) != null;
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
        PasterDreamAPI.LOGGER.info("[EntityAPI] 📦 已缓存实体: {} | 缓存总数: {} | entityClass={}", result.name(), total, result.entityClass().getSimpleName());
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
        PasterDreamAPI.LOGGER.info("[EntityAPI] 📦 已缓存实体属性: {} | 属性缓存总数: {}", name, total);
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
        PasterDreamAPI.LOGGER.info("[EntityAPI] 📦 已缓存生成蛋颜色: {} | bg=#{}, hl=#{} | 颜色缓存总数: {}",
                name, Integer.toHexString(backgroundColor), Integer.toHexString(highlightColor), total);
    }

    /**
     * 缓存实体的技能列表（内部使用）
     *
     * @param entityName 实体注册名称
     * @param skills     实体技能列表
     */
    public static void cacheSkills(String entityName, java.util.List<EntitySkill> skills) {
        ENTITY_SKILLS.put(entityName, java.util.List.copyOf(skills));
        int total = ENTITY_SKILLS.size();
        PasterDreamAPI.LOGGER.info("[EntityAPI] 📦 已缓存实体技能: {} | 技能数: {} | 技能缓存实体总数: {}",
                entityName, skills.size(), total);
        for (EntitySkill skill : skills) {
            PasterDreamAPI.LOGGER.debug("[EntityAPI]   ├─ 技能: {} | anim={}, damage={}, range={}, cooldown={}",
                    skill.name(), skill.animationName(), skill.damage(), skill.range(), skill.cooldownTicks());
        }
    }
}