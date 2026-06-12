package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * 梦境环境粒子 (Dream Ambient Particle)
 * <p>
 * 在染梦维度中持续生成的漂浮梦幻粉尘效果。
 * 具有缓慢上浮、轻柔漂移、渐隐渐现的梦境般运动特性。
 * 颜色在淡粉、淡紫、淡蓝等梦幻色系中随机选取。
 */
public class DreamAmbientParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    private static final float[][] PASTEL_COLORS = {
            {1.0f, 0.78f, 0.92f},
            {0.85f, 0.75f, 1.0f},
            {0.75f, 0.85f, 1.0f},
            {1.0f, 0.75f, 0.85f},
            {0.90f, 0.80f, 1.0f},
            {0.80f, 1.0f, 0.95f}
    };

    private static final int FADE_IN_TICKS = 30;
    private static final int FADE_OUT_TICKS = 60;

    /**
     * 构造梦境环境粒子
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
    protected DreamAmbientParticle(ClientLevel level, double x, double y, double z,
                                    double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = spriteSet;
        this.gravity = -0.002f;
        this.hasPhysics = false;

        int colorIndex = this.random.nextInt(PASTEL_COLORS.length);
        this.rCol = PASTEL_COLORS[colorIndex][0];
        this.gCol = PASTEL_COLORS[colorIndex][1];
        this.bCol = PASTEL_COLORS[colorIndex][2];

        this.lifetime = 160 + this.random.nextInt(120);
        this.quadSize = 0.2f + this.random.nextFloat() * 0.4f;

        this.alpha = 0.0f;

        this.xd = (this.random.nextDouble() - 0.5) * 0.02;
        this.yd = 0.01 + this.random.nextDouble() * 0.03;
        this.zd = (this.random.nextDouble() - 0.5) * 0.02;

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

        float ageRatio = (float) this.age / this.lifetime;

        if (ageRatio < (float) FADE_IN_TICKS / this.lifetime) {
            this.alpha = Math.min(1.0f, ageRatio * this.lifetime / FADE_IN_TICKS);
        } else if (ageRatio > 1.0f - (float) FADE_OUT_TICKS / this.lifetime) {
            this.alpha = Math.max(0.0f, (1.0f - ageRatio) * this.lifetime / FADE_OUT_TICKS);
        } else {
            this.alpha = 1.0f;
        }

        double swayAngle = this.age * 0.05;
        this.xd += Math.sin(swayAngle) * 0.001;
        this.zd += Math.cos(swayAngle * 0.7 + 1.0) * 0.001;
        this.xd *= 0.98;
        this.zd *= 0.98;

        if (this.age % 10 == 0) {
            this.quadSize += (this.random.nextFloat() - 0.5f) * 0.02f;
            this.quadSize = Math.max(0.1f, Math.min(0.8f, this.quadSize));
        }

        this.move(this.xd, this.yd, this.zd);

        if (this.onGround) {
            this.yd = Math.abs(this.yd) * 0.5f + 0.01;
            this.move(0, 0.05, 0);
        }

        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /**
     * 梦境环境粒子的工厂类
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
            return new DreamAmbientParticle(level, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}