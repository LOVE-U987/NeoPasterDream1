package net.pasterdream.procedures;

import net.minecraft.world.entity.player.Player;
import net.pasterdream.capability.SanCapability;
import top.theillusivec4.curios.api.CuriosApi;

import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class CalaisSpiceBottleCurioPr0Procedure {
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
		if (entity instanceof LivingEntity lv && !lv.level().isClientSide && CuriosApi.getCuriosInventory(lv).map(handler ->
                        handler.findFirstCurio(PasterdreamModItems.CALAIS_SPICE_BOTTLE_CURIO.get()).isPresent())
                .orElse(false) && itemstack.getItem().isEdible()) {
            int rand_case = Mth.nextInt(lv.getRandom(), 1, 14);
            assert itemstack.getItem().getFoodProperties() != null;
            double saturation = itemstack.getItem().getFoodProperties().getSaturationModifier();
            double nutrition = itemstack.getItem().getFoodProperties().getNutrition();
            double total = nutrition + saturation;
			if (rand_case <= 3) {
                if(lv instanceof Player pl)
                {
                    SanCapability.addPlayerSanWithCheck(pl,total * 0.1);
                }
            }
			if (rand_case == 4) {
				lv.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,
                        (int) (total * 40), 0));
			}
			if (rand_case == 5) {
				lv.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,
                        (int) (total * 40), 0));
			}
			if (rand_case == 6) {
				lv.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
                        (int) (total * 40), 0));
            }
			if (rand_case == 7) {
				lv.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,
                        (int) (total * 40), 0));
			}
			if (rand_case == 8) {
				lv.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,
                        (int) (total * 40), 0));
			}
			if (rand_case == 9) {
				lv.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION,
                        (int) (total * 40), 0));
			}
			if (rand_case == 10) {
				lv.addEffect(new MobEffectInstance(MobEffects.JUMP,
                        (int) (total * 40), 0));
			}
			if (rand_case >= 11) {
				lv.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
                        (int) (total * 40), 0));
			}
		}
	}
}
