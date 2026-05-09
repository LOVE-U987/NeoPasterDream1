package net.pasterdream.procedures;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.pasterdream.init.PasterdreamModGameRules;
import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.block.entity.DreamSpawner0BlockEntity;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

public class DreamSpawner0Pr0Procedure {
	public static void execute(LevelAccessor level, double x, double y, double z) {
		BlockPos pos = new BlockPos(Mth.floor(x), Mth.floor(y), Mth.floor(z));
		Level world = (Level) level;
		if (!(world.getBlockEntity(pos) instanceof DreamSpawner0BlockEntity blockEntity)) return;
		if (!world.getLevelData().getGameRules().getBoolean(PasterdreamModGameRules.PASTERDREAMDEBUGMODE)) {
			if (!world.getEntitiesOfClass(Player.class, AABB.ofSize(new Vec3(x, y, z), blockEntity.getPersistentData().getDouble("player_range"), blockEntity.getPersistentData().getDouble("player_range"), blockEntity.getPersistentData().getDouble("player_range")), e -> true).isEmpty()) {
				ItemStack item = blockEntity.getItem(0);
				if (item == ItemStack.EMPTY || world.isClientSide()) return;
				ServerLevel sl = (ServerLevel) world;
				BlockState _bs = world.getBlockState(pos);

				if (!blockEntity.getPersistentData().getBoolean("first")) {
					blockEntity.getPersistentData().putBoolean("first", true);
					world.sendBlockUpdated(pos, _bs, _bs, 3);

					((SpawnEggItem) item.getItem()).getType(item.getOrCreateTag()).spawn((ServerLevel) world, ItemStack.EMPTY, null, pos, MobSpawnType.SPAWN_EGG, true, true);
					sl.sendParticles(ParticleTypes.SMOKE, (x + 0.5), (y + 0.5), (z + 0.5), 10, 0.5, 0.5, 0.5, 0);
					sl.sendParticles(ParticleTypes.FLAME, (x + 0.5), (y + 0.5), (z + 0.5), 10, 0.5, 0.5, 0.5, 0);
				} else {
					if (blockEntity.getPersistentData().getDouble("number") >= 1) {
						if (world.random.nextDouble() < 0.1) {
							blockEntity.getPersistentData().putDouble("number", blockEntity.getPersistentData().getDouble("number") - 1);
							world.sendBlockUpdated(pos, _bs, _bs, 3);

							((SpawnEggItem) item.getItem()).getType(item.getOrCreateTag()).spawn((ServerLevel) world, ItemStack.EMPTY, null, pos, MobSpawnType.SPAWN_EGG, true, true);
							sl.sendParticles(ParticleTypes.SMOKE, (x + 0.5), (y + 0.5), (z + 0.5), 10, 0.5, 0.5, 0.5, 0);
							sl.sendParticles(ParticleTypes.FLAME, (x + 0.5), (y + 0.5), (z + 0.5), 10, 0.5, 0.5, 0.5, 0);
						}
					} else world.setBlock(pos, PasterdreamModBlocks.DREAM_SPAWNER_1.get().defaultBlockState(), 3);
				}
			}
		}
	}
}
