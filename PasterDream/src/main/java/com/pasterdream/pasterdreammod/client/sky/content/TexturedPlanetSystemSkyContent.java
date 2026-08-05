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
import org.joml.Matrix4f;

import java.util.List;

/**
 * 纹理行星系统内容 —— 带纹理的行星与其公转卫星
 * <p>
 * 行星以广告牌绘制并缓慢自转；卫星在行星的切空间平面内
 * 绕行公转（轨道半径、速度、相位可配），投影回天空球面。
 */
public class TexturedPlanetSystemSkyContent implements SkyContent {

    private final ResourceLocation id;
    private final int priority;
    private final List<Planet> planets;

    /**
     * @param id       内容标识
     * @param priority 绘制优先级
     * @param planets  行星列表
     */
    public TexturedPlanetSystemSkyContent(ResourceLocation id, int priority, List<Planet> planets) {
        this.id = id;
        this.priority = priority;
        this.planets = List.copyOf(planets);
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
    public float targetAlpha(SkyboxRenderContext context) {
        // 行星/卫星全天可见（白天像月亮一样淡一些，夜晚全亮）
        return Mth.clamp(context.weatherFactor() * (0.55F + 0.45F * context.nightFactor()), 0.0F, 1.0F);
    }

    @Override
    public void render(SkyboxRenderContext context, float alpha) {
        // 用 getPositionTexShader（纯纹理 shader，无光照）——Iris 光影下
        // getParticleShader 的顶点色会被光照变黑（星星/行星黑色根因）。
        // 行星纹理是实心圆（圆内 alpha=255、圆外全透明），标准混合下实心显示，
        // 透明度由纹理 alpha 控制。顶点色用白色（无着色）。
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Matrix4f matrix = context.poseStack().last().pose();
        Tesselator tesselator = Tesselator.getInstance();

        for (Planet planet : this.planets) {
            SkyPoint center = SkyGeometry.point(planet.yaw(), planet.pitch());
            RenderSystem.setShaderTexture(0, planet.texture());
            BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            SkyGeometry.addTexturedBillboard(
                    buffer, matrix, center, planet.size(),
                    planet.roll() + context.renderTime() * planet.rollSpeed()
            );
            BufferUploader.drawWithShader(buffer.buildOrThrow());

            for (Satellite satellite : planet.satellites()) {
                float orbitAngle = satellite.orbitOffset() + context.renderTime() * satellite.orbitSpeed();
                SkyPoint satelliteCenter = orbit(center, orbitAngle, satellite.orbitRadius());
                RenderSystem.setShaderTexture(0, satellite.texture());
                BufferBuilder satBuffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                SkyGeometry.addTexturedBillboard(
                        satBuffer, matrix, satelliteCenter, satellite.size(),
                        orbitAngle + satellite.roll()
                );
                BufferUploader.drawWithShader(satBuffer.buildOrThrow());
            }
        }
        RenderSystem.enableCull();
    }

    /**
     * 计算卫星绕行星中心的轨道位置
     * <p>
     * 以行星方向向量为法线构建切空间（tangent/bitangent），
     * 轨道圆在其平面内，结果重投影回半径 100 球面。
     *
     * @param center     行星球面位置
     * @param angle      轨道角（弧度）
     * @param radius     轨道半径
     * @return 卫星球面位置
     */
    private static SkyPoint orbit(SkyPoint center, float angle, float radius) {
        float normalX = center.x() / 100.0F;
        float normalY = center.y() / 100.0F;
        float normalZ = center.z() / 100.0F;
        // 切向量（与法线正交）
        float tangentX;
        float tangentY = 0.0F;
        float tangentZ = -normalX;
        float tangentLength = Mth.sqrt(normalZ * normalZ + tangentZ * tangentZ);
        if (tangentLength < 0.001F) {
            tangentX = 1.0F;
            tangentZ = 0.0F;
        } else {
            tangentX = normalZ / tangentLength;
            tangentZ /= tangentLength;
        }
        // 副切向量 = 法线 × 切向量
        float bitangentX = normalY * tangentZ - normalZ * tangentY;
        float bitangentY = normalZ * tangentX - normalX * tangentZ;
        float bitangentZ = normalX * tangentY - normalY * tangentX;

        float offsetX = (tangentX * Mth.cos(angle) + bitangentX * Mth.sin(angle)) * radius;
        float offsetY = (tangentY * Mth.cos(angle) + bitangentY * Mth.sin(angle)) * radius;
        float offsetZ = (tangentZ * Mth.cos(angle) + bitangentZ * Mth.sin(angle)) * radius;

        float x = center.x() + offsetX;
        float y = center.y() + offsetY;
        float z = center.z() + offsetZ;
        float length = 100.0F / Mth.sqrt(x * x + y * y + z * z);
        return new SkyPoint(x * length, y * length, z * length);
    }

    /**
     * 行星定义
     *
     * @param texture   纹理
     * @param yaw       偏航角（弧度）
     * @param pitch     俯仰角（弧度）
     * @param size      尺寸
     * @param roll      初始自旋角
     * @param rollSpeed 自旋速度
     * @param color     着色颜色
     * @param opacity   不透明度
     * @param satellites 卫星列表
     */
    public record Planet(
            ResourceLocation texture, float yaw, float pitch, float size,
            float roll, float rollSpeed, SkyColor color, float opacity,
            List<Satellite> satellites
    ) {
    }

    /**
     * 卫星定义
     *
     * @param texture     纹理
     * @param size        尺寸
     * @param orbitRadius 轨道半径
     * @param orbitSpeed  轨道角速度
     * @param orbitOffset 轨道初始相位
     * @param roll        自旋角
     * @param color       着色颜色
     * @param opacity     不透明度
     */
    public record Satellite(
            ResourceLocation texture, float size, float orbitRadius,
            float orbitSpeed, float orbitOffset, float roll, SkyColor color, float opacity
    ) {
    }
}
