package net.pasterdream.entity.model;

import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.constant.DataTickets;

import net.pasterdream.entity.BoneWingEntity;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

public class BoneWingModel extends GeoModel<BoneWingEntity> {
	@Override
	public ResourceLocation getAnimationResource(BoneWingEntity entity) {
		return new ResourceLocation("pasterdream", "animations/bone_wing.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BoneWingEntity entity) {
		return new ResourceLocation("pasterdream", "geo/bone_wing.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BoneWingEntity entity) {
		return new ResourceLocation("pasterdream", "textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(BoneWingEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
