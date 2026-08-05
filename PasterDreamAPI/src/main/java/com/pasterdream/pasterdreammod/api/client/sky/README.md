# PasterDream 天空盒 API 集成指南（1.21.1 NeoForge + Iris 光影）

> 本指南基于**用户实测验证**的最终方案（2026-08-05）。
> 染梦维度星空系统（星星/行星/极光/光带/星座/流星/彩虹/tint）已按此方案实现并稳定运行。

---

## 一、核心架构（三层）

```
data/<modid>/skyboxes/*.json     →  数据驱动配置（推荐）
SkyboxDataReloadListener         →  加载 JSON → SkyboxRegistry
SkyboxRenderer                   →  每帧渲染（候选切换 + 淡入淡出）
  └─ AFTER_SKY 事件挂载（SkyboxClientEvents）
```

- **数据驱动**：所有天空内容用 JSON 配置，改配置即可换天空，无需重编译。
- **代码注册**：`SkyboxAPI.register(content, condition)` 或 `SkyboxPresets` 预设。

---

## 二、⚠️ 必读：易犯错误清单（实测踩坑总结）

### 🔴 错误 1：用 `renderSky` Mixin 注入渲染（最致命）
```java
// ❌ 错误：在 LevelRenderer#renderSky 的 @Inject(RETURN) 渲染
// Iris 光影下粒子 shader 内容被当作"世界几何体"处理 →
// 星星/行星变黑、光带/极光消失
@Inject(method = "renderSky", at = @At("RETURN"))
private void bad(PoseStack ps, ...) { SkyboxRenderer.render(ps, camera, tick); }

// ✅ 正确：用 RenderLevelStageEvent.AFTER_SKY 事件
@SubscribeEvent
public static void onRender(RenderLevelStageEvent event) {
    if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
    PoseStack poseStack = new PoseStack();
    poseStack.mulPose(event.getModelViewMatrix());   // AFTER_SKY 的 PoseStack 为 null！
    SkyboxRenderer.render(poseStack, event.getCamera(), event.getPartialTick().getGameTimeDeltaPartialTick(false));
}
```
> **关键**：`AFTER_SKY` 事件传入的 `getPoseStack()` 是 **null**，必须用 `new PoseStack() + mulPose(modelViewMatrix)` 构建（含相机旋转）。

### 🔴 错误 2：内容类自己改混合模式
```java
// ❌ 错误：极光/光带用加法混合、行星禁用混合 → Iris 下状态混乱
RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

// ✅ 正确：内容类只设 shader + disableCull/enableCull，不设混合
RenderSystem.setShader(GameRenderer::getPositionColorShader);
RenderSystem.disableCull();
// ... 绘制 ...
RenderSystem.enableCull();
```
> 混合模式由 `SkyboxRenderer.render` **统一设置**（标准混合 `SRC_ALPHA, ONE_MINUS_SRC_ALPHA`）。

### 🔴 错误 3：星星/行星用 `getParticleShader` + `POSITION_TEX_COLOR`
```java
// ❌ 错误：粒子 shader 的顶点色在 Iris 下被光照变黑
RenderSystem.setShader(GameRenderer::getParticleShader);
buffer = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

// ✅ 正确：纯纹理 shader（无顶点色），纹理自带颜色
RenderSystem.setShader(GameRenderer::getPositionTexShader);
buffer = tesselator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
SkyGeometry.addTexturedBillboard(buffer, matrix, center, size, angle);  // 无颜色版
```
> `POSITION_TEX` 格式**没有颜色属性**，用旧版 `addTexturedBillboard(..., color, alpha)` 调 `setColor` 会抛异常。

### 🔴 错误 4：极光/光带大面积内容做色相归一化
```java
// ❌ 错误：normalizeHue（最亮分量提到 1）→ 多 band 加法叠加后 RGB 全钳到 1 → 白色层状
// ✅ 正确：保持原色，标准混合不钳白
```

### 🔴 错误 5：行星纹理带半透明边缘
```java
// ❌ 行星纹理有 0<alpha<255 的半透明边缘 → 原版半透明、Iris 下黑边
// ✅ 修复：纹理实心化（tools/solidify_planets.py），圆内 alpha=255、圆外=0
```

### 🟡 注意 6：`renderSky` 签名（如需抑制原版星星的 Mixin）
```java
// 1.21.1 renderSky 签名：第一个参数是 Matrix4f（不是 PoseStack）！
void renderSky(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean foggy, Runnable skyFogSetup)
// Mixin @Redirect ordinal=1 抑制原版星星（星盘），参数必须与目标完全匹配
```

### 🟡 注意 7：`hasSupportedSky` 判定
```java
// 天空类型为 NORMAL 或 END 即可（染梦是 NORMAL）
return skyType == SkyType.NORMAL || skyType == SkyType.END;
// 具体维度过滤由 JSON 的 dimensions 条件完成
```

---

## 三、内容类模板（正确写法）

### 程序化内容（极光/光带/tint/星座/流星）—— `getPositionColorShader`
```java
@Override
public void render(SkyboxRenderContext context, float alpha) {
    RenderSystem.setShader(GameRenderer::getPositionColorShader);
    RenderSystem.disableCull();
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    Matrix4f matrix = context.poseStack().last().pose();
    BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
    // ... 顶点：buffer.addVertex(matrix, x, y, z).setColor(r, g, b, alpha)
    BufferUploader.drawWithShader(buffer.buildOrThrow());
    RenderSystem.enableCull();
}
```

### 纹理内容（星星/行星/星座星点）—— `getPositionTexShader`
```java
@Override
public void render(SkyboxRenderContext context, float alpha) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.disableCull();
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    Matrix4f matrix = context.poseStack().last().pose();
    BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    RenderSystem.setShaderTexture(0, texture);
    SkyGeometry.addTexturedBillboard(buffer, matrix, center, size, angle);  // 无颜色版
    BufferUploader.drawWithShader(buffer.buildOrThrow());
    RenderSystem.enableCull();
}
```

---

## 四、JSON 配置模板

```json
{
  "fade_speed": 0.12,
  "weight": 100,
  "dimensions": ["pasterdream:dyedream_world"],
  "biomes": ["pasterdream:biome_dyedream_0"],
  "time": { "from": 13000, "to": 23000 },
  "layers": [
    { "type": "pasterdream:sky_tint", "priority": -100, "color": [0.3, 0.18, 0.42], "opacity": 0.05 },
    { "type": "pasterdream:star_field", "priority": -5, "textures": ["..."], "count": 1000, "min_size": 0.5, "max_size": 1.5 },
    { "type": "pasterdream:planet_system", "priority": 8, "planets": [{ "texture": "...", "yaw": 0.5, "pitch": 0.2, "size": 8.0, "opacity": 0.95 }] },
    { "type": "pasterdream:aurora", "priority": 20, "opacity": 0.5, "bands": 3, "colors": [[0.3, 0.7, 1.0]] },
    { "type": "pasterdream:ribbons", "priority": -12, "opacity": 0.35, "thickness": 0.16, "colors": [[0.5, 0.7, 1.0]] },
    { "type": "pasterdream:shooting_stars", "priority": 24, "count": 2, "interval_ticks": 110, "duration_ticks": 18 }
  ]
}
```

---

## 五、预设库（SkyboxPresets）

- `tint(color, opacity)`、`starField(theme)`、`aurora(colors, opacity, bands)`、
  `ribbons(colors, opacity)`、`planetSystem(theme, count)`、`shootingStars(count)`、
  `constellation(stars, lines, color, yaw, pitch)`、`rainbow()`
- 组合：`galaxyNight(theme, bandColor)` / `auroraNight(theme, colors, opacity)` / `starryNight(theme)`
- 组装：`asSkybox(biomes, dimensions, layers, fadeSpeed, weight)`
- 注册：`SkyboxPresetLoader.register(id, skybox)`

---

## 六、验证清单（每次改动后）

- [ ] `.\gradlew :PasterDream:compileJava` 通过
- [ ] 客户端启动无崩溃、进入染梦维度
- [ ] `debug.log` 有 `[Skybox] 渲染: entries=N 候选=...`（N=60 = 6 JSON × 10 层）
- [ ] Iris 光影下：星星/行星有颜色（非黑）、极光/光带可见、行星实心
- [ ] 无 `VertexFormat` / `IllegalArgument` 错误（`POSITION_TEX` 未调 setColor）
