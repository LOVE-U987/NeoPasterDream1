---
name: "pasterdream-effect-api"
description: "PasterDream模组药水效果注册专用API，提供Facade+Builder模式一键注册自定义MobEffect。在需要创建新状态效果、配置效果属性/着色器/粒子/回调/药水酿造时调用。"
---

# PasterDream MobEffect API

本 Skill 提供 PasterDream 模组药水效果（MobEffect）注册专用 API 的使用指南，采用 **Facade + Builder** 模式（与 BlockAPI / EntityAPI / ParticleAPI 风格一致），通过链式调用即可完成状态效果的类型注册、属性配置和资源引用。

## 适用场景

- 创建新的自定义状态效果（BUFF / DEBUFF / 中性效果）
- 配置效果属性（分类 / 颜色 / 瞬时效果）
- 自定义屏幕着色器效果
- 与 ParticleAPI 联动的自定义粒子效果
- 配置效果回调（onApply / onRemove / onTick）
- 自定义效果叠加行为
- 注册可酿造药水（Potion）并与效果联动
- 查询已注册的效果类型

## 快速开始

```java
// ====== 在 PDEffects.java（或任意注册类）中注册效果 ======
MobEffectResult dreamwish = MobEffectAPI.createEffect("dreamwish_buff")
    .beneficial()                                    // 有益效果
    .color(0xFFFA8CE6)                                // 粉红色
    .build();

// ====== 在其他地方获取效果引用 ======
MobEffectResult result = MobEffectAPI.getEffect("dreamwish_buff");
if (result != null) {
    MobEffect effect = result.effect();              // 获取 MobEffect 实例
}
```

## 前置条件

在 `PasterDreamMod` 构造函数中注册 MobEffectAPI 的 REGISTRY：

```java
public PasterDreamMod(IEventBus modEventBus) {
    // ... 其他注册器 ...
    MobEffectAPI.REGISTRY.register(modEventBus);
}
```

## API 架构

```
MobEffectAPI                        ← Facade 门面
  ├── createEffect(name)            ← 工厂方法 → MobEffectBuilder
  ├── getEffect(name)               ← 查询 MobEffectResult
  ├── getEffectType(name)           ← 查询 MobEffect 直接引用
  ├── getEffectSupplier(name)       ← 查询 MobEffect Supplier
  ├── getPasterDreamEffect(name)    ← 查询 PasterDreamEffect 强类型引用
  ├── getRegisteredEffects()        ← 所有已注册效果的不可变视图
  └── REGISTRY                      ← DeferredRegister<MobEffect>

MobEffectBuilder                    ← Builder 构建器
  ├── beneficial() / harmful() / neutral() / category()  ← 分类
  ├── color(int)                    ← 效果颜色（十六进制）
  ├── instant()                     ← 瞬时效果（如瞬间治疗）
  ├── shaderTexture(ResourceLocation)  ← 屏幕着色器
  ├── particleType(ParticleType<?>) ← 粒子联动
  ├── onTick(ObjIntConsumer)        ← 每 tick 回调
  ├── onApply(BiConsumer)           ← 应用回调
  ├── onRemove(BiConsumer)          ← 移除回调
  ├── stackingHandler(BiFunction)   ← 叠加处理
  ├── builder(name)                 ← 静态工厂方法
  └── build()                       ← 注册 + 缓存 → MobEffectResult

MobEffectResult                     ← Record 结果
  ├── name()                        → String（注册名）
  ├── holder()                      → DeferredHolder<MobEffect, MobEffect>
  ├── typeSupplier()                → Supplier<MobEffect>
  ├── effect()                      → MobEffect
  └── asPasterDreamEffect()         → PasterDreamEffect / null

PasterDreamEffect                   ← MobEffect 子类
  ├── getConfig()                   → EffectConfig
  ├── getShaderTexture()            → ResourceLocation / null
  ├── getEffectParticleType()       → ParticleType<?> / null
  ├── spawnEffectParticles(...)     → 生成效果粒子
  ├── onApply(entity, amp)          → 应用时回调
  ├── onRemove(entity, amp)         → 移除时回调
  ├── handleStacking(existing, new) → 叠加处理
  └── EffectConfig                  ← 内部配置类（Builder 模式）
       ├── builder()                → EffectConfig.Builder
       ├── DEFAULT                  → 默认空配置
       └── 支持 shaderTexture / particleType / onTick / onApply / onRemove / stackingHandler
```

## Builder 配置参考

| 方法 | 参数 | 说明 |
|------|------|------|
| `beneficial()` | — | 设置为有益效果 |
| `harmful()` | — | 设置为有害效果 |
| `neutral()` | — | 设置为中性效果 |
| `category(MobEffectCategory)` | 分类 | 直接设置效果分类 |
| `color(int)` | 十六进制颜色 | 效果颜色（如 `0xFF69B4`） |
| `instant()` | — | 标记为瞬时效果（如瞬间治疗/伤害） |
| `shaderTexture(ResourceLocation)` | 资源路径 | 屏幕后期着色器纹理 |
| `particleType(ParticleType<?>)` | 粒子类型 | 与 ParticleAPI 联动 |
| `onTick(ObjIntConsumer<LivingEntity>)` | `(entity, amp) -> {}` | 每 tick 逻辑 |
| `onApply(BiConsumer<LivingEntity, Integer>)` | `(entity, amp) -> {}` | 应用时回调 |
| `onRemove(BiConsumer<LivingEntity, Integer>)` | `(entity, amp) -> {}` | 移除时回调 |
| `stackingHandler(BiFunction<MobEffectInstance, MobEffectInstance, MobEffectInstance>)` | `(existing, newInstance) -> result` | 叠加行为自定义 |

### 静态工厂方法

| 方法 | 说明 |
|------|------|
| `MobEffectBuilder.builder(String name)` | 直接创建构建器（无需通过 MobEffectAPI） |

## 完整示例

### 基础纯标记效果

```java
// 简单的有益标记效果，无额外逻辑
MobEffectResult dreamwish = MobEffectAPI.createEffect("dreamwish_buff")
    .beneficial()
    .color(0xFFFA8CE6)
    .build();
```

### 着色器 + 粒子联动效果

```java
MobEffectResult dreamwish = MobEffectAPI.createEffect("dreamwish_buff")
    .beneficial()
    .color(0xFF69B4)
    .shaderTexture(new ResourceLocation("pasterdream", "shaders/post/dreamwish.json"))
    .particleType(ParticleTypes.END_ROD)
    .build();
```

### 带 onTick 回调和 onApply/onRemove 的效果

```java
MobEffectResult expup = MobEffectAPI.createEffect("expup_buff")
    .beneficial()
    .color(0xFFABABD5)
    .onTick((entity, amplifier) -> {
        // 每 tick 1/1000 概率给 1 点经验
        if (Mth.nextInt(RandomSource.create(), 1, 1000) <= 10) {
            if (entity instanceof Player player) {
                player.giveExperiencePoints(1);
            }
        }
    })
    .onApply((entity, amp) -> entity.heal(5))
    .onRemove((entity, amp) -> {
        entity.hurt(entity.damageSources().magic(), 2);
    })
    .build();
```

### 自定义叠加行为

```java
MobEffectResult stackingEffect = MobEffectAPI.createEffect("stacking_demo")
    .beneficial()
    .color(0xFFFF9F6A)
    .stackingHandler((existing, newInstance) -> {
        // 叠加时延长持续时间（上限 6000 ticks）
        int totalDuration = existing.getDuration() + newInstance.getDuration();
        existing.duration = Math.min(totalDuration, 6000);
        return existing;
    })
    .build();
```

### 有害效果

```java
MobEffectResult confusion = MobEffectAPI.createEffect("confusion_buff")
    .harmful()
    .color(0xFF4A0080)
    .build();

MobEffectResult silence = MobEffectAPI.createEffect("shadow_silence_buff")
    .harmful()
    .color(0xFF2A0040)
    .build();
```

### 药水（可酿造）联动

在 `PDPotions.java` 中注册可酿造药水，与已注册的 MobEffect 联动：

```java
public class PDPotions {

    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(
            Registries.POTION, PasterDreamMod.MOD_ID);

    // 梦境祝福药水（3 分钟 = 3600 ticks）
    public static final DeferredHolder<Potion, Potion> DREAMWISH_POTION =
            POTIONS.register("dreamwish_potion",
                    () -> new Potion(new MobEffectInstance(
                            PDEffects.DREAMWISH_BUFF.holder(), 3600, 0, false, true)));

    // 经验提升药水（3 分钟 = 3600 ticks）
    public static final DeferredHolder<Potion, Potion> EXPUP_POTION =
            POTIONS.register("expup_potion",
                    () -> new Potion(new MobEffectInstance(
                            PDEffects.EXPUP_BUFF.holder(), 3600, 0, false, true)));
}
```

## 效果查询

```java
// 通过名称查询 MobEffectResult
MobEffectResult result = MobEffectAPI.getEffect("dreamwish_buff");
if (result != null) {
    MobEffect effect = result.effect();
}

// 直接获取 MobEffect 引用（无结果时返回 null）
MobEffect effect = MobEffectAPI.getEffectType("dreamwish_buff");

// 获取 Supplier 形式（延迟获取）
Supplier<MobEffect> supplier = MobEffectAPI.getEffectSupplier("dreamwish_buff");

// 获取 PasterDreamEffect 强类型引用（可访问自定义配置）
PasterDreamEffect pde = MobEffectAPI.getPasterDreamEffect("dreamwish_buff");
if (pde != null) {
    ResourceLocation shader = pde.getShaderTexture();
    ParticleType<?> particle = pde.getEffectParticleType();
}

// 获取所有已注册效果
Map<String, MobEffectResult> all = MobEffectAPI.getRegisteredEffects();
```

## 注意事项

1. **必填参数校验**：`category` 和 `color` 为必填参数，缺少任一会在 `build()` 时抛出 `IllegalStateException`
2. **REGISTRY 必须在 mod 构造函数中注册**：`MobEffectAPI.REGISTRY.register(modEventBus);`
3. **客户端专属代码**：`PasterDreamEffect` 类不包含任何客户端专属类型引用，确保服务端兼容
4. **药水注册**：药水（Potion）注册使用独立的 `DeferredRegister<Potion>`，通过 `MobEffectInstance` 与效果联动

## 引用文件

- [MobEffectAPI.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/effect/MobEffectAPI.java) — 门面类
- [MobEffectBuilder.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/effect/builder/MobEffectBuilder.java) — 构建器
- [MobEffectResult.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/effect/MobEffectResult.java) — 结果类
- [PasterDreamEffect.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/effect/base/PasterDreamEffect.java) — 自定义效果基类
- [PDEffects.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDEffects.java) — 效果注册示例
- [PDPotions.java](file:///C:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDPotions.java) — 药水注册示例