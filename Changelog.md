# PasterDream Changelog

---

## v0.9.2 — 2026-08-03

### 高层摘要（TL;DR）

*   **影响范围：** 中 - 新增熔梦（Meltdream）工具体系、首次登录指南书、Patchouli 1.21 兼容修复，以及开发环境从 TRAE 迁移到 VS Code
*   **核心变更：**
    *   ⚒️ **熔梦系统上线**：熔梦能量 0 戒指与 4 种熔梦工具（镐/斧/铲/锄），带独特「熔梦能量修复」机制
    *   📖 **首次登录自动发放多蕾米指南书**（Patchouli 可选依赖，新旧存档均支持）
    *   🔧 **Patchouli 1.21 手册兼容**：修正指南合成配方，恢复改名错拷贴图
    *   🏗️ **开发环境迁移**：移除 TRAE 环境，资源迁移到 VS Code（skills / 工具脚本 / 文档归档）
    *   🛠️ **构建与规范**：修复 emoji 导致的构建失败，新增分支管理规范与全库审查文档

---

### ⚒️ 3.1 熔梦系统（Meltdream）

**核心变更：** 将熔梦相关物品注册收口到主模组命名空间，并引入独特的「熔梦能量修复」机制。

| 变更 | 说明 |
|------|------|
| 熔梦能量 0 戒指 | 注册到主模组命名空间，无论是否加载 `PasterDreamMeltDream` 模块均可用（`MeltdreamEnergy0RingItem`） |
| 熔梦工具系列 | 新增熔梦镐/斧/铲/锄 4 种工具（`MeltdreamPickaxeItem` 等），采用熔梦能量进行独特修复（`MeltdreamToolHelper`） |
| 工具提示更新 | 熔梦物品 tooltip 同步说明新修复机制（中英文语言文件） |
| 物品动画 | 为染梦剑、影之剑、熔金真剑、尖啸波等物品新增 `.mcmeta` 动画帧 |
| 模块清理 | 移除 `PasterDreamMeltDream` 模块中的冗余注册，统一由主模组管理 |
| 戒指标签 | `curios/tags/item/ring.json` 更新，纳入熔梦能量 0 戒指 |

#### 熔梦锻造配方修正

| 变更 | 说明 |
|------|------|
| base/addition 槽位纠正 | 将 dyedream 工具放入 `base`、`meltdream_crystal_0` 放入 `addition`，修复 4 个熔梦升级锻造配方（镐/斧/铲/锄）槽位颠倒的问题 |

---

### 📖 3.2 首次登录指南书

**核心变更：** 玩家首次进入世界时自动获得多蕾米指南书。

| 变更 | 说明 |
|------|------|
| 自动发放 | `PlayerDataEvents.onPlayerLoggedIn` 新增 `giveGuideBookIfNeeded`，通过 `PatchouliAPI` 发放 `pasterdream:doremys_guidebook` |
| 新旧存档支持 | 新档与已有存档首次登录均会触发 |
| 防重复发放 | 持久化 NBT 标记 `pasterdream.guide_book_given` |
| 可选依赖 | Patchouli 声明为 `compileOnly`，未安装时静默跳过（与 JEI 可选依赖模式一致） |

---

### 🔧 3.3 Patchouli 1.21 手册兼容

| 变更 | 说明 |
|------|------|
| 合成配方 | 帕斯特指南合成改为 `guide_book` + `patchouli:book` 组件，声明可选依赖 |
| 贴图恢复 | 从原版找回武器 / 密实冰 / 细影石砖 / 平滑石英贴图，并修正对应模型引用（细影石砖系列楼梯/台阶/墙等） |

---

### 🏗️ 3.4 开发环境迁移（TRAE → VS Code）

| 变更 | 说明 |
|------|------|
| 删除 `.trae/` | 移除 42 个规则/skills/工具/specs/报告文档文件 |
| skills 迁移 | 8 个项目 skills 迁移至 `.github/skills/`（ItemAPI、BlockDrops、EffectAPI、EntityAPI、ModDev、ParticleAPI、RuinAPI、WorldDecoration） |
| 工具脚本迁移 | 20 个脚本迁移至 `tools/`，同步更新 `build.gradle`、`.run/*.run.xml` 引用 |
| 文档归档 | docs 迁移至 `docs/archive/` |
| AGENTS.md | 合并 TRAE 项目规则 + Git 提交信息规范 |
| .gitignore | 增加 `__pycache__/` 与 `*.py[cod]`，清理已跟踪的编译缓存 |

---

### 🛠️ 3.5 构建与文档规范

| 变更 | 说明 |
|------|------|
| 构建修复 | 修复特定情况下 emoji 导致的构建失败（`wuyu_doll` 模型与 `mineable/hoe` 标签） |
| AGENTS.md | 新增分支管理规范（禁止直接提交 main、按「用户名/主题」命名分支） |
| 全库审查 | 新增 full-repository-review 文档、合并 CHANGELOG 与修复对比表（fix-comparison-table） |
| 归档清理 | 归档结案审查快照，清理无效文档与 build-errors 残渣 |

