# 主模 → PasterDreamAPI 可上收候选

> **类别**：参考 · 架构 / 边界  
> **日期**：2026-07-30（P0 + P1 批已落地）  
> **范围**：五域只读排查（实体·世界生成·方块物品·横切工具·客户端杂项）  
> **原则**：API = 可复用框架 / Builder / 基类 / 横切工具；主模 = 内容数据、玩法数值、具体资产与维度 ID  
> **功能是否对等** → [`功能状态.md`](功能状态.md)  
> **已有 API 关系** → [`地表生成与实体动画API.md`](地表生成与实体动画API.md) · [`架构差异.md`](架构差异.md)

---

## 0. 一句话结论

| 域 | 结论 |
| :--- | :--- |
| **横切工具** | ✅ 已上收 `ServerScheduler` · `StructureInventoryHelper` |
| **实体** | ✅ 已上收伤害免疫 Config + 基类 · `AbstractWandProjectileEntity`；`EntityImmunitySetup` 数据表留主模 |
| **方块/物品** | ✅ 已上收 `HorizontalWaterloggedBlock` · `AbstractGeoDisplayItem` · `FluidTypeAPI`/`FluidBuilder` · `SimpleContainerMenu` · Block datagen 基类；Item 双注册为文档收敛 |
| **客户端** | ✅ BGM **纯逻辑**上收 `api/audio`（FadeState/Cooldown/LoopRestart/BiomeMusicTable/IMusicEventLookup）；播放实现留主模 `client.audio` |
| **世界生成** | **几乎无内容可搬**；只做 Tree 注册增强 / locator 工具 / 可选新 DecorationType |
| **明确勿上收** | 具体实体·装饰·遗迹列表·ChunkGen·SAN/能量 Attachment·HUD·VERIFY hooks·配置项 |

当前 API 已覆盖 Builder 族 + **横切调度 / 结构库存 / 免疫·投射物·Geo 显示基类** + **P1（FluidType / Menu 骨架 / Block datagen / BGM 纯逻辑）**。剩余多为 P2（Tree/locator、W4 BE 骨架等）与内容清理，而非再搬一批内容类。

### 0.1 P0 落地（2026-07-29）

| API 落点 | 主模变更要点 | 编译 |
| :--- | :--- | :---: |
| `api/util/ServerScheduler` | 去 `@EventBusSubscriber(modid)`；`register(IEventBus)`；`PasterDreamMod` → `ServerScheduler.register(NeoForge.EVENT_BUS)`；保留 `advanceForTest` | ✅ |
| `api/util/StructureInventoryHelper` | BE 等 import 改 API；行为（双格式 NBT / 书页迁移 / loot unpack）不变 | ✅ |
| `api/entity/damage/{DamageImmunityConfig,ConfigurableImmunityEntity}` | mob / 投射物 import 改 API；`EntityImmunitySetup` **留主模** 填 `PDEntities` | ✅ |
| `api/entity/projectile/AbstractWandProjectileEntity` | 法杖弹子类 import 改 API | ✅ |
| `api/block/HorizontalWaterloggedBlock` | ClayPot / ChristmasLights 等 | ✅ |
| `api/item/base/AbstractGeoDisplayItem` | ~20 DisplayItem | ✅ |

验证：`JAVA_HOME=…/java-21-openjdk sh gradlew :PasterDreamAPI:compileJava :PasterDream:compileJava --offline` → **BUILD SUCCESSFUL**。主模旧路径已删；旧 package 引用 grep 为 0。

### 0.2 P1 落地（2026-07-30）

| API 落点 | 主模变更要点 | 编译 |
| :--- | :--- | :---: |
| `api/fluid/FluidTypeAPI` + `FluidBuilder`（`fluidType`/`source`/`flowing`/`buildPair`） | `PDFluidsType` 改走 `FluidTypeAPI.register`；去掉主模自有 FLUID_TYPES；`PasterDreamMod` 侧效加载 Type+Fluid | ✅ |
| `api/menu/SimpleContainerMenu` | 箱子/框/桌/永恒书卷等 Menu 继承骨架（槽网格 + 玩家背包 + quickMove + stillValid） | ✅ |
| `api/data/ApiBlockModelProvider` · `ApiBlockTagProvider` | `PDBlockModelProvider` / `PDBlockTagProvider` 薄壳；手写 tag 列表在 `addExtraTags` | ✅ |
| `api/audio/*`（`FadeState` · `CooldownManager` · `LoopRestartManager` · `BiomeMusicTable` · `IMusicEventLookup` · `BgmAPI`） | 主模 `client.audio`：`Cooldown`/`LoopRestart` 薄继承 API；`BiomeMusicRegistry` extends `BiomeMusicTable`；`SoundEventLookup` implements `IMusicEventLookup`；**VolumeSound / Crossfade / ModMusicManager 播放层留主模**（server-safe） | ✅ |
| **Item 双注册收敛** | **文档约定**（非 bulk 迁 600+ 条）：简单材料/食物/工具优先 `ItemAPI.*`；复杂自定义 / BlockItem / Geo / 强交互可继续 `PDItems.ITEMS` 或 `ItemAPI.registerCustom`；两路均为 `createItems("pasterdream")`，勿再开第三条 DeferredRegister | ✅ |

验证：同上 compile 命令 → **BUILD SUCCESSFUL**（2026-07-30）。


---

## 1. 已有 API 边界（勿重复当候选）

| 域 | API 已有 |
| :--- | :--- |
| 注册门面 | `PasterDreamAPI.registerAll` · Block/Item/Entity/Menu/**Fluid+FluidType**/Particle/Curio/BlockEntity API + Builder |
| 世界生成 | `worldgen/decor/*` · `WorldGenUtils` · `TreeRegistry`（键） · RuinAPI · `dimension/terrain/*` |
| 实体动画 | `GeckoLib{Mob,Monster,Projectile}Entity` · `ProcedureAnimationHandler` · `EntitySkill*` |
| 实体扩展 | `api/entity/damage/*`（免疫 Config + 基类）· `api/entity/projectile/AbstractWandProjectileEntity` |
| 方块 / 物品基类 | `SelfDropBlock` · `HorizontalWaterloggedBlock` · `api/item/base/AbstractGeoDisplayItem` · `api/menu/SimpleContainerMenu` · `api/data/ApiBlock{Model,Tag}Provider` |
| 横切工具 | `api/util/ServerScheduler`（显式 `register(bus)`，非 DeferredRegister）· `api/util/StructureInventoryHelper` |
| 音效 / BGM | `ApiSoundRegistry` · DimensionBuilder `.withMusic` · **`api/audio`**（纯逻辑 + Lookup/Table 契约；播放实现不在 API） |
| Curios | `CurioAPI` / `CurioBuilder` + `CurioClientBridge` |
| 缺口 | **无** `network/` · `attachment/` · 客户端 **播放** 层（刻意留主模）· 通用 BE 容器基类（Menu 骨架已有） |

---

## 2. 推荐上收（按优先级）

### 2.1 P0 — ~~高价值、低内容耦合~~（本批已完成）

| 候选 | 状态 | API 落点 | 备注 |
| :--- | :---: | :--- | :--- |
| **ServerScheduler** | ✅ | `api/util/ServerScheduler` | 主模 `PasterDreamMod` 显式 `register(NeoForge.EVENT_BUS)` |
| **DamageImmunityConfig** | ✅ | `api/entity/damage/` | 数据表仍由主模 `EntityImmunitySetup` 填 |
| **ConfigurableImmunityEntity** | ✅ | 同上 | 继承 API `GeckoLibMobEntity` |
| **AbstractWandProjectileEntity** | ✅ | `api/entity/projectile/` | vanilla-only |
| **HorizontalWaterloggedBlock** | ✅ | `api/block/` | |
| **AbstractGeoDisplayItem** | ✅ | `api/item/base/` | |
| **StructureInventoryHelper** | ✅ | `api/util/` | 原 P1，与 P0 同批落地 |

### 2.2 P1 — ~~明显可泛化~~（本批已完成）

| 候选 | 状态 | API 落点 | 备注 |
| :--- | :---: | :--- | :--- |
| **FluidType + 完整 FluidBuilder** | ✅ | `api/fluid/FluidTypeAPI` · `builder/FluidBuilder` | 主模 `PDFluidsType` 仅持有 Holder；`buildPair` 可选一站式 |
| **通用容器 Menu 骨架** | ✅ | `api/menu/SimpleContainerMenu` | 复杂机台 Menu 可继续手写槽，或只复用 `addPlayerInventory` / quickMove helper |
| **Block 数据生成 Provider** | ✅ | `api/data/ApiBlockModelProvider` · `ApiBlockTagProvider` | 主模 hook：`registerExtraStatesAndModels` / `addExtraTags` |
| **BGM 交叉淡入框架（去资产）** | ✅ | `api/audio/*` | **仅纯逻辑 + 契约**；`CrossfadeManager`/`VolumeSoundInstance`/`ModMusicManager` 留主模 |
| **Item 双注册收敛** | ✅ 文档 | （无新类） | 见 §0.2 / §3.3：优先 ItemAPI；复杂件可 `PDItems` 或 `registerCustom` |
| **EntityImmunitySetup** | — | 数据留主模 | 机制已在 P0 API；无需再迁 |

### 2.3 P2 — 可选 / 需重构后再收

| 候选 | 说明 |
| :--- | :--- |
| **TreePlacerRegistry / 增强 TreeRegistry** | 主模 `DyedreamTreePlacers` + `PDTreeDecorators` 仍是内容；API 只统一 Type 注册门面 |
| **结构 nearest locator** | 自 `PDShadowDoorLocator` 抽 tag/维度参数化工具 → RuinAPI 或 StructureUtils |
| **更多 DecorationType / Placer** | 评估浮岛/巨型簇等是否值得进 generic_decor，减少 `PDFeatures` 特例 |
| **W4Data / W4GeoData BE 泛化** | 自由 Compound + Geo 双控制器 → `api/blockentity/base` 骨架；主模保留 PD 字段语义 |
| **W4GeoBlockRenderer · AnimUtils** | 通用 Defaulted Geo 方块/展示物渲染 + 骨骼挂 ModelPart |
| **PasterItemData 底层** | CUSTOM_DATA get/put 工具；工坊键名语义留主模 |
| **维度 reset 文件操作** | 自 `PDCommands` 抽删 region/*.mca 辅助；命令本身留主模 |
| **CurioClientBridge 默认实现** | 薄遍历注册；主模只留日志 |
| **Particle 常见 RenderType** | 自 `PDParticleRenderTypes` 抽发光/加法等常量 |
| **技能状态机迁 EntitySkillManager** | 非上收文件：ShadowGolem 等手写 timer → 接 API；需先钉 `IAnimatedEntity` 与基类缝 |
| **清理空壳** | 主模 `entity.GeckoLibMonsterEntity`（无引用）删除；勿上收 |

### 2.4 P3 — 仅文档 / 不上收类「模式」

- `RendererRegistry` 集中注册写法  
- `PDEntities` / `PDEntityEvents` 作为 EntityAPI 用法示例  
- Attachment + 登录/重生/克隆同步生命周期（模式文档，类不搬）  
- Payload 定义/注册惯例（业务 handler 不搬）

---

## 3. 分域明细摘要

### 3.1 实体 / 动画 / AI

| 动作 | 项 |
| :--- | :--- |
| **已上收** | `DamageImmunityConfig` · `ConfigurableImmunityEntity` · `AbstractWandProjectileEntity` → `api/entity/damage|projectile` |
| **部分** | `EntityImmunitySetup`（机制在 API；`PDEntities` 数据表留主模） |
| **保持** | 全部具体 mob/projectile；`SpellEffects`；`AaroncosHandEntity`；手写 skillTimer/charge；`RendererRegistry` 与全部 *Renderer/*Model；`PDEntities`/`PDEntityEvents` |
| **清理** | 主模空壳 `entity.GeckoLibMonsterEntity` |
| **重构建议** | 更多实体改继承免疫基类消 inline `hurt`；Boss skill 评估接 `EntitySkillManager`（基类需 implements 接口缝，见地表/动画文档 §3.3） |

### 3.2 世界生成 / 结构 / 维度地形

| 动作 | 项 |
| :--- | :--- |
| **勿搬内容** | `chunkgen/*`（DyedreamChunkGenerator/BiomeSource/Noises）· 全部专用 Feature 实现 · 树 placer/decorator 实现 · `*Decorations` / `PDRuinsRegistration` 列表 · `PDLampShadowWorldgen` |
| **API 增强（非搬文件）** | Tree 注册门面 · Structure locator 工具 · 可选新 DecorationType · terrain 与自定义 CG 集成示例 |
| **清理** | 废弃 `PDDyedreamBiomeModifier`（逻辑已 JSON）；疑似死代码 `CalcitePillarFeature`（Ice 已迁 PILLAR） |
| **保持** | `PDFeatures` 作特例入口；`PDWorldgenRegistries` 供维度 JSON `type` |

装饰双轨（Builder 内存条目 vs 数据包 JSON）与 Ruin/Decor 同级关系不变，见 [`地表生成与实体动画API.md`](地表生成与实体动画API.md)。

### 3.3 方块 / BE / 物品 / 菜单 / 流体 / 粒子

| 动作 | 项 |
| :--- | :--- |
| **已上收基类** | `HorizontalWaterloggedBlock` · `AbstractGeoDisplayItem` |
| **已上收 P1** | FluidTypeAPI + FluidBuilder；SimpleContainerMenu；ApiBlock Model/Tag Provider |
| **可选泛化** | W4Data/W4Geo 骨架 · SimpleMarker BE · 液态方块基 · Particle RenderType |
| **保持** | 全部 `registry/blocks|items/*` 分组内容 · 具体 Menu/BE/流体实现 · MemorialDoll 领域链 |
| **工程债（约定）** | Item：`PDItems.ITEMS` 与 `ItemAPI.REGISTRY` 双轨允许共存（同 modid）；**新建简单物品走 ItemAPI**；勿开第三条 DR；bulk 迁非本批范围 |

### 3.4 网络 / 调度 / Attachment / 命令 / 配置 / util

| 动作 | 项 |
| :--- | :--- |
| **已上收** | `ServerScheduler` · `StructureInventoryHelper` → `api/util`（Scheduler 须主模 `register(NeoForge.EVENT_BUS)`） |
| **部分抽取** | `PasterItemData` 底层 · 维度 region 重置 helpers |
| **保持** | `PDNetwork` + 全部 Payload 业务 · `attachment/*`（SAN/能量）· `config/*` · `PDCommands` 命令体 · `WeaponWorkshopVariables` / `WorkshopMultiBlock` · `world/PDSanHelper` · Wind/LampShadow/Arena 事件 |

### 3.5 客户端 / BGM / HUD / 数据生成 / VERIFY

| 动作 | 项 |
| :--- | :--- |
| **已上收框架** | `api/audio` 纯逻辑（Cooldown/Loop/Fade/Table/Lookup）；biome→音乐表与维度白名单、播放/交叉淡化实现留主模 `client.audio` |
| **已上收工具** | Block Model/Tag Provider（读 BlockAPI configs）；W4Geo*Renderer · AnimUtils 仍属 P2 可选 |
| **薄完善** | Curio 默认 ClientBridge |
| **保持** | 全部 HUD · 具体 Model/Renderer/Particle/Screen/Sky · smoketest `*VerifyHooks` · ClientSetup 业务 glue |

**API 客户端约束**：优先桥接/事件暴露，避免破坏「API server-safe、少直接引用客户端类型」；BGM/Geo 上收时沿用 CurioClientBridge / Particle 注册分离模式。

---

## 4. 不要上收（硬清单）

下列无论「看起来像工具」都 **留在主模**：

1. **玩法数值与玩家数据**：SAN / 融梦能量 Attachment、Payload、tick 环境修饰、游戏规则绑定  
2. **维度灵魂**：Dyedream ChunkGenerator / BiomeSource / Noises 与维度 JSON type 注册  
3. **内容注册表**：实体/方块/物品/装饰/遗迹/树的具体条目与美学参数  
4. **专用 Feature 与树形状实现**（除非未来抽成新的通用 DecorationType 且去掉 PD 方块硬编码）  
5. **Boss/技能时序与法术效果表**（`SpellEffects`、手写 skillTimer）  
6. **HUD / Boss 条 / 失智叠层**  
7. **配置文件键与默认平衡**  
8. **VERIFY / smoke hooks**  
9. **强绑定资产路径**：具体 geo 名、音乐名列表、结构模板名、出生点坐标  

---

## 5. 建议落地顺序

```text
✅ 1. ServerScheduler → API + 主模改 import + 总线 register(NeoForge.EVENT_BUS)
✅ 2. DamageImmunity* + AbstractWandProjectile → API；EntityImmunitySetup 留主模填表
✅ 3. HorizontalWaterloggedBlock + AbstractGeoDisplayItem
✅ 4. StructureInventoryHelper
✅ 5. FluidTypeAPI + FluidBuilder 一站式；Item 双注册文档收敛
✅ 6. BGM 纯逻辑上收 api/audio（Lookup/Table 注入；播放层留主模）
✅ 7. Menu/容器骨架、Block data providers
8. 清理：主模空壳 GeckoLibMonster、废弃 BiomeModifier、死 Feature
9. （可选）Tree/locator 增强 · W4 BE 骨架 · EntitySkill 与基类接口钉死后迁 Boss 状态机
```

每步：编译 → 相关 VERIFY 套件（scheduler 影响面大，优先 smoke/core / main-flow）→ 更新本文件与 [`架构差异.md`](架构差异.md)。

---

## 6. 与既有文档的交叉引用

| 主题 | 文档 |
| :--- | :--- |
| generic_decor / procedure 动画接线 | [`地表生成与实体动画API.md`](地表生成与实体动画API.md) |
| Capability→Attachment、queueServerWork→Scheduler、有意增强 | [`架构差异.md`](架构差异.md) |
| playerAnimator 姿势未迁、功能缺口 | [`功能状态.md`](功能状态.md) |
| 用法陷阱 | `.trae/skills/world-decoration-api/` · `pasterdream-entity-api/` |

---

## 7. 维护

- 完成一次上收后：从 §2 删掉已迁项，记入 §1「已有」；主模路径改为 re-export 或删除。  
- 新增主模「通用基类」时先问：是否零 `PD*` 注册表依赖？是 → 候选进本文；否 → 内容。  
- 本文 **不** 记录功能缺失；只记录边界与迁移优先级。  
- 五域排查为 2026-07-29 静态只读快照；P1 落地补记 2026-07-30。大改注册结构后应重扫 P2。
