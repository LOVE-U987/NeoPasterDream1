package net.pasterdream.entity.model;

import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.constant.DataTickets;

import net.pasterdream.entity.AshBoneWingEntity;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

public class AshBoneWingModel extends GeoModel<AshBoneWingEntity> {
	@Override
	public ResourceLocation getAnimationResource(AshBoneWingEntity entity) {
		return new ResourceLocation("pasterdream", "animations/ash_bone_wing.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(AshBoneWingEntity entity) {
		return new ResourceLocation("pasterdream", "geo/ash_bone_wing.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AshBoneWingEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(AshBoneWingEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
