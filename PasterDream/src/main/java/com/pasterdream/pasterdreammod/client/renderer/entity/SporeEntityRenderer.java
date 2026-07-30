package com.pasterdream.pasterdreammod.client.renderer.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.client.model.SporeEntityModel;
import com.pasterdream.pasterdreammod.entity.mob.SporeEntityEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * 孢子实体渲染器
 * <p>
 * 使用自定义立方体模型（{@link SporeEntityModel}）代替原蜘蛛模型，
 * 存活时隐藏身体（仅显示粒子），死后才显示模型实体。
 * <p>
 * 原模组对照：FixPasterDream 的 {@code SporeEntityRenderer} + {@code PinkSlimePr1Procedure}（{@code isAlive} 检测）
 */
public class SporeEntityRenderer extends MobRenderer<SporeEntityEntity, SporeEntityModel<SporeEntityEntity>> {

    /** 孢子实体纹理路径 */
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/entity/spore_entity.png");

    /**
     * 构造孢子实体渲染器
     *
     * @param context 渲染器上下文
     */
    public SporeEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new SporeEntityModel<>(context.bakeLayer(SporeEntityModel.LAYER_LOCATION)), 0.2f);
    }

    @Override
    public ResourceLocation getTextureLocation(SporeEntityEntity entity) {
        return TEXTURE;
    }

    /**
     * 覆盖身体可见性 —— 存活时不可见（仅显示粒子），死后显示模型
     * <p>
     * 原模组逻辑：{@code !PinkSlimePr1Procedure.execute(entity)} = {@code !entity.isAlive()}
     */
    @Override
    protected boolean isBodyVisible(SporeEntityEntity entity) {
        return !entity.isAlive();
    }
}
