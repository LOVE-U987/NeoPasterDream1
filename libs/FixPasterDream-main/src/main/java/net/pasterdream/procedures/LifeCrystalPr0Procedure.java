package net.pasterdream.procedures;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import java.util.UUID;

public class LifeCrystalPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		AttributeInstance instance = ((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
		if (instance!=null &&
				instance.hasModifier((new AttributeModifier(UUID.fromString("4619a21e-8140-4f6b-a384-5a3e83b65e3c"), "life_crystal", 2, AttributeModifier.Operation.ADDITION)))) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u4F60\u5DF2\u7ECF\u5438\u6536\u8FC7\u751F\u547D\u6C34\u6676\u4E86\uFF01"), false);
		} else {
			{
				int _value = 1;
				BlockPos _pos = BlockPos.containing(x, y, z);
				BlockState _bs = world.getBlockState(_pos);
				if (_bs.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
					world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
			}
			PasterdreamMod.queueServerWork(2, () -> {
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.HEART, (x + 0.5), (y + 0.5), (z + 0.5), 3, 0.3, 0.3, 0.3, 0.01);
				PasterdreamMod.queueServerWork(5, () -> {
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.HEART, (x + 0.5), (y + 0.5), (z + 0.5), 3, 0.3, 0.3, 0.3, 0.01);
					PasterdreamMod.queueServerWork(5, () -> {
						if (world instanceof ServerLevel _level)
							_level.sendParticles(ParticleTypes.HEART, (x + 0.5), (y + 0.5), (z + 0.5), 3, 0.3, 0.3, 0.3, 0.01);
						PasterdreamMod.queueServerWork(5, () -> {
							if (world instanceof ServerLevel _level)
								_level.sendParticles(ParticleTypes.HEART, (x + 0.5), (y + 0.5), (z + 0.5), 3, 0.3, 0.3, 0.3, 0.01);
							PasterdreamMod.queueServerWork(5, () -> {
								if (world instanceof ServerLevel _level)
									_level.sendParticles(ParticleTypes.HEART, (x + 0.5), (y + 0.5), (z + 0.5), 3, 0.3, 0.3, 0.3, 0.01);
								PasterdreamMod.queueServerWork(5, () -> {
									if (world instanceof ServerLevel _level)
										_level.sendParticles(ParticleTypes.HEART, (x + 0.5), (y + 0.5), (z + 0.5), 3, 0.3, 0.3, 0.3, 0.01);
									PasterdreamMod.queueServerWork(5, () -> {
										if (world instanceof ServerLevel _level)
											_level.sendParticles(ParticleTypes.HEART, (x + 0.5), (y + 0.5), (z + 0.5), 3, 0.3, 0.3, 0.3, 0.01);
										PasterdreamMod.queueServerWork(5, () -> {
											if (world instanceof ServerLevel _level)
												_level.sendParticles(ParticleTypes.HEART, (x + 0.5), (y + 0.5), (z + 0.5), 3, 0.3, 0.3, 0.3, 0.01);
											PasterdreamMod.queueServerWork(3, () -> {
												if (world instanceof ServerLevel _level)
													_level.sendParticles(ParticleTypes.HEART, (x + 0.5), (y + 0.5), (z + 0.5), 4, 0.3, 0.3, 0.3, 0.01);
												if (world instanceof ServerLevel _level)
													_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.MELTDREAM_CRYSTAL_PARTICLE.get()), (x + 0.5), (y + 0.5), (z + 0.5), 8, 0.3, 0.3, 0.3, 0.01);
											});
										});
									});
								});
							});
						});
					});
				});
			});
			PasterdreamMod.queueServerWork(40, () -> {
				if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == PasterdreamModBlocks.LIFE_CRYSTAL.get()) {
					world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
					if (!(((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH)
							.hasModifier((new AttributeModifier(UUID.fromString("4619a21e-8140-4f6b-a384-5a3e83b65e3c"), "life_crystal", 2, AttributeModifier.Operation.ADDITION)))))
						((LivingEntity) entity).getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH)
								.addPermanentModifier((new AttributeModifier(UUID.fromString("4619a21e-8140-4f6b-a384-5a3e83b65e3c"), "life_crystal", 2, AttributeModifier.Operation.ADDITION)));
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("\u751F\u547D\u6C34\u6676\u7834\u788E\u5E76\u6D8C\u5165\u4F60\u7684\u4F53\u5185 \u00A7a\u6700\u5927\u751F\u547D\u503C+2"), false);
				}
			});
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:life_crystal")), SoundSource.NEUTRAL, (float) 0.6, 1);
				} 
			}
		}
	}
}
