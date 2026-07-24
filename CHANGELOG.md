# Ver.1 — 新Bug审计问题核实报告

> **核实日期**：2026-07-25\
> **核实人**：MomoNyako\
> **对照文档**：[`审计状态_代码核实报告.md`](审计状态_代码核实报告.md)

## 审计概述

对项目进行的新一轮分析，目前已审查报告出18个问题，已确认18个

## 问题清单

| P级 | 问题 | 文件位置 | 状态 |
| :---: | --- | --- | :---: |
| P0 | **NaN速度导致崩溃** | `EntitySkillManager.java:260-261` | 已确认 |
| P1 | **`canContinueToUse()` 错误委托** | `ShadowSquealGhostEntity.java:91-95`<br>`ShadowSquealGhost0Entity.java:141-144` | 已确认 |
| P1 | **吼叫冷却永不自然衰减** | `TerrorbeakEntity.java:126` | 已确认 |
| P1 | **`isPlaying()` 永久为true** | `MusicPlaybackController.java:84-86` | 已确认 |
| P1 | **强制类型转换风险** | `LifeCrystalBlockEntity.java:205` | 已确认 |
| P1 | **跨生物群系时音乐切换滞后3秒** | `ModMusicManager.java:199-201` | 已确认 |
| P2 | **冗余 `instanceof` 检查** | `FoxFireEntity.java:165-166` | 已确认 |
| P2 | **`setSwitchCooldownTicks()` 为空操作** | `ModMusicManager.java:162` | 已确认 |
| P2 | **静默物品丢失** | `MeltdreamChestBlockEntity.java:256-266` | 已确认 |
| P2 | **每秒tick做 `instanceof` 检查** | `ShadowVortexBlock.java:66-74` | 已确认 |
| P2 | **着火时间过长** | `BoneWingFireBallProjectileEntity.java:139` | 已确认 |
| P2 | **文档与实际不符** | `MeltdreamChestBlock.java:47-48` | 已确认 |
| P2 | **羽毛粒子尺寸无限增长** | `FeatherWhiteParticle.java:87` | 已确认 |
| P2 | **银色粒子尺寸无文档化收缩** | `SilverParticle.java:67` | 已确认 |
| P3 | **每次技能触发重复创建数组** | `AaroncosLefthand0Entity.java:411` | 已确认 |
| P3 | **双端空ticker浪费CPU** | `DreamAccumulatorBlock.java:109-117` | 已确认 |
| P3 | **每tick创建临时对象** | `MinecraftMixin.java:46` | 已确认 |
| P3 | **影子NPC动画延迟** | `ShadowNpc0Entity.java:139-148` | 已确认 |

## 统计摘要

| P级 | 报告数 | 确认数 | 占比 |
|-----|--------|--------|------|
| P0 | 1 | 1 | 5.6% |
| P1 | 5 | 5 | 27.8% |
| P2 | 8 | 8 | 44.4% |
| P3 | 4 | 4 | 22.2% |
| **总计** | **18** | **18** | **100%** |

## 其他更改
`#9` `#10`问题已同步更新至`#10` `#11`

---

# Ver.0 — 代码审计问题核实报告

> **核实日期**：2026-07-23\
> **核实人**：MomoNyako\
> **对照文档**：[`审计状态_代码核实报告.md`](审计状态_代码核实报告.md)

## 审计概述

对代码审计报告的42个问题逐项核验，确认 42个

## 问题清单

| 优先级 | 问题 | 文件位置 | 状态 |
| :---: | --- | --- | :---: |
| P0 | **并发修改异常风险** | `AaroncosLefthand0Entity.java:356-365` | 已确认 |
| P0 | **双重检查锁定失效** | `PDClientEvents.java:86` | 已确认 |
| P0 | **交叉淡入淡出状态泄漏** | `CrossfadeManager.java:78-83` | 已确认 |
| P1 | **投射物双倍速度** | `SquealWaveProjectileEntity.java:46-50` | 已确认 |
| P1 | **声音重复播放** | `BoneWingFireBallProjectileEntity.java:78-88` | 已确认 |
| P1 | **NBT 状态未持久化** | `LifeCrystalBlockEntity.java:55-58` | 已确认 |
| P1 | **玩家登出时水晶被销毁** | `LifeCrystalBlockEntity.java:217-237` | 已确认 |
| P1 | **冷却时空音乐名称** | `ModMusicManager.java:211-218` | 已确认 |
| P1 | **音乐突然切断** | `ModMusicManager.java:265-271` | 已确认（已有 TODO） |
| P1 | **假交叉淡入淡出** | `CrossfadeManager.java:101-115` | 已确认（已有 TODO） |
| P1 | **GUI 立即打开** | `ShadowChestBlock.java:143-160` | 已确认 |
| P2 | **目标优先级重复** | `ShadowSquealGhostEntity.java:143,154` | 已确认 |
| P2 | **目标优先级重复** | `ShadowSquealGhost0Entity.java:137,140` | 已确认 |
| P2 | **远程攻击空实现** | `ShadowSquealGhost0Entity.java:322-326` | 已确认 |
| P2 | **NBT 持久化缺失** | `SmallStoneSpiritEntity.java:58,155-162` | 已确认 |
| P2 | **数值无限增长** | `SmallStoneSpiritEntity.java:200` | 已确认 |
| P2 | **吼叫触发逻辑错误** | `TerrorbeakEntity.java:100-133` | 已确认 |
| P2 | **空闲动画未设置** | `DreamCauldronBlockEntity.java:73-75` | 已确认 |
| P2 | **物品生成位置问题** | `MeltdreamChestBlockEntity.java:261-268` | 已确认 |
| P2 | **双重 tick 调度** | `MeltdreamLiquidBlock.java:48-71` | 已确认 |
| P2 | **客户端/服务端 tick 不必要** | `ShadowVortexBlock.java:66-74` | 已确认 |
| P2 | **状态未持久化** | `ShadowVortexBlockEntity.java:57` | 已确认 |
| P2 | **击退覆盖速度** | `EntitySkillManager.java:260-263` | 已确认 |
| P2 | **冷却管理器空值检查缺失** | `CooldownManager.java:74` | 已确认 |
| P2 | **播放状态不精确** | `MusicPlaybackController.java:84-86` | 已确认 |
| P3 | **类型使用不当** | `ShadowGolemEntity.java:65` | 已确认 |
| P3 | **自我效果应用** | `FoxFireEntity.java:164-168` | 已确认 |
| P3 | **魔法数字未文档化** | `ShadowGolemEntity.java:105` | 已确认 |
| P3 | **客户端/服务端状态不一致** | `ShadowNpc0Entity.java:139-148` | 已确认 |
| P3 | **文档与代码不一致** | `MeltdreamChestBlock.java:47-48` | 已确认 |
| P3 | **空 ticker** | 多处 | 已确认 |
| P3 | **冗余空值检查** | `MeltdreamLiquidBlock.java:65` | 已确认 |
| P3 | **粒子淡出缺失** | `SilverParticle.java:56` | 已确认 |
| P3 | **粒子淡出缺失** | `LeavesParticle.java:81-96` | 已确认 |
| P3 | **速度未阻尼** | `FeatherWhiteParticle.java:76` | 已确认 |
| P3 | **粒子无限增长** | `CrackParticle.java:66` | 已确认 |
| P3 | **占位符按钮** | `DreamCauldronScreen.java:123-135` | 已确认 |
| P3 | **常量重复定义** | 多处 | 已确认 |
| P3 | **GL 状态恢复依赖外部** | `PDParticleRenderTypes.java:28-46` | 已确认 |
| P3 | **冗余调用** | `ModMusicManager.java:249` | 已确认 |
| P3 | **服务端空 ticker** | `DreamAccumulatorBlock.java:109-117` | 已确认 |
| P3 | **早期初始化风险** | `MinecraftMixin.java:46` | 已确认 |

## 统计摘要

| P级 | 报告数 | 确认数 | 占比 |
|-----|--------|--------|------|
| P0 | 3 | 3 | 7.1% |
| P1 | 8 | 8 | 19% |
| P2 | 14 | 14 | 33.3% |
| P3 | 17 | 17 | 40.6% |
| **总计** | **42** | **42** | **100%** |

**已知问题**：#9（无淡出停止音乐）和 #10（假交叉淡入淡出）在代码中已有 TODO 标注，为已标记未实现项。