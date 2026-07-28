# 设计 · 暮影据点真·要塞式世界生成 + locate

> **日期**：2026-07-28  
> **状态**：已实现 · 计划 [`../plans/2026-07-28-shadow-world-door-structure.md`](../plans/2026-07-28-shadow-world-door-structure.md)  
> **VERIFY**：`twilight-lantern` **36/0**（2026-07-29）；Locator 超平 SKIP 预期内  
> **范围备注**：本设计原不含灯影 spawn / hide_7 / 返程；后续 P0 已另实现，见 todo  
> **玩法参考** → [`docs/暮影之笼.md`](../../暮影之笼.md)  
> **缺口跟踪** → [`docs/todo_暮影之笼流程缺口.md`](../../todo_暮影之笼流程缺口.md)

---

## 0. 一句话

主世界暮影之笼据点改为 **数据驱动结构**（jigsaw 单模板 + `concentric_rings` structure_set），坐标一律经 **`findNearestMapStructure`** 获取；笔记 8/9 全改 locate，**不再**用 `randomCoord*21` 公式。  
（设计原文非目标：灯影 spawn / hide_7 / 返程 — **后续 P0 已另实现**，见 todo / VERIFY 36/0。）

---

## 1. 背景与目标

### 1.1 现状（Neo）

- 事件本体（点燃 → 刷怪 → key/hide_9 → 真影床）已与原版高度一致。
- 主世界 **无** 自动据点：原版 `GenerateWorldPr0` Load 放置未移植。
- 笔记坐标读 `randomCoordX/Z` 公式 `r*21 + offset`，与门本应同公式，但门不生成则坐标空指。
- `structure_block_9` / 调试杖可放模板（VERIFY 已证），仅调试旁路。

### 1.2 原版 Paster 生成（对照，不照搬）

- `GenerateWorldPr0` @ Level Load：若 `randomCoordX/Z` 均为 0 则随机 `[-100,100]`，在  
  `(r*21 - 22, -60, r*21 - 21)` `placeInWorld(shadow_world_door)`。
- 笔记写同一公式。**不是** 结构集，**不能** `/locate`。

### 1.3 原版 MC 末影之眼（可取坐标参考）

- `EnderEyeItem.use` → `ServerLevel.findNearestMapStructure(StructureTags.EYE_OF_ENDER_LOCATED, pos, 100, false)`。
- `EyeOfEnder.signalTo(BlockPos)`：实体只飞向给定点，不自带「查表」。
- 要塞：`structure_set/strongholds.json` 使用 `minecraft:concentric_rings`。

### 1.4 目标

| # | 目标 |
|---| :--- |
| G1 | 主世界 **chunkgen** 自然出现含 `twilight_lantern` + `true_shadow_bed` 的据点 |
| G2 | 坐标 **可程序获取**：统一 Locator → `findNearestMapStructure` |
| G3 | 笔记 8/9 **全改 locate**，与真实最近据点一致 |
| G4 | placement 为 **concentric_rings**（要塞同类）；具体数字 **先可运行默认，后调** |
| G5 | 调试触发块/杖保留；不破坏已有事件逻辑 |

### 1.5 非目标

- 灯影 `shadow_world_spawn` 自动放置  
- Warden → hide_7 / sculk_heart  
- 返程裸 `spawn` 兜底  
- 新「类末影之眼」物品（Locator 预留即可）  
- 修改 `shadow_world_door.nbt` 建筑内容  
- 把暮影门加入 `minecraft:eye_of_ender_located`（避免原版之眼误指）  
- 锁死 rings 最终数值或做 Common Config（首版不做配置项）

---

## 2. 决策记录

| 决策 | 选择 | 备选否决 |
| :--- | :--- | :--- |
| 生成机制 | 真结构 + structure_set | Load 强制 place；仅 API |
| 坐标消费 | 全改 locate | 双写 randomCoord；笔记仍公式 |
| Placement | `concentric_rings` | `random_spread` 作退路 |
| 参数策略 | 先链路后调参 | 设计锁死 / 配置项 |
| 实现形态 | **数据驱动 jigsaw**（方案 A） | 自定义 StructureType place NBT（B） |

**方案 A 推荐理由**：与现有 `PDRuinsRegistration` / 教堂栈一致；模板复用 NBT；`findNearestMapStructure` 对任意结构 tag 可用；无需新 StructureType/CODEC。RuinAPI 的 set 生成器只熟 `random_spread` 字段 → rings **手写 JSON**。

---

## 3. 架构与数据流

```text
[datapack]
  worldgen/structure/shadow_world_door.json
  worldgen/template_pool/shadow_world_door.json  → structure NBT shadow_world_door
  worldgen/structure_set/shadow_world_doors.json   concentric_rings
  tags/worldgen/structure/twilight_lantern_located.json
  tags/worldgen/biome/has_structure/shadow_world_door.json

[chunkgen]
  rings 候选 chunk → jigsaw size=1 → 深层 Y≈-60 落模板（笼+真影床）

[消费]
  笔记 8/9 · 调试 ·（日后追踪物）
        │
        ▼
  PDShadowDoorLocator.locate(level, origin, radius)
        │
        ▼
  findNearestMapStructure(TWILIGHT_LANTERN_LOCATED, …)
        │
        ▼
  Optional<BlockPos> → NBT x/z · 聊天 · 可选 EyeOfEnder.signalTo
```

### 3.1 单元边界

| 单元 | 职责 | 不负责 |
| :--- | :--- | :--- |
| datapack 三件套 + tags | 生成与可 locate | 事件、笔记文案 |
| `PDShadowDoorLocator` | **唯一**读坐标入口 | place、写 gamerule |
| `DreamnotesLogic` | 解锁时调用 locator 写 NBT | 自己算 r\*21 |
| `structure_block_9` / 杖 | 调试旁路 | 生存主路径 |
| 笼子/床事件 | 不变 | 世界生成 |

### 3.2 有意分叉（相对原 Paster）

- 不再 Level Load 强制 `placeInWorld`。  
- 不再用 `randomCoordX/Z` 驱动门坐标（gamerule **保留注册**，避免旧指令/档异常）。  
- 世界可有 **多座**；locate 取 **最近**。  
- 散布由 rings 参数决定，不保证落在原 ±2100 量级。

---

## 4. 组件与文件清单

### 4.1 新建 · datapack

| 路径 | 内容要点 |
| :--- | :--- |
| `data/pasterdream/worldgen/structure/shadow_world_door.json` | `type: minecraft:jigsaw`；`biomes: #pasterdream:has_structure/shadow_world_door`；`step: underground_structures`；`terrain_adaptation: none`（首版；后可 `bury`）；`start_pool: pasterdream:shadow_world_door`；`size: 1`；**无** `project_start_to_heightmap`；`start_height: { "absolute": -60 }`；`max_distance_from_center` ≥ 80；`use_expansion_hack: false` |
| `data/pasterdream/worldgen/template_pool/shadow_world_door.json` | 单 `single_pool_element`，`location: pasterdream:shadow_world_door`，`projection: rigid`；processors 可 `block_ignore` structure_block（与教堂一致） |
| `data/pasterdream/worldgen/structure_set/shadow_world_doors.json` | 见 §5 默认 rings |
| `data/pasterdream/tags/worldgen/biome/has_structure/shadow_world_door.json` | 起步包含 overworld 可生成集合（`#minecraft:is_overworld` 或实现时以能生成+locate 为准的等价写法） |
| `data/pasterdream/tags/worldgen/structure/twilight_lantern_located.json` | `values: ["pasterdream:shadow_world_door"]` |

模板 NBT 已在：`data/pasterdream/structure/shadow_world_door.nbt`（不改内容）。

### 4.2 新建 · Java

| 路径 | 职责 |
| :--- | :--- |
| `com.pasterdream.pasterdreammod.worldgen.PDShadowDoorLocator`（包名实现时可落 `util`/`structure`，保持单类） | `TagKey<Structure> TWILIGHT_LANTERN_LOCATED`；`locate(ServerLevel, BlockPos, int radius) → Optional<BlockPos>` |

可选：在 `PDRuinsRegistration` 旁增加注册日志/表项 **仅当** 需要进现有 VERIFY 结构名册；**不必** 自定义 StructureType（使用原版 `jigsaw`）。

### 4.3 修改

| 路径 | 改动 |
| :--- | :--- |
| `dreamnotes/DreamnotesLogic.java` | `writeCoords` → locator；失败不写假坐标；去掉 randomCoord 反射读 |
| `smoketest/PDTwilightLanternVerifyHooks.java` | 正向：datapack 在场、locate/笔记；删除「无自动笼 = 缺口 PASS」 |
| `docs/暮影之笼.md` | §1 改为结构集 + locate；randomCoord 废弃驱动 |
| `docs/todo_暮影之笼流程缺口.md` | P0 主世界门改为本设计；勾选标准更新 |
| `docs/功能状态.md` | 开放项表述与生成机制同步 |

### 4.4 明确不改

- `TwilightLanternBlock` / `TrueShadowBedBlock` 事件  
- `PDStructureBlock` SPECS 9  
- `PDGameRules.RANDOM_COORD_*` 注册本身  
- 灯影、hide_7、返程相关类  

---

## 5. Placement 默认（可后调，非最终手感）

```json
{
  "structures": [
    { "structure": "pasterdream:shadow_world_door", "weight": 1 }
  ],
  "placement": {
    "type": "minecraft:concentric_rings",
    "distance": 32,
    "spread": 3,
    "count": 64,
    "salt": 26072801,
    "preferred_biomes": "#minecraft:is_overworld"
  }
}
```

实现阶段若 `preferred_biomes` / biome tag 导致 0 候选，**优先改到能生成+locate**，再开 PR 拧手感。不在首版加 Common Config。

Y：**absolute -60**（对齐原版 place 深度语义）。大模板穿插洞穴为已知风险，首版不阻塞；后续可 processors / adaptation。

---

## 6. API 与笔记行为

### 6.1 `PDShadowDoorLocator`

```text
locate(ServerLevel level, BlockPos origin, int radius) → Optional<BlockPos>
```

| 规则 | 值 |
| :--- | :--- |
| 内部 | `level.findNearestMapStructure(TWILIGHT_LANTERN_LOCATED, origin, radius, false)` |
| 默认 radius | **100**（与 `EnderEyeItem` 相同 API 语义） |
| 维度 | **仅 OVERWORLD**；其它维 → empty |
| 副作用 | 无 place、无写 gamerule、无加载笔记 |

### 6.2 笔记 8 / 9

| 时机 | 行为 |
| :--- | :--- |
| 解锁 hide_8 / hide_10 需写坐标 | locate(player) → 成功：`switch=true`，`x/z=pos`，原格式打印 |
| 已解锁后再读需刷新 | 再 locate（多座时可能变「最近」） |
| locate 失败 | 不写/不保留假 x/z；提示「尚未感应到暮影据点的方位」（文案可微调）；不抛 |
| 创造笔记 9 分支 | 同样 locate，禁止 r\*21 |

删除：`writeCoords` 内 `readRandomCoord` 与 `r*21+offset`（含 -22/-21 偏移分支——locate 已指向结构锚点，**不再**手工减模板角偏移；若显示点与笼子有固定像素差，实现时用一次游戏内校准决定是否加常量偏移，**写进实现计划而非本 spec 锁死**）。

### 6.3 错误与边界

| 情况 | 处理 |
| :--- | :--- |
| `generateStructures == false` | locate null → 笔记失败文案 |
| 测试超平 / biome 不匹配 | 同上；VERIFY 需开结构或可生成设置 |
| datapack 缺失 | 注册/资源断言失败 |
| 多座 | 永远最近 |
| 与调试手动放置共存 | locate 可能命中手动或自然座；可接受 |

---

## 7. 测试计划

套件：现有 `twilight-lantern`（仍默认 **不** 进 `all`，直到稳定）。

| # | 断言 | 期望 |
|---| :--- | :--- |
| T1 | structure / template_pool / structure_set / 两 tag 可解析 | PASS |
| T2 | `Locator.locate` 在 overworld | 非 empty；若当前 VERIFY 世界无法生成结构则 **显式 skip + 报告原因**，不得假绿 |
| T3 | 笔记路径：grant 前置 → use → NBT x/z 与同次 locate 一致 | PASS |
| T4 | 删除旧「主世界抽样无笼 = 缺口确认 PASS」 | 改为正向或移除 |
| T5 | `structure_block_9` 仍能落地笼+床 | 保留（调试旁路） |
| T6 | 裸 `spawn` / hide_7 缺口确认 | **可暂留**（非本设计，仍属 todo 其它 P0） |

手工：`/locate structure pasterdream:shadow_world_door`（或 tag）在新档 overworld 有结果；笔记显示坐标可抵达据点。

---

## 8. 文档与迁移

- `暮影之笼.md` §1：自然来源 = structure_set + rings；触发块 = 调试；坐标 = locate。  
- `todo_暮影之笼流程缺口.md`：主世界 P0 勾选条件改为本 spec 验收；P0.5 已关不变。  
- `功能状态.md` §3：开放项改为「暮影据点结构集 + 笔记 locate」进度。  
- `randomCoord`：文档标明「历史/未再驱动门」。  

旧档：已探索区块 **不会** 回溯生成；新探索 rings 候选 chunk 会生成。不提供自动迁移 place。

---

## 9. 实现顺序（供 writing-plans，非本 spec 详工单）

1. datapack：pool → structure → biome tag → structure_set → locate tag  
2. `PDShadowDoorLocator`  
3. `DreamnotesLogic` 改 locate  
4. VERIFY 套件改向 + 本地 runClient  
5. 文档同步  
6. （另任务）灯影 spawn / hide_7 / 返程  

---

## 10. 自检

| 检查 | 结果 |
| :--- | :--- |
| 无 TBD 实现空洞 | 校准偏移留给实现计划，已标明 |
| 与决策一致 | 真结构、rings、全 locate、后调参、方案 A |
| 范围 | 单系统可一计划落地；灯影/hide_7/spawn 已排除 |
| 歧义 | 多座=最近；仅 overworld；不进原版之眼 tag |

---

*审阅通过后 → `writing-plans` 出实现计划 → 再动代码。*
