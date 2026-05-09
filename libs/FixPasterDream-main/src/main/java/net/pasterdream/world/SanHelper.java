package net.pasterdream.world;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.capability.SanCapability;
import net.pasterdream.configuration.PasterdreamConfigCommonConfiguration;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.init.PasterdreamModGameRules;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModMobEffects;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = PasterdreamMod.MODID)
public class SanHelper {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void CheckAttributeOnItems(ItemAttributeModifierEvent event)
    {
        if (EffectiveSide.get().isClient() && !SanCapability.IsSanCheckSystem()) {
            //保证客户端不显示
            event.removeAttribute(PasterdreamModAttributes.SAN_VARIABILITY.get());
        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void CheckAttributeOnCurios(CurioAttributeModifierEvent event)
    {
        if (EffectiveSide.get().isClient() && !SanCapability.IsSanCheckSystem()) {
            //保证客户端不显示
            event.removeAttribute(PasterdreamModAttributes.SAN_VARIABILITY.get());
        }
    }
    //当对应规则关闭，或不为玩家生物时，纯精神类effect免疫
    @SubscribeEvent
    public static void SanEffectDeny(MobEffectEvent.Applicable event)
    {
        if(SanCapability.checkSanEffect(event.getEffectInstance().getEffect()))
        {
            if(!SanCapability.IsSanCheckSystem() || !(event.getEntity() instanceof Player))
            {
                event.setResult(Event.Result.DENY);
            }
        }
    }
    private static final String uuid = "1217d41a-1591-455a-8821-3356aad56a00";
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side == LogicalSide.SERVER) {
            ServerPlayer pl = (ServerPlayer) event.player;
            Level lv = pl.level();
            if(SanCapability.IsSanCheckSystem())
            {
                int num = lv.getGameRules().getInt(PasterdreamModGameRules.SANVARIABILITYPERTICK);
                if(pl.tickCount > 0 && pl.tickCount % num == 0 && pl.getAttributeValue(PasterdreamModAttributes.SAN_VARIABILITY.get()) != 0.0)
                {
                    SanCapability.addPlayerSanWithCheck(pl,num * pl.getAttributeValue(PasterdreamModAttributes.SAN_VARIABILITY.get()) / 1200.0);
                }
                if (pl.tickCount % PasterdreamConfigCommonConfiguration.PLAYER_TOTAL_TICK_UPDATE.get() == 0 && PasterdreamConfigCommonConfiguration.LOW_SAN_DEBUFF.get() && !pl.isSpectator())
                {
                    if (pl.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).map(cap -> cap.getSanValue() <= 1 ).orElse(false)) {
                        pl.addEffect(new MobEffectInstance(PasterdreamModMobEffects.INSAND_BUFF.get(), 20, 2, false, false));
                    }
                    else if (pl.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).map(cap -> cap.getSanValue() <= 10 ).orElse(false)) {
                        pl.addEffect(new MobEffectInstance(PasterdreamModMobEffects.INSAND_BUFF.get(), 20, 1, false, false));
                    }
                    else if (pl.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).map(cap -> cap.getSanValue() <= 20 ).orElse(false)) {
                        pl.addEffect(new MobEffectInstance(PasterdreamModMobEffects.INSAND_BUFF.get(), 20, 0, false, false));
                    }
                    else if (pl.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).map(cap -> cap.getSanValue() <= 40 ).orElse(false)) {
                        pl.addEffect(new MobEffectInstance(PasterdreamModMobEffects.TRANCE_BUFF.get(), 20, 0, false, false));
                    }
                    else if (pl.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).map(cap -> cap.getSanValue() <= 60 ).orElse(false)) {
                        pl.addEffect(new MobEffectInstance(PasterdreamModMobEffects.LETHARGY_BUFF.get(), 20, 0, false, false));
                    }
                    if(pl.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).map(cap -> cap.getSanValue() >= PasterdreamConfigCommonConfiguration.CHEERUP_BUFF_THRESHOLD_VALUE.get() ).orElse(false)) {
                        pl.addEffect(new MobEffectInstance(PasterdreamModMobEffects.CHEERUP_BUFF.get(), 20, 0, false, false));
                    }
                    //环境san值影响
                    AttributeInstance instance = pl.getAttribute(PasterdreamModAttributes.SAN_VARIABILITY.get());
                    if(instance != null)
                    {
                        instance.removeModifier(UUID.fromString(uuid));
                        double environment_san_variability = 0;
                        if (CuriosApi.getCuriosInventory(pl).map(handler -> handler.findFirstCurio(PasterdreamModItems.WHITE_FLOWER_BODY.get()).isEmpty()).orElse(true)) {
                            if (lv.dimension() == Level.OVERWORLD && !lv.isDay()) {
                                if (PasterdreamConfigCommonConfiguration.OVERWORLD_NIGHT_LOWERS_SAN.get()) {
                                    environment_san_variability -= 0.96;
                                }
                            }
                            if (lv.dimension() == ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:lamp_shadow_world"))) {
                                environment_san_variability -= 2.4;
                            }
                            if (lv.dimension() == ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:aaroncos_arena_world"))) {
                                environment_san_variability -= 9.6;
                            }
                        }
                        if (lv.dimension() == ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:dyedream_world"))) {
                            environment_san_variability += 4.8;
                        }
                        if (lv.dimension() == ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:wind_journey_world"))) {
                            environment_san_variability += 1.2;
                            pl.addEffect(new MobEffectInstance(PasterdreamModMobEffects.CLOUDMIST_BUFF.get(), 200, 0, false, false));
                        }
                        instance.addTransientModifier(new AttributeModifier(UUID.fromString(uuid),"pasterdream.environment.sanvariability",environment_san_variability, AttributeModifier.Operation.ADDITION));
                    }
                }
            }
        }
    }
}
