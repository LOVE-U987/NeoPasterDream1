# AGENTS.md — PasterDream 项目规则

> 由 `Trae CN/.trae/rules/project_rules.md` 迁移而来
> 迁移日期: 2026-08-03

## ⚠️ 任务启动强制流程（最高优先级）

**每次开始执行任何任务（开发/修复/排查/回答）前，必须先完成：**

1. 读取个人全局规则文件：`.opencode/personal-rules.md`
2. 逐条遵守其中的所有规则（沟通风格、代码规范、Git 规范、Python 脚本优先、Skills 优先等）
3. 任务涉及物品/注册/实体/粒子/效果/遗迹/装饰/维度时，先读取 `.github/skills/` 中对应 SKILL 再动手

> 若个人规则文件内容已自动注入上下文，可跳过第 1 步；否则必须读取。

## 项目概况

PasterDream NeoForge 1.21.1 模组开发项目。**核心理念:精神续作,而非代码移植。**

- 原模组(`libs/FixPasterDream-main/`)仅作为**参考**,部分开发方向已偏离原模组设计
- 原模组是 MCreator 生成,代码不可移植,必须基于 NeoForge 1.21.1 API 重新实现
- **不直接复制或修改原代码**,相同效果用不同技术方案实现
- 版本跨度:1.20.1 Forge → 1.21.1 NeoForge

## 项目结构

```
NeoPasterDream1/
├── PasterDreamAPI/           # API 模块(Builder/Facade/Result/Config)
│   └── src/main/java/.../api/
├── PasterDream/              # 主模块(方块/物品/实体/渲染/注册)
│   └── src/main/java/.../
├── PasterDreamSpells/        # 附属模块:法术系统(thin 发行,编译期依赖 API)
├── PasterDreamSanity/        # 附属模块:理智系统(thin 发行,编译期依赖 API)
├── PasterDreamMeltDream/     # 附属模块:融梦能量系统(thin 发行,编译期依赖 API)
├── src/                      # 旧目录(已归档 @Deprecated,不再参与构建)
└── libs/FixPasterDream-main/ # 原模组(只读参考)
```

> **附属模块说明**: PasterDreamSpells/Sanity/MeltDream 均为 thin 发行模式,不内嵌 PasterDreamAPI(由 PasterDream 主模组打包提供)。运行时需主模组作为前置。

## 多模块架构策略:模块归属决策

新代码按以下条件判断归属:

| 条件(满足任一) | 目标模块 |
|----------------|---------|
| API 接口 / Builder / Facade / Result / Config 类 | → `PasterDreamAPI` |
| 会被多个业务模块引用的类 | → `PasterDreamAPI` |
| 属于注册体系(DeferredRegister/DataGen) | → `PasterDreamAPI` |
| 需要被其他模组作为库依赖 | → `PasterDreamAPI` |
| 法术系统(卷轴/投射物/立场/法术效果) | → `PasterDreamSpells` |
| 理智系统(San值/环境修饰/低San惩罚) | → `PasterDreamSanity` |
| 融梦能量系统(能量池/恢复/消耗) | → `PasterDreamMeltDream` |
| 以上均不满足(方块/物品/实体/渲染/客户端代码) | → `PasterDream` |

> **口诀**:API/Builder/注册门面 → API 模块;法术 → Spells;理智 → Sanity;融梦 → MeltDream;其余 → 主模块。

## 开发工作流

1. **分析效果**:查看原模组资源文件,理解游戏机制
2. **重新设计**:基于 NeoForge 1.21.1 API 实现
3. **手写代码**:使用 `DeferredRegister`、DataGen、GeckoLib
4. **编译测试**:
   - 全量编译:`.\gradlew compileJava`(编译所有模块)
   - 单模块编译:`.\gradlew :PasterDreamSpells:compileJava` 等
   - 数据生成:`.\gradlew runData`
   - 客户端测试:`.\gradlew runClient`

## Git 提交信息规范

### 提交格式

```
类型(范围): 内容
```

**类型**: `feat` / `fix` / `docs` / `style` / `refactor` / `test` / `chore`

**范围**(可选): `api` / `block` / `entity` / `item` / `model` / `render` / `registry` / `client` / `server` / `refactor` / `code & docs`

**示例**:
```
fix(model): correct dyedream_hanging_vine item and drop form
fix(code & docs): disable fillHang for cloud fall and update Issue-#11 tracker
fix(refactor): reduce the formation of ice_crystal_spike
```

### 基本要求

1. 语言准确,避免使用特殊字符,确保使用的语言为英文
2. 提交信息简洁明了,避免使用复杂的语言

## 分支策略

### 分支命名

使用 `类型/负责人/主题` 格式:

- **类型**: `feature` / `fix` / `refactor` / `docs` / `test`
- **负责人**: GitHub 用户名
- **主题**: 简短描述,使用小写字母和连字符

**示例**:
```
feature/momonyako/dream-meter
fix/phantomdaze/loot-table
refactor/username/cleanup-api
```

### 工作流程

1. 从 `main` 分支或基于主分支变基的个人分支创建功能分支
2. 在功能分支上进行开发
3. 完成开发后,创建 Pull Request,交由核心开发者审查
4. 经过代码审查后合并到 `main`
5. 合并后及时删除功能分支

## 多线程开发策略

| 模块 | 开发方式 | 注意 |
|-----|---------|------|
| 独立物品/方块 | 可并行 | 避免同时修改同一文件 |
| 实体系统 | 可并行 | 需协调渲染器注册 |
| 数据生成 | 可并行 | - |
| 附属模块(Spells/Sanity/MeltDream) | 可并行 | 各模块独立,但共享 API |
| 跨模块功能 | 串行/协调 | Capability、网络包等 |

## API 迁移对照

| 1.20.1 Forge | 1.21.1 NeoForge |
|-------------|----------------|
| `forge:` | `neoforge:` 或 `c:` |
| `forge/tags/items/` | `c/tags/item/` |
| `forge:fluid_container` | `neoforge:fluid_container` |

## 代码规范

### 命名约定

- **类**:PascalCase(如 `ShadowGolemEntity`)
- **方法**:camelCase(如 `createAttributes`)
- **常量**:UPPER_SNAKE_CASE(如 `MOD_ID`)
- **注册名**:snake_case(如 `shadow_golem`)

### 格式规范

- **缩进**:4 空格,禁止制表符
- **大括号**:K&R 风格(右花括号在同一行)
- **换行符**:LF(Unix 格式)
- **行长度**:推荐 120 字符,最大 150 字符
- **空格**:运算符周围、逗号后、冒号后

### 编码规范

- **非 MD 文件**:标准 ASCII 字符 + UTF-8 编码,禁止使用 Emoji
- **MD 文件**:UTF-8 编码,允许 Unicode 和 Emoji
- **代码注释**:允许使用 UTF-8 字符(如中文)

### 导入顺序

1. 项目内部导入
2. 第三方库导入
3. Java 标准库导入

### 注册与实体

- **注册**:必须使用 `DeferredRegister`
- **实体**:继承 `GeckoLibMonsterEntity`/`GeckoLibAnimalEntity`
- **注释**:类级+方法级注释,参数用 `@param`

## 第三方库

| 库 | 依赖方式 | 说明 |
|----|---------|------|
| GeckoLib | Maven | 实体/方块/物品 GeckoLib 渲染 |
| Curios | Maven | 饰品系统集成 |
| Player Animator | Maven (optional) | 玩家动画姿势(evasion/none) |
| JEI | compileOnly + localRuntime | 可选:配方查看器;发布 jar 不携带 |
| Patchouli | optional (纯数据) | 可选:图鉴手册包;无 Java 硬依赖 |

> Curios/GeckoLib/playerAnimator 已从 git 剥离(原 `libs/` 目录),改走 Maven 依赖。

## 资源处理

**可直接复制**:纹理、声音、GeckoLib 模型/动画、语言文件
**需重新创建**:配方、战利品表、标签(DataGen)、维度文件、生物群系修饰器

### 战利品表 JSON 格式规范（NeoForge 1.21.1）

> ⚠️ **1.20 旧格式会导致整个战利品表解析失败 → 方块掉落本体/无掉落，且无任何报错！**

**1. 数据包路径必须是单数 `loot_table`**（不是 1.20 的 `loot_tables`）：
```
data/<modid>/loot_table/blocks/<block_name>.json
```

**2. `match_tool` 条件的 predicate 必须是 1.21.1 新格式**：

| 版本 | 格式 |
|------|------|
| ❌ 1.20 旧（解析失败） | `"predicate": { "enchantments": [ { "enchantment": "minecraft:silk_touch", "levels": {"min": 1} } ] }` |
| ✅ 1.21 新 | `"predicate": { "predicates": { "minecraft:enchantments": [ { "enchantments": "minecraft:silk_touch", "levels": {"min": 1} } ] } }` |

关键差异：外层需 `predicates."minecraft:enchantments"` 包装，`enchantment` → `enchantments`（复数）。

**参考原版**：`data/minecraft/loot_table/blocks/diamond_ore.json`（唯一权威对照）。

**3. 格式错误后果**：`ItemPredicate` codec 解析失败 → 战利品表整体退回 `LootTable.EMPTY` → 普通 `Block` 无掉落、`SelfDropBlock` 兜底掉本体（矿石会掉矿石本体而非粗矿）。

**4. 常见错误清单**：
- ❌ 用 `loot_tables` 复数目录 → 静默忽略
- ❌ `match_tool` predicate 用单数 `enchantment` + 无 `predicates` 包装 → 解析失败
- ❌ 空数组 `"functions": []` / `"conditions": []` → 解析失败
- ❌ 文件名大小写/拼写不匹配

**5. 批量校验**：改完战利品表后，用项目脚本验证所有 JSON 可解析且无残留旧格式（参照 `tools/` 或临时脚本扫描 `"enchantment": "minecraft:silk_touch"` + 无 `predicates` 的情况）。

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
6. ❌ 禁止主动对Git状态进行任何更改
