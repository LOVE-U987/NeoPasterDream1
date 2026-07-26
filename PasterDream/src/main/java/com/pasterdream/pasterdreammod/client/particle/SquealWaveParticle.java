package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 魂啸粒子 (Squeal Wave Particle)
 * <p>
 * 还原自原版 {@code SquealWaveParticleParticle}：魂啸法杖音波投射物的飞行拖尾。
 * <ul>
 *   <li>碰撞尺寸 0.2×0.2，渲染尺寸 ×2</li>
 *   <li>寿命 12 ± 2 tick，无重力，带物理碰撞，初速度 = 发射速度 ×1</li>
 *   <li>3 帧动画每 tick 前进一帧，全亮度自发光（LIT 渲染层）</li>
 * </ul>
 */
public class SquealWaveParticle extends TextureSheetParticle {

    /** 全亮度光照值（0xF000F0） */
    private static final int FULL_BRIGHT = 15728880;

    /** 动画总帧数（squeal_wave_particle_1 ~ _3） */
    private static final int FRAME_COUNT = 3;

    private final SpriteSet spriteSet;

    /**
     * 构造魂啸粒子
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
    protected SquealWaveParticle(ClientLevel level, double x, double y, double z,
                                 double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.2f, 0.2f);
        this.quadSize *= 2f;
        this.lifetime = Math.max(1, 12 + (this.random.nextInt(4) - 2));
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
        if (!this.removed) {
            this.setSprite(this.spriteSet.get(this.age % FRAME_COUNT + 1, FRAME_COUNT));
        }
    }

    /**
     * 魂啸粒子提供器
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
            return new SquealWaveParticle(level, x, y, z, vx, vy, vz, this.spriteSet);
        }
    }
}
