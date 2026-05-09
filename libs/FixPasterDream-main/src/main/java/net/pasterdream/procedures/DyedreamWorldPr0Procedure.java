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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

public class DyedreamWorldPr0Procedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if ((entity.level().dimension()) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:dyedream_world"))) && (entity instanceof ServerPlayer _plr3 && _plr3.level() instanceof ServerLevel
				&& _plr3.getAdvancements().getOrStartProgress(_plr3.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_b_0"))).isDone()) == false) {
			if (entity instanceof Player _player) {
				ItemStack _setstack = new ItemStack(PasterdreamModItems.DREAMNOTES_2.get());
				_setstack.setCount(1);
				ItemHandlerHelper.giveItemToPlayer(_player, _setstack);
			}
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u4F60\u53D1\u73B0\u4E86\u4E00\u5F20\u5BFB\u68A6\u8005\u7B14\u8BB0\uFF0C\u5E76\u6536\u8FDB\u4E86\u4F60\u7684\u80CC\u5305"), false);
			PasterdreamMod.queueServerWork(120, () -> {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, new BlockPos(0, 120, 0), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:sweetdream")), SoundSource.MUSIC, (float) 0.5, 1);
					} else {
						_level.playLocalSound(0, 120, 0, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:sweetdream")), SoundSource.MUSIC, (float) 0.5, 1, false);
					}
				}
			});
		}
	}
}
