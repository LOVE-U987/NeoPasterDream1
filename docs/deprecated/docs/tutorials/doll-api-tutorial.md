# PasterDream 玩偶 API（DollAPI）使用教程

> 适用版本：Minecraft 1.21.1 / NeoForge 21.1.219 / PasterDream 0.9.0+

## 1. 什么是玩偶 API？

玩偶 API 让你可以用 **Java 代码** 或 **KubeJS 脚本** 快速注册新的玩偶方块。

它的特点是：

- 支持自定义模型与**自定义皮肤纹理**（任意命名空间）。
- 抱物功能通过开关控制，旧模型只要提供 `_holding.geo.json` 也能开启。
- 同时支持“新模型”与“旧模型”两种工作流。
- KubeJS 是**可选依赖**，未安装 KubeJS 时 Java API 仍可正常使用。

## 2. 注册生命周期（Java）

如果你通过 Java API 注册玩偶，必须在模组主类的构造方法里把 `DollAPI` 的两个注册器挂到 **mod 事件总线** 上：

```java
DollAPI.BLOCK_REGISTRY.register(modEventBus);
DollAPI.ITEM_REGISTRY.register(modEventBus);
```

完整示例：

```java
public class MyMod {
    public MyMod(IEventBus modEventBus) {
        DollAPI.BLOCK_REGISTRY.register(modEventBus);
        DollAPI.ITEM_REGISTRY.register(modEventBus);

        MyModDolls.register();
    }
}
```

如果跳过这一步，通过 `DollAPI.create(...).register()` 注册的方块和物品不会进入游戏，也不会在创造标签页或 `/give` 命令中出现。

> 注意：**渲染注册已经是自动的**。`RendererRegistry` 和 `PDClientItemExtensions` 会内部处理方块实体渲染器（BER）与物品渲染器，作者不需要手动调用 `event.registerBlockEntityRenderer(...)` 或 `CuriosRendererRegistry.register(...)` 之类的代码。

## 3. 资源文件约定

每注册一个名为 `my_doll`、命名空间为 `<namespace>` 的玩偶，典型的资源目录结构如下：

```text
assets/<namespace>/
  geo/block/my_doll.geo.json
  geo/block/my_doll_holding.geo.json   # 仅在 canHoldItems=true 且使用新模型或旧模型抱物时需要
  textures/block/my_doll.png
  blockstates/my_doll.json             # 如果使用 DataGen（PDBlockModelProvider）则自动生成
  models/item/my_doll.json             # 如果使用 DataGen 则自动生成
  lang/zh_cn.json
```

在语言文件里添加：

```json
{
  "block.<namespace>.my_doll": "我的玩偶",
  "item.<namespace>.my_doll": "我的玩偶"
}
```

> 提示：旧模型（legacy）默认路径与上表一致；新模型可以通过 `.model(...)` 和 `.texture(...)` 把路径改到任意命名空间。

## 4. Java API 用法

在模组的注册类里写：

```java
import com.pasterdream.pasterdreammod.api.doll.DollAPI;
import net.minecraft.resources.ResourceLocation;

public class MyModDolls {
    public static void register() {
        DollAPI.create("my_doll")
            .model(ResourceLocation.fromNamespaceAndPath("mypack", "geo/block/my_doll.geo.json"))
            .texture(ResourceLocation.fromNamespaceAndPath("mypack", "textures/block/my_doll_skin.png"))
            .canHoldItems(true)
            .register();
    }
}
```

然后在主类/初始化阶段调用 `MyModDolls.register()` 即可。

### Builder 可用方法

| 方法 | 说明 |
|---|---|
| `model(ResourceLocation)` | 基础模型路径，默认 `pasterdream:geo/block/<name>.geo.json` |
| `texture(ResourceLocation)` | 皮肤纹理路径，默认 `pasterdream:textures/block/<name>.png` |
| `canHoldItems(boolean)` | 是否允许抱物，默认 `false` |
| `holdingModel(ResourceLocation)` | 抱物模型路径，默认 `<name>_holding.geo.json` |
| `legacy()` | 切换到旧模型工作流（默认是新模型） |
| `itemProperties(Item.Properties)` | 物品属性 |
| `blockProperties(BlockBehaviour.Properties)` | 方块属性 |

## 5. 新模型 vs 旧模型

玩偶 API 提供两种模型工作流，你可以根据已有资源选择。

### 新模型（默认）

使用 `DollBlockModel` 渲染，特点包括：

- 通过 `.model(...)` 和 `.texture(...)` 显式指定模型与皮肤纹理的 `ResourceLocation`。
- 支持双层皮肤纹理（例如类似原版玩家皮肤的 64×64 布局）。
- 支持 `.holdingModel(...)` 自定义抱物模型；如果未显式设置，默认会寻找 `<name>_holding.geo.json`。

新模型适合从零开始制作玩偶，或需要精确控制模型路径与纹理位置的场景。

### 旧模型

如果你的资源沿用 `love_u_doll` / `eoul_doll` 等早期玩偶的约定，请在 builder 上调用 `.legacy()`：

```java
DollAPI.create("my_old_doll")
    .legacy()
    .canHoldItems(true)
    .register();
```

旧模型会按以下约定自动解析资源：

- 基础模型：`geo/block/<name>.geo.json`
- 基础纹理：`textures/block/<name>.png`
- 抱物模型：`geo/block/<name>_holding.geo.json`（开启抱物时）

旧模型的纹理路径不会随抱物状态改变，因此基础纹理和抱物纹理共用同一张 `textures/block/<name>.png`。

### 抱物模型中的 `bb_main` 骨骼

无论是新模型还是旧模型，只要开启抱物，抱物模型里都需要一个名为 **`bb_main`** 的骨骼。API 会把被抱物品渲染到 `bb_main` 骨骼的位置上；如果缺少该骨骼，被抱物品会不可见。

如果玩偶不抱物时也要显示手持物品，可以在基础模型里同样放置 `bb_main` 骨骼，但这通常只在抱物模型中需要。

## 6. KubeJS 用法

确保已安装 KubeJS。在 `kubejs/startup_scripts/` 下新建脚本：

```js
PasterDreamEvents.dollRegistry(event => {
    event.create("my_doll")
        .model("mypack:geo/block/my_doll.geo.json")
        .texture("mypack:textures/block/my_doll_skin.png")
        .canHoldItems(true)
        .register();
});
```

### KubeJS 中设置语言名

```js
ClientEvents.lang('zh_cn', event => {
    event.add('block.pasterdream.my_doll', '我的玩偶');
    event.add('item.pasterdream.my_doll', '我的玩偶');
});
```

## 7. 抱物功能说明

- 开启 `.canHoldItems(true)` 后，玩家手持任意物品对方块按 **右键** 即可让玩偶抱住该物品。
- 再次空手右键可取下物品。
- 模型会自动切换到抱物模型：
  - **新模型**：如果调用过 `.holdingModel(...)`，使用该路径；否则默认使用 `geo/block/<name>_holding.geo.json`。
  - **旧模型**：必须提供 `geo/block/<name>_holding.geo.json`，纹理仍使用 `textures/block/<name>.png`。
- 如果未提供抱物模型，游戏会显示紫黑缺失纹理的立方体。
- 抱物模型中必须包含 **`bb_main`** 骨骼，否则被抱物品不会渲染。

## 8. 常见问题

### 纹理不显示

1. 检查纹理路径是否为 `textures/...`，而不是 `texture/...`。
2. 检查命名空间是否拼写正确。
3. 检查文件是否被打包进 jar / 资源包。

### 模型不显示

1. 检查 `.geo.json` 是否放在 `geo/block/` 下，而不是 `geo/entity/`。
2. 检查 JSON 是否合法（可用 VS Code 的 JSON 插件验证）。
3. 查看 `latest.log` 是否有 GeckoLib 加载失败的提示。

### 未安装 KubeJS 时脚本不生效

这是预期行为。玩偶 API 的 Java 部分不依赖 KubeJS；如果你用 KubeJS 注册玩偶，自然需要 KubeJS 环境。

### 玩偶注册后在游戏内完全找不到

最常见的原因是**没有将 `DollAPI.BLOCK_REGISTRY` 和 `DollAPI.ITEM_REGISTRY` 注册到 mod 事件总线**。请参考上文“注册生命周期（Java）”一节，在模组主类构造方法里添加：

```java
DollAPI.BLOCK_REGISTRY.register(modEventBus);
DollAPI.ITEM_REGISTRY.register(modEventBus);
```

### 开启抱物后物品不显示

请依次检查：

1. 是否设置了 `.canHoldItems(true)`。
2. 是否提供了正确的抱物模型：
   - 新模型：`.holdingModel(...)` 或 `geo/block/<name>_holding.geo.json`。
   - 旧模型：`geo/block/<name>_holding.geo.json`。
3. 抱物模型里是否有名为 `bb_main` 的骨骼。缺少该骨骼时，被抱物品会渲染到世界原点或完全不可见。

### 模型/纹理路径放错目录

玩偶 API 的资源约定是 `geo/block/` 和 `textures/block/`，不是 `geo/entity/` 或 `textures/entity/`。例如：

- 错误：`textures/entity/my_doll.png`
- 正确：`textures/block/my_doll.png`

### KubeJS 脚本没反应

玩偶注册脚本必须写在 `kubejs/startup_scripts/` 目录下。放在 `server_scripts/` 或 `client_scripts/` 都不会在正确的注册阶段执行。

### ResourceLocation 命名空间写错

字符串中的命名空间必须与资源文件实际所在命名空间一致。例如资源在 `assets/mypack/geo/block/my_doll.geo.json`，代码里就要用 `mypack:geo/block/my_doll.geo.json`，而不是 `pasterdream:...` 或 `kubejs:...`。

## 9. 示例：用玩家皮肤布局的 2 层玩偶

如果你想要一个类似原版玩家皮肤的 2 层玩偶：

1. 用 Blockbench 制作一个 64×64 纹理的玩偶模型。
2. 提供基础模型 `my_doll.geo.json` 与抱物模型 `my_doll_holding.geo.json`。
3. 通过 `.texture(...)` 指向你的皮肤纹理：

```java
.texture(ResourceLocation.fromNamespaceAndPath("mypack", "textures/block/my_skin.png"))
```

`texture(...)` 可以接受任意 `ResourceLocation`，但约定上建议把玩偶皮肤纹理放在 `textures/block/` 下。

4. 模型 UV 按你的纹理布局调整即可。

## 10. 进阶：获取已注册玩偶

```java
DollAPI.getRegistration("my_doll").ifPresent(result -> {
    Block block = result.block().get();
    Item item = result.item().get();
});
```

---

祝玩得开心！如果遇到 API 本身的 bug，请保留 `latest.log` 并反馈。
