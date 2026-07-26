package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 灵魂粒子 (Soul Particle)
 * <p>
 * 还原自原版 {@code SoulParticleParticle}：魂啸法杖命中时爆发的魂焰。
 * <ul>
 *   <li>碰撞尺寸 0.2×0.2，渲染尺寸 ×1.5</li>
 *   <li>寿命 20 ± 2 tick，重力 -0.1（缓缓上升），带物理碰撞，初速度归零</li>
 *   <li>不透明渲染层，单帧随机贴图（不自发光）</li>
 * </ul>
 */
public class SoulParticle extends TextureSheetParticle {

    /**
     * 构造灵魂粒子
     *
     * @param level     客户端世界
     * @param x         初始 X 坐标
     * @param y         初始 Y 坐标
     * @param z         初始 Z 坐标
     * @param spriteSet 精灵表集合
     */
    protected SoulParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.setSize(0.2f, 0.2f);
        this.quadSize *= 1.5f;
        this.lifetime = Math.max(1, 20 + (this.random.nextInt(4) - 2));
        this.gravity = -0.1f;
        this.hasPhysics = true;
        // 与原版一致：发射速度 ×0，纯靠负重力上升
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.pickSprite(spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    /**
     * 灵魂粒子提供器
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
            return new SoulParticle(level, x, y, z, this.spriteSet);
        }
    }
}
