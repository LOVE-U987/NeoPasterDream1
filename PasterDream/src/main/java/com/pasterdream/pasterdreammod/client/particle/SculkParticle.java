package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 幽匿粒子 (Sculk Particle)
 * <p>
 * 还原自原版 {@code SculkParticleParticle}：幽匿系武器/生物演出的不透明碎屑。
 * <ul>
 *   <li>碰撞尺寸 0.2×0.2（渲染尺寸不缩放）</li>
 *   <li>寿命 10 ± 2 tick，无重力，带物理碰撞，初速度 = 发射速度 ×1</li>
 *   <li>不透明渲染层，单帧随机贴图（不自发光）</li>
 * </ul>
 */
public class SculkParticle extends TextureSheetParticle {

    /**
     * 构造幽匿粒子
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
    protected SculkParticle(ClientLevel level, double x, double y, double z,
                            double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.setSize(0.2f, 0.2f);
        this.lifetime = Math.max(1, 10 + (this.random.nextInt(4) - 2));
        this.gravity = 0f;
        this.hasPhysics = true;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.pickSprite(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    /**
     * 幽匿粒子提供器
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
            return new SculkParticle(level, x, y, z, vx, vy, vz, this.spriteSet);
        }
    }
}
