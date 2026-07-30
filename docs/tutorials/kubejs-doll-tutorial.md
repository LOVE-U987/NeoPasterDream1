# PasterDream 玩偶 KubeJS 注册教程

> 适用版本：Minecraft 1.21.1 / NeoForge 21.1.219 / KubeJS 7.x / PasterDream 0.9.0+

## 目录

1. [前置条件](#1-前置条件)
2. [快速上手](#2-快速上手)
3. [注册一个完整玩偶](#3-注册一个完整玩偶)
4. [设置中文名](#4-设置中文名)
5. [资源文件准备](#5-资源文件准备)
6. [真实案例：注册幻胧玩偶](#6-真实案例注册幻胧玩偶)
7. [新模型 vs 旧模型](#7-新模型-vs-旧模型)
8. [常见问题](#8-常见问题)

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

玩偶注册脚本必须放在 **`kubejs/startup_scripts/`** 目录下（启动阶段注册用）：

```
kubejs/
  startup_scripts/    ← 玩偶注册放这里
  server_scripts/     ← × 不行
  client_scripts/     ← × 不行
```

> 放到 `server_scripts/` 或 `client_scripts/` 都不会在正确的注册阶段执行，玩偶不会出现在游戏中。

---

## 2. 快速上手

创建 `kubejs/startup_scripts/my_doll.js`，内容如下：

```js
PasterDreamEvents.dollRegistry(event => {
    event.create("my_doll")
        .model("kubejs:geo/block/my_doll.geo.json")
        .texture("kubejs:textures/block/my_doll.png")
        .canHoldItems(true)
        .register();
});
```

然后在 `kubejs/assets/kubejs/` 下准备好对应的资源文件（详见[第 5 节](#5-资源文件准备)），重启游戏即可。

### JS Builder 可用方法

| 方法 | 说明 |
|------|------|
| `model(path)` | 基础模型路径，默认 `pasterdream:geo/block/<name>.geo.json` |
| `texture(path)` | 皮肤纹理路径，默认 `pasterdream:textures/block/<name>.png` |
| `canHoldItems(bool)` | 是否允许抱物，默认 `false` |
| `holdingModel(path)` | 抱物模型路径，默认 `pasterdream:geo/block/<name>_holding.geo.json` |
| `register()` | 执行注册（最后必须调用） |

> 对比 Java API，JS Builder 不支持 `legacy()`、`itemProperties()`、`blockProperties()` 方法。
> 如果你需要旧模型，请使用 Java API 注册。KubeJS 默认使用新模型工作流。

---

## 3. 注册一个完整玩偶

完整玩偶的注册脚本通常包含以下要素，这里以注册一个可爱的"棉花云"玩偶为例：

```js
// kubejs/startup_scripts/cotton_cloud_doll.js
PasterDreamEvents.dollRegistry(event => {
    // 1. 创建玩偶，命名为 cotton_cloud_doll（snake_case）
    const doll = event.create("cotton_cloud_doll");
    
    // 2. 指定模型路径（必须指向 .geo.json 文件）
    doll.model("kubejs:geo/block/cotton_cloud_doll.geo.json");
    
    // 3. 指定皮肤纹理路径（必须指向 .png 文件）
    doll.texture("kubejs:textures/block/cotton_cloud_doll.png");
    
    // 4. 开启抱物功能后，按默认规则会自动寻找 _holding.geo.json
    doll.canHoldItems(true);
    
    // 5. 也可以显式指定抱物模型路径
    doll.holdingModel("kubejs:geo/block/cotton_cloud_doll_holding.geo.json");
    
    // 6. 注册（必须调用）
    doll.register();
});
```

可以链式调用写在一行：

```js
PasterDreamEvents.dollRegistry(event => {
    event.create("cotton_cloud_doll")
        .model("kubejs:geo/block/cotton_cloud_doll.geo.json")
        .texture("kubejs:textures/block/cotton_cloud_doll.png")
        .canHoldItems(true)
        .register();
});
```

### 不抱物的玩偶

如果不想要抱物功能，删掉 `canHoldItems` 即可：

```js
PasterDreamEvents.dollRegistry(event => {
    event.create("simple_doll")
        .model("kubejs:geo/block/simple_doll.geo.json")
        .texture("kubejs:textures/block/simple_doll.png")
        .register();
});
```

此时不需要提供 `<name>_holding.geo.json`。

---

## 4. 设置中文名

玩偶注册后默认显示注册名（如 `block.pasterdream.my_doll`），需要在语言文件中添加翻译。

### 方式一：KubeJS 语言事件（推荐）

在同一个 `startup_scripts/` 或 `client_scripts/` 中：

```js
// kubejs/client_scripts/doll_lang.js
ClientEvents.lang('zh_cn', event => {
    event.add('block.pasterdream.cotton_cloud_doll', '棉花云玩偶');
    event.add('item.pasterdream.cotton_cloud_doll', '棉花云玩偶');
});
```

```js
ClientEvents.lang('en_us', event => {
    event.add('block.pasterdream.cotton_cloud_doll', 'Cotton Cloud Doll');
    event.add('item.pasterdream.cotton_cloud_doll', 'Cotton Cloud Doll');
});
```

### 方式二：直接修改资源包

如果你在制作资源包，可以在 `assets/pasterdream/lang/zh_cn.json` 中添加：

```json
{
  "block.pasterdream.cotton_cloud_doll": "棉花云玩偶",
  "item.pasterdream.cotton_cloud_doll": "棉花云玩偶"
}
```

> 注意：用 KubeJS 注册的玩偶**注册命名空间固定为 `pasterdream`**，所以语言键是 `pasterdream.cotton_cloud_doll`，不是 `kubejs.cotton_cloud_doll`。

---

## 5. 资源文件准备

玩偶注册除了脚本，还需要对应的模型和纹理文件。

### 目录结构

```
kubejs/assets/pasterdream/
├── geo/
│   └── block/
│       ├── my_doll.geo.json          ← 基础模型（必选）
│       └── my_doll_holding.geo.json  ← 抱物模型（开启抱物时必选）
└── textures/
    └── block/
        └── my_doll.png               ← 皮肤纹理（必选）
```

> 是的，路径前缀必须是 `pasterdream`（因为注册命名空间是 `pasterdream`），即使你是通过 KubeJS 注册的。

### 快捷提示

- 模型文件用 **Blockbench** 制作，导出为 GeckoLib 格式（`.geo.json`）
- 纹理尺寸建议 **64×64**（复制玩家皮肤布局）
- **模型和纹理文件名**必须与注册名的 snake_case 一致

### 命名空间说明

通过 KubeJS 注册的玩偶，命名空间固定为 `pasterdream`。所以：
- 脚本中 `model()` / `texture()` 的路径如果省略命名空间，默认也是 `pasterdream`
- 例如 `.model("pasterdream:geo/block/my_doll.geo.json")` 和 `.model("geo/block/my_doll.geo.json")` 等价

> 如果你把资源文件放到 `kubejs/assets/kubejs/` 下，就需要显式指定 `"kubejs:geo/block/..."`。

---

## 6. 真实案例：注册幻胧玩偶

以下是一个完整的真实案例，使用模组内置的玩家皮肤模型和抱物模型：

### 第一步：准备纹理

把 `phantom_daze.png`（64×64 皮肤纹理）放到：
```
kubejs/assets/pasterdream/textures/block/phantom_daze.png
```

### 第二步：准备模型

把 `phantom_daze.geo.json` 和 `phantom_daze_holding.geo.json` 放到：
```
kubejs/assets/pasterdream/geo/block/
```

如果你没有自己的模型，可以**从模组原文件复制**：
- `PasterDream/src/main/resources/assets/pasterdream/geo/block/eoul_doll.geo.json`
- `PasterDream/src/main/resources/assets/pasterdream/geo/block/eoul_doll_holding.geo.json`

复制后重命名为 `phantom_daze.geo.json` 和 `phantom_daze_holding.geo.json` 即可。

### 第三步：写注册脚本

```js
// kubejs/startup_scripts/phantom_daze_doll.js
PasterDreamEvents.dollRegistry(event => {
    event.create("phantom_daze")
        .model("pasterdream:geo/block/phantom_daze.geo.json")
        .texture("pasterdream:textures/block/phantom_daze.png")
        .holdingModel("pasterdream:geo/block/phantom_daze_holding.geo.json")
        .canHoldItems(true)
        .register();
});

// 也可以直接用默认路径（资源放在约定位置时）：
// PasterDreamEvents.dollRegistry(event => {
//     event.create("phantom_daze")
//         .canHoldItems(true)
//         .register();
// });
```

> 如果资源文件放在 `kubejs/assets/pasterdream/` 的标准路径下，甚至可以省略 `model()` 和 `texture()`——API 会自动寻找默认位置。

### 第四步：添加语言

```js
// kubejs/client_scripts/phantom_daze_lang.js
ClientEvents.lang('zh_cn', event => {
    event.add('block.pasterdream.phantom_daze', '小大幻翼');
    event.add('item.pasterdream.phantom_daze', '小大幻翼');
});
```

### 第五步：重启游戏

重启后：
- 用 `/give @s pasterdream:phantom_daze` 获取玩偶
- 摆放后右键手持物品→玩偶抱物
- 空手右键→取下物品

---

## 7. 新模型 vs 旧模型

### 新模型（KubeJS 默认）

通过 KubeJS 注册的玩偶**默认使用新模型**（`DollModelType.NEW`），特点：

- 可以通过 `model()` 和 `texture()` 显式指定任意路径
- 支持双层皮肤纹理（64×64 类似玩家皮肤的布局）
- 支持通过 `holdingModel()` 自定义抱物模型

```js
// 新模型（默认）
PasterDreamEvents.dollRegistry(event => {
    event.create("my_new_doll")
        .model("mypack:geo/block/custom_model.geo.json")
        .texture("mypack:textures/block/custom_skin.png")
        .canHoldItems(true)
        .register();
});
```

### 旧模型（仅 Java API 支持）

KubeJS 的 JS Builder 目前不提供 `.legacy()` 方法。
如果你需要使用旧模型工作流，请通过 Java API 注册。

---

## 8. 常见问题

### Q：脚本写了但玩偶不出现

排查顺序：

1. **是否放对目录** → 必须 `kubejs/startup_scripts/`
2. **是否有 KubeJS + Rhino** → 看日志有没有 `[PasterDream] KubeJS 插件已初始化`
3. **模型/纹理文件是否存在** → 检查 `kubejs/assets/pasterdream/` 下的路径
4. **文件命名是否正确** → 注册名和文件名必须一致（注册 `my_doll` → 模型 `my_doll.geo.json`）
5. **语法错误** → 看日志是否有 JS 报错

### Q：报错 "Cannot read properties of undefined (reading 'dollRegistry')"

这说明 `PasterDreamEvents` 没有注册成功。原因：
- KubeJS 插件没有加载 → 检查 KubeJS / Rhino 版本是否兼容
- PasterDream 版本太旧 → 更新到支持 DollAPI 的版本

### Q：纹理显示为紫黑方块

- 检查纹理路径是 `textures/block/` 不是 `texture/block/`
- 检查图片格式是 PNG
- 检查文件名大小写是否匹配

### Q：抱物后物品不显示

- 检查 `canHoldItems(true)` 是否设置
- 检查抱物模型是否存在且有 `bb_main` 骨骼
- 检查抱物模型路径是否正确

### Q：玩偶用手拿的时候旋转不对

玩偶的物品显示由内置的 `displaysettings/doll_default.item` 控制，默认已配置好手持/展示柜/头顶的显示参数。
你不需要手动设置物品模型 JSON——`PDBlockModelProvider` 会自动帮你生成。

---

> 如果你觉得这个教程缺少某个场景的示例，或者遇到了文档没覆盖的 bug，欢迎反馈！
