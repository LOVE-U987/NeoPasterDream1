package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.entity.MoltengoldWandProjectileEntity;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.client.Minecraft;

import java.util.List;
import java.util.Comparator;

public class CalleCard6Pr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity)
			_entity.swing(InteractionHand.MAIN_HAND, true);
		if (world.isClientSide())
			Minecraft.getInstance().gameRenderer.displayItemActivation(new ItemStack(PasterdreamModItems.CALLE_CARD_6.get()));
		itemstack.shrink(1);
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(19 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (entityiterator instanceof LivingEntity _livEnt4 && _livEnt4.getMobType() == MobType.UNDEAD && entityiterator instanceof ZombieVillager == false) {
					if (world instanceof ServerLevel projectileLevel) {
						Projectile _entityToSpawn = new Object() {
							public Projectile getArrow(Level level, float damage, int knockback, byte piercing) {
								AbstractArrow entityToSpawn = new MoltengoldWandProjectileEntity(PasterdreamModEntities.MOLTENGOLD_WAND_PROJECTILE.get(), level);
								entityToSpawn.setBaseDamage(damage);
								entityToSpawn.setKnockback(knockback);
								entityToSpawn.setSilent(true);
								entityToSpawn.setPierceLevel(piercing);
								return entityToSpawn;
							}
						}.getArrow(projectileLevel, 5, 0, (byte) 1);
						_entityToSpawn.setPos((entityiterator.getX()), (entityiterator.getY() + 7), (entityiterator.getZ()));
						_entityToSpawn.shoot(0, (-1), 0, 1, 0);
						projectileLevel.addFreshEntity(_entityToSpawn);
					}
					entityiterator.setSecondsOnFire(15);
					PasterdreamMod.queueServerWork(3, () -> {
						entityiterator.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)), 20);
						if (world instanceof ServerLevel _level)
							_level.sendParticles(ParticleTypes.LANDING_LAVA, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 6, 0.5, 0.5, 0.5, 2);
					});
				}
				if (entityiterator instanceof ZombieVillager) {
					PasterdreamMod.queueServerWork(3, () -> {
						if (world instanceof ServerLevel _level)
							_level.sendParticles(ParticleTypes.HEART, (entityiterator.getX()), (entityiterator.getY()), (entityiterator.getZ()), 6, 0.5, 0.5, 0.5, 2);
						if (!entityiterator.level().isClientSide())
							entityiterator.discard();
						if (world instanceof ServerLevel _level) {
							Entity entityToSpawn = EntityType.VILLAGER.spawn(_level, BlockPos.containing(entityiterator.getX(), entityiterator.getY(), entityiterator.getZ()), MobSpawnType.MOB_SUMMONED);
							if (entityToSpawn != null) {
								entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
							}
						}
					});
				}
			}
		}
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.CALLE_PARTICLE.get()), x, y, z, 64, 2, 1.5, 2, 0.1);
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.FIREWORK, x, y, z, 256, 9, 0.5, 9, 0.25);
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.totem.use")), SoundSource.NEUTRAL, 1, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.totem.use")), SoundSource.NEUTRAL, 1, 1, false);
			}
		}
	}
}
