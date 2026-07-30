# 🌙 新帕斯特之梦 Realm of Dyed Dreams · 染梦之境 🌙

*闭上双眼，深吸一口气。*

*当你再次睁开时，世界已然不同。*

![](https://img.shields.io/badge/🔮_梦境冒险-9B59B6?style=flat-square) ![](https://img.shields.io/badge/🎨_视觉杰作-4ECDC4?style=flat-square) ![](https://img.shields.io/badge/🏗️_从零构建-FF6B6B?style=flat-square)

***

## 📖 这是什么？

**新帕斯特之梦（NeoPasterDream）** 是经典模组 **PasterDream** 在 Minecraft **1.21.1 NeoForge** 上的**精神续作**。

原版模组（1.20.1 Forge）作者精美的纹理、模型和视觉设计——我们全部保留。但**所有代码从零重写**，完全不使用 MCreator 生成的代码。

> 🐦‍⬛ **授权问题**：我已获得原作者授权，这里的所有活动均接受原作者监督

***

## 🔥 这不仅仅是移植。这是**重生**。

原版模组使用 MCreator 构建，这在代码结构、性能和扩展性上都有所限制。新帕斯特之梦使用 **NeoForge 原生 API + DeferredRegister + GeckoLib** 重新实现了一切，为模组带来了更好的性能和更大的扩展空间。

**原版主路径注册与自动化行为抽测已闭环**（CORE 近次 140/0，含饰品 curios×53；全量行为 250+）。它仍会成长——但首先，你可以完整游玩原作者设计的染梦/影灯/风旅主线。

***

## 📊 进度总览

| 类别 | 进度 | 状态 |
| :--- | :--- | :--- |
| **方块与物品（注册）** | ██████████**100%** | ✅ 终验通过（334 方块 / 690 物品期望全在场） |
| **实体与生物** | ██████████**100%** | ✅ 51/51 |
| **状态效果 / 药水 / 附魔** | ██████████**100%** | ✅ 46+4+2 |
| **世界生成与四维度** | ██████████**100%** | ✅ 四维 + 114 结构/集/池；structure 行为终验 18 项 |
| **配方与成就** | ██████████**100%** | ✅ 444 配方 · 62/62 成就 |
| **GUI / 菜单** | ██████████**100%** | ✅ 19/19（工坊·高炉·研究台·笔记·蓝图·储物袋等） |
| **自动化验证** | ██████████**100%** | ✅ VERIFY 行为时间线（structures×20 + workshop×28 + struct-dim×15 + gallery×3 + entity-gallery×5 + **curios×53** + stale-comments 等，合计 250+） |
| **新内容设计** | ░░░░░░░░░░**\~5%** | ⏳ 原版对齐完成后的扩展位 |

详见 [`docs/功能状态.md`](docs/功能状态.md) · 索引 [`docs/README.md`](docs/README.md)。

***

## ✅ 已完成

### 🧱 方块与物品（注册 100%）

| 类型 | 内容 |
| :--- | :--- |
| **基础 / 维度方块** | 染梦系列 · 影石/暗影木全套 · 风泊木全套 · 苍青岩 · 云与风旅地表 |
| **功能性工作站** | 梦之坩埚 · 蓄梦池 · 染梦书桌 · 研究台 · 暗影高炉 · 武器工坊群（铁砧/锻炉/冷却盆/磨石）· 强征传送塔 |
| **容器与家具** | 融梦箱 · 影之箱 · 储物袋×2 · 野餐篮 · 风泊木箱 · 影之床/真影之床 · 影书桌 · 玻璃罐×3 · structure_block×24 等 |
| **矿石与材料** | 钛 · 熔金 · 染梦石英/粉尘 · 琥珀糖 · 灵魂 · 风行者水晶 · 锈黑金属 |
| **工具武器法杖** | 铜→钛→染梦合金→熔金杖三阶 · 魂啸杖 · 聚梦/魔力杖 · 星愿 · 影漩书 · 白剑雨 · 传说近战全套 |
| **收集 / 剧情** | 寻梦者笔记 0–14 · 凯尔卡 0–9 · 蓝图×2 · 帕斯特指南（Patchouli 条件） |

### 👾 实体与生物（51/51）

| 类型 | 物种 | 数量 |
| :--- | :--- | :-: |
| **中立/被动** | 萤火虫 · 金狐 · 水母 · 粉鸡 · 融梦水晶 · 小石灵 等 | — |
| **敌对** | 暗影魔像 · 惧喙系 · 骨翼 · 幽灵 · 雷云 · 风骑士 · 暗影之手 · 黑甲虫 · 振动水晶 等 | — |
| **Boss / 特殊** | 亚伦柯斯之触 · 高压雷云 · 惧喙（狂暴）· 黑甲虫女王 | — |
| **战斗投射 / 立场** | 五法术投射物 · 法杖投射物族 · 治疗/狂暴立场 · 大地之刃剑气 | — |

> 生物配备 **GeckoLib 3D 动画**（适用者）

### 🌍 维度与世界生成

| 特性 | 状态 | 详情 |
| :--- | :-: | :--- |
| **染梦维度** | ✅ | 自定义 biome source · 噪声地形 · 极光天空 |
| **影灯世界** | ✅ | 暗影群系 · 地牢/结构 · 暮影之笼事件 |
| **风之旅途** | ✅ | 风泊木 · 风骑士唤醒台 · 风向规则 |
| **亚伦柯斯竞技场** | ✅ | Boss 战维度 |
| **结构 / 遗迹** | ✅ | 染梦列车 · 世界树 · 教堂 · 地牢 · RuinAPI 等 |
| **生物群系音乐** | ✅ | 独立 BGM · 渐变切换 · `/pasterdream bgm` |

### 🧪 验证与质量门禁

| 工具 | 用途 |
| :--- | :--- |
| `PASTERDREAM_SMOKETEST=1` | 炼药锅等链路自动冒烟 |
| `PASTERDREAM_VERIFY=1` | 注册表 diff + 行为时间线（默认 KEEP_OPEN 不退出，便于人工观察方块/实体总览；CORE 含 curios / stale-comments） |
| `PASTERDREAM_VERIFY_SUITES=…` | **分类运行**（逗号分隔）；未设/`all`=全量。见下表 |
| `PASTERDREAM_VERIFY_KEEP_OPEN=0` | 测完自动退出客户端（CI/无头用） |
| `.trae/tools/check_lang.py` | 中英语言键完整性 |
| `.trae/tools/verify_resource_closure.py` | JSON/模型/纹理/音效/loot 闭包 |

**VERIFY 套件（`PASTERDREAM_VERIFY_SUITES`）**——未选中的套件完全不调度，时间线按所选压缩：

| 套件名 | 内容 |
| :--- | :--- |
| `registry` | manifest 注册表 diff + datapack 统计 |
| `core` | 属性/附件/效果/梦志/蓝图/储物袋/粉蛋/效果修饰/无尽书 + **curios 饰品** + **stale-comments** |
| `dimensions` / `dim` | 影灯世界 · 风之旅途往返 |
| `spells` | 五法术投射物行为 |
| `content` / `machines` | 暗影高炉 / 法杖扫射 / angel_block_item |
| `structures` | 结构生成子系统 |
| `workshop` | 武器工坊群 E2E |
| `struct-dim` | 结构→维度映射 |
| `gallery` | 方块总览展台 |
| `entity-gallery` | 实体展台 |
| `twilight-lantern` / `twilight` / `lantern` | 暮影之笼链路（**不在** `all`；玩法 `docs/superpowers/游戏流程分析/暮影之笼.md`；开放项 `docs/功能状态.md` §3） |
| `wind-journey` / `wind` / `third-dream` | 第三梦境风旅核实（**不在** `all`；玩法 `docs/superpowers/游戏流程分析/第三梦境.md`） |
| `wind-lake` / `wind_lake` / `lake` | 水色湖专项：NORMAL+开建筑；校验正式常驻 `safe_lake`；**不在** `all` |
| `dyedream` / `dye-dream` / `dream-world` | 染梦专项：狐狸雕像仪式 + flower_12 多方块 + 莲花；**不在** `all` |
| `second-dream` / `second` / `lamp-shadow` | 第二梦境灯影核实（**不在** `all`；玩法 `docs/superpowers/游戏流程分析/第二梦境.md`） |
| **快捷** `quick` | = `registry,core` |
| **快捷** `behavior` | = `core,dimensions,spells,content` |
| **快捷** `worldgen` | = `structures,struct-dim` |
| **快捷** `galleries` / `visual` | = `gallery,entity-gallery` |
| **快捷** `all` | 全量（默认；**不含** twilight-lantern / wind-journey / wind-lake / second-dream） |

方块总览展台（VERIFY 收尾自动铺，出生点东/南）：**主台**静物全量（门/双层植物上下半、染梦植物垫染梦草）；**特展带**用物品框+告示展示结构触发块 / 唤星限时块 / 流体（避免自毁与漫延）。

实体展台（VERIFY 紧随其后，出生点西侧）：**刷怪蛋木桶**装齐本模组 `*_spawn_egg`；**无蛋实体名签桶**（投射物/MISC 用命名纸张）；**活体玻璃笼** NoAI+无敌对照模型。测完玩家停在实体展台起点，可飞回方块台。

### ▶️ 进测试 / 终验命令（复制即用）

工作目录：仓库根 `/opt/MDEV/NeoPasterDream1`。需要 **Java 21**。  
下列每一段可**单独复制执行**，不要混在同一串里。

IDEA / Fleet：根目录 [`.run/`](.run/) 已写入同名运行配置，打开工程后直接 Run。  
配置**不**写死 `JAVA_HOME`（由 IDE / 系统 JDK 21 解析）；若本机默认不是 21，请在 IDEA 的 Gradle JVM / Project SDK 选 Java 21。

| 配置名 | 作用 |
| :--- | :--- |
| `PD VERIFY KEEP_OPEN` | 全量终验，测完不退出（推荐人工观察） |
| `PD VERIFY CI` | 全量终验，测完退出 |
| `PD VERIFY quick` | 仅 registry+core，测完退出 |
| `PD VERIFY workshop` | 仅工坊，测完退出 |
| `PD VERIFY structures` | 仅 worldgen（structures+struct-dim），测完退出 |
| `PD VERIFY wind-journey` | 第三梦境专项，测完退出（不在 all） |
| `PD VERIFY wind-lake` | 水色湖专项（非超平坦+开建筑），测完退出（不在 all） |
| `PD VERIFY twilight-lantern` | 暮影之笼专项，测完退出（不在 all） |
| `PD VERIFY second-dream` | 第二梦境专项，测完退出（不在 all） |
| `PD VERIFY dyedream` | 染梦专项（雕像仪式/莲花等），测完退出（不在 all） |
| `PD VERIFY main-flow` | 主干全链路（默认 dark），测完退出（不在 all） |
| `PD VERIFY main-flow (light)` | 主干全链路 light 抉择，测完退出 |
| `PD VERIFY spells` | 仅法术，测完退出 |
| `PD VERIFY behavior` | core+dimensions+spells+content，测完退出 |
| `PD VERIFY gallery` | 方块+实体展台，KEEP_OPEN |
| `PD SMOKETEST` / `PD SMOKETEST+VERIFY` | 冒烟 / 冒烟+终验 |
| `PD runClient` | 普通客户端 |
| `PD compileJava` | 仅编译 |
| `PD packageMod` | 打可安装单 jar → `build/dist/` |
| `PD check_lang` / `PD verify_resource_closure` | 静态门禁 |
| `PD read_verify_report` | 读 `pd_verify_report.json` |

#### A. 编译（可选）

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk sh gradlew :PasterDream:compileJava --offline
```

#### B. 静态门禁（不启动游戏）

```bash
python3 .trae/tools/check_lang.py
```

```bash
python3 .trae/tools/verify_resource_closure.py
```

#### C. 【推荐】人工观察终验（VERIFY，测完不退出）

行为说明：

- 删除旧存档 `test-audit` 并新建超平坦创造世界  
- 全程夜视 / 飞行 / 创造  
- 收尾铺**方块总览**（主台 + 特展带，东/南）与**实体展台**（刷怪蛋木桶 + 玻璃笼，西侧）  
- 玩家最终停在**实体展台**起点（可飞行回方块台）  
- 报告：`PasterDream/run/pd_verify_report.json`  
- 看完后**手动关游戏窗口**

```bash
PASTERDREAM_VERIFY=1 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

#### D. CI / 只要结果（VERIFY，测完自动退出）

```bash
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

#### D2. 分类运行（只跑需要的套件，避免一次跑完全部）

```bash
# 快速：注册表 + 核心行为
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=quick PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

```bash
# 仅工坊
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=workshop PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

```bash
# 结构 + 结构维度
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=worldgen PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

```bash
# 仅法术
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=spells PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

```bash
# 行为链：core + 维度 + 法术 + 高炉/法杖
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=behavior PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

```bash
# 展台（方块+实体，测完保持打开供观察）
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=galleries \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

```bash
# 第三梦境风旅专项（不在默认 all；结构/出维/祭坛/雷云等）
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=wind-journey PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

```bash
# 水色湖专项（不在 all；NORMAL+开建筑；校验正式常驻 safe_lake）
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=wind-lake PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

```bash
# 染梦世界专项（不在 all；狐狸雕像 + 迷梦冶梦莲多方块）
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=dyedream PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

```bash
# 第二梦境灯影专项（不在默认 all；门钥/d_0/e_0/GUARD/terrorbeak/pale/倒计时）
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=second-dream PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

```bash
# 暮影之笼专项（不在默认 all）
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=twilight-lantern PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

```bash
# 任意组合，逗号分隔
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=registry,workshop,spells \
  PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

#### E. 仅冒烟（炼药锅链路；不跑全量 VERIFY）

```bash
PASTERDREAM_SMOKETEST=1 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

#### F. 冒烟 + 终验双开（KEEP_OPEN 默认仍保持打开）

```bash
PASTERDREAM_SMOKETEST=1 PASTERDREAM_VERIFY=1 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

#### G. 查看机器可读报告

```bash
python3 - <<'PY'
import json
from pathlib import Path
p = Path("PasterDream/run/pd_verify_report.json")
d = json.loads(p.read_text())
print(f"pass={d['pass']} fail={d['fail']}")
fails = [a for a in d.get("assertions", []) if not a.get("pass")]
for a in fails[:30]:
    print("FAIL", a.get("suite"), a.get("name"), a.get("detail"))
PY
```

| 环境变量 | 默认 | 含义 |
| :--- | :-: | :--- |
| `PASTERDREAM_VERIFY=1` | 关 | 启用移植终验时间线 |
| `PASTERDREAM_VERIFY_SUITES` | `all` | 逗号分隔套件；见上方套件表 |
| `PASTERDREAM_VERIFY_KEEP_OPEN` | **开**（未设置即保持打开） | `0`/`false` = 测完退出 |
| `PASTERDREAM_SMOKETEST=1` | 关 | 炼药锅等冒烟（可与 VERIFY 同开） |

崩溃日志：`PasterDream/run/crash-reports/` · 最新运行日志：`PasterDream/run/logs/latest.log`。
***

## 🔄 进行中 / 后续

| 任务 | 优先级 | 描述 |
| :--- | :-: | :--- |
| **人工游玩回归** | 🔥 高 | 暗影成就链 · 真影之床抉择 · 工坊手感 · 法杖耗能 · Curios 佩戴 · 唤星裂隙 |
| **第三方可选联动** | 🌙 低 | Croptopia 深度；Tetra 待上游 1.21.1 |
| **新内容设计** | ⏳ 低 | 原版对齐完成后的扩展 |

详见 [`docs/功能状态.md`](docs/功能状态.md) · [`docs/验证复现.md`](docs/验证复现.md) · [`docs/注释审计.md`](docs/注释审计.md) · 索引 [`docs/README.md`](docs/README.md)。

***

## 📦 安装与打包

本仓库两个子模块，**发布给玩家时只装一个 jar**（再加下方运行时依赖）：

| 产物 | 说明 |
| :--- | :--- |
| `build/dist/pasterdream-<version>.jar` | **本模组**可安装包；已**内嵌** `PasterDreamAPI`（无独立 `mods.toml`） |
| `PasterDreamAPI` 带 `api` classifier 的 jar | 仅开发/依赖用，**不要**丢进 `mods/` |

### 玩家 / 整合包需一并安装的依赖

本模组**不**把第三方 jar 打进发布包（标准 soft-dep 方式）。`neoforge.mods.toml`：

| modId | 关系 | 建议版本（与开发一致） |
| :--- | :-: | :--- |
| `geckolib` | **required** | 4.8.4（1.21.1 NeoForge） |
| `curios` | **required** | 9.5.1+1.21.1 |
| `playeranimator` | optional | 2.0.4+1.21.1 |
| `jei` | optional | 开发期 localRuntime，玩家自选 |

### 开发者依赖（Maven，不再使用 `libs/*.jar`）

版本集中在根目录 [`gradle.properties`](gradle.properties)：`geckolib_version` / `curios_version` / `player_anim_version` / `jei_version`。

```groovy
// :PasterDream — 节选
implementation "software.bernie.geckolib:geckolib-neoforge-${minecraft_version}:${geckolib_version}"
implementation "top.theillusivec4.curios:curios-neoforge:${curios_version}"
implementation "dev.kosmx.player-anim:player-animation-lib-forge:${player_anim_version}"
```

仓库：GeckoLib Cloudsmith · TheIllusiveC4 · KosmX · BlameJared（JEI）。  
`libs/` 仅作**对照源码**（如 `FixPasterDream-main`），**不要**再放依赖 jar。

```bash
# 仓库根；需要 Java 21（首次解析依赖需联网；之后可用 --offline）
JAVA_HOME=/usr/lib/jvm/java-21-openjdk sh gradlew packageMod
# 或 :PasterDream:packageMod ；assemble / build 也会触发 packageMod
```

拷贝 `build/dist/pasterdream-*.jar` 与 GeckoLib / Curios（及可选 Player Animator）到游戏 `mods/` 即可（版本号见 `gradle.properties` 的 `mod_version`）。

***

## 🧭 开发：快捷切换维度

创造/作弊开着时，用原版 `execute in`（Y=160 与终验一致，落地后可飞行）：

```mcfunction
/execute in pasterdream:dyedream_world run tp @s 0 160 0
/execute in pasterdream:lamp_shadow_world run tp @s 0 160 0
/execute in pasterdream:wind_journey_world run tp @s 0 160 0
/execute in pasterdream:aaroncos_arena_world run tp @s 0 160 0
/execute in minecraft:overworld run tp @s ~ ~ ~
```

| 维度 | 正式入口（非调试） |
| :--- | :--- |
| 染梦 | 染梦裂隙 · 染梦传送水晶 |
| 影灯 | 暗影地牢门等流程 |
| 风之旅途 | 风维度流程物品 |
| 竞技场 | 亚伦柯斯竞技场相关 |

说明：`/pasterdream dimension reset <id>` 是**重置维度存档**，不是传送。调试栏 `DEBUG_WAND_*` 只刷结构。

***

## 🎒 创造模式标签（摘要）

原版多 tab 语义已按主题重排（不按 `paster_tab_*` 旧名 diff）：

| 标签 | 内容 |
| :--- | :--- |
| **生物实体** | **仅刷怪蛋**；掉落材料不进此页 |
| 武器工具 | 锭/胚/武器 + 灵魂尘、粉史莱姆球、甲虫甲壳等掉落材料 |
| 阴影维度 | 影系方块/机关 + 暗影吐息、苔藓幻膜等 |
| 食物饮品 | 含水母泥 / 水母果冻 |
| 纪念品 | 笔记/蓝图/法术物等 + 草莓之心 |
| §c 调试功能 | 结构调试杖等 |

特效实体刷怪蛋（如大地之刃剑气、治疗立场）不进创造刷怪蛋栏（与原版一致）。

***

## ❓ 常见问题

| 问题                        | 回答                                                                  |
| :------------------------ | :------------------------------------------------------------------ |
| **与原版 PasterDream 有何区别？** | 原版是 1.20.1 Forge + MCreator。新帕斯特之梦在 1.21.1 NeoForge 下使用原生 API 完全重写。 |
| **美术风格会改变吗？**             | \*\*不会。\*\*原版作者的纹理、模型和视觉设计是我们珍视的遗产，将完整保留。                           |
| **现在可以玩吗？** | **可以。** 四维度主路径、工作站、笔记/卡牌/法杖与 62 成就均已接通；详见差距报告终验表。 |
| **要装几个 jar？** | **一个。** `packageMod` 产出的 `pasterdream-*.jar`（已内嵌 API）。不要再装 `PasterDreamAPI`。 |
| **怎么快速去自定义维度？** | 见上文「快捷切换维度」；或用裂隙/传送水晶等正式入口。 |
| **存档会损坏吗？** | 染梦等维度通过裂隙/物品进入，不改写主世界生成。未来更新尽量向后兼容。 |
| **支持多人联机吗？** | **完全兼容**多人服务器。 |
| **构建报 class file major version 70？** | 系统默认成了 Java 26。请用 **Java 21**（`JAVA_HOME=/usr/lib/jvm/java-21-openjdk` 或 IDE 的 Gradle JVM）。 |

***

## 🤝 致谢

> **原版 PasterDream 模组作者**
>
> 感谢您在纹理绘制、模型构建和视觉设计方面的所有工作。这些艺术作品是新帕斯特之梦的基石。我们站在巨人的肩膀上。

- 以及一路上帮助和支持的所有朋友们。

***

## 📜 许可协议

MIT 许可证 · 开源共享

***

**🌙 世界在等待你。染梦之门已经开启。🌙**


*✨ 为了你自己，或是为了那些先行的寻梦者。✨*


