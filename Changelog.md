# PasterDream Changelog

---

## v0.9.4 — 2026-08-06

### 修复：烈焰花（flower_6）正常采集不掉落烈焰粉

*   **背景**：`DyedreamFlowerBlock.getDrops()` 覆写了所有染梦花的掉落逻辑——只有剪刀采集才掉本体，空手/其它工具一律返回空列表。因此 `flower_6` 虽配置了战利品表，但被 Java 逻辑完全绕过，正常采集什么都掉不出来。
*   **修复**：
    * 新增 `BlazeFlowerBlock`（继承 `DyedreamFlowerBlock`）：**剪刀采集掉自身**（不变），**正常采集（空手/其它工具）掉 1 个烈焰粉** `minecraft:blaze_powder`
    * `PDBlocksVegetation` 将 `flower_6` 从 `FLOWERS_SINGLE` 批量注册中拆出，改用 `BlazeFlowerBlock` 单独注册（其余花不受影响）
    * 同步更新 `flower_6.json` 战利品表：剪刀 → 掉本体，其余 → 掉烈焰粉
*   **验证**：`:PasterDream:compileJava` 通过；其它花（flower_1/2/3/5/8/9…）掉落行为不变。

---

## v0.9.4 — 2026-08-06

### 清理：移除 geo/animations 根目录冗余副本（63 个）

*   **背景**：资源审计发现 `geo/` 与 `animations/` 根目录存在大量与子目录（`geo/block/`、`geo/entity/`）内容重复的副本文件，是早期路径混乱遗留的冗余资源，增大 jar 体积但无功能影响。
*   **清理 63 个文件**：
    * **53 个**内容与子目录完全一致且未被任何代码/JSON 引用的副本（27 geo + 26 animations）
    * **10 个**实体 geo 旧名孤儿副本（`geo/xxx.geo.json` → 实为 `geo/entity/xxx.geo.json` 的旧 identifier 遗留，如 `geometry.shyspirit`）
*   **保留**：6 个被自定义 GeoModel 硬编码引用根路径的 geo（`angel_wing`、`dream_meter`、`forsakens_wing`、`machine_wing`、`shadow_hand_lantern`、`weakness_terrorbeak`）+ 4 个被硬编码引用的根目录 animations。
*   **验证**：`verify_resource_closure.py` 闭包验证 PASS（3802 JSON），`compileJava` 通过，无任何引用破坏。
*   新增工具脚本：`tools/clean_dup_geo_anim.py`、`tools/clean_dup_entity_geo.py`。

---

## v0.9.4 — 2026-08-06

### 修复：4 个玩偶方块缺失动画文件（死资源）

*   **审计发现**：`DefaultedBlockGeoModel("qin_doll_0" / "little_purple_doll_0" / "eoul_doll" / "love_u_doll")` 会解析 `animations/block/{name}.animation.json`，但 4 个玩偶的动画文件缺失（geo/texture 均存在，原版也无动画 → 静态模型）。
*   **修复**：为 4 个玩偶创建空动画占位文件（内容与 `empty.animation.json` 一致），消除 GeckoLib "Couldn't load animation" 死资源引用。

---

## v0.9.4 — 2026-08-06

### 文档：战利品表 1.21.1 格式规范写入项目规则

*   `AGENTS.md` 新增「战利品表 JSON 格式规范（NeoForge 1.21.1）」章节，固化 `match_tool` predicate 必须用 `predicates."minecraft:enchantments"` 新格式、路径用单数 `loot_table`、常见错误清单与批量校验方式
*   `.github/skills/neoforge-block-drops/SKILL.md` 新增「4a. match_tool predicate 必须用 1.21.1 新格式」陷阱条目，含错误/正确格式对照表与批量校验命令
*   目的：防止其他 AI / 后续开发再次使用 1.20 旧格式导致战利品表静默解析失败（矿石掉本体）

---

## v0.9.4 — 2026-08-06

### 修复：战利品表 match_tool 条件使用 1.20 旧格式导致全部解析失败

*   **真正的根因**（`data/pasterdream/loot_table/blocks/*.json`，23 个文件 62 处）：战利品表 JSON 中的 `match_tool` 条件用了 **1.20 旧格式**：
    ```json
    "predicate": { "enchantments": [ { "enchantment": "minecraft:silk_touch", "levels": { "min": 1 } } ] }
    ```
    而 1.21.1 的 `ItemPredicate`/`EnchantmentPredicate` 要求 **`predicates."minecraft:enchantments"` 嵌套 + `enchantments` 复数**。旧格式解析失败 → 战利品表整体退回 `LootTable.EMPTY` → 矿石挖出**本体**而非粗矿。
*   **修复**：批量将 62 处 `match_tool` 的 predicate 转换为 1.21.1 正确格式：
    ```json
    "predicate": { "predicates": { "minecraft:enchantments": [ { "enchantments": "minecraft:silk_touch", "levels": { "min": 1 } } ] } }
    ```
*   **影响**：修复后钛矿石/炙炎金矿石/深层钛矿石、灵魂矿石、凝风矿石等所有带精准采集判断的方块，普通挖掘掉粗矿、精准采集掉本体，行为恢复正常。303 个战利品表 JSON 全部可解析、无残留错误格式。
*   参考：原版 `data/minecraft/loot_table/blocks/diamond_ore.json` 结构确认。

---

## v0.9.4 — 2026-08-06

### 修复：SelfDropBlock 空战利品表兜底误伤矿石掉落

*   **根因**（`PasterDreamAPI/api/block/SelfDropBlock.java`）：`SelfDropBlock.getDrops()` 原实现为"战利品表返回空列表时就回退掉落方块自身"。对矿石等有战利品表的方块，当战利品表因 `match_tool`/精准采集拦截、`requiresCorrectToolForDrops` 空掉落等原因返回空时，被错误兜底成掉落矿石本体，而非正确的粗矿。
*   **修复**：改为仅当方块**没有实际战利品表**（`getLootTable()` 返回 `BuiltInLootTables.EMPTY`，或实际加载到 `LootTable.EMPTY`）时才回退掉落自身；有战利品表的方块完全交给战利品表，保留"需正确工具/条件不满足无掉落"语义。
*   **影响面评估**：`registerSimpleBlocks` 27 个方块中 25 个有战利品表（含全部矿石），仅 `dyedream_deepstone`、`dyedream_sandstone` 两个纯装饰方块依赖兜底掉落，行为不变。
*   新增 import：`ResourceKey`、`ServerLevel`、`BuiltInLootTables`、`LootTable`

---

## v0.9.4 — 2026-08-06

### 修复：白厄花胸针合并注册至主模组

*   **白厄花胸针（white_flower_body）原本只注册在 PasterDreamSanity 附属模组**，但主模组大量引用它（创造栏、亚伦柯斯手宝箱掉落、smoketest 等），导致仅安装主模组（不装附属）时物品缺失、引用无法解析
*   **合并注册到主模组**（`PasterDream/.../registry/items/PDItemsCurios.java`）：
    * 使用 `CurioAPI.create("white_flower_body").slot(CurioSlot.BODY)` 注册（pasterdream 命名空间，与原版 ID 一致），行为对齐既有 `MELTDREAM_ENERGY_0_RING` 迁移先例
    * 新增主模组物品类 `item/WhiteFlowerBodyItem.java`（自 PasterDreamSanity 迁移，tooltip 文案一致）
    * 主模组 `PDItems` re-export `WHITE_FLOWER_BODY`；创造栏 `PDCreativeTabsCurio` 改为直接显示，不再依赖运行时注册表查询
    * 主模组 `data/curios/tags/item/body.json` 补充 `pasterdream:white_flower_body`
    * **PasterDreamSanity 移除重复注册**（`PDSanityItems` 清空为占位注册器）并删除已迁移的孤儿类 `WhiteFlowerBodyItem.java`
*   引用点确认：`PDSanityHelper` / `PDSanityEffects` 均按 ID 字符串查询，主模组注册后运行时正常解析，无需改动
*   smoketest 注释同步更新

---

## v0.9.4 — 2026-08-06

### 修复：灯影长床进入 / 暗影地牢冷却 / 地牢大门朝向 / 染梦晶芽掉落

*   **灯影之下长床无法进入**（`block/TrueShadowBedBlock.java`）：
    * 原误用 `achievement_shadow_start`（进入灯影世界后才授予 → 死锁），导致前置全完成后首次右键长床仍无法进入
    * 改为对齐原版 `TrueShadowBedPr0Procedure`：判定「上方 y+2 为暮影之笼且 `key=true`（据点守卫完成）+ 已达成 `achievement_hide_9`」，夜晚/雷暴时右键真·影之床即传送至灯影之下
*   **暗影地牢冷却不计时**（`block/ShadowDungeonPortalBlock.java`）：
    * 修复后的完整地牢核心替换了 `BrokenShadowDungeonProtalBlock`，原冷却 tick（`cd` 递增 `time`，1800t 结束）只存在于破损版，替换后冷却永不走动
    * 为 `ShadowDungeonPortalBlock` 补上 `onPlace`/`tick` 冷却逻辑，冷却计时恢复正常；时之沙刷新仍生效
*   **地牢底部通往长床的大门方向出错**（`block/ShadowDungeonDoorBlock.java` + `assets/pasterdream/blockstates/shadow_dungeon_door_*.json` + `shadowdungeondoor_2/3.json`）：
    * 根因：门方块缺 `FACING` 属性，`shadow_dungeon` 遗迹由 jigsaw 以 `RandomRotation` 随机旋转放置时门不跟随 → "遗迹横过来了大门没横过来"
    * 为 `ShadowDungeonDoorBlock` 增加 `FACING`（放置/旋转/镜像），4 个 blockstates 补 facing 四方向 + y 旋转，结构旋转时门同步转向
*   **染梦晶芽破坏掉落本体**（`block/DyedreamBudBlock.java`）：`getDrops` 由掉落花蕾本体改为掉落「染梦晶芽粒」（`dyedream_bud_nugget`）

---

## v0.9.3 — 2026-08-06

### 重构：清除天空盒相关调试日志输出

*   **移除全部调试日志**（`client/sky/SkyboxRenderer.java` / `content/ConstellationSkyContent.java` / `data/SkyboxDataReloadListener.java`）：
    * 删除 `SkyboxRenderer` 中 3 处 DEBUG 日志（渲染状态、每晚随机、黎明回退）与 `LOGGER` 字段
    * 删除 `ConstellationSkyContent` 中望远镜对准 DEBUG 日志（`logAimed` 方法）及 `LOGGER`/节流字段——**观星成就授予逻辑保留**，内联到星点渲染处
    * 删除 `SkyboxDataReloadListener` 数据重载 INFO 日志
    * 无功能性改动，仅清理日志输出

---

## v0.9.3 — 2026-08-06

### 新增：白天到来时回退玩家夜间操作

*   **黎明回退**（`client/sky/SkyboxRenderer.java` `checkDayRollback`）：
    * 每帧检测"夜晚 → 白天"边沿（夜晚因子降至 0.5 以下），触发一次回退：
        * **清空所有玩家用星空枕绘制的连线星体**（`PlayerSkyLinkData.clearAll`，新增）
        * 重置连线星体透明度缓存
        * 快捷栏上方提示"天亮了，昨夜绘制的星空星体已随风消散……"（`message.pasterdream.skylink.day_reset`，仅提示一次）
    * 黄昏（白天 → 夜晚）重置提示标记，允许下一个黎明再次提示；维度切换后重置检测状态避免误触发
*   **望远镜瞄准状态回退**（`client/sky/content/ConstellationSkyContent.java`）：夜晚因子 ≤ 0.5（白天）时所有星点的瞄准放大进度 `aimState[]` 强制归零——玩家观星/放大进度随天亮一并回退
*   **新增语言键 2 个**（zh_cn/en_us 成对）：`message.pasterdream.skylink.day_reset`

---

## v0.9.3 — 2026-08-06

### 修复：羽星占卜图录 / 星空枕无法从融梦水晶箱开出

*   **根因**（`block/MeltdreamChestBlock.java`）：融梦水晶箱的掉落是 **Java 代码硬编码**的三档品质池（普通/稀有/传说），**不读取 JSON 战利品表**。此前仅在 `data/pasterdream/loot_table/chests/*.json`（遗迹宝箱 `loots_relic_*`、深藏宝物 `loots_deep_treasure_*`）添加条目，对融梦水晶箱无效
*   **修复**：
    * 稀有品质池（30% 概率档）新增 `memento_item_03`（羽星占卜图录）、`memento_item_08`（星空枕），权重各 6
    * 传说品质池（20% 概率档）新增两件，权重各 8（更高概率开出）
    * 遗迹宝箱 / 深藏宝箱的 JSON 条目保留（两条途径均有效）
*   **tooltip 文案更新**（zh_cn/en_us）：获取方式由"可在染梦世界的宝箱中发现"改为"可在染梦世界的**遗迹宝箱或融梦水晶箱**中发现"

---

## v0.9.3 — 2026-08-06

### 调整：十字星发光效果放大

*   **十字星改为大型发光效果**（`client/sky/content/ConstellationSkyContent.java` `renderAimCrosshairs`）：
    * 臂长从星大小的 0.85~1.15 倍提升至 **2.6~3.3 倍**（`CROSS_ARM_BASE=2.6` + 闪烁伸缩 ±0.7），呈现"星星在发光"的辐射感
    * 三层由外到内叠加（宽 0.17 暗光晕 → 中 0.10 中亮 → 细 0.045 亮核），颜色由暖白过渡到更白的亮核，形成发光渐变
    * 闪烁循环、淡入淡出、Iris 兼容（仍为标准混合 + AFTER_SKY）保持不变

---

## v0.9.3— 2026-08-06

### 修复：昼夜/群系天空淡入淡出；望远镜需对准星心；占卜对地可用；极光加强5倍；观星特效

*   **昼夜交替平滑淡入淡出**（`client/sky/SkyboxRenderer.java` `getNightFactor`）：
    * 夜晚因子改为 `clamp(-cos×3)` + smoothstep 曲线——白天严格 0，黄昏/黎明更柔和连续地渐入渐出（原斜率 4 + 0.2 基线过渡偏硬）
*   **群系切换淡入淡出**（`client/sky/SkyboxRenderer.java`）：
    * 根因：群系切换立即替换候选，旧/新条目 alpha 交叉过快，视觉上星星与天体"突然消失/出现"
    * 修复：新增 `lastBiomeSwitchTime` + 40 tick 过渡窗口，窗口内 alpha 交叉速度 ×0.35（约 2 秒平滑交叉）；维度切换时一并重置
*   **望远镜需对准星星中心才触发**（`client/sky/content/ConstellationSkyContent.java` `isAimed`）：
    * 阈值从固定 0.06 rad 改为按星大小动态计算：`size/天空半径 + 0.008 rad`——只有准星真正对准星本体（中心）才触发放大/成就
*   **单星独立缓慢进入/退出动画**（`ConstellationSkyContent`）：
    * 新增每颗星独立的 `aimState[]`（原全局静态 `aimProgress` 仅作望远镜开关与连线增亮）
    * 对准时该星以 `AIM_TRANSITION_SPEED=0.045/tick`（约 1 秒）缓慢放大变亮，移开视线以同样速度缓慢退出
*   **十字星循环闪烁**（`ConstellationSkyContent.renderAimCrosshairs`）：
    * 对准的星中心新增暖白十字标记：沿 yaw/pitch 正交臂，淡入完成后按正弦循环变亮变暗（臂长随闪烁伸缩）
    * 移开视线随 `aimState` 平滑淡出；松开望远镜立即隐藏
*   **极光加强 5 倍**（`client/sky/content/AuroraSkyContent.java`）：
    * 新增 `BRIGHTNESS_BOOST=5`：有效透明度（含双重 opacity）整体 ×5，峰值约 0.51 不过曝
    * 保持 AFTER_SKY 挂载 + 标准混合，Iris 光影下兼容
*   **修复占卜对地面/不对准天体也可用**（`item/DivinationItem.java`）：
    * 根因：`AIM_THRESHOLD=0.15 rad` 过大 + 未限制视线方向——玩家看地面时逆变换的局部方向可能命中地平线附近的连线星体，且扫过即"对准"
    * 修复：阈值收紧至 0.08 rad；新增 `MIN_LOOK_Y=-0.05`（看向地面/水平以下直接拒绝）；服务端同样校验抬头；双端均拒绝"对地占卜"

---

## v0.9.3 — 2026-08-06

### 修复：望远镜放大仍过大 / 占卜未对准也能用；星空枕左键移除；夜间功能限制；夜空交互成就

*   **望远镜放大进一步克制**（`client/sky/content/ConstellationSkyContent.java`）：
    * 程序化星 ×1.6→×1.25、纹理星 ×1.25→×1.1、亮度 ×2.0→×1.8、对准阈值 0.07→0.06 rad——望远镜下星点仅轻微放大，不再遮挡星座全貌（星空枕连线星体大小保持原样）
*   **白天禁用夜间功能**（`item/SkyLinkItem.java` + `item/DivinationItem.java` + `client/sky/SkyboxRenderer.java`）：
    * 新增 `SkyboxRenderer.isNight()`（夜晚因子 > 0.5）；星空枕创建/移除、羽星占卜在白天使用均提示 `message.*.night_only`（"晚上再来试试吧~"）并拒绝执行
*   **修复占卜"未对准也能用、双提示都触发"**：
    * 根因：`isCelestialTargeted` 未检查夜晚，白天连线星体数据仍保留（星体不可见但可命中）→ 白天视线扫过隐藏星体即"对准"成功
    * 修复：`isCelestialTargeted` 开头加夜晚检查（白天恒 false）；`DivinationItem` 服务端增加夜晚兜底校验（`isServerNight`），双端均拒绝白天占卜
*   **占卜成功提示位置调整**（`item/DivinationItem.java`）：占卜结果由聊天框（`sendSystemMessage`）改为快捷栏上方 action bar（`displayClientMessage(..., true)`），与"请先对准天体"提示同位置
*   **星空枕左键移除**（`item/SkyLinkItem.java` + 新增 `client/SkyLinkItemEvents.java`）：
    * 右键仅负责创建星体；左键（`PlayerInteractEvent.LeftClickBlock`/`LeftClickEmpty`）对准已创建星体时移除（`SkyLinkItem.tryRemoveStarAt`）
    * 对准方块左键移除成功后取消事件（不破坏方块）；白天左键同样提示晚上再用
*   **连线星体开放链**（`client/sky/content/SkyLinkContent.java`）：移除首尾闭环，星体按创建顺序依次连接成开放链（不再闭合为集合体）
*   **夜空交互成就 ×3**（`data/pasterdream/advancement/achievement_stargaze|skylink|divination.json`）：
    * 观星者：用望远镜对准星座星点（`ConstellationSkyContent` 首次对准时授予）
    * 织星者：用星空枕创建第一颗连线星体（`SkyLinkItem` 创建成功后授予）
    * 星语占卜师：对准天体完成一次占卜（`DivinationItem` 服务端授予）
    * 客户端授予统一走 `SkyboxRenderer.awardClient`（单机提交服务端线程 + 会话级防重复）
*   **物品使用描述与获取方式**（`item/SkyLinkItem.java` + `item/DivinationItem.java` + 语言文件）：
    * 星空枕/占卜图录新增 `appendHoverText`：使用描述（夜晚限定/右键创建左键移除/对准天体占卜）+ 获取方式（"可在染梦世界的宝箱中发现"）
    * 战利品表：染梦 13 个宝箱（`loot_table/chests/loots_relic_0~9`、`loots_deep_treasure_0/1(_super)`）新增 `memento_item_03`、`memento_item_08` 低权重条目（weight 1，脚本 `tools/add_memento_to_chests.py`）
*   **语言键**（zh_cn/en_us）：更新 `tooltip.pasterdream.memento_item_03/08.effect`；新增 `tooltip.*.source` ×2、`message.*.night_only` ×2、成就 title/descr ×6

---

## v0.9.3 — 2026-08-06

### 修复：白天天体残留 / 望远镜视角过近 / 星空枕错位；新增占卜对准要求

*   **白天天体残留**（`client/sky/content/TexturedPlanetSystemSkyContent.java`）：
    * 根因：行星 `targetAlpha` 覆写为 `weatherFactor × (0.55 + 0.45 × nightFactor)`，白天夜晚因子归 0 后仍有 0.55 → 行星/卫星白天也显示在天空
    * 修复：改为返回 `context.visibility()`（与星空/星座一致，仅夜晚可见，白天完全消失）
*   **望远镜视角过近**（`client/sky/content/ConstellationSkyContent.java`）：
    * 根因：望远镜 FOV 收窄后，星座星点放大倍率（×2.2）过大，单颗星遮挡星空全貌
    * 修复：克制放大——程序化星 ×2.2→×1.6、纹理星 ×1.4→×1.25、亮度 ×2.5→×2.0；对准阈值 0.09→0.07 rad（需更精确框住星体才触发放大）
*   **星空枕放置错位**（`item/SkyLinkItem.java`）：
    * 根因：上一轮坐标换算修正引入符号错误（`Y(-A)` 旋转方向写反），创建的星体偏离玩家实际瞄准方向
    * 修复：恢复正确逆变换 `先 X(90°) 再 Y(-A)`（`sx=lx·cosA-ly·sinA, sy=-lz, sz=lx·sinA+ly·cosA`），与渲染正变换 `X(-90°)·Y(A)` 严格互逆
*   **占卜需对准天体**（`item/DivinationItem.java` + `client/sky/SkyboxRenderer.java`）：
    * 新行为：羽星占卜图录必须**对准天空中的天体**（星座星点、行星或星空枕连线星体）再右键，未对准时提示 `message.pasterdream.divination.aim_first`（"请先对准天空中的天体..."）且不触发占卜/冷却
    * 实现：客户端将世界视线逆变换为天空局部坐标，经 `SkyboxRenderer.isCelestialTargeted` 检测当前候选天空中的星座（`ConstellationSkyContent.containsStarNear`）、行星（`TexturedPlanetSystemSkyContent.containsPlanetNear`）与玩家连线星体；未对准返回 `PASS`（不向服务端发包），对准返回 `SUCCESS` 触发服务端占卜
    * `SkyPoint` 新增 `length()` 向量模长辅助方法
*   **新增语言键 2 个**（zh_cn/en_us 成对）：`message.pasterdream.divination.aim_first`

---

## v0.9.3 — 2026-08-06

### 修复：星空枕崩溃 / 望远镜观星失效 / 星空白天渲染

*   **星空枕崩溃**（`client/sky/content/SkyLinkContent.java`）：
    * 根因：玩家只创建 **1 颗连线星体**时，连线缓冲没有任何顶点，`buffer.buildOrThrow()` 抛 `IllegalStateException: BufferBuilder was empty` 直接崩溃（crash-report 指向 `SkyLinkContent.render` L93）
    * 修复：连线仅在 `stars.size() > 1` 时绘制；星体缓冲与连线缓冲改用安全提交 `SkyGeometry.drawIfNotEmpty`（空缓冲静默跳过）
*   **空缓冲统一防御**（`client/sky/render/SkyGeometry.java` + 全部 9 个内容类）：
    * 新增 `SkyGeometry.drawIfNotEmpty(BufferBuilder)`：`build()` 判空后再提交，替代所有 `buildOrThrow()`（共 13 处），杜绝任意内容类因数据问题（某纹理帧无星、两点重合线段被跳过等）产生同类崩溃
*   **望远镜观星放大失效**（`client/sky/content/ConstellationSkyContent.java`）：
    * 根因 1：`renderGlowStars` 中 `aimed` 仅调用日志、**不改变星点绘制**（放大倍率常量未生效）
    * 根因 2：`renderTexturedStars` 中 `starSize = size * (aimed ? 1.0F : 1.0F)` —— aimed 分支恒为 1.0F（笔误），对准放大完全失效
    * 修复：对准星点真正放大（程序化星 ×2.2 / 纹理星 ×1.4）并变亮（×2.5），额外叠加亮核层；`aimed` 过渡仍由 `aimProgress` 平滑插值
    * 附带：`logAimed` 增加 30 tick 节流（多星座实例共享静态状态曾导致 DEBUG 日志每帧刷屏）
*   **星空白天渲染**（`client/sky/SkyboxRenderer.java` `getNightFactor`）：
    * 根因：`getSunAngle()` 返回 `timeOfDay × 2π`，timeOfDay 语义为**正午 = 0、午夜 = π**——用 `sin` 判断太阳高度时正午与午夜 sin 均为 0，白天（正午）夜晚因子高达 0.2 → 星空/行星/极光等白天也渲染（日志实测午夜可见度仅 0.45）
    * 修复：改用 **`cos`**（正午 1、午夜 -1）——白天夜晚因子归 0，午夜全亮 1；同时修正方法注释
*   **星空枕坐标换算修正**（`item/SkyLinkItem.java`）：
    * 根因：世界→天空球局部逆变换公式符号错误（`Y(-A)` 旋转写反），创建的星体会偏离玩家实际瞄准方向
    * 修复：改为正确逆变换 `先 X(90°) 再 Y(-A)`，与渲染正变换 `X(-90°)·Y(A)` 及望远镜 `isAimed` 检测完全一致
*   **羽星占卜图录核查**（`item/DivinationItem.java`）：确认安全——`use()` 仅在服务端执行占卜（纯 `ServerPlayer` + `PDEffects`），客户端仅同步冷却，无客户端类引用，无崩溃风险

---

## v0.9.3 — 2026-08-06

### 新增：夜空交互系统（望远镜观星 / 连线星体 / 天体占卜）+ 群系切换过渡

*   **望远镜观星改造**（`PasterDream/src/main/java/.../client/sky/content/ConstellationSkyContent.java`）：
    * 星座星点"放大变亮"效果改为**仅玩家手持望远镜（Spyglass）且正在放大观看时触发**（`isUsingItem && useItem == SPYGLASS`）
    * 放大/缩小带**过渡动画**：`aimProgress` 0~1 平滑插值，星点大小（×2.2）与亮度（×2.5）随进度渐入渐出
    * 望远镜对准/移开日志由 INFO 降为 DEBUG（避免刷屏）
*   **星空夜间特有**：确认 `star_field` 等全部天象 `targetAlpha` 默认返回 `context.visibility()`（夜晚因子×天气×遮挡），白天不渲染
*   **连线星体**（`memento_item_08` 星空枕）：新增 `item/SkyLinkItem.java` + `client/sky/PlayerSkyLinkData.java` + `client/sky/content/SkyLinkContent.java`
    * 手持星空枕**抬头对准天空右键**：在视线方向生成一颗连线星体，按创建顺序依次连线（>2 颗时首尾闭环成星座环）
    * **再次右键已创建的星体可取消**（角距离阈值判定）
    * 数量上限**默认 8，可配置**：`PasterDream-Common.toml` → `Sky.skylink max stars`（范围 1~64）
    * 坐标换算：世界视线方向按天空旋转逆变换存为天空球局部坐标，星体随夜空转动；纯客户端视觉数据（不跨存档持久化）
    * 新增 `SkyboxRenderer.renderPlayerSkyLinks` 挂载至 `AFTER_SKY`，透明度按可见度淡入淡出
*   **天体占卜**（`memento_item_03` 羽星占卜图录）：新增 `item/DivinationItem.java`
    * 使用后随机占卜事件（3 选 1）：
      * "今晚是个好梦~" → 梦境祝福 `dreamwish_buff`
      * "运气真好~" → 寻梦者的祈愿 `memento_buff`（幸运 +10）
      * "星光在你指尖流转~" → 染梦附魔 `dyedreamup_buff`
    * 冷却 5 秒（100 tick）、BUFF 持续 10 秒（200 tick）；服务端随机与施加，客户端同步冷却显示
*   **群系切换过渡**（`SkyboxRenderer.updateSelectedCandidate`）：群系变化时跳过 60 tick 防抖**立即切换候选**，由逐条目 alpha lerp 完成新旧天空交叉淡入淡出
*   **新增语言键 9 个**（zh_cn/en_us 成对）：`tooltip.pasterdream.memento_item_03.effect`、`tooltip.pasterdream.memento_item_08.effect`、`message.pasterdream.divination.good_dream/lucky/starlight`、`message.pasterdream.skylink.added/removed/full/look_up`

---

## v0.9.3 — 2026-08-06

### 修复：染梦维度遗迹浮空/遁地（低地群系导致贴地结构泡水）

*   **根因**：染梦维度地形调整（sea_level 63→55 + 新增海岸/河流/浅海群系）后，`#pasterdream:dyedream_biome` 结构专用群系标签包含了海岸（`biome_dyedream_shore`）、河流（`biome_dyedream_river`）与浅海（`biome_dyedream_3`）等**地形低于水面 55 的低地群系**，贴地遗迹（亭子/营地/酒馆等，底部为 dirt 地基、`start_height` 负偏移）按高度图偏移生成后底部沉入水下 → 视觉表现为「遁地/泡水」
*   **修复**（`PasterDream/src/main/resources/data/pasterdream/tags/worldgen/biome/dyedream_biome.json`）：
    * 从 `#pasterdream:dyedream_biome` 移除 `biome_dyedream_3`（浅海）、`biome_dyedream_shore`（海岸）、`biome_dyedream_river`（河流）三个低地群系
    * 保留高地群系：`biome_dyedream_0/1/2`、`biome_dyedream_dense_forest`（地形均 ≥ 海平面 55，贴地结构底部不再泡水）
    * 影响结构：`dream_church_0~9`、`dream_wishingtree_0/1`（不再在低地生成，悬浮高度恢复正常）
*   **贴地结构浅海群系修正**（`worldgen/structure/*.json`）：
    * `dyedream_pavilion_0.json`：biomes 由 `[biome_dyedream_0, biome_dyedream_3]` 收敛为 `biome_dyedream_0`
    * `dyedream_pavilion_2.json`：biomes 由 `biome_dyedream_3`（浅海）改为 `biome_dyedream_0`（平原）
*   **保留**：`big_bubbles_0~5`（云坠泡泡）、`dream_train`（染梦列车）、`dyedream_floating_temple`（悬浮寺庙）、`garden_decryption_2`（悬浮花园）在浅海群系生成——均为悬浮设计（底部为泡泡/云），浅海上空悬浮属正常
*   **注意（破坏性）**：已有染梦维度存档需重新生成区块方可看到修复效果；`/locate` 定位新结构同样需要新区块

---

## v0.9.3 — 2026-08-05

### 重构：染梦维度地下结构（深层全方解石 / 地下岩浆改地下河 / 浅层染梦砂岩递减）

*   **深层全方解石**：`PasterDream/src/main/resources/data/pasterdream/worldgen/noise_settings/dyedream_world.json` 删除 `deepstone_layer` 规则（原底部染梦深层石与方解石的混搭渐变），深层（Y-64~0）改为默认方块 `minecraft:calcite` 纯方解石
*   **地下岩浆 → 地下河**（三路并除）：
    * 含水层随机岩浆：`noise_router.lava` 由 `aquifer_lava` 噪声改为常数 `0.0`，深水区不再随机变岩浆
    * 洞穴雕刻岩浆：新增 `pasterdream:dyedream_cave` / `dyedream_cave_extra_underground` / `dyedream_canyon` 三个自定义 configured carver（`lava_level` 降至 `absolute -2032`），染梦 9 个群系 `carvers` 引用同步替换
    * 世界底部硬编码岩浆层：新增服务端 Mixin `NoiseBasedChunkGeneratorMixin`（已注册至 `pasterdream.mixins.json`），仅对默认方块为方解石的染梦维度拦截 `createFluidPicker`，将原版硬编码的 `y < -54 → LAVA` 替换为「低于海平面一律默认流体（水）」
*   **浅层染梦砂岩由上到下递减**：删除原 `sandstone_layer`（仅深海群系、方向相反），新增全局规则「`minecraft:not` + `vertical_gradient`」——Y≥64（above_bottom≥128）为 100% 染梦砂岩，Y0~64 线性递减，Y<0 无砂岩（深层方解石）；对地表非染梦泥土的群系（雪原/沙地/海岸/河流）同样生效
*   **保留**：各群系地表表层与填充层规则、`permafrost_layer`（biome_dyedream_2 深层永冻冰）
*   **注意（破坏性）**：已有染梦维度存档需重新生成区块方可看到新地下结构

---

## v0.9.3 — 2026-08-05

### 新增：染梦世界夜空体系（仿照模组 Stellara 风格）

*   **夜空装饰**：染梦世界夜晚新增群系主题星空（星星/行星/卫星/星座/流星/极光/光带/彩虹/tint），每个群系有专属配色与组合（温暖平原暖橙、炎热森林翠绿、寒冷冰雪冰蓝+强极光、海洋深海蓝+多行星、蘑菇平原菌紫、密林粉紫）
*   **每晚随机天空**：每个群系配置 3 套天空变体，每晚（新的一天）从候选池随机选一套 → 每个夜晚出现不同的天空效果（强极光之夜 / 银河光带之夜 / 密集流星之夜等），保留群系风格
*   **极光修正**：所有极光位于高空（min_pitch/max_pitch > 0），不再贴地
*   **望远镜对准星座星点**：玩家视线对准星座中某颗星时，该星**放大 2.2 倍、变亮 2.5 倍**并额外绘制亮核；移开视线自动恢复
*   **渲染架构（Iris 光影兼容）**：
    * `RenderLevelStageEvent.AFTER_SKY` 事件渲染天空内容（`SkyboxClientEvents`）
    * 内容类不设混合模式，统一标准混合（`SkyboxRenderer`）
    * 星星/行星/星座纹理用 `getPositionTexShader`（纯纹理，避免粒子 shader 在光影下顶点色变黑）
    * 极光/光带/流星用 `getPositionColorShader`
*   **API 集成**：`SkyboxAPI` 新增 `buildSkyPoseStack` / `isAfterSky`；`api/client/sky/README.md` 完整集成指南（含 7 条易犯错误）；`SkyboxPresets` 预设库 + `SkyboxPresetLoader`
*   **纹理**：`tools/gen_sky_textures.py` 程序化生成群系主题星星/行星/卫星；`tools/recolor_stellara_textures.py` 换色复用 Stellara 纹理；`tools/solidify_planets.py` 行星实心化（避免半透明/黑边）
*   **配置**：`data/pasterdream/skyboxes/*.json` 数据驱动（基础 6 套 + 变体 18 套）；`tools/verify_skyboxes.py` 校验

---

## v0.9.3 — 2026-08-05

### 新增：融梦水晶箱开箱冷却改为 10 分钟，并接入配置界面

*   **改动**：融梦水晶箱玩家开箱冷却由写死的 1 分钟（1200 tick）改为 10 分钟（12000 tick），且时长可配置
*   **配置项**：`PasterDreamMeltDream` 新增 `chest cooldown` 配置键（`pasterdreammeltdream-common.toml`，默认 `12000`，范围 1 ~ `Integer.MAX_VALUE`）
*   **接口**：`IMeltDreamEnergySystemConfig` 新增 `chestCooldownTicks()` 方法（`PasterDreamAPI`），主模组 `MeltdreamChestBlockEntity.setCooldown()` 改为动态读取配置；未安装融梦模组时注册表空实现回退 12000 tick，主模组独立运行不崩溃
*   **配置界面**：新配置项注册到 `PDAddonConfigRegistry`，Mod 列表「配置」界面 → 融梦系统分类显示「水晶箱开箱冷却」（tick 输入），保存即写入 TOML
*   **资源/文档**：中英文语言文件新增 `gui.pasterdream.config.meltdream_chest_cooldown`（含 tooltip）；`PasterDreamMeltDream/README.md` 配置表格同步

---

## v0.9.3 — 2026-08-05

### 重构：破风骑士祭坛 5 方块合并为单方块（STAGE 属性 + FACING 朝向）

*   **改动**：`wind_knight_spawnblock_0..4` 五个独立方块/物品/方块实体合并为单一 `wind_knight_spawnblock`
*   **机制**：拼装样式改由方块数据 `STAGE`(0..4) 决定——右键交互不再“每搭建一次替换方块”，而是写入 `STAGE` 值切换样式；阶段推进逻辑与原版一致（风行者水晶→1、凝风铁×3→2/3/4、闪电法术→86t 召唤风之骑士+四雷云→回 stage 0）
*   **朝向**：新增 `FACING` 水平朝向，`getStateForPlacement` 取玩家朝向反向，GeckoLib 渲染自动按朝向旋转；实现 `rotate`/`mirror`
*   **渲染**：`WindKnightSpawnblockModel` 按 STAGE 动态切换 `geo/block/wind_knight_spawnblock_N.geo.json`；纹理/动画统一单文件；BE 统一为 `WIND_KNIGHT_SPAWNBLOCK`
*   **资源**：blockstates 合并（stage 变体）、loot/lang 合并；删除 `geo/` 根与 `animations/` 根重复文件
*   **遗迹同步**：风之旅途 `lost_windknight_ruins` 结构 NBT 内引用的旧 ID `pasterdream:wind_knight_spawnblock_0` 已改写为新 ID（2 处，NBT 二进制重写；`tools/rewrite_structure_spawnblock_id.py`）；不改写则结构加载时祭坛变空气
*   **追踪**：`pd_porting_manifest.json`（renames `_0` + excluded `_1.._4`）、`tag_audit.json`、VERIFY hooks 同步
*   **注意（破坏性）**：旧档已放置的 `wind_knight_spawnblock_0..4` 会因 ID 合并消失，需重新放置；**已生成区块中遗迹里的旧 ID 祭坛会变空气**——新档/新探索区域正常生成，旧档可用 `/fill ... air replace pasterdream:wind_knight_spawnblock_0` 清理后重新生成

---

## v0.9.3 — 2026-08-05

### 移除：彻底删除 `/pasterdream` 指令树

*   **改动**：删除整个 `/pasterdream` 指令（`dimension reset` / `arena locate` / `arena tp` / `bgm debug` / `bgm play` / `bgm list`），`PDCommands.java` 及 `command/` 目录一并移除
*   **根因（顺带实锤）**：`bgm list` 将 `ResourceLocation` 直接传给 `Component.translatable()` 参数，而 `TranslatableContents` 只接受 `Component/Number/Boolean/String` → 触发 `IllegalArgumentException`（日志：`Was given pasterdream:music.dream_meadow for message.pasterdream.command.bgm_list_registered`），是 BGM 指令崩溃的元凶
*   **清理范围**：
    * `PasterDreamMod.java` 移除 `PDCommands::register` 注册监听与 import
    * 中英文语言文件各移除 40 个 `message.pasterdream.command.*` 键
    * `PDAaroncosArenaSpawnData` / `PDAaroncosArenaWorldgen` 注释中的指令引用一并修正
    * `docs/验证复现.md` 指令相关说明同步更新（archive 历史文档保留）
*   **保留**：`DimensionRegionHelper`（API 工具类，非指令，供维度重置相关逻辑复用）

### 修复：暮影之笼（twilight_lantern）激活时与原版 BGM 双播

*   **症状**：激活暮影之笼后，`shadow_music_0`（SoundSource.MUSIC）与原版背景音乐（如主世界 `music.game`）同时播放
*   **根因**：1.21.1 `SoundEngine.play` 播放 `MUSIC` 源声音时不会停止原版 `MusicManager` 正在播放的音乐
*   **修复**（`TwilightLanternMusicPayload.java` / `TwilightLanternMusicHandler.java` / `TwilightLanternMusicState.java` / `PDNetwork.java` / `PDClientVfx.java` / `TwilightLanternBlock.java`）：
    * 新增 S2C 包 `TwilightLanternMusicPayload`（布尔 `active`），事件激活（+55t）广播 `true`、事件结束（+2600t）广播 `false`
    * 客户端 `TwilightLanternMusicHandler` 监听 NeoForge `SelectMusicEvent`，激活期间 `setMusic(null)` 使原版 `MusicManager` 停止并保持静音，事件结束自动恢复
    * **边界处理**（新增 `TwilightLanternMusicState` 服务端状态追踪）：
        * 多笼并发：按「维度 → 激活事件计数」维护，任一笼子激活即静音，全部结束才恢复
        * 玩家中途换维度/登录重连：按目标维度计数补发状态（离开事件维度立即恢复原版 BGM）
        * 玩家断线/退出：客户端监听 `ClientPlayerNetworkEvent.LoggingOut` 重置标志，防残留

---

## v0.9.3 — 2026-08-05

### 新增：染梦遗迹奖励箱加入原版基础资源

*   `loot_table/chests/loots_relic_0.json`（染梦世界遗迹通用）与 `loot_table/loots_relic_1.json`（染梦世界遗迹少量）的金属锭池（pool[5]）新增 8 种原版基础资源：
    *   铁锭（weight 6，2–5）、煤炭（weight 5，3–8）、铜锭（weight 4，2–6）、金锭（weight 3，1–3）、红石（weight 3，2–6）、青金石（weight 2，1–4）、钻石（weight 1，1–2）、绿宝石（weight 1，1–2）
*   条目插入在 `tabitem_1` 空占位之前，保留原文件格式与 `random_sequence` 字段

---

## v0.9.3 — 2026-08-05

### 修复：染梦列车遗迹由召唤方块改为完整多方块列车结构

*   **症状**：染梦列车遗迹生成的是 1×1×1 的 `dream_train_structure` 召唤方块（右键仅提示「列车即将到站」），而非多方块列车
*   **根因**：`template_pool/dream_train.json` 的 `location` 指向 `dream_train_platform` NBT（单方块占位）；完整列车 `dream_train.nbt`（25×43×228，24470 方块）从未被遗迹引用
*   **修复**（`worldgen/template_pool/dream_train.json` / `worldgen/structure/dream_train.json`）：
    * `location` 改为 `pasterdream:dream_train`，遗迹直接生成完整染梦列车结构
    * `max_distance_from_center` 64 → 120（列车 Z 长 228 半长 114；受 1.21.1 codec 约束 `maxDistance+terrainOffset ≤ 128`）
    * 高度保持原配置（55 + 地表高度，空中漂浮列车，与染梦结构约定一致）

---

## v0.9.3 — 2026-08-05

### 修复：暮影之笼（据点守卫）结构生成不刷怪

*   **症状**：jigsaw 自然生成的据点里，暮影之笼点燃后 130 秒内不刷任何怪；玩家手动放置的笼子正常
*   **根因**：结构生成走 `WorldGenRegion.setBlock` → `ProtoChunk`（FEATURES 阶段），该路径**不创建 BlockEntity、不调用 `onPlace`**，仅写入 `id=DUMMY` 占位 NBT；区块转 `LevelChunk` 时 `BlockEntity.loadStatic("DUMMY")` 找不到类型 → BE 永久缺失。后果：点燃时 `putBooleanAt("switch", true)` 静默失败（`FreeDataBlockEntity.putBooleanAt` 对 null BE 直接返回），且 tick 从未被调度 → 不刷怪
*   **修复**（`TwilightLanternBlock.java` / `PasterBlockResetToolItem.java` / `PDTwilightLanternVerifyHooks.java`）：
    * `TwilightLanternBlock.ensureBlockEntity()`：BE 缺失时按 `newBlockEntity` 补建；点燃分支、`onPlace`、重置工具均调用
    * 点燃分支额外 `scheduleTick(20)` 启动计数循环（结构笼 `onPlace` 从未调度）
    * VERIFY `twilight-lantern` 新增「BE 缺失自愈」回归项

