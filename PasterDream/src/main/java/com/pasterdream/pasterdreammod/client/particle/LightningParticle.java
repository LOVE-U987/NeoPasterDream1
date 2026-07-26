package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 闪电粒子 (Lightning Particle)
 * <p>
 * 还原自原版 {@code LightningParticleParticle}：闪电投射物飞行拖尾的电弧。
 * <ul>
 *   <li>碰撞尺寸 0×0，渲染尺寸 ×1.5</li>
 *   <li>固定寿命 12 tick，无重力，带物理碰撞，初速度 = 发射速度 ×1</li>
 *   <li>4 帧动画每 3 tick 前进一帧，全亮度自发光（LIT 渲染层）</li>
 * </ul>
 */
public class LightningParticle extends TextureSheetParticle {

    /** 全亮度光照值（0xF000F0） */
    private static final int FULL_BRIGHT = 15728880;

    /** 动画总帧数（lightning_particle_1 ~ _4） */
    private static final int FRAME_COUNT = 4;

    private final SpriteSet spriteSet;

    /**
     * 构造闪电粒子
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
    protected LightningParticle(ClientLevel level, double x, double y, double z,
                                double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0f, 0f);
        this.quadSize *= 1.5f;
        this.lifetime = 12;
        this.gravity = 0f;
        this.hasPhysics = true;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    protected int getLightColor(float partialTick) {
        return FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed) {
            this.setSprite(this.spriteSet.get((this.age / 3) % FRAME_COUNT + 1, FRAME_COUNT));
        }
    }

    /**
     * 闪电粒子提供器
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
            return new LightningParticle(level, x, y, z, vx, vy, vz, this.spriteSet);
        }
    }
}
