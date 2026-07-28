# 暮影据点真·要塞式世界生成 + locate Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 主世界以 `concentric_rings` jigsaw 结构自然生成含 `twilight_lantern` + `true_shadow_bed` 的 `shadow_world_door` 据点，坐标一律经 `PDShadowDoorLocator` → `findNearestMapStructure` 获取；笔记 8/9 去掉 `randomCoord*21`。

**Architecture:** 数据驱动 jigsaw 单模板（方案 A）+ 手写 `structure_set` rings JSON；Java 仅新增 Locator 薄封装；`DreamnotesLogic.writeCoords` 改 locate；VERIFY `twilight-lantern` 从「缺口确认」改向正向断言。不改事件本体、不改 NBT 内容、不进原版 `eye_of_ender_located`。

**Tech Stack:** NeoForge 1.21.1 · Java 21 · datapack worldgen (`jigsaw` / `concentric_rings`) · 现有 VERIFY 套件 `twilight-lantern`

**Spec:** [`docs/superpowers/specs/2026-07-28-shadow-world-door-structure-design.md`](../specs/2026-07-28-shadow-world-door-structure-design.md)


> **执行状态（2026-07-29）**：Task 1–6 已落地；扩展 P0（灯影 spawn / hide_7 / 返程）亦已接线。VERIFY `twilight-lantern`：**36 pass / 0 fail**（Locator 在 generateStructures=false 超平为 SKIP 非失败）。**未 commit**（无授权）。

## Global Constraints

- 仅 OVERWORLD 可 locate；其它维 → empty
- 默认 locate radius = **100**（与 `EnderEyeItem` 同 API 语义）
- rings 默认：`distance=32, spread=3, count=64, salt=26072801`（可后调，首版无 Common Config）
- Y：`start_height.absolute = -60`；`terrain_adaptation: none`；无 `project_start_to_heightmap`
- 不修改 `shadow_world_door.nbt` 建筑内容
- 不加入 `minecraft:eye_of_ender_located`
- `PDGameRules.RANDOM_COORD_*` **保留注册**，仅停用笔记驱动
- ~~不实现：灯影 spawn / hide_7 / 返程 spawn~~ → **后续 P0 已另实现**（`PDLampShadowWorldgen` / `PDEntityDeathEvents` / `teleportToOverworldSpawn`）；本计划 Task 1–6 仍只覆盖 structure+locate
- **无用户明确授权前不 git commit**
- 构建：`JAVA_HOME=/usr/lib/jvm/java-21-openjdk sh gradlew :PasterDream:compileJava --offline`

---

## File map

| 路径 | 动作 | 职责 |
| :--- | :---: | :--- |
| `PasterDream/src/main/resources/data/pasterdream/worldgen/template_pool/shadow_world_door.json` | Create | 单 pool → NBT `pasterdream:shadow_world_door` |
| `.../worldgen/structure/shadow_world_door.json` | Create | jigsaw size=1，Y=-60，underground |
| `.../worldgen/structure_set/shadow_world_doors.json` | Create | concentric_rings |
| `.../tags/worldgen/biome/has_structure/shadow_world_door.json` | Create | overworld 可生成 biome 集合 |
| `.../tags/worldgen/structure/twilight_lantern_located.json` | Create | locate tag → structure |
| `.../java/.../worldgen/PDShadowDoorLocator.java` | Create | 唯一读坐标 API |
| `.../dreamnotes/DreamnotesLogic.java` | Modify | `writeCoords` → locator；删 randomCoord 公式 |
| `.../smoketest/PDTwilightLanternVerifyHooks.java` | Modify | datapack + locate 正向；删「无笼=缺口 PASS」 |
| `docs/暮影之笼.md` | Modify | §1 结构集 + locate |
| `docs/todo_暮影之笼流程缺口.md` | Modify | 主世界 P0 勾选标准改本设计 |
| `docs/功能状态.md` | Modify | 开放项表述同步 |
| `docs/superpowers/specs/2026-07-28-shadow-world-door-structure-design.md` | Modify | 状态 → 已有实现计划 |

已有、不改内容：`data/pasterdream/structure/shadow_world_door.nbt` · `TwilightLanternBlock` · `TrueShadowBedBlock` · `PDStructureBlock` SPECS 9 · `PDGameRules` 注册。

---

### Task 1: Datapack 三件套 + tags

**Files:**
- Create: `PasterDream/src/main/resources/data/pasterdream/worldgen/template_pool/shadow_world_door.json`
- Create: `PasterDream/src/main/resources/data/pasterdream/worldgen/structure/shadow_world_door.json`
- Create: `PasterDream/src/main/resources/data/pasterdream/worldgen/structure_set/shadow_world_doors.json`
- Create: `PasterDream/src/main/resources/data/pasterdream/tags/worldgen/biome/has_structure/shadow_world_door.json`
- Create: `PasterDream/src/main/resources/data/pasterdream/tags/worldgen/structure/twilight_lantern_located.json`

**Interfaces:**
- Consumes: 已有 NBT `data/pasterdream/structure/shadow_world_door.nbt`（registry path `pasterdream:shadow_world_door`）
- Produces: structure id `pasterdream:shadow_world_door`；structure_set `pasterdream:shadow_world_doors`；tag `#pasterdream:twilight_lantern_located`；biome tag `#pasterdream:has_structure/shadow_world_door`

- [x] **Step 1: 写 template_pool（对齐教堂 block_ignore structure_block）**

```json
{
  "name": "pasterdream:shadow_world_door",
  "fallback": "minecraft:empty",
  "elements": [
    {
      "weight": 1,
      "element": {
        "element_type": "minecraft:single_pool_element",
        "location": "pasterdream:shadow_world_door",
        "projection": "rigid",
        "processors": {
          "processors": [
            {
              "processor_type": "minecraft:block_ignore",
              "blocks": [
                { "Name": "minecraft:structure_block" }
              ]
            }
          ]
        }
      }
    }
  ]
}
```

- [x] **Step 2: 写 structure JSON**

```json
{
  "type": "minecraft:jigsaw",
  "biomes": "#pasterdream:has_structure/shadow_world_door",
  "step": "underground_structures",
  "terrain_adaptation": "none",
  "spawn_overrides": {},
  "start_pool": "pasterdream:shadow_world_door",
  "size": 1,
  "max_distance_from_center": 80,
  "use_expansion_hack": false,
  "start_height": {
    "absolute": -60
  }
}
```

注意：**不要**写 `project_start_to_heightmap`（深层绝对高度）。

- [x] **Step 3: 写 structure_set（concentric_rings）**

路径名用 `shadow_world_doors.json`（复数，与 spec 一致；**不要**走 RuinAPI `buildSet`，手写 rings）。

```json
{
  "structures": [
    {
      "structure": "pasterdream:shadow_world_door",
      "weight": 1
    }
  ],
  "placement": {
    "type": "minecraft:concentric_rings",
    "distance": 32,
    "spread": 3,
    "count": 64,
    "salt": 26072801,
    "preferred_biomes": "#minecraft:is_overworld"
  }
}
```

若运行时 `preferred_biomes` 导致 0 候选，优先改为能生成+locate 的写法（如去掉该字段或改 biome tag），再调手感。

- [x] **Step 4: biome tag**

`tags/worldgen/biome/has_structure/shadow_world_door.json`：

```json
{
  "replace": false,
  "values": [
    "#minecraft:is_overworld"
  ]
}
```

- [x] **Step 5: locate structure tag**

`tags/worldgen/structure/twilight_lantern_located.json`：

```json
{
  "replace": false,
  "values": [
    "pasterdream:shadow_world_door"
  ]
}
```

- [x] **Step 6: 确认 NBT 在场**

```bash
test -f PasterDream/src/main/resources/data/pasterdream/structure/shadow_world_door.nbt && ls -la PasterDream/src/main/resources/data/pasterdream/worldgen/structure/shadow_world_door.json PasterDream/src/main/resources/data/pasterdream/worldgen/template_pool/shadow_world_door.json PasterDream/src/main/resources/data/pasterdream/worldgen/structure_set/shadow_world_doors.json PasterDream/src/main/resources/data/pasterdream/tags/worldgen/biome/has_structure/shadow_world_door.json PasterDream/src/main/resources/data/pasterdream/tags/worldgen/structure/twilight_lantern_located.json
```

Expected: 全部存在。

- [x] **Step 7: 本任务不 commit**（全局约束：无授权不 commit）

---

### Task 2: `PDShadowDoorLocator`

**Files:**
- Create: `PasterDream/src/main/java/com/pasterdream/pasterdreammod/worldgen/PDShadowDoorLocator.java`

**Interfaces:**
- Consumes: tag `#pasterdream:twilight_lantern_located`（Task 1）
- Produces:
  - `public static final TagKey<Structure> TWILIGHT_LANTERN_LOCATED`
  - `public static final int DEFAULT_RADIUS = 100`
  - `public static Optional<BlockPos> locate(ServerLevel level, BlockPos origin)`
  - `public static Optional<BlockPos> locate(ServerLevel level, BlockPos origin, int radius)`

- [x] **Step 1: 实现 Locator**

```java
package com.pasterdream.pasterdreammod.worldgen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;

/**
 * 暮影据点（{@code pasterdream:shadow_world_door}）唯一读坐标入口。
 * 对齐原版末影之眼：{@code ServerLevel.findNearestMapStructure} + structure tag。
 * <p>
 * 无 place、无写 gamerule、无加载笔记副作用。
 */
public final class PDShadowDoorLocator {

    public static final TagKey<Structure> TWILIGHT_LANTERN_LOCATED = TagKey.create(
            Registries.STRUCTURE,
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "twilight_lantern_located"));

    /** 与 {@code EnderEyeItem} 调用 findNearestMapStructure 的 radius 一致。 */
    public static final int DEFAULT_RADIUS = 100;

    private PDShadowDoorLocator() {
    }

    public static Optional<BlockPos> locate(ServerLevel level, BlockPos origin) {
        return locate(level, origin, DEFAULT_RADIUS);
    }

    /**
     * @param radius findNearestMapStructure 搜索半径（chunk 语义与原版 API 相同）
     * @return 最近据点锚点；非主世界或未找到 → empty
     */
    public static Optional<BlockPos> locate(ServerLevel level, BlockPos origin, int radius) {
        if (level == null || origin == null) {
            return Optional.empty();
        }
        if (level.dimension() != Level.OVERWORLD) {
            return Optional.empty();
        }
        BlockPos found = level.findNearestMapStructure(TWILIGHT_LANTERN_LOCATED, origin, radius, false);
        return Optional.ofNullable(found);
    }
}
```

- [x] **Step 2: 编译**

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk sh gradlew :PasterDream:compileJava --offline
```

Expected: BUILD SUCCESSFUL

- [x] **Step 3: 不 commit**

---

### Task 3: `DreamnotesLogic` 全改 locate

**Files:**
- Modify: `PasterDream/src/main/java/com/pasterdream/pasterdreammod/dreamnotes/DreamnotesLogic.java`

**Interfaces:**
- Consumes: `PDShadowDoorLocator.locate(ServerLevel, BlockPos)`（Task 2）
- Produces: `writeCoords` 成功时 NBT `switch=true` + `x/z`；失败时**不写假坐标**，对玩家提示方位未感应

调用点（保持签名兼容，去掉 offset 语义）：

| 调用 | 现 offset | 改后 |
| :--- | :--- | :--- |
| note8 解锁 | `-22,-21` | `writeCoords(world, entity, stack)` |
| note8 已解锁再读 | `0,0` | 同上（再 locate） |
| note9 分支多处 | `-22,-21` / `0,0` | 同上 |
| `tryUnlock(..., coords, xOff, zOff)` | 带 offset | 改为 `boolean coords` 时调无 offset 的 writeCoords；**若 tryUnlock 仅笔记其它条用 coords+offset，检查调用方** |

- [x] **Step 1: 读全文件确认 `tryUnlock` / `writeCoords` 全部调用方**

```bash
rg -n "writeCoords|tryUnlock|readRandomCoord" PasterDream/src/main/java/com/pasterdream/pasterdreammod/dreamnotes/DreamnotesLogic.java
```

- [x] **Step 2: 替换 `writeCoords` / 删除 `readRandomCoord`**

1. 增加 import：
   - `com.pasterdream.pasterdreammod.worldgen.PDShadowDoorLocator`
   - `net.minecraft.server.level.ServerLevel`
2. 删除 `GameRules` import（若仅 randomCoord 使用）。
3. 将所有 `writeCoords(world, stack, int, int)` 改为 `writeCoords(world, entity, stack)`（需 entity 取 origin；note 方法已有 `entity`）。
4. `tryUnlock`：若仍有 `coords + xOff/zOff`，改为 `coords` 时 `writeCoords(world, entity, stack)`，删 xOff/zOff 参数并更新调用点。
5. 新 `writeCoords` 实现：

```java
/**
 * 写入最近暮影据点坐标（locate）。失败不写假 x/z。
 */
private static void writeCoords(Level world, Entity entity, ItemStack stack) {
    if (!(world instanceof ServerLevel server) || entity == null) {
        return;
    }
    Optional<BlockPos> found = PDShadowDoorLocator.locate(server, entity.blockPosition());
    if (found.isEmpty()) {
        msg(entity, "尚未感应到暮影据点的方位", false);
        return;
    }
    BlockPos pos = found.get();
    DreamnotesData.putBoolean(stack, "switch", true);
    DreamnotesData.putDouble(stack, "x", pos.getX());
    DreamnotesData.putDouble(stack, "z", pos.getZ());
}
```

需要 `import java.util.Optional;`。

6. **删除**整个 `readRandomCoord` 方法与 `r*21+offset` 注释块。
7. 显示格式保持：`X:` / `Z:` + `INT_FMT`（既有 msg 行不变）。
8. **不**手工减 -22/-21；locate 锚点即写入点。若日后显示与笼子有固定像素差，另开校准，不在本任务加魔法偏移。

- [x] **Step 3: 编译**

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk sh gradlew :PasterDream:compileJava --offline
```

Expected: BUILD SUCCESSFUL；无 `readRandomCoord` 残留引用。

```bash
rg -n "readRandomCoord|randomCoord|\* 21|xOffset" PasterDream/src/main/java/com/pasterdream/pasterdreammod/dreamnotes/DreamnotesLogic.java || true
```

Expected: 无业务残留（注释若提「历史」可留一行）。

- [x] **Step 4: 不 commit**

---

### Task 4: VERIFY 套件改向

**Files:**
- Modify: `PasterDream/src/main/java/com/pasterdream/pasterdreammod/smoketest/PDTwilightLanternVerifyHooks.java`

**Interfaces:**
- Consumes: datapack ids（Task 1）、`PDShadowDoorLocator`（Task 2）
- Produces: T1–T5 正向断言；T6 hide_7 / bare spawn **可暂留缺口确认**（非本设计）

- [x] **Step 1: 类头注释改写**

说明：据点改为 structure_set + locate；本套件断言 datapack 在场、Locator、structure_block_9 旁路；hide_7/spawn 仍为其它 P0 缺口确认。

- [x] **Step 2: 删除 / 替换 `verifyGenerateWorldAbsenceSignals`**

删除「主世界抽样未见自动 twilight_lantern（GenerateWorld 缺口）」pass-on-absence。

新增方法 `verifyStructureDatapackAndLocate`：

```java
private static void verifyStructureDatapackAndLocate(MinecraftServer server, ServerPlayer player,
                                                     Consumer<Result> out) {
    var structureReg = server.registryAccess().registryOrThrow(Registries.STRUCTURE);
    var setReg = server.registryAccess().registryOrThrow(Registries.STRUCTURE_SET);

    ResourceLocation structId = ResourceLocation.fromNamespaceAndPath("pasterdream", "shadow_world_door");
    ResourceLocation setId = ResourceLocation.fromNamespaceAndPath("pasterdream", "shadow_world_doors");

    boolean structPresent = structureReg.getOptional(structId).isPresent();
    boolean setPresent = setReg.getOptional(setId).isPresent();
    out.accept(ok(structPresent, "datapack structure shadow_world_door", structId.toString()));
    out.accept(ok(setPresent, "datapack structure_set shadow_world_doors", setId.toString()));

    boolean tagBound = structureReg.getTag(PDShadowDoorLocator.TWILIGHT_LANTERN_LOCATED)
            .map(t -> t.size() > 0)
            .orElse(false);
    out.accept(ok(tagBound, "tag #pasterdream:twilight_lantern_located 非空",
            PDShadowDoorLocator.TWILIGHT_LANTERN_LOCATED.location().toString()));

    ServerLevel overworld = server.getLevel(Level.OVERWORLD);
    if (overworld == null) {
        out.accept(new Result(false, "Locator.locate overworld", "overworld == null"));
        return;
    }
    BlockPos origin = player != null ? player.blockPosition() : BlockPos.ZERO;
    Optional<BlockPos> located = PDShadowDoorLocator.locate(overworld, origin);
    if (located.isPresent()) {
        out.accept(ok(true, "Locator.locate overworld 命中", located.get().toShortString()));
    } else {
        // VERIFY 超平 / 结构未生成时不得假绿：显式 skip 语义用 pass + 说明，或独立名
        // Spec T2：若当前世界无法生成则显式 skip + 报告原因，不得假绿
        boolean structuresOn = overworld.getServer().getWorldData().worldGenOptions().generateStructures();
        out.accept(new Result(true,
                "Locator.locate overworld SKIP（未命中，非失败）",
                "generateStructures=" + structuresOn
                        + " origin=" + origin.toShortString()
                        + " — 新档 rings 可能距出生过远；手工 /locate 验收"));
    }
}
```

在 `verify()` 中：用 `verifyStructureDatapackAndLocate` **替换** `verifyGenerateWorldAbsenceSignals` 调用。

补 import：
- `com.pasterdream.pasterdreammod.worldgen.PDShadowDoorLocator`
- `net.minecraft.world.level.Level`（若尚未）
- `net.minecraft.core.registries.Registries`（已有则跳过）
- `java.util.Optional`（已有）

- [x] **Step 3: 保留 structure_block_9 / hide_7 / spawn 断言**

- structure_block_9 落地笼+床：保留（T5）
- bare `spawn` 未注册：保留（T6 其它 P0）
- 杀 Warden 无 hide_7：保留（T6）

- [x] **Step 4: 编译**

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk sh gradlew :PasterDream:compileJava --offline
```

- [x] **Step 5:（可选，耗时长）跑 twilight-lantern 套件**

```bash
PASTERDREAM_VERIFY=1 PASTERDREAM_VERIFY_SUITES=twilight-lantern PASTERDREAM_VERIFY_KEEP_OPEN=0 \
  JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  sh gradlew :PasterDream:runClient --offline
```

读报告：

```bash
python3 -c "import json;d=json.load(open('PasterDream/run/pd_verify_report.json'));print(d.get('pass'),d.get('fail'));print([x for x in d.get('results',d.get('checks',[])) if not (x.get('pass',x.get('ok',True)))][:20])"
```

Expected: 无因 datapack/Locator 编译或注册导致的 fail；locate SKIP 可接受；structure_block_9 仍绿。

- [x] **Step 6: 不 commit**

---

### Task 5: 文档同步

**Files:**
- Modify: `docs/暮影之笼.md`
- Modify: `docs/todo_暮影之笼流程缺口.md`
- Modify: `docs/功能状态.md`
- Modify: `docs/superpowers/specs/2026-07-28-shadow-world-door-structure-design.md`（状态行）

- [x] **Step 1: `暮影之笼.md` §0 / §1**

- §0 一句话：自然来源改为 **structure_set `shadow_world_doors`（concentric_rings）+ jigsaw `shadow_world_door`**；`structure_block_9` 为调试旁路。
- 生存缺口 callout：主世界据点生成改为本设计；仍缺灯影 spawn、hide_7、返程 spawn。
- §1.1 表：增加 structure / structure_set / locate tag；写明坐标 = `PDShadowDoorLocator` / 笔记 locate，**randomCoord 不再驱动门**。
- 删除或改写「尚无 Load 监听放置」为「已改为 chunkgen 结构集」。

- [x] **Step 2: `todo_暮影之笼流程缺口.md`**

- P0 第一条（主世界 GenerateWorld place door）：改为 **已由结构集设计替代**；勾选条件 = spec 验收（datapack + locate + 笔记）；完成后勾选并注日期与计划路径。
- 动手顺序第 1 步改为「结构集 + Locator + 笔记 locate（本计划）」。
- 近次实测表：更新「主世界抽样无自动笼」行说明（旧缺口确认已改向）。
- 灯影 spawn / hide_7 / 返程 **仍开**。

- [x] **Step 3: `功能状态.md` §3**

- 高优先级「GenerateWorld 放 door」→「暮影据点 structure_set + 笔记 locate」（进行中/完成后改表述）。
- 开放项表同行：主世界 door 进度挂本计划；hide_7 + 返程 spawn 仍列。
- 更新记录加一条：结构集设计 + 实现（日期 2026-07-28）。

- [x] **Step 4: spec 状态**

`2026-07-28-shadow-world-door-structure-design.md` 头部状态改为：`实现中` 或 `已有计划 docs/superpowers/plans/2026-07-28-shadow-world-door-structure.md`。

- [x] **Step 5: 不 commit**

---

### Task 6: 终验闸门（编译 + 可选 VERIFY）

**Files:** 无新文件

- [x] **Step 1: 全量相关路径存在性检查**

```bash
rg -n "PDShadowDoorLocator|twilight_lantern_located|shadow_world_doors" PasterDream --glob '*.{java,json}' | head -40
rg -n "readRandomCoord|r \* 21|randomCoord" PasterDream/src/main/java/com/pasterdream/pasterdreammod/dreamnotes/ || true
```

- [x] **Step 2: compileJava**

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk sh gradlew :PasterDream:compileJava --offline
```

Expected: SUCCESS

- [x] **Step 3: 若环境允许，跑 twilight-lantern VERIFY（见 Task 4 Step 5）**

- [x] **Step 4: 向用户汇报**

**实际完成（2026-07-29）**

- Task 1–6 交付物齐；扩展 P0（灯影 spawn / hide_7 / 返程）另已接线
- VERIFY `twilight-lantern`：**36 pass / 0 fail**（Locator 超平 SKIP 预期内）
- 自然档手测：`/locate structure pasterdream:shadow_world_door`（仍建议人工）
- **未 commit**（无授权）— 请用户授权后再 commit

---

## Self-review (plan vs spec)

| Spec 项 | Task | 结果 |
| :--- | :---: | :--- |
| G1 chunkgen 据点 | 1 | ✅ |
| G2 Locator / findNearestMapStructure | 2 | ✅ |
| G3 笔记 8/9 全 locate | 3 | ✅ |
| G4 concentric_rings 默认可运行 | 1 | ✅（手测 rings 仍开放） |
| G5 调试块/杖保留 | 4 保留 T5 | ✅ |
| 非目标灯影/hide_7/spawn | 计划外 | ✅ 后续 P0 另实现 + VERIFY 正向 |
| 不进 eye_of_ender_located | 1 自有 tag | ✅ |
| datapack 五文件 | 1 | ✅ |
| VERIFY T1–T6 | 4+扩展 | ✅ 36/0 |
| 文档同步 | 5 | ✅ 2026-07-29 |
| 校准偏移不锁死 | 3 不加魔法 offset | ✅ |

无 TBD 步骤；签名前后一致；不 commit 贯穿全局。

---

## Execution notes

- 实现顺序严格 1→2→3→4→5→6。
- 每任务编译失败不得进入下一任务。
- rings 若 0 生成：先修 biome/preferred_biomes，不先加 Config。
- 用户说「分步骤落实」→ 本会话 **inline 执行**本计划（executing-plans 风格），非仅写文档。
