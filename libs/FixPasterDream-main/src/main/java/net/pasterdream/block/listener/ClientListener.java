package net.pasterdream.block.listener;

import net.pasterdream.init.PasterdreamModBlockEntities;
import net.pasterdream.block.renderer.WindKnightSpawnblock4TileRenderer;
import net.pasterdream.block.renderer.WindKnightSpawnblock3TileRenderer;
import net.pasterdream.block.renderer.WindKnightSpawnblock2TileRenderer;
import net.pasterdream.block.renderer.WindKnightSpawnblock1TileRenderer;
import net.pasterdream.block.renderer.WindKnightSpawnblock0TileRenderer;
import net.pasterdream.block.renderer.WeaponWorkshopTileRenderer;
import net.pasterdream.block.renderer.WeaponTableTileRenderer;
import net.pasterdream.block.renderer.UuzDoll0TileRenderer;
import net.pasterdream.block.renderer.TwilightLanternTileRenderer;
import net.pasterdream.block.renderer.TheEndlessBookOfDreamSeekersTileRenderer;
import net.pasterdream.block.renderer.ShadowVortexTileRenderer;
import net.pasterdream.block.renderer.ShadowTrap0TileRenderer;
import net.pasterdream.block.renderer.ShadowDungeonPortalTileRenderer;
import net.pasterdream.block.renderer.ShadowChestTileRenderer;
import net.pasterdream.block.renderer.ShadowBrazierTileRenderer;
import net.pasterdream.block.renderer.ShadowBlastFurnaceTileRenderer;
import net.pasterdream.block.renderer.ResearchTableTileRenderer;
import net.pasterdream.block.renderer.QymDoll0TileRenderer;
import net.pasterdream.block.renderer.PicnicBasketTileRenderer;
import net.pasterdream.block.renderer.MeltdreamChestTileRenderer;
import net.pasterdream.block.renderer.MeltdreamChestOpenTileRenderer;
import net.pasterdream.block.renderer.LightFireflyGlassJarTileRenderer;
import net.pasterdream.block.renderer.LifeCrystalTileRenderer;
import net.pasterdream.block.renderer.GuardCrystalTileRenderer;
import net.pasterdream.block.renderer.GoldenFoxSculptureTileRenderer;
import net.pasterdream.block.renderer.ForcedTowerTileRenderer;
import net.pasterdream.block.renderer.FireflyGlassJarTileRenderer;
import net.pasterdream.block.renderer.EcologyGlassJarTileRenderer;
import net.pasterdream.block.renderer.DreamCauldronTileRenderer;
import net.pasterdream.block.renderer.DreamAccumulatorTileRenderer;
import net.pasterdream.block.renderer.DesertHeroTombTileRenderer;
import net.pasterdream.block.renderer.BrokenShadowDungeonProtalTileRenderer;
import net.pasterdream.block.renderer.BirdsNestTileRenderer;
import net.pasterdream.block.renderer.AaroncoshandspawnblockTileRenderer;
import net.pasterdream.block.renderer.AaroncosHandChestTileRenderer;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(modid = PasterdreamMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientListener {
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.MELTDREAM_CHEST.get(), context -> new MeltdreamChestTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.MELTDREAM_CHEST_OPEN.get(), context -> new MeltdreamChestOpenTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.DREAM_ACCUMULATOR.get(), context -> new DreamAccumulatorTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.RESEARCH_TABLE.get(), context -> new ResearchTableTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.FORCED_TOWER.get(), context -> new ForcedTowerTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.WEAPON_TABLE.get(), context -> new WeaponTableTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.WEAPON_WORKSHOP.get(), context -> new WeaponWorkshopTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.DESERT_HERO_TOMB.get(), context -> new DesertHeroTombTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.QYM_DOLL_0.get(), context -> new QymDoll0TileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.UUZ_DOLL_0.get(), context -> new UuzDoll0TileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.TWILIGHT_LANTERN.get(), context -> new TwilightLanternTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.SHADOW_CHEST.get(), context -> new ShadowChestTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.SHADOW_BLAST_FURNACE.get(), context -> new ShadowBlastFurnaceTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.GUARD_CRYSTAL.get(), context -> new GuardCrystalTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.BROKEN_SHADOW_DUNGEON_PROTAL.get(), context -> new BrokenShadowDungeonProtalTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.SHADOW_DUNGEON_PORTAL.get(), context -> new ShadowDungeonPortalTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.SHADOW_TRAP_0.get(), context -> new ShadowTrap0TileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.SHADOW_BRAZIER.get(), context -> new ShadowBrazierTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.AARONCOS_HAND_CHEST.get(), context -> new AaroncosHandChestTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.DREAM_CAULDRON.get(), context -> new DreamCauldronTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.BIRDS_NEST.get(), context -> new BirdsNestTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS.get(), context -> new TheEndlessBookOfDreamSeekersTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.ECOLOGY_GLASS_JAR.get(), context -> new EcologyGlassJarTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.FIREFLY_GLASS_JAR.get(), context -> new FireflyGlassJarTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.LIFE_CRYSTAL.get(), context -> new LifeCrystalTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.PICNIC_BASKET.get(), context -> new PicnicBasketTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.WIND_KNIGHT_SPAWNBLOCK_0.get(), context -> new WindKnightSpawnblock0TileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.GOLDEN_FOX_SCULPTURE.get(), context -> new GoldenFoxSculptureTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.SHADOW_VORTEX.get(), context -> new ShadowVortexTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.AARONCOSHANDSPAWNBLOCK.get(), context -> new AaroncoshandspawnblockTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.LIGHT_FIREFLY_GLASS_JAR.get(), context -> new LightFireflyGlassJarTileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.WIND_KNIGHT_SPAWNBLOCK_1.get(), context -> new WindKnightSpawnblock1TileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.WIND_KNIGHT_SPAWNBLOCK_2.get(), context -> new WindKnightSpawnblock2TileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.WIND_KNIGHT_SPAWNBLOCK_3.get(), context -> new WindKnightSpawnblock3TileRenderer());
		event.registerBlockEntityRenderer(PasterdreamModBlockEntities.WIND_KNIGHT_SPAWNBLOCK_4.get(), context -> new WindKnightSpawnblock4TileRenderer());
	}
}
