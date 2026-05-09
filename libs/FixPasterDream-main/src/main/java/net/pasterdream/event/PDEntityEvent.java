package net.pasterdream.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingSwapItemsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.init.PasterdreamModMobEffects;
import net.pasterdream.potion.EvasionBuffMobEffect;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.world.inventory.BlueprintGui0Menu;

@Mod.EventBusSubscriber(modid = PasterdreamMod.MODID)
public class PDEntityEvent {
    @SubscribeEvent
    public static void onEntityDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            var effectEvasion = PasterdreamModMobEffects.EVASION_BUFF.get();

            if (player.hasEffect(effectEvasion)) {
                var effect = player.getEffect(effectEvasion);
                assert effect != null;

                player.removeEffect(effectEvasion);
                int lv = effect.getAmplifier();
                if (lv > 0) player.addEffect(new MobEffectInstance(effectEvasion, effect.getDuration(), lv - 1));
                MinecraftForge.EVENT_BUS.post(new EvasionBuffMobEffect.PlayerEvasionDamageEvent(player));
                event.setCanceled(true);
                return;
            }
        }

        var entity = event.getEntity();
		if (!entity.hasEffect(PasterdreamModMobEffects.VULNERABILITY_BUFF.get())) return;
        var effect = entity.getEffect(PasterdreamModMobEffects.VULNERABILITY_BUFF.get());
        assert effect != null;
		event.setAmount(event.getAmount() * 0.1f * effect.getAmplifier());
    }

    @SubscribeEvent
    public static void onEntityHeal(LivingHealEvent event) {
        if (event.getEntity() instanceof Player player) {
            var entity = event.getEntity();
            var value = entity.getAttributeValue(PasterdreamModAttributes.TOTALHEALING.get());
            event.setAmount((float) (event.getAmount() * value));
        }
    }
}