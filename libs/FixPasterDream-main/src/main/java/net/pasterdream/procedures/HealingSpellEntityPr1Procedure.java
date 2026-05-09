package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.SimpleParticleType;

import java.util.List;
import java.util.Comparator;

public class HealingSpellEntityPr1Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity.getPersistentData().getDouble("time") <= 0) {
			if (!entity.level().isClientSide())
				entity.discard();
		} else {
			entity.getPersistentData().putDouble("time", (entity.getPersistentData().getDouble("time") - 1));
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.HEALING_SPELL_PARTICLE.get()), x, (y - 0.5), z, 3, 1.7, 0.5, 1.7, 0.05);
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.YELLOW_SMOKE_PARTICLE.get()), x, (y + 0.1), z, 2, 1.7, 0.5, 1.7, 0.05);
			{
				final Vec3 _center = new Vec3(x, y, z);
				List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(5 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
				for (Entity entityiterator : _entfound) {
					if (entityiterator instanceof PathfinderMob || entityiterator instanceof Player) {
						if (entityiterator instanceof LivingEntity _entity)
							_entity.setHealth((float) ((entityiterator instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + (entityiterator instanceof LivingEntity _livEnt ? _livEnt.getMaxHealth() : -1) / 400));
					}
				}
			}
		}
	}
}
