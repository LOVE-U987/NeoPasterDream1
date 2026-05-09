package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.init.PasterdreamModBlocks;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.BlockPos;

public class GoldenFoxSculpturePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((world.getBlockState(BlockPos.containing(x + 9, y, z + 9))).getBlock() == PasterdreamModBlocks.FLOWER_12.get() && (world.getBlockState(BlockPos.containing(x - 9, y, z - 9))).getBlock() == PasterdreamModBlocks.FLOWER_12.get()
				&& (world.getBlockState(BlockPos.containing(x - 9, y, z + 9))).getBlock() == PasterdreamModBlocks.FLOWER_12.get() && (world.getBlockState(BlockPos.containing(x + 9, y, z - 9))).getBlock() == PasterdreamModBlocks.FLOWER_12.get()
				&& (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.GLOW_BERRIES && world.dayTime() % 24000 <= 450L && world.dayTime() % 24000 >= 0L) {
			(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).shrink(1);
			world.destroyBlock(BlockPos.containing(x + 9, y, z + 9), false);
			world.destroyBlock(BlockPos.containing(x - 9, y, z - 9), false);
			world.destroyBlock(BlockPos.containing(x + 9, y, z - 9), false);
			world.destroyBlock(BlockPos.containing(x - 9, y, z + 9), false);
			world.destroyBlock(BlockPos.containing(x, y, z), false);
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = PasterdreamModEntities.GOLDEN_FOX.get().spawn(_level, BlockPos.containing(x + 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.fox.ambient")), SoundSource.MASTER, (float) 1.2, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.fox.ambient")), SoundSource.MASTER, (float) 1.2, 1, false);
				}
			}
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:ding_0")), SoundSource.PLAYERS, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:ding_0")), SoundSource.PLAYERS, 1, 1, false);
				}
			}
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.HEALING_SPELL_PARTICLE.get()), (x + 0.5), (y + 0.2), (z + 0.5), 12, 0.5, 0.4, 0.5, 0.1);
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.YELLOW_SMOKE_PARTICLE.get()), (x + 0.5), (y + 0.2), (z + 0.5), 12, 0.5, 0.4, 0.5, 0.1);
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u96D5\u50CF\u6CA1\u6709\u53CD\u5E94..."), false);
		}
	}
}
