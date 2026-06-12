package com.pasterdream.pasterdreammod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * 染梦孢子粒子 (Dyedream Particle)
 * <p>
 * 用于蘑菇平原生物群系，单帧暖金色孢子粒子从地面缓缓飘散，
 * 模拟夜晚发光的魔法孢子粉尘在空气中呼吸脉动的效果。
 * 白天半透明正常渲染，夜晚切换为发光渲染，伴随大小脉冲呼吸效果。
 */
public class DyedreamParticle extends TextureSheetParticle {

    private final SpriteSet sprites;
    private static final int FADE_IN_TICKS = 15;
    private static final int FADE_OUT_TICKS = 35;
    private final float baseQuadSize;
    private final float pulsePhase;

    /**
     * 构造染梦孢子粒子
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
    protected DyedreamParticle(ClientLevel level, double x, double y, double z,
                                double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = spriteSet;
        this.setSprite(spriteSet.get(this.random));
        this.setSize(0.2f, 0.2f);
        this.baseQuadSize = 0.25f + this.random.nextFloat() * 0.3f;
        this.quadSize = this.baseQuadSize;
        this.lifetime = 100 + this.random.nextInt(80);
        this.gravity = 0.003f;
        this.hasPhysics = false;

        this.xd = vx + (this.random.nextDouble() - 0.5) * 0.01;
        this.yd = -0.005 - this.random.nextDouble() * 0.012;
        this.zd = vz + (this.random.nextDouble() - 0.5) * 0.01;

        this.pulsePhase = this.random.nextFloat() * (float) Math.PI * 2;

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

        double driftAngle = this.age * 0.03;
        this.xd += Math.sin(driftAngle + this.pulsePhase) * 0.003;
        this.zd += Math.cos(driftAngle * 0.7 + this.pulsePhase + 1.0) * 0.003;
        this.xd *= 0.97;
        this.zd *= 0.97;

        float pulse = (float) Math.sin(this.age * 0.08 + this.pulsePhase) * 0.15f + 1.0f;
        this.quadSize = this.baseQuadSize * pulse;

        this.move(this.xd, this.yd, this.zd);

        if (this.onGround) {
            this.remove();
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
     * 染梦孢子粒子的工厂类
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
            return new DyedreamParticle(level, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}