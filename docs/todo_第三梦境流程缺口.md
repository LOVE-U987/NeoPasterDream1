# TODO · 第三梦境（风之旅途）流程缺口

> **类别**：活文档 · 待办 / 缺口跟踪  
> **日期**：2026-07-28  
> **来源**：原版 `libs/FixPasterDream-main` 与 Neo 对照审计（主 agent + 三 subagent：进/出维 · 材料祭坛 Boss · 辅系统；主 agent 交叉核实；**二轮** todo 对照扫描回写）  
> **玩法参考** → [`第三梦境.md`](第三梦境.md)  
> **功能总览** → [`功能状态.md`](功能状态.md)

---

## 0. 一句话

**进维门控、祭坛五阶段过程、Boss 实体本体、维度/结构 datapack、材料配方多已接线，不是空壳。**  
**纯生存端到端走不完**：风维 **不自动挂云雾 → 设计出口失效**；**矿 + 风草花无 biome_modifier（且 grass 谓词误绑染梦地）→ 材料断供**；**Boss 无实体 loot**（`pulse_windrunner_crystal` 零获取路径）；**融梦箱不授风旅宝藏**；风向 / 破风幕 / 进维反馈 / Boss AOE 与原版仍有差距。

| 维度 | 判定 |
| :--- | :--- |
| 进维条件 vs 原版 | **HIGH**（Y/成就/迷梦一致；包序列与进维反馈缺） |
| 祭坛组装 vs 原版 | **HIGH**（五阶段语义齐） |
| Boss 战 vs 原版 | **MED**（能打；AOE/掉落/伴生雷云偏弱或缺失） |
| 整条玩家链路 vs 原版 | **LOW–MED**（入口可试、出口与资源链断） |
| 生存可玩性 | **PARTIAL** — 命令/创造可走；生存卡在 **出维 + 风维 worldgen 材料 + 掉落** |

---

## 1. 优先级待办

### P0 — 阻断生存主线 / 闭环

- [ ] **移植 `SanHelper` 风维分支：自动续 `cloudmist_buff` + 环境 San**
  - 原版：`SanHelper` 玩家 total-tick：维 = `wind_journey_world` 时  
    - `addEffect(CLOUDMIST_BUFF, 200, 0, false, false)`  
    - 环境 `SAN_VARIABILITY` 瞬时修饰约 **+1.2**（与灯影 −1.2 对称）  
  - Neo 现状：`PDEffects.cloudmistTick` **仅在已有** `cloudmist_buff` 时跑；全仓对玩家 **无** 自动 `addEffect(CLOUDMIST…)`（仅 `TerraswordWaveEntity` 自用 25t）。  
  - **后果**：掉到 Y≤5 **不会**返程 → 设计出口死锁（床/死亡/命令可旁路）。  
  - 建议落点：玩家 tick / 维度环境钩（与染梦、灯影 San 环境统一更佳）。  
  - 验收：进风维后无需命令即持有云雾（可刷新）；持效果降至 Y≤5 → 主世界重生/出生 XZ、**Y=304**；San 变化方向与原版同号。

- [ ] **补风维 worldgen 挂接：双矿 + 草花/地表特征 biome_modifier（并修 placed 谓词）**
  - 原版：`forge/biome_modifier` 对 `wind_journey_biome_0` 挂  
    - `windrunner_crystal_ore` / `congeal_wind_ore`（`underground_ores`）  
    - `grass_13`（及同类）+ `ground_feature_wind_journey_*`（`vegetal_decoration` 等）  
  - Neo 现状：  
    - `configured_feature` / `placed_feature` / 方块 loot **在**；  
    - `data/pasterdream/neoforge/biome_modifier/` **零** `wind*` 条目（染梦有完整 `dyedream_ores` 等可对照 `neoforge:add_features`）；  
    - `wind_journey_biome_0.json` 的 `features` 近空（仅 vanilla `bamboo_light` 一类）；  
    - **`placed_feature/grass_13|14|15.json` / `flower_18.json` 的 `block_predicate_filter` 误要求 `dyedream_grass/dirt/sand…`**，且缺原版式纯 `minecraft:biome` 过滤（原版 grass_13 placed 仅 count/square/heightmap/biome）。  
  - 后果：**凝结之风 / 风行者水晶** 与 **`wind_plant_extract` 原料（grass_13..15 + flower_18）** 双断 → 祭坛「水晶 + 三锭」难自给（结构 NBT 零星掉落不可当主路径）。  
  - 验收：新生成风维区块可挖双矿（高度带与 placed：水晶约 40–80、凝风约 15–80）；地表可见 grass_13 等；`mortar` 链可合成 extract → `wind_iron_ingot`；VERIFY/扫描非零。

- [ ] **补 Boss 实体战利品表 `loot_table/entities/wind_knight.json`**
  - 原版：固定 **1× `pulse_windrunner_crystal`**。  
  - Neo 现状：`data/pasterdream/loot_table/entities/` 无 `wind_knight.json`；实体死亡仅 XP/移除。  
  - 附注：`pulse_windrunner_crystal` **已注册**，全 data/loot **无任何**其它获取路径 → 仅修表即可闭环。  
  - 验收：击杀 `wind_knight` 掉落 1× 脉冲风行者水晶。

- [ ] **融梦箱开箱授予 `achievement_treasure_wind_journey`**
  - 原版：`MeltdreamChestPr0` 若 `dimension == wind_journey_world` → award 全准则。  
  - Neo 现状：`MeltdreamChestBlock` 稀有池可出风铁/风碟，**无条件、无** Advancement award / 维度分支。成就 JSON（`impossible`）在；全 Java **无** `treasure_wind_journey` 引用。  
  - 验收：风维内首次开融梦箱获得隐藏成就「风旅宝藏」（其它维开箱不授）。

### P0.5 — 须游戏内 / VERIFY 核实后再升/降级

- [ ] **核实 `lost_windknight_ruins` 自然生成（datapack jigsaw）**
  - 现状：`worldgen/structure` + `structure_set`（spacing 42 / sep 25 / salt）+ NBT + pool **齐全**，绑 `wind_journey_biome_0`。  
  - **未**进 `PDRuinsRegistration`：**预期如此**（RuinAPI 主服务染梦教堂等；风遗迹走原版 structure_set datapack）。  
  - 与暮影之笼「无 GenerateWorld 强制 place」不同：此处应靠 **结构集随机散布**；需确认 1.21.1 + 噪声世界实际出结构。  
  - 验收：`/locate structure pasterdream:lost_windknight_ruins` 有结果；模板内含 `wind_knight_spawnblock_0`。

- [ ] **核实祭坛 BE 创建 + 五阶段推进（非静态 NPE）**
  - `PDBlockEntitiesFurniture` 工厂 lambda **延后**取 `WIND_KNIGHT_SPAWNBLOCKS.get(index)`，与 `STRUCTURE_BLOCKS` / 玻璃罐相同；**class init 不调用 factory**。  
  - 二轮审计仍 **不采信静态 NPE**；须手测放置 `wind_knight_spawnblock_0`：BE + Geo + 右键 0→4 + 86t 出 knight + 台回 0。  
  - 验收：五阶段无崩；阶段替换保留朝向等属性；召唤布局四雷云 + 骑士。

- [ ] **核实进维落点 Y 与维度 height=256（必要时 clamp）**
  - `fondillusionTick` 保留主世界 **Y≥306** 直传；风维 `min_y=0` / `height=256` → 可能夹顶。原版 `FondillusionBuffPr0` 同未 clamp；出维侧落点固定 304。  
  - 验收：迷梦 Y=308 进维后可站立/不虚空；若炸则 clamp 到云海安全 Y 并回写文档。

### P1 — 体验 / 原版 parity

- [ ] **移植 `WindDirectionPr0`：风维日更风向 + 广播 + 音效/粒子**
  - 原版：`OnWorldTick`，`dayTime % 24000 == 0` 时随机 0–7 写 gamerule、聊天方向、wind_chime / breeze_wind、羽毛粒子。  
  - Neo：`PDGameRules.WIND_DIRECTION` + `WindVaneItem` 只读；**无**日更。  
  - 验收：风维跨日 gamerule 变、有消息；风向标读数一致。

- [ ] **移植 `WindDirectionPr1` / `Pr2`：面朝施加顺风/逆风 + 破风旗帜反转**
  - 原版：玩家 tick（节流）按 YRot 与风向锥；Curios 有 `wind_knight_flag` 时逆弧改顺风，否则逆风 `deadwind_buff`。  
  - Neo：`tailwind_buff` / `deadwind_buff` **属性 onApply/onRemove 在**；**无** tick 施加；旗帜仅 tooltip。  
  - 验收：面朝/背风效果切换；戴旗帜逆当顺。

- [ ] **`BreakwindCurtain` `entityInside` 弹射**
  - 原版：未着地时 `delta = look * 5`。  
  - Neo：`PDBlocksDyedreamPhase2.BREAKWIND_CURTAIN` = 普通 `Block` + `noCollission`，无逻辑。  
  - 验收：穿过幕帐获明显水平推进（结构 ID 注意 `breakwing_curtain_0` vs 方块 `breakwind_curtain`）。

- [ ] **进维 `WindJourneyWorldPr0`：消息 + 主题曲**
  - 原版：进维聊天 `§4本主题梦境尚未完工` + MUSIC `wind_journey`。  
  - Neo：`PlayerDataEvents` 仅 sync San/融梦；biome music 另算。  
  - 验收：首次/每次进维有文案；音乐可接受（事件播放或明确依赖 biome）。

- [ ] **进维传送包序列（可选对齐）**
  - 原版 fondillusion：`WIN_GAME` + abilities + 效果同步 + level event 1032。  
  - Neo：裸 `ServerPlayer.teleportTo`。  
  - 验收：进维后能力/效果图标/客户端无长期不同步（对照灯影床进维实现可复用）。

- [ ] **Boss AOE 对齐 `WindKnightPr0`（或有意重平衡并文档化）**
  - 原版：约 180t 充能、11 格索敌、**6** 格 **30** 伤、25t 前摇、粒子/爆炸音、self 速/缓。  
  - Neo：`serverAoeTick` **300t**、10 格 + LOS、**4** 格 **12** 伤、无前摇粒子段。  
  - 验收：数值与手感二选一并写进 [`第三梦境.md`](第三梦境.md) §7.2。

- [ ] **召唤雷云 `ThundercloudEntity` 敌对 AI**
  - 现状：飞行 + 闲逛；**无**攻击/索敌 goal；祭坛仍刷 4 只。  
  - 验收：与原版一致（若原版亦无攻则关闭本项并改文档）。

- [ ] **`wind_knight_flag` 获取路径**
  - 注册为 Curio；无配方/无明确 loot/无 Boss 表引用。  
  - 验收：结构箱、Boss 掉落或配方之一可稳定获得；或图鉴改为创造/未实装。

### P2 — 抛光 / 文档 / VERIFY

- [ ] **扩展 VERIFY 套件 `wind-journey`（或并入 dim/structure）**
  - 现状：`PDPortingVerifyTest` 维度往返 + 风向标烟雾；结构/图鉴抽样 windmoor；**无**云雾出维、矿/草生成、祭坛阶段、loot、宝藏成就断言。  
  - 建议断言（修好 P0 后）：  
    - 风维玩家 tick 后持有 cloudmist  
    - Y≤5 返主世界 Y≈304  
    - chunk 扫描：双矿 + grass_13（或 extract 可合成）>0  
    - 祭坛 0→4 道具推进 + 86t 出 knight  
    - 击杀掉落 pulse（非零）  
    - 风维开融梦箱 → treasure_wind_journey  
  - **勿**在 P0 未修前并入默认 `all` 绿集（同 twilight-lantern 策略）。

- [ ] **同步文档**
  - [`第三梦境.md`](第三梦境.md) §10 与本 TODO 对齐；结构名 `breakwing_curtain_0` 脚注。  
  - [`功能状态.md`](功能状态.md) §3.2 挂本文件。  
  - [`README.md`](README.md) 文档地图已含第三梦境玩法页时，补本 TODO 行。

- [ ] **（可选）HUD `cloudmist_percent`**
  - Neo 客户端自算浓度；服务端仍写 persistentData。非阻断；若要多端一致可统一读服务端字段。

---

## 2. 已确认「不用当缺口重做」的部分

| 模块 | 状态 |
| :--- | :--- |
| 笔记 1/2/14 → `a_0` / `b_0` / `hide_16` | ✅ `DreamnotesLogic` |
| `dreamnotes_14` 于 `dream_church_10` + RuinAPI 自然生成 | ✅ |
| `queer_soup` → 迷梦 6000t；药水路径认效果 | ✅ |
| `fondillusionTick` Y 带 260–310 / 门 **Y≥306** + 双成就 | ✅ 条件与原版同 |
| `cloudmistTick` 出维公式与落点语义 | ✅ **逻辑在**；缺自动挂效 |
| 维度 JSON / `PDDimensions.WIND_JOURNEY_*` | ✅ min_y0 height256 bed_works |
| 群系 0/1 音乐粒子刷怪表 datapack | ✅ |
| 结构集：遗迹/风车/岛/池/树/幕/侵染石 等 JSON+NBT | ✅ 数据在（生成见 P0.5） |
| 材料配方：`wind_plant_extract`、`wind_iron_ingot_*` | ✅ JSON 在；**原料生成见 P0 worldgen** |
| 梦境炼药釜闪电法术配方 | ✅ `DreamCauldronBlockEntity`（引导药等染梦侧可先行） |
| 祭坛五阶段 + 86t 召唤骑士与四雷云 + 台重置 0 | ✅ `WindKnightSpawnblockBlock` |
| 五阶段方块/物品/Geo 资源 | ✅ |
| Boss 属性/近战 AI/基础免疫/XP | ✅ 可战 |
| 物品注册：水晶/锭/法术/脉冲/旗帜/风向标 | ✅ |
| 机器翼等可达 Y≥306 | ✅（或鞘翅） |

---

## 3. 建议动手顺序

```text
1. SanHelper 风维：cloudmist 自动续 + San +1.2              ← 解锁设计出口
2. 风维 worldgen：双矿 + grass/flower + ground_feature 挂接
   （修 grass placed 谓词；对照 dyedream_ores modifier）     ← 解锁材料自给
3. wind_knight 实体 loot → pulse_windrunner_crystal
4. MeltdreamChest 风维 grant treasure_wind_journey
5. VERIFY/手测：lost_windknight_ruins locate + 祭坛 0→4 + 进维 Y
6. 风向 Pr0 → Pr1/Pr2 + 旗帜；破风幕 entityInside
7. 进维消息/音乐（+ 可选包序列）；Boss AOE / 雷云 AI 取舍
8. 文档与功能状态；可选 wind-journey VERIFY 套件
```

### 3.1 复测备忘（不写 `.run`）

```bash
# 维度往返烟雾（现有；不覆盖 P0）
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=dim PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

游戏内快捷（对齐 [`第三梦境.md`](第三梦境.md) §12）：

```mcfunction
/advancement grant @s only pasterdream:achievement_b_0
/advancement grant @s only pasterdream:achievement_hide_16
/effect give @s pasterdream:fondillusion_buff 6000 0 true
/execute in minecraft:overworld run tp @s ~ 308 ~
# 出维（P0 修好前须手动云雾）
/effect give @s pasterdream:cloudmist_buff 200 0 true
/execute in pasterdream:wind_journey_world run tp @s ~ 3 ~
```

---

## 4. 审计交叉结论（防误报）

| 主张 | 结论 |
| :--- | :--- |
| 出维无自动云雾 | **成立** — P0（二轮再确认） |
| 双矿无 Neo biome_modifier | **成立** — P0；并入 **草花/地表特征** 同批 worldgen |
| grass_13 placed 谓词绑 dyedream 地 | **成立** — 二轮新增；与缺 modifier 叠加阻断 extract |
| 无 `wind_knight` 实体 loot / pulse 零路径 | **成立** — P0 |
| 融梦箱不授 treasure_wind | **成立** — P0（全 Java 无 ID 引用） |
| 祭坛 BE 静态 NPE | **不采信** — 延迟 factory；P0.5 手测五阶段 |
| 遗迹因未进 PDRuinsRegistration 必不生成 | **不采信为 P0** — structure_set 路径预期有效；P0.5 locate |
| 风向 / 幕帐 / 进维文案缺失 | **成立** — P1 |
| Boss AOE 数值不同 | **成立** — P1 取舍 |
| 文档 §8–10 大体正确 | **成立**；`breakwing`/`breakwind` 脚注即可 |

---

*修完一项请勾选并在 [`功能状态.md`](功能状态.md) / [`第三梦境.md`](第三梦境.md) 同步；全部 P0 关闭后再考虑并入默认 VERIFY 绿集。*
