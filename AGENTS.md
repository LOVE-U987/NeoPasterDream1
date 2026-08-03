# AGENTS.md — PasterDream 项目规则

> 由 `Trae CN/.trae/rules/project_rules.md` 迁移而来
> 迁移日期: 2026-08-03

## 项目概况

PasterDream NeoForge 1.21.1 模组开发项目。**核心理念:精神续作,而非代码移植。**

- 参考原模组呈现效果,但**不直接复制或修改原代码**
- 相同效果,用不同技术方案重新实现
- 原模组是 MCreator 生成,必须重写,MCreator 代码不可移植
- 版本跨度:1.20.1 Forge → 1.21.1 NeoForge

## 项目结构

```
NeoPasterDream1/
├── PasterDreamAPI/          # API 模块(Builder/Facade/Result/Config)
│   └── src/main/java/.../api/
├── PasterDream/             # 主模块(方块/物品/实体/渲染/注册)
│   └── src/main/java/.../
├── src/                     # 旧目录(已归档 @Deprecated,不再参与构建)
└── libs/FixPasterDream-main/  # 原模组(只读参考)
```

## API-Split 多模块架构策略:模块归属决策

新代码按以下条件判断归属:

| 条件(满足任一) | 目标模块 |
|----------------|---------|
| API 接口 / Builder / Facade / Result / Config 类 | → `PasterDreamAPI` |
| 会被多个业务模块引用的类 | → `PasterDreamAPI` |
| 属于注册体系(DeferredRegister/DataGen) | → `PasterDreamAPI` |
| 需要被其他模组作为库依赖 | → `PasterDreamAPI` |
| 以上均不满足(方块/物品/实体/渲染/客户端代码) | → `PasterDream` |

> **口诀**:API/Builder/注册门面 → API 模块;方块/物品/实体/渲染 → 主模块。

## 开发工作流

1. **分析效果**:查看原模组资源文件,理解游戏机制
2. **重新设计**:基于 NeoForge 1.21.1 API 实现
3. **手写代码**:使用 `DeferredRegister`、DataGen、GeckoLib
4. **编译测试**:`.\gradlew compileJava`(自动编译两个模块)→ `.\gradlew runData` → `.\gradlew runClient`

## Git 提交信息规范

> 由 TRAE `rules/git-commit-message.md` 迁移而来(2026-08-03)

1. 语言准确,避免使用特殊字符,确保使用的语言为英文
2. 提交信息简洁明了,避免使用复杂的语言
3. 提交信息中包含相关代码的行号和文件名,方便定位问题

## 分支与合并策略

1. 禁止直接对主分支 `main` 提交
2. 开发优先以「用户名/主题」命名分支提交，如 `momonyako/develop`、
   `phantomdaze/arena-fix`
3. 功能完成并自测通过后，合并到 `main`
   确认；fork 提交的 PR 遵循同一规则
4. 分支合并后及时删除，避免远程堆积，个人开发主分支不受此约束

## 多线程开发策略

| 模块 | 开发方式 | 注意 |
|-----|---------|------|
| 独立物品/方块 | 可并行 | 避免同时修改同一文件 |
| 实体系统 | 可并行 | 需协调渲染器注册 |
| 数据生成 | 可并行 | - |
| 跨模块功能 | 串行/协调 | Capability、网络包等 |

## API 迁移对照

| 1.20.1 Forge | 1.21.1 NeoForge |
|-------------|----------------|
| `forge:` | `neoforge:` 或 `c:` |
| `forge/tags/items/` | `c/tags/item/` |
| `forge:fluid_container` | `neoforge:fluid_container` |

## 代码规范

- **命名**:类 PascalCase,方法 camelCase,常量 UPPER_SNAKE_CASE,注册名 snake_case
- **注册**:必须使用 `DeferredRegister`
- **实体**:继承 `GeckoLibMonsterEntity`/`GeckoLibAnimalEntity`
- **注释**:类级+方法级注释,参数用 `@param`

## 资源处理

**可直接复制**:纹理、声音、GeckoLib 模型/动画、语言文件
**需重新创建**:配方、战利品表、标签(DataGen)、维度文件、生物群系修饰器

### GeckoLib 动画/模型文件目录规范

**复制原模组资源文件时,必须按以下规则放置,放错目录 = 游戏加载不到该文件且无任何报错!**

GeckoLib 的 `DefaultedGeoModel` 系列会根据 `subtype()` 自动决定资源路径的二级子目录:

| 模型类型 | 模型文件 (`geo/`) | 动画文件 (`animations/`) | 纹理文件 (`textures/`) |
|---------|------------------|------------------------|----------------------|
| `DefaultedEntityGeoModel` | `geo/entity/<name>.geo.json` | `animations/entity/<name>.animation.json` | `textures/entity/<name>.png` |
| `DefaultedBlockGeoModel` | `geo/block/<name>.geo.json` | `animations/block/<name>.animation.json` | `textures/block/<name>.png` |
| `DefaultedItemGeoModel` | `geo/item/<name>.geo.json` | `animations/item/<name>.animation.json` | `textures/item/<name>.png` |

**> 口诀:entity → entity/、block → block/、item → item/,别一股脑全塞 entity/ 里!**

#### 特殊情况处理

1. **BlockItem/DualRenderer(方块 & 物品共用资源)**:方块渲染器走 `block/`,物品渲染器如果也使用 `DefaultedBlockGeoModel`,则物品的动画/模型也走 `block/` 路径。
2. **自定义 `GeoModel` 子类**(如 `DreamMeterItemModel` 直接继承 `GeoModel`):路径完全由代码中硬编码的 `ResourceLocation` 决定,与上述约定无关。**修改代码中的路径字符串时,必须同时确认文件实际存在。**

#### 常见错误

| ❌ 错误行为 | 后果 | ✅ 正确做法 |
|-----------|------|-----------|
| 把方块动画 `dream_cauldron.animation.json` 放到 `entity/` 目录 | 方块动画不播放,无报错 | 放到 `block/` 目录 |
| 把实体动画放到 `animations/` 根目录 | 实体动画不播放,无报错 | 放到 `entity/` 目录 |
| 复制原模组资源文件时不分目录一股脑全放 `entity/` | 方块/物品动画变孤儿文件,或无法加载 | 按上表分门别类放置 |
| 自定义 GeoModel 改路径后不检查文件是否存在 | 运行时 FileNotFoundException | 改路径后确认目标文件实际存在 |

#### 操作检查清单

**每次从原模组复制动画/模型/纹理文件后,必须核对:**
1. [ ] 文件放对子目录了?(entity/entity?block/block?item/item?)
2. [ ] 对应的 Renderer 用了哪种 `DefaultedGeoModel`?
3. [ ] 自定义 `GeoModel` 的硬编码路径与文件实际位置一致?
4. [ ] 对于 `animations` 根目录、`animations/entity/`、`animations/block/`,每个目录里没有不相关的文件?

## 禁止事项

1. ❌ 修改原模组代码(`libs/` 只读)
2. ❌ 复制 MCreator 代码
3. ❌ 硬编码配置
4. ❌ 忽略编译错误
5. ❌ 跳过 DataGen
