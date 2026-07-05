package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * 梦幻孢子粒子 (Dream Spore Particle)
 * <p>
 * 用于染梦维度梦幻平原生物群系的环境粒子效果。
 * 紫色/粉色渐变发光孢子，具有以下特性：
 * <ul>
 *   <li>缓慢旋转飘浮，模拟孢子在空中漂浮</li>
 *   <li>颜色在粉/紫/淡蓝梦幻色系中随机选取</li>
 *   <li>带有柔和呼吸发光效果（加法混合渲染）</li>
 *   <li>全程保持全亮度渲染，夜晚可见度最高</li>
 * </ul>
 */
public class DreamSporeParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    /** 梦幻色系调色板（粉/紫/淡蓝） */
    private static final float[][] DREAM_COLORS = {
            {1.0f, 0.65f, 0.85f},  // 粉红
            {0.85f, 0.55f, 1.0f},  // 淡紫
            {0.65f, 0.55f, 1.0f},  // 紫蓝
            {1.0f, 0.55f, 0.75f},  // 暖粉
            {0.75f, 0.60f, 1.0f}   // 紫罗兰
    };

    /** 淡入时间（tick数） */
    private static final int FADE_IN_TICKS = 25;
    /** 淡出时间（tick数） */
    private static final int FADE_OUT_TICKS = 50;

    /** 旋转角速度 */
    private float angularVelocity;

    /**
     * 构造梦幻孢子粒子
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
    protected DreamSporeParticle(ClientLevel level, double x, double y, double z,
                                  double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = spriteSet;
        this.setSize(0.2f, 0.2f);
        this.quadSize = 0.25f + this.random.nextFloat() * 0.35f;
        this.lifetime = 160 + this.random.nextInt(100);

        // 负重力 = 上浮
        this.gravity = -0.003f;
        this.hasPhysics = false;

        // 随机选取梦幻色系
        int colorIndex = this.random.nextInt(DREAM_COLORS.length);
        this.rCol = DREAM_COLORS[colorIndex][0];
        this.gCol = DREAM_COLORS[colorIndex][1];
        this.bCol = DREAM_COLORS[colorIndex][2];

        // 初始速度：缓慢随机漂移 + 缓慢上浮
        this.xd = vx + (this.random.nextDouble() - 0.5) * 0.006;
        this.yd = 0.005 + this.random.nextDouble() * 0.015;
        this.zd = vz + (this.random.nextDouble() - 0.5) * 0.006;

        // 随机旋转速度
        this.angularVelocity = (this.random.nextFloat() - 0.5f) * 0.08f;

        // 初始透明（淡入用）
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

        // ===== 透明度控制：淡入 → 保持 → 淡出 =====
        float ageRatio = (float) this.age / this.lifetime;
        if (ageRatio < (float) FADE_IN_TICKS / this.lifetime) {
            this.alpha = Math.min(0.85f, ageRatio * this.lifetime / FADE_IN_TICKS);
        } else if (ageRatio > 1.0f - (float) FADE_OUT_TICKS / this.lifetime) {
            this.alpha = Math.max(0.0f, (1.0f - ageRatio) * this.lifetime / FADE_OUT_TICKS);
        } else {
            // 呼吸效果：在保持期做正弦微变
            float breath = (float) (Math.sin(this.age * 0.08) * 0.08f);
            this.alpha = Math.min(0.85f, 0.75f + breath);
        }

        // ===== 飘浮运动：正弦摆动模拟空中漂浮 =====
        double swayAngle = this.age * 0.04;
        this.xd += Math.sin(swayAngle) * 0.0008;
        this.zd += Math.cos(swayAngle * 0.7 + 1.2) * 0.0008;
        this.xd *= 0.98;
        this.zd *= 0.98;

        // ===== 旋转动画 =====
        this.oRoll = this.roll;
        this.roll += this.angularVelocity;

        // ===== 缓慢大小变化 =====
        if (this.age % 8 == 0) {
            this.quadSize += (this.random.nextFloat() - 0.5f) * 0.015f;
            this.quadSize = Math.max(0.15f, Math.min(0.7f, this.quadSize));
        }

        this.move(this.xd, this.yd, this.zd);

        // 碰到地面时弹起
        if (this.onGround) {
            this.yd = Math.abs(this.yd) * 0.5f + 0.008;
            this.move(0, 0.03, 0);
        }

        // 更新精灵帧
        if (this.age % 5 == 0) {
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
     * 梦幻孢子粒子的工厂类
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
            return new DreamSporeParticle(level, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}
