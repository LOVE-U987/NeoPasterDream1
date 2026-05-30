# CUSTOM 装饰物生成器 + 倒塌冰门 实施计划

> **For agentic workers:** Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为 WorldDecorationAPI 实现 CUSTOM 类型扩展点，并利用它创建倒塌冰门装饰物

**Architecture:** 
- API 层：ICustomDecorationGenerator 接口 → DecorationConfig 新增 customGeneratorKey → DecorationRegistry 管理生成器注册表 → GenericDecorationFeature.placeCustom() 调度
- 应用层：FallenIceGateGenerator 实现接口，包含地形检测 + 倾斜/断裂两种倒塌形态 + 横梁坠落碎片

**Tech Stack:** NeoForge 1.21.1, Minecraft WorldGen API

---

### Task 1: 创建 ICustomDecorationGenerator 接口

**Files:**
- Create: `src/main/java/com/pasterdream/pasterdreammod/worldgen/decor/ICustomDecorationGenerator.java`

- [ ] **Step 1: 创建接口文件**

```java
package com.pasterdream.pasterdreammod.worldgen.decor;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * 自定义装饰物生成器接口 —— 为 WorldDecorationAPI 提供 CUSTOM 类型扩展点
 * <p>
 * 实现此接口并注册到 {@link DecorationRegistry#registerCustomGenerator(String, ICustomDecorationGenerator)}，
 * 即可通过 DecorationBuilder 的 CUSTOM 类型使用自定义生成逻辑，
 * 享受 API 的 Builder 链式配置和 JSON 自动生成能力。
 */
@FunctionalInterface
public interface ICustomDecorationGenerator {

    /**
     * 执行自定义装饰物生成逻辑
     *
     * @param context 特征放置上下文（含 level, random, config, origin）
     * @return 是否放置了至少一个方块
     */
    boolean generate(FeaturePlaceContext<DecorationConfig> context);
}
```

---

### Task 2: DecorationConfig 新增 customGeneratorKey 字段

**Files:**
- Modify: `src/main/java/com/pasterdream/pasterdreammod/worldgen/decor/DecorationConfig.java`

- [ ] **Step 1: record 定义新增 customGeneratorKey 参数**

在 `replaceable` 之后添加 `String customGeneratorKey`：

```java
    @Nullable BlockPredicate replaceable,
    String customGeneratorKey
```

- [ ] **Step 2: CODEC encode 新增 custom_generator_key 编码**

在 `replaceable` 编码块之后添加：

```java
            if (config.customGeneratorKey() != null && !config.customGeneratorKey().isEmpty()) {
                prefix.add("custom_generator_key", Codec.STRING.encodeStart(ops, config.customGeneratorKey()));
            }
```

- [ ] **Step 3: CODEC decode 新增 custom_generator_key 解码**

在 `replaceable` 解码行之后添加：

```java
            DataResult<String> customGeneratorKey = decodeOptional(ops, input, "custom_generator_key", Codec.STRING, "");
```

并在构造调用末尾传入 `customGeneratorKey.getOrThrow()`。

- [ ] **Step 4: keys 方法添加 custom_generator_key**

---

### Task 3: DecorationBuilder 新增 customGenerator() 方法

**Files:**
- Modify: `src/main/java/com/pasterdream/pasterdreammod/worldgen/decor/DecorationBuilder.java`

- [ ] **Step 1: 新增字段**

```java
    /** 自定义生成器键（CUSTOM 类型专用） */
    private String customGeneratorKey = "";
```

- [ ] **Step 2: 新增链式方法**

```java
    public DecorationBuilder customGenerator(String key) {
        this.customGeneratorKey = key;
        return this;
    }
```

- [ ] **Step 3: register() 方法中传入新字段**

在 `DecorationConfig` 构造调用的末尾（`replaceable` 之后）添加 `customGeneratorKey`。

---

### Task 4: DecorationRegistry 新增生成器注册表

**Files:**
- Modify: `src/main/java/com/pasterdream/pasterdreammod/worldgen/decor/DecorationRegistry.java`
- Import: `java.util.HashMap`, `java.util.Map`

- [ ] **Step 1: 添加字段和方法**

```java
    /** 自定义装饰物生成器注册表 */
    private static final Map<String, ICustomDecorationGenerator> CUSTOM_GENERATORS = new HashMap<>();

    /**
     * 注册自定义装饰物生成器
     *
     * @param key       生成器键名（对应配置中的 customGeneratorKey）
     * @param generator 生成器实现
     */
    public static void registerCustomGenerator(String key, ICustomDecorationGenerator generator) {
        CUSTOM_GENERATORS.put(key, generator);
        PasterDreamMod.LOGGER.info("[DecorationRegistry] 已注册自定义生成器: {}", key);
    }

    /**
     * 获取自定义装饰物生成器
     *
     * @param key 生成器键名
     * @return 生成器实例，未找到则返回 null
     */
    @Nullable
    public static ICustomDecorationGenerator getCustomGenerator(String key) {
        return CUSTOM_GENERATORS.get(key);
    }
```

- [ ] **Step 2: 添加 import**

添加 `import javax.annotation.Nullable;` 和 `import java.util.HashMap;`、`import java.util.Map;`。

---

### Task 5: GenericDecorationFeature.placeCustom() 实现

**Files:**
- Modify: `src/main/java/com/pasterdream/pasterdreammod/worldgen/decor/GenericDecorationFeature.java`

- [ ] **Step 1: 替换 placeCustom() 实现**

```java
    /**
     * 自定义生成：从配置中获取 customGeneratorKey，查找已注册的生成器并调度
     * <p>
     * 通过 {@link DecorationRegistry#getCustomGenerator(String)} 查找生成器，
     * 如果未设置 key 或未找到对应生成器则返回 false。
     *
     * @param context 特征放置上下文
     * @return 是否生成成功
     */
    private boolean placeCustom(FeaturePlaceContext<DecorationConfig> context) {
        DecorationConfig config = context.config();
        String key = config.customGeneratorKey();
        if (key == null || key.isEmpty()) {
            return false;
        }
        ICustomDecorationGenerator generator = DecorationRegistry.getCustomGenerator(key);
        if (generator == null) {
            return false;
        }
        return generator.generate(context);
    }
```

---

### Task 6: 创建 FallenIceGateGenerator

**Files:**
- Create: `src/main/java/com/pasterdream/pasterdreammod/worldgen/FallenIceGateGenerator.java`

- [ ] **Step 1: 创建完整生成器类**

```java
package com.pasterdream.pasterdreammod.worldgen;

import com.pasterdream.pasterdreammod.worldgen.decor.DecorationConfig;
import com.pasterdream.pasterdreammod.worldgen.decor.ICustomDecorationGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.HashSet;
import java.util.Set;

/**
 * 倒塌冰门自定义生成器 —— 当冰门的"另一半"因地形无法生成时触发
 * <p>
 * 两种倒塌形态：
 * <ul>
 *   <li>🅰️ 倾斜版：柱子像比萨斜塔一样阶梯式倾斜 15~45°，每 3~4 格偏移 1 格</li>
 *   <li>🅱️ 断柱版：柱子 40~70% 高度处折断，上半截横躺在地上</li>
 * </ul>
 * 横梁从柱顶"掉下"，在柱子底部到对面方向散落成碎片。
 */
public class FallenIceGateGenerator implements ICustomDecorationGenerator {

    @Override
    public boolean generate(FeaturePlaceContext<DecorationConfig> context) {
        DecorationConfig config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // 1. 找当前柱子根部的坚实地面
        int myGroundY = WorldGenUtils.findGroundY(level, config.replaceable(),
                origin.getX(), origin.getY(), origin.getZ(), 8);
        if (myGroundY == Integer.MIN_VALUE) {
            return false;
        }

        // 2. 随机决定倒塌方向（左/右）和半边宽度
        boolean goLeft = random.nextBoolean();
        int halfWidth = random.nextIntBetweenInclusive(2, 6);
        int otherX = goLeft ? origin.getX() - halfWidth : origin.getX() + halfWidth;

        // 3. 检测"对面"地形 —— 如果对面适合立柱子，则放弃生成
        int otherGroundY = WorldGenUtils.findGroundY(level, config.replaceable(),
                otherX, myGroundY + 4, origin.getZ(), 8);
        boolean otherIsValid = otherGroundY != Integer.MIN_VALUE
                && Math.abs(otherGroundY - myGroundY) <= 4
                && WorldGenUtils.isSolidSurface(level, new BlockPos(otherX, otherGroundY, origin.getZ()));

        if (otherIsValid) {
            // 对面可以立柱子 → 说明另一半没倒 → 这个倒塌冰门不生成了
            return false;
        }

        // 4. 确定倒塌方向偏移方向
        int tiltDirX = goLeft ? -1 : 1;
        int height = random.nextIntBetweenInclusive(10, 25);
        int topY = myGroundY + height;

        // 5. 随机选倒塌形态
        boolean isTilted = random.nextBoolean();

        Set<BlockPos> placedPositions = new HashSet<>();
        boolean placedAny = false;

        if (isTilted) {
            placedAny = placeTilted(level, random, config, origin.getX(), origin.getZ(),
                    myGroundY, topY, tiltDirX, placedPositions);
        } else {
            placedAny = placeBroken(level, random, config, origin.getX(), origin.getZ(),
                    myGroundY, topY, tiltDirX, placedPositions);
        }

        // 6. 横梁碎片：从柱子底部向对面方向散落
        placedAny |= scatterBeamDebris(level, random, config,
                origin.getX(), origin.getZ(), myGroundY, tiltDirX, halfWidth);

        return placedAny;
    }

    /**
     * 生成倾斜版倒塌冰门
     * <p>
     * 柱子阶梯式向对面方向倾斜，每 3~5 格偏移 1 格，
     * 偏移角度在 15°~45° 之间（由高度和偏移量共同决定）。
     */
    private boolean placeTilted(WorldGenLevel level, RandomSource random, DecorationConfig config,
                                 int centerX, int centerZ, int groundY, int topY,
                                 int tiltDir, Set<BlockPos> placedPositions) {
        int tiltInterval = random.nextIntBetweenInclusive(3, 5);
        int accumulatedOffset = 0;
        boolean placedAny = false;

        for (int y = groundY; y <= topY; y++) {
            int relativeY = y - groundY;
            if (relativeY > 0 && relativeY % tiltInterval == 0) {
                accumulatedOffset += 1;
            }

            float progress = (float) (y - groundY) / (float) (topY - groundY);
            int radius = Math.max(1, Math.round(3 + (1 - 3) * progress));

            int pillarX = centerX + accumulatedOffset * tiltDir;

            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    float distSq = dx * dx + dz * dz;
                    if (distSq > (radius + 0.5f) * (radius + 0.5f)) {
                        continue;
                    }

                    BlockPos pos = new BlockPos(pillarX + dx, y, centerZ + dz);
                    if (!WorldGenUtils.isReplaceable(level, config.replaceable(), pos)) {
                        continue;
                    }

                    if (y > groundY) {
                        boolean supported = hasSupport(level, pos, placedPositions);
                        if (!supported) {
                            continue;
                        }
                    }

                    BlockState state = config.bodyBlock().getState(random, pos);
                    level.setBlock(pos, state, 3);
                    placedPositions.add(pos);
                    placedAny = true;
                }
            }
        }
        return placedAny;
    }

    /**
     * 生成断裂版倒塌冰门
     * <p>
     * 柱子从高度 40%~70% 处折断，上半截向对面方向倒下，
     * 断裂点以上方块逐层下放到地面，形成横躺的柱身。
     */
    private boolean placeBroken(WorldGenLevel level, RandomSource random, DecorationConfig config,
                                 int centerX, int centerZ, int groundY, int topY,
                                 int tiltDir, Set<BlockPos> placedPositions) {
        int height = topY - groundY;
        int breakPoint = groundY + (int)(height * (0.4 + random.nextDouble() * 0.3));
        int radius = 3;
        boolean placedAny = false;

        // 断裂点以下：正常垂直柱子（部分断裂）
        for (int y = groundY; y <= breakPoint; y++) {
            float progress = (float) (y - groundY) / (float) (topY - groundY);
            int currentRadius = Math.max(1, Math.round(3 + (1 - 3) * progress));

            // 断裂点附近缩小截面，模拟断裂面
            if (y >= breakPoint - 1) {
                currentRadius = Math.max(1, currentRadius - 1);
            }

            for (int dx = -currentRadius; dx <= currentRadius; dx++) {
                for (int dz = -currentRadius; dz <= currentRadius; dz++) {
                    float distSq = dx * dx + dz * dz;
                    if (distSq > (currentRadius + 0.5f) * (currentRadius + 0.5f)) {
                        continue;
                    }

                    BlockPos pos = new BlockPos(centerX + dx, y, centerZ + dz);
                    if (!WorldGenUtils.isReplaceable(level, config.replaceable(), pos)) {
                        continue;
                    }

                    if (y > groundY) {
                        boolean supported = hasSupport(level, pos, placedPositions);
                        if (!supported) {
                            continue;
                        }
                    }

                    BlockState state = config.bodyBlock().getState(random, pos);
                    level.setBlock(pos, state, 3);
                    placedPositions.add(pos);
                    placedAny = true;
                }
            }
        }

        // 断裂点以上：倒塌的柱身，从断裂点逐层"落到"地面
        int fallenLength = topY - breakPoint;
        int fallDirX = tiltDir;
        for (int layer = 0; layer < fallenLength; layer++) {
            int y = groundY + layer; // 从地面开始逐层放倒
            int offsetX = layer * fallDirX; // 每层向对面方向偏移1格

            // 越往外半径越小（模拟碎开）
            int currentRadius = Math.max(1, radius - layer / 2);

            for (int dx = -currentRadius; dx <= currentRadius; dx++) {
                for (int dz = -currentRadius; dz <= currentRadius; dz++) {
                    float distSq = dx * dx + dz * dz;
                    if (distSq > (currentRadius + 0.5f) * (currentRadius + 0.5f)) {
                        continue;
                    }

                    BlockPos pos = new BlockPos(centerX + offsetX + dx, y, centerZ + dz);
                    if (!WorldGenUtils.isReplaceable(level, config.replaceable(), pos)) {
                        continue;
                    }

                    // 检查下方支撑
                    if (!WorldGenUtils.isSolidSurface(level, pos.below())
                            && !placedPositions.contains(pos.below())) {
                        continue;
                    }

                    // 断裂面的方块随机保留部分，制造碎裂效果
                    if (layer < fallenLength - 1 && random.nextFloat() < 0.85f) {
                        BlockState state = config.bodyBlock().getState(random, pos);
                        level.setBlock(pos, state, 3);
                        placedPositions.add(pos);
                        placedAny = true;
                    }
                }
            }
        }

        return placedAny;
    }

    /**
     * 散落横梁碎片
     * <p>
     * 在柱子底部到对面方向 5~12 格范围内散落冰块，
     * 靠近柱子处密集，远离处稀疏。
     */
    private boolean scatterBeamDebris(WorldGenLevel level, RandomSource random, DecorationConfig config,
                                       int centerX, int centerZ, int groundY,
                                       int tiltDir, int halfWidth) {
        boolean placedAny = false;
        int debrisCount = random.nextIntBetweenInclusive(8, 15);
        int maxRange = random.nextIntBetweenInclusive(5, 12);

        for (int i = 0; i < debrisCount; i++) {
            double distanceFactor = random.nextDouble();
            int dx = (int) (distanceFactor * maxRange * tiltDir);
            // 靠近柱子处密集：distanceFactor 小的时随机偏移小
            int dz = (int) ((random.nextDouble() - 0.5) * (2 + distanceFactor * 3));

            BlockPos debrisPos = new BlockPos(centerX + dx, groundY, centerZ + dz);

            if (WorldGenUtils.isReplaceable(level, config.replaceable(), debrisPos)) {
                boolean hasSupport = WorldGenUtils.isSolidSurface(level, debrisPos.below())
                        || placedPositionsContains(level, debrisPos.below());
                if (hasSupport) {
                    BlockState state = config.bodyBlock().getState(random, debrisPos);
                    level.setBlock(debrisPos, state, 3);
                    placedAny = true;
                }
            }
        }
        return placedAny;
    }

    /**
     * 检查指定位置下方是否有支撑（固体地面或已放置方块）
     */
    private boolean hasSupport(WorldGenLevel level, BlockPos pos, Set<BlockPos> placedPositions) {
        BlockPos below = pos.below();
        if (placedPositions.contains(below)) {
            return true;
        }
        return WorldGenUtils.isSolidSurface(level, below);
    }

    /**
     * 简易版本：检查下方是否有已放置的方块（用于碎片散落）
     */
    private boolean placedPositionsContains(WorldGenLevel level, BlockPos pos) {
        return !level.getBlockState(pos).isAir();
    }
}
```

---

### Task 7: 注册倒塌冰门

**Files:**
- Modify: `src/main/java/com/pasterdream/pasterdreammod/registry/ModDecorations.java`

- [ ] **Step 1: 在 register() 方法中添加 registerFallenIceGate() 调用**

```java
registerFallenIceGate();
```

- [ ] **Step 2: 添加注册方法**

加到 `registerSeaIceMound()` 后面：

```java
    /**
     * 注册倒塌冰门装饰物
     * <p>
     * 使用 API 的 CUSTOM 类型 + 自定义生成器 ICustomDecorationGenerator，
     * 当冰门的"另一半"因地形无法生成时，生成倒塌形态（倾斜/断裂两种变种）。
     */
    private static void registerFallenIceGate() {
        SimpleWeightedRandomList<BlockState> bodyList = SimpleWeightedRandomList.<BlockState>builder()
                .add(Blocks.ICE.defaultBlockState(), 40)
                .add(Blocks.PACKED_ICE.defaultBlockState(), 35)
                .add(Blocks.BLUE_ICE.defaultBlockState(), 25)
                .build();

        // 注册自定义生成器
        DecorationRegistry.registerCustomGenerator("fallen_ice_gate", new FallenIceGateGenerator());

        // 用 Builder 注册装饰物
        DecorationBuilder.create()
                .type(DecorationType.CUSTOM)
                .body(new WeightedStateProvider(bodyList))
                .customGenerator("fallen_ice_gate")
                .height(10, 25)
                .checkHang(false)
                .biome("pasterdream:biome_dyedream_3")
                .rarity(7)
                .step(GenerationStep.Decoration.TOP_LAYER_MODIFICATION)
                .register("fallen_ice_gate");
    }
```

- [ ] **Step 3: 添加 import**

```java
import com.pasterdream.pasterdreammod.worldgen.FallenIceGateGenerator;
import com.pasterdream.pasterdreammod.worldgen.decor.ICustomDecorationGenerator;
```

---

### Task 8: 创建 JSON 文件

- [ ] **Step 1: configured_feature JSON**

`src/main/resources/data/pasterdream/worldgen/configured_feature/fallen_ice_gate.json`:
```json
{
  "type": "pasterdream:generic_decor",
  "config": {
    "type": "CUSTOM",
    "body_block": {
      "type": "minecraft:weighted_state_provider",
      "entries": [
        { "weight": 40, "data": { "Name": "minecraft:ice" } },
        { "weight": 35, "data": { "Name": "minecraft:packed_ice" } },
        { "weight": 25, "data": { "Name": "minecraft:blue_ice" } }
      ]
    },
    "min_height": 10,
    "max_height": 25,
    "check_hang": false,
    "custom_generator_key": "fallen_ice_gate"
  }
}
```

- [ ] **Step 2: placed_feature JSON**

`src/main/resources/data/pasterdream/worldgen/placed_feature/fallen_ice_gate.json`:
```json
{
  "feature": "pasterdream:fallen_ice_gate",
  "placement": [
    { "type": "minecraft:rarity_filter", "chance": 7 },
    { "type": "minecraft:in_square" },
    { "type": "minecraft:heightmap", "heightmap": "MOTION_BLOCKING" },
    { "type": "minecraft:biome" }
  ]
}
```

---

### Task 9: 更新 Biome Modifier

- [ ] **Step 1: 修改 biome_modifier**

在 `dyedream_frozen_ocean_features.json` 中添加 `fallen_ice_gate`：

```json
{
  "type": "neoforge:add_features",
  "biomes": "pasterdream:biome_dyedream_3",
  "features": [
    "pasterdream:ice_spike",
    "pasterdream:ice_gate",
    "pasterdream:fallen_ice_gate"
  ],
  "step": "top_layer_modification"
}
```

---

### Task 10: 编译检查

- [ ] **Step 1: 编译**

Run: `gradlew build`
Expected: BUILD SUCCESSFUL
