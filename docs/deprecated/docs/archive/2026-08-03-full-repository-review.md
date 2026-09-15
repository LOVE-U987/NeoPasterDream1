# 2026-08-03 全库审查 + 新增 Bug 合并报告

> 基准：当前 HEAD `d1fc1a7d` · momonyako 分支
> 核实：已逐项确认以下所有 bug 在当前代码中**仍存在、未修复**
> 说明：本文档为**多份审计来源的合并版**，条目均已标注来源文件（见下表）；早期审计的已修复/误报项不在本文档清单内，仅在「来源说明」中提及排除理由。
> 配套：修复状态跟踪见 [`fix-comparison-table.md`](2026-08-03-full-repository-review/fix-comparison-table.md) · 变更记录见 [`CHANGELOG.md`](2026-08-03-full-repository-review/CHANGELOG.md)

---

## 0. 合并来源说明

| 来源文件 | 合并进本文档的条目 | 未合并内容（排除理由） |
|---|---|---|
| **本次 08-03 全库深度审计** | B1–B14（新发现，本审计独有） | — |
| [`2026-08-02-全库多agent代码审查.md`](2026-08-02-全库多agent代码审查.md) | C1、C2、H1、H2、H3、H4、H5、M1–M8 | 7 条已修复误报（07-31 C1/C2、H1–H4 等，见该文档 §1） |
| [`bugfix_plan.md`](bugfix_plan.md) | **N1**（问题 3 工具挖掘等级） | 问题 1/2/7 已修复；问题 5 已实现；问题 6 用户延后；问题 4 Tab 待验证 |
| [`pinyin-resource-scan.md`](pinyin-resource-scan.md) | **N2**（`geo/` 根目录模型残留） | `textures/entities/` 等已删除部分 |
| [`config-port-report.md`](config-port-report.md) | **Low 追加项**：配置未消费项 | 8 项配置已被后续功能接线（MOD_ACCOUOCEMENT、NO_RETURN_DYEDREAM_CRACK、BAN_TIME_HOURGLASS 等） |
| 07-18 重构审查/计划三份 | 无（全部 P0/P1 已修复） | `structures/`、`entity_types/`、`Class.forName`、`java.util.Random`、巨型类拆分、`check_lang.py` 均已完成 |

> 说明：`bugfix_plan.md` 的「工具等级参考表」本身有误（原版实际 Copper=1、Dyedream=4、Meltdream=5、Moltengold=1、Titanium=4、ShadowErosion=4，与计划表声称的 1/2/1/1/3/3 不符）。合并后**以原版 `getLevel()` 源码为准**，仅 Meltdream 工具组当前等级（铁级 2）确实低于原版（5），其余镐等级当前已正确。

---

## Critical / High（11 项）

| ID | 来源 | 问题 | 位置 | 核实状态                         |
|---|---|---|---|----------------------------------|
| C1 | [08-02 审查] | 音乐碟硬 import 客户端 `Screen` | `item/PastedreamMusicDiscItem.java:3,85` | 仍存在                           |
| C2 | [08-02 审查] | Sculk/Titanium/Dyedream 盔甲 `removeEffect` 剥外来 buff | `SculkArmorItem.java:44`、`TitaniumArmorItem.java:44`、`DyedreamArmorItem.java:48`、`CopperArmorItem.java:43` | 仍存在                           |
| B1 | [08-03 审计] | 蓝图翻页包 `BlueprintPagePayload` 未注册 → 断线 | `BlueprintScreen.java:90` + `PDNetwork.java:63-83` | 仍存在（无注册）                 |
| B2 | [08-03 审计] | 地牢修复后冷却永久卡死 | `BrokenShadowDungeonProtalBlock.java:251` → Portal 无 tick | 仍存在                           |
| B3 | [08-03 审计] | 无名 NPC 发 `WIN_GAME` 包 → 终章字幕+改生存 | `ShadowNpc0Entity.java:433` | 仍存在                           |
| B4 | [08-03 审计] | 配置重置按钮主菜单 NPE | `PDConfigScreen.java:1340-1341` | 仍存在（:1301 有守卫、:1340 无） |
| B5 | [08-03 审计] | 卡勒卡牌 6 双重伤害 25≠20 | `CalleCardItem.java:308,315` | 仍存在                           |
| H2 | [08-02 审查] | 地牢清场 discard 全部非玩家实体 | `BrokenShadowDungeonProtalBlock.java:367-374` | 仍存在                           |
| H3 | [08-02 审查] | `ServerScheduler` 纯内存，重启丢世界修改 | `ServerScheduler.java:32-100` | 仍存在                           |
| H4 | [08-02 审查] | `DyedreamBiomeSource` 噪声未绑种子 | `DyedreamBiomeSource.java:100` | 仍存在                           |
| H5 | [08-02 审查] | 竞技场 gamerule 无 try/finally | `PDAaroncosArenaWorldgen.java:146-152` | 仍存在                           |

---

## Medium（16 项）

| ID | 来源 | 问题 | 位置 | 核实状态                   |
|---|---|---|---|----------------------------|
| B6 | [08-03 审计] | `processLavaBucket` 覆盖出液槽、桶叠 17 个 | `WorkshopBlastBlockEntity.java:183` | 仍存在                     |
| B7 | [08-03 审计] | 白厄剑雨双扣耐久 | `WhiteSwordRainItem.java:78` + `AbstractChargeWandItem.java:135` | 仍存在                     |
| B9 | [08-03 审计] | 5 方块客户端直接 `ServerScheduler.schedule` | `ShadowTrap0Block:149`、`ShadowBrazierBlock:217/222`、`GuardCrystalBlock:150/181`、`GlassJarBlock:202`、`DesertHeroTombBlock` | 仍存在                     |
| B10 | [08-03 审计] | 变体方块共享静态 codec() | `GlassJarBlock.java:63`、`ShadowDungeonKeyBlock.java:38` | 仍存在                     |
| B11 | [08-03 审计] | `TruestMoltengold` 散射无 owner | `TruestMoltengoldWandProjectileEntity.java:71-78` | 仍存在                     |
| B12 | [08-03 审计] | `ServerScheduler` 非线程安全 + 世界生成线程调用 | `AaroncosArenaPortalStructure.java:114` | 仍存在                     |
| B13 | [08-03 审计] | 附属 mods.toml 声明错误依赖 | Sanity/MeltDream/Spells `neoforge.mods.toml` | 仍存在                     |
| B14 | [08-03 审计] | Spells 雪花粒子无粒子 JSON/贴图 | `PDSpellsParticles.java:77-81`（particles 目录 Count=0）| 仍存在                     |
| N1 | [bugfix_plan #3] | **Meltdream 工具组挖掘等级低于原版**：原版 `getLevel()=5`（超下界合金），当前全组 `incorrect_for_iron_tool`（铁级）→ 无法挖掘原本应能挖的高阶矿石 | `PDItemsTools.java:216,345,352,359` | 仍存在                     |
| N2 | [pinyin-scan] | **`geo/` 根目录 37 个独有模型未迁移**：`dream_cauldron`、`meltdream_chest_0/1`、`weapon_workshop`、`wind_knight_spawnblock_0-4`、`angel_wing` 等仍留在 `assets/pasterdream/geo/` 根；`shadow_rune_totem.geo.json` 根目录与 `geo/entity/` 并存（4008B vs 4024B） | `assets/pasterdream/geo/` | 仍存在（潜在模型加载不到） |
| M1–M8 | [08-02 审查] | Angel shrink、curio hurt、PortalRestoration 字符串、energy enabled、fat/thin 混装、gallery in all、菜单 stillValid、Sleep→notes 时序 | — | 仍存在                     |
| H1 | [08-02 审查] | VERIFY 大量 direct fixture + `accept(true)` 假绿 | `smoketest/*` | 仍存在                     |

---

## Low（19 项）

| 来源 | 问题 | 位置 |
|---|---|---|
| [08-03] | `UUID.fromString` 未捕获 → 损坏存档崩溃 | `MeltdreamChestBlockEntity.java:551` |
| [08-03] | `ResourceLocation.parse` 未校验 | `ShadowBlastFurnaceBlockEntity.java:407` |
| [08-03] | `LifespanTicks` NBT 只读不写 | `ShadowMagicballEntity.java:52-62` |
| [08-03] | 幽灵刷怪 z+6 缺失 | `TwilightLanternBlock.java:186-187` |
| [08-03] | `NatureBeltItem`/`GarlandItem` 属性逐字节相同 | `item/NatureBeltItem.java` |
| [08-03] | SquealWave AoE 不排除施法者 + tag 检查对象错 | `SquealWaveWandProjectileEntity.java:99-106` |
| [08-03] | 能量消耗 `>` 严格比较 | `PDAttachments.java:164,175` |
| [08-03] | `RegionClaimManager` 认领永释放 | `PasterDreamAPI/.../RegionClaimManager.java` |
| [08-03] | 竞技场 `placed=true, center=null` 卡死 | `AaroncosArenaPortalStructure.java:93-108` |
| [08-03] | `SHADOW_HAND_LANTERN` 重复注册 | `PDClientItemExtensions.java:106,118` |
| [08-03] | Sanity 无条件 `LOGGER.error` dump | `PasterDreamSanityMod.java:69-75` |
| [08-03] | `StructureSetBuilder` 默认 salt=0 | `PasterDreamAPI/.../StructureSetBuilder.java:78` |
| [08-03] | `RuinBuilder` 模板池 `elements:[]` | `PasterDreamAPI/.../RuinBuilder.java:425-440` |
| [08-02] | `DyedreamChunkGenerator` 是死代码 | `PDWorldgenRegistries.java:51` + 维度 JSON |
| [08-02] | 河流群系未被 biome source 选中 | `DyedreamBiomeSource.computeBiome` |
| [08-03] | `DimensionBuilder.saveDimensionJson` 缺字段 | `PasterDreamAPI/.../DimensionBuilder.java:681-747` |
| [08-02] | 风之旅途美化未实现 | `docs/archive/specs/` |
| [config-port] | 配置未消费项（定义可编辑但无业务消费）：`DYEDREAM_CRACK_GENERATE`、`SLEEP_SAN_RECOVERY_AMOUNT`、`MELTDREAM_CHEST_LEGEND/RARE_MULTIPLIER`、`BAN_FIRE_NECKLACE`、`LOADING_GUI_TIPS` | `PDCommonConfig` / `PDClientConfig` |

---

## 汇总统计

| 级别          | 数量 |
|---------------|---|
| Critical/High | 11（C1、C2、B1–B5、H2–H5）|
| Medium        | 16（B6–B14 ×9、N1–N2 ×2、M1–M8、H1）|
| Low/存疑    | 19 |
| **合计**      | **46** |

---

## 建议修复顺序

1. **C1 / B4** — 专用服务器启动 + 客户端崩溃
2. **B1 / B2 / B3** — 主线玩法阻断（断线/地牢锁死/字幕）
3. **C2 / H2 / H3** — 数据破坏
4. **B5 / B7 / B11 / N1** — 战斗数值
5. **H4 / H5 / B12** — 世界/多人稳定性
6. **N2 / B10 / B14** — 资源加载
7. **M 系列** — 一致性
