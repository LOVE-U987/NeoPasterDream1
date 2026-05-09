package net.pasterdream.procedures;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.pasterdream.entity.LightningProjectileEntity;
import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.init.PasterdreamModSounds;

import java.util.Comparator;

public class ThundercloudPr1Procedure {
	public static void execute(LevelAccessor levelAccessor, double x, double y, double z) {
		if (!(levelAccessor instanceof ServerLevel world)) return;
		if (Math.random() > 0.5) return;
		if (world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, (y - 10), z), 24, 24, 24), e -> true).isEmpty())
			return;

		var player = world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, (y - 5), z), 24, 24, 24), e -> true).stream().min(Comparator.comparingDouble(it -> it.distanceToSqr(x, (y - 5), z))).orElse(null);
		if (player == null) return;

		for (int index = 0; index < 6; index++) {
			AbstractArrow entityToSpawn;
			entityToSpawn = new LightningProjectileEntity(PasterdreamModEntities.LIGHTNING_PROJECTILE.get(), world);
			entityToSpawn.setBaseDamage(7);
			entityToSpawn.setKnockback(0);
			entityToSpawn.setSilent(true);
			entityToSpawn.setPierceLevel((byte) 1);

			entityToSpawn.setPos((0.1 * Mth.nextDouble(RandomSource.create(), -6, 6) + player.getX()), player.getY() + 5, (0.1 * Mth.nextDouble(RandomSource.create(), -6, 6) + player.getZ()));
			entityToSpawn.shoot(0, (-1), 0, 1, 0);
			world.addFreshEntity(entityToSpawn);
		}

		world.playSound(null, player.getOnPos(), PasterdreamModSounds.THUNDERCLOUD_ATTACK.get(), SoundSource.MASTER, (float) 0.6, 1);
		world.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, player.getX(), player.getY(), player.getZ(), 16, 0.4, 0.2, 0.4, 0.004);
	}
}
