# TODO · 第二梦境（灯影 · 开放项）

> **类别**：活文档 · 待办  
> **日期**：2026-07-29  
> **玩法参考** → [`第二梦境.md`](第二梦境.md)  
> **功能总览** → [`功能状态.md`](功能状态.md)  
> **前置入口** → [`todo_暮影之笼流程缺口.md`](todo_暮影之笼流程缺口.md)

灯影内主干（地牢门钥、竞技场 d_0/e_0、LampShadow title/窥视、Pale 三围、terrorbeak、GUARD、无名 inflate 16、唯一胜利倒计时、右键开箱/未开箱补发）与 VERIFY `second-dream` **57/0** 已收口。  
**本文件不再挂代码缺口清单。** 生存进灯影仍依赖暮影据点/自然档，开放项只在暮影 todo。

---

## 仍相关（非本弧代码缺口）

| 依赖 | 说明 |
| :--- | :--- |
| 暮影自然档 / 可选抛光 | `/locate shadow_world_door`、笔记坐标、`number`/ANIMATION/rings → [`todo_暮影之笼流程缺口.md`](todo_暮影之笼流程缺口.md) |
| 人工游玩回归 | 真影床→研究/地牢→无名→抉择→竞技场整链手感 → [`功能状态.md` §3](功能状态.md) |
| 图鉴 vs 配置 | 进灯影赠骨针：配置默认 **false**，图鉴写「会被给予」— 产品择一即可 |

---

## 复测（改灯影/竞技场代码后）

```bash
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=second-dream PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

别名：`second` / `lamp-shadow`。**不在** `all`。报告：`PasterDream/run/pd_verify_report.json`。

```mcfunction
/advancement grant @s only pasterdream:achievement_shadow_start
/execute in pasterdream:lamp_shadow_world run tp @s 0.5 104 0.5
/advancement grant @s only pasterdream:achievement_shadow_d_0
/execute in pasterdream:aaroncos_arena_world run tp @s 0.5 70 0.5
```

---

## 改时锚点

| 用途 | 路径 |
| :--- | :--- |
| 地牢门/钥 | `block/ShadowDungeonDoorBlock.java` · `ShadowDungeonKeyBlock.java` |
| 完好/破损门 | `ShadowDungeonPortalBlock` · `BrokenShadowDungeonProtalBlock` |
| 进/离灯影 | `registry/LampShadowEvents.java` |
| 无名 / 入侵 | `entity/mob/ShadowNpc0Entity.java` · `PDEffects` |
| 抉择 | `TrueShadowBedBlock` · `ShadowSelectEndMenu` |
| 竞技场 | `AaroncosArenaPortalsBlock` · `PDArenaEvents` · `PDArenaBossManager` |
| 战利品箱 | `AaroncosHandChestBlock` · `AaroncosHandChestBlockEntity` |
| 骨针 | `PaleBoneneedleItem` · `RootsPaleBoneneedleItem` |
| VERIFY | `smoketest/PDSecondDreamVerifyHooks.java` |
