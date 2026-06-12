package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * 衍梦粉尘粒子 (Dreamfertiliter Falling Particle)
 * <p>
 * 用于温暖平原生物群系，模拟染梦粉尘从天空中缓缓飘落的效果。
 * 具有轻微重力缓慢下落、水平摇曳和淡入淡出的梦幻特性。
 */
public class DreamfertiliterFallingParticle extends TextureSheetParticle {

    private static final int FADE_IN_TICKS = 20;
    private static final int FADE_OUT_TICKS = 40;

    /**
     * 构造衍梦粉尘粒子
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
    protected DreamfertiliterFallingParticle(ClientLevel level, double x, double y, double z,
                                              double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);
        this.setSprite(spriteSet.get(this.random));
        this.setSize(0.15f, 0.15f);
        this.quadSize = 0.2f + this.random.nextFloat() * 0.3f;
        this.lifetime = 160 + this.random.nextInt(80);
        this.gravity = 0.008f;
        this.hasPhysics = true;

        this.xd = vx + (this.random.nextDouble() - 0.5) * 0.003;
        this.yd = -0.005 - this.random.nextDouble() * 0.01;
        this.zd = vz + (this.random.nextDouble() - 0.5) * 0.003;

        this.alpha = 0f;
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

        this.yd -= 0.04 * this.gravity;

        double swayAngle = this.age * 0.03 + this.x * 0.05;
        this.xd += Math.sin(swayAngle) * 0.001;
        this.zd += Math.cos(swayAngle * 0.7 + 1.0) * 0.001;

        this.move(this.xd, this.yd, this.zd);

        if (this.onGround) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /**
     * 衍梦粉尘粒子的工厂类
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
            return new DreamfertiliterFallingParticle(level, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}