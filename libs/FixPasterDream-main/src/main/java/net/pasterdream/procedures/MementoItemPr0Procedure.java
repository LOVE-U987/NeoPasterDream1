package net.pasterdream.procedures;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.pasterdream.capability.MeltDreamEnergyCapability;
import net.pasterdream.capability.SanCapability;
import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class MementoItemPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (itemstack.getItem() == PasterdreamModItems.MEMENTO_ITEM_01.get()
                || itemstack.getItem() == PasterdreamModItems.MEMENTO_ITEM_06.get()
                || itemstack.getItem() == PasterdreamModItems.MEMENTO_ITEM_10.get()
				|| itemstack.getItem() == PasterdreamModItems.MEMENTO_ITEM_05.get()) {
			if(entity instanceof ServerPlayer sp)
            {
                SanCapability.addPlayerSanWithCheck(sp,10);
                MeltDreamEnergyCapability.addPlayerMeltDreamEnergy(sp,10);
            }
        }
		if (itemstack.getItem() == PasterdreamModItems.MEMENTO_ITEM_07.get()) {
            if(entity instanceof ServerPlayer sp)
            {
                if (Mth.nextDouble(sp.getRandom(),0,1) <= 0.01) {
                    SanCapability.addPlayerSanWithCheck(sp,-100);
                } else {
                    SanCapability.addPlayerSanWithCheck(sp,20);
                }
            }

		}
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.MEMENTO_BUFF.get(), 3600, 0));
		itemstack.shrink(1);
		if (world instanceof Level _level) {
			if (!_level.isClientSide()) {
				_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:ding_0")), SoundSource.PLAYERS, 1, 1);
			} 
		}
	}
}
