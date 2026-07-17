# 染梦世界河流与树木扩展 - 验证清单

## 代码实现完整性
- [x] FastNoise 工具类存在且编译通过（已验证）
- [x] RiverEntry 记录类存在于 DyedreamBiomeSource，包含 CODEC 支持
- [x] DyedreamBiomeSource 重写完成，实现梯度噪声检测算法
- [x] DyedreamBiomeSource 包含 FastNoise 实例用于河流噪声和 warp 噪声
- [x] DyedreamBiomeSource 包含 setSeed() 方法初始化噪声种子
- [x] DyedreamBiomeSource 包含群系缓存机制
- [x] DyedreamChunkGenerator 河流逻辑更新完成
- [x] DyedreamLeavesBlock 通过 ClientSetup 颜色提供者支持群系颜色变化
- [x] dyedream_leaves 模型已添加 tintindex 支持染色

## 配置文件完整性
- [x] biome_dyedream_river.json 河流生物群系配置文件存在且格式正确
- [x] dyedream_world.json 维度配置已更新，添加 rivers 参数
- [x] dyedream_tree_large.json configured_feature 存在
- [x] dyedream_tree_weeping.json configured_feature 存在
- [x] dyedream_tree_icy.json configured_feature 存在
- [x] dyedream_tree_selector.json configured_feature 存在
- [x] dyedream_trees_dense.json placed_feature 存在
- [x] dyedream_trees_sparse.json placed_feature 存在
- [x] dyedream_trees_icy.json placed_feature 存在
- [x] dyedream_trees_mushroom.json placed_feature 存在
- [x] dyedream_forest_features.json biome_modifier 存在
- [x] dyedream_plains_features.json biome_modifier 存在
- [x] dyedream_permafrost_features.json biome_modifier 存在
- [x] dyedream_highlands_features.json biome_modifier 存在
- [x] dyedream_mushroom_features.json biome_modifier 存在
- [x] patch_dyedream_reeds configured_feature 存在
- [x] dyedream_river_features.json biome_modifier 存在

## 游戏内验证
- [ ] 染梦世界河流正确生成，贯穿不同地形
- [ ] 河流呈现粉色/紫色调（water_color 设置正确）
- [ ] 河流走向自然流畅，无明显锯齿或直线（domain warp 效果）
- [ ] 河流宽度随噪声变化，自然过渡
- [ ] 河床底部为染梦沙方块
- [ ] 平原生物群系（dyedream_0）树叶偏粉色（foliage_color: -145678）
- [ ] 森林生物群系（dyedream_1）树叶偏深紫色（foliage_color: -216083）
- [ ] 冰雪生物群系（dyedream_2）树叶偏蓝白色（foliage_color: -22035）
- [ ] 高原生物群系（dyedream_3）树叶呈现独特颜色（foliage_color: -22035）
- [ ] 蘑菇平原树叶呈现青绿/蓝绿色（foliage_color: -541729）
- [ ] 平原生物群系生成稀疏基础树木（密度低）
- [ ] 森林生物群系生成密集多种树形（密度高，包含普通/巨型/垂泪树）
- [ ] 冰雪生物群系生成冰晶树变种
- [ ] 高原生物群系生成稀疏特殊树木
- [ ] 蘑菇平原生成蘑菇树变种
- [ ] 河岸两侧生成芦苇和水生植物
- [ ] 河流底部生成水草
- [ ] 特殊树形（巨型树、垂泪树）正确生成

## 性能验证
- [x] 代码编译无错误（.\gradlew compileJava）
- [x] 数据生成无错误（.\gradlew runData）
- [ ] 世界生成无明显卡顿
- [ ] 游戏运行无崩溃
- [ ] 内存使用正常

## 视觉一致性
- [ ] 河流效果与 ES 模组风格相似（自然弯曲走向）
- [ ] 新增元素与染梦世界粉色/紫色主题一致
- [ ] 树木和植被分布自然合理
- [ ] 河流与周围地形融合自然
- [ ] 树叶颜色随群系变化自然和谐
- [ ] 特殊效果（发光、垂泪）符合梦幻主题
- [ ] 河岸植被与河流主题协调一致