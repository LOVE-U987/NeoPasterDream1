package com.pasterdream.pasterdreammod.client.sky.content;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.pasterdream.pasterdreammod.api.client.sky.SkyContent;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxRenderContext;
import com.pasterdream.pasterdreammod.client.sky.SkyboxRenderer;
import com.pasterdream.pasterdreammod.client.sky.math.SkyColor;
import com.pasterdream.pasterdreammod.client.sky.math.SkyPoint;
import com.pasterdream.pasterdreammod.client.sky.render.SkyGeometry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * 星座内容 —— 由节点连线与双层光晕星点构成的星座图案
 * <p>
 * 节点按 (u, v) 相对坐标映射到天空球面，节点间以宽线段连线
 * （加法混合发光），星点渲染为"外圈大光晕 + 内圈亮核"双层广告牌，
 * 随时间闪烁自旋。
 */
public class ConstellationSkyContent implements SkyContent {

    /** 纹理星点核心亮化系数 */
    private static final float STAR_CORE = 0.7F;
    /** 纹理星点光晕倍率 */
    private static final float STAR_HALO = 1.5F;

    /**
     * 对准时星点放大倍率（克制放大，望远镜下仅轻微放大，避免遮挡星空全貌） */
    private static final float AIM_SIZE_MULT = 1.25F;
    /** 对准时星点亮度倍率 */
    private static final float AIM_ALPHA_MULT = 1.8F;
    /** 纹理星点对准放大倍率（纹理星本身较大，倍率低于程序化光晕星） */
    private static final float AIM_TEXTURED_SIZE_MULT = 1.1F;
    /** 对准过渡动画速度（每 tick 变化量，单颗星独立；放缓实现"缓慢放大变亮"的进入/退出） */
    private static final float AIM_TRANSITION_SPEED = 0.045F;
    /** 十字星闪烁速度（循环变亮变暗） */
    private static final float CROSS_FLICKER_SPEED = 0.12F;
    /** 十字星臂长基数倍率（相对星大小；放大后呈现"星星发光"的辐射效果） */
    private static final float CROSS_ARM_BASE = 2.6F;
    /** 十字星臂长随闪烁伸缩幅度 */
    private static final float CROSS_ARM_PULSE = 0.7F;
    /** 十字高亮纹理（golden_particle 金色十字星芒，粒子三帧动画逐帧循环）
     *  路径格式与星域/行星一致：带 textures/ 前缀与 .png 后缀的完整纹理路径 */
    private static final ResourceLocation[] GOLDEN_PARTICLE_FRAMES = {
            ResourceLocation.fromNamespaceAndPath("pasterdream", "textures/particle/golden_particle_1.png"),
            ResourceLocation.fromNamespaceAndPath("pasterdream", "textures/particle/golden_particle_2.png"),
            ResourceLocation.fromNamespaceAndPath("pasterdream", "textures/particle/golden_particle_3.png")
    };
    /** golden_particle 帧动画速率（帧/tick，与粒子定义 mcmeta frametime=10 一致） */
    private static final float CROSS_FRAME_RATE = 0.1F;
    /** golden_particle 十字臂占整张纹理的比例（16px 图中十字端到端约 13px，
     *  用于把"臂长"换算为广告牌半边长，使纹理十字视觉跨度与原版线段十字一致） */
    private static final float CROSS_TEXTURE_FILL = 13.0F / 16.0F;
    /** 十字淡入淡出最小缩放（粒子式动画：淡入时从 55% 放大到 100%，淡出时反向缩小） */
    private static final float CROSS_FADE_MIN_SCALE = 0.55F;
    /**
     * 望远镜缩放进度（0~1，平滑过渡动画）
     * <p>
     * 玩家手持望远镜放大观看时递增，松开/移开时递减；
     * 星点大小与亮度按此值插值，实现平滑放大缩小。
     */
    private static float aimProgress;

    private final ResourceLocation id;
    private final int priority;
    private final SkyColor color;
    private final SkyColor coreColor;
    private final float twinkleSpeed;
    private final float lineWidth;
    private final List<ResourceLocation> textures;
    private final List<Star> stars;
    private final List<Link> links;
    /** 每颗星独立的瞄准放大进度（0~1，进入/退出平滑动画；与星索引一一对应） */
    private final float[] aimState;

    /**
     * @param id            内容标识
     * @param priority      绘制优先级
     * @param color         星座颜色
     * @param centerYaw     中心偏航角
     * @param centerPitch   中心俯仰角
     * @param scale         整体缩放
     * @param twinkleSpeed  闪烁速度
     * @param lineWidth     连线宽度
     * @param textures      星点纹理（可空，空则程序化光晕）
     * @param shape         星座形状（节点+连线）
     * @param seed          随机种子
     */
    public ConstellationSkyContent(
            ResourceLocation id, int priority, SkyColor color,
            float centerYaw, float centerPitch, float scale,
            float twinkleSpeed, float lineWidth,
            List<ResourceLocation> textures, Shape shape, long seed
    ) {
        this.id = id;
        this.priority = priority;
        this.color = color;
        this.coreColor = brighten(color, STAR_CORE);
        this.twinkleSpeed = twinkleSpeed;
        this.lineWidth = lineWidth;
        this.textures = List.copyOf(textures);
        this.links = shape.links();
        this.stars = makeStars(shape, centerYaw, centerPitch, scale, textures.size(), seed);
        this.aimState = new float[this.stars.size()];
    }

    @Override
    public ResourceLocation id() {
        return this.id;
    }

    @Override
    public int priority() {
        return this.priority;
    }

    @Override
    public void render(SkyboxRenderContext context, float alpha) {
        // 白天（夜晚因子低）时回退望远镜瞄准状态：星空消失，玩家观星/放大进度一并归零
        if (context.nightFactor() <= 0.5F) {
            for (int i = 0; i < this.aimState.length; i++) {
                this.aimState[i] = 0.0F;
            }
        }
        Matrix4f matrix = context.poseStack().last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        float time = context.renderTime();
        float pulse = 0.7F + 0.3F * Mth.sin(time * this.twinkleSpeed * 0.5F);
        RenderSystem.disableCull();
        // 与 Stellara 一致：不设置混合模式，依赖外层 SkyboxRenderer 统一的标准混合

        // 相机视线方向（用于望远镜对准检测）
        org.joml.Vector3f look = context.camera().getLookVector();
        float lookX = look.x();
        float lookY = look.y();
        float lookZ = look.z();
        // 天空旋转角（弧度）——星点坐标需应用此旋转后才是屏幕实际位置
        float skyAngleRad = (float) Math.toRadians(context.skyAngle());

        // 望远镜缩放进度（平滑过渡）：手持望远镜放大观看时递增，否则递减
        aimProgress = Mth.clamp(aimProgress + (isUsingSpyglass() ? AIM_TRANSITION_SPEED : -AIM_TRANSITION_SPEED), 0.0F, 1.0F);
        // 仅手持望远镜放大观看时才启用对准效果（未使用望远镜时 aimProgress=0）
        boolean aimEnabled = aimProgress > 0.01F;

        // 连线（望远镜对准时略增亮；无连线或两点重合时缓冲为空，安全提交）
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder lineBuffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        float lineAlphaMult = 1.0F + 0.6F * aimProgress;
        for (Link link : this.links) {
            if (link.from() < this.stars.size() && link.to() < this.stars.size()) {
                SkyGeometry.addLine(
                        lineBuffer, matrix,
                        this.stars.get(link.from()).point(), this.stars.get(link.to()).point(),
                        this.lineWidth, this.color, alpha * pulse * 0.5F * lineAlphaMult
                );
            }
        }
        SkyGeometry.drawIfNotEmpty(lineBuffer);

        // 星点（支持望远镜对准放大变亮）
        if (this.textures.isEmpty()) {
            renderGlowStars(matrix, tesselator, time, alpha, lookX, lookY, lookZ, skyAngleRad, aimEnabled, aimProgress);
        } else {
            renderTexturedStars(matrix, tesselator, time, alpha, lookX, lookY, lookZ, skyAngleRad, aimEnabled, aimProgress);
        }
        // 十字星标记：对准的星中心闪烁（淡入后循环变亮变暗，移开淡出）
        renderAimCrosshairs(matrix, tesselator, time, lookX, lookY, lookZ, skyAngleRad, aimEnabled);

        RenderSystem.enableCull();
    }

    /**
     * 判断玩家是否手持望远镜（Spyglass）且正在放大观看
     *
     * @return 是否正在使用望远镜
     */
    private static boolean isUsingSpyglass() {
        net.minecraft.client.player.LocalPlayer player = net.minecraft.client.Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        return player.isUsingItem() && player.getUseItem().is(net.minecraft.world.item.Items.SPYGLASS);
    }

    /**
     * 渲染纹理星点（外圈光晕 + 内圈亮核）
     * <p>
     * 玩家用望远镜对准某颗星时，该星放大变亮；移开视线恢复。
     *
     * @param matrix     变换矩阵
     * @param tesselator 细分器
     * @param time       动画时间
     * @param alpha      整体透明度
     * @param lookX      相机视线 X
     * @param lookY      相机视线 Y
     * @param lookZ      相机视线 Z
     */
    private void renderTexturedStars(Matrix4f matrix, Tesselator tesselator, float time, float alpha, float lookX, float lookY, float lookZ, float skyAngleRad, boolean aimEnabled, float aimProgress) {
        // 纯纹理 shader（避免 Iris 光影下粒子 shader 顶点色变黑）
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        // 全局望远镜缩放进度（仅控制整体开关，单星放大由 aimState 插值）
        float sizeMult = 1.0F + (AIM_SIZE_MULT - 1.0F) * aimProgress;
        float alphaMult = 1.0F + (AIM_ALPHA_MULT - 1.0F) * aimProgress;
        for (int textureIndex = 0; textureIndex < this.textures.size(); textureIndex++) {
            RenderSystem.setShaderTexture(0, this.textures.get(textureIndex));
            BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            for (int index = 0; index < this.stars.size(); index++) {
                Star star = this.stars.get(index);
                if (star.textureIndex() != textureIndex) {
                    continue;
                }
                // 望远镜对准中心检测（按星大小）→ 更新该星独立的缓慢进入/退出进度
                boolean aimed = aimEnabled && isAimed(star.point(), star.size(), lookX, lookY, lookZ, skyAngleRad);
                if (aimed) {
                    SkyboxRenderer.awardClient("achievement_stargaze");
                }
                aimState[index] = Mth.clamp(aimState[index] + (aimed ? AIM_TRANSITION_SPEED : -AIM_TRANSITION_SPEED), 0.0F, 1.0F);
                float state = aimState[index];
                float twinkle = 0.78F + 0.22F * Mth.sin(time * this.twinkleSpeed + star.phase());
                float angle = star.baseAngle() + time * star.spinSpeed();
                // 对准星缓慢放大变亮（由该星 aimState 平滑驱动）
                float aimedSizeMult = 1.0F + (AIM_TEXTURED_SIZE_MULT - 1.0F) * state;
                float starSize = star.size() * sizeMult * aimedSizeMult;
                float starAlpha = alpha * twinkle * alphaMult * (1.0F + (AIM_ALPHA_MULT - 1.0F) * state);
                SkyGeometry.addTexturedBillboard(buffer, matrix, star.point(), starSize * STAR_HALO, angle);
                SkyGeometry.addTexturedBillboard(buffer, matrix, star.point(), starSize * STAR_CORE, angle);
                // 对准的星额外绘制一层亮核（更亮）
                if (state > 0.01F) {
                    SkyGeometry.addTexturedBillboard(buffer, matrix, star.point(), starSize * STAR_CORE * 1.2F, angle);
                }
            }
            // 该纹理帧没有星点时缓冲为空，安全提交避免崩溃
            SkyGeometry.drawIfNotEmpty(buffer);
        }
    }

    /**
     * 渲染程序化光晕星点（无纹理时，支持望远镜对准）
     *
     * @param matrix     变换矩阵
     * @param tesselator 细分器
     * @param time       动画时间
     * @param alpha      整体透明度
     * @param lookX      相机视线 X
     * @param lookY      相机视线 Y
     * @param lookZ      相机视线 Z
     */
    private void renderGlowStars(Matrix4f matrix, Tesselator tesselator, float time, float alpha, float lookX, float lookY, float lookZ, float skyAngleRad, boolean aimEnabled, float aimProgress) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        // 全局望远镜缩放进度
        float sizeMult = 1.0F + (AIM_SIZE_MULT - 1.0F) * aimProgress;
        float alphaMult = 1.0F + (AIM_ALPHA_MULT - 1.0F) * aimProgress;
        for (int index = 0; index < this.stars.size(); index++) {
            Star star = this.stars.get(index);
            float twinkle = 0.6F + 0.4F * Mth.sin(time * this.twinkleSpeed + star.phase());
            float angle = star.baseAngle() + time * star.spinSpeed();
            // 对准中心检测 → 更新该星独立进度
            boolean aimed = aimEnabled && isAimed(star.point(), star.size(), lookX, lookY, lookZ, skyAngleRad);
            if (aimed) {
                SkyboxRenderer.awardClient("achievement_stargaze");
            }
            aimState[index] = Mth.clamp(aimState[index] + (aimed ? AIM_TRANSITION_SPEED : -AIM_TRANSITION_SPEED), 0.0F, 1.0F);
            float state = aimState[index];
            // 对准星缓慢放大变亮
            float aimedSizeMult = 1.0F + (AIM_SIZE_MULT - 1.0F) * state;
            float aimedAlphaMult = 1.0F + (AIM_ALPHA_MULT - 1.0F) * state;
            float size = star.size() * sizeMult * aimedSizeMult;
            SkyGeometry.addBillboard(buffer, matrix, star.point(), size * 1.7F, angle, this.color, alpha * twinkle * 0.32F * alphaMult * aimedAlphaMult);
            SkyGeometry.addBillboard(buffer, matrix, star.point(), size * 0.85F, angle, this.coreColor, alpha * twinkle * alphaMult * aimedAlphaMult);
        }
        SkyGeometry.drawIfNotEmpty(buffer);
    }

    /**
     * 判断相机视线是否对准某颗星（夹角小于该星的角半径 + 中心容差）
     * <p>
     * ⚠️ 关键：星点坐标是天空球局部坐标，渲染时被 PoseStack 旋转
     * （先 Y(skyAngle) 再 X(-90°)）。此处需对星点坐标应用同样的旋转
     * 得到屏幕实际位置，再与相机视线点积。
     * <p>
     * 阈值按星的大小动态计算：角半径 = size / 天空半径，加 0.008 rad 中心容差
     * ——只有准星真正对准星的中心（落在星本体上）才触发事件。
     *
     * @param point       星的球面位置（天空球局部坐标）
     * @param size        星的尺寸（天空球半径 100 下的长度）
     * @param lookX       视线 X
     * @param lookY       视线 Y
     * @param lookZ       视线 Z
     * @param skyAngleRad 天空旋转角（弧度）
     * @return 是否对准中心
     */
    private static boolean isAimed(SkyPoint point, float size, float lookX, float lookY, float lookZ, float skyAngleRad) {
        float x = point.x();
        float y = point.y();
        float z = point.z();
        float cosA = Mth.cos(skyAngleRad);
        float sinA = Mth.sin(skyAngleRad);
        // 渲染时 PoseStack: mulPose(X(-90)) 后 mulPose(Y(skyAngle))，矩阵右乘 → 先 Y 后 X
        // 先 Y(skyAngle): (x,z) 平面旋转
        float ty = -x * sinA + z * cosA;
        float tx = x * cosA + z * sinA;
        // 再 X(-90°): (a,b,c) -> (a, c, -b)
        float sx = tx;
        float sy = ty;
        float sz = -y;
        // 归一化星点方向（旋转后，与渲染屏幕位置一致）
        float length = Mth.sqrt(sx * sx + sy * sy + sz * sz);
        if (length < 0.001F) {
            return false;
        }
        float dot = (sx * lookX + sy * lookY + sz * lookZ) / length;
        // 动态阈值：星角半径（size/半径）+ 中心容差；对准星中心才触发
        float angle = Math.max(size / SkyGeometry.SKY_RADIUS, 0.004F) + 0.008F;
        return dot > Math.cos(angle);
    }

    /**
     * 渲染十字星标记 —— 对准的星中心出现十字闪光，循环变亮变暗
     * <p>
     * 十字随该星 {@link #aimState} 做**粒子式淡入淡出**：smoothstep 缓动同时
     * 驱动透明度渐入渐出与尺寸从小到大/从大到小（淡入轻盈亮起、淡出缩小消散），
     * 淡入完成后按正弦循环闪烁。高亮使用 golden_particle 金色十字星芒纹理，
     * 三层叠加呈现光晕渐变，十字尺寸随闪烁伸缩且按粒子帧动画逐帧循环。
     *
     * @param matrix     变换矩阵
     * @param tesselator 细分器
     * @param time       动画时间
     * @param lookX      相机视线 X
     * @param lookY      相机视线 Y
     * @param lookZ      相机视线 Z
     * @param skyAngleRad 天空旋转角（弧度）
     * @param aimEnabled 是否启用瞄准（望远镜缩放中）
     */
    private void renderAimCrosshairs(Matrix4f matrix, Tesselator tesselator, float time, float lookX, float lookY, float lookZ, float skyAngleRad, boolean aimEnabled) {
        // 纯纹理 shader（与纹理星点一致，避免 Iris 光影下粒子 shader 顶点色变黑），
        // 透明度由 RenderSystem.setShaderColor 的 alpha 通道逐层控制
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        // golden_particle 三帧动画（与粒子定义 mcmeta frametime=10 对齐），逐帧循环呈现金色闪烁
        ResourceLocation texture = GOLDEN_PARTICLE_FRAMES[
                Math.floorMod((int) (time * CROSS_FRAME_RATE), GOLDEN_PARTICLE_FRAMES.length)
        ];
        RenderSystem.setShaderTexture(0, texture);
        for (int index = 0; index < this.stars.size(); index++) {
            float state = this.aimState[index];
            // 未启用瞄准（未持望远镜）或未对准（含淡出中）的星不显示十字
            if (!aimEnabled || state < 0.01F) {
                continue;
            }
            Star star = this.stars.get(index);
            // 粒子式淡入淡出：smoothstep 缓动（起止都慢，进入轻盈、消散拖尾），
            // 同时驱动透明度渐入渐出与尺寸从小到大/从大到小
            float fade = smoothstep(state);
            // 循环闪烁：淡入完成后在 0.25~1 之间往复（未使用时 aimState=0 整体淡出）
            float flicker = 0.5F + 0.5F * Mth.sin(time * CROSS_FLICKER_SPEED + star.phase());
            float crossAlpha = fade * (0.25F + 0.75F * flicker);
            // 十字臂长：大幅放大（约 3 倍星大小）且随闪烁伸缩，呈现星星发光的辐射感
            float arm = star.size() * (CROSS_ARM_BASE + CROSS_ARM_PULSE * flicker);
            // 纹理十字臂未填满整张图 → 按占比放大广告牌半边长，使十字视觉跨度与原版一致；
            // 叠加粒子式缩放：淡入时从最小 55% 放大到 100%，淡出时反向缩小
            float scale = CROSS_FADE_MIN_SCALE + (1.0F - CROSS_FADE_MIN_SCALE) * fade;
            float size = arm / CROSS_TEXTURE_FILL * scale;
            // 三层由外到内叠加（对应原三层线段光晕）：宽光晕(暗) → 中光晕(中亮) → 亮核(最亮)
            drawCrossLayer(tesselator, matrix, star.point(), size * 1.45F, crossAlpha * 0.28F);
            drawCrossLayer(tesselator, matrix, star.point(), size * 1.18F, crossAlpha * 0.60F);
            drawCrossLayer(tesselator, matrix, star.point(), size * 1.00F, crossAlpha);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * 绘制一层 golden_particle 十字纹理广告牌
     * <p>
     * 广告牌以星中心为球面基准、半边长 {@code size}、无自旋（十字臂沿 yaw/pitch
     * 正交方向），透明度通过 {@link RenderSystem#setShaderColor} 的 alpha 通道控制
     * （POSITION_TEX 无顶点色，与纹理星点方案一致）。
     *
     * @param tesselator 细分器
     * @param matrix     变换矩阵
     * @param center     星中心（球面位置）
     * @param size       广告牌半边长
     * @param alpha      透明度（0~1）
     */
    private void drawCrossLayer(Tesselator tesselator, Matrix4f matrix, SkyPoint center, float size, float alpha) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        SkyGeometry.addTexturedBillboard(buffer, matrix, center, size, 0.0F);
        SkyGeometry.drawIfNotEmpty(buffer);
    }

    /**
     * smoothstep 缓动（三次平滑曲线）
     * <p>
     * 输入 0~1 线性进度，输出 0~1 缓动曲线：起点与终点斜率均为 0
     * （渐入渐出自然，无生硬突变），用于粒子式淡入淡出动画。
     *
     * @param t 线性进度（0~1，越界自动截断）
     * @return 缓动后进度（0~1）
     */
    private static float smoothstep(float t) {
        float x = Mth.clamp(t, 0.0F, 1.0F);
        return x * x * (3.0F - 2.0F * x);
    }

    /**
     * 判断天空局部方向附近是否存在星座星点（供占卜/观星交互检测）
     * <p>
     * 方向比较用归一化点积：点积 &gt; {@code cos(threshold)} 即视为"对准"。
     *
     * @param localLook 天空局部空间方向（无需乘半径）
     * @param threshold 对准夹角阈值（弧度）
     * @return 是否有星点在该方向附近
     */
    public boolean containsStarNear(SkyPoint localLook, float threshold) {
        float lookLen = localLook.length();
        if (lookLen < 0.001F) {
            return false;
        }
        float cosThreshold = Mth.cos(threshold);
        for (Star star : this.stars) {
            SkyPoint p = star.point();
            float len = p.length();
            if (len < 0.001F) {
                continue;
            }
            float dot = (p.x() * localLook.x() + p.y() * localLook.y() + p.z() * localLook.z()) / (len * lookLen);
            if (dot > cosThreshold) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将形状节点映射为星点列表
     *
     * @param shape        形状
     * @param centerYaw    中心偏航角
     * @param centerPitch  中心俯仰角
     * @param scale        缩放
     * @param textureCount 纹理数
     * @param seed         种子
     * @return 星点列表
     */
    private static List<Star> makeStars(Shape shape, float centerYaw, float centerPitch, float scale, int textureCount, long seed) {
        LegacyRandomSource random = new LegacyRandomSource(seed);
        List<Star> stars = new ArrayList<>();
        List<Node> nodes = shape.nodes();
        for (int index = 0; index < nodes.size(); index++) {
            Node node = nodes.get(index);
            SkyPoint point = SkyGeometry.point(centerYaw + node.u() * scale, centerPitch + node.v() * scale);
            float phase = index * 1.7F + (float) Math.floorMod(seed, 628L) / 100.0F;
            int textureIndex = textureCount > 0 ? random.nextInt(textureCount) : 0;
            float baseAngle = random.nextFloat() * 6.2831855F;
            float spinMagnitude = 0.008F + random.nextFloat() * 0.014F;
            float spinSpeed = random.nextBoolean() ? spinMagnitude : -spinMagnitude;
            stars.add(new Star(point, node.size(), phase, textureIndex, baseAngle, spinSpeed));
        }
        return stars;
    }

    /**
     * 颜色提亮
     *
     * @param color  原色
     * @param amount 提亮比例
     * @return 提亮后颜色
     */
    private static SkyColor brighten(SkyColor color, float amount) {
        return new SkyColor(
                Mth.lerp(amount, color.red(), 1.0F),
                Mth.lerp(amount, color.green(), 1.0F),
                Mth.lerp(amount, color.blue(), 1.0F)
        );
    }

    /**
     * 星座连线
     *
     * @param from 起点节点索引
     * @param to   终点节点索引
     */
    public record Link(int from, int to) {
    }

    /**
     * 星座节点（相对坐标）
     *
     * @param u    横向相对坐标
     * @param v    纵向相对坐标
     * @param size 星点尺寸
     */
    public record Node(float u, float v, float size) {
    }

    /**
     * 星座形状
     *
     * @param nodes 节点列表
     * @param links 连线列表
     */
    public record Shape(List<Node> nodes, List<Link> links) {
    }

    /**
     * 星点静态属性
     *
     * @param point        球面位置
     * @param size         尺寸
     * @param phase        闪烁相位
     * @param textureIndex 纹理索引
     * @param baseAngle    基础自旋角
     * @param spinSpeed    自旋速度
     */
    private record Star(SkyPoint point, float size, float phase, int textureIndex, float baseAngle, float spinSpeed) {
    }
}
