package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.configuration.PasterdreamConfigCommonConfiguration;
import net.pasterdream.PasterdreamMod;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.SimpleParticleType;

public class AngelWingPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (PasterdreamConfigCommonConfiguration.BAN_ALL_THE_WINGS.get() == true) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u00A74\u6B64\u7269\u54C1\u5DF2\u88AB\u7981\u7528"), true);
		} else {
			if (Math.random() >= 0.4) {
				if (world instanceof ServerLevel _level)
					_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.FEATHER_WHITE_PARTICLE.get()), x, (y + 1), z, 1, 0.6, 0.3, 0.6, 0.05);
			}
			if ((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem() == PasterdreamModItems.ANGEL_WING_CHESTPLATE.get()) {
				PasterdreamMod.queueServerWork(2, () -> {
					if (!((entity instanceof LivingEntity _entGetArmor ? _entGetArmor.getItemBySlot(EquipmentSlot.CHEST) : ItemStack.EMPTY).getItem() == PasterdreamModItems.ANGEL_WING_CHESTPLATE.get())) {
						PasterdreamMod.queueServerWork(2, () -> {
							if (entity instanceof Player _player) {
								_player.getAbilities().mayfly = false;
								_player.onUpdateAbilities();
							}
							if (entity instanceof Player _player) {
								_player.getAbilities().flying = false;
								_player.onUpdateAbilities();
							}
						});
					}
				});
				if (entity instanceof Player _player) {
					_player.getAbilities().mayfly = true;
					_player.onUpdateAbilities();
				}
				entity.fallDistance = 0;
			}
		}
	}
}
