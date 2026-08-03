# 版本号：Ver.0 — 2026-08-03 全库审查合并

> 时间：2026-8-3
> 
> 作者：MomoNyako

> 本文件记录 `2026-08-03-full-repository-review.md` 的合并与后续修复变更。
> 状态图例：🆕 本次合并 · 🔧 已修复 · ✅ 已核验 · 📄 文档更新

---

## 2026-08-03 · 多源合并整理

### 🆕 合并来源

将多份审计/计划文档合并为单一 Bug 清单，条目标注来源：

| 来源文件 | 贡献条目 |
|---|---|
| 08-03 全库深度审计| B1–B14 |
| `2026-08-02-全库多agent代码审查.md` | C1、C2、H1、H2、H3、H4、H5、M1–M8 |
| `bugfix_plan.md` | N1（工具挖掘等级，问题 3） |
| `pinyin-resource-scan.md` | N2（`geo/` 根目录模型残留） |
| `config-port-report.md` | Low 追加：配置未消费项 |

### ✅ 排除（未入清单）

| 来源 | 排除项 | 理由 |
|---|---|---|
| `bugfix_plan.md` | 问题 1/2/7 | 已修复（树叶战利品、树苗 SaplingBlock、漩涡伤害） |
| `bugfix_plan.md` | 问题 5 | 已实现（矿物生成器三矿 JSON 在场） |
| `bugfix_plan.md` | 问题 6 | 用户延后处理 |
| `config-port-report.md` | 8 项"未消费"配置 | 已被后续功能接线（MOD_ACCOUOCEMENT、NO_RETURN_DYEDREAM_CRACK、BAN_TIME_HOURGLASS 等） |
| 07-18 重构三份 | 全部 P0/P1 | `structures/`、`entity_types/`、`Class.forName`、`java.util.Random`、巨型类拆分、`check_lang.py` 均已完成 |
| 7/25 报告 #5 | — | 误报（裸 `Projectile.tick()` 不移动实体） |

### 📄 文档变化

- 主文档新增「§0 合并来源说明」：条目级来源溯源 + 排除清单。
- 主文档「汇总统计」：Medium 14→16（N1、N2）、Low 18→19（配置未消费项）、合计 45→**46**。
- 主文档「建议修复顺序」：N1 并入战斗数值组、N2 并入资源加载组。
- `fix-comparison-table.md`：建立全量修复跟踪表（状态列待更新）。

### 待办（修复后回填本文件）

- [ ] 按 `fix-comparison-table.md` 逐项修复，完成一项在表中置 `🔧 已修复` 并在本文件追加记录。
- [ ] 修复完成后更新主文档各条「核实状态」列。
