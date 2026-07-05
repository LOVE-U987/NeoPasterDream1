# 染梦世界地形优化方案

> 基于 Eternal Starlight 模组的设计理念，对染梦世界进行全面地形优化
> 
> **参考模组**: `eternal-starlight/`
> **目标维度**: `dyedream_world`
> **版本**: Minecraft 1.21.1 / NeoForge 21.1.219

---

## 一、设计理念来源

### 1.1 Eternal Starlight 的核心设计模式

通过分析 `eternal-starlight` 模组的世界生成代码，提炼出以下关键设计模式：

| 设计模式 | ES 实现方式 | 染梦世界现状 | 优化方向 |
|---------|-----------|-------------|---------|
| **多层群系过渡** | 21个群系，包含 shore/river 等过渡群系 | 6个群系，缺少过渡 | 新增3个过渡群系 |
| **Y层地表分层** | `vertical_gradient` + `not(y_above)` 创建地下岩层 | 仅简单 grass→dirt 分层 | 4层深度分层 |
| **水域邻近检测** | `minecraft:water` 条件实现沙滩带 | 无 | 添加水域邻近条件 |
| **群系专属粒子** | 每个群系有独特粒子效果 | 无 | 6种粒子效果 |
| **低海平面** | sea_level=48，营造戏剧性海岸线 | sea_level=63 | 降至55 |
| **多层Feature填充** | 填充全部11层Feature层 | 仅用第10层 | 扩展到5层 |

### 1.2 为什么这样设计

**问题诊断**：当前染梦世界存在以下视觉问题：
1. **过渡突兀**：海洋→陆地直接切换，没有缓冲区域
2. **层次单一**：地下都是同一种方块，缺乏深度感
3. **氛围不足**：没有粒子效果，缺少梦幻感
4. **海岸线平淡**：海平面过高，地形起伏被淹没
5. **地下空荡**：洞穴内几乎没有装饰物

---

## 二、优化方案详细说明

### 2.1 阶段一：新增过渡群系

#### 2.1.1 新增群系清单

| 群系ID | 群系名称 | 功能定位 | 设计意图 |
|--------|---------|---------|---------|
| `biome_dyedream_shore` | 染梦海岸 | 海洋→陆地过渡带 | 解决海洋与陆地直接切换的突兀感，创建自然的海岸景观 |
| `biome_dyedream_river` | 染梦河流 | 陆地内自然水道 | 增加地形多样性，连接不同群系，提供水源 |
| `biome_dyedream_dense_forest` | 染梦密林 | 平原的密集变体 | 增加平原群系的内部多样性，营造神秘森林氛围 |

#### 2.1.2 multi_noise 参数设计

参考 ES 的参数配置方式，使用三维深度参数（depth）实现更好的群系分布：

**染梦海岸 (`biome_dyedream_shore`)**:
```json
{
  "temperature": [-1.0, 1.0],
  "humidity": [-1.0, 1.0],
  "continentalness": [-0.19, -0.11],  // 紧邻海洋边缘
  "weirdness": [-1.0, 1.0],
  "erosion": [-1.0, 1.0],
  "depth": [-1.0, 1.0],               // 全深度
  "offset": 0
}
```
**设计原因**：continentalness 值介于海洋（-0.7~-0.11）和平原（-0.11~0.5）之间，确保海岸群系出现在两者交界处。

**染梦河流 (`biome_dyedream_river`)**:
```json
{
  "temperature": [-1.0, 1.0],
  "humidity": [-1.0, 1.0],
  "continentalness": [-0.11, 1.0],
  "weirdness": [-1.0, -0.9333],       // 极低怪异度 = 河流走向
  "erosion": [0.55, 1.0],             // 高侵蚀度 = 水道形成
  "depth": [-1.0, 1.0],
  "offset": 0
}
```
**设计原因**：weirdness 值极低时形成线性特征（河流），高 erosion 值确保形成水道凹陷。

**染梦密林 (`biome_dyedream_dense_forest`)**:
```json
{
  "temperature": [-0.35, 0.1],
  "humidity": [-0.1, 0.3],
  "continentalness": [-0.11, 0.5],
  "weirdness": [0.2, 0.6],            // 中等怪异度 = 密集区域
  "erosion": [-0.19, 0.3],
  "depth": 0,
  "offset": 0
}
```
**设计原因**：与平原群系共享基础参数，但 weirdness 范围不同，形成平原内部的密集区域。

#### 2.1.3 群系效果配置

**染梦海岸**：
- 天空/雾色：温暖的粉紫色调
- 粒子：`bubble`（海浪气泡），概率 0.001
- 生物：粉史莱姆

**染梦河流**：
- 天空/雾色：清新的蓝紫色调
- 粒子：`bubble`（流水气泡），概率 0.002
- 生物：粉史莱姆

**染梦密林**：
- 天空/雾色：深邃的紫绿色调
- 粒子：`dust`（粉紫色花粉），概率 0.0015
- 生物：粉史莱姆、Allay（增加梦幻感）

---

### 2.2 阶段二：地表材质分层

#### 2.2.1 Y层设计方案

```
┌─────────────────────────────────────────────────────────────────┐
│ 深度分层示意图                                                   │
├─────────────────────────────────────────────────────────────────┤
│ Y > 55  ──────────────────────────────────────► 地表层           │
│   平原: dyedream_grass (水边→dyedream_sand) → dyedream_dirt     │
│   冰原: snow_block → dyedream_packed_ice                       │
│   海岸: dyedream_sand → dyedream_grass (内陆过渡)               │
├─────────────────────────────────────────────────────────────────┤
│ 32 < Y ≤ 55  ────────────────────────────────► 浅层地下         │
│   平原/海岸: dyedream_sandstone (近海区域)                       │
│   冰原: dyedream_packed_ice                                     │
├─────────────────────────────────────────────────────────────────┤
│ 0 < Y ≤ 32  ────────────────────────────────► 中层地下          │
│   全群系: dyedream_block (现有主方块)                            │
├─────────────────────────────────────────────────────────────────┤
│ Y ≤ 0  ──────────────────────────────────────► 深层地下          │
│   全群系: dyedream_deepstone (新增深色变体)                      │
└─────────────────────────────────────────────────────────────────┘
```

#### 2.2.2 surface_rule 实现策略

参考 ES 的 `sequence` + `condition` + `vertical_gradient` 模式：

```json
{
  "type": "minecraft:sequence",
  "sequence": [
    {
      "type": "minecraft:condition",
      "if_true": { "type": "minecraft:y_above", "y": 55 },
      "then_run": {
        "type": "minecraft:sequence",
        "sequence": [
          {
            "type": "minecraft:condition",
            "if_true": { "type": "minecraft:biome", "biome_is": ["pasterdream:biome_dyedream_2"] },
            "then_run": { "type": "minecraft:block", "result_state": { "Name": "minecraft:snow_block" } }
          },
          {
            "type": "minecraft:condition",
            "if_true": { "type": "minecraft:water" },
            "then_run": { "type": "minecraft:block", "result_state": { "Name": "pasterdream:dyedream_sand" } }
          },
          {
            "type": "minecraft:block",
            "result_state": { "Name": "pasterdream:dyedream_grass_block" }
          }
        ]
      }
    },
    {
      "type": "minecraft:condition",
      "if_true": { "type": "minecraft:y_above", "y": 32 },
      "then_run": {
        "type": "minecraft:condition",
        "if_true": { "type": "minecraft:biome", "biome_is": ["pasterdream:biome_dyedream_2"] },
        "then_run": { "type": "minecraft:block", "result_state": { "Name": "pasterdream:dyedream_packed_ice" } },
        "else_run": { "type": "minecraft:block", "result_state": { "Name": "pasterdream:dyedream_sandstone" } }
      }
    },
    {
      "type": "minecraft:condition",
      "if_true": { "type": "minecraft:y_above", "y": 0 },
      "then_run": { "type": "minecraft:block", "result_state": { "Name": "pasterdream:dyedream_block" } }
    },
    {
      "type": "minecraft:block",
      "result_state": { "Name": "pasterdream:dyedream_deepstone" }
    }
  ]
}
```

#### 2.2.3 设计原因

| 设计决策 | 原因 |
|---------|------|
| Y > 55 为地表 | 海平面降至55，确保地表完全暴露 |
| 水边检测 sand | 创建自然的沙滩带效果 |
| Y ≤ 0 使用 deepstone | 增加地下深度感，深色方块营造神秘地下氛围 |
| 冰原使用 packed_ice | 保持冰原的寒冷主题一致性 |

---

### 2.3 阶段三：氛围增强

#### 2.3.1 群系粒子效果配置

| 群系 | 粒子类型 | 参数 | 概率 | 效果描述 |
|------|---------|------|------|---------|
| `biome_dyedream_0` (平原) | `dust` | [0.7, 0.3, 0.9, 1.0] 粉紫色 | 0.0015 | 梦幻花粉漂浮 |
| `biome_dyedream_1` (温暖平原) | `dust` | [0.9, 0.5, 0.3, 1.0] 橙粉色 | 0.002 | 温暖光芒粒子 |
| `biome_dyedream_2` (冰原) | `snowflake` | - | 0.002 | 冰晶飘落 |
| `biome_dyedream_mushroom_plains` (蘑菇平原) | `glow` | - | 0.001 | 萤火虫般的发光粒子 |
| `biome_dyedream_shore` (海岸) | `bubble` | - | 0.001 | 海浪气泡 |
| `biome_dyedream_river` (河流) | `bubble` | - | 0.002 | 流水气泡 |

**设计原因**：每个群系的粒子效果与其主题相匹配，增强沉浸感：
- 平原：粉紫色花粉 → 梦幻感
- 温暖平原：橙粉色 → 温暖感
- 冰原：雪花 → 寒冷感
- 蘑菇平原：发光粒子 → 神秘感
- 海岸/河流：气泡 → 水元素感

#### 2.3.2 海平面调整

**当前值**: `sea_level: 63`  
**目标值**: `sea_level: 55`

**设计原因**：
1. ES 使用 sea_level=48，效果非常好
2. 降低海平面可以：
   - 暴露更多海岸线和悬崖
   - 创造浅滩和潮间带
   - 增强地形立体感
   - 让玩家更容易探索海底

---

### 2.4 阶段四：装饰物层扩展

#### 2.4.1 Feature层填充方案

| Feature层 | 层名称 | 装饰物类型 | 群系 | 设计意图 |
|-----------|-------|-----------|------|---------|
| 第1层 | raw_generation | 大型水晶脉、热泉 | 冰原/平原 | 世界生成初期的大型地质特征 |
| 第3层 | local_modifications | 地表水晶斑块 | 所有陆地群系 | 地表细节装饰 |
| 第7层 | underground_decoration | 悬挂水晶、钟乳石 | 所有群系地下 | 洞穴内装饰 |
| 第8层 | vegetal_decoration | 蘑菇森林 | 蘑菇平原 | 植被层装饰 |
| 第10层 | top_layer_modification | 现有装饰物 | 各群系 | 地表装饰物 |

#### 2.4.2 新增地下装饰物

| 装饰物ID | 类型 | 描述 | 设计意图 |
|---------|------|------|---------|
| `hanging_crystal` | SPIKE (倒置) | 洞穴顶部悬挂的发光水晶 | 填补洞穴顶部空白，增加神秘感 |
| `crystal_stalactite` | SPIKE (倒置) | 钟乳石状水晶 | 形成洞穴钟乳石景观 |
| `underground_crystal_garden` | SCATTER | 洞穴底部的水晶簇 | 增加洞穴底部的丰富度 |

#### 2.4.3 设计原因

当前染梦世界仅使用第10层，导致：
- 地下完全空荡
- 缺少地质特征
- 地形层次单一

扩展到多层后：
- 第1层：创建世界骨架（大型地质特征）
- 第3层：添加地表细节
- 第7层：填充地下空间
- 第8层：增加植被多样性
- 第10层：保持现有装饰

---

## 三、新增方块需求

### 3.1 新增方块清单

| 方块ID | 方块名称 | 材质要求 | 用途 | 获取方式 |
|--------|---------|---------|------|---------|
| `dyedream_deepstone` | 染梦深层石 | 深色变体，带紫色微光 | 深层地下基础方块 | 自然生成 Y ≤ 0 |
| `dyedream_sandstone` | 染梦砂岩 | 染梦沙的硬化版 | 近海区域浅层地下 | 自然生成近海区域 |

### 3.2 纹理设计建议

| 方块 | 纹理风格 | 参考 |
|------|---------|------|
| `dyedream_deepstone` | 深色、带紫色荧光颗粒 | 类似 ES 的 voidstone |
| `dyedream_sandstone` | 浅色、带粉紫色调 | 类似 ES 的 twilight_sand |

---

## 四、实施步骤

### 4.1 准备工作

1. ✅ 确认 `eternal-starlight` 模组作为参考
2. ✅ 备份当前 `dyedream_world` 配置
3. ✅ 确认项目编译通过

### 4.2 阶段一：新增过渡群系（预计耗时：2小时）

| 步骤 | 任务 | 文件 |
|------|------|------|
| 1 | 创建 `biome_dyedream_shore.json` | `worldgen/biome/` |
| 2 | 创建 `biome_dyedream_river.json` | `worldgen/biome/` |
| 3 | 创建 `biome_dyedream_dense_forest.json` | `worldgen/biome/` |
| 4 | 更新 `dyedream_world.json` 维度配置，添加新群系 | `dimension/` |
| 5 | 添加语言文件翻译 | `assets/pasterdream/lang/zh_cn.json` |

### 4.3 阶段二：地表材质分层（预计耗时：3小时）

| 步骤 | 任务 | 文件 |
|------|------|------|
| 1 | 注册新增方块 `dyedream_deepstone`、`dyedream_sandstone` | `PDBlocks.java` |
| 2 | 创建方块模型和纹理 | `assets/pasterdream/models/block/` |
| 3 | 更新 `noise_settings/dyedream_world.json` 的 surface_rule | `worldgen/noise_settings/` |
| 4 | 添加语言文件翻译 | `assets/pasterdream/lang/zh_cn.json` |

### 4.4 阶段三：氛围增强（预计耗时：1.5小时）

| 步骤 | 任务 | 文件 |
|------|------|------|
| 1 | 为6个群系添加粒子效果 | `worldgen/biome/*.json` |
| 2 | 修改海平面为55 | `noise_settings/dyedream_world.json` |
| 3 | 测试粒子效果 | 运行客户端 |

### 4.5 阶段四：装饰物层扩展（预计耗时：3小时）

| 步骤 | 任务 | 文件 |
|------|------|------|
| 1 | 创建地下装饰物类 | `DyedreamDecorations.java` |
| 2 | 注册新装饰物到相应群系 | `ModDecorations.java` |
| 3 | 生成 configured_feature JSON | Python 脚本 |
| 4 | 生成 placed_feature JSON | Python 脚本 |
| 5 | 生成 biome_modifier JSON | Python 脚本 |
| 6 | 添加语言文件翻译 | `assets/pasterdream/lang/zh_cn.json` |

### 4.6 测试验证（预计耗时：2小时）

| 步骤 | 任务 |
|------|------|
| 1 | 编译验证 | `gradlew compileJava` |
| 2 | DataGen 验证 | `gradlew runData` |
| 3 | 客户端测试 | `gradlew runClient` |
| 4 | 检查群系过渡是否平滑 |
| 5 | 检查地表分层是否正确 |
| 6 | 检查粒子效果是否正常 |
| 7 | 检查装饰物是否生成 |

---

## 五、预期效果对比

### 5.1 群系过渡效果

```
优化前:
  深海 ←→ 海洋 ←→ 平原 (直接切换，生硬)

优化后:
  深海 → 海洋 → 海岸 → 平原 → 密林 (平滑过渡，自然)
                 ↘ 河流 ↗
```

### 5.2 地表层次效果

```
优化前:
  地表: dyedream_grass
  地下: dyedream_block (单调)

优化后:
  地表: dyedream_grass (水边→dyedream_sand)
  浅层: dyedream_sandstone (近海) / dyedream_packed_ice (冰原)
  中层: dyedream_block
  深层: dyedream_deepstone (深色神秘)
```

### 5.3 氛围效果

```
优化前:
  无粒子效果，氛围单调

优化后:
  平原: 粉紫色花粉漂浮
  温暖平原: 橙粉色光芒
  冰原: 冰晶飘落
  蘑菇平原: 萤火虫发光
  海岸: 海浪气泡
  河流: 流水气泡
```

### 5.4 海岸线效果

```
优化前 (sea_level=63):
  海岸线平缓，地形起伏被淹没

优化后 (sea_level=55):
  海岸线陡峭，悬崖+浅滩，戏剧性增强
  潮间带暴露，海底探索更容易
```

### 5.5 地下效果

```
优化前:
  洞穴内空荡，只有矿石

优化后:
  洞穴顶部: 悬挂水晶、钟乳石
  洞穴底部: 地下水晶花园
  洞穴壁: 水晶脉
```

---

## 六、风险评估

| 风险 | 概率 | 影响 | 应对措施 |
|------|------|------|---------|
| 群系参数配置不当导致过渡异常 | 中 | 群系分布混乱 | 参考 ES 参数，小步测试 |
| surface_rule 语法错误导致崩溃 | 中 | 世界无法生成 | 先验证 JSON 语法 |
| 海平面调整导致现有建筑被淹没 | 低 | 存档兼容问题 | 新地图生效，旧地图不受影响 |
| 装饰物过多导致性能问题 | 低 | 帧率下降 | 控制装饰物生成密度 |
| 新增方块缺少纹理导致紫色黑块 | 中 | 视觉错误 | 提前准备纹理资源 |

---

## 七、参考文件

### 7.1 Eternal Starlight 参考文件

| 文件 | 路径 | 参考内容 |
|------|------|---------|
| 维度配置 | `common/src/generated/resources/data/eternal_starlight/dimension/starlight.json` | multi_noise 参数配置 |
| 噪声设置 | `common/src/generated/resources/data/eternal_starlight/worldgen/noise_settings/starlight.json` | surface_rule 分层设计 |
| 群系配置 | `common/src/generated/resources/data/eternal_starlight/worldgen/biome/*.json` | 粒子效果、群系参数 |

### 7.2 当前染梦世界文件

| 文件 | 路径 | 修改内容 |
|------|------|---------|
| 维度配置 | `PasterDream/src/main/resources/data/pasterdream/dimension/dyedream_world.json` | 添加新群系 |
| 噪声设置 | `PasterDream/src/main/resources/data/pasterdream/worldgen/noise_settings/dyedream_world.json` | 修改 surface_rule、sea_level |
| 群系文件 | `PasterDream/src/main/resources/data/pasterdream/worldgen/biome/*.json` | 添加粒子效果 |
| 方块注册 | `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/PDBlocks.java` | 新增方块 |
| 装饰物注册 | `PasterDream/src/main/java/com/pasterdream/pasterdreammod/registry/DyedreamDecorations.java` | 新增装饰物 |

---

## 八、结论

通过借鉴 Eternal Starlight 的设计理念，染梦世界将从一个简单的6群系维度，升级为具有：
1. **平滑群系过渡**（9个群系）
2. **多层地表结构**（4层深度）
3. **丰富氛围效果**（粒子+海平面）
4. **立体装饰物分布**（5层Feature）

的高质量维度。整个优化过程仅涉及 JSON 配置和少量 Java 代码，风险可控，效果显著。
