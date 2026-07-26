package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 黄色烟雾粒子 (Yellow Smoke Particle)
 * <p>
 * 还原自原版 PasterDream 的 yellow_smoke_particle，治疗立场底部的暖黄烟雾。
 * 行为与原版一致：2 倍尺寸、约 2.5 秒寿命、正重力缓缓下沉、原地生成、4 帧动画。
 */
public class YellowSmokeParticle extends TextureSheetParticle {

    private final SpriteSet spriteSet;

    /**
     * 构造黄色烟雾粒子
     *
     * @param level     客户端世界
     * @param x         初始 X 坐标
     * @param y         初始 Y 坐标
     * @param z         初始 Z 坐标
     * @param spriteSet 精灵表集合
     */
    protected YellowSmokeParticle(ClientLevel level, double x, double y, double z,
                                  SpriteSet spriteSet) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.2f, 0.2f);
        this.quadSize *= 2f;
        this.lifetime = Math.max(1, 50 + (this.random.nextInt(20) - 10));
        this.gravity = 0.1f;
        this.hasPhysics = true;
        // 与原版一致：初速为零，仅靠重力下沉
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
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
     * 黄色烟雾粒子提供器
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
            return new YellowSmokeParticle(level, x, y, z, this.spriteSet);
        }
    }
}
