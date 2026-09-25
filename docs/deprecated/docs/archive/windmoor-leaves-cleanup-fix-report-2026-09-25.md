# 风泊树叶合并遗留问题修复报告（2026-09-25）

> 背景：v0.10.0 变更将 `windmoor_leaves_0` / `windmoor_leaves_1` / `windmoor_leaves_2` 三合一为 `windmoor_leaves`，并新增 `windmoor_hanging_vine`。
> 收尾审查在该变更中发现 3 处问题与 1 处待判定差异，本报告记录问题描述、根因与修复方案。
> 变更明细与验证记录见 `Changelog.md` 的 `## v0.10.0 — 2026-09-25` 条目，此处不复述。

---

## 问题一：方块删除后的死引用与孤立资源

### 现象

- `PasterDream/src/main/resources/pd_porting_manifest.json` 两处仍列出 `windmoor_leaves_0`、`windmoor_leaves_1`（`windmoor_leaves_2` 已被前一步替换为 `windmoor_hanging_vine`），清单与代码实际注册不符
- `lang/zh_cn.json`、`lang/en_us.json` 中残留 `block.pasterdream.windmoor_leaves_0`、`block.pasterdream.windmoor_leaves_1` 键，指向已不存在的方块
- 4 个资源文件失去全部引用：`blockstates/windmoor_leaves_0.json`、`blockstates/windmoor_leaves_1.json`、`models/item/windmoor_leaves_0.json`、`models/item/windmoor_leaves_1.json`

### 根因

删除方块注册项时只同步了部分引用，属"删注册漏清配套"。

两类引用在静态搜索下的可见性差别很大，是本次遗漏的主因：

- **可搜索到**：tag、manifest、lang、blockstate、loot_table 都以 `pasterdream:` 前缀的字符串形式出现在文本中
- **搜索不到**：`models/item/<id>.json` 由物品 ID 自动解析，文件与 ID 之间不存在任何文本关联，普通的 ID 全文检索必然漏检

### 修复

1. `pd_porting_manifest.json` 两处将 `windmoor_leaves_0` / `windmoor_leaves_1` 合并为 `windmoor_leaves`，并按字母序重排为 `windmoor_hanging_vine` → `windmoor_leaves` → `windmoor_log`
2. 两个语言文件删除上述两个死键
3. 删除 4 个孤立资源文件

**不可一并删除**：`models/block/windmoor_leaves_0.json`、`models/block/windmoor_leaves_1.json` 仍被 `windmoor_leaves` 的加权 `variants` 引用（用于纹理随机化），必须保留。孤立判定不能只看文件名，要看是否还有 blockstate 指向。

### 验证

`python tools/verify_resource_closure.py` PASS：3851 个 JSON、238 个方块、669 个物品，JSON / 模型 / 纹理 / 粒子 / 音效 / 注册资源 / loot 闭包全部完整。该脚本是识别此类孤儿文件的兜底手段，删除后仍通过即证明删的是真孤立文件。

---

## 问题二：`SUPPRESSED` 状态属性为死状态

### 现象

`PasterDream/.../block/WindmoorHangingVineBlock.java` 中 `SUPPRESSED`（`BooleanProperty`）被注册进方块状态定义、在 `randomTick` 中被读取（`if (state.getValue(SUPPRESSED)) return;`），但全代码库没有任何一处写入它。

### 根因

设计未闭环。该属性注释为"是否暂时抑制生长（防止链式触发）"，但：

- 类中唯一的方块状态写入点是 `tryGrowDown()`，它只推进 `AGE` 与放置 `fig_vine`，不触碰 `SUPPRESSED`
- 也没有任何清除该标志的调度逻辑

因此该属性恒为 `false`，`randomTick` 里的判断是死分支；同时方块状态空间被无意义地翻倍。

需要说明的是，**不能简单"补写入"**：若在 `tryGrowDown()` 中置 `true` 而无清除机制，生长会被永久锁死，属于引入新 bug。

### 修复

整体删除该属性，包括：字段声明、`registerDefaultState` 中的赋值、`createBlockStateDefinition` 中的注册、`randomTick` 中的判断，以及不再使用的 `BooleanProperty` 导入。

---

## 问题三：`windmoor_hanging_vine` 方块状态变体键错误

### 现象

`assets/pasterdream/blockstates/windmoor_hanging_vine.json` 使用空字符串 `""` 作为 `variants` 的键，而该方块具备 `AGE`（0~2）属性。

### 根因

`""` 仅适用于**无属性**方块。判断依据来自项目内既有资源的写法一致性：

- 无属性方块用 `""`：`fig_vine`（其 `FigVineBlock` 无 `createBlockStateDefinition`）、`windmoor_leaves_0`
- 有属性方块显式枚举：`windmoor_log`（按 `axis` 枚举）

有问题属性的方块若沿用 `""`，方块状态无法与模型变体正确匹配。

### 修复

变体键改为 `age=0` / `age=1` / `age=2` 三条显式枚举，共同指向 `pasterdream:block/windmoor_hanging_vine` 模型，写法与同目录 `windmoor_log.json` 保持一致。

---

## 非问题：可穿行变体的消失（判定为保持现状）

### 现象

原模组 `WindmoorLeaves2Block` 为 `noCollission()` 且 `getVisualShape` 返回 `Shapes.empty()` 的**隐形可穿行**方块，与 `WindmoorLeaves0Block` 等有碰撞方块功能不同。三合一后所有风泊树叶统一使用 `windmoorLeavesProps()`，该可穿行行为不再存在。这一差异无法通过 blockstate 加权变体弥补——碰撞体积是方块属性，不是模型变体属性。

### 判定依据

用 `git grep -a` 直接检索二进制 blob（含当前工作区与 HEAD 版本）：

```
git grep -a -c "windmoor_leaves_2" HEAD -- "*.nbt"   # 无命中
git grep -a -o -h "windmoor_leaves_[0-9]" HEAD -- "*.nbt" | Sort-Object -Unique   # 无输出
```

仓库内 5 个 `.nbt` 结构（`lost_windknight_ruins`、`wind_island_0`、`wind_pond_0`、`windmill_lodge`、`windmoor_tree_0`）与全部数据、世界生成文件中均未引用任何一个旧树叶 ID。**该可穿行变体在现有数据中零使用**，故不恢复该注册项，按有碰撞合并保留（与原版树叶及 `_0` / `_1` 行为一致）。

若后续需要在结构中放置可穿行的隐形填充方块，需单独重新设计一个方块，而非从树叶合并中找回。

---

## 遗留项

| 项 | 说明 |
|----|------|
| `PasterDream/tag_audit.json` | 一次性审计快照，仍列有 `windmoor_leaves_0/1/2` 的 tag 与 loot 条目，需重新生成 |
| `tools/check_chest_loot_ids.py` | 物品池为硬编码旧值，未跟随战利品表迁移改为解析 JSON；其中 `bobo_plume` 为笔误（真实 ID 为 `boboji_plume`），当前运行会误报 |
| 融梦箱战利品上下文 | 使用 `LootContextParamSets.CHEST`，不含 `BlockPos` 与玩家信息，后续若需按位置或玩家做条件判断需扩展参数集 |

## 最终验证

- `.\gradlew :PasterDream:compileJava` BUILD SUCCESSFUL（`PasterDream` / `PasterDreamAPI` 任务实际执行，无错误，仅有既存的"使用了已过时 API"编译注记）
- `python tools/verify_resource_closure.py` PASS（见问题一）
- 三个新增 chest 战利品表条目语言键逐条核对：`meltdream_common`(17) / `meltdream_rare`(16) / `meltdream_legendary`(11)，无缺失
- 本轮修改的 6 个文本文件换行符均为 LF、无 BOM；`pd_porting_manifest.json` 原为 CRLF，已统一为 LF
- 待游戏内实测：融梦箱三品质掉落分布、悬挂藤生长与骨粉催长、风之旅维度地表、粒子观感、5 个 NBT 结构变化
