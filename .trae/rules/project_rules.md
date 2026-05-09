---
alwaysApply: false
description: 
---
# PasterDream NeoForge 1.21.1 开发规则

## 核心理念：精神续作，而非代码移植

- **不看代码，只看效果**：参考原模组呈现效果，但**不直接复制或修改原代码**
- **重新实现，思路不同**：相同效果，用不同技术方案
- **MCreator 代码不可移植**：原模组是 MCreator 生成，必须重写
- **版本跨度**：1.20.1 Forge → 1.21.1 NeoForge

## 项目结构

```
NeoPasterDream1/
├── src/main/java/com/pasterdream/pasterdreammod/  # 新代码（手写）
├── src/main/resources/assets/pasterdream/         # 资源文件
└── libs/FixPasterDream-main/  # 原模组（只读参考）
```

## 开发工作流

1. **分析效果**：查看原模组资源文件，理解游戏机制
2. **重新设计**：基于 NeoForge 1.21.1 API 实现
3. **手写代码**：使用 `DeferredRegister`、DataGen、GeckoLib
4. **编译测试**：`./gradlew compileJava` → `./gradlew runData` → `./gradlew runClient`

## 多线程开发策略

| 模块 | 开发方式 | 注意 |
|-----|---------|------|
| 独立物品/方块 | 可并行 | 避免同时修改同一文件 |
| 实体系统 | 可并行 | 需协调渲染器注册 |
| 数据生成 | 可并行 | - |
| 跨模块功能 | 串行/协调 | Capability、网络包等 |

## API 迁移对照

| 1.20.1 Forge | 1.21.1 NeoForge |
|-------------|----------------|
| `forge:` | `neoforge:` 或 `c:` |
| `forge/tags/items/` | `c/tags/item/` |
| `forge:fluid_container` | `neoforge:fluid_container` |

## 代码规范

- **命名**：类 PascalCase，方法 camelCase，常量 UPPER_SNAKE_CASE，注册名 snake_case
- **注册**：必须使用 `DeferredRegister`
- **实体**：继承 `GeckoLibMonsterEntity`/`GeckoLibAnimalEntity`
- **注释**：类级+方法级注释，参数用 `@param`

## 资源处理

**可直接复制**：纹理、声音、GeckoLib 模型/动画、语言文件
**需重新创建**：配方、战利品表、标签（DataGen）、维度文件、生物群系修饰器

## 禁止事项

1. ❌ 修改原模组代码（`libs/` 只读）
2. ❌ 复制 MCreator 代码
3. ❌ 硬编码配置
4. ❌ 忽略编译错误
5. ❌ 跳过 DataGen

## 版本信息

Minecraft 1.21.1 | NeoForge 21.1.219 | GeckoLib 4.7.3 | Java 21
