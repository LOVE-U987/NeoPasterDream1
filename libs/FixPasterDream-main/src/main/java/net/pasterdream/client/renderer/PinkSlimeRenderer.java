
package net.pasterdream.client.renderer;

import net.pasterdream.procedures.PinkSlimePr1Procedure;
import net.pasterdream.entity.PinkSlimeEntity;
import net.pasterdream.client.model.Modelslime;

import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class PinkSlimeRenderer extends MobRenderer<PinkSlimeEntity, Modelslime<PinkSlimeEntity>> {
	public PinkSlimeRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelslime(context.bakeLayer(Modelslime.LAYER_LOCATION)), 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(PinkSlimeEntity entity) {
		return new ResourceLocation("pasterdream:textures/entities/fen_hong_shi_lai_mu_.png");
	}

	@Override
	protected boolean isBodyVisible(PinkSlimeEntity entity) {
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		return !PinkSlimePr1Procedure.execute(entity);
	}
}
