<div align="center">

# 🧠 PasterDreamSanity · 理智系统

<p><i>看得越多，越不像自己。</i></p>
<p><i>理智是脆弱的灯，梦境的风一直在吹。</i></p>

</div>

---

## 🌙 模块简介

**PasterDreamSanity** 是 `NeoPasterDream` 的附属模组之一，负责承载原版的「理智值（Sanity / San）」系统。

它从主模组中独立出来，编译期仅依赖 `PasterDreamAPI`，不反向依赖 `PasterDream` 主模组代码。运行时 `PasterDreamAPI` 被打包在 `PasterDream` 主模组内，因此仍需主模组作为前置。所有 San 值变化、环境修饰与低 San 惩罚均在此模块中处理。

> 屏幕会抖动，视野会暗淡，你会听见不该听见的东西。太低了，噩梦就会找到你。

---

## 🔧 核心机制

### 理智值 (San)

- 每位玩家拥有独立的 San 值。
- 受环境、维度、天气与饰品属性 `SAN_VARIABILITY` 影响。
- 高 San 可获得「振奋」增益；低 San 会陷入「恍惚」「不振」，直至「疯狂」。

### 状态阶段

| San 区间 | 效果 |
| :------- | :--- |
| `> 80` | 获得 **振奋 (cheerup_buff)**：移速、攻速、瞬身术与战技冷却优化。 |
| `≤ 60` | 获得 **不振 (lethargy_buff)**：小幅减速、减攻速、增加冷却。 |
| `≤ 40` | 获得 **恍惚 (trance_buff)**：中等减速、减攻、增加冷却。 |
| `≤ 20` | 获得 **疯狂 (insand_buff)**：画面抖动、幻觉生物、大幅削弱。 |
| `≤ 10` | 疯狂等级 II |
| `≤ 1`  | 疯狂等级 III |

### 配置项 (`pasterdreamsanity-common.toml`)

| 配置键 | 说明 | 默认值 |
| :----- | :--- | :----- |
| `enable san system` | 是否启用 San 系统总开关 | `true` |
| `enable low san debuff` | 是否启用低 San debuff | `true` |
| `overworld night lowers san` | 主世界夜晚是否降 San | `true` |
| `nether lowers san` | 下界是否降 San | `true` |
| `end lowers san` | 末地是否降 San | `true` |
| `rain lowers san` | 雨天是否降 San | `true` |
| `thunder lowers san` | 雷暴是否降 San | `true` |
| `recover interval` | 自然恢复间隔（tick） | `1200` |
| `recover amount` | 自然恢复量 | `0.1` |
| `cheerup threshold` | 振奋效果阈值 | `80.0` |
| `tick update interval` | San 系统总刻更新间隔 | `5` |

---

## 🌼 已注册内容

### 物品

| 注册名 | 说明 |
| :----- | :--- |
| `pasterdream:white_flower_body` | **白花胸针** · Curios 身体槽史诗饰品。装备后不再受到环境造成的降 San 影响。 |

### 状态效果

| 注册名 | 说明 |
| :----- | :--- |
| `pasterdream:cheerup_buff` | **振奋** · 有益效果，提升多项战斗属性。 |
| `pasterdream:insand_buff` | **疯狂** · 有害效果，画面抖动并召唤恐怖幻觉。 |
| `pasterdream:trance_buff` | **恍惚** · 有害效果，削弱战斗与机动能力。 |
| `pasterdream:lethargy_buff` | **不振** · 有害效果，轻微削弱。 |
| `pasterdream:san_increase` | **精神回复** · 瞬时增加 San 值。 |
| `pasterdream:san_decrease` | **精神损伤** · 瞬时减少 San 值。 |

---

## 🏗️ 架构说明

- `PDSanityHelper.onPlayerTick` 挂接在游戏总线，负责每 tick 的环境修饰刷新与低 San 效果施加。
- 配置通过 `SanConfigRegistry` 注册到 `PasterDreamAPI`，主模组与其他模块可无硬依赖读取。
- `white_flower_body` 使用 `pasterdream` 命名空间注册，以兼容原版 ID、主模组语言文件与剧情掉落。

---

## 📦 构建与运行

```bash
# 编译本模块（同时会编译 PasterDreamAPI）
.\gradlew :PasterDreamSanity:compileJava

# 数据生成
.\gradlew :PasterDreamSanity:runData

# 客户端测试
.\gradlew :PasterDreamSanity:runClient
```

> 本模块为 thin 发行模式，不内嵌 `PasterDreamAPI`（由 `PasterDream` 主模组打包提供）。运行时请确保 `PasterDream` 主模组已加载。

---

<div align="center">

<i>光明并非救赎，它只是让你看清，黑暗有多深。</i>

</div>
