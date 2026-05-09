package net.pasterdream.procedures;

import net.pasterdream.capability.MeltDreamEnergyCapability;
import top.theillusivec4.curios.api.CuriosApi;

import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.TagKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Comparator;

public class IceshadowHammerPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Player entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (MeltDreamEnergyCapability.consumePlayerMeltDreamEnergy(entity,0.1)) {
            entity.getCooldowns().addCooldown(itemstack.getItem(), 80);
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = PasterdreamModEntities.SHAKING_CRYSTAL.get().spawn(_level, BlockPos.containing(x, y + 1, z), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
			entity.setDeltaMovement(new Vec3(0, 0.4, 0));
			{
				final Vec3 _center = new Vec3(x, y, z);
				List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
				for (Entity entityiterator : _entfound) {
					if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag"))) && !(entityiterator instanceof Player)) {
						entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK)),
								(float) (((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue() * 0.5
										* ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLMULTIPLIER.get()).getValue()));
					}
				}
			}
			PasterdreamMod.queueServerWork(10, () -> {
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag")))) {
							entityiterator.setDeltaMovement(new Vec3(0, 1, 0));
						}
					}
				}
			});
			PasterdreamMod.queueServerWork(11, () -> {
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag"))) && !(entityiterator instanceof Player)) {
							entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK)),
									(float) (((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue() * 0.7
											* ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLMULTIPLIER.get()).getValue()));
						}
					}
				}
			});
			PasterdreamMod.queueServerWork(22, () -> {
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag"))) && !(entityiterator instanceof Player)) {
							entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK)),
									(float) (((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue() * 0.4
											* ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLMULTIPLIER.get()).getValue()));
						}
					}
				}
			});
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.explode")), SoundSource.NEUTRAL, (float) 0.7, (float) 0.5);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.generic.explode")), SoundSource.NEUTRAL, (float) 0.7, (float) 0.5, false);
				}
			}
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:stone_break")), SoundSource.NEUTRAL, (float) 0.7, (float) 0.8);
				} 
			}

			if (CuriosApi.getCuriosInventory(entity).map(handler ->
                            handler.findFirstCurio(PasterdreamModItems.ICESHADOW_CURIO.get()).isPresent())
                    .orElse(false)) {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = PasterdreamModEntities.SHAKING_CRYSTAL.get().spawn(_level, BlockPos.containing(x + Math.abs(x) - Math.abs(entity.getX()), y + 1, z + Math.abs(z) - Math.abs(entity.getZ())), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = PasterdreamModEntities.SHAKING_CRYSTAL.get().spawn(_level, BlockPos.containing(x - (Math.abs(x) - Math.abs(entity.getX())), y + 1, z - (Math.abs(z) - Math.abs(entity.getZ()))), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
					}
				}
				{
					final Vec3 _center = new Vec3((x + Math.abs(x) - Math.abs(entity.getX())), y, (z + Math.abs(z) - Math.abs(entity.getZ())));
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag"))) && !(entityiterator instanceof Player)) {
							entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK)),
									(float) (((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue() * 0.5
											* ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLMULTIPLIER.get()).getValue()));
						}
					}
				}
				{
					final Vec3 _center = new Vec3((x - (Math.abs(x) - Math.abs(entity.getX()))), y, (z - (Math.abs(z) - Math.abs(entity.getZ()))));
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag"))) && !(entityiterator instanceof Player)) {
							entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK)),
									(float) (((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue() * 0.5
											* ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLMULTIPLIER.get()).getValue()));
						}
					}
				}
				PasterdreamMod.queueServerWork(10, () -> {
					{
						final Vec3 _center = new Vec3((x + Math.abs(x) - Math.abs(entity.getX())), y, (z + Math.abs(z) - Math.abs(entity.getZ())));
						List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
						for (Entity entityiterator : _entfound) {
							if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag")))) {
								entityiterator.setDeltaMovement(new Vec3(0, 1, 0));
							}
						}
					}
					{
						final Vec3 _center = new Vec3((x - (Math.abs(x) - Math.abs(entity.getX()))), y, (z - (Math.abs(z) - Math.abs(entity.getZ()))));
						List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
						for (Entity entityiterator : _entfound) {
							if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag")))) {
								entityiterator.setDeltaMovement(new Vec3(0, 1, 0));
							}
						}
					}
				});
				PasterdreamMod.queueServerWork(11, () -> {
					{
						final Vec3 _center = new Vec3((x + Math.abs(x) - Math.abs(entity.getX())), y, (z + Math.abs(z) - Math.abs(entity.getZ())));
						List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
						for (Entity entityiterator : _entfound) {
							if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag"))) && !(entityiterator instanceof Player)) {
								entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK)),
										(float) (((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue() * 0.5
												* ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLMULTIPLIER.get()).getValue()));
							}
						}
					}
					{
						final Vec3 _center = new Vec3((x - (Math.abs(x) - Math.abs(entity.getX()))), y, (z - (Math.abs(z) - Math.abs(entity.getZ()))));
						List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
						for (Entity entityiterator : _entfound) {
							if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag"))) && !(entityiterator instanceof Player)) {
								entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK)),
										(float) (((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue() * 0.5
												* ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLMULTIPLIER.get()).getValue()));
							}
						}
					}
				});
				PasterdreamMod.queueServerWork(22, () -> {
					{
						final Vec3 _center = new Vec3((x + Math.abs(x) - Math.abs(entity.getX())), y, (z + Math.abs(z) - Math.abs(entity.getZ())));
						List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
						for (Entity entityiterator : _entfound) {
							if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag"))) && !(entityiterator instanceof Player)) {
								entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK)),
										(float) (((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue() * 0.5
												* ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLMULTIPLIER.get()).getValue()));
							}
						}
					}
					{
						final Vec3 _center = new Vec3((x - (Math.abs(x) - Math.abs(entity.getX()))), y, (z - (Math.abs(z) - Math.abs(entity.getZ()))));
						List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
						for (Entity entityiterator : _entfound) {
							if (!entityiterator.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag"))) && !(entityiterator instanceof Player)) {
								entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.PLAYER_ATTACK)),
										(float) (((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE).getValue() * 0.5
												* ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLMULTIPLIER.get()).getValue()));
							}
						}
					}
				});
			}
		} else {
			if (entity.level().isClientSide())
                entity.displayClientMessage(Component.literal("\u878D\u68A6\u80FD\u91CF\u4E0D\u8DB3"), true);
		}
	}
}
