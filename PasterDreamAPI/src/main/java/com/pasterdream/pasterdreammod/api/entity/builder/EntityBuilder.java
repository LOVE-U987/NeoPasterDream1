package com.pasterdream.pasterdreammod.api.entity.builder;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.entity.EntityAPI;
import com.pasterdream.pasterdreammod.api.entity.EntityResult;
import com.pasterdream.pasterdreammod.api.entity.skill.EntitySkill;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 实体配置构建器 —— 采用 Builder 模式链式配置实体类型
 * <p>
 * 解决 {@code PDEntities.java} 中手动构建 {@link EntityType.Builder} 的繁琐问题。
 * 通过链式调用即可完成实体类型注册、属性注册、渲染器关联等完整流程。
 * <p>
 * 使用示例：
 * <pre>{@code
 * EntityResult<ShadowGolemEntity> shadowGolem = EntityAPI.createEntity("shadow_golem")
 *     .category(MobCategory.MONSTER)
 *     .size(2.2f, 3.5f)
 *     .trackingRange(64)
 *     .updateInterval(3)
 *     .velocityUpdates(true)
 *     .entityClass(ShadowGolemEntity.class)
 *     .attributes(ShadowGolemEntity::createAttributes)
 *     .build();
 * }</pre>
 *
 * @param <T> 实体类型参数
 * @see com.pasterdream.pasterdreammod.api.entity.EntityAPI
 */
public class EntityBuilder<T extends Entity> {

    private final DeferredRegister<EntityType<?>> registry;
    private final String name;
    private final String modId;

    // 必要参数
    private MobCategory category;
    private float width;
    private float height;
    private Class<T> entityClass;

    // 可选参数（附默认值）
    private int trackingRange = 64;
    private int updateInterval = 3;
    private boolean velocityUpdates = true;

    @Nullable
    private Supplier<AttributeSupplier> attributesSupplier;

    @Nullable
    private Integer spawnEggBackgroundColor;

    @Nullable
    private Integer spawnEggHighlightColor;

    /** 实体技能列表 */
    private final List<EntitySkill> skills = new ArrayList<>();

    /**
     * 构造实体构建器
     *
     * @param registry 实体类型的 DeferredRegister
     * @param modId    模组 ID
     * @param name     实体注册名称（snake_case 格式）
     */
    public EntityBuilder(DeferredRegister<EntityType<?>> registry, String modId, String name) {
        this.registry = registry;
        this.modId = modId;
        this.name = name;
        PasterDreamAPI.LOGGER.debug("[EntityBuilder] 创建实体构建器: modId={}, name={}", modId, name);
    }

    // ======================== 实体分类 ========================

    /**
     * 设置实体分类
     * <p>
     * 决定实体属于怪物（MONSTER）、动物（CREATURE）、水生生物（WATER_CREATURE）
     * 等分组，影响生物容量计算和生成行为。
     *
     * @param category 实体分类
     * @return 当前构建器实例
     */
    public EntityBuilder<T> category(MobCategory category) {
        this.category = category;
        PasterDreamAPI.LOGGER.debug("[EntityBuilder] {} → category={}", name, category.getName());
        return this;
    }

    /**
     * 设置碰撞箱尺寸
     *
     * @param width  宽度（水平方向）
     * @param height 高度（垂直方向）
     * @return 当前构建器实例
     */
    public EntityBuilder<T> size(float width, float height) {
        this.width = width;
        this.height = height;
        PasterDreamAPI.LOGGER.debug("[EntityBuilder] {} → size={}x{}", name, width, height);
        return this;
    }

    // ======================== 追踪与更新 ========================

    /**
     * 设置追踪范围（单位：格）
     * <p>
     * 决定客户端与服务端同步实体状态的距离范围。
     * 默认值 {@code 64}。
     *
     * @param trackingRange 追踪范围，单位格
     * @return 当前构建器实例
     */
    public EntityBuilder<T> trackingRange(int trackingRange) {
        this.trackingRange = trackingRange;
        PasterDreamAPI.LOGGER.debug("[EntityBuilder] {} → trackingRange={}", name, trackingRange);
        return this;
    }

    /**
     * 设置实体更新间隔（单位：tick）
     * <p>
     * 控制实体位置同步到客户端的频率。
     * 默认值 {@code 3}（每 3 tick 同步一次）。
     *
     * @param updateInterval 更新间隔 tick 数
     * @return 当前构建器实例
     */
    public EntityBuilder<T> updateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
        PasterDreamAPI.LOGGER.debug("[EntityBuilder] {} → updateInterval={}", name, updateInterval);
        return this;
    }

    /**
     * 设置是否接收速度更新
     * <p>
     * 如果实体不需要客户端运动物理模拟（如炮台类静态怪物），可设为 false 以减少网络流量。
     * 默认值 {@code true}。
     *
     * @param velocityUpdates 是否接收速度更新
     * @return 当前构建器实例
     */
    public EntityBuilder<T> velocityUpdates(boolean velocityUpdates) {
        this.velocityUpdates = velocityUpdates;
        PasterDreamAPI.LOGGER.debug("[EntityBuilder] {} → velocityUpdates={}", name, velocityUpdates);
        return this;
    }

    // ======================== 实体类 ========================

    /**
     * 设置实体类
     * <p>
     * 指定实体的 Java 类，需要有一个 {@code (EntityType, Level)} 构造方法。
     *
     * @param entityClass 实体类对象
     * @param <X>         实体类型
     * @return 当前构建器实例（类型参数已更新）
     */
    @SuppressWarnings("unchecked")
    public <X extends Entity> EntityBuilder<X> entityClass(Class<X> entityClass) {
        this.entityClass = (Class<T>) entityClass;
        PasterDreamAPI.LOGGER.debug("[EntityBuilder] {} → entityClass={}", name, entityClass.getSimpleName());
        return (EntityBuilder<X>) this;
    }

    // ======================== 属性 ========================

    /**
     * 设置实体属性生成器
     * <p>
     * 对应 {@link EntityAttributeCreationEvent} 中注册的 {@link AttributeSupplier}。
     * 可使用 {@link com.pasterdream.pasterdreammod.api.entity.gen.EntityAttributesGenerator}
     * 提供的预设模板。
     *
     * @param attributesSupplier 属性构造器 Supplier（通常在 build 后调用 .build()）
     * @return 当前构建器实例
     */
    public EntityBuilder<T> attributes(Supplier<AttributeSupplier.Builder> attributesSupplier) {
        this.attributesSupplier = () -> attributesSupplier.get().build();
        PasterDreamAPI.LOGGER.debug("[EntityBuilder] {} → attributes=已配置（Builder 模式）", name);
        return this;
    }

    /**
     * 设置实体属性生成器（直接接收已构建的 {@link AttributeSupplier}）
     *
     * @param attributesSupplier 已构建的属性 Supplier
     * @return 当前构建器实例
     */
    public EntityBuilder<T> attributesBuilt(Supplier<AttributeSupplier> attributesSupplier) {
        this.attributesSupplier = attributesSupplier;
        PasterDreamAPI.LOGGER.debug("[EntityBuilder] {} → attributes=已配置（直接 Supplier）", name);
        return this;
    }

    // ======================== 生成蛋 ========================

    /**
     * 配置生成蛋
     * <p>
     * 仅存储生成蛋的颜色配置，实际的 {@code SpawnEggItem} 注册需要额外处理。
     * 可通过 {@link EntityAPI#getSpawnEggColors(String)} 获取已缓存的颜色配置。
     *
     * @param backgroundColor  生成蛋底色（16 进制颜色值）
     * @param highlightColor   生成蛋高光色（16 进制颜色值）
     * @return 当前构建器实例
     */
    public EntityBuilder<T> spawnEgg(int backgroundColor, int highlightColor) {
        this.spawnEggBackgroundColor = backgroundColor;
        this.spawnEggHighlightColor = highlightColor;
        PasterDreamAPI.LOGGER.debug("[EntityBuilder] {} → spawnEgg=#{}/#{}",
                name, Integer.toHexString(backgroundColor), Integer.toHexString(highlightColor));
        return this;
    }
    
    // ======================== 技能 ========================

    /**
     * 为实体注册一个技能
     * <p>
     * 技能包含动画、伤害、范围、冷却、粒子效果和音效等配置。
     * 使用 {@link EntitySkill#builder(String)} 创建技能定义。
     * <p>
     * 使用示例：
     * <pre>{@code
     * EntityAPI.createEntity("shadow_golem")
     *     .category(MobCategory.MONSTER)
     *     .size(2.2f, 3.5f)
     *     .entityClass(ShadowGolemEntity.class)
     *     .attributes(ShadowGolemEntity::createAttributes)
     *     .skill(EntitySkill.builder("dash_attack")
     *         .animationName("dash")
     *         .damage(10.0f).range(5.0f).cooldownTicks(100)
     *         .particle("sparkle")
     *         .sound("pasterdream:dash")
     *         .build())
     *     .build();
     * }</pre>
     *
     * @param skill 技能定义
     * @return 当前构建器实例
     */
    public EntityBuilder<T> skill(EntitySkill skill) {
        this.skills.add(skill);
        PasterDreamAPI.LOGGER.debug("[EntityBuilder] {} → 添加技能: {} | skills.count={}", name, skill.name(), this.skills.size());
        return this;
    }

    /**
     * 为实体批量注册多个技能
     *
     * @param skills 技能定义数组
     * @return 当前构建器实例
     */
    public EntityBuilder<T> skills(EntitySkill... skills) {
        for (EntitySkill skill : skills) {
            this.skills.add(skill);
        }
        PasterDreamAPI.LOGGER.debug("[EntityBuilder] {} → 批量添加技能: {} 个 | skills.count={}", name, skills.length, this.skills.size());
        return this;
    }

    // ======================== 构建 ========================

    /**
     * 执行注册，完成实体类型注册和结果缓存
     * <p>
     * 完成以下工作：
     * <ol>
     *   <li>使用 {@link EntityType.Builder} 构建实体类型</li>
     *   <li>通过 DeferredRegister 注册到注册表</li>
     *   <li>缓存属性 Supplier（如果配置）到 {@link EntityAPI}</li>
     *   <li>缓存生成蛋颜色（如果配置）到 {@link EntityAPI}</li>
     *   <li>返回 {@link EntityResult} 包含实体类型引用和类信息</li>
     * </ol>
     *
     * @return {@link EntityResult} 包含实体相关的所有引用
     * @throws IllegalStateException 如果缺少必要参数
     */
    public EntityResult<T> build() {
        PasterDreamAPI.LOGGER.info("[EntityBuilder] ===== 开始构建实体: {} =====", name);
        validate();

        PasterDreamAPI.LOGGER.debug("[EntityBuilder] 构建 EntityType.Builder: category={}, size={}x{}, tracking={}, interval={}, velocity={}",
                category.getName(), width, height, trackingRange, updateInterval, velocityUpdates);
        EntityType.Builder<T> typeBuilder = EntityType.Builder.<T>of(
                this::createEntity, category
        ).sized(width, height)
                .setTrackingRange(trackingRange)
                .setUpdateInterval(updateInterval)
                .setShouldReceiveVelocityUpdates(velocityUpdates);

        String descriptionId = "entity." + modId + "." + name;
        PasterDreamAPI.LOGGER.debug("[EntityBuilder] 注册实体类型: name={}, descriptionId={}", name, descriptionId);

        DeferredHolder<EntityType<?>, EntityType<T>> holder = registry.register(
                name, () -> typeBuilder.build(descriptionId));

        EntityResult<T> result = new EntityResult<>(name, holder, entityClass);
        PasterDreamAPI.LOGGER.debug("[EntityBuilder] 创建 EntityResult: name={}, holder={}, entityClass={}",
                name, holder, entityClass.getSimpleName());

        EntityAPI.cacheEntity(result);

        if (attributesSupplier != null) {
            PasterDreamAPI.LOGGER.debug("[EntityBuilder] 缓存实体属性: {}", name);
            EntityAPI.cacheAttributes(name, attributesSupplier);
        } else {
            PasterDreamAPI.LOGGER.debug("[EntityBuilder] 未配置属性，跳过属性缓存: {}", name);
        }

        if (spawnEggBackgroundColor != null && spawnEggHighlightColor != null) {
            PasterDreamAPI.LOGGER.debug("[EntityBuilder] 缓存生成蛋颜色: {}", name);
            EntityAPI.cacheSpawnEgg(name, spawnEggBackgroundColor, spawnEggHighlightColor);
        } else {
            PasterDreamAPI.LOGGER.debug("[EntityBuilder] 未配置生成蛋颜色，跳过: {}", name);
        }

        // 缓存技能
        if (!skills.isEmpty()) {
            PasterDreamAPI.LOGGER.debug("[EntityBuilder] 缓存实体技能: {} 个技能", skills.size());
            EntityAPI.cacheSkills(name, skills);
        } else {
            PasterDreamAPI.LOGGER.debug("[EntityBuilder] 未配置技能，跳过技能缓存: {}", name);
        }

        PasterDreamAPI.LOGGER.info("[EntityBuilder] ✅ 已注册实体: {} (尺寸: {}x{}, 分类: {}, 追踪范围: {})",
                name, width, height, category.getName(), trackingRange);

        return result;
    }

    /**
     * 验证必要参数是否已配置
     *
     * @throws IllegalStateException 如果缺少必要参数
     */
    private void validate() {
        if (category == null) {
            PasterDreamAPI.LOGGER.error("[EntityBuilder] ❌ 验证失败: {} → category 未设置", name);
            throw new IllegalStateException("EntityBuilder: 缺少实体分类（category），请调用 .category() 设置");
        }
        if (width <= 0 || height <= 0) {
            PasterDreamAPI.LOGGER.error("[EntityBuilder] ❌ 验证失败: {} → size 未设置 (width={}, height={})", name, width, height);
            throw new IllegalStateException("EntityBuilder: 缺少碰撞箱尺寸（size），请调用 .size() 设置");
        }
        if (entityClass == null) {
            PasterDreamAPI.LOGGER.error("[EntityBuilder] ❌ 验证失败: {} → entityClass 未设置", name);
            throw new IllegalStateException("EntityBuilder: 缺少实体类（entityClass），请调用 .entityClass() 设置");
        }
        PasterDreamAPI.LOGGER.debug("[EntityBuilder] ✅ 验证通过: {}", name);
    }

    /**
     * 通过反射创建实体实例
     *
     * @param type  实体类型
     * @param level 世界实例
     * @return 新建的实体实例
     */
    private T createEntity(EntityType<T> type, Level level) {
        try {
            return entityClass.getConstructor(EntityType.class, Level.class).newInstance(type, level);
        } catch (Exception e) {
            PasterDreamAPI.LOGGER.error("[EntityBuilder] ❌ 无法创建实体实例 [{}]: {}", name, e.getMessage(), e);
            throw new RuntimeException("EntityBuilder: 无法创建实体实例 [" + name + "]", e);
        }
    }
}