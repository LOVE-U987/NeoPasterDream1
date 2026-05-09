
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import net.pasterdream.client.particle.YellowSmokeParticleParticle;
import net.pasterdream.client.particle.WhiteStarParticleParticle;
import net.pasterdream.client.particle.TerraswordWaveParticleParticle;
import net.pasterdream.client.particle.StarcallParticleParticle;
import net.pasterdream.client.particle.SquealWaveParticleParticle;
import net.pasterdream.client.particle.SporeParticleParticle;
import net.pasterdream.client.particle.SoulParticleParticle;
import net.pasterdream.client.particle.Snowflake1ParticleParticle;
import net.pasterdream.client.particle.Snowflake0ParticleParticle;
import net.pasterdream.client.particle.SilverParticleParticle;
import net.pasterdream.client.particle.ShadowStoneParticleParticle;
import net.pasterdream.client.particle.SculkParticleParticle;
import net.pasterdream.client.particle.PoisonSoulParticleParticle;
import net.pasterdream.client.particle.PoisonGasParticleParticle;
import net.pasterdream.client.particle.MeltdreamCrystalParticleParticle;
import net.pasterdream.client.particle.MeltdreamCrystalBigParticleParticle;
import net.pasterdream.client.particle.LightningParticleParticle;
import net.pasterdream.client.particle.LightFireflyGlassJarParticleParticle;
import net.pasterdream.client.particle.LeavesParticleParticle;
import net.pasterdream.client.particle.HealingSpellParticleParticle;
import net.pasterdream.client.particle.GoldenParticleParticle;
import net.pasterdream.client.particle.FurySpellParticleParticle;
import net.pasterdream.client.particle.FoxFire1ParticleParticle;
import net.pasterdream.client.particle.FoxFire0ParticleParticle;
import net.pasterdream.client.particle.FireflyParticleParticle;
import net.pasterdream.client.particle.FireflyGlassJarParticleParticle;
import net.pasterdream.client.particle.FeatherWhiteParticleParticle;
import net.pasterdream.client.particle.Dyedream0ParticleParticle;
import net.pasterdream.client.particle.Dust0ParticleParticle;
import net.pasterdream.client.particle.DreamfertiliterParticleParticle;
import net.pasterdream.client.particle.Crack0ParticleParticle;
import net.pasterdream.client.particle.CalleParticleParticle;
import net.pasterdream.client.particle.Buff0ParticleParticle;
import net.pasterdream.client.particle.Attack0ParticleParticle;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PasterdreamModParticles {
	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(PasterdreamModParticleTypes.LEAVES_PARTICLE.get(), LeavesParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.BUFF_0_PARTICLE.get(), Buff0ParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.DYEDREAM_0_PARTICLE.get(), Dyedream0ParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.DUST_0_PARTICLE.get(), Dust0ParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.CRACK_0_PARTICLE.get(), Crack0ParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.DREAMFERTILITER_PARTICLE.get(), DreamfertiliterParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.SCULK_PARTICLE.get(), SculkParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.ATTACK_0_PARTICLE.get(), Attack0ParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.MELTDREAM_CRYSTAL_PARTICLE.get(), MeltdreamCrystalParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.MELTDREAM_CRYSTAL_BIG_PARTICLE.get(), MeltdreamCrystalBigParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.STARCALL_PARTICLE.get(), StarcallParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.SPORE_PARTICLE.get(), SporeParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.SOUL_PARTICLE.get(), SoulParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.SQUEAL_WAVE_PARTICLE.get(), SquealWaveParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.SHADOW_STONE_PARTICLE.get(), ShadowStoneParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.CALLE_PARTICLE.get(), CalleParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.TERRASWORD_WAVE_PARTICLE.get(), TerraswordWaveParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.POISON_GAS_PARTICLE.get(), PoisonGasParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.POISON_SOUL_PARTICLE.get(), PoisonSoulParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.HEALING_SPELL_PARTICLE.get(), HealingSpellParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.GOLDEN_PARTICLE.get(), GoldenParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.SILVER_PARTICLE.get(), SilverParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.FIREFLY_PARTICLE.get(), FireflyParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.FIREFLY_GLASS_JAR_PARTICLE.get(), FireflyGlassJarParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.LIGHT_FIREFLY_GLASS_JAR_PARTICLE.get(), LightFireflyGlassJarParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.LIGHTNING_PARTICLE.get(), LightningParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.YELLOW_SMOKE_PARTICLE.get(), YellowSmokeParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.FURY_SPELL_PARTICLE.get(), FurySpellParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.SNOWFLAKE_0_PARTICLE.get(), Snowflake0ParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.SNOWFLAKE_1_PARTICLE.get(), Snowflake1ParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.FOX_FIRE_0_PARTICLE.get(), FoxFire0ParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.FOX_FIRE_1_PARTICLE.get(), FoxFire1ParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.FEATHER_WHITE_PARTICLE.get(), FeatherWhiteParticleParticle::provider);
		event.registerSpriteSet(PasterdreamModParticleTypes.WHITE_STAR_PARTICLE.get(), WhiteStarParticleParticle::provider);
	}
}
