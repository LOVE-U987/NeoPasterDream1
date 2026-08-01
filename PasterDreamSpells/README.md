<div align="center">

# 🔮 PasterDreamSpells · 法术系统

<p><i>梦境有它的语言，而你已经学会了几个音节。</i></p>
<p><i>念出它们——然后看着现实裂开缝隙。</i></p>

</div>

---

## 🌙 模块简介

**PasterDreamSpells** 是 `NeoPasterDream` 的附属模组之一，负责承载原版的「法术」系统。

它从主模组中独立出来，编译期依赖 `PasterDreamAPI`，可选依赖 `PasterDreamSanity` 与 `PasterDreamMeltDream`。运行时 `PasterDreamAPI` 被打包在 `PasterDream` 主模组内，因此仍需主模组作为前置。模块内包含五种元素法术卷轴、对应的投射物实体、立场实体、粒子与音效。

> 法杖不是装饰，它们是梦的另一种形状。

---

## 🔧 核心机制

### 法术物品 (Spell Item)

- 五种法术卷轴共用 `SpellItem` 基类。
- 长按右键蓄力，松开后发射法术投射物（类似弓）。
- 非创造模式下，每次施放消耗 1 个卷轴。
- 堆叠上限 8。

### 五大法术

| 法术 | 效果范围 | 命中效果 |
| :--- | :------- | :------- |
| **闪电法术** | 5×5 区域 | 4 次随机落雷 + 电火花粒子脉冲 |
| **剧毒法术** | 7×7 区域 | 三波毒雾攻势，施加剧毒 IV / 虚弱 / 缓慢 |
| **治疗法术** | 5×5 区域 | 生成持续 20 秒的治疗立场，每秒回复 5% 最大生命值 |
| **狂暴法术** | 8×8 区域 | 生成狂暴立场，多次脉冲给予玩家狂暴增益 |
| **冰冻法术** | 7×7 区域 | 5 波冻结，定身并附加冰冻减益 |

### 配置项 (`pasterdreamspells-common.toml`)

| 配置键 | 说明 | 默认值 |
| :----- | :--- | :----- |
| `enable spell system` | 是否启用法术系统总开关 | `true` |
| `spell cost multiplier` | 法术消耗倍率 | `1.0` |
| `spell cooldown multiplier` | 法术冷却倍率 | `1.0` |

---

## ✨ 已注册内容

### 物品

| 注册名 | 说明 |
| :----- | :--- |
| `lightning_spell` | **闪电法术** · 落雷型范围伤害。 |
| `poison_spell` | **剧毒法术** · 持续毒雾与削弱。 |
| `healing_spell` | **治疗法术** · 团队持续恢复立场。 |
| `fury_spell` | **狂暴法术** · 范围增益立场。 |
| `ice_spell` | **冰冻法术** · 范围冻结控制。 |

### 实体

| 注册名 | 说明 |
| :----- | :--- |
| `fury_spell_entity` | **狂暴法术立场** · 持续释放狂暴脉冲。 |
| `healing_spell_entity` | **治疗法术立场** · 持续治疗范围内友方。 |
| `*_spell_projectile` | 五种法术投射物（弓箭弹道，命中触发效果）。 |

### 状态效果

| 注册名 | 说明 |
| :----- | :--- |
| `fury_spell_buff` | **狂暴法术** · 大幅提升攻击力、攻速与移速，减少瞬身术和战技冷却。 |
| `ice_spell_buff` | **冰冻** · 被冰封定身，无法移动与造成伤害。 |

### 粒子与音效

- 自定义粒子：`fury_spell_particle`、`healing_spell_particle`、`poison_gas_particle`、`poison_soul_particle`、`snowflake_0/1_particle`、`yellow_smoke_particle`
- 专属音效：`fury_spell`、`healing_spell`、`ice_spell`、`lightning_spell`、`poison_spell` 等

---

## 🏗️ 架构说明

- 法术命中效果集中管理于 `SpellEffects`，通过 `ServerScheduler` 实现原版 `queueServerWork` 的精确时序。
- 客户端渲染与粒子提供器注册在 `PDSpellsClientSetup`。
- 法术投射物继承 `AbstractArrow`，无伤害、无击退、静音、不可拾取，命中后触发对应效果。

---

## 📦 构建与运行

```bash
# 编译本模块（同时会编译 PasterDreamAPI）
.\gradlew :PasterDreamSpells:compileJava

# 数据生成
.\gradlew :PasterDreamSpells:runData

# 客户端测试
.\gradlew :PasterDreamSpells:runClient
```

> 本模块为 thin 发行模式，不内嵌 `PasterDreamAPI`（由 `PasterDream` 主模组打包提供）。运行时请确保 `PasterDream` 主模组已加载。

---

<div align="center">

<i>五个音节，五种结局。你选哪一种？</i>

</div>
