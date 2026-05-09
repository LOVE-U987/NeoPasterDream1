package net.pasterdream.procedures;

import net.pasterdream.configuration.PasterdreamConfigCommonConfiguration;
import net.pasterdream.PasterdreamMod;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

public class Fire0NecklacePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		PasterdreamMod.queueServerWork(0, () -> {
			if (PasterdreamConfigCommonConfiguration.BAN_FIRE_NECKLACE.get() == true) {
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u00A74\u6B64\u7269\u54C1\u5DF2\u88AB\u7981\u7528"), true);
			} else {
				if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.AIR) {
					world.setBlock(BlockPos.containing(x, y, z), Blocks.FIRE.defaultBlockState(), 3);
				}
				if (entity.isOnFire()) {
					if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
						_entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2, 0, false, false));
				}
			}
		});
	}
}
