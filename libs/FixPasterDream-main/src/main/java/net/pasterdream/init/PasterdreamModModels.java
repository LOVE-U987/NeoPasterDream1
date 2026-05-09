
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import net.pasterdream.client.model.Modelsqueal_wave;
import net.pasterdream.client.model.Modelspore_entity;
import net.pasterdream.client.model.Modelslime;
import net.pasterdream.client.model.Modelfireball_projectile;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class PasterdreamModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(Modelsqueal_wave.LAYER_LOCATION, Modelsqueal_wave::createBodyLayer);
		event.registerLayerDefinition(Modelspore_entity.LAYER_LOCATION, Modelspore_entity::createBodyLayer);
		event.registerLayerDefinition(Modelslime.LAYER_LOCATION, Modelslime::createBodyLayer);
		event.registerLayerDefinition(Modelfireball_projectile.LAYER_LOCATION, Modelfireball_projectile::createBodyLayer);
	}
}
