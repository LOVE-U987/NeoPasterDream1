# 2026-08-03 全库审查合并

> 时间：2026-8-3
> 
> 作者：MomoNyako

> 本文件记录 `2026-08-03-full-repository-review.md` 的合并与后续修复变更。
> 状态图例：🆕 本次合并 · 🔧 已修复 · ✅ 已核验 · 📄 文档更新

---

## Ver.2 2026-08-04 · C2-3 修复 + C2-4 核验

> 前置分析：临时审查报告 [`2026-08-04-C2-review.md`](2026-08-04-C2-review.md)（C2-3 单点收敛 / C2-4 源码核验）

### 🔧 已修复

| ID | 变更 |
|---|---|
| C2-3 | 四件护甲（Sculk/Titanium/Dyedream/Copper）`inventoryTick` 收敛到头盔单点触发全套检查，每 tick 触发次数 4× → 1×，行为不变 |

### ✅ 已核验（不修）

| ID | 变更 |
|---|---|
| C2-4 | 满套 `addEffect(10t/0级)` **不会**覆盖外来更强/更长的同名效果：vanilla `MobEffectInstance.update` 仅在「新效果更高等级」或「同级且更长」时替换，弱效果不占优；仅存在一次性 cosmetic 干扰（外来同名效果 `visible/showIcon` 被翻 false → HUD 图标/粒子隐藏）。维持现状不修 |

---

### C2-3 详细 — 四件各自每 tick 重复执行全套检查

**问题**：四个盔甲类各自覆写 `inventoryTick` 并完整执行 `checkAndApplySetEffect`，满套时每 tick 重复 4 次全套检查（携带多件时更多），无单点。

**根因（调用链，1.21.1 反汇编确认）**：

```
Player.tick() → LivingEntity.tick() → aiStep() → Inventory.tick()      [Player.java:552, Inventory.java:228-240]
└─ Inventory.tick() 遍历 compartments [main(36), armor(4), offhand(1)]
     └─ 护甲槽(36-39) 每件各触发一次 inventoryTick                        [Inventory.java:235]
          └─ <对应>ArmorItem.inventoryTick(...)     [ArmorItem 基类不覆写,行为全由各盔甲类决定]
               └─ checkAndApplySetEffect(entity)     ← 满套时 4× 重复
```

- `Inventory.tick()`（`Inventory.java:35,228-240`）对护甲槽 36-39 每件各调用一次 `inventoryTick`。
- 参考范本 `QymArmorItem.java:56` 已收敛到头盔单点。

**修复**：在四个盔甲类 `inventoryTick` 内、C2-1 守卫**之前**追加头盔单点短路：

```java
// C2-3 修复：仅头盔作为套装检查触发点
if (this.getType() != ArmorItem.Type.HELMET) {
    return;
}
```

- 满套必然含头盔且头盔穿在护甲槽 36，每 tick 必被触发 → 单点可靠。
- **保留** C2-1 归属守卫（比 Qym 范本多一层，背包携带头盔也不触发）。
- 触发次数 4×/tick → 1×/tick，`checkAndApplySetEffect` 逻辑与套装判定不变。

**变更文件**：

| 文件 | 变更 |
|---|---|
| `PasterDream/.../item/SculkArmorItem.java` | `inventoryTick` 加头盔单点短路 |
| `PasterDream/.../item/TitaniumArmorItem.java` | 同上 |
| `PasterDream/.../item/DyedreamArmorItem.java` | 同上 |
| `PasterDream/.../item/CopperArmorItem.java` | 同上 |

**验证**：`.\gradlew compileJava` → BUILD SUCCESSFUL。

**修复后行为**：✅ 满套每 tick 仅头盔执行 1 次全套检查；✅ 背包携带头盔仍不触发（C2-1 保留）。

---

### C2-4 核验 — 满套 `addEffect` 覆盖外来更强效果

**原报告**：满套每 tick `addEffect(同名效果, 10t, 0级)` 会覆盖外来更强/更长效果（如药水隐身 II 被降级为 0 级 10t）。

**核验（源码，NeoForge 21.1.219 反汇编）**：**不成立**。

`LivingEntity.addEffect`（`LivingEntity.java:971-991`）对已有同名效果调用 `MobEffectInstance.update(other)`（`MobEffectInstance.java:138-181`），替换条件仅两个方向：

1. `other.amplifier > this.amplifier`（新效果**更高等级**）→ 替换等级+时长；
2. `isShorterDurationThan(other)`（现有**更短**且**同级**）→ 仅拉长时长。

10t/0级 弱效果在两个条件上都不占优 → 外来隐身 II / 长时隐身 I **永不**被替换降级；异级且现有效果更短时弱效果存入 `hiddenEffect` 链，等强效果到期后自动续接（vanilla 正常链式语义）。

**真实残留（次要，不修）**：

- `update` 末尾（`:165-178`）无条件以新实例翻转 `ambient`/`visible`/`showIcon` → 外来同名效果 HUD 图标/粒子被**一次性**隐藏（纯外观，首次翻转后不再触发）。
- 同级药水剩余 <10t 时被拉平到 10t（轻微延长，非削弱）。

**结论**：`fix-comparison-table.md` 状态置 `已核验（不修）`。

---

### 待处理（C2 剩余）

- **C2-5** Copper 满套剥 `DIG_SLOWDOWN`（设计免疫，待产品审核）。
- **C2-6** 孤儿效果 `DYEDREAM/SCULK_ARMOR_BUFF` + `armorBuffRemove` 同模式 removeEffect。

### 📄 文档变化

- `fix-comparison-table.md`：C2-3 → `已修复`；C2-4 → `已核验（不修）`。
- 临时报告 `2026-08-04-C2-3修复报告.md` 已并入本节后移除。

---

## Ver.1 2026-08-04 · C2-1/C2-2 修复

> 前置分析：临时审查报告 [`2026-08-04-C2-review.md`](2026-08-04-C2-review.md)（原 C2 拆分为 C2-1~C2-6）

### 🔧 已修复

| ID | 变更 |
|---|---|
| C2-1 | 四个盔甲类（Sculk/Titanium/Dyedream/Copper）`inventoryTick` 增加护甲槽归属守卫，背包持有不再每 tick 触发套装检查、不再剥外来 buff |
| C2-2 | Sculk/Titanium/Dyedream 删除非满套 else 分支 `removeEffect`，不再剥药水/信标等外来同名 buff；满套仅短时效刷新、脱套自然过期 |

---

### C2-1 详细 — 盔甲 `inventoryTick` 无护甲槽归属守卫

**问题**：四个盔甲类的 `inventoryTick` 无「护甲槽归属」守卫，玩家**背包持有（主背包/副手，未穿戴）**一件对应盔甲时，每 tick 也进入 `checkAndApplySetEffect`：

- 非满套 → 执行 else-分支 `removeEffect(INVISIBILITY|NIGHT_VISION|HEALTH_BOOST)` → 药水/信标等外来同名 buff 被立刻剥掉（已实际游戏测试复现）。
- 副作用：背包持有也造成无谓的每 tick 全套检查（冗余执行）。

**根因（调用链，1.21.1 反汇编确认）**：

```
ServerPlayer.tick()
└─ this.inventory.tick()                      [Player.java:457]
     └─ Inventory.tick()                      遍历 compartments [main(36), armor(4), offhand(1)]
          └─ stack.inventoryTick(level, player, j, j==selected)      ∀非空槽
               └─ Item.inventoryTick(stack, level, owner, slot, selected)
                    └─ <对应>ArmorItem.inventoryTick(...)
                         └─ checkAndApplySetEffect(entity)            ← 未校验是否穿在护甲槽
```

- `Inventory.tick()` 遍历主背包 + 护甲 + 副手，穿与带都会触发 `inventoryTick`。
- `slot` 参数为 compartment 局部索引（主背包 0-35 / 护甲 0-3 / 副手 0），索引区间重叠，**无法用 `slot` 值区分穿/带**。
- `Player.getItemBySlot(HEAD/CHEST/LEGS/FEET)` 读的就是 `Inventory.armor` —— 与被 tick 的护甲槽对象是同一对象（可用身份比较 `==` 精确判定穿戴）。

**修复**：在四个盔甲类的 `inventoryTick` 进入 `checkAndApplySetEffect` 前增加护甲槽归属守卫：

```java
if (livingEntity.getItemBySlot(this.getType().getSlot()) != itemstack) {
    return;
}
```

- `this.getType().getSlot()` → 该件对应的护甲槽（HEAD/CHEST/LEGS/FEET）。
- 穿戴时 `getItemBySlot(护甲槽)` 返回被 tick 的同一 stack 对象 → `==` 成立 → 继续；主背包/副手中的副本是不同对象 → `==` 失败 → 提前 return。
- 采用身份 `==` 而非原版 `Iterables.contains(getArmorSlots(), itemstack)`（其用 `ItemStack.equals`，穿一件同款又带一件同款时误判触发），更精确。

**变更文件**：

| 文件 | 变更 |
|---|---|
| `PasterDream/.../item/SculkArmorItem.java` | `inventoryTick` 加归属守卫 |
| `PasterDream/.../item/TitaniumArmorItem.java` | 同上 |
| `PasterDream/.../item/DyedreamArmorItem.java` | 同上 |
| `PasterDream/.../item/CopperArmorItem.java` | 同上 |

**验证**：`.\gradlew compileJava` → BUILD SUCCESSFUL。

**修复后行为**：✅ 背包持有（主背包/副手）不再触发 `checkAndApplySetEffect`、不再剥外来 buff；✅ 实际穿戴在对应护甲槽时正常触发套装效果（逻辑不变）。

---

### C2-2 详细 — 非满套 else 分支 `removeEffect` 剥外来同名 buff

**问题**：三个盔甲类（Sculk/Titanium/Dyedream）的 `checkAndApplySetEffect` 在非满套时执行：

```java
} else {
    entity.removeEffect(MobEffects.INVISIBILITY|NIGHT_VISION|HEALTH_BOOST);
}
```

`removeEffect` 不区分效果来源，任何来源（药水/信标/他人施加）的同名效果都会被无条件剥掉：玩家喝隐身/夜视/生命提升药水（或站信标范围内），只要未穿满对应套装 → 每 tick 药水效果被清（已实际游戏测试复现）。

**根因**：`LivingEntity.removeEffect(Holder<MobEffect>)` 直接删除整条 effect 实例，无来源概念；盔甲 else-分支在「未满套」时对同名效果无条件删除，与是否为盔甲自身施加的效果无关。

**修复**：删除 else-分支的 `removeEffect`，仅保留满套时的短时效 `addEffect`：

- 满套 → 每 tick `addEffect(同名效果, 10t, 0级)` 刷新，效果持续；
- 脱套 → 不再刷新，效果在 ≤10 tick 内自然过期（约 0.5s），不剥外来效果。

（对齐 `QymArmorItem.java:33` 既有范本：仅 add、不 remove。）

**变更文件**：

| 文件 | 变更 |
|---|---|
| `PasterDream/.../item/SculkArmorItem.java` | 删除 `else removeEffect(INVISIBILITY)` |
| `PasterDream/.../item/TitaniumArmorItem.java` | 删除 `else removeEffect(NIGHT_VISION)` |
| `PasterDream/.../item/DyedreamArmorItem.java` | 删除 `else removeEffect(HEALTH_BOOST)` |

> Copper 的满套 `removeEffect(DIG_SLOWDOWN)` 属设计免疫（C2-5），本次不改动。

**验证**：`.\gradlew compileJava` → BUILD SUCCESSFUL。

**修复后行为**：✅ 非满套（穿单件/混穿/携带）不再剥药水、信标等外来同名 buff；✅ 满套套装效果正常生效（短时效刷新）。

---

### 待处理（C2 剩余）

- **C2-3** ×4 件每 tick 重复执行全套检查（无单点）。
- **C2-4** 满套 `addEffect` 无条件重加弱效果，覆盖外来更强效果。
- **C2-5** Copper 满套剥 `DIG_SLOWDOWN`（设计免疫，待产品审核）。
- **C2-6** 孤儿效果 `DYEDREAM/SCULK_ARMOR_BUFF` + `armorBuffRemove` 同模式 removeEffect。

### 📄 文档变化

- 原 C2 拆分为 C2-1~C2-6（`fix-comparison-table.md`：Critical/High 11→16、合计 48→53）。

---

## Ver.0 2026-08-03 · 多源合并整理

### 🆕 合并来源

将多份审计/计划文档合并为单一 Bug 清单，条目标注来源：

| 来源文件 | 贡献条目 |
|---|---|
| 08-03 全库深度审计| B1–B14 |
| `2026-08-02-全库多agent代码审查.md` | C1、C2、H1、H2、H3、H4、H5、M1–M8 |
| `bugfix_plan.md` | N1（工具挖掘等级，问题 3） |
| `pinyin-resource-scan.md` | N2（`geo/` 根目录模型残留） |
| `config-port-report.md` | Low 追加：配置未消费项 |

### ✅ 排除（未入清单）

| 来源 | 排除项 | 理由 |
|---|---|---|
| `bugfix_plan.md` | 问题 1/2/7 | 已修复（树叶战利品、树苗 SaplingBlock、漩涡伤害） |
| `bugfix_plan.md` | 问题 5 | 已实现（矿物生成器三矿 JSON 在场） |
| `bugfix_plan.md` | 问题 6 | 用户延后处理 |
| `config-port-report.md` | 8 项"未消费"配置 | 已被后续功能接线（MOD_ACCOUOCEMENT、NO_RETURN_DYEDREAM_CRACK、BAN_TIME_HOURGLASS 等） |
| 07-18 重构三份 | 全部 P0/P1 | `structures/`、`entity_types/`、`Class.forName`、`java.util.Random`、巨型类拆分、`check_lang.py` 均已完成 |
| 7/25 报告 #5 | — | 误报（裸 `Projectile.tick()` 不移动实体） |

### 📄 文档变化

- 主文档新增「§0 合并来源说明」：条目级来源溯源 + 排除清单。
- 主文档「汇总统计」：Medium 14→16（N1、N2）、Low 18→19（配置未消费项）、合计 45→**46**。
- 主文档「建议修复顺序」：N1 并入战斗数值组、N2 并入资源加载组。
- `fix-comparison-table.md`：建立全量修复跟踪表（状态列待更新）。

### 待办（修复后回填本文件）

- [ ] 按 `fix-comparison-table.md` 逐项修复，完成一项在表中置 `🔧 已修复` 并在本文件追加记录。
- [ ] 修复完成后更新主文档各条「核实状态」列。
