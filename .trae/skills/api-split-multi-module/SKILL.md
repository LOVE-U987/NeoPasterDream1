---
name: "api-split-multi-module"
description: "Guides API-Split multi-module architecture decisions for NeoPasterDream (NeoForge 1.21.1). Invoke when deciding where to place new code (PasterDreamAPI vs PasterDream), creating Builder/Facade/Result/Config classes, or designing new registration systems."
---

# API-Split 多模块架构策略

## 适用条件与范围

### 应当优先使用 API-Split 的场景

| 场景           | 说明                                  | 示例                                              |
| ------------ | ----------------------------------- | ----------------------------------------------- |
| **新增 API 类** | 创建新的 Builder/Facade/Result/Config 类 | `BlockAPI`, `EntityBuilder`, `ParticleResult` 等 |
| **新增注册体系**   | 设计新的注册流程或数据生成器                      | `DimensionAPI`, `RuinAPI` 等注册 Facade            |
| **跨模块调用**    | API 类被多个业务模块引用                      | `EntityResult` 被 `PDEntities.java` 和业务代码同时使用    |
| **独立发布需求**   | 需要被其他模组或项目作为库依赖                     | 附属模组需要依赖核心 API                                  |
| **包路径变更**    | 移动或重命名 API 相关包                      | 将 `worldgen/decor/` 中的 API 类移至 API 模块           |

### 不应使用 API-Split 的场景

| 场景                                          | 原因                        |
| ------------------------------------------- | ------------------------- |
| **纯业务逻辑**（具体的方块/物品/实体类）                     | 属于主模块职责，不需要独立发布           |
| **客户端专属代码**（渲染器、粒子效果、GUI）                   | 强依赖 Minecraft 客户端，不适合独立发布 |
| **临时调试工具**（调试命令、测试用 Wand 物品）                | 一次性代码，不值得模块化              |
| **原版 Minecraft 扩展类**（继承原版 Block/Item 的自定义类） | 属于业务实现层                   |

## 决策树

```
[新代码要放在哪?]
    │
    ├─ 属于 API 接口/Builder/Facade/Result/Config？
    │   ├─ ✅ → PasterDreamAPI 模块
    │   └─ ❌ → 继续判断
    │
    ├─ 会被多个业务模块引用？
    │   ├─ ✅ → PasterDreamAPI 模块
    │   └─ ❌ → 继续判断
    │
    ├─ 是注册体系的一部分（DeferredRegister/DataGen）？
    │   ├─ ✅ → PasterDreamAPI 模块
    │   └─ ❌ → 继续判断
    │
    ├─ 需要被其他模组作为库依赖？
    │   ├─ ✅ → PasterDreamAPI 模块
    │   └─ ❌ → PasterDream 模块（主模块）
```

**口诀**: API 接口、Builder、Result、注册门面 → 放 API 模块；具体的方块、物品、实体、渲染 → 放主模块。

## 模块职责边界

| 模块                 | 包路径                                                                | 应包含的内容                                                                                                                    | 不应包含的内容                              |
| ------------------ | ------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------- | ------------------------------------ |
| **PasterDreamAPI** | `PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/` | BlockAPI, EntityAPI, ParticleAPI, DimensionAPI, RuinAPI, MobEffectAPI, ItemMigrationAPI, 以及所有 Builder/Result/Config/Gen 类 | 具体的 Block/Item/Entity 子类，客户端渲染代码     |
| **PasterDream**    | `PasterDream/src/main/java/com/pasterdream/pasterdreammod/`        | 所有方块、物品、实体、流体、容器、渲染器、注册管理器（PDBlocks, PDItems 等）、DataGen 提供者                                                               | 纯 API 接口/门面类（应通过依赖使用 PasterDreamAPI） |
| **旧目录（已归档）**       | `src/main/java/com/pasterdream/pasterdreammod/`                    | 仅保留 `package-info.java` 标记为 @Deprecated                                                                                   | 不应在此目录添加新代码                          |

## 通用备选处理流程

如果 API-Split 架构不适用，按以下优先级选择替代方案：

1. **单模块内分层**：如果项目未来可能演进为多模块，先在单个模块内按 `api/`、`impl/` 分包，为日后拆分预留空间
2. **Facade 门面封装**：如果不值得拆模块，但需要统一入口，至少提供一个 Facade 类将注册逻辑封装起来（参考 `BlockAPI` 的设计）
3. **inline 直写**：对于极简单的一行式注册（如只调 `ITEMS.register()`），直接在注册类中内联即可，不需要额外抽象

## 执行指引

### 新增 API 模块内容时

1. **创建文件位置**：`PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/` 下的对应子包
2. **注册事件总线**：在 `PasterDreamMod.java` 构造器中 `.register(modEventBus)`
3. **添加语言文件**：检查 `PasterDream/src/main/resources/assets/pasterdream/lang/zh_cn.json`
4. **编译验证**：`.\gradlew compileJava`（会自动编译两个模块）

## API 设计一致性

所有 API 应遵循统一的设计模式（Facade + Builder）：

| API | Facade 入口           | Builder 类                       | 注册方法                  |
| --- | ------------------- | ------------------------------- | --------------------- |
| 方块  | `BlockAPI`          | `SimpleBlockBuilder`            | `.register()`         |
| 实体  | `EntityAPI`         | `EntityBuilder`                 | `.buildAndRegister()` |
| 粒子  | `ParticleAPI`       | `ParticleAPI.ParticleBuilder`   | `.build()`            |
| 维度  | `DimensionAPI`      | `DimensionAPI.DimensionBuilder` | `.register()`         |
| 装饰物 | `DecorationBuilder` | （自身即是Builder）                   | `.register()`         |

新 API 请遵循此模式，确保调用方式一致。

