package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 攻击粒子 0 (Attack 0 Particle)
 * <p>
 * 还原自原版 {@code Attack0ParticleParticle}：近战/战技命中演出的单帧全亮度特效。
 * <ul>
 *   <li>碰撞尺寸 0.1×0.1，渲染尺寸 ×1.5</li>
 *   <li>寿命 5 ± 1 tick，无重力，带物理碰撞，初速度 = 发射速度 ×1</li>
 *   <li>全亮度自发光（LIT 渲染层），单帧随机贴图</li>
 * </ul>
 */
public class Attack0Particle extends TextureSheetParticle {

    /** 全亮度光照值（0xF000F0） */
    private static final int FULL_BRIGHT = 15728880;

    /**
     * 构造攻击粒子 0
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
    protected Attack0Particle(ClientLevel level, double x, double y, double z,
                              double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.setSize(0.1f, 0.1f);
        this.quadSize *= 1.5f;
        this.lifetime = Math.max(1, 5 + (this.random.nextInt(2) - 1));
        this.gravity = 0f;
        this.hasPhysics = true;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.pickSprite(spriteSet);
    }

    @Override
    protected int getLightColor(float partialTick) {
        return FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    /**
     * 攻击粒子 0 提供器
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
            return new Attack0Particle(level, x, y, z, vx, vy, vz, this.spriteSet);
        }
    }
}
