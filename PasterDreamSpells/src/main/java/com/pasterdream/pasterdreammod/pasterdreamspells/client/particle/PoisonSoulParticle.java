package com.pasterdream.pasterdreammod.pasterdreamspells.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 毒魂粒子 (Poison Soul Particle)
 * <p>
 * 还原自原版 PasterDream 的 poison_soul_particle，剧毒法术中缓缓上飘的毒魂点缀。
 * 行为与原版一致：短寿命（约 1 秒）、负重力上浮、随速度移动、随机单帧。
 *
 * @author PasterDream
 */
public class PoisonSoulParticle extends TextureSheetParticle {

    /**
     * 构造毒魂粒子
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
    protected PoisonSoulParticle(ClientLevel level, double x, double y, double z,
                                 double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.setSize(0.2f, 0.2f);
        this.lifetime = Math.max(1, 20 + (this.random.nextInt(8) - 4));
        this.gravity = -0.1f;
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
     * 毒魂粒子提供器
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
            return new PoisonSoulParticle(level, x, y, z, vx, vy, vz, this.spriteSet);
        }
    }
}
