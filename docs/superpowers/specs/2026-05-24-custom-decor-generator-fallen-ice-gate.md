# 自定义装饰物生成器 + 倒塌冰门 设计文档

> **日期**: 2026-05-24
> **状态**: 已批准

## 概述

两层改动：
1. **API 层**：将 `DecorationType.CUSTOM` 从空壳实现为真正的扩展点，通过 `ICustomDecorationGenerator` 接口 + 注册表机制，允许用户注册任意复杂度的装饰物生成逻辑，并享受 API 的 Builder 和 JSON 自动生成能力
2. **应用层**：利用 CUSTOM 扩展实现倒塌冰门（FallenIceGate），当冰门的"另一半"因地形无法生成时，这根孤独的柱子会以倾斜/断裂形态倒塌

## API 改动

### ICustomDecorationGenerator 接口

```java
@FunctionalInterface
public interface ICustomDecorationGenerator {
    boolean generate(FeaturePlaceContext<DecorationConfig> context);
}
```

### DecorationConfig 新增字段

新增 `String customGeneratorKey` 字段（CUSTOM 类型专用，默认空字符串），CODEC encode/decode 时需同步处理。

### DecorationBuilder 新增方法

```java
public DecorationBuilder customGenerator(String key) {
    this.customGeneratorKey = key;
    return this;
}
```

### DecorationRegistry 新增静态注册表

```java
private static final Map<String, ICustomDecorationGenerator> CUSTOM_GENERATORS = new HashMap<>();

public static void registerCustomGenerator(String key, ICustomDecorationGenerator generator) {
    CUSTOM_GENERATORS.put(key, generator);
}

public static ICustomDecorationGenerator getCustomGenerator(String key) {
    return CUSTOM_GENERATORS.get(key);
}
```

### GenericDecorationFeature.placeCustom()

从当前实现 `return false` 改为：
```java
String key = config.customGeneratorKey();
if (key == null || key.isEmpty()) return false;
ICustomDecorationGenerator generator = DecorationRegistry.getCustomGenerator(key);
if (generator == null) return false;
return generator.generate(context);
```

## 倒塌冰门设计

### 地形检测（"另一半有没有生成"）

倒塌冰门在 `origin` 位置尝试生成时，会检查**对面柱子应该站的位置**的地形：
- 对面位置定位：`(origin.x ± halfWidth, origin.z)`
- 半边宽度：随机 2~6 格
- 用 `findGroundY` 查找对面是否有坚实地面
- 再检查对面的地形**可接受性**：
  - 对面如果是水域 → 柱子立不住 ✅ → 倒塌
  - 对面地面 Y 与当前底座 Y 差值 > 4 → 陡坡 ✅ → 倒塌
  - 对面有坚实地面且高度差 ≤ 4 → 可能立得住 ❌ → 放弃生成

### 倾斜版本（🅰️）

- 柱子从地面开始，总高度 10~25 格
- 每 3~5 格向**对面方向**偏移 1 格
- 偏移量 = `(y - baseY) * tan(angle)`，angle 在 15°~45° 间随机
- 圆形截面，半径从 3 渐变为 1~2
- 柱子每个方块检查支撑（坠云），避免悬空

### 断柱版本（🅱️）

- 柱子从地面开始，总高度 10~20 格
- 在高度 40%~70% 处随机选断裂点
- 断裂点以下：正常垂直柱子
- 断裂点以上：柱子方块向对面方向"倒下"，从断裂点高度逐层下放到地面
- 横截面从圆形变为不规则碎片

### 横梁碎片

- 横梁从柱顶"掉下"
- 在柱子底部到对面方向 5~12 格范围内散落 8~15 个冰块
- 碎片靠近柱子处密集，远离处稀疏

### 配置参数

| 参数 | 值 |
|:----|:----|
| 类型 | `DecorationType.CUSTOM` |
| 自定义生成器键 | `"fallen_ice_gate"` |
| 材质 | 冰 40% / 浮冰 35% / 蓝冰 25% |
| 高度 | 10~25 格 |
| 群系 | `biome_dyedream_3` |
| 稀有度 | 7 |
| 生成阶段 | `TOP_LAYER_MODIFICATION` |
