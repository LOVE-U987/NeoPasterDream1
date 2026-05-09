
package net.pasterdream.client.particle;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.multiplayer.ClientLevel;

@OnlyIn(Dist.CLIENT)
public class MeltdreamCrystalBigParticleParticle extends TextureSheetParticle {
	public static MeltdreamCrystalBigParticleParticleProvider provider(SpriteSet spriteSet) {
		return new MeltdreamCrystalBigParticleParticleProvider(spriteSet);
	}

	public static class MeltdreamCrystalBigParticleParticleProvider implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteSet;

		public MeltdreamCrystalBigParticleParticleProvider(SpriteSet spriteSet) {
			this.spriteSet = spriteSet;
		}

		public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new MeltdreamCrystalBigParticleParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
		}
	}

	private final SpriteSet spriteSet;

	protected MeltdreamCrystalBigParticleParticle(ClientLevel world, double x, double y, double z, double vx, double vy, double vz, SpriteSet spriteSet) {
		super(world, x, y, z);
		this.spriteSet = spriteSet;
		this.setSize(0f, 0f);
		this.quadSize *= 1.2f;
		this.lifetime = (int) Math.max(1, 50 + (this.random.nextInt(10) - 5));
		this.gravity = 0f;
		this.hasPhysics = false;
		this.xd = vx * 0.05;
		this.yd = vy * 0.05;
		this.zd = vz * 0.05;
		this.setSpriteFromAge(spriteSet);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.removed) {
			this.setSprite(this.spriteSet.get((this.age / 3) % 4 + 1, 4));
		}
	}
}
