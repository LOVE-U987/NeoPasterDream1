# KubeJS 精铸工坊教程

> 适用版本：Minecraft 1.21.1 · NeoForge 21.1.x · KubeJS 7.x · PasterDream 0.10.0+
>
> 面向读者：想用 KubeJS 添加自定义精铸工坊配方的整合包作者与服务器管理员。
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

**精铸工坊**（`pasterdream:weapon_workshop`）是 5 材料槽的锻造机器，
配方类型为 `pasterdream:weapon_forge`，脚本入口：

```js
event.recipes.pasterdream.weapon_forge(产物, 5材料数组)
```

## 2. 快速上手：从例子开始

```js
// kubejs/server_scripts/pd_recipes.js
ServerEvents.recipes(event => {
    event.recipes.pasterdream.weapon_forge(
        'minecraft:netherite_scrap',   // 第 1 参：锻造产物
        [                              // 第 2 参：材料数组，固定 5 个元素
            'minecraft:iron_ingot',
            'minecraft:gold_ingot',
            'minecraft:copper_ingot',
            'minecraft:diamond',
            'minecraft:emerald'
        ]
    );
});
```

逐行拆解：

- **材料数组必须恰好 5 个元素**，多一个少一个都会解析失败（schema 用 `IntBounds(5,5)` 硬校验）
- 数组元素按位置对应工坊 GUI 的 5 个材料格，支持物品 id、标签等通用材料写法
- 未设置的可选参数默认：`inlay = false`、`final_result` 为空

保存脚本，游戏内 `/reload` 即生效，配方同时出现在 JEI。

## 3. 扩展：可选参数

| 方法 | 默认值 | 说明 |
|------|--------|------|
| `.inlay(bool)` | `false` | JEI 镶嵌槽展示标记（实际镶嵌由工坊强化石槽驱动，此参数只影响展示） |
| `.finalResult(物品)` | 空 | 原胚的最终成品预览，仅显示在 JEI 第二产出槽 |

组合示例——锻造染梦剑胚，并提示玩家下一步用强化石镶嵌后的最终形态：

```js
ServerEvents.recipes(event => {
    event.recipes.pasterdream.weapon_forge(
        'pasterdream:dream_blade',                     // 产物：剑胚
        ['#forge:ingots/iron', '#forge:ingots/copper', // 5 格材料
         'minecraft:diamond', 'minecraft:diamond',
         'minecraft:amethyst_shard']
    )
    .inlay(true)                                       // JEI 显示镶嵌槽
    .finalResult('pasterdream:dream_blade_enhanced');  // 最终成品预览
});
```

## 4. 行为细节

- 配方 id 由产物生成，产物不同即可重复注册
- `inlay` 是展示标记，**不是**锻造条件——真正的镶嵌走工坊强化石槽的强化系统
- `finalResult` 只用于 JEI 预览，不参与匹配与产出

## 5. 常见问题

| 现象 | 原因与处理 |
|------|-----------|
| 脚本报错材料数量不符 | 材料数组必须恰好 5 个，多写少写都会解析失败 |
| `/reload` 后 JEI 不显示 | 检查脚本是否在 `server_scripts` 目录、产物 id 拼写正确 |
| 启动日志无插件信息 | 未安装 KubeJS（可选依赖），安装后应见 `[PasterDream] KubeJS 插件已初始化` |

## 6. 源码索引

| 内容 | 位置 |
|------|------|
| KubeJS 插件入口与 schema 注册 | `PasterDream/.../integration/kubejs/PasterDreamKubeJSPlugin.java` |
| 配方 schema 字段定义 | `PasterDream/.../integration/kubejs/PDRecipeSchemas.java`（`WEAPON_FORGE` 段） |
| 配方逻辑 | `PasterDream/.../recipe/WeaponWorkshopRecipe.java` |
| 机器运行逻辑 | `PasterDream/.../block/entity/WeaponWorkshopBlockEntity.java` |
