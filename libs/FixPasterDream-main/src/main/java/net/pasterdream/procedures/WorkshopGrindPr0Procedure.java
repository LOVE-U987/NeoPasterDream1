package net.pasterdream.procedures;

import net.pasterdream.network.PasterdreamModVariables;
import net.pasterdream.init.PasterdreamModParticleTypes;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.ItemTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

public class WorkshopGrindPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).is(ItemTags.create(new ResourceLocation("pasterdream:embryo_items")))
				&& (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getDouble("process") == 3) {
			if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getDouble("level") >= 9) {
				PasterdreamModVariables.weapon_workshop_item = (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY);
				WorkshopGrindInlay0Procedure.execute(world, x, y, z);
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u6253\u78E8\u8FDB\u5EA6 100%"), true);
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.levelup")), SoundSource.NEUTRAL, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("entity.player.levelup")), SoundSource.NEUTRAL, 1, 1, false);
					}
				}
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), (x + 0.5), (y + 1), (z + 0.5), 20, 0.3, 0.5, 0.3, 0.05);
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.MELTDREAM_CRYSTAL_PARTICLE.get()), (x + 0.5), (y + 1), (z + 0.5), 5, 0.3, 0.5, 0.3, 0.05);
				WorkshopGrindRecipe0Procedure.execute(entity);
			} else {
				if ((entity instanceof Player _plr ? _plr.experienceLevel : 0) >= 10 - (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getDouble("level")) {
					if (entity instanceof Player _player)
						_player.giveExperienceLevels(-(1));
					(entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().putDouble("level",
							((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getDouble("level") + 1));
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(
								Component.literal(
										("\u6253\u78E8\u8FDB\u5EA6 " + ((new java.text.DecimalFormat("#").format((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getOrCreateTag().getDouble("level"))) + "0%"))),
								true);
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.grindstone.use")), SoundSource.NEUTRAL, 1, 1);
						} else {
							_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.grindstone.use")), SoundSource.NEUTRAL, 1, 1, false);
						}
					}
					if (world instanceof ServerLevel _level)
						_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.DUST_0_PARTICLE.get()), (x + 0.5), (y + 1), (z + 0.5), 16, 0.3, 0.5, 0.3, 0.05);
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.ASH, (x + 0.5), (y + 1), (z + 0.5), 6, 0.3, 0.5, 0.3, 0.05);
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.SMOKE, (x + 0.5), (y + 1), (z + 0.5), 3, 0.3, 0.5, 0.3, 0.05);
				} else {
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("\u7ECF\u9A8C\u503C\u4E0D\u8DB3"), true);
				}
			}
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u7528\u4E8E\u6253\u78E8\u539F\u80DA \u8FC7\u7A0B\u603B\u8BA1\u9700\u8981\u6D88\u801710\u7ECF\u9A8C\u7B49\u7EA7"), true);
		}
	}
}
