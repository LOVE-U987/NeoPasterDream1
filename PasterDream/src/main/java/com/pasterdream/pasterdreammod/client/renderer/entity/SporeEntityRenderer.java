package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.mob.SporeEntityEntity;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * 孢子实体渲染器
 * <p>
 * 使用原版蜘蛛模型（{@link SpiderModel}）作为基础模型，
 * 加载自定义纹理 {@code pasterdream:textures/entity/spore_entity.png}。
 * <p>
 * 因为 {@link SporeEntityEntity} 不实现 GeckoLib 的 {@code GeoEntity}，
 * 所以使用原版 {@link MobRenderer} 体系进行渲染。
 */
public class SporeEntityRenderer extends MobRenderer<SporeEntityEntity, SpiderModel<SporeEntityEntity>> {

    /** 孢子实体纹理路径 */
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/entity/spore_entity.png");

    /**
     * 构造孢子实体渲染器
     *
     * @param context 渲染器上下文
     */
    public SporeEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderModel<>(context.bakeLayer(ModelLayers.SPIDER)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(SporeEntityEntity entity) {
        return TEXTURE;
    }
}