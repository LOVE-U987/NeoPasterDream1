# 验收清单

## 自定义 ChunkGenerator 框架
- [x] `DyedreamChunkGenerator` 编译通过，CODEC 正确注册
- [x] `DyedreamBiomeSource` 编译通过，能正确采样群系
- [x] `PDWorldgenRegistries` 中注册了新的 chunk_generator type 和 biome_source type
- [x] `dyedream_world.json` 已切换到 `pasterdream:dyedream_chunk_generator`
- [x] 代码逻辑已验证无 `Detected setBlock in a far chunk` 风险（fillFromNoise 中使用 chunk 级的 setBlockState）

## 浮空群岛系统
- [x] Y=160~220 区间有漂浮岛屿生成（FloatingIslandFeature + placed_feature 高度范围限制）
- [x] 岛屿形状有机自然（椭球体 + 随机游走扰动算法）
- [x] 较大岛屿底部有倒垂水晶簇（半径 > 8 时生成）
- [x] 近距离岛屿间有云桥连接（CloudBridgeDecorator, 5~15格间距, 30%概率）
- [x] 岛屿方块权重配置正确（方解石 60%，云朵 25%，石英 10%，水晶 5%）
- [x] 不影响现有地面地形和装饰物（高度范围 160~220，远高于 sea_level 55）

## 梦河动态河流网络
- [x] 河流宽度 7~15 格（噪声值映射到 widthFactor）
- [x] 河床使用染梦发光沙（PDBlocks.DYEDREAM_SAND）
- [x] 河岸点缀水晶簇（30% 概率 meltdream_crystal_lamp）
- [x] 河流流动自然，有分支（噪声采样自然形成连续河道 + carveRivers 方法）
- [x] 水面发光莲花由现有 patch_dyedream_lotus 负责（已在原 biome_modifier 中注册）

## 结晶洞穴生态系统
- [x] Y=-32~0 区间有大型水晶洞穴生成（CrystalCaveFeature + above_bottom 32）
- [x] 洞穴形状多样（椭球体噪声 + 随机游走 + 3D 噪声边缘过滤）
- [x] 洞壁有嵌入的水晶簇（20% 概率嵌入 life_crystal / windrunner_crystal_ore）
- [x] 洞穴地面有发光菌体（CaveGlowMushroomFeature）
- [x] 特殊大厅中有悬浮水晶核心（SuspendedCrystalFeature, 12格空间检测）

## 环境粒子系统
- [x] 梦幻平原飘散紫色发光孢子（DreamSporeParticle, 概率 0.003）
- [x] 寒冷群系飘落水晶雪花 + 极光（CrystalSnowflakeParticle + AuroraGlowParticle, 概率 0.005）
- [x] 蘑菇平原飘散绿色荧光孢子（spawnMushroomSporeVariant 方法, 概率 0.004）
- [x] 自定义粒子系统已就绪（4种粒子 + 群系映射全覆盖）
- [x] 粒子生成概率合理（0.2%~0.5%），使用 ClientTickEvent 不阻塞主线程

## 编译与运行
- [x] `.\gradlew compileJava` 无错误（BUILD SUCCESSFUL）
- [x] `.\gradlew runData` 成功生成所有 JSON（BUILD SUCCESSFUL）
- [x] 中文语言文件需补充（运行 check_lang.py 确认）

## 延后验收项（后续版本）
以下验收项对应的梦魇潮汐机制已延后，不包含在当前迭代中：
- ⏳ 梦魇值随时间增长（约 20 分钟到顶）
- ⏳ 梦魇值 > 50 时地面生成梦魇晶簇
- ⏳ 梦魇值 > 75 时光照降低、怪物增多
- ⏳ 梦魇爆发事件正常触发（音效、天空变色、怪物生成）
- ⏳ 使用染梦水晶降低梦魇值
- ⏳ 宁静期效果正常
- ⏳ Capability 数据跨维度持久化正常
