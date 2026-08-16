# Issue #11 诊断报告

> 生成日期: 2026-08-16
> 状态: 所有问题已完成根因分析

---

## #1 染梦睡莲水下渲染异常

### 涉及文件

- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/block/DyedreamLilyPadBlock.java`
- `PasterDream/src/main/resources/assets/pasterdream/models/custom/dyedream_lily_pad.json`
- `PasterDream/src/main/resources/assets/pasterdream/models/block/dyedream_lily_pad.json`

### 子问题分析

| 子问题 | 根因 | 对比原版 |
|--------|------|---------|
| **水下不渲染莲叶** | Blockbench 模型零厚度平面 (`from y=0, to y=0`)，`cutout` 渲染层在水下深度排序异常 | 原版 `box(1,0,1,15,1.5,15)` 有 1.5px 厚度 |
| **无法立即破坏** | `strength(0.5f)` 而非 `instabreak()`，需约 0.75 秒挖掘 | 原版 `instabreak()` (0 秒) |
| **船被阻挡** | `getCollisionShape()` 返回实际碰撞箱，原版返回 `Shapes.empty()` | 原版 `noCollission()` 无碰撞 |

### 修复方向

- **碰撞/破坏**: 在 Properties 中添加 `.noCollission().instabreak()` 即可对齐原版行为
- **水下渲染**: 将模型厚度从 0 改为 1-2 像素 (`from y=0, to y=1`)，或覆写 `getRenderShape` 返回 `INVISIBLE` 并使用 TESR 渲染

---

## #2 染梦树叶无法自然腐烂

### 涉及文件

- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/block/DyedreamLeavesBlock.java:63-66`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/block/DyedreamGlowingLeavesBlock.java:47-50`

### 根因

`isRandomlyTicking()` 硬编码返回 `false`，阻断了原版 `LeavesBlock` 的腐烂逻辑。注释明确写着"不自然消失"——**这是有意设计**。

```java
@Override
public boolean isRandomlyTicking(BlockState state) {
    return false;  // ← 根本原因
}
```

### 修复方向

如果要恢复腐烂，改为 `return !state.getValue(PERSISTENT);` 并确保构造函数调用 `.randomTicks()`。需确认设计意图。

---

## #3 染梦晶芽掉落数量一致 + 无获取方式

### 涉及文件

- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/block/DyedreamBudBlock.java:195-199`
- `PasterDream/src/main/resources/data/pasterdream/loot_table/blocks/dyedream_bud_0.json`
- `PasterDream/src/main/resources/data/pasterdream/loot_table/blocks/dyedream_bud_1.json`
- `PasterDream/src/main/resources/data/pasterdream/loot_table/blocks/dyedream_bud_2.json`

### 根因

`getDrops()` 硬编码返回 `List.of(new ItemStack(DYEDREAM_BUD_NUGGET))`，完全无视 `budSize` 字段，且无精准采集检测。

```java
@Override
public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
    return List.of(new ItemStack(PDItemsMaterials.DYEDREAM_BUD_NUGGET.get()));
}
```

| 问题 | 原因 |
|------|------|
| 三个战利品表 JSON 从未被调用 | `getDrops()` override 绕过了战利品表 |
| 掉落数量相同 | 未根据 `budSize` 计算数量 |
| 无精准采集行为 | 未检查附魔 |
| EMI 查不到获取方式 | 获取途径确实只有挖晶芽一种，且仅掉 1 个 |

### 修复方向

根据 `budSize` 返回不同数量（size 0 = 1-2, size 1 = 2-3, size 2 = 3-4），增加精准采集检测掉本体。

---

## #4 星空枕连线无判定 / 羽星占卜无天体校验

### 涉及文件

- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/item/SkyLinkItem.java:76-139`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/item/DivinationItem.java:88-96`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/client/sky/SkyboxRenderer.java:205-239`

### 星空枕 (SkyLinkItem)

**根因**: `handleSkyLink()` 仅检查夜晚 + 视线 Y > -0.35，**完全没有调用** `SkyboxRenderer.isCelestialTargeted()` 校验视线是否对准天体。玩家可在天空任意位置创建星体。

### 羽星占卜 (DivinationItem)

**根因**: 客户端正确调用了 `isAimingAtCelestial()`，但**服务端 `use()` 方法仅校验夜晚 + 抬头**，未独立验证天体对准。客户端校验可被绕过。

### 修复方向

- 星空枕: 在创建星体前调用 `isCelestialTargeted()`
- 占卜图录: 通过网络包传递客户端校验结果供服务端二次验证

---

## #5 啵啵鸡华丽飞羽图标丢失

### 涉及文件

- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDEffects.java:347-353`
- `libs/FixPasterDream-main/src/main/resources/assets/pasterdream/textures/mob_effect/boboji_buff.png` (原模组)

### 根因

`boboji_buff.png` 纹理文件未从原模组复制到新项目。

| 检查项 | 状态 |
|--------|------|
| 效果注册 `boboji_buff` | 正常 |
| 语言文件 | 正常 |
| 客户端 HUD 隐藏 | 正常 (`HIDE_GUI_ONLY`) |
| `textures/mob_effect/boboji_buff.png` | **缺失** |

### 修复方向

从 `libs/FixPasterDream-main/.../textures/mob_effect/boboji_buff.png` 复制到 `PasterDream/.../textures/mob_effect/boboji_buff.png`。

---

## #6 染梦冻洋冰柱子融化

### 涉及文件

- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/IceDecorations.java`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/OceanDecorations.java`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/blocks/PDBlocksSimple.java:42-43`

### 根因

多个冰装饰物中使用了 `minecraft:ice` 方块，该方块在光照 ≥ 11 时会融化为水。

受影响的装饰物及 `minecraft:ice` 权重占比:

| 装饰物 | ICE 占比 | 融化风险 |
|--------|:--------:|:--------:|
| `ice_spike` | **50%** | 高 |
| `ice_crystal_spike` | **50%** | 高 |
| `ice_gate` | **35%** | 高 |
| `ice_pillar` | 30% | 中 |
| `underwater_ice_spike` | 50% | 中 |
| `sea_ice_mound` | 20% | 低 |

`DYEDREAM_ICE` 基于 `Blocks.ICE` 属性复制，同样会融化。

### 修复方向

将所有冰装饰物中的 `Blocks.ICE` 替换为 `Blocks.PACKED_ICE`（或创建不融化自定义冰方块）。

---

## #8 水晶球结构水填充问题

### 涉及文件

- `PasterDream/src/main/resources/data/pasterdream/structure/crystal_ball_0.nbt`
- `PasterDream/src/main/resources/data/pasterdream/structure/crystal_ball_1.nbt`
- `PasterDream/src/main/resources/data/pasterdream/worldgen/template_pool/crystal_ball_0.json`
- `PasterDream/src/main/resources/data/pasterdream/worldgen/template_pool/crystal_ball_1.json`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/block/PDStructureBlock.java:109-112, 284-289`

### 根因

NBT 模板中 **884 个 `minecraft:air` 方块** (32.5%)，放置时覆盖水面，导致边缘处无水。模板池 `projection: "rigid"` + `terrain_adaptation: "none"`，不适应地形。

### 修复方向

修改 NBT 模板边缘与水面交汇处的 air → water，或改用 `terrain_matching` 投影。

---

## #9 树木悬空 + 落叶在水中

### 涉及文件

- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/worldgen/tree/decorator/DyedreamFallenLeavesDecorator.java:87-95`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/block/DyedreamFallenLeavesBlock.java`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/worldgen/tree/decorator/DyedreamRootDecorator.java:74-82`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/worldgen/tree/trunk/DyedreamStraightTrunkPlacer.java`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/worldgen/tree/trunk/DyedreamBranchingTrunkPlacer.java`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/worldgen/tree/trunk/DyedreamMegaTrunkPlacer.java`

### 落叶在水中

**根因**: `DyedreamFallenLeavesDecorator.java:87-95` 的 `findGround()` 用 `!BlockState::isAir` 判断地面，**水方块被当作地面**。`DyedreamFallenLeavesBlock` 缺少 `updateShape` 重写，放置后不会因邻居更新自我移除。

### 树木悬空

**根因**: 所有 Trunk Placer 的 `placeTrunk()` 从树基向上放置原木，**不检查路径上是否有地形支撑**。斜坡/悬崖边缘生成时树干中间段可能悬空。

### 修复方向

- 落叶: `findGround()` 增加 `|| state.getFluidState().isEmpty()` 排除水；`DyedreamFallenLeavesBlock` 添加 `updateShape` 覆写
- 悬空: 在 Trunk Placer 中增加向下填充逻辑

---

## #12 染梦维度洞穴永久亮

### 涉及文件

- `PasterDream/src/main/resources/data/pasterdream/dimension_type/dyedream_world.json`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDDimensions.java:71`

### 根因

`dimension_type/dyedream_world.json` 的 `"ambient_light": 0.5`，给所有方块 (包括 0 光照的深层洞穴) 额外添加 50% 亮度。

### 修复方向

将 `ambient_light` 从 `0.5` 改为 `0.0` (或 `0.05`)。需同时修改 JSON 文件和 `PDDimensions.java:71` 的 `.withAmbientLight(0.5)`。

---

## #10-1 结构生成重复化

### 10.1.1 [P0] 风之旅 small_ballon 系列 —— 11 个独立结构集共享相同配置

**文件**: `structure_set/small_ballon_0.json` ~ `small_ballon_10.json`

**现象**: 11 个完全独立的 structure_set，共享相同配置：

| 参数 | 值 |
|------|-----|
| spacing | 10 |
| separation | 2 |
| ratio (sep/sp) | 0.20 |
| biome | wind_journey_biome_1 |

**问题**: 每个 `small_ballon_X` 是独立的随机格点生成器，互相之间没有距离约束。在 10x10 区块面积内，每个 set 尝试放置一次，11 个 set = 11 次独立尝试。不同编号的气球可以重叠，导致 3~5 个紧挨着的小气球。

**修复方向**: 合并为单一 random_selector 结构集，或大幅增大 spacing。

---

### 10.1.2 [P0] biome_dyedream_0 承受 42 个结构集竞争

biome_dyedream_0 是染梦维度核心陆地群系，承载了 **42 个 structure_set**：

- 17 个直接引用
- 18 个通过 `#dyedream_biome` tag 引用
- 7 个通过 multi-biome {0,1,2,3} 引用

小型结构 (spacing ≤ 25) 有 5 个：
- stone_pillar_0: spacing=5, sep=2
- stone_pillar_1: spacing=8, sep=4
- dyedream_pavilion_0: spacing=17, sep=7
- picnic_basket_structure: spacing=20, sep=10
- dyedream_campsite_0: spacing=25, sep=12

**修复方向**: 审查并减少重叠群系的结构数量，或将部分结构分散到其他子群系。

---

### 10.1.3 [P1] desert_cottage_0 和 dream_wishingtree_1 比值异常

| 结构 | spacing | separation | ratio | 正常范围 |
|------|---------|------------|-------|---------|
| desert_cottage_0 | 60 | 48 | **0.800** | 0.25~0.50 |
| dream_wishingtree_1 | 89 | 72 | **0.809** | 0.25~0.50 |

ratio 过高意味着两个结构之间最小距离仅为 spacing 的 80%，密度极高。

**修复方向**: desert_cottage_0 增大 spacing（建议 90+）或减小 separation（建议 30）；dream_wishingtree_1 增大 spacing（建议 120+）与 wishingtree_0 匹配。

---

## #10-2 装饰物重复化

### 10.2.1 [P1] biome_dyedream_2 冰系特征密度过高

biome_2 在 `top_layer_modification` 阶段注入了 5 个装饰特征：

| 装饰物 | rarity | 估算频率 |
|--------|:------:|:--------:|
| ice_crystal_spike | **1** | **100%** (每区块必出) |
| ice_spike | 2 | ~50% |
| ice_crystal_garden | 2 | ~50% |
| ice_pillar | 3 | ~33% |
| ice_gate | 5 | ~20% |

每个区块至少会尝试生成 ice_crystal_spike，同时约 50% 概率叠加 ice_spike + ice_crystal_garden。

**修复方向**: 将 `ice_crystal_spike` 的 rarity 从 1 调整为 2~3。

---

### 10.2.2 [P2] warm_crystal_spike 缺失 JSON 文件

Java 代码中 `DyedreamDecorations.registerWarmCrystalSpike()` 被调用，注册了 rarity=4 的 SPIKE 装饰到 biome_1。但是：

- `configured_feature/warm_crystal_spike.json` **不存在**
- `placed_feature/warm_crystal_spike.json` **不存在**

由于 `DecorationRegistry.generateAllJson()` 已被移除，该装饰物实际上未生效。

**修复方向**: 手动生成 `configured_feature/warm_crystal_spike.json` 和 `placed_feature/warm_crystal_spike.json`，并在 biome_modifier 中注入到 biome_1。

---

## #10-3 树木生成重复化

### 10.3.1 [P0] biome_dyedream_1（森林）注入了 9 个树 placed_feature

**文件**:
- `neoforge/biome_modifier/dyedream_forest_trees.json`
- `neoforge/biome_modifier/dyedream_highlands_features.json`
- `neoforge/biome_modifier/bb_forest_trees.json`

| 来源 modifier | features | 估算密度 |
|-------------|----------|---------|
| dyedream_forest_trees | dyedream_trees | ~4-8/区块 |
| dyedream_forest_trees | dyedream_trees_old_growth | ~3-6/区块 |
| dyedream_forest_trees | dyedream_tree_colossal | ~0.03/区块 |
| dyedream_forest_trees | dyedream_tree_worldtree | ~0.01/区块 |
| **dyedream_highlands_features** | **dyedream_trees_highlands** | **~4-8/区块** |
| bb_forest_trees | bb_trees_bush | ~0-3/区块 |
| bb_forest_trees | bb_trees_plaintree | ~0-2/区块 |
| bb_forest_trees | bb_trees_aspen | ~0-1/区块 |
| bb_forest_trees | bb_trees_blossom | ~0-1/区块 |

**总计: 约 11-28 棵树/区块**（原版 Forest 约 10 棵，Dark Forest 约 12 棵）

**问题**: `dyedream_highlands_features.json` 将 `dyedream_trees_highlands` 注入到了 `biome_dyedream_1`，与 `dyedream_forest_trees.json` 的 `dyedream_trees` 叠加，造成**双重密度**。

**修复方向**: 从 `dyedream_highlands_features.json` 中移除对 biome_dyedream_1 的注入，或从 `dyedream_forest_trees.json` 中移除 dyedream_trees（保留 old_growth）。

---

### 10.3.2 [P0] biome_dyedream_dense_forest 的 count=10 固定值过高

**文件**: `placed_feature/dyedream_trees_dense.json`

```json
"placement": [
  { "type": "minecraft:count", "count": 10 }
]
```

每个区块恒定生成 10 棵染梦树。加上 4 个 BB 移植树，总密度高达 11-17/区块。

**修复方向**: 将 count 从 10 降到 6-7。

---

### 10.3.3 [P1] random_selector 巨型树概率偏高

**文件**: `configured_feature/dyedream_tree_selector.json`

| 变体 | 权重 | 说明 |
|------|:----:|------|
| default | 0.30 | 最常见的小树 |
| giant | **0.18** | 巨型树 (trunk 16+5+4, foliage r=5,h=6) |
| bushy | 0.14 | 茂密树 |
| weeping | 0.14 | 垂枝树 |
| fancy | 0.12 | 繁茂树 |
| glowing | 0.08 | 发光树 |
| colossal | **0.04** | 超巨型树 (trunk 32+10+6, foliage r=7,h=8) |

giant (18%) + colossal (4%) = 22% 的概率选择巨型/超巨型树。在高密度注入下，每区块平均有 2-6 棵巨型树，冠幅半径 5-9 格，视觉上会显得"到处都是大树"。

**修复方向**: giant 从 0.18 降到 0.10，colossal 从 0.04 降到 0.02。

---

## #10-4 云团地下大量生成（性能严重问题）

### 涉及文件

- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/ModDecorations.java` (L39-105)
- `PasterDream/src/main/resources/data/pasterdream/worldgen/placed_feature/cloudfall_mound.json`
- `PasterDream/src/main/resources/data/pasterdream/worldgen/configured_feature/cloudfall_mound_dense.json`
- `PasterDream/src/main/resources/data/pasterdream/worldgen/configured_feature/cloudfall_mound_sparse.json`
- `PasterDream/src/main/resources/data/pasterdream/neoforge/biome_modifier/cloudfall_mound.json`

### 问题现象

游戏内地下存在大量云团结构（灰色/粉色方块），严重影响游戏性能。

### 根因分析

#### 10.4.1 [P0] heightmap=MOTION_BLOCKING（最主要原因）

**文件**: `placed_feature/cloudfall_mound.json`, `placed_feature/floating_cloud_island.json`

```json
{ "type": "minecraft:heightmap", "heightmap": "MOTION_BLOCKING" }
```

**问题**: `MOTION_BLOCKING` 会找到任何碰撞形状非空的最高方块 Y，包括**洞穴天花板**。当特征在有洞穴的坐标生成时，定位点是洞穴天花板而非地面，导致云团在地下生成。

**正确应使用**: `WORLD_SURFACE_WG`（只看世界表面，忽略洞穴）。

#### 10.4.2 [P0] fillHang=true（加剧问题）

**文件**: `ModDecorations.java` (cloudfall_mound_dense L59, cloudfall_mound_sparse L96)

```java
.fillHang(true)  // 悬空填充：从生成位置向下延伸到有支撑的位置
```

**效果**: 如果云团在洞穴天花板附近生成，`fillHang` 会从云团位置向下填充云方块直到到达地面，形成"云柱"连接天花板和地面，进一步扩大影响范围。

#### 10.4.3 [P1] rarity=2（密度过高）

**文件**: `placed_feature/cloudfall_mound.json`

```json
{ "type": "minecraft:rarity_filter", "chance": 2 }
```

每 2 个区块就尝试生成一次，几乎每个区块都会有一个云团。

#### 10.4.4 [P2] replaceable 包含 CAVE_AIR

**文件**: `ModDecorations.java` (L62, L99)

```java
BlockPredicate.matchesBlocks(Blocks.AIR, Blocks.CAVE_AIR, ...)
```

洞穴空气被视为可替换方块，BLOB 的随机游走可以在洞穴内部放置云块。

#### 10.4.5 [P2] 注入到全部 9 个群系

**文件**: `biome_modifier/cloudfall_mound.json`

```json
"biomes": "#pasterdream:is_dyedream"
```

所有染梦群系（包括海洋、密林）都注入了云团特征。

### 云团特征参数汇总

| 特征 | clusterSize | fillHang | rarity | heightmap | biome |
|------|:-----------:|:--------:|:------:|:---------:|-------|
| cloudfall_mound_dense | **90** | **true** | 1 | MOTION_BLOCKING | 9 个群系 |
| cloudfall_mound_sparse | **55** | **true** | 1 | MOTION_BLOCKING | 9 个群系 |
| floating_cloud_island | 80 | false | 4 | MOTION_BLOCKING | biome_0 |

### 修复方向

| 优先级 | 问题 | 修复方案 |
|:------:|------|---------|
| **P0** | heightmap=MOTION_BLOCKING | 改为 `WORLD_SURFACE_WG` |
| **P0** | fillHang=true | 改为 `false` |
| **P1** | rarity=2 | 提高到 4-6 |
| **P2** | replaceable 包含 CAVE_AIR | 移除 CAVE_AIR |
| **P2** | 注入到全部 9 个群系 | 缩小到陆地群系 |

---

## #13 染梦树苗无法生长

### 涉及文件

- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/block/DyedreamSaplingBlock.java:39-44`

### 根因

`TreeGrower` 构造函数参数顺序错误：`TREE_SELECTOR` 被传到了 `megaTree`（大型树/2x2）位置而非 `tree`（普通树）位置。

```java
// 修复前（错误）:
private static final TreeGrower DYEDREAM_TREE_GROWER = new TreeGrower(
        "dyedream_tree",
        Optional.of(TreeRegistry.TREE_SELECTOR),  // megaTree ← 错误位置
        Optional.empty(),                         // tree ← 为空
        Optional.empty()
);
```

**执行流程**:
1. `randomTick` 触发 → `advanceTree()` → STAGE=1
2. `TreeGrower.growTree()` 被调用
3. `getConfiguredMegaFeature()` 返回 `TREE_SELECTOR`（因为传到了 megaTree 位置）
4. `isTwoByTwoSapling()` 检测失败（单株树苗）
5. `getConfiguredFeature()` 读取 `this.tree` = `Optional.empty()` → 返回 `null`
6. `featureKey == null` → **return false** → 树苗无法生长

---

## #14 染梦裂隙传送后玩家未生成在裂隙旁

### 涉及文件

- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/block/DyedreamCrackBlock.java:136-192, 225-248`
- `PasterDream/src/main/java/com/pasterdream/pasterdreammod/item/DyedreamTeleportCrystal.java:111-119`

### 问题现象

玩家通过染梦裂隙传送后，并非生成在裂隙旁，而是传送到重生点/世界出生点区域，可能导致玩家死亡。

### 根因分析

#### 14.1 [设计] 裂隙没有"配对传送门"机制

传送目标始终是重生点/世界出生点，而非对应维度中另一个裂隙的位置。与原模组行为一致。

```java
// 传送逻辑完全忽略裂隙方块的 pos
BlockPos targetPos = findSafePosition(targetWorld, player);  // 只用 player 和 targetWorld
```

#### 14.2 [P2] findSafePosition() 扫描算法缺陷

```java
private BlockPos findSafePosition(ServerLevel world, ServerPlayer player) {
    BlockPos spawnPos;
    if (player.getRespawnPosition() != null && player.getRespawnDimension().equals(world.dimension())) {
        spawnPos = player.getRespawnPosition();
    } else {
        spawnPos = world.getSharedSpawnPos();
    }

    // ⚠️ 从最高处向下扫描，找第一个非空气方块
    for (int y = world.getMaxBuildHeight() - 1; y > world.getMinBuildHeight(); y--) {
        checkPos.setY(y);
        if (world.isLoaded(checkPos) && !world.getBlockState(checkPos).isAir()) {
            return checkPos.above(2).immutable();   // ⚠️ 可能命中洞穴天花板
        }
    }
    return spawnPos.above(3);  // ⚠️ 未加载时高空坠落
}
```

| 问题 | 影响 |
|------|------|
| 找第一个非空气块可能命中**洞穴天花板** | 玩家被卡在天花板上方 |
| `spawnPos.above(3)` 无安全检测 | 区块未加载时高空坠落 |
| 不检查是否有 2 格高空间 | 玩家可能窒息 |

### 修复方向

| 方案 | 说明 | 难度 |
|------|------|:----:|
| A. 使用 Heightmap | 用 `Heightmap.Types.MOTION_BLOCKING` 获取真实地表高度，替代逐格扫描 | 低 |
| B. 安全空间检测 | 在目标位置检查是否有 2 格高的安全空间 | 低 |
| C. 配对裂隙机制 | 记录裂隙位置，传送时传送到对应裂隙旁（设计变更，需确认） | 高 |

---

## 修复优先级

| 优先级 | 序号 | 问题 |       修复难度        | 影响范围 |
|:------:|:----:|------|:---------------------:|---------|
| **P0** | #12 | 维度洞穴永久亮 |   低 (改 2 处数值)    | 全维度体验 |
| **P0** | #13 | 染梦树苗无法生长 |          低           | 树苗系统 |
| **P0** | #5 | 效果纹理缺失 |   低 (复制 1 文件)    | 视觉缺失 |
| **P0** | #10.4.1 | 云团 heightmap=MOTION_BLOCKING |     低 (改 JSON)      | 全维度性能 |
| **P0** | #10.4.2 | 云团 fillHang=true |     低 (改 JSON)      | 全维度性能 |
| **P0** | #10.3.1 | biome_dyedream_1 注入 9 个树 placed_feature |          低           | 森林群系 |
| **P0** | #10.3.2 | dyedream_trees_dense count=10 过高 |          低           | 茂密森林 |
| **P0** | #10.1.1 | 风之旅 small_ballon 11 个独立结构集 |          中           | 风之旅维度 |
| **P0** | #10.1.2 | biome_dyedream_0 承受 42 个结构集竞争 |          高           | 染梦核心群系 |
| **P1** | #1 | 睡莲碰撞/破坏 |      低 (改属性)      | 核心交互 |
| **P1** | #3 | 晶芽掉落 |   中 (改 getDrops)    | 经济系统 |
| **P1** | #6 | 冰柱融化 |     中 (替换方块)     | 世界生成 |
| **P1** | #10.2.1 | biome_dyedream_2 ice_crystal_spike rarity=1 |          低           | 冰雪群系 |
| **P1** | #10.1.3 | desert_cottage_0 / wishingtree_1 ratio 异常 |          低           | 主世界/染梦 |
| **P1** | #10.3.3 | random_selector 巨型树概率偏高 |          低           | 全维度 |
| **P1** | #10.4.3 | 云团 rarity=2 密度过高 |     低 (改 JSON)      | 全维度性能 |
| **P2** | #2 | 树叶腐烂 |    低 (改 1 方法)     | 设计决策需确认 |
| **P2** | #4 | 星空枕/占卜 |   中高 (加校验逻辑)   | 游戏平衡 |
| **P2** | #9 | 落叶/悬空 |   中 (改 decorator)   | 世界生成 |
| **P2** | #10.2.2 | warm_crystal_spike 缺失 JSON |          中           | biome_1 |
| **P2** | #10.4.4 | 云团 replaceable 包含 CAVE_AIR | 低 | 世界生成 |
| **P2** | #10.4.5 | 云团注入到全部 9 个群系 | 低 | 全维度 |
| **P2** | #14 | 裂隙传送后玩家未生成在裂隙旁 | 低~高 | 传送系统 |
| **P3** | #8 | 水晶球水填充 | 高 (改 NBT/processor) | 特定结构 |
