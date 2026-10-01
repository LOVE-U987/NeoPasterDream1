# KubeJS 玩偶教程

> 适用版本：Minecraft 1.21.1 · NeoForge 21.1.x · KubeJS 7.x（1.21.1 对应版本）· PasterDream 0.9.0+
> 本文示例在 PasterDream 0.10.0-pre.3 开发环境中验证通过
>
> 面向读者：想用 KubeJS 给 PasterDream 添加自定义玩偶的玩家、整合包作者与服务器管理员。
> 无需编写任何 Java 代码，只需「一个启动脚本 + 一张 PNG + 一个语言脚本」。

***

## 目录

1. [概述](#1-概述)
2. [前置条件](#2-前置条件)
3. [快速上手：注册 new\_skin\_1 玩偶](#3-快速上手注册-new_skin_1-玩偶)
4. [API 参考](#4-api-参考)
5. [资源文件详解](#5-资源文件详解)
6. [语言与本地化](#6-语言与本地化)
7. [游戏内行为速查](#7-游戏内行为速查)
8. [进阶用法](#8-进阶用法)
9. [常见问题排查](#9-常见问题排查)
10. [仓库示例与相关源码索引](#10-仓库示例与相关源码索引)

***

## 1. 概述

**玩偶**是 PasterDream 提供的一类装饰方块：GeckoLib 渲染的 3D 小人，可水平旋转摆放、
可放入水中，开启「抱物」后还能让玩偶抱住一件物品展示。

PasterDream 在模组内提供了玩偶注册 API（`DollAPI`），并通过 KubeJS 插件把它开放给脚本使用。
你可以在 KubeJS 启动脚本中注册自己的玩偶，获得：

- 一个完整的方块 + 物品（如 `kubejs:new_skin_1`），自动进入 **PasterDream 纪念品创造栏**
- 自动注册的客户端渲染器（方块形态与手持形态都是 3D 模型）
- 自动生成的 `blockstates` 与物品模型 JSON（无需手写）
- 可选的抱物功能与灰色悬浮描述

注册方式有两种：

| 方式                                  | 适用场景                                                 |
| ----------------------------------- | ---------------------------------------------------- |
| `DollAPI.registerDollWithSkin(...)` | **只想换皮肤**：复用模组内置 `eoul_doll` 骨骼模型，只提供一张 64×64 贴图（推荐） |
| `DollAPI.registerDoll(...)`         | **完全自定义**：使用自己制作的 GeckoLib 模型（Blockbench 导出）         |

***

## 2. 前置条件

### 2.1 模组清单

| 模组              | 角色                | 说明                                                 |
| --------------- | ----------------- | -------------------------------------------------- |
| **PasterDream** | 宿主模组              | 提供玩偶 API 与渲染逻辑，硬依赖 GeckoLib 4.8.4                  |
| **KubeJS**      | 脚本引擎（1.21.1 对应版本） | PasterDream 对其为**可选依赖**：不装 KubeJS 模组照常运行，装了才具备本文功能 |
| **Rhino**       | KubeJS 的 JS 引擎    | 随 KubeJS 一同安装，版本必须匹配                               |
| **GeckoLib**    | 3D 模型动画库          | 已是 PasterDream 硬依赖，无需额外操心                          |

### 2.2 验证插件已加载

启动游戏后在日志（`latest.log`）中看到这一行，说明 PasterDream 的 KubeJS 插件已就绪：

```
[PasterDream] KubeJS 插件已初始化
```

### 2.3 脚本目录结构

玩家实例中，所有文件放在 `<游戏实例>/kubejs/` 下：

```
<游戏实例>/kubejs/
├── startup_scripts/      ← 玩偶注册脚本（必须）
├── client_scripts/       ← 语言脚本
├── assets/               ← 纹理等客户端资源（KubeJS 会作为资源包同步给进服客户端）
└── data/                 ← 战利品表等数据包文件
```

> **本仓库开发者注意**：开发环境下的游戏目录是 `PasterDream/run/`，
> 因此本文所有示例的实际路径形如 `PasterDream/run/kubejs/startup_scripts/...`。

> **startup 脚本不响应** **`/reload`**：修改注册脚本后必须完整重启游戏/服务器才会生效。
> 联机时客户端与服务端都要安装 KubeJS 并执行同一份启动脚本，否则注册表不同步会报错。

***

## 3. 快速上手：注册 new\_skin\_1 玩偶

以下是仓库内的真实示例，把一张自定义皮肤注册为可抱物的玩偶，共三步。

### 步骤 1：放置皮肤纹理

把 64×64 的 PNG 纹理放到 `kubejs/assets/pasterdream/textures/block/new_skin_1.png`。

纹理参数要求：

| 项目    | 要求                                 |
| ----- | ---------------------------------- |
| 尺寸    | **64×64**，与模组内置 `eoul_doll.png` 一致 |
| 格式    | PNG                                |
| UV 排布 | 必须匹配 `eoul_doll` 模型的 UV 展开方式       |

> 最稳妥的做法：从 PasterDream 的 jar 中提取 `assets/pasterdream/textures/block/eoul_doll.png`，
> 以它为底稿绘制新皮肤，各部件位置就不会错位。

### 步骤 2：编写注册脚本

创建 `kubejs/startup_scripts/doll_direct_test.js`：

```js
// ============================================================
// PasterDream 玩偶 API 精简示例
// 用途：在 KubeJS 启动阶段注册一个使用自定义皮肤的玩偶
// 命名空间：kubejs（由 PDCreativeTabsSouvenir 统一收集显示）
// ============================================================

// 必须在注册阶段调用，否则 BuiltInRegistries 冻结后会报 IllegalStateException
StartupEvents.registry('block', event => {
    const DollAPI = Java.loadClass('com.pasterdream.pasterdreammod.api.doll.DollAPI');

    // 使用默认 eoul_doll 模型 + 自定义皮肤，开启抱物功能
    DollAPI.registerDollWithSkin(
        'kubejs',                                       // 命名空间
        'new_skin_1',                                   // 注册名（snake_case）
        'pasterdream:textures/block/new_skin_1.png',    // 皮肤纹理路径
        true                                            // 是否允许抱物
    );

    console.log('[PasterDream-DollDirectTest] 已注册 kubejs:new_skin_1，将显示在 PasterDream 纪念品创造栏');
});
```

**两个关键点**：

1. 必须包在 `StartupEvents.registry('block', ...)` 里 —— 该事件恰好在注册表冻结之前触发，
   是直连注册唯一可靠的时序窗口。
2. 纹理路径参数是**完整资源路径**（含 `textures/` 前缀与 `.png` 后缀），
   `pasterdream:textures/block/new_skin_1.png` 会解析到 `kubejs/assets/pasterdream/textures/block/new_skin_1.png`。

### 步骤 3：编写语言脚本

创建 `kubejs/client_scripts/new_skin_1_lang.js`：

```js
// ============================================================
// kubejs:new_skin_1 玩偶语言文件
// 注意：玩偶注册命名空间为 kubejs，因此语言键前缀必须是 kubejs 而非 pasterdream
// ============================================================

ClientEvents.lang('zh_cn', event => {
    event.add('block.kubejs.new_skin_1', '新皮肤 1 玩偶');
    event.add('item.kubejs.new_skin_1', '新皮肤 1 玩偶');
    // 玩偶悬浮提示描述，显示为灰色
    event.add('block.kubejs.new_skin_1.desc', '由 KubeJS 注册的自定义皮肤玩偶');
});

ClientEvents.lang('en_us', event => {
    event.add('block.kubejs.new_skin_1', 'New Skin 1 Doll');
    event.add('item.kubejs.new_skin_1', 'New Skin 1 Doll');
    event.add('block.kubejs.new_skin_1.desc', 'A custom skin doll registered by KubeJS');
});
```

> 注意 `.desc` 描述键挂在 `block.` 前缀下（详见 [第 6 节](#6-语言与本地化)），
> 这是玩偶物品悬浮提示的实际读取位置。

### 步骤 4：重启游戏验证

完整重启游戏后：

1. `/give @s kubejs:new_skin_1` 获取玩偶，或在 **PasterDream 纪念品** 创造栏中找到它
2. 放置玩偶（放置时背对玩家，可四向旋转，也可直接放进水里）
3. 手持任意物品右键玩偶 → 玩偶抱住该物品（消耗 1 个）
4. 空手右键 → 取回物品
5. 创造栏拿出的玩偶悬浮时，名称下方应显示灰色描述行

同时，模组会自动在 `kubejs/assets/kubejs/` 下生成两个 JSON（内容见 [5.3 节](#53-模组自动生成的文件)）：

```
kubejs/assets/kubejs/blockstates/new_skin_1.json
kubejs/assets/kubejs/models/item/new_skin_1.json
```

***

## 4. API 参考

### 4.1 `DollAPI.registerDollWithSkin(namespace, name, skinTexture, canHoldItems)`

换皮专用：复用模组内置 `eoul_doll.geo.json` 骨骼，开启抱物时自动复用 `eoul_doll_holding.geo.json`。

| 参数             | 类型      | 说明                                                       |
| -------------- | ------- | -------------------------------------------------------- |
| `namespace`    | string  | 注册命名空间，决定最终 ID 与语言键，通常传 `'kubejs'`                       |
| `name`         | string  | 注册名，snake\_case，如 `'new_skin_1'`                         |
| `skinTexture`  | string  | 皮肤纹理完整路径，如 `'pasterdream:textures/block/new_skin_1.png'` |
| `canHoldItems` | boolean | `true` 开启抱物功能（自动配好内置抱物模型）                                |

### 4.2 `DollAPI.registerDoll(namespace, name, model, texture, canHoldItems, holdingModel)`

完整版：可指定自己的 GeckoLib 模型。

| 参数             | 类型             | 说明                                             |
| -------------- | -------------- | ---------------------------------------------- |
| `namespace`    | string         | 同上                                             |
| `name`         | string         | 同上                                             |
| `model`        | string \| null | 基础模型路径，如 `'kubejs:geo/block/my_doll.geo.json'` |
| `texture`      | string \| null | 皮肤纹理路径，同 4.1                                   |
| `canHoldItems` | boolean        | 是否允许抱物                                         |
| `holdingModel` | string \| null | 抱物模型路径；`canHoldItems=false` 时传 `null` 即可       |

> `model`/`texture` 传 `null` 会退回模组命名空间的默认路径
> （`pasterdream:geo/block/<name>.geo.json` 等），只对模组内置资源有意义；
> KubeJS 玩偶请**显式传完整路径**。

### 4.3 返回值

两个方法都返回 Java 侧的 `DollResult`，JS 中一般可以忽略；需要时可用
`result.item()`、`result.block()`、`result.config()` 等进一步操作。

### 4.4 命名空间规则

| 注册 namespace    | 物品 ID            | 语言键前缀                  | 自动生成资源目录                |
| --------------- | ---------------- | ---------------------- | ----------------------- |
| `kubejs`（推荐）    | `kubejs:my_doll` | `block.kubejs.my_doll` | `kubejs/assets/kubejs/` |
| `mypack`（任意自定义） | `mypack:my_doll` | `block.mypack.my_doll` | `kubejs/assets/mypack/` |

- namespace 只能包含小写字母、数字、`_`、`-`、`.`、`/`（资源路径字符规则）
- 注册 namespace 与**纹理所在 namespace** 互相独立：上例注册到 `kubejs`，纹理却放在
  `pasterdream` 命名空间目录下，路径对得上即可
- 无论用哪个 namespace，玩偶都会被 **PasterDream 纪念品创造栏** 自动收集显示

***

## 5. 资源文件详解

### 5.1 皮肤纹理（registerDollWithSkin 路线）

```
kubejs/assets/<纹理命名空间>/textures/block/
└── my_doll.png     ← 64×64 PNG，UV 与 eoul_doll 模型一致（使用玩家皮肤纹理即可）
```

- 文件名**大小写敏感**，必须与脚本中路径完全一致
- 路径必须是 `textures/block/`（复数 `textures`），写成 `texture/` 会加载不到且无报错

### 5.2 自定义模型（registerDoll 路线）

需要准备 GeckoLib 模型文件（Blockbench 制作，导出为 GeckoLib 格式）：

```
kubejs/assets/<ns>/
├── geo/block/
│   ├── my_doll.geo.json          ← 基础模型（必选）
│   └── my_doll_holding.geo.json  ← 抱物模型（canHoldItems=true 时必选）
└── textures/block/
    └── my_doll.png               ← 皮肤纹理（必选）
```

要点：

- **不需要动画文件**。玩偶是静态的，渲染时强制使用模组内置空动画
  （`pasterdream:animations/block/empty.animation.json`）
- **抱物模型需要包含名为** **`bb_main`** **的骨骼**作为物品锚点：渲染器会隐藏该骨骼自带的
  立方体，并在骨骼中心以 0.5 倍缩放渲染被抱物品；没有 `bb_main` 骨骼时物品不会显示
- 没有自己的模型时，可复制模组内置的 `eoul_doll.geo.json` / `eoul_doll_holding.geo.json` 作为模板修改

### 5.3 模组自动生成的文件

当注册 namespace 不是 `pasterdream` 时，`registerDirect` 会在注册的同时自动写出两个 JSON，
避免方块/物品显示紫黑占位，**无需手动创建**：

`kubejs/assets/kubejs/blockstates/my_doll.json`（通用粒子模型 + 朝向旋转）：

```json
{
  "variants": {
    "facing=north": { "model": "pasterdream:custom/doll_generic_particle" },
    "facing=east": { "model": "pasterdream:custom/doll_generic_particle", "y": 90 },
    "facing=south": { "model": "pasterdream:custom/doll_generic_particle", "y": 180 },
    "facing=west": { "model": "pasterdream:custom/doll_generic_particle", "y": 270 }
  }
}
```

`kubejs/assets/kubejs/models/item/my_doll.json`（通用手持/展示显示参数）：

```json
{
  "parent": "pasterdream:displaysettings/doll_generic.item"
}
```

### 5.4 需要自己补的：战利品表

⚠️ **自动生成不包含战利品表** —— 不补的话，生存模式下挖掘玩偶**什么都不会掉**（被抱住的
物品仍会弹出，但玩偶本体直接消失）。创造模式不受影响。

模组内置玩偶都带有各自掉落表；KubeJS 玩偶需要在 `kubejs/data/` 下自行补齐。
创建 `kubejs/data/kubejs/loot_table/blocks/my_doll.json`：

```json
{
  "type": "minecraft:block",
  "pools": [
    {
      "rolls": 1.0,
      "entries": [
        {
          "type": "minecraft:item",
          "name": "kubejs:my_doll"
        }
      ],
      "conditions": [
        {
          "condition": "minecraft:survives_explosion"
        }
      ]
    }
  ]
}
```

注意 1.21 的目录规则（与 1.20 不同）：

- ✅ 路径为单数 `loot_table/blocks/`（`data/<ns>/loot_table/blocks/<name>.json`）
- ❌ 1.20 的复数 `loot_tables/` 目录会被静默忽略，方块不掉落且无任何报错

若玩偶注册在其他 namespace（如 `mypack`），战利品表放在
`kubejs/data/mypack/loot_table/blocks/my_doll.json`，`name` 字段对应 `mypack:my_doll`。

***

## 6. 语言与本地化

玩偶物品由 `BlockItem` 派生，翻译键遵循以下规则：

| 语言键                      | 是否必需   | 说明                                  |
| ------------------------ | ------ | ----------------------------------- |
| `block.<ns>.<name>`      | **必需** | 物品名与方块名实际读取的键（BlockItem 走 block 前缀） |
| `item.<ns>.<name>`       | 推荐     | 部分 UI/信息类模组会按 item 前缀读取，补上最稳        |
| `block.<ns>.<name>.desc` | 可选     | 灰色悬浮描述行，添加语言键即生效，无需任何代码             |

```js
ClientEvents.lang('zh_cn', event => {
    event.add('block.kubejs.my_doll', '我的玩偶');
    event.add('item.kubejs.my_doll', '我的玩偶');
    event.add('block.kubejs.my_doll.desc', '这是一行灰色的悬浮描述');
});
```

> **常见坑**：`.desc` 键挂在 `block.` 前缀下。虽然描述显示在物品悬浮提示上，
> 但玩偶物品的描述 ID 是 `block.<ns>.<name>`，写成 `item.<ns>.<name>.desc` 不会生效。

语言脚本属于 client\_scripts，改完 `/reload` 即可生效（无需重启）。

***

## 7. 游戏内行为速查

| 行为  | 表现                                     |
| --- | -------------------------------------- |
| 放置  | 背对玩家四向放置（`facing` 属性），可含水（waterlogged） |
| 碰撞箱 | 8×16×8（居中，略窄于整格）                       |
| 硬度  | 1.0，徒手可破坏；放置/破坏音效为装饰花盆（Decorated Pot）  |
| 渲染  | 全 GeckoLib 3D 渲染，手持/展示柜/头顶姿态由内置显示参数处理  |
| 创造栏 | 自动进入 PasterDream 纪念品栏                  |

**抱物交互**（`canHoldItems=true` 时）：

- 手持任意物品右键玩偶 → 玩偶抱住该物品（消耗 1 个，播放盔甲穿戴音效）
- 空手右键 → 取回被抱物品（背包满则掉落地面）
- 手持玩偶本体右键该玩偶 → 不会放置（防套娃）
- 破坏玩偶 → 被抱物品原地弹出
- 抱物状态通过方块 `holding` 属性同步，客户端自动切换抱物模型

**`canHoldItems=false`** **时**：右键无特殊交互，仅作装饰摆放。

***

## 8. 进阶用法

### 8.1 一个脚本注册多个玩偶

在同一个事件回调中连续调用即可，也可拆成多个脚本文件：

```js
StartupEvents.registry('block', event => {
    const DollAPI = Java.loadClass('com.pasterdream.pasterdreammod.api.doll.DollAPI');

    DollAPI.registerDollWithSkin('kubejs', 'skin_a', 'kubejs:textures/block/skin_a.png', true);
    DollAPI.registerDollWithSkin('kubejs', 'skin_b', 'kubejs:textures/block/skin_b.png', false);
});
```

### 8.2 完全自定义模型

```js
StartupEvents.registry('block', event => {
    const DollAPI = Java.loadClass('com.pasterdream.pasterdreammod.api.doll.DollAPI');

    DollAPI.registerDoll(
        'kubejs',
        'custom_doll',
        'kubejs:geo/block/custom_doll.geo.json',
        'kubejs:textures/block/custom_doll.png',
        true,
        'kubejs:geo/block/custom_doll_holding.geo.json'
    );
});
```

资源放置要求见 [5.2 节](#52-自定义模型registerdoll-路线)。

### 8.3 备选事件：`PasterDreamEvents.dollRegistry`

模组的 KubeJS 插件注册了 `PasterDreamEvents` 事件组，其中 `dollRegistry` 是一个
与 Java 侧 Builder 同构的链式 API：

```js
PasterDreamEvents.dollRegistry(event => {
    event.create('my_doll')
        .namespace('kubejs')
        .texture('kubejs:textures/block/my_doll.png')
        .canHoldItems(true)
        .register();   // 内部同样走 registerDirect
});
```

**注意**：该事件的触发时机由 KubeJS 通用 startup 事件调度决定，不保证早于注册表冻结，
仓库官方示例因此未采用此方式。如果用它注册时遇到
`[DollBuilder] registerDirect 无法在注册表锁定后注册方块 ...` 异常，
请改用本文推荐的 `StartupEvents.registry('block')` + `DollAPI` 直连写法。

***

## 9. 常见问题排查

**Q：脚本写了但玩偶不出现**

按顺序检查：

1. 脚本在 `kubejs/startup_scripts/`？放到 `server_scripts/`/`client_scripts/` 都不会生效
2. 用的是 `StartupEvents.registry('block', ...)` 事件？
3. KubeJS + Rhino 已安装？日志中应有 `[PasterDream] KubeJS 插件已初始化`
4. 启动日志中有没有 JS 报错（脚本语法错误会整段跳过）

**Q：日志报** **`registerDirect 无法在注册表锁定后注册方块`（IllegalStateException）**

注册发生在注册表冻结之后。原因通常是写到了 `StartupEvents.init` 或其它更晚的事件里，
请移回 `StartupEvents.registry('block')`。

**Q：方块/物品显示紫黑占位，或模型是缺失材质的紫黑小人**

- 纹理路径拼写与大小写是否与实际文件完全一致
- 路径是否为 `textures/block/`（不是 `texture/`）
- 文件是否为 PNG、是否 64×64
- 渲染配置查不到时会回退到模组的 `missing_doll` 占位模型，说明注册结果与配置对不上——
  检查是否重名覆盖了同名注册

**Q：生存模式挖掉玩偶没有掉落**

正常现象：自动生成不包含战利品表，按 [5.4 节](#54-需要自己补的战利品表) 补一份
`loot_table/blocks/<name>.json` 即可（注意是单数 `loot_table`）。

**Q：改了脚本** **`/reload`** **后没变化**

启动脚本不响应 `/reload`，必须完整重启。语言脚本（client\_scripts）才能 `/reload`。

**Q：名字显示为** **`block.kubejs.my_doll`** **原文**

语言键没加或前缀不对。键的 namespace 必须与注册时的 `namespace` 参数一致。

**Q：灰色描述（.desc）不显示**

描述键是 `block.<ns>.<name>.desc`（`block.` 前缀），不是 `item.`。详见第 6 节。

**Q：右键玩偶没反应，抱不了东西**

注册时第 4 个参数（`canHoldItems`）传了 `false`。

**Q：抱住物品后物品渲染位置奇怪**

自定义抱物模型的 `bb_main` 骨骼决定了物品渲染位置（见 [5.2 节](#52-自定义模型registerdoll-路线)），
调整该骨骼在模型中的位置即可。

**Q：服务器上玩家看不到皮肤 / 注册表不同步报错**

客户端与服务端都需要：安装 KubeJS + Rhino + PasterDream，且持有相同的启动脚本。
`kubejs/assets` 由 KubeJS 自动同步给进服客户端；纹理也可打包进资源包分发。

**Q：删除/改名已注册的玩偶后，存档里已放置的玩偶消失**

KubeJS 注册内容不持久化，注册名即身份。改名等于删除旧玩偶再注册新玩偶，
旧方块会从世界中移除——改版前请告知玩家或先回收放置的玩偶。

***

## 10. 仓库示例与相关源码索引

### 10.1 本文对应的完整示例（开发环境路径）

| 文件                                                                        | 作用                  |
| ------------------------------------------------------------------------- | ------------------- |
| `PasterDream/run/kubejs/startup_scripts/doll_direct_test.js`              | 启动注册脚本（本文步骤 2 的原文件） |
| `PasterDream/run/kubejs/client_scripts/new_skin_1_lang.js`                | 中英语言脚本（本文步骤 3 的原文件） |
| `PasterDream/run/kubejs/assets/pasterdream/textures/block/new_skin_1.png` | 64×64 皮肤纹理          |

### 10.2 想深入实现的开发者

| 源码                                                                                          | 内容                                             |
| ------------------------------------------------------------------------------------------- | ---------------------------------------------- |
| `PasterDream/src/main/java/com/pasterdream/pasterdreammod/api/doll/DollAPI.java`            | 玩偶注册门面（含 KubeJS 便捷方法）                          |
| `PasterDream/src/main/java/com/pasterdream/pasterdreammod/api/doll/DollBuilder.java`        | Builder 与 `registerDirect` 实现、KubeJS 资源自动生成    |
| `PasterDream/src/main/java/com/pasterdream/pasterdreammod/integration/kubejs/`              | KubeJS 插件与 `PasterDreamEvents.dollRegistry` 事件 |
| `PasterDream/src/main/java/com/pasterdream/pasterdreammod/client/model/DollBlockModel.java` | 模型/纹理/抱物切换的客户端解析逻辑                             |
| `PasterDream/src/main/resources/assets/pasterdream/geo/block/eoul_doll.geo.json`            | 内置玩偶骨骼（换皮路线的模板）                                |

### 下一步

- [添加方块](添加方块.md) —— 若想把玩偶直接写进模组 Java 侧
- [问题排查](../开发指南/问题排查.md) —— 方块掉落/战利品表问题通用排查
- [资源规范](../开发指南/资源规范.md) —— 模组内资源文件放置规范

