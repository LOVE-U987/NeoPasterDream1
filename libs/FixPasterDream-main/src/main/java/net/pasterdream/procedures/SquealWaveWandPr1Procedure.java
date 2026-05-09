package net.pasterdream.procedures;

import net.minecraft.world.level.Level;
import net.pasterdream.capability.MeltDreamEnergyCapability;
import net.pasterdream.capability.SanCapability;
import top.theillusivec4.curios.api.CuriosApi;

import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.atomic.AtomicReference;

public class SquealWaveWandPr1Procedure {
	public static void execute(Level world, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (entity instanceof Player pl) {
            if (!(pl.getAbilities().instabuild || CuriosApi.getCuriosInventory(pl).map(inv->inv.findFirstCurio(PasterdreamModItems.GHOST_FACE_HEAD.get()).isPresent()).orElse(false))) {
                MeltDreamEnergyCapability.consumePlayerMeltDreamEnergy(pl,0.01);
                SanCapability.addPlayerSanWithCheck(pl,-0.02);
            }
            {
                AtomicReference<IItemHandler> _iitemhandlerref = new AtomicReference<>();
                entity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(_iitemhandlerref::set);
                if (_iitemhandlerref.get() != null) {
                    for (int _idx = 0; _idx < _iitemhandlerref.get().getSlots(); _idx++) {
                        ItemStack itemstackiterator = _iitemhandlerref.get().getStackInSlot(_idx).copy();
                        if (itemstackiterator.is(ItemTags.create(new ResourceLocation("pasterdream:magic")))) {
                            if (entity instanceof Player _player)
                                _player.getCooldowns().addCooldown(itemstackiterator.getItem(), (int) (18 + ((LivingEntity) entity).getAttribute(PasterdreamModAttributes.MAGICCD.get()).getValue()));
                        }
                    }
                }
            }
		}
	}
}
