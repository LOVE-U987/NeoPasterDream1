# 风旅祭坛阶段模型/贴图不切换 · 修复计划

> **类别**：计划 · 缺陷修复  
> **日期**：2026-07-30  
> **状态**：**已实施**（2026-07-30）  
> **关联开放项**：[`功能状态.md`](../../功能状态.md) §3.2「风之旅途 P0.5 剩余手测：祭坛手感」  
> **玩法参考**：[`archive/游戏流程分析/第三梦境.md`](../../archive/游戏流程分析/第三梦境.md)  
> **VERIFY**：`wind-journey` / `main-flow`（目前只断言方块阶段与召唤，**不**断言客户端 mesh）

---

## 1. 问题陈述

玩家修复破风骑士祭坛（`wind_knight_spawnblock_0` → `_4`）时，**逻辑阶段会推进**（耗材、音效、粒子、最终召唤），但**观感上不按进度切换模型/贴图**（始终像底座或某一固定阶段）。

---

## 2. 结论（根因摘要）

| # | 结论 | 严重度 |
|:-:|:---|:---:|
| A | **进度不是「单方块 + 属性」，而是五个独立方块 ID**；推进靠 `setBlock` 换成下一 stage。逻辑路径与原版 procedure 对齐，VERIFY 已覆盖 0→4。 | 背景 |
| B | **贴图「不换」在原版即如此**：五阶段 `getTextureResource` 都指向同一张 `textures/block/wind_knight_spawnblock.png`；Neo 多出来的 `_0.._4.png` 与主贴图 **md5 完全相同**，Defaulted 路径即使用分阶段文件也看不出色差。玩家若期望「每阶不同贴图」，属于增强，不是还原缺口。 | 低 / 预期 |
| C | **外观差异应主要来自 geo mesh**（骨与 cube 数：0→1→2→3→4 递增）。资源本身与原版 geo **逐字节一致**，且 `PDClientFurniture` 已按 stage 注册 `W4GeoBlockRenderer("wind_knight_spawnblock_"+i)`。 | 背景 |
| D | **最可疑的实现/运行时问题（优先查）**：换块后客户端 BER/BE 是否真正换成对应 type 的 renderer；`MapCodec` 恒构造 `stage=0` 是否在同步/重载路径写坏 `newBlockEntity`；以及 stage0→1 **立即** `setBlock` vs 1→4 **延后 1t** 的客户端刷新差。 | **高** |
| E | geo 内 `identifier` 五阶段都写成 `geometry.wind_knight_spawnblock_0`（原版也如此）。GeckoLib 4.8 缓存键是 **文件 ResourceLocation**，不是 identifier，**单独不构成缓存串台**；仍建议修正以免工具链/未来版本踩坑。 | 低 |
| F | 动画五阶段文件内容相同（仅 idle `"0"`）；**阶段观感不靠 animation**。 | 背景 |
| G | 资源可能同时存在扁平 `geo/`·`animations/` 与 Defaulted 用的 `geo/block/`·`animations/block/` 副本；运行时以 Defaulted 路径为准，避免改错目录。 | 中（实施时） |

一句话：**还原向的「贴图每阶不同」不成立；若 mesh 也不变，优先查换块后的 BE/BER 绑定与 Codec/stage 字段，而不是缺资源。**

> Explore 子代理结论与上表一致，并强调：新版从「每 stage 独立 Model 类硬编码路径」改为「BE type 注册名 + Defaulted 派生路径」；理论应切换，实际不切换时优先 CODEC/BE recreate/纹理无差。

---

## 3. 证据链

### 3.1 阶段如何推进（逻辑 OK）

- 方块：`PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_0..4` → 同一类 `WindKnightSpawnblockBlock(stage, props)`  
  `PasterDream/.../registry/blocks/PDBlocksFurniture.java`
- 交互：`WindKnightSpawnblockBlock.interact`  
  - 0 + 风行者水晶 → 立即 `replaceKeepingProperties(..., _1)`  
  - 1/2/3 + 凝风铁锭 → 音效/粒子后 `ServerScheduler.schedule(1, setBlock next)`  
  - 4 + 闪电法术 → 86t 后召唤并重置 `_0`  
  `PasterDream/.../block/WindKnightSpawnblockBlock.java`（约 148–246 行）
- 原版：`WindKnightSpawnblockPr0Procedure` 同样五方块 `setBlock` + 1 tick 队列（Forge `queueServerWork`）

### 3.2 渲染接线（静态上看是分阶段的）

- BE：每 stage 独立 `BlockEntityType` + `validBlock` 仅对应一方块  
  `PDBlockEntitiesFurniture.WIND_KNIGHT_SPAWNBLOCKS`
- BER：`PDClientFurniture.registerRenderers`  
  `new W4GeoBlockRenderer("wind_knight_spawnblock_" + i)` → API `DefaultedBlockGeoModel`  
  路径约定：`geo/block/{name}.geo.json`、`textures/block/{name}.png`、`animations/block/{name}.animation.json`
- GeckoLib 4.8.4：`GeckoLibCache` 按 **geo 文件 RL** bake；`DefaultedGeoModel.getModelResource` 返回构造时写入的 `modelPath`（每 renderer 实例固定 name）

### 3.3 资源（mesh 有差，贴图无差）

| 资源 | 0 | 1 | 2 | 3 | 4 |
|:---|--:|--:|--:|--:|--:|
| bones | 2 | 3 | 3 | 9 | 10 |
| cubes（合计） | 1 | 4 | 7 | 21 | 25 |
| 典型增量 | 底座 | +body | body 加厚 | +双臂 | +头 |
| geo vs 原版 | 字节一致 | 同 | 同 | 同 | 同 |
| animation vs 原版 | 一致（仅 `"0"` idle） | 同 | 同 | 同 | 同 |
| 贴图 md5 | 与原版 `wind_knight_spawnblock.png` **五份全相同** | | | | |

原版 BlockModel **显式**共用主贴图：

```text
textures/block/wind_knight_spawnblock.png   // stage 0..4 皆此
geo/wind_knight_spawnblock_{n}.geo.json
animations/wind_knight_spawnblock_{n}.animation.json
```

Neo Defaulted 则解析为分阶段贴图路径；因文件内容相同，**视觉等价于原版**。

### 3.4 高优先级疑点

1. **`MapCodec` 丢 stage**  
   ```java
   public static final MapCodec<WindKnightSpawnblockBlock> CODEC =
           simpleCodec(p -> new WindKnightSpawnblockBlock(0, p));
   ```  
   五个 DeferredBlock 各自 `new WindKnightSpawnblockBlock(i, p)` 在注册时 stage 正确；但任何走 `codec()` 反序列化/复制的路径会得到 **stage=0** 的实例，导致 `newBlockEntity` 使用 `WIND_KNIGHT_SPAWNBLOCKS.get(0)`。需确认 1.21.1 方块状态同步/世界加载是否触碰该 codec。

2. **换块后 BE 类型切换**  
   stage 变化 = 不同 `Block` + 不同 `BlockEntityType`（同类 `W4GeoDataBlockEntity`）。若客户端未销毁旧 BE / 未换 BER，会继续用 stage0 的 `DefaultedBlockGeoModel`。应对：F3+I / 日志打印 BE type registry name；断点 `BlockEntityRenderer` 的 modelPath。

3. **0→1 立即 setBlock，1→4 延后 1t**  
   与原版一致；若仅 0→1 外观不变、后面才变，偏向客户端刷新；若全程不变，偏向 BER/BE/Codec。

4. **geometry identifier 重名**（原版遗留）  
   五文件皆 `geometry.wind_knight_spawnblock_0`。当前 GeckoLib 不按此键缓存；修复计划仍建议改成 `..._1`…`_4` 以防工具或后续库行为变化。

5. **blockstate JSON** 仅 particle 模型 + `RenderShape.ENTITYBLOCK_ANIMATED`：正常，不负责阶段外观。

---

## 4. 原版 vs Neo 对照

| 项 | 原版 (1.20.1 MCreator) | Neo (1.21.1) | 是否问题 |
|:---|:---|:---|:---:|
| 阶段推进 | 5 block + procedure setBlock | 5 block + `WindKnightSpawnblockBlock.interact` | 否 |
| Tile/BE | 每 stage 独立 TE + Renderer/Model 类 | 每 stage 独立 BE type + `W4GeoBlockRenderer(name)` | 设计等价 |
| geo 路径 | `geo/{name}.geo.json` | `geo/block/{name}.geo.json`（Defaulted subtype） | 否（文件在） |
| 贴图 | **固定** `wind_knight_spawnblock.png` | Defaulted → `wind_knight_spawnblock_{n}.png`（内容相同） | 观感同原版 |
| animation | `animations/{name}.animation.json` | `animations/block/{name}.animation.json` | 否 |
| Codec/stage | 每 block 独立类，无共享 stage 字段 | **共享类 + Codec 写死 stage=0** | **疑点** |
| VERIFY | 无客户端 mesh 断言 | 只断言 block is stage N | 盲区 |

---

## 5. 修复策略

### 5.0 先复现与分诊（必做，0.5–1h）

1. 创造模式放 `_0`，F3 看 block id。  
2. 依次：水晶 → 铁锭×3 → 观察 **每一跳** 后：  
   - `/data get block ~ ~ ~` 或调试：BE 的 `BlockEntityType` 注册名是否变为 `wind_knight_spawnblock_n`  
   - 外形是否出现 body / 臂 / 头  
3. 直接 `/setblock` `_0`…`_4` 各一格并排：若并排五阶段 mesh 正确、仅交互推进后错误 → 专注 `replaceKeepingProperties`/调度；若并排也全像 `_0` → BER 资源或缓存。  
4. 物品栏拿 `_0`…`_4` DisplayItem：物品渲染是否分阶段（走 `W4GeoDisplayItemRenderer`，与方块 BER 同源 Defaulted）。

**分支判定**

| 现象 | 走向 |
|:---|:---|
| 并排 setblock 五阶段 mesh 都对，仅 use 推进后不对 | §5.1 换块/BE 刷新 |
| 并排 setblock 也不对 / 物品也不对 | §5.2 渲染资源绑定 |
| mesh 其实在变，只是贴图一样 | §5.3 文档/预期 或 可选美化 |
| 重进世界后 stage 方块变回 0 或 BE 错乱 | §5.1 Codec/stage |

### 5.1 换块与 BE/Codec（主修复候选）

有序步骤：

1. **修 `MapCodec`**（防御性必做）  
   - 为 stage 增加 `RecordCodecBuilder` 字段，或五阶段改回独立 Block 子类/独立 codec（对齐原版「一类一块」）。  
   - 保证 `newBlockEntity` 使用的 `stage` 与注册方块一致。  
   - 回归：世界保存/重进、结构放置、`/setblock`、交互推进后 BE type。

2. **审视 `replaceKeepingProperties`**  
   - flags=3 与原版一致；确认是否需要 `Block.UPDATE_ALL` 等以强制客户端 BE 重建（仅当复现证明 BE 滞留时再改，避免瞎加 flag）。  
   - 换块前后打调试日志：`oldBlock`、`newBlock`、`be.getType()` registry name。  
   - 若证实客户端 BE/BER 滞留：再评估 `setBlockAndUpdate`、显式 `sendBlockUpdated`、或（慎用）客户端预测 setBlock；**默认不对铁锭阶段做客户端预测**，以免与原版/服务端权威分叉。

3. **0→1 与 1→4 时序**  
   - 保持与原版一致优先；若仅客户端偶发不同步，再考虑 0→1 也 delay 1t（行为微调，需手感确认）。

4. **VERIFY 增强（服务端可做）**  
   - 推进后断言 `level.getBlockEntity(pos).getType()` 等于对应 `WIND_KNIGHT_SPAWNBLOCKS.get(n)`。  
   - 仍无法自动断言 mesh，但能锁 BE 绑定。

### 5.2 渲染绑定（若 §5.0 指向资源/BER）

1. 确认运行时 RL：  
   - `pasterdream:geo/block/wind_knight_spawnblock_n.geo.json`  
   - `pasterdream:textures/block/wind_knight_spawnblock_n.png`  
   - `pasterdream:animations/block/wind_knight_spawnblock_n.animation.json`  
2. 日志中是否有 GeckoLib `Unable to find model` / 错误版本 format。  
3. 可选：像原版一样 **显式 GeoModel**，贴图统一 `withAltTexture(.../wind_knight_spawnblock)`，模型/动画仍分 stage——减少「五张相同 png」的维护噪音。  
4. 修正 geo `description.identifier` 为每 stage 唯一（与文件名一致）。

### 5.3 贴图预期（产品）

- **还原**：保持单贴图（或五份相同文件），文档写明「阶段差在模型零件，不在贴图」。  
- **增强（非必须）**：美术出 `_1.._4` 差异贴图后再换文件；代码可不动。

### 5.4 明确不做

- 不为「补 oneshot」改 `ANIMATION` 驱动（与暮影灯同类：资源仅 idle `"0"`）。  
- 不把五个 stage 合并成单方块 integer property（除非单独立项重构；会动结构 NBT/存档）。  
- 不把祭坛内容上收到 API。

---

## 6. 实施任务拆分

| ID | 任务 | 产出 | 依赖 |
|:--:|:---|:---|:---|
| T0 | 手测分诊 §5.0（并排 setblock / 交互 / 物品 / 重进） | 现象记录 + 分支（5.1 / 5.2 / 5.3） | — |
| T1 | 修 Codec 或拆 class，保证 stage 与 BE type 一致 | 代码 + 重进世界用例 | T0 |
| T2 | 换块后 BE type VERIFY 断言 | `PDWindJourneyVerifyHooks` 或专项 | T1 |
| T3 | 若需：BER 路径日志 / 显式 Model+withAltTexture | 客户端代码 | T0 |
| T4 | geo identifier 唯一化（低风险资源修补） | 5 个 geo.json | 可并行 |
| T5 | 文档：功能状态祭坛条从「手感」细化为「阶段 mesh 已修/贴图预期如此」 | `功能状态.md` §3 | T1–T3 完成后 |
| T6 | 人工验收清单 §7 全勾 | 验收记录 | T1–T4 |

建议默认路径：**T0 → T1 → T2 →（按需 T3）→ T4 → T5/T6**。

---

## 7. 验收点（手测）

1. 并排 `/setblock` `_0`…`_4`：底座 → 躯干 → 更完整躯干 → 双臂 → 带头颅，差异可辨。  
2. 生存/创造交互完整 0→4：每消耗正确物品后 **立刻或 1t 内** 外形进入下一阶段。  
3. 错误物品：actionbar 提示不变，方块与外形不变。  
4. stage4 闪电法术后 86t：骑士+雷云、台座回 `_0` 外形。  
5. 断线重进 / 重开世界：停留在 `_n` 的祭坛 BE 与 mesh 仍为 n。  
6. 物品形态 `_0`…`_4` 与放置后一致（允许视角/缩放差）。  
7. `PASTERDREAM_VERIFY_SUITES=wind-journey` 仍 0 fail；若加了 BE type 断言则全绿。

---

## 8. 相关文件清单

**代码**

- `PasterDream/.../block/WindKnightSpawnblockBlock.java`
- `PasterDream/.../registry/blocks/PDBlocksFurniture.java`
- `PasterDream/.../registry/PDBlockEntitiesFurniture.java`
- `PasterDream/.../client/PDClientFurniture.java`
- `PasterDream/.../client/renderer/block/W4GeoBlockRenderer.java`
- `PasterDreamAPI/.../client/renderer/DefaultedGeoBlockRenderer.java`
- `PasterDream/.../client/renderer/item/W4GeoDisplayItemRenderer.java`
- `PasterDream/.../smoketest/PDWindJourneyVerifyHooks.java`
- 原版对照：`libs/FixPasterDream-main/.../procedures/WindKnightSpawnblockPr0Procedure.java`  
  `.../block/model/WindKnightSpawnblock{0-4}BlockModel.java`

**资源（Neo）**

- `assets/pasterdream/geo/block/wind_knight_spawnblock_{0-4}.geo.json`
- `assets/pasterdream/animations/block/wind_knight_spawnblock_{0-4}.animation.json`
- `assets/pasterdream/textures/block/wind_knight_spawnblock.png`（权威）
- `assets/pasterdream/textures/block/wind_knight_spawnblock_{0-4}.png`（与上相同内容）
- `assets/pasterdream/blockstates/wind_knight_spawnblock_{0-4}.json`
- `assets/pasterdream/models/item/wind_knight_spawnblock_{0-4}.json`
- `assets/pasterdream/models/custom/wind_knight_spawnblock_{0-4}_particle.json`（若存在）

---

## 9. 风险

- 改 Codec/拆 Block 类可能影响已放置方块的状态序列化——T1 后必须做「旧世界祭坛」回归。  
- 乱改 setBlock flags 可能导致多余更新或闪烁；以复现为准。  
- 只改贴图文件而不修 BE：若根因是 BER 滞留，美化无效。

---

## 10. 完成定义

- [ ] T0 分支判定写回本文件「实施记录」小节（可后补）  
- [ ] 并排 + 交互 + 重进三项 mesh 验收通过  
- [ ] 贴图策略（保持单贴图 / 或增强）在功能状态 §3.2b 或本计划中有一句定论  
- [ ] wind-journey VERIFY 绿；可选 BE type 断言已加  
- [ ] 不引入 API 边界违规（内容仍在主模）

---

*排查：主代理对照原版 procedure/Model + GeckoLib 4.8.4 源码缓存键；Explore 子代理并行扫目录。*

---

## 11. 实施记录（2026-07-30）

**分支判定（代码侧，等价 T0）**

| 检查 | 结果 |
|:---|:---|
| geo mesh 0..4 差异 | 有（bones/cubes 递增）；`geo/` 与 `geo/block/` 字节一致 |
| 贴图 | `_0.._4` 与主图 md5 相同 → **还原向预期如此**（阶段差在 mesh） |
| BER 注册 | 每 stage 独立 `W4GeoBlockRenderer("wind_knight_spawnblock_"+i)` |
| 静态 CODEC | **确认缺陷**：`simpleCodec(p -> new ...(0,p))` |
| 交互双端 | `useWithoutItem` 无 client early-return；`ServerScheduler` 为 JVM 静态队列 → 集成端有风险 |

**已改**

1. **T1** `WindKnightSpawnblockBlock`  
   - 每实例 `MapCodec` 闭包捕获 stage；`registerDefaultState(ANIMATION=0)`  
   - `newBlockEntity` 经 `stageOf(state.getBlock())` 解析 BE type（防字段写坏）  
   - 交互 **仅服务端** 推进/扣物/`ServerScheduler`；各 stage 分支 `return` 防连锁  
2. **T2** `PDWindJourneyVerifyHooks`：并排 setblock 0..4 + 交互每步后断言 `be.getType() == WIND_KNIGHT_SPAWNBLOCKS.get(n)`；召唤后回 stage0 同检  
3. **T4** 十份 geo（flat + `geo/block/`）`identifier` → `geometry.wind_knight_spawnblock_{n}`  
4. **T3** 未改 BER（资源路径已正确；贴图策略保持单图内容）

**构建**：`compileJava` SUCCESS  

**手测仍建议**：并排/交互 mesh 观感、重进世界（§7.1/2/5）。

