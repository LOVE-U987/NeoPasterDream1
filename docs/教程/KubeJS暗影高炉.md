# KubeJS 暗影高炉教程

> 适用版本：Minecraft 1.21.1 · NeoForge 21.1.x · KubeJS 7.x · PasterDream 0.10.0+
>
> 面向读者：想用 KubeJS 添加自定义暗影高炉配方的整合包作者与服务器管理员。
> 前置：安装 KubeJS 与 Rhino，无需任何 Java 代码。

## 目录

1. [概述](#1-概述)
2. [快速上手：从例子开始](#2-快速上手从例子开始)
3. [扩展：可选参数](#3-扩展可选参数)
4. [行为细节](#4-行为细节)
5. [常见问题](#5-常见问题)
6. [源码索引](#6-源码索引)

***

## 1. 概述

**暗影高炉**（`pasterdream:shadow_blast_furnace`）是消耗**暗影液体 + 梦魇燃料**的冶炼机器，
配方类型为 `pasterdream:shadow_blasting`，脚本入口：

```js
event.recipes.pasterdream.shadow_blasting(主产物, 原料)
```

## 2. 快速上手：从例子开始

```js
// kubejs/server_scripts/pd_recipes.js
ServerEvents.recipes(event => {
    event.recipes.pasterdream.shadow_blasting(
        'minecraft:diamond',      // 第 1 参：主产物——煤块冶炼成钻石
        'minecraft:coal_block'    // 第 2 参：原料
    );
});
```

逐行拆解：

- **产物在前、原料在后**——这是两个位置参数的最小配方，写完即可用
- 原料支持物品 id、标签、物品列表等 KubeJS 通用材料写法（如 `'#forge:ingots/copper'`）
- 未设置的可选参数全部走默认值：

| 参数 | 默认值 |
|------|--------|
| 冶炼时长 `blasting_tick` | `100` tick |
| 暗影液体消耗 `spend_fluid_fuel` | `500` mB |
| 梦魇燃料消耗 `spend_fuel` | `1` |
| 副产物 `by_result` | 无 |
| 副产物概率 `by_result_probability` | `1.0`（100%） |

保存脚本，游戏内 `/reload` 即生效，配方同时出现在 JEI。

## 3. 扩展：可选参数

| 方法 | 默认值 | 说明 |
|------|--------|------|
| `.byResult(物品)` | 空 | 副产物（独立产出槽） |
| `.byResultProbability(0~1)` | `1.0` | 副产物产出概率 |
| `.blastingTick(tick)` / `.time(tick)` | `100` | 冶炼总时长 |
| `.spendFluidFuel(mB)` | `500` | 每次冶炼消耗的暗影液体量 |
| `.spendFuel(数量)` | `1` | 每次冶炼消耗的梦魇燃料数 |

组合示例——煤块炼钻石，附 35% 概率出绿宝石，缩短耗时并降低液体消耗：

```js
ServerEvents.recipes(event => {
    event.recipes.pasterdream.shadow_blasting('minecraft:diamond', 'minecraft:coal_block')
        .byResult('minecraft:emerald')   // 副产物：绿宝石
        .byResultProbability(0.35)       // 35% 概率产出
        .time(50)                        // 冶炼缩短到 50 tick
        .spendFluidFuel(300)             // 暗影液体少烧点，200 mB
        .spendFuel(2);                   // 每次烧 2 个梦魇燃料
});
```

## 4. 行为细节

- 机器槽位：**0 冶炼输入 / 1 梦魇燃料 / 2 主产物 / 3 副产物**
- **燃料槽必须放入梦魇燃料**——即使 `spendFuel(0)` 也要求槽内有燃料才开工（校验燃料种类，不校验扣减量）
- 副产物每次冶炼按概率独立掷骰，不保证产出
- 配方 id 由主产物生成，主产物不同即可重复注册

## 5. 常见问题

| 现象 | 原因与处理 |
|------|-----------|
| 机器不工作 | 确认暗影液体量 ≥ `spendFluidFuel`、燃料槽有梦魇燃料 |
| `/reload` 后 JEI 不显示 | 检查脚本是否在 `server_scripts` 目录、产物 id 拼写正确 |
| 启动日志无插件信息 | 未安装 KubeJS（可选依赖），安装后应见 `[PasterDream] KubeJS 插件已初始化` |

## 6. 源码索引

| 内容 | 位置 |
|------|------|
| KubeJS 插件入口与 schema 注册 | `PasterDream/.../integration/kubejs/PasterDreamKubeJSPlugin.java` |
| 配方 schema 字段定义 | `PasterDream/.../integration/kubejs/PDRecipeSchemas.java`（`SHADOW_BLASTING` 段） |
| 配方逻辑与 MapCodec | `PasterDream/.../recipe/ShadowBlastFurnaceRecipe.java` |
| 机器运行逻辑（燃料/液体校验） | `PasterDream/.../block/entity/ShadowBlastFurnaceBlockEntity.java` |
