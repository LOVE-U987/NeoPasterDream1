package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 尘埃粒子 0 (Dust 0 Particle)
 * <p>
 * 还原自原版 PasterDream 的 dust_0_particle，用于梦境炼药锅炼制过程等场景的尘雾效果。
 * 行为与原版一致：
 * <ul>
 *   <li>短寿命（约 10-20 tick），无重力，带物理碰撞</li>
 *   <li>初速度为发射速度的 0.3 倍</li>
 *   <li>缓慢自转（角速度 0.01 rad/tick）</li>
 * </ul>
 */
public class Dust0Particle extends TextureSheetParticle {

    /** 每 tick 自转角速度（弧度） */
    private static final float ANGULAR_VELOCITY = 0.01f;

    /**
     * 构造尘埃粒子
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
    protected Dust0Particle(ClientLevel level, double x, double y, double z,
                            double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.setSize(0.2f, 0.2f);
        this.quadSize *= 1.1f;
        this.lifetime = Math.max(1, 15 + (this.random.nextInt(10) - 5));
        this.gravity = 0f;
        this.hasPhysics = true;
        this.xd = vx * 0.3;
        this.yd = vy * 0.3;
        this.zd = vz * 0.3;
        this.pickSprite(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void tick() {
        super.tick();
        // 缓慢自转（与原版一致，角加速度为 0）
        this.oRoll = this.roll;
        this.roll += ANGULAR_VELOCITY;
    }

    /**
     * 尘埃粒子提供器
     */
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        /**
         * 构造粒子提供器
         *
         * @param spriteSet 精灵表集合
         */
        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new Dust0Particle(level, x, y, z, vx, vy, vz, this.spriteSet);
        }
    }
}
