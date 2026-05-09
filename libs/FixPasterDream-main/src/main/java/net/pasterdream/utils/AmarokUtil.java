package net.pasterdream.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.BossEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.init.PasterdreamModEntities;
import net.pasterdream.mixin.BossHealthOverlayMixin;


@Mod.EventBusSubscriber(modid = PasterdreamMod.MODID)
public class AmarokUtil {
    public static String left_name;
    public static String right_name;

    /*
    @SubscribeEvent
    public static void commonInit(FMLCommonSetupEvent event) {
        left_name = PasterdreamModEntities.AARONCOS_LEFTHAND_0.get().getDescription().getString();
        right_name = PasterdreamModEntities.AARONCOS_RIGHTHAND_0.get().getDescription().getString();
    }
    */

    @SubscribeEvent @OnlyIn(Dist.CLIENT)
    public static void renderTick(TickEvent.RenderTickEvent event) {
        var mc = Minecraft.getInstance();
        var layout = mc.gui.getBossOverlay();
        var bossEvent = ((BossHealthOverlayMixin) layout).getEvents();

        var player = mc.player;
        if (player == null) return;
        var nbt = player.getPersistentData();

        if (nbt.contains("left_hand_id")) {
            var id = nbt.getUUID("left_hand_id");
            if (bossEvent.containsKey(id)) return;
            nbt.remove("left_hand_hp");
            nbt.remove("left_hand_id");
        }

        if (nbt.contains("right_hand_id")) {
            var id = nbt.getUUID("right_hand_id");
            if (bossEvent.containsKey(id)) return;
            nbt.remove("right_hand_hp");
            nbt.remove("left_hand_id");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST) @OnlyIn(Dist.CLIENT)
    public static void onBossBarRender(CustomizeGuiOverlayEvent.BossEventProgress event) {
        BossEvent eventBoss = event.getBossEvent();
        String name = eventBoss.getName().getString();
        var player = Minecraft.getInstance().player;

        if (left_name == null) left_name   = PasterdreamModEntities.AARONCOS_LEFTHAND_0.get().getDescription().getString();
        if (right_name == null) right_name = PasterdreamModEntities.AARONCOS_RIGHTHAND_0.get().getDescription().getString();
        assert player != null;
        var nbt = player.getPersistentData();

        if (name.equals(left_name)) {
            nbt.putFloat("left_hand_hp", eventBoss.getProgress());
            nbt.putUUID("left_hand_id", eventBoss.getId());
            event.setCanceled(true);
        }
        else if (name.equals(right_name)) {
            nbt.putFloat("right_hand_hp", eventBoss.getProgress());
            nbt.putUUID("right_hand_id", eventBoss.getId());
            event.setCanceled(true);
        }
    }
}
