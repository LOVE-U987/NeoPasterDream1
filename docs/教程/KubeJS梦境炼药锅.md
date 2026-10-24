# KubeJS 梦境炼药锅教程

> 适用版本：Minecraft 1.21.1 · NeoForge 21.1.x · KubeJS 7.x · PasterDream 0.10.0+
>
> 面向读者：想用 KubeJS 添加自定义梦境炼药锅法术配方的整合包作者与服务器管理员。
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

**梦境炼药锅**（`pasterdream:dream_cauldron`）的**法术酿造通道**使用配方类型
`pasterdream:dream_brewing`，脚本入口：

```js
event.recipes.pasterdream.dream_brewing(产物, 3材料数组)
```

> 炼药锅另有**药水酿造 / 药水合并**通道（消耗药水与融梦液体），那是机器内置逻辑，
> 不走配方类型，本文不覆盖。

## 2. 快速上手：从例子开始

```js
// kubejs/server_scripts/pd_recipes.js
ServerEvents.recipes(event => {
    event.recipes.pasterdream.dream_brewing(
        'minecraft:golden_apple',   // 第 1 参：酿造产物
        [                           // 第 2 参：材料数组，固定 3 个元素
            'minecraft:apple',
            'minecraft:gold_ingot',
            'minecraft:glowstone_dust'
        ]
    );
});
```

逐行拆解：

- **材料数组必须恰好 3 个元素**，按序对应炼药锅 GUI 的瓶槽 1-3（schema 用 `IntBounds(3,3)` 硬校验）
- 元素支持物品 id、标签等通用材料写法
- 未设置的可选参数默认：引导药剂为**药引瓶**（`pasterdream:guiding_drug`）、时长 `60` tick

保存脚本，游戏内 `/reload` 即生效，配方同时出现在 JEI。

## 3. 扩展：可选参数

| 方法 | 默认值 | 说明 |
|------|--------|------|
| `.guidingDrug(材料)` | `pasterdream:guiding_drug`（药引瓶） | 引导药剂，对应材料槽 0 |
| `.craftTick(tick)` / `.time(tick)` | `60` | 酿造总时长，下限 4 tick |

组合示例——把引导药剂换成蜂蜜瓶、加快酿造：

```js
ServerEvents.recipes(event => {
    event.recipes.pasterdream.dream_brewing(
        'minecraft:golden_apple',
        ['minecraft:apple', 'minecraft:gold_ingot', 'minecraft:glowstone_dust']
    )
    .guidingDrug('minecraft:honey_bottle')  // 药引改为蜂蜜瓶
    .craftTick(40);                         // 酿造缩短到 40 tick
});
```

## 4. 行为细节

- **槽位约束**（GUI 过滤，Shift 点击也绕不过）：
  - 槽 0（材料槽）：只能放引导药剂类物品，**拒绝一切药水容器**（含水瓶）
  - 槽 1-3（瓶槽）：只收药水容器或已注册 dream_brewing 配方的材料
  - 槽 4：只收融梦液体桶
- 在炼药锅中酿造的药水获得**梦之强化**：持续效果时长 ×1.5 且等级 +1、即时效果等级 +1
- 配方 id 由产物生成，产物不同即可重复注册

## 5. 常见问题

| 现象 | 原因与处理 |
|------|-----------|
| 脚本报错材料数量不符 | 材料数组必须恰好 3 个，多写少写都会解析失败 |
| 材料放不进瓶槽 | 瓶槽只收药水容器或已注册配方的材料，先确认配方已加载 |
| `/reload` 后 JEI 不显示 | 检查脚本是否在 `server_scripts` 目录、产物 id 拼写正确 |
| 启动日志无插件信息 | 未安装 KubeJS（可选依赖），安装后应见 `[PasterDream] KubeJS 插件已初始化` |

## 6. 源码索引

| 内容 | 位置 |
|------|------|
| KubeJS 插件入口与 schema 注册 | `PasterDream/.../integration/kubejs/PasterDreamKubeJSPlugin.java` |
| 配方 schema 字段定义 | `PasterDream/.../integration/kubejs/PDRecipeSchemas.java`（`DREAM_BREWING` 段） |
| 配方逻辑与 MapCodec | `PasterDream/.../recipe/DreamBrewingRecipe.java` |
| 机器运行逻辑（酿造/合并/强化） | `PasterDream/.../block/entity/DreamCauldronBlockEntity.java`、`util/DreamPotionEnhancer.java` |
| 槽位过滤 | `PasterDream/.../menu/DreamCauldronMenu.java` |
