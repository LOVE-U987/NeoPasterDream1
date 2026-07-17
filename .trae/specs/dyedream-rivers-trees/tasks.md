# 染梦世界河流与树木扩展 - 实现计划

## [x] Task 1: FastNoise 工具类（已存在）
- **Priority**: high
- **Depends On**: None
- **Description**: 
  - FastNoise 工具类已存在于 `util/FastNoise.java`，支持 domain warp 功能
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-1.1: FastNoise 类编译通过（已验证）

## [x] Task 2: 创建 RiverEntry 记录类
- **Priority**: high
- **Depends On**: None
- **Description**: 
  - 在 `DyedreamBiomeSource` 中创建 `RiverEntry` 记录类（参考 ES 模组）
  - 字段：riverData（群系 Holder）、size（河流宽度）、transitionData（过渡群系）、transitionSize（过渡宽度）、offset（偏移量）、canGenerateInOcean、canGenerateInOceanOnly
  - 添加 CODEC 支持 JSON 序列化
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-2.1: RiverEntry 类编译通过
  - `human-judgement` TR-2.2: CODEC 配置正确，JSON 可正确解析

## [x] Task 3: 重写 DyedreamBiomeSource，实现ES风格梯度噪声检测
- **Priority**: high
- **Depends On**: Task 2
- **Description**: 
  - 更新 CODEC 添加 rivers 参数
  - 添加 FastNoise 实例用于河流噪声和 warp 噪声
  - 实现梯度噪声检测算法：
    1. 使用 domain warp 噪声采样获取噪声值
    2. 计算梯度向量：`gradX = (noise(x+1,z) - noise(x-1,z)) / 2`，`gradZ = (noise(x,z+1) - noise(x,z-1)) / 2`
    3. 使用 `riverValue = |noiseValue| / gradLength` 判定河流范围
    4. 支持河流宽度 `size` 和过渡区域 `transitionSize` 参数
  - 添加 setSeed() 方法初始化噪声种子
  - 添加群系缓存机制优化性能
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-3.1: DyedreamBiomeSource 编译通过
  - `human-judgement` TR-3.2: 河流在世界中自然生成，走向流畅

## [x] Task 4: 创建河流生物群系配置
- **Priority**: high
- **Depends On**: None
- **Description**: 
  - 创建 `biome_dyedream_river.json` 生物群系文件
  - 设置独特的粉色/紫色水色（water_color）和雾气效果（fog_color）
  - 添加河流雕刻器配置（carvers）
  - 配置河岸植被生成（features）
- **Acceptance Criteria Addressed**: AC-1, AC-4
- **Test Requirements**:
  - `human-judgement` TR-4.1: 验证河流生物群系 JSON 配置正确
  - `human-judgement` TR-4.2: 验证河流呈现粉色/紫色调

## [x] Task 5: 更新维度配置使用自定义 BiomeSource 和河流参数
- **Priority**: high
- **Depends On**: Task 3, Task 4
- **Description**: 
  - 更新 `dyedream_world.json` 将 biome_source 切换为 `pasterdream:dyedream_biome_source`
  - 添加 rivers 配置参数（宽度、过渡区域等）
  - 添加河流生物群系到群系列表
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `human-judgement` TR-5.1: 验证维度配置正确加载自定义 BiomeSource
  - `human-judgement` TR-5.2: 验证河流生物群系在世界中正确生成

## [x] Task 6: 更新 DyedreamChunkGenerator 河流逻辑
- **Priority**: high
- **Depends On**: Task 3
- **Description**: 
  - 更新 `carveRivers()` 方法，使用新的梯度噪声检测方式
  - 更新 `restoreRiverWater()` 方法与新 BiomeSource 保持一致
  - 确保河床地形和水面恢复正常工作
- **Acceptance Criteria Addressed**: AC-1
- **Test Requirements**:
  - `programmatic` TR-6.1: DyedreamChunkGenerator 编译通过
  - `human-judgement` TR-6.2: 验证河流地形正确生成，河床底部为染梦沙

## [x] Task 7: 启用树叶群系颜色变化
- **Priority**: high
- **Depends On**: None
- **Description**: 
  - 修改 `DyedreamLeavesBlock` 构造函数，启用 `color: true` 属性（使用 `LeavesBlock.Properties.color(true)`）
  - 更新 `dyedream_leaves.json` 方块状态定义支持群系颜色变化（添加 color 属性）
  - 更新树叶模型使用 `tintindex` 支持染色（在模型 JSON 中添加 `tintindex: 0`）
- **Acceptance Criteria Addressed**: AC-2
- **Test Requirements**:
  - `human-judgement` TR-7.1: 验证树叶颜色随群系变化
  - `human-judgement` TR-7.2: 验证平原偏粉色（-145678）、森林偏深紫（-216083）、冰雪偏蓝白（-22035）

## [x] Task 8: 创建多样化树形 configured_feature 配置
- **Priority**: high
- **Depends On**: None
- **Description**: 
  - 创建 `dyedream_tree_normal.json`（普通树形，5-8格高）
  - 创建 `dyedream_tree_large.json`（巨型树形，10-15格高）
  - 创建 `dyedream_tree_weeping.json`（垂泪树形，向下垂的树叶）
  - 创建 `dyedream_tree_icy.json`（冰晶树形，使用冰系方块）
  - 创建 `dyedream_tree_mushroom.json`（蘑菇树变种）
  - 创建 `dyedream_tree_selector.json`（树形选择器，随机选择不同树形）
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `human-judgement` TR-8.1: 验证 configured_feature 配置正确（JSON 格式正确）
  - `human-judgement` TR-8.2: 验证不同树形在世界中正确生成

## [x] Task 9: 创建多样化树木 placed_feature 配置
- **Priority**: high
- **Depends On**: Task 8
- **Description**: 
  - 创建 `dyedream_trees_dense.json`（高密度森林，使用 tree_selector）
  - 创建 `dyedream_trees_sparse.json`（稀疏平原/高原，使用 normal tree）
  - 创建 `dyedream_trees_icy.json`（冰雪群系，使用 icy tree）
  - 创建 `dyedream_trees_mushroom.json`（蘑菇平原，使用 mushroom tree）
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `human-judgement` TR-9.1: 验证 placed_feature 配置正确（JSON 格式正确）
  - `human-judgement` TR-9.2: 验证不同密度的树木在各群系正确生成

## [x] Task 10: 添加生物群系修饰器配置
- **Priority**: medium
- **Depends On**: Task 9
- **Description**: 
  - 创建 `dyedream_forest_features.json` 添加森林高密度树木（dyedream_1）
  - 创建 `dyedream_plains_features.json` 添加平原稀疏树木（dyedream_0）
  - 创建 `dyedream_permafrost_features.json` 添加冰雪树木/冰晶（dyedream_2）
  - 创建 `dyedream_highlands_features.json` 添加高原特殊树木（dyedream_3）
  - 创建 `dyedream_mushroom_features.json` 添加蘑菇树变种（dyedream_mushroom_plains）
- **Acceptance Criteria Addressed**: AC-3
- **Test Requirements**:
  - `human-judgement` TR-10.1: 验证 biome_modifier 正确应用到对应生物群系
  - `human-judgement` TR-10.2: 验证各生物群系生成预期的树木密度和类型

## [x] Task 11: 添加河岸和水生植被配置
- **Priority**: medium
- **Depends On**: Task 4
- **Description**: 
  - 创建 `patch_dyedream_reeds` configured_feature（芦苇）
  - 创建 `patch_dyedream_water_flowers` configured_feature（水生花卉）
  - 创建 `dyedream_river_vegetation` placed_feature
  - 创建 `dyedream_river_features.json` biome_modifier 添加河流植被到河流生物群系
- **Acceptance Criteria Addressed**: AC-4
- **Test Requirements**:
  - `human-judgement` TR-11.1: 验证河岸两侧生成芦苇和水生植物
  - `human-judgement` TR-11.2: 验证河流底部生成水草

## [x] Task 12: 编译测试和验证
- **Priority**: high
- **Depends On**: All previous tasks
- **Description**: 
  - 运行 `.\gradlew compileJava` 验证代码编译
  - 运行 `.\gradlew runData` 生成数据文件
  - 运行 `.\gradlew runClient` 测试游戏内效果
- **Acceptance Criteria Addressed**: AC-1, AC-2, AC-3, AC-4, AC-5, AC-6
- **Test Requirements**:
  - `programmatic` TR-12.1: 编译无错误
  - `programmatic` TR-12.2: 数据生成无错误
  - `human-judgement` TR-12.3: 游戏内世界生成正常，无崩溃
  - `human-judgement` TR-12.4: 河流、树木、树叶颜色、水生植被正确生成