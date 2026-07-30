package com.pasterdream.pasterdreammod.api.entity.tag;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.entity.EntityResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 实体标签注册表 —— 统一管理实体到内置标签的绑定关系
 * <p>
 * 支持单个实体注册、批量注册以及通过 {@link com.pasterdream.pasterdreammod.api.entity.builder.EntityBuilder}
 * 在注册实体时直接打标。所有标签关系集中存储，便于运行时快速查询。
 * <p>
 * 使用示例：
 * <pre>{@code
 * // 批量为灯影怪物添加标签
 * EntityTagRegistry.registerAll(EntityTag.LAMP_SHADOW_MONSTER,
 *     PDEntities.SHADOW_GOLEM.get(),
 *     PDEntities.SHADOW_GHOST.get(),
 *     PDEntities.SHADOW_HAND.get());
 *
 * // 通过 EntityResult 注册
 * EntityTagRegistry.register(result, EntityTag.SPELL_INVINCIBLE);
 *
 * // 运行时查询
 * if (EntityTagRegistry.hasTag(entity, EntityTag.LAMP_SHADOW_MONSTER)) { ... }
 * }</pre>
 *
 * @see EntityTag
 * @see com.pasterdream.pasterdreammod.api.entity.builder.EntityBuilder#tag(EntityTag...)
 */
public final class EntityTagRegistry {

    /** 实体类型 → 标签集合 的映射 */
    private static final Map<EntityType<?>, Set<EntityTag>> ENTITY_TAGS = new HashMap<>();

    /** 标签 → 实体类型集合 的反向映射 */
    private static final Map<EntityTag, Set<EntityType<?>>> TAG_ENTITIES = new HashMap<>();

    private EntityTagRegistry() {
        throw new UnsupportedOperationException("EntityTagRegistry 是纯静态注册表，不可实例化");
    }

    // ======================== 注册方法 ========================

    /**
     * 为单个实体类型注册一个或多个标签
     *
     * @param entityType 实体类型
     * @param tags       要绑定的标签
     */
    public static void register(EntityType<?> entityType, EntityTag... tags) {
        if (entityType == null || tags == null || tags.length == 0) {
            return;
        }
        Set<EntityTag> tagSet = ENTITY_TAGS.computeIfAbsent(entityType, k -> EnumSet.noneOf(EntityTag.class));
        for (EntityTag tag : tags) {
            if (tag == null) {
                continue;
            }
            tagSet.add(tag);
            TAG_ENTITIES.computeIfAbsent(tag, k -> new HashSet<>()).add(entityType);
            PDDebugLogger.apiDebug("[EntityTagRegistry] 为实体 [{}] 添加标签: {}",
                    EntityType.getKey(entityType), tag.getId());
        }
    }

    /**
     * 为实体注册结果注册一个或多个标签
     *
     * @param result 实体注册结果
     * @param tags   要绑定的标签
     */
    public static void register(EntityResult<?> result, EntityTag... tags) {
        if (result == null) {
            return;
        }
        register(result.entityType(), tags);
    }

    /**
     * 批量为多个实体类型注册同一个标签
     * <p>
     * 适合在主模块初始化时一次性为同系列实体（如灯影怪物）统一打标。
     *
     * @param tag   要绑定的标签
     * @param types 实体类型数组
     */
    public static void registerAll(EntityTag tag, EntityType<?>... types) {
        if (tag == null || types == null || types.length == 0) {
            return;
        }
        for (EntityType<?> type : types) {
            register(type, tag);
        }
    }

    /**
     * 批量为多个实体注册结果注册同一个标签
     *
     * @param tag     要绑定的标签
     * @param results 实体注册结果数组
     */
    public static void registerAll(EntityTag tag, EntityResult<?>... results) {
        if (tag == null || results == null || results.length == 0) {
            return;
        }
        for (EntityResult<?> result : results) {
            register(result, tag);
        }
    }

    // ======================== 查询方法 ========================

    /**
     * 判断实体是否拥有指定标签
     *
     * @param entity 实体实例
     * @param tag    标签
     * @return 如果实体类型绑定了该标签返回 {@code true}
     */
    public static boolean hasTag(Entity entity, EntityTag tag) {
        if (entity == null || tag == null) {
            return false;
        }
        return hasTag(entity.getType(), tag);
    }

    /**
     * 判断实体类型是否拥有指定标签
     *
     * @param entityType 实体类型
     * @param tag        标签
     * @return 如果该类型绑定了标签返回 {@code true}
     */
    public static boolean hasTag(EntityType<?> entityType, EntityTag tag) {
        if (entityType == null || tag == null) {
            return false;
        }
        Set<EntityTag> tags = ENTITY_TAGS.get(entityType);
        return tags != null && tags.contains(tag);
    }

    /**
     * 获取实体类型已绑定的所有标签
     *
     * @param entityType 实体类型
     * @return 标签集合（不可变），未注册返回空集合
     */
    public static Set<EntityTag> getTags(EntityType<?> entityType) {
        if (entityType == null) {
            return Collections.emptySet();
        }
        Set<EntityTag> tags = ENTITY_TAGS.get(entityType);
        return tags == null ? Collections.emptySet() : Collections.unmodifiableSet(tags);
    }

    /**
     * 获取实体实例已绑定的所有标签
     *
     * @param entity 实体实例
     * @return 标签集合（不可变），未注册返回空集合
     */
    public static Set<EntityTag> getTags(Entity entity) {
        if (entity == null) {
            return Collections.emptySet();
        }
        return getTags(entity.getType());
    }

    /**
     * 获取拥有指定标签的所有实体类型
     *
     * @param tag 标签
     * @return 实体类型集合（不可变），未注册返回空集合
     */
    public static Set<EntityType<?>> getEntities(EntityTag tag) {
        if (tag == null) {
            return Collections.emptySet();
        }
        Set<EntityType<?>> types = TAG_ENTITIES.get(tag);
        return types == null ? Collections.emptySet() : Collections.unmodifiableSet(types);
    }

    /**
     * 获取所有已注册标签的实体类型映射
     *
     * @return 实体类型 → 标签集合 的不可变视图
     */
    public static Map<EntityType<?>, Set<EntityTag>> getAllRegistrations() {
        Map<EntityType<?>, Set<EntityTag>> copy = new HashMap<>();
        for (Map.Entry<EntityType<?>, Set<EntityTag>> entry : ENTITY_TAGS.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));
        }
        return Collections.unmodifiableMap(copy);
    }

    // ======================== 内部方法 ========================

    /**
     * 清空所有标签注册数据
     * <p>
     * 主要用于测试场景，确保每次测试在干净的注册表状态下运行。
     */
    public static void resetForTesting() {
        ENTITY_TAGS.clear();
        TAG_ENTITIES.clear();
        PDDebugLogger.apiDebug("[EntityTagRegistry] 已清空实体标签注册表");
    }
}
