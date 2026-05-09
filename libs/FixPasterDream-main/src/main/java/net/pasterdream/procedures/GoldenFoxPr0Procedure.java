package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

public class GoldenFoxPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (itemstack.getItem() == Items.GOLDEN_APPLE) {
			itemstack.shrink(1);
			if (!entity.level().isClientSide())
				entity.discard();
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.YELLOW_SMOKE_PARTICLE.get()), x, y, z, 32, 0.6, 0.6, 0.6, 0.1);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.CLOUD, x, y, z, 12, 0.6, 0.6, 0.6, 0.1);
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.fox.ambient")), SoundSource.MASTER, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.fox.ambient")), SoundSource.MASTER, 1, 1, false);
				}
			}
			PasterdreamMod.queueServerWork(15, () -> {
				if (world instanceof ServerLevel _level) {
					ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));
					entityToSpawn.setPickUpDelay(10);
					_level.addFreshEntity(entityToSpawn);
				}
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u91D1\u8272\u72D0\u72F8\u5728\u5B8C\u6210\u4E86\u4F60\u8BB8\u4E0B\u7684\u613F\u671B\u4E4B\u540E\u6D88\u5931\u4E86..."), false);
			});
		}
		if (itemstack.getItem() == Items.BUCKET) {
			itemstack.shrink(1);
			if (!entity.level().isClientSide())
				entity.discard();
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.YELLOW_SMOKE_PARTICLE.get()), x, y, z, 32, 0.6, 0.6, 0.6, 0.1);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.CLOUD, x, y, z, 12, 0.6, 0.6, 0.6, 0.1);
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.fox.ambient")), SoundSource.MASTER, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.fox.ambient")), SoundSource.MASTER, 1, 1, false);
				}
			}
			PasterdreamMod.queueServerWork(15, () -> {
				if (world instanceof ServerLevel _level) {
					ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(PasterdreamModItems.MELTDREAM_LIQUID_BUCKET.get()));
					entityToSpawn.setPickUpDelay(10);
					_level.addFreshEntity(entityToSpawn);
				}
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u91D1\u8272\u72D0\u72F8\u5728\u5B8C\u6210\u4E86\u4F60\u8BB8\u4E0B\u7684\u613F\u671B\u4E4B\u540E\u6D88\u5931\u4E86..."), false);
			});
		}
		if (itemstack.getItem() == PasterdreamModItems.DREAM_COIN_1.get()) {
			itemstack.shrink(1);
			if (!entity.level().isClientSide())
				entity.discard();
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.YELLOW_SMOKE_PARTICLE.get()), x, y, z, 32, 0.6, 0.6, 0.6, 0.1);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.CLOUD, x, y, z, 12, 0.6, 0.6, 0.6, 0.1);
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.fox.ambient")), SoundSource.MASTER, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.fox.ambient")), SoundSource.MASTER, 1, 1, false);
				}
			}
			PasterdreamMod.queueServerWork(15, () -> {
				world.setBlock(BlockPos.containing(x, y, z), PasterdreamModBlocks.MELTDREAM_CHEST.get().defaultBlockState(), 3);
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u91D1\u8272\u72D0\u72F8\u5728\u5B8C\u6210\u4E86\u4F60\u8BB8\u4E0B\u7684\u613F\u671B\u4E4B\u540E\u6D88\u5931\u4E86..."), false);
			});
		}
		if (itemstack.getItem() == PasterdreamModItems.MELTDREAM_CRYSTAL_0.get() || itemstack.getItem() == PasterdreamModBlocks.MEMENTO_ITEM_11.get().asItem()) {
			itemstack.shrink(1);
			if (!entity.level().isClientSide())
				entity.discard();
			if (world instanceof ServerLevel _level)
				_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.YELLOW_SMOKE_PARTICLE.get()), x, y, z, 32, 0.6, 0.6, 0.6, 0.1);
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.CLOUD, x, y, z, 12, 0.6, 0.6, 0.6, 0.1);
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.fox.ambient")), SoundSource.MASTER, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.fox.ambient")), SoundSource.MASTER, 1, 1, false);
				}
			}
			PasterdreamMod.queueServerWork(15, () -> {
				if (world instanceof ServerLevel _level) {
					ItemEntity entityToSpawn = new ItemEntity(_level, x, y, z, new ItemStack(PasterdreamModItems.CRADLE_IN_ONES_ARMS.get()));
					entityToSpawn.setPickUpDelay(10);
					_level.addFreshEntity(entityToSpawn);
				}
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u91D1\u8272\u72D0\u72F8\u5728\u5B8C\u6210\u4E86\u4F60\u8BB8\u4E0B\u7684\u613F\u671B\u4E4B\u540E\u6D88\u5931\u4E86..."), false);
			});
		}
	}
}
