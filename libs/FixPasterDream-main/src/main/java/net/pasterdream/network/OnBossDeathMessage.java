package net.pasterdream.network;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.pasterdream.PasterdreamMod;

import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = PasterdreamMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OnBossDeathMessage {
    int nbt;

    static final Map<Integer, String> map = Maps.newHashMap();

    static {
        map.put(0, "left_hand_hp");
        map.put(1, "right_hand_hp");
    }

    public OnBossDeathMessage(int nbtName) {
        this.nbt = nbtName;
    }

    public OnBossDeathMessage(FriendlyByteBuf buffer) {
        this.nbt = buffer.readInt();
    }

    public static void buffer(OnBossDeathMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.nbt);
    }

    public static void handler(OnBossDeathMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handlePacket(message, contextSupplier));});
        context.setPacketHandled(true);
    }

    public static void handlePacket(OnBossDeathMessage message, Supplier<NetworkEvent.Context> ctx) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        player.getPersistentData().remove(map.get(message.nbt));
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        PasterdreamMod.addNetworkMessage(OnBossDeathMessage.class, OnBossDeathMessage::buffer, OnBossDeathMessage::new, OnBossDeathMessage::handler);
    }
}
