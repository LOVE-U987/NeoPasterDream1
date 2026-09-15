# 无效配置项审计报告

> 生成日期：2026-08-12（更新：2026-08-13）
> 扫描方式：全库引用分析（tools/scan_config_usage.py）+ 原版对比核实
> 结论：主模块 4 项移植遗漏已修复（v0.9.7）；附属模块 13 项为「配置预留但功能未实现」，**保留配置、暂不实现**

---

## 一、已修复（移植遗漏，v0.9.7）

| 配置 | 原版实现 | 新模组修复 |
|------|---------|-----------|
| `ban fire necklace`（禁用业火项链） | `Fire0NecklacePr0Procedure`：禁用时提示"此物品已被禁用"，不禁用时脚下点火+燃烧急迫 I | `Fire0NecklaceItem.curioTick` 补充 BAN 检查与点火逻辑 |
| `loading gui tips`（加载界面 tips） | `ClientEvent.drawScreen`：连接/加载/进度界面底部绘制随机 tips | 新增 `PDLoadingTipsClientEvents`（ScreenEvent.Render.Post） |
| `the origin of the world initially generated dyedream crack`（0,0 原点裂隙） | `GenerateWorldPr0Procedure` 主世界分支 | 新增 `PDOriginCrackWorldgen`（LevelEvent.Load + SavedData 防重复） |
| `dyedream origin spawnpoint`（染梦出生点岛屿） | `GenerateWorldPr0Procedure` 染梦分支 | 同上 |

> 工具：`tools/scan_config_usage.py` 可随时重扫全库配置引用。

---

## 二、配置预留但功能未实现（保留，待实现）

以下配置项仅被配置界面 + 接口 getter 引用，**无任何业务消费**，属于「移植规划中预留」。
删除会影响配置文件兼容性与未来开发计划，故保留并记录。

### PDSanityConfig（6 项）

| 配置键 | 默认 | 缺失功能 |
|--------|------|---------|
| `recover interval` | 1200 | San 值自然恢复逻辑未实现（当前仅环境修饰，无时间恢复） |
| `recover amount` | 0.1 | 同上 |
| `nether lowers san` | true | 下界环境降 San 未实现（`PDSanityHelper.applyEnvironmentSan` 仅处理主世界夜晚/灯影/竞技场/染梦/风旅） |
| `end lowers san` | true | 末地环境降 San 未实现 |
| `rain lowers san` | true | 雨天环境降 San 未实现 |
| `thunder lowers san` | true | 雷暴环境降 San 未实现 |

### PDMeltDreamConfig（4 项，已实现 2 项）

| 配置键 | 默认 | 缺失功能 |
|--------|------|---------|
| ~~`recover interval`~~ | 1200 | ✅ 已实现（v0.9.10）：`PDMeltDreamEvents.onPlayerTick` 按间隔自然恢复 |
| ~~`recover amount`~~ | 0.1 | ✅ 同上 |
| ~~`chest generation multiplier`~~ | 1.0 | ✅ 已实现（v0.9.10）：水晶箱开箱奖励 +2 能量 × 倍率（`MeltdreamChestBlock`） |
| `chest hurt multiplier` | 1.0 | 融梦水晶箱被攻击时能量倍率未实现 |
| `chest kill multiplier` | 1.0 | 融梦水晶箱被杀死时能量倍率未实现 |
| `chest max energy` | 1000.0 | 融梦水晶箱能量转化上限未实现 |

### PDSpellsConfig（3 项）

| 配置键 | 默认 | 缺失功能 |
|--------|------|---------|
| `enable spell system` | true | 法术系统总开关无消费（法术系统未接入配置） |
| `spell cost multiplier` | 1.0 | 法术消耗倍率无消费 |
| `spell cooldown multiplier` | 1.0 | 法术冷却倍率无消费 |

---

## 三、实现建议（若后续开发）

1. **San 环境降值**：`PDSanityHelper.applyEnvironmentSan` 增加 `level.dimension()` 判断（NETHER/END）+ 天气判断（isRaining/isThundering），复用现有 `environment -=` 模式。
2. **San 自然恢复**：在 `onPlayerTick` 中按 `recover interval` 周期调用 `SanHelper.addPlayerSanWithCheck(sp, recoverAmount)`。
3. ~~**MeltDream 恢复/倍率**~~：已实现（v0.9.10）——恢复逻辑挂玩家 tick（`PDMeltDreamEvents`），水晶箱开箱倍率消费于 `MeltdreamChestBlock`；剩余 `chest hurt/kill multiplier`、`chest max energy` 待实现。
4. **Spells 倍率**：在法术施法/冷却处乘 `spellCostMultiplier`/`spellCooldownMultiplier`。
