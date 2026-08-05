package com.pasterdream.pasterdreammod.client.sky.content;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
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
 * 星域内容 —— 数量庞大的纹理星星，聚团分布并闪烁自旋
 * <p>
 * 每颗星从纹理列表中随机取一帧（支持 7 帧动画循环），在单位球上
 * 按聚类分布，逐帧呼吸闪烁（正弦脉动）并缓慢自旋。
 */
public class StarFieldSkyContent implements SkyContent {

    private final ResourceLocation id;
    private final int priority;
    private final List<ResourceLocation> textures;
    private final SkyColor color;
    private final List<Star> stars;

    /**
     * @param id       内容标识
     * @param priority 绘制优先级
     * @param textures 星星纹理列表（多帧动画帧）
     * @param count    星星数量
     * @param minSize  最小尺寸
     * @param maxSize  最大尺寸
     * @param color    着色颜色
     * @param seed     随机种子
     */
    public StarFieldSkyContent(
            ResourceLocation id, int priority, List<ResourceLocation> textures,
            int count, float minSize, float maxSize, SkyColor color, long seed
    ) {
        this.id = id;
        this.priority = priority;
        this.textures = List.copyOf(textures);
        this.color = color;
        this.stars = makeStars(count, minSize, maxSize, textures.size(), seed);
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
        // 用 getPositionTexShader（纯纹理 shader，无光照）——Iris 光影下
        // getParticleShader 的顶点色会被光照变黑。纹理自带染梦群系色。
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Matrix4f matrix = context.poseStack().last().pose();
        Tesselator tesselator = Tesselator.getInstance();

        // 按纹理帧分批绘制，减少纹理切换
        for (int textureIndex = 0; textureIndex < this.textures.size(); textureIndex++) {
            RenderSystem.setShaderTexture(0, this.textures.get(textureIndex));
            BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            for (Star star : this.stars) {
                if (star.textureIndex() == textureIndex) {
                    float pulse = 0.85F + 0.15F * Mth.sin(context.renderTime() * star.pulseSpeed() + star.pulseOffset());
                    SkyGeometry.addTexturedBillboard(
                            buffer, matrix, star.point(),
                            star.size() * pulse,
                            star.angle() + context.renderTime() * star.spinSpeed()
                    );
                }
            }
            // 该帧没有任何星星时缓冲为空，安全提交避免崩溃
            SkyGeometry.drawIfNotEmpty(buffer);
        }

        RenderSystem.enableCull();
    }

    /**
     * 生成星星列表（聚类分布 + 随机属性）
     *
     * @param count        数量
     * @param minSize      最小尺寸
     * @param maxSize      最大尺寸
     * @param textureCount 纹理帧数
     * @param seed         种子
     * @return 星星列表
     */
    private static List<Star> makeStars(int count, float minSize, float maxSize, int textureCount, long seed) {
        LegacyRandomSource random = new LegacyRandomSource(seed);
        StarClusters clusters = StarClusters.make(count, random);
        List<Star> stars = new ArrayList<>(count);
        int attempts = 0;
        while (stars.size() < count && attempts < count * 4) {
            SkyPoint point = clusters.sample(random);
            attempts++;
            if (point == null) {
                continue;
            }
            stars.add(new Star(
                    point,
                    minSize + random.nextFloat() * (maxSize - minSize),
                    random.nextFloat() * 6.2831855F,
                    random.nextFloat() * 6.2831855F,
                    0.025F + random.nextFloat() * 0.055F,
                    -0.014F + random.nextFloat() * 0.028F,
                    random.nextInt(Math.max(1, textureCount))
            ));
        }
        return stars;
    }

    /**
     * 单颗星星的静态属性
     *
     * @param point        球面位置
     * @param size         基础尺寸
     * @param angle        初始自旋角
     * @param pulseOffset  脉动相位偏移
     * @param pulseSpeed   脉动速度
     * @param spinSpeed    自旋速度（可负）
     * @param textureIndex 纹理帧索引
     */
    private record Star(SkyPoint point, float size, float angle, float pulseOffset, float pulseSpeed, float spinSpeed, int textureIndex) {
    }
}
