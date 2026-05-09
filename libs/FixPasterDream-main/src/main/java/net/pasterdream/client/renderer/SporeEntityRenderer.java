
package net.pasterdream.client.renderer;

import net.pasterdream.procedures.PinkSlimePr1Procedure;
import net.pasterdream.entity.SporeEntityEntity;
import net.pasterdream.client.model.Modelspore_entity;

import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class SporeEntityRenderer extends MobRenderer<SporeEntityEntity, Modelspore_entity<SporeEntityEntity>> {
	public SporeEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelspore_entity(context.bakeLayer(Modelspore_entity.LAYER_LOCATION)), 0.2f);
	}

	@Override
	public ResourceLocation getTextureLocation(SporeEntityEntity entity) {
		return new ResourceLocation("pasterdream:textures/entities/spore_entity.png");
	}

	@Override
	protected boolean isBodyVisible(SporeEntityEntity entity) {
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		return !PinkSlimePr1Procedure.execute(entity);
	}
}
