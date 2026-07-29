# 设计 · VERIFY 主干全链路连续流程测试（main-flow）

> **日期**：2026-07-29  
> **状态**：已批准设计 · 修订（无名全对话 + 光/暗抉择）· 待实现计划  
> **范围**：单客户端 VERIFY 套件，串起染梦 → 暮影 → 灯影（**全无名对话** + **影之抉择光/暗**）→ 竞技场 → 风旅  
> **玩法参考** → [`docs/第二梦境.md`](../../第二梦境.md) · [`docs/第三梦境.md`](../../第三梦境.md) · [`docs/暮影之笼.md`](../../暮影之笼.md)  
> **验证入口** → [`docs/验证复现.md`](../../验证复现.md)

---

## 0. 一句话

新增专项 VERIFY 套件 **`main-flow`**（默认不进 `all`）：在一次 `runClient` 内按叙事顺序连续驱动主线门控，证明「主要流程能连续进行」。第二梦段必须 **真实跑完无名 `npc_0..5` 全对话链**（含 Stage2 遣返与入侵得 `npc_3`），并 **真实打开影之抉择菜单且光/暗两个按钮都点过**；结构出现用摆放，长等待用 `ServerScheduler.advanceForTest` 压缩。

---

## 1. 背景与目标

### 1.1 现状

- 已有隔离专项：`twilight-lantern`、`second-dream`、`wind-journey`（均不在默认 `all`）。
- 各专项假设相对干净的玩家/局部摆件；`ServerScheduler.advanceForTest` 与成就/背包会互相污染，**不能**简单 `SUITES=a,b,c` 当成连续主线。
- 默认 VERIFY 世界：超平、关结构生成、关昼夜/天气、EASY（VERIFY 时）；适合稳定回归，不适合自然 locate 漫游。
- **没有**跨章节连续主线测试。

### 1.2 目标

单次 `runClient` 跑通主干：

**染梦起步 → 暮影据点 → 灯影/地牢（无名全对话 + 影之抉择光与暗）→ 竞技场 → 风之旅途进·打·出**

成功标准：

- 关键门控与结算走生产代码路径（交互、buff tick、Y 阈值、BossManager、出维条件、**NPC `mobInteract` 对话调度**、**抉择 `openMenu` + `clickMenuButton`** 等）。
- **无名**：`achievement_shadow_npc_0`…`_5` 均由对话/入侵生产路径获得（禁止 grant 跳过对话链）。
- **影之抉择**：灯影内真影床打开 `ShadowSelectEndMenu`；**光明与黑暗两个按钮均真实点击**并断言奖励；连续主线随后以 **黑暗**（`talent_shadow`）进入竞技场叙事（与文档「寻找双手眼睛」一致）。
- 报告含 phase 与 `continuousFlags`；墙钟目标约 **8–18 分钟**（对话调度较长，advance 压缩后仍高于无对话版）。
- 失败可定位到 phase，后续 phase 依赖跳过，避免雪崩误报。

### 1.3 非目标

- 不替换三个现有专项（细节密度与边界回归仍靠它们）。
- 不把 `main-flow` 并入默认 `all`。
- 不覆盖全书笔记、**非主线**支线 NPC、自然 `/locate` 漫游、客户端渲染/音效/UI **像素级**手感（抉择只要求菜单打开与按钮服务端语义，不截图验 GUI 布局）。
- 不强制开 DEFAULT 世界赌结构生成（见 §2 创世策略）。
- 不逐条断言无名每句聊天文本（断言阶段成就、锁 `switch` 释放、Stage2 TP、礼物金块等**可观测结果**；可选 log 抽样一句）。

### 1.4 已拍板决策

| 项 | 选择 |
|---|---|
| 覆盖范围 | 主干全链路（染梦 → 暮影 → 第二梦 → 风旅） |
| 门控真实度 | 尽量全真触发 |
| 运行形态 | 单客户端长测 |
| 实现形态 | **方案 A**：新套件 `main-flow`，非串联旧专项 |
| 第二梦对话 | **全无名阶段**真实 `mobInteract` + scheduler 播完 |
| 光与影 | **两按钮都点**；连续线保留黑暗进竞技场 |

---

## 2. 架构

### 2.1 套件与入口

- `PDPortingVerifyTest.Suite.MAIN_FLOW`  
  别名：`main-flow` / `main` / `story` / `full-flow`
- **排除**默认 `all` / `*`（与 twilight / wind / second 相同处理）
- Hooks：`smoketest/PDMainFlowVerifyHooks.java`（phase 状态机）
- IDE：`.run/PD VERIFY main-flow.run.xml`
- 运行：

```bash
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=main-flow \
  PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

### 2.2 创世与运行时环境

| 项 | 默认 VERIFY | main-flow |
|---|---|---|
| 世界预设 | 超平 FLAT | **保持超平基底**（稳、快） |
| 结构生成 | 关 | **关**；关键点 **模板/程序化摆放** |
| 昼夜/天气 | 创世时关 | **phase 内强制** `setDayTime` / `setWeatherParameters`（床/风暴门控） |
| 难度 | EASY | EASY（需怪/Boss） |
| 模式 | 创造开局 | phase 内 **生存** 测门控；需高度时临时飞行或搭柱，用完还原 |

说明：在「尽量全真」与可重复绿之间，**结构出现**用夹具，**门控逻辑**不 grant 跳过结局。不开自然 worldgen 赌 locate，避免 flaky。

### 2.3 编排模型

```
onLogin → buildMainFlowTimeline
  Phase0 Bootstrap
  Phase1 染梦
  Phase2 暮影
  Phase3 灯影 + 竞技场
  Phase4 风旅
  Phase5 收尾报告
```

- 仍用现有 `at(tick)` 时间线 + `ServerScheduler.advanceForTest` 压缩等待。
- 每 phase 结束：断言维度 / 关键成就 / 关键物品；清理临时实体与多余方块；**保留**主线成就与连续状态。
- Phase 硬失败后：**跳过**后续 phase（`SKIPPED_DEPENDENCY`）；Phase5 仍汇总。

### 2.4 与旧专项关系

| 套件 | 关系 |
|---|---|
| `twilight-lantern` / `second-dream` / `wind-journey` | 保留；边界与高密度断言仍靠它们 |
| `main-flow` | 叙事连续 + 真门控串联；断言密度低于专项全集 |
| 代码复用 | 可抽 `PDVerifyFixtures`（摆件、useBlock、dump）；**非强制**本任务内重构专项 |
| 文档 | README、`验证复现.md`、`功能状态.md` 登记入口与耗时 |

---

## 3. Phase 清单与真触发 / 夹具边界

原则：**门控与结算走真实路径**；**结构出现与冗长探索**用摆放 / 最小笔记触发；**等待**用 advance 压缩，不跳过回调逻辑。

### 3.1 Phase 0 — Bootstrap

| 步骤 | 做法 | 真/夹具 |
|---|---|---|
| 确认套件、切生存、清无关创造物 | 代码 | 夹具 |
| `achievement_start` 仅当真实路径无法自举 | 最小 grant | 夹具 |
| 主世界测试锚点小平台 | 平整/铺方块 | 夹具 |

### 3.2 Phase 1 — 染梦起步（a_0 → b_0 → hide_16）

| 步骤 | 做法 | 真/夹具 |
|---|---|---|
| 放置 `dyedream_crack`（或最小裂隙现场） | 摆方块 | 夹具（出现） |
| 笔记/交互解锁 `dreamnotes_1` → `a_0` | `DreamnotesLogic` 或等价生产路径 | **真** |
| 进入染梦 | 优先生产入口（裂隙/传送手段） | **真** |
| 染梦内 `dreamnotes_2` → `b_0` | 真实 unlock | **真** |
| `dreamnotes_14` → `hide_16`（父 `b_0`） | 阅读/unlock，**禁止**直接 grant hide_16 | **真** |
| 断言 a_0 / b_0 / hide_16；可回主世界 | | |

**不测**：church_10 整座漫游、全量染梦遗迹。

### 3.3 Phase 2 — 暮影据点 → 进灯影

| 步骤 | 做法 | 真/夹具 |
|---|---|---|
| 摆暮影最小现场（lantern + true_shadow_bed + 几何） | 对齐 twilight hooks / 模板 | 夹具 |
| 激活笼前置（碎片等） | 能走方块则走；缺探索物则 give 再右键 | 混合 |
| 守卫波推进至 key / hide_9 | 真实 lantern 逻辑 + advance | **真**（时间压缩） |
| Warden → hide_7（若主线需要） | 生成并击杀走死亡事件 | **真** |
| 夜间/风暴 | `setDayTime` / 天气参数 | 夹具（环境） |
| 真影床 → `lamp_shadow_world` | `TrueShadowBedBlock` 真路径 | **真** |
| 断言在灯影；LampShadow 进维副作用可观测 | | |

**不测**：自然 `/locate shadow_world_door`、完整墙钟 130s 守卫。

### 3.4 Phase 3 — 灯影 / 无名全对话 / 影之抉择 / 竞技场

连续状态从上阶段带入（已在灯影）。本 phase 是修订重点。

#### 3.4.1 地牢门钥与无名现场

| 步骤 | 做法 | 真/夹具 |
|---|---|---|
| 摆地牢门+钥、无名 `shadow_npc_0`、下层真影床（抉择用） | 程序化放置 | 夹具（出现） |
| 钥匙拾取 → 门 cascade（下层钥开；上层门需 `npc_5` 后另测） | 生存交互 | **真** |
| 玩家站在无名 **16 格内**（同步听台词范围） | TP 靠近 | 夹具 |

#### 3.4.2 无名全对话链（禁止 grant `npc_*`）

对齐 `ShadowNpc0Entity` / [`第二梦境.md`](../../第二梦境.md) §6。每一段：

1. 确认 `DATA_SWITCH`（对话锁）为 false  
2. **`mobInteract` 真实右键**（服务端）  
3. `advanceForTest` 覆盖该段最长 schedule（见下表）  
4. 断言对应成就、锁释放、附加副作用  

| 段 | 前置 | 交互后目标 | schedule 量级（tick） | 额外断言 |
|---|---|---|---|---|
| Stage0 | 无 npc_0 | `npc_0` | ~560 | 附近掉落金块（见面礼） |
| Stage1 | npc_0 ∧ ¬npc_1 | `npc_1` | ~600 | 锁释放 |
| Stage2 | (npc_1∧¬npc_2)∨(npc_2∧¬npc_3) | `npc_2` | ~160 | 配置默认开时 **TP 主世界** 重生点/出生点 |
| **入侵 → npc_3** | 已 npc_2，在主世界 | `npc_3` | 见下 | **不得** grant npc_3 |
| Stage4 | npc_3 ∧ ¬npc_4 | `npc_4` | ~500 | 需先回灯影地牢见无名 |
| Stage5 | npc_4 ∧ ¬npc_5 | `npc_5` | ~260 | 提示下层/抉择；锁释放 |

**`npc_3`（主世界暗影入侵平息）** — 生产路径在 `PDEffects.shadowIntrudeCalm`（窥视 buff tick：入侵结束或白天 calm）。main-flow 要求：

1. Stage2 后玩家应在主世界且具备窥视条件（离灯影时 `LampShadowEvents`：`npc_2` ∧ ¬`e_0` → `shadow_spyon_buff`；若 Stage2 TP 未离维挂 buff，则 **真实再进再出灯影一次** 触发 Pr1）。  
2. 驱动入侵状态至可 calm：优先走 buff 的 `shadow_intrude` / `shadow_intrude_end` 逻辑；允许 **写入与原版 tick 相同的 player persistent 标志** 后继续跑 **真实** `shadowIntrudeTick`/`Calm`（或白天 `setDayTime` 触发 calm），**禁止**直接 `award(npc_3)`。  
3. 断言 `npc_3` + 窥视按代码移除/保留语义。  
4. 再回灯影，继续 Stage4/5。

若入侵随机性过强：允许「最小确定性驱动」（设 end 标志 + 推进 tick / 强制白天），仍须经过 `shadowIntrudeCalm` 授成就。

#### 3.4.3 影之抉择（光与暗均真实点击）

条件：灯影内、已 `npc_5`、未 `d_0`。方块：`TrueShadowBedBlock` → `openMenu(ShadowSelectEndMenu)`。

| 步骤 | 做法 | 真/夹具 |
|---|---|---|
| 右键真影床 | 生产 `use` 路径 | **真** |
| 断言 `containerMenu instanceof ShadowSelectEndMenu` | | **真** |
| **先点光明** `clickMenuButton(BUTTON_LIGHT)` | 等价客户端按钮 1 | **真** |
| 断言 `d_0` + `talent_light` + 背包有 `white_crystal` | | |
| **回滚抉择（仅测试用）** | revoke `d_0` 与 `talent_light`；移除本次白水晶；关闭菜单 | 夹具（恢复可再选） |
| 再右键真影床打开菜单 | | **真** |
| **再点黑暗** `clickMenuButton(BUTTON_DARK)` | 按钮 0 | **真** |
| 断言 `d_0` + `talent_shadow` + `shadow_hilt`；黑暗旁白 schedule 可 advance | | |
| 连续主线 **保留黑暗** 进入竞技场 | | |

说明：

- **禁止** 用私有 `chooseDark`/`chooseLight` 反射代替 `clickMenuButton`（必须走菜单按钮通道）。  
- 允许服务端直接 `player.containerMenu.clickMenuButton(player, id)`，无需机器人点屏幕像素。  
- 光明先测再回滚，保证两分支奖励都验到，且后续手箱天赋分支与文档黑暗线一致。  
- 上层地牢门 `npc_5` 开启若在流程需要，在抉择前或后按文档插一次真交互。

#### 3.4.4 竞技场

| 步骤 | 做法 | 真/夹具 |
|---|---|---|
| 无 d_0 时门户应拒（若回滚窗口已过，可在抉择前单独测一次） | 生存踩门 | **真** |
| 有 d_0 踩 `AaroncosArenaPortals` 进场 | | **真** |
| GUARD / terrorbeak 调度 | 进场事件真；Boss 伤害至死可加速 | 混合 |
| 胜利 e_0、窥视移除、唯一 410t 倒计时 | `PDArenaBossManager` | **真** |
| 右键手箱 → **shadow 天赋**掉落 + 取消强制离 | 真开箱（`talent_shadow`） | **真** |
| 离场主世界；e_0 保留 | | |

**不测**：未开箱强制离全长（`second-dream` 专项）；非无名的其它闲聊 NPC；入侵整段刷怪表演（只要求 calm→npc_3）。

### 3.5 Phase 4 — 风之旅途

依赖 Phase1 保留的 `b_0` + `hide_16`。

| 步骤 | 做法 | 真/夹具 |
|---|---|---|
| 回主世界；获得 `queer_soup` | 优先配方；否则 give | 夹具（获得） |
| **真实食用** → fondillusion | 物品 use | **真** |
| Y≥306 + buff tick → 进风维 | `fondillusionTick` | **真** |
| 风维 cloudmist 续效 | SanHelper / 效果 | **真** |
| 祭坛 0→4 | 摆 spawnblock 链，投物+闪电法术，真实阶段 | **真** |
| 击败 wind_knight | 死亡与 loot 真路径 | 击杀可加速 |
| cloudmist + Y≤5 → 主世界 Y≈304 | `cloudmistTick` | **真** |

**不测**：自然 locate 遗迹、风向日更、破风幕全图。

### 3.6 Phase 5 — 收尾

- 汇总 phase pass/fail/skip。
- `continuousFlags` 终检（见 §4）。
- `KEEP_OPEN=0` 写报告并停服。

### 3.7 跨 phase 状态

| 保留 | 清理 |
|---|---|
| 主线成就（含 npc_0..5、d_0、talent_shadow、e_0） | 测试刷的敌对实体；**光明回滚时**临时 talent_light / 白水晶 |
| 必要任务物品（钥、shadow_hilt、针等，限数量） | 临时结构、多余掉落、对话见面礼金块可清 |
| 未完成的主线 schedule | 测试残留 schedule（按 phase 窗口 advance；对话段必须 advance 到 endDialogue） |
| 生存为主 | 临时创造/飞行结束还原 |

### 3.8 对话与抉择时长预算（压缩后）

| 段 | advance 下限（约） |
|---|---|
| Stage0–5 合计 | ~560+600+160+500+260 ≈ **2100 t**（+缓冲） |
| 入侵 calm | 视驱动方式，预留数百 t |
| 抉择黑暗旁白 | 260 t |
| 竞技场 410 t 倒计时 | 可提前开箱取消，预留 50–410 t |

Phase3 墙钟粗估 **4–8 min**（含 advance）；全 main-flow **约 8–18 min**。

---

## 4. 失败策略与报告

### 4.1 失败策略

| 规则 | 行为 |
|---|---|
| 断言失败 | 记录 phase + name + detail；不立刻杀进程 |
| Phase 内 | 局部断言默认可继续；硬前置已毁则本 phase 剩余 **SKIP** |
| 跨 phase | 硬失败后后续 phase **SKIPPED_DEPENDENCY**；Phase5 仍汇总 |
| 未捕获异常 | catch → fail + dump；尽量回主世界锚点 |
| 退出码 | 任一 fail → 失败；仅 skip 无 fail → 成功但报告注明 |

**Dump**（失败或 phase 边界可选）：维度、XYZ、GameType、关键成就、背包 top N、phase、scheduler 队列深度（若可观测）。

### 4.2 报告格式

扩展 `PasterDream/run/pd_verify_report.json`（旧字段兼容）：

```json
{
  "suite": "main-flow",
  "phases": [
    {"id": "P1_dyedream", "pass": 12, "fail": 0, "skip": 0},
    {"id": "P2_twilight", "pass": 8, "fail": 1, "skip": 3}
  ],
  "results": [
    {
      "phase": "P2_twilight",
      "pass": false,
      "name": "真影床进灯影",
      "detail": "still overworld"
    }
  ],
  "continuousFlags": {
    "a_0": true,
    "b_0": true,
    "hide_16": true,
    "entered_lamp": true,
    "npc_0": true,
    "npc_1": true,
    "npc_2": true,
    "npc_3": true,
    "npc_4": true,
    "npc_5": true,
    "choice_light": true,
    "choice_dark": true,
    "d_0": true,
    "talent_shadow": true,
    "e_0": true,
    "wind_enter": false,
    "wind_exit": false
  }
}
```

控制台：每 phase 一行汇总；总评 `MAIN-FLOW x/y pass`。

### 4.3 continuousFlags（终检）

| 标志 | 含义 |
|---|---|
| `a_0` / `b_0` / `hide_16` | 染梦线前置 |
| `entered_lamp` | 曾进入 `lamp_shadow_world`（真影床进维路径） |
| `npc_0`…`npc_5` | 无名对话/入侵链完整 |
| `choice_light` / `choice_dark` | 影之抉择两按钮均真实点击成功 |
| `d_0` / `talent_shadow` | 连续线保留的黑暗抉择结果 |
| `e_0` | 竞技场胜利 |
| `wind_enter` / `wind_exit` | 迷梦高空进维与 cloudmist 低 Y 出维 |

---

## 5. 风险与缓解

| 风险 | 缓解 |
|---|---|
| 守卫波 / 410t / 祭坛 / **对话 2k+t** 过长 | advance；phase tick 预算，超时 fail |
| Scheduler 跨 phase / 对话未 end 就下一段 | 每段 advance 到锁释放；禁止 overlapping interact |
| 影之抉择 | **必须** `openMenu` + `clickMenuButton`；禁止 grant d_0 跳过 |
| 光明回滚不干净 | revoke 双成就 + 清白水晶后再开黑暗；断言菜单可再次打开 |
| 入侵 npc_3 随机 | 确定性驱动 persistent + 真 calm；禁止 award npc_3 |
| Stage2 配置关 TP | 读 `SHADOW_NPC_THIRD_DIALOGUE_AFTER_TP_*`；若关则断言仍授 npc_2，并 **强制走一次离灯影** 挂窥视 |
| 床的昼夜/风暴 | phase 内强制时间天气 |
| Boss AI 不稳 | 进场事件真；击杀可加速 |
| 超平无结构 | 全程摆放；locate 自然命中不作硬断言 |
| KEEP_OPEN 观察 | 支持；失败保留现场 |
| CI 时长 | 默认不进 `all`；IDE/专用命令跑 |

**时长粗估（压缩后）**：P0–1 ~0.5–1 min；P2 ~1–3 min；P3 ~4–8 min（对话+抉择+竞技场）；P4 ~1–2 min；合计约 **8–18 min**。

---

## 6. 实现边界（计划阶段拆任务）

1. `Suite.MAIN_FLOW` + timeline 挂载 + 排除 `all`
2. `PDMainFlowVerifyHooks` phase 状态机 + 报告 phase / continuousFlags
3. main-flow 运行时：强制时间天气、生存切换、dump helper
4. Phase1–2、Phase4（对齐现有专项摆件与门控，连续状态）
5. **Phase3 核心**：无名全对话驱动 + 入侵 calm→npc_3 + 真影床抉择光/暗 + 竞技场
6. `.run` 配置 + README / `验证复现.md` / `功能状态.md`
7. 本地跑通至 continuousFlags 全 true，或记录已知 FAIL 与原因

### 6.1 计划阶段再定（不阻塞本设计）

- 入侵确定性驱动的精确字段与是否抽测试专用 package-private 钩子（默认优先公开/现有 tick）
- 各 phase tick 预算具体数字
- helper 抽取范围（新建 `PDVerifyFixtures` vs 先私有在 MainFlow）
- 报告是否写 wall-clock ms
- 是否在报告中记录 `choice_order: light_then_dark`

---

## 7. 关键源码锚点

| 区域 | 路径 |
|---|---|
| VERIFY 时间线 | `smoketest/PDPortingVerifyTest.java` |
| 创世 | `smoketest/PDSmokeTest.java` |
| 现有专项 | `PDTwilightLanternVerifyHooks` / `PDSecondDreamVerifyHooks` / `PDWindJourneyVerifyHooks` |
| 调度 | `util/ServerScheduler.java` |
| 笔记 | `dreamnotes/DreamnotesLogic.java` |
| 暮影/床 | `block/TwilightLanternBlock.java` · `TrueShadowBedBlock.java` |
| **无名对话** | `entity/mob/ShadowNpc0Entity.java` |
| **影之抉择** | `menu/ShadowSelectEndMenu.java` · `client/screen/ShadowSelectEndScreen.java` |
| 灯影进出 | `registry/LampShadowEvents.java` |
| **入侵 / npc_3** | `registry/PDEffects.java`（`shadowIntrudeTick` / `shadowIntrudeCalm`） |
| 竞技场 | `registry/PDArenaBossManager.java` · `PDArenaEvents.java` |
| 迷梦/云雾 | `registry/PDEffects.java`（fondillusion / cloudmist tick） |
| 风旅事件 | `world/WindJourneyEvents.java` · `PDSanHelper.java` |

---

## 8. 审批记录

- 2026-07-29：方案 A + 主干全链路 + 尽量全真 + 单客户端长测；§1–§3 设计批准。
- 2026-07-29 **修订**：用户要求 **真实全无名 NPC 对话** + **光与影抉择均测**。Phase3 扩展为 npc_0..5 真交互、入侵 calm→npc_3、抉择 `clickMenuButton` 先光后暗（回滚后保留黑暗连续线）；取消「抉择等价后门 / 不测全 NPC」表述；时长与 continuousFlags 同步更新。
