# AGENTS.md — PasterDream 项目规则

> AI Agent / 开发助手工作规则
> 重写日期: 2026-09-14（基于新版文档体系）
> 完整开发文档入口: [docs/README.md](docs/README.md)
> AI 辅助开发规则分层说明: [docs/开发指南/Agent使用规范.md](docs/开发指南/Agent使用规范.md)

## ⚠️ 任务启动强制流程（最高优先级）

**每次开始执行任何任务（开发/修复/排查/回答）前，必须先完成：**

1. 读取个人规则文件（如 `.opencode/personal-rules.md`——属个人工作区配置，非仓库文件；文件不存在或内容已自动注入上下文时跳过）
2. 逐条遵守其中的所有规则（沟通风格、代码规范、Git 规范、Python 脚本优先等）；个人配置与本文件或 `docs/` 冲突时，以项目规范为准
3. 任务涉及具体系统时，先查阅 `docs/` 下对应文档再动手

### 任务类型 → 必读文档

| 任务类型 | 必读文档 |
|---------|---------|
| 注册方块/物品/实体/粒子/效果/维度/遗迹 | `docs/开发指南/注册指南.md` |
| 添加新方块/实体/物品（完整流程） | `docs/教程/` 对应教程 |
| 资源文件放置（模型/动画/纹理） | `docs/开发指南/资源规范.md` |
| 判断代码放哪个模块 | `docs/架构/模块边界.md` |
| 方块掉落/战利品表问题 | `docs/开发指南/问题排查.md` |
| 编译/测试/VERIFY | `docs/开发指南/测试指南.md` |
| API 迁移对照（1.20→1.21） | `docs/参考/版本迁移.md` |
| API 上收/边界判定 | `docs/参考/API边界.md` |

## 项目概况

PasterDream 是面向 NeoForge 1.21.1 的**经原作者正式授权的移植项目**，以还原原模组的核心内容与玩法体验为目标，并针对新版本进行适配、修复和改进。

本项目已获得原作者异星之尘（Aerolite_Dust）的正式授权，由 NPD 团队独立开发和维护。相关记录见根目录[授权截图](授权截图.jpg)，版权归属及具体使用范围见[版权声明与授权状态](PERMISSIONS.md)。

- 原模组(`libs/FixPasterDream-main/`)作为内容与玩法的参考基准，目录保持只读
- 原模组由 MCreator 生成，技术实现必须基于 NeoForge 1.21.1 API 手写重实现
- **不直接复制或修改原代码**；以内容和行为的还原结果验证移植质量
- 对原版的修复、改进和有意差异需在设计文档中说明，并通过对应测试验收
- 版本跨度:1.20.1 Forge → 1.21.1 NeoForge

## 项目结构

```
NeoPasterDream1/
├── PasterDreamAPI/           # API 模块(Builder/Facade/Result/Config)
├── PasterDream/              # 主模块(方块/物品/实体/渲染/注册)
├── PasterDreamSpells/        # 附属模块:法术系统(thin 发行,编译期依赖 API)
├── PasterDreamSanity/        # 附属模块:理智系统(thin 发行,编译期依赖 API)
├── PasterDreamMeltDream/     # 附属模块:融梦能量系统(thin 发行,编译期依赖 API)
├── docs/                     # 开发文档(新版文档体系)
├── tools/                    # 工具脚本(Python)
└── libs/FixPasterDream-main/ # 原模组(只读参考)
```

> **附属模块说明**: PasterDreamSpells/Sanity/MeltDream 均为 thin 发行模式,不内嵌 PasterDreamAPI(由 PasterDream 主模组打包提供)。运行时需主模组作为前置。

## 文档体系

新版文档位于 `docs/`,入口为 [docs/README.md](docs/README.md):

| 目录 | 内容 |
|------|------|
| `docs/入门/` | 新人入门:环境搭建/项目结构/首次贡献 |
| `docs/架构/` | 架构文档:总览/模块边界/注册流程/客户端服务端 |
| `docs/开发指南/` | 开发指南:代码规范/Git/注册/资源/测试/排查/Agent 使用 |
| `docs/参考/` | 参考文档:版本迁移/API 边界 |
| `docs/教程/` | 教程:添加方块/实体/物品 |

> 旧版文档已移至 `docs/deprecated/`,后续将被移除,**勿引用**。

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
>
> 详细判定规则与勿上收清单见 [docs/架构/模块边界.md](docs/架构/模块边界.md) 与 [docs/参考/API边界.md](docs/参考/API边界.md)。

## 开发工作流

1. **分析效果**:查看原模组资源文件,理解游戏机制
2. **重新设计**:基于 NeoForge 1.21.1 API 实现
3. **手写代码**:使用 `DeferredRegister`、DataGen、GeckoLib
4. **编译测试**:
   - 全量编译:`.\gradlew compileJava`(编译所有模块)
   - 单模块编译:`.\gradlew :PasterDreamSpells:compileJava` 等
   - 数据生成:`.\gradlew runData`

环境搭建与常用命令详见 [docs/入门/环境搭建.md](docs/入门/环境搭建.md)。

## Git 提交信息规范

格式 `类型(范围): 内容`,使用中文,简洁明了:

- **类型**: `feat` / `fix` / `docs` / `style` / `refactor` / `test` / `chore`
- **范围**(可选): `api` / `block` / `entity` / `item` / `model` / `render` / `registry` / `client` / `server` / `worldgen` / `code & docs`

```
feat(api): 添加 BiomeShading API 以支持数据驱动的生物群系雾颜色
```

详见 [docs/开发指南/Git规范.md](docs/开发指南/Git规范.md)。

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

### Agent 写文件换行规范

- Agent 新建或修改文本文件时，必须显式使用 LF；`.bat` 按 `.editorconfig` 和 `.gitattributes` 使用 CRLF。
- `.editorconfig` 依赖编辑工具支持，`.gitattributes` 不会在脚本写入后自动转换换行符，不得仅凭配置存在就认为输出符合规则。
- Python 文本写入必须指定 `newline="\n"`（如 `open(path, "w", encoding="utf-8", newline="\n")`），或先统一换行后用 `write_bytes()` 写入 UTF-8 字节；不得依赖 Windows 默认文本换行转换。
- PowerShell 写入时，必须先将文本中的 CRLF 和孤立 CR 统一为 LF，再通过 `[System.IO.File]::WriteAllText()` 使用 UTF-8 无 BOM 编码保存；不得依赖 `Set-Content` / `Out-File` 的默认换行行为。
- 使用补丁工具后也必须检查实际文件字节。交付前仅校验本次新建或修改的文本文件：除 `.bat` 外，不得含 CRLF 或孤立 CR；发现后须转换为 LF 并复查差异。
- 不得为修复换行符批量重写无关文件、修改 `libs/`，或执行 Git 暂存、重置、检出等操作。

### 编码规范

- **非 MD 文件**:标准 ASCII 字符 + UTF-8 编码,禁止使用 Emoji
- **MD 文件**:UTF-8 编码,允许 Unicode 和 Emoji
- **代码注释**:使用 UTF-8 字符,如中文,但禁止使用 Emoji
- **代码注释规范**:推荐使用中文

### 导入顺序

1. 项目内部导入
2. 第三方库导入
3. Java 标准库导入

### 注册与实体

- **注册**:必须使用 `DeferredRegister`
- **实体**:继承 `GeckoLibMonsterEntity`/`GeckoLibAnimalEntity`
- **注释**:类级+方法级注释,参数用 `@param`

详见 [docs/开发指南/代码规范.md](docs/开发指南/代码规范.md)。

## 资源处理

**可直接复制**:纹理、声音、GeckoLib 模型/动画、语言文件
**需重新创建**:配方、战利品表、标签(DataGen)、维度文件、生物群系修饰器

### 战利品表 JSON 格式规范（NeoForge 1.21.1）

> ⚠️ **1.20 旧格式会导致整个战利品表解析失败 → 方块掉落本体/无掉落，且无任何报错！**

1. **数据包路径必须是单数 `loot_table`**（不是 1.20 的 `loot_tables`，复数目录会被静默忽略）：
   `data/<modid>/loot_table/blocks/<block_name>.json`
2. **`match_tool` 条件的 predicate 必须是 1.21.1 新格式**——外层需 `predicates."minecraft:enchantments"` 包装，`enchantment` → `enchantments`（复数）：

| 版本 | 格式 |
|------|------|
| ❌ 1.20 旧（解析失败） | `"predicate": { "enchantments": [ { "enchantment": "minecraft:silk_touch", "levels": {"min": 1} } ] }` |
| ✅ 1.21 新 | `"predicate": { "predicates": { "minecraft:enchantments": [ { "enchantments": "minecraft:silk_touch", "levels": {"min": 1} } ] } }` |

3. **权威对照**：`data/minecraft/loot_table/blocks/diamond_ore.json`
4. **常见错误**：`loot_tables` 复数目录；predicate 用单数 `enchantment` 且无 `predicates` 包装；空数组 `"functions": []` / `"conditions": []`；文件名大小写/拼写不匹配
5. **错误后果**：codec 解析失败 → 战利品表整体退回 `LootTable.EMPTY` → 普通 `Block` 无掉落、`SelfDropBlock` 兜底掉本体

改完战利品表后，用 `tools/` 下脚本批量校验 JSON 可解析且无旧格式残留。详见 [docs/开发指南/问题排查.md](docs/开发指南/问题排查.md)。

### GeckoLib 动画/模型文件目录规范

**复制原模组资源文件时,必须按以下规则放置,放错目录 = 游戏加载不到该文件且无任何报错!**

GeckoLib 的 `DefaultedGeoModel` 系列会根据 `subtype()` 自动决定资源路径的二级子目录:

| 模型类型 | 模型文件 (`geo/`) | 动画文件 (`animations/`) | 纹理文件 (`textures/`) |
|---------|------------------|------------------------|----------------------|
| `DefaultedEntityGeoModel` | `geo/entity/<name>.geo.json` | `animations/entity/<name>.animation.json` | `textures/entity/<name>.png` |
| `DefaultedBlockGeoModel` | `geo/block/<name>.geo.json` | `animations/block/<name>.animation.json` | `textures/block/<name>.png` |
| `DefaultedItemGeoModel` | `geo/item/<name>.geo.json` | `animations/item/<name>.animation.json` | `textures/item/<name>.png` |

**> 口诀:entity → entity/、block → block/、item → item/,别一股脑全塞 entity/ 里!**

特殊情况:

1. **BlockItem/DualRenderer(方块 & 物品共用资源)**:方块渲染器走 `block/`,物品渲染器若也使用 `DefaultedBlockGeoModel`,则动画/模型也走 `block/` 路径。
2. **自定义 `GeoModel` 子类**:路径由代码硬编码的 `ResourceLocation` 决定,与上表无关。**修改代码路径字符串时,必须同时确认文件实际存在。**

常见错误:方块动画放 `entity/`、实体动画放 `animations/` 根目录、复制资源不分目录全塞 `entity/`——后果均为加载不到且无报错;自定义 GeoModel 改路径后不检查文件存在 → 运行时 FileNotFoundException。

**操作检查清单**(每次复制动画/模型/纹理后核对):

1. [ ] 文件放对子目录了?(entity/entity? block/block? item/item?)
2. [ ] 对应的 Renderer 用了哪种 `DefaultedGeoModel`?
3. [ ] 自定义 `GeoModel` 的硬编码路径与文件实际位置一致?
4. [ ] `animations` 根目录及各子目录里没有不相关的文件?

详见 [docs/开发指南/资源规范.md](docs/开发指南/资源规范.md)。

## 第三方库

| 库 | 依赖方式 | 说明 |
|----|---------|------|
| GeckoLib | Maven | 实体/方块/物品 GeckoLib 渲染 |
| Curios | Maven | 饰品系统集成 |
| Player Animator | Maven (optional) | 玩家动画姿势(evasion/none) |
| JEI | compileOnly + localRuntime | 可选:配方查看器;发布 jar 不携带 |
| Patchouli | optional (纯数据) | 可选:图鉴手册包;无 Java 硬依赖 |

> Curios/GeckoLib/playerAnimator 已从 git 剥离(原 `libs/` 目录),改走 Maven 依赖。

## 禁止事项

1. ❌ 修改原模组代码(`libs/` 只读)
2. ❌ 复制 MCreator 代码
3. ❌ 硬编码配置
4. ❌ 忽略编译错误
5. ❌ 跳过 DataGen
6. ❌ 禁止主动对 Git 状态进行任何更改
7. ❌ 引用 `docs/deprecated/` 下的旧文档（后续将被移除）
