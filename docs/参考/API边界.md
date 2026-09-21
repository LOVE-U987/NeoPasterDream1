# API 边界

> 本文档定义哪些代码应该上收 API 模块,哪些应该留在主模块。

---

## 判定规则

新增主模「通用基类」时先问:

**是否零 `PD*` 注册表 / 零玩法数值 / 零具体资产路径 依赖?**

- **是** → 可考虑进 API
- **否** → 内容,留主模

---

## 应该上收 API 的代码

### 1. 可复用框架

```
✅ Builder 模式 (如 BlockBuilder, EntityBuilder, ItemBuilder)
✅ Facade/Result 类
✅ 注册门面 (如 BlockAPI, EntityAPI, ItemAPI)
✅ 横切工具 (如 ServerScheduler, StructureLocator)
✅ 配置注册 (如 PDAddonConfigRegistry)
✅ 数据附件定义 (如 PDPlayerAttachments)
✅ 自定义属性 (如 APIAttributes)
```

### 2. 会被多个模块引用的类

```
✅ 工具类 (如 DimensionRegionHelper, CustomItemData)
✅ 基类 (如 FreeDataBlockEntity, GeoFreeDataBlockEntity)
✅ 接口 (如 ISanSystemConfig, IMeltDreamEnergySystemConfig)
```

### 3. 注册体系

```
✅ DeferredRegister 定义
✅ DataGen 提供器
✅ 标签定义
```

---

## 不应该上收 API 的代码

### 1. 游戏内容

```
❌ 具体方块/物品/实体的实现
❌ 玩法数值 (伤害/冷却/持续时间)
❌ 具体资产路径 (纹理/模型/声音文件名)
❌ 维度 ID / 生物群系 ID
```

### 2. 客户端代码

```
❌ HUD / Boss 条实现
❌ 具体 Renderer / Model / Screen / Particle 实现
❌ 客户端效果处理器
```

### 3. 业务逻辑

```
❌ 网络包处理器 (Payload 定义可在 API,处理器在主模)
❌ 事件处理器
❌ 命令体
```

### 4. 测试/调试

```
❌ VERIFY / smoke hooks
❌ 调试工具
```

---

## 已上收 API 的能力

### P1 上收 (2026-07-30)

| 能力 | API | 主模 |
|------|-----|------|
| FluidType | `FluidTypeAPI` + `FluidBuilder.buildPair` | `PDFluidsType` 仅 Holder |
| 容器 Menu | `SimpleContainerMenu` | 简单箱/桌继承 |
| Block datagen | `ApiBlockModelProvider` / `ApiBlockTagProvider` | PD* 薄壳 |
| BGM | `api/audio` 状态机与表/Lookup | `client.audio` 播放与交叉淡化 |
| Item 注册 | `ItemAPI.REGISTRY` 与 `PDItems.ITEMS` 同 modid 双轨 | 约定:新简单物品优先 ItemAPI |

### P2 上收 (2026-07-30)

| 能力 | API | 主模 |
|------|-----|------|
| 物品 CUSTOM_DATA | `CustomItemData` | `PasterItemData` 薄委托 |
| 结构 nearest | `StructureLocator` | `PDShadowDoorLocator` |
| 维度 region 文件 | `DimensionRegionHelper` | `PDCommands` 传送/文案 |
| 自由 Compound BE | `FreeDataBlockEntity` / `GeoFreeDataBlockEntity` | `W4*Data` 覆写 |
| Geo 方块渲染 | `DefaultedGeoBlockRenderer` · `AnimUtils` | `W4GeoBlockRenderer` 薄壳 |
| 粒子 RenderType | `ApiParticleRenderTypes` | `PDParticleRenderTypes` 别名 |
| Curio 客户端桥 | `DefaultCurioClientBridge` | `CurioClientHandler` 日志 |
| 树 Placer DR 门面 | `TreePlacerAPI` | `DyedreamTreePlacers` / `PDTreeDecorators` |

### ID 兼容层上收 (2026-09-17)

| 能力 | API | 主模 |
|------|-----|------|
| ID 别名兼容 | `api/compat`（`IdAlias` / `IdAliasTable` / `IdAliasRegistry` / `IdCompatAPI`） | `PDIdAliases` 具体映射表 + `PDSaveCompat*` 存档检测/备份/提示 |

> 框架零具体 ID、零事件处理器、零命令体；具体旧→新映射表与存档逻辑留主模，
> 详见 [存档兼容层](../设计/存档兼容层.md)。

---

## 勿上收清单

以下无论「看起来像工具」都 **留在主模**:

1. **玩法数值与玩家数据**: SAN / 融梦能量 Attachment、Payload、tick 环境修饰、游戏规则绑定
2. **维度灵魂**: Dyedream ChunkGenerator / BiomeSource / Noises 与维度 JSON type 注册
3. **内容注册表**: 实体/方块/物品/装饰/遗迹/树的具体条目与美学参数
4. **专用 Feature 与树形状实现** (除非未来抽成新的通用 DecorationType)
5. **Boss/技能时序与法术效果表** (`SpellEffects`, 手写 skillTimer)
6. **HUD / Boss 条 / 失智叠层**
7. **配置文件键与默认平衡**
8. **VERIFY / smoke hooks**
9. **强绑定资产路径**: 具体 geo 名、音乐名列表、结构模板名、出生点坐标

### 补充 (同样留主模)

| 类 | 说明 |
|---|---|
| **BGM 播放层** | `CrossfadeManager` / `VolumeSoundInstance` / `ModMusicManager` |
| **网络业务** | `PDNetwork` 与全部 Payload handler |
| **命令体** | `PDCommands` 文案/传送 |
| **EntityImmunitySetup 数据表** | 免疫机制在 API;具体 mob 填表数据留主模 |
| **Attachment 生命周期 glue** | 登录/重生/克隆同步模式可写文档,类不搬 |
| **具体 Renderer / Model / Screen / Particle 实现** | 通用骨架可在 API;内容绑定类留主模 |

---

## 示例

### 示例 1: 工具类

```java
// ✅ 可以上收 API
// 零 PD* 注册表依赖
// 零玩法数值依赖
// 零具体资产路径依赖
public class StructureLocator {
    public static BlockPos findNearestStructure(ServerLevel level, ResourceLocation structureId, BlockPos pos, int searchRadius, boolean skipKnownChunks) {
        // 通用逻辑
    }
}
```

### 示例 2: 内容类

```java
// ❌ 应该留在主模
// 依赖 PD* 注册表
// 有玩法数值
// 有具体资产路径
public class ShadowGolemEntity extends GeckoLibMonsterEntity {
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 100.0)  // 玩法数值
            .add(Attributes.ATTACK_DAMAGE, 15.0);  // 玩法数值
    }
}
```

### 示例 3: 边界情况

```java
// ⚠️ 需要判断
// 有 PD* 注册表依赖 → 留主模
public class PDEntityEvents {
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ShadowGolemEntity) {
            // 依赖具体实体类 → 留主模
        }
    }
}
```

---

## 下一步

- [架构总览](../架构/架构总览.md) — 理解整体架构
- [模块边界](../架构/模块边界.md) — 代码归属决策
- [注册指南](../开发指南/注册指南.md) — 注册系统详解
- [API 架构详解](../架构/API架构详解.md) — API 模块内部架构
