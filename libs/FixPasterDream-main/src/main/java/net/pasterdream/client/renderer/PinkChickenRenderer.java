
package net.pasterdream.client.renderer;

import net.pasterdream.entity.PinkChickenEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.ChickenModel;

public class PinkChickenRenderer extends MobRenderer<PinkChickenEntity, ChickenModel<PinkChickenEntity>> {
	public PinkChickenRenderer(EntityRendererProvider.Context context) {
		super(context, new ChickenModel(context.bakeLayer(ModelLayers.CHICKEN)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(PinkChickenEntity entity) {
		return new ResourceLocation("pasterdream:textures/entities/fen_hong_ji_.png");
	}
}
