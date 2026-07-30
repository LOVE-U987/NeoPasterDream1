# 玩偶 API（DollAPI）设计文档

> 状态：待实现\
> 目标版本：Minecraft 1.21.1 / NeoForge 21.1.219 / GeckoLib 4.7.3

## 1. 背景与目标

项目已有两类玩偶：

* **旧模型**：`qin_doll_0`、`little_purple_doll_0` 等单层静态玩偶，无抱物能力。

* **新模型**：`love_u_doll`、`eoul_doll` 等双层模型玩偶，已有 `_holding` 变体并支持抱物。

本 API 目标：

1. 让玩家 / 整合包作者可通过 **KubeJS 脚本** 快速注册自定义玩偶。
2. 同时为模组内部提供 **Java API**，统一玩偶注册流程。
3. 新玩偶支持**玩家指定皮肤纹理**（自定义纹理路径）。
4. 抱物功能通过开关控制；旧模型只要提供了 `_holding.geo.json` 也能开启。

## 2. 设计原则

* **独立门面**：玩偶注册比普通方块复杂（方块实体、渲染器、模型切换、抱物逻辑、动态纹理），因此单独建立 `DollAPI`，不与 `BlockAPI` 合并。

* **旧/新模型不区分类型**：API 只认 `.geo.json` 路径与纹理路径。模型是“单层”还是“双层”由资源文件决定。

* **KubeJS 可选依赖**：未安装 KubeJS 时，Java API 仍可正常工作；所有 KubeJS 相关类必须做空值 / 可选加载检查。

* **统一渲染**：所有 API 玩偶共用同一套 BlockRenderer / ItemRenderer / GeoModel，通过运行时查表获取各自配置。

## 3. 模块归属

| 文件/类                                                                              | 所属模块                        | 说明          |
| --------------------------------------------------------------------------------- | --------------------------- | ----------- |
| `DollAPI`                                                                         | `PasterDream`               | Facade 入口（强依赖主模块渲染与方块实体） |
| `DollBuilder`                                                                     | `PasterDream`               | 链式 Builder  |
| `DollConfig` / `DollResult`                                                       | `PasterDream`               | 配置数据 / 注册结果 |
| `DollBlock`                                                                       | `PasterDream`               | 通用玩偶方块类     |
| `DollBlockEntity`                                                                 | `PasterDream`               | 通用玩偶方块实体    |
| `DollDisplayItem`                                                                 | `PasterDream`               | 通用玩偶物品类     |
| `DollBlockRenderer` / `DollItemRenderer` / `DollModel`                            | `PasterDream` (client only) | 渲染与模型       |
| `PasterDreamKubeJSPlugin` / `PasterDreamKubeJSEvents` / `DollRegistryKubeJSEvent` | `PasterDream` (optional)    | KubeJS 事件包装 |

## 4. 核心 API

### 4.1 Java 用法

```java
DollAPI.create("my_doll")
    .model(ResourceLocation.fromNamespaceAndPath("mypack", "geo/block/my_doll.geo.json"))
    .texture(ResourceLocation.fromNamespaceAndPath("mypack", "textures/block/my_doll_skin.png"))
    .canHoldItems(true)
    .itemProperties(new Item.Properties().rarity(Rarity.RARE))
    .register();
```

### 4.2 KubeJS 用法

```js
PasterDreamEvents.dollRegistry(event => {
    event.create("my_doll")
        .model("mypack:geo/block/my_doll.geo.json")
        .texture("mypack:textures/block/my_doll_skin.png")
        .canHoldItems(true)
        .register();
});
```

### 4.3 Builder 配置项

| 方法                                           | 默认值                                             | 说明               |
| -------------------------------------------- | ----------------------------------------------- | ---------------- |
| `name(String)`                               | 必填                                              | 注册名（snake\_case） |
| `model(ResourceLocation)`                    | `pasterdream:geo/block/<name>.geo.json`         | 基础模型路径           |
| `texture(ResourceLocation)`                  | `pasterdream:textures/block/<name>.png`         | 皮肤纹理路径           |
| `canHoldItems(boolean)`                      | `false`                                         | 是否允许抱物           |
| `holdingModel(ResourceLocation)`             | `pasterdream:geo/block/<name>_holding.geo.json` | 抱物模型路径           |
| `itemProperties(Item.Properties)`            | 普通属性                                            | 物品属性             |
| `blockProperties(BlockBehaviour.Properties)` | 装饰罐音效、强度 1.0                                    | 方块属性             |

## 5. 内部实现

### 5.1 方块与方块实体

* 所有玩偶共用同一个 `DollBlock extends MemorialDollBlock`。

* `DollAPI` 内部维护 `Map<Block, DollConfig>`，通过方块对象身份映射到对应配置。

* `DollAPI` 注册一个共享的 `BlockEntityType<doll>`，validBlocks 包含所有已注册玩偶方块。

* `DollBlock#newBlockEntity` 返回 `DollBlockEntity(sharedType, pos, state)`。

* `DollBlock#useItemOn` 先检查 `DollConfig#canHoldItems()`，为 `false` 时直接 `PASS_TO_DEFAULT_BLOCK_INTERACTION`。

### 5.2 渲染

* `DollBlockRenderer`：注册到共享 BlockEntityType，根据当前 BlockState 的 block 查 `DollConfig`，返回对应模型与纹理；根据 `HOLDING` block state 切换基础/抱物模型。

* `DollItemRenderer`：注册到 `DollDisplayItem` 类，从物品持有的 block 反查配置。

* `DollModel`：同时服务 Block 与 Item，核心逻辑为 `ResourceLocation getModelResource(T)` / `getTextureResource(T)`，内部统一走 `DollAPI.getConfig(Block)`。

### 5.3 动态纹理

* 纹理路径完全由 `DollConfig.texture()` 决定，支持任意命名空间（如 `kubejs:block/my_skin`）。

* 模型文件中的 UV 布局由资源作者负责，API 不做额外校验。

### 5.4 注册时序

1. `PasterDreamMod` 构造函数中：手动注册 `DollAPI.BLOCK_REGISTRY` 与 `DollAPI.ITEM_REGISTRY` 到 `modEventBus`。
2. KubeJS 启动脚本 / Java API 调用 `DollAPI.create(...).register()`，向 `DollAPI` 的 DeferredRegister 添加条目，并记录 `DollConfig`。
3. 注册事件触发时，NeoForge 完成 Block / Item / BlockEntityType 注册。
4. 客户端事件注册统一的 Renderer。

## 6. 资源文件约定

每注册一个名为 `<name>` 的玩偶，资源作者至少需要提供：

```text
assets/<namespace>/
  geo/block/<name>.geo.json
  textures/block/<name>.png
  lang/zh_cn.json        # 添加 item.<namespace>.<name> 与 block.<namespace>.<name>
```

若开启 `canHoldItems(true)`，还需提供：

```text
assets/<namespace>/
  geo/block/<name>_holding.geo.json
```

> 纹理路径可通过 `.texture(...)` 改为其他位置；模型路径同理。

## 7. 依赖变更

* `PasterDream/build.gradle` 新增 KubeJS **可选依赖**：

  * `compileOnly`：保证编译时可用。

  * 不强制运行时安装 KubeJS。

* 主模组代码中凡涉及 KubeJS 类的地方，均需通过 `ModList.get().isLoaded("kubejs")` 或 Optional 检查，避免未安装时类加载失败。

## 8. 验收标准

* [ ] 不安装 KubeJS 时，模组能正常启动，Java API 可注册玩偶。

* [ ] 安装 KubeJS 时，脚本可通过 `PasterDreamEvents.dollRegistry(...)` 注册玩偶。

* [ ] 新玩偶能正确显示指定纹理。

* [ ] 开启 `canHoldItems(true)` 的玩偶能正常抱物/取物，未开启的玩偶不能触发抱物逻辑。

* [ ] 旧模型在提供 `_holding.geo.json` 后也能抱物。

* [ ] `gradlew compileJava` 与 `gradlew runClient` 无错误。

* [ ] 交付一份 `docs/tutorials/doll-api-tutorial.md` 教程文件。

