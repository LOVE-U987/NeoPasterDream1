package net.pasterdream.procedures;

import top.theillusivec4.curios.api.CuriosApi;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.entity.FriendlyGhostEntity;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.TagKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Comparator;

public class SquealWaveWandPr3Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("particle.soul_escape")), SoundSource.NEUTRAL, (float) 0.5, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("particle.soul_escape")), SoundSource.NEUTRAL, (float) 0.5, 1, false);
			}
		}
		if (sourceentity instanceof Player) {
			if (((LivingEntity) sourceentity).getAttribute(PasterdreamModAttributes.MAGICPOWER.get()).getBaseValue() >= 1) {
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(3 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (!entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("pasterdream:special_entity_tag")))) {
							entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC)),
									(float) (2 + ((LivingEntity) sourceentity).getAttribute(PasterdreamModAttributes.MAGICPOWER.get()).getBaseValue() + 1.5));
						}
					}
				}
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.SOUL_PARTICLE.get()), x, y, z, 16, 0.7, 0.5, 0.7, 0);
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.SOUL, x, y, z, 16, 0.7, 0.5, 0.7, 0);
			}
			if (sourceentity instanceof LivingEntity lv ? CuriosApi.getCuriosHelper().findEquippedCurio(PasterdreamModItems.GHOST_FACE_HEAD.get(), lv).isPresent() : false) {
				if (Math.random() <= 0.1) {
					PasterdreamMod.queueServerWork(1, () -> {
						if (world instanceof ServerLevel _level) {
							Entity entityToSpawn = PasterdreamModEntities.FRIENDLY_GHOST.get().spawn(_level, BlockPos.containing(x, y + 1, z), MobSpawnType.MOB_SUMMONED);
							if (entityToSpawn != null) {
								entityToSpawn.setYRot(1);
								entityToSpawn.setYBodyRot(1);
								entityToSpawn.setYHeadRot(1);
							}
						}
						{
							final Vec3 _center = new Vec3(x, y, z);
							List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
							for (Entity entityiterator : _entfound) {
								if (entityiterator instanceof FriendlyGhostEntity) {
									if (entityiterator instanceof TamableAnimal _toTame && sourceentity instanceof Player _owner)
										_toTame.tame(_owner);
								}
							}
						}
					});
				}
			}
		}
	}
}
