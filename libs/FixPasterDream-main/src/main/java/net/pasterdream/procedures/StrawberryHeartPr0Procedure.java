package net.pasterdream.procedures;

import net.pasterdream.capability.MeltDreamEnergyCapability;
import top.theillusivec4.curios.api.CuriosApi;

import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.tags.ItemTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import java.util.concurrent.atomic.AtomicReference;
import java.util.List;
import java.util.Comparator;

public class StrawberryHeartPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, LivingEntity entity, ItemStack itemstack) {
		if (entity == null)
			return;

		if (CuriosApi.getCuriosInventory(entity).map(inv -> inv.findFirstCurio(PasterdreamModItems.QYM_HEAD.get()).isPresent()).orElse(false)) {
			if (entity instanceof Player _player)
				_player.getCooldowns().addCooldown(itemstack.getItem(), 0);
		} else {
			{
				AtomicReference<IItemHandler> _iitemhandlerref = new AtomicReference<>();
				entity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(_iitemhandlerref::set);
				if (_iitemhandlerref.get() != null) {
					for (int _idx = 0; _idx < _iitemhandlerref.get().getSlots(); _idx++) {
						ItemStack itemstackiterator = _iitemhandlerref.get().getStackInSlot(_idx).copy();
						if (itemstackiterator.is(ItemTags.create(new ResourceLocation("pasterdream:magic")))) {
							if (entity instanceof Player _player)
								_player.getCooldowns().addCooldown(itemstackiterator.getItem(), (int) (12 * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.MAGICCD.get()).getValue()));
						}
					}
				}
			}
		}
		if (entity.isShiftKeyDown()) {
			if (entity instanceof Player pl && MeltDreamEnergyCapability.consumePlayerMeltDreamEnergy(pl,0.25)) {
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.note_block.guitar")), SoundSource.PLAYERS, (float) 1.2, (float) 0.8);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.note_block.guitar")), SoundSource.PLAYERS, (float) 1.2, (float) 0.8, false);
					}
				}
				PasterdreamMod.queueServerWork(4, () -> {
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.note_block.guitar")), SoundSource.PLAYERS, (float) 1.2, 1);
						} else {
							_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.note_block.guitar")), SoundSource.PLAYERS, (float) 1.2, 1, false);
						}
					}
				});
				PasterdreamMod.queueServerWork(7, () -> {
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.note_block.guitar")), SoundSource.PLAYERS, (float) 1.2, (float) 1.2);
						} else {
							_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.note_block.guitar")), SoundSource.PLAYERS, (float) 1.2, (float) 1.2, false);
						}
					}
				});
				PasterdreamMod.queueServerWork(10, () -> {
					if (world instanceof Level _level) {
						if (!_level.isClientSide()) {
							_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.note_block.guitar")), SoundSource.PLAYERS, (float) 1.4, (float) 1.7);
						} else {
							_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.note_block.guitar")), SoundSource.PLAYERS, (float) 1.4, (float) 1.7, false);
						}
					}
				});
				{
					final Vec3 _center = new Vec3(x, y, z);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(8 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (entityiterator instanceof Player) {
							if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 0));
							if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
							if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0));
							if (entityiterator instanceof LivingEntity _entity && !_entity.level().isClientSide())
								_entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 0));
						}
					}
				}
				if (entity instanceof Player) {
					{
						AtomicReference<IItemHandler> _iitemhandlerref = new AtomicReference<>();
						entity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(_iitemhandlerref::set);
						if (_iitemhandlerref.get() != null) {
							for (int _idx = 0; _idx < _iitemhandlerref.get().getSlots(); _idx++) {
								ItemStack itemstackiterator = _iitemhandlerref.get().getStackInSlot(_idx).copy();
								if (itemstackiterator.is(ItemTags.create(new ResourceLocation("pasterdream:magic")))) {
									if (entity instanceof Player _player)
										_player.getCooldowns().addCooldown(itemstackiterator.getItem(), (int) (100 * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.MAGICCD.get()).getValue()));
								}
							}
						}
					}
				}
			} else {
				if (entity instanceof Player _player && !_player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u878D\u68A6\u80FD\u91CF\u4E0D\u8DB3"), true);
			}
		}
	}
}
