---
name: "pasterdream-vfx-api"
description: "PasterDream 特效系统专用 API，提供 Facade+Builder 模式的服务端触发入口（屏幕特效/打击帧/过场动画/粒子发射器/残影/雾色氛围/屏幕晃动）。在需要为 BOSS 战/技能/事件添加全屏特效、相机过场、粒子演出、残影拖尾或雾色氛围时调用。"
---

# PasterDream VFX API

本 Skill 提供 PasterDream 模组特效系统 API 的使用指南。特效系统借鉴开源模组 Qliphoth Awakening（FDBosses + FDLib，作者 FINDERFEED）的设计思路，在 PasterDreamAPI 内**独立实现**（其许可证禁止复制源码，仅借鉴思路）。

## 适用场景

- BOSS 战演出：召唤过场 / 受击打击帧 / 狂暴全屏特效
- 技能特效：粒子发射器演出 / 冲刺残影拖尾
- 全屏屏幕特效：渐入渐出的颜色/后处理滤镜
- 相机过场：镜头路径动画（CatmullRom/Linear 曲线）
- 氛围铺垫：雾色暗化 / 血色雾（BOSS 狂暴/阶段切换）
- 大冲击演出：屏幕晃动（BOSS 终结技/爆炸时刻）

## 架构分层

| 层 | 内容 |
|---|---|
| API 通用侧 | `api/effect/screen|impact|cutscene|particle|ghost|atmosphere|shake/` 数据类 + 静态注册表 + Facade（服务端安全） |
| API 客户端侧 | `api/client/effect/` 渲染骨架（客户端专用，专用服不加载） |
| 主模挂载 | `PDEffectClientEvents`（客户端游戏总线）+ `PDShaderBootstrap`（MOD 总线 shader 注册）+ mixin |

## 1. 打击帧 ImpactFrame（最简单）

```java
// 服务端任意位置触发
ImpactFrameAPI.sendImpactFrames(serverLevel, pos, 80.0,
        new ImpactFrame(0.45f, 0.03f, 3, false));   // threshold, lerp, duration, invert
// 反相黑场（狂暴特写）
ImpactFrameAPI.sendImpactFrames(serverLevel, pos, 99.0,
        new ImpactFrame(0.3f, 0.05f, 6, true));
```

## 2. 屏幕特效 ScreenEffect

```java
// 服务端触发纯色全屏渐入渐出
ScreenEffectAPI.sendScreenEffectToPlayers(serverLevel, center, 99.0,
        ScreenColorEffect.TYPE, new ScreenColorData(0x55000000), 10, 20, 10);
```

自定义特效类型：实现 `ScreenEffectData` + 客户端 `ScreenEffect` 子类，用 `ScreenEffectAPI.registerType(id, codec, factory)` 注册。

## 3. 粒子发射器 ParticleEmitter

```java
// 服务端触发圆形向上喷射的灵魂粒子
ParticleEmitterAPI.spawn(serverLevel, pos, 99.0,
        ParticleEmitterData.builder(ParticleTypes.SOUL)
                .position(pos.add(0, 2.5, 0))
                .lifetime(40)
                .particlesPerTick(6)
                .processor(new CircleSpawnProcessor(2.5f))  // 半径 2.5，向上飞出
                .build());
```

内置处理器：`EmptyEmitterProcessor` / `CircleSpawnProcessor` / `BoundToEntityProcessor`。自定义处理器实现 `EmitterProcessor` + `EmitterProcessorType`，经 `ParticleEmitterAPI.registerProcessorType` 注册。

## 4. 过场动画 Cutscene

```java
// 服务端向范围内玩家播放环绕过场
CutsceneAPI.startCutsceneForPlayers(serverLevel, center, 99.0,
        CutsceneData.create()
                .time(80)
                .moveCurveType(CurveType.CATMULLROM)
                .timeEasing(EasingType.SMOOTHSTEP)
                .addCameraPos(CameraPos.of(center.add(0, 10, 22), center))
                .addCameraPos(CameraPos.of(center.add(0, 16, 0), center)));
// 强制停止
CutsceneAPI.stopCutsceneForPlayers(serverLevel, center, 99.0);
```

过场自动接管玩家输入、相机、HUD（保留 screen_effect 层）；`StopMode.AUTOMATIC` 播完自动恢复。

## 5. 残影 GhostEffect（BOSS 冲刺拖尾）

```java
// 服务端为指定实体开启残影拖尾（每 tick 采样实体位置生成半透明副本）
GhostEffectAPI.startGhostTrail(serverLevel, pos, 99.0,
        boss.getId(), 24, 50);   // entityId, 持续 tick, 初始透明度(0-255)
```

- 客户端 `GhostHandler` 在 `RenderLevelStageEvent.AFTER_ENTITIES` 用实体渲染器重渲染实体副本，经 `ColoredVertexConsumer` 强制半透明白。
- **每个残影快照按自身 age 独立渐出**（拖尾越靠后越淡）。
- 残影位置/朝向冻结在采样点，但**实体骨骼动画实时渲染**（GeckoLib 机制）——残影会随动画运动，属固有行为。

## 6. 雾色/暗化氛围 AtmosphereEffect（BOSS 狂暴）

```java
// 服务端触发血色雾（全场雾色染红，duration 后自动衰减退出）
AtmosphereEffectAPI.bloodFog(serverLevel, pos, 99.0, 0.9f, 120);
// 暗化灰雾
AtmosphereEffectAPI.darken(serverLevel, pos, 99.0, 0.7f, 100);
```

- 客户端 `AtmosphereHandler` 用阻尼插值（`current += (target - current) * 0.15`）平滑进出。
- 挂载 `ViewportEvent.ComputeFogColor` 修改雾色，duration 到期后目标归零自动衰减。

## 网络与专用服

- 全部 S2C payload 注册在 `PDNetwork.registerPayloads`，客户端经 `PDClientVfx` 反射落地（专用服安全）。
- API Facade 只引用通用类（`ServerPlayer`/`PacketDistributor`），不含客户端符号。

## 前置条件

`PasterDreamAPI.registerAll(modEventBus)` 已统一注册（含 `CutsceneAPI.ENTITY_REGISTRY` 与内置处理器类型）。

## 引用文件

- `PasterDreamAPI/.../api/effect/impact/ImpactFrameAPI.java` — 打击帧门面
- `PasterDreamAPI/.../api/effect/screen/ScreenEffectAPI.java` — 屏幕特效门面
- `PasterDreamAPI/.../api/effect/particle/ParticleEmitterAPI.java` — 粒子发射器门面
- `PasterDreamAPI/.../api/effect/cutscene/CutsceneAPI.java` — 过场门面
- `PasterDreamAPI/.../api/effect/ghost/GhostEffectAPI.java` — 残影门面
- `PasterDreamAPI/.../api/effect/atmosphere/AtmosphereEffectAPI.java` — 雾色氛围门面
- `PasterDreamAPI/.../api/effect/shake/ScreenShakeAPI.java` — 屏幕晃动门面（`api/effect/shake/ScreenShakeData.java` 数据）
- `PasterDreamAPI/.../api/client/util/ColoredVertexConsumer.java` — 半透明重渲染工具
- `PasterDream/.../registry/PDArenaBossManager.java` — BOSS 集成示例（召唤过场）
- `PasterDream/.../entity/mob/AaroncosHandEntity.java` — BOSS 集成示例（狂暴血色雾 + 灵魂粒子）
- `PasterDream/.../entity/mob/AaroncosLefthand0Entity.java` — BOSS 集成示例（冲刺残影）
- `PasterDream/.../entity/mob/ShadowTuneTotemEntity.java` — BOSS 集成示例（终结技图腾：黑场蓄力 + 爆炸黑场/晃动/暗化）
