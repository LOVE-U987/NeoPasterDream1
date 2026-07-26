package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 金辉粒子 (Golden Particle)
 * <p>
 * 还原自原版 {@code GoldenParticleParticle}：转身衣装激活时的旋转金光。
 * <ul>
 *   <li>碰撞尺寸 0.1×0.1，渲染尺寸 ×0.5</li>
 *   <li>寿命 35 ± 10 tick，无重力，带物理碰撞，初速度 = 发射速度 ×1</li>
 *   <li>每 tick 自转 0.1 弧度（角加速度 0），3 帧动画每 10 tick 前进一帧</li>
 *   <li>全亮度自发光（LIT 渲染层）</li>
 * </ul>
 */
public class GoldenParticle extends TextureSheetParticle {

    /** 全亮度光照值（0xF000F0） */
    private static final int FULL_BRIGHT = 15728880;

    /** 动画总帧数（golden_particle_1 ~ _3） */
    private static final int FRAME_COUNT = 3;

    /** 每 tick 自转角速度（原版 angularVelocity = 0.1，角加速度 0） */
    private static final float ANGULAR_VELOCITY = 0.1f;

    private final SpriteSet spriteSet;

    /**
     * 构造金辉粒子
     *
     * @param level     客户端世界
     * @param x         初始 X 坐标
     * @param y         初始 Y 坐标
     * @param z         初始 Z 坐标
     * @param vx        X 发射速度
     * @param vy        Y 发射速度
     * @param vz        Z 发射速度
     * @param spriteSet 精灵表集合
     */
    protected GoldenParticle(ClientLevel level, double x, double y, double z,
                             double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.1f, 0.1f);
        this.quadSize *= 0.5f;
        this.lifetime = Math.max(1, 35 + (this.random.nextInt(20) - 10));
        this.gravity = 0f;
        this.hasPhysics = true;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    protected int getLightColor(float partialTick) {
        return FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public void tick() {
        super.tick();
        // 与原版一致：恒定角速度自转
        this.oRoll = this.roll;
        this.roll += ANGULAR_VELOCITY;
        if (!this.removed) {
            this.setSprite(this.spriteSet.get((this.age / 10) % FRAME_COUNT + 1, FRAME_COUNT));
        }
    }

    /**
     * 金辉粒子提供器
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
            return new GoldenParticle(level, x, y, z, vx, vy, vz, this.spriteSet);
        }
    }
}
