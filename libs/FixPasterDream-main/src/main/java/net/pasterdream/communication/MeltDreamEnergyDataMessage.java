package net.pasterdream.communication;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.pasterdream.capability.MeltDreamEnergyCapability;

import java.util.function.Supplier;

public class MeltDreamEnergyDataMessage {
    private final CompoundTag capabilityNBT;
    public MeltDreamEnergyDataMessage(MeltDreamEnergyCapability  cap) {
        var tag = new CompoundTag();
        cap.saveNBTData(tag);
        this.capabilityNBT = tag;
    }
    public MeltDreamEnergyDataMessage(FriendlyByteBuf buffer) {
        this.capabilityNBT = buffer.readNbt();
    }
    public void write(FriendlyByteBuf buffer) {
        buffer.writeNbt(capabilityNBT);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Handler.handle(this, context));
    }

    private static class Handler {
        static void handle(MeltDreamEnergyDataMessage message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                Player player = Minecraft.getInstance().player;
                assert player != null;
                player.getCapability(MeltDreamEnergyCapability.Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).ifPresent(cap -> {
                    cap.loadNBTData(message.capabilityNBT);
                });
            });
            context.get().setPacketHandled(true);
        }
    }
}
