package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 灯影维度进出瞬时效果 —— 对齐原版 {@code LampShadowPr0/Pr1}。
 * <p>
 * 进维：title「灯影之下」+ 配置赠苍白骨针；离维：npc_2 且未 e_0 时挂 {@code shadow_spyon_buff}。
 * 在 {@link PasterDreamMod} 构造器中通过
 * {@code NeoForge.EVENT_BUS.addListener(LampShadowEvents::onPlayerChangedDimension)} 注册。
 */
public final class LampShadowEvents {

    private LampShadowEvents() {
    }

    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        if (event.getTo().equals(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY)) {
            onEnterLampShadow(player);
        }

        if (event.getFrom().equals(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY)) {
            onLeaveLampShadow(player);
        }
    }

    /** 原 LampShadowPr0：times + 延迟 title + 可选赠针 */
    private static void onEnterLampShadow(ServerPlayer player) {
        player.connection.send(new ClientboundSetTitlesAnimationPacket(30, 40, 20));
        ServerScheduler.schedule(5, () -> {
            if (!player.isAlive()
                    || !player.level().dimension().equals(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY)) {
                return;
            }
            player.connection.send(new ClientboundSetTitleTextPacket(Component.literal("灯影之下")));
            if (Boolean.TRUE.equals(PDCommonConfig.IN_LAMP_SHADOW_GIVE_PALE_BONENEEDLE.get())) {
                ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(PDItems.PALE_BONENEEDLE.get()));
            }
        });
    }

    /** 原 LampShadowPr1：npc_2 && !e_0 → shadow_spyon_buff 32000t */
    private static void onLeaveLampShadow(ServerPlayer player) {
        if (hasAdvancement(player, "achievement_shadow_npc_2")
                && !hasAdvancement(player, "achievement_shadow_e_0")) {
            player.addEffect(new MobEffectInstance(
                    PDEffects.SHADOW_SPYON_BUFF.holder(), 32000, 0, false, false));
            PDDebugLogger.mainDebug("[LampShadowEvents] 已为 {} 挂上 shadow_spyon_buff",
                    player.getGameProfile().getName());
        }
    }

    private static boolean hasAdvancement(ServerPlayer player, String name) {
        if (!PDAdvancements.isAdvancementLocked(player, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name))) {
            return true;
        }
        AdvancementHolder holder = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name));
        return holder != null && player.getAdvancements().getOrStartProgress(holder).isDone();
    }
}
