package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 治疗法术粒子 (Healing Spell Particle)
 * <p>
 * 还原自原版 PasterDream 的 healing_spell_particle，治疗立场中缓缓升起的十字光点。
 * 行为与原版一致：0.6 倍尺寸、固定 32 tick 寿命、负重力上浮、原地生成、8 帧动画。
 */
public class HealingSpellParticle extends TextureSheetParticle {

    private final SpriteSet spriteSet;

    /**
     * 构造治疗法术粒子
     *
     * @param level     客户端世界
     * @param x         初始 X 坐标
     * @param y         初始 Y 坐标
     * @param z         初始 Z 坐标
     * @param spriteSet 精灵表集合
     */
    protected HealingSpellParticle(ClientLevel level, double x, double y, double z,
                                   SpriteSet spriteSet) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.2f, 0.2f);
        this.quadSize *= 0.6f;
        this.lifetime = 32;
        this.gravity = -0.1f;
        this.hasPhysics = true;
        // 与原版一致：初速为零，仅靠负重力上浮
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
     * 治疗法术粒子提供器
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
            return new HealingSpellParticle(level, x, y, z, this.spriteSet);
        }
    }
}
