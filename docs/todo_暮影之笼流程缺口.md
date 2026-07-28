# TODO · 暮影之笼流程缺口

> **类别**：活文档 · 待办 / 缺口跟踪  
> **日期**：2026-07-28  
> **来源**：原版 `libs/FixPasterDream-main` 与 Neo 对照审计（主 agent + 双 subagent）  
> **玩法参考** → [`暮影之笼.md`](暮影之笼.md)  
> **功能总览** → [`功能状态.md`](功能状态.md)

---

## 0. 一句话

**事件本体（点燃 → 刷怪 → key/hide_9 → 真影床进维）与原版高度一致，不是临时空壳。**  
**纯生存端到端走不完**：主世界据点不自动生成、灯影出生结构不放、`hide_7` 无授予、返程依赖未注册的 `/spawn`。

| 维度 | 判定 |
| :--- | :--- |
| 笼子事件逻辑 vs 原版 | **HIGH** 一致 |
| 整条玩家链路 vs 原版 | **MED**（核心交互齐，入口/出口断） |
| 生存可玩性 | **PARTIAL** — 创造/调试可走；生存卡在入口 |

---

## 1. 优先级待办

### P0 — 阻断生存主线

- [ ] **移植 `GenerateWorldPr0` 等价：主世界加载放置 `shadow_world_door`**
  - 原版：`GenerateWorldPr0Procedure` @ `LevelEvent.Load`，OVERWORLD 内用 gamerule `randomCoordX/Z`（若为 0 则随机 -100..100）计算坐标，在 **Y≈-60** 附近 `placeInWorld(shadow_world_door)`。
  - Neo 现状：`PDGameRules.RANDOM_COORD_X/Z` 已注册；**无任何 Load 监听放置该模板**。仅有 `structure_block_9` SPECS + 调试杖/创造。
  - 验收：新档进主世界后，按笔记/坐标规则附近存在含 `twilight_lantern` + `true_shadow_bed` 的据点。

- [ ] **移植同 Pr0 灯影分支：加载 `lamp_shadow_world` 时放置 `shadow_world_spawn`**
  - 原版：heightmap 判定后在约 `(-11,100|-11,150,-9)` 放模板（内含返程笼）。
  - Neo 现状：床传送落点 `(0.5, 104|154, 0.5)` 有；**结构不放 → 可能无笼可点返程**。
  - 验收：首次进灯影后出生点附近有 `twilight_lantern`。

- [ ] **补 `hide_7` 授予：监守者死亡 → 原 `SculkHeartPr0`**
  - 原版：`EntityDeathPr0` 若实体为 `Warden` → `SculkHeartPr0`（需已有 `achievement_start`、尚未 hide_7）授 hide_7 + 文案/黑暗/减速等；并掉落 `sculk_heart`（无 silentsdelight 时）。
  - Neo 现状：`sculk_heart` **仅物品注册**；全仓无 LivingDeath→hide_7。
  - 影响：笔记 8 → hide_8 死锁（「你尚未知晓如何激活影灯」）。
  - 旁路：hide_10（笔记 9 + `achievement_b_0`）仍可点燃，**不替代**据点生成。
  - 验收：杀一只监守者（已有 start）获得 hide_7；读 dreamnotes_8 得 hide_8。

### P0.5 — 触发块放置路径（须先游戏内核实）

- [x] **核实 `PDStructureBlock` / STM 对 `structure/` 单数目录** — **2026-07-28 终验关闭（非缺口）**
  - 套件：`PASTERDREAM_VERIFY_SUITES=twilight-lantern` → **32 pass / 0 fail**（`pd_verify_report.json`）。
  - STM `get`/`getOrCreate(pasterdream:shadow_world_door)`：**成功** size `45x42x45`；资源直读 `structure/*.nbt` 同尺寸。
  - `structure_block_9` onPlace：自毁为空气；扫描非空气 **37971**；**twilight_lantern×1** + **true_shadow_bed×1**。
  - 结论：Neo 单数 `data/pasterdream/structure/` 可被 1.21.1 STM 加载；子代理「目录阻断」**不成立**。杖 fallback 仍可保留作双保险，**不必**为 P0 改路径。

### P1 — 返程与体验

- [ ] **返程不依赖外部字面命令 `spawn`**
  - 现状：与原版同，`performPrefixedCommand(..., "spawn")`；`PDCommands` **未**注册 `spawn`。失败时静默；至少已有 WIN_GAME + `teleportTo(overworld)`。
  - 建议：失败或默认改为 `ServerPlayer` 重生点 / 世界共享出生点传送；或注册模组命令并保留原版字面兼容。
  - 验收：灯影右键笼 → 主世界且落在合理重生/出生区域，无需第三方 `/spawn`。

- [ ] **（可选）事件结束重置 `number` 或再点燃时清零**
  - 原版同疾：结束只关 `switch`，`number` 残留 → 二次点燃难再命中刷怪节点。
  - 非阻断（key 已 true 仍可上床）；若要对齐「可重复守卫战」再改。

### P2 — 抛光 / 文档

- [ ] **`TwilightLanternBlock` 的 `ANIMATION` 状态**：定义了 0..1，事件路径未 `setValue`；核对原版/Geo 是否依赖，必要时驱动。
- [ ] **更新 [`暮影之笼.md`](暮影之笼.md)**：
  - 主路径写清原版 = **世界加载直接 place 模板**，`structure_block_9` 为触发块/调试旁路，非唯一自然来源。
  - 写明 hide_7 = 监守者（SculkHeart）；hide_10 旁路。
  - 返程：`spawn` 依赖与 Neo 缺口。
  - 灯影 `shadow_world_spawn` 需自动放置。
- [ ] **[`功能状态.md`](功能状态.md) §3 开放项** 挂一条「暮影之笼入口：GenerateWorld + hide_7 + 返程 spawn」。
- [ ] **VERIFY（可选）**：结构放置断言 / hide_7 授予钩子 / 灯影有笼；勿把「仅 SPECS 可加载」当成「世界已生成据点」。

---

## 2. 已确认「不用当缺口重做」的部分

下列与原版 Pr 对齐，**优先修入口，勿重写事件**：

| 模块 | 状态 |
| :--- | :--- |
| 点燃条件（hide_8\|10 + 碎片 + !switch） | ✅ |
| 黑暗 140 / +18 / +55 / +2600 时序与文案音效 | ✅ |
| number 节点刷怪表 + z-6 双写幽魂 | ✅ |
| 结束 46 格玩家 → key + hide_9 + switch=false | ✅ |
| 真影床：夜/雷 + 上 2 格笼 key + hide_9 → SAN-10 + 进维 | ✅ |
| `WorldSpawnPr1` 落点语义 (0.5, 104\|154, 0.5) | ✅ |
| 实体 SHADOW_GHOST / SQUEAL / GOLEM / TERRORBEAK | ✅ 已注册 |
| 维度 `lamp_shadow_world` | ✅ |
| `meltdream_crystal_0` | ✅ |
| 重置杖清 `switch` | ✅ |
| 普通 `shadow_bed`（需 shadow_start，进维后自动） | ✅ 逻辑在；依赖已进过灯影 |

---

## 3. 建议动手顺序

```text
0. ~~P0.5 模板路径~~ ✅ 已关（twilight-lantern VERIFY 32/0）
1. GenerateWorld 主世界 shadow_world_door（+ randomCoord 初始化）
2. GenerateWorld 灯影 shadow_world_spawn
3. LivingDeath Warden → hide_7（+ sculk_heart 掉落，对齐原版）
4. 返程 spawn 兜底
5. 文档与功能状态开放项同步
6. （可选）number 重置 / ANIMATION；全量 VERIFY 仍默认不含 twilight-lantern
```

### 3.1 复测命令（不写 `.run`，复制即用）

```bash
# 仅暮影缺口核实；测完自动退出；报告 PasterDream/run/pd_verify_report.json
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=twilight-lantern PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

别名：`twilight` / `lantern`。  
**不在** `all` 默认集内（缺口确认类断言会与全绿终验语义冲突；修好 P0 后可改断言方向或并入 structures）。

### 3.2 近次实测摘要（2026-07-28）

| 断言主题 | 结果 | 含义 |
| :--- | :---: | :--- |
| STM + 直读 door/spawn | PASS | 模板路径 OK |
| structure_block_9 → 笼+床 | PASS | **P0.5 关闭** |
| 主世界抽样无自动笼 | PASS | **GenerateWorld 缺口仍在** |
| 裸 `spawn` 未注册 | PASS | **返程缺口仍在** |
| 杀 Warden 无 hide_7 | PASS | **SculkHeart 缺口仍在** |
| BE key/switch/number、hide_9 可授、实体/维/成就 JSON | PASS | 事件层可用 |

---

## 4. 调试绕过（修之前人工验收事件用）

```mcfunction
# 据点
/give @s pasterdream:structure_block_9
# 或调试杖模板 shadow_world_door

# 成就（跳过 hide_7 时直接 hide_8 或 hide_10）
/advancement grant @s only pasterdream:achievement_hide_8
/give @s pasterdream:meltdream_crystal_0

# 灯影
/execute in pasterdream:lamp_shadow_world run tp @s 0 104 0
/give @s pasterdream:twilight_lantern
```

---

## 5. 代码锚点（修时打开）

| 用途 | 路径 |
| :--- | :--- |
| 原版世界加载放置 | `libs/FixPasterDream-main/.../GenerateWorldPr0Procedure.java` |
| 原版 hide_7 | `.../EntityDeathPr0Procedure.java` · `SculkHeartPr0Procedure.java` |
| 原版事件 | `TwilightLanternPr0/Pr1/Pr2` · `TrueShadowBedPr0` · `WorldSpawnPr1` |
| Neo 事件 | `block/TwilightLanternBlock.java` · `TrueShadowBedBlock.java` · `ShadowBedBlock.java` |
| 触发块 SPECS 9 | `block/PDStructureBlock.java` |
| 规则坐标 | `registry/PDGameRules.java`（RANDOM_COORD_* 已有、生成未用） |
| 笔记 hide_8/10 | `dreamnotes/DreamnotesLogic.java` |
| 命令 | `command/PDCommands.java`（无 spawn） |
| 模板 | `data/pasterdream/structure/shadow_world_door.nbt` · `shadow_world_spawn.nbt` |

---

## 6. 审计备注

- 子代理「流程可玩性」曾写「原版 hide_7 也无授予」——**不准确**；以 `SculkHeartPr0` + Warden 死亡为准。
- 子代理「对照原版」将 `structure/` vs `structures/` 标为硬阻断——**已否决**（twilight-lantern VERIFY：STM 与 structure_block_9 均可加载/放置）。
- `number` 默认：W4 `getDouble` 缺键为 0（注释已核对调用点）；与原版 persistentData 语义一致。
- SAN-10 受 `pasterdreamSanSystem` 控制；关规则时可能不扣理智，进维逻辑仍应执行。
- 双 subagent + 主线 + 游戏核实后：事件层与触发块 OK；**仍待修 = GenerateWorld 双结构 + hide_7 + 返程 spawn**。
- 实现：`smoketest/PDTwilightLanternVerifyHooks.java`；`PDPortingVerifyTest.Suite.TWILIGHT_LANTERN`（默认 all 排除）。

---

*完成一项请勾选并在本文件或功能状态中留一行日期；全部 P0 关闭后更新 [`暮影之笼.md`](暮影之笼.md) 生成章节。*
