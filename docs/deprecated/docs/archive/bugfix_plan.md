# PasterDream 模组 Bug 修复计划

## 📋 问题清单

| # | 问题描述                    | 严重程度 | 状态  |
| - | ----------------------- | ---- | --- |
| 1 | 染梦树叶挖掘掉落物异常，使用了错误的战利品列表 | 高    | 待修复 |
| 2 | 染梦树苗无法正常生长              | 高    | 待修复 |
| 3 | 部分工具挖掘品质错误（连铁镐品质都没有）    | 中    | 待修复 |
| 4 | 修复错误的 Tab 标签            | 低    | 待修复 |
| 5 | 注入矿物生成器（确认已实现）          | 低    | 待验证 |
| 6 | 修改染梦水晶系列方块为含水方块         | 中    | 延后  |
| 7 | 修复阴影漩涡无伤害的问题            | 高    | 待修复 |

***

## 🔧 修复方案

### 问题 1：染梦树叶挖掘掉落物异常

**当前状态**：`dyedream_leaves.json` 战利品表只在精准采集时掉落树叶

**目标**：概率掉落染梦树苗（约 1/18）和染梦果（约 2/18），支持 Fortune 附魔加成

**修改文件**：

* `PasterDream/src/main/resources/data/pasterdream/loot_table/blocks/dyedream_leaves.json`

**方案**：参考原模组的战利品表格式，添加 weighted 条目

***

### 问题 2：染梦树苗无法正常生长

**当前状态**：`DyedreamSaplingBlock` 继承 `FlowerBlock`，仅作为装饰方块，无生长逻辑

**目标**：实现随机 tick 生长机制，支持右键骨粉催熟

**修改文件**：

* `PasterDream/src/main/java/com/pasterdream/pasterdreammod/block/DyedreamSaplingBlock.java` - 重写生长逻辑

* 可能需要新建 `DyedreamSaplingBlockEntity.java` - 存储树苗生长阶段

**方案**：

1. 继承 `SaplingBlock` 而非 `FlowerBlock`
2. 实现 `growTree()` 方法生成染梦树结构
3. 添加 `randomTick()` 随机生长逻辑
4. 支持骨粉催熟

***

### 问题 3：部分工具挖掘品质错误

**当前状态**：部分工具（如铜镐、融梦镐等）的挖掘等级不足

**目标**：根据原模组配方和材料，为工具设置正确的挖掘等级

**修改文件**：

* `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDItems.java`

* `PasterDreamAPI/src/main/java/com/pasterdream/pasterdreammod/api/item/builder/ToolItemBuilder.java`

**方案**：

* 检查原模组工具属性（`libs/FixPasterDream-main/src/main/java/net/pasterdream/init/PasterdreamModItems.java`）

* 为每个工具添加正确的 `miningLevel()` 调用

**工具等级参考**：

| 工具                     | 原模组等级   | 当前状态 | 需要修改 |
| ---------------------- | ------- | ---- | ---- |
| Copper Pickaxe         | 石级 (1)  | 无    | ✅    |
| Dyedream Pickaxe       | 铁级 (2)  | 无    | ✅    |
| Meltdream Pickaxe      | 石级 (1)  | 无    | ✅    |
| Moltengold Pickaxe     | 石级 (1)  | 无    | ✅    |
| Titanium Pickaxe       | 钻石级 (3) | 无    | ✅    |
| Shadow Erosion Pickaxe | 钻石级 (3) | 无    | ✅    |

***

### 问题 4：修复错误的 Tab 标签

**当前状态**：`PDCreativeTabs.java` 中物品分类可能存在错误

**目标**：确保物品正确分配到对应的标签页

**修改文件**：

* `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDCreativeTabs.java`

**方案**：

* 检查每个物品是否在正确的标签页中

* 确保没有重复或遗漏的物品

* 按逻辑分组（武器、工具、食物等）

***

### 问题 5：注入矿物生成器

**当前状态**：`PDDyedreamBiomeModifier.java` 已实现矿物注入，但需要验证

**目标**：确认矿物生成器正常工作

**验证步骤**：

1. 检查 `data/pasterdream/worldgen/placed_feature/ore_*.json` 文件是否存在
2. 检查 `data/pasterdream/worldgen/configured_feature/ore_*.json` 文件是否存在
3. 运行游戏测试矿物是否生成

**已知存在的文件**：

* `ore_amber_candy.json` ✅

* `ore_dyedreamdust.json` ✅

* `ore_dyedreamquartz.json` ✅

***

### 问题 6：修改染梦水晶系列方块为含水方块

**延后处理**：用户表示"等一会再说"，暂不处理

***

### 问题 7：修复阴影漩涡无伤害的问题

**当前状态**：`ShadowVortexBlockEntity.tick()` 仅处理生命周期，无伤害逻辑

**目标**：漩涡在存在期间对附近生物造成伤害

**修改文件**：

* `PasterDream/src/main/java/com/pasterdream/pasterdreammod/block/entity/ShadowVortexBlockEntity.java`

**方案**：

1. 在 `tick()` 方法中添加 AABB 检测附近实体
2. 对范围内的生物（排除创造者模式玩家）施加伤害
3. 使用自定义伤害源（如 `DamageSource.MAGIC` 或自定义）

***

## 📁 文件修改清单

| 文件                                | 操作 | 描述                     |
| --------------------------------- | -- | ---------------------- |
| `dyedream_leaves.json`            | 修改 | 替换战利品表，添加概率掉落树苗和染梦果    |
| `DyedreamSaplingBlock.java`       | 重写 | 继承 SaplingBlock，实现生长逻辑 |
| `DyedreamSaplingBlockEntity.java` | 新建 | 树苗方块实体（如果需要）           |
| `PDItems.java`                    | 修改 | 添加 miningLevel 到各工具    |
| `ToolItemBuilder.java`            | 修改 | 支持 miningLevel 设置      |
| `PDCreativeTabs.java`             | 修改 | 修复物品分类错误               |
| `ShadowVortexBlockEntity.java`    | 修改 | 添加伤害逻辑                 |

***

## ⚠️ 风险与注意事项

1. **树苗生长逻辑**：需要确保生成的树木结构与原模组一致
2. **工具等级**：修改挖掘等级可能影响游戏平衡，需参考原模组数据
3. **阴影漩涡伤害**：需避免对玩家造成不合理伤害，添加伤害范围限制
4. **矿物生成验证**：需要在游戏中实际测试才能确认是否生效

***

## ✅ 验证标准

1. 染梦树叶：挖掘时概率掉落树苗和染梦果，精准采集掉落树叶
2. 染梦树苗：随机 tick 生长，骨粉催熟有效
3. 工具：能够正确挖掘对应等级的矿石
4. Tab 标签：物品分类正确，无重复遗漏
5. 阴影漩涡：对附近生物造成持续伤害

