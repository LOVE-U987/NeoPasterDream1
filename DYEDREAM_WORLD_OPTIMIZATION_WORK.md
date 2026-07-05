# 染梦世界地形优化 - 实施任务清单

> 基于 `DYEDREAM_WORLD_OPTIMIZATION.md` 的详细实施步骤
> 
> **状态标记**: ✅ 已完成 | 🚧 进行中 | ⏳ 待开始 | ❌ 有问题

---

## 全局准备

| 任务 | 描述 | 状态 | 负责人 | 预计耗时 |
|------|------|------|--------|---------|
| PREP-01 | 确认项目编译通过 | ✅ | AI | 15分钟 |
| PREP-02 | 备份当前 `dyedream_world` 配置 | ✅ | AI | 5分钟 |
| PREP-03 | 确认 `eternal-starlight` 参考文件就绪 | ✅ | AI | 5分钟 |

---

## 阶段一：新增过渡群系

### 1.1 创建群系 JSON 文件

| 任务 | 描述 | 文件路径 | 状态 | 负责人 |
|------|------|---------|------|--------|
| PHASE1-01 | 创建 `biome_dyedream_shore.json` 群系配置 | `PasterDream/src/main/resources/data/pasterdream/worldgen/biome/biome_dyedream_shore.json` | ✅ | AI |
| PHASE1-02 | 创建 `biome_dyedream_river.json` 群系配置 | `PasterDream/src/main/resources/data/pasterdream/worldgen/biome/biome_dyedream_river.json` | ✅ | AI |
| PHASE1-03 | 创建 `biome_dyedream_dense_forest.json` 群系配置 | `PasterDream/src/main/resources/data/pasterdream/worldgen/biome/biome_dyedream_dense_forest.json` | ✅ | AI |

**群系配置要点**：

**biome_dyedream_shore**:
- has_precipitation: true
- temperature: 0.7
- downfall: 0.45
- 天空/雾色：温暖粉紫色调
- 粒子：bubble (0.001)
- 生物：粉史莱姆

**biome_dyedream_river**:
- has_precipitation: true
- temperature: 0.8
- downfall: 0.5
- 天空/雾色：清新蓝紫色调
- 粒子：bubble (0.002)
- 生物：粉史莱姆

**biome_dyedream_dense_forest**:
- has_precipitation: true
- temperature: 0.6
- downfall: 0.6
- 天空/雾色：深邃紫绿色调
- 粒子：dust (0.7, 0.3, 0.9, 1.0) (0.0015)
- 生物：粉史莱姆、Allay

---

### 1.2 更新维度配置

| 任务 | 描述 | 文件路径 | 状态 | 负责人 |
|------|------|---------|------|--------|
| PHASE1-04 | 更新 `dyedream_world.json` 添加3个新群系到 multi_noise | `PasterDream/src/main/resources/data/pasterdream/dimension/dyedream_world.json` | ✅ | AI |

**multi_noise 参数配置**：

```
海岸群系 (continentalness: -0.19 ~ -0.11):
  - depth: [-1.0, 1.0] (全深度)
  
河流群系 (weirdness: -1.0 ~ -0.9333, erosion: 0.55 ~ 1.0):
  - continentalness: [-0.11, 1.0]
  - depth: [-1.0, 1.0]
  
密林群系 (weirdness: 0.2 ~ 0.6):
  - 共享平原参数，仅 weirdness 范围不同
```

---

### 1.3 更新语言文件

| 任务 | 描述 | 文件路径 | 状态 | 负责人 |
|------|------|---------|------|--------|
| PHASE1-05 | 添加新群系中文翻译 | `PasterDream/src/main/resources/assets/pasterdream/lang/zh_cn.json` | ✅ | AI |

**翻译内容**：
```json
{
  "biome.pasterdream.biome_dyedream_shore": "染梦海岸",
  "biome.pasterdream.biome_dyedream_river": "染梦河流",
  "biome.pasterdream.biome_dyedream_dense_forest": "染梦密林"
}
```

---

### 1.4 阶段一验证

| 任务 | 描述 | 状态 | 负责人 |
|------|------|------|--------|
| PHASE1-06 | 编译验证 | ✅ | AI |
| PHASE1-07 | 运行客户端测试群系生成 | ✅ | AI |

---

## 阶段二：地表材质分层

### 2.1 注册新增方块

| 任务 | 描述 | 文件路径 | 状态 | 负责人 |
|------|------|---------|------|--------|
| PHASE2-01 | 注册 `dyedream_deepstone` 方块 | `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDBlocks.java` | ⏳ | AI |
| PHASE2-02 | 注册 `dyedream_sandstone` 方块 | `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDBlocks.java` | ⏳ | AI |

**方块属性**：
- `dyedream_deepstone`: 深色基础方块，不可燃，爆炸抗性高
- `dyedream_sandstone`: 浅色砂岩，可挖掘，爆炸抗性中等

---

### 2.2 创建方块模型和纹理

| 任务 | 描述 | 文件路径 | 状态 | 负责人 |
|------|------|---------|------|--------|
| PHASE2-03 | 创建 `dyedream_deepstone.json` 方块状态 | `PasterDream/src/main/resources/assets/pasterdream/blockstates/dyedream_deepstone.json` | ⏳ | AI |
| PHASE2-04 | 创建 `dyedream_deepstone.json` 方块模型 | `PasterDream/src/main/resources/assets/pasterdream/models/block/dyedream_deepstone.json` | ⏳ | AI |
| PHASE2-05 | 创建 `dyedream_deepstone.png` 纹理 | `PasterDream/src/main/resources/assets/pasterdream/textures/block/dyedream_deepstone.png` | ⏳ | 用户 |
| PHASE2-06 | 创建 `dyedream_sandstone.json` 方块状态 | `PasterDream/src/main/resources/assets/pasterdream/blockstates/dyedream_sandstone.json` | ⏳ | AI |
| PHASE2-07 | 创建 `dyedream_sandstone.json` 方块模型 | `PasterDream/src/main/resources/assets/pasterdream/models/block/dyedream_sandstone.json` | ⏳ | AI |
| PHASE2-08 | 创建 `dyedream_sandstone.png` 纹理 | `PasterDream/src/main/resources/assets/pasterdream/textures/block/dyedream_sandstone.png` | ⏳ | 用户 |

---

### 2.3 更新 noise_settings

| 任务 | 描述 | 文件路径 | 状态 | 负责人 |
|------|------|---------|------|--------|
| PHASE2-09 | 更新 `surface_rule` 添加4层深度分层 | `PasterDream/src/main/resources/data/pasterdream/worldgen/noise_settings/dyedream_world.json` | ⏳ | AI |
| PHASE2-10 | 添加水域邻近条件（水边 sand） | `PasterDream/src/main/resources/data/pasterdream/worldgen/noise_settings/dyedream_world.json` | ⏳ | AI |

**surface_rule 结构**：
```
sequence:
  1. y_above(55) → 地表层
     sequence:
       - biome_is(冰原) → snow_block
       - water → dyedream_sand
       - else → dyedream_grass_block
  2. y_above(32) → 浅层地下
     - biome_is(冰原) → dyedream_packed_ice
     - else → dyedream_sandstone
  3. y_above(0) → 中层地下
     - dyedream_block
  4. else → 深层地下
     - dyedream_deepstone
```

---

### 2.4 更新语言文件

| 任务 | 描述 | 文件路径 | 状态 | 负责人 |
|------|------|---------|------|--------|
| PHASE2-11 | 添加新方块中文翻译 | `PasterDream/src/main/resources/assets/pasterdream/lang/zh_cn.json` | ⏳ | AI |

**翻译内容**：
```json
{
  "block.pasterdream.dyedream_deepstone": "染梦深层石",
  "block.pasterdream.dyedream_sandstone": "染梦砂岩",
  "item.pasterdream.dyedream_deepstone": "染梦深层石",
  "item.pasterdream.dyedream_sandstone": "染梦砂岩"
}
```

---

### 2.5 阶段二验证

| 任务 | 描述 | 状态 | 负责人 |
|------|------|------|--------|
| PHASE2-12 | 编译验证 | ⏳ | AI |
| PHASE2-13 | 运行客户端测试地表分层 | ⏳ | AI |

---

## 阶段三：氛围增强

### 3.1 添加群系粒子效果

| 任务 | 描述 | 文件路径 | 状态 | 负责人 |
|------|------|---------|------|--------|
| PHASE3-01 | 平原添加粉紫色 dust 粒子 | `PasterDream/src/main/resources/data/pasterdream/worldgen/biome/biome_dyedream_0.json` | ⏳ | AI |
| PHASE3-02 | 温暖平原添加橙粉色 dust 粒子 | `PasterDream/src/main/resources/data/pasterdream/worldgen/biome/biome_dyedream_1.json` | ⏳ | AI |
| PHASE3-03 | 冰原添加 snowflake 粒子 | `PasterDream/src/main/resources/data/pasterdream/worldgen/biome/biome_dyedream_2.json` | ⏳ | AI |
| PHASE3-04 | 蘑菇平原添加 glow 粒子 | `PasterDream/src/main/resources/data/pasterdream/worldgen/biome/biome_dyedream_mushroom_plains.json` | ⏳ | AI |
| PHASE3-05 | 海岸添加 bubble 粒子 | `PasterDream/src/main/resources/data/pasterdream/worldgen/biome/biome_dyedream_shore.json` | ⏳ | AI |
| PHASE3-06 | 河流添加 bubble 粒子 | `PasterDream/src/main/resources/data/pasterdream/worldgen/biome/biome_dyedream_river.json` | ⏳ | AI |

**粒子配置格式**：
```json
"particle": {
  "options": {
    "type": "minecraft:dust",
    "params": [0.7, 0.3, 0.9, 1.0]
  },
  "probability": 0.0015
}
```

---

### 3.2 调整海平面

| 任务 | 描述 | 文件路径 | 状态 | 负责人 |
|------|------|---------|------|--------|
| PHASE3-07 | 修改 `sea_level` 从 63 到 55 | `PasterDream/src/main/resources/data/pasterdream/worldgen/noise_settings/dyedream_world.json` | ⏳ | AI |

---

### 3.3 阶段三验证

| 任务 | 描述 | 状态 | 负责人 |
|------|------|------|--------|
| PHASE3-08 | 编译验证 | ⏳ | AI |
| PHASE3-09 | 运行客户端测试粒子效果和海岸线 | ⏳ | AI |

---

## 阶段四：装饰物层扩展

### 4.1 创建地下装饰物类

| 任务 | 描述 | 文件路径 | 状态 | 负责人 |
|------|------|---------|------|--------|
| PHASE4-01 | 创建 `hanging_crystal` 悬挂水晶装饰 | `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/DyedreamDecorations.java` | ⏳ | AI |
| PHASE4-02 | 创建 `crystal_stalactite` 钟乳石装饰 | `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/DyedreamDecorations.java` | ⏳ | AI |
| PHASE4-03 | 创建 `underground_crystal_garden` 地下水晶花园 | `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/DyedreamDecorations.java` | ⏳ | AI |

**装饰物配置**：

| 装饰物 | 类型 | 参数 | 群系 |
|--------|------|------|------|
| hanging_crystal | SPIKE (倒置) | height: 3~6, radius: 1~2, checkHang: true | 所有群系 |
| crystal_stalactite | SPIKE (倒置) | height: 2~5, radius: 1, checkHang: true | 所有群系 |
| underground_crystal_garden | SCATTER | clusterSize: 8, checkHang: true | 所有群系 |

---

### 4.2 注册装饰物到群系

| 任务 | 描述 | 文件路径 | 状态 | 负责人 |
|------|------|---------|------|--------|
| PHASE4-04 | 在 `ModDecorations.register()` 中注册新装饰物 | `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/ModDecorations.java` | ⏳ | AI |

---

### 4.3 生成 JSON 文件

| 任务 | 描述 | 文件路径 | 状态 | 负责人 |
|------|------|---------|------|--------|
| PHASE4-05 | 更新 Python 脚本添加新装饰物配置 | `generate_decoration_json.py` | ⏳ | AI |
| PHASE4-06 | 运行脚本生成 configured_feature JSON | 运行 `generate_decoration_json.py` | ⏳ | AI |
| PHASE4-07 | 运行脚本生成 placed_feature JSON | 运行 `generate_decoration_json.py` | ⏳ | AI |
| PHASE4-08 | 运行脚本生成 biome_modifier JSON | 运行 `generate_decoration_json.py` | ⏳ | AI |

---

### 4.4 更新语言文件

| 任务 | 描述 | 文件路径 | 状态 | 负责人 |
|------|------|---------|------|--------|
| PHASE4-09 | 添加新装饰物中文翻译 | `PasterDream/src/main/resources/assets/pasterdream/lang/zh_cn.json` | ⏳ | AI |

**翻译内容**：
```json
{
  "feature.pasterdream.hanging_crystal": "悬挂水晶",
  "feature.pasterdream.crystal_stalactite": "水晶钟乳石",
  "feature.pasterdream.underground_crystal_garden": "地下水晶花园"
}
```

---

### 4.5 阶段四验证

| 任务 | 描述 | 状态 | 负责人 |
|------|------|------|--------|
| PHASE4-10 | 编译验证 | ⏳ | AI |
| PHASE4-11 | 运行客户端测试地下装饰物生成 | ⏳ | AI |

---

## 最终验证

| 任务 | 描述 | 状态 | 负责人 |
|------|------|------|--------|
| FINAL-01 | 全面编译验证 | ⏳ | AI |
| FINAL-02 | DataGen 验证 | ⏳ | AI |
| FINAL-03 | 客户端完整测试 | ⏳ | AI |
| FINAL-04 | 群系过渡平滑度检查 | ⏳ | AI |
| FINAL-05 | 地表分层正确性检查 | ⏳ | AI |
| FINAL-06 | 粒子效果正常运行检查 | ⏳ | AI |
| FINAL-07 | 装饰物生成密度检查 | ⏳ | AI |

---

## 依赖关系图

```
PREP-01 → PREP-02 → PREP-03
         ↓
PHASE1-01 → PHASE1-02 → PHASE1-03 → PHASE1-04 → PHASE1-05 → PHASE1-06 → PHASE1-07
         ↓
PHASE2-01 → PHASE2-02 → PHASE2-03 → PHASE2-04 → PHASE2-06 → PHASE2-07
         ↓                    ↓                    ↓
    PHASE2-09 ← PHASE2-10 ← PHASE2-11 → PHASE2-12 → PHASE2-13
         ↓
PHASE3-01 → PHASE3-02 → PHASE3-03 → PHASE3-04 → PHASE3-05 → PHASE3-06
         ↓
    PHASE3-07 → PHASE3-08 → PHASE3-09
         ↓
PHASE4-01 → PHASE4-02 → PHASE4-03 → PHASE4-04 → PHASE4-05
         ↓
    PHASE4-06 → PHASE4-07 → PHASE4-08 → PHASE4-09 → PHASE4-10 → PHASE4-11
         ↓
FINAL-01 → FINAL-02 → FINAL-03 → FINAL-04 → FINAL-05 → FINAL-06 → FINAL-07
```

---

## 关键依赖说明

| 阶段 | 依赖 | 说明 |
|------|------|------|
| 阶段二 | 阶段一 | 新增方块需要在阶段一之后注册 |
| 阶段三 | 阶段一、阶段二 | 粒子效果需要新群系存在，海平面调整依赖 noise_settings |
| 阶段四 | 阶段一、阶段二 | 装饰物需要新群系和新方块存在 |
| 最终验证 | 所有阶段 | 需要所有修改完成后进行 |

---

## 资源需求清单

### 方块纹理

| 资源名 | 尺寸 | 描述 | 优先级 |
|--------|------|------|--------|
| `dyedream_deepstone.png` | 16x16 | 深色基础方块纹理 | 高 |
| `dyedream_sandstone.png` | 16x16 | 浅色砂岩纹理 | 高 |

### 音效资源（可选）

| 资源名 | 描述 | 优先级 |
|--------|------|--------|
| `ambient.dyedream_shore.ambience` | 海岸环境音效 | 低 |
| `ambient.dyedream_river.ambience` | 河流环境音效 | 低 |
| `ambient.dyedream_dense_forest.ambience` | 密林环境音效 | 低 |

---

## 代码变更影响范围

### Java 代码变更

| 文件 | 修改类型 | 影响范围 |
|------|---------|---------|
| `PDBlocks.java` | 新增 | 注册2个新方块 |
| `DyedreamDecorations.java` | 新增 | 注册3个新装饰物 |
| `ModDecorations.java` | 修改 | 添加新装饰物注册调用 |

### 资源文件变更

| 文件类型 | 修改数量 | 影响范围 |
|----------|---------|---------|
| 群系 JSON | 新增3个，修改6个 | 群系配置和粒子效果 |
| 维度 JSON | 修改1个 | multi_noise 参数 |
| noise_settings JSON | 修改1个 | surface_rule、sea_level |
| 方块状态 JSON | 新增2个 | 方块渲染 |
| 方块模型 JSON | 新增2个 | 方块渲染 |
| 语言文件 JSON | 修改1个 | 翻译 |
| 纹理 PNG | 新增2个 | 方块外观 |

---

## 测试要点

### 群系测试

1. ✅ 深海 → 海洋 → 海岸 → 平原 过渡是否平滑
2. ✅ 河流是否穿插在陆地群系中
3. ✅ 密林是否出现在平原内部
4. ✅ 各群系生物是否正确生成

### 地表测试

1. ✅ Y > 55 是否显示正确地表材质
2. ✅ 水边是否生成 sand 沙滩带
3. ✅ Y ≤ 0 是否显示 deepstone
4. ✅ 冰原区域是否使用 ice 材质

### 氛围测试

1. ✅ 各群系粒子效果是否正常显示
2. ✅ 粒子颜色是否正确
3. ✅ 海平面是否为55
4. ✅ 海岸线是否有悬崖和浅滩

### 装饰物测试

1. ✅ 地下是否生成悬挂水晶
2. ✅ 洞穴顶部是否有钟乳石
3. ✅ 洞穴底部是否有水晶花园
4. ✅ 装饰物生成密度是否合理

---

## 完成标准

| 标准 | 描述 |
|------|------|
| 编译通过 | `gradlew compileJava` 无错误 |
| DataGen 通过 | `gradlew runData` 无错误 |
| 客户端启动 | `gradlew runClient` 正常启动 |
| 群系生成 | 9个群系正确生成，过渡平滑 |
| 地表分层 | 4层深度正确显示 |
| 粒子效果 | 6个群系粒子效果正常 |
| 装饰物 | 地下装饰物正确生成 |
| 语言文件 | 所有新增内容有中文翻译 |
