package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModMobEffects;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;

import java.util.List;
import java.util.Comparator;

public class FurySpellPr1Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.ENCHANT, x, (y + 3), z, 100, 2.5, 1, 2.5, 0.02);
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.DRAGON_BREATH, x, (y + 0.8), z, 100, 2.5, 0.3, 2.5, 0.01);
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.FURY_SPELL_PARTICLE.get()), x, (y + 2), z, 12, 2.5, 1, 2.5, 0.02);
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.END_ROD, x, (y + 2), z, 6, 2.5, 1, 2.5, 0.02);
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Player> _entfound = world.getEntitiesOfClass(Player.class, new AABB(_center, _center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Player entityiterator : _entfound) {
				if (!entityiterator.level().isClientSide()) {
                    entityiterator.addEffect(new MobEffectInstance(PasterdreamModMobEffects.FURY_SPELL_BUFF.get(), 60, 0));
				}
			}
		}
	}
}
