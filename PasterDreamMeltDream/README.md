<div align="center">

# ⚡ PasterDreamMeltDream · 融梦能量

<p><i>那不是经验，也不是饥饿。</i></p>
<p><i>是你与梦境之间的连接强度。</i></p>

</div>

---

## 🌙 模块简介

**PasterDreamMeltDream** 是 `NeoPasterDream` 的附属模组之一，负责承载原版的「融梦能量」系统。

它从主模组中独立出来，编译期仅依赖 `PasterDreamAPI`，不反向依赖 `PasterDream` 主模组代码。运行时 `PasterDreamAPI` 被打包在 `PasterDream` 主模组内，因此仍需主模组作为前置。

> 当能量枯竭，法术将归于沉寂。

---

## 🔧 核心机制

### 融梦能量 (MeltDream Energy)

- 独立的玩家能量池，用于驱动法杖、法术与其他梦境装备。
- 自然恢复：每配置间隔（默认 1200 tick）恢复一定量。
- 身处梦境维度（染梦世界 / 灯影世界）时，可通过特定饰品进一步获取能量。

### 配置项 (`pasterdreammeltdream-common.toml`)

| 配置键 | 说明 | 默认值 |
| :----- | :--- | :----- |
| `enable meltdream system` | 是否启用融梦能量系统总开关 | `true` |
| `recover interval` | 自然恢复间隔（tick） | `1200` |
| `recover amount` | 自然恢复量 | `0.1` |
| `chest generation multiplier` | 融梦水晶箱自然产生能量倍率 | `1.0` |
| `chest hurt multiplier` | 融梦水晶箱被攻击时能量产生倍率 | `1.0` |
| `chest kill multiplier` | 融梦水晶箱被杀死时能量产生倍率 | `1.0` |
| `chest max energy` | 融梦水晶箱能量转化上限 | `1000.0` |

---

## 💍 已注册内容

### 物品

| 注册名 | 说明 |
| :----- | :--- |
| `meltdream_energy_0_ring` | **融梦光环戒指** · Curios 戒指槽饰品。身处梦境维度时，每秒额外恢复 0.0025 点融梦能量（约 0.15 / 分钟）。 |

### 状态效果

| 注册名 | 说明 |
| :----- | :--- |
| `pasterdream:melt_dream_energy_increase` | **融梦能量增加** · 瞬时生效，能量 + (等级 + 1)。 |
| `pasterdream:melt_dream_energy_decrease` | **融梦能量减少** · 瞬时生效，能量 - (等级 + 1)。 |

---

## 🏗️ 架构说明

- 所有配置通过 `PDAddonConfigRegistry` 注册到 `PasterDreamAPI`，供主模组配置界面统一展示与持久化。
- 配置实现 `IMeltDreamEnergySystemConfig` 接口，并通过 `MeltDreamEnergyConfigRegistry` 向 API 注册，供其他模块无硬依赖读取。
- 状态效果注册在 `MobEffectAPI.REGISTRY`（`pasterdream` 命名空间），确保主模组在运行时能够查找。

---

## 📦 构建与运行

```bash
# 编译本模块（同时会编译 PasterDreamAPI）
.\gradlew :PasterDreamMeltDream:compileJava

# 数据生成
.\gradlew :PasterDreamMeltDream:runData

# 客户端测试
.\gradlew :PasterDreamMeltDream:runClient
```

> 本模块为 thin 发行模式，不内嵌 `PasterDreamAPI`（由 `PasterDream` 主模组打包提供）。运行时请确保 `PasterDream` 主模组已加载。

---

<div align="center">

<i>梦境在呼吸。你，是否也在同步？</i>

</div>
