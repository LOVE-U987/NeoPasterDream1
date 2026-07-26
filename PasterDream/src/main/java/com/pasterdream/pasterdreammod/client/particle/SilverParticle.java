package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * 银色粒子 (Silver Particle)
 * <p>
 * 用于寒冷冰雪生物群系，3帧冰晶银色粒子缓慢旋转上浮。
 * 犹如冰雪中的魔法冰晶，自旋发光，营造寒冷梦幻的氛围。
 * <p>
 * 尺寸行为：粒子存续期间 quadSize 每 tick 乘以 0.999，随寿命极缓慢缩小
 * （整个生命周期约缩小 13%~20%），模拟冰晶逐渐升华消融的效果，属有意设计。
 */
public class SilverParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private float angularVelocity;
    private static final int FADE_IN_TICKS = 20;
    private static final int FADE_OUT_TICKS = 40;

    /**
     * 构造银色粒子
     */
    protected SilverParticle(ClientLevel level, double x, double y, double z,
                              double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = spriteSet;
        this.setSize(0.15f, 0.15f);
        this.quadSize = 0.2f + this.random.nextFloat() * 0.25f;
        this.lifetime = 140 + this.random.nextInt(80);
        this.gravity = -0.003f;
        this.hasPhysics = false;

        this.xd = vx + (this.random.nextDouble() - 0.5) * 0.004;
        this.yd = 0.005 + this.random.nextDouble() * 0.015;
        this.zd = vz + (this.random.nextDouble() - 0.5) * 0.004;

        this.angularVelocity = (this.random.nextFloat() - 0.5f) * 0.12f;

        this.alpha = 0f;
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

        float ageRatio = (float) this.age / this.lifetime;

        if (ageRatio < (float) FADE_IN_TICKS / this.lifetime) {
            this.alpha = Math.min(1.0f, ageRatio * this.lifetime / FADE_IN_TICKS);
        } else if (ageRatio > 1.0f - (float) FADE_OUT_TICKS / this.lifetime) {
            // 生命周期尾段线性淡出，避免粒子在寿命结束被 remove() 时突兀消失
            this.alpha = Math.max(0.0f, (1.0f - ageRatio) * this.lifetime / FADE_OUT_TICKS);
        } else {
            this.alpha = 1.0f;
        }

        this.yd -= 0.04 * this.gravity;

        double swayAngle = this.age * 0.035;
        this.xd += Math.sin(swayAngle) * 0.001;
        this.zd += Math.cos(swayAngle * 0.6 + 1.5) * 0.001;

        this.oRoll = this.roll;
        this.roll += this.angularVelocity;

        // 每 tick 轻微缩小，模拟冰晶缓慢升华消融（有意设计，详见类 Javadoc）
        this.quadSize *= 0.999f;

        this.move(this.xd, this.yd, this.zd);

        if (this.age % 6 == 0) {
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
     * 银色粒子的工厂类
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
            return new SilverParticle(level, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}