package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 萤火虫粒子 (Firefly Particle)
 * <p>
 * 还原自原版 PasterDream 的 {@code FireflyParticleParticle}，用于风之旅途生物群系
 * （wind_journey_biome_0）的环境萤光光点，以及萤火虫相关方块/实体的发光效果。
 * 行为参数与原版一致：
 * <ul>
 *   <li>碰撞尺寸 0.2×0.2，渲染尺寸 quadSize ×1.6</li>
 *   <li>寿命 100 ± 10 tick（约 4.5~5.5 秒）</li>
 *   <li>无重力，带物理碰撞，初速度为发射速度的 0.02 倍（近乎悬停漂浮）</li>
 *   <li>全亮度自发光（光照值 15728880 = 0xF000F0，LIT 渲染层）</li>
 *   <li>10 帧动画，每 2 tick 前进一帧循环闪烁</li>
 * </ul>
 */
public class FireflyParticle extends TextureSheetParticle {

    /** 全亮度光照值（0xF000F0，方块光/天光均拉满，萤火虫自发光） */
    private static final int FULL_BRIGHT = 15728880;

    /** 动画总帧数（对应 firefly_particle_1 ~ firefly_particle_10 共 10 帧贴图） */
    private static final int FRAME_COUNT = 10;

    private final SpriteSet spriteSet;

    /**
     * 构造萤火虫粒子
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
    protected FireflyParticle(ClientLevel level, double x, double y, double z,
                              double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z);
        this.spriteSet = spriteSet;
        this.setSize(0.2f, 0.2f);
        this.quadSize *= 1.6f;
        // 与原版一致：寿命 100 ± 10 tick
        this.lifetime = Math.max(1, 100 + (this.random.nextInt(20) - 10));
        this.gravity = 0f;
        this.hasPhysics = true;
        // 与原版一致：初速度为发射速度的 0.02 倍，几乎原地悬浮
        this.xd = vx * 0.02;
        this.yd = vy * 0.02;
        this.zd = vz * 0.02;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    protected int getLightColor(float partialTick) {
        // 与原版一致：恒定全亮度，营造萤火虫自发光效果
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
            // 与原版一致：10 帧动画每 2 tick 前进一帧循环播放
            this.setSprite(this.spriteSet.get((this.age / 2) % FRAME_COUNT + 1, FRAME_COUNT));
        }
    }

    /**
     * 萤火虫粒子提供器
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
            return new FireflyParticle(level, x, y, z, vx, vy, vz, this.spriteSet);
        }
    }
}
