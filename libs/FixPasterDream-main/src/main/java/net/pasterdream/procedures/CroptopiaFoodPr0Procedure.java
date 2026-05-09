package net.pasterdream.procedures;

import net.minecraft.world.entity.player.Player;
import net.pasterdream.capability.SanCapability;
import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class CroptopiaFoodPr0Procedure {
	@SubscribeEvent
	public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity(), event.getItem());
		}
	}

	public static void execute(Entity entity, ItemStack itemstack) {
		execute(null, entity, itemstack);
	}

	private static void execute(@Nullable Event event, Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (itemstack.is(ItemTags.create(new ResourceLocation("croptopia:advancements_food_big"))) || itemstack.is(ItemTags.create(new ResourceLocation("pasterdream:delight_cooked_tag")))) {
			if (entity instanceof Player pl && !pl.level().isClientSide() && pl.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).isPresent()) {
				pl.addEffect(new MobEffectInstance(PasterdreamModMobEffects.COOK_BUFF.get(), 1200, 0, false, false));
                SanCapability.addPlayerSanWithCheck(pl,1);
			}
		}
	}
}
