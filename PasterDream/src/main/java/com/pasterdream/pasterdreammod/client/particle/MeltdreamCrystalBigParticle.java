package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 融梦水晶大粒子 (Meltdream Crystal Big Particle)
 * <p>
 * 还原自原版 {@code MeltdreamCrystalBigParticleParticle}：融梦水晶演出的大尺寸半透明水晶光。
 * <ul>
 *   <li>碰撞尺寸 0×0，渲染尺寸 ×1.2</li>
 *   <li>寿命 50 ± 5 tick，无重力，无物理碰撞，初速度 = 发射速度 ×0.05</li>
 *   <li>4 帧动画每 3 tick 前进一帧，半透明渲染层（不自发光）</li>
 * </ul>
 */
public class MeltdreamCrystalBigParticle extends TextureSheetParticle {

    /** 动画总帧数（meltdream_crystal_big_particle_1 ~ _4） */
    private static final int FRAME_COUNT = 4;

    private final SpriteSet spriteSet;

    /**
     * 构造融梦水晶大粒子
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
    protected MeltdreamCrystalBigParticle(ClientLevel level, double x, double y, double z,
                                          double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0f, 0f);
        this.quadSize *= 1.2f;
        this.lifetime = Math.max(1, 50 + (this.random.nextInt(10) - 5));
        this.gravity = 0f;
        this.hasPhysics = false;
        this.xd = vx * 0.05;
        this.yd = vy * 0.05;
        this.zd = vz * 0.05;
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
            this.setSprite(this.spriteSet.get((this.age / 3) % FRAME_COUNT + 1, FRAME_COUNT));
        }
    }

    /**
     * 融梦水晶大粒子提供器
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
            return new MeltdreamCrystalBigParticle(level, x, y, z, vx, vy, vz, this.spriteSet);
        }
    }
}
