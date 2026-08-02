# NeoPasterDream1 重构修复计划（2026-07-18）

> 目标：在 `./gradlew compileJava` 每步必过的约束下，逐项完成重构审查报告中的剩余修复。

---

## 1. Summary（摘要）

当前项目已完成 P0/P1 级重构修复（随机源优化、异常细化、废弃 API 清理、资源目录规范化、拼音资源重命名、装饰 API 迁移等）。
剩余未完成任务集中在 **代码结构整理**、**文档补全**、**随机源现代化** 和 **公共基类抽取** 四个方向。本计划将这些任务按风险从低到高排序，每完成一步立即执行编译验证，确保项目始终可构建。

---

## 2. Current State Analysis（当前状态）

基于 2026-07-18 的代码扫描与文件读取，剩余任务状态如下：

| # | 任务 | 状态 | 关键文件 | 风险等级 |
|---|------|------|----------|----------|
| 1 | 移除 `PDDimensions` 中对已废弃 `withMusic()` 的调用 | 未完成 | `PasterDream/src/main/java/.../registry/PDDimensions.java:50` | 低 |
| 2 | 为公共 API 类补全 Javadoc | 未完成 | `PasterDreamAPI/.../api/dimension/terrain/TerrainRequirements.java`、`TerrainAssessment.java`、`builder/DimensionBuilder.java` 等 | 低 |
| 3 | 将 `DyedreamChunkGenerator` 中的 `java.util.Random` 替换为 `RandomSource` | 未完成 | `PasterDream/src/main/java/.../worldgen/chunkgen/DyedreamChunkGenerator.java:282, 492` | 中 |
| 4 | 抽取 `AaroncosHandEntity` 公共基类 | 未完成 | `PasterDream/src/main/java/.../entity/mob/AaroncosLefthand0Entity.java`、`AaroncosRighthand0Entity.java` | 中 |
| 5 | 引入 `check_lang.py` 自动校验语言文件 | 未完成 | 新建 `.trae/tools/check_lang.py` | 低 |
| 6 | 拆分巨型注册类 `PDItems`、`PDBlocks`、`PDCreativeTabs` | 未完成 | 上述三个文件均超过 1000 行 | 高 |

---

## 3. Proposed Changes（分步实施方案）

### Step 1：修复 `PDDimensions` 的废弃 `withMusic()` 调用

**目标**：消除对已废弃 `DimensionBuilder#withMusic()` 的依赖，避免编译警告/未来移除时崩溃。

**修改文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDDimensions.java`

**修改内容**：
- 删除第 50 行的 `.withMusic("dyedream_world")` 链式调用。

**验证**：
- 执行 `./gradlew compileJava`，确保编译通过且无 `withMusic` 相关弃用警告。

---

### Step 2：补全公共 API 类的 Javadoc 注释

**目标**：提升 API 模块可读性与可维护性，满足项目注释规范。

**修改文件**：
- `PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/dimension/terrain/TerrainRequirements.java`
- `PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/dimension/terrain/TerrainAssessment.java`
- `PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/dimension/builder/DimensionBuilder.java`

**修改内容**：
- 为 `TerrainRequirements` 的所有字段、getter、builder 方法添加参数/返回值说明。
- 为 `TerrainAssessment` 的 `Status` 枚举值、builder 方法、静态工厂方法补全注释。
- 为 `DimensionBuilder` 中尚未注释的字段和便捷方法（如 `ultraWarm()`、`piglinSafe()` 等）补充说明。

**验证**：
- 执行 `./gradlew compileJava`，仅注释变更不应影响编译。

---

### Step 3：将 `DyedreamChunkGenerator` 中的 `java.util.Random` 替换为 `RandomSource`

**目标**：统一使用 Minecraft 世界生成随机源，避免世界种子不一致和 `java.util.Random` 的线程/序列化隐患。

**修改文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/worldgen/chunkgen/DyedreamChunkGenerator.java`

**修改内容**：
- 删除 `import java.util.Random;`。
- 在 `carveRivers(...)` 中，将 `new Random(chunkPos.x * 3129871L ^ chunkPos.z * 116129781L)` 替换为基于 `RandomSource` 的随机源（如 `RandomSource.create(chunkPos.x * 3129871L ^ chunkPos.z * 116129781L)`）。
- 在 `restoreRiverWater(...)` 中，将 `new Random(...)` 同样替换为 `RandomSource.create(...)`。
- 更新 `nextInt(int)` / `nextFloat()` 调用为 `RandomSource` 对应 API（签名一致）。

**验证**：
- 执行 `./gradlew compileJava`，确保 `RandomSource` 的导入与 API 调用正确。

---

### Step 4：引入 `check_lang.py` 自动校验语言文件

**目标**：在新增注册项后能快速发现 `zh_cn.json` 中缺失的翻译键。

**修改文件**：
- 新建 `.trae/tools/check_lang.py`

**脚本功能**：
1. 扫描 `PDItems.java`、`PDBlocks.java` 中所有注册名。
2. 读取 `PasterDream/src/main/resources/assets/pasterdream/lang/zh_cn.json`。
3. 检查是否包含 `block.pasterdream.<name>` 和 `item.pasterdream.<name>` 键（物品形态可 fallback 到方块翻译，但仍建议补全）。
4. 输出缺失条目列表。

**验证**：
- 运行 `python .trae/tools/check_lang.py`，确认脚本能正常输出。
- 执行 `./gradlew compileJava`，脚本本身不影响编译。

---

### Step 5：抽取 `AaroncosHandEntity` 公共基类

**目标**：消除 `AaroncosLefthand0Entity` 与 `AaroncosRighthand0Entity` 的重复代码。

**修改文件**：
- 新建 `PasterDream/src/main/java/com/pasterdream/pasterdreammod/entity/mob/AaroncosHandEntity.java`
- 修改 `AaroncosLefthand0Entity.java`
- 修改 `AaroncosRighthand0Entity.java`

**实现策略**：
- 将左右手实体中完全相同的 AI、属性、碰撞箱、注册逻辑迁移到 `AaroncosHandEntity`。
- 左右手实体继承该基类，仅保留与左右手差异相关的代码（如模型/渲染偏移、特定技能判定）。
- 保持原有注册名和生成蛋颜色不变。

**验证**：
- 执行 `./gradlew compileJava`，确保子类覆写与基类可见性正确。

---

### Step 6：拆分巨型注册类

**目标**：降低 `PDItems.java`（约 1400+ 行）、`PDBlocks.java`（约 1000+ 行）、`PDCreativeTabs.java`（约 1000+ 行）的维护难度。

**方案**：采用「分文件注册 + 原类聚合引用」策略，保持外部调用方式不变（`PDItems.XXX` 仍可访问）。

#### 6.1 `PDItems` 拆分

**新建文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsBlocks.java` — 方块物品（`BlockItem`）。
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsDecorations.java` — 玩偶/雕像/装饰物品。
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsMaterials.java` — 原材料/掉落物。
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsFood.java` — 食物/消耗品。
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsTools.java` — 工具/武器/护甲。
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsCurios.java` — Curio 饰品。
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/PDItemsMisc.java` — 刷怪蛋/唱片/调试法杖/杂项。

**修改 `PDItems.java`**：
- 保留 `public static final DeferredRegister.Items ITEMS = ...`。
- 将具体注册语句迁移到上述新文件。
- `PDItems.java` 中通过 `public static final DeferredItem<...> XXX = PDItemsBlocks.XXX;` 等方式聚合导出，确保外部引用不变。

#### 6.2 `PDBlocks` 拆分

**新建文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/blocks/PDBlocksBuilding.java` — 建筑方块。
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/blocks/PDBlocksNature.java` — 自然/植物方块。
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/blocks/PDBlocksFunctional.java` — 功能方块（箱子、门、炼药锅等）。
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/blocks/PDBlocksDecoration.java` — 装饰方块（玩偶、雕像等）。

**修改 `PDBlocks.java`**：
- 保留 `public static final DeferredRegister.Blocks BLOCKS = BlockAPI.REGISTRY;`。
- 通过聚合引用保持 `PDBlocks.XXX` 可用。

#### 6.3 `PDCreativeTabs` 拆分

**新建文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/tabs/PDCreativeTabDefinitions.java` — 每个标签页的定义。

**修改 `PDCreativeTabs.java`**：
- 保留 `public static final DeferredRegister<CreativeModeTab> TABS = ...`。
- 将各个 `TABS.register(...)` 迁移到 `PDCreativeTabDefinitions`。
- `PDCreativeTabs.java` 通过聚合引用保持 `PDCreativeTabs.ENTITY_TAB` 等常量可用。

**验证**：
- 每完成一个类的拆分即执行 `./gradlew compileJava`。
- 最终确认所有外部对 `PDItems.XXX`、`PDBlocks.XXX`、`PDCreativeTabs.XXX` 的引用仍然有效。

---

## 4. Assumptions & Decisions（假设与决策）

1. **保持向后兼容**：拆分注册类时，不改变外部调用方式（仍通过 `PDItems.XXX` 访问），以降低风险。
2. **分文件仍使用统一注册器**：新文件中的注册语句继续使用 `PDItems.ITEMS`、`PDBlocks.BLOCKS`，避免改动 `PasterDreamMod` 构造函数。
3. **不修改注册名、语言键、模型路径**：仅调整代码组织形式，不影响游戏内资源。
4. **编译为唯一验证门槛**：每步修改后必须 `./gradlew compileJava` 通过；若失败，立即回退或就地修复，不进入下一步。
5. **Aaroncos 手部实体**：假设左右手差异主要在渲染/模型偏移，公共逻辑可安全抽取到基类。

---

## 5. Verification Steps（全局验证清单）

- [ ] Step 1 后：`./gradlew compileJava` 通过，`PDDimensions` 无 `withMusic` 调用。
- [ ] Step 2 后：`./gradlew compileJava` 通过，API 类关键方法均补全 Javadoc。
- [ ] Step 3 后：`./gradlew compileJava` 通过，`DyedreamChunkGenerator` 无 `java.util.Random` 引用。
- [ ] Step 4 后：`python .trae/tools/check_lang.py` 可正常运行并输出缺失翻译键。
- [ ] Step 5 后：`./gradlew compileJava` 通过，左右手实体继承 `AaroncosHandEntity`。
- [ ] Step 6 后：`./gradlew compileJava` 通过，原注册类外部引用不变。
- [ ] 全部完成后：执行一次完整的 `./gradlew compileJava`，确认无新增编译错误与警告。

---

## 6. Rollback Strategy（回滚策略）

- 每步修改前，若涉及文件移动或大量替换，先通过 git 创建临时检查点（不提交到远程，仅本地标签）。
- 若某步编译失败且 10 分钟内无法修复，回退该步变更，记录阻塞原因，进入下一可独立完成的任务。
- 巨型类拆分过程中若引用链断裂，优先保证 `PDItems.java`、`PDBlocks.java`、`PDCreativeTabs.java` 能单独编译通过，再逐步迁移分组。
