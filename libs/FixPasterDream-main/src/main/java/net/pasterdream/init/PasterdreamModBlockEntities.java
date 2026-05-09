
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import net.pasterdream.block.entity.WorkshopGrindBlockEntity;
import net.pasterdream.block.entity.WorkshopCauldeonBlockEntity;
import net.pasterdream.block.entity.WorkshopBlastBlockEntity;
import net.pasterdream.block.entity.WorkshopAnvilBlockEntity;
import net.pasterdream.block.entity.WindmoorCrateBlockEntity;
import net.pasterdream.block.entity.WindKnightSpawnblock4TileEntity;
import net.pasterdream.block.entity.WindKnightSpawnblock3TileEntity;
import net.pasterdream.block.entity.WindKnightSpawnblock2TileEntity;
import net.pasterdream.block.entity.WindKnightSpawnblock1TileEntity;
import net.pasterdream.block.entity.WindKnightSpawnblock0TileEntity;
import net.pasterdream.block.entity.WeaponWorkshopTileEntity;
import net.pasterdream.block.entity.WeaponTableTileEntity;
import net.pasterdream.block.entity.UuzDoll0TileEntity;
import net.pasterdream.block.entity.TwilightLanternTileEntity;
import net.pasterdream.block.entity.TrueShadowBedBlockEntity;
import net.pasterdream.block.entity.TheEndlessBookOfDreamSeekersTileEntity;
import net.pasterdream.block.entity.StructureBlock9BlockEntity;
import net.pasterdream.block.entity.StructureBlock8BlockEntity;
import net.pasterdream.block.entity.StructureBlock7BlockEntity;
import net.pasterdream.block.entity.StructureBlock6BlockEntity;
import net.pasterdream.block.entity.StructureBlock5BlockEntity;
import net.pasterdream.block.entity.StructureBlock4BlockEntity;
import net.pasterdream.block.entity.StructureBlock3BlockEntity;
import net.pasterdream.block.entity.StructureBlock2BlockEntity;
import net.pasterdream.block.entity.StructureBlock23BlockEntity;
import net.pasterdream.block.entity.StructureBlock22BlockEntity;
import net.pasterdream.block.entity.StructureBlock21BlockEntity;
import net.pasterdream.block.entity.StructureBlock20BlockEntity;
import net.pasterdream.block.entity.StructureBlock1BlockEntity;
import net.pasterdream.block.entity.StructureBlock19BlockEntity;
import net.pasterdream.block.entity.StructureBlock18BlockEntity;
import net.pasterdream.block.entity.StructureBlock17BlockEntity;
import net.pasterdream.block.entity.StructureBlock16BlockEntity;
import net.pasterdream.block.entity.StructureBlock15BlockEntity;
import net.pasterdream.block.entity.StructureBlock14BlockEntity;
import net.pasterdream.block.entity.StructureBlock13BlockEntity;
import net.pasterdream.block.entity.StructureBlock12BlockEntity;
import net.pasterdream.block.entity.StructureBlock11BlockEntity;
import net.pasterdream.block.entity.StructureBlock10BlockEntity;
import net.pasterdream.block.entity.StructureBlock0BlockEntity;
import net.pasterdream.block.entity.ShadowVortexTileEntity;
import net.pasterdream.block.entity.ShadowTrap0TileEntity;
import net.pasterdream.block.entity.ShadowFungusBlockEntity;
import net.pasterdream.block.entity.ShadowDungeonPortalTileEntity;
import net.pasterdream.block.entity.ShadowDeskBlockEntity;
import net.pasterdream.block.entity.ShadowChestTileEntity;
import net.pasterdream.block.entity.ShadowBrazierTileEntity;
import net.pasterdream.block.entity.ShadowBlastFurnaceTileEntity;
import net.pasterdream.block.entity.ShadowBedBlockEntity;
import net.pasterdream.block.entity.RestrainmoveBlockBlockEntity;
import net.pasterdream.block.entity.ResearchTableTileEntity;
import net.pasterdream.block.entity.QymDoll0TileEntity;
import net.pasterdream.block.entity.PicnicBasketTileEntity;
import net.pasterdream.block.entity.MeltdreamChestTileEntity;
import net.pasterdream.block.entity.MeltdreamChestOpenTileEntity;
import net.pasterdream.block.entity.LostSwordBlockBlockEntity;
import net.pasterdream.block.entity.LightFireflyGlassJarTileEntity;
import net.pasterdream.block.entity.LifeCrystalTileEntity;
import net.pasterdream.block.entity.GuardCrystalTileEntity;
import net.pasterdream.block.entity.GuardBlockBlockEntity;
import net.pasterdream.block.entity.Grass14BlockEntity;
import net.pasterdream.block.entity.GoldenFoxSculptureTileEntity;
import net.pasterdream.block.entity.ForcedTowerTileEntity;
import net.pasterdream.block.entity.Flower17BlockEntity;
import net.pasterdream.block.entity.FireflyNestBlockEntity;
import net.pasterdream.block.entity.FireflyGlassJarTileEntity;
import net.pasterdream.block.entity.EcologyGlassJarTileEntity;
import net.pasterdream.block.entity.DyedreamSaplingBlockEntity;
import net.pasterdream.block.entity.DyedreamDeskBlockEntity;
import net.pasterdream.block.entity.DyedreamCrackBlockEntity;
import net.pasterdream.block.entity.DreamSpawner0BlockEntity;
import net.pasterdream.block.entity.DreamCauldronTileEntity;
import net.pasterdream.block.entity.DreamAccumulatorTileEntity;
import net.pasterdream.block.entity.DesertHeroTombTileEntity;
import net.pasterdream.block.entity.Claypan1BlockEntity;
import net.pasterdream.block.entity.BrokenShadowDungeonProtalTileEntity;
import net.pasterdream.block.entity.BirdsNestTileEntity;
import net.pasterdream.block.entity.AaroncoshandspawnblockTileEntity;
import net.pasterdream.block.entity.AaroncosHandChestTileEntity;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;

public class PasterdreamModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PasterdreamMod.MODID);
	public static final RegistryObject<BlockEntityType<MeltdreamChestTileEntity>> MELTDREAM_CHEST = REGISTRY.register("meltdream_chest",
			() -> BlockEntityType.Builder.of(MeltdreamChestTileEntity::new, PasterdreamModBlocks.MELTDREAM_CHEST.get()).build(null));
	public static final RegistryObject<BlockEntityType<MeltdreamChestOpenTileEntity>> MELTDREAM_CHEST_OPEN = REGISTRY.register("meltdream_chest_open",
			() -> BlockEntityType.Builder.of(MeltdreamChestOpenTileEntity::new, PasterdreamModBlocks.MELTDREAM_CHEST_OPEN.get()).build(null));
	public static final RegistryObject<BlockEntityType<DreamAccumulatorTileEntity>> DREAM_ACCUMULATOR = REGISTRY.register("dream_accumulator",
			() -> BlockEntityType.Builder.of(DreamAccumulatorTileEntity::new, PasterdreamModBlocks.DREAM_ACCUMULATOR.get()).build(null));
	public static final RegistryObject<BlockEntityType<ResearchTableTileEntity>> RESEARCH_TABLE = REGISTRY.register("research_table",
			() -> BlockEntityType.Builder.of(ResearchTableTileEntity::new, PasterdreamModBlocks.RESEARCH_TABLE.get()).build(null));
	public static final RegistryObject<BlockEntityType<ForcedTowerTileEntity>> FORCED_TOWER = REGISTRY.register("forced_tower", () -> BlockEntityType.Builder.of(ForcedTowerTileEntity::new, PasterdreamModBlocks.FORCED_TOWER.get()).build(null));
	public static final RegistryObject<BlockEntityType<WeaponTableTileEntity>> WEAPON_TABLE = REGISTRY.register("weapon_table", () -> BlockEntityType.Builder.of(WeaponTableTileEntity::new, PasterdreamModBlocks.WEAPON_TABLE.get()).build(null));
	public static final RegistryObject<BlockEntityType<WeaponWorkshopTileEntity>> WEAPON_WORKSHOP = REGISTRY.register("weapon_workshop",
			() -> BlockEntityType.Builder.of(WeaponWorkshopTileEntity::new, PasterdreamModBlocks.WEAPON_WORKSHOP.get()).build(null));
	public static final RegistryObject<BlockEntityType<?>> DYEDREAM_DESK = register("dyedream_desk", PasterdreamModBlocks.DYEDREAM_DESK, DyedreamDeskBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> LOST_SWORD_BLOCK = register("lost_sword_block", PasterdreamModBlocks.LOST_SWORD_BLOCK, LostSwordBlockBlockEntity::new);
	public static final RegistryObject<BlockEntityType<DesertHeroTombTileEntity>> DESERT_HERO_TOMB = REGISTRY.register("desert_hero_tomb",
			() -> BlockEntityType.Builder.of(DesertHeroTombTileEntity::new, PasterdreamModBlocks.DESERT_HERO_TOMB.get()).build(null));
	public static final RegistryObject<BlockEntityType<QymDoll0TileEntity>> QYM_DOLL_0 = REGISTRY.register("qym_doll_0", () -> BlockEntityType.Builder.of(QymDoll0TileEntity::new, PasterdreamModBlocks.QYM_DOLL_0.get()).build(null));
	public static final RegistryObject<BlockEntityType<UuzDoll0TileEntity>> UUZ_DOLL_0 = REGISTRY.register("uuz_doll_0", () -> BlockEntityType.Builder.of(UuzDoll0TileEntity::new, PasterdreamModBlocks.UUZ_DOLL_0.get()).build(null));
	public static final RegistryObject<BlockEntityType<TwilightLanternTileEntity>> TWILIGHT_LANTERN = REGISTRY.register("twilight_lantern",
			() -> BlockEntityType.Builder.of(TwilightLanternTileEntity::new, PasterdreamModBlocks.TWILIGHT_LANTERN.get()).build(null));
	public static final RegistryObject<BlockEntityType<?>> SHADOW_BED = register("shadow_bed", PasterdreamModBlocks.SHADOW_BED, ShadowBedBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> SHADOW_DESK = register("shadow_desk", PasterdreamModBlocks.SHADOW_DESK, ShadowDeskBlockEntity::new);
	public static final RegistryObject<BlockEntityType<ShadowChestTileEntity>> SHADOW_CHEST = REGISTRY.register("shadow_chest", () -> BlockEntityType.Builder.of(ShadowChestTileEntity::new, PasterdreamModBlocks.SHADOW_CHEST.get()).build(null));
	public static final RegistryObject<BlockEntityType<ShadowBlastFurnaceTileEntity>> SHADOW_BLAST_FURNACE = REGISTRY.register("shadow_blast_furnace",
			() -> BlockEntityType.Builder.of(ShadowBlastFurnaceTileEntity::new, PasterdreamModBlocks.SHADOW_BLAST_FURNACE.get()).build(null));
	public static final RegistryObject<BlockEntityType<GuardCrystalTileEntity>> GUARD_CRYSTAL = REGISTRY.register("guard_crystal", () -> BlockEntityType.Builder.of(GuardCrystalTileEntity::new, PasterdreamModBlocks.GUARD_CRYSTAL.get()).build(null));
	public static final RegistryObject<BlockEntityType<BrokenShadowDungeonProtalTileEntity>> BROKEN_SHADOW_DUNGEON_PROTAL = REGISTRY.register("broken_shadow_dungeon_protal",
			() -> BlockEntityType.Builder.of(BrokenShadowDungeonProtalTileEntity::new, PasterdreamModBlocks.BROKEN_SHADOW_DUNGEON_PROTAL.get()).build(null));
	public static final RegistryObject<BlockEntityType<ShadowDungeonPortalTileEntity>> SHADOW_DUNGEON_PORTAL = REGISTRY.register("shadow_dungeon_portal",
			() -> BlockEntityType.Builder.of(ShadowDungeonPortalTileEntity::new, PasterdreamModBlocks.SHADOW_DUNGEON_PORTAL.get()).build(null));
	public static final RegistryObject<BlockEntityType<ShadowTrap0TileEntity>> SHADOW_TRAP_0 = REGISTRY.register("shadow_trap_0", () -> BlockEntityType.Builder.of(ShadowTrap0TileEntity::new, PasterdreamModBlocks.SHADOW_TRAP_0.get()).build(null));
	public static final RegistryObject<BlockEntityType<ShadowBrazierTileEntity>> SHADOW_BRAZIER = REGISTRY.register("shadow_brazier",
			() -> BlockEntityType.Builder.of(ShadowBrazierTileEntity::new, PasterdreamModBlocks.SHADOW_BRAZIER.get()).build(null));
	public static final RegistryObject<BlockEntityType<AaroncosHandChestTileEntity>> AARONCOS_HAND_CHEST = REGISTRY.register("aaroncos_hand_chest",
			() -> BlockEntityType.Builder.of(AaroncosHandChestTileEntity::new, PasterdreamModBlocks.AARONCOS_HAND_CHEST.get()).build(null));
	public static final RegistryObject<BlockEntityType<DreamCauldronTileEntity>> DREAM_CAULDRON = REGISTRY.register("dream_cauldron",
			() -> BlockEntityType.Builder.of(DreamCauldronTileEntity::new, PasterdreamModBlocks.DREAM_CAULDRON.get()).build(null));
	public static final RegistryObject<BlockEntityType<BirdsNestTileEntity>> BIRDS_NEST = REGISTRY.register("birds_nest", () -> BlockEntityType.Builder.of(BirdsNestTileEntity::new, PasterdreamModBlocks.BIRDS_NEST.get()).build(null));
	public static final RegistryObject<BlockEntityType<?>> WINDMOOR_CRATE = register("windmoor_crate", PasterdreamModBlocks.WINDMOOR_CRATE, WindmoorCrateBlockEntity::new);
	public static final RegistryObject<BlockEntityType<TheEndlessBookOfDreamSeekersTileEntity>> THE_ENDLESS_BOOK_OF_DREAM_SEEKERS = REGISTRY.register("the_endless_book_of_dream_seekers",
			() -> BlockEntityType.Builder.of(TheEndlessBookOfDreamSeekersTileEntity::new, PasterdreamModBlocks.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS.get()).build(null));
	public static final RegistryObject<BlockEntityType<?>> FIREFLY_NEST = register("firefly_nest", PasterdreamModBlocks.FIREFLY_NEST, FireflyNestBlockEntity::new);
	public static final RegistryObject<BlockEntityType<EcologyGlassJarTileEntity>> ECOLOGY_GLASS_JAR = REGISTRY.register("ecology_glass_jar",
			() -> BlockEntityType.Builder.of(EcologyGlassJarTileEntity::new, PasterdreamModBlocks.ECOLOGY_GLASS_JAR.get()).build(null));
	public static final RegistryObject<BlockEntityType<FireflyGlassJarTileEntity>> FIREFLY_GLASS_JAR = REGISTRY.register("firefly_glass_jar",
			() -> BlockEntityType.Builder.of(FireflyGlassJarTileEntity::new, PasterdreamModBlocks.FIREFLY_GLASS_JAR.get()).build(null));
	public static final RegistryObject<BlockEntityType<LifeCrystalTileEntity>> LIFE_CRYSTAL = REGISTRY.register("life_crystal", () -> BlockEntityType.Builder.of(LifeCrystalTileEntity::new, PasterdreamModBlocks.LIFE_CRYSTAL.get()).build(null));
	public static final RegistryObject<BlockEntityType<PicnicBasketTileEntity>> PICNIC_BASKET = REGISTRY.register("picnic_basket", () -> BlockEntityType.Builder.of(PicnicBasketTileEntity::new, PasterdreamModBlocks.PICNIC_BASKET.get()).build(null));
	public static final RegistryObject<BlockEntityType<WindKnightSpawnblock0TileEntity>> WIND_KNIGHT_SPAWNBLOCK_0 = REGISTRY.register("wind_knight_spawnblock_0",
			() -> BlockEntityType.Builder.of(WindKnightSpawnblock0TileEntity::new, PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_0.get()).build(null));
	public static final RegistryObject<BlockEntityType<GoldenFoxSculptureTileEntity>> GOLDEN_FOX_SCULPTURE = REGISTRY.register("golden_fox_sculpture",
			() -> BlockEntityType.Builder.of(GoldenFoxSculptureTileEntity::new, PasterdreamModBlocks.GOLDEN_FOX_SCULPTURE.get()).build(null));
	public static final RegistryObject<BlockEntityType<?>> DYEDREAM_CRACK = register("dyedream_crack", PasterdreamModBlocks.DYEDREAM_CRACK, DyedreamCrackBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> GUARD_BLOCK = register("guard_block", PasterdreamModBlocks.GUARD_BLOCK, GuardBlockBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> RESTRAINMOVE_BLOCK = register("restrainmove_block", PasterdreamModBlocks.RESTRAINMOVE_BLOCK, RestrainmoveBlockBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> DREAM_SPAWNER_0 = register("dream_spawner_0", PasterdreamModBlocks.DREAM_SPAWNER_0, DreamSpawner0BlockEntity::new);
	public static final RegistryObject<BlockEntityType<ShadowVortexTileEntity>> SHADOW_VORTEX = REGISTRY.register("shadow_vortex", () -> BlockEntityType.Builder.of(ShadowVortexTileEntity::new, PasterdreamModBlocks.SHADOW_VORTEX.get()).build(null));
	public static final RegistryObject<BlockEntityType<?>> WORKSHOP_CAULDEON = register("workshop_cauldeon", PasterdreamModBlocks.WORKSHOP_CAULDEON, WorkshopCauldeonBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> WORKSHOP_BLAST = register("workshop_blast", PasterdreamModBlocks.WORKSHOP_BLAST, WorkshopBlastBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> WORKSHOP_ANVIL = register("workshop_anvil", PasterdreamModBlocks.WORKSHOP_ANVIL, WorkshopAnvilBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> WORKSHOP_GRIND = register("workshop_grind", PasterdreamModBlocks.WORKSHOP_GRIND, WorkshopGrindBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> DYEDREAM_SAPLING = register("dyedream_sapling", PasterdreamModBlocks.DYEDREAM_SAPLING, DyedreamSaplingBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> FLOWER_17 = register("flower_17", PasterdreamModBlocks.FLOWER_17, Flower17BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> SHADOW_FUNGUS = register("shadow_fungus", PasterdreamModBlocks.SHADOW_FUNGUS, ShadowFungusBlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> GRASS_14 = register("grass_14", PasterdreamModBlocks.GRASS_14, Grass14BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_0 = register("structure_block_0", PasterdreamModBlocks.STRUCTURE_BLOCK_0, StructureBlock0BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_1 = register("structure_block_1", PasterdreamModBlocks.STRUCTURE_BLOCK_1, StructureBlock1BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_2 = register("structure_block_2", PasterdreamModBlocks.STRUCTURE_BLOCK_2, StructureBlock2BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_3 = register("structure_block_3", PasterdreamModBlocks.STRUCTURE_BLOCK_3, StructureBlock3BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_4 = register("structure_block_4", PasterdreamModBlocks.STRUCTURE_BLOCK_4, StructureBlock4BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_5 = register("structure_block_5", PasterdreamModBlocks.STRUCTURE_BLOCK_5, StructureBlock5BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_6 = register("structure_block_6", PasterdreamModBlocks.STRUCTURE_BLOCK_6, StructureBlock6BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_7 = register("structure_block_7", PasterdreamModBlocks.STRUCTURE_BLOCK_7, StructureBlock7BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_8 = register("structure_block_8", PasterdreamModBlocks.STRUCTURE_BLOCK_8, StructureBlock8BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_9 = register("structure_block_9", PasterdreamModBlocks.STRUCTURE_BLOCK_9, StructureBlock9BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_10 = register("structure_block_10", PasterdreamModBlocks.STRUCTURE_BLOCK_10, StructureBlock10BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_11 = register("structure_block_11", PasterdreamModBlocks.STRUCTURE_BLOCK_11, StructureBlock11BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_12 = register("structure_block_12", PasterdreamModBlocks.STRUCTURE_BLOCK_12, StructureBlock12BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_13 = register("structure_block_13", PasterdreamModBlocks.STRUCTURE_BLOCK_13, StructureBlock13BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_14 = register("structure_block_14", PasterdreamModBlocks.STRUCTURE_BLOCK_14, StructureBlock14BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_15 = register("structure_block_15", PasterdreamModBlocks.STRUCTURE_BLOCK_15, StructureBlock15BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> CLAYPAN_1 = register("claypan_1", PasterdreamModBlocks.CLAYPAN_1, Claypan1BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_16 = register("structure_block_16", PasterdreamModBlocks.STRUCTURE_BLOCK_16, StructureBlock16BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_17 = register("structure_block_17", PasterdreamModBlocks.STRUCTURE_BLOCK_17, StructureBlock17BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> TRUE_SHADOW_BED = register("true_shadow_bed", PasterdreamModBlocks.TRUE_SHADOW_BED, TrueShadowBedBlockEntity::new);
	public static final RegistryObject<BlockEntityType<AaroncoshandspawnblockTileEntity>> AARONCOSHANDSPAWNBLOCK = REGISTRY.register("aaroncoshandspawnblock",
			() -> BlockEntityType.Builder.of(AaroncoshandspawnblockTileEntity::new, PasterdreamModBlocks.AARONCOSHANDSPAWNBLOCK.get()).build(null));
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_18 = register("structure_block_18", PasterdreamModBlocks.STRUCTURE_BLOCK_18, StructureBlock18BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_19 = register("structure_block_19", PasterdreamModBlocks.STRUCTURE_BLOCK_19, StructureBlock19BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_20 = register("structure_block_20", PasterdreamModBlocks.STRUCTURE_BLOCK_20, StructureBlock20BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_21 = register("structure_block_21", PasterdreamModBlocks.STRUCTURE_BLOCK_21, StructureBlock21BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_22 = register("structure_block_22", PasterdreamModBlocks.STRUCTURE_BLOCK_22, StructureBlock22BlockEntity::new);
	public static final RegistryObject<BlockEntityType<?>> STRUCTURE_BLOCK_23 = register("structure_block_23", PasterdreamModBlocks.STRUCTURE_BLOCK_23, StructureBlock23BlockEntity::new);
	public static final RegistryObject<BlockEntityType<LightFireflyGlassJarTileEntity>> LIGHT_FIREFLY_GLASS_JAR = REGISTRY.register("light_firefly_glass_jar",
			() -> BlockEntityType.Builder.of(LightFireflyGlassJarTileEntity::new, PasterdreamModBlocks.LIGHT_FIREFLY_GLASS_JAR.get()).build(null));
	public static final RegistryObject<BlockEntityType<WindKnightSpawnblock1TileEntity>> WIND_KNIGHT_SPAWNBLOCK_1 = REGISTRY.register("wind_knight_spawnblock_1",
			() -> BlockEntityType.Builder.of(WindKnightSpawnblock1TileEntity::new, PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_1.get()).build(null));
	public static final RegistryObject<BlockEntityType<WindKnightSpawnblock2TileEntity>> WIND_KNIGHT_SPAWNBLOCK_2 = REGISTRY.register("wind_knight_spawnblock_2",
			() -> BlockEntityType.Builder.of(WindKnightSpawnblock2TileEntity::new, PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_2.get()).build(null));
	public static final RegistryObject<BlockEntityType<WindKnightSpawnblock3TileEntity>> WIND_KNIGHT_SPAWNBLOCK_3 = REGISTRY.register("wind_knight_spawnblock_3",
			() -> BlockEntityType.Builder.of(WindKnightSpawnblock3TileEntity::new, PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_3.get()).build(null));
	public static final RegistryObject<BlockEntityType<WindKnightSpawnblock4TileEntity>> WIND_KNIGHT_SPAWNBLOCK_4 = REGISTRY.register("wind_knight_spawnblock_4",
			() -> BlockEntityType.Builder.of(WindKnightSpawnblock4TileEntity::new, PasterdreamModBlocks.WIND_KNIGHT_SPAWNBLOCK_4.get()).build(null));

	private static RegistryObject<BlockEntityType<?>> register(String registryname, RegistryObject<Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}
}
