package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.entity.BlackBeetleEntity;
import net.pasterdream.PasterdreamMod;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;

public class BlackBeetlePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity, ItemStack itemstack) {
		if (entity == null || sourceentity == null)
			return;
		if (entity instanceof BlackBeetleEntity) {
			((BlackBeetleEntity) entity).setAnimation("skill");
		}
		if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PasterdreamModBlocks.FLOWER_15.get().asItem()
				|| (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == PasterdreamModBlocks.FLOWER_15.get().asItem()) {
			itemstack.shrink(1);
			if ((entity instanceof TamableAnimal _tamEnt ? _tamEnt.isTame() : false) == false) {
				if (Math.random() < 0.5) {
					if (entity instanceof TamableAnimal _toTame && sourceentity instanceof Player _owner)
						_toTame.tame(_owner);
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.HEART, x, (y + 0.4), z, 2, 0.4, 0.2, 0.4, 0.08);
				} else {
					if (world instanceof ServerLevel _level)
						_level.sendParticles(ParticleTypes.SMOKE, x, (y + 0.4), z, 5, 0.3, 0.2, 0.3, 0.05);
				}
			} else {
				if (entity instanceof LivingEntity _entity)
					_entity.setHealth((float) ((entity instanceof LivingEntity _livEnt ? _livEnt.getHealth() : -1) + 4));
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.HEART, x, (y + 0.4), z, 1, 0.4, 0.2, 0.4, 0.08);
			}
		} else {
			if ((entity instanceof TamableAnimal _tamEnt ? _tamEnt.isTame() : false) == true && (entity.getDisplayName().getString()).equals("\u5C0F\u4EAE")) {
				if (sourceentity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u6765,\u5C0F\u4EAE\u7ED9\u5927\u5BB6\u6574\u4E2A\u6D3B\uFF01"), false);
				if (entity instanceof BlackBeetleEntity) {
					((BlackBeetleEntity) entity).setAnimation("cao_zou_hulue");
				}
				PasterdreamMod.queueServerWork(25, () -> {
					if (sourceentity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("\u8349,\u8D70,\u5FFD\u7565!\u133F \u1288 \u127C \u12FD \u133F"), false);
				});
			}
		}
	}
}
