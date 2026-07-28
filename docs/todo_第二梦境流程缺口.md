# TODO · 第二梦境流程缺口

> **类别**：活文档 · 待办 / 缺口跟踪  
> **日期**：2026-07-28  
> **来源**：对照 [`第二梦境.md`](第二梦境.md) 三段源码审查 + 原版 `libs/FixPasterDream-main`（主 agent + 三 subagent）  
> **玩法参考** → [`第二梦境.md`](第二梦境.md)  
> **前置入口** → [`todo_暮影之笼流程缺口.md`](todo_暮影之笼流程缺口.md)（据点生成 / hide_7 / 返程 spawn）  
> **功能总览** → [`功能状态.md`](功能状态.md)

---

## 0. 一句话

**灯影内主干（床进维、研究/笔记、地牢生成与冷却、无名对话、入侵→npc_3、影之抉择、竞技场放置/双手战/胜利掉落）与原版高度一致，不是空壳。**  
**端到端仍有断点**：地牢门/钥匙过程式交互未接线；竞技场缺 `d_0` 门控与 `e_0` 授予；进维 title/赠针/离开窥视 buff 未接；普通苍白骨针不能出灯影；恐怖鸟增援未调用。进灯影资格本身仍受暮影入口缺口约束。

| 维度 | 判定 |
| :--- | :--- |
| 床 / 研究 / 地牢 Portal / 无名 / 入侵 npc_3 / 抉择 | **HIGH** 一致 |
| 地牢门拆开 · 竞技场资格与收尾成就 | **LOW–MED**（交互/门控断） |
| 进维即时反馈 · 骨针 · 增援 · 冒险禁改 | **PARTIAL** |
| 生存可玩性（灯影弧） | **PARTIAL** — 调试可走主线；地牢门与 Boss 资格/成就差一手 |

---

## 1. 优先级待办

### P0 — 阻断或严重偏离主线体验

- [ ] **补暗影地牢门 / 钥匙过程式交互（原 `ShadowDungeonDoorPr0/1/2`）**
  - 原版：门可交互；上层耗 `shadow_dungeon_key` 拆门；底层门需 **`achievement_shadow_npc_5`** 才开，否则「大门紧闭不打开」；另有放置/销毁门层与音效。
  - Neo 现状：`ShadowDungeonDoorBlock` / `ShadowDungeonKeyBlock` **仅 shape + 注册/生成放置**；全仓无 `use`/钥匙绑定/`npc_5` 门检。`generateDungeon` 会放门，但玩家无法按原版打开。
  - 影响：地牢探索叙事（「下去是出口」）、底层认可、与 npc_5 后流程的衔接感。
  - 验收：持钥匙右键上层门可开并耗钥；无 npc_5 时底层门拒开并提示；有 npc_5 可开；与原版提示/音效对齐。

- [ ] **竞技场传送门检 `d_0`（或创造）**
  - 原版：`AaroncosArenaPortalsPr0` — 有 `achievement_shadow_d_0` 或创造才传送，否则「尚未完成前置进度」。
  - Neo：`AaroncosArenaPortalsBlock.entityInside` **无检**；lang 已有 `message.pasterdream.aaroncos_arena_portals.locked` 未用。
  - 验收：无 d_0 非创造踩门不传 + 提示；有 d_0/创造可进 `(0.5,70,0.5)`。

- [ ] **胜利授予 `achievement_shadow_e_0`**
  - 原版：双手死亡过程授 e_0（challenge，1000 XP，父 d_0），并常伴随移除 `shadow_spyon_buff` 等。
  - Neo：`PDArenaBossManager.triggerVictorySequence` **注释有、代码未 grant**；成就 JSON 为 impossible，靠代码授。
  - 验收：双灭后场内相关玩家拿到 e_0 toast；可选对齐移除窥视 buff。

### P1 — 行为不一致 / 体验缺口

- [ ] **进维 `LampShadowPr0` 等价：title + 配置赠针**
  - 原版：进灯影 title「灯影之下」；配置 `in lamp shadow give pale boneneedle` 为 true 时给 1 苍白骨针。
  - Neo：`PDCommonConfig.IN_LAMP_SHADOW_GIVE_PALE_BONENEEDLE` **已有、默认 false**；`PlayerDataEvents` 仅 sync San/融梦，**无 title / give**。
  - 验收：进维见 title；配置 true 时背包 +1 pale_boneneedle。

- [ ] **离开灯影挂 `shadow_spyon_buff`（原 `LampShadowPr1`）**
  - 原版：离开时若已 `npc_2` 且未 `e_0` → 挂窥视（长时），驱动主世界入侵。
  - Neo：`PDEffects` 有窥视 tick / 入侵 / calm→npc_3；**未见**「离维瞬间」显式 Pr1 授予。
  - 验收：npc_2 且未 e_0 时离开灯影获得 buff；平息仍授 npc_3。

- [ ] **`PaleBoneneedleItem` 维度与原版对齐**
  - 原版：染梦 + **灯影** + **竞技场**。
  - Neo：仅 `dyedream_world`；灯影/竞技场靠 `RootsPaleBoneneedleItem`（三围已通）。
  - 验收：灯影/竞技场右键普通苍白骨针可扣血返主（与原版一致）。

- [ ] **进竞技场冒险模式 + 禁改（guard / 图鉴语义）**
  - 原版 portals：进场 `ADVENTURE`；图鉴禁改。
  - Neo：仅缓降；入口侧未见 `setGameMode(ADVENTURE)` / `guard_block_buff`。
  - 验收：进场为冒险（或等价不可改地形）；胜利回主世界恢复生存（现有 eye 路径已 SURVIVAL）。

- [ ] **接线 `spawnTerrorbeakReinforcements`**
  - Neo：`PDArenaEvents` 内方法完整（50/100/150t 各 2 只），**无调用点**。
  - 原版：召唤序列中有恐怖鸟增援节点。
  - 验收：开战/召唤后时序刷出增援。

- [ ] **（可选）胜利收尾旁白 / 清场 / 倒计时**
  - 原版 handspawn Pr1：倒计时文案、清场、再 TP 等。
  - Neo：右键之眼即回；箱+40t 地面掉落已有。
  - 非阻断；要对齐手感再补。

### P2 — 抛光 / 文档 / 验证

- [ ] **无名对话同步范围**：Neo inflate **8**，原版约 **16**；文档写「约 8」。确认是否有意缩小，否则调回 16。
- [ ] **更新 [`第二梦境.md`](第二梦境.md) §13**：门交互未接线标为已核实 P0；与本 todo 互链；进维 title/Pr1/e_0/d_0 门控保持同步。
- [ ] **[`功能状态.md`](功能状态.md) §3**：挂「第二梦境：地牢门 Pr + 竞技场 d_0/e_0 + LampShadow 即时效果」。
- [ ] **VERIFY（可选）**：门钥匙交互、portal locked、e_0 grant、pale 三围、title/config 赠针；勿把「门方块已注册」当成「可开门」。

---

## 2. 已确认「不用当缺口重做」的部分

下列与原版 Pr / 文档主路径对齐，**优先接线缺口，勿重写**：

| 模块 | 状态 |
| :--- | :--- |
| 真影床：夜/雷 + 上 2 格笼 key + hide_9 → SAN-10 + 进维 | ✅ |
| 影床：shadow_start → a_1 + 进维；落点 (0.5, 104\|154, 0.5) + 包序列 | ✅ |
| 灯影笼返程 20t + WIN_GAME + 主世界（spawn 字面命令见暮影 todo） | ✅ |
| Roots 骨针三维度 + 标记/未标记 | ✅ |
| 研究台 studyNotes 梯度 + start 前置 | ✅ |
| Dreamnotes 10–13 → hide_11/12/14/15 | ✅ |
| brokennotes_0 → a_0；blackmetal → b_0（adv） | ✅ |
| Broken 门修复：y≤20 / 创造 / hide_14+双手影灯与黑金属 | ✅ |
| 完好地牢门：exit、cd/time≥1800、五层模板与坐标、首次 c_0 | ✅ |
| structure_block_17 外壳 + jigsaw `shadow_dungeon`（biome_shadow_0） | ✅ |
| 无名 Stage0/1/2/4/5 + 成就顺序；无直接授 npc_3；Stage2 config TP | ✅ |
| 入侵平息 shadowIntrudeCalm → npc_3 + 文案 + 去窥视 | ✅ |
| 影之抉择：npc_5∧¬d_0 → GUI；光/暗 → d_0 + talent + 物品 + 旁白 | ✅ |
| 竞技场：放置 nbt、清场、init、双手位置/召唤动画、双灭箱与 talent 分支掉落、eye 回主 | ✅ |
| 手形遗迹 jigsaw / 感染 tick | ✅ |
| 配置键 IN_LAMP… / THIRD_DIALOGUE… 已注册 | ✅（行为接线见 P1） |

**依赖暮影 todo（本文件不重复开单）**：主世界 `shadow_world_door` 自动生成、灯影 `shadow_world_spawn`、hide_7、返程裸 `spawn`。无据点则真影床首进链仍断。

---

## 3. 建议动手顺序

```text
1. 地牢门 + 钥匙 + 底层 npc_5（原 DoorPr*）     ← 地牢可玩性
2. 竞技场 portal d_0 门控 + locked 文案
3. triggerVictorySequence 授 e_0（+ 可选去 spyon）
4. LampShadow 进维 title + 配置赠针
5. 离维 npc_2∧¬e_0 → shadow_spyon_buff
6. PaleBoneneedle 扩维；spawnTerrorbeak 调用；进场 ADVENTURE/禁改
7. 文档 §13 / 功能状态 / 可选 VERIFY
8. （P2）无名范围 16、胜利倒计时旁白
```

### 3.1 调试绕过（修之前人工走弧）

```mcfunction
# 跳过暮影入口
/advancement grant @s only pasterdream:achievement_shadow_start
/execute in pasterdream:lamp_shadow_world run tp @s 0.5 104 0.5

# 研究 / 地牢前置
/advancement grant @s only pasterdream:achievement_shadow_b_0
/advancement grant @s only pasterdream:achievement_hide_14
/give @s pasterdream:blackmetal_ingot
/give @s pasterdream:shadow_light_0
/give @s pasterdream:shadow_dungeon_key

# 无名 / 抉择 / Boss
/advancement grant @s only pasterdream:achievement_shadow_npc_0
# … npc_1..5
/advancement grant @s only pasterdream:achievement_shadow_d_0
/execute in pasterdream:aaroncos_arena_world run tp @s 0.5 70 0.5
```

### 3.2 复测方向（可选套件，修好后再写死断言）

- 门：钥匙消耗 / npc_5 底层 / 无钥无成就拒绝。  
- Portal：无 d_0 不进；有 d_0 进。  
- Boss：双灭 → e_0；掉落分支 talent。  
- 进维：title；config 赠针。  
- 骨针：pale 在 lamp/arena 可用。

---

## 4. 代码锚点（修时打开）

| 用途 | 路径 |
| :--- | :--- |
| 原版地牢门 | `libs/.../ShadowDungeonDoorPr0/1/2Procedure.java` + Door*Block |
| 原版完好/破损门 | `ShadowDungeonPortalPr0/1/2` · `BrokenShadowDungeonProtalPr0` |
| 原版无名 / 进维瞬间 | `ShadowNpc0Pr0` · `LampShadowPr0` · `LampShadowPr1` |
| 原版竞技场 | `AaroncosArenaPortalsPr0` · `AaroncoshandspawnblockPr*` · `AaroncosArenaWorldPr0` |
| Neo 地牢 | `block/ShadowDungeonPortalBlock.java` · `BrokenShadowDungeonProtalBlock.java` · `ShadowDungeonDoorBlock.java` · `ShadowDungeonKeyBlock.java` |
| Neo 无名 / 入侵 | `entity/mob/ShadowNpc0Entity.java` · `registry/PDEffects.java` |
| Neo 抉择 | `block/TrueShadowBedBlock.java` · `menu/ShadowSelectEndMenu.java` |
| Neo 竞技场 | `block/AaroncosArenaPortalsBlock.java` · `registry/PDArenaEvents.java` · `PDArenaBossManager.java` · `AaroncosHandSpawnBlock.java` |
| 骨针 | `item/PaleBoneneedleItem.java` · `RootsPaleBoneneedleItem.java` |
| 进维事件 | `PlayerDataEvents`（或等价 dim change 钩子） |
| 配置 | `config/PDCommonConfig.java` |
| 成就 JSON | `data/pasterdream/advancement/achievement_shadow_*.json` |

---

## 5. 审计备注

- 三段审查结论一致：**主干移植质量高**；缺口集中在「未接线的原版副作用 / 门交互 / 资格与终局成就」，不是整段重做。
- 地牢门：审查评为 **PARTIAL→FAIL（交互）**；方块与生成 **PASS**。以「玩家能否按原版开门」为 P0，不以「方块已注册」关闭。
- 竞技场 d_0 / e_0 / 增援 / 冒险：与 [`第二梦境.md` §13](第二梦境.md) 记载一致，本次源码复核确认仍在。
- Pale 仅染梦、Roots 三围：有意或疏漏需产品确认；对照原版 **P1 扩维** 更贴原体验。
- 无名 8 vs 16：非主线阻断；改前确认是否平衡向缩小。
- 进灯影生存链仍依赖暮影 P0；本 todo 假设玩家已能进 `lamp_shadow_world`。
- 修复计划（原版逐步对照）见下文 §6（子代理核对后填入）。

---

## 6. 修复计划（原版对照 · 可施工）

> **来源**：三子代理只读对照原版 Pr + Neo 现状（2026-07-28）。  
> **原则**：只接线差异，不推倒 SavedData / Portal 生成 / 抉择 / 无名主干。  
> **动手顺序**仍以 §3 为准。

---

### 6.1 P0 · 暗影地牢门 / 钥匙（原 `ShadowDungeonDoorPr0/1/2` · `ShadowDungeonKeyPr0`）

| 项 | 内容 |
| :--- | :--- |
| 原版 | Pr0=`onPlace` 填 8 邻格；Pr1=破坏中心时 cascade `destroy(false)`；Pr2=`use`：door0 耗钥拆 9 格，door2 需 `npc_5`；KeyPr0=右键 key 块 destroy+give 物品 |
| Neo 改 | `ShadowDungeonDoorBlock` · `ShadowDungeonKeyBlock`；（可选）`ShadowDungeonKeyItem` + `PDItemsMaterials` 注册 |
| 不动 | `ShadowDungeonPortalBlock.generateDungeon` 仍只 set 中心门；`onPlace` 自然补全 |

**DoorBlock 应实现**

1. **`onPlace`**  
   - `SHADOW_DUNGEON_DOOR_0` → 同 y 平面 8 邻放 `DOOR_1`（偏移：`±1,0,±1` 与轴向，共 8，对齐原 Pr0）。  
   - `SHADOWDUNGEONDOOR_2` → x/y 平面 8 邻放 `DOOR_3`（`±1,±1,0` 等，对齐原 Pr0 else）。  
2. **`onDestroyedByPlayer`**（中心 0/2）：`destroyDoorGroup(pos, isLower)`，offsets 与上表一致，一律 `destroyBlock(..., false)`。  
3. **`useWithoutItem`**（仅 0/2；1/3 PASS）  
   - 每次先 `playSound(PDSounds.SHADOW_DOOR, BLOCKS, 1, 1)`。  
   - **下层 door0**：主手 `shadow_dungeon_key` → server `shrink(1)` + `destroyDoorGroup(lower)`；否则 actionbar「需要在本层寻找暗影地牢钥匙以打开大门」。  
   - **上层 door2**：`ServerPlayer` 且 `achievement_shadow_npc_5` → `destroyDoorGroup(upper)`；否则「大门紧闭不开」（原版文案以 Pr2 为准；若原串为「大人…」以原版字面为准）。  
4. 辅助：`hasAdvancement` 复制 Portal/Bed 现有实现；副作用均 `!isClientSide`。

**KeyBlock**

- `getDrops` → `singletonList(new ItemStack(SHADOW_DUNGEON_KEY))`（现状 emptyList 不对）。  
- `useWithoutItem`：server `destroyBlock(pos,false)` + `ItemHandlerHelper.giveItemToPlayer(key×1)`。

**可选**：`ShadowDungeonKeyItem` tooltip `§7用于打开暗影地牢下层的大门`。

**验收**

```mcfunction
/setblock ~ ~ ~ pasterdream:shadow_dungeon_door_0
/give @s pasterdream:shadow_dungeon_key
# 无钥提示；有钥拆 9 格并耗钥
/advancement grant @s only pasterdream:achievement_shadow_npc_5
/setblock ~ ~5 ~ pasterdream:shadowdungeondoor_2
# 有 npc_5 可开；撤销后拒开
/setblock ~ ~ ~ pasterdream:shadow_dungeon_key_0
# 右键得钥匙物品
```

完整：portal generate → brazier/墙 key → 各层 door0 → npc_5 → door2。

**风险**：cascade 必须 `drop=false`；双人同开靠 server shrink；fill 无 facing（与原版一致）；模板勿盖住中心门格。

---

### 6.2 P0 · 竞技场 `d_0` 门控

| 项 | 内容 |
| :--- | :--- |
| 插入点 | `AaroncosArenaPortalsBlock.entityInside`：确认 ServerPlayer、非竞技场维之后、`changeDimension` 之前 |
| 条件 | `hasAdvancement(d_0) \|\| abilities.instabuild`（原 `AaroncosArenaPortalsPr0`） |
| 失败 | `displayClientMessage`「尚未完成前置进度」或已有 lang `message.pasterdream.aaroncos_arena_portals.locked` |
| 通过 | 保持现有 `(0.5,70,0.5)` + 缓降 120t |

**验收**：无 d_0 非创造不传；有 d_0/创造可进；已在竞技场再踩无动作。CreateItem 调试旁路可保留。

---

### 6.3 P0 · 胜利授予 `e_0`

| 项 | 内容 |
| :--- | :--- |
| 插入点 | `PDArenaBossManager.triggerVictorySequence`：`setPhase(VICTORY)` 与放箱之后 |
| 逻辑 | 遍历 `arenaLevel.players()`：`ServerPlayer` 对 `achievement_shadow_e_0` 未完成则 award 全部 remaining criteria（对齐 handspawn Pr1） |
| 可选同段 | 全员 `removeEffect(SHADOW_SPYON_BUFF)` |

**验收**：双灭后在场玩家有 e_0；已完成不重复刷；烟测/进度界面可见。

---

### 6.4 P1 · 进场 ADVENTURE / `guard_block_buff`

| 项 | 内容 |
| :--- | :--- |
| 插入点 | `PDArenaEvents.onPlayerChangedDimension`（to=arena）在缓降之后；可选 portals 成功传送后补一层 |
| 推荐 | `addEffect(GUARD_BLOCK_BUFF, 长 duration, …)` — `PDEffects` onApply 已 `setGameMode(ADVENTURE)` |
| 兜底 | 显式 `setGameMode(ADVENTURE)` |
| 离场 | 现有 eye/`teleportPlayersToOverworld` 已 SURVIVAL；可 `removeEffect` 让 onRemove 再设一次 |

**验收**：进场变冒险+持 buff；回主世界生存；多玩家各自生效。

---

### 6.5 P1 · 恐怖鸟增援接线

| 项 | 内容 |
| :--- | :--- |
| 插入点 | `PDArenaEvents.spawnAaroncosBosses` **末尾**（或 `onSpawnAnimationComplete`→FIGHTING） |
| 调用 | 已有 private `spawnTerrorbeakReinforcements(arenaLevel)`（50/100/150t 南北各 2） |
| 原版 | handspawn Pr1 定时 time0 节点刷 TERRORBEAK |

**验收**：开战后勤看 50/100/150t 共 6 只；位置约 `(0,70,±12)`。

---

### 6.6 P1 · 进维 title + 配置赠针 + 离维窥视（`LampShadowPr0/Pr1`）

| 项 | 内容 |
| :--- | :--- |
| 钩子 | 新建 `registry/LampShadowEvents.onPlayerChangedDimension`（风格对齐 `PDArenaEvents`）；`PasterDreamMod` 构造器 `NeoForge.EVENT_BUS.addListener(...)` |
| 触发 | 原版：`LampShadowWorldDimension.onPlayerChangedDimension` — **to**=灯影 Pr0，**from**=灯影 Pr1。床/笼/任意 tp 均会打事件，**勿**在床内重复写 |

**to = lamp_shadow_world**

1. 立即 `ClientboundSetTitlesAnimationPacket(30, 40, 20)`（优于字面 `title` 命令）。  
2. `ServerScheduler.schedule(5, …)`：守卫 `isAlive` 且仍在灯影 → `ClientboundSetTitleTextPacket(Component.literal("灯影之下"))`。  
3. 同 delay 内若 `PDCommonConfig.IN_LAMP_SHADOW_GIVE_PALE_BONENEEDLE.get()` → `ItemHandlerHelper.giveItemToPlayer(PALE_BONENEEDLE×1)`。

**from = lamp_shadow_world**

- `hasAdvancement(npc_2) && !hasAdvancement(e_0)` → `addEffect(SHADOW_SPYON_BUFF, 32000, 0, false, false)`。  
- 与 `PDEffects.shadowIntrudeCalm`：**互补** — Pr1 只注入 buff；calm 在 `!npc_3` 时授 npc_3 并 remove；`e_0` 后不再注入。勿在 calm 里改 Pr1 条件。

**验收**：床进见 title；config true 得针；npc_2∧¬e_0 离灯影有 buff；e_0 后无；入侵平息仍 npc_3。

---

### 6.7 P1 · `PaleBoneneedleItem` 扩维

| 项 | 内容 |
| :--- | :--- |
| 文件 | `item/PaleBoneneedleItem.java` |
| 改法 | dim 判定改为与 Roots 相同三 key：`dyedream \|\| lamp_shadow \|\| aaroncos_arena`（可抽 `isPasterDreamDimension`） |
| 可选 | dim 内补粒子/音/`achievement_b_2`（贴近 Pr0）；最小改只扩 if 即可 |
| 不动 | `RootsPaleBoneneedleItem` |

**验收**：灯影/竞技场右键普通骨针可扣血返主并消耗。

---

### 6.8 P2 · 可选胜利倒计时 / 清场

- 原 handspawn Pr1：全员 e_0 + 去 spyon + 20/10/5/3/1s 文案 + ~410t 后全员 TP 主世界 SURVIVAL + 清实体；destroy spawn、放 chest。  
- Neo 现状：VICTORY + 箱 + 40t 地面掉落 + **右键之眼**离场。  
- 建议：先落地 §6.3 e_0（+去 spyon）；倒计时强制离场作后续体验项，避免与「点眼离开」双路径冲突前先定产品偏好。

---

### 6.9 P2 · 无名同步范围

- Neo `forEachNearbyPlayer` inflate **8**；原版约 **16**。  
- 改 `ShadowNpc0Entity` 一处常量即可；改前确认是否有意缩小。

---

### 6.10 改动文件总表

| 优先级 | 文件 |
| :--- | :--- |
| P0 | `block/ShadowDungeonDoorBlock.java` · `ShadowDungeonKeyBlock.java` |
| P0 | `block/AaroncosArenaPortalsBlock.java` |
| P0 | `registry/PDArenaBossManager.java` |
| P1 | `registry/PDArenaEvents.java`（增援 + 可选进场 buff） |
| P1 | **新** `registry/LampShadowEvents.java` + `PasterDreamMod` 注册 |
| P1 | `item/PaleBoneneedleItem.java` |
| 可选 | `item/ShadowDungeonKeyItem.java` · `PDItemsMaterials` · lang locked/title |
| 文档 | 本文件勾选 · [`第二梦境.md`](第二梦境.md) §13 · [`功能状态.md`](功能状态.md) §3 |

**原版对照路径（libs）**

- `procedures/ShadowDungeonDoorPr0/1/2Procedure.java` · `ShadowDungeonKeyPr0Procedure.java`  
- `AaroncosArenaPortalsPr0Procedure.java` · `AaroncoshandspawnblockPr0/Pr1Procedure.java` · `AaroncosArenaWorldPr0Procedure.java`  
- `LampShadowPr0/Pr1Procedure.java` · `PaleBoneneedlePr0Procedure.java`

**建议施工顺序（与 §3 对齐）**

```text
门+钥匙 → portal d_0 → e_0(+spyon remove)
→ LampShadowEvents(title/针/离维buff) → Pale 扩维
→ terrorbeak 调用 → 进场 GUARD/ADVENTURE
→（可选）胜利倒计时 · 无名 16 · 文档/VERIFY
```

**最小回归手测链**

1. 灯影研究/修复门 → generate 地牢 → 钥开上层门 → 无名至 npc_5 → 底层门。  
2. 抉择 d_0 → 无 d_0 踩竞技场门应拒 → 有 d_0 进 → 召唤见增援 → 双灭 e_0 + 掉落 → 点眼回主。  
3. 进灯影 title；config 赠针；npc_2 离开得窥视；pale 在灯影可用。

---

*完成一项请勾选并在本文件或功能状态中留一行日期；P0 关闭后更新 [`第二梦境.md`](第二梦境.md) §5.5 / §8 / §13。*
