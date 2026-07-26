package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 增益粒子 0 (Buff 0 Particle)
 * <p>
 * 还原自原版 {@code Buff0ParticleParticle}：增益/祝福演出的单帧全亮度特效。
 * <ul>
 *   <li>碰撞尺寸 0.2×0.2，渲染尺寸 ×1.6</li>
 *   <li>寿命 10 ± 1 tick，重力 -1（快速上飘），带物理碰撞，初速度归零（原地上升）</li>
 *   <li>全亮度自发光（LIT 渲染层），单帧随机贴图</li>
 * </ul>
 */
public class Buff0Particle extends TextureSheetParticle {

    /** 全亮度光照值（0xF000F0） */
    private static final int FULL_BRIGHT = 15728880;

    /**
     * 构造增益粒子 0
     *
     * @param level     客户端世界
     * @param x         初始 X 坐标
     * @param y         初始 Y 坐标
     * @param z         初始 Z 坐标
     * @param spriteSet 精灵表集合
     */
    protected Buff0Particle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.setSize(0.2f, 0.2f);
        this.quadSize *= 1.6f;
        this.lifetime = Math.max(1, 10 + (this.random.nextInt(2) - 1));
        this.gravity = -1f;
        this.hasPhysics = true;
        // 与原版一致：发射速度 ×0，纯靠负重力上升
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
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
     * 增益粒子 0 提供器
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
            return new Buff0Particle(level, x, y, z, this.spriteSet);
        }
    }
}
