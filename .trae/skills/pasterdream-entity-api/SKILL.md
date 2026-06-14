---
name: "pasterdream-entity-api"
description: "PasterDream模组实体注册专用API，提供Facade+Builder模式一键注册自定义实体。在需要创建新实体、配置实体属性/AI/碰撞箱/追踪范围/生物技能、动画系统或注册渲染器时调用。"
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
  ├── createSpawnEggItem(registry, name, supplier) ← 刷怪蛋物品注册
  ├── setSpawnEggModelsOutputDir(path)             ← 刷怪蛋模型输出目录
  ├── cacheSpawnEgg(name, bg, hl)                  ← 缓存刷怪蛋颜色
  ├── getSpawnEggColors(name)      ← 查询刷怪蛋颜色
  ├── getEntityType(name)          ← 查询 EntityType
  ├── getEntityResult(name)        ← 查询 EntityResult
  └── getRegisteredEntities()      ← 所有已注册实体

EntityBuilder<T>                     ← Builder 构建器
  ├── category(MobCategory)          ← 实体分类（必要）
  ├── size(float, float)             ← 碰撞箱尺寸（必要）
  ├── entityClass(Class<T>)          ← 实体类（必要）
  ├── trackingRange(int)             ← 追踪范围（默认 64）
  ├── updateInterval(int)            ← 更新间隔（默认 3）
  ├── velocityUpdates(boolean)       ← 速度同步（默认 true）
  ├── attributes(Supplier<Builder>)  ← AI 属性（AttributeSupplier.Builder）
  ├── attributesBuilt(Supplier)      ← AI 属性（预构建）
  ├── spawnEgg(int, int)             ← 刷怪蛋颜色 [底色, 高光色]
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

## 刷怪蛋系统

Entity API 提供了一个完整的刷怪蛋解决方案，包括**颜色配置**、**自动物品注册**和**模型文件自动生成**。

### 1. 颜色配置（Builder 阶段）

在 `EntityBuilder` 的链式调用中通过 `.spawnEgg()` 配置颜色：

```java
EntityResult<ShadowGolemEntity> golem = EntityAPI.createEntity("shadow_golem")
    .category(MobCategory.MONSTER)
    .size(2.2f, 3.5f)
    .entityClass(ShadowGolemEntity.class)
    .attributes(ShadowGolemEntity::createAttributes)
    .spawnEgg(0x2C2C2C, 0x6B3FAF)    // 底色, 高光色
    .build();
```

颜色值以 16 进制 RGB 格式传入，Builder 在 `build()` 时自动缓存颜色。

### 2. 刷怪蛋物品注册（PDItems 阶段）

在 `PDItems.java` 中使用 `EntityAPI.createSpawnEggItem()` 统一注册刷怪蛋物品，无需手动指定颜色：

```java
// 所有刷怪蛋颜色由 PDEntities 中的 .spawnEgg() 统一管理
public static final DeferredItem<Item> SHADOW_GOLEM_SPAWN_EGG =
    EntityAPI.createSpawnEggItem(ITEMS, "shadow_golem", PDEntities.SHADOW_GOLEM);
```

**原理**：`createSpawnEggItem()` 会从 PDEntities 中 `.spawnEgg()` 缓存的颜色数组中自动读取，生成 `SpawnEggItem`。

### 3. 刷怪蛋模型自动生成

当 EntityBuilder 的 `.build()` 执行时，如果已配置模型输出目录，会自动在对应路径生成 `{name}_spawn_egg.json` 模型文件（内容固定为 `{"parent": "minecraft:item/template_spawn_egg"}`）。

**配置方式**：在 `PasterDreamMod.java` 构造函数中设置输出目录：

```java
public PasterDreamMod(IEventBus modEventBus, ModContainer modContainer) {
    // ... 其他注册器 ...

    // 配置刷怪蛋模型自动生成输出目录
    EntityAPI.setSpawnEggModelsOutputDir(
        Path.of("PasterDream", "src", "main", "resources", "assets",
                PasterDreamMod.MOD_ID, "models", "item"));

    // ... 后续初始化 ...
}
```

### 完整集成示例

```java
// ====== PDEntities.java ======
private static final EntityResult<ShadowGolemEntity> SHADOW_GOLEM_RESULT =
    EntityAPI.createEntity("shadow_golem")
        .category(MobCategory.MONSTER).size(2.2f, 3.5f)
        .entityClass(ShadowGolemEntity.class)
        .attributes(ShadowGolemEntity::createAttributes)
        .spawnEgg(0x191926, 0xA7A5B1)  // ← 颜色在此配置
        .build();

// 向后兼容常量
public static final Supplier<EntityType<ShadowGolemEntity>> SHADOW_GOLEM =
    SHADOW_GOLEM_RESULT.entityTypeSupplier();

// ====== PDItems.java ======
public static final DeferredItem<Item> SHADOW_GOLEM_SPAWN_EGG =
    EntityAPI.createSpawnEggItem(ITEMS, "shadow_golem", PDEntities.SHADOW_GOLEM);

// ====== PasterDreamMod.java ======
public PasterDreamMod(IEventBus modEventBus, ModContainer modContainer) {
    // ... 注册器 ...
    EntityAPI.setSpawnEggModelsOutputDir(
        Path.of("PasterDream", "src", "main", "resources", "assets",
                "pasterdream", "models", "item"));
    // ...
}
```

### 检查清单

| 步骤 | 操作 | 位置 |
|:----:|------|------|
| 1 | 在 `.spawnEgg()` 中配置颜色 | PDEntities.java |
| 2 | 用 `EntityAPI.createSpawnEggItem()` 注册刷怪蛋物品 | PDItems.java |
| 3 | 调用 `EntityAPI.setSpawnEggModelsOutputDir()` 配置输出目录 | PasterDreamMod.java |
| 4 | 确保实体有向后兼容常量（`public static final Supplier<EntityType<T>>`） | PDEntities.java |

> ⚠️ 若未配置 `.spawnEgg()` 但调用了 `createSpawnEggItem()`，会在运行时抛出 `IllegalStateException`，提示 "未配置生成蛋颜色"。

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

- [EntityAPI.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/entity/EntityAPI.java) — 门面类
- [EntityBuilder.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/entity/builder/EntityBuilder.java) — 构建器
- [EntityResult.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/entity/EntityResult.java) — 结果类
- [EntityAttributesGenerator.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/entity/gen/EntityAttributesGenerator.java) — 属性预设模板

## 动画系统

Entity API 提供了一套完整的 **procedure 动画播放系统**，用于播放由服务端触发的一次性动画（如技能释放、咆哮、受击等）。

### 架构

```
服务端                             客户端
  │                                 │
  ├─ setAnimation("roar") ──────►  ├─ 同步数据到达
  │   (更新 entityData.set)        │
  │                                ├─ ProcedureAnimationHandler.predicate()
  │                                │    ├─ 检测新动画 → 通过 GeckoLib 播放一次
  │                                │    ├─ 动画播放中 → 返回 CONTINUE
  │                                │    └─ 动画播完 → 自动重置为 "empty"
  │                                │
  │                                └─ movementPredicate()
  │                                     ├─ procedure 动画进行中 → STOP
  │                                     └─ procedure 为空 → 播放 idle/walk/death
```

### 正确实现步骤

#### 1. 实体类中加入 ProcedureAnimationHandler

```java
// 在实体类字段中：
/** 客户端 procedure 动画处理器 */
private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();
```

#### 2. 实现 procedurePredicate 回调

```java
private PlayState procedurePredicate(AnimationState<MyEntity> state) {
    return procAnim.predicate(state,
            level().isClientSide(),
            this::getSyncedAnimation,
            () -> setAnimation("empty"));
}
```

参数说明：
- `state` — GeckoLib 动画状态（传入原生 AnimationState）
- `level().isClientSide()` — 是否在客户端侧（服务端不播动画）
- `this::getSyncedAnimation` — 同步动画数据的 getter
- `() -> setAnimation("empty")` — 动画播完后重置的回调

#### 3. 实现 movementPredicate（正确检测 procedure）

```java
private PlayState movementPredicate(AnimationState<MyEntity> state) {
    // 必须使用 getSyncedAnimation() 而非 this.animationprocedure
    if (this.getSyncedAnimation().equals("empty")) {
        if ((state.isMoving() || !(state.getLimbSwingAmount() > -0.15F && state.getLimbSwingAmount() < 0.15F))) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
        }
        if (this.isDeadOrDying()) {
            return state.setAndContinue(RawAnimation.begin().thenPlay("death"));
        }
        return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
    }
    return PlayState.STOP;
}
```

**关键规则**：`movementPredicate` 必须检查 `getSyncedAnimation()`（同步数据），**不能**检查 `this.animationprocedure`（本地字段）。

#### 4. 注册控制器

```java
@Override
public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
    controllers.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
}
```

注意：procedure 控制器的 transition length 建议与 movement 一致（通常 4 tick）。

#### 5. 服务端触发动画

```java
// 在 hurt()、技能逻辑、或其他需要触发动画的地方：
this.setAnimation("roar");  // 服务端同步数据 → 客户端自动播放
```

### ❌ 常见错误

| 错误模式 | 后果 |
|---------|------|
| `movementPredicate` 使用 `this.animationprocedure` 判断 | procedure 动画被 movementPredicate 覆盖，永远播不出 |
| `procedurePredicate` 不检查 `level().isClientSide()` | 服务端和客户端争抢控制，动画行为不可预测 |
| `procedurePredicate` 没有 `currentlyPlaying` 追踪 | 每帧重复触发动画，导致卡顿或循环 |
| 直接在 procedurePredicate 里写完整逻辑 | 每个实体重复同样的代码，容易出错 |

### ProcedureAnimationHandler API

| 方法 | 说明 |
|------|------|
| `predicate(state, isClientSide, syncedAnimSupplier, setEmptyAnim)` | 标准 procedure 动画回调 |
| `reset()` | 重置处理器状态（实体死亡时调用） |
| `getCurrentlyPlaying()` | 获取当前正在播放的动画名称 |

### 与 EntitySkillManager 集成

`EntitySkillManager` 是更高级的技能管理系统，内置了动画同步机制：

```java
// 实体类中
private final EntitySkillManager skillManager = new EntitySkillManager(this);

// 构造方法中注册技能
public MyEntity(EntityType<? extends Monster> type, Level level) {
    super(type, level);
    skillManager.registerSkill(EntitySkill.builder("roar")
        .animationName("roar")
        .damage(12.0f).range(5.0f).cooldownTicks(200)
        .particleName("explosion")
        .soundId("pasterdream:terrorbeak_roar")
        .build());
}

// baseTick 中更新
@Override
public void baseTick() {
    super.baseTick();
    skillManager.tick();
}

// 触发技能
skillManager.tryTriggerSkill("roar", target);
```

`EntitySkillManager` 会自动处理：
1. 冷却计时
2. 动画同步（服务端 → 客户端）
3. 技能音效播放
4. 技能粒子效果
5. 技能伤害范围判定
6. 动画播完后重置

### 引用文件

- [ProcedureAnimationHandler.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/entity/anim/ProcedureAnimationHandler.java) — 动画处理器
- [EntitySkillManager.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/api/entity/skill/EntitySkillManager.java) — 技能管理器
- [EntitySkill.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/api/entity/skill/EntitySkill.java) — 技能数据记录
- [EntitySkillBuilder.java](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/src/main/java/com/pasterdream/pasterdreammod/api/entity/skill/EntitySkillBuilder.java) — 技能构建器