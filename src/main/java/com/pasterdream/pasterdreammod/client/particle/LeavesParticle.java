package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * 落叶粒子 (Leaves Particle)
 * <p>
 * 用于温暖平原生物群系，模拟树叶从树上飘落的自然效果。
 * 具有重力（缓慢下落）、水平摇曳、自旋旋转和碰撞检测等物理特性。
 * 落地后经过短暂的停留动画后消失。
 */
public class LeavesParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private float angularVelocity;
    private int groundTicks;

    private static final int MAX_GROUND_TICKS = 60;

    /**
     * 构造落叶粒子
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
    protected LeavesParticle(ClientLevel level, double x, double y, double z,
                              double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = spriteSet;
        this.setSize(0.2f, 0.2f);
        this.quadSize = 0.18f + this.random.nextFloat() * 0.12f;
        this.lifetime = 200 + this.random.nextInt(80);
        this.gravity = 0.02f;
        this.hasPhysics = true;
        this.groundTicks = 0;

        this.xd = vx + (this.random.nextDouble() - 0.5) * 0.004;
        this.yd = -0.01 - this.random.nextDouble() * 0.02;
        this.zd = vz + (this.random.nextDouble() - 0.5) * 0.004;

        this.angularVelocity = (this.random.nextFloat() - 0.5f) * 0.08f;

        this.pickSprite(spriteSet);
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

        if (this.onGround) {
            this.groundTicks++;
            this.xd *= 0.7;
            this.zd *= 0.7;
            this.yd = 0;
            if (this.groundTicks > MAX_GROUND_TICKS) {
                this.remove();
                return;
            }
            float fadeRatio = 1.0f - (float) this.groundTicks / MAX_GROUND_TICKS;
            this.alpha = Math.max(0.0f, fadeRatio);
            this.setSpriteFromAge(this.sprites);
            return;
        }

        float ageRatio = (float) this.age / this.lifetime;

        this.yd -= 0.04 * this.gravity;

        double swayAngle = this.age * 0.04 + this.x * 0.1;
        this.xd += Math.sin(swayAngle) * 0.002;
        this.zd += Math.cos(swayAngle * 0.6 + 1.0) * 0.002;

        this.oRoll = this.roll;
        this.roll += this.angularVelocity;

        this.move(this.xd, this.yd, this.zd);

        this.alpha = Math.min(1.0f, ageRatio * 5.0f);

        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    /**
     * 落叶粒子的工厂类
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
            return new LeavesParticle(level, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}