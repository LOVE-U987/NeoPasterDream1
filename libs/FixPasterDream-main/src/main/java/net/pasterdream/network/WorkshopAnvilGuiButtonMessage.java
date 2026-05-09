
package net.pasterdream.network;

import net.pasterdream.world.inventory.WorkshopAnvilGuiMenu;
import net.pasterdream.procedures.WorkshopAnvilPr0Procedure;
import net.pasterdream.procedures.NbtNumberButton5Procedure;
import net.pasterdream.procedures.NbtNumberButton4Procedure;
import net.pasterdream.procedures.NbtNumberButton3Procedure;
import net.pasterdream.procedures.NbtNumberButton2Procedure;
import net.pasterdream.procedures.NbtNumberButton1Procedure;
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
public class WorkshopAnvilGuiButtonMessage {
	private final int buttonID, x, y, z;

	public WorkshopAnvilGuiButtonMessage(FriendlyByteBuf buffer) {
		this.buttonID = buffer.readInt();
		this.x = buffer.readInt();
		this.y = buffer.readInt();
		this.z = buffer.readInt();
	}

	public WorkshopAnvilGuiButtonMessage(int buttonID, int x, int y, int z) {
		this.buttonID = buttonID;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static void buffer(WorkshopAnvilGuiButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.buttonID);
		buffer.writeInt(message.x);
		buffer.writeInt(message.y);
		buffer.writeInt(message.z);
	}

	public static void handler(WorkshopAnvilGuiButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player entity = context.getSender();
			int buttonID = message.buttonID;
			int x = message.x;
			int y = message.y;
			int z = message.z;
			handleButtonAction(entity, buttonID, x, y, z);
		});
		context.setPacketHandled(true);
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
		HashMap guistate = WorkshopAnvilGuiMenu.guistate;
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;
		if (buttonID == 0) {

			WorkshopAnvilPr0Procedure.execute(world, x, y, z);
		}
		if (buttonID == 1) {

			NbtNumberButton1Procedure.execute(world, x, y, z);
		}
		if (buttonID == 2) {

			NbtNumberButton2Procedure.execute(world, x, y, z);
		}
		if (buttonID == 3) {

			NbtNumberButton3Procedure.execute(world, x, y, z);
		}
		if (buttonID == 4) {

			NbtNumberButton4Procedure.execute(world, x, y, z);
		}
		if (buttonID == 5) {

			NbtNumberButton5Procedure.execute(world, x, y, z);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		PasterdreamMod.addNetworkMessage(WorkshopAnvilGuiButtonMessage.class, WorkshopAnvilGuiButtonMessage::buffer, WorkshopAnvilGuiButtonMessage::new, WorkshopAnvilGuiButtonMessage::handler);
	}
}
