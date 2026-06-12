package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * 荧光羽毛粒子 (Feather White Particle)
 * <p>
 * 用于染梦深海生物群系，12帧白色羽毛状粒子从深海上浮，
 * 模拟发光浮游生物/深海羽毛水母的梦幻效果。
 * 白天半透明正常渲染，夜晚切换为发光渲染，营造深海生物荧光的氛围。
 */
public class FeatherWhiteParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private float angularVelocity;
    private static final int FADE_IN_TICKS = 20;
    private static final int FADE_OUT_TICKS = 40;

    /**
     * 构造荧光羽毛粒子
     *
     * @param level     客户端世界
     * @param x         初始 X 坐标
     * @param y         初始 Y 坐标
     * @param z         初始 Z 坐标
     * @param vx        X 方向速度
     * @param vy        Y 方向速度
     * @param vz        Z 方向速度
     * @param spriteSet 精灵表
     */
    protected FeatherWhiteParticle(ClientLevel level, double x, double y, double z,
                                   double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = spriteSet;
        this.setSize(0.2f, 0.2f);
        this.quadSize = 0.3f + this.random.nextFloat() * 0.35f;
        this.lifetime = 120 + this.random.nextInt(80);
        this.gravity = -0.004f;
        this.hasPhysics = false;

        this.xd = vx + (this.random.nextDouble() - 0.5) * 0.006;
        this.yd = 0.008 + this.random.nextDouble() * 0.015;
        this.zd = vz + (this.random.nextDouble() - 0.5) * 0.006;

        this.angularVelocity = (this.random.nextFloat() - 0.5f) * 0.08f;

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
            this.alpha = Math.max(0.0f, (1.0f - ageRatio) * this.lifetime / FADE_OUT_TICKS);
        } else {
            this.alpha = 1.0f;
        }

        this.yd -= 0.04 * this.gravity;

        double swayAngle = this.age * 0.04;
        this.xd += Math.sin(swayAngle) * 0.002;
        this.zd += Math.cos(swayAngle * 0.6 + 1.5) * 0.002;
        this.xd *= 0.98;
        this.zd *= 0.98;

        this.oRoll = this.roll;
        this.roll += this.angularVelocity;

        this.quadSize *= 1.001f;

        this.move(this.xd, this.yd, this.zd);

        if (this.age % 6 == 0) {
            this.setSpriteFromAge(this.sprites);
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        if (level != null) {
            long dayTime = level.getDayTime() % 24000;
            if (dayTime >= 13000 && dayTime < 23000) {
                return PDParticleRenderTypes.GLOWING_SHEET;
            }
        }
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick) {
        if (level != null) {
            long dayTime = level.getDayTime() % 24000;
            if (dayTime >= 13000 && dayTime < 23000) {
                return LightTexture.FULL_BRIGHT;
            }
        }
        return super.getLightColor(partialTick);
    }

    /**
     * 荧光羽毛粒子的工厂类
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
            return new FeatherWhiteParticle(level, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}