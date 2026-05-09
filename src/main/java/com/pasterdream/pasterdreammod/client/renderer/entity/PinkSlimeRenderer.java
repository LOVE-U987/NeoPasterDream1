package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.model.Modelslime;
import com.pasterdream.pasterdreammod.entity.mob.PinkSlimeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * 粉色史莱姆渲染器
 * 使用自定义 Modelslime 模型，匹配原版 UV 映射纹理
 */
public class PinkSlimeRenderer extends MobRenderer<PinkSlimeEntity, Modelslime<PinkSlimeEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/entity/pink_slime.png");

    public PinkSlimeRenderer(EntityRendererProvider.Context context) {
        super(context, new Modelslime<>(context.bakeLayer(Modelslime.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(PinkSlimeEntity entity) {
        return TEXTURE;
    }
}