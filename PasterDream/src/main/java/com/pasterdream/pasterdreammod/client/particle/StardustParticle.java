package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * 星尘粒子 (Stardust Particle)
 * <p>
 * 用于染梦维度多种生物群系的环境点缀粒子效果，为 {@link com.pasterdream.pasterdreammod.client.DyedreamEnvironmentRenderer} 的补充粒子。
 * 微小白点星尘，具有以下特性：
 * <ul>
 *   <li>极小尺寸，呈微小白点状</li>
 *   <li>旋转飘落，带有随机闪烁</li>
 *   <li>颜色在银色/金色/淡粉之间随机选取</li>
 *   <li>加法混合渲染，产生星芒闪烁效果</li>
 * </ul>
 */
public class StardustParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    /** 星尘色系 */
    private static final float[][] STAR_COLORS = {
            {1.0f, 0.95f, 0.85f},  // 暖白
            {1.0f, 0.85f, 0.65f},  // 淡金
            {0.85f, 0.85f, 1.0f},  // 淡蓝白
            {1.0f, 0.80f, 0.90f},  // 淡粉
            {0.90f, 1.0f, 0.90f}   // 淡绿白
    };

    /** 淡入时间（tick数） */
    private static final int FADE_IN_TICKS = 15;
    /** 淡出时间（tick数） */
    private static final int FADE_OUT_TICKS = 30;

    /** 闪烁相位 */
    private final float twinklePhase;

    /**
     * 构造星尘粒子
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
    protected StardustParticle(ClientLevel level, double x, double y, double z,
                                double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = spriteSet;
        this.setSize(0.08f, 0.08f);
        this.quadSize = 0.08f + this.random.nextFloat() * 0.12f;
        this.lifetime = 80 + this.random.nextInt(60);

        // 轻微负重力 = 缓慢上浮
        this.gravity = -0.001f;
        this.hasPhysics = false;

        // 随机选取星尘色
        int colorIndex = this.random.nextInt(STAR_COLORS.length);
        this.rCol = STAR_COLORS[colorIndex][0];
        this.gCol = STAR_COLORS[colorIndex][1];
        this.bCol = STAR_COLORS[colorIndex][2];

        // 初始极慢速度
        this.xd = vx + (this.random.nextDouble() - 0.5) * 0.004;
        this.yd = 0.003 + this.random.nextDouble() * 0.008;
        this.zd = vz + (this.random.nextDouble() - 0.5) * 0.004;

        // 随机闪烁相位
        this.twinklePhase = this.random.nextFloat() * (float) (Math.PI * 2);

        // 初始透明
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

        // ===== 透明度控制：快速淡入 → 闪烁 → 淡出 =====
        float ageRatio = (float) this.age / this.lifetime;
        if (ageRatio < (float) FADE_IN_TICKS / this.lifetime) {
            this.alpha = Math.min(1.0f, ageRatio * this.lifetime / FADE_IN_TICKS);
        } else if (ageRatio > 1.0f - (float) FADE_OUT_TICKS / this.lifetime) {
            this.alpha = Math.max(0.0f, (1.0f - ageRatio) * this.lifetime / FADE_OUT_TICKS);
        } else {
            // 快速闪烁效果
            float twinkle = (float) (Math.sin(this.age * 0.2 + this.twinklePhase) * 0.35f);
            this.alpha = 0.6f + twinkle;
        }

        // ===== 缓慢旋转飘落 =====
        double swayAngle = this.age * 0.05 + this.twinklePhase;
        this.xd += Math.sin(swayAngle) * 0.0004;
        this.zd += Math.cos(swayAngle * 0.8 + 1.5) * 0.0004;
        this.xd *= 0.96;
        this.zd *= 0.96;

        // ===== 大小呼吸变化 =====
        if (this.age % 4 == 0) {
            float breathe = (float) (Math.sin(this.age * 0.15 + this.twinklePhase) * 0.02f);
            this.quadSize = Math.max(0.05f, Math.min(0.3f, this.quadSize + breathe));
        }

        this.move(this.xd, this.yd, this.zd);

        // 更新精灵帧
        if (this.age % 3 == 0) {
            this.setSpriteFromAge(this.sprites);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return PDParticleRenderTypes.GLOWING_SHEET;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    /**
     * 星尘粒子的工厂类
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
            return new StardustParticle(level, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}
