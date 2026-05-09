package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.items.ItemHandlerHelper;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Advancement;

public class ShadowSelectEndPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player _player)
			_player.closeContainer();
		if (entity instanceof ServerPlayer _player) {
			Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_d_0"));
			AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
			if (!_ap.isDone()) {
				for (String criteria : _ap.getRemainingCriteria())
					_player.getAdvancements().award(_adv, criteria);
			}
		}
		if (entity instanceof ServerPlayer _player) {
			Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_talent_shadow"));
			AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
			if (!_ap.isDone()) {
				for (String criteria : _ap.getRemainingCriteria())
					_player.getAdvancements().award(_adv, criteria);
			}
		}
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:shadow_door")), SoundSource.NEUTRAL, 1, 1);
			} else {
				_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:shadow_door")), SoundSource.NEUTRAL, 1, 1, false);
			}
		}
		if (entity instanceof Player _player) {
			ItemStack _setstack = new ItemStack(PasterdreamModItems.SHADOW_HILT.get());
			_setstack.setCount(1);
			ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
		}
		PasterdreamMod.queueServerWork(80, () -> {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u770B\u6765\u4F60\u5DF2\u7ECF\u505A\u51FA\u4E86\u9009\u62E9"), false);
		});
		PasterdreamMod.queueServerWork(140, () -> {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u5982\u679C\u4F60\u60F3\u6218\u80DC\u7942 \u963B\u6B62\u7942 \u63A5\u7EB3\u7942 \u8FD8\u662F\u878D\u5165\u7942"), false);
		});
		PasterdreamMod.queueServerWork(200, () -> {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u65E0\u540D\uFF1A\u5C31\u8BF7\u5728\u8FD9\u7247\u706F\u5F71\u4E2D\u5BFB\u627E\u5427 \u5BFB\u627E\u4E9A\u4F26\u67EF\u65AF\u53CC\u624B\u7684\u773C\u775B"), false);
		});
		PasterdreamMod.queueServerWork(260, () -> {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(
						Component.literal("\u00A7a\u5728\u6B64\u7EF4\u5EA6\u4E2D\u5BFB\u627E\u56DB\u6839\u624B\u6307\u7EC4\u6210\u7684\u7C7B\u4F3C\u624B\u7684\u9057\u8FF9\uFF0C\u5E76\u8FDB\u5165\u4E2D\u95F4\u7684\u4F20\u9001\u95E8"), false);
		});
	}
}
