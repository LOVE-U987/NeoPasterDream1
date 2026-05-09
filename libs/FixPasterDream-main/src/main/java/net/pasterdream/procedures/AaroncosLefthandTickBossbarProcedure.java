package net.pasterdream.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Comparator;

public class AaroncosLefthandTickBossbarProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		{
			final Vec3 _center = new Vec3(x, y, z);
			List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(64 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
			for (Entity entityiterator : _entfound) {
				if (entityiterator instanceof Player) {
					entityiterator.getPersistentData().putString("aaroncos_lefthand_0_name", (entity.getDisplayName().getString()));
					entityiterator.getPersistentData().putDouble("aaroncos_lefthand_0_hp", (entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1));
					if (entity.isAlive()) {
						entityiterator.getPersistentData().putBoolean("aaroncos_lefthand_0_life", true);
					} else {
						entityiterator.getPersistentData().putBoolean("aaroncos_lefthand_0_life", false);
					}
				}
			}
		}
	}
}
