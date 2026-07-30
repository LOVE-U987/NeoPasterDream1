package com.pasterdream.pasterdreammod.pasterdreamspells.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 狂暴法术粒子 (Fury Spell Particle)
 * <p>
 * 还原自原版 PasterDream 的 fury_spell_particle，狂暴立场中悬浮的能量符文。
 * 行为与原版一致：1.3 倍尺寸、约 2 秒寿命、无重力、近乎悬停（速度 ×0.02）、10 帧动画。
 *
 * @author PasterDream
 */
public class FurySpellParticle extends TextureSheetParticle {

    private final SpriteSet spriteSet;

    /**
     * 构造狂暴法术粒子
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
    protected FurySpellParticle(ClientLevel level, double x, double y, double z,
                                double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.2f, 0.2f);
        this.quadSize *= 1.3f;
        this.lifetime = Math.max(1, 40 + (this.random.nextInt(10) - 5));
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
        // 随寿命推进动画帧
        if (!this.removed) {
            this.setSpriteFromAge(this.spriteSet);
        }
    }

    /**
     * 狂暴法术粒子提供器
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
            return new FurySpellParticle(level, x, y, z, vx, vy, vz, this.spriteSet);
        }
    }
}
