package net.pasterdream.procedures;

import net.pasterdream.capability.MeltDreamEnergyCapability;
import software.bernie.geckolib.animatable.GeoItem;

import net.pasterdream.item.MachineWingItem;
import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.configuration.PasterdreamConfigCommonConfiguration;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

public class MachineWingPr0Procedure {
	public static void execute(Player entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (PasterdreamConfigCommonConfiguration.BAN_ALL_THE_WINGS.get() == true) {
			if (entity.level().isClientSide())
                entity.displayClientMessage(Component.literal("\u00A74\u6B64\u7269\u54C1\u5DF2\u88AB\u7981\u7528"), true);
		} else {
            boolean flag = (entity.level().dimension()) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:aaroncos_arena_world")));
            if(entity.tickCount % 20 == 0)
            {
                if(entity.getItemBySlot(EquipmentSlot.CHEST).getItem() == PasterdreamModItems.MACHINE_WING_CHESTPLATE.get() && MeltDreamEnergyCapability.consumePlayerMeltDreamEnergy(entity,0.02*(flag?50:1)))
                {
                    if (!entity.level().isClientSide())
                        entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.MACHINE_WING_EFFECT.get(), 22, 0, false, false));
                }
            }
            if (!entity.onGround()) {
                if (itemstack.getItem() instanceof MachineWingItem)
                    itemstack.getOrCreateTag().putString("geckoAnim", "fly");
            }
		}
	}
}
