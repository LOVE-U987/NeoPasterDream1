# 风之旅途 Lake 专项 VERIFY 设计

日期：2026-07-29  
状态：已定稿（方案 A）· **正式挂接已于 2026-07-30 收口**  
相关：`ground_feature_wind_journey_1`（`pasterdream:safe_lake`）、正式 `wind_journey_ground_surface`

## 0. 修复与挂接结论

| 项 | 结论 |
|----|------|
| 根因 | `LakeFeature.place` 水面 `getBiome` → BiomeManager 采样越出 WorldGenRegion → FATAL |
| 修复 | `SafeLakeFeature`（`pasterdream:safe_lake`）：同形貌，省略结冰/`getBiome` |
| configured | `ground_feature_wind_journey_1.json` → `"type": "pasterdream:safe_lake"` |
| 回归 | `PASTERDREAM_VERIFY_SUITES=wind-lake` → **15/0**；0× chunk unavailable |
| **正式挂接（2026-07-30）** | `neoforge/biome_modifier/wind_journey_ground_surface.json` 常驻含 `_1`（biome_0 · surface_structures，对齐原版） |
| VERIFY modifier | `pasterdream:wind_lake_verify` codec 保留兼容；`modify` **no-op**（防双重注入）；套件门控仅控制建档/hooks |

## 1. 背景（历史）

- 水色湖 configured/placed 一直在：
  - `configured_feature/ground_feature_wind_journey_1.json`
  - `placed_feature/ground_feature_wind_journey_1.json`
- 曾因 1.21.1 `LakeFeature` 越界 FATAL **暂不**正式挂接，改用 VERIFY-only `wind_lake_verify` 门控注入。
- `safe_lake` 消除越界后，正式包与原版一致常驻挂接；wind-lake 套件改为**校验正式路径**，而非临时挂湖。
- 旧版原版：`libs/FixPasterDream-main/.../ground_feature_wind_journey_1_biome_modifier.json` → biome_0 + `surface_structures`。
- 默认 VERIFY 建档仍为 **超平坦 + structures=false**；wind-lake 套件改建档 NORMAL+structures。

## 2. 目标（套件职责）

| 要求 | 约定 |
|------|------|
| 测试世界 | **非超平坦** + **开启建筑生成** |
| 维度 | 真实 `pasterdream:wind_journey_world` |
| 湖挂接 | **正式 jar 常驻**；hooks 断言 biome_0 已含 placed `_1` |
| 形态 | 自动化套件（可 CI）；可选 `KEEP_OPEN` |
| pass | **不崩服** + **扫描到湖形貌**（水 + `cyan_stone`，≥1） |

## 3. 非目标

- 不替代 `wind-journey` 全流程（祭坛/Boss/出维等）。
- 不新建简化「湖复现迷你维」。

## 4. 方案总览

1. 套件 `wind-lake`（不进 `all`）。
2. 门控建档：`WorldPresets.NORMAL` + `generateStructures=true`。
3. 正式 `wind_journey_ground_surface` 已含 `_1`。
4. TP 风维 → force gen → 断言不崩 + 有湖 → `pd_verify_report.json`。

## 5. 建档与套件门控

与初版相同：`Suite.WIND_LAKE` 别名 `wind-lake`/`wind_lake`/`lake`；不在 `all`；`isVerifyLakeEnabled()` 读 `SELECTED_SUITES`。

## 6. 挂接（现行）

- **正式**：`data/pasterdream/neoforge/biome_modifier/wind_journey_ground_surface.json`  
  features 含 `ground_feature_wind_journey_0/1/3/5/6`，step=`surface_structures`，biomes=`wind_journey_biome_0`。
- **已删**：`wind_lake_verify_only.json`（VERIFY-only 注入 JSON）。
- **兼容**：`PDWindLakeBiomeModifier` 注册仍在，`modify` 空实现。

## 7. 验证

```bash
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=wind-lake PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk sh gradlew :PasterDream:runClient --offline
```

历史基线：**15/0**（safe_lake 修复后）。正式挂接收口后应同量级绿（`feature_wired` 来自正式 modifier）。

## 8. 变更记录

| 日期 | 内容 |
|------|------|
| 2026-07-29 | safe_lake + VERIFY-only 挂接 + wind-lake 15/0 |
| 2026-07-30 | 正式 `wind_journey_ground_surface` 常驻 `_1`；VERIFY modifier no-op |
