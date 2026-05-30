package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * 裂纹粒子 (Crack Particle)
 * <p>
 * 用于温暖海洋生物群系，1帧紫色半透明粒子犹如水下气泡般缓慢上浮。
 * 模拟海底升腾的魔法气泡或梦境水泡。
 */
public class CrackParticle extends TextureSheetParticle {

    private static final int FADE_IN_TICKS = 10;
    private static final int FADE_OUT_TICKS = 30;

    /**
     * 构造裂纹粒子
     */
    protected CrackParticle(ClientLevel level, double x, double y, double z,
                             double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);
        this.setSprite(spriteSet.get(this.random));
        this.setSize(0.2f, 0.2f);
        this.quadSize = 0.3f + this.random.nextFloat() * 0.3f;
        this.lifetime = 80 + this.random.nextInt(60);
        this.gravity = -0.006f;
        this.hasPhysics = false;

        this.xd = vx + (this.random.nextDouble() - 0.5) * 0.006;
        this.yd = 0.01 + this.random.nextDouble() * 0.02;
        this.zd = vz + (this.random.nextDouble() - 0.5) * 0.006;

        this.alpha = 0f;
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

        double wobbleAngle = this.age * 0.06;
        this.xd += Math.sin(wobbleAngle) * 0.002;
        this.zd += Math.cos(wobbleAngle * 0.5 + 1.0) * 0.002;

        this.quadSize *= 1.002f;

        this.move(this.xd, this.yd, this.zd);

        if (this.y > this.yo + 3.0) {
            this.alpha *= 0.95f;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /**
     * 裂纹粒子的工厂类
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
            return new CrackParticle(level, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}