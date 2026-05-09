
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import net.pasterdream.entity.WindKnightEntity;
import net.pasterdream.entity.WhiteSwordRainProjectileEntity;
import net.pasterdream.entity.WeakenessTerrorbeakEntity;
import net.pasterdream.entity.TruestMoltengoldWandProjectileEntity;
import net.pasterdream.entity.TrueMoltengoldWandProjectileEntity;
import net.pasterdream.entity.ThundercloudEntity;
import net.pasterdream.entity.TerrorbeakEntity;
import net.pasterdream.entity.TerraswordWaveEntity;
import net.pasterdream.entity.StrawberryHeartProjectEntity;
import net.pasterdream.entity.SquealWaveWandProjectileEntity;
import net.pasterdream.entity.SquealWaveProjectileEntity;
import net.pasterdream.entity.SporeEntityEntity;
import net.pasterdream.entity.SmallStoneSpiritEntity;
import net.pasterdream.entity.ShakingCrystalEntity;
import net.pasterdream.entity.ShadowVortexBookProjectileEntity;
import net.pasterdream.entity.ShadowTuneTotemEntity;
import net.pasterdream.entity.ShadowSquealGhostEntity;
import net.pasterdream.entity.ShadowSquealGhost0Entity;
import net.pasterdream.entity.ShadowNpc0Entity;
import net.pasterdream.entity.ShadowMagicballEntity;
import net.pasterdream.entity.ShadowHandEntity;
import net.pasterdream.entity.ShadowGolemEntity;
import net.pasterdream.entity.ShadowGhostEntity;
import net.pasterdream.entity.PoisonSpellProjectileEntity;
import net.pasterdream.entity.PinkeggProjectileEntity;
import net.pasterdream.entity.PinkSlimeEntity;
import net.pasterdream.entity.PinkChickenEntity;
import net.pasterdream.entity.MoltengoldWandProjectileEntity;
import net.pasterdream.entity.MeltdreamCrystalEntityEntity;
import net.pasterdream.entity.LightningSpellProjectileEntity;
import net.pasterdream.entity.LightningProjectileEntity;
import net.pasterdream.entity.JellyfishEntity;
import net.pasterdream.entity.IceSpellProjectileEntity;
import net.pasterdream.entity.HighvoltageThundercloudEntity;
import net.pasterdream.entity.HealingSpellProjectileEntity;
import net.pasterdream.entity.HealingSpellEntityEntity;
import net.pasterdream.entity.GoldenFoxEntity;
import net.pasterdream.entity.FurySpellProjectileEntity;
import net.pasterdream.entity.FurySpellEntityEntity;
import net.pasterdream.entity.FriendlyGhostEntity;
import net.pasterdream.entity.FoxFireEntity;
import net.pasterdream.entity.FireflyEntity;
import net.pasterdream.entity.CrazyTerrorbeakEntity;
import net.pasterdream.entity.BoneWingFireBallProjectileEntity;
import net.pasterdream.entity.BoneWingEntity;
import net.pasterdream.entity.BlackBeetleMotherEntity;
import net.pasterdream.entity.BlackBeetleEntity;
import net.pasterdream.entity.BasaltSnailEntity;
import net.pasterdream.entity.AshBoneWingEntity;
import net.pasterdream.entity.AaroncosRighthand0Entity;
import net.pasterdream.entity.AaroncosLefthand0Entity;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PasterdreamModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PasterdreamMod.MODID);
	public static final RegistryObject<EntityType<PinkChickenEntity>> PINK_CHICKEN = register("pink_chicken",
			EntityType.Builder.<PinkChickenEntity>of(PinkChickenEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(PinkChickenEntity::new)

					.sized(0.4f, 0.7f));
	public static final RegistryObject<EntityType<PinkSlimeEntity>> PINK_SLIME = register("pink_slime",
			EntityType.Builder.<PinkSlimeEntity>of(PinkSlimeEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(PinkSlimeEntity::new)

					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<BasaltSnailEntity>> BASALT_SNAIL = register("basalt_snail", EntityType.Builder.<BasaltSnailEntity>of(BasaltSnailEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(BasaltSnailEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<SporeEntityEntity>> SPORE_ENTITY = register("spore_entity",
			EntityType.Builder.<SporeEntityEntity>of(SporeEntityEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(SporeEntityEntity::new)

					.sized(0.2f, 0.2f));
	public static final RegistryObject<EntityType<ShadowGolemEntity>> SHADOW_GOLEM = register("shadow_golem",
			EntityType.Builder.<ShadowGolemEntity>of(ShadowGolemEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ShadowGolemEntity::new)

					.sized(2.2f, 3.5f));
	public static final RegistryObject<EntityType<TerrorbeakEntity>> TERRORBEAK = register("terrorbeak", EntityType.Builder.<TerrorbeakEntity>of(TerrorbeakEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
			.setUpdateInterval(3).setCustomClientFactory(TerrorbeakEntity::new).fireImmune().sized(1.8f, 4f));
	public static final RegistryObject<EntityType<ShadowHandEntity>> SHADOW_HAND = register("shadow_hand", EntityType.Builder.<ShadowHandEntity>of(ShadowHandEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
			.setUpdateInterval(3).setCustomClientFactory(ShadowHandEntity::new).fireImmune().sized(0.6f, 0.8f));
	public static final RegistryObject<EntityType<CrazyTerrorbeakEntity>> CRAZY_TERRORBEAK = register("crazy_terrorbeak", EntityType.Builder.<CrazyTerrorbeakEntity>of(CrazyTerrorbeakEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(CrazyTerrorbeakEntity::new).fireImmune().sized(1.8f, 4f));
	public static final RegistryObject<EntityType<ShadowGhostEntity>> SHADOW_GHOST = register("shadow_ghost", EntityType.Builder.<ShadowGhostEntity>of(ShadowGhostEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ShadowGhostEntity::new).fireImmune().sized(0.7f, 1.2f));
	public static final RegistryObject<EntityType<ShadowSquealGhostEntity>> SHADOW_SQUEAL_GHOST = register("shadow_squeal_ghost", EntityType.Builder.<ShadowSquealGhostEntity>of(ShadowSquealGhostEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ShadowSquealGhostEntity::new).fireImmune().sized(0.7f, 1.2f));
	public static final RegistryObject<EntityType<ShadowSquealGhost0Entity>> SHADOW_SQUEAL_GHOST_0 = register("shadow_squeal_ghost_0", EntityType.Builder.<ShadowSquealGhost0Entity>of(ShadowSquealGhost0Entity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ShadowSquealGhost0Entity::new).fireImmune().sized(0.8f, 1.3f));
	public static final RegistryObject<EntityType<FriendlyGhostEntity>> FRIENDLY_GHOST = register("friendly_ghost", EntityType.Builder.<FriendlyGhostEntity>of(FriendlyGhostEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(FriendlyGhostEntity::new).fireImmune().sized(0.7f, 1.2f));
	public static final RegistryObject<EntityType<BlackBeetleEntity>> BLACK_BEETLE = register("black_beetle",
			EntityType.Builder.<BlackBeetleEntity>of(BlackBeetleEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(48).setUpdateInterval(3).setCustomClientFactory(BlackBeetleEntity::new)

					.sized(0.6f, 0.5f));
	public static final RegistryObject<EntityType<BlackBeetleMotherEntity>> BLACK_BEETLE_MOTHER = register("black_beetle_mother",
			EntityType.Builder.<BlackBeetleMotherEntity>of(BlackBeetleMotherEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(48).setUpdateInterval(3).setCustomClientFactory(BlackBeetleMotherEntity::new)

					.sized(2f, 1f));
	public static final RegistryObject<EntityType<ShadowNpc0Entity>> SHADOW_NPC_0 = register("shadow_npc_0", EntityType.Builder.<ShadowNpc0Entity>of(ShadowNpc0Entity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ShadowNpc0Entity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<WeakenessTerrorbeakEntity>> WEAKENESS_TERRORBEAK = register("weakeness_terrorbeak", EntityType.Builder.<WeakenessTerrorbeakEntity>of(WeakenessTerrorbeakEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(WeakenessTerrorbeakEntity::new).fireImmune().sized(1.5f, 3f));
	public static final RegistryObject<EntityType<AaroncosLefthand0Entity>> AARONCOS_LEFTHAND_0 = register("aaroncos_lefthand_0", EntityType.Builder.<AaroncosLefthand0Entity>of(AaroncosLefthand0Entity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(AaroncosLefthand0Entity::new).fireImmune().sized(3.5f, 3.5f));
	public static final RegistryObject<EntityType<AaroncosRighthand0Entity>> AARONCOS_RIGHTHAND_0 = register("aaroncos_righthand_0", EntityType.Builder.<AaroncosRighthand0Entity>of(AaroncosRighthand0Entity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(AaroncosRighthand0Entity::new).fireImmune().sized(3.5f, 3.5f));
	public static final RegistryObject<EntityType<ShadowTuneTotemEntity>> SHADOW_TUNE_TOTEM = register("shadow_tune_totem", EntityType.Builder.<ShadowTuneTotemEntity>of(ShadowTuneTotemEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ShadowTuneTotemEntity::new).fireImmune().sized(2f, 8f));
	public static final RegistryObject<EntityType<ShakingCrystalEntity>> SHAKING_CRYSTAL = register("shaking_crystal", EntityType.Builder.<ShakingCrystalEntity>of(ShakingCrystalEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ShakingCrystalEntity::new).fireImmune().sized(0.6f, 1.8f));
	public static final RegistryObject<EntityType<BoneWingEntity>> BONE_WING = register("bone_wing",
			EntityType.Builder.<BoneWingEntity>of(BoneWingEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(BoneWingEntity::new)

					.sized(1.7f, 0.8f));
	public static final RegistryObject<EntityType<AshBoneWingEntity>> ASH_BONE_WING = register("ash_bone_wing",
			EntityType.Builder.<AshBoneWingEntity>of(AshBoneWingEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(AshBoneWingEntity::new)

					.sized(1.7f, 0.8f));
	public static final RegistryObject<EntityType<FireflyEntity>> FIREFLY = register("firefly",
			EntityType.Builder.<FireflyEntity>of(FireflyEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(FireflyEntity::new)

					.sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ThundercloudEntity>> THUNDERCLOUD = register("thundercloud",
			EntityType.Builder.<ThundercloudEntity>of(ThundercloudEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ThundercloudEntity::new)

					.sized(1f, 0.8f));
	public static final RegistryObject<EntityType<HighvoltageThundercloudEntity>> HIGHVOLTAGE_THUNDERCLOUD = register("highvoltage_thundercloud",
			EntityType.Builder.<HighvoltageThundercloudEntity>of(HighvoltageThundercloudEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)
					.setCustomClientFactory(HighvoltageThundercloudEntity::new).fireImmune().sized(1.4f, 1.1f));
	public static final RegistryObject<EntityType<JellyfishEntity>> JELLYFISH = register("jellyfish",
			EntityType.Builder.<JellyfishEntity>of(JellyfishEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(JellyfishEntity::new)

					.sized(0.7f, 1.4f));
	public static final RegistryObject<EntityType<WindKnightEntity>> WIND_KNIGHT = register("wind_knight",
			EntityType.Builder.<WindKnightEntity>of(WindKnightEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(WindKnightEntity::new).fireImmune().sized(1f, 2f));
	public static final RegistryObject<EntityType<GoldenFoxEntity>> GOLDEN_FOX = register("golden_fox", EntityType.Builder.<GoldenFoxEntity>of(GoldenFoxEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
			.setUpdateInterval(3).setCustomClientFactory(GoldenFoxEntity::new).fireImmune().sized(0.6f, 0.6f));
	public static final RegistryObject<EntityType<MeltdreamCrystalEntityEntity>> MELTDREAM_CRYSTAL_ENTITY = register("meltdream_crystal_entity",
			EntityType.Builder.<MeltdreamCrystalEntityEntity>of(MeltdreamCrystalEntityEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)
					.setCustomClientFactory(MeltdreamCrystalEntityEntity::new).fireImmune().sized(0.6f, 1f));
	public static final RegistryObject<EntityType<TerraswordWaveEntity>> TERRASWORD_WAVE = register("terrasword_wave", EntityType.Builder.<TerraswordWaveEntity>of(TerraswordWaveEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true)
			.setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(TerraswordWaveEntity::new).fireImmune().sized(0.1f, 0.1f));
	public static final RegistryObject<EntityType<ShadowMagicballEntity>> SHADOW_MAGICBALL = register("shadow_magicball", EntityType.Builder.<ShadowMagicballEntity>of(ShadowMagicballEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(ShadowMagicballEntity::new).fireImmune().sized(1f, 1.5f));
	public static final RegistryObject<EntityType<HealingSpellEntityEntity>> HEALING_SPELL_ENTITY = register("healing_spell_entity", EntityType.Builder.<HealingSpellEntityEntity>of(HealingSpellEntityEntity::new, MobCategory.MONSTER)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(HealingSpellEntityEntity::new).fireImmune().sized(0.1f, 0.1f));
	public static final RegistryObject<EntityType<PinkeggProjectileEntity>> PINKEGG_PROJECTILE = register("projectile_pinkegg_projectile", EntityType.Builder.<PinkeggProjectileEntity>of(PinkeggProjectileEntity::new, MobCategory.MISC)
			.setCustomClientFactory(PinkeggProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<MoltengoldWandProjectileEntity>> MOLTENGOLD_WAND_PROJECTILE = register("projectile_moltengold_wand_projectile",
			EntityType.Builder.<MoltengoldWandProjectileEntity>of(MoltengoldWandProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(MoltengoldWandProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<TrueMoltengoldWandProjectileEntity>> TRUE_MOLTENGOLD_WAND_PROJECTILE = register("projectile_true_moltengold_wand_projectile",
			EntityType.Builder.<TrueMoltengoldWandProjectileEntity>of(TrueMoltengoldWandProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(TrueMoltengoldWandProjectileEntity::new).setShouldReceiveVelocityUpdates(true)
					.setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<TruestMoltengoldWandProjectileEntity>> TRUEST_MOLTENGOLD_WAND_PROJECTILE = register("projectile_truest_moltengold_wand_projectile",
			EntityType.Builder.<TruestMoltengoldWandProjectileEntity>of(TruestMoltengoldWandProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(TruestMoltengoldWandProjectileEntity::new).setShouldReceiveVelocityUpdates(true)
					.setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<SquealWaveProjectileEntity>> SQUEAL_WAVE_PROJECTILE = register("projectile_squeal_wave_projectile", EntityType.Builder.<SquealWaveProjectileEntity>of(SquealWaveProjectileEntity::new, MobCategory.MISC)
			.setCustomClientFactory(SquealWaveProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<WhiteSwordRainProjectileEntity>> WHITE_SWORD_RAIN_PROJECTILE = register("projectile_white_sword_rain_projectile",
			EntityType.Builder.<WhiteSwordRainProjectileEntity>of(WhiteSwordRainProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(WhiteSwordRainProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<SquealWaveWandProjectileEntity>> SQUEAL_WAVE_WAND_PROJECTILE = register("projectile_squeal_wave_wand_projectile",
			EntityType.Builder.<SquealWaveWandProjectileEntity>of(SquealWaveWandProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(SquealWaveWandProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<LightningSpellProjectileEntity>> LIGHTNING_SPELL_PROJECTILE = register("projectile_lightning_spell_projectile",
			EntityType.Builder.<LightningSpellProjectileEntity>of(LightningSpellProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(LightningSpellProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<ShadowVortexBookProjectileEntity>> SHADOW_VORTEX_BOOK_PROJECTILE = register("projectile_shadow_vortex_book_projectile",
			EntityType.Builder.<ShadowVortexBookProjectileEntity>of(ShadowVortexBookProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(ShadowVortexBookProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<PoisonSpellProjectileEntity>> POISON_SPELL_PROJECTILE = register("projectile_poison_spell_projectile",
			EntityType.Builder.<PoisonSpellProjectileEntity>of(PoisonSpellProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(PoisonSpellProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<HealingSpellProjectileEntity>> HEALING_SPELL_PROJECTILE = register("projectile_healing_spell_projectile",
			EntityType.Builder.<HealingSpellProjectileEntity>of(HealingSpellProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(HealingSpellProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<BoneWingFireBallProjectileEntity>> BONE_WING_FIRE_BALL_PROJECTILE = register("projectile_bone_wing_fire_ball_projectile",
			EntityType.Builder.<BoneWingFireBallProjectileEntity>of(BoneWingFireBallProjectileEntity::new, MobCategory.MISC).setCustomClientFactory(BoneWingFireBallProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<LightningProjectileEntity>> LIGHTNING_PROJECTILE = register("projectile_lightning_projectile", EntityType.Builder.<LightningProjectileEntity>of(LightningProjectileEntity::new, MobCategory.MISC)
			.setCustomClientFactory(LightningProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<FurySpellProjectileEntity>> FURY_SPELL_PROJECTILE = register("projectile_fury_spell_projectile", EntityType.Builder.<FurySpellProjectileEntity>of(FurySpellProjectileEntity::new, MobCategory.MISC)
			.setCustomClientFactory(FurySpellProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<FurySpellEntityEntity>> FURY_SPELL_ENTITY = register("fury_spell_entity", EntityType.Builder.<FurySpellEntityEntity>of(FurySpellEntityEntity::new, MobCategory.MISC)
			.setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(FurySpellEntityEntity::new).fireImmune().sized(2f, 0.1f));
	public static final RegistryObject<EntityType<IceSpellProjectileEntity>> ICE_SPELL_PROJECTILE = register("projectile_ice_spell_projectile", EntityType.Builder.<IceSpellProjectileEntity>of(IceSpellProjectileEntity::new, MobCategory.MISC)
			.setCustomClientFactory(IceSpellProjectileEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<StrawberryHeartProjectEntity>> STRAWBERRY_HEART_PROJECT = register("projectile_strawberry_heart_project",
			EntityType.Builder.<StrawberryHeartProjectEntity>of(StrawberryHeartProjectEntity::new, MobCategory.MISC).setCustomClientFactory(StrawberryHeartProjectEntity::new).setShouldReceiveVelocityUpdates(true).setTrackingRange(64)
					.setUpdateInterval(1).sized(0.5f, 0.5f));
	public static final RegistryObject<EntityType<FoxFireEntity>> FOX_FIRE = register("fox_fire",
			EntityType.Builder.<FoxFireEntity>of(FoxFireEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(FoxFireEntity::new).fireImmune().sized(1f, 0.1f));
	public static final RegistryObject<EntityType<SmallStoneSpiritEntity>> SMALL_STONE_SPIRIT = register("small_stone_spirit",
			EntityType.Builder.<SmallStoneSpiritEntity>of(SmallStoneSpiritEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).setCustomClientFactory(SmallStoneSpiritEntity::new)

					.sized(0.7f, 1f));

	private static <T extends Entity> RegistryObject<EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			PinkChickenEntity.init();
			PinkSlimeEntity.init();
			BasaltSnailEntity.init();
			SporeEntityEntity.init();
			ShadowGolemEntity.init();
			TerrorbeakEntity.init();
			ShadowHandEntity.init();
			CrazyTerrorbeakEntity.init();
			ShadowGhostEntity.init();
			ShadowSquealGhostEntity.init();
			ShadowSquealGhost0Entity.init();
			FriendlyGhostEntity.init();
			BlackBeetleEntity.init();
			BlackBeetleMotherEntity.init();
			ShadowNpc0Entity.init();
			WeakenessTerrorbeakEntity.init();
			AaroncosLefthand0Entity.init();
			AaroncosRighthand0Entity.init();
			ShadowTuneTotemEntity.init();
			ShakingCrystalEntity.init();
			BoneWingEntity.init();
			AshBoneWingEntity.init();
			FireflyEntity.init();
			ThundercloudEntity.init();
			HighvoltageThundercloudEntity.init();
			JellyfishEntity.init();
			WindKnightEntity.init();
			GoldenFoxEntity.init();
			MeltdreamCrystalEntityEntity.init();
			TerraswordWaveEntity.init();
			ShadowMagicballEntity.init();
			HealingSpellEntityEntity.init();
			FurySpellEntityEntity.init();
			FoxFireEntity.init();
			SmallStoneSpiritEntity.init();
		});
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(PINK_CHICKEN.get(), PinkChickenEntity.createAttributes().build());
		event.put(PINK_SLIME.get(), PinkSlimeEntity.createAttributes().build());
		event.put(BASALT_SNAIL.get(), BasaltSnailEntity.createAttributes().build());
		event.put(SPORE_ENTITY.get(), SporeEntityEntity.createAttributes().build());
		event.put(SHADOW_GOLEM.get(), ShadowGolemEntity.createAttributes().build());
		event.put(TERRORBEAK.get(), TerrorbeakEntity.createAttributes().build());
		event.put(SHADOW_HAND.get(), ShadowHandEntity.createAttributes().build());
		event.put(CRAZY_TERRORBEAK.get(), CrazyTerrorbeakEntity.createAttributes().build());
		event.put(SHADOW_GHOST.get(), ShadowGhostEntity.createAttributes().build());
		event.put(SHADOW_SQUEAL_GHOST.get(), ShadowSquealGhostEntity.createAttributes().build());
		event.put(SHADOW_SQUEAL_GHOST_0.get(), ShadowSquealGhost0Entity.createAttributes().build());
		event.put(FRIENDLY_GHOST.get(), FriendlyGhostEntity.createAttributes().build());
		event.put(BLACK_BEETLE.get(), BlackBeetleEntity.createAttributes().build());
		event.put(BLACK_BEETLE_MOTHER.get(), BlackBeetleMotherEntity.createAttributes().build());
		event.put(SHADOW_NPC_0.get(), ShadowNpc0Entity.createAttributes().build());
		event.put(WEAKENESS_TERRORBEAK.get(), WeakenessTerrorbeakEntity.createAttributes().build());
		event.put(AARONCOS_LEFTHAND_0.get(), AaroncosLefthand0Entity.createAttributes().build());
		event.put(AARONCOS_RIGHTHAND_0.get(), AaroncosRighthand0Entity.createAttributes().build());
		event.put(SHADOW_TUNE_TOTEM.get(), ShadowTuneTotemEntity.createAttributes().build());
		event.put(SHAKING_CRYSTAL.get(), ShakingCrystalEntity.createAttributes().build());
		event.put(BONE_WING.get(), BoneWingEntity.createAttributes().build());
		event.put(ASH_BONE_WING.get(), AshBoneWingEntity.createAttributes().build());
		event.put(FIREFLY.get(), FireflyEntity.createAttributes().build());
		event.put(THUNDERCLOUD.get(), ThundercloudEntity.createAttributes().build());
		event.put(HIGHVOLTAGE_THUNDERCLOUD.get(), HighvoltageThundercloudEntity.createAttributes().build());
		event.put(JELLYFISH.get(), JellyfishEntity.createAttributes().build());
		event.put(WIND_KNIGHT.get(), WindKnightEntity.createAttributes().build());
		event.put(GOLDEN_FOX.get(), GoldenFoxEntity.createAttributes().build());
		event.put(MELTDREAM_CRYSTAL_ENTITY.get(), MeltdreamCrystalEntityEntity.createAttributes().build());
		event.put(TERRASWORD_WAVE.get(), TerraswordWaveEntity.createAttributes().build());
		event.put(SHADOW_MAGICBALL.get(), ShadowMagicballEntity.createAttributes().build());
		event.put(HEALING_SPELL_ENTITY.get(), HealingSpellEntityEntity.createAttributes().build());
		event.put(FOX_FIRE.get(), FoxFireEntity.createAttributes().build());
		event.put(SMALL_STONE_SPIRIT.get(), SmallStoneSpiritEntity.createAttributes().build());
	}
}
