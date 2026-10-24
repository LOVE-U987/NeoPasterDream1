# PasterDream Changelog

> 本文件记录 **v0.9.6 发布以来** 的全部变更（`0.9.7-pre`、`0.10.0-pre.1~5`、`0.10.0-rc.1`、`0.10.0`、`0.10.1` 等里程碑统一归入 **v0.10.0**）。
> v0.9.6 及更早的历史见 [docs/Changelog-历史.md](docs/Changelog-历史.md)。

---

## v0.10.0

### 调整：染梦解密仪式保留书桌并修复上半格触发与采集

*   **背景**：玩家反馈花园解密只有在点击 `flower_11` 下半格时才能触发；剪刀/园艺钳破坏 `flower_11`、`flower_12` 上半格无掉落；同时两处解密仪式成功后都会销毁没有合成配方的染梦书桌。经确认，第二项与上半格判定同源，书桌销毁为原版行为，但本项目决定改为保留。
*   **调整**（`PasterDream` `block/DyedreamGardenDecryptFlowerBlock.java`）：`tryDecrypt` 在维度校验前把 `HALF == UPPER` 的点击坐标归一化到下半格，书桌/花阵校验与后续替换均使用归一化坐标；删除 2 tick 回调中的 `destroyBlock(pos.below(3), false)`，仪式后书桌保留。
*   **调整**（`PasterDream` `block/DyedreamFlower16Block.java`）：删除雪傀儡解密成功后销毁书桌的调用，书桌保留。
*   **修复**（`PasterDream` `block/DyedreamDoublePlantBlock.java`）：新增 `playerWillDestroy` 覆写——生存模式下破坏上半格时按下半格状态与玩家主手工具补一次掉落，解决剪刀/园艺钳从上半格采集不掉落的问题；下半格原路径不变，不会双倍掉落，创造模式仍不掉落。该基类修复同时覆盖全部染梦双层植物。
*   **验证**：`PDDyedreamVerifyHooks` 将 `dyedream.garden.success` / `dyedream.snowpuzzle.success` 的书桌断言改为保留，新增 `dyedream.garden.success_from_upper`、`dyedream.flower11.drops`、`dyedream.flower11.no_drop_empty`、`dyedream.flower12.drops`、`dyedream.flower12.no_drop_empty`；原使用空手却期望掉落的过期断言 `dyedream.flower12.drops` 已重写。
*   **说明**：书桌保留与上半格采集均为相对原版的有意差异，详见 [docs/设计/染梦解密仪式调整.md](docs/设计/染梦解密仪式调整.md)。

### 新增：草薙武器行为移植——中毒/击杀成长/天丛云战技

*   **现象**：草薙（grass_sword）攻击无中毒效果、也无法成长为『天丛云』草薙（true_grass_sword）——两把剑此前仅以 ItemAPI 裸剑注册，原模组的武器行为逻辑完全未移植
*   **根因**：原模组成长机制不走武器工作台——`GrassSwordItem#hurtEnemy` 附加中毒（3 秒）并累计击杀数（NBT `killed`），手持时 `killed>=200` 自动消耗并发放『天丛云』草薙（提示+skill0 音效+融梦水晶粒子）；工作台配方中 TRUE_GRASS_SWORD 仅为合成天丛云剑的材料（该配方此前已移植）
*   **新增**（`GrassSwordItem`）：还原命中中毒 3 秒 + 1 tick 后死亡判定计 `killed+1`（`ServerScheduler` 替代原 queueServerWork）+ 手持 `killed>=200` 成长链路（消耗/发放/快捷栏提示/音效/粒子）；剑属性与原 ItemAPI 注册逐项一致（耐久 874/伤害加成 6/攻速 -2.5/附魔 16），并补还原原版修复材料丛林孢子（ItemAPI 注册此前缺失）；成长时经 `applyComponents(getComponentsPatch())` 继承旧剑全部物品组件——附魔/耐久损耗/自定义数据（原模组成长即换新剑丢附魔，属有意体验改进，组件继承需在 shrink 前完成）
*   **新增**（`TrueGrassSwordItem`）：还原被动中毒 5 秒（比草薙更长）+ 战技"极致锋利"——右键写入 `paster_atk`（原版公式 7.5×锋利×0.5+攻击力属性）与 `skill_multiplier`，播放 sword1 音效/强化粒子/挥剑，并给 `pasterdream:skill` 标签物品施加 80×SKILLCD 冷却（沿用 TerraSwordItem 移植口径取 baseValue）；命中时海豚音效+skill1 音效+尘雾/孢子粒子，对首次命中的目标造成 `paster_atk×skill_multiplier` 额外伤害（persistentData 标记，每个目标仅一次）
*   **注册**（`PDItemsTools`）：GRASS_SWORD/TRUE_GRASS_SWORD 从 `ItemAPI.toolItem` 换为自定义类注册（属性逐项对齐，参照 terra_sword 先例）
*   **语言**：tooltip ×9 键 + 成长提示 ×1 键（双语），原硬编码文本全部入语言键
*   **工具**（`tools/verify_tool_stats.py`）：grass_sword/true_grass_sword 加入自定义类跳过名单（同 white_sword 模式，数值已在类定义逐项对齐）
*   **验证**：`gradlew compileJava` BUILD SUCCESSFUL；`verify_tool_stats.py` 42 项 0 差异；行尾 LF 复查通过；实机待验：中毒显示/200 杀成长触发/战技额外伤害与冷却
### 新增：简单类武器行为批量移植（10 件，武器行为补全第一批）

*   **背景**：全量盘点发现 16 件武器仅有 ItemAPI 裸注册（原模组行为逻辑未移植：剑类 9 + 熔金工具系 5 + 战技类 4 + SAN 联动 1）；本批先补简单类 10 件，战技类（truest_moltengold_sword/true_desert_sword/true_tide_sword/dyedream_sword_0）与 shadow_sword 留待后续批次
*   **剑类 ×6**：`MoltengoldSwordItem`（攻击引燃 3 秒）、`TrueMoltengoldSwordItem`（可叠加引燃：已燃目标 +剩余×0.05+2 秒，未燃点燃 4 秒）、`TideSwordItem`（手持水下呼吸，无粒子短刷新）、`DesertSwordItem`（手持缓速-15%+抗性+20% 代价型被动，无粒子短刷新）、`ThermalDaggerItem`（命中 thermal_dagger 音效+气泡粒子×64，音效此前已注册未接入）、`CreativeSwordItem`（命中 1 亿虚空伤害+1 tick 后清实体，攻速为正 6.0 的调试剑）
*   **工具类 ×5**：`MoltengoldPickaxe/Axe/Shovel/HoeItem` 四件套共用 `MoltengoldToolHelper`（右键消耗 4 点饱食度换 20 秒急迫，冷却 10 秒）+ `TrueMoltengoldPickaxeItem`（30 秒急迫、冷却仅 8 秒）
*   **属性对齐**：全部与原 ItemAPI 注册逐项一致；按原版补 fireResistant（熔金全家/温热匕首/创造剑，ItemAPI 注册此前缺失）；修复材料还原（熔金锭/海晶晶粒/钛粒/空），获取等级对齐（剑类 wooden、工具 stone 级）
*   **语言**：tooltip ×18 键（双语），四件套共用 moltengold_tool 键组；原硬编码文本（含温热匕首"§7§o-- 深海迷航"梗文案）全部入语言键
*   **工具**（`tools/verify_tool_stats.py`）：+11 ID 进自定义类跳过名单（累计 14）
*   **验证**：本批文件编译 0 错误（全量编译当前失败于 `DreamBrewingRecipe.java:214`，属并行开发中的他人负责区域，未越界处理）；`verify_tool_stats.py` 42 项 0 差异；`check_lang.py` 双语全过；行尾 LF 全过；实机待验：引燃叠加/被动刷新/饱食急迫/调试剑清实体
### 新增：战技类武器行为批量移植（5 件，武器行为补全第二批 · 收官）

*   **背景**：武器行为补全第二批，5 件战技类全部完成（含最复杂的 SAN 联动 shadow_sword），至此盘点发现的 16 件裸注册武器行为全部补齐
*   **新增**（`TruestMoltengoldSwordItem`）：战技"融骸斩击"——右键开启（200×SKILLCD 冷却），命中时 skill2+龙息爆炸音效、熔岩粒子×64，造成 (2+paster_atk+剩余引燃×0.03)×倍率 的熔岩类型伤害，剩余引燃≥10 tick 追加缓慢 II 后清火复位；被动叠火 +3 秒（比真熔金剑 +2 更高）；按原版补 fireResistant
*   **新增**（`TrueDesertSwordItem`）：战技"绝地反击"——开启时记录已损失生命比例+3 秒吸收（80×SKILLCD 冷却），命中造成 (5+(损失比例×2+1)×paster_atk)×倍率（越残血越痛）；被动与沙漠剑共用（缓速-15%/抗性+20%，抽取 `DesertSwordItem.applyDesertPassive` 包内复用）
*   **新增**（`TrueTideSwordItem`）：战技"涌流剑技"——水中向视线冲刺（×2 初速）+18 tick 抗性 IV（免疫 80%）+挥剑（50×SKILLCD，独立于战技状态，忠实原版结构），命中时目标在水中 (3+1.2×paster_atk)×倍率 否则 paster_atk；被动与潮汐剑共用（水下呼吸，抽取 `TideSwordItem.applyTidePassive`）
*   **新增**（`DyedreamSword0Item`）：战技"凌空斩"——命中额外 (2+paster_atk×1.2)×倍率 伤害+上击飞（0.8），拥有染梦守护效果时冷却 60×SKILLCD（3 秒）否则 100×（5 秒，原版分支）
*   **新增**（`ShadowSwordItem`）：精神值联动——手持且理智系统开启时 SAN_VARIABILITY -3.6、按精神值加成攻速/攻击（0.5/0.75×(1-san/100)，精神越低越强）；挥砍"暗影之才"成就门控（`PDAdvancements.has`），未达成聊天栏提示+虚空伤害至剩 1 血+黑暗 3 秒；理智耗尽挥砍魔法反噬至剩 1 血。**1.21.1 迁移决策**：原版 getAttributeModifiers 覆写在属性组件化后调用语义不可靠，改用瞬时属性修饰符 + tick 维护（与 `PDSanityHelper` 环境修饰符同模式），收剑/关闭系统时移除，行为等价；tooltip 保留原模组 `item.pasterdream.shadow_sword.hovertext.*` 键名
*   **共享**（`WeaponSkillHelper`）：4 件战技的 AtkPr0 等价（paster_atk=攻击力+锋利×0.5 / skill_multiplier=倍率基础值）、BUFF_0+sword1 开启演出、战技标签冷却统一抽取（terra_sword/true_grass_sword 先期移植保持不动）
*   **语言**：tooltip ×37 键（双语，含 shadow_sword 原模组已有的 is_san/describe 键补全 en_us）+ 反噬提示 ×1；硬编码"未选则<暗影> 无法使用此剑"入语言键
*   **工具**（`tools/verify_tool_stats.py`）：+5 ID 进自定义类跳过名单（累计 19，16 件盘点武器全部覆盖）
*   **验证**：`gradlew compileJava` BUILD SUCCESSFUL（一次通过）；`verify_tool_stats.py` 42 项 0 差异；`check_lang.py` 双语全过；行尾 LF 全过；实机待验：各战技伤害/冷却/演出、SAN 联动属性随精神变化、成就门控反噬
### 修复：北海若冲刺无效 + 暗影沉默体系怪物侧缺失（实机反馈两项）

*   **现象 1**：『北海若』引潮（true_tide_sword）右键在水中无冲刺位移（抗性/音效正常）；**现象 2**：白厄剑雨命中的"暗影沉默"对怪物无实际作用
*   **根因 1**（`TrueTideSwordItem`）：服务端对玩家 `setDeltaMovement` 不广播运动包，客户端物理下一 tick 即覆盖速度——原版 MCreator 代码未置 `hurtMarked`，玩家侧冲刺在原版同样无效；修复：置位 `hurtMarked = true` 走 vanilla 击退同款广播路径（属对原版的有意修复，注释说明）
*   **根因 2**（沉默体系）："暗影沉默"是标记型效果——怪物释放特定技能前检查 `hasEffect(SHADOW_SILENCE)` 跳过技能；剑雨侧挂效果代码已移植（亚伦柯斯之触 6%/暗影生物 12%，200t），但**怪物侧检查为 0 处**。且排查发现移植时的共性偏差：4 处怪物的沉默检查被 `hasEffect(MobEffects.CONFUSION)`（恶心）顶替，源码注释仍自称"沉默"
*   **修复**（7 处检查 + 1 处自清，均保留既有 CONFUSION 守卫并列追加真正的沉默检查）：`ShadowHandEntity#serverChargeTick` 突进、`Terrorbeak/WeakenessTerrorbeak/CrazyTerrorbeakEntity#hurt` 咆哮、`AaroncosLefthand0Entity#tickSkillCycle`（冲刺/重击两段）与 `#triggerSwordSkill` 剑雨技、`AaroncosRighthand0Entity#tickSkillCycle`（魔法弹/涡流/调音图腾三段一并拦截，等价原版两独立检查）；另补原版有而移植版缺的 `AaroncosHandEntity#tryBloodLock` 触发时自清沉默（鲜血锁链不可被沉默的反向逻辑）
*   **语义对齐**：被沉默时不写怪物技能标记/计分板（不消耗冷却），沉默到期后技能正常释放（与原版一致）
*   **验证**：`gradlew compileJava` BUILD SUCCESSFUL；`check_lang` 不涉及；行尾 LF 复查通过；实机待验：北海若水中右键位移、白厄剑雨沉默期间上述怪物技能被跳过

### 新增：融梦水晶箱 JEI 战利品展示——每池一页、概率/品质实时解析

*   **新增**（`thirdparty/jei/meltdreamchest/`）：`MeltdreamChestJeiRecipe`（客户端资源包实时解析战利品表 JSON：染梦主表 4 池 + 风旅/灯影主表 + 传说附加 3 池，每池一条，零硬编码物品）+ `MeltdreamChestJeiCategory`（程序化绘制无需 png 资产，物品槽下方标注概率，悬停提示概率/数量范围/品质）；`PDJeiPlugin` 注册分类与配方，catalyst = 融梦水晶箱
*   **概率口径**：条目权重 / 池内全部条目权重之和——空占位 `tabitem_1`（如装备池 w150）计入分母但不展示，还原"该池大概率不出实物"的真实概率（若剔除会虚高约 170 倍）
*   **品质**：抓取 lang 现成 `tooltip.pasterdream.<id>.quality` 键（宝藏 11 件中 7 件有定义，缺失的物品不显示品质行，优雅降级）
*   **语言**：分类名 + 8 个池名 + 概率/数量提示行（双语 ×11 键）
*   **UI 瘦身**（实机截图反馈）：概率从槽位下方整行文字（槽间距 18px 装不下 30px 文字导致"9.1%9.1%…"重叠成乱码）改为**槽内右上角 0.6 倍小字整数百分比角标**（精确到 1 位小数的概率保留在悬停提示）；悬停提示**移除重复品质行**（物品本体 tooltip 已含，缺失的物品不再代劳）；**移除池标题的条目数后缀**（JEI 原生计数角标已有）；画布高 84→72
*   **兼容**：战利品表读取经三轮实机排查后定稿为**双数据源**——第一优先逻辑服务端资源管理器（支持存档数据包覆盖），失败（`out` 为空）时回退 `Class.getResource` classpath 直读（`/data/pasterdream/…`，dev classpath 与 prod 模组 jar 通用，绕开资源管理器 pack 机制）；每步 INFO/WARN 日志自证；主菜单 / 联机客户端返回空列表；前两版（客户端 rm 直读、`AddReloadListenerEvent` 缓存）均因资源管理器上下文问题被实机日志证伪回退，详见知识库 2026-10-18 笔记
*   **验证**：`gradlew :PasterDream:compileJava` BUILD SUCCESSFUL；`check_lang.py` 双语对齐通过；行尾 LF 复查通过；实机待验：JEI 中点融梦水晶箱图标查看各池掉落、概率与品质显示
### 修复：染梦维度不生成岩浆（还原原模组岩浆生成链路）

*   **根因**：原模组的岩浆由四层机制叠加产生，本项目四层全被切断——① 群系 `features[1]`（LAKES 步）为空且 `minecraft:lake_lava_underground` 未迁移为 biome modifier（`6fda3db8` 迁移遗漏）；② `noise_settings/dyedream_world.json` 的 `noise_router.lava` 被改为常量 `0.0`；③ `NoiseBasedChunkGeneratorMixin` 拦截 `createFluidPicker`，抹除 Y-64 ~ Y-55 底部岩浆层；④ 三个 `configured_carver/dyedream_*.json` 的 `lava_level` 被改为 `{"absolute": -2032}`。后三项来自同一提交 `6136a762`（意图「地下岩浆改为地下河」），本次经确认推翻。
*   **新增**（`PasterDream` `data/pasterdream/neoforge/biome_modifier/dyedream_lava_lake.json`）：`neoforge:add_features` 挂 `minecraft:lake_lava_underground` 到 `#pasterdream:is_dyedream`，`step: "lakes"`（该步在此前 41 个既有 biome modifier 中从未使用，本文件为首例，现目录共 42 个）。
*   **恢复**（`data/pasterdream/worldgen/noise_settings/dyedream_world.json`）：`noise_router.lava` 由 `0.0` 改回 `minecraft:aquifer_lava` 噪声（对齐原模组 `dimension/dyedream_world.json:160-165`），洞穴深处含水层岩浆池恢复生成。
*   **恢复**（`data/pasterdream/worldgen/configured_carver/dyedream_cave.json` / `dyedream_cave_extra_underground.json` / `dyedream_canyon.json`）：`lava_level` 由 `{"absolute": -2032}` 改回 `{"above_bottom": 8}`（原版口径，Y = -56），洞穴雕刻底部岩浆恢复。保留本项目自定义 carver 及其 probability / y / yScale 调参，不回退原版 `minecraft:cave` 系列。
*   **移除**（`PasterDream` `mixin/NoiseBasedChunkGeneratorMixin.java` + `src/main/resources/pasterdream.mixins.json`）：删除该 Mixin 及其注册条目，原版 `createFluidPicker` 的 `y < min(-54, seaLevel) → LAVA` 恢复生效（`StructureTemplateAccessor` 保留）。Mixin 仅在 `defaultBlock == minecraft:calcite` 时命中，项目中仅染梦维度受影响。
*   **连带（已确认接受）**：染梦与冷域共用 `pasterdream:dyedream_cave` 系列 carver，`cold_domain_biome` / `cold_domain_tundra` 亦会生成 Y-56 以下洞穴岩浆。冷域 `noise_settings` 的 `lava: 0` 不改（本项目新维度，原模组无对照）；风旅 `noise_settings` / `dimension` 内联的 `lava: 0` 亦不改（原模组本就为 0）。
*   **新增**（`tools/verify_dyedream_lava.py`）：四断点静态校验脚本（lakes biome modifier / aquifer_lava 噪声 / carver above_bottom / Mixin 已移除）。
*   **验证**：`python tools/verify_dyedream_lava.py` 4/4 通过；`.\gradlew compileJava` BUILD SUCCESSFUL；`.\gradlew runData` 退出码 0（`written: 0`，未覆盖手写 JSON）；启动日志 `Preparing pasterdream.mixins.json (6)` 确认 Mixin 移除生效。
*   **同步**：`docs/开发指南/问题排查.md` 新增「世界生成」章节（四层岩浆排查路径）；`docs/架构/主模组架构详解.md` 两处 mixin 类数登记 7 → 6（名单移除 NoiseBasedChunkGenerator）。

### 新增：丛林孢子植株生长机制（类蘑菇蔓延 + 骨粉催生）

*   **背景**：`pasterdream:jungle_spore_plant` 此前仅注册为普通 `FlowerBlock`，无任何生长机制；原模组 `JungleSporePlantBlock` 同样未实现 tick。本项为相对原模组的**有意改进**（非还原项）。
*   **新增**（`PasterDream` `block/JungleSporePlantBlock.java`，新）：继承 `FlowerBlock` 并实现 `BonemealableBlock`，保留原中毒效果语义。
*   **生长**：`randomTick` 以 1/25 概率复刻原版 `MushroomBlock.randomTick` 扩散算法——扫描 `origin ± (4,1,4)` 同类上限 5、目标点 `±1`（y 为 -1~+1）随机尝试 4 次、放置 flag 2；只复制自身。
*   **忽略亮度**：`canSurvive` 沿 `FlowerBlock → BushBlock`（泥土类 / 耕地 / `canSustainPlant`）判定，不含原版蘑菇 `getRawBrightness < 13` 门槛，故明亮环境下同样蔓延；存活土壤维持原方块现行为。
*   **骨粉**：`performBonemeal` 立即催生一次同样的蔓延（受同一密度上限约束），不做巨型化。
*   **注册**（`PasterDream` `registry/blocks/PDBlocksMisc.java:154`）：`JUNGLE_SPORE_PLANT` 由 `new FlowerBlock(...)` 改为 `new JungleSporePlantBlock(...)`，属性链新增 `randomTicks()`；`PDBlocks` re-export 与 BlockItem 注册无需改动。
*   **验证**：`.\gradlew :PasterDream:compileJava` 与 `.\gradlew compileJava` 均 BUILD SUCCESSFUL；无数据文件改动。

### 调整：丛林孢子植株掉落受时运影响（每级独立 15% 额外）

*   **背景**：原模组战利品表对本方块无时运加成；本项为**有意改进**（非还原项）。
*   **公式**：普通掉落 `jungle_spore` 追加 `minecraft:apply_bonus` + `minecraft:binomial_with_bonus_count`（`extra=0`, `probability=0.15`）——每级时运独立一次 15% 概率 +1：Fortune I/II/III 最多 +1/+2/+3，期望 1.15/1.30/1.45。
*   **改动**（`PasterDream` `data/pasterdream/loot_table/blocks/jungle_spore_plant.json`）：仅池 1（非精准采集）的 `functions` 在 `set_count` 之后追加 `apply_bonus`；精准采集池（掉本体）不变。
*   **验证**：JSON 解析通过；`python tools/verify_resource_closure.py` 全库 JSON 可解析/无 BOM（仅报告 `libs/` 原模组 3 个 shadowshelf 历史缺失，与本改动无关）。
*   **同步**：设计文档 `docs/设计/丛林孢子掉落时运.md`。

### 修复：染梦/风旅维度 BGM 与群系错位、蘑菇平原实际不生成

*   **根因 1（映射语义错位）**：自定义维度内 BGM 的唯一权威来源是 `client/audio/ModMusicManager#initializeDefaultBiomeMusic()`（`mixin/MinecraftMixin` 在自定义维度内屏蔽原版群系音乐），但该表沿用旧群系 ID 重命名前的对应关系，曲名与群系语义错位——`梦幻三角洲`（`dream_delta`）挂在染梦冰雪冻原、`梦幻雪林`（`dream_taiga`）挂在染梦冰冻海洋。
*   **根因 2（挂在不存在的群系上）**：风之旅途维度实际群系为 `wind_journey_islands`/`wind_journey_desert`（`dimension/wind_journey_world.json`），音乐表与 `smoketest/PDWindLakeVerifyHooks` 仍引用已不生成的 `wind_journey_biome_0/1` → 该维度被 mixin 静音后无人接管，**完全无 BGM**；`wind_journey_departure`/`wind_journey_midsummer` 两首从未被播放。
*   **根因 3（群系实际不生成）**：`worldgen/chunkgen/DyedreamBiomeSource` 的 `MUSHROOM_PLAINS_THRESHOLD = 0.86`。用仓库内第三方 `FastNoise` 实测 1677 万采样点，覆盖率仅 **0.0049%**（约每 2 万个区块 1 个），等同不生成，连带 `snowfall_dream_music` 无人能听到。
*   **修复**（`client/audio/ModMusicManager`）：染梦 9 群系按曲名语义重排——冰雪冻原→`梦幻雪林`、河流→`梦幻三角洲`、冰冻海洋/深海/海岸→`甜蜜的梦`、平原→`染梦世界`、森林/密林→`梦幻荒原`+`Daisy`、蘑菇平原→`落雪之梦`；风之旅途改挂 `wind_journey_islands`/`wind_journey_desert`。
*   **修复**（`worldgen/chunkgen/DyedreamBiomeSource`）：蘑菇平原噪声阈值 `0.86 → 0.75`，实测覆盖率 **0.506%**（斑块约 32 格见方），稀有但可稳定找到；阈值-覆盖率实测数据写入常量注释。
*   **修复**（`smoketest/PDWindLakeVerifyHooks`）：`wind_journey_biome_0 → wind_journey_islands`（常量、断言名、提示文案同步），该 VERIFY 套件此前因群系不存在恒失败。
*   **同步**（`data/pasterdream/worldgen/biome/`，6 个染梦群系 JSON）：`music.sound` 字段与新映射对齐（该字段在自定义维度内仅作 mixin 失效时的兜底，不代表实际播放结果），并统一行尾为 LF。
*   **验证**：`gradlew compileJava` BUILD SUCCESSFUL；蘑菇平原覆盖率由独立 FastNoise 采样程序实测（0.70→1.387%、0.75→0.506%、0.80→0.115%、0.86→0.0049%）。

### 新增：pasterdream-api-guide 技能包（API 总览 + 缺失域教程，签名经源码核签）

*   **新增**（`docs/deprecated/skills/pasterdream-api-guide/SKILL.md`，新，673 行）：API 使用总览教程——三件套模式（Facade+Builder+Result）、`registerAll` 生命周期（前置 mod `PasterDreamAPIMod` 独占挂接 15 个 DeferredRegister + 2 处特殊注册，下游禁止重复调用，否则 `Cannot register DeferredRegister to more than one event bus`）、共享门面命名空间语义（内容落 `pasterdream:`，FluidType 例外 `pasterdreamapi`）、成熟度速查；11 个无独立技能包域的核签快速上手（方块/物品/维度/流体/菜单/饰品/理智/融梦能量/法术/BGM/附属配置）+ Doll API（Java 侧）全貌（含 `.desc` 语言键、loot_table 手写、`bb_main` 几何约定、legacy 路径硬编码等坑位）
*   **核签方法**：三路只读 Agent 逐项比对源码——入口精确签名、Builder 方法与默认值、Result 组件、真实调用示例（文件:行）
*   **结论**（`docs/开发指南/注册指南.md`）：其方块/物品/维度/遗迹/菜单段示例签名**大面积失效**（`createBlock`/`createItem` 系列、`registerBlockItem`、`registerToTab`、`TerrainNegotiation` 等均不存在；`registerAll` 归属已变更）——新技能包内已标注勿参考；同内容副本 `docs/架构/注册流程.md` 一并待处置
*   **核签结论**（9 个既有技能包，`docs/deprecated/skills/`）：均判「部分过时」，签名层总体健康；横切问题——7 个技能仍教开发者在主模构造器调 `registerAll`/`XXAPI.REGISTRY.register`（当前机制下照做即 double-register 崩溃）；点状失效——`neoforge-block-drops` 的 `DeferredBlock.toItem()` 代码不可编译、`pasterdream-mod-dev` 的 `GeckoLibAnimalEntity` 不存在/GeckoLib 4.7.3（应 4.8.4）/变体集与批量 Builder 示例缺参、`pasterdream-vfx-api` 的 `ScreenEffectAPI.registerType` 实为两参、`world-decoration-api` 参数一览漏 `claimCheck` 等 4 个新方法、`SelfDropBlock` 掉落策略语义已改为「无实际战利品表才回退自掉落」（影响 mod-dev/block-drops 两处口径）。修正清单已出，待确认后落地
*   **删除**（旧档清理）：误导性旧教程 `docs/deprecated/docs/tutorials/kubejs-doll-tutorial.md`（内容已被 `docs/教程/KubeJS玩偶.md` 取代）；`docs/deprecated/docs/tutorials/doll-api-tutorial.md` 同判部分过时（战利品表/.desc/namespace/legacy 五坑），处置待定

### 修复：融梦水晶箱只能开出万象神戒与啵啵鸡的华丽飞羽（战利品以 diff/momonyako/main 为准）

*   **根因**：移植时弃用原版战利品表 `loots_meltdream_chest_0/_1`，改为「3 品质手写配置池」（`config/MeltdreamChestLootConfig.java`）。原版该表含独立饰品池 11 件（`embryo_ring`/`embryo_necklace`/`health_0_necklace`/`rabbit_0_necklace`/`fire_0_necklace`/`red_dew_0_ring`/`red_dew_1_ring`/`embryo_belt`/`traveler_belt`/`garland`/`nature_belt`），移植默认池仅剩 `allkinds_ring`（万象神戒）与 `boboji_plume`（啵啵鸡的华丽飞羽）两件饰品，其余全部缺失。
*   **修复**（`PasterDream` `data/pasterdream/loot_table/chests/`，新）：默认掉落改回原版维度战利品表 `loots_meltdream_chest_0.json` / `_1.json`（4 池：材料/宝石/饰品/装备）；`MeltdreamChestBlock.populateLoot` 按维度取表：染梦 → `_0`，风旅/灯影 → `_1`，其它维度或表缺失/无效（`LootTable.EMPTY`）回退隐藏默认池。
*   **保留附加**：默认路径在战利品表基础上叠加移植版附加——稀有档 slot 0 唱片 + 50% 玩偶；传说档玩偶（基础件数 ≥2 时替换一槽，否则放空槽）+ slot 8 融梦水晶碎片；稀有/传说档约 10% 纪念品。
*   **配置调整**（`PasterDream` `config/PDCommonConfig.java`）：三个物品池默认值改为空列表（内置池转为隐藏 `DEFAULT_*` 常量，不出现在 toml/GUI）；自定义开关保持，开启后玩家池逐条容错，留空/全无效回退隐藏默认池。新增 `config/MeltdreamChestLootConfigMigration.java` 一次性迁移：仅当配置值仍等于旧默认时清空，玩家自定义保留。
*   **恢复**（`PasterDream` `client/gui/config/`）：恢复 `ConfigCategory.MELTDREAM_CHEST` 枚举与 `PDConfigScreen` 的 4 个配置 UI 条目（1 开关 + 3 物品池），与配置项保持一致。
*   **移除**：main 侧新增的 `chests/meltdream_common|rare|legendary.json` 品质表随整合移除。
*   **有意差异**：不再复刻原版「品质决定部分维度取表分支」，统一按维度取表；品质仅决定动画/音效与附加内容；非梦境维度走隐藏默认池。
*   **同步**：`lang/zh_cn.json`、`lang/en_us.json` 四个 tooltip 文案；`docs/设计/融梦水晶箱战利品.md` 记录三层解析与槽位算法。

### 重构：融梦水晶箱战利品物品集合数据化解耦

*   **背景**：上一轮修复后，水晶碎片/纪念品/唱片/玩偶/3 兜底池仍硬编码在 `MeltdreamChestBlock` 与 `MeltdreamChestLootConfig`，无法通过数据包/API 扩展。
*   **水晶/纪念品**（`PasterDream` 资源，新）：新增附加战利品表 `loots_meltdream_chest_bonus_{common,rare,legendary}.json`；水晶必出（传说）、纪念品约 10%（沿用现值）。空条目沿用仓库惯例 `pasterdream:tabitem_1` + `set_count 0`。
*   **唱片**（`PasterDream` `registry/PDItemTags.java` + 资源，新）：新增物品标签 `pasterdream:music_discs`（13 张），`rollDisc` 按标签取样并保留"优先未拥有"；不可复用 `minecraft:music_discs`（含全部原版唱片）。
*   **玩偶**（`PasterDream` `api/doll/DollAPI.java`）：新增 `registerLootItem`/`getLootItems` 轻量战利品登记，`DollBuilder` 自动登记；新增 `registry/PDDollLootRegistrations.java` 登记旧 5 玩偶，动态玩偶自动入池；删除 `getAllDolls` 硬编码。
*   **兜底池**（`PasterDream` 资源，新）：新增 `loots_meltdream_chest_fallback_{common,rare,legendary}.json`（rolls 8/7/8），删除 `MeltdreamChestLootConfig.DEFAULT_*`/`getFallbackLoot`/`resolvePool`；保留 `LEGACY_V1_*` 供迁移。
*   **统一附加**（`PasterDream` `block/MeltdreamChestBlock.java`）：`fillBaseFromPool`/`fillFromLootTable` + 单一 `applyQualityExtras`（唱片/玩偶/附加表/水晶归位），三路行为对齐；空池/空标签/缺失表均容忍并告警。
*   **实体判定**（`PasterDream` `block/entity/MeltdreamChestBlockEntity.java`）：slot 8 水晶判定改用标签 `pasterdream:meltdream_chest_crystal`。
*   **有意差异**：配置池路径统一附加后也获得约 10% 纪念品；稀有档玩偶由"随机覆盖可能吞件"改为"追加不吞件"；兜底由硬编码池改为数据表（行为保留、机制变更）。
*   **同步**：`docs/设计/融梦水晶箱战利品.md` 更新为数据驱动口径。
*   **验证**：`:PasterDream:compileJava` BUILD SUCCESSFUL；新增战利品表/标签 JSON 解析合法。

### 新增：风泊悬挂藤 windmoor_hanging_vine

*   **新增**（`PasterDream` `block/WindmoorHangingVineBlock.java`，新，232 行）：悬挂植被，`canSurvive` 要求上方为 `LOGS` / `PLANKS` / `LEAVES` / `STONE_ORE_REPLACEABLES` / `DEEPSLATE_ORE_REPLACEABLES`，否则 `neighborChanged` 中移除自身
*   **状态**：`AGE`(0~2) 与 `SUPPRESSED`(防链式触发)；形状 `box(2, 0, 2, 14, 10, 14)`，无碰撞，`getVisualShape` 返回 `Shapes.empty()`
*   **生长**：随机刻 5% 概率 `tryGrowDown()` 在下方空气格放置 `PDBlocks.FIG_VINE` 并推进 `AGE`；`countVineLength()` 限制单链最长 12 格（扫描上限 64）
*   **骨粉**：`performBonemeal` 催长一格后按 60% 概率再长一格
*   **新增**（`PasterDream` 资源，新）：`blockstates/windmoor_hanging_vine.json`、`models/block/windmoor_hanging_vine.json`（cross）、`models/item/windmoor_hanging_vine.json`、`loot_table/blocks/windmoor_hanging_vine.json`
*   **注册**（`registry/blocks/PDBlocksWindJourney.java:142~151`）：`randomTicks()` + `noCollission()` + `noOcclusion()`；同步 `PDBlocks` / `PDItems` / `PDItemsBlocks` / `PDCreativeTabsWind` 门面与创造模式物品栏

### 重构：风泊树叶三合一（windmoor_leaves_0/1/2 → windmoor_leaves）

*   **重构**（`registry/blocks/PDBlocksWindJourney.java:133~140`）：删除 `WINDMOOR_LEAVES_0` / `_1` / `_2` 三个注册项，合并为单个 `windmoor_leaves`
*   **新增**（`assets/pasterdream/blockstates/windmoor_leaves.json`，新）：`variants` 加权数组（各 `weight: 1`）随机选用 `block/windmoor_leaves_0` 与 `block/windmoor_leaves_1` 模型实现纹理随机化——故 `models/block/` 下同名模型与 `blockstates/windmoor_leaves_0/1.json` 得以保留
*   **新增**（`models/item/windmoor_leaves.json`、`loot_table/blocks/windmoor_leaves.json`，新）
*   **删除**：`blockstates/windmoor_leaves_2.json`、`models/block/windmoor_leaves_2.json`、`models/item/windmoor_leaves_2.json`、`loot_table/blocks/windmoor_leaves_0/1/2.json`
*   **Tag 合并**：`data/minecraft/tags/block/leaves.json`、`data/minecraft/tags/item/leaves.json`、`data/c/tags/block/leaves.json`、`data/c/tags/item/leaves.json` 中的三个条目合并为 `pasterdream:windmoor_leaves`
*   **⚠️ 行为差异**：旧 `windmoor_leaves_2` 为 `noCollission()` 可穿行方块，合并后所有风泊树叶统一使用 `windmoorLeavesProps()`，**可穿行变体消失**（需确认是否符合预期）
*   **破坏性变更**：`windmoor_leaves_0/1/2` 的方块 ID 与 `BlockItem` 均不再存在，旧存档中的这些方块将丢失映射

### 优化：染梦环境粒子前移生成与降速

*   **修改**（`PasterDream` `client/DyedreamEnvironmentRenderer.java`）：新增常量 `FORWARD_OFFSET`(6.0)、`SPAWN_RADIUS_MIN`(2.0)、`SPAWN_RADIUS_MAX`(12.0)，新增 `getForwardSpawnCenter(Minecraft)` 按玩家偏航角/俯仰角计算前移生成中心（水平前移 6 格，俯仰按 0.3 系数轻微补偿）
*   **修改**：梦幻孢子 / 蘑菇孢子 / 水晶雪花 / 星尘四类粒子由「以玩家为圆心、4~16 格」改为「以前移中心为圆心、水平 2~12 格」，避免转动视角时粒子出现在视野反方向
*   **修改**：四类粒子的水平与垂直速度分量全部减半（如梦幻孢子 X 由 ±0.004 → ±0.002，Y 由 -0.003~-0.011 → -0.002~-0.006），延长粒子在视野中的停留时长

### 新增：风之旅维度噪声设置

*   **新增**（`PasterDream` `data/pasterdream/worldgen/noise_settings/wind_journey_world.json`，新，280 行）：`default_block` = `pasterdream:thick_cloud`，`noise.min_y` = 0、`noise.height` = 128、`island_noise_override` = true；`surface_rule` 为 sequence + 生物群系条件，`wind_journey_islands` 顶部铺 `cyan_moss_stone` 或水
*   **更新**（二进制 NBT，5 个）：`lost_windknight_ruins.nbt`、`wind_island_0.nbt`、`wind_pond_0.nbt`、`windmill_lodge.nbt`、`windmoor_tree_0.nbt`（二进制差异无法静态评审，须游戏内实测）

### 调整：标签与本地化

*   **修改**（`data/pasterdream/tags/block/swayable_plants.json`）：新增 `fig_vine` 与 `windmoor_hanging_vine`（随风摆动）
*   **修改**（`data/minecraft/tags/block/small_flowers.json`）：移除 4 个 `pinkagaric_0~3`，仅保留 `goldenrod`
*   **修改**（`lang/zh_cn.json`、`lang/en_us.json`）：新增 13 个生物群系翻译键（`biome.pasterdream.*`：染梦系 9 个 + 风旅系 4 个），并新增 `block.pasterdream.windmoor_leaves` 与 `block.pasterdream.windmoor_hanging_vine`
*   **修改**（`pd_porting_manifest.json`）：两处 `windmoor_leaves_2` 替换为 `windmoor_hanging_vine`

### 修复：树叶合并后的死引用、孤立资源与死状态清理

*   **修复**（`PasterDream` `pd_porting_manifest.json`）：清单中残留的 `windmoor_leaves_0` / `windmoor_leaves_1` 条目合并为 `windmoor_leaves`（两处），并使 `windmoor_hanging_vine` / `windmoor_leaves` 回归字母序
*   **修复**（`lang/zh_cn.json`、`lang/en_us.json`）：删除指向已移除方块的 `block.pasterdream.windmoor_leaves_0` / `windmoor_leaves_1` 死键
*   **删除**（孤立资源，4 个）：`blockstates/windmoor_leaves_0.json`、`blockstates/windmoor_leaves_1.json`、`models/item/windmoor_leaves_0.json`、`models/item/windmoor_leaves_1.json`——对应方块/物品已不存在；`models/block/windmoor_leaves_0|1.json` 仍被加权 blockstate 引用，保留
*   **修复**（`PasterDream` `block/WindmoorHangingVineBlock.java`）：删除从未被写入的 `SUPPRESSED` 状态属性（含字段、默认状态、`createBlockStateDefinition`、`randomTick` 判断与 `BooleanProperty` 导入）——该属性只被读取、不被写入，恒为 `false`，属死状态，删除后同时消除了每个方块状态多出的无效变体
*   **修复**（`assets/pasterdream/blockstates/windmoor_hanging_vine.json`）：变体键由 `""` 改为按 `age=0` / `age=1` / `age=2` 显式枚举——方块具备 `AGE` 属性时必须枚举，写法与同目录 `windmoor_log.json` 一致
*   **确认（无代码变更）**：原模组 `windmoor_leaves_2` 为 `noCollission()` 且 `getVisualShape` 为空的隐形可穿行方块，合并后该可穿行行为不再存在。经检索，仓库内 5 个 `.nbt` 结构（含 HEAD 版本，`git grep -a` 二进制无命中）与全部数据/世界生成文件均未引用 `windmoor_leaves_0/1/2`，该变体在现有数据中零使用，故按「有碰撞」合并保留

### 遗留项（未处理）

*   `PasterDream/tag_audit.json` 为一次性审计快照，仍列有 `windmoor_leaves_0/1/2` 的 tag 与 loot 条目，需重新生成
*   战利品上下文使用 `LootContextParamSets.CHEST`（不含 `BlockPos` 与玩家信息），后续若需按位置或玩家做条件判断需扩展参数集

### 验证

*   `.\gradlew :PasterDream:compileJava` BUILD SUCCESSFUL（`PasterDream` / `PasterDreamAPI` 任务实际执行，无错误；仅有既存的「使用了已过时 API」编译注记）
*   `python tools/verify_resource_closure.py` PASS：JSON/模型/纹理/粒子/音效/注册资源/loot 闭包全部完整（3851 JSON、238 方块、669 物品）
*   战利品表逐条核对：`loots_meltdream_chest_0` 含 11 件饰品 ID，`loots_meltdream_chest_1` 含 `cyan_moss_stone`，条目均具备 `item.pasterdream.*` 或 `block.pasterdream.*` 语言键
*   待游戏内实测：融梦箱按维度掉落分布、悬挂藤生长与骨粉催长、风之旅维度地表、粒子观感、5 个 NBT 结构变化

### 修复：丛林孢子植株未入创造栏并恢复丛林群系生成

*   **根因**：`pasterdream:jungle_spore_plant` 注册后未加入创造模式物品栏，且缺少 `neoforge:add_features` biome modifier，导致原版丛林（`jungle`/`bamboo_jungle`/`sparse_jungle`）中不生成。
*   **改动**（`PasterDream` `data/pasterdream/neoforge/biome_modifier/jungle_spore_plant.json`，新）：`neoforge:add_features` 将 `pasterdream:jungle_spore_plant` 注入三种原版丛林群系的 `vegetal_decoration`。
*   **改动**（`PasterDream` `registry/creativetabs/PDCreativeTabsDyedream.java`）：补入 `JUNGLE_SPORE_PLANT`、`FOURLEAF_CLOVER`、`FIG_VINE`、`CROP_0B~4B`、`DYEDREAM_DEEPSTONE`、`DYEDREAM_SANDSTONE` 等遗漏方块。
*   **改动**（`PasterDream` `registry/creativetabs/PDCreativeTabsFunctional.java`、`PDCreativeTabsSouvenir.java`）：补入 `LIGHTBALL`、`CLAY_POT_0`、`CLAYPAN_0/2`、`CHRISTMAS_LIGHTS`、`MEMENTO_ITEM_11`、`CALLE_CARD_BLOCK` 等移植遗漏条目。

### 修复：钛矿石与深层钛矿石无法生成（补齐 biome modifier）

*   **根因**：`titanium_ore` / `deepslate_titanium_ore` 的 `configured_feature`/`placed_feature` 为手写 JSON，但缺少 `neoforge:add_features` biome modifier，没有注入任何群系，导致实际不生成。
*   **改动**（`PasterDream` `data/PDWorldgenProvider.java`、`registry/PDPlacedFeatures.java`）：两者的 configured/placed 迁入 `PDWorldgenProvider` 经 DataGen 生成；新增 `TITANIUM_ORE` / `DEEPSLATE_TITANIUM_ORE` 键；钛矿 size 3、count 6、y -60~20，深层钛矿 size 3、count 3、y -60~-10。
*   **改动**（`PasterDream` `data/pasterdream/neoforge/biome_modifier/titanium_ore.json`、`deepslate_titanium_ore.json`，新）：`neoforge:add_features` 分别注入 `#pasterdream:is_dyedream` 与 `#minecraft:is_overworld` 的 `underground_ores`。
*   **迁移**（`PasterDream`）：删除 `src/main/resources` 下 4 个手写 worldgen JSON，改由 `src/generated/resources` 产出。
*   **同步**（`docs/开发指南/问题排查.md`、`docs/架构/主模组架构详解.md`）：注明钛矿 configured/placed 已走 DataGen、biome_modifier 仍为手写。
*   **验证**：`./gradlew :PasterDream:runData` BUILD SUCCESSFUL，生成 4 个 worldgen JSON；`./gradlew :PasterDream:compileJava` BUILD SUCCESSFUL；`runClient` 启动日志无 biome_modifier / configured_feature / placed_feature 解析报错；待游戏内实测染梦 y -60~20 方解石区与主世界 y -60~-10 深板岩区。

### 修复：灵魂矿土无法生成（迁移至 DataGen）

*   **根因**：灵魂矿土 `soul_ore` 的 `configured_feature`/`placed_feature` 为手写 JSON，且缺少 `neoforge:add_features` 的 biome modifier 将其注入灵魂沙峡谷，导致实际不生成。
*   **改动**（`PasterDream` `data/PDWorldgenProvider.java`，新）：新增 `DatapackBuiltinEntriesProvider`（`RegistrySetBuilder` 按 configured→placed→biome_modifier 顺序），以代码定义 `Feature.ORE`（灵魂沙→灵魂矿土，size 16，`discard_chance_on_air_exposure 0`）、`CountPlacement(10)+InSquare+HeightRange(0~120)+BiomeFilter` 与注入 `minecraft:soul_sand_valley` `underground_ores` 的 modifier。
*   **改动**（`PasterDream` `PasterDreamMod.java`、`registry/PDPlacedFeatures.java`）：`gatherData` 注册 `PDWorldgenProvider`，新增 `SOUL_ORE` 键，旧矿石 `ORE_AMBER_CANDY`/`ORE_DYEDREAMDUST`/`ORE_DYEDREAMQUARTZ` 标记 `@Deprecated(forRemoval)` 计划 0.11.x 迁移。
*   **迁移**（`PasterDream`）：`configured_feature/soul_ore.json`、`placed_feature/soul_ore.json` 由 `src/main/resources` 迁至 `src/generated/resources`，新增 `neoforge/biome_modifier/soul_ore.json`。
*   **同步**（`docs/开发指南/问题排查.md`、`docs/架构/主模组架构详解.md`）：注明 `soul_ore` 已走 DataGen、DataGen Provider 由 2 个增至 3 个。

### 重构：暗影书架四合一（shadowshelf_0/1/2/3 → shadowshelf / shadowshelf_with_key）

*   **背景**：早期移植版用 4 个独立方块模拟暗影书架的纹理/带钥匙差异，占用注册名且冗余。
*   **改动**（`PasterDream` `registry/blocks/PDBlocksDungeon.java:176`、`registry/PDBlocks.java`、`registry/items/PDItemsBlocks.java`）：删除 `SHADOWSHELF_0/1/2/3`，合并为 `shadowshelf`（blockstate 加权随机模型实现 3 种纹理）与独立的 `shadowshelf_with_key`（钥匙正面纹理，掉落 `shadow_dungeon_key`）。
*   **改动**（`PasterDream` `assets/pasterdream/blockstates/shadowshelf.json`，新；`blockstates/shadowshelf_0..3.json` 删除）：新增加权随机变体 blockstate；`models/block/shadowshelf_3.json → shadowshelf_with_key.json`、`models/item/...` 同步合并，删除 `shadowshelf_1/2/3` 模型。
*   **改动**（`PasterDream` `data/pasterdream/loot_table/blocks/shadowshelf.json`，新/改；`shadowshelf_1.json`、`shadowshelf_2.json` 删除；`shadowshelf_with_key.json` 新增）：战利品表随方块合并。
*   **改动**（`PasterDream` `data/minecraft/tags/block/mineable/axe.json`、`registry/creativetabs/PDCreativeTabsShadow.java`、`lang/*.json`、`compat/PDIdAliases.java` 新增 `shadowshelf_0/1/2 → shadowshelf`、`shadowshelf_3 → shadowshelf_with_key`）：标签、创造栏、语言与旧存档别名同步。

### 新增：风之旅途岛系布局 API 基础设施

*   **新增**（`PasterDreamAPI` `api/worldgen/island/`，新）：`IslandArchetype`（普通/风蚀原型）、`IslandClusterLayout`（集群布局）、`IslandLayoutManager`（由世界种子+集群网格坐标确定性推导主岛形状/半径、环岛、云托锚点、蛀空区、风向）、`IslandLayoutSpec`（形态/阈值参数规格）、`IslandSurface`（逐列地表查询）——抽取确定性布局以规避 Feature 的 ±1 区块写半径限制。
*   **同步**（`docs/设计/风之旅途重构.md`，新）：记录"中心主岛 + 环绕环岛 + 岛下云托"的岛系集群设计与分期。
*   **说明**：本期为 API 骨架与设计落地，主模尚未接入使用。

### 新增：风之旅途风泊树程序化生成（18 变体）与调试变体水晶

*   **新增**（`PasterDream` `worldgen/feature/WindMoorTreeGenerator.java`，新）：程序化风泊树——贴合地表的竖直主干、主干 40%~85% 高度区间分层伸出的上扬枝节、顶端三层收缩椭球树冠（内层留孔+外层随机缺失）、枝端/树冠下按概率挂风泊悬挂藤并交由随机刻向下生长。
*   **新增**（`PasterDream` `registry/WindMoorTrees.java`，新）：按 `大中小 × 低中高 × 少/多枝节` 注册 18 种 CUSTOM 装饰变体 `windmoor_tree_<size>_<height>_<branch>`，目标群系标签 `#pasterdream:is_wind_journey`。
*   **新增**（`PasterDream` `data/pasterdream/worldgen/configured_feature/*`、`placed_feature/*`、`neoforge/biome_modifier/wind_journey_trees.json`、`tags/worldgen/biome/is_wind_journey.json`，新）：18 组 configured/placed JSON 与 biome modifier 注入 `wind_journey_islands`/`wind_journey_desert` 的 `vegetal_decoration` 阶段，新增风之旅途群系标签。
*   **新增**（`PasterDream` `item/DebugVariantWandItem.java`，新；`network/DebugWandVariantPayload.java`、`client/DebugWandClientEvents.java`）：单水晶承载多变体，潜行+滚轮切换（存 `CUSTOM_DATA` 客户端同步服务端），支持结构 NBT 与 `ConfiguredFeature` 装饰两种放置模式。
*   **移除/调整**：删除旧 `patch_dyedream_grass` 配置地物与 `debug_wand_grass` 调试物品，改由 `debug_wand_wind_journey` 统一调试变体（见 `docs/设计/存档兼容层.md` 未纳入别名说明）。

### 修复：竞技场遗迹真实放置确认与感染门控重构

*   **根因**：结构查询方法 `findGenerationPoint` 承担副作用，第三方结构搜索/预览工具一次查询即触发不可逆感染并永久锁死竞技场生成；传送门方块 `onPlace` 无法区分真实放置与查询；感染缺少 BOSS 击败/群系边界/配置门控。
*   **改动（放置确认）**（`PasterDream` `block/AaroncosArenaPortalsBlock.java`、`worldgen/PDAaroncosArenaWorldgen.java`）：借助 `hasPostProcess=true` 的 PostProcessing 二层 `onPlace`（`oldState.getBlock()==this`）判定真实落地，经 `offerPendingPlacement` 线程安全队列转主线程 `confirmArenaPlacement` 落库 `placed`/中心并启动群系刷写与感染；查询路径零写入，`onServerStopped` 清空队列。
*   **改动（状态数据）**（`PasterDream` `world/PDAaroncosArenaSpawnData.java`）：`placed` 改 volatile，新增 `biomePainted`/`defeated` 持久化，`rollback()` 拆为 `markDefeated()` 与 `resetBiomePainted()`。
*   **改动（统一门控）**（`PasterDream` `world/ArenaInfectionUtils.java`、`world/ArenaRuinInfection.java`）：`infectSurroundingBlocks` 统一校验配置开关（默认关）、仅主世界、BOSS 未击败、目标在 `#pasterdream:is_aaroncos_arena` 群系标签内；`isAlreadyInfected` 改包内共享以与回滚判定一致；感染循环每批检查开关与击败状态可中途终止。
*   **改动（治愈/清理）**（`PasterDream` `worldgen/PDAaroncosArenaWorldgen.java`）：`cureInfection`（BOSS 击败）与 `clearInfection`（配置关闭自动清理，不标记击败）共用 `rollbackInfection`；群系还原用 `BiomeSource.getNoiseBiome` 按种子重推导原始群系（`restoreArenaBiomeAsync`/`restoreArenaBiome`，四分位量化对齐 `FillBiomeCommand`），服务器启动时续期未完成的还原。
*   **改动（交互健壮性）**（`PasterDream` `block/BrokenShadowDungeonProtalBlock.java`、`block/GuardCrystalBlock.java`）：修复/出口传送与水晶自毁均改用 BE 内"截止时刻"触发锁（按游戏时间自动过期）防重复；修正破损传送门用 `inventoryMenu.getCraftSlots()` 搜索导致材料不消耗的 bug（改为直接 `shrink` 手持主/副手）；水晶自毁改服务端独占并用 `removeCrystal` 显式移除方块实体/强制同步清残影。
*   **验证补充**（`PasterDream` `smoketest/PDArenaInfectionVerifyHooks.java`，新）：新增竞技场感染门控与放置确认断言。

### 调整：染梦晶芽改为仅洞穴生成并调整生成密度

*   **背景**：染梦晶芽 `dyedream_bud_0/1/2` 混入地表装饰物（水晶簇/水晶花园/柱体/云团等），与"晶芽仅生长于洞穴"的设定冲突。
*   **改动**（`PasterDream` `registry/DyedreamDecorations.java`、`registry/IceDecorations.java:141`、`registry/ModDecorations.java`）：从全部地表装饰的 `SimpleWeightedRandomList` 中移除晶芽方块，水晶簇/花园/柱面只保留矿石与水晶灯。
*   **改动**（`PasterDream` `registry/IceDecorations.java:139` `registerBudDyedream`）：晶芽保留为唯一自然入口，`clusterChance 0.1 → 0.4`、`clusterSize 6 → 8`、`clusterRadius 3 → 4`。
*   **改动**（`PasterDream` `data/pasterdream/worldgen/configured_feature/bud_dyedream.json`、`placed_feature/bud_dyedream.json`）：放置由 `rarity_filter 24 + uniform above_bottom16~0` 改为 `count 4 + trapezoid -60~40`，强制洞穴高度范围。
*   **改动**（`PasterDreamAPI` `api/worldgen/decor/GenericDecorationFeature.java`、`DecorationJsonGenerator.java`）：增强 BUD 类型洞穴环境判定（只接受空气+实心地面），并在 JSON 生成器中跳过 BUD 手写洞穴配置。

### 修复：染梦维度夜间雾色、霞光相位与环境光照

*   **根因 1**：染梦维度 `getSunriseColor` 用 `sin(timeOfDay * 2π)` 计算太阳高度，`timeOfDay` 语义为正午 0/午夜 0.5，导致霞光出现在正午与午夜而非黄昏/黎明。
*   **根因 2**：`getBrightnessDependentFogColor` 第二参数实为天空亮度因子 `clamp(cos(timeOfDay*2π)*2+0.5,0,1)`（0 午夜 / 0.5 地平线 / 1 正午），代码却按 -1~1 的太阳高度做三色插值，夜/昼色错位。
*   **改动**（`PasterDream` `client/ClientSetup.java:325,364`）：`getSunriseColor` 由 `sin` 改为 `cos`；参数 `sunHeight` 更名 `brightness`。
*   **改动**（`PasterDreamAPI` `api/client/shading/BiomeShadingAPI.java`、`BiomeShadingEntry.java`、`BiomeShadingRegistry.java`）：插值改为按天空亮度分段（`b>=0.5` 黄昏→日间，`b<0.5` 夜色→黄昏），以 `Mth.clamp` + 线性 `lerp` 重写 `interpolateTriColor`。
*   **改动**（`PasterDream` `registry/PDDimensions.java:71`、`PasterDreamAPI` `api/dimension/APIDimensions.java:60`、`data/pasterdream/dimension_type/dyedream_world.json:11`）：`ambient_light 0.5 → 0`，消除夜间与洞穴恒亮的 50% 底噪。

### 修复：扩大染梦裂隙原点避让半径避免重复生成

*   **背景**：裂隙结构 `origin_exclusion_radius=2` 过小，出生点原点的既有裂隙与随机生成的裂隙可能重叠/重复。
*   **改动**（`PasterDream` `data/pasterdream/worldgen/structure/struct_dyedream_crack_0.json:17`、`struct_dyedream_crack_1.json:17`）：`origin_exclusion_radius 2 → 16`，扩大原点避让范围。

### 调整：降低染梦维度空中结构密度并上抬云泡泡生成高度

*   **改动**（`PasterDream` `worldgen/feature/CloudBubbleGenerator.java:32`）：云泡泡中心高度由 `waterSurfaceY + 25 + rand(20)` 改为 `+ 45 + rand(25)`，避免贴近地面/水面。
*   **改动**（`PasterDream` `data/pasterdream/worldgen/placed_feature/cloud_bubble.json`、`floating_island.json`）：`cloud_bubble` rarity `4 → 16`、`floating_island` rarity `6 → 30`，降低空中结构密度。
*   **改动**（`PasterDream` `data/pasterdream/worldgen/structure_set/struct_dyedream_crack_0.json`）：`random_spread` `spacing 37 → 83`、`separation 20 → 45`，进一步稀疏化裂隙结构集。

### 调整：冰门/冰拱门/冰柱/浮冰堆生成密度至中等水平

*   **背景**：冷域/冰冻海洋冰系结构密度失衡——部分过密（冰门、浮冰堆、冰柱），部分过稀（破损冰拱门）。
*   **改动**（`PasterDream` `registry/IceDecorations.java:100`）：冰门 `ice_gate` `rarity 5 → 30`。
*   **改动**（`PasterDream` `registry/OceanDecorations.java:183,223,257,289`）：`floating_ice_mound 2 → 10`、`ice_arch 36 → 40`、`ice_arch_ruined 105 → 80`、`dyedream_ice_pillar 3 → 15`。
*   **改动**（`PasterDream` `data/pasterdream/worldgen/placed_feature/`）：同步手写 JSON——`dyedream_ice_pillar` 3→15、`floating_ice_mound` 2→10、`ice_arch` 8→40、`ice_arch_ruined` 20→80、`ice_gate` 与 `ice_gate_frozen_ocean` 5→30。

### 修复：迁移剩余废弃竞技场群系 ID 引用

*   **背景**：竞技场群系由旧 `aaroncos_arena_biome` 改名为 `aaroncos_arena`（并新增 `aaroncos_arena_void`），部分代码仍引用旧常量 `PDBiomes.BIOME_AARONCOS_ARENA`，导致群系氛围/感染判定与实际注册名不一致。
*   **改动**（`PasterDream` `client/PDClientEvents.java:131,583`）：竞技场群系判定改用 `PDBiomes.AARONCOS_ARENA_VOID`（自言自语文本与暗影迷雾触发）。
*   **改动**（`PasterDream` `worldgen/PDAaroncosArenaWorldgen.java:98`）：`setArenaBiomeAsync` 取群系改为 `PDBiomes.AARONCOS_ARENA`。
*   **改动**（`PasterDream` `client/ClientSetup.java:392`、`registry/PDRuinsRegistration.java:260`）：注释中的旧群系名统一为新名。

### 修复：沙漠剑升级与工坊锻造丢失附魔

*   **根因**：`DesertHeroTombBlock` 升级时用 `new ItemStack(TRUE_DESERT_SWORD)` 整体替换主手，丢弃旧剑附魔、自定义名称、工坊强化与耐久；`WeaponWorkshopBlockEntity` 产出 `new ItemStack(recipe.result())`，未继承 5 个输入槽的附魔。
*   **改动**（`PasterDream` `block/DesertHeroTombBlock.java:231`）：改用 `player.getMainHandItem().transmuteCopy(PDItemsTools.TRUE_DESERT_SWORD.get(), 1)` 保留原物品全部数据组件。
*   **改动**（`PasterDream` `block/entity/WeaponWorkshopBlockEntity.java:229`）：扣减输入前用 `EnchantmentHelper.getEnchantmentsForCrafting` 读取 5 槽附魔，同名取最高等级合并，再用 `EnchantmentHelper.setEnchantments` 写入产物。
*   **验证补充**（`PasterDream` `smoketest/PDDesertTombVerifyHooks.java`、`PDWorkshopVerifyHooks.java`，新；`PDPortingVerifyTest.java` 登记）：新增沙漠之墓/武器工坊附魔保留断言并纳入移植验证套件。

### 新增：旧注册名兼容别名层与旧存档升级提示

*   **新增**（`PasterDreamAPI` `api/compat/`，新）：`IdAlias` / `IdAliasTable` / `IdAliasRegistry` / `IdCompatAPI`——基于 NeoForge `IRegistryExtension#addAlias` 的旧→新注册名别名层，提供登记、`applyStatic(DeferredRegister...)`（须在 `RegisterEvent` 前调用，按 `from` 去重避免 "Infinite alias loop"）、`resolve` 与 `selfCheck`（须在 `FMLCommonSetupEvent` 注册完成后调用，报告生效/遮蔽/失败）。
*   **新增**（`PasterDream` `compat/PDIdAliases.java`）：主模别名表——风之骑士唤醒台 `wind_knight_spawnblock_0..4 → wind_knight_spawnblock`、`meltdream_crystal_lamp → dyedream_lantern`、`yinhul_cotton_candy → silver_fox_cotton_candy`、调试杖改名等。
*   **新增**（`PasterDream` `compat/`）：存档兼容层（`PDSaveCompatHandler` 于 `LevelEvent.CreateSpawnPosition` 写新档标记、`ServerStartedEvent` 判旧档并后台备份、`PlayerLoggedInEvent` 投递提示；`PDSaveCompatData` SavedData 记录 schema 版本；`PDSaveSchema.CURRENT_ID_SCHEMA_VERSION=1`；`SaveBackupHelper`；`command/PDSaveCommand` 提供 `/pasterdream save status|backup|upgrade`）。
*   **新增**（`PasterDream` `network/SaveUpgradePromptPayload` / `SaveUpgradeConfirmPayload`、`client/PDSaveCompatClientEvents`、`client/screen/SaveUpgradePromptScreen`）：单机弹旧存档备份/更新提示界面（确认并继续/打开备份目录/退出世界），确认后仅写 schema 标记，不重写世界数据。
*   **配置**（`PDCommonConfig.java`）：新增 `[Save Compat]` 段 `save compat enabled`（默认 true）、`save compat auto backup`（默认 true）；`PDBiomes` 注释旧群系名移除时间表由 0.9.10 改为 0.11.0。

### 文档：数字后缀注册 ID 迁移计划与扫描脚本

*   **背景**：移植自 MCreator 的注册名大量使用 `_<数字>` 后缀（同族变体、无意义 `_0`、粒子 `*_0_particle`），需一套统一的改名/别名迁移口径。
*   **新增**（`tools/scan_numeric_registrations.py`，新，321 行）：扫描 5 个模块 `src/main/java/**/registry/**` 字符串字面量与 `src/main/resources/data/**` 注册目录文件名，命中名称含 `_<数字>` 段（结尾或后接 `_`），排除 `libs/`、`build/`、`deprecated/`、`packs/` 及调试法杖/战利品工具的结构 ID 参数；输出 `docs/设计/ID迁移计划.md`。
*   **产出**：文档按代码侧（方块/方块实体/物品/实体/状态效果/音效/粒子/菜单/进度/结构）与数据包侧（群系/进度/结构/结构集/模板池/配置地物/放置地物/战利品表/配方/画）分类统计。
*   **迁移建议**：P0「无同族 `_0` 直接去数字」（如 `meltdream_crystal_0`→`meltdream_crystal`）、P1「同族编号语义化」、P2「群系旧数字名按 `PDBiomes` 时间表移除」、结构/地物/配方等编号保留。

### 修复：风之旅途风泊树生成密度不足、树干悬空与落位逻辑

*   **背景/根因**：风泊树 `windmoor_tree_0` 原以原版 `minecraft:jigsaw` 结构类型生成，结构集密度过低，且 `start_height.absolute=0` 使树干浮在岛面上方；简单下调高度无法保证树干区域落在连续岛面，树冠伸出岛缘即被误判。
*   **改动**（`PasterDream` `data/pasterdream/worldgen/structure_set/windmoor_tree_0.json`）：`random_spread` 由 `spacing 16 / separation 6` 恢复为 `spacing 4 / separation 3`，恢复生成密度。
*   **改动**（`PasterDream` `data/pasterdream/worldgen/structure/windmoor_tree_0.json`）：`start_height.absolute 0 → -8`，使树干下沉贴合岛面。
*   **改动**（`PasterDream` `worldgen/structure/WindmoorTreeStructure.java`，新；`registry/PDRuinsRegistration.java` `registerWindmoorTree`）：新增自定义结构类型 `pasterdream:windmoor_tree_0`，委托 `JigsawStructure` 生成后以 `hasStableTrunkSupport` 校验树干承重区（`support_radius=4`、`sample_step=4`、`min_surface_y=10`、`max_surface_difference=24`）——要求树干中心 ±4 格地表为接近中心高度的连续岛面，树冠可自然伸出岛缘；结构集同步细分 `spacing 3 / separation 2`。
*   **验证补充**（`PasterDream` `smoketest/PDStructureVerifyHooks.java` `verifyWindmoorInterior`）：新增平坦/缓坡/树冠出岛/虚空/裂隙/悬崖/编解码七项断言。

### 修复：染梦晶芽粒掉落随大小与时运缩放

*   **背景/根因**：`DyedreamBudBlock` 掉落固定为 1 个晶芽粒，且不掉落本体，与晶芽尺寸/附魔无关。
*   **改动**（`PasterDream` `block/DyedreamBudBlock.java:209`）：`getDrops` 读取工具附魔，精准采集掉落本体；否则按尺寸计算基础数量（size0 2~3、size1 1~3、size2 1~2、size3 1）并乘原版时运乘数 `max(nextInt(fortune+2), 1)`。
*   **改动**（`PasterDream` `data/pasterdream/loot_table/blocks/dyedream_bud_{0,1,2}.json`）：改为 `alternatives`，精准采集分支掉本体、否则掉晶芽粒并叠加 `apply_bonus`（`ore_drops`）与 `explosion_decay`。

### 修复：洞穴荧光蘑菇生成逻辑

*   **背景/根因**：原实现逐格边检边写，空间不足时会留下半截蘑菇；附着面语义不统一（地板也被当作可吸附），且未避让地下遗迹。
*   **改动**（`PasterDream` `worldgen/feature/CaveGlowMushroomFeature.java:82,158,179,232`）：改为“计划—提交”两阶段放置，菌柄高度自适应降级（放不下则缩短重试），新增 `isNearRegisteredRuin` 遗迹避让，附着面仅接受天花板型（固体下方为空气），墙壁附着返回固体上方 Y。
*   **改动**（`PasterDreamAPI` `api/worldgen/WorldGenUtils.java` 与 `api/worldgen/decor/GenericDecorationFeature.java`）：将通用工具/遗迹检测下沉到 `WorldGenUtils`，移除 `GenericDecorationFeature` 中重复实现。
*   **改动**（`PasterDream` `placed_feature/cave_glow_mushroom.json`）：新增 `rarity_filter chance 2`，`count` 3→1。

### 修复：移除染梦地下蜂窝状地形

*   **背景/根因**：`dyedream_world` 噪声设置中的垂直渐变表层规则（浅层砂岩、冻原永久冻土包冰）与洞穴雕刻叠加，生成类似蜂窝/棋盘的非自然地下结构。
*   **改动**（`PasterDream` `data/pasterdream/tags/block/dyedream_carver_replaceables.json` 新增）：定义含 `overworld_carver_replaceables`、方解石、雪块及染梦土系方块的雕刻可替换标签。
*   **改动**（`PasterDream` `worldgen/configured_carver/{dyedream_canyon,dyedream_cave,dyedream_cave_extra_underground}.json`）：`replaceable` 由 `#minecraft:overworld_carver_replaceables` 改为 `#pasterdream:dyedream_carver_replaceables`。
*   **改动**（`PasterDream` `worldgen/noise_settings/dyedream_world.json`）：移除产生蜂窝地形的砂岩/永久冻土表层规则。

### 重构：晶芽生成逻辑（BUD 装饰类型）

*   **背景/根因**：原冰晶簇/花园用固定配置生成，缺乏洞穴吸附、含水与簇状集群控制，且与普通装饰耦合。
*   **改动**（`PasterDreamAPI` `api/worldgen/decor/DecorationType.java`、`DecorationBuilder.java`、`DecorationConfig.java`、`GenericDecorationFeature.java`）：新增 `BUD` 类型，Builder 增加 `clusterChance`/`clusterRadius`/`waterlog`，Config Codec 增加 `cluster_chance`/`cluster_radius`/`waterlog` 并统一参数校验；`placeBud` 实现洞穴判定、六向支撑扫描、`FACING`+`WATERLOGGED` 写入与概率集群。
*   **改动**（`PasterDream` `registry/IceDecorations.java:150`、`registry/OceanDecorations.java:300`、`registry/ModDecorations.java:137,150`）：新增 `registerBudDyedream`/`registerBudIce` 并挂入注册流程。
*   **改动**（`PasterDream` `worldgen/configured_feature/{bud_dyedream,bud_ice}.json`、`placed_feature/{bud_dyedream,bud_ice}.json` 新增，`ice_crystal_{cluster,garden}.json` 移除；`neoforge/biome_modifier/*.json`）：以 `bud_dyedream`/`bud_ice` 替换旧冰晶生成，并调整群系修改器。
*   **改动**（`PasterDream` `registry/PDItems.java`、`registry/creativetabs/PDCreativeTabsDebug.java`、`assets/pasterdream/models/item/debug_wand_bud_{dyedream,ice}.json`、`lang/{en_us,zh_cn}.json`）：新增两种调试法杖物品、模型与语言。

### 修复：研钵在合成中被消耗

*   **背景/根因**：研钵作为合成工具，原注册为普通物品，参与合成后被消耗。
*   **改动**（`PasterDream` `item/MortarItem.java:9`）：新增 `MortarItem`，`hasCraftingRemainingItem` 返回 `true`、`getCraftingRemainingItem` 返回 `itemStack.copy()`。
*   **改动**（`PasterDream` `registry/items/PDItemsMaterials.java`）：`MORTAR` 改注册为 `MortarItem::new` 且 `stacksTo(1)`。

### 新增：BiomeShading API（数据驱动群系雾色）

*   **背景/根因**：群系三色雾色长期硬编码在 `ClientSetup.getBrightnessDependentFogColor`，难以扩展与复用。
*   **改动**（`PasterDreamAPI` `api/client/shading/BiomeShading.java`、`BiomeShadingEntry.java`、`BiomeShadingAPI.java`、`BiomeShadingRegistry.java`）：新增着色接口/记录/门面/注册表，支持代码注册与数据包条目（`replaceDataEntries` 优先级更高），提供按太阳高度在日/黄昏/夜间三色间插值的 `interpolateColor`。
*   **改动**（`PasterDream` `client/PDShadingRegistration.java:64`）：集中注册全部群系默认三色配置（平原/森林/冻原/海洋/深海/蘑菇/海岸/密林/冷域）。
*   **改动**（`PasterDream` `client/ClientSetup.java:91,327,366`）：初始化时调用 `registerDefaults()`，雾色获取改走 `BiomeShadingAPI.interpolateColor`。

### 修复：染梦河流生成逻辑与视觉效果

*   **背景/根因**：染梦河流群系的雾色/水色偏离整体梦幻色调，维度噪声中河流尺寸与过渡偏小。
*   **改动**（`PasterDream` `data/pasterdream/worldgen/biome/dyedream_river.json`）：`fog_color`/`water_color`/`water_fog_color` 调整为与平原一致的梦幻数值。
*   **改动**（`PasterDream` `data/pasterdream/dimension/dyedream_world.json`）：河流 `size` 0.45→0.50、`transition_size` 0.7→0.65。
*   **改动**（`PasterDream` `client/ClientSetup.java`）：染梦河流雾色改为与平原相同的三色配置。

### 修复：发光鱿鱼刷新数量上限

*   **背景/根因**：仅按位置/高度限制无法约束局部数量，染梦维度仍可能单点堆积过多发光鱿鱼。
*   **改动**（`PasterDream` `config/PDCommonConfig.java:104`）：新增 `glow squid spawn cap enabled`（默认 true）与 `glow squid spawn cap`（默认 16，范围 1~128）。
*   **改动**（`PasterDream` `registry/PDEntityEvents.java:337`）：新增 `capGlowSquidSpawn`，在 `MobSpawnEvent.PositionCheck` 中统计玩家模拟距离半径内非持久化发光鱿鱼，达到上限则拒绝自然生成。
*   **改动**（同文件 `:315`）：`restrictGlowSquidSpawn` 在保留原版 `checkGlowSquidSpawnRules` 基础上追加海床高度判定。

### 重构：染梦裂隙生成逻辑（浮岛结构 + 原点避让）

*   **背景/根因**：原 `DyedreamCrackStructure` 仅包装 `JigsawStructure` 做配置拦截，裂隙高度由结构集固定，易与原点浮岛重叠；`OriginFixedPlacement`/`struct_dyedream_crack_origin` 的固定 (0,0) 方案职责分散。
*   **改动**（`PasterDream` `worldgen/structure/FloatingCrackStructure.java`）：新增浮岛式 jigsaw 结构，复制 `JigsawStructure` 字段并追加 `float_low_y`/`float_high_y`（120→110/160 动态浮空）与 `origin_exclusion_radius`/`origin_chunk_x/z`（原点方形避让）。
*   **改动**（`PasterDream` `registry/PDRuinsRegistration.java`、`PDStructurePlacements.java`）：`struct_dyedream_crack_1`（主世界）与新增 `struct_dyedream_crack_0`（染梦）均改用 `FloatingCrackStructure`；删除 `DyedreamCrackStructure` 与 `origin_fixed` 策略。
*   **改动**（`PasterDream` `data/pasterdream/worldgen/structure/struct_dyedream_crack_{0,1}.json`）：补入 `float_low_y/float_high_y/origin_exclusion_radius/origin_chunk_*`；删除 `struct_dyedream_crack_origin.json` 及其 structure_set。
*   **改动**（`PasterDream` `worldgen/PDTeleportLanding.java`）：原点落点改为按浮岛模板包围盒扫描裂隙；`smoketest/PDStructureVerifyHooks.java` 期望结构数 42→43。

### 修复：降低结构密度并改为地表相对放置

*   **背景/根因**：大量同族结构各自拆成独立 structure_set 竞争生成，且起始高度多用固定/底部偏置值，导致密度过高、洞穴/斜坡放置异常。
*   **改动**（`PasterDream` `worldgen/structure_set/`）：把同族集合合并为单一 `_set`（`small_ballon_set`、`dream_church_set`、`shadow_chain_set`、`shadow_foundry_set`、`shadow_fungus_house_set`/`_nest_set`、`shadow_shelter_set`、`shadow_underground_workroom_set`、`traveler_house_set`、`stone_pillar_set`、`wind_infested_stone_set`、`big_bubbles_*_set`、`hot_air_balloon_set`、`pinkagaric_house_set` 等），并普遍上调 `spacing`/`separation`。
*   **改动**（`PasterDream` `worldgen/structure/{small_ballon_*,hot_air_balloon_*,wind_pond_0,windmoor_tree_0,wind_island_0,big_bubbles_*,breakwind_curtain_0}.json`）：高度提供器改为 `absolute: 0` 并使用 `project_start_to_heightmap: WORLD_SURFACE_WG`，实现地表相对放置。
*   **改动**（`PasterDream` `worldgen/structure_set/{shadow_dungeon,shadow_hand_0,warped_relic_0,dyedream_campsite_0_set,dyedream_laboratory_0_set,dyedream_tavern_set,garden_decryption_*}.json`）：单独拉大间距或调整结构集合。

### 修复：降低巨型树频率并将结构移出染梦平原

*   **背景/根因**：染梦平原同时承载巨型树/世界树结构导致拥挤，愿望树与世界树结构落入平原破坏开阔感。
*   **改动**（`PasterDream` `neoforge/biome_modifier/dyedream_plains_trees.json`）：平原移除 `dyedream_tree_colossal`。
*   **改动**（`PasterDream` `configured_feature/dyedream_tree_selector.json`、`placed_feature/{dyedream_tree_colossal,dyedream_tree_worldtree,dyedream_trees_sparse}.json`）：选择器 chance 0.013→0.005，colossal chance 35→80，worldtree chance 80→160，稀疏树改高 `noise_factor`/负 `noise_offset`。
*   **改动**（`PasterDream` `tags/worldgen/biome/dyedream_biome_no_plains.json`、`worldgen/structure/dream_wishingtree_{0,1}.json`）：新增“排除平原”标签并用于愿望树结构。
*   **改动**（`PasterDream` `worldgen/structure/dyedream_worldtree_{0,1}.json`、`registry/PDRuinsRegistration.java`）：世界树结构群系改为 `dyedream_forest`。

### 新增：MomoNyako 白猫玩偶（EPIC 品质）

*   **背景/根因**：新增人偶收藏物品，所需模型/纹理/掉落/语言条目此前缺失。
*   **改动**（`PasterDream` `registry/PDCustomDolls.java:51`）：通过 `DollAPI.create("momonyako_doll")` 注册，复用 `eoul_doll` 骨骼模型、替换专属纹理、`canHoldItems(false)`，物品属性 `Rarity.EPIC` + `fireResistant()`。
*   **改动**（`PasterDream` `assets/pasterdream/blockstates/momonyako_doll.json`、`models/item/momonyako_doll.json`、`models/custom/momonyako_doll_particle.json`、`textures/block/momonyako_doll.png`）：补齐方块状态、物品模型、粒子模型与纹理。
*   **改动**（`PasterDream` `data/pasterdream/loot_table/blocks/momonyako_doll.json`、`data/minecraft/tags/block/mineable/axe.json`、`lang/{en_us,zh_cn}.json`）：添加战利品表、斧可挖掘标签与中英文名称。

### 修复：染梦巨木悬空与结构树落地支撑

*   **背景/根因**：巨木 origin 对齐到区块中心后仍复用原 heightmap 的 Y，地形高差导致整树悬空；结构树直接放置，未检测底部支撑，斜坡上产生悬空树干/灌木。
*   **改动**（`PasterDream` `worldgen/tree/DyedreamTreeFeature.java`）：对齐 X/Z 后按新区块中心重新查询 `MOTION_BLOCKING` 高度，修正 Y。
*   **改动**（`PasterDream` `worldgen/tree/DyedreamStructureTreeFeature.java:104,138`）：新增 `hasGroundSupport`，取最低占用层每列正下方必须为实体表面；配套 `mixin/StructureTemplateAccessor.java` 暴露 `palettes` 以遍历模板全部方块。
*   **改动**（`PasterDream` `worldgen/tree/trunk/Dyedream{Colossal,Mega,WorldTree}TrunkPlacer.java`）：新增 `findMinGroundY`/`fillTrunkGap`，按足迹范围将主柱向下延伸填补悬空。
*   **改动**（`PasterDreamAPI` `api/config/PDAddonConfigRegistry.java`）：注册表改用 `ConcurrentHashMap`/`CopyOnWriteArrayList` 以适配 FML 多线程模组构造。

### 修复：粉色史莱姆跳跃速度异常

*   **背景/根因**：`PinkSlimeEntity` 跳跃调用 `setDeltaMovement(look.x * 3, 0.5, look.z * 3)`，弹射过远过快。
*   **改动**（`PasterDream` `entity/mob/PinkSlimeEntity.java:128`）：改为 `look.x / 4, 0.3, look.z / 4`，对齐原模组水平/垂直初速度并补充注释。

### 修复：染梦树生成比例向程序化树倾斜

*   **背景/根因**：结构树（NBT）在原版移植中占比过高，挤占程序化树（`bb_trees_*`）分布，森林外观偏离原模组。
*   **改动**（`PasterDream` `neoforge/biome_modifier/bb_plains_trees.json`、`placed_feature/bb_trees_{aspen,blossom,bush,cherrybush,conifer,palm,plaintree,snowtree}.json`）：平原移除 `bush`/`plaintree` 仅留 `cherrybush`，并整体调高 `noise_offset`、调低 `noise_to_count_ratio` 提升程序化树占比。
*   **改动**（`PasterDream` `neoforge/biome_modifier/dyedream_shore_trees.json`、`placed_feature/dyedream_trees_shore.json`）：新增海岸树生成配置。
*   **改动**（`PasterDream` `placed_feature/dyedream_trees_icy.json`、`dyedream_trees_sparse.json`）：生成器由 `rarity_filter` 改为 `noise_based_count`，`dyedream_trees_dense.json` count 4→6。

### 重构：裂隙与传送水晶落点安全计算

*   **背景/根因**：旧实现“自顶向下找第一个非空气方块”，会落在云顶/树冠/洞顶；兜底 `above(3)` 无任何安全检查；且染梦原点裂隙依赖随机结构，落点不确定。
*   **改动**（`PasterDream` `worldgen/PDTeleportLanding.java:64,132`）：新增统一工具类，提供 `findDyedreamOriginArrival`（定位原点浮岛裂隙并在其面前/两侧降落）与 `findSafeRespawnLanding`（Heightmap + 碰撞箱/流体校验 + 螺旋地面搜索），核心 `isSafeLanding` 要求下方实心、玩家碰撞箱 2 格空、无流体。
*   **改动**（`PasterDream` `block/DyedreamCrackBlock.java`、`item/DyedreamTeleportCrystal.java`）：删除各自 `findSafePosition`，统一调用 `PDTeleportLanding`。
*   **改动**（`PasterDream` `structure/placement/OriginFixedPlacement.java`、`registry/PDStructurePlacements.java`、`data/pasterdream/worldgen/structure/struct_dyedream_crack_origin.json`、`structure_set/struct_dyedream_crack_origin_set.json`）：新增 `origin_fixed` 放置策略，锁定 (0,0) 区块确保原点裂隙必生成。

### 修复：发光鱿鱼在染梦含水洞穴大量生成

*   **背景/根因**：原版 `GlowSquid` 生成仅要求水方块，叠加染梦维度 `aquifers_enabled: true` 后，海洋群系覆盖区的地下含水洞穴成为大量生成点，造成卡顿。
*   **改动**（`PasterDream` `registry/PDEntityEvents.java:315`）：新增 `restrictGlowSquidSpawn`，染梦维度内要求生成点 Y 不低于海床高度图 `OCEAN_FLOOR_WG`，露天水体仍可生成。
*   **改动**（`PasterDream` `worldgen/biome/{dyedream_cold_ocean,dyedream_deep_ocean,biome_dyedream_3,biome_dyedream_deep_ocean}.json`）：`glow_squid` 由 `water_creature` 移入 `water_ambient`，权重与数量下调（weight 15→8、maxCount 4→2）。

### 修复：雪誓头饰的 `snow_vow_buff` 对佩戴者不生效

*   **背景/根因**：`SnowVowHeadItem` 未实现 `curioTick`，饰品佩戴时不会施加增益；且原版为“7 格内玩家”范围光环，需按本项目设计改为仅佩戴者。
*   **改动**（`PasterDream` `item/SnowVowHeadItem.java:46`）：新增 `curioTick`，服务端为佩戴者刷 20 tick 的 `snow_vow_buff`（幸运 +3、免疫燃烧/冻结）。
*   **改动**（`PasterDream` `smoketest/PDCurioVerifyHooks.java`）：校验期望由 `["7", "+3"]` 改为 `["+3"]`。
*   **改动**（`PasterDream` `assets/pasterdream/lang/{en_us,zh_cn}.json`）：`effect_1` 提示由“附近直径 7 格内玩家”改为“佩戴者”。

### 调整：废弃群系别名移除版本改为 0.11.0

*   **背景/根因**：命名重构时标注的移除版本（`since = "0.9.9"`、注释 `0.9.10`）与实际规划不符。
*   **改动**（`PasterDream` `registry/PDBiomes.java`）：所有旧群系常量 `@Deprecated(since = "0.11.0", forRemoval = true)`，注释统一为“将在 0.11.0 版本移除”。

### 修复：染梦垂藤物品与掉落形态异常

*   **背景/根因**：方块模型直接继承 `minecraft:block/vine` 且 `render_type` 非命名空间格式，导致物品/放置形态渲染错误。
*   **改动**（`PasterDream` `assets/pasterdream/models/block/dyedream_hanging_vine.json`）：父模型改为 `minecraft:block/block`，`render_type` 改为 `minecraft:cutout`，`ambientocclusion=false`，并手写单层平面 `elements`。
*   **改动**（`PasterDream` `assets/pasterdream/models/item/dyedream_hanging_vine.json`）：物品模型由方块父模型改为 `minecraft:item/generated` + `layer0`。

### 修复：抑制地下结构异常密集与云团悬挂生成

*   **背景/根因**：云团/浮岛使用 `MOTION_BLOCKING` 高度图并从顶部扫描，导致其生成在地下结构与洞穴顶板处形成“云落/hang”；多组结构集 spacing 过小、`fillHang` 开启，使地下异常拥挤。
*   **改动**（`PasterDream` `configured_feature/cloudfall_mound_{dense,sparse}.json`、`placed_feature/cloudfall_mound.json`）：`fill_hang` 改 `false`，`chance` 2→6，高度图改 `WORLD_SURFACE_WG`。
*   **改动**（`PasterDream` `registry/ModDecorations.java`）：两处云团装饰 `.fillHang(false)`，并将 `CAVE_AIR` 从 `replaceable` 中剔除。
*   **改动**（`PasterDream` `placed_feature/floating_cloud_island.json`）：高度图改 `WORLD_SURFACE_WG`。
*   **改动**（`PasterDream` `configured_feature/dyedream_tree_selector.json`、`placed_feature/dyedream_trees_dense.json`、`structure_set/{desert_cottage_0_set,dream_wishingtree_1_set}.json`）：树选择器概率下调、密林 count 10→4、结构 spacing 拉大。
*   **改动**（`PasterDream` `registry/IceDecorations.java`）：`ice_crystal_spike` rarity 1→3。

### 修复：迁移染梦维度残留的废弃群系 ID

*   **背景/根因**：命名体系重构后，客户端与噪声设置中仍残留 `biome_dyedream_*` 等旧 ID 的硬编码比较/引用。
*   **改动**（`PasterDream` `client/ClientSetup.java`、`client/DyedreamEnvironmentRenderer.java`、`client/PDClientEvents.java`、`client/audio/ModMusicManager.java`）：雾色、粒子、音乐等按群系分发逻辑全部切换为新 ID（如 `PDBiomes.DYEDREAM_PLAINS`）。
*   **改动**（`PasterDream` `data/pasterdream/worldgen/noise_settings/dyedream_world.json`）：噪声设置内的群系引用同步更新。

### 重构：生物群系命名体系改为 `{dimension}_{biome_type}`

*   **背景/根因**：旧群系 ID（`biome_dyedream_0..3`、`aaroncos_arena_biome`、`cold_domain_biome` 等）语义不透明、难以维护，且跨维度命名不统一。
*   **改动**（`PasterDream` `registry/PDBiomes.java:24`）：新增 16 个新命名 `ResourceKey`（`dyedream_plains`、`dyedream_dense_forest`、`shadow_wastes`、`wind_journey_islands`、`cold_domain_tundra`、`aaroncos_arena` 等），旧常量标记 `@Deprecated(forRemoval = true)` 保留兼容。
*   **改动**（`PasterDream` `registry/DyedreamDecorations.java`、`registry/OceanDecorations.java`、`registry/IceDecorations.java`、`registry/PDRuinsRegistration.java`）：装饰物与遗迹群系引用切换到新 ID。
*   **改动**（`PasterDream` `data/pasterdream/worldgen/biome/*.json`、`data/pasterdream/dimension/*.json`、`neoforge/biome_modifier/*.json`、`data/pasterdream/skyboxes/*.json`、`tags/worldgen/biome/*.json`、`assets/pasterdream/lang/{en_us,zh_cn}.json`）：群系 JSON 重命名、维度/修改器/天空盒/标签/语言键批量迁移。
*   **改动**（`PasterDream` `client/sky/data/SkyboxDataReloadListener.java`）：更新文档示例中的群系 ID。

### 修复：旁观模式打开容器菜单导致断线

*   **背景/根因**：旁观者通过原版单参 `openMenu` 打开容器时传入的 `FriendlyByteBuf` 为空（`readableBytes == 0`），构造器直接 `readBlockPos()` 抛 `IndexOutOfBoundsException` 造成连接丢失；且原实现仅在 `blockEntity != null` 时添加槽位，空 BE 时客户端/服务端槽位数量不一致。
*   **改动**（`PasterDream` `menu/` 下 16 个 Menu 类，如 `mixins` 无关的 `DreamCauldronMenu.java:52`、`ResearchTableMenu.java:52`、`DreamAccumulatorMenu.java:42`、`BlueprintGui0Menu.java:63`、`TheEndlessBookOfDreamSeekersMenu.java:41`）：读取前统一加 `extraData != null && extraData.readableBytes() >= N` 防御判断。
*   **改动**（同上）：`blockEntity == null` 时回退到按容量新建 `ItemStackHandler`，保证槽位结构始终一致，由 `stillValid` 返回 `false` 让服务端自动关闭。

### 修复：染梦树叶不腐烂

*   **背景/根因**：`DyedreamLeavesBlock` 与 `DyedreamGlowingLeavesBlock` 覆写 `isRandomlyTicking` 返回 `false`，失去原版树叶随机刻判定，隔离后的树叶永不消失。
*   **改动**（`PasterDream` `worldgen` 同级 `block/DyedreamLeavesBlock.java:62`、`block/DyedreamGlowingLeavesBlock.java`）：`isRandomlyTicking` 改为 `true`，恢复腐烂并同步移除过时的类注释。

### 修复：染梦方块标签归属纠正

*   **背景/根因**：`dyedream_block` 被错误加入 `c:stones`（方块/物品）与 `plantable_on` 标签；它属于岩石方块、不可种植，也不符合 `stones` 约定。
*   **改动**（`PasterDream` `registry/blocks/PDBlocksSimple.java:47`）：移除 `dyedream_block` 配置上的 `.plantable()`，DataGen 重新生成 `plantable_on.json`。
*   **改动**（`PasterDream` `src/main/resources/data/c/tags/block/stones.json`、`.../data/c/tags/item/stones.json`）：从两个 `stones` 标签中移除 `pasterdream:dyedream_block`。

### 修复：boboji 增益效果图标纹理丢失

*   **背景/根因**：`boboji_buff` 状态效果引用的图标纹理文件缺失，效果图标显示为紫黑占位。
*   **改动**（`PasterDream`）：补充 `PasterDream/src/main/resources/assets/pasterdream/textures/mob_effect/boboji_buff.png`。

### 修复：染梦树苗无法生长

*   **背景/根因**：`DyedreamSaplingBlock` 构造 `TreeGrower` 时参数顺序写反——把 `TreeRegistry.TREE_SELECTOR` 放在了 `megaTree` 位置、`tree` 位置留空。原版催熟逻辑在绝大多数随机分支走 `tree`（为空即失败），导致树苗基本无法长大。
*   **改动**（`PasterDream` `PasterDream/src/main/java/com/pasterdream/pasterdreammod/block/DyedreamSaplingBlock.java:39`）：将 `TREE_SELECTOR` 移到 `tree` 参数、`megaTree` 置空。
*   **改动**（`PasterDream` `registry/PDBlocks.java`、`registry/blocks/PDBlocksSimple.java:29`）：为 `dyedream_grass`、`dyedream_dirt` 补上 `.plantable()` 配置。
*   **改动**（`PasterDream` `src/generated/resources/data/pasterdream/tags/block/plantable_on.json`、`.../data/c/tags/block/lanterns.json`、`.../minecraft/tags/block/mineable/pickaxe.json`）：`plantable_on` 补入 `dyedream_grass`/`dyedream_dirt`，`dyedream_lantern` 加入镐可挖掘标签，灯笼标签顺序整理。

### 新增：染梦灯笼（dyedream_lantern）悬挂式灯笼方块

*   **新增**（`block/DyedreamLanternBlock.java`）：悬挂式灯笼，参考原版 `LanternBlock` 实现，支持悬挂（hanging）/放置双状态与含水（waterlogged）
    *   玻璃音效、硬度 0.3、15 级光照、自发光、无遮挡、非红石导体（与染梦水晶灯同风格）
*   **注册**（`PasterDream`）：方块 `PDBlocksSimple.DYEDREAM_LANTERN`、物品 `PDItemsBlocks.DYEDREAM_LANTERN`、门面 `PDBlocks`/`PDItems`、配置 `PDBlocks.putConfig("dyedream_lantern", mineable("pickaxe"))`（自动生成 `mineable/pickaxe` 标签）、创造标签 `PDCreativeTabsDyedream`
*   **资源**：blockstates（hanging 双变体）、模型（parent 原版 `template_lantern`/`template_hanging_lantern`）、占位纹理 `textures/block/dyedream_lantern.png`（`tools/gen_dyedream_lantern_tex.py` 生成，待正式美术替换）、战利品表、合成配方（与染梦水晶灯同配方，产物 2 个）
*   **语言**：`zh_cn` 染梦灯笼 / `en_us` Dyedream Lantern
*   **注册 ID 校验**：全工作区确认 `dyedream_lantern` 无重复注册；与原模组拼写错误的 `dyedream_lartern`（染梦水晶灯，已移植）不冲突
*   **验证**：`:PasterDream:compileJava` BUILD SUCCESSFUL

### 修复：禁用「染梦裂隙自然生成」配置不生效（主世界天空仍生成裂隙浮岛）

*   **根因**（`PasterDream`）：
    1. `PDCommonConfig.DYEDREAM_CRACK_GENERATE` 此前仅被配置界面引用，生成注册从未读取该值
    2. `worldgen/structure/struct_dyedream_crack_1.json` 使用原版 `minecraft:jigsaw` 类型，结构生成完全由静态 JSON 驱动，与代码注册无关 → 只要 JSON 存在，主世界 Y=32 裂隙浮岛必生成
    3. `PasterDreamMod` 构造器中配置注册（`registerConfig`）位于结构注册（`PDRuinsRegistration.register()`）之后，此时读取配置会抛 `IllegalStateException` 或读到默认值
*   **修复**：
    *   `PasterDreamMod.java`：配置文件注册提前至构造器最前部，确保后续代码可安全读取配置值
    *   `PDRuinsRegistration.java`：`registerDyedreamCrack()` 增加配置判断（含方法内防御），关闭时跳过 `struct_dyedream_crack_1` 的 StructureType 注册并输出调试日志
    *   `worldgen/structure/struct_dyedream_crack_1.json`：`type` 由 `minecraft:jigsaw` 改为 `pasterdream:struct_dyedream_crack_1`（RuinBuilder 注册的自定义类型）——配置关闭时该类型未注册 → 结构 JSON 解析失败 → structure_set 引用失效 → 不生成；配置开启（默认）时行为不变
    *   `PDConfigScreen.java`：删除 BASIC 分类中重复的「染梦裂隙自然生成」条目（保留 System 分类），条目计数同步修正
    *   `PDStructureVerifyHooks.java`：`verifyRuinApi` 断言随配置联动（关闭裂隙生成时期望数 42→41）
*   **配置**：`PasterDream-Common.toml` → `[System]` → `dyedream crack generate`（默认 `true`；关闭后需新建世界生效）
*   **验证**：`:PasterDream:compileJava` + `:PasterDream:processResources` BUILD SUCCESSFUL，构建输出 JSON 已同步新 type

### 新增：自定义出生维度/群系（默认关闭）

*   **新增**（`world/PDCustomSpawnEvents.java`）：玩家登录（新玩家首次进入世界）时，若配置开启且该玩家尚未执行过自定义出生，自动传送到配置指定的维度与群系位置并设置重生点
    *   以目标维度出生点为中心，用 `ServerLevel.findClosestBiome3d` 搜索指定群系（搜索不到时回退到维度出生点）；用 `Heightmap.MOTION_BLOCKING` 计算安全地表高度
    *   执行完成后在玩家 `PlayerPersisted` 子标签写入标记，同一存档内只生效一次（跨死亡/重登保留，与《帕斯特指南》发放标记同模式）
    *   维度/群系 ID 非法或未注册时跳过并保留原版出生，不影响现有流程
*   **配置**（`PDCommonConfig.java`）：`PasterDream-Common.toml` 新增 `[Custom Spawn]` 段
    *   `custom spawn enabled`（默认 `false`）—— 总开关
    *   `custom spawn dimension`（默认 `minecraft:overworld`）—— 出生维度 ID，如 `pasterdream:dyedream_world`
    *   `custom spawn biome`（默认 `minecraft:plains`）—— 出生群系 ID，如 `pasterdream:dyedream`
    *   `custom spawn search radius`（默认 `10000`，范围 `100~100000`）—— 群系搜索半径（格）
*   **事件注册**（`PasterDreamMod.java`）：`NeoForge.EVENT_BUS` 注册 `PDCustomSpawnEvents::onPlayerLoggedIn`
*   **配置界面**（`PDConfigScreen.java` / `ConfigCategory.java`）：新增「自定义出生 / Custom Spawn」分类，提供总开关与搜索半径条目（维度/群系 ID 请直接编辑 TOML）
*   **语言**：`zh_cn` / `en_us` 新增分类标题与配置项翻译
*   **验证**：`:PasterDream:compileJava` BUILD SUCCESSFUL

### 修复：全库无效配置审计——补齐 4 项移植遗漏配置的功能实现

*   **审计**（`tools/scan_config_usage.py` 新增）：全库引用扫描 6 个配置类（PDCommonConfig/PDClientConfig/PDMeltDreamConfig/PDSanityConfig/PDSpellsConfig）共 100+ 配置项，识别出「仅配置界面引用、无业务读取」的无效配置
*   **补实现**（`PasterDream` 主模块）：
    *   `ban fire necklace`（禁用业火项链）→ `Fire0NecklaceItem.curioTick` 补充 BAN 检查：禁用时提示"此物品已被禁用"；不禁用时脚下空气处点火 + 燃烧时急迫 I（对齐原版 `Fire0NecklacePr0Procedure`）
    *   `loading gui tips`（加载界面 tips）→ 新增 `PDLoadingTipsClientEvents`（ScreenEvent.Render.Post）：连接/加载/进度界面底部绘制随机 tips，22 条文案沿用原版（对齐原版 `ClientEvent`）
    *   `the origin of the world initially generated dyedream crack`（主世界 0,0 原点裂隙）+ `dyedream origin spawnpoint`（染梦出生点岛屿）→ 新增 `PDOriginCrackWorldgen`（LevelEvent.Load + SavedData 防重复，对齐原版 `GenerateWorldPr0Procedure`，高度逻辑 heightmap≤100 → (-9,110,-10) 否则 (-9,160,-10)）
*   **报告**（`docs/invalid-config-audit.md`）：附属模块 15 项「配置预留但功能未实现」保留待实现——San 恢复/下界/末地/雨天/雷暴降值（6 项）、MeltDream 恢复/水晶箱倍率/上限（6 项）、Spells 法术倍率（3 项）
*   **验证**：`:PasterDream:compileJava` + 三个附属模块 BUILD SUCCESSFUL

### 完善：染梦灯笼（dyedream_lantern）正式纹理 + 灯笼标签

*   **纹理**（`PasterDream`）：用原模组正式美术替换占位纹理
    *   复制原模组 `ran_meng_deng_long_.png`（16×48，3 帧 frametime 4 灯光闪烁动画）至 `textures/block/dyedream_lantern.png`，并重命名去除拼音命名（原拼音 `ran_meng_deng_long_` → `dyedream_lantern`，同步复制 `.mcmeta`）
    *   删除占位纹理生成脚本 `tools/gen_dyedream_lantern_tex.py`
*   **模型**：按用户要求**复制原模组模型文件写法**（对齐 `dyedream_lartern`）
    *   `models/block/dyedream_lantern.json` / `dyedream_lantern_hanging.json`：`parent: block/cube` 全方块模型 + `render_type: translucent`，六个面纹理统一引用重命名后的 `pasterdream:block/dyedream_lantern`
    *   **修复**：上一版自定义 box 模型存在纹理变量 `#lantern` 未定义导致纹理不显示的 bug，复制原模组写法后直接引用具体纹理路径，纹理正确应用
*   **模型与碰撞箱同步**（后续修正）：全方块模型与 LanternBlock 小灯笼碰撞箱（约 6×7×6）不匹配，改为 parent 原版 `template_lantern` / `template_hanging_lantern`
    *   放置态 `dyedream_lantern.json`：主体 `[5,0,5]→[11,7,11]` + 顶部环 `[6,7,6]→[10,9,10]`，与 Java `AABB` 完全一致
    *   悬挂态 `dyedream_lantern_hanging.json`：主体 `[5,1,5]→[11,8,11]` + 顶部环 `[6,8,6]→[10,10,10]`，与 Java `HANGING_AABB` 完全一致
    *   `textures.lantern` 引用 `pasterdream:block/dyedream_lantern`，渲染为灯笼形状而非全方块
*   **标签**（灯笼特征）：
    *   `registry/PDBlockTags.java`：新增 `LANTERNS = c:lanterns` 社区约定标签常量（与 Fabric Convention Tags 兼容）
    *   `data/PDBlockTagProvider.java`：`addExtraTags` 写入染梦灯笼、染梦水晶灯及原版 `lantern`/`soul_lantern`
    *   `src/generated/resources/data/c/tags/block/lanterns.json`：手动生成等价标签文件（DataGen 被并行开发的配置加载问题阻塞期间，保证资源就位；待其修复后 runData 会重新生成同样内容）
*   **验证**：`:PasterDream:compileJava` BUILD SUCCESSFUL；全部 JSON/纹理/mcmeta 校验通过
*   **注意**：本轮 `runData` 受并行开发中的 `PasterDreamMod` 配置读取 bug（`Cannot get config value before config is loaded`）阻塞，标签已手动落盘，DataGen 代码同步就绪

### 修复：融梦能量自然恢复 + 水晶箱开箱奖励（2 项功能补齐）

*   **自然恢复**（`PasterDreamMeltDream`）：新增 `PDMeltDreamEvents.java`（`PlayerTickEvent.Post`），实现 `PasterDreamMeltDream-Common.toml` 中 `recover interval`（默认 1200 tick = 60 秒）与 `recover amount`（默认 0.1）两项配置——玩家在线期间按间隔自动恢复融梦能量；系统总开关关闭时跳过；按玩家独立 tickCount 差值计时，登录事件清理残留记录
*   **水晶箱奖励**（`PasterDream` 主模块 `MeltdreamChestBlock.java`）：开箱成功时按原版 `MeltdreamChestPr0Procedure` 行为奖励 +2 融梦能量，并乘 `chest generation multiplier` 倍率（默认 1.0）；系统总开关关闭时不给能量
*   **验证**：`:PasterDream:compileJava` + `:PasterDreamMeltDream:compileJava` BUILD SUCCESSFUL

### 修复：染梦砂岩 / 染梦深岩破坏无掉落物

*   **根因**（`PasterDream`）：`PDBlocksSimple.java` 用 `addCustom` 注册了 `dyedream_sandstone` 与 `dyedream_deepstone`，但 `PDItemsBlocks.java` 未注册对应 BlockItem、`data/pasterdream/loot_table/blocks/` 下缺失战利品表 JSON → `Block.asItem()` 返回 `Items.AIR` 且无战利品表 → 破坏零掉落（验证报告 `pd_verify_report.json` blocks 类目 `extra` 字段早已列出这两项）
*   **修复**：
    *   `registry/items/PDItemsBlocks.java`：新增 `DYEDREAM_DEEPSTONE`、`DYEDREAM_SANDSTONE` 的 `registerSimpleBlockItem` 注册
    *   `registry/PDItems.java`：聚合区补两个 `PDItemsBlocks` 引用
    *   `data/pasterdream/loot_table/blocks/dyedream_sandstone.json`、`dyedream_deepstone.json`：新增自掉落战利品表（对齐兄弟方块 `dyedream_block.json` 格式，无条件 + `survives_explosion`）
*   **验证**：`:PasterDream:compileJava` BUILD SUCCESSFUL；两个新 JSON 解析校验通过；无 lint 错误

### 修复：语言文件缺失翻译键 + 英文文件中的中文字符

*   **补全缺失键**（`PasterDream` 主模块 `lang/zh_cn.json`、`lang/en_us.json`）：新增 `item.pasterdream.dyedream_deepstone`、`item.pasterdream.dyedream_sandstone` 翻译键（染梦深层石 / Dyedream Deepstone、染梦砂岩 / Dyedream Sandstone），消除 `tools/check_lang.py` 报告的 2 处缺失
*   **清理中文字符**（`PasterDream` `lang/en_us.json`）：`painting.pasterdream.pasterdream_draw_0.author` 全角括号 `【pl】Mo` → `[pl] Mo`；`tooltip.pasterdream.calle_card.*` 全角引号 `『』` → 半角单引号（含 `card_drawn`、`card_title` 及 `name.1~9` 共 11 处）
*   **清理中文字符**（`PasterDreamSpells` `lang/en_us.json`）：`itemGroup.pasterdreamspells` 中文竖线 `PasterDream丨Spells` → `PasterDream | Spells`
*   **验证**：`tools/check_lang.py` 全部注册项（431 物品 / 290 方块）在中英文语言文件中均已找到对应翻译键；全项目 `en_us.json` CJK 字符扫描 0 残留

### 修复：合并冲突残留导致的编译失败

*   **根因**：合并 `momonyako` 分支（v0.9.6）时，4 处冲突标记未真正解决即被提交，导致 `:PasterDream:compileJava` 语法错误（`<<<<<<< HEAD` / `=======` / `>>>>>>>` 残留）
*   **修复**（`PasterDream`）：
    *   `PasterDreamMod.java`：裂隙出生点监听器注册统一为 v0.9.6 方案 `PDOverworldOriginCrackWorldgen::onLevelLoad`
    *   `registry/PDRuinsRegistration.java`：`registerDyedreamCrack` 注释冲突合并（保留 HEAD 详细说明：StructureType 无条件注册 + 生成阶段按配置判断）
    *   `worldgen/structure/DyedreamCrackStructure.java`：类注释/方法注释取详细版，配置判断用防御性写法 `Boolean.TRUE.equals(...)`（与 `DyedreamCrackPlacement` 一致）
    *   `world/PDOriginCrackWorldgen.java`：删除——与 v0.9.6 引入的 `PDOverworldOriginCrackWorldgen` 功能完全重复（合并遗留的旧实现）
*   **修复**（`Changelog.md`）：v0.9.6 条目上移至顶部，清除残留冲突标记块
*   **验证**：`:PasterDream` / `:PasterDreamAPI` / `:PasterDreamMeltDream` / `:PasterDreamSanity` / `:PasterDreamSpells` 五模块 `compileJava` 全 BUILD SUCCESSFUL

### 修复：草莓甜心（strawberry_heart）无法使用

*   **根因**（`PasterDream` `item/StrawberryHeartItem.java`）：移植为普通 `Item`，未覆写任何 `use` / `getUseAnimation` / `getUseDuration` / `releaseUsing` 方法——右键无任何行为（不蓄力、不发射、不演奏），等同于"无法使用"的装饰物
*   **修复**：参照 `ShadowVortexBookItem` 的"松开蓄力施法"范式重写 `StrawberryHeartItem.java`，还原原版 `StrawberryHeartItem` + `StrawberryHeartPr0Procedure` 行为：
    *   右键蓄力（`UseAnim.BOW`，时长 72000），松手 `releaseUsing` 发射 `StrawberryHeartProjectileEntity`（动能 2、伤害 1、吉他发射音），弹药 = 魔法石（先双手后主背包），法杖自体 `hurtAndBreak(1)`，创造免弹药
    *   施法冷却（`applyStrawberryHeartProcedure`）：佩戴俏皮鬼头饰 `qym_head` → 本法杖冷却 0；否则全部 `pasterdream:magic` 标签物品冷却 `12 * MAGICCD` tick（默认 1 → 12 tick = 0.6s，与 tooltip 一致）
    *   潜行演奏：消耗 0.25 融梦能量（`PDAttachments.consumePlayerMeltDreamEnergy`）→ 播放 4 段吉他琶音（立即 + `ServerScheduler.schedule` 延迟 4/7/10 tick，音量 1.2/1.2/1.2/1.4、音调 0.8/1.0/1.2/1.7）→ 8 格半径内玩家获得瞬间治疗（4 点）、生命恢复 5s、力量 10s、速度 10s → 全部法术物品长冷却 `100 * MAGICCD`；能量不足显示 `message.pasterdream.strawberry_heart.no_energy`
*   **新增**（`PasterDream` `lang/zh_cn.json`、`lang/en_us.json`）：`message.pasterdream.strawberry_heart.no_energy` 翻译键（融梦能量不足 / Not enough Meltdream Energy!）
*   **验证**：`:PasterDream:compileJava` BUILD SUCCESSFUL；两个语言文件 JSON 解析合法且含新键

### 新增：首次登录是否赠送《帕斯特指南》配置选项

*   **新增**（`PasterDream` `config/PDCommonConfig.java`）：`GIVE_GUIDE_BOOK` 布尔配置项（`Basic` 段，键 `give guide book`，默认 `true`）——关闭后首次登录不再赠送《帕斯特指南》Patchouli 图鉴
*   **接入**（`PasterDream` `attachment/PlayerDataEvents.java:107`）：`giveGuideBookIfNeeded` 入口处判配置关闭则直接返回
*   **接入**（`PasterDream` `client/gui/config/PDConfigScreen.java:230`）：`Basic` 分类注册 `GIVE_GUIDE_BOOK` 布尔条目
*   **新增**（`PasterDream` `lang/zh_cn.json`、`lang/en_us.json`）：`gui.pasterdream.config.give_guide_book` + `.tooltip` 翻译键
*   **验证**：`:PasterDream:compileJava` BUILD SUCCESSFUL；两个语言文件 JSON 解析合法且含新键

### 修复：配置日志刷屏死循环（PasterDream-Common.toml 无限纠正）

*   **根因**（`PasterDream` `config/PDCommonConfig.java:186~194`）：`Meltdream Chest` 三个品质物品池（common/rare/legendary loot）用 `List.of(...)` 作默认值调用二参 `Builder.define(String, T)`。该重载生成的默认 validator 为 `default.getClass().isAssignableFrom(value.getClass())`——`List.of` 返回不可变 `ImmutableCollections$ListN`，而 nightconfig 从 TOML 读回的是 `ArrayList`，运行时类不匹配 → 配置**每次重载都被判 "Incorrect key ... was corrected to its default"**（日志 `from` 与 `to` 内容相同却仍判错误，正是类不同值相同）→ 纠正写盘 → NeoForge `FileWatcher` 检测文件变化 → 重载 → 再纠正 → **无限循环刷屏**（用户日志 5000~20000+ 行均为该 WARN）
*   **修复**：三个物品池改为三参 `define(String, T, Predicate)`，显式传入 `o -> o instanceof List` validator——任意 List 实现（含 `ArrayList`）均通过校验，不再触发纠正，循环消除
*   **验证**：`:PasterDream:compileJava` BUILD SUCCESSFUL；`lambda$define$0` 字节码确认默认 validator 为类兼容性检查，`instanceof List` 可覆盖所有 List 运行时类

### 修复：暮影长床再入死锁——进入灯影世界时代码补授「灯影之下」进度

*   **现象**：首次经真·影之床（暮影之笼据点守卫完成后）进入灯影世界并返回主世界后，右键自制的暮影长床（shadow_bed）提示「缺少灯影之下的进度」，无法再次进入
*   **根因**：`achievement_shadow_start`（灯影之下）在数据包中仅有 `changed_dimension`（to=lamp_shadow_world）触发器一条授予路径，实机未稳定生效（0.9.4 真·影之床误用该进度导致首入死锁已是同一根因的先例）；暮影长床再入唯一前置即该进度 → 首入后永远拿不到 → 再入死锁。VERIFY 套件只覆盖真床首入（hide_9 门控），未覆盖暮影长床再入，缺口未被发现
*   **修复**（`ShadowBedBlock#teleportToLampShadowWorld`）：跨维度传送成功后由代码补授 `PDAdvancements.award(SHADOW_START)`，语义与原版一致（en_us 描述即 "Enter the dream 'Beneath Lamp & Shadow'"）；`award` 幂等（已完成/holder 缺失无副作用），与数据包触发器双路径兼容；真床、暮影长床、入睡事件（`PDSleepEvents`）三条首入路径共用该方法，全部覆盖；研究台（`ResearchTableBlockEntity` 对同进度的检查）一并受益
*   **验证**：`gradlew :PasterDream:compileJava` BUILD SUCCESSFUL；实机待验：首入灯影 → 返回 → 夜晚右键自制暮影长床应可再次进入

### 修复：暗影地牢五层生成穿基岩——维度底钳制+整体上移

*   **现象**：玩家按流程经竖井底部已修复传送门进入暗影地牢时，下半部分层级（第 4/5 层与层间门）落入基岩层甚至维度边界之下而"消失"，地牢底部呈现虚空
*   **根因**：自然竖井（jigsaw，25×91×25）内嵌传送门位于模板 y=14（世界 y ≈ 地表−63，标准地形下 y≈1），而 `generateDungeon` 以传送门为基准写死下铺偏移（五层 y−28~−64、层间门 y−29~−67）——基准太低导致最深层（y−67≈−66）低于维度安全线；原模组 1.20.1 同参数同维度（min_y=−64），属原模组遗留缺陷
*   **修复**（`BrokenShadowDungeonProtalBlock`）：新增 `calcDungeonShift` 计算钳制上移量（保证最低门位于 min_y+6 之上，避开基岩层）；五层模板、6 扇层间门、清场区域中心与进入传送目标（y−19）统一 +shift；出口传送（y+66）向上恒安全，维持不动
*   **验证**：`gradlew :PasterDream:compileJava` BUILD SUCCESSFUL；标准地形（传送门 y≈1）shift=8，五层落于 y−58~−12，玩法与层间距不变

### 修复：暮影长床再入死锁——进入灯影世界时代码补授「灯影之下」进度

*   **现象**：首次经真·影之床（暮影之笼据点守卫完成后）进入灯影世界并返回主世界后，右键自制的暮影长床（shadow_bed）提示「缺少灯影之下的进度」，无法再次进入
*   **根因**：`achievement_shadow_start`（灯影之下）在数据包中仅有 `changed_dimension`（to=lamp_shadow_world）触发器一条授予路径，实机未稳定生效（0.9.4 真·影之床误用该进度导致首入死锁已是同一根因的先例）；暮影长床再入唯一前置即该进度 → 首入后永远拿不到 → 再入死锁。VERIFY 套件只覆盖真床首入（hide_9 门控），未覆盖暮影长床再入，缺口未被发现
*   **修复**（`ShadowBedBlock#teleportToLampShadowWorld`）：跨维度传送成功后由代码补授 `PDAdvancements.award(SHADOW_START)`，语义与原版一致（en_us 描述即 "Enter the dream 'Beneath Lamp & Shadow'"）；`award` 幂等（已完成/holder 缺失无副作用），与数据包触发器双路径兼容；真床、暮影长床、入睡事件（`PDSleepEvents`）三条首入路径共用该方法，全部覆盖；研究台（`ResearchTableBlockEntity` 对同进度的检查）一并受益
*   **验证**：`gradlew :PasterDream:compileJava` BUILD SUCCESSFUL；实机验证通过（2026-10-18 用户确认）：首入灯影 → 返回 → 夜晚右键自制暮影长床可再次进入

### 新增：梦境炼药锅 KubeJS RecipeSchema 联动（三系统联动集齐）

*   **背景**：炼药锅已完成 `dream_brewing` 数据驱动化（见下条改造），本次补齐其 KubeJS RecipeSchema 联动，与暗影高炉/精铸工坊统一为 `event.recipes.pasterdream.<type>` 写法
*   **新增**（`PDRecipeSchemas`）：`DREAM_BREWING` schema——位置参数（产物, 3 材料数组，`IntBounds(3,3)` 防呆），可选链式参数 `guidingDrug(...)` / `craftTick(...)`；`PasterDreamKubeJSPlugin` 注册到 `pasterdream:dream_brewing`
*   **细节**：`guiding_drug` 的 KubeJS key 用 `defaultOptional()`（非 `optional(Ingredient)`）——schema 类在 KubeJS 注册期初始化，不可触碰 `PDItems.GUIDING_DRUG` DeferredHolder；脚本未设置时不写该字段，由 MapCodec 的 `optionalFieldOf` 默认值（药引瓶）兜底，两侧解耦
*   **测试产物**：`pd_recipe_kubejs_test.js` 追加 2 条炼药锅测试配方（苹果/金锭/荧石粉→金苹果，缺省引导药剂；面包/小麦/糖→蛋糕，指定 `guidingDrug('minecraft:milk_bucket')`）；VERIFY 套件 `kubejs-recipes` 扩展：`dream_brewing` 数据包配方 ≥5、KubeJS 注入 ≥2、`DreamBrewingInput`（药引瓶+测试材料）`getRecipeFor` 实际匹配命中金苹果
*   **验证**：`gradlew compileJava` 全模块 BUILD SUCCESSFUL（修复 `ItemStack` 双重包装一处）；改动 4 文件行尾 LF 全过；实机待验：`kubejs-recipes` 套件全绿、JEI 炼药锅分类显示脚本配方

### 新增：暗影高炉 + 精铸工坊 KubeJS 配方联动；工坊配方 JSON 数据驱动化（经用户确认）

*   **背景**：暗影高炉（`shadow_blasting`）与精铸工坊（`weapon_forge`）配方无法被 KubeJS 增删改；工坊 13 条锻造配方还硬编码在 BlockEntity 内（与 JEI 展示层构成双份硬编码副本）。经确认：两系统均走 KubeJS RecipeSchema 模式，工坊先行数据驱动重构，梦境炼药锅本次不动
*   **工坊配方数据驱动化**（参照暗影高炉模式）：新增 `WeaponWorkshopRecipe implements Recipe<WeaponWorkshopRecipeInput>`（字段：`ingredients`×5 / `result` / `inlay`（JEI 镶嵌槽展示标记）/ `final_result`（JEI 成品预览））+ `WeaponWorkshopRecipeInput`（材料槽 0-4 只读视图）+ `pasterdream:weapon_forge` RecipeType/Serializer（MapCodec + StreamCodec，ingredients 固定 5 格校验，长度不符报错而非静默失效）；13 条原配方迁移为 JSON（`data/pasterdream/recipe/weapon_forge/`，物品与产物逐一保真）；`WeaponWorkshopBlockEntity` 删除硬编码配方表改走 `RecipeManager.getRecipeFor`，附魔合并（同名取最高）与镶嵌结算保留为锻造通用行为
*   **KubeJS RecipeSchema 联动**：`PasterDreamKubeJSPlugin#registerRecipeSchemas` 注册两套 schema（`PDRecipeSchemas`，仅被插件类引用，KubeJS 缺席不加载）；玩家脚本写法——`event.recipes.pasterdream.shadow_blasting('产物', '原料').byResult(...).blastingTick(50).spendFluidFuel(100).spendFuel(0)`、`event.recipes.pasterdream.weapon_forge('产物', [5 材料]).inlay(true).finalResult(...)`；schema 生成的 JSON 与 MapCodec 字段一一对应，经合成数据包进 vanilla RecipeManager 加载链（数据包覆盖/JEI/配方查询自然生效）
*   **JEI 展示层同步**：`WeaponWorkshopJeiRecipe` 删除 13 条硬编码展示数据，改从运行时 RecipeManager 动态读取（同暗影高炉模式：客户端世界未就绪注册空列表、配方重载自动补全），数据包增删改配方后 JEI 自动跟随；行为差异（有意）：原展示层材料数量角标（×2/×3 等）与实际「每槽各扣 1」不符，现按实际消耗展示
*   **测试产物**：`run/kubejs/server_scripts/pd_recipe_kubejs_test.js`（高炉 ×2 + 工坊 ×2 测试配方，VERIFY 全绿后可删）；新增 VERIFY 专项套件 `kubejs-recipes`（`PDRecipeKubeJsVerifyHooks`：RecipeType 注册 / 数据包配方装载 ≥13+≥5 / kubejs 命名空间注入 ≥2+≥2 / 脚本配方 getRecipeFor 实际匹配；不在 all 默认集合，`PASTERDREAM_VERIFY_SUITES=kubejs-recipes` 显式开启）
*   **验证**：`gradlew compileJava` 全模块 BUILD SUCCESSFUL（修复 3 处：`SingleRecipeInput` 泛型绑定、`ModList.get().isLoaded` 静态调用、`getAllRecipesFor` 泛型方法签名）；本次 25 个改动/新建文件行尾 LF 全过；smoketest 工坊锻造 E2E（配方匹配/附魔合并断言）走新 RecipeManager 链路预期原样通过；实机待验：KubeJS 脚本注册配方生效、JEI 工坊分类动态显示、`kubejs-recipes` 套件全绿

### 改造：梦境炼药锅 → 类酿造台自动酿造——双通道配方 + 梦之强化 + 配方数据包化（经用户确认）

*   **背景**：原坩埚为「引导药剂+3材料→法术卷轴」按钮触发炼制，5 个法术配方硬编码于方块实体，无法数据包覆盖、KubeJS 无法增删改，且不产出药水
*   **槽位重排（酿造台式）**：槽 0 引导药剂槽 → 通用材料槽（药水酿造材料/法术引导药剂双用）；槽 1-3 材料槽 → 药水瓶槽（水瓶/任意药水，兼作法术配方材料位）；槽 4-6（融梦桶/空桶回收/成品）不变；GUI 坐标与纹理不动
*   **双通道自动酿造**（移除原「炼制」按钮，材料放入即由服务端 tick 自动启动）：法术配方精确匹配优先（时长由配方 `craft_tick` 决定，默认 60 tick，时序与原版 procedure 语义一致：末前 2 tick 消耗/末前 1 tick 产物入槽 6）；否则走通用药水酿造（400 tick 与原版一致，`Level#potionBrewing()` 判定与执行——NeoForge patched 实例自动涵盖原版全部配方与所有模组注册的 `IBrewingRecipe`，无需逐一适配）；酿造中材料被拿走/替换即中断（原版酿造台行为）；GUI 新增酿造进度条（DataSlot 同步，原按钮位置）
*   **梦之强化**（仅药水酿造产物）：新增 `PDDataComponents.DREAM_ENHANCED` 标记组件 + `DreamPotionEnhancer`——持续型效果时长 ×1.5、即时型效果等级 +1（无限时长保持原样）；实现为清空 potion 字段后强化效果全量写入 customEffects（避免原版效果+强化效果双重叠加），药水原名经 `ITEM_NAME` 组件以翻译键保留（无斜体）；tooltip 追加「梦之强化」提示行（双语，LIGHT_PURPLE）
*   **配方数据包化（技术债清理，经用户确认）**：新增 `pasterdream:dream_brewing` RecipeType + MapCodec/StreamCodec 序列化器（同 shadow_blasting 模式，字段：`guiding_drug`/`materials`×3/`result`/`craft_tick`）+ 5 个配方 JSON（`data/pasterdream/recipe/dream_brewing/`，内容与原硬编码一致）；BlockEntity 改走 `RecipeManager.getAllRecipesFor` 查询（旧存档酿造中恢复可自愈重匹配，配方按 ID 比对）；JEI 改从运行时 RecipeManager 读配方（数据包增删改自动跟随，同暗影高炉模式）；KubeJS 可按配方 ID 增删改（如 `server.recipes.remove({ id: 'pasterdream:dream_brewing/lightning_spell' })`）
*   **行为差异（有意）**：法术产物不再弹出地面改为写入成品槽 6 由玩家取走（自动酿造下 GUI 保持打开，成品槽占用时法术酿造不启动）；PasterDreamSpells 未安装时引用其物品的配方 JSON 解析失败跳过（伴随 error 日志，属数据包生态预期行为，功能正确）
*   **验证**：`gradlew compileJava` BUILD SUCCESSFUL（修复 `DreamBrewingRecipe` 的 `getResultItem` 方法引用歧义一处）；`check_lang.py` 双语全过；18 个改动/新建文件行尾 LF 全过；`BUTTON_CRAFT`/`tryStartCraft`/`activeRecipeIndex` 残留清零；smoketest 坩埚链路同步适配（自动酿造断言 + 产物写入槽 6 断言，移除按钮点击与掉落物断言）；实机待验：法术/药水双通道酿造、模组配方兼容（Iron's Spells 等注册配方）、梦之强化数值、进度条渲染

### 修复：玄岩药水酿造配方移植缺失——JEI 无酿造步骤、药水无法产出（用户实机反馈）

*   **现象**：JEI 中点玄岩药水看不到酿造步骤；原版酿造台与梦境炼药锅均无法产出该药水（用户实机反馈「药水配方 JEI 看不到」，经现象定位 + 配方清单比对确认）
*   **根因**：原模组酿造配方 `Brewing0BrewingRecipe`（粗制药水 + 玄武岩蜗牛壳 → 玄岩药水，Forge 1.20.1 `BrewingRecipeRegistry` 注册）在 1.20.1 → 1.21.1 移植时整体缺失——移植版从未向 NeoForge 注册过任何药水酿造配方；蜗牛壳物品与玄岩药水本身均已移植，唯独配方断链
*   **新增**（`recipe/BasaltBrewingRecipe` + `registry/PDBrewingRecipes`）：还原原配方语义（`isInput`=任意药水容器且内容为粗制，`isIngredient`=玄武岩蜗牛壳，`getOutput`=保持容器类型替换药水内容为玄岩）；注册方式为 NeoForge 1.21.1 事件 `RegisterBrewingRecipesEvent`（**注意：本版本已移除 BREWING_RECIPES 注册表**，改为 `PotionBrewing#bootstrap` 时游戏总线广播事件、模组经 `event.getBuilder().addRecipe(IBrewingRecipe)` 收集式注册；`@EventBusSubscriber` 注解的 `bus` 参数已过时，事件按类型自动路由）
*   **生效范围**：原版酿造台可酿造 + JEI 酿造分类自动展示步骤（`BrewingRecipeMaker` 数据源含 `IBrewingRecipe` 集合）+ 梦境炼药锅通用酿造通道自动兼容（`potionBrewing()` 三件套委托链路）
*   **验证**：`gradlew compileJava` BUILD SUCCESSFUL（零警告）；行尾 LF 复查通过；实机待验：粗制药水+蜗牛壳在酿造台/坩埚中产出玄岩药水、JEI 显示酿造步骤（普通/喷溅/滞留三容器变体）

### 改进：坩埚 JEI 分类展示兼容的药水酿造配方——R 页自动关联坩埚（用户实机反馈）

*   **现象**：JEI「梦境炼药锅」分类里看不到兼容的原版药水配方，对原版药水按 R 也只显示原版酿造台分类（功能上坩埚通用酿造已兼容，但展示层缺位）
*   **根因**：`DreamCauldronJeiRecipe` 仅展示 5 个法术配方，未把通用酿造通道兼容的配方（原版 mixes + 模组 IBrewingRecipe）转换为坩埚展示配方；JEI 配方页按「配方的 output」自动关联分类，药水未被注册为坩埚分类配方的产物，自然只关联酿造台
*   **改进**（`DreamCauldronJeiRecipe` + `DreamCauldronJeiCategory`）：`build()` 扩展双数据源——法术配方（dream_brewing 数据包，原有）之外，从 `level.potionBrewing()` 枚举：① 原版药水混合（材料候选经 `isIngredient`/`isContainerIngredient` 过滤后 × potion registry 全量药水实例笛卡尔积，`hasMix`/`mix` 试探出全部 (材料, 药水→成品) 组合）；② NeoForge `IBrewingRecipe`（`BrewingRecipe` 具体类走 `getInput/getIngredient/getOutput` 精确展示，其余实现药水域试探、每配方取首条匹配防重复爆炸）；转换展示为坩埚配方（材料入槽 0 位、药水容器入瓶槽位、融梦桶催化剂），药水酿造条目标注 `dreamEnhanced` 并在成品槽 tooltip 提示「坩埚酿造将获得梦之强化」（双语）；空栈槽位（药水酿造无空桶返还/仅 1 瓶）不再渲染
*   **验证**：`gradlew compileJava` BUILD SUCCESSFUL；`check_lang.py` 双语全过；行尾 LF 复查通过；实机待验：坩埚分类出现原版/模组药水酿造条目（约 80+ 条）、药水 R 页同时显示酿造台与坩埚两个分类、强化 tooltip 生效

### 修复：染梦粉尘与小撮染梦粉尘配方未对齐——比例定为 1:4（有意差异，经用户确认）

*   **现象**：1 染梦粉尘可分解为 9 小撮染梦粉尘，但 4 小撮（2×2）即可合成 1 染梦粉尘——4→1→9 循环每轮白赚 5 个小撮，配方永动机
*   **根因**：原模组仅 1:9 一对配方（crafting_113 分解 1→9 / crafting_94 合成 9→1）；移植时多写出「4小撮→1粉尘」（`dyedream_dust_1.json`），与原模组同款「9小撮→1粉尘」（`dyedream_dust_2.json`）并存，与分解 1→9 三者互不咬合
*   **修复**（经用户确认定为 1:4，有意差异：原模组为 1:9，粉尘获取提速 2.25 倍）：分解 `dyedream_dust_piece_1.json` 改为 1 粉尘→4 小撮（category `food`→`misc` 贴原模组口径）；删除多余的 `dyedream_dust_2.json`（9小撮→1粉尘），保留 `dyedream_dust_1.json`（4小撮→1粉尘）为唯一合成路径；`tag_audit.json` 同步移除该条目
*   **波折**：首测仍见 1→9——`bin/main`（Eclipse JDT/VSCode 输出目录）资源未随 src 刷新，手动同步解决；二测仍见 9→1——`dyedream_dust_1.json` 中途被并行协作者改回 shapeless 9→1（等效复活已删的 `dust_2`），改回 shaped 2×2（4小撮）并同步 src/build/bin 三份副本
*   **验证**：改动 JSON 可解析、行尾 LF 复查通过；src/bin/build 三份副本逐项一致（dust_1=2×2 四小撮、piece_1=1→4、dust_2 不存在）；全库无 `dyedream_dust_2` 引用残留；实机待验：配方书 1↔4 互转正常、9 小撮不再可合成粉尘

### 新增：调试水晶 JEI 收拢——130+ 个 debug_wand_* 收拢为虚拟条目「调试水晶」（经用户确认）

*   **现象**：130+ 个调试水晶（`debug_wand_*`）逐个挤占玩家 JEI 物品列表；且若借用任一真实水晶作代表，名称只能是该水晶本名（如「染梦列车」），无法就叫「调试水晶」
*   **根因**：JEI 19.39 物品列表完全来自创造模式标签页（`ItemStackListFactory` 遍历 `CreativeModeTabs.allTabs()`），调试创造栏收录了全部调试水晶；新版 JEI 已移除注册期隐藏 API（`IIngredientVisibility#hideIngredient` 不复存在），且不支持为单个 ItemStack 覆盖显示名
*   **新增**（`thirdparty/jei/debugcrystal/`）：自定义 JEI 原料类型 `DebugCrystalJeiIngredient`（虚拟条目，渲染沿用代表物品染梦列车贴图，codec 走注册名编码支持书签）+ `DebugCrystalJeiHelper`（搜索名「调试水晶」+ 固定 uid `pasterdream:debug_crystal`）+ `DebugCrystalJeiRenderer`（tooltip：名称 + 灰色获取提示行）；`PDJeiPlugin#registerIngredients` 以 5 参重载注册该类型
*   **新增**（`PDJeiPlugin#onRuntimeAvailable`）：按注册名前缀 `debug_wand_` 扫描物品注册表，家族全部成员经 `removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, ...)` 移出 JEI 列表；JEI 每次启动/资源重载重建运行时后重新回调，收拢自动持续；前缀扫描自动覆盖后续新增的水晶，无需硬编码清单
*   **语言**：`jei.pasterdream.debug_crystal.name/desc` 双语（名称「调试水晶」+ 获取提示）
*   **边界**：仅影响 JEI 展示，创造模式标签页完全不受影响（开发团队照常取用）；调试栏其他开发用物品（机关/BOSS 调试物品等）不在收拢范围（经用户确认）；虚拟条目非真实物品，创造模式（cheat）下 JEI 不提供发放，tooltip 注明获取途径；玩家 JEI 开「显示隐藏物品」亦无法绕过（移除作用于运行时物品注册层面）
*   **验证**：`gradlew compileJava` BUILD SUCCESSFUL；行尾 LF 复查通过；实机待验：JEI 搜索「调试水晶」仅见一条目且名称正确、创造栏调试页 130+ 个水晶齐全

### 重构：玩偶 API 上收至 PasterDreamAPI + KubeJS 玩偶掉落兜底

*   **上收**（`api.doll` 历史特例，见 API 架构详解 4.7）：`DollAPI`/`DollBuilder`/`DollResult`/`DollConfig`/`DollModelType` 5 类自主模组迁入 `PasterDreamAPI/.../api/doll/`（同包名，全限定名不变，消费方 import 零改动），并新增 `DollImplementations` record（6 类）
*   **解耦方案**（Provider 注入）：实现类 `DollBlock`/`DollBlockEntity`/`DollDisplayItem` 留主模组；`PasterDreamMod` 新增 `public static ensureDollInstalled()`（构造方法首行 + `DollJSBuilder` 构造器首行双挂载，幂等守卫），经 `DollAPI.install(DollImplementations)` 注入三个实现工厂与注册命名空间 modId；API 层零实现依赖，`DollResult` 改通配泛型持有（`DeferredBlock<? extends Block>` 等）
*   **关键约束**：DeferredRegister 由 `install` 按注入 modId（`"pasterdream"`）延迟创建——不得使用 `PasterDreamAPI.MOD_ID`（`"pasterdreamapi"`），否则玩偶资源 ID 命名空间全部错位；第三方模组直连 `DollAPI` 须声明对 PasterDream 的运行时依赖
*   **掉落兜底**（KubeJS 联动补全）：非主模组命名空间的玩偶注册时自动生成 `kubejs/data/<ns>/loot_table/blocks/<name>.json`（标准 1.21.1 自掉落 + `survives_explosion`，与内置玩偶战利品表同构）；文件已存在则跳过（保留脚本作者自定义掉落）；`docs/教程/KubeJS玩偶.md` 5.4 节同步改写为"自动兜底，可自定义覆盖"
*   **连带修正**（泛型通配化引发，均已编译验证）：`RendererRegistry` 新增 `dollBlockEntityType()` 收敛辅助（运行时不变量：DollAPI 玩偶 BE 均为 `DollBlockEntity`）；`DollBlock#newBlockEntity` 局部类型改通配；`BlockEntityAPI` 工厂类型取非通配 `BlockEntitySupplier<BlockEntity>`（方法引用协变兼容，避免 capture 逃逸）
*   **文档同步**：`API架构详解.md`（4.7 改写为已上收 + 对照表）、`主模组架构详解.md`（目录树/分区表/注册流程/子包表 4 处）、`KubeJS玩偶.md`（5.4 + 源码索引路径）、`融梦水晶箱战利品.md`（关联实现路径）、`架构演变.md`（3.1 标记完成）
*   **验证**：`gradlew compileJava` 全 5 模块 BUILD SUCCESSFUL（附属三模块零改动编译通过，证实无交叉引用）；`gradlew runData` BUILD SUCCESSFUL 且玩偶相关生成文件零 diff；审核文件与新增/修改文本均 LF、UTF-8 无 BOM；实机已验证（2026-10-25，用户确认全部通过）：KubeJS 启动脚本注册玩偶挖掘掉落本体、手写战利品表不被覆盖、4 个内置动态玩偶渲染/抱物/掉落正常、融梦水晶箱玩偶掉落池正常、启动脚本注册时序无回归
*   **范围外**：玩偶 WIKI 由其他成员负责；旧手写玩偶注册方式、主模组命名空间战利品表 DataGen 迁移均不变

### 修复：钛矿/深层钛矿/熔融金矿缺失 biome_modifier 导致不生成

*   **现象**：钛矿、深层钛矿、熔融金矿三者的 configured_feature / placed_feature JSON 均已移植，但缺少 `neoforge/biome_modifier` 注入文件，矿石在世界中完全不生成（对照原模组 `forge/biome_modifier/*_biome_modifier.json` 确认）
*   **改动**（`data/pasterdream/neoforge/biome_modifier/` 新增 3 个文件，`neoforge:add_features`，`underground_ores` 阶段）：
    *   `titanium_ore.json`：注入 `#pasterdream:is_dyedream` 全部染梦群系（原模组仅 biome_dyedream_0~3，随既有 dyedream_ores.json 的有意差异扩展；经用户确认），placed 参数不变（count 6，高度 -60~20，替换 calcite）
    *   `deepslate_titanium_ore.json`：注入 `#minecraft:is_overworld`，count 3，高度 -60~-10，替换 deepslate
    *   `moltengold_ore.json`：注入 `#minecraft:is_nether`，count 3，高度 5~60，替换 netherrack
*   **验证**：JSON 解析 + LF 行尾 + UTF-8 无 BOM 校验通过；实机待验：染梦维度挖钛矿、主世界深板岩层挖深层钛矿、下界挖熔融金矿
*   **备注**：`PDWorldgenProvider` TODO 中"剩余矿石迁移到 DataGen"为后续事项，本次按既有手写 JSON 风格最小修复

### 调整：主世界染梦裂隙自然生成概率减半

*   **需求**：主世界染梦裂隙（struct_dyedream_crack_1）生成密度偏高，降低约 50%
*   **改动**（`data/pasterdream/worldgen/structure_set/struct_dyedream_crack_1_set.json`）：placement 新增 `"frequency": 0.5`——原版标准字段（`StructurePlacement.placementCodec` 可选字段，经 `DyedreamCrackPlacement` CODEC 解析无碍），DEFAULT 频率削减方式按 salt 哈希对每个候选点做 50% 概率判定；spacing/separation/salt 不变，候选点网格不动、分布平滑变稀
*   **行为**：新世界裂隙密度约减半；已生成区块不受影响；`/locate` 仍可定位但更稀疏；配置 `DYEDREAM_CRACK_GENERATE` 关闭时先被放置策略拦截，frequency 无机会参与，行为不变；染梦维度裂隙（struct_dyedream_crack_0）不受影响
*   **其他**：该 JSON 原为 CRLF，随本次改动整文件转 LF（UTF-8 无 BOM）
*   **验证**：JSON 解析 + 字段值域校验通过；`gradlew :PasterDream:processResources` BUILD SUCCESSFUL；行尾 LF 复查通过；实机待验：新世界探索/`/locate structure pasterdream:struct_dyedream_crack_1` 确认裂隙仍可生成但明显变稀
