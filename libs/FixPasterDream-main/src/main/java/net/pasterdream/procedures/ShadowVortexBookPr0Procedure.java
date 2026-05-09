package net.pasterdream.procedures;

import net.pasterdream.capability.MeltDreamEnergyCapability;
import net.pasterdream.capability.SanCapability;
import top.theillusivec4.curios.api.CuriosApi;

import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import java.util.concurrent.atomic.AtomicReference;

public class ShadowVortexBookPr0Procedure {
	public static void execute(LevelAccessor world, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (entity instanceof Player pl) {
            if (entity instanceof ServerPlayer _plr4 && _plr4.level() instanceof ServerLevel
                    && _plr4.getAdvancements().getOrStartProgress(_plr4.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_talent_shadow"))).isDone()) {
                MeltDreamEnergyCapability.consumePlayerMeltDreamEnergy(_plr4,0.01);
                SanCapability.addPlayerSanWithCheck(_plr4,-0.05);
                {
                    AtomicReference<IItemHandler> _iitemhandlerref = new AtomicReference<>();
                    entity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(_iitemhandlerref::set);
                    if (_iitemhandlerref.get() != null) {
                        for (int _idx = 0; _idx < _iitemhandlerref.get().getSlots(); _idx++) {
                            ItemStack itemstackiterator = _iitemhandlerref.get().getStackInSlot(_idx).copy();
                            if (itemstackiterator.is(ItemTags.create(new ResourceLocation("pasterdream:magic")))) {
                                if (entity instanceof Player _player)
                                    _player.getCooldowns().addCooldown(itemstackiterator.getItem(), (int) (24 + ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.MAGICCD.get()).getValue()));
                            }
                        }
                    }
                }
            } else {
                {
                    AtomicReference<IItemHandler> _iitemhandlerref = new AtomicReference<>();
                    entity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(_iitemhandlerref::set);
                    if (_iitemhandlerref.get() != null) {
                        for (int _idx = 0; _idx < _iitemhandlerref.get().getSlots(); _idx++) {
                            ItemStack itemstackiterator = _iitemhandlerref.get().getStackInSlot(_idx).copy();
                            if (itemstackiterator.is(ItemTags.create(new ResourceLocation("pasterdream:magic")))) {
                                if (entity instanceof Player _player)
                                    _player.getCooldowns().addCooldown(itemstackiterator.getItem(), 1000);
                            }
                        }
                    }
                }
                entity.hurt(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC)), 5);
                if (entity instanceof Player _player && !_player.level().isClientSide())
                    _player.displayClientMessage(Component.literal("\u4F60\u4F7F\u7528\u8FD9\u672C\u4E66\u7684\u65F6\u5019\u906D\u5230\u4E86\u53CD\u566C"), true);
            }
		}
	}
}
