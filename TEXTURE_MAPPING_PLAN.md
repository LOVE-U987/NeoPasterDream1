# PasterDream 模组纹理移植完整协调方案

## 📋 核心发现

### 1. 原模组纹理命名现状

**物品纹理 (item/)**：约 **200+ 个**，全部使用**中文拼音**

**方块纹理 (block/)**：约 **200+ 个**，混合使用：
- 中文拼音（如 `ran_meng_shi_ying_kuai_.png`）
- 部分英文（如 `dream_accumulator.png`, `life_crystal.png`）

### 2. 新项目注册名（英文）

新项目使用**纯英文注册名**，例如：
- `cotton` (原 `cao_ti_`)
- `blueprint` (原 `lan_tu_`)
- `dyedream_sword` (原 `ji_feng_ran_meng_he_jin_jian_`)

### 3. 关键问题

**模型引用关系**：
```
模型文件 (models/item/xxx.json)
    ↓ 引用
纹理文件 (textures/item/xxx.png)
    ↓ 必须匹配
Java注册名 (PDItems.java)
```

**当前断裂点**：
- 模型引用的是**中文拼音**纹理名
- Java注册的是**英文**名
- 导致纹理找不到 → 紫黑色错误

---

## 🎯 协调方案：三层一致性

### 方案选择

**方案A：修改模型引用**（不推荐）
- 修改所有JSON模型文件，指向中文拼音纹理
- ❌ 问题：违反命名规范，后期维护困难

**方案B：重命名纹理**（✅ 推荐）
- 将中文拼音纹理重命名为英文
- ✅ 优点：符合规范，模型无需修改

**方案C：混合方案**（最优）
- 重命名纹理为英文
- 同时检查并修复模型中的`builtin/entity`问题
- ✅ 优点：彻底解决所有问题

---

## 📊 完整映射表（关键物品）

### 物品纹理映射

| 中文拼音文件名 | 英文注册名 | 说明 | 优先级 |
|--------------|-----------|------|--------|
| `cao_ti_.png` | `cotton.png` | 草苺→棉花 | 🔴 高 |
| `cu_tai_.png` | `cotton.png` | 醋苔→棉花 | 🔴 高 |
| `lan_tu_.png` | `blueprint.png` | 蓝图→蓝图 | 🔴 高 |
| `tai_fu_.png` | `sorbent.png` | 太傅→吸水面罩 | 🔴 高 |
| `tai_li_.png` | `salt.png` | 太李→盐 | 🔴 高 |
| `yan_.png` | `yeast.png` | 燕→酵母 | 🔴 高 |
| `yan_bo_.png` | `yeast.png` | 燕波→酵母 | 🔴 高 |
| `huan_xing_.png` | `lightball.png` | 幻星→光球 | 🟡 中 |
| `bian_xi_chu_wu_dai_.png` | `storage_bag.png` | 编织储物袋 | 🟡 中 |
| `bei_wang_que_de_xuan_lu_.png` | `music_disc_0.png` | 被遗忘的旋律 | 🟡 中 |
| `bai_e_jian_.png` | `white_sword.png` | 白鹅剑→白剑 | 🔴 高 |
| `ji_feng_ran_meng_he_jin_jian_.png` | `dyedream_sword.png` | 染梦剑 | 🔴 高 |
| `dream_meter.png` | `dream_meter.png` | 梦境计量器 | ✅ 已英文 |
| `doremys_guidebook.png` | `doremys_guidebook.png` | 哆来咪的指南 | ✅ 已英文 |
| `christmas_lights.png` | `christmas_lights.png` | 圣诞灯 | ✅ 已英文 |

### 方块纹理映射

| 中文拼音文件名 | 英文注册名 | 说明 | 优先级 |
|--------------|-----------|------|--------|
| `gou_meng_shua_guai_long_.png` | `dream_accumulator.png` | 蓄梦池 | 🔴 高 |
| `ran_meng_shi_ying_kuai_.png` | `dyedream_quartz_block.png` | 染梦石英块 | 🔴 高 |
| `ran_meng_bo_li_.png` | `dyedream_glass.png` | 染梦玻璃 | 🔴 高 |
| `yin_ying_shi_zhuan_.png` | `shadow_stone_bricks.png` | 暗影石砖 | 🔴 高 |
| `feng_bo_mu_.png` | `windmoor_planks.png` | 风鸣木板 | 🔴 高 |
| `dream_accumulator.png` | `dream_accumulator.png` | 蓄梦池 | ✅ 已英文 |
| `life_crystal.png` | `life_crystal.png` | 生命水晶 | ✅ 已英文 |
| `shadow_chest.png` | `shadow_chest.png` | 影之箱子 | ✅ 已英文 |
| `forced_tower.png` | `forced_tower.png` | 强制塔 | ✅ 已英文 |

---

## 🔧 模型修复清单

### 需要修复 `builtin/entity` 的物品模型

这些方块在原模组中使用 TESR（TileEntitySpecialRenderer），item model 使用 `builtin/entity`：

| 方块名 | 当前问题 | 修复方案 |
|--------|---------|---------|
| `dream_accumulator` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `dream_cauldron` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `dream_meter` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `life_crystal` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `shadow_chest` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `shadow_vortex` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `meltdream_chest_0` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `meltdream_chest_1` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `forced_tower` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `golden_fox_sculpture` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `guard_crystal` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `research_table` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `weapon_table` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `weapon_workshop` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `shadow_blast_furnace` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `shadow_brazier` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `twilight_lantern` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `uuz_doll_0` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `qym_doll_0` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `picnic_basket` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `birds_nest` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `ecology_glass_jar` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `desert_hero_tomb` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `broken_shadow_dungeon_portal` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `aaroncos_hand_chest` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `aaroncos_hand_spawn_block` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `wind_knight_spawnblock` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `the_endless_book_of_dream_seekers` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `shadow_trap_0` | 创造模式透明 | 改为 `item/generated` + 纹理 |
| `shadow_dungeon_portal` | 创造模式透明 | 改为 `item/generated` + 纹理 |

---

## 🛠️ 执行步骤

### 阶段 1：纹理重命名（2-4 小时）

1. **提取所有中文拼音纹理**
   ```bash
   # 从原模组复制所有纹理
   cp -r libs/FixPasterDream-main/src/main/resources/assets/pasterdream/textures/* \
         src/main/resources/assets/pasterdream/textures/
   ```

2. **批量重命名**
   - 使用映射表将中文拼音文件名改为英文
   - 保持英文文件名不变

3. **验证**
   - 检查是否所有纹理都有对应的英文名称
   - 确保没有遗漏

### 阶段 2：模型修复（2-4 小时）

1. **修复 `builtin/entity` 模型**
   - 将所有使用 `builtin/entity` 的 item model 改为 `item/generated`
   - 添加正确的纹理引用

2. **修复 `#missing` 纹理引用**
   - 在 custom 模型中，将所有 `#missing` 改为 `#0` 或其他有效引用

3. **验证模型完整性**
   - 确保所有模型引用的纹理都存在

### 阶段 3：Java 代码检查（1-2 小时）

1. **检查 PDItems.java**
   - 确保所有物品注册名与纹理名一致

2. **检查 PDBlocks.java**
   - 确保所有方块注册名与纹理名一致
   - 检查方向性方块是否正确使用 `registerBlock`

### 阶段 4：测试验证（2-4 小时）

1. **编译测试**
   ```bash
   ./gradlew compileJava
   ```

2. **运行测试**
   ```bash
   ./gradlew runClient
   ```

3. **游戏内验证**
   - 检查所有物品是否正常显示
   - 检查所有方块是否正常显示
   - 检查创造模式物品栏

---

## 📁 文件结构（目标状态）

```
src/main/resources/assets/pasterdream/
├── textures/
│   ├── item/
│   │   ├── cotton.png          # 原 cao_ti_.png
│   │   ├── blueprint.png       # 原 lan_tu_.png
│   │   ├── dyedream_sword.png  # 原 ji_feng_ran_meng_he_jin_jian_.png
│   │   ├── dream_meter.png     # 保持
│   │   └── ... (所有英文命名)
│   ├── block/
│   │   ├── dream_accumulator.png   # 保持
│   │   ├── dyedream_quartz_block.png # 原 ran_meng_shi_ying_kuai_.png
│   │   ├── life_crystal.png        # 保持
│   │   └── ... (所有英文命名)
│   ├── entity/                 # 实体纹理
│   ├── particle/               # 粒子纹理
│   └── ...
├── models/
│   ├── item/
│   │   ├── cotton.json         # { "parent": "item/generated", "textures": { "layer0": "pasterdream:item/cotton" } }
│   │   ├── dream_accumulator.json  # { "parent": "item/generated", ... } (修复后)
│   │   └── ...
│   ├── block/
│   │   ├── dream_accumulator.json
│   │   └── ...
│   └── custom/                 # Blockbench 自定义模型
│       ├── dreamaccumulator.json
│       └── ...
└── blockstates/
    ├── dream_accumulator.json
    └── ...
```

---

## ✅ 成功标准

1. **纹理**：所有纹理文件使用英文命名，无中文拼音
2. **模型**：所有模型正确引用纹理，无 `builtin/entity`（除必要外）
3. **Java**：所有注册名与纹理名一致
4. **游戏内**：无紫黑色错误纹理，创造模式物品正常显示

---

## 🚀 下一步行动

请告诉我你希望：

1. **立即开始执行？** → 我将创建 Python 脚本自动完成纹理重命名和模型修复
2. **先修复关键物品？** → 我将优先处理 5-10 个最重要的物品
3. **需要更多映射？** → 我将扩展映射表，覆盖更多物品
4. **其他需求？** → 请告诉我你的具体想法
