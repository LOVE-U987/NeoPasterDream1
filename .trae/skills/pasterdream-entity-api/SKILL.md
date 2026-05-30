---
name: "pasterdream-entity-api"
description: "PasterDream模组实体注册专用API，提供Facade+Builder模式一键注册自定义实体。在需要创建新实体、配置实体属性/AI/碰撞箱/追踪范围或注册渲染器时调用。"
---

# PasterDream Entity API

本 Skill 提供 PasterDream 模组实体注册专用 API 的使用指南，采用 **Facade + Builder** 模式（与 BlockAPI / DimensionAPI 风格一致），通过链式调用即可完成实体的注册、属性配置、渲染器注册和生成蛋颜色管理。

## 适用场景

- 创建新的自定义实体（Entity / LivingEntity 子类）
- 配置实体碰撞箱尺寸、追踪范围、更新频率
- 设置实体 AI 属性（攻击力、生命值、移动速度等）
- 注册实体渲染器（客户端）
- 配置生成蛋颜色（底色 + 高光色）
- 批量查询已注册的实体类型和属性

## 快速开始

```java
// ====== 1. 在 PDEntities.java 中注册实体 ======
EntityResult<ShadowGolemEntity> shadowGolem = EntityAPI.createEntity("shadow_golem")
    .category(MobCategory.MONSTER)              // 实体分类
    .size(2.2f, 3.5f)                           // 碰撞箱尺寸
    .trackingRange(64)                          // 追踪范围
    .updateInterval(3)                          // 更新间隔
    .velocityUpdates(true)                      // 启用速度同步
    .entityClass(ShadowGolemEntity.class)       // 实体类
    .attributes(ShadowGolemEntity::createAttributes)  // AI 属性
    .spawnEgg(0x333333, 0xFF4444)               // 生成蛋颜色
    .build();

// ====== 2. 在 ClientSetup.java 中注册渲染器 ======
@SubscribeEvent
public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    EntityAPI.registerRenderer(event, shadowGolem, ShadowGolemRenderer::new);
}

// ====== 3. 在 PDEntityEvents.java 中注册属性 ======
@SubscribeEvent
public static void registerAttributes(EntityAttributeCreationEvent event) {
    EntityAPI.registerAttributes(event, shadowGolem);
}

// ====== 4. 在代码中生成实体 ======
EntityType<ShadowGolemEntity> type = shadowGolem.entityType();
ShadowGolemEntity golem = type.create(level);
golem.setPos(x, y, z);
level.addFreshEntity(golem);
```

## 前置条件

在 `PasterDreamMod` 构造函数中注册 EntityAPI 的 REGISTRY：

```java
public PasterDreamMod(IEventBus modEventBus) {
    // ... 其他注册器 ...
    EntityAPI.REGISTRY.register(modEventBus);
}
```

## API 架构

```
EntityAPI                            ← Facade 门面
  ├── createEntity(name)             ← 工厂方法 → EntityBuilder
  ├── registerRenderer(event, result, provider)    ← 渲染器注册
  ├── registerRenderer(event, name, provider)      ← 按名称注册渲染器
  ├── registerAttributes(event, result)            ← 属性注册（缓存）
  ├── registerAttributes(event, result, supplier)  ← 属性注册（显式）
  ├── registerAttributes(event, name)              ← 按名称注册属性
  ├── getEntityType(name)            ← 查询 EntityType
  ├── getEntityResult(name)          ← 查询 EntityResult
  ├── getRegisteredEntities()        ← 所有已注册实体
  └── getSpawnEggColors(name)        ← 查询生成蛋颜色

EntityBuilder<T>                     ← Builder 构建器
  ├── category(MobCategory)          ← 实体分类（必要）
  ├── size(float, float)             ← 碰撞箱尺寸（必要）
  ├── entityClass(Class<T>)          ← 实体类（必要）
  ├── trackingRange(int)             ← 追踪范围（默认 64）
  ├── updateInterval(int)            ← 更新间隔（默认 3）
  ├── velocityUpdates(boolean)       ← 速度同步（默认 true）
  ├── attributes(Supplier<Builder>)  ← AI 属性（AttributeSupplier.Builder）
  ├── attributesBuilt(Supplier)      ← AI 属性（预构建）
  ├── spawnEgg(int, int)             ← 生成蛋颜色 [底色, 高光色]
  └── build()                        ← 注册 → EntityResult<T>

EntityResult<T>                      ← Record 结果
  ├── name()                         → String（实体注册名）
  ├── entityTypeSupplier()           → Supplier<EntityType<T>>
  ├── entityClass()                  → Class<T>
  ├── entityType()                   → EntityType<T>（便捷获取）
  └── deferredHolder()               → DeferredHolder
```

## Builder 配置参考

| 方法 | 参数 | 说明 | 必需 |
|------|------|------|:----:|
| `category(MobCategory)` | 实体分类 | 决定生物容量和生成行为 | ✅ |
| `size(float, float)` | 宽度, 高度 | 碰撞箱尺寸 | ✅ |
| `entityClass(Class)` | 实体 Class | 实体 Java 类（需有 `(EntityType, Level)` 构造） | ✅ |
| `trackingRange(int)` | 格数 | 客户端同步距离（默认 64） | ❌ |
| `updateInterval(int)` | tick 数 | 位置同步频率（默认 3） | ❌ |
| `velocityUpdates(boolean)` | bool | 是否接收速度更新（默认 true） | ❌ |
| `attributes(Supplier)` | AttributeSupplier.Builder | AI 属性配置 | ❌ |
| `attributesBuilt(Supplier)` | AttributeSupplier | 预构建的属性 | ❌ |
| `spawnEgg(int, int)` | 底色, 高光色 | 生成蛋颜色（16 进制） | ❌ |

### MobCategory 参考

| 分类 | 说明 |
|------|------|
| `MobCategory.MONSTER` | 敌对生物（容量 70） |
| `MobCategory.CREATURE` | 友好动物（容量 10） |
| `MobCategory.AMBIENT` | 环境生物（如蝙蝠，容量 15） |
| `MobCategory.WATER_CREATURE` | 水生生物（容量 5） |
| `MobCategory.WATER_AMBIENT` | 水下环境生物（如鱼，容量 20） |
| `MobCategory.MISC` | 其他（如掉落物、箭矢） |

## EntityAttributesGenerator 预设模板

API 提供了 [`EntityAttributesGenerator`](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/api/entity/gen/EntityAttributesGenerator.java) 工具类，包含多种预设属性模板，可直接通过方法引用传入 Builder：

| 方法 | 适用 | 预设值 |
|------|------|--------|
| `createMonsterAttributes()` | 怪物 | 攻击 3.0, 盔甲 2.0, 追踪 32 |
| `createCreatureAttributes()` | 动物 | 生命 10.0, 速度 0.2, 追踪 16 |
| `createFlyingAttributes()` | 飞行生物 | 生命 10.0, 速度 0.2, 飞行 0.4, 追踪 24 |
| `createWaterCreatureAttributes()` | 水生生物 | 生命 15.0, 速度 0.3, 追踪 16 |

## 完整示例

### 怪物 — 暗影傀儡

```java
// 注册
EntityResult<ShadowGolemEntity> golem = EntityAPI.createEntity("shadow_golem")
    .category(MobCategory.MONSTER)
    .size(2.2f, 3.5f)
    .trackingRange(64)
    .entityClass(ShadowGolemEntity.class)
    .attributes(() -> Mob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 80.0)
        .add(Attributes.ATTACK_DAMAGE, 12.0)
        .add(Attributes.ARMOR, 8.0)
        .add(Attributes.MOVEMENT_SPEED, 0.25)
        .add(Attributes.FOLLOW_RANGE, 48))
    .spawnEgg(0x2C2C2C, 0x6B3FAF)
    .build();

// 渲染器
EntityAPI.registerRenderer(event, golem, ShadowGolemRenderer::new);

// 属性
EntityAPI.registerAttributes(event, golem);
```

### 动物 — 森林精灵

```java
EntityResult<ForestSpiritEntity> spirit = EntityAPI.createEntity("forest_spirit")
    .category(MobCategory.CREATURE)
    .size(0.6f, 1.8f)
    .entityClass(ForestSpiritEntity.class)
    .attributes(EntityAttributesGenerator::createCreatureAttributes)
    .spawnEgg(0x4CAF50, 0x81C784)
    .build();
```

### 飞行生物 — 梦魇蝙蝠

```java
EntityResult<NightmareBatEntity> bat = EntityAPI.createEntity("nightmare_bat")
    .category(MobCategory.AMBIENT)
    .size(0.8f, 0.8f)
    .entityClass(NightmareBatEntity.class)
    .attributes(EntityAttributesGenerator::createFlyingAttributes)
    .updateInterval(1)  // 飞行生物需要更频繁同步
    .spawnEgg(0x1A1A2E, 0xE94560)
    .build();
```

### 按名称注册（便捷方式）

```java
// 在任意位置，只要知道实体名称就可以查询和注册
@SubscribeEvent
public static void registerAttributes(EntityAttributeCreationEvent event) {
    // 自动查找已缓存的 shadow_golem 实体和属性
    EntityAPI.registerAttributes(event, "shadow_golem");
}

@SubscribeEvent
public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    EntityAPI.registerRenderer(event, "shadow_golem", ShadowGolemRenderer::new);
}
```

## 生成蛋颜色配置

生成蛋颜色以 16 进制格式传入，Builder 仅负责缓存颜色值，实际的 `SpawnEggItem` 注册需要额外处理：

```java
// 1. 在 Builder 中配置颜色
    .spawnEgg(0x2C2C2C, 0x6B3FAF)

// 2. 在物品注册中查询颜色
int[] colors = EntityAPI.getSpawnEggColors("shadow_golem");
if (colors != null) {
    int bgColor = colors[0];
    int hlColor = colors[1];
    // 创建 SpawnEggItem...
}
```

## 实体类构造要求

实体类必须包含 `(EntityType, Level)` 构造方法：

```java
public class ShadowGolemEntity extends Monster {
    public ShadowGolemEntity(EntityType<? extends ShadowGolemEntity> type, Level level) {
        super(type, level);
    }
}
```

## 引用文件

- [EntityAPI.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/api/entity/EntityAPI.java) — 门面类
- [EntityBuilder.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/api/entity/builder/EntityBuilder.java) — 构建器
- [EntityResult.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/api/entity/EntityResult.java) — 结果类
- [EntityAttributesGenerator.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/api/entity/gen/EntityAttributesGenerator.java) — 属性预设模板