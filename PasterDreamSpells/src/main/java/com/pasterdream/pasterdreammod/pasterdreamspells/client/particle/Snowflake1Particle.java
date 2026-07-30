package com.pasterdream.pasterdreammod.pasterdreamspells.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 雪花粒子 1 (Snowflake 1 Particle)
 * <p>
 * 还原自原版 PasterDream 的 snowflake_1_particle，冰冻法术的第二种雪花。
 * 行为与原版一致：固定 20 tick 寿命、正重力下落、原地生成、4 帧动画。
 *
 * @author PasterDream
 */
public class Snowflake1Particle extends TextureSheetParticle {

    private final SpriteSet spriteSet;

    /**
     * 构造雪花粒子 1
     *
     * @param level     客户端世界
     * @param x         初始 X 坐标
     * @param y         初始 Y 坐标
     * @param z         初始 Z 坐标
     * @param spriteSet 精灵表集合
     */
    protected Snowflake1Particle(ClientLevel level, double x, double y, double z,
                                 SpriteSet spriteSet) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.2f, 0.2f);
        this.lifetime = 20;
        this.gravity = 0.1f;
        this.hasPhysics = true;
        // 与原版一致：初速为零，仅靠重力下落
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
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
     * 雪花粒子 1 提供器
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
            return new Snowflake1Particle(level, x, y, z, this.spriteSet);
        }
    }
}
