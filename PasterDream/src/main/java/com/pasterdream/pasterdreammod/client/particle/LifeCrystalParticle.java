package com.pasterdream.pasterdreammod.client.particle;

import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * 融梦水晶粒子 (Meltdream Crystal Particle)
 * 用于生命水晶的发光粒子效果，带有随机的红粉色系色彩
 * 与原模组 MeltdreamCrystalParticleParticle 功能一致
 */
public class LifeCrystalParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    /**
     * 构造融梦水晶粒子
     *
     * @param level       客户端世界
     * @param x           初始 X 坐标
     * @param y           初始 Y 坐标
     * @param z           初始 Z 坐标
     * @param vx          X 速度
     * @param vy          Y 速度
     * @param vz          Z 速度
     * @param spriteSet   精灵表集合
     */
    protected LifeCrystalParticle(ClientLevel level, double x, double y, double z,
                                   double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);
        this.sprites = spriteSet;
        this.gravity = 0.0f;
        this.hasPhysics = false;

        // 随机颜色：红粉色系 (0.5~1.0 范围)
        this.rCol = 0.5f + this.random.nextFloat() * 0.5f;
        this.gCol = 0.5f + this.random.nextFloat() * 0.5f;
        this.bCol = 0.5f + this.random.nextFloat() * 0.5f;

        // 粒子生命周期：10~30 tick
        this.lifetime = 10 + this.random.nextInt(20);
        // 粒子尺寸
        this.quadSize = 0.5f + this.random.nextFloat();
        // 运动速度
        this.xd = vx + (this.random.nextDouble() - 0.5) * 0.1;
        this.yd = vy + this.random.nextDouble() * 0.1;
        this.zd = vz + (this.random.nextDouble() - 0.5) * 0.1;

        this.setSpriteFromAge(spriteSet);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        // 逐渐淡出
        this.alpha = 1.0f - ((float) this.age / this.lifetime);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /**
     * 融梦水晶粒子的工厂类
     * 负责从客户端接收到的粒子数据创建粒子实例
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
            return new LifeCrystalParticle(level, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}