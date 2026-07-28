# TODO · 暮影之笼流程缺口

> **类别**：活文档 · 待办 / 缺口跟踪  
> **日期**：2026-07-29  
> **来源**：原版 `libs/FixPasterDream-main` 与 Neo 对照审计（主 agent + 双 subagent）  
> **玩法参考** → [`暮影之笼.md`](暮影之笼.md)  
> **功能总览** → [`功能状态.md`](功能状态.md)  
> **设计 / 计划** → [`superpowers/specs/2026-07-28-shadow-world-door-structure-design.md`](superpowers/specs/2026-07-28-shadow-world-door-structure-design.md) · [`superpowers/plans/2026-07-28-shadow-world-door-structure.md`](superpowers/plans/2026-07-28-shadow-world-door-structure.md)

---

## 0. 一句话

**事件本体 + 生存入口/出口 P0 已收口**（structure_set+locate、灯影 spawn、Warden→hide_7、返程重生点）。  
VERIFY `twilight-lantern`：**36 pass / 0 fail**（2026-07-29）。  
仍开放：可选 `number` 重置、ANIMATION 驱动、rings 手感、**自然世界** `/locate` 手测。

| 维度 | 判定 |
| :--- | :--- |
| 笼子事件逻辑 vs 原版 | **HIGH** 一致 |
| 整条玩家链路 vs 原版 | **HIGH**（入口/出口已接；手测 rings 散布） |
| 生存可玩性 | **READY（自动化）** — 自然档 `/locate`+笔记坐标仍建议手验 |

---

## 1. 优先级待办

### P0 — 阻断生存主线（已全部关闭）

- [x] **主世界据点：真结构 + concentric_rings + locate（替代 GenerateWorld Load place）** — **2026-07-28**
  - 设计：[`superpowers/specs/2026-07-28-shadow-world-door-structure-design.md`](superpowers/specs/2026-07-28-shadow-world-door-structure-design.md)
  - 计划：[`superpowers/plans/2026-07-28-shadow-world-door-structure.md`](superpowers/plans/2026-07-28-shadow-world-door-structure.md)
  - datapack：`worldgen/structure|template_pool/shadow_world_door` + `structure_set/shadow_world_doors`（rings）+ tags `has_structure/shadow_world_door` / `twilight_lantern_located`
  - Java：`PDShadowDoorLocator`；笔记 8/9 **全 locate**（不再 `randomCoord*21`）；`RANDOM_COORD_*` 仍注册不驱动门
  - VERIFY：datapack/tag 正向；Locator 在 VERIFY 超平 `generateStructures=false` 为 **SKIP 非失败**
  - 手测验收：正常世界 `/locate structure pasterdream:shadow_world_door`；笔记 x/z 与同次 locate 一致（未命中不写假坐标）

- [x] **灯影分支：加载 `lamp_shadow_world` 时放置 `shadow_world_spawn`** — **2026-07-28**
  - 实现：`world/PDLampShadowWorldgen` @ `LevelEvent.Load`；heightmap(-9,-9)≤100 → `(-11,100,-9)` 否则 `(-11,150,-9)`；SavedData 每维一次。
  - VERIFY：灯影含 `twilight_lantern×1`。

- [x] **补 `hide_7` 授予：监守者死亡 → 原 `SculkHeartPr0`** — **2026-07-28**
  - 实现：`world/PDEntityDeathEvents` @ `LivingDeathEvent`；需 `achievement_start`、未 hide_7；文案/黑暗/减速/虚弱；无 `silentsdelight` 时掉 `sculk_heart`；顺带 ElderGuardian → `elder_guardian_scale`。
  - VERIFY：杀 Warden → hide_7 正向。

### P0.5 — 触发块放置路径

- [x] **核实 `PDStructureBlock` / STM 对 `structure/` 单数目录** — **2026-07-28 终验关闭（非缺口）**
  - STM / 直读 door `45x42x45`、spawn `20x23x19`；`structure_block_9` → 笼+真影床；非空气约 37971。

### P1 — 返程与体验

- [x] **返程不依赖外部字面命令 `spawn`** — **2026-07-28**
  - 实现：`TwilightLanternBlock.teleportToOverworldSpawn` — WIN_GAME + 主世界玩家重生点，否则共享出生点 heightmap；**不再** `performPrefixedCommand("spawn")`。
  - VERIFY：root 无 child `spawn`；返程走重生点路径。

- [ ] **（可选）事件结束重置 `number` 或再点燃时清零**
  - 原版同疾：结束只关 `switch`，`number` 残留 → 二次点燃难再命中刷怪节点。
  - 非阻断（key 已 true 仍可上床）；若要对齐「可重复守卫战」再改。

### P2 — 抛光

- [ ] **`TwilightLanternBlock` 的 `ANIMATION` 状态**：定义了 0..1，事件路径未 `setValue`；核对原版/Geo 是否依赖，必要时驱动。
- [ ] **rings 手感调参**（distance/spread/count）：首版 `32/3/64`；自然档散布过稀/过密再调。
- [x] **文档同步（暮影之笼 / 功能状态 / 本 todo / 计划勾选）** — **2026-07-29**
- [x] **VERIFY `twilight-lantern` 统一跑** — **2026-07-29**：**36 pass / 0 fail**（`PasterDream/run/pd_verify_report.json`）

---

## 2. 已确认「不用当缺口重做」的部分

下列与原版 Pr 对齐，**勿重写事件**：

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
| 主世界 structure_set + Locator + 笔记 locate | ✅ |
| 灯影 shadow_world_spawn | ✅ |
| Warden → hide_7 | ✅ |
| 返程重生点/出生点 | ✅ |

---

## 3. 建议动手顺序（历史）

```text
0. ~~P0.5 模板路径~~ ✅
1. ~~主世界 structure_set + Locator + 笔记 locate~~ ✅ 2026-07-28
2. ~~灯影 shadow_world_spawn~~ ✅ PDLampShadowWorldgen
3. ~~Warden → hide_7~~ ✅ PDEntityDeathEvents
4. ~~返程 spawn 兜底~~ ✅ teleportToOverworldSpawn
5. ~~文档与功能状态同步~~ ✅ 2026-07-29
6. ~~统一跑 twilight-lantern VERIFY~~ ✅ 36/0（2026-07-29）
7. （可选）number 重置 / ANIMATION；rings 手感；自然档 /locate 手测
```

### 3.1 复测命令（不写 `.run`，复制即用）

```bash
# 仅暮影套件；测完自动退出；报告 PasterDream/run/pd_verify_report.json
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=twilight-lantern PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

别名：`twilight` / `lantern`。  
**不在** `all` 默认集内（专用套件；与 CORE 终验分离）。

### 3.2 近次实测摘要（2026-07-29 统一 VERIFY）

| 断言主题 | 结果 | 含义 |
| :--- | :---: | :--- |
| STM + 直读 door/spawn | PASS | 模板路径 OK |
| structure_block_9 → 笼+床 | PASS | 调试旁路 OK |
| datapack structure / set / locate tag | PASS | 结构集在场 |
| Locator.locate overworld | SKIP | VERIFY 超平 `generateStructures=false`（非失败） |
| 灯影 spawn 有笼 | PASS | PDLampShadowWorldgen |
| 杀 Warden → hide_7 | PASS | PDEntityDeathEvents |
| 返程不依赖裸 `/spawn` | PASS | teleportToOverworldSpawn |
| BE key/switch/number、hide_9 可授、实体/维/成就 | PASS | 事件层可用 |
| **合计** | **36/0** | 套件全绿 |

---

## 4. 调试 / 手测

```mcfunction
# 据点（调试旁路）
/give @s pasterdream:structure_block_9
# 或调试杖模板 shadow_world_door

# 自然据点（需开结构生成的正常世界）
/locate structure pasterdream:shadow_world_door
# 或 /locate structure #pasterdream:twilight_lantern_located

# 成就
/advancement grant @s only pasterdream:achievement_hide_8
/give @s pasterdream:meltdream_crystal_0

# 灯影
/execute in pasterdream:lamp_shadow_world run tp @s 0 104 0
/give @s pasterdream:twilight_lantern
```

---

## 5. 代码锚点

| 用途 | 路径 |
| :--- | :--- |
| 原版世界加载放置 | `libs/FixPasterDream-main/.../GenerateWorldPr0Procedure.java` |
| 原版 hide_7 | `.../EntityDeathPr0Procedure.java` · `SculkHeartPr0Procedure.java` |
| 原版事件 | `TwilightLanternPr0/Pr1/Pr2` · `TrueShadowBedPr0` · `WorldSpawnPr1` |
| Neo 事件 | `block/TwilightLanternBlock.java` · `TrueShadowBedBlock.java` · `ShadowBedBlock.java` |
| Locator | `worldgen/PDShadowDoorLocator.java` |
| 灯影 spawn | `world/PDLampShadowWorldgen.java` |
| hide_7 / 掉落 | `world/PDEntityDeathEvents.java` |
| 触发块 SPECS 9 | `block/PDStructureBlock.java` |
| 规则坐标 | `registry/PDGameRules.java`（RANDOM_COORD_* 已有、不驱动门） |
| 笔记 hide_8/10 | `dreamnotes/DreamnotesLogic.java` |
| VERIFY | `smoketest/PDTwilightLanternVerifyHooks.java` |
| 模板 | `data/pasterdream/structure/shadow_world_door.nbt` · `shadow_world_spawn.nbt` |
| datapack | `worldgen/structure{,_set}/shadow_world_door(s).json` · `template_pool/` · tags |

---

## 6. 审计备注

- 子代理「流程可玩性」曾写「原版 hide_7 也无授予」——**不准确**；以 `SculkHeartPr0` + Warden 死亡为准。
- 子代理「对照原版」将 `structure/` vs `structures/` 标为硬阻断——**已否决**。
- `number` 默认：W4 `getDouble` 缺键为 0；与原版 persistentData 语义一致。
- SAN-10 受 `pasterdreamSanSystem` 控制；关规则时可能不扣理智，进维逻辑仍应执行。
- **structure 计划**（Task 1–6）范围原不含灯影/hide_7/返程；后续 P0 已另实现并纳入本 todo。
- **P0 生存入口（2026-07-28）** + **统一 VERIFY 36/0（2026-07-29）**。默认 `all` 仍排除 `twilight-lantern`。
- **未 git commit**（无用户授权）。

---

*可选抛光（number / ANIMATION / rings）完成后再勾；自然档 `/locate` 手测记入功能状态「人工游玩回归」。*
