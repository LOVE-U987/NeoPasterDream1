package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * 白星粒子 (White Star Particle)
 * <p>
 * 4帧白色星光粒子，用于炎热森林生物群系。
 * 在空中缓慢漂浮闪烁，犹如林间洒落的星光。
 */
public class WhiteStarParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private static final int FADE_IN_TICKS = 20;
    private static final int FADE_OUT_TICKS = 40;

    /**
     * 构造白星粒子
     */
    protected WhiteStarParticle(ClientLevel level, double x, double y, double z,
                                 double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = spriteSet;
        this.setSize(0.2f, 0.2f);
        this.quadSize = 0.25f + this.random.nextFloat() * 0.3f;
        this.lifetime = 120 + this.random.nextInt(80);
        this.gravity = 0f;
        this.hasPhysics = false;

        this.xd = vx + (this.random.nextDouble() - 0.5) * 0.004;
        this.yd = (this.random.nextDouble() - 0.5) * 0.008;
        this.zd = vz + (this.random.nextDouble() - 0.5) * 0.004;

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

        double swayAngle = this.age * 0.03;
        this.xd += Math.sin(swayAngle) * 0.0015;
        this.zd += Math.cos(swayAngle * 0.7 + 1.0) * 0.0015;
        this.xd *= 0.98;
        this.zd *= 0.98;

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
     * 白星粒子的工厂类
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
            return new WhiteStarParticle(level, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}