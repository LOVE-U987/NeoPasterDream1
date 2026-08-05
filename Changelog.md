# PasterDream Changelog

---

## v0.9.3 — 2026-08-05

### 重构：破风骑士祭坛 5 方块合并为单方块（STAGE 属性 + FACING 朝向）

*   **改动**：`wind_knight_spawnblock_0..4` 五个独立方块/物品/方块实体合并为单一 `wind_knight_spawnblock`
*   **机制**：拼装样式改由方块数据 `STAGE`(0..4) 决定——右键交互不再“每搭建一次替换方块”，而是写入 `STAGE` 值切换样式；阶段推进逻辑与原版一致（风行者水晶→1、凝风铁×3→2/3/4、闪电法术→86t 召唤风之骑士+四雷云→回 stage 0）
*   **朝向**：新增 `FACING` 水平朝向，`getStateForPlacement` 取玩家朝向反向，GeckoLib 渲染自动按朝向旋转；实现 `rotate`/`mirror`
*   **渲染**：`WindKnightSpawnblockModel` 按 STAGE 动态切换 `geo/block/wind_knight_spawnblock_N.geo.json`；纹理/动画统一单文件；BE 统一为 `WIND_KNIGHT_SPAWNBLOCK`
*   **资源**：blockstates 合并（stage 变体）、loot/lang 合并；删除 `geo/` 根与 `animations/` 根重复文件；结构 `lost_windknight_ruins.nbt` 内旧 ID 改写为新 ID
*   **追踪**：`pd_porting_manifest.json`（renames `_0` + excluded `_1.._4`）、`tag_audit.json`、VERIFY hooks 同步
*   **注意**：旧档已放置的 `wind_knight_spawnblock_0..4` 会因 ID 合并消失，需重新放置

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

---

## v0.9.2 — 2026-08-03

### 高层摘要（TL;DR）

*   **影响范围：** 中 - 新增熔梦（Meltdream）工具体系、首次登录指南书、Patchouli 1.21 兼容修复，以及开发环境从 TRAE 迁移到 VS Code
*   **核心变更：**
    *   ⚒️ **熔梦系统上线**：熔梦能量 0 戒指与 4 种熔梦工具（镐/斧/铲/锄），带独特「熔梦能量修复」机制
    *   📖 **首次登录自动发放多蕾米指南书**（Patchouli 可选依赖，新旧存档均支持）
    *   🔧 **Patchouli 1.21 手册兼容**：修正指南合成配方，恢复改名错拷贴图
    *   🏗️ **开发环境迁移**：移除 TRAE 环境，资源迁移到 VS Code（skills / 工具脚本 / 文档归档）
    *   🛠️ **构建与规范**：修复 emoji 导致的构建失败，新增分支管理规范与全库审查文档

---

### ⚒️ 3.1 熔梦系统（Meltdream）

**核心变更：** 将熔梦相关物品注册收口到主模组命名空间，并引入独特的「熔梦能量修复」机制。

| 变更 | 说明 |
|------|------|
| 熔梦能量 0 戒指 | 注册到主模组命名空间，无论是否加载 `PasterDreamMeltDream` 模块均可用（`MeltdreamEnergy0RingItem`） |
| 熔梦工具系列 | 新增熔梦镐/斧/铲/锄 4 种工具（`MeltdreamPickaxeItem` 等），采用熔梦能量进行独特修复（`MeltdreamToolHelper`） |
| 工具提示更新 | 熔梦物品 tooltip 同步说明新修复机制（中英文语言文件） |
| 物品动画 | 为染梦剑、影之剑、熔金真剑、尖啸波等物品新增 `.mcmeta` 动画帧 |
| 模块清理 | 移除 `PasterDreamMeltDream` 模块中的冗余注册，统一由主模组管理 |
| 戒指标签 | `curios/tags/item/ring.json` 更新，纳入熔梦能量 0 戒指 |

#### 熔梦锻造配方修正

| 变更 | 说明 |
|------|------|
| base/addition 槽位纠正 | 将 dyedream 工具放入 `base`、`meltdream_crystal_0` 放入 `addition`，修复 4 个熔梦升级锻造配方（镐/斧/铲/锄）槽位颠倒的问题 |

---

### 📖 3.2 首次登录指南书

**核心变更：** 玩家首次进入世界时自动获得多蕾米指南书。

| 变更 | 说明 |
|------|------|
| 自动发放 | `PlayerDataEvents.onPlayerLoggedIn` 新增 `giveGuideBookIfNeeded`，通过 `PatchouliAPI` 发放 `pasterdream:doremys_guidebook` |
| 新旧存档支持 | 新档与已有存档首次登录均会触发 |
| 防重复发放 | 持久化 NBT 标记 `pasterdream.guide_book_given` |
| 可选依赖 | Patchouli 声明为 `compileOnly`，未安装时静默跳过（与 JEI 可选依赖模式一致） |

---

### 🔧 3.3 Patchouli 1.21 手册兼容

| 变更 | 说明 |
|------|------|
| 合成配方 | 帕斯特指南合成改为 `guide_book` + `patchouli:book` 组件，声明可选依赖 |
| 贴图恢复 | 从原版找回武器 / 密实冰 / 细影石砖 / 平滑石英贴图，并修正对应模型引用（细影石砖系列楼梯/台阶/墙等） |

---

### 🏗️ 3.4 开发环境迁移（TRAE → VS Code）

| 变更 | 说明 |
|------|------|
| 删除 `.trae/` | 移除 42 个规则/skills/工具/specs/报告文档文件 |
| skills 迁移 | 8 个项目 skills 迁移至 `.github/skills/`（ItemAPI、BlockDrops、EffectAPI、EntityAPI、ModDev、ParticleAPI、RuinAPI、WorldDecoration） |
| 工具脚本迁移 | 20 个脚本迁移至 `tools/`，同步更新 `build.gradle`、`.run/*.run.xml` 引用 |
| 文档归档 | docs 迁移至 `docs/archive/` |
| AGENTS.md | 合并 TRAE 项目规则 + Git 提交信息规范 |
| .gitignore | 增加 `__pycache__/` 与 `*.py[cod]`，清理已跟踪的编译缓存 |

---

### 🛠️ 3.5 构建与文档规范

| 变更 | 说明 |
|------|------|
| 构建修复 | 修复特定情况下 emoji 导致的构建失败（`wuyu_doll` 模型与 `mineable/hoe` 标签） |
| AGENTS.md | 新增分支管理规范（禁止直接提交 main、按「用户名/主题」命名分支） |
| 全库审查 | 新增 full-repository-review 文档、合并 CHANGELOG 与修复对比表（fix-comparison-table） |
| 归档清理 | 归档结案审查快照，清理无效文档与 build-errors 残渣 |

