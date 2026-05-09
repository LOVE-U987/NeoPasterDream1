package net.pasterdream.procedures;

import net.pasterdream.capability.SanCapability;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

public class ShadowBedPr0Procedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if ((world instanceof Level _lvl0 && _lvl0.isDay()) == false || world.getLevelData().isThundering() == true
				|| ((ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:lamp_shadow_world"))) == (world instanceof Level _lvl ? _lvl.dimension() : Level.OVERWORLD)) == false) {
			if (entity instanceof ServerPlayer _plr5 && _plr5.level() instanceof ServerLevel
					&& _plr5.getAdvancements().getOrStartProgress(_plr5.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_start"))).isDone()) {
                SanCapability.addPlayerSanWithCheck(_plr5, -10);
                if (!(entity instanceof ServerPlayer _plr10 && _plr10.level() instanceof ServerLevel
						&& _plr10.getAdvancements().getOrStartProgress(_plr10.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_a_1"))).isDone())) {
					if (entity instanceof ServerPlayer _player) {
						Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_a_1"));
						AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
						if (!_ap.isDone()) {
							for (String criteria : _ap.getRemainingCriteria())
								_player.getAdvancements().award(_adv, criteria);
						}
					}
				}
				WorldSpawnPr1Procedure.execute(world, entity);
			} else {
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u7F3A\u5C11\u706F\u5F71\u4E4B\u4E0B\u7684\u8FDB\u5EA6"), true);
			}
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u4F60\u53EA\u80FD\u5728\u591C\u665A\u6216\u96F7\u66B4\u4E2D\u5165\u7720"), true);
		}
		if (entity instanceof LivingEntity _entity)
			_entity.swing(InteractionHand.MAIN_HAND, true);
	}
}
