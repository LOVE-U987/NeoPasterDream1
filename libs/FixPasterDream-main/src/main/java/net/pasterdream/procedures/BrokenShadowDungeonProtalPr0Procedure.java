package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

public class BrokenShadowDungeonProtalPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (y <= 20) {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u5730\u7262\u7684\u7ED3\u6784\u88AB\u7834\u574F\uFF0C\u4F20\u9001\u95E8\u6CA1\u6709\u53CD\u5E94"), false);
		} else {
			if (entity instanceof Player _plr ? _plr.getAbilities().instabuild : false) {
				{
					int _value = 1;
					BlockPos _pos = BlockPos.containing(x, y, z);
					BlockState _bs = world.getBlockState(_pos);
					if (_bs.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
						world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
				}
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.smithing_table.use")), SoundSource.NEUTRAL, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.smithing_table.use")), SoundSource.NEUTRAL, 1, 1, false);
					}
				}
				if (world instanceof ServerLevel _level)
					_level.sendParticles(ParticleTypes.END_ROD, (x + 0.5), (y + 0.5), (z + 0.5), 24, 1, 1, 1, 0.3);
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u521B\u9020\u6A21\u5F0F\uFF1A\u6838\u5FC3\u65E0\u6761\u4EF6\u4FEE\u590D"), false);
				PasterdreamMod.queueServerWork(20, () -> {
					world.setBlock(BlockPos.containing(x, y, z), PasterdreamModBlocks.SHADOW_DUNGEON_PORTAL.get().defaultBlockState(), 3);
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("\u6838\u5FC3\u5DF2\u4FEE\u590D"), true);
				});
			} else {
				if (entity instanceof ServerPlayer _plr9 && _plr9.level() instanceof ServerLevel
						&& _plr9.getAdvancements().getOrStartProgress(_plr9.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_14"))).isDone()) {
					if ((entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PasterdreamModBlocks.SHADOW_LIGHT_0.get().asItem()
							&& (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == PasterdreamModItems.BLACKMETAL_INGOT.get()
							|| (entity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PasterdreamModItems.BLACKMETAL_INGOT.get()
									&& (entity instanceof LivingEntity _livEnt ? _livEnt.getOffhandItem() : ItemStack.EMPTY).getItem() == PasterdreamModBlocks.SHADOW_LIGHT_0.get().asItem()) {
						{
							int _value = 1;
							BlockPos _pos = BlockPos.containing(x, y, z);
							BlockState _bs = world.getBlockState(_pos);
							if (_bs.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty _integerProp && _integerProp.getPossibleValues().contains(_value))
								world.setBlock(_pos, _bs.setValue(_integerProp, _value), 3);
						}
						if (world instanceof Level _level) {
							if (!_level.isClientSide()) {
								_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.smithing_table.use")), SoundSource.NEUTRAL, 1, 1);
							} else {
								_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.smithing_table.use")), SoundSource.NEUTRAL, 1, 1, false);
							}
						}
						if (world instanceof ServerLevel _level)
							_level.sendParticles(ParticleTypes.END_ROD, (x + 0.5), (y + 0.5), (z + 0.5), 24, 1, 1, 1, 0.3);
						if (entity instanceof Player _player) {
							ItemStack _stktoremove = new ItemStack(PasterdreamModItems.BLACKMETAL_INGOT.get());
							_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
						}
						if (entity instanceof Player _player) {
							ItemStack _stktoremove = new ItemStack(PasterdreamModBlocks.SHADOW_LIGHT_0.get());
							_player.getInventory().clearOrCountMatchingItems(p -> _stktoremove.getItem() == p.getItem(), 1, _player.inventoryMenu.getCraftSlots());
						}
						PasterdreamMod.queueServerWork(20, () -> {
							world.setBlock(BlockPos.containing(x, y, z), PasterdreamModBlocks.SHADOW_DUNGEON_PORTAL.get().defaultBlockState(), 3);
							if (entity instanceof Player _player && !_player.level().isClientSide())
								_player.displayClientMessage(Component.literal("\u6838\u5FC3\u5DF2\u4FEE\u590D"), true);
						});
					} else {
						if (entity instanceof Player _player && !_player.level().isClientSide())
							_player.displayClientMessage(Component.literal("\u53CC\u624B\u6301\u00A7e\u9ED1\u91D1\u5C5E\u00A7f\u548C\u00A7e\u5F71\u706F\u00A7f\u4EE5\u4FEE\u590D\u6838\u5FC3"), true);
					}
				} else {
					if (entity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("\u7F3A\u5C11\u77E5\u8BC6 \u4F60\u8FD8\u4E0D\u7406\u89E3\u8FD9\u4E2A\u6838\u5FC3\u7684\u5DE5\u4F5C\u539F\u7406\u548C\u4FEE\u590D\u65B9\u6CD5"), true);
				}
			}
		}
	}
}
