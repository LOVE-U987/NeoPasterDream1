
package net.pasterdream.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.registries.ForgeRegistries;
import net.pasterdream.world.inventory.BlueprintGui0Menu;
import net.pasterdream.PasterdreamMod;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;

import java.util.function.Supplier;
import java.util.HashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BlueprintGui0ButtonMessage {
	private final int now_page, x, y, z;

	public BlueprintGui0ButtonMessage(FriendlyByteBuf buffer) {
		this.now_page = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public BlueprintGui0ButtonMessage(int now_page, int x, int y, int z) {
		this.now_page = now_page;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(BlueprintGui0ButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.now_page);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handler(BlueprintGui0ButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			int now_page = message.now_page;
			int x = message.x;
			int y = message.y;
			int z = message.z;
            handleButtonAction(entity, now_page, x, y, z);
		});
		context.setPacketHandled(true);
	}

	public static void handleButtonAction(Player entity, int now_page, int x, int y, int z) {
		Level world = entity.level();
        if (!world.hasChunkAt(new BlockPos(x, y, z)))
            return;
        if(entity.containerMenu instanceof BlueprintGui0Menu bp)
        {
            HashMap<String, Object> guistate = bp.guistate;
            // security measure to prevent arbitrary chunk generation
            if (!world.isClientSide()) {
                world.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.book.page_turn")), SoundSource.NEUTRAL, 1, 1);
            } else {
                world.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("item.book.page_turn")), SoundSource.NEUTRAL, 1, 1, false);
            }
            bp.refreshSlot(entity,now_page - 1);
        }

	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		PasterdreamMod.addNetworkMessage(BlueprintGui0ButtonMessage.class, BlueprintGui0ButtonMessage::buffer, BlueprintGui0ButtonMessage::new, BlueprintGui0ButtonMessage::handler);
	}
}
