# 设计 · VERIFY 主干全链路连续流程测试（main-flow）

> **日期**：2026-07-29  
> **状态**：已批准设计 · 待实现计划  
> **范围**：单客户端 VERIFY 套件，串起染梦 → 暮影 → 灯影/竞技场 → 风旅  
> **玩法参考** → [`docs/第二梦境.md`](../../第二梦境.md) · [`docs/第三梦境.md`](../../第三梦境.md) · [`docs/暮影之笼.md`](../../暮影之笼.md)  
> **验证入口** → [`docs/验证复现.md`](../../验证复现.md)

---

## 0. 一句话

新增专项 VERIFY 套件 **`main-flow`**（默认不进 `all`）：在一次 `runClient` 内按叙事顺序连续驱动主线门控，证明「主要流程能连续进行」；结构出现用摆放，门控与结算走真实代码路径，长等待用 `ServerScheduler.advanceForTest` 压缩。

---

## 1. 背景与目标

### 1.1 现状

- 已有隔离专项：`twilight-lantern`、`second-dream`、`wind-journey`（均不在默认 `all`）。
- 各专项假设相对干净的玩家/局部摆件；`ServerScheduler.advanceForTest` 与成就/背包会互相污染，**不能**简单 `SUITES=a,b,c` 当成连续主线。
- 默认 VERIFY 世界：超平、关结构生成、关昼夜/天气、EASY（VERIFY 时）；适合稳定回归，不适合自然 locate 漫游。
- **没有**跨章节连续主线测试。

### 1.2 目标

单次 `runClient` 跑通主干：

**染梦起步 → 暮影据点 → 灯影/地牢/竞技场 → 风之旅途进·打·出**

成功标准：

- 关键门控与结算走生产代码路径（交互、buff tick、Y 阈值、BossManager、出维条件等）。
- 报告含 phase 与 `continuousFlags`；墙钟目标约 **5–12 分钟**（视机器与 advance 策略）。
- 失败可定位到 phase，后续 phase 依赖跳过，避免雪崩误报。

### 1.3 非目标

- 不替换三个现有专项（细节密度与边界回归仍靠它们）。
- 不把 `main-flow` 并入默认 `all`。
- 不覆盖全书笔记、全部支线、自然 `/locate` 漫游、客户端渲染/音效/UI 手感。
- 不强制开 DEFAULT 世界赌结构生成（见 §2 创世策略）。

### 1.4 已拍板决策

| 项 | 选择 |
|---|---|
| 覆盖范围 | 主干全链路（染梦 → 暮影 → 第二梦 → 风旅） |
| 门控真实度 | 尽量全真触发 |
| 运行形态 | 单客户端长测 |
| 实现形态 | **方案 A**：新套件 `main-flow`，非串联旧专项 |

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

### 3.4 Phase 3 — 灯影 / 地牢 / 竞技场

连续状态从上阶段带入。

| 步骤 | 做法 | 真/夹具 |
|---|---|---|
| 钥匙拾取 → 门 cascade | 摆门+钥，生存交互 | **真** |
| `d_0` 门槛 | 无 d_0 应拒；有 d_0 可进 | **真** |
| 取得 d_0 | 优先自动化影之抉择；若无稳定 UI API → 仅 VERIFY/`main-flow` 的 **选择等价** 服务端触发，报告标 `fixture:choice-equivalent` | 尽量真 |
| 踩竞技场门户进场 | `AaroncosArenaPortals` | **真** |
| GUARD / terrorbeak 调度 | 进场事件真；Boss 可用伤害至死加速 | 混合 |
| 胜利 e_0、窥视移除、唯一 410t 倒计时 | `PDArenaBossManager` | **真** |
| **固定分支 A**：右键手箱 → 天赋掉落 + 取消强制离 | 真开箱 | **真** |
| 离场后在主世界；e_0 保留 | | |

**不测**：全 NPC 对话周目、影子入侵长段、未开箱强制离（归 `second-dream` 专项）。

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
| 主线成就 | 测试刷的敌对实体 |
| 必要任务物品（限数量） | 临时结构、多余掉落 |
| 未完成的主线 schedule | 测试残留 schedule（按 phase 窗口 advance，禁止无界误 drain） |
| 生存为主 | 临时创造/飞行结束还原 |

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
    "entered_lamp": false,
    "d_0": false,
    "e_0": false,
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
| `entered_lamp` | 曾进入 `lamp_shadow_world`（真影床路径） |
| `d_0` / `e_0` | 竞技场资格与胜利 |
| `wind_enter` / `wind_exit` | 迷梦高空进维与 cloudmist 低 Y 出维 |

---

## 5. 风险与缓解

| 风险 | 缓解 |
|---|---|
| 守卫波 / 410t / 祭坛过长 | advance；phase tick 预算，超时 fail |
| Scheduler 跨 phase 污染 | 只 advance 本 phase 登记窗口 |
| 影之抉择 GUI | 优先真 UI/包；否则 `fixture:choice-equivalent` 并文档标明 |
| 床的昼夜/风暴 | phase 内强制时间天气 |
| Boss AI 不稳 | 进场事件真；击杀可加速 |
| 超平无结构 | 全程摆放；locate 自然命中不作硬断言 |
| KEEP_OPEN 观察 | 支持；失败保留现场 |
| CI 时长 | 默认不进 `all`；IDE/专用命令跑 |

**时长粗估（压缩后）**：P0–1 ~0.5–1 min；P2 ~1–3 min；P3 ~2–4 min；P4 ~1–2 min；合计约 **5–12 min**。

---

## 6. 实现边界（计划阶段拆任务）

1. `Suite.MAIN_FLOW` + timeline 挂载 + 排除 `all`
2. `PDMainFlowVerifyHooks` phase 状态机 + 报告 phase / continuousFlags
3. main-flow 运行时：强制时间天气、生存切换、dump helper
4. Phase1–4 步骤（对齐现有三套 hooks 的摆件与门控，改为连续状态）
5. `.run` 配置 + README / `验证复现.md` / `功能状态.md`
6. 本地跑通至 continuousFlags 全 true，或记录已知 FAIL 与原因

### 6.1 计划阶段再定（不阻塞本设计）

- 影之抉择自动化的最终 API 形状
- 各 phase tick 预算具体数字
- helper 抽取范围（新建 `PDVerifyFixtures` vs 先私有在 MainFlow）
- 报告是否写 wall-clock ms

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
| 灯影进出 | `registry/LampShadowEvents.java` |
| 竞技场 | `registry/PDArenaBossManager.java` · `PDArenaEvents.java` |
| 迷梦/云雾 | `registry/PDEffects.java`（fondillusion / cloudmist tick） |
| 风旅事件 | `world/WindJourneyEvents.java` · `PDSanHelper.java` |

---

## 8. 审批记录

- 2026-07-29：方案 A + 主干全链路 + 尽量全真 + 单客户端长测；§1–§3 设计批准。
