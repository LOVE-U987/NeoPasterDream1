# TODO · 第三梦境（风之旅途 · 开放项）

> **类别**：活文档 · 待办  
> **日期**：2026-07-29  
> **玩法参考** → [`第三梦境.md`](第三梦境.md)  
> **功能总览** → [`功能状态.md`](功能状态.md)

主路径（进/出维、SanHelper 云雾、worldgen 矿草、祭坛过程、Boss AOE/loot、雷云落雷、风向、破风幕、融梦箱宝藏）与 VERIFY `wind-journey` **21/0** 已收口；水色湖因 1.21.1 `LakeFeature` 生成期越界**暂不挂接**。  
**本文件只跟踪仍可能要改的项。**

---

## 开放

### P0.5 — 须游戏内手测（自动化不能代替）

- [ ] **`lost_windknight_ruins` 自然生成**  
  datapack `structure` + `structure_set`（spacing 42 / sep 25）齐全，**未**进 RuinAPI（预期）。  
  验收：开结构的世界 `/locate structure pasterdream:lost_windknight_ruins` 有结果；模板内含 `wind_knight_spawnblock_0`。  
  VERIFY 超平 `generateStructures=false` 只断言注册，不能代替本项。

- [ ] **祭坛手感**  
  VERIFY 已覆盖 0→4 + 86t 召唤骑士/四雷云 + 台回 0。仍须生存手感：BE/Geo、阶段替换朝向、右键耗材提示。  
  验收：放置 `wind_knight_spawnblock_0` 走完整五阶段无崩、无异常朝向。

- [ ] **迷梦进维落点 Y**  
  主世界 **Y≥306** 直传；风维 `height=256` 可能夹顶。原版同未 clamp。  
  验收：`fondillusion` + Y=308 进维后可站立/不虚空；若炸则 clamp 到云海安全 Y 并回写 [`第三梦境.md`](第三梦境.md)。

### 可选 / 抛光

- [ ] **水色湖再挂接**  
  `ground_feature_wind_journey_1`（`minecraft:lake`）configured/placed **保留**，biome_modifier **已卸**（进维 FATAL：`Requested chunk unavailable during world generation`）。  
  若要水色湖：勿直接挂回 vanilla `LakeFeature`；需自研不越界取 biome 的 feature 或换步/替换实现。

- [ ] **HUD `cloudmist_percent` 多端一致**（低）  
  客户端本地按 Y 重算；服务端仍写 persistentData。非阻断。

---

## 复测（改代码后）

```bash
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=wind-journey PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

别名：`wind` / `third-dream`。**不在** `all`。报告：`PasterDream/run/pd_verify_report.json`。

```mcfunction
/advancement grant @s only pasterdream:achievement_b_0
/advancement grant @s only pasterdream:achievement_hide_16
/effect give @s pasterdream:fondillusion_buff 6000 0 true
/execute in minecraft:overworld run tp @s ~ 308 ~
/execute in pasterdream:wind_journey_world run tp @s 0 160 0
/locate structure pasterdream:lost_windknight_ruins
/give @s pasterdream:windrunner_crystal 1
/give @s pasterdream:wind_iron_ingot 3
/give @s pasterdream:lightning_spell 1
/give @s pasterdream:wind_knight_spawnblock_0 1
```

---

## 改时锚点

| 项 | 路径 |
| :--- | :--- |
| 进维 / 出维 | `registry/PDEffects.java`（`fondillusionTick` / `cloudmistTick`） |
| 环境云雾 + San | `world/PDSanHelper.java` |
| 风向 / 进维文案 | `world/WindJourneyEvents.java` |
| 祭坛 | `block/WindKnightSpawnblockBlock.java` |
| Boss / 雷云 | `entity/mob/WindKnightEntity.java` · `ThundercloudEntity` · `HighvoltageThundercloudEntity` |
| 遗迹 structure_set | `data/.../worldgen/structure{,_set}/lost_windknight_ruins*` |
| 湖（未挂） | `worldgen/configured_feature` · `placed_feature` · ~~biome_modifier/wind_journey_lakes~~ |
| VERIFY | `smoketest/PDWindJourneyVerifyHooks.java` |

---

*P0.5 手测通过后可考虑将部分断言并入 dim/structures；默认 `all` 仍建议不含本专项。*
