package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 萤火虫玻璃罐粒子 (Firefly Glass Jar Particle)
 * <p>
 * 还原自原版 {@code FireflyGlassJarParticleParticle}：萤火虫玻璃罐罐内的半透明萤光。
 * <ul>
 *   <li>碰撞尺寸 0.1×0.1，渲染尺寸 ×0.7</li>
 *   <li>寿命 50 ± 5 tick，无重力，带物理碰撞，初速度 = 发射速度 ×0.02（近乎悬停）</li>
 *   <li>10 帧动画每 2 tick 前进一帧循环，半透明渲染层（不自发光）</li>
 * </ul>
 */
public class FireflyGlassJarParticle extends TextureSheetParticle {

    /** 动画总帧数（firefly_glass_jar_particle_1 ~ _10） */
    private static final int FRAME_COUNT = 10;

    private final SpriteSet spriteSet;

    /**
     * 构造萤火虫玻璃罐粒子
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
    protected FireflyGlassJarParticle(ClientLevel level, double x, double y, double z,
                                      double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.1f, 0.1f);
        this.quadSize *= 0.7f;
        this.lifetime = Math.max(1, 50 + (this.random.nextInt(10) - 5));
        this.gravity = 0f;
        this.hasPhysics = true;
        this.xd = vx * 0.02;
        this.yd = vy * 0.02;
        this.zd = vz * 0.02;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setSprite(this.spriteSet.get((this.age / 2) % FRAME_COUNT + 1, FRAME_COUNT));
        }
    }

    /**
     * 萤火虫玻璃罐粒子提供器
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
            return new FireflyGlassJarParticle(level, x, y, z, vx, vy, vz, this.spriteSet);
        }
    }
}
