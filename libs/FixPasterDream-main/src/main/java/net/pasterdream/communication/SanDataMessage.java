package net.pasterdream.communication;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.pasterdream.capability.SanCapability;

import java.util.function.Supplier;

public class SanDataMessage {
    int kind;//kind = 0 仅修改数值，kind = 1 仅修改sancheck，kind = 2 数据同步
    double SanValue;
    boolean SanCheck;
    private CompoundTag capabilityNBT;
    public SanDataMessage(double value) {
        this.kind = 0;
        this.SanValue = value;
    }
    public SanDataMessage(boolean value) {
        this.kind = 1;
        this.SanCheck = value;
    }
    public SanDataMessage(SanCapability cap) {
        var tag = new CompoundTag();
        cap.saveNBTData(tag);
        this.kind = 2;
        this.capabilityNBT = tag;
    }
    public SanDataMessage(FriendlyByteBuf buffer) {
        this.kind = buffer.readInt();
        if(this.kind == 0) {
            this.SanValue = buffer.readDouble();
        }
        else if(this.kind == 1) {
            this.SanCheck = buffer.readBoolean();
        }
        else {
            this.capabilityNBT = buffer.readNbt();
        }
    }
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.kind);
        if(this.kind == 0) {
            buffer.writeDouble(SanValue);
        }
        else if(this.kind == 1) {
            buffer.writeBoolean(SanCheck);
        }
        else {
            buffer.writeNbt(capabilityNBT);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Handler.handle(this, context));
    }

    private static class Handler {
        static void handle(SanDataMessage message, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                Player player = Minecraft.getInstance().player;
                assert player != null;
                player.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).ifPresent(cap -> {
                    if(message.kind == 0)
                    {
                        cap.setSanValue(message.SanValue);
                    }
                    else if(message.kind == 1) {
                        cap.setSanCheck(message.SanCheck);
                    }
                    else {
                        cap.loadNBTData(message.capabilityNBT);
                    }
                });
            });
            context.get().setPacketHandled(true);
        }
    }
}
