# 修复对照表 — 2026-08-03 全库审查

> 对应主文档：[`../2026-08-03-full-repository-review.md`](../2026-08-03-full-repository-review.md)
> 用途：**唯一修复跟踪表**。每完成一项，将「状态」列改为 `已修复`，填写「修复提交/说明」，并在 [`CHANGELOG.md`](CHANGELOG.md) 追加记录。
> 状态图例：未计划 · 已修复 · 已核验（代码确认存在/已消失）

---

## 修复注意事项：

在执行修复前**提前**申领并及时推送更新，避免多人同时修复统一问题

---

## Critical / High（16）

| ID | 问题摘要 | 涉及文件 | 优先级 | 状态                                 |
|---|---|---|---|--------------------------------------|
| C1 | 音乐碟硬 import 客户端 `Screen` → DS 启动风险 | `item/PastedreamMusicDiscItem.java` | 1 | 已核验（懒解析无崩溃，维持现状不修） |
| C2-1 | 盔甲 `inventoryTick` 无护甲槽归属守卫 → 背包持有即触发剥 buff | `item/SculkArmorItem.java` `TitaniumArmorItem.java` `DyedreamArmorItem.java` `CopperArmorItem.java` | 1 | 已修复                               |
| C2-2 | 非满套 else 分支 `removeEffect` 剥外来同名 buff | `item/SculkArmorItem.java:40` `TitaniumArmorItem.java:40` `DyedreamArmorItem.java:40` | 1 | 已修复                               |
| C2-3 | 四件各自每 tick 重复执行全套检查（无单点） | 上述 4 个 ArmorItem | 3 | 修复中MomoNyako负责                  |
| C2-4 | 满套 `addEffect` 无条件重加弱效果覆盖外来更强效果 | `item/SculkArmorItem.java:41` `TitaniumArmorItem.java:41` `DyedreamArmorItem.java:41` | 4 | MomoNyako负责                        |
| C2-5 | Copper 满套剥 `DIG_SLOWDOWN` 连主动饮用药水也剥（设计争议，待产品审核） | `item/CopperArmorItem.java:43` | 4 | 未计划                               |
| C2-6 | 孤儿效果 `DYEDREAM/SCULK_ARMOR_BUFF` + `armorBuffRemove` 同模式 removeEffect | `registry/PDEffects.java:389,579,881` | 6 | 未计划                               |
| B1 | 蓝图翻页包未注册 → 点翻页断线 | `network/PDNetwork.java` | 2 | 未计划                               |
| B2 | 地牢修复后冷却永久卡死 | `block/BrokenShadowDungeonProtalBlock.java` `ShadowDungeonPortalBlock.java` | 2 | 未计划                               |
| B3 | 无名 NPC 发 `WIN_GAME` → 终章字幕+改生存 | `entity/mob/ShadowNpc0Entity.java` | 2 | 未计划                               |
| B4 | 配置重置按钮主菜单 NPE | `client/gui/config/PDConfigScreen.java` | 1 | 未计划                               |
| B5 | 卡勒卡牌 6 双重伤害 25≠20 | `item/CalleCardItem.java` | 4 | 未计划                               |
| H2 | 地牢清场 discard 全部非玩家实体 | `block/BrokenShadowDungeonProtalBlock.java` | 3 | 未计划                               |
| H3 | `ServerScheduler` 纯内存，重启丢世界修改 | `PasterDreamAPI/.../api/util/ServerScheduler.java` | 5 | 未计划                               |
| H4 | `DyedreamBiomeSource` 噪声未绑种子 | `worldgen/chunkgen/DyedreamBiomeSource.java` | 5 | 未计划                               |
| H5 | 竞技场 gamerule 无 try/finally | `worldgen/PDAaroncosArenaWorldgen.java` | 5 | 未计划                               |

---

## Medium（16）

| ID | 问题摘要 | 涉及文件 | 优先级 | 状态 |
|---|---|---|---|---|
| B6 | `processLavaBucket` 覆盖出液槽、桶叠 17 | `block/entity/WorkshopBlastBlockEntity.java` | 4 | 未计划 |
| B7 | 白厄剑雨双扣耐久 | `item/WhiteSwordRainItem.java` `item/AbstractChargeWandItem.java` | 4 | 未计划 |
| B9 | 5 方块客户端直接 `ServerScheduler.schedule` | `ShadowTrap0Block` `ShadowBrazierBlock` `GuardCrystalBlock` `GlassJarBlock` `DesertHeroTombBlock` | 6 | 未计划 |
| B10 | 变体方块共享静态 codec() | `block/GlassJarBlock.java` `block/ShadowDungeonKeyBlock.java` | 6 | 未计划 |
| B11 | `TruestMoltengold` 散射无 owner | `entity/projectile/TruestMoltengoldWandProjectileEntity.java` | 4 | 未计划 |
| B12 | `ServerScheduler` 非线程安全 | `PasterDreamAPI/.../ServerScheduler.java` `worldgen/structure/AaroncosArenaPortalStructure.java` | 5 | 未计划 |
| B13 | 附属 mods.toml 依赖声明错误 | `PasterDreamSanity/MeltDream/Spells` `neoforge.mods.toml` | 7 | 未计划 |
| B14 | Spells 雪花粒子无粒子 JSON/贴图 | `PasterDreamSpells/.../PDSpellsParticles.java` | 6 | 未计划 |
| N1 | Meltdream 工具组等级低于原版（5 vs 铁级2） | `registry/items/PDItemsTools.java`（:216,345,352,359） | 4 | 未计划 |
| N2 | `geo/` 根目录 37 模型未迁移 / 双目录并存 | `assets/pasterdream/geo/` | 6 | 未计划 |
| M1 | AngelBlockItem 创造仍 shrink | `item/AngelBlockItem.java` | 7 | 未计划 |
| M2 | curio/法杖 `hurtAndBreak` 无创造跳过 | 多文件 | 7 | 未计划 |
| M3 | `PortalRestorationHandler` 字符串匹配 + 硬编码 overworld | `PortalRestorationHandler.java` | 7 | 未计划 |
| M4 | `MeltDreamEnergyAPI.consumeEnergy` 无 enabled 守卫 | `PasterDreamAPI/.../MeltDreamEnergyAPI.java` | 7 | 未计划 |
| M5 | fat/thin jar 混装冲突 | 根 `build.gradle` | 7 | 未计划 |
| M6 | Gallery 仍在默认 `all` 套件 | `smoketest/PDPortingVerifyTest.java` | 7 | 未计划 |
| M7 | player-data 菜单 `clickMenuButton` 无 stillValid | `menu/PlayerBookMenu.java` 等 | 7 | 未计划 |
| M8 | Sleep→notes 主路径 VERIFY 时序不可靠 | `PDSleepEvents` `DreamnotesLogic` `main-flow hooks` | 7 | 未计划 |
| H1 | VERIFY 大量 fixture/accept(true) 假绿 | `smoketest/*VerifyHooks.java` | 7 | 未计划 |

---

## Low（19）

| ID | 问题摘要 | 涉及文件 | 状态 |
|---|---|---|---|
| L1 | `UUID.fromString` 未捕获 → 存档崩溃 | `block/entity/MeltdreamChestBlockEntity.java` | 未计划 |
| L2 | `ResourceLocation.parse` 未校验 | `block/entity/ShadowBlastFurnaceBlockEntity.java` | 未计划 |
| L3 | `LifespanTicks` NBT 只读不写 | `entity/projectile/ShadowMagicballEntity.java` | 未计划 |
| L4 | 幽灵刷怪 z+6 缺失 | `block/TwilightLanternBlock.java` | 未计划 |
| L5 | `NatureBelt`/`Garland` 属性逐字节相同 | `item/NatureBeltItem.java` `item/GarlandItem.java` | 未计划 |
| L6 | SquealWave AoE 不排除施法者 | `entity/projectile/SquealWaveWandProjectileEntity.java` | 未计划 |
| L7 | 能量消耗 `>` 严格比较 | `attachment/PDAttachments.java` | 未计划 |
| L8 | `RegionClaimManager` 认领永释放 | `PasterDreamAPI/.../RegionClaimManager.java` | 未计划 |
| L9 | 竞技场 `placed=true, center=null` 卡死 | `worldgen/structure/AaroncosArenaPortalStructure.java` | 未计划 |
| L10 | `SHADOW_HAND_LANTERN` 重复注册 | `client/PDClientItemExtensions.java` | 未计划 |
| L11 | Sanity 无条件 `LOGGER.error` dump | `PasterDreamSanity/.../PasterDreamSanityMod.java` | 未计划 |
| L12 | `StructureSetBuilder` 默认 salt=0 | `PasterDreamAPI/.../StructureSetBuilder.java` | 未计划 |
| L13 | `RuinBuilder` 模板池 `elements:[]` | `PasterDreamAPI/.../RuinBuilder.java` | 未计划 |
| L14 | `DyedreamChunkGenerator` 死代码 | `registry/PDWorldgenRegistries.java` + 维度 JSON | 未计划 |
| L15 | 河流群系未被选中 | `worldgen/chunkgen/DyedreamBiomeSource.java` | 未计划 |
| L16 | `DimensionBuilder.saveDimensionJson` 缺字段 | `PasterDreamAPI/.../DimensionBuilder.java` | 未计划 |
| L17 | 风之旅途美化未实现 | `docs/archive/specs/2026-07-30-...` | 未计划 |
| L18 | 配置未消费项 | `PDCommonConfig` `PDClientConfig` | 未计划 |

---

## 汇总

| 级别 | 计划项 | 已修复 |
|---|---|---|
| Critical/High | 16 | 0 |
| Medium | 19 | 0 |
| Low | 18 | 0 |
| **合计** | **53** | **0** |

> 注：2026-08-04 对原 C2 拆分为 C2-1~C2-6（Critical/High 11→16、合计 48→53），详见临时报告 [`2026-08-04-C2-review.md`](2026-08-04-C2-review.md)。主文档合计 46（Medium 16 含 M1–M8 合并计 1、H1 计 1；本表展开计 19）。计数口径差异因主文档将 M/H 系列合并表述，本表按条目展开。
