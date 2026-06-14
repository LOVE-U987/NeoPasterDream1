package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.PinkChickenEntity;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * 粉色鸡渲染器
 * 使用原版小鸡模型 + 粉色纹理
 */
public class PinkChickenRenderer extends MobRenderer<PinkChickenEntity, ChickenModel<PinkChickenEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/entity/pink_chicken.png");

    /**
     * 构造粉色鸡渲染器
     *
     * @param context 渲染器上下文
     */
    public PinkChickenRenderer(EntityRendererProvider.Context context) {
        super(context, new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(PinkChickenEntity entity) {
        return TEXTURE;
    }
}