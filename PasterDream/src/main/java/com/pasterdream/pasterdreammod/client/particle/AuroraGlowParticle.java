package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * 极光粒子 (Aurora Glow Particle)
 * <p>
 * 用于染梦维度深海和极地生物群系的环境粒子效果。
 * 半透明发光粒子水平缓慢飘动，具有以下特性：
 * <ul>
 *   <li>颜色在青/蓝/紫之间正弦渐变，模拟微缩极光</li>
 *   <li>水平方向缓慢漂移，几乎不升降</li>
 *   <li>加法混合渲染，产生朦胧发光光晕</li>
 *   <li>全程全亮度，在任何光照下都可见</li>
 * </ul>
 */
public class AuroraGlowParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    /** 极光色系调色板 */
    private static final float[][] AURORA_COLORS = {
            {0.4f, 0.7f, 1.0f},   // 天蓝
            {0.3f, 0.5f, 1.0f},   // 靛蓝
            {0.5f, 0.3f, 1.0f},   // 紫蓝
            {0.3f, 0.8f, 1.0f},   // 青蓝
            {0.6f, 0.4f, 1.0f}    // 紫罗兰
    };

    /** 淡入时间（tick数） */
    private static final int FADE_IN_TICKS = 35;
    /** 淡出时间（tick数） */
    private static final int FADE_OUT_TICKS = 60;

    /** 生命周期内的颜色偏移相位 */
    private final float colorPhase;
    /** 水平漂移相位 */
    private final float driftPhase;

    /**
     * 构造极光粒子
     *
     * @param level     客户端世界
     * @param x         初始 X 坐标
     * @param y         初始 Y 坐标
     * @param z         初始 Z 坐标
     * @param vx        X 速度
     * @param vy        Y 速度
     * @param vz        Z 速度
     * @param spriteSet 精灵表集合
     */
    protected AuroraGlowParticle(ClientLevel level, double x, double y, double z,
                                  double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = spriteSet;
        this.setSize(0.3f, 0.3f);
        this.quadSize = 0.3f + this.random.nextFloat() * 0.5f;
        this.lifetime = 200 + this.random.nextInt(160);

        // 接近零的重力 -> 水平浮动
        this.gravity = -0.002f;
        this.hasPhysics = false;

        // 随机选择基础极光色
        int colorIndex = this.random.nextInt(AURORA_COLORS.length);
        this.rCol = AURORA_COLORS[colorIndex][0];
        this.gCol = AURORA_COLORS[colorIndex][1];
        this.bCol = AURORA_COLORS[colorIndex][2];

        // 初始速度：水平缓慢漂移，垂直几乎不动
        this.xd = vx + (this.random.nextDouble() - 0.5) * 0.003;
        this.yd = (this.random.nextDouble() - 0.5) * 0.002;
        this.zd = vz + (this.random.nextDouble() - 0.5) * 0.003;

        // 随机相位
        this.colorPhase = this.random.nextFloat() * (float) (Math.PI * 2);
        this.driftPhase = this.random.nextFloat() * (float) (Math.PI * 2);

        // 初始透明
        this.alpha = 0.0f;

        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        // ===== 透明度控制 =====
        float ageRatio = (float) this.age / this.lifetime;
        if (ageRatio < (float) FADE_IN_TICKS / this.lifetime) {
            this.alpha = Math.min(0.7f, ageRatio * this.lifetime / FADE_IN_TICKS);
        } else if (ageRatio > 1.0f - (float) FADE_OUT_TICKS / this.lifetime) {
            this.alpha = Math.max(0.0f, (1.0f - ageRatio) * this.lifetime / FADE_OUT_TICKS);
        } else {
            // 保持期：缓慢脉冲
            float pulse = (float) (Math.sin(this.age * 0.05 + this.driftPhase) * 0.1f);
            this.alpha = Math.min(0.7f, 0.55f + pulse);
        }

        // ===== 颜色渐变：基础色随时间正弦偏移 =====
        float colorShift = (float) (Math.sin(this.age * 0.02 + this.colorPhase) * 0.3f);
        int baseIndex = (int) (this.age * 0.01) % AURORA_COLORS.length;

        // 缓慢的颜色插值变化
        float shiftR = (float) Math.sin(this.age * 0.015 + this.colorPhase) * 0.2f;
        float shiftG = (float) Math.cos(this.age * 0.012 + this.colorPhase * 0.7f) * 0.15f;
        float shiftB = (float) Math.sin(this.age * 0.018 + this.colorPhase * 1.3f) * 0.2f;

        this.rCol = Math.max(0.2f, Math.min(1.0f, AURORA_COLORS[baseIndex][0] + shiftR));
        this.gCol = Math.max(0.2f, Math.min(1.0f, AURORA_COLORS[baseIndex][1] + shiftG));
        this.bCol = Math.max(0.2f, Math.min(1.0f, AURORA_COLORS[baseIndex][2] + shiftB));

        // ===== 水平漂移：大圆弧漂移模拟极光流动感 =====
        double driftAngle = this.age * 0.02 + this.driftPhase;
        this.xd += Math.sin(driftAngle) * 0.0005;
        this.zd += Math.cos(driftAngle * 0.5 + 0.8) * 0.0005;
        this.xd *= 0.99;
        this.zd *= 0.99;

        // ===== 缓慢上下浮动 =====
        this.yd = Math.sin(this.age * 0.025 + this.driftPhase) * 0.002;

        this.move(this.xd, this.yd, this.zd);

        // 更新精灵帧
        if (this.age % 8 == 0) {
            this.setSpriteFromAge(this.sprites);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return PDParticleRenderTypes.GLOWING_SHEET;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    /**
     * 极光粒子的工厂类
     */
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new AuroraGlowParticle(level, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}
