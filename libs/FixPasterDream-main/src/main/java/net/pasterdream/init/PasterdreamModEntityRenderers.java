
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import net.pasterdream.client.renderer.WindKnightRenderer;
import net.pasterdream.client.renderer.WeakenessTerrorbeakRenderer;
import net.pasterdream.client.renderer.TruestMoltengoldWandProjectileRenderer;
import net.pasterdream.client.renderer.TrueMoltengoldWandProjectileRenderer;
import net.pasterdream.client.renderer.ThundercloudRenderer;
import net.pasterdream.client.renderer.TerrorbeakRenderer;
import net.pasterdream.client.renderer.TerraswordWaveRenderer;
import net.pasterdream.client.renderer.SquealWaveWandProjectileRenderer;
import net.pasterdream.client.renderer.SporeEntityRenderer;
import net.pasterdream.client.renderer.SmallStoneSpiritRenderer;
import net.pasterdream.client.renderer.ShakingCrystalRenderer;
import net.pasterdream.client.renderer.ShadowVortexBookProjectileRenderer;
import net.pasterdream.client.renderer.ShadowTuneTotemRenderer;
import net.pasterdream.client.renderer.ShadowSquealGhostRenderer;
import net.pasterdream.client.renderer.ShadowSquealGhost0Renderer;
import net.pasterdream.client.renderer.ShadowNpc0Renderer;
import net.pasterdream.client.renderer.ShadowMagicballRenderer;
import net.pasterdream.client.renderer.ShadowHandRenderer;
import net.pasterdream.client.renderer.ShadowGolemRenderer;
import net.pasterdream.client.renderer.ShadowGhostRenderer;
import net.pasterdream.client.renderer.PinkSlimeRenderer;
import net.pasterdream.client.renderer.PinkChickenRenderer;
import net.pasterdream.client.renderer.MoltengoldWandProjectileRenderer;
import net.pasterdream.client.renderer.MeltdreamCrystalEntityRenderer;
import net.pasterdream.client.renderer.JellyfishRenderer;
import net.pasterdream.client.renderer.HighvoltageThundercloudRenderer;
import net.pasterdream.client.renderer.HealingSpellEntityRenderer;
import net.pasterdream.client.renderer.GoldenFoxRenderer;
import net.pasterdream.client.renderer.FurySpellEntityRenderer;
import net.pasterdream.client.renderer.FriendlyGhostRenderer;
import net.pasterdream.client.renderer.FoxFireRenderer;
import net.pasterdream.client.renderer.FireflyRenderer;
import net.pasterdream.client.renderer.CrazyTerrorbeakRenderer;
import net.pasterdream.client.renderer.BoneWingRenderer;
import net.pasterdream.client.renderer.BoneWingFireBallProjectileRenderer;
import net.pasterdream.client.renderer.BlackBeetleRenderer;
import net.pasterdream.client.renderer.BlackBeetleMotherRenderer;
import net.pasterdream.client.renderer.BasaltSnailRenderer;
import net.pasterdream.client.renderer.AshBoneWingRenderer;
import net.pasterdream.client.renderer.AaroncosRighthand0Renderer;
import net.pasterdream.client.renderer.AaroncosLefthand0Renderer;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PasterdreamModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(PasterdreamModEntities.PINK_CHICKEN.get(), PinkChickenRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.PINK_SLIME.get(), PinkSlimeRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.BASALT_SNAIL.get(), BasaltSnailRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.SPORE_ENTITY.get(), SporeEntityRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.SHADOW_GOLEM.get(), ShadowGolemRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.TERRORBEAK.get(), TerrorbeakRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.SHADOW_HAND.get(), ShadowHandRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.CRAZY_TERRORBEAK.get(), CrazyTerrorbeakRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.SHADOW_GHOST.get(), ShadowGhostRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.SHADOW_SQUEAL_GHOST.get(), ShadowSquealGhostRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.SHADOW_SQUEAL_GHOST_0.get(), ShadowSquealGhost0Renderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.FRIENDLY_GHOST.get(), FriendlyGhostRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.BLACK_BEETLE.get(), BlackBeetleRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.BLACK_BEETLE_MOTHER.get(), BlackBeetleMotherRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.SHADOW_NPC_0.get(), ShadowNpc0Renderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.WEAKENESS_TERRORBEAK.get(), WeakenessTerrorbeakRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.AARONCOS_LEFTHAND_0.get(), AaroncosLefthand0Renderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.AARONCOS_RIGHTHAND_0.get(), AaroncosRighthand0Renderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.SHADOW_TUNE_TOTEM.get(), ShadowTuneTotemRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.SHAKING_CRYSTAL.get(), ShakingCrystalRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.BONE_WING.get(), BoneWingRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.ASH_BONE_WING.get(), AshBoneWingRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.FIREFLY.get(), FireflyRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.THUNDERCLOUD.get(), ThundercloudRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.HIGHVOLTAGE_THUNDERCLOUD.get(), HighvoltageThundercloudRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.JELLYFISH.get(), JellyfishRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.WIND_KNIGHT.get(), WindKnightRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.GOLDEN_FOX.get(), GoldenFoxRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.MELTDREAM_CRYSTAL_ENTITY.get(), MeltdreamCrystalEntityRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.TERRASWORD_WAVE.get(), TerraswordWaveRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.SHADOW_MAGICBALL.get(), ShadowMagicballRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.HEALING_SPELL_ENTITY.get(), HealingSpellEntityRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.PINKEGG_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.MOLTENGOLD_WAND_PROJECTILE.get(), MoltengoldWandProjectileRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.TRUE_MOLTENGOLD_WAND_PROJECTILE.get(), TrueMoltengoldWandProjectileRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.TRUEST_MOLTENGOLD_WAND_PROJECTILE.get(), TruestMoltengoldWandProjectileRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.SQUEAL_WAVE_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.WHITE_SWORD_RAIN_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.SQUEAL_WAVE_WAND_PROJECTILE.get(), SquealWaveWandProjectileRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.LIGHTNING_SPELL_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.SHADOW_VORTEX_BOOK_PROJECTILE.get(), ShadowVortexBookProjectileRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.POISON_SPELL_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.HEALING_SPELL_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.BONE_WING_FIRE_BALL_PROJECTILE.get(), BoneWingFireBallProjectileRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.LIGHTNING_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.FURY_SPELL_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.FURY_SPELL_ENTITY.get(), FurySpellEntityRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.ICE_SPELL_PROJECTILE.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.STRAWBERRY_HEART_PROJECT.get(), ThrownItemRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.FOX_FIRE.get(), FoxFireRenderer::new);
		event.registerEntityRenderer(PasterdreamModEntities.SMALL_STONE_SPIRIT.get(), SmallStoneSpiritRenderer::new);
	}
}
