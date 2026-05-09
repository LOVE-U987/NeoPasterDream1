
package net.pasterdream.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.procedures.DreamAccumulatorPr1Procedure;
import net.pasterdream.procedures.EvasionBuffPr0Procedure;
import net.pasterdream.world.inventory.DreamAccumulatorGuiMenu;

import java.util.HashMap;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EvasionBuffPackage {

	public EvasionBuffPackage() {
	}

	public EvasionBuffPackage(FriendlyByteBuf buffer) {
	}

	public static void buffer(EvasionBuffPackage message, FriendlyByteBuf buffer) {
	}

	public static void handler(EvasionBuffPackage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			Player player = context.getSender();
			EvasionBuffPr0Procedure.execute(player.level(), player.getX(), player.getY(), player.getZ(), player);
		});
		context.setPacketHandled(true);
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		PasterdreamMod.addNetworkMessage(EvasionBuffPackage.class, EvasionBuffPackage::buffer, EvasionBuffPackage::new, EvasionBuffPackage::handler);
	}
}
