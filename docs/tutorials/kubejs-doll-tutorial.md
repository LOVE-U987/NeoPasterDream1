# PasterDream 玩偶 KubeJS 注册教程

> 适用版本：Minecraft 1.21.1 / NeoForge 21.1.219 / KubeJS 7.x / PasterDream 0.9.0+

## 目录

1. [前置条件](#1-前置条件)
2. [快速上手](#2-快速上手)
3. [可用 API 方法](#3-可用-api-方法)
4. [资源文件准备](#4-资源文件准备)
5. [设置中文名](#5-设置中文名)
6. [真实案例：注册 New Skin 1 玩偶](#6-真实案例注册-new-skin-1-玩偶)
7. [进阶：完整 registerDoll](#7-进阶完整-registerdoll)
8. [命名空间与创造栏](#8-命名空间与创造栏)
9. [常见问题](#9-常见问题)

---

## 1. 前置条件

### 必装模组

| 模组 | 角色 | 下载 |
|------|------|------|
| **KubeJS** | KubeJS 本体（7.x，对应 1.21.1） | Modrinth / CurseForge |
| **Rhino** | KubeJS 的 JS 引擎（必须匹配版本） | Modrinth |
| **PasterDream** | 玩偶 API 的宿主模组 | 本模组 |
| **GeckoLib** | GeckoLib 动画驱动（4.7.3+） | Modrinth / CurseForge |

> 三个模组都以 **放在 `mods/` 目录** 的方式安装。
> 如果你在开发环境测试 KubeJS，`build.gradle` 中需要添加 `localRuntime` 依赖（参考 PasterDream 的 `build.gradle`）。

### 安装检查

启动游戏后查看日志，出现以下内容说明 KubeJS 插件加载成功：

```
[PasterDream] KubeJS 插件已初始化
```

如果没看到这行日志，请检查 KubeJS 和 Rhino 是否已正确安装。

### 脚本目录

玩偶注册脚本必须放在 **`kubejs/startup_scripts/`** 目录下，并且使用 `StartupEvents.registry('block', ...)` 事件：

```
kubejs/
  startup_scripts/    ← 玩偶注册放这里
  server_scripts/     ← × 不行
  client_scripts/     ← × 不行
```

> 放到 `server_scripts/` 或 `client_scripts/` 都不会在正确的注册阶段执行，玩偶不会出现在游戏中。
> 也不能在 `StartupEvents.init` 中调用，因为此时方块/物品注册表已经冻结。

---

## 2. 快速上手

创建 `kubejs/startup_scripts/my_doll.js`，内容如下：

```js
StartupEvents.registry('block', event => {
    const DollAPI = Java.loadClass('com.pasterdream.pasterdreammod.api.doll.DollAPI');

    DollAPI.registerDollWithSkin(
        'kubejs',                               // 命名空间
        'my_doll',                              // 注册名
        'kubejs:textures/block/my_doll.png',    // 皮肤纹理路径
        true                                    // 是否允许抱物
    );
});
```

然后在 `kubejs/assets/kubejs/textures/block/` 下放置 `my_doll.png`，重启游戏即可。

游戏启动后：
- 用 `/give @s kubejs:my_doll` 获取玩偶
- 玩偶会自动显示在 **PasterDream 纪念品创造栏**

---

## 3. 可用 API 方法

### `DollAPI.registerDollWithSkin(namespace, name, skinTexture, canHoldItems)`

最简注册方式，复用模组内置的 `eoul_doll` 模型和抱物模型。

| 参数 | 类型 | 说明 |
|------|------|------|
| `namespace` | `string` | 注册命名空间，如 `"kubejs"` |
| `name` | `string` | 玩偶注册名，snake_case |
| `skinTexture` | `string` | 皮肤纹理路径，如 `"kubejs:textures/block/my_doll.png"` |
| `canHoldItems` | `boolean` | 是否允许抱物 |

### `DollAPI.registerDoll(namespace, name, model, texture, canHoldItems, holdingModel)`

完整注册方式，可指定自定义 GeckoLib 模型。

| 参数 | 类型 | 说明 |
|------|------|------|
| `namespace` | `string` | 注册命名空间 |
| `name` | `string` | 玩偶注册名 |
| `model` | `string\|null` | 基础模型路径，传 `null` 使用默认路径 |
| `texture` | `string\|null` | 皮肤纹理路径，传 `null` 使用默认路径 |
| `canHoldItems` | `boolean` | 是否允许抱物 |
| `holdingModel` | `string\|null` | 抱物模型路径，不需要可传 `null` |

---

## 4. 资源文件准备

### 使用 `registerDollWithSkin`（推荐）

只需要准备皮肤纹理：

```
kubejs/assets/kubejs/textures/block/
└── my_doll.png          ← 皮肤纹理（64×64，类似玩家皮肤布局）
```

模型会自动使用模组内置的 `pasterdream:geo/block/eoul_doll.geo.json`。

### 使用 `registerDoll` 自定义模型

需要准备完整的模型和纹理：

```
kubejs/assets/mypack/
├── geo/
│   └── block/
│       ├── my_doll.geo.json          ← 基础模型（必选）
│       └── my_doll_holding.geo.json  ← 抱物模型（开启抱物时必选）
└── textures/
    └── block/
        └── my_doll.png               ← 皮肤纹理（必选）
```

> 模型文件用 **Blockbench** 制作，导出为 GeckoLib 格式（`.geo.json`）。
> 如果你没有自己的模型，可以从模组复制 `eoul_doll.geo.json` 和 `eoul_doll_holding.geo.json` 作为模板。

---

## 5. 设置中文名

玩偶注册后默认显示注册名（如 `block.kubejs.my_doll`），需要在语言文件中添加翻译。

### 方式一：KubeJS 语言事件（推荐）

创建 `kubejs/client_scripts/doll_lang.js`：

```js
ClientEvents.lang('zh_cn', event => {
    event.add('block.kubejs.my_doll', '我的玩偶');
    event.add('item.kubejs.my_doll', '我的玩偶');
});
```

```js
ClientEvents.lang('en_us', event => {
    event.add('block.kubejs.my_doll', 'My Doll');
    event.add('item.kubejs.my_doll', 'My Doll');
});
```

### 方式二：直接修改资源包

如果你在制作资源包，可以在 `assets/kubejs/lang/zh_cn.json` 中添加：

```json
{
  "block.kubejs.my_doll": "我的玩偶",
  "item.kubejs.my_doll": "我的玩偶"
}
```

> 语言键的命名空间必须和注册时传入的 `namespace` 一致。

### 添加悬浮描述

`DollDisplayItem` 会自动读取 `item.<namespace>.<name>.desc` 作为灰色悬浮提示。例如：

```js
ClientEvents.lang('zh_cn', event => {
    event.add('item.kubejs.my_doll.desc', '这是一行灰色的物品描述');
});
```

不需要额外代码，添加语言键即可生效。

---

## 6. 真实案例：注册 New Skin 1 玩偶

### 第一步：准备纹理

把 `New Skin 1.png` 重命名为 `new_skin_1.png`，放到：

```
kubejs/assets/pasterdream/textures/block/new_skin_1.png
```

> 也可以放到 `kubejs/assets/kubejs/textures/block/new_skin_1.png`，此时脚本里的路径要改成 `kubejs:textures/block/new_skin_1.png`。

### 第二步：写注册脚本

创建 `kubejs/startup_scripts/new_skin_1_doll.js`：

```js
StartupEvents.registry('block', event => {
    const DollAPI = Java.loadClass('com.pasterdream.pasterdreammod.api.doll.DollAPI');

    DollAPI.registerDollWithSkin(
        'kubejs',
        'new_skin_1',
        'pasterdream:textures/block/new_skin_1.png',
        true
    );
});
```

### 第三步：添加语言

创建 `kubejs/client_scripts/new_skin_1_lang.js`（文件名需与脚本对应）：

```js
ClientEvents.lang('zh_cn', event => {
    event.add('block.kubejs.new_skin_1', '新皮肤 1 玩偶');
    event.add('item.kubejs.new_skin_1', '新皮肤 1 玩偶');
    event.add('item.kubejs.new_skin_1.desc', '由 KubeJS 注册的自定义皮肤玩偶');
});
```

### 第四步：重启游戏

重启后：
- 用 `/give @s kubejs:new_skin_1` 获取玩偶
- 在 **PasterDream 纪念品创造栏** 找到该玩偶
- 摆放后右键手持物品 → 玩偶抱物
- 空手右键 → 取下物品

---

## 7. 进阶：完整 registerDoll

如果你想使用自己的 GeckoLib 模型，或者使用旧模型，可以用完整版：

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

---

## 8. 命名空间与创造栏

- 玩偶注册时传入的 `namespace` 决定了物品的 ID，如 `kubejs:new_skin_1`。
- 无论使用哪个命名空间，`PDCreativeTabsSouvenir` 都会自动把所有 DollAPI 注册的玩偶收集到 **PasterDream 纪念品创造栏**。
- 当命名空间不是 `pasterdream` 时，API 会自动在 `kubejs/assets/<namespace>/` 下生成必要的 `blockstates/<name>.json` 和 `models/item/<name>.json`，避免方块/物品显示紫黑占位。

---

## 9. 常见问题

### Q：脚本写了但玩偶不出现

排查顺序：

1. **是否放对目录** → 必须 `kubejs/startup_scripts/`
2. **是否使用正确事件** → 必须是 `StartupEvents.registry('block', ...)`
3. **是否有 KubeJS + Rhino** → 看日志有没有 `[PasterDream] KubeJS 插件已初始化`
4. **是否报错 `Registry is already frozen`** → 说明写到了 `StartupEvents.init` 等太晚的事件
5. **纹理文件是否存在** → 检查 `kubejs/assets/<namespace>/textures/block/` 下的路径
6. **语法错误** → 看日志是否有 JS 报错

### Q：用旧教程里的 `PasterDreamEvents.dollRegistry` 为什么不工作

`PasterDreamEvents.dollRegistry` 事件机制在当前版本存在问题，已弃用。
请改用本文档中的 `StartupEvents.registry('block', ...)` + `DollAPI.registerDollWithSkin()` 方案。

### Q：纹理显示为紫黑方块

- 检查纹理路径是 `textures/block/` 不是 `texture/block/`
- 检查图片格式是 PNG
- 检查文件名大小写是否匹配

### Q：抱物后物品不显示

- 检查 `canHoldItems(true)` 是否设置
- 检查抱物模型是否存在且有 `bb_main` 骨骼

### Q：玩偶用手拿的时候旋转不对

内置的 `pasterdream:displaysettings/doll_generic.item` 已配置好手持/展示柜/头顶的显示参数。不需要手动设置。

---

> 如果你觉得这个教程缺少某个场景的示例，或者遇到了文档没覆盖的 bug，欢迎反馈！
