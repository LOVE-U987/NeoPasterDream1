---
name: "pasterdream-entity-api"
description: "PasterDream模组实体注册专用API，提供Facade+Builder模式一键注册自定义实体。在需要创建新实体、配置实体属性/AI/碰撞箱/追踪范围/生物技能/标签、动画系统或注册渲染器时调用。"
---

# PasterDream Entity API

本 Skill 提供 PasterDream 模组实体注册专用 API 的使用指南，采用 **Facade + Builder** 模式（与 BlockAPI / DimensionAPI 风格一致），通过链式调用即可完成实体的注册、属性配置、技能/标签绑定和生成蛋颜色管理。

## 适用场景

- 创建新的自定义实体（Entity / LivingEntity 子类）
- 配置实体碰撞箱尺寸、追踪范围、更新频率
- 设置实体 AI 属性（攻击力、生命值、移动速度等）
- 为实体绑定技能（动画/伤害/范围/冷却/粒子/音效）
- 为实体绑定内置标签（友伤豁免、法术无敌等）
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
    .skill(EntitySkill.builder("roar")
        .animationName("roar").damage(12.0f).range(5.0f).cooldownTicks(200)
        .particle("explosion").sound("pasterdream:terrorbeak_roar").build())
    .spawnEgg(0x333333, 0xFF4444)               // 生成蛋颜色
    .build();

// ====== 2. 在 ClientSetup.java 中注册渲染器 ======
@SubscribeEvent
public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(shadowGolem.entityType(), ShadowGolemRenderer::new);
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

在 `PasterDreamMod` 构造函数中注册 EntityAPI 的 REGISTRY（或直接调用统一入口）：

```java
// 方式一：统一注册（推荐，包含所有 API）
PasterDreamAPI.registerAll(modEventBus);

// 方式二：单独注册
EntityAPI.REGISTRY.register(modEventBus);
```

## API 架构

```
EntityAPI                            ← Facade 门面
  ├── createEntity(name)             ← 工厂方法 → EntityBuilder
  ├── registerAttributes(event, result)                    ← 属性注册（缓存）
  ├── registerAttributes(event, result, AttributeSupplier) ← 属性注册（显式）
  ├── registerAttributes(event, name)                      ← 按名称注册属性
  ├── createSpawnEggItem(registry, name, entityTypeSupplier) ← 刷怪蛋物品注册
  ├── setSpawnEggModelsOutputDir(path) ← 刷怪蛋模型输出目录
  ├── cacheSpawnEgg(name, bg, hl)     ← 缓存刷怪蛋颜色（Builder 内部调用）
  ├── getSpawnEggColors(name)         ← 查询刷怪蛋颜色 → Optional<int[]>
  ├── getEntityType(name)             ← 查询 EntityType → Optional
  ├── getEntityResult(name)           ← 查询 EntityResult → Optional
  ├── getEntitySkills(name) / getEntitySkill(name, skillName) / hasEntitySkill(...)
  └── getRegisteredEntities()         ← 所有已注册实体（不可变视图）

EntityBuilder<T>                     ← Builder 构建器
  ├── category(MobCategory)          ← 实体分类（必要）
  ├── size(float, float)             ← 碰撞箱尺寸（必要）
  ├── entityClass(Class<T>)          ← 实体类（必要，返回类型参数更新）
  ├── trackingRange(int)             ← 追踪范围（默认 64）
  ├── updateInterval(int)            ← 更新间隔（默认 3）
  ├── velocityUpdates(boolean)       ← 速度同步（默认 true）
  ├── attributes(Supplier<AttributeSupplier.Builder>)  ← AI 属性（Builder 模式）
  ├── attributesBuilt(Supplier<AttributeSupplier>)     ← AI 属性（预构建）
  ├── skill(EntitySkill) / skills(EntitySkill...)      ← 绑定技能
  ├── tag(EntityTag...) / tags(EntityTag...)           ← 绑定内置标签
  ├── spawnEgg(int, int)             ← 刷怪蛋颜色 [底色, 高光色]
  └── build()                        ← 注册 → EntityResult<T>

EntityResult<T>                      ← Record 结果
  ├── name()                         → String（实体注册名）
  ├── entityTypeSupplier()           → Supplier<EntityType<T>>
  ├── entityClass()                  → Class<T>
  └── entityType()                   → EntityType<T>（便捷获取）
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
| `attributes(Supplier<Builder>)` | AttributeSupplier.Builder | AI 属性（自动 `.build()`） | ❌ |
| `attributesBuilt(Supplier<AttributeSupplier>)` | AttributeSupplier | 预构建的属性 | ❌ |
| `skill(EntitySkill)` | 技能 | 绑定单个技能 | ❌ |
| `skills(EntitySkill...)` | 技能数组 | 批量绑定技能 | ❌ |
| `tag(EntityTag...)` | 标签 | 绑定内置标签 | ❌ |
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

## 实体属性（Attributes）

### 属性预设模板

`EntityAttributesGenerator` 提供常用属性模板，位于 `PasterDreamAPI/src/test/` 目录（测试辅助类，需自行复制或直接手写属性）：

| 方法 | 适用 | 预设值 |
|------|------|--------|
| `createMonsterAttributes()` | 怪物 | 攻击 3.0, 盔甲 2.0, 追踪 32 |
| `createCreatureAttributes()` | 动物 | 生命 10.0, 速度 0.2, 追踪 16 |
| `createFlyingAttributes()` | 飞行生物 | 生命 10.0, 速度 0.2, 飞行 0.4, 追踪 24 |
| `createWaterCreatureAttributes()` | 水生生物 | 生命 15.0, 速度 0.3, 追踪 16 |

```java
// 直接手写（推荐）
EntityResult<ShadowGolemEntity> golem = EntityAPI.createEntity("shadow_golem")
    .category(MobCategory.MONSTER)
    .size(2.2f, 3.5f)
    .entityClass(ShadowGolemEntity.class)
    .attributes(() -> Mob.createMobAttributes()
        .add(Attributes.MAX_HEALTH, 80.0)
        .add(Attributes.ATTACK_DAMAGE, 12.0)
        .add(Attributes.ARMOR, 8.0)
        .add(Attributes.MOVEMENT_SPEED, 0.25)
        .add(Attributes.FOLLOW_RANGE, 48))
    .build();
```

### 属性注册（EntityAttributeCreationEvent）

```java
@SubscribeEvent
public static void registerAttributes(EntityAttributeCreationEvent event) {
    // 方式一：使用 Builder 缓存的属性
    EntityAPI.registerAttributes(event, shadowGolem);
    // 方式二：显式指定
    EntityAPI.registerAttributes(event, shadowGolem, someAttributeSupplier);
    // 方式三：按名称
    EntityAPI.registerAttributes(event, "shadow_golem");
}
```

> ⚠️ `registerAttributes(event, result, supplier)` 的第三参数类型是 **`AttributeSupplier`**（不是 Supplier）。未配置 `.attributes()` 就调用注册会抛 `IllegalStateException`。

## 技能系统（EntitySkill / EntitySkillManager）

实体技能 = 动画 + 伤害 + 范围 + 冷却 + 粒子 + 音效。通过 `EntityBuilder.skill()` 绑定，运行时用 `EntitySkillManager` 触发。

### 1. 定义技能（EntitySkill.builder）

```java
EntitySkill roar = EntitySkill.builder("roar")     // 技能名（snake_case）
    .animationName("roar")                          // GeckoLib 动画名（必填）
    .damage(12.0f)                                  // 基础伤害（>=0）
    .range(5.0f)                                    // 作用范围（>0）
    .cooldownTicks(200)                             // 冷却 tick（>0）
    .particle("explosion")                          // 粒子名（与 ParticleAPI 联动，可选）
    .sound("pasterdream:terrorbeak_roar")           // 音效 ID（可选）
    .build();                                       // 缺少必填参数抛 IllegalStateException
```

### 2. 绑定到实体（EntityBuilder）

```java
EntityResult<MyEntity> result = EntityAPI.createEntity("my_entity")
    .category(MobCategory.MONSTER)
    .size(1.0f, 2.0f)
    .entityClass(MyEntity.class)
    .skills(roar, dash)                              // 或 .skill(roar)
    .build();
```

### 3. 运行时触发（EntitySkillManager）

```java
// 实体类中
private final EntitySkillManager skillManager = new EntitySkillManager(this);

@Override
public void baseTick() {
    super.baseTick();
    skillManager.tick();                              // 每 tick 更新冷却/执行阶段
}

// 触发技能（返回 false = 冷却中/未注册/已有技能执行中）
boolean ok = skillManager.tryTriggerSkill("roar", target);
```

### EntitySkillManager 方法

| 方法 | 说明 |
|------|------|
| `registerSkill(EntitySkill)` / `registerSkills(EntitySkill...)` | 注册技能（可链式） |
| `tick()` | 每 tick 调用，处理冷却与技能执行 |
| `tryTriggerSkill(String, LivingEntity)` | 尝试触发技能 |
| `getSkill(String)` / `getSkills()` | 查询技能 |
| `isOnCooldown(String)` / `getRemainingCooldown(String)` | 冷却查询 |
| `isSkillActive()` / `getCurrentSkill()` / `getSkillTimer()` | 执行状态查询 |
| `getCurrentlyPlayingAnim()` / `setCurrentlyPlayingAnim(String)` | 客户端 procedure 动画名读写 |
| 嵌套接口 `IAnimatedEntity` / `IProcedureAnimatable` | 实体实现后与动画系统联动 |

## 内置标签系统（EntityTag / EntityTagRegistry）

通过 `EntityBuilder.tag()` 为实体绑定内置行为标签，自动注册到 `EntityTagRegistry`：

```java
EntityAPI.createEntity("lamp_shadow")
    .category(MobCategory.MONSTER).size(1.0f, 2.0f)
    .entityClass(LampShadowEntity.class)
    .tag(EntityTag.LAMP_SHADOW_MONSTER)      // 同标签实体间不互相伤害
    .build();
```

| 标签常量 | 行为 |
|---------|------|
| `EntityTag.LAMP_SHADOW_MONSTER` | 同标签实体间不会互相造成伤害 |
| `EntityTag.SPELL_INVINCIBLE` | 实体加入世界后自动无敌（法术实体） |

运行时查询：

```java
EntityTagRegistry.hasTag(entity, EntityTag.LAMP_SHADOW_MONSTER);   // 实例判断
EntityTagRegistry.getTags(entityType);                             // 获取标签集合
EntityTagRegistry.getEntities(tag);                                // 反向查询实体
EntityTagRegistry.register(entityType, EntityTag...);              // 手动注册（非 Builder 路径）
```

## 完整示例

### 怪物 — 暗影傀儡（含技能）

```java
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
    .skill(EntitySkill.builder("dash_attack")
        .animationName("dash").damage(10.0f).range(4.0f).cooldownTicks(100)
        .particle("sparkle").sound("pasterdream:golem_dash").build())
    .spawnEgg(0x2C2C2C, 0x6B3FAF)
    .build();

// 渲染器（事件直接注册）
event.registerEntityRenderer(golem.entityType(), ShadowGolemRenderer::new);

// 属性
EntityAPI.registerAttributes(event, golem);
```

## 刷怪蛋系统

### 1. 颜色配置（Builder 阶段）

```java
EntityResult<ShadowGolemEntity> golem = EntityAPI.createEntity("shadow_golem")
    .category(MobCategory.MONSTER).size(2.2f, 3.5f)
    .entityClass(ShadowGolemEntity.class)
    .attributes(ShadowGolemEntity::createAttributes)
    .spawnEgg(0x2C2C2C, 0x6B3FAF)    // 底色, 高光色
    .build();
```

### 2. 刷怪蛋物品注册（PDItems 阶段）

```java
// registry/items/PDItemsSpawnEggs.java
public static final DeferredItem<Item> SHADOW_GOLEM_SPAWN_EGG =
    EntityAPI.createSpawnEggItem(ITEMS, "shadow_golem", PDEntities.SHADOW_GOLEM);
```

**原理**：`createSpawnEggItem()` 从 `.spawnEgg()` 缓存的颜色中自动读取，生成 `SpawnEggItem`。

### 3. 刷怪蛋模型自动生成

`EntityBuilder.build()` 时若已配置输出目录，自动生成 `{name}_spawn_egg.json`（内容固定 `{"parent": "minecraft:item/template_spawn_egg"}`）：

```java
EntityAPI.setSpawnEggModelsOutputDir(
    Path.of("PasterDream", "src", "main", "resources", "assets",
            "pasterdream", "models", "item"));
```

> ⚠️ 未配置 `.spawnEgg()` 就调用 `createSpawnEggItem()`，运行时抛 `IllegalStateException`「未配置生成蛋颜色」。

## 动画系统（ProcedureAnimationHandler）

服务端触发的一次性动画（技能、咆哮、受击），通过同步数据驱动 GeckoLib 播放。

### 架构

```
服务端                             客户端
  │                                 │
  ├─ setAnimation("roar") ──────►  ├─ 同步数据到达
  │   (更新 entityData.set)        │
  │                                ├─ ProcedureAnimationHandler.predicate()
  │                                │    ├─ 检测新动画 → 播放一次
  │                                │    ├─ 播放中 → CONTINUE
  │                                │    └─ 播完 → 重置 "empty"
  │                                └─ movementPredicate()
```

### 实现步骤

```java
// 1. 实体类字段
private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();

// 2. procedure 控制器回调
private PlayState procedurePredicate(AnimationState<MyEntity> state) {
    return procAnim.predicate(state,
            level().isClientSide(),
            this::getSyncedAnimation,
            () -> setAnimation("empty"));
}

// 3. movement 控制器（必须用 getSyncedAnimation() 判断）
private PlayState movementPredicate(AnimationState<MyEntity> state) {
    if (this.getSyncedAnimation().equals("empty")) {
        if (state.isMoving()) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
        }
        if (this.isDeadOrDying()) {
            return state.setAndContinue(RawAnimation.begin().thenPlay("death"));
        }
        return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
    }
    return PlayState.STOP;
}

// 4. 注册控制器
@Override
public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    controllers.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
    controllers.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
}

// 5. 服务端触发
this.setAnimation("roar");
```

### ❌ 常见错误

| 错误模式 | 后果 |
|---------|------|
| `movementPredicate` 使用本地 `animationprocedure` 字段判断 | procedure 动画被覆盖，永远播不出 |
| `procedurePredicate` 不检查 `level().isClientSide()` | 服务端/客户端争抢控制 |
| 无 currentlyPlaying 追踪 | 每帧重复触发动画 |

### ProcedureAnimationHandler API

| 方法 | 说明 |
|------|------|
| `predicate(state, isClientSide, syncedAnimSupplier, setEmptyAnim)` | 标准 procedure 动画回调 |
| `reset()` | 重置处理器状态（实体死亡时调用） |
| `getCurrentlyPlaying()` | 当前播放的动画名称（无则 "empty"） |

## 实体类构造要求

实体类必须包含 `(EntityType, Level)` 构造方法（Builder 通过反射创建实例）：

```java
public class ShadowGolemEntity extends Monster {
    public ShadowGolemEntity(EntityType<? extends ShadowGolemEntity> type, Level level) {
        super(type, level);
    }
}
```

## 查询方法（均返回 Optional）

```java
// 查询实体类型
Optional<EntityType<?>> type = EntityAPI.getEntityType("shadow_golem");

// 查询 EntityResult
Optional<EntityResult<?>> result = EntityAPI.getEntityResult("shadow_golem");

// 查询生成蛋颜色
Optional<int[]> colors = EntityAPI.getSpawnEggColors("shadow_golem");

// 查询实体技能
List<EntitySkill> skills = EntityAPI.getEntitySkills("shadow_golem");
Optional<EntitySkill> roar = EntityAPI.getEntitySkill("shadow_golem", "roar");
boolean has = EntityAPI.hasEntitySkill("shadow_golem", "roar");
```

## 引用文件

- [EntityAPI.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/entity/EntityAPI.java) — 门面类
- [EntityBuilder.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/entity/builder/EntityBuilder.java) — 构建器
- [EntityResult.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/entity/EntityResult.java) — 结果类
- [EntitySkill.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/entity/skill/EntitySkill.java) — 技能数据 record
- [EntitySkillBuilder.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/entity/skill/EntitySkillBuilder.java) — 技能构建器
- [EntitySkillManager.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/entity/skill/EntitySkillManager.java) — 技能管理器
- [ProcedureAnimationHandler.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/entity/anim/ProcedureAnimationHandler.java) — procedure 动画处理器
- [EntityTag.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/entity/tag/EntityTag.java) — 内置标签枚举
- [EntityTagRegistry.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/entity/tag/EntityTagRegistry.java) — 标签注册与查询
- [EntityAttributesGenerator.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/test/java/com/pasterdream/pasterdreammod/api/entity/gen/EntityAttributesGenerator.java) — 属性预设模板（test 目录）
