# PasterDream 装备、武器、配方、套装效果与方块移植 - 产品需求文档

## Overview
- **Summary**: 移植原 FixPasterDream 模组中的装备（盔甲套装）、武器工具、配方数据、套装效果以及染梦维度剩余方块，并为遗迹结构添加战利品列表。
- **Purpose**: 完善 NeoForge 1.21.1 版本的 PasterDream 模组功能，确保与原模组功能一致。
- **Target Users**: Minecraft 玩家、模组开发者

## Goals
- 移植 5 套盔甲套装（Copper/Titanium/Sculk/Dyedream/Qym）
- 实现套装效果（染梦盔甲护盾效果、潜声盔甲效果）
- 补全缺失的武器和工具
- 生成配方 JSON（合成、熔炉、切石机等）
- 移植染梦维度剩余方块
- 添加遗迹结构战利品列表

## Non-Goals (Out of Scope)
- 不移植 Tetra 模组相关的工具数据（原模组的 tetra 材料系统）
- 不移植 GeckoLib 动画（仅移植静态物品/方块）
- 不修改原模组代码（libs/ 只读参考）

## Background & Context
- 当前项目已移植了部分材料、食物、工具和武器，但盔甲套装和套装效果完全缺失
- 原模组使用 MCreator 生成，代码结构不适合直接移植
- 需要使用 NeoForge 1.21.1 API 重新实现盔甲材料和套装效果逻辑
- 配方和战利品表需要重新生成以适配 1.21 新格式

## Functional Requirements

### FR-1: 盔甲套装注册
- **FR-1.1**: 注册 Copper Armor（头盔、胸甲、护腿、靴子）
- **FR-1.2**: 注册 Titanium Armor（头盔、胸甲、护腿、靴子）
- **FR-1.3**: 注册 Sculk Armor（头盔、胸甲、护腿、靴子）
- **FR-1.4**: 注册 Dyedream Armor（头盔、胸甲、护腿、靴子）
- **FR-1.5**: 注册 Qym Armor（头盔、胸甲、护腿、靴子）

### FR-2: 套装效果实现
- **FR-2.1**: Dyedream Armor 套装效果：穿戴全套时最大生命值+4，随时间获得护盾，增强手持染梦工具
- **FR-2.2**: Sculk Armor 套装效果：类似增强效果

### FR-3: 补全武器和工具
- **FR-3.1**: 注册 Meltdream 工具系列（axe, shovel, hoe）
- **FR-3.2**: 注册 Shadow Erosion 工具系列（axe, hoe, shovel）
- **FR-3.3**: 注册 Moltengold 工具系列（axe, shovel, hoe）
- **FR-3.4**: 注册法杖系列（moltengold_wand, dream_wand 等）

### FR-4: 配方生成
- **FR-4.1**: 生成盔甲合成配方
- **FR-4.2**: 生成工具合成配方
- **FR-4.3**: 生成熔炉/高炉配方
- **FR-4.4**: 生成切石机配方

### FR-5: 方块移植
- **FR-5.1**: 移植染梦维度剩余的缺失方块
- **FR-5.2**: 生成方块战利品表
- **FR-5.3**: 生成方块挖掘标签

### FR-6: 遗迹战利品列表
- **FR-6.1**: 为各遗迹结构添加战利品表配置
- **FR-6.2**: 战利品包含材料、装备、稀有物品

## Non-Functional Requirements
- **NFR-1**: 所有注册必须使用 `DeferredRegister` 模式
- **NFR-2**: 代码遵循项目代码规范（命名、注释）
- **NFR-3**: 编译通过，无语法错误
- **NFR-4**: 语言文件完整（zh_cn.json）

## Constraints
- **Technical**: NeoForge 1.21.1, Java 21, GeckoLib 4.7.3
- **Dependencies**: 依赖现有的 ItemMigrationAPI、BlockAPI
- **1.21 Data Format**: 使用单数目录名（recipe/, loot_table/, structure/）

## Assumptions
- 原模组的装备属性数据（耐久、防御、韧性）已通过代码分析获取
- 原模组纹理文件可直接复制使用
- ItemMigrationAPI 的 RecipeGenerator 和 LootTableGenerator 可用

## Acceptance Criteria

### AC-1: 盔甲注册完成
- **Given**: 编译项目
- **When**: 运行 `.\gradlew compileJava`
- **Then**: 5套盔甲（20个物品）成功注册，无编译错误
- **Verification**: `programmatic`

### AC-2: 套装效果生效
- **Given**: 玩家穿戴全套染梦盔甲
- **When**: 进入游戏并查看玩家属性
- **Then**: 最大生命值增加4，随时间获得护盾效果
- **Verification**: `human-judgment`

### AC-3: 武器工具补全
- **Given**: 编译项目
- **When**: 运行 `.\gradlew compileJava`
- **Then**: 所有缺失的武器和工具成功注册
- **Verification**: `programmatic`

### AC-4: 配方生成完整
- **Given**: 运行游戏
- **When**: 在工作台合成装备/工具
- **Then**: 配方正确显示并可合成
- **Verification**: `human-judgment`

### AC-5: 方块移植完成
- **Given**: 编译项目
- **When**: 运行 `.\gradlew compileJava`
- **Then**: 染梦维度剩余方块成功注册，战利品表和挖掘标签生成正确
- **Verification**: `programmatic`

### AC-6: 遗迹战利品配置
- **Given**: 运行游戏
- **When**: 探索遗迹并打开宝箱
- **Then**: 宝箱包含配置的战利品物品
- **Verification**: `human-judgment`

## Open Questions
- [ ] Qym Armor 的具体属性和套装效果需要进一步分析原模组代码
- [ ] 法杖的具体功能需要分析原模组代码
- [ ] 染梦维度剩余方块的完整列表需要对比分析
