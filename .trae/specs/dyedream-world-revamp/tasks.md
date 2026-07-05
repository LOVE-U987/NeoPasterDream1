# 任务列表

## Task Dependencies

- [Task 2] 依赖于 [Task 1]
- [Task 3] 依赖于 [Task 1]
- [Task 4] 可部分与 [Task 2][Task 3] 并行（环境系统独立）
- [Task 5] 依赖于 [Task 2][Task 3][Task 4]（集成测试在所有模块完成后进行）

---

## 任务分解

### Task 1: 自定义染梦世界 ChunkGenerator 框架

搭建自定义世界生成器的基础架构，继承 `NoiseBasedChunkGenerator`，为后续的地形改造提供扩展点。

- [ ] **1.1** 创建 `DyedreamChunkGenerator` 类，继承 `NoiseBasedChunkGenerator`，实现 `CODEC`
- [ ] **1.2** 创建 `DyedreamBiomeSource` 类，继承 `BiomeSource`，添加噪声驱动的群系采样和河流网络数据
- [ ] **1.3** 在 `PDCodecs` 中注册新的 chunk_generator type 和 biome_source type
- [ ] **1.4** 创建 `DyedreamNoises` 工具类，管理所有自定义噪声（FastNoise 或原版 NoiseRouter）
- [ ] **1.5** 修改 `dyedream_world.json`，将 generator.type 切换为 `pasterdream:dyedream_chunk_generator`

### Task 2: 浮空群岛系统

基于 Task 1 的 ChunkGenerator，在 Y=160~220 高度区间生成漂浮岛屿群。

- [ ] **2.1** 创建 `FloatingIslandFeature` 类（`Feature<FloatingIslandConfiguration>`），实现岛屿生成算法
  - 噪声密度采样决定岛屿位置和形状
  - 椭球体 + 随机游走生成有机岛屿轮廓
  - 底部倒垂水晶簇装饰
- [ ] **2.2** 创建 `FloatingIslandConfiguration` 记录类，配置方块权重、大小范围、密度阈值
- [ ] **2.3** 创建 `CloudBridgeDecorator`，在近距离岛屿间生成云桥/藤蔓桥
- [ ] **2.4** 在 `PDFeatures` 中注册 `FloatingIslandFeature` 和 `CloudBridgeDecorator`
- [ ] **2.5** 创建 `placed_feature/floating_islands.json` 和 `configured_feature/floating_islands.json`
- [ ] **2.6** 在 biome_modifier 中将浮岛特征注入到所有染梦群系的 `TOP_LAYER_MODIFICATION` 阶段（限制生成高度 Y=160+）
- [ ] **2.7** 补充语言文件翻译

### Task 3: 梦河动态河流网络 + 结晶洞穴生态系统

实现自定义河流雕刻和地下水晶洞穴系统。

- [ ] **3.1** 在 `DyedreamBiomeSource` 中添加河流噪声采样逻辑，生成河流网络数据
- [ ] **3.2** 创建 `DyedreamRiverCarver` 类，使用噪声数据在 `DyedreamChunkGenerator` 中雕刻河流通道
  - 河床 7~15 格宽，使用染梦发光沙
  - 河岸装饰水晶簇
- [ ] **3.3** 创建 `CrystalCaveCarver` 类，在地下 Y=-32~0 生成大型拱形水晶洞穴
  - 自定义洞穴噪声（参考 ESChunkGenerator 的 caveNoise）
  - 洞壁嵌入水晶簇逻辑
- [ ] **3.4** 创建 `SuspendedCrystalFeature`，在洞穴大厅中生成悬浮水晶核心
- [ ] **3.5** 创建 `CaveGlowMushroomFeature`，在地下洞穴生成发光菌体群落（复用 PinkagaricClusterFeature 逻辑）
- [ ] **3.6** 注册所有新 Feature 和 BiomeModifier

### Task 4: 染梦环境粒子系统升级

将目前 JSON 中硬编码的群系粒子效果升级为动态的、基于代码的环境渲染系统。

- [ ] **4.1** 创建 `DyedreamEnvironmentRenderer` 客户端类，管理所有群系的环境效果
- [ ] **4.2** 定义新的粒子类型（`PDParticles`添加）：`dream_spore`、`crystal_snowflake`、`aurora_glow`、`stardust`
- [ ] **4.3** 为每个染梦群系创建专属的粒子配置（概率、颜色、运动轨迹）
- [ ] **4.4** 在客户端事件（`ClientTickEvent`）中驱动粒子生成
- [ ] **4.5** 创建极光天空渲染器 `DyedreamAuroraRenderer`：在寒冷群系（biome_2）天空绘制动态极光效果

### Task 5: 集成测试与发布

- [ ] **5.1** 编写 `build.gradle` 确保所有新依赖正确（FastNoise 等）
- [ ] **5.2** 运行 `.\gradlew compileJava` 检查编译错误
- [ ] **5.3** 运行 `.\gradlew runData` 生成数据文件
- [ ] **5.4** 启动 `runClient` 进入染梦世界视觉验证
- [ ] **5.5** 对照 `checklist.md` 逐条验证完成情况

---

## 延后任务（后续版本实现）

以下任务已延后至后续版本，不在当前迭代范围内。

### Task D: 梦魇潮汐机制

实现周期性的环境状态变化系统。

- [ ] **D.1** 创建 `NightmareCapability` 能力系统（使用 NeoForge Capability API）
  - 存储玩家在当前维度的梦魇值（0~100）
  - 提供增加/减少/获取方法
- [ ] **D.2** 创建服务器端 Tick 事件监听器，每 tick 更新梦魇值
  - 在染梦世界每 tick +0.003（约 20 分钟到 100）
  - 使用染梦水晶物品 -20
- [ ] **D.3** 创建梦魇阶段效果处理器：
  - 梦魇值 > 50：表面生成 `nightmare_crystal_cluster` 装饰物
  - 梦魇值 > 75：全维度光照降低，使用 `LevelEvent` 调整亮度
  - 梦魇值 = 100：触发"梦魇爆发"事件
- [ ] **D.4** 创建 `NightmareBloomEvent` 爆发事件：
  - 全维度音效广播
  - 天空变色（客户端使用 `DimensionSpecialEffects` 覆盖）
  - 玩家周围生成 3~8 个梦魇怪物
  - 持续 3 分钟后自动消退
- [ ] **D.5** 创建 `DreamCrystalItem` 交互：右击降低梦魇值 20
- [ ] **D.6** 注册 Capability 和数据持久化
