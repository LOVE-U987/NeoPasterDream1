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

**原版主路径注册与自动化行为抽测已闭环**（2026-07-27 客户端终验 194/194，含结构/工坊/结构维度/方块总览）。它仍会成长——但首先，你可以完整游玩原作者设计的染梦/影灯/风旅主线。

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
| **自动化验证** | ██████████**100%** | ✅ VERIFY 行为时间线（structures×20 + workshop×28 + struct-dim×15 + gallery×3 + entity-gallery×5） |
| **新内容设计** | ░░░░░░░░░░**\~5%** | ⏳ 原版对齐完成后的扩展位 |

详见根目录 [`功能还原差距报告.md`](功能还原差距报告.md)。

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
| `PASTERDREAM_VERIFY=1` | 注册表 diff + 行为时间线（终验 194 pass；默认 KEEP_OPEN 不退出，便于人工观察方块总览） |
| `PASTERDREAM_VERIFY_KEEP_OPEN=0` | 测完自动退出客户端（CI/无头用） |
| `.trae/tools/check_lang.py` | 中英语言键完整性 |
| `.trae/tools/verify_resource_closure.py` | JSON/模型/纹理/音效/loot 闭包 |

方块总览展台（VERIFY 收尾自动铺，出生点东/南）：**主台**静物全量（门/双层植物上下半、染梦植物垫染梦草）；**特展带**用物品框+告示展示结构触发块 / 唤星限时块 / 流体（避免自毁与漫延）。

实体展台（VERIFY 紧随其后，出生点西侧）：**刷怪蛋木桶**装齐本模组 `*_spawn_egg`；**无蛋实体名签桶**（投射物/MISC 用命名纸张）；**活体玻璃笼** NoAI+无敌对照模型。测完玩家停在实体展台起点，可飞回方块台。

### ▶️ 进测试 / 终验命令（复制即用）

工作目录：仓库根 `/opt/MDEV/NeoPasterDream1`。需要 **Java 21**。  
下列每一段可**单独复制执行**，不要混在同一串里。

IDEA / Fleet：根目录 [`.run/`](.run/) 已写入同名运行配置（`PD VERIFY KEEP_OPEN` 等），打开工程后直接 Run。

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
| `PASTERDREAM_VERIFY_KEEP_OPEN` | **开**（未设置即保持打开） | `0`/`false` = 测完退出 |
| `PASTERDREAM_SMOKETEST=1` | 关 | 炼药锅等冒烟（可与 VERIFY 同开） |

崩溃日志：`PasterDream/run/crash-reports/` · 最新运行日志：`PasterDream/run/logs/latest.log`。
***

## 🔄 进行中 / 后续

| 任务 | 优先级 | 描述 |
| :--- | :-: | :--- |
| **人工游玩回归** | 🔥 高 | 暗影成就链 · 真影之床抉择 · 工坊手感 · 法杖耗能 · Curios 佩戴 · 唤星裂隙 |
| **装饰性差异** | 🌙 低 | 卡牌全屏展示动画 · 部分纯 VFX · playerAnimator 资源 |
| **第三方可选联动** | 🌙 低 | Croptopia 深度；Tetra 待上游 1.21.1 |
| **新内容设计** | ⏳ 低 | 原版对齐完成后的扩展 |

详见 [`功能还原差距报告.md`](功能还原差距报告.md) §2 模块 · §3 后续 · §4 命令（2026-07-27）。

***

## ❓ 常见问题

| 问题                        | 回答                                                                  |
| :------------------------ | :------------------------------------------------------------------ |
| **与原版 PasterDream 有何区别？** | 原版是 1.20.1 Forge + MCreator。新帕斯特之梦在 1.21.1 NeoForge 下使用原生 API 完全重写。 |
| **美术风格会改变吗？**             | \*\*不会。\*\*原版作者的纹理、模型和视觉设计是我们珍视的遗产，将完整保留。                           |
| **现在可以玩吗？** | **可以。** 四维度主路径、工作站、笔记/卡牌/法杖与 62 成就均已接通；详见差距报告终验表。 |
| **存档会损坏吗？**               | 染梦维度通过裂隙进入，不影响主世界。未来更新保持向后兼容。                                       |
| **支持多人联机吗？**              | **完全兼容**多人服务器。                                                      |

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


