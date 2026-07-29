# 风之旅途 Lake 专项 VERIFY 设计

日期：2026-07-29  
状态：已定稿（方案 A）  
相关：`ground_feature_wind_journey_1`（现 `pasterdream:safe_lake`）、VERIFY-only 挂接

## 0. 修复结论（2026-07-29 后补）

| 项 | 结论 |
|----|------|
| 根因 | `LakeFeature.place` 水面 `getBiome` → BiomeManager 采样越出 WorldGenRegion → FATAL |
| 修复 | `SafeLakeFeature`（注册名 `pasterdream:safe_lake`）：同形貌，省略结冰/`getBiome` |
| configured | `ground_feature_wind_journey_1.json` → `"type": "pasterdream:safe_lake"` |
| 回归 | `PASTERDREAM_VERIFY_SUITES=wind-lake` → **15/0**；0× chunk unavailable |
| 正式挂接 | 仍默认仅 VERIFY modifier 门控；feature 本体已可安全 gen |

## 1. 背景

- 水色湖 configured/placed 资源仍在：
  - `data/pasterdream/worldgen/configured_feature/ground_feature_wind_journey_1.json`
  - `data/pasterdream/worldgen/placed_feature/ground_feature_wind_journey_1.json`
- 正式包默认 **不** 常驻挂接 `_1`（VERIFY-only `wind_lake_verify`）；历史原因是 1.21.1 `LakeFeature` `getBiome` 越界 FATAL（现已用 `safe_lake` 消除）。
- 旧版曾挂接：`libs/FixPasterDream-main/.../ground_feature_wind_journey_1_biome_modifier.json` → biome_0 + `surface_structures`。
- 现 VERIFY 建档默认 **超平坦 + `generateStructures=false`**，不适合作为该崩溃/修复的复现床 → wind-lake 改建档。

## 2. 目标

做一套 **自动化专项 VERIFY**，专门用于测试与修复水色湖：

| 要求 | 约定 |
|------|------|
| 测试世界 | **非超平坦** + **开启建筑生成** |
| 维度 | 真实 `pasterdream:wind_journey_world`（不建假维） |
| 湖挂接 | **仅 VERIFY 临时启用**；正式 `neoforge/biome_modifier` 不重挂 |
| 形态 | 自动化套件（可 CI）；可选 `KEEP_OPEN` 留下世界手修 |
| pass | **不崩服** + **扫描到湖形貌**（水 + `cyan_stone` barrier，≥1 处） |

## 3. 非目标

- 不把 lake 写回正式发布资源挂接（日常进维仍 OPEN_BY_DESIGN）。
- 不替代 `wind-journey` 全流程（祭坛/Boss/出维等）。
- 不新建简化「湖复现迷你维」（避免修分身、漏掉 end_islands/surface 边界条件）。

## 4. 方案总览（A）

1. 新套件 `wind-lake`（不进 `all`）。
2. 该套件门控下改建档：`WorldPresets.NORMAL` + `generateStructures=true`。
3. VERIFY-only 将 `ground_feature_wind_journey_1` 挂到 `wind_journey_biome_0` / `surface_structures`。
4. TP 风维 → 强制 gen chunk → 断言不崩 + 有湖 → 写 `pd_verify_report.json`。

## 5. 建档与套件门控

### 5.1 套件

- 枚举：`PDPortingVerifyTest.Suite.WIND_LAKE`
- 主键 / 别名：`wind-lake`、`wind_lake`、`lake`
- `parseSelectedSuites`：与 `WIND_JOURNEY` 等相同，**从 `all`/`*` 默认集合移除**
- 未知套件警告白名单补上 `wind-lake`

### 5.2 建档（`PDSmokeTest`）

当 `PDPortingVerifyTest.ENABLED && suite 含 WIND_LAKE`：

| 项 | 值 |
|----|-----|
| 世界名 | `test-audit`（开跑前删除重建） |
| 预设 | `WorldPresets.NORMAL`（非 FLAT） |
| `WorldOptions` | `(seed, generateStructures=**true**, bonusChest=false)` |
| 难度 | EASY（沿用 VERIFY） |
| 模式 | CREATIVE |

否则保持现状：FLAT + `generateStructures=false`。

套件探测必须与 `SELECTED_SUITES` **同一解析结果**（直接读 `PDPortingVerifyTest.SELECTED_SUITES` / 公开 `suite` 查询），禁止再解析一遍 env 造成分叉。

日志需明确打印：`creating NORMAL structures-on test world` vs 原有 superflat 文案，便于确认门控生效。

### 5.3 进维

建档完成后由 hook `teleportTo(PDDimensions.WIND_JOURNEY_WORLD_LEVEL_KEY, x, y, z, …)`，建议 Y∈[120,160]，与现 wind-journey 一致。

## 6. VERIFY-only lake 挂接

### 6.1 正式资源边界（硬约束）

- **禁止** 向 `src/main/resources/data/pasterdream/neoforge/biome_modifier/` 添加引用 `_1` 的 JSON。
- configured / placed 保持不动，供挂接引用。

### 6.2 启用方式（实现优先级）

**优先：代码路径（仅 VERIFY）**

- 在服务端、生物群系修饰/世界生成可用的时机，当且仅当 `ENABLED && SELECTED_SUITES 含 WIND_LAKE` 时，把 placed feature `pasterdream:ground_feature_wind_journey_1` 加入 `pasterdream:wind_journey_biome_0` 的 `surface_structures` 步。
- 具体 API 以实现时 NeoForge 1.21.1 可编译路径为准（例如动态 `BiomeModifier`、测试用 datapack 注入、或等价 add_features）。若动态注册不可行，退到备选。

**备选：VERIFY 专用 datapack**

- 仅挂在 run/VERIFY classpath 或运行时复制进 `datapacks/`，内容对齐旧版：

```json
{
  "type": "neoforge:add_features",
  "biomes": "pasterdream:wind_journey_biome_0",
  "features": "pasterdream:ground_feature_wind_journey_1",
  "step": "surface_structures"
}
```

- 不得打进正式 jar 的默认 data。

### 6.3 生效校验

Hook 启动时应能证明「本 run 湖 feature 已进入风维 biome_0 的生成列表」；若无法启用挂接 → **fail**（写明原因），不要静默 skip 成假绿。

## 7. 生成触发与断言

### 7.1 Hook 类

- 建议：`PDWindLakeVerifyHooks`（与 `PDWindJourneyVerifyHooks` 并列）
- 由 `PDPortingVerifyTest` 在 `suite(WIND_LAKE)` 时调度（独立时间线分支，不依赖祭坛等）

### 7.2 流程

1. 校验挂接生效  
2. TP 风维安全点  
3. 强制加载玩家周围 **N×N chunk**（默认 N=5，常量可调）  
4. 等待若干 server tick，使 surface feature 落地  
5. 在水平半径 R（默认与 chunk 覆盖相当）、合理 Y 带扫描：  
   - 存在 `minecraft:water`  
   - 同柱或邻格存在 `pasterdream:cyan_stone`（configured barrier）  
6. 命中 ≥1 个「有效湖斑」→ 断言 pass；0 → fail  

### 7.3 「不崩」判据

- 流程跑完并写出含 pass 的 `pd_verify_report.json`，且 JVM/集成服未因 FATAL 退出。  
- 若 `LakeFeature` 再次越界崩服：无完整报告 / 非零退出 → CI 与本地判失败。  
- 不在此套件内做「expected crash」模式（用户选定：不崩 + 有湖才过）。

### 7.4 报告

- 沿用 `pd_verify_report.json` assertions 数组。  
- 建议 assertion id 前缀：`wind_lake.*`（如 `wind_lake.hook_enabled`、`wind_lake.no_crash_gen`、`wind_lake.feature_shape`）。  
- `KEEP_OPEN=1` 时不自动退出，便于对照 crash-report / 地形手修。

## 8. 运行入口

### 8.1 Shell

```bash
PASTERDREAM_VERIFY=1 \
PASTERDREAM_VERIFY_SUITES=wind-lake \
PASTERDREAM_VERIFY_KEEP_OPEN=0 \
JAVA_HOME=… \
sh gradlew :PasterDream:runClient --offline
```

### 8.2 IDE

- 新增 `.run/PD VERIFY wind-lake.run.xml`  
- 镜像 `PD VERIFY wind-journey.run.xml`，仅 `PASTERDREAM_VERIFY_SUITES=wind-lake`

### 8.3 文档

- `docs/验证复现.md`、`docs/功能状态.md`：登记 wind-lake 专项、建档差异、`safe_lake` 修复与正式默认不挂。

## 9. 主要改动面

| 区域 | 改动 |
|------|------|
| `PDSmokeTest` | WIND_LAKE 门控 NORMAL + structures on |
| `PDPortingVerifyTest` | Suite、all 排除、调度、警告白名单 |
| 新 `PDWindLakeVerifyHooks` | 挂接校验、TP、gen、扫描断言 |
| VERIFY-only 挂接 | 代码或专用 datapack（非正式 biome_modifier） |
| `.run/PD VERIFY wind-lake.run.xml` | IDE 入口 |
| 文档 | 验证复现 / 功能状态 短记 |

**明确不改**：正式 `wind_journey_ground_*.json` 去加入 `_1`。

## 10. 风险与缓解

| 风险 | 缓解 |
|------|------|
| 非 flat 拖慢其它套件 | 严格 `WIND_LAKE` 门控 |
| 挂接泄漏进正式包 | 禁止 main resources biome_modifier；代码/run-only datapack |
| rarity/噪声导致扫不到假 fail | placed rarity=1；扩大 N/R；日志输出采样统计 |
| 生成线程 FATAL 难 try/catch | 以进程存活 + 报告落盘为不崩判据 |
| 与 wind-journey 时间线缠车 | 独立 suite 分支 |

## 11. 验收清单

- [ ] 仅 `SUITES=wind-lake` 时日志显示 NORMAL + structures on  
- [ ] 其它套件仍 FLAT + structures off  
- [ ] 正式 jar/data 无 `_1` biome_modifier  
- [ ] VERIFY run 内湖挂接生效可观测  
- [ ] 崩服 → 失败；不崩且扫到湖 → pass  
- [ ] IDE / shell 均可一键跑  
- [ ] 文档已登记  

## 12. 后续（本设计范围外）

~~修复 `LakeFeature` 越界本身在专项套件变绿之后单开~~ → **已完成**：`SafeLakeFeature` + configured 切换；本设计床已回归 **15/0**。
