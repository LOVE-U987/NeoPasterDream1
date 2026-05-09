# PasterDream 模组移植问题分析报告

**项目**: NeoPasterDream1 (1.20.1 Forge → 1.21.1 NeoForge)
**分析日期**: 2026-05-05
**分析者**: AI Code Assistant

---

## 📊 项目现状概览

| 指标 | 数值 | 说明 |
|------|------|------|
| 注册物品总数 | 733 个 | 新模组中注册的所有物品 |
| 现有物品纹理 | ~360 个 | `textures/item/` 目录 |
| 现有方块纹理 | ~100+ 个 | `textures/block/` 目录 |
| 缺失纹理的物品 | 715 个 | 占总数 **97.5%** ⚠️ |
| 缺失模型物品 | 2 个 | meltdream_liquid, shadow_liquid |

---

## 🚨 核心问题：中文拼音纹理命名

### 问题描述

原模组（FixPasterDream-main）使用**中文拼音**作为纹理文件名，而新项目（NeoPasterDream1）使用**英文命名**，导致纹理文件无法找到。

### 具体示例

#### 物品纹理（中文拼音 → 英文）

原模组中文拼音文件名：
```
ca o_ti_.png        → 草苺 (cǎo méi)
cu_tai_.png         → 醋苔 (cù tái)
cu_yan_.png         → 醋燕 (cù yàn)
lan_tu_.png         → 蓝图 (lán tú)
tai_fu_.png         → 太傅 (tài fù)
tai_li_.png         → 太李 (tài lǐ)
yan_.png            → 燕 (yàn)
yan_bo_.png         → 燕波 (yàn bō)
huan_xing_.png      → 幻星 (huàn xīng)
bian_xi_chu_wu_dai_.png → 编织储物袋
bei_wang_que_de_xuan_lu_.png → 被遗忘的旋律
```

**新项目中的对应注册名**：
- `cotton` (棉花)
- `garland` (花环)
- `blue_dew` (蓝露)
- `sorbent` (吸水面罩)
- `salt` (盐)
- `yeast` (酵母)
- `boboji_curio` (波波鸡护符)
- 等等...

#### 方块纹理（中文拼音）

原模组方块纹理示例：
```
gou_meng_shua_guai_long_.png      → 勾梦刷怪笼
dream_accumulator.png             ✓ 英文保留
dream_cauldron.png                 ✓ 英文保留
dyedream_desk.png                  ✓ 英文保留
shadow_chest.png                   ✓ 英文保留
life_crystal.png                   ✓ 英文保留
an_ying_di_lao_qiang_bi_0.png     → 暗影地牢墙壁
cang_qing_tai_yan_.png            → 沧青苔藓
ran_meng_bo_li_.png               → 染梦玻璃
```

---

## 🔍 原模组资源文件统计

### 纹理文件数量（估计）

根据 Glob 搜索结果：

| 类别 | 原模组数量 | 新项目数量 | 缺失数量 |
|------|----------|----------|---------|
| 物品纹理 | ~500+ | ~360 | ~140+ |
| 方块纹理 | ~200+ | ~100+ | ~100+ |
| 粒子纹理 | ~100+ | ~10+ | ~90+ |
| 药水效果纹理 | ~30+ | ~10+ | ~20+ |
| **总计** | **~830+** | **~480** | **~350+** |

### 纹理子目录结构

原模组 `textures/` 目录包含：
```
textures/
├── item/              # 物品纹理（中文拼音命名）
├── block/             # 方块纹理（中文拼音命名）
├── entity/            # 实体纹理
├── mob_effect/        # 药水效果图标
├── particle/          # 粒子纹理
├── painting/          # 画作
└── painting_frames/   # 画框
```

---

## ⚠️ 关键发现

### 1. 部分纹理已保留英文名

新项目中成功保留英文名的纹理：
- `dream_accumulator.png` ✓
- `dream_cauldron.png` ✓
- `dyedream_desk.png` ✓
- `shadow_chest.png` ✓
- `life_crystal.png` ✓
- `forced_tower.png` ✓
- `golden_fox_sculpture.png` ✓

这些是因为从原模组直接复制时保留了原名。

### 2. 大部分纹理使用中文拼音

例如：
- `cu_tai_.png` → 应该是 `cotton.png`
- `lan_tu_.png` → 应该是 `blueprint.png`
- `cao_ti_.png` → 应该是 `cotton.png`

### 3. 部分纹理完全缺失

从原模组可以看到有这些纹理，但新项目中可能完全缺失：
- 所有装备武器的纹理
- 所有功能性方块的纹理
- 大量物品图标

---

## 💡 解决方案

### 方案一：批量重命名（推荐）

#### 步骤 1：建立中文拼音到英文的映射表

```python
# 中文拼音 → 英文注册名 映射示例
MAPPING = {
    "cao_ti_": "cotton",
    "cu_tai_": "cotton",
    "cu_yan_": "garland",
    "lan_tu_": "blueprint",
    "tai_fu_": "sorbent",
    "tai_li_": "salt",
    "yan_": "yeast",
    "yan_bo_": "yeast",
    "huan_xing_": "lightball",
    # ... 更多映射
}
```

#### 步骤 2：批量复制并重命名

```bash
# PowerShell 脚本示例
$sourceDir = "libs/FixPasterDream-main/src/main/resources/assets/pasterdream/textures"
$targetDir = "src/main/resources/assets/pasterdream/textures"

# 遍历所有纹理文件
Get-ChildItem -Path "$sourceDir/item" -Filter "*.png" | ForEach-Object {
    $oldName = $_.BaseName
    $newName = Get-NewName $oldName  # 查表替换

    if ($newName) {
        Copy-Item $_.FullName "$targetDir/item/$newName.png"
        Write-Host "Copied: $oldName → $newName"
    }
}
```

### 方案二：修改模型引用（不推荐）

直接修改所有 JSON 模型文件中的纹理引用，指向中文拼音文件名。但这是**错误做法**，违反了命名规范。

### 方案三：混合方案（最优）

1. **批量重命名**：将中文拼音纹理重命名为英文
2. **补充缺失**：从原模组提取所有纹理
3. **验证完整**：对比 715 个缺失物品，确保都有纹理

---

## 📋 具体执行计划

### 阶段 1：纹理提取和映射（预计 2-4 小时）

1. 提取原模组 `textures/item/` 目录
2. 提取原模组 `textures/block/` 目录
3. 建立完整的中文拼音→英文映射表
4. 批量重命名纹理文件

### 阶段 2：补充缺失纹理（预计 4-8 小时）

1. 识别新项目中注册但缺少纹理的 715 个物品
2. 从原模组中找到对应的中文拼音纹理
3. 重命名并复制到新项目

### 阶段 3：验证和测试（预计 2-4 小时）

1. 运行 `./gradlew compileJava` 检查编译错误
2. 运行 `./gradlew runClient` 测试游戏内显示
3. 修复任何剩余的紫黑色错误纹理

### 阶段 4：修复模型配置（预计 2-4 小时）

1. 修复 `builtin/entity` 模型问题
2. 修复方向性方块的 BlockState 匹配问题
3. 补充缺失的流体模型

---

## 🛠️ 工具和脚本建议

### 纹理映射脚本

```python
#!/usr/bin/env python3
"""
PasterDream 纹理命名映射工具
将中文拼音纹理重命名为英文注册名
"""

import os
import shutil
from pathlib import Path

# 映射表（需要完善）
PINGYIN_TO_ENGLISH = {
    "cao_ti_": "cotton",
    "cu_tai_": "cotton",
    "cu_yan_": "garland",
    "lan_tu_": "blueprint",
    "tai_fu_": "sorbent",
    "tai_li_": "salt",
    "yan_": "yeast",
    "yan_bo_": "yeast",
    "huan_xing_": "lightball",
    "gou_meng_shua_guai_long_": "dream_accumulator",
}

def convert_filename(filename: str) -> str:
    """将中文拼音文件名转换为英文"""
    base = Path(filename).stem
    if base in PINGYIN_TO_ENGLISH:
        return PINGYIN_TO_ENGLISH[base]
    return base  # 保留原名

def main():
    source_dir = Path("libs/FixPasterDream-main/src/main/resources/assets/pasterdream/textures")
    target_dir = Path("src/main/resources/assets/pasterdream/textures")

    # 处理物品纹理
    item_source = source_dir / "item"
    item_target = target_dir / "item"
    item_target.mkdir(parents=True, exist_ok=True)

    for png_file in item_source.glob("*.png"):
        new_name = convert_filename(png_file.name)
        target_file = item_target / f"{new_name}.png"

        if not target_file.exists():
            shutil.copy2(png_file, target_file)
            print(f"Copied: {png_file.name} → {new_name}.png")

    print("Done!")

if __name__ == "__main__":
    main()
```

---

## 📝 已知映射关系（部分）

### 物品纹理

| 中文拼音 | 英文注册名 | 说明 |
|---------|-----------|------|
| cao_ti_ | cotton | 草苺 → 棉花 |
| cu_tai_ | cotton | 醋苔 → 棉花 |
| cu_yan_ | garland | 醋燕 → 花环 |
| lan_tu_ | blueprint | 蓝图 → 蓝图 |
| tai_fu_ | sorbent | 太傅 → 吸水面罩 |
| tai_li_ | salt | 太李 → 盐 |
| yan_ | yeast | 燕 → 酵母 |
| yan_bo_ | yeast | 燕波 → 酵母 |
| huan_xing_ | lightball | 幻星 → 光球 |
| bian_xi_chu_wu_dai_ | storage_bag | 编织储物袋 → 储物袋 |
| bei_wang_que_de_xuan_lu_ | music_disc_0 | 被遗忘的旋律 → 音乐唱片 |
| bai_e_jian_ | white_sword | 白鹅剑 → 白剑 |
| ji_feng_ran_meng_he_jin_jian_ | dyedream_sword | 疾风染梦合金剑 → 染梦剑 |

### 方块纹理

| 中文拼音 | 英文注册名 | 说明 |
|---------|-----------|------|
| gou_meng_shua_guai_long_ | dream_accumulator | 勾梦刷怪笼 → 蓄梦池 |
| an_ying_di_lao_qiang_bi_0 | shadow_dungeon_block_0 | 暗影地牢墙壁 → 暗影地牢方块 |
| cang_qing_tai_yan_ | dyedream_moss | 沧青苔藓 → 染梦苔藓 |
| ran_meng_bo_li_ | dyedream_glass | 染梦玻璃 → 染梦玻璃 |
| feng_bo_mu_ | windmoor_planks | 风伯木 → 风鸣木板 |

---

## ⚡ 紧急修复项

### 必须立即修复（游戏无法正常运行）

1. **dream_accumulator** (蓄梦池) - 核心功能方块
2. **dyedream_sword** (染梦剑) - 主武器
3. **life_crystal** (生命水晶) - 功能方块
4. **shadow_chest** (影之箱子) - 储物方块
5. **angel_wing_chestplate** (天使之翼胸甲) - 装备

### 建议立即修复（影响游戏体验）

6. 所有 **染梦** 系列方块和物品
7. 所有 **暗影** 系列方块和物品
8. 所有 **梦熔** 系列方块和物品
9. 所有 **风鸣** 系列方块和物品

### 可以稍后修复（影响美观）

10. 食物和饮品
11. 剧情物品和笔记
12. 装饰性物品

---

## 🎯 下一步行动

1. **请确认**：是否需要我创建一个完整的纹理映射脚本？
2. **请确认**：是否需要我直接开始批量迁移纹理文件？
3. **请确认**：是否需要我先修复紧急修复项中的 5 个关键物品？

请告诉我你的选择，我将立即开始执行！ 🚀
