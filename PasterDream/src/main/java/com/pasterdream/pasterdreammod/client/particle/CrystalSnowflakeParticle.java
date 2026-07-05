package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * 水晶雪花粒子 (Crystal Snowflake Particle)
 * <p>
 * 用于染梦维度寒冷染梦生物群系的环境粒子效果。
 * 白色半透明冰晶雪花，具有以下特性：
 * <ul>
 *   <li>缓慢旋转飘落，模拟雪花轻盈下落</li>
 *   <li>纯白半透明外观，带有微弱冷光闪烁</li>
 *   <li>水平方向受微风扰动产生漂移</li>
 *   <li>全亮度渲染，在暗处也能清晰可见</li>
 * </ul>
 */
public class CrystalSnowflakeParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    /** 淡入时间（tick数） */
    private static final int FADE_IN_TICKS = 20;
    /** 淡出时间（tick数） */
    private static final int FADE_OUT_TICKS = 40;

    /** 旋转角速度 */
    private float angularVelocity;
    /** 微风水平漂移方向 */
    private float windPhase;

    /**
     * 构造水晶雪花粒子
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
    protected CrystalSnowflakeParticle(ClientLevel level, double x, double y, double z,
                                        double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = spriteSet;
        this.setSize(0.12f, 0.12f);
        this.quadSize = 0.15f + this.random.nextFloat() * 0.2f;
        this.lifetime = 120 + this.random.nextInt(80);

        // 正重力 = 下落
        this.gravity = 0.008f;
        this.hasPhysics = false;

        // 纯白颜色，略带半透明感
        this.rCol = 1.0f;
        this.gCol = 1.0f;
        this.bCol = 1.0f;

        // 初始速度：水平随机 + 下落
        this.xd = vx + (this.random.nextDouble() - 0.5) * 0.004;
        this.yd = -0.01 - this.random.nextDouble() * 0.02;
        this.zd = vz + (this.random.nextDouble() - 0.5) * 0.004;

        // 随机旋转速度（缓慢）
        this.angularVelocity = (this.random.nextFloat() - 0.5f) * 0.05f;

        // 随机微风相位
        this.windPhase = this.random.nextFloat() * (float) (Math.PI * 2);

        // 初始透明（淡入用）
        this.alpha = 0.0f;

        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        // ===== 透明度控制：淡入 → 保持 → 淡出 =====
        float ageRatio = (float) this.age / this.lifetime;
        if (ageRatio < (float) FADE_IN_TICKS / this.lifetime) {
            this.alpha = Math.min(0.9f, ageRatio * this.lifetime / FADE_IN_TICKS);
        } else if (ageRatio > 1.0f - (float) FADE_OUT_TICKS / this.lifetime) {
            this.alpha = Math.max(0.0f, (1.0f - ageRatio) * this.lifetime / FADE_OUT_TICKS);
        } else {
            // 保持期微弱的闪烁
            float sparkle = (float) (Math.sin(this.age * 0.12 + this.windPhase) * 0.06f);
            this.alpha = Math.min(0.9f, 0.82f + sparkle);
        }

        // ===== 微风扰动：水平方向正弦漂移 =====
        double swayAngle = this.age * 0.03 + this.windPhase;
        this.xd += Math.sin(swayAngle) * 0.0006;
        this.zd += Math.cos(swayAngle * 0.6 + 1.0) * 0.0006;
        this.xd *= 0.97;
        this.zd *= 0.97;

        // 限制水平速度范围
        this.xd = Math.max(-0.02, Math.min(0.02, this.xd));
        this.zd = Math.max(-0.02, Math.min(0.02, this.zd));

        // ===== 旋转动画 =====
        this.oRoll = this.roll;
        this.roll += this.angularVelocity;

        // ===== 轻微大小变化 =====
        if (this.age % 10 == 0) {
            this.quadSize += (this.random.nextFloat() - 0.5f) * 0.01f;
            this.quadSize = Math.max(0.1f, Math.min(0.45f, this.quadSize));
        }

        this.move(this.xd, this.yd, this.zd);

        // 碰到地面时消失
        if (this.onGround) {
            this.remove();
            return;
        }

        // 落到太低位置时消失
        if (this.y < this.level.getMinBuildHeight()) {
            this.remove();
            return;
        }

        // 更新精灵帧
        if (this.age % 4 == 0) {
            this.setSpriteFromAge(this.sprites);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    /**
     * 水晶雪花粒子的工厂类
     */
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new CrystalSnowflakeParticle(level, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}
