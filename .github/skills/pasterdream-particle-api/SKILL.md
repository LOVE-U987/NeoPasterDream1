---
name: "pasterdream-particle-api"
description: "PasterDream模组粒子注册专用API，提供Facade+Builder模式一键注册自定义粒子效果。在需要创建新粒子、配置粒子属性/纹理/多帧动画或生成粒子JSON时调用。"
---

# PasterDream Particle API

本 Skill 提供 PasterDream 模组粒子注册专用 API 的使用指南，采用 **Facade + Builder** 模式（与 BlockAPI / DimensionAPI 风格一致），通过链式调用即可完成粒子类型的注册、纹理配置和资源文件生成。

## 适用场景

- 创建新的自定义粒子效果（SimpleParticleType）
- 配置粒子属性（alwaysShow / 纹理 / 重力）
- 生成粒子定义 JSON（`particles/{name}.json`）
- 生成粒子纹理元数据（`textures/particle/{name}.json`）
- 注册粒子 Provider（客户端 SpriteSet 模式）
- 查询已注册的粒子类型

## 快速开始

```java
// ====== 1. 在 PDParticles.java（或任意注册类）中注册粒子 ======
ParticleResult sparkle = ParticleAPI.createParticle("sparkle")
    .alwaysShow()                          // 粒子始终更新渲染
    .texture("pasterdream:sparkle")        // 设置粒子纹理
    .withGravity(0.05f)                    // 设置重力参考值
    .build();                              // 注册 + 生成 JSON

// ====== 2. 在 ClientSetup.java 中注册 Provider ======
@SubscribeEvent
public static void registerParticles(RegisterParticleProvidersEvent event) {
    ParticleAPI.getParticle("sparkle").ifPresent(result ->
        event.registerSpriteSet(
            (SimpleParticleType) result.particleType(),
            SparkleParticle.Provider::new));
}

// ====== 3. 在代码中生成粒子 ======
ParticleAPI.getParticleType("sparkle").ifPresent(type ->
    level.addParticle(type, x, y, z, vx, vy, vz));
```

## 前置条件

在 `PasterDreamMod` 构造函数中注册 ParticleAPI 的 REGISTRY（或调用统一入口）：

```java
public PasterDreamMod(IEventBus modEventBus) {
    // 方式一：统一注册（推荐，包含所有 API）
    PasterDreamAPI.registerAll(modEventBus);
    // 方式二：单独注册
    // ParticleAPI.REGISTRY.register(modEventBus);
}
```

## API 架构

```
ParticleAPI                        ← Facade 门面
  ├── createParticle(name)         ← 工厂方法 → ParticleBuilder
  ├── getParticle(name)            ← 查询 ParticleResult → Optional
  ├── getParticleType(name)        ← 查询 ParticleType → Optional
  ├── getParticleSupplier(name)    ← 查询 ParticleType Supplier → Optional
  └── getRegisteredParticles()     ← 所有已注册粒子的不可变视图

ParticleBuilder                    ← Builder 构建器
  ├── alwaysShow()/alwaysShow(boolean)  ← 始终更新
  ├── texture(String)              ← 纹理路径
  ├── withGravity(float)           ← 重力参考值（文档用途）
  ├── generateJson(boolean)        ← 是否自动生成 JSON
  ├── basePath(String)             ← 资源文件基础路径
  └── build()                      ← 注册 + 生成 JSON → ParticleResult

ParticleResult                     ← Record 结果
  ├── name()                       → String（粒子注册名）
  ├── holder()                     → DeferredHolder
  ├── typeSupplier()               → Supplier<ParticleType<?>>
  ├── particleType()               → ParticleType<?>
  └── textureId()                  → String（如 "pasterdream:sparkle"）
```

## Builder 配置参考

| 方法 | 参数 | 说明 |
|------|------|------|
| `alwaysShow()` | — | 粒子始终更新渲染（等同于 `alwaysShow(true)`） |
| `alwaysShow(boolean)` | bool | 设置 alwaysUpdate 参数 |
| `texture(String)` | 纹理路径 | 粒子纹理路径（如 `pasterdream:sparkle`，默认 = `{modId}:{name}`） |
| `withGravity(float)` | 重力值 | 重力参考值（正数向下加速，实际重力需在 Particle 类中实现） |
| `generateJson(boolean)` | bool | 是否自动生成 JSON 文件（默认 true） |
| `basePath(String)` | 路径 | 资源文件基础路径（默认 `src/main/resources`） |

### 静态工厂方法

| 方法 | 说明 |
|------|------|
| `ParticleBuilder.builder(String name)` | 直接创建构建器（无需通过 ParticleAPI） |

## 完整示例

### 基础粒子 — 闪烁火花

```java
// 注册
ParticleResult sparkle = ParticleAPI.createParticle("sparkle")
    .alwaysShow()
    .withGravity(0.05f)
    .build();

// Provider（使用 SpriteSet）
public static class SparkleParticle extends TextureSheetParticle {
    protected SparkleParticle(ClientLevel level, double x, double y, double z,
                              SpriteSet spriteSet, double vx, double vy, double vz) {
        super(level, x, y, z, vx, vy, vz);
        this.setSpriteFromAge(spriteSet);
        this.gravity = 0.05f;
        this.lifetime = 30;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public record Provider(SpriteSet sprite) implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new SparkleParticle(level, x, y, z, sprite, vx, vy, vz);
        }
    }
}
```

### 多帧动画粒子 — 旋转水晶

```java
// 注册（配合多帧纹理）
ParticleResult crystal = ParticleAPI.createParticle("crystal_particle")
    .texture("pasterdream:crystal_particle")
    .build();

// 多帧动画：在 particles/{name}.json 的 textures 数组中列出所有帧
// 手动编辑 data/../assets/pasterdream/particles/crystal_particle.json：
// {
//   "textures": [
//     "pasterdream:crystal_particle_1",
//     "pasterdream:crystal_particle_2",
//     "pasterdream:crystal_particle_3",
//     "pasterdream:crystal_particle_4"
//   ]
// }
// 对应纹理 PNG 放在 textures/particle/ 下
```

### 复杂粒子 — 物理掉落物

```java
// 生命周期长、有重力的粒子
ParticleResult leaf = ParticleAPI.createParticle("falling_leaf")
    .alwaysShow()
    .withGravity(0.01f)
    .build();

// 查询并使用
ParticleAPI.getParticleType("falling_leaf").ifPresent(leafType ->
    level.addParticle(leafType, x, y, z,
        random.nextGaussian() * 0.02,
        -0.05,
        random.nextGaussian() * 0.02));
```

## 生成的 JSON 文件

```
src/main/resources/
└── assets/{modId}/
    ├── particles/{name}.json              ← 粒子定义（纹理列表）
    └── textures/particle/{name}.json      ← 粒子纹理元数据（重力、路径参考）
```

### particles/{name}.json 格式

```json
{
  "textures": [
    "pasterdream:sparkle"
  ]
}
```

### textures/particle/{name}.json 格式

```json
{
  "particle": "pasterdream:sparkle",
  "gravity": 0.05,
  "texture_dir": "assets/pasterdream/textures/particle/",
  "texture_base": "pasterdream:sparkle"
}
```

## 粒子纹理放置位置

```
assets/{modId}/textures/particle/{name}.png    ← 粒子纹理图片
```

对于多帧粒子，建议使用精灵表（Sprite Sheet）或在 textures/particle/ 目录下放置多张 PNG：
- `assets/{modId}/textures/particle/{name}_1.png`
- `assets/{modId}/textures/particle/{name}_2.png`
- ...

## Provider 注册说明

客户端在 `RegisterParticleProvidersEvent` 中注册 Provider，**没有** `ParticleAPI.registerProviderSprite()` 方法（已移除）。请直接通过 `ParticleAPI.getParticle()` 查询结果并使用事件的 `registerSpriteSet`：

```java
@SubscribeEvent
public static void registerParticles(RegisterParticleProvidersEvent event) {
    // ✅ 正确：通过 API 查询后直接注册
    ParticleAPI.getParticle("sparkle").ifPresent(result ->
        event.registerSpriteSet((SimpleParticleType) result.particleType(), SparkleParticle.Provider::new));

    // ⚠️ 未注册的名称查询会返回 empty Optional，静默跳过
    ParticleAPI.getParticle("sparkle_miss").ifPresent(result -> { /* 不会执行 */ });
}
```

> 粒子名称必须与 `createParticle()` 传入的名称一致（内部缓存按名称查询）。

## 引用文件

- [ParticleAPI.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/particle/ParticleAPI.java) — 门面类
- [ParticleBuilder.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/particle/builder/ParticleBuilder.java) — 构建器
- [ParticleResult.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/particle/ParticleResult.java) — 结果类
- [PDParticles.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDParticles.java) — 粒子注册示例
- [SparkleParticle.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDream/src/main/java/com/pasterdream/pasterdreammod/client/particle/SparkleParticle.java) — 粒子类示例（主模块 client/particle/）