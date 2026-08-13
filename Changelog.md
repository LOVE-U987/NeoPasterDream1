# PasterDream Changelog

---
## v0.9.5 — 2026-08-13

### 修复：合并冲突残留导致的编译失败

*   **根因**：合并 `momonyako` 分支（v0.9.6）时，4 处冲突标记未真正解决即被提交，导致 `:PasterDream:compileJava` 语法错误（`<<<<<<< HEAD` / `=======` / `>>>>>>>` 残留）
*   **修复**（`PasterDream`）：
    *   `PasterDreamMod.java`：裂隙出生点监听器注册统一为 v0.9.6 方案 `PDOverworldOriginCrackWorldgen::onLevelLoad`
    *   `registry/PDRuinsRegistration.java`：`registerDyedreamCrack` 注释冲突合并（保留 HEAD 详细说明：StructureType 无条件注册 + 生成阶段按配置判断）
    *   `worldgen/structure/DyedreamCrackStructure.java`：类注释/方法注释取详细版，配置判断用防御性写法 `Boolean.TRUE.equals(...)`（与 `DyedreamCrackPlacement` 一致）
    *   `world/PDOriginCrackWorldgen.java`：删除——与 v0.9.6 引入的 `PDOverworldOriginCrackWorldgen` 功能完全重复（合并遗留的旧实现）
*   **修复**（`Changelog.md`）：v0.9.6 条目上移至顶部，清除残留冲突标记块
*   **验证**：`:PasterDream` / `:PasterDreamAPI` / `:PasterDreamMeltDream` / `:PasterDreamSanity` / `:PasterDreamSpells` 五模块 `compileJava` 全 BUILD SUCCESSFUL

---


#### 负责人：MomoNyako

### 修复：染梦裂隙生成配置不生效与 `/locate` 卡死

*   **修复**：关闭 `DYEDREAM_CRACK_GENERATE` 后裂隙仍会生成；且配置关闭时 `/locate` 裂隙结构会因扫描真实磁盘 region 文件而严重卡顿
*   **新增**（`worldgen/structure/placement/DyedreamCrackPlacement.java`，新）：自定义结构放置策略 `dyedream_crack_spread`，继承 `RandomSpreadStructurePlacement`，配置关闭时通过两个拦截点禁用生成与定位：
    *   `getPotentialStructureChunk`：配置关闭时返回世界边界外坐标（`Integer.MAX_VALUE`），`/locate` 候选点全部落到界外，`scanChunk` 对界外 chunk 快速失败，从源头消除磁盘 I/O 卡顿
    *   `applyAdditionalChunkRestrictions`：配置关闭时返回 `false`，世界生成不产生候选；作为 `locate` 候选判定的兜底拦截
*   **新增**（`registry/PDStructurePlacements.java`，新）：注册 `STRUCTURE_PLACEMENT` 类型的 `DeferredRegister` 与 `dyedream_crack_spread` 类型，供 `structure_set/struct_dyedream_crack_1_set.json` 的 `placement.type` 引用
*   **新增**（`worldgen/structure/DyedreamCrackStructure.java`，新）：裂隙结构类，生成与否受 `DYEDREAM_CRACK_GENERATE` 控制；`PDRuinsRegistration.java:185` 的 `registerDyedreamCrack` 改用它替换 `JigsawStructure`
*   **新增**（`world/PDOverworldOriginCrackWorldgen.java`，新）：出生点裂隙放置（SavedData 去重），对齐原版 `GenerateWorldPr0Procedure` 裂隙分支
    *   主世界：`THE_ORIGIN_OF_THE_WORLD_INITIALLY_GENERATED_DYEDREAM_CRACK` 开启时在 `(0,0)` 附近放置 `dyedreamcrack0` 模板（可见天空放 `(-9,110,-10)`，否则 `(-9,160,-10)`）
    *   染梦维度：`DYEDREAM_ORIGIN_SPAWNPOINT` 开启时在出生点放置（回主世界入口），`LevelEvent.Load` 时触发，每维度仅放置一次
*   **结构 JSON 修正**：`struct_dyedream_crack_0/1`、`structure_set`、`template_pool` 的裂隙相关文件对齐新放置策略
*   **验证**：`:PasterDream:compileJava` BUILD SUCCESSFUL

### 修复：配置界面点击「恢复默认」导致游戏崩溃

*   **修复**：`PDConfigScreen.resetAll()` 中 `Minecraft.getInstance().player.displayClientMessage(...)` 缺少判空
    *   配置界面通过 Mod 列表的「配置」按钮打开，此时玩家尚未进入世界，`player` 为 `null`
    *   崩溃栈：`NullPointerException: Cannot invoke "LocalPlayer.displayClientMessage(...)" because "Minecraft.getInstance().player" is null`（`PDConfigScreen.java:1358`）
    *   补充 `player != null` 判空，与 `saveConfig()`（`PDConfigScreen.java:1318`）保持一致

### 修复：染梦裂隙生成配置在「系统」与「基础机制」重复显示

*   **修复**：`DYEDREAM_CRACK_GENERATE` 被重复注册到 `ConfigCategory.SYSTEM` 与 `ConfigCategory.BASIC` 两个分类
    *   两个 `BooleanEntry` 共享同一 `ConfigValue` 却持有独立 `pendingValue`，只改一处时界面不同步、保存时互相覆盖
    *   删除 `PDConfigScreen.java:188` 的 SYSTEM 入口，保留「基础机制」分类下的原有入口；「系统」分类无配置项后自动隐藏侧边栏按钮

---


### 修复：语言文件缺失翻译键 + 英文文件中的中文字符

*   **补全缺失键**（`PasterDream` 主模块 `lang/zh_cn.json`、`lang/en_us.json`）：新增 `item.pasterdream.dyedream_deepstone`、`item.pasterdream.dyedream_sandstone` 翻译键（染梦深层石 / Dyedream Deepstone、染梦砂岩 / Dyedream Sandstone），消除 `tools/check_lang.py` 报告的 2 处缺失
*   **清理中文字符**（`PasterDream` `lang/en_us.json`）：`painting.pasterdream.pasterdream_draw_0.author` 全角括号 `【pl】Mo` → `[pl] Mo`；`tooltip.pasterdream.calle_card.*` 全角引号 `『』` → 半角单引号（含 `card_drawn`、`card_title` 及 `name.1~9` 共 11 处）
*   **清理中文字符**（`PasterDreamSpells` `lang/en_us.json`）：`itemGroup.pasterdreamspells` 中文竖线 `PasterDream丨Spells` → `PasterDream | Spells`
*   **验证**：`tools/check_lang.py` 全部注册项（431 物品 / 290 方块）在中英文语言文件中均已找到对应翻译键；全项目 `en_us.json` CJK 字符扫描 0 残留

---

### 修复：染梦砂岩 / 染梦深岩破坏无掉落物

*   **根因**（`PasterDream`）：`PDBlocksSimple.java` 用 `addCustom` 注册了 `dyedream_sandstone` 与 `dyedream_deepstone`，但 `PDItemsBlocks.java` 未注册对应 BlockItem、`data/pasterdream/loot_table/blocks/` 下缺失战利品表 JSON → `Block.asItem()` 返回 `Items.AIR` 且无战利品表 → 破坏零掉落（验证报告 `pd_verify_report.json` blocks 类目 `extra` 字段早已列出这两项）
*   **修复**：
    *   `registry/items/PDItemsBlocks.java`：新增 `DYEDREAM_DEEPSTONE`、`DYEDREAM_SANDSTONE` 的 `registerSimpleBlockItem` 注册
    *   `registry/PDItems.java`：聚合区补两个 `PDItemsBlocks` 引用
    *   `data/pasterdream/loot_table/blocks/dyedream_sandstone.json`、`dyedream_deepstone.json`：新增自掉落战利品表（对齐兄弟方块 `dyedream_block.json` 格式，无条件 + `survives_explosion`）
*   **验证**：`:PasterDream:compileJava` BUILD SUCCESSFUL；两个新 JSON 解析校验通过；无 lint 错误

---

### 修复：融梦能量自然恢复 + 水晶箱开箱奖励（2 项功能补齐）

*   **自然恢复**（`PasterDreamMeltDream`）：新增 `PDMeltDreamEvents.java`（`PlayerTickEvent.Post`），实现 `PasterDreamMeltDream-Common.toml` 中 `recover interval`（默认 1200 tick = 60 秒）与 `recover amount`（默认 0.1）两项配置——玩家在线期间按间隔自动恢复融梦能量；系统总开关关闭时跳过；按玩家独立 tickCount 差值计时，登录事件清理残留记录
*   **水晶箱奖励**（`PasterDream` 主模块 `MeltdreamChestBlock.java`）：开箱成功时按原版 `MeltdreamChestPr0Procedure` 行为奖励 +2 融梦能量，并乘 `chest generation multiplier` 倍率（默认 1.0）；系统总开关关闭时不给能量
*   **验证**：`:PasterDream:compileJava` + `:PasterDreamMeltDream:compileJava` BUILD SUCCESSFUL

---

### 完善：染梦灯笼（dyedream_lantern）正式纹理 + 灯笼标签

*   **纹理**（`PasterDream`）：用原模组正式美术替换占位纹理
    *   复制原模组 `ran_meng_deng_long_.png`（16×48，3 帧 frametime 4 灯光闪烁动画）至 `textures/block/dyedream_lantern.png`，并重命名去除拼音命名（原拼音 `ran_meng_deng_long_` → `dyedream_lantern`，同步复制 `.mcmeta`）
    *   删除占位纹理生成脚本 `tools/gen_dyedream_lantern_tex.py`
*   **模型**：按用户要求**复制原模组模型文件写法**（对齐 `dyedream_lartern`）
    *   `models/block/dyedream_lantern.json` / `dyedream_lantern_hanging.json`：`parent: block/cube` 全方块模型 + `render_type: translucent`，六个面纹理统一引用重命名后的 `pasterdream:block/dyedream_lantern`
    *   **修复**：上一版自定义 box 模型存在纹理变量 `#lantern` 未定义导致纹理不显示的 bug，复制原模组写法后直接引用具体纹理路径，纹理正确应用
*   **模型与碰撞箱同步**（后续修正）：全方块模型与 LanternBlock 小灯笼碰撞箱（约 6×7×6）不匹配，改为 parent 原版 `template_lantern` / `template_hanging_lantern`
    *   放置态 `dyedream_lantern.json`：主体 `[5,0,5]→[11,7,11]` + 顶部环 `[6,7,6]→[10,9,10]`，与 Java `AABB` 完全一致
    *   悬挂态 `dyedream_lantern_hanging.json`：主体 `[5,1,5]→[11,8,11]` + 顶部环 `[6,8,6]→[10,10,10]`，与 Java `HANGING_AABB` 完全一致
    *   `textures.lantern` 引用 `pasterdream:block/dyedream_lantern`，渲染为灯笼形状而非全方块
*   **标签**（灯笼特征）：
    *   `registry/PDBlockTags.java`：新增 `LANTERNS = c:lanterns` 社区约定标签常量（与 Fabric Convention Tags 兼容）
    *   `data/PDBlockTagProvider.java`：`addExtraTags` 写入染梦灯笼、染梦水晶灯及原版 `lantern`/`soul_lantern`
    *   `src/generated/resources/data/c/tags/block/lanterns.json`：手动生成等价标签文件（DataGen 被并行开发的配置加载问题阻塞期间，保证资源就位；待其修复后 runData 会重新生成同样内容）
*   **验证**：`:PasterDream:compileJava` BUILD SUCCESSFUL；全部 JSON/纹理/mcmeta 校验通过
*   **注意**：本轮 `runData` 受并行开发中的 `PasterDreamMod` 配置读取 bug（`Cannot get config value before config is loaded`）阻塞，标签已手动落盘，DataGen 代码同步就绪

---

### 修复：全库无效配置审计——补齐 4 项移植遗漏配置的功能实现

*   **审计**（`tools/scan_config_usage.py` 新增）：全库引用扫描 6 个配置类（PDCommonConfig/PDClientConfig/PDMeltDreamConfig/PDSanityConfig/PDSpellsConfig）共 100+ 配置项，识别出「仅配置界面引用、无业务读取」的无效配置
*   **补实现**（`PasterDream` 主模块）：
    *   `ban fire necklace`（禁用业火项链）→ `Fire0NecklaceItem.curioTick` 补充 BAN 检查：禁用时提示"此物品已被禁用"；不禁用时脚下空气处点火 + 燃烧时急迫 I（对齐原版 `Fire0NecklacePr0Procedure`）
    *   `loading gui tips`（加载界面 tips）→ 新增 `PDLoadingTipsClientEvents`（ScreenEvent.Render.Post）：连接/加载/进度界面底部绘制随机 tips，22 条文案沿用原版（对齐原版 `ClientEvent`）
    *   `the origin of the world initially generated dyedream crack`（主世界 0,0 原点裂隙）+ `dyedream origin spawnpoint`（染梦出生点岛屿）→ 新增 `PDOriginCrackWorldgen`（LevelEvent.Load + SavedData 防重复，对齐原版 `GenerateWorldPr0Procedure`，高度逻辑 heightmap≤100 → (-9,110,-10) 否则 (-9,160,-10)）
*   **报告**（`docs/invalid-config-audit.md`）：附属模块 15 项「配置预留但功能未实现」保留待实现——San 恢复/下界/末地/雨天/雷暴降值（6 项）、MeltDream 恢复/水晶箱倍率/上限（6 项）、Spells 法术倍率（3 项）
*   **验证**：`:PasterDream:compileJava` + 三个附属模块 BUILD SUCCESSFUL

---

### 新增：自定义出生维度/群系（默认关闭）

*   **新增**（`world/PDCustomSpawnEvents.java`）：玩家登录（新玩家首次进入世界）时，若配置开启且该玩家尚未执行过自定义出生，自动传送到配置指定的维度与群系位置并设置重生点
    *   以目标维度出生点为中心，用 `ServerLevel.findClosestBiome3d` 搜索指定群系（搜索不到时回退到维度出生点）；用 `Heightmap.MOTION_BLOCKING` 计算安全地表高度
    *   执行完成后在玩家 `PlayerPersisted` 子标签写入标记，同一存档内只生效一次（跨死亡/重登保留，与《帕斯特指南》发放标记同模式）
    *   维度/群系 ID 非法或未注册时跳过并保留原版出生，不影响现有流程
*   **配置**（`PDCommonConfig.java`）：`PasterDream-Common.toml` 新增 `[Custom Spawn]` 段
    *   `custom spawn enabled`（默认 `false`）—— 总开关
    *   `custom spawn dimension`（默认 `minecraft:overworld`）—— 出生维度 ID，如 `pasterdream:dyedream_world`
    *   `custom spawn biome`（默认 `minecraft:plains`）—— 出生群系 ID，如 `pasterdream:dyedream`
    *   `custom spawn search radius`（默认 `10000`，范围 `100~100000`）—— 群系搜索半径（格）
*   **事件注册**（`PasterDreamMod.java`）：`NeoForge.EVENT_BUS` 注册 `PDCustomSpawnEvents::onPlayerLoggedIn`
*   **配置界面**（`PDConfigScreen.java` / `ConfigCategory.java`）：新增「自定义出生 / Custom Spawn」分类，提供总开关与搜索半径条目（维度/群系 ID 请直接编辑 TOML）
*   **语言**：`zh_cn` / `en_us` 新增分类标题与配置项翻译
*   **验证**：`:PasterDream:compileJava` BUILD SUCCESSFUL

---


### 修复：禁用「染梦裂隙自然生成」配置不生效（主世界天空仍生成裂隙浮岛）

*   **根因**（`PasterDream`）：
    1. `PDCommonConfig.DYEDREAM_CRACK_GENERATE` 此前仅被配置界面引用，生成注册从未读取该值
    2. `worldgen/structure/struct_dyedream_crack_1.json` 使用原版 `minecraft:jigsaw` 类型，结构生成完全由静态 JSON 驱动，与代码注册无关 → 只要 JSON 存在，主世界 Y=32 裂隙浮岛必生成
    3. `PasterDreamMod` 构造器中配置注册（`registerConfig`）位于结构注册（`PDRuinsRegistration.register()`）之后，此时读取配置会抛 `IllegalStateException` 或读到默认值
*   **修复**：
    *   `PasterDreamMod.java`：配置文件注册提前至构造器最前部，确保后续代码可安全读取配置值
    *   `PDRuinsRegistration.java`：`registerDyedreamCrack()` 增加配置判断（含方法内防御），关闭时跳过 `struct_dyedream_crack_1` 的 StructureType 注册并输出调试日志
    *   `worldgen/structure/struct_dyedream_crack_1.json`：`type` 由 `minecraft:jigsaw` 改为 `pasterdream:struct_dyedream_crack_1`（RuinBuilder 注册的自定义类型）——配置关闭时该类型未注册 → 结构 JSON 解析失败 → structure_set 引用失效 → 不生成；配置开启（默认）时行为不变
    *   `PDConfigScreen.java`：删除 BASIC 分类中重复的「染梦裂隙自然生成」条目（保留 System 分类），条目计数同步修正
    *   `PDStructureVerifyHooks.java`：`verifyRuinApi` 断言随配置联动（关闭裂隙生成时期望数 42→41）
*   **配置**：`PasterDream-Common.toml` → `[System]` → `dyedream crack generate`（默认 `true`；关闭后需新建世界生效）
*   **验证**：`:PasterDream:compileJava` + `:PasterDream:processResources` BUILD SUCCESSFUL，构建输出 JSON 已同步新 type

---


### 新增：染梦灯笼（dyedream_lantern）悬挂式灯笼方块

*   **新增**（`block/DyedreamLanternBlock.java`）：悬挂式灯笼，参考原版 `LanternBlock` 实现，支持悬挂（hanging）/放置双状态与含水（waterlogged）
    *   玻璃音效、硬度 0.3、15 级光照、自发光、无遮挡、非红石导体（与染梦水晶灯同风格）
*   **注册**（`PasterDream`）：方块 `PDBlocksSimple.DYEDREAM_LANTERN`、物品 `PDItemsBlocks.DYEDREAM_LANTERN`、门面 `PDBlocks`/`PDItems`、配置 `PDBlocks.putConfig("dyedream_lantern", mineable("pickaxe"))`（自动生成 `mineable/pickaxe` 标签）、创造标签 `PDCreativeTabsDyedream`
*   **资源**：blockstates（hanging 双变体）、模型（parent 原版 `template_lantern`/`template_hanging_lantern`）、占位纹理 `textures/block/dyedream_lantern.png`（`tools/gen_dyedream_lantern_tex.py` 生成，待正式美术替换）、战利品表、合成配方（与染梦水晶灯同配方，产物 2 个）
*   **语言**：`zh_cn` 染梦灯笼 / `en_us` Dyedream Lantern
*   **注册 ID 校验**：全工作区确认 `dyedream_lantern` 无重复注册；与原模组拼写错误的 `dyedream_lartern`（染梦水晶灯，已移植）不冲突
*   **验证**：`:PasterDream:compileJava` BUILD SUCCESSFUL



### 新增：扫盘工具 — 物品堆叠 / BUFF 数值与原模组对齐检查

*   **新增**（`tools/scan_stack_buff_parity.py`）：自动对比原模组（`libs/FixPasterDream-main`）与新模组的物品堆叠数量与 BUFF 属性修饰符
    *   **物品堆叠**：解析 `stacksTo(N)` / `durability()`（自动堆叠 1）/ 默认 64，支持 `XxxItem::new`、`ItemAPI` builder、内部类注册（`XxxItem.Helmet()`）；基于 1.21.1 机制 `durability()` 强制堆叠 1、默认 64
    *   **BUFF 数值**：解析 `addAttributeModifier` 元组（属性/数值/操作），操作名 1.20→1.21 映射（`ADDITION→ADD_VALUE` 等），`0.6 * fix` 表达式求值；覆盖主模 `PDEffects` + Sanity/MeltDream/Spells 附属模块
    *   排除项：`cheerup_buff`（振奋）/ `strawberry_heart`（草莓甜心）/ `cradle_in_ones_arms`（怀中御守）
*   **扫盘结果**：
    *   BUFF 数值（45 项）全部对齐 ✅（fury/ice_spell 原版走 procedure 永久修饰符、insand 仅 1.21 属性改名 `ENTITY_REACH→ENTITY_INTERACTION_RANGE`、oppression 原版方法引用注册，均已核验一致）
    *   报告输出：`scratchpad/scan_stack_buff_report.json`

### 修复：物品堆叠数量对齐原版（扫盘发现的 10 项差异）

*   **原 16 → 16**：`dyedream_perfume`（`PDItemsFunctional.java:119`）、`dyedream_upgrade`（`PDItemsMaterials.java:109`）、`meltdream_crystal_0`（`MeltdreamCrystal0Item.java:25`）
*   **原 1 → 1**：
    *   `meltdream_elixir_bottle` / `rage_elixir_0`（`PDItemsFoods.java`：GlassDrinkItem 加 `stacksTo(1)`）
    *   `light_moss_phantom_membrane` / `moss_phantom_membrane` / `shadow_breath` / `squeal_wave`（对应 Item 类 `stacksTo(64)`→`stacksTo(1)`）
    *   `pale_boneneedle`（`PDItemsCurios.java:138`：注册加 `stacksTo(1)`，原版 durability(1) 等效堆叠 1，改用 stacksTo 避免引入耐久语义）
*   **验证**：重跑 `tools/scan_stack_buff_parity.py` 堆叠差异清零 ✅；`:PasterDream:compileJava` BUILD SUCCESSFUL

---


### 调整：移植树密度改为噪声驱动，实现群系错落感

*   **需求**：树木过密、均匀，缺乏群系内的疏密变化。
*   **调整**（`worldgen/placed_feature/bb_trees_*.json`，10 个全部改为 `minecraft:noise_based_count`）：
    *   **语义**：`count = ceil((BIOME_INFO_NOISE + offset) × ratio)`，噪声 -1~1 → offset 决定"出现区域占比"（0.4+ = 大片区域有树但稀疏，0.1~0.2 = 少数区块扎堆），ratio 决定每区块数量
    *   **主树**（bush/plaintree/snowtree/cherrybush）：offset 0.4~0.45、ratio 2~3 → 大片区域稀疏铺底
    *   **点缀树**（blossom/tallbirch/poplar/aspen）：offset 0.1~0.2、ratio 1 → 少数区块扎堆出现
    *   **地标树**（conifer）：offset 0.08、ratio 1 → 零星巨型针叶，地标感
*   **叠加控制**：`bb_forest_trees` 从 6 树型减至 4（bush+plaintree+aspen+blossom），避免多 placed_feature 叠加过密
*   **模拟验证**：森林 ~5 棵/区块（14% 无树区块）、密林 1.9、平原 4.3、蘑菇 3.0、冰雪 1.3、海岸 1.4 —— 各群系有密有疏，错落自然
*   **验证**：`:PasterDream:compileJava` + `runData` 通过（全部 JSON 可解析）

---



### 新增：Better Biomes 移植树写入地形生成器（结构树 Feature + 群系分配）

*   **需求**：将 12 棵移植树接入染梦世界自然生成；灌木等贴地树与高树形成上下结构差异；部分群系改造为移植树主导。
*   **结构树 Feature**（`PasterDream`，新）：
    *   `DyedreamStructureTreeFeature`（`pasterdream:structure_tree`）：生成阶段从 `StructureTemplateManager` 加载 `bb_*.nbt` 结构并放置（`placeInWorld`，`WorldGenLevel extends ServerLevelAccessor` 直接可用）；origin 为水平中心，结构 -X/-Z 偏移半尺寸；超大树（22×22 conifer）origin 对齐区块中心防 far chunk
    *   `Config(structurePath)` record + `MapCodec`（`.codec()` 转 `Codec`），已注册 `PDFeatures.STRUCTURE_TREE`
*   **configured_feature**（10 个）：`bb_tree_{bush,cherrybush,plaintree,blossom,aspen,tallbirch,poplar,snowtree,conifer,palm}.json`
*   **placed_feature**（10 个，上下结构差异 = 密度分层）：
    *   低层贴地灌木：`bb_trees_bush`（count 24 密植）、`bb_trees_cherrybush`（count 16）
    *   中层树：`bb_trees_plaintree`（8）、`bb_trees_aspen`（10）、`bb_trees_snowtree`（8）、`bb_trees_palm`（6，water_depth 1）
    *   高层树：`bb_trees_tallbirch`（6）、`bb_trees_poplar`（6）、`bb_trees_blossom`（4）、`bb_trees_conifer`（2 稀疏巨树）
*   **biome_modifier**（6 个，`neoforge:add_features` + `vegetal_decoration`，全部新增不动现有）：
    *   `bb_forest_trees` → `biome_dyedream_1`（森林主导：bush+plaintree+aspen+blossom+tallbirch+poplar）
    *   `bb_dense_forest_trees` → `biome_dyedream_dense_forest`（密林主导：bush+cherrybush+blossom+conifer）
    *   `bb_plains_trees` → `biome_dyedream_0`（平原：bush+cherrybush+plaintree）
    *   `bb_mushroom_trees` → `biome_dyedream_mushroom_plains`（低矮灌木：bush+cherrybush）
    *   `bb_snow_trees` → `biome_dyedream_2`（雪树）
    *   `bb_shore_trees` → `biome_dyedream_shore`（棕榈水边）
*   **验证**：`:PasterDream:compileJava` + `runData` 通过（`structure_tree` codec 与全部 JSON 可解析）；资源闭包校验通过（configured↔placed↔biome_modifier 引用完整）；10+10+6 新文件入构建产物

---



### 修复：巨型针叶染梦树顶部被截断（补树冠段）

*   **问题**：`debug_wand_dyedream_tree_conifer` 放置的巨型针叶树顶部被平切，高部分缺失。
*   **根因**：数据包 `bigtree.mcfunction` 显示巨型针叶树是**两段拼接**设计 —— `conifers/conifer_a`（树干段，posY:-1）+ `conifers/conifer_b`（树冠段，posY:45），但下载包内**只有 `big1-0.nbt`（树干/中下段，22 层）**，树冠段 `conifer_b` 缺失。提取的 `bb_conifer_big` 顶部 y=20~21 仍是 17 个 log + 16 叶平铺（被平切截面）。
*   **修复**（`tools/bb_conifer_crown.py`，新脚本）：在结构顶部追加**锥形针叶树冠**：
    *   半径从 8 平滑收尖（平方曲线），log 内圈从 3×3 → 1×1 → 顶部无 log
    *   高度 22 → **40 层**，顶层正好 1 方块尖顶（y=39 leaf:1）
    *   新增 1814 方块，总 blocks 918 → 2732
*   **验证**：补冠后 NBT 完整（size=[22,40,22]，palette 仅染梦 log/leaves）；`processResources` 复制到构建产物 BUILD SUCCESSFUL

---



### 调整：染梦河流热带鱼生成大幅削减

*   **需求**：染梦河流群系每次刷新大量热带鱼，密度过高影响观感。
*   **修复**（`PasterDream/src/main/resources/data/pasterdream/worldgen/biome/biome_dyedream_river.json`）：
    *   热带鱼从 `water_creature`（错误分类，原版热带鱼属 `water_ambient`）移到 `water_ambient`，与其他染梦群系（深海/海岸）分类一致
    *   密度削减：`weight` 10 → **4**，单次生成数量 `minCount 2 / maxCount 6` → **1 / 2**
*   **说明**：其余群系 `water_creature` 检查无同类问题（glow_squid/jellyfish/dolphin 分类均正确）
*   **验证**：JSON 解析校验通过；`:PasterDream:processResources` BUILD SUCCESSFUL

---



### 新增：Better Biomes 移植树批量扩充至 12 棵

*   **需求**：在已移植 6 棵树基础上，批量移植数据包剩余可用树结构。
*   **新增 6 棵**（`tools/bb_tree_import.py` 提取，方块替换为染梦 log/leaves，`centered=true` 居中放置）：
    *   `bb_bush` 灌木（5×6×5，oak 叶+干）
    *   `bb_cherrybush` 樱桃灌木（5×3×5，birch 叶+dark_oak 干）
    *   `bb_plaintree` 平原树（13×14×12，oak 树冠大冠）
    *   `bb_smallpalm1` 棕榈树（6×6×6，jungle 棕榈形态）
    *   `bb_snowtree` 雪树（7×17×6，spruce + **保留雪方块** 49 格）
    *   `bb_conifer_big` 巨型针叶树（22×22×22，spruce，568 原木 + 350 叶，数据包最大树）
*   **调试水晶**（`DebugStructureWandItem` + `centered=true`）：
    *   `debug_wand_dyedream_tree_bush` / `cherrybush` / `plain` / `palm` / `snow` / `conifer`
    *   已加入调试创造栏 `PDCreativeTabsDebug.DEBUG_TAB`；中英语言条目补齐（429 注册项双语覆盖）
    *   模型文件 `models/item/debug_wand_dyedream_tree_{bush,cherrybush,plain,palm,snow,conifer}.json`（life_crystal 纹理）
*   **说明**：数据包实际存在的树结构 NBT 已全部移植（12 棵）；`prismarinespike` 为水下装饰非树未移植；函数引用的 `trees:redwood/*`、`trees:forest/*`、`trees:jungle/*`、`trees:mushroom/*`、`trees:willow/*`、`trees:conifers/tiny1|small1` 等 NBT 在数据包内缺失（下载版本不完整）
*   **验证**：`:PasterDream:compileJava` + `processResources` 通过；12 个 `bb_*.nbt` 全部入构建产物；`check_lang.py` 全绿（429 注册项双语覆盖）

---



### 修复：Better Biomes 移植树以点击点为中心放置

*   **需求**：Better Biomes 树结构 NBT 原本从边角展开，调试水晶右键放置时树冠/树干偏向 +X+Z 一侧（"按正方体边角放置"）。
*   **分析**：结构 NBT 内容包围盒中心 = size 中心（平移量全 0），NBT 本身无偏移；问题在 `DebugStructureWandItem` 放置逻辑 —— 结构以点击点为**最小角**向 +X+Y+Z 展开。
*   **修复**（`DebugStructureWandItem.java`）：
    *   新增 `centered` 构造参数（默认 `false` 保持边角放置，兼容现有 48 个结构法杖）
    *   `centered=true` 时放置起点改为 `targetPos.offset(-(sizeX-1)/2, 0, -(sizeZ-1)/2)`，结构中心对齐点击点，Y 保持点击面外侧（地表）
    *   6 个 Better Biomes 移植树调试水晶（`debug_wand_dyedream_tree_*`）改用 `new DebugStructureWandItem(props, path, true)`
*   **验证**：`:PasterDream:compileJava` 通过；奇数尺寸树（tallbirch/blossom/poplar）完全对称包裹点击点（-2..2 / -8..8），偶数尺寸树（aspen 系）偏差 ≤0.5 格

---



### 修复：染梦耕地不再变成原版泥土 + 湿润/干涸行为对齐原版

*   **需求**：染梦耕地踩坏有概率变成原版泥土；湿润速度表现不对。
*   **根因**：原版 `FarmBlock` 内部有 **4 条「退化为泥土」路径全部硬编码 `Blocks.DIRT`**（原版泥土），此前只覆写了其中 2 条：
    1. `fallOn`（踩踏）：覆写中调用 `super.fallOn` 导致**二次踩踏判定** —— `FarmBlock.fallOn` 内部再次 `CommonHooks.onFarmlandTrample(..., Blocks.DIRT, ...)` 并 `turnToDirt`（原版泥土）覆盖染梦泥土 → **"有概率变原版泥土"根因**
    2. `randomTick`（干涸+上方无作物 → 原版泥土）—— 委托 super 漏掉
    3. `getStateForPlacement`（放置时上方不合法 → 直接给原版泥土）—— 未覆写
    4. `tick`（支撑不足 → 原版泥土）—— 已修
*   **修复**（`DyedreamFarmlandBlock` 完整重写）：
    *   `fallOn`：**不再调用 `super.fallOn`**（避免二次判定 + 原版泥土覆盖），改为手动 `entity.causeFallDamage` 保留掉落伤害（等价原版 `Block#fallOn` 职责）
    *   `randomTick`：复制原版湿润/干涸逻辑（`isNearWater` / `shouldMaintainFarmland` 原版为 private，已在子类复制），水边/下雨瞬间湿润到 7、干燥逐级 -1、干涸且无作物 → **染梦泥土**；保留作物 2 倍速加速
    *   `getStateForPlacement`：上方不合法时兜底给 **染梦泥土**（替换原版泥土）
    *   `tick`：支撑不足 → 染梦泥土（保持）
*   **湿润速度说明**：湿润/干涸节奏与原版耕地**完全一致**（4 格内水源瞬间湿润到满，随机刻驱动）；此前表现异常是「变原版泥土」bug 干扰所致，修复后湿润状态可正确保持。
*   **验证**：`:PasterDream:compileJava` 通过；已从 neoforge sources jar 核对 `FarmBlock` 全部 4 条 `Blocks.DIRT` 调用点均已覆写。

---



### 新增：染梦耕地完整农业交互 + 冷域木头剥皮

*   **需求**：① 染梦耕地踩坏返回染梦泥土；② 染梦草方块/染梦泥土可用锄头改为染梦耕地；③ 染梦耕地作物生长 2 倍速；④ 冷域木头用斧头变为去皮冷域木头。
*   **染梦耕地**（`DyedreamFarmlandBlock` extends `FarmBlock`）：
    *   踩踏退化：`fallOn` 经 `CommonHooks.onFarmlandTrample` 判定后 → 染梦泥土（已有）
    *   支撑退化：新增覆写 `tick`，上方被不透明方块遮挡（支撑不足）时 → 染梦泥土（替换原版硬编码原版泥土）
    *   作物 2 倍速：新增覆写 `randomTick`，super 耕地逻辑后若上方是 `CropBlock` 作物，额外调用 `BlockState.randomTick` 触发一次作物生长判定（耕地已退化则跳过）
*   **工具转换事件**（新增 `world/PDToolConversionEvents.java`）：监听 NeoForge `BlockEvent.BlockToolModificationEvent`（锄头/斧头右键经 `BlockState.getToolModifiedState` 触发）
    *   `HOE_TILL`：染梦草方块 `dyedream_grass` / 染梦泥土 `dyedream_dirt` → 染梦耕地
    *   `AXE_STRIP`：冷域木头 `cold_domain_log` → 去皮冷域木头（保留 AXIS 轴向）
    *   音效/粒子/耐久由原版工具逻辑自动处理
*   **注册**：`PasterDreamMod` 构造器 `NeoForge.EVENT_BUS.addListener(PDToolConversionEvents::onBlockToolModification)`
*   **验证**：`:PasterDream:compileJava` 通过；已从 neoforge sources jar 确认 `HoeItem` 官方注释指引使用 `BlockToolModificationEvent` 实现自定义开垦

---



### 修复：冷域木头正反面交换 + 方向性放置确认

*   **需求**：冷域木头（含去皮）的端面（年轮截面）与侧面（树皮）纹理切反，导致横放/竖放视觉无方向感。
*   **修复**：重新切割「冷域木头的.png」「去皮的冷域木头.png」——`side`（树皮）取自拼贴图 `(0,1)` 行的深蓝格，`end`（年轮截面）取自 `(1,0)` 的亮蓝格，纹理文件 `cold_domain_log.png` / `cold_domain_log_top.png`（含 stripped 版）内容互换。
*   **方向性放置**：方块类为 `RotatedPillarBlock`（AXIS 属性），DataGen 生成 `axis=x/y/z` 变体 blockstate（横放走 `cube_column_horizontal` + 旋转），放置时按点击面自动旋转轴向 —— 此前逻辑正确，正反面修正后竖放显示年轮截面、横放显示树皮侧面的方向感即可体现。
*   **验证**：`:PasterDream:compileJava` + `processResources` 通过，build 产物纹理已刷新。

---



### 修复：配置界面描述文字自动换行，不再超出界面

*   **问题**：配置界面中较长的提示文字（tooltip，如融梦水晶箱物品池的格式说明）以单行绘制，超出面板宽度溢出到界面外。
*   **修复**：
    *   `ConfigEntry`：提示文字改用 `font.split()` 按可用宽度自动换行；条目行高随提示文字行数动态伸缩（`getVisualHeight(int rowWidth)`），控件垂直居中改为相对整行动态高度
    *   `ConfigEntry.ListEntry`：标题行同样支持提示文字换行，展开区偏移与标题行动态高度联动
    *   `PDConfigScreen`：`getEntryVisualHeight` 增加行宽参数，滚动条/滚轮/点击检测/滚动边界计算全部使用动态高度，保证变高条目布局一致
*   **验证**：`:PasterDream:compileJava` 及全模块编译通过

---



### 修复：装备数值对齐原模组（盔甲防御槽位 + 工具属性）

*   **需求**：对比原模组（1.20.1）与新模组的盔甲/武器/工具数值，确保一致；故意修改项（融梦工具、terra_sword 等）予以规避。
*   **盔甲防御槽位错位**（`PDArmorMaterials.java`）：原模组防御数组按 `[靴,腿,胸,头]` 顺序（1.20.1 `getSlot().getIndex()` 语义），新模组误按 `[头,胸,腿,靴]` 直抄导致槽位错位。已修正五套：
    *   铜甲 `{1,3,5,2}` → 头2 胸5 腿3 靴1（原错位为 头1 胸3 腿5 靴2）
    *   钛/幽匿/染梦 `{3,6,8,3}` → 头3 胸8 腿6 靴3（原胸腿对调）
    *   Qym `{2,10,10,10}` → 头10 胸10 腿10 靴2（原头靴对调）
    *   三件翅膀（天使/遗落/机械）槽位本就正确，未改动
*   **工具属性模板化遗漏**（`PDItemsTools.java` / `PDItemsMaterials.java`）：镐类与染梦/融金/影蚀系列工具被套用原版工具模板（耐久 131/2031、挖速 9、附魔落默认 5、修复材料错用圆石/下界合金锭），逐项修正为原模组数值（耐久/挖速/伤害/攻速/附魔/修复材料，含 1.20 构造语义 1+bonus 的最终伤害换算）：
    *   镐类：copper(225/4.0/2.5/12)、titanium(1721/9.0/4.5/17)、dyedream(1314/11.0/5.0/22)、dyedream_hammer(6570/3.0/10.0/22)、moltengold(251/14.0/3.0/23)、true_moltengold(1255/16.0/4.0/23)、shadow_erosion(1725/13.0/5.0/16)
    *   斧/锹/锄：copper_axe(8.0)、copper_shovel(3.0) 等伤害换算修正；dyedream/moltengold/shadow_erosion 三系补齐耐久/挖速/伤害/附魔
    *   修复材料统一改为对应金属锭（铜/钛/染梦/融金/黑金），并改用 `repairWith(Supplier)` 延迟加载避免静态初始化 unbound value 崩溃
*   **规避项（故意修改，未改动）**：融梦工具四件（250/6/铁级/附魔5）、terra_sword（1561/8/附魔5）、white_sword 挖速 0→2（对剑无影响）、翅膀未设耐久=无限
*   **验证**：`tools/verify_tool_stats.py` 42 项工具数值全对齐（0 差异）+ `:PasterDream:compileJava` 通过

---



### 新增：染梦世界解密玩法完整移植（花园解密 / 雪傀儡解密 / 冻结之花）

*   **需求**：参考原模组花园解密等染梦世界解密玩法，完整移植且保证可用。
*   **花园解密**（flower_11 → flower_12，原 `GardenDecryptionPr0Procedure`）：
    *   新建 `DyedreamGardenDecryptFlowerBlock`（`PasterDream`）：染梦世界中花下 3 格为 `dyedream_desk` 且周围 2 格花阵正确（`flower_13`/`crop_3a`/`flower_8`/`crop_2a` + `grass_3`×4）时，右键花 11 触发：1 tick 后花阵销毁 → 裂纹/尘埃粒子 + `dream0` 音效 → 2 tick 后花 11 完整替换为花 12（双格）、书桌消失。
    *   修复原版缺陷：双格植物成对替换为花 12（原版只替换被点击半格，残留半截花 11）。
*   **雪傀儡解密**（flower_16 → flower_17，原 `Flower16Pr0Procedure`）：
    *   新建 `DyedreamFlower16Block`：书桌 + 四方位 5 格花阵（`crop_0a`/`dyedream_sapling`/`flower_14`/`flower_9`）+ 9 格内雪傀儡与悦灵齐备时，右键献祭最近的雪傀儡与悦灵 → 花阵销毁 → 花 16 变花 17 → 书桌消失 → 雪花/雪球粒子 + `dream0` 音效。
*   **冻结之花**（flower_17 随机刻，原 `Flower17Pr0/Pr1`）：
    *   新建 `DyedreamFlower17Block`（`EntityBlock` + `randomTicks`）：随机刻播撒雪花粒子并循环 8 次随机偏移（-3..3/-1..0），空气凝结成雪、水源冻结成冰；随机偏移暂存于方块实体 NBT（复用 `SimpleMarkerBlockEntity` + `PDBlockEntities.FLOWER_17`）；物品提示"冻结周围的水源 并在地面凝结成雪"。
    *   方块属性跟随原版：`flower_16/17` 发光等级 5、触碰效果为移动速度（flower_17 时长 100 tick）。
*   **注册调整**（`PDBlocksVegetation.java`）：`flower_11/16/17` 从批量注册拆出，改用专属类注册（注册名/门面/创造栏不变）。
*   **回归验证**（`PDDyedreamVerifyHooks`，`dyedream` 专项）：新增 `dyedream.garden.fail_wrong_layout`/`dyedream.garden.success`、`dyedream.snowpuzzle.fail_no_mobs`/`dyedream.snowpuzzle.success`、`dyedream.flower17.freeze` 五项，覆盖负例/正例/交互路径/随机刻冻结。

---



### 新增：Better Biomes 移植树（原封不动提取结构 NBT + 替换方块）

*   **需求**：分析 Better Biomes 数据包（`示例/data/`）的树木结构，定点移植到染梦世界，并加入调试界面方便测试。采用「原封不动提取树结构 NBT、仅替换方块材质」方案，保证外形与数据包一致。
*   **提取方案**（`tools/bb_tree_import.py`，完整 NBT 无损读写）：
    *   从 `示例/data/trees/structures/` 提取 6 个树结构 NBT 到 `data/pasterdream/structure/bb_*.nbt`
    *   palette 方块名替换：原版 `birch/oak/dark_oak/spruce/jungle/acacia/mangrove` 的 leaves → `pasterdream:dyedream_leaves`，log/wood → `pasterdream:dyedream_log`（`distance`/`persistent`/`axis` 属性原样保留，DataVersion 由 DFU 自动升级）
    *   提取清单：`bb_tallbirch`(5×28×5)、`bb_blossom`(17×18×17)、`bb_aspen_big/mid/small`(8×17×8/6×13×7/4×12×4)、`bb_poplar`(5×27×5)；aspen_big 保留 1 个 cobblestone 装饰
*   **调试水晶改用结构放置**（`DebugStructureWandItem`，直接放结构 NBT）：
    *   `debug_wand_dyedream_tree_tallbirch` → `bb_tallbirch`、`blossom` → `bb_blossom`、`aspen` → `bb_aspen_big`、`poplar` → `bb_poplar`
    *   新增 `debug_wand_dyedream_tree_aspen_mid` → `bb_aspen_mid`、`aspen_small` → `bb_aspen_small`
    *   已加入调试创造栏 `PDCreativeTabsDebug.DEBUG_TAB`；中英语言条目补齐（423 注册项双语覆盖）
*   **清理**：删除早期 Placer 模拟方案（`DyedreamTaperedTrunkPlacer` / `DyedreamProfileFoliagePlacer` 类与注册、4 个 configured_feature JSON）
*   **工具脚本**（`tools/`）：新增 `bb_tree_import.py`（NBT 无损提取 + 方块替换）、`analyze_bb_tree_layers.py`（逐层布局分析）
*   **验证**：`:PasterDream:compileJava` + `processResources`（结构 NBT 复制到 build 产物）+ 全模块编译通过；`check_lang.py` 全绿（423 注册项双语覆盖）

---



### 重构：染梦耕地归位染梦注册类

*   **需求**：染梦耕地（dyedream_farmland）是染梦维度的方块，不应放在冷域（PDBlocksColdDomain / PDItemsColdDomain）注册类中。
*   **调整**：方块注册移至 `PDBlocksSimple`（与 dyedream_dirt/grass/log 同区），物品注册移至 `PDItemsBlocks`（与 DYEDREAM_GRASS 相邻）；`PDBlocks` / `PDItems` 聚合引用同步更新；冷域类注释移除染梦耕地描述。
*   **功能不变**：创造栏位仍为染梦标签页；`c:farmlands` 标签、湿润状态、踩踏变染梦泥土等行为不受影响。
*   **验证**：`:PasterDream:compileJava` 通过。

---



### 新增：冷域维度（cold_domain_world）+ 冷域/染梦新方块

*   **需求**：将「模型储存」目录的 7 张纹理注册为模组方块，写入正确创造栏位；「冷域」作为新维度，每个方块具备正确的功能/模型/标签。
*   **冷域维度**（`PasterDream`）：
    *   `PDDimensions.COLD_DOMAIN_WORLD` + `PDBiomes.BIOME_COLD_DOMAIN`（`cold_domain_biome`）
    *   JSON：`dimension/cold_domain_world.json`（fixed 群系 + 冷域噪声设置）、`dimension_type/cold_domain_world.json`（自然/天空/床可用/无袭击/高度 384）、`worldgen/biome/cold_domain_biome.json`（寒冷降雪、freeze_top_layer、极地熊）
    *   `worldgen/noise_settings/cold_domain_world.json`：基于染梦噪声设置，`default_block` 改石头，`surface_rule` 改为冷域地表（表层 `snowy_cold_domain_grass`、下 `cold_domain_dirt`、底部基岩）
    *   `ClientSetup` 注册冷域维度特效（淡冰蓝天空/蓝紫黄昏/深蓝夜雾）
*   **冷域方块**（`PDBlocksColdDomain` / `PDItemsColdDomain`）：
    *   `cold_domain_log` 冷域木头、`stripped_cold_domain_log` 去皮冷域木头：轴向方块（RotatedPillarBlock），斧挖掘，`minecraft:logs` / `logs_that_burn` 标签（block+item），模型由 DataGen 生成（cube_column + axis blockstate）
    *   `cold_domain_dirt` 冷域泥土：锹挖掘，`minecraft:dirt` 标签（可被草方块蔓延）
    *   `snowy_cold_domain_grass` 雪地草坪：继承原版 `SpreadingSnowyDirtBlock`（随机蔓延/退化冷域泥土/雪覆盖切换 snowy 侧面纹理），锹挖掘
    *   `cold_domain_leaves` 冷域树叶：原版 `LeavesBlock` 衰变行为，锄挖掘 + `minecraft:leaves` 标签，剪刀/精准采集掉落，可掉木棍
    *   纹理：`冷域木头的.png`（side/end 切割）、`去皮的冷域木头.png`、`冷域木头的树叶.png`、`泥土的.png`、`雪地草坪.png`（top/side/bottom 切割）
*   **染梦耕地**（`DyedreamFarmlandBlock` extends `FarmBlock`）：
    *   `dyedream_farmland`：完整耕地行为（moisture 湿润/干涸、踩踏变染梦泥土），blockstate 按 moisture 0-6 干纹理 / 7 湿润纹理（`moist_dyedream_farmland`），`c:farmlands` 标签（block+item）
*   **创造栏位**：新增「冷域」标签页 `PDCreativeTabsColdDomain`（雪地草坪/冷域泥土/冷域木头/去皮冷域木头/冷域树叶，图标雪地草坪）；染梦耕地加入染梦标签页
*   **工具脚本**（`tools/`）：`gen_cold_domain_noise_settings.py`（生成噪声设置）、`update_cold_domain_tags.py`（追加标签）、`update_cold_domain_lang.py`（语言条目）、`verify_cold_domain_resources.py`（资源校验）
*   **验证**：`:PasterDream:compileJava` 与全模块编译通过；`:PasterDream:runData` 生成冷域方块模型/方块状态/mineable 标签；资源校验脚本全绿（纹理/JSON/战利品表/维度/语言/标签）

---



### 新增：融梦水晶箱战利品自定义配置（配置界面可见）

*   **需求**：允许玩家自定义融梦水晶箱三个品质（普通/稀有/传说）物品池中的任何物品，并可在配置界面中直接编辑。
*   **实现**：
    *   `PDCommonConfig`（`PasterDream-Common.toml`）新增 `Meltdream Chest` 配置段：`meltdream chest custom loot enabled`（总开关，默认 false）+ 三个 `List<String>` 物品池（`meltdream chest common/rare/legendary loot`，默认值为原内置池）
    *   新增 `MeltdreamChestLootConfig`：解析「物品ID [数量] [权重]」格式，无命名空间时先按 `minecraft:` 再按 `pasterdream:` 解析；无效条目自动跳过，全部无效时回退内置默认池
    *   `MeltdreamChestBlock`：物品池改为从配置读取，删除硬编码懒加载池与内部 `LootEntry`
    *   配置界面：新增「融梦水晶箱」分类（`ConfigCategory.MELTDREAM_CHEST`）；`ConfigEntry` 新增 `ListEntry` 多行编辑控件（支持换行/方向键/Home/End/滚动，点击标题展开折叠），三个物品池均可在界面内直接编辑
*   **配置**：`config/PasterDream-Common.toml` → `Meltdream Chest` 段，键名 `meltdream chest custom loot enabled` / `meltdream chest common loot` / `meltdream chest rare loot` / `meltdream chest legendary loot`，条目格式 `物品ID 数量 权重`
*   **验证**：`:PasterDream:compileJava` 通过；语言文件 zh_cn/en_us JSON 校验通过

---



### 修复：河流群系不再出现在海洋/海岸带

*   **需求**：河流是陆地地貌，不应在海洋/海岸（水下区域）生成——否则水下会铺出染梦沙河床带。
*   **根因**：`DyedreamBiomeSource.computeBiome` 中 river 判定在浅海之后、海岸之前，导致海岸带（大陆性 -0.3 ~ -0.12 的水下浅滩）也会命中 river 群系。
*   **修复**：river 判定增加陆地条件 `continentalness >= SHORE_THRESHOLD`（大陆性 ≥ -0.12），海洋/海岸带一律不生成河流。
*   **验证**（干净世界 NBT 分析）：river 群系仅占陆地 3.33% 地表（此前含海岸带时 10.2%），海岸带 river 单元为 0；地图确认 river 带完全位于陆地、与海岸线自然过渡、不侵入海洋。

---



### 调整：染梦河流形态改为「水道 + 两岸沙地」窄带蜿蜒

*   **需求**：河流不应是大片水域，而应模拟现实河流——窄水道（约 3-5 格）+ 两岸 3-5 格沙质河岸，形成弯曲引导性的窄带河道。
*   **实现**：
    *   `DyedreamBiomeSource`：`RIVER_WEIRDNESS_BAND` 0.14 → **0.045**（weirdness 窄带判定 river 群系，band 内为河道+河岸）
    *   `density_function/dyedream_river_f.json`：挖空起始带宽 0.14 → **0.010**，渐变系数 12 → **150**（`clamp(150*(|w|-0.01),0,1)`）——只挖 |w|<0.01 的核心水道，0.01~0.017 陡渐变，0.045 群系带内其余部分不挖空（保留为两岸沙地）
*   **效果**（干净世界 NBT 分析）：river 群系带从 24-48 格宽带 → **蜿蜒窄带 8-24 格**；水道（水面）**4-8 格**；两岸沙地各 4-8 格。群系带（0.045）> 挖空带（0.010）保证两岸不挖空、只铺沙。
*   **验证**：`compileJava` 通过；干净世界 81 区块 forceload + NBT 分析确认窄带形态。

---



### 调整：染梦维度海平面提高 5 格（55 → 60），抬升河道水面

*   **需求**：用户反馈河流水面过低，需要提高 5-6 格才达到效果。
*   **实现**：`noise_settings/dyedream_world.json` 的 `sea_level` 从 55 → 60。染梦维度使用 vanilla 生成器时，`sea_level` **只控制含水层水位（水面）**，不影响陆地高度（陆地由 final_density 固定表达式决定）——所以只需调 sea_level 即可精确抬升水面、陆地不动，天然兼容。
*   **验证**（干净世界 NBT 分析）：水面顶层 Y 从 54 → **59**（+5 格，243 列水面），水方块从 y=40 到 y=59 连续；陆地高度不变。

---



### 修复：染梦沙无重力下落物理行为

*   **根因**：`dyedream_sand` 通过 `SimpleBlockBuilder` 批量注册，默认使用 `SelfDropBlock`（普通 `Block`）——虽然属性复制自 `Blocks.SAND`，但类不是下落方块，没有 `FallingBlock` 的重力物理行为（破坏下方方块后不会下落形成 `FallingBlockEntity`）。
*   **修复**（`PDBlocksSimple`）：给 `dyedream_sand` 配置自定义 `blockFactory`，改用 **`ColoredFallingBlock`**（1.21.1 原版沙子类，继承 `FallingBlock`，自带 CODEC）：
    * `new ColoredFallingBlock(new ColorRGBA(0xF0BAD3), p)` — `dustColor` 取染梦沙纹理主色 `(240,186,211)`，下落尘粒子颜色与原版一致机制
    * 保留 `mineable("shovel")` / `plantable()` / 战利品表（掉自身）不变
*   **验证**：`:PasterDream:compileJava` 通过。

---



### 修复：染梦 river 群系大面积占据地表 + 河道挖空导致地表抬升

*   **问题**：上轮修复后出现两个新问题——① `biome_dyedream_river` 群系占据约 46% 的地表（应为窄条河网而非独立群系）；② 河道挖空密度函数把部分列从海平面挖到 y=290 巨洞，地表出现"抬升到建筑最高度"的异常。
*   **根因**：
    * **群系判定公式错误**：用 vanilla 旧版 riverFactor 雕刻公式（`|erosion-0.4|` / `|ridges-0.9|`）做群系判定，该公式在大部分区域值都大，导致 river 群系大面积命中。vanilla 的 river 群系实际由 MultiNoise 的 **weirdness 窄带**决定（`|weirdness| < ~0.05`）。
    * **挖空无高度限制**：sink 密度函数作用于全高度，0.5 的正偏移在密度临界列会把整列从海平面挖到 y=290。
*   **修复**：
    * **群系判定**：`DyedreamBiomeSource.computeBiome` 改为 vanilla 风格 `|weirdness| < RIVER_WEIRDNESS_BAND(0.25)`（约 14% 区域，形成可见河网）；**不能用 riverFactor 雕刻公式做群系判定**（已在注释中说明教训）。
    * **挖空 mask**：`dyedream_river_f.json` 改为 `clamp(3×(|weirdness|-0.25), 0, 1)`（与群系判定一致，`|weirdness| < 0.25` 时=0 即河心）。
    * **挖空幅度**：`dyedream_river_sink.json` = `0.5 × (1-river_f)`（河心正偏移挖空），**限制高度 y=52~78**（仅海平面附近），避免整列挖空。
*   **验证**（干净世界 NBT 分析）：river 群系占比 46% → **14.1%**；地表高度 avg 69.7 / max **160**（此前 294）；>200 抬升列 190 → **0**；水+染梦沙共存区块 **95 个**（河床有效果）。`compileJava` BUILD SUCCESSFUL。

---


### 修复：染梦维度"河流状干沟壑"（海平面调低后河道无水 + 河床无染梦沙）

*   **问题**：染梦维度地形存在大量像河流的雕刻沟壑，但里面没有水，底部也不是对应的水下方块（裸露默认方解石）。
*   **根因**：
    * 染梦维度当前使用 vanilla `minecraft:noise` 生成器（自定义 `DyedreamChunkGenerator` 未启用），地形由 noise_settings 的原版 `overworld/depth` 等密度函数生成——原版 depth 内置河流沟壑雕刻逻辑，这就是"像河流的雕刻"的来源。
    * 海平面从原版参考 63 调低到 55 后，大量河床沟壑**高于海平面** → 含水层不灌水 → 干涸暴露。
    * `DyedreamBiomeSource.computeBiome` **从不返回河流群系**（`biome_dyedream_river`），surface_rule 中给河流铺染梦沙的规则永远不会触发 → 河床裸露方解石。
*   **修复**（不回退海平面、维持 vanilla 生成器的兼容方案）：
    * **新增密度函数** `data/pasterdream/worldgen/density_function/dyedream_river_f.json`：复刻原版 riverFactor 公式 `f = clamp(max(|erosion-0.4|-0.6, |ridges_folded-0.9|×0.85), 0, 0.75)`（用 `ridges_folded` 与群系判定保持一致）。
    * **新增密度函数** `dyedream_river_sink.json`：在 y52+ 对河道区域施加**正密度偏移**（挖空河道到海平面以下，含水层自动灌水成真河）。
    * **noise_settings** `final_density` 外层包 `add` 接入 `dyedream_river_sink`（仅 final 一处，避免与 initial 双重挖空）。
    * **surface_rule** 移除 river 群系规则的 `above_preliminary_surface` 包裹，使河床在水下也能铺染梦沙（原版水下铺沙用 `stone_depth floor` 而非 preliminary surface）。
    * **`DyedreamBiomeSource.computeBiome`** 在海洋判定后、陆地群系前新增河流判定：复刻同一 riverF 公式，`f > 0.55` 返回 `biome_dyedream_river`，激活铺沙规则。
*   **验证**：专用服务器生成干净世界，NBT 区块分析确认大量区块出现"水 + 染梦沙"河流特征（如 chunk(-2,-2): 106水+143沙、(-1,-2): 287水+38沙、(0,-6): 346水+22沙），`locate biome biome_dyedream_river` 正常命中；`compileJava` BUILD SUCCESSFUL。
*   **工具**：新增 `tools/analyze_dyedream_river_chunk.py`（纯标准库解析 region NBT，统计各区块水/染梦沙/方解石分布，可复用于未来地形验证）。

---


### 修复：旁观者打开容器菜单崩溃（extraData null NPE → 网络协议错误）

*   **问题**：旁观模式下右键可储存方块（染梦书桌等）客户端崩溃：`Failed to handle packet ClientboundOpenScreenPacket` + `NPE: extraData.readBlockPos() because extraData is null`（DyedreamDeskMenu.java:33），并报"网络协议错误"。
*   **根因**（NeoForge 21.1.219 字节码 + vanilla 源码确认）：
    * 旁观者右键方块时，`ServerPlayerGameMode.useItemOn` 走 **SPECTATOR 专用分支**：`state.getMenuProvider()` → `player.openMenu(provider)`（**vanilla 单参**，不写 BlockPos）——不经过我们方块里的 `useWithoutItem`（双参 `openMenu(provider, pos)`）
    * 单参 `openMenu` → `openMenu(provider, null)`（consumer 为 null）→ `FriendlyByteBufUtil.writeCustomData` 返回**空数组** → 服务端走 **vanilla `ClientboundOpenScreenPacket`（不带 extraData）**，而非 NeoForge `AdvancedOpenScreenPayload`
    * 客户端收到 vanilla 包 → `MenuScreens` → `IContainerFactory.create(id, inv, null)` → 菜单网络构造器 `extraData.readBlockPos()` → **NPE**
    * 影响范围：所有用 `IContainerFactory` 网络构造器直接读 `extraData` 的菜单（15 个 BE 菜单 + 储物袋），旁观者打开必崩；`DreamnotesGui0Menu`/`ShadowSelectEndMenu` 已有防御故不崩
*   **修复**（15 个菜单 + StorageBagMenu 网络构造器加 null 防御）：extraData 为 null 时兜底构造空菜单（BE 传 null → `stillValid` 返回 false 由服务端自动关闭），Blueprint/储物袋兜底为无效/默认数据正常显示。修改：`DyedreamDeskMenu`/`ShadowChestMenu`/`MeltdreamChestMenu`/`DreamCauldronMenu`/`DreamAccumulatorMenu`/`ResearchTableMenu`/`ShadowBlastFurnaceMenu`/`ShadowDeskMenu`/`WindmoorCrateMenu`/`PicnicBasketMenu`/`TheEndlessBookOfDreamSeekersMenu`/`WeaponWorkshopMenu`/`WorkshopAnvilMenu`/`WorkshopBlastMenu`/`BlueprintGui0Menu`/`StorageBagMenu`
*   **验证**：全模块 `compileJava` BUILD SUCCESSFUL。

---


### 改动：调音图腾终结技演出重做（三段暗化 + 黑白闪 + 6 秒高强度晃动 + 破坏范围减半 + 粒子持续 5 秒）

*   **背景**：用户反馈终结技演出"不全面"——缺爆炸前的亮度渐进、爆炸瞬间缺黑白闪与高强度长时晃动、地形破坏过猛、粒子瞬时即散。
*   **改动**（`ShadowTuneTotemEntity.executeSkillTick`）：
    * **300t 提醒时缓慢降亮度**：广播"即将爆破"的同时 `AtmosphereEffectAPI.darken(0.3f, 200)`——客户端阻尼插值（0.15/tick）缓慢爬升，覆盖到爆炸后，先让玩家感知"危机逼近"
    * **400t 爆炸动画时快速压黑**：暗化强度 `0.7f → 0.95f`，覆盖 300t 的缓降，爆炸动画瞬间近乎全黑
    * **482t 爆炸高潮黑白闪**：`ImpactFrame` 从单黑场（8t）改为**黑白交替 4 帧**（黑 3t / 白 3t / 黑 3t / 白 3t，invert 切换）
    * **6 秒高强度屏幕晃动**：`inTime(4).stayTime(8).outTime(108)` 总 120t（6 秒），amplitude `0.15f → 0.35f`（高强度），out 段长时递减——强度由最高随时间衰减
    * **破坏地形范围减半**：爆炸半径 `15.0f → 7.5f`（破坏范围减半，2500 魔法伤害半径 50 格不变）
    * **粒子改为持续 5 秒**：一次性 `sendParticles`（128 个暗影石+烟雾瞬时爆发）改为 `ParticleEmitterAPI.spawn` 持续发射器 `lifetime(100)`（5 秒，每 tick 6 个，CircleSpawnProcessor 向上扩散），爆炸核心闪光保留 EXPLOSION_EMITTER 一次性
*   **验证**：`:PasterDream:compileJava` BUILD SUCCESSFUL。

---


### 修复：世界生成 far chunk 日志刷屏 + 光照 DataLayer NPE 崩溃（浮空岛/巨型染梦树跨区块写入）

*   **问题**：新世界生成时 `floating_island` 与 `dyedream_tree_colossal` 刷屏 `Detected setBlock in a far chunk`，随后光照线程崩溃 `NullPointerException: Cannot invoke "DataLayer.get" because "datalayer" is null`（`LayerLightSectionStorage.getStoredLevel`），世界生成卡死。
*   **根因**：
    * **写半径限制**（NeoForge 21.1.219 `WorldGenRegion.ensureCanWrite`，经本地反编译字节码确认）：features 阶段 `blockStateWriteRadius = 1`，只允许向「中心区块 ±1」（3×3 区块）写入；越界即打印 "Detected setBlock in a far chunk" 并拒绝写入
    * **两个地物横向跨度超出 ±1 区块**：浮岛椭球体半径 ≤12 + 随机游走 ±30% + 云桥最远 `radius+15` = 27 格；超巨型树 3×3 主柱 + 角柱 + Bresenham 侧枝（5~10 格）+ 二分支，横向最远 ~18 格——origin 靠近区块边缘时方块落在未就绪区块
    * **连锁崩溃**：越界写入尝试污染光照一致性，光照引擎 `propagateIncreases` 对无 DataLayer 的 section 调 `getStoredLevel` → `datalayer == null` NPE
    * 说明：已有 `safeSetBlock`/`validTreePos` 的 `canPlaceInRegion` 预检能拦截写入，但 `ensureCanWrite` 本身在判定越界时会打 ERROR 日志，故仍刷屏
*   **修复**（区块中心对齐方案）：
    * `WorldGenUtils` 新增 **`alignToChunkCenter(origin)`**：X/Z 对齐到所在区块中心（`chunkX*16+8`），Y 保持不变
    * `FloatingIslandFeature.place`：origin 先对齐区块中心再生成，岛体 + 水晶 + 藤蔓 + 云桥全程落在 ±1 区块写半径内
    * `DyedreamTreeFeature.place`：新增 `alignGiantTree`，仅对 `DyedreamColossalTrunkPlacer` / `DyedreamWorldTreeTrunkPlacer` 两类巨型树对齐 origin（X/Z），构造新 `FeaturePlaceContext` 委托 vanilla TreeFeature；普通染梦树分布不受影响
*   **验证**：`:PasterDreamAPI:compileJava` + `:PasterDream:compileJava` 通过。

---


### 修复：资源重载崩溃（PostChain.close 在资源加载线程执行）

*   **问题**：F3+T 重载资源 / 加载世界时崩溃（`IllegalStateException`，PostShaderManager.java:124 `PostChain.close()`）。
*   **根因**：`PDEffectClientEvents.onAddReloadListeners` 在 `AddReloadListenerEvent`（`Util.backgroundExecutor()` 的 Worker-Main 线程）中**同步**调用 `PostShaderManager.reloadAll()`；而 `PostChain.close()` 内部调用 `RenderTarget.destroyBuffers()` 属 OpenGL 操作，必须在渲染线程执行——资源加载线程触发即崩溃。不能改用 `executeBlocking` 绕到渲染线程：加载世界时渲染线程正阻塞等待 reload future，会死锁。
*   **修复**（`PostShaderManager` + `PDEffectClientEvents`）：
    * `PostShaderManager.reloadAll()` → **`requestReload()`**：仅置位 `volatile reloadPending` 标记，任意线程可安全调用
    * 新增 **`processPendingReload()`**：渲染线程 tick 中消费标记，执行真实的 `close()` 销毁 + 清缓存 + 重置窗口尺寸；重建保持惰性，重载完成后 `getChain` 用新资源实例化
    * `PDEffectClientEvents.onClientTick` 开头调用 `processPendingReload()`；`onAddReloadListeners` 改为只调 `requestReload()`，并补充线程约束注释
*   **验证**：`:PasterDreamAPI:compileJava` + `:PasterDream:compileJava` 通过。

---


### 修复：亚伦柯斯终结技「调音图腾」部分战局无法释放（贴脸永不评估）+ 远程手贴脸卡普攻

*   **问题**：终结技在部分战局完全不释放，尤其玩家近战贴脸输出时；「远程手」贴脸后纯普攻、不放任何技能。
*   **根因**（`AaroncosRighthand0Entity.tickSkillCycle`）：终结技评估被锁在「目标距离 > 6 格」的远程技能分支内（`distanceToSqr > 36`）。而 BOSS 的 AI（`MeleeAttackGoal` + `FlyingPursuitGoal`）会主动追到贴脸——玩家肉搏时右手血量压到 1/3 也不放终结技，直到被击杀。
*   **修复**：
    * **终结技评估无条件化**（`AaroncosRighthand0Entity`）：`tryTriggerTuneTotemFinale()` 移到目标/距离检查之前，血量条件（<1/3 且左手死/≤1/5）满足即释放——贴脸肉搏、玩家短暂脱战失去目标都能触发
    * **远程手拉开距离**（`AaroncosHandEntity`）：新增 `getPreferredCombatRange()`（右手覆写=10 格，左手默认=0 保持近战）；`FlyingPursuitGoal` 增加保持距离逻辑（过近远离 / 过远逼近 / 舒适带悬停），`BossMeleeAttackGoal` 在远程手过近时禁用近战普攻，交由低优先级追击 AI 拉开距离——避免贴脸卡在普攻不放任何远程技能
*   **验证**：`:PasterDream:compileJava` 通过。

---


### 改动：暗影调和图腾爆炸可破坏地形 + 伤害提升至 2500 + 粒子匹配范围

*   **背景**：图腾爆炸原为纯实体伤害（`target.hurt(magic, 250)`），不破坏任何方块，缺少爆炸的冲击破坏感。
*   **改动**（`ShadowTuneTotemEntity.executeSkillTick` 的 t482 爆炸）：
    * **伤害提升**：50 格内魔法伤害 **250 → 2500**（`target.hurt(damageSources().magic(), 2500.0F)`）
    * 新增 **15 格破坏地形爆炸**：`serverLevel.explode(...)` 使用 **`Level.ExplosionInteraction.BLOCK`**（不受 `mobGriefing` 游戏规则限制，必然破坏方块；`MOB` 交互会被 mobGriefing 关闭而失效）
    * **关闭爆炸自身对实体的伤害/击退**：自定义 `ExplosionDamageCalculator` 覆写 `shouldDamageEntity` 返回 `false`，避免爆炸伤害与 2500 魔法伤害叠加（2500 魔法伤害逻辑原样保留）
    * **粒子匹配范围**：保留 10/20/30/40/50 格同心圆环扩散粒子（标记 50 格伤害波及范围），与爆炸视觉一致
*   **验证**：`:PasterDream:compileJava` 通过。

---


### 移除：暗影之手冲刺残影特效 + 残影采样间距过滤

*   **移除暗影之手冲刺残影**：`ShadowHandEntity.serverChargeTick` 中的 `GhostEffectAPI.startGhostTrail` 调用删除——暗影之手位移小（6 格），残影快照重叠看不出渐变，直接去掉该特效。暗影之手冲刺保留位移/音效/动画本身。
*   **残影采样间距过滤**：`GhostHandler` 新增 `MIN_SAMPLE_DIST_SQ = 1.0²`——实体每 tick 位移不足 1 格时跳过采样，让位移小的实体生成"少而拉开"的快照（BOSS 冲锋位移大不受影响）；`start()`/`clearAll()` 重置采样位置，避免新旧残影源串扰。
*   **验证**：`:PasterDream:compileJava` BUILD SUCCESSFUL。

---


### 修复：竞技场 BOSS 战 BGM 听不到（高空音源 16 格衰减 + 单次播放）

*   **问题**：BOSS 战 BGM（aaroncos_music）从一开始就完全听不到；召唤音效同理。
*   **根因 1（致命）**：`playBossMusic` 用 `arenaLevel.playSound` 位置音效，音源固定在 `ARENA_CENTER`(0,70,0) 高空；玩家在 y≈42 战斗，距离 28 格远超 `aaroncos_music` 默认 **16 格衰减范围**，客户端音量衰减到 0 → 全程静音。原模组从召唤方块坐标（玩家身旁）播放故正常。
*   **根因 2**：原模组用 `time0` 计数器每 150 tick 周期性重新播放实现循环；重写后仅单次播放，播完 149 秒即静音。
*   **修复**（`PDArenaEvents`）：
    * 新增 `playArenaSoundForAll`——对竞技场**每个在场玩家 `playNotifySound`**（无位置、无衰减，全场清晰），BGM 与召唤音效均改用它
    * BGM 改 `startBossMusicLoop`：召唤时播放一次，之后经 `ServerScheduler` 每 `BOSS_MUSIC_REPLAY_INTERVAL=2980` tick（≈149 秒，略短于整曲避免间隙）重播一次；战斗阶段离开 SUMMONING/FIGHTING（VICTORY 或初始化重置）或竞技场无玩家时自动停止；`bossMusicLoopGen` 代际号防重复召唤叠加多条循环链
*   **验证**：全模块 `compileJava` BUILD SUCCESSFUL。

---


### 修复：每次进世界重复发放《帕斯特指南》（玩家死亡重生后标记丢失）

*   **问题**：玩家每次进入世界都收到一本《帕斯特指南》，应为新存档首次登录发放一次。
*   **根因**：发放标记 `pasterdream.guide_book_given` 写在 `persistentData` 顶层（`NeoForgeData`）。NeoForge 的 `ServerPlayer#restoreFrom` 在玩家克隆（死亡重生/末地返回）时**仅**复制 `PlayerPersisted` 子标签，顶层数据随克隆清空 → 标记丢失 → 下次登录重新发书。竞技场/维度测试中玩家死亡频繁触发克隆，故每次进世界都补发。
*   **修复**（`PlayerDataEvents.giveGuideBookIfNeeded`）：
    * 标记改存至 `Player.PERSISTED_NBT_TAG`（`PlayerPersisted`）子标签下，克隆时自动保留
    * 兼容迁移：历史存档顶层的旧标记读取时自动迁入新位置，已领过书的玩家不会重复发放
*   **验证**：全模块 `compileJava` BUILD SUCCESSFUL。

---


### 改动：BOSS 技能改为「攻击时释放」+ 距离区分 + 技能期间停普攻

用户反馈 BOSS 技能在无仇恨目标时也定时释放（和平模式/脱战也会放技能），且技能与普攻割裂。改造：

*   **技能只对仇恨目标释放**：左右手 `tickSkillCycle()` 开头加 `getCombatTarget()` 检查——无存活目标直接返回，脱战/和平模式站桩不放技能
*   **区分近远距离**（用户确认）：
    * 左手（近战手）：目标近身（`<12` 格）才释放冲刺/重击
    * 右手（远程手）：目标稍远（`>6` 格）才释放魔法弹/涡流，终结技图腾也走远程距离判定
*   **技能期间停普攻**（用户确认）：`AaroncosSkill==1`（技能执行中）时暂停近战普攻与追击——
    * 原版 `MeleeAttackGoal` 替换为内部 `BossMeleeAttackGoal`（`canUse`/`canContinueToUse` 加 `!isSkillActive()`）
    * `FlyingPursuitGoal` 的 `canUse`/`canContinueToUse`/`tick` 均加 `!isSkillActive()`（技能期间不贴脸、不 `doHurtTarget`）
*   **新增基类辅助**：`isSkillActive()`（读 AaroncosSkill）与 `getCombatTarget()`（存活仇恨目标），供 goal 与子类复用
*   **验证**：全模块 `compileJava` BUILD SUCCESSFUL

---


### 修复：竞技场 BOSS 击杀机制统一（击杀即踢出 → 手动右键返回）

*   **问题**：检测到 BOSS 击杀即把玩家踢回主世界（甚至只杀一只 BOSS 也会触发），多个离场机制重叠冲突。
*   **统一为单一机制**：玩家击杀全部（左右手）BOSS 后进入 `VICTORY` 阶段并**留在竞技场**，开箱捡取战利品后**手动右键中心召唤方块**（`AaroncosHandSpawnBlock`）返回主世界。
*   **删除自动踢出**：`PDArenaBossManager.triggerVictorySequence` 不再立即 `teleportAllPlayersToOverworld`；移除 410t 强制离场倒计时（`scheduleVictoryCountdown`）及 forceLeave 状态机（`ForceLeaveActive`/`ForceLeaveGen` 字段与 NBT）。
*   **修复未召唤即判胜利**：死亡回调原本只检查存活标志（初始化即 false），未召唤时任意 BOSS 死亡回调就会误判"双手已灭"直接触发胜利。改为 `onLeftHandDeath`/`onRightHandDeath` **仅在 `FIGHTING` 阶段接受死亡判定**（未召唤/召唤中/已胜利阶段一律忽略）。
*   **幂等保护**：双 BOSS 同 tick 死亡时胜利序列只触发一次（VICTORY 阶段直接忽略重复触发）。
*   **修复残留 VICTORY 阶段误判**（未召唤却被传送回主世界）：`ArenaBossData` 为 SavedData，phase 会跨会话持久化；旧逻辑在 VICTORY 阶段跳过竞技场初始化，导致上一场残留的 VICTORY 无法清除，玩家进竞技场右键召唤方块即命中离场分支。改为 `PDArenaEvents` **仅当竞技场中没有其他玩家时初始化**（首个进入者重置 BOSS 状态，清除持久化残留阶段；已有玩家在场不重置，保护胜利战利品箱）。
*   **联动清理**：`AaroncosHandChestBlockEntity` 移除开箱取消强制离场调用；语言文件删除 `loot_opened_leave_via_eye` 文案、更新胜利提示；smoke test `PDSecondDreamVerifyHooks` 改为断言「击杀后留场 + 手动右键离场补包」。
*   **修复战利品箱悬空**：箱子原生成在 `ARENA_CENTER.below()`（(0,69,0)）——竞技场结构内部的高空，下方无支撑方块。竞技场中心地面实为 y=39 实心 `shadow_arena_block_0` 地基 + y=41 `shadow_fissure_5` 顶面（y=42 为 BOSS 战斗区地面）。新增 `VICTORY_CHEST_POS` 常量统一引用，箱子落在竞技场中心地面，玩家战斗结束走一步即达；smoke test 断言位置同步更新。
*   **修复战利品箱卡地**：`VICTORY_CHEST_POS` 初版 (0,41,0) 直接替换中心 `shadow_fissure_5` 装饰方块，箱子嵌进地面（卡地里）→ 上提一格至 **(0,42,0)**，箱子站在 y=41 地面上方完整露出。
*   **验证**：全模块 `compileJava` BUILD SUCCESSFUL。

---


### 修复：BOSS 过场后仍无敌 + 常规技能黑白闪移除 + 终结技重构 + 删除贴花系统

*   **修复 BOSS 过场后无敌**：`AaroncosHandEntity.baseTick` 首次初始化时无条件 `pendingTasks.clear()`，清掉了 `onAddedToLevel` 排程的「召唤结束解除无敌」任务 → `isSummoning` 永不解除 → 过场后 BOSS 仍无敌。改为**仅非召唤状态清空**，召唤期间保留解除任务。
*   **移除常规技能黑白闪**：普通受击、左手冲刺起手、重击三段、右手魔法弹发射、涡流爆发、狂暴的 ImpactFrame（全屏黑白闪）全部移除——黑白闪只保留给终结技，避免频繁闪屏影响战斗。
*   **终结技重构（参考 Chesed BOSS 演出）**：
    * t0 释放：只保留短促暗化铺垫（0.4 强度 60t），不再闪白
    * 图腾 t400 充能：暗化蓄力铺垫（0.35 强度持续 82t，黑场蓄力压迫感）
    * 图腾 t482 爆炸：**全屏黑场打击帧 + 屏幕晃动 + 暗化峰值（0.7 强度）**——高潮集中在爆炸时刻
*   **删除贴花系统**：用户反馈"贴花没啥观赏性"，移除整个 Decal 子系统——API（`DecalData`/`DecalEffectAPI`/`DecalPayload`/`Decal`/`DecalHandler`）、网络接线、全部 BOSS 调用、调试命令 `/pasterdream vfx decal`、SKILL 文档贴花章节。
*   **验证**：全模块 `compileJava` BUILD SUCCESSFUL。

---


### 新增：屏幕晃动系统 ScreenShake + 终结技大演出（受击移除闪白）

*   **新增屏幕晃动系统**：借鉴 FDLib `ScreenShake`/`DefaultShake` 思路独立实现。`ScreenShakeAPI`（服务端）→ `ScreenShakePayload`(in/stay/out/amplitude/frequency) → 客户端 `ScreenShakeHandler`（投影矩阵确定性随机偏移 + 三阶段衰减 + 上一帧 lerp 平滑）。`GameRendererMixin` 注入原版 `bobHurt`（受击视角晃动）HEAD，把晃动偏移 translate 进相机矩阵。
*   **移除普通受击闪白**：`AaroncosHandEntity.hurt()` 不再每次受击触发全屏打击帧（打一下就闪白太频繁）。
*   **终结技配屏幕晃动**：`tryTriggerTuneTotemFinale` t0 在原有「全屏黑场打击帧 + 暗化氛围」基础上新增**屏幕晃动**（in2/stay8/out14，amplitude 0.15），配合闪白形成终结技大冲击演出。
*   **验证**：全模块 `compileJava` BUILD SUCCESSFUL。

---


### 新增：亚伦柯斯 BOSS 全技能特效挂载

将 7 个特效系统系统性地挂到亚伦柯斯双体 BOSS 的每个技能上（适中选择，每技能 1-2 特效点缀）：

**左手 `AaroncosLefthand0Entity`**
*   冲刺：t16 冲锋起手打击帧（`ImpactFrame(0.5,0.03,3)`）+ t17/t24 落地冲击波贴花（size4）
*   重击：三段落地 t19/30/53 各打击帧 + 冲击波纹贴花（size 7→9→11 递增），第三段反相黑场
*   剑雨：每段落点 t∈{57,70,83,88,95,105,112} 生成预警圈贴花（size2，短暂）

**右手 `AaroncosRighthand0Entity`**
*   魔法弹：t5 蓄力光团粒子发射器（灵魂火焰，30t）+ t35 发射打击帧
*   涡流：t42 爆发反相打击帧 + BOSS 脚下漩涡贴花 + 每玩家落点预警圈贴花
*   终结技调音图腾：t0 释放全屏黑场打击帧 + 暗化氛围（强度 0.6，100t）+ t21 图腾落点贴花

**子实体**
*   魔法弹 `ShadowMagicballEntity`：爆炸点暗影贴花
*   调音图腾 `ShadowTuneTotemEntity`：t400 充能光柱粒子发射器（82t）+ t482 爆炸反相打击帧 + 爆炸贴花（size8）
*   暗影之手 `ShadowHandEntity`：冲刺瞬间残影拖尾（20t，alpha40）

**验证**：全模块 `compileJava` BUILD SUCCESSFUL。

---


### 修复：残影/贴花消失（渲染矩阵双重相机变换）

用户反馈残影与贴花全部不可见。排查根因：

*   **根因**：上一轮为修复"贴花跟随视角"误把残影和贴花的渲染矩阵从「`translate(pos - camPos)` 相对相机偏移」改成「`mulPose(modelViewMatrix)` + `translate(世界坐标)`」。但 `RenderType`（entityTranslucent / 实体渲染器）的 shader 会**再乘一次当前全局 ModelViewMat（相机 view）**，导致双重相机变换，顶点被渲染到视野外 → 两个特效都不可见。
*   **修复**：残影与贴花全部恢复「相对相机偏移」方式（`new PoseStack()` + `translate(pos - camPos)`，不乘 modelView），shader 的 view 负责相机变换。同时贴花保留了最初缺的 `setNormal`，并真正修复了"跟随视角"问题（最初是多了 `mulPose(modelView)` 所致）。
*   **验证**：`:PasterDreamAPI:compileJava` / `:PasterDream:compileJava` 通过。

---


### 修复：贴花跟随视角 + 残影独立淡出 + SKILL 更新

*   **贴花跟随玩家视角 / 不对齐地面**：`Decal.render` 在调用方已乘模型视图矩阵（世界坐标 → 相机空间）后，又手动减相机位置 → 双重变换导致贴花"贴屏幕"跟随视角。已移除 `-camPos`，改为**直接 translate 世界坐标**（参照 SkyboxAPI.buildSkyPoseStack）；`Decal.render`/`DecalHandler.renderAll` 签名去掉 Camera 参数。贴花现在固定在世界空间、按 direction 对齐。
*   **残影透明度统一变动**：原实现采样时用 `active.alpha * fade`（基于残影源年龄），所有快照共享 fade → 同时淡出。已改为**每个残影快照记录采样时初始 alpha，渲染时按自身 age 独立渐出**（拖尾越靠后越淡）；快照寿命恢复 12 tick，`partialTick` 恢复实时值（用户认可的最初版本效果）。
*   **开发 API（SKILL）更新**：`pasterdream-vfx-api/SKILL.md` 追加残影/贴花/雾色三节 + 引用文件清单，description 更新为 7 个特效。
*   **验证**：全模块 `compileJava` BUILD SUCCESSFUL。

---


### 修复：贴花崩溃（缺 Normal）+ 残影"自己动"

*   **贴花崩溃**：`Decal.render` 用 `RenderType.entityTranslucent`（顶点格式含 Normal），但四顶点漏写 `setNormal` → `IllegalStateException: Missing elements in vertex: Normal`（`/pasterdream vfx decal` 崩溃）。已补 `.setNormal(0, 1, 0)`（平面法线局部坐标 +Y）。
*   **残影"自己动"**：残影位置/朝向已冻结在采样点，但重渲染实体时**骨骼动画实时播放**（GeckoLib/玩家渲染器机制），导致残影播实时动画。修复：
    * 残影快照寿命 12 → **3 tick**（快速淡出，动画变化不可见，视觉为"定格重影拖尾"，与 FDBosses 原版一致）
    * 渲染 `partialTick` 固定传 0（使用上一完整 tick 姿态，进一步减弱动画运动感）
*   **验证**：`:PasterDreamAPI:compileJava` / `:PasterDream:compileJava` 通过。

---


### 新增：三个 BOSS 战特效（残影 / 贴花 / 雾色氛围，借鉴 FDBosses 设计思路独立实现）

借鉴开源模组 Qliphoth Awakening 本体（FDBosses，作者 FINDERFEED）的 BOSS 特效编排（许可证禁止复制源码，仅借鉴思路），在现有特效系统基础上新增三个通用特效：

*   **残影特效 GhostEffect**：`GhostEffectAPI`（服务端）→ `GhostPayload`(entityId/duration/alpha) → 客户端 `GhostHandler`（每 tick 采样实体位置生成残影快照，`RenderLevelStageEvent.AFTER_ENTITIES` 阶段用实体渲染器重渲染半透明副本）。核心 `ColoredVertexConsumer`（API `api/client/util/`）强制把顶点颜色替换为半透明白。**亚伦柯斯左手冲刺技能**（`executeSprintSkill` tick 16 冲锋后）触发 24 tick 残影拖尾
*   **贴花特效 DecalEffect**：`DecalEffectAPI`（服务端）→ `DecalPayload`(pos/direction/in-stay-out/size/texture) → 客户端 `DecalHandler`/`Decal`（渲染 XZ 平面四顶点 + 方向旋转 + 三阶段 alpha 缓动）。**亚伦柯斯狂暴**时脚下生成灵魂沙纹理暗色法阵圈（10 渐入 / 100 持续 / 30 渐出）
*   **雾色/暗化氛围 AtmosphereEffect**：`AtmosphereEffectAPI`（服务端）→ `AtmospherePayload`(kind/strength/duration) → 客户端 `AtmosphereHandler`（阻尼插值平滑进出）→ `PDEffectClientEvents` 的 `ViewportEvent.ComputeFogColor` 修改雾色（暗化灰雾 / 血色雾）。**亚伦柯斯狂暴**时全场血色雾（强度 0.9，持续 120 tick 后衰减退出）
*   **网络**：3 个 S2C payload（`GhostPayload`/`DecalPayload`/`AtmospherePayload`）注册进 `PDNetwork`，经 `PDClientVfx` 反射落地（专用服安全）
*   **验证**：全模块 `compileJava` BUILD SUCCESSFUL

---


### 修复：过场调试命令环绕轨迹 + 清理调试日志

*   **过场环绕轨迹**：`/pasterdream vfx cutscene` 原 3 个相机点都在 X=0 平面（仅 Z/Y 变化），表现为"向一个方向波浪运动"而非环绕。改为 4 个环绕点（前/右/后/左，X/Z 平面分布 + 上下起伏），CatmullRom 样条画出环绕轨迹。
*   **清理调试日志**：移除 `PDClientVfx.handleStartCutscene` 的 `[PDClientVfx] 收到过场包` info 日志、`CutsceneCameraHandler.start` 的 `[CutsceneCameraHandler] 过场开始` info 日志、`resolveCameraType` 降级 warn 日志；保留 `过场启动失败` error 日志（仅异常时输出，用于故障诊断）。
*   **验证**：`:PasterDreamAPI:compileJava` / `:PasterDream:compileJava` 通过。

---

## v0.9.5 — 2026-08-07

### 修正：方解石笋（grass_5 / grass_6）不再作为植被

用户反馈"方解石笋不是植被"。`grass_5` / `grass_6`（显示名"方解石笋"）此前按草地植被注册：被加入 `minecraft:small_flowers` 花标签、使用草音效、仅剪刀采集掉落自身。已改为**石质装饰方块**：

*   **标签**：从 `minecraft:small_flowers`（花）标签移除（连带自动脱离 `minecraft:flowers`、`pasterdream:swayable_plants` 摇摆植物标签）；新增 `minecraft:mineable/pickaxe`（镐挖掘）
*   **工具**：镐挖掘 + `requiresCorrectToolForDrops`——空手/其他工具破坏不掉落，用镐破坏掉落自身
*   **破坏音效**：草音效 `SoundType.GRASS` → 方解石音效 `SoundType.CALCITE`（与原模组 `Grass5Block`/`Grass6Block` 一致）
*   **实现**：新建 `CalciteSpikeBlock`（`PasterDream/.../block/CalciteSpikeBlock.java`，继承 `Block`、脱离 `FlowerBlock` 植被逻辑，`getDrops` 覆写为"镐挖掉自身"）；`grass_5`/`grass_6` 从 `GRASSES_SINGLE` 批量注册拆出单独注册，属性复制 `Blocks.CALCITE`；`PDBlockTagProvider` 补充镐挖掘标签
*   **验证**：`:PasterDream:compileJava` 通过

---


### 修复：过场动画 NPE（ClientCameraEntity 继承 LivingEntity 属性缺失）

用户日志定位：过场启动失败 `java.lang.NullPointerException: Cannot invoke "...AttributeSupplier.getValue(Holder)" because "this.supplier" is null`。

*   **根因**：`ClientCameraEntity` 原继承 `LivingEntity`，而 `LivingEntity.tick()` 访问属性系统（`AttributeMap.supplier`）。`client_camera` 实体类型未注册任何属性（`DefaultAttributes` 中没有），导致 `supplier == null` → NPE → 过场启动失败。
*   **修复**：`ClientCameraEntity` 改为继承 **`Entity`**（非 `LivingEntity`）——相机实体只需位置/旋转，不需要属性、装备、药水效果等复杂逻辑。实现 `Entity` 抽象方法（`readAdditionalSaveData`/`addAdditionalSaveData`/`defineSynchedData` 空实现），覆写 `push(Vec3)`/`push(Entity)` 为空（防物理推动）。
*   **验证**：`:PasterDreamAPI:compileJava` / `:PasterDream:compileJava` 通过。等待运行验证过场相机环绕。

---


### 修复：过场动画无效（相机实体 + 启动健壮性）

用户反馈过场动画无效（打击帧/屏幕特效/粒子发射器均正常，仅过场不工作）。排查修复：

*   **ClientCameraEntity 对齐 FDLib**：补上相机实体防物理干扰的关键覆写——`push` 系列空实现（防止相机实体被推动/推挤）、`hasEffect/getEffect` 转发到本地玩家（避免空 level 的 NPE）、`tick` 调 `super.tick()` 保持实体数据同步
*   **过场启动健壮性**：`CutsceneCameraHandler.start()` 加 try-catch + 日志（`[CutsceneCameraHandler] 过场开始/启动失败`），启动失败不再静默
*   **相机接管确保**：`tick()` 每帧 `ensureCameraEntity()`——vanilla 可能重置 `cameraEntity`，现在每 tick 确保相机实体接管
*   **实体类型降级**：`resolveCameraType` 优先用已注册的 `client_camera` 实体类型；若注册失败或取值异常（极端场景）则现场构建 `EntityType` 兜底，保证过场可用
*   **过场包日志**：`PDClientVfx.handleStartCutscene` 输出 `[PDClientVfx] 收到过场包: N ticks, N 个相机点`，便于确认网络包是否送达
*   **验证**：`:PasterDreamAPI:compileJava` / `:PasterDream:compileJava` 通过

---


### 修复：特效系统"看不到"问题（shader 资源 + 调试命令）

针对用户反馈"原版下看不到特效"进行排查修复：

*   **根因：ImpactFrame 后处理链无法加载**。`shaders/program/impact_frame.json` 的 `"vertex"` 误写为 `"blit_screen"`——vanilla 1.21.1 **不存在** `blit_screen` program（只有 `blit`），导致 PostChain 加载失败，被 `PostShaderManager` 静默捕获并从加载器表移除 → ImpactFrame 灰闪永不显示（且无日志）。已修正：
    * program json：`"vertex": "blit"`（vanilla 存在的 program）+ 补全 `blend` 块、samplers/uniforms 对齐 vanilla 格式
    * post json：pass 顺序改为「impact_frame 处理 main→swap，blit 拷贝回 main」（与已验证可用的 FDLib 结构一致）
*   **新增特效调试命令** `/pasterdream vfx <impact|screen|particle|cutscene>`：无需进 BOSS 战即可手动触发各特效系统，便于运行验证。注册于 `PasterDreamMod` 构造器（`PDVfxCommand`）。
*   **shader 加载失败日志**：`PostShaderManager.getChain` 加载失败时输出 `[PostShaderManager] 后处理链加载失败: ...`，不再静默。
*   **验证**：`:PasterDreamAPI:compileJava` / `:PasterDream:compileJava` 通过。

---


### 新增：PasterDreamAPI 特效系统（借鉴 Qliphoth Awakening / FDLib 设计思路独立实现）

借鉴开源模组 FDLib（作者 FINDERFEED，Qliphoth Awakening 前置库）的 BOSS 战特效架构，在 PasterDreamAPI 内**从零重写**（其许可证禁止复制源码，仅借鉴思路）四个特效子系统，供主模 BOSS 战与附属模组使用：

*   **后处理管线骨架**：API `api/client/effect/post/`（`PostShaderEvent` Level/Screen 事件 + `PostShaderManager` PostChain 惰性注册表）；主模 `GameRendererMixin` 双注入分发 + `PDShaderBootstrap` 注册加载器 + `shaders/post/impact_frame.json` 四件资源（全新建立仓库首个 shader 后处理管线）
*   **屏幕特效 ScreenEffect**：API `api/effect/screen/`（`ScreenEffectType` 注册表 + `ScreenEffectAPI` Facade + `ScreenColorData`）+ 客户端 `api/client/effect/screen/`（`ScreenEffect` 三阶段生命周期 + `ScreenEffectOverlay` GUI 层）；主模 `PDHudLayers` 注册 screen_effect 层，支持渐入/持续/渐出
*   **打击帧 ImpactFrame**：API `api/effect/impact/`（`ImpactFrame` record + `ImpactFrameAPI` Facade）+ 客户端 `ImpactFramesHandler`（队列 + uniform 控制灰闪）
*   **过场动画 Cutscene**：API `api/effect/cutscene/`（`CutsceneData`/`CameraPos`/`CurveType`/`EasingType`/`CutsceneAPI` 含 `ENTITY_REGISTRY`）+ 客户端 `api/client/effect/cutscene/`（`ClientCameraEntity`/`CutsceneExecutor`/`CutsceneCameraHandler`，CatmullRom/Linear 路径插值）；主模 `LocalPlayerMixin`/`KeyboardInputMixin` + 相机/输入/HUD 接管事件
*   **粒子发射器 ParticleEmitter**：API `api/effect/particle/`（`EmitterProcessor` 注册表 + `ParticleEmitterData` + 内置 CircleSpawn/BoundToEntity/Empty 处理器）+ 客户端 `ParticleEmitter`/`ParticleEmitterHandler`
*   **网络**：5 个 S2C payload（`ScreenEffectPayload`/`ImpactFramesPayload`/`StartCutscenePayload`/`StopCutscenePayload`/`ParticleEmitterPayload`）注册进主模 `PDNetwork`，经 `PDClientVfx` 反射落地（专用服安全）
*   **亚伦柯斯 BOSS 集成示范**：召唤时播放环绕过场（`PDArenaBossManager.triggerBossSummon`）、受击/狂暴触发打击帧 + 灵魂粒子发射器（`AaroncosHandEntity.hurt`/`tryBloodLock`）
*   **验证**：`:PasterDreamAPI:compileJava` / `:PasterDream:compileJava` 通过（无警告）

---

### 修复：特效系统崩溃隐患（专用服 ClassNotFound + 网络解码健壮性）

针对特效系统进行崩溃审查与加固，消除以下运行时崩溃风险：

*   **专用服 ClassNotFound**：`ScreenEffectAPI.sendScreenEffect` 原接收带客户端工厂的 `ScreenEffectType<D, S>`，服务端触发会加载客户端类（`ScreenColorEffect` 引用 `GuiGraphics`/`RenderSystem`）→ 专用服崩溃。已重构：
    * `ScreenEffectType` 只保留服务端安全的 id + 数据编解码（去掉客户端工厂泛型）
    * 新增客户端 `ScreenEffectFactoryRegistry`（id → 工厂），客户端反查创建实例
    * `ScreenColorData.TYPE` 为通用元数据（服务端安全），`ScreenColorEffect` 为客户端实现
    * `ScreenEffectAPI.sendScreenEffect` 用通用 `ScreenEffectType`，不引用客户端类
*   **网络解码崩溃**：未知特效类型/粒子类型时原抛异常 → 客户端断连。已加固：
    * `ScreenEffectPayload` 改为「typeId + 数据字节(长度前缀) + 时间」，未知类型解码返回 `data=null`，handler 静默跳过
    * `ParticleEmitterData` 粒子列表改为「id + 长度 + 数据」，未知粒子类型安全跳过
    * `EmitterProcessor.STREAM_CODEC` 未知处理器类型降级为 `EmptyEmitterProcessor` 而非抛异常
*   **过场实体获取**：`CutsceneCameraHandler.start()` 原经 `ENTITY_REGISTRY.getEntries()` 反射遍历查实体类型（运行时脆弱），改为 `CutsceneAPI.CLIENT_CAMERA`（DeferredHolder）直接查询
*   **shader 加载防御**：`PDShaderBootstrap.createImpactFrameChain` 增加主渲染目标 null 检查，避免渲染前触发 NPE
*   **验证**：`:PasterDreamAPI:compileJava` / `:PasterDream:compileJava` 通过

---


### 改动：注册园艺钳到社区剪刀 tag（c:shears / forge:shears）

*   **背景**：让我们的园艺钳（`pasterdream:pliers`）能被其他模组植被的「剪刀判定」识别，需要遵循社区通用 tag 约定，而非只依赖模组内 `pasterdream:shears`。
*   **改动**：
    * 新建 `data/c/tags/item/shears.json`（`c:shears`）：`minecraft:shears` + `pasterdream:pliers`
    * 新建 `data/forge/tags/item/shears.json`（`forge:shears`，兼容旧约定）：`minecraft:shears` + `pasterdream:pliers`
    * 配合 `PliersItem extends ShearsItem` 的 Java 层 `instanceof ShearsItem` 天然兼容，覆盖三类判定方式中的两类
*   **说明**：1.21 生态目前尚无官方 `c:shears` / `forge:shears` 约定（Fabric convention tags 与 NeoForge `Tags.Items` 均无 SHEARS 常量），本改动为「播种」性质——若其他模组遵循此 tag 约定，即可识别园艺钳；硬编码 `"items": "minecraft:shears"` 的模组仍需原版剪刀，无法通过 tag 解决。
*   **验证**：`:PasterDream:compileJava` 通过；`verify_resource_closure.py` 资源闭包 PASS。

---


### 改动：望远镜观星十字高亮改用 golden_particle 金色十字星芒纹理

*   **背景**：使用望远镜对准星座星点时，星中心会显示一个由线段绘制的十字高亮（`ConstellationSkyContent.renderAimCrosshairs`）。
*   **改动**：
    * 十字高亮从「线段三层光晕」改为 **golden_particle 金色十字星芒纹理** 广告牌渲染，三层叠加保留发光渐变（外 0.28 / 中 0.60 / 核 1.0 alpha）
    * 使用 `pasterdream:textures/particle/golden_particle_{1,2,3}` 三帧循环动画（每 10 tick 一帧，与粒子定义 mcmeta `frametime=10` 对齐），呈现金色闪烁
    * 尺寸匹配：新增 `CROSS_TEXTURE_FILL`（十字臂占 16px 图约 13px）把原「臂长」换算为广告牌半边长，使十字视觉跨度与原版一致；保留闪烁伸缩（`CROSS_ARM_BASE` / `CROSS_ARM_PULSE`）
    * **粒子式淡入淡出**：新增 `smoothstep` 缓动 + `CROSS_FADE_MIN_SCALE`（55%）——淡入/淡出时透明度渐入渐出且尺寸从小到大/从大到小（淡入轻盈亮起、消散拖尾缩小），替代原线性 `aimState` 直接乘透明度
    * 移除不再使用的旧线段常量：`CROSS_COLOR` / `CROSS_CORE_COLOR` / `CROSS_OUTER_WIDTH` / `CROSS_MID_WIDTH` / `CROSS_CORE_WIDTH`
*   **修复**：纹理路径需带 `.png` 后缀（与星域/行星一致，如 `pasterdream:textures/particle/golden_particle_1.png`），否则加载不到 → 显示紫黑块（missing texture）
*   **验证**：`:PasterDream:compileJava` 通过。

---


### 改动：新增 `pasterdream:shears` item tag 统一剪刀类工具判定

*   **背景**：园艺钳（`pasterdream:pliers`，继承 `ShearsItem`）应与原版剪刀功能一致，但此前各处判定方式不统一——有的用 `instanceof ShearsItem`（已涵盖园艺钳），有的用 `tool.is(Items.SHEARS)`（不含园艺钳），战利品表 JSON 则硬编码 `"items": "minecraft:shears"`。
*   **改动**：
    * 新建 item tag `data/pasterdream/tags/item/shears.json`：包含 `minecraft:shears` + `pasterdream:pliers`，后续新增同类工具只需追加此文件
    * 新增 `PDItemTags.SHEARS` 常量（`registry/PDItemTags.java`，仿 `PDBlockTags`）
    * Java 侧统一改用 `tool.is(PDItemTags.SHEARS)`：
        * `DyedreamFlowerBlock` / `DyedreamDoublePlantBlock` / `BlazeFlowerBlock`（原 `instanceof ShearsItem`）
        * `DyedreamLeavesBlock` / `DyedreamSeagrassBlock`（原 `tool.is(Items.SHEARS)`）
    * 战利品表 JSON 统一改用 `"items": "#pasterdream:shears"`：`dyedream_leaves` / `dyedream_glowing_leaves` / `dyedream_worldtree_leaves` / `dyedream_seagrass` / `flower_6`
*   **验证**：`:PasterDream:compileJava` 通过，相关 JSON 语法校验通过。

---


### 修复：染梦海草（dyedream_seagrass）任何工具都掉落自身

*   **背景**：`DyedreamSeagrassBlock.getDrops()` 无条件返回自身（`List.of(new ItemStack(this))`），导致空手或任意工具破坏都会掉落本体，不符合原版海草「需剪刀/精准采集才掉落」的规则。
*   **修复**：改为**仅剪刀或精准采集时掉落自身**，其它工具/空手不掉落任何物品：
    * `tool.is(Items.SHEARS)` 判断剪刀
    * 通过 `Enchantments.SILK_TOUCH` + `tool.getEnchantmentLevel(...) > 0` 判断精准采集
    * 同步更新 `dyedream_seagrass.json` 战利品表：剪刀或精准采集（1.21.1 新格式 `predicates."minecraft:enchantments"`）→ 掉本体
*   **验证**：`:PasterDream:compileJava` 通过。

---

### 移除：aurora_glow 极光粒子及其全部调用

*   删除粒子注册 `PDParticles.AURORA_GLOW`（`registry/PDParticles.java`）
*   删除客户端 Provider 注册（`client/ClientSetup.java`）与粒子类 `client/particle/AuroraGlowParticle.java`
*   `DyedreamEnvironmentRenderer` 移除 `biome_dyedream_3`（暖色海岸/海洋）与 `biome_dyedream_deep_ocean`（晶莹深海）的极光粒子分支与 `spawnAuroraGlow` 方法；这两个群系改回显式分支，改用**星尘**粒子且密度对齐原极光概率（0.003 / 0.004）
*   删除资源文件：`particles/aurora_glow.json`、`textures/particle/aurora_glow.png`、`aurora_glow.png.mcmeta`
*   **验证**：`:PasterDream:compileJava` 通过。

---


### 修复：亚伦柯斯左右手 BOSS 技能动画在战斗中不播放

*   **排查结论**：动画资源文件**齐全且有效**（`animations/entity/aaroncos_{left,right}hand_0.animation.json` 含全部 `skill_*` 动画、`geo`/`textures` 均存在、JSON 校验通过），问题出在动画触发逻辑。
*   **根因 1（动画被覆盖）**：`AaroncosHandEntity.movementPredicate` 用 `this.animationprocedure`（本地字段，只在服务端 `setAnimation` 赋值，**客户端永远为 "empty"**）判断是否播基础动画，导致客户端 movement 控制器始终播放 fly/idle，且注册顺序在 procedure 控制器之后 → **逐帧覆盖技能动画的骨骼变换**。正常实体（Terrorbeak/WindKnight）用的是 `getSyncedAnimation()`（同步 entity data）。
*   **根因 2（同名技能不重播）**：`GeckoLibMobEntity.setAnimation` 对同名动画（右手 3 连 `skill_magicball`、左手 3 连 `skill_sprint`）不会标记脏数据 → 客户端收不到重新同步 → 第 2、3 次同名技能动画不播放。
*   **修复**：
    * `AaroncosHandEntity.movementPredicate`（`entity/mob/AaroncosHandEntity.java:500`）：改用 `getSyncedAnimation()`，技能动画激活时 movement 控制器返回 `STOP`，不再覆盖 procedure 技能动画
    * `GeckoLibMobEntity.setAnimation`（`PasterDreamAPI/.../api/entity/base/GeckoLibMobEntity.java:60`）：同名动画时先写 `"empty"` 强制脏标记再写入目标动画，确保每次触发都重新同步
    * `TerraswordWaveEntity.movementPredicate`（`entity/mob/TerraswordWaveEntity.java:215`）：同根因的 `animationprocedure` 字段改为 `getSyncedAnimation()`（"1"/"2"/"3" 战技段位动画同受影响）
*   **验证**：`:PasterDream:compileJava` / `:PasterDreamAPI:compileJava` 通过。

### 修复：BOSS 技能方向指向敌人而非最近玩家

*   `AaroncosRighthand0Entity.executeMagicballSkill`（35 tick 瞄准）与 `AaroncosLefthand0Entity.executeSprintSkill`（16 tick 瞄准）原先用 `getNearestPlayer` 锁定方向，现改为锁定 **BOSS 当前攻击目标 `getTarget()`（敌人）**；无目标时不强行转向（保持当前朝向）。
*   仅影响技能朝向逻辑，弹幕实体 `ShadowMagicballEntity` 的追踪行为不变。

---


### 改动：右手调音图腾改为终结技 + 图腾数值重做

*   **终结技释放条件**（`AaroncosRighthand0Entity.tryTriggerTuneTotemFinale`）：
    *   双 BOSS（左右手）血量均低于各自最大血量的 **1/5**（各 500HP → 100HP）
    *   或另一只手（左手）已死亡
    *   全程 **有且只释放一次**（`AaroncosTuneTotemFinale` NBT 标记）
*   **受击不再触发**：`onHurtTriggerSkill()` 改为空实现，调音图腾不再作为受击反击技。
*   **释放效果**：播放 `skill_tunetotem` 动画 + 向 64 格内玩家广播高危提示
    （`message.pasterdream.shadow_tune.finale_warning`，告知伤害极高、需尽快打掉图腾）+ 召唤图腾。
*   **图腾数值**（`ShadowTuneTotemEntity`）：
    *   **生命值 40 → 50**
    *   爆炸改为**半径 50 格内 250 点魔法伤害**的巨型爆炸（原为 99 格内逐目标 5 格爆炸 + 暗影效果）
    *   图腾被玩家打掉后**立即停止倒计时**，不再爆炸（规避手段）
    *   倒计时提示文案更新：`charging` / `detonation_soon` 明确提示玩家摧毁图腾
*   **验证**：`:PasterDream:compileJava` / `:PasterDreamAPI:compileJava` 通过，语言文件 JSON 校验通过。

### 改动：BOSS 狂暴阈值从固定 100 血改为本体血量 1/3

*   `AaroncosHandEntity.tryBloodLock`（鲜血锁链/狂暴）：触发条件由 `getHealth() > 100` 改为
    `getHealth() > getMaxHealth() / 3f`，即血量降至**最大血量的 1/3**（500HP → 约 166HP）时触发狂暴。

---


### 修复：右手 BOSS 飞弹出生即炸自身（未飞出去）

*   **根因**：`ShadowMagicballEntity.detectAndTriggerExplosion()` 检测飞弹周围 1.5 格内所有 `LivingEntity`，
    而飞弹生成位置就在 BOSS 前方 1.5 格处（`executeMagicballSkill` 中 `boss + look * 1.5`），
    飞弹第一个 tick 就把 BOSS 本体误判为"目标"触发爆炸 → 原地爆炸、飞不出去。
*   **修复**（`entity/projectile/ShadowMagicballEntity.java`）：
    *   碰撞检测排除**发射者**（`getOwner()`，生成时 `magicball.setOwner(boss)`）与 **`pasterdream:shadow_mob` 暗影系标签**（不再误炸 BOSS / 暗影召唤物）
    *   `trackPlayer()` 改为只追踪非创造玩家（`getNearestPlayer(x, y, z, 64, predicate)`），仅剩创造玩家时保持直线飞行

### 改动：BOSS 及其技能不再攻击创造模式玩家

*   **AI 目标**（`AaroncosHandEntity.registerGoals`）：`NearestAttackableTargetGoal` 增加 predicate
    `!(target instanceof Player && isCreative())`，创造玩家不会被锁定为目标。
*   **基类统一判定**：新增 `isAttackablePlayer(Entity)`（存活且非创造），并应用到全部玩家遍历：
    `hurtNearbyPlayers` / `hurtNearbyLivingWithConfusion` / `pushNearbyPlayers` / `tryBloodLock`（狂暴）。
*   **左右手技能**：
    *   右手 `executeVortexSkill`（涡流）：只影响可攻击玩家，创造玩家不再受击退/缓慢/脚下漩涡
    *   左手 `triggerSwordSkill`（剑雨）初始混乱与多段 AoE：排除创造玩家
*   **暗影漩涡方块**（`ShadowVortexBlockEntity`）：创造玩家**完全不受影响**（跳过伤害与黑暗/混乱/缓慢负面效果，
    原仅跳过伤害仍会吃效果）
*   **调音图腾爆炸**（`ShadowTuneTotemEntity`）：250 魔法伤害排除创造玩家（语义一致）
*   **验证**：`:PasterDream:compileJava` 通过。

---


### 修复：终结技释放条件强制右手血量 < 1/3 + 边界补释放

*   `AaroncosRighthand0Entity.tryTriggerTuneTotemFinale` 释放条件调整：
    *   **强制前置**：释放者（右手）自身血量必须低于最大血量的 **1/3**（500HP → 约 166HP），否则不释放
    *   触发条件：左手已死亡，或左手血量低于其最大血量的 1/5
    *   **边界修复**：条件不满足时不再写任何标记，避免「左手先死但右手血量未达标」时只评估一次就永久放弃——右手血量降至 1/3 后仍会重新评估并正常释放终结技

### 修复：图腾爆炸粒子范围与 50 格伤害范围对齐

*   `ShadowTuneTotemEntity` 爆炸时新增**多层同心圆环扩散粒子**（10/20/30/40/50 格半径，每环 24 个爆炸粒子），
    让 50 格内的玩家都能直观看到爆炸波及范围，与 250 魔法伤害的 50 格半径一致。

### 修复：终结技玩家提示合并为一行

*   右手释放终结技时已广播完整的 `finale_warning`（含高伤害警告 + 打图腾指令）；
    图腾生成时不再重复广播 `charging`，避免两条动作栏提示在 1 秒内互相覆盖、玩家看不清。
    `detonation_soon`（爆炸前 15s 警告）保留。
*   **验证**：`:PasterDream:compileJava` 通过。

---


### 修复：BOSS 战 BGM（亚伦柯斯之触）未播放

*   **根因**：`PDArenaEvents.playBossMusic()` 方法已实现但**从未被调用**；且 `sounds.json` 中
    `aaroncos_music` 的 `"stream": false`（项目内其它音乐条目均为 `stream: true`），2.4MB 音乐非流式加载存在播放异常风险。
*   **修复**：
    *   `PDArenaEvents.spawnAaroncosBosses`（BOSS 召唤统一入口）在召唤音效后调用 `playBossMusic(arenaLevel)`，
        BOSS 战斗开始即播放 BGM
    *   `sounds.json` 中 `aaroncos_music` 的 `"stream": false` → `"stream": true`，与其他音乐条目一致，流式加载长音乐
*   **验证**：`:PasterDream:compileJava` 通过，`sounds.json` JSON 校验通过。

---


### 修复：BOSS 飞弹（ShadowMagicball）trackPlayer 不追踪玩家

*   **根因**：`trackPlayer()` 使用 `level().getNearestPlayer(x, y, z, 64.0, predicate)`——该方法内部基于
    `TargetingConditions.forCombat().range(64.0)` 做**视线（line-of-sight）检查**。竞技场是封闭结构，
    飞弹从两侧 BOSS 位置发射，到玩家的视线经常被结构/地面阻挡 → 返回 `null` → `trackPlayer` 完全不执行，
    飞弹只靠初始速度直线飞行、不追踪。
*   **修复**（`entity/projectile/ShadowMagicballEntity.java`）：
    *   `trackPlayer()` 改为手动遍历 `level().players()` 取最近**存活非创造**玩家，**不依赖视线**，确保竞技场内必定能追踪到目标
    *   平滑转向：7 成保留原速方向 + 3 成偏转目标，保持恒速 3.0；无初速时防御性直接朝目标
*   **验证**：逻辑类型正确（`getEyePosition` / `distanceToSqr` / `players()` 均为项目既有 API）。
    ⚠️ 当前全量 `compileJava` 被**他人进行中的包重构**阻塞（`registry.blocks`、`attachment`、`api.effect` 等
    大量半完成文件导致"程序包不存在"连锁报错），需重构完成后复验。

---


### 修复：BOSS 飞弹仍不移动 —— Projectile 基类 tick 不自动移动（核心根因）

*   **真正的根因**：`ShadowMagicballEntity extends GeckoLibProjectileEntity extends Projectile`，
    而 **`Projectile.tick()` 只调用 `baseTick()`，不会自动执行 `move(getDeltaMovement())`**——弹道移动与
    方块碰撞检测逻辑在 `AbstractArrow` / `AbstractHurtingProjectile` / `ThrowableProjectile` 子类中实现。
    项目内正常工作的投射物（`BoneWingFireBallProjectileEntity`、`SpellProjectileEntity`）都继承 `AbstractArrow`，
    故能自动飞行；本飞弹直接继承 `Projectile` 从未移动，即便设置了 `deltaMovement`。
*   **修复**（`entity/projectile/ShadowMagicballEntity.java`）：
    *   `tick()` 服务端新增 `moveProjectile()`：每 tick 手动 `level().clip(ClipContext)` 检测方块碰撞
        （命中 → `onHit` → 爆炸），未命中则 `setPos(position + deltaMovement)` 推进位置，
        并按速度方向更新 `yRot` / `xRot` 供 fly 动画对齐
    *   修正 `ClipContext` import：`net.minecraft.world.level.ClipContext`（1.21.1 实际包名）
*   **验证**：`:PasterDream:compileJava` 过滤本文件无编译错误（全量编译仍被他人重构阻塞）。

---


### 修复：BOSS 大部分时间不面向敌人 → 技能空位

*   **技术根因**：技能发射点用 `this.lookAt(target, 360, 360)` → 内部是 `Mob.getLookControl().setLookAt()`
    （`Mob.lookAt(Entity, float, float)` 委托给 LookControl），**只更新 `yHeadRot`/`xRot`，不更新 `yRot`**；
    而 `getLookAngle()`（用于计算魔法弹/冲刺发射方向）基于 **`yRot`（身体朝向）**。
    结果 BOSS 头转向目标了，但**发射方向仍用旧身体朝向**，技能打空。
*   **修复**（`AaroncosHandEntity` / `AaroncosRighthand0Entity` / `AaroncosLefthand0Entity`）：
    *   基类 `aiStep()`：非召唤且有目标时，每 tick `getLookControl().setLookAt(target, 30, 30)` 平滑转头，
        并 `setYRot(getYHeadRot())` + `setYBodyRot(getYHeadRot())` 同步身体朝向 → BOSS 持续面对敌人
    *   技能发射点（右手魔法弹 35t、左手冲刺 16t）：`lookAt` 后立即 `setYRot(getYHeadRot())` 同步，
        确保发射/冲锋瞬间方向绝对对准敌人

### 修复：退出重进世界后 BOSS 变傻（技能系统卡死）

*   **根因**：技能中间状态（`AaroncosSkill` 及各计数器）通过 NBT 持久化，但**延迟任务队列
    `pendingTasks` 不持久化**。若在技能释放中（`AaroncosSkill=1`）保存并重进，加载后 `skill` 卡死，
    且没有 queueTask 来解锁 → `tickSkillCycle` 永久 return，BOSS 不再释放任何技能。
*   **修复**（`AaroncosHandEntity.baseTick`）：首次初始化块（实体实例化后首 tick，含 chunk 重载）清零
    技能中间状态 `AaroncosSkill` / `AaroncosMagicball` / `AaroncosVortex` / `AaroncosSprint` / `AaroncosHit` /
    `AaroncosSword` 并清空 `pendingTasks`；**仅保留一次性标记**（`AaroncosBloodLock` / `AaroncosTuneTotemFinale`）。
*   **验证**：`:PasterDream:compileJava` 过滤三个 BOSS 文件无编译错误（全量编译仍被他人重构阻塞）。



