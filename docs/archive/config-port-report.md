# PasterDream 配置项移植报告

> 来源：`libs/FixPasterDream-main`（1.20.1 Forge / MCreator 生成）  
> 目标：`PasterDream`（1.21.1 NeoForge）  
> 报告时间：2026-07-29

## 一、结论

原模组的全部配置项**已经完整移植**到当前项目，并以 NeoForge `ModConfigSpec` 重新实现。配置键、注释、默认值与原版保持一致（包括原版个别键末尾的空格，以确保旧 TOML 文件兼容）。

- 客户端配置：`PasterDream-Client.toml`（7 项）
- 通用配置：`PasterDream-Common.toml`（19 项）
- 合计：**26 项配置**

当前项目注册、读取、保存、界面编辑均已可用，`./gradlew :PasterDream:compileJava` 编译通过。

---

## 二、原模组配置定义位置

| 原文件 | 类型 | 说明 |
|:--|:--|:--|
| `net/pasterdream/configuration/PasterdreamConfigClientConfiguration.java` | Client | 7 项 HUD 相关配置 |
| `net/pasterdream/configuration/PasterdreamConfigCommonConfiguration.java` | Common | 19 项游戏机制/性能/禁用配置 |
| `net/pasterdream/init/PasterdreamModConfigs.java` | 注册 | 使用 `ModLoadingContext.registerConfig` 注册 CLIENT / COMMON |

---

## 三、当前项目对应实现

| 当前文件 | 对应原文件 | 说明 |
|:--|:--|:--|
| `com.pasterdream.pasterdreammod.config.PDClientConfig` | `PasterdreamConfigClientConfiguration` | NeoForge `ModConfigSpec` 重写 |
| `com.pasterdream.pasterdreammod.config.PDCommonConfig` | `PasterdreamConfigCommonConfiguration` | NeoForge `ModConfigSpec` 重写 |
| `com.pasterdream.pasterdreammod.PasterDreamMod` | `PasterdreamModConfigs` | 构造器中 `registerConfig` 注册 |
| `com.pasterdream.pasterdreammod.client.gui.config.PDConfigScreen` | 新增 | 原模组无配置界面，当前项目新增 |

---

## 四、配置项对照表

### 4.1 客户端配置（PasterDream-Client.toml）

| 当前字段 | 原字段 | TOML 键 | 类型 | 默认值 | 当前使用状态 |
|:--|:--|:--|:--|:--|:--|
| `STEALTH_DISPLAY_ATTRIBUTE_HUD` | ✅ | `stealth display attribute hud` | boolean | false | ✅ 已用于 HUD 渲染 |
| `LOADING_GUI_TIPS` | ✅ | `loading gui tips` | boolean | true | ⚠️ 已定义，暂无消费者 |
| `PASTER_HEALTH_HUD` | ✅ | `paster health hud` | boolean | true | ✅ 已用于 HUD 渲染 |
| `MELTDREAMENERGY_TANK_XBASE` | ✅ | `meltdreamenergy tank xbase ` | double | 1.0 | ✅ 已用于 HUD 渲染 |
| `MELTDREAMENERGY_TANK_YBASE` | ✅ | `meltdreamenergy tank ybase ` | double | -19.0 | ✅ 已用于 HUD 渲染 |
| `SAN_TANK_XBASE` | ✅ | `san tank xbase` | double | -36.0 | ✅ 已用于 HUD 渲染 |
| `SAN_TANK_YBASE` | ✅ | `san tank ybase` | double | -34.0 | ✅ 已用于 HUD 渲染 |

### 4.2 通用配置 — Basic（PasterDream-Common.toml）

| 当前字段 | 原字段 | TOML 键 | 类型 | 默认值 | 当前使用状态 |
|:--|:--|:--|:--|:--|:--|
| `OVERWORLD_NIGHT_LOWERS_SAN` | ✅ | `overworld night lowers san` | boolean | true | ✅ `PDSanHelper` 中生效 |
| `DYEDREAM_CRACK_GENERATE` | ✅ | `dyedream crack generate` | boolean | true | ⚠️ 已定义，暂无消费者 |
| `LOW_SAN_DEBUFF` | ✅ | `low san debuff` | boolean | true | ✅ `PDSanHelper` 中生效 |
| `CHEERUP_BUFF_THRESHOLD_VALUE` | ✅ | `cheerup buff threshold value` | double | 99.0 | ✅ `PDSanHelper` 中生效 |
| `MELTDREAM_CHEST_LEGEND_MULTIPLIER` | ✅ | `meltdream chest legend multiplier` | double | 1.0 | ⚠️ 已定义，暂无消费者 |
| `MELTDREAM_CHEST_RARE_MULTIPLIER` | ✅ | `meldtream chest rare multiplier` | double | 1.0 | ⚠️ 已定义，暂无消费者 |
| `SLEEP_SAN_RECOVERY_AMOUNT` | ✅ | `sleep san recovery amount` | double | 10.0 | ⚠️ 已定义，暂无消费者 |
| `LOW_SAN_PICTURE_JITTER` | ✅ | `low san picture jitter` | boolean | true | ⚠️ 已定义，暂无消费者 |
| `THE_ORIGIN_OF_THE_WORLD_INITIALLY_GENERATED_DYEDREAM_CRACK` | ✅ | `the origin of the world initially generated dyedream crack ` | boolean | false | ⚠️ 已定义，暂无消费者 |
| `MOD_ACCOUOCEMENT` | ✅ | `mod accouocement` | boolean | true | ⚠️ 已定义，暂无消费者 |
| `IN_LAMP_SHADOW_GIVE_PALE_BONENEEDLE` | ✅ | `in lamp shadow give pale boneneedle` | boolean | false | ✅ `LampShadowEvents` 中生效 |
| `NO_RETURN_DYEDREAM_CRACK` | ✅ | `no return dyedream crack` | boolean | false | ⚠️ 已定义，暂无消费者 |
| `DYEDREAM_ORIGIN_SPAWNPOINT` | ✅ | `dyedream origin spawnpoint` | boolean | true | ⚠️ 已定义，暂无消费者 |
| `SHADOW_NPC_THIRD_DIALOGUE_AFTER_TP_PLAYER_BACK_TO_OVERWORLD` | ✅ | `shadow npc third dialogue after tp player back to overworld` | boolean | true | ✅ `ShadowNpc0Entity` 中生效 |

### 4.3 通用配置 — property / Ban

| 当前字段 | 原字段 | TOML 键 | 类型 | 默认值 | 当前使用状态 |
|:--|:--|:--|:--|:--|:--|
| `PLAYER_TOTAL_TICK_UPDATE` | ✅ | `player total tick update` | int | 5 | ✅ `WindJourneyEvents`、`PDSanHelper` 中生效 |
| `BAN_ALL_THE_WINGS` | ✅ | `ban all the wings` | boolean | false | ✅ 多个翅膀物品中生效 |
| `BAN_TERRA_SWORD` | ✅ | `ban terra sword` | boolean | false | ✅ `TerraSwordItem` 中生效 |
| `BAN_FIRE_NECKLACE` | ✅ | `ban fire necklace` | boolean | false | ⚠️ 已定义，暂无消费者 |
| `BAN_TIME_HOURGLASS` | ✅ | `ban time hourglass` | boolean | false | ⚠️ 已定义，暂无消费者 |

**状态图例：**
- ✅ 已接入当前功能代码
- ⚠️ 已定义并可在配置界面编辑，但对应的功能模块尚未移植/实现，因此当前未产生游戏内效果

---

## 五、移植时保留的原版细节

1. **TOML 键名完全保留**  
   包括原版键末尾的空格：
   - `meltdreamenergy tank xbase `
   - `meltdreamenergy tank ybase `
   - `the origin of the world initially generated dyedream crack `

2. **注释内容完全保留**  
   所有 `builder.comment(...)` 的文案与原版一致，确保生成的 TOML 文件对玩家而言没有变化。

3. **分类结构完全保留**  
   - Client：`[HUD]`
   - Common：`[Basic]`、`[property]`、`[Ban]`

4. **数据类型映射**  
   | 原版 Forge | 当前 NeoForge |
   |:--|:--|
   | `ForgeConfigSpec` | `ModConfigSpec` |
   | `ForgeConfigSpec.ConfigValue<T>` | `ModConfigSpec.ConfigValue<T>` |
   | `ForgeConfigSpec.Builder` | `ModConfigSpec.Builder` |

---

## 六、集成与可用性验证

### 6.1 注册

在 [`PasterDreamMod.java`](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDream/src/main/java/com/pasterdream/pasterdreammod/PasterDreamMod.java#L193-L194) 构造器中完成注册：

```java
modContainer.registerConfig(ModConfig.Type.CLIENT, PDClientConfig.SPEC, "PasterDream-Client.toml");
modContainer.registerConfig(ModConfig.Type.COMMON, PDCommonConfig.SPEC, "PasterDream-Common.toml");
```

### 6.2 游戏内编辑

通过 Mod 列表 → PasterDream → Config 打开 [`PDConfigScreen`](file:///c:/Users/97128/Documents/GitHub/NeoPasterDream1/PasterDream/src/main/java/com/pasterdream/pasterdreammod/client/gui/config/PDConfigScreen.java)，支持：

- 分类切换（界面显示 / 基础机制 / 性能属性 / 功能禁用）
- 布尔开关、数值输入
- 输入范围校验
- 保存 / 恢复默认
- 入场动画与变更反馈

### 6.3 编译验证

```bash
./gradlew :PasterDream:compileJava
# BUILD SUCCESSFUL
```

---

## 七、前置条件与未生效项说明

当前项目按照既定规划**仅开发了部分系统 API**（染梦能量、法术、San 系统等），未全面移植原模组的所有物品/方块/机制。因此：

- 已接入的功能配置（如 HUD、San 系统、翅膀禁用等）**可立即生效**。
- 未接入的功能配置（如染梦裂隙生成、融梦水晶箱倍率、睡眠恢复、聊天栏公告等）**已完整定义并可被编辑保存**，但需对应功能模块移植后才能产生实际效果。

这些未生效项不属于配置系统本身的前置条件，而是对应游戏功能的前置条件。按你的要求，**已跳过这些功能移植**，仅确保配置项本身可用、可持久化、可编辑。

---

## 八、文件清单

| 文件 | 说明 |
|:--|:--|
| `PasterDream/src/main/java/com/pasterdream/pasterdreammod/config/PDClientConfig.java` | 客户端配置定义 |
| `PasterDream/src/main/java/com/pasterdream/pasterdreammod/config/PDCommonConfig.java` | 通用配置定义 |
| `PasterDream/src/main/java/com/pasterdream/pasterdreammod/PasterDreamMod.java` | 配置注册 |
| `PasterDream/src/main/java/com/pasterdream/pasterdreammod/client/gui/config/PDConfigScreen.java` | 配置界面 |
| `PasterDream/src/main/java/com/pasterdream/pasterdreammod/client/gui/config/ConfigEntry.java` | 配置项控件 |
| `PasterDream/src/main/resources/assets/pasterdream/lang/zh_cn.json` | 配置界面翻译键 |
| `PasterDream/run/config/PasterDream-Client.toml` | 运行时客户端配置 |
| `PasterDream/run/config/PasterDream-Common.toml` | 运行时通用配置 |

---

## 九、后续建议

当对应功能模块继续移植时，直接读取已有配置即可，无需再次修改配置定义：

```java
// 示例
if (PDCommonConfig.DYEDREAM_CRACK_GENERATE.get()) {
    // 生成逻辑
}
```

对于数值类配置，建议始终做范围保护（配置 UI 已做 2~20 / 0~5000 等校验，代码侧仍建议 `Math.max/min` 兜底）。
