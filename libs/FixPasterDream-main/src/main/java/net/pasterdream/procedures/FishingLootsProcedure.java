package net.pasterdream.procedures;

import net.pasterdream.network.PasterdreamModVariables;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.player.ItemFishedEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class FishingLootsProcedure {
	@SubscribeEvent
	public static void onPlayerFishItem(ItemFishedEvent event) {
		execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof ServerPlayer _plr0 && _plr0.level() instanceof ServerLevel
				&& _plr0.getAdvancements().getOrStartProgress(_plr0.server.getAdvancements().getAdvancement(new ResourceLocation("minecraft:husbandry/fishy_business"))).isDone()) {
			entity.getPersistentData().putDouble("deep_treasure_luck", 1);
			if (world.canSeeSkyFromBelowWater(BlockPos.containing(x, y, z))) {
				entity.getPersistentData().putDouble("deep_treasure_luck", (entity.getPersistentData().getDouble("deep_treasure_luck") + 0.1));
			}
			if (world.getLevelData().isRaining()) {
				entity.getPersistentData().putDouble("deep_treasure_luck", (entity.getPersistentData().getDouble("deep_treasure_luck") + 0.1));
			}
			if (world.getLevelData().isThundering()) {
				entity.getPersistentData().putDouble("deep_treasure_luck", (entity.getPersistentData().getDouble("deep_treasure_luck") + 0.2));
			}
			if (!(world instanceof Level _lvl11 && _lvl11.isDay())) {
				entity.getPersistentData().putDouble("deep_treasure_luck", (entity.getPersistentData().getDouble("deep_treasure_luck") + 0.1));
			}
			entity.getPersistentData().putDouble("deep_treasure_luck", (entity.getPersistentData().getDouble("deep_treasure_luck") + 0.5 * world.dimensionType().moonPhase(world.dayTime())));
			if (entity instanceof LivingEntity _livEnt17 && _livEnt17.hasEffect(MobEffects.LUCK)) {
				entity.getPersistentData().putDouble("deep_treasure_luck", (entity.getPersistentData().getDouble("deep_treasure_luck") + 0.2));
			}
			if (entity instanceof LivingEntity _livEnt20 && _livEnt20.hasEffect(MobEffects.UNLUCK)) {
				entity.getPersistentData().putDouble("deep_treasure_luck", (entity.getPersistentData().getDouble("deep_treasure_luck") - 0.9));
			}
			if (entity instanceof Player) {
				entity.getPersistentData().putDouble("deep_treasure_luck", (entity.getPersistentData().getDouble("deep_treasure_luck") + ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.LUCK.get()).getBaseValue() * 0.02));
			}
			if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FISHING_LUCK, (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY)) != 0) {
				entity.getPersistentData().putDouble("deep_treasure_luck",
						(entity.getPersistentData().getDouble("deep_treasure_luck") + (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getEnchantmentLevel(Enchantments.FISHING_LUCK) * 0.1));
			}
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PasterdreamModItems.STAR_WISH_ROD.get()
					|| (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == PasterdreamModItems.STAR_WISH_ROD.get()) {
				entity.getPersistentData().putDouble("deep_treasure_luck", (entity.getPersistentData().getDouble("deep_treasure_luck") * 2.2));
				entity.getPersistentData().putBoolean("star_wish_rod", true);
			}
		}
		PasterdreamMod.queueServerWork(1, () -> {
			if ((world instanceof Level _lvl ? _lvl.dimension() : Level.OVERWORLD) == Level.OVERWORLD && world.getBiome(BlockPos.containing(x, y, z)).is(new ResourceLocation("ocean"))) {
				if (Math.random() < 0.025 * entity.getPersistentData().getDouble("deep_treasure_luck")) {
					PasterdreamModVariables.deep_treasure = new ItemStack(PasterdreamModItems.DEEP_TREASURE_0.get());
					PasterdreamModVariables.deep_treasure.getOrCreateTag().putDouble("deep_treasure_luck", (entity.getPersistentData().getDouble("deep_treasure_luck")));
					if (entity.getPersistentData().getBoolean("star_wish_rod") == true) {
						if (Math.random() < 0.1) {
							PasterdreamModVariables.deep_treasure.getOrCreateTag().putBoolean("deep_treasure_super", true);
							if (world instanceof ServerLevel _level) {
								ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, PasterdreamModVariables.deep_treasure);
								entityToSpawn.setPickUpDelay(5);
								_level.addFreshEntity(entityToSpawn);
							}
						} else {
							if (world instanceof ServerLevel _level) {
								ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, PasterdreamModVariables.deep_treasure);
								entityToSpawn.setPickUpDelay(5);
								_level.addFreshEntity(entityToSpawn);
							}
						}
					} else {
						if (world instanceof ServerLevel _level) {
							ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, PasterdreamModVariables.deep_treasure);
							entityToSpawn.setPickUpDelay(5);
							_level.addFreshEntity(entityToSpawn);
						}
					}
				}
			}
			if ((world instanceof Level _lvl ? _lvl.dimension() : Level.OVERWORLD) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:dyedream_world")))
					&& world.getBiome(BlockPos.containing(x, y, z)).is(new ResourceLocation("pasterdream:biome_dyedream_3"))) {
				if (Math.random() < 0.029 * entity.getPersistentData().getDouble("deep_treasure_luck")) {
					PasterdreamModVariables.deep_treasure = new ItemStack(PasterdreamModItems.DEEP_TREASURE_1.get());
					PasterdreamModVariables.deep_treasure.getOrCreateTag().putDouble("deep_treasure_luck", (entity.getPersistentData().getDouble("deep_treasure_luck")));
					if (entity.getPersistentData().getBoolean("star_wish_rod") == true) {
						if (Math.random() < 0.1) {
							PasterdreamModVariables.deep_treasure.getOrCreateTag().putBoolean("deep_treasure_super", true);
							if (world instanceof ServerLevel _level) {
								ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, PasterdreamModVariables.deep_treasure);
								entityToSpawn.setPickUpDelay(5);
								_level.addFreshEntity(entityToSpawn);
							}
						} else {
							if (world instanceof ServerLevel _level) {
								ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, PasterdreamModVariables.deep_treasure);
								entityToSpawn.setPickUpDelay(5);
								_level.addFreshEntity(entityToSpawn);
							}
						}
					} else {
						if (world instanceof ServerLevel _level) {
							ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, PasterdreamModVariables.deep_treasure);
							entityToSpawn.setPickUpDelay(5);
							_level.addFreshEntity(entityToSpawn);
						}
					}
				}
			}
			PasterdreamMod.queueServerWork(1, () -> {
				entity.getPersistentData().putBoolean("star_wish_rod", false);
				entity.getPersistentData().putDouble("deep_treasure_luck", 0);
			});
		});
	}
}
