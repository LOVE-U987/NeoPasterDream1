package net.pasterdream.communication;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.pasterdream.PasterdreamMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,modid = PasterdreamMod.MODID)
public class ChannelEventTracker {
    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }
    private static final String PROTOCOL_VERSION = "1.0";
    public static SimpleChannel Channel = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(PasterdreamMod.MODID,"main"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();
    @SubscribeEvent
    public static void setUp(FMLCommonSetupEvent event) {
        Channel.messageBuilder(SanDataMessage.class, id())
                .encoder(SanDataMessage::write)
                .decoder(SanDataMessage::new)
                .consumerMainThread(SanDataMessage::handle)
                .add();
        Channel.messageBuilder(MeltDreamEnergyDataMessage.class, id())
                .encoder(MeltDreamEnergyDataMessage::write)
                .decoder(MeltDreamEnergyDataMessage::new)
                .consumerMainThread(MeltDreamEnergyDataMessage::handle)
                .add();
    }
    public static <MSG> void sendToServer(MSG message) {
        Channel.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        Channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    public static <MSG> void sendToAllPlayers(MSG message) {
        Channel.send(PacketDistributor.ALL.noArg(), message);
    }
    public static <MSG> void sendToPlayersTrackingEntity(MSG message, Entity entity) {
        sendToPlayersTrackingEntity(message, entity, false);
    }
    public static <MSG> void sendToPlayersTrackingEntity(MSG message, Entity entity, boolean sendToSource) {
        Channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
        if (sendToSource && entity instanceof ServerPlayer serverPlayer)
            sendToPlayer(message, serverPlayer);
    }
}
