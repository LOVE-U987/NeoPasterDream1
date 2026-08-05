package com.pasterdream.pasterdreammod.client.sky.content;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.pasterdream.pasterdreammod.api.client.sky.SkyContent;
import com.pasterdream.pasterdreammod.api.client.sky.SkyboxRenderContext;
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

    /** 星点核心亮化系数 */
    private static final float STAR_CORE = 0.7F;
    /** 星点光晕倍率 */
    private static final float STAR_HALO = 1.5F;

    private final ResourceLocation id;
    private final int priority;
    private final SkyColor color;
    private final SkyColor coreColor;
    private final float twinkleSpeed;
    private final float lineWidth;
    private final List<ResourceLocation> textures;
    private final List<Star> stars;
    private final List<Link> links;

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
        Matrix4f matrix = context.poseStack().last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        float time = context.renderTime();
        float pulse = 0.7F + 0.3F * Mth.sin(time * this.twinkleSpeed * 0.5F);
        RenderSystem.disableCull();
        // 与 Stellara 一致：不设置混合模式，依赖外层 SkyboxRenderer 统一的标准混合

        // 连线
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder lineBuffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (Link link : this.links) {
            if (link.from() < this.stars.size() && link.to() < this.stars.size()) {
                SkyGeometry.addLine(
                        lineBuffer, matrix,
                        this.stars.get(link.from()).point(), this.stars.get(link.to()).point(),
                        this.lineWidth, this.color, alpha * pulse * 0.5F
                );
            }
        }
        BufferUploader.drawWithShader(lineBuffer.buildOrThrow());

        // 星点
        if (this.textures.isEmpty()) {
            renderGlowStars(matrix, tesselator, time, alpha);
        } else {
            renderTexturedStars(matrix, tesselator, time, alpha);
        }

        RenderSystem.enableCull();
    }

    /**
     * 渲染纹理星点（外圈光晕 + 内圈亮核）
     *
     * @param matrix    变换矩阵
     * @param tesselator 细分器
     * @param time      动画时间
     * @param alpha     整体透明度
     */
    private void renderTexturedStars(Matrix4f matrix, Tesselator tesselator, float time, float alpha) {
        RenderSystem.setShader(GameRenderer::getParticleShader);
        for (int textureIndex = 0; textureIndex < this.textures.size(); textureIndex++) {
            RenderSystem.setShaderTexture(0, this.textures.get(textureIndex));
            BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            for (Star star : this.stars) {
                if (star.textureIndex() == textureIndex) {
                    float twinkle = 0.78F + 0.22F * Mth.sin(time * this.twinkleSpeed + star.phase());
                    float angle = star.baseAngle() + time * star.spinSpeed();
                    SkyGeometry.addTexturedBillboard(buffer, matrix, star.point(), star.size() * STAR_HALO, angle, this.color, alpha * twinkle * 0.5F);
                    SkyGeometry.addTexturedBillboard(buffer, matrix, star.point(), star.size() * STAR_CORE, angle, this.coreColor, alpha * twinkle);
                }
            }
            BufferUploader.drawWithShader(buffer.buildOrThrow());
        }
    }

    /**
     * 渲染程序化光晕星点（无纹理时）
     *
     * @param matrix     变换矩阵
     * @param tesselator 细分器
     * @param time       动画时间
     * @param alpha      整体透明度
     */
    private void renderGlowStars(Matrix4f matrix, Tesselator tesselator, float time, float alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        for (Star star : this.stars) {
            float twinkle = 0.6F + 0.4F * Mth.sin(time * this.twinkleSpeed + star.phase());
            float angle = star.baseAngle() + time * star.spinSpeed();
            SkyGeometry.addBillboard(buffer, matrix, star.point(), star.size() * 1.7F, angle, this.color, alpha * twinkle * 0.32F);
            SkyGeometry.addBillboard(buffer, matrix, star.point(), star.size() * 0.85F, angle, this.coreColor, alpha * twinkle);
        }
        BufferUploader.drawWithShader(buffer.buildOrThrow());
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
