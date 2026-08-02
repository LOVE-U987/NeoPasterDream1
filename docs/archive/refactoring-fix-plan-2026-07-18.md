# NeoPasterDream1 全面重构修复计划

> 目标：按重构审查报告逐项修复 P0/P1/P2/P3 全部问题，每完成一步执行一次 `./gradlew compileJava` 确保编译通过。
> 基准：当前 `./gradlew compileJava` 已通过，无编译错误。

---

## 一、执行策略

- **每步编译**：每次文件修改后执行 `./gradlew compileJava`， green 才进入下一步。
- **先止血后整容**：先处理 P0/P1 功能正确性风险，再处理 P2 结构拆分，最后处理 P3 文档/债务。
- **最小破坏**：保持所有公共 API 签名、注册名、资源路径语义不变，仅物理移动或内部重构。
- **先完成当前在制品**：Aaroncos 左右手基类迁移是当前未完成的重构，优先收尾。

---

## 二、阶段一：当前在制品收尾（Step 1-3）

### Step 1：完成 AaroncosHandEntity 基类迁移

**目标**：让 `AaroncosLefthand0Entity` 和 `AaroncosRighthand0Entity` 继承 `AaroncosHandEntity`，删除重复代码。

**修改文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/entity/mob/AaroncosLefthand0Entity.java`
  - 改为 `extends AaroncosHandEntity`
  - 删除 `ConfigurableImmunityEntity` import
  - 删除重复字段：`SHADOW_MOB_TAG`、`swinging`、`lastSwing`、`DelayedTask`、`pendingTasks`、`serverTickCounter`、`skillSwitchInitialized`、`isSummoning`
  - 删除重复方法：`setSummoning`、`isSummoning`、`registerGoals`（保留 `createAttributes` 差异）、`removeWhenFarAway`、`causeFallDamage`、`hurt`、`onAddedToLevel`、`tickDeath`、`baseTick`、`aiStep`、`queueTask`、`processPendingTasks`、`tryBloodLock`、`createNavigation`、`checkFallDamage`、`setNoGravity`、`isPersistenceRequired`、`hurtNearbyPlayers`、`hurtNearbyLivingWithConfusion`、`pushNearbyPlayers`、`movementPredicate`、`attackingPredicate`、`registerControllers`、`FlyingPursuitGoal`
  - 实现抽象方法：
    - `getHandName()` → `"AaroncosLefthand0"`
    - `getSpawnAnimationTicks()` → `80`
    - `onSpawnAnimationComplete(ServerLevel)` → 调用 `PDArenaBossManager.onSpawnAnimationComplete(serverLevel)`（已在竞技场维度时）
    - `onHandDeath(ServerLevel)` → 调用 `PDArenaBossManager.onLeftHandDeath(serverLevel)`
    - `onHurtTriggerSkill()` → 调用 `triggerSwordSkill()`
    - `saveHandSpecificData(CompoundTag)` → 保存 `AaroncosSprint`、`AaroncosHit`、`AaroncosSword`
    - `readHandSpecificData(CompoundTag)` → 读取上述字段
    - `getMeleeAttackSpeed()` → `1.2`
    - `tickSkillCycle()` → 保留左手技能循环逻辑
  - 保留 `createAttributes()`、左手专属技能方法（`tickSkillCycle`、`executeSprintSkill`、`executeHitSkill`、`triggerSwordSkill`）和类级 Javadoc。
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/entity/mob/AaroncosRighthand0Entity.java`
  - 类似上述操作，右手实现：
    - `getHandName()` → `"AaroncosRighthand0"`
    - `getSpawnAnimationTicks()` → `40`
    - `onSpawnAnimationComplete(ServerLevel)` → 调用 `PDArenaBossManager.onSpawnAnimationComplete(serverLevel)`
    - `onHandDeath(ServerLevel)` → 调用 `PDArenaBossManager.onRightHandDeath(serverLevel)`
    - `onHurtTriggerSkill()` → 调用 `triggerTuneTotemSkill()`
    - `saveHandSpecificData(CompoundTag)` → 保存 `AaroncosMagicball`、`AaroncosVortex`、`AaroncosTuneTotem`
    - `readHandSpecificData(CompoundTag)` → 读取上述字段
    - `getMeleeAttackSpeed()` → `1.0`
    - `tickSkillCycle()` → 保留右手技能循环逻辑
  - 保留右手专属技能方法：`executeMagicballSkill`、`executeVortexSkill`、`triggerTuneTotemSkill`。
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/entity/mob/AaroncosHandEntity.java`
  - 检查并补全文件末尾缺失的右花括号（当前 `Read` 显示文件在第 582 行后截断，需确认完整性）。
  - 若 `AaroncosHandEntity` 未实现 `getDefaultTexture()` 的抽象，确认子类已覆盖。

**验证**：`./gradlew compileJava`

---

### Step 2：修复资源目录新旧并存（P0）

**目标**：消除 `structures/`、`entity_types/`、`textures/entities/` 三个复数目录，将独有文件迁移到正确目录后删除复数目录。

**操作**：
1. 对比 `data/pasterdream/structure/` 与 `data/pasterdream/structures/`，将 `structures/` 下独有的 7 个文件移动到 `structure/`。
2. 对比 `data/pasterdream/tags/entity_type/` 与 `data/pasterdream/tags/entity_types/`，将 `entity_types/` 下独有的 1 个文件移动到 `entity_type/`。
3. 对比 `assets/pasterdream/textures/entity/` 与 `assets/pasterdream/textures/entities/`，将 `entities/` 下独有的 19 个文件移动到 `entity/`。
4. 删除三个复数目录：`structures/`、`entity_types/`、`entities/`。

**验证**：
- 文件系统检查：复数目录不存在。
- `./gradlew compileJava`

---

### Step 3：修复 DyedreamBudBlock 磨制方解石检测 Bug（P0）

**目标**：让 `isCalciteOrPolishedCalcite` 名副其实。

**修改文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/block/DyedreamBudBlock.java`
  - 找到 `isCalciteOrPolishedCalcite(BlockState state)` 方法。
  - 将 `return state.is(Blocks.CALCITE);` 改为 `return state.is(Blocks.CALCITE) || state.is(PDBlocks.POLISHED_CALCITE.get());`。
  - 若 `PDBlocks.POLISHED_CALCITE` 不存在，改为字符串标签或确认对应注册名。

**验证**：`./gradlew compileJava`

---

## 三、阶段二：P1 止血与代码质量（Step 4-8）

### Step 4：替换 PDEffects 中高频 RandomSource.create()

**目标**：效果 tick 使用实体自身随机源，保证可复现并降低 GC。

**修改文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDEffects.java`
  - `EXPUP_BUFF.onTick` 中：`Mth.nextInt(RandomSource.create(), 1, 1000)` 改为 `Mth.nextInt(entity.getRandom(), 1, 1000)`。
  - 添加命名常量：
    - `private static final int EXPUP_CHANCE = 10;`
    - `private static final int EXPUP_DENOMINATOR = 1000;`
  - 使用常量替换硬编码 `10` 和 `1000`。

**验证**：`./gradlew compileJava`

---

### Step 5：修复 BoneWingFireBallProjectileEntity 和 AaroncosArenaPortalsBlock 的 RandomSource.create()

**目标**：将这两个文件中 `RandomSource.create()` 替换为上下文随机源。

**修改文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/entity/projectile/BoneWingFireBallProjectileEntity.java`
  - 将 `RandomSource.create()` 替换为 `this.level().getRandom()` 或 `this.random`（根据可用字段）。
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/block/AaroncosArenaPortalsBlock.java`
  - 将 `RandomSource.create()` 替换为 `level.getRandom()` 或 `random`（方法参数中通常已有 `RandomSource random`）。

**验证**：`./gradlew compileJava`

---

### Step 6：处理 PasterDreamMod 中脆弱的类初始化模式

**目标**：移除 `Class.forName(...)` + 空 catch，改为显式引用常量触发静态初始化。

**修改文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/PasterDreamMod.java`
  - 搜索所有 `Class.forName(...)` 调用，确认当前代码中是否仍然存在（根据上次读取结果，当前已改为显式引用常量，但需再次确认）。
  - 如果仍存在，改为显式引用一个静态字段，例如 `Object unusedXxx = XxxClass.REGISTRY;`。
  - 删除任何空的 `catch (ClassNotFoundException ignored) {}` 块。
  - 如果已经清理完毕，记录为「已完成」。

**验证**：`./gradlew compileJava`

---

### Step 7：迁移或清理根目录 `模型动画/` 源资源

**目标**：将根目录下的源资源文件移出版本控制根目录，避免仓库污染。

**操作**：
1. 检查 `c:/Users/97128/Documents/GitHub/NeoPasterDream1/模型动画/` 内容。
2. 若内容为可复用的 Blockbench 源文件/纹理源文件，移动到 `art/` 或 `assets-src/` 目录。
3. 若内容已过时或已在 resources 中存在对应产物，直接删除。
4. 更新 `.gitignore`，忽略 `art/` 或 `assets-src/`（如果决定保留在仓库中则无需忽略）。

**验证**：`./gradlew compileJava`（此步骤通常不影响编译，但仍执行一遍）

---

### Step 8：DyedreamChunkGenerator 改用 RandomSource

**目标**：将 `java.util.Random` 替换为 Minecraft 的 `RandomSource`。

**修改文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/worldgen/chunkgen/DyedreamChunkGenerator.java`
  - 找到所有 `new Random(...)` 或 `java.util.Random` 的使用。
  - 替换为 `RandomSource.create(seed)` 或从 `WorldGenLevel` 获取随机源。
  - 更新 import。

**验证**：`./gradlew compileJava`

---

## 四、阶段三：P2 结构拆分（Step 9-11）

### Step 9：拆分 PDItems

**目标**：将 2040 行的 `PDItems.java` 按主题拆分为多个子文件，保持外部引用不变。

**策略**：采用「分文件注册 + 原类聚合引用」模式。

**新增文件**（位于 `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/items/`）：
- `PDItemsBlocks.java`：BlockItem 注册（如 `DREAM_ACCUMULATOR`、`DYEDREAM_DESK` 等）。
- `PDItemsDolls.java`：玩偶/雕像物品。
- `PDItemsMaterials.java`：原材料/杂物。
- `PDItemsTools.java`：工具/武器。
- `PDItemsFoods.java`：食物/消耗品。
- `PDItemsCurios.java`：Curio 饰品。
- `PDItemsSpawnEggs.java`：刷怪蛋。
- `PDItemsMisc.java`：其他无法归类项。

**修改文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDItems.java`
  - 删除所有具体注册项。
  - 保留 `public static final DeferredRegister.Items ITEMS = ...`。
  - 通过 `public static final DeferredItem<X> XXX = PDItemsXxx.XXX;` 聚合暴露所有常量。
  - 调整 import。

**验证**：`./gradlew compileJava`

---

### Step 10：拆分 PDBlocks

**目标**：将 1458 行的 `PDBlocks.java` 按主题拆分。

**新增文件**（位于 `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/blocks/`）：
- `PDBlocksDimension.java`：染梦维度相关方块。
- `PDBlocksDecoration.java`：装饰方块。
- `PDBlocksFunctional.java`：功能方块（如生命水晶、蓄梦池等）。
- `PDBlocksBuilding.java`：建筑材料。
- `PDBlocksPlants.java`：植物/自然方块。
- `PDBlocksMisc.java`：其他。

**修改文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDBlocks.java`
  - 保留 `DeferredRegister.Blocks BLOCKS`。
  - 聚合引用各子文件中的注册常量。

**验证**：`./gradlew compileJava`

---

### Step 11：拆分 PDCreativeTabs

**目标**：将 891 行的 `PDCreativeTabs.java` 按标签页拆分。

**新增文件**（位于 `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/tabs/`）：
- `PDCombatTab.java`
- `PDBuildingBlocksTab.java`
- `PDNaturalBlocksTab.java`
- `PDSouvenirTab.java`
- `PDMaterialsTab.java`
- `PDFoodsTab.java`
- `PDMiscTab.java`

**修改文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDCreativeTabs.java`
  - 保留 `DeferredRegister<CreativeModeTab> TABS`。
  - 聚合引用各标签页常量。

**验证**：`./gradlew compileJava`

---

## 五、阶段四：P3 债务清理与文档（Step 12-17）

### Step 12：清理自造 @Deprecated(forRemoval = true) API

**目标**：扫描并迁移/删除 ParticleAPI、EntityResult、DimensionAPI 中标记为废弃的内部 API。

**操作**：
1. 搜索这些类中的 `@Deprecated(forRemoval = true)` 方法。
2. 搜索主模块中的调用点。
3. 迁移调用点到新 API。
4. 删除已无调用的废弃方法。

**验证**：`./gradlew compileJava`

---

### Step 13：为 TerrainRequirements / TerrainAssessment / DimensionBuilder 补全 Javadoc

**目标**：公共 API 的链式 setter/getter 补全方法级注释。

**修改文件**：
- `PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/dimension/terrain/TerrainRequirements.java`
- `PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/dimension/terrain/TerrainAssessment.java`
- `PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/dimension/builder/DimensionBuilder.java`

**操作**：
- 对每个 public 方法添加类级/方法级 Javadoc。
- 简单 getter 至少写 `@return ...`。

**验证**：`./gradlew compileJava`

---

### Step 14：清理注释掉的死代码

**目标**：删除 `PDCreativeTabs.java`、`FireflyEntity.java`、`PasterDreamMod.java` 中的注释掉代码。

**修改文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDCreativeTabs.java`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/entity/mob/FireflyEntity.java`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/PasterDreamMod.java`

**操作**：
- 删除长期注释掉的代码块。
- 若代码有价值，迁移到 TODO 文件或 issue 中。

**验证**：`./gradlew compileJava`

---

### Step 15：隔离 FastNoise.java 到 thirdparty 子包

**目标**：将外部库 FastNoise.java 放入独立子包并标注来源。

**操作**：
1. 创建 `PasterDream/src/main/java/com/pasterdream/pasterdreammod/util/thirdparty/`。
2. 将 `FastNoise.java` 移动到该包。
3. 更新所有引用 `FastNoise` 的 import。
4. 在文件头添加来源与许可证注释。

**验证**：`./gradlew compileJava`

---

### Step 16：MeltdreamChestBlockEntity 冷却表过期清理

**目标**：防止 `playerCooldowns` 无限增长。

**修改文件**：
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/block/entity/MeltdreamChestBlockEntity.java`
  - 在打开箱子或 tick 时遍历 `playerCooldowns`，删除已过期条目。
  - 或改用 Guava `CacheBuilder`（若项目已依赖 Guava）。

**验证**：`./gradlew compileJava`

---

### Step 17：边界层 catch (Exception) 细化

**目标**：将 `ItemManager.java`、`PDArenaEvents.java`、`PDCommands.java` 中的 `catch (Exception e)` 细化为具体异常。

**修改文件**：
- `PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/item/manager/ItemManager.java`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDArenaEvents.java`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/command/PDCommands.java`

**操作**：
- 分析每个 try 块可能抛出的具体异常。
- 改为捕获 `IOException`、`CompletionException`、`IllegalArgumentException` 等。
- 保留 `Exception` 作为兜底的地方添加注释说明。

**验证**：`./gradlew compileJava`

---

## 六、阶段五：最终验证与工具集成（Step 18-19）

### Step 18：集成 check_lang.py 到构建流程或作为提交前检查

**目标**：确保新增注册项后语言文件完整性可自动校验。

**操作**：
1. 确认 `.trae/tools/check_lang.py` 可正常运行。
2. 可选择：在 `build.gradle` 中添加一个 Gradle task 调用 `check_lang.py`。
3. 或至少在每次大改动后手动运行。
4. 运行 `check_lang.py` 确认当前无缺失。

**验证**：`python .trae/tools/check_lang.py` + `./gradlew compileJava`

---

### Step 19：最终完整验证

**操作**：
1. 执行 `./gradlew clean compileJava`。
2. 执行 `./gradlew runData`（若时间允许）。
3. 运行 `.trae/tools/check_lang.py`。
4. 检查重构报告中的所有问题是否已处理：
   - [ ] 数据目录新旧并存
   - [ ] DyedreamBudBlock 磨制方解石检测
   - [ ] Class.forName + 空 catch
   - [ ] 高频 RandomSource.create()
   - [ ] 根目录源资源
   - [ ] 拆分 PDItems/PDBlocks/PDCreativeTabs
   - [ ] 清理 @Deprecated API
   - [ ] DyedreamChunkGenerator 改用 RandomSource
   - [ ] 补全公共 API Javadoc
   - [ ] MeltdreamChestBlockEntity 冷却清理
   - [ ] 细化边界层 catch
   - [ ] Aaroncos 左右手基类抽取

---

## 七、风险与回退

- **高风险步骤**：Step 9-11 的巨型类拆分，涉及大量 import 和引用变更。
- **回退策略**：每步都基于上一步成功编译的基础上进行，若某步失败则回退该步修改。
- **并行