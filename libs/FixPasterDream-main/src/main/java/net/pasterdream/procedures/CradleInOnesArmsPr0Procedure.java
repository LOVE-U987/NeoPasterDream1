package net.pasterdream.procedures;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.pasterdream.capability.MeltDreamEnergyCapability;
import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

import java.util.concurrent.atomic.AtomicReference;

public class CradleInOnesArmsPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player pl) {
            if (MeltDreamEnergyCapability.consumePlayerMeltDreamEnergy(pl,5)) {
				if (world instanceof ServerLevel _level) {
					Entity entityToSpawn = PasterdreamModEntities.FOX_FIRE.get().spawn(_level, BlockPos.containing(x, y + 1, z), MobSpawnType.MOB_SUMMONED);
					if (entityToSpawn != null) {
						entityToSpawn.setDeltaMovement(0, 0, 0);
					}
				}
                pl.swing(InteractionHand.MAIN_HAND, true);
				{
					AtomicReference<IItemHandler> _iitemhandlerref = new AtomicReference<>();
					entity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(_iitemhandlerref::set);
					if (_iitemhandlerref.get() != null) {
						for (int _idx = 0; _idx < _iitemhandlerref.get().getSlots(); _idx++) {
							ItemStack itemstackiterator = _iitemhandlerref.get().getStackInSlot(_idx).copy();
							if (itemstackiterator.is(ItemTags.create(new ResourceLocation("pasterdream:magic")))) {
								if (entity instanceof Player _player)
									_player.getCooldowns().addCooldown(itemstackiterator.getItem(), (int) (160 * ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.SKILLCD.get()).getValue()));
							}
						}
					}
				}
			} else {
				if (entity instanceof Player _player && _player.level().isClientSide())
					_player.displayClientMessage(Component.literal("\u878D\u68A6\u80FD\u91CF\u4E0D\u8DB3"), true);
			}
		}
	}
}
