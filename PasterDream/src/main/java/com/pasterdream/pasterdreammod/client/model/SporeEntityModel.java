package com.pasterdream.pasterdreammod.client.model;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * 孢子实体模型（SporeEntityModel）—— Blockbench 导出自定义模型
 * <p>
 * 原模组对照：FixPasterDream 的 {@code Modelspore_entity}（2x2x2 小型立方体）
 * <p>
 * 模型结构：
 * <ul>
 *   <li>{@code bb_main} — 2x2x2 中心立方体，UV(0,0)，16x16 纹理</li>
 * </ul>
 * <p>
 * 因为孢子实体在存活时通过 {@code isBodyVisible} 隐藏身体（仅显示粒子），
 * 死后才显示此模型，所以模型本身仅为一个简洁的孢子残留物。
 */
public class SporeEntityModel<T extends Entity> extends EntityModel<T> {

    /** 模型层位置 */
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(
                    ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "spore_entity"),
                    "main");

    /** 主体模型部件 */
    public final ModelPart bb_main;

    /**
     * 构造孢子实体模型
     *
     * @param root 模型根部部件
     */
    public SporeEntityModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    /**
     * 创建模型层定义
     * <p>
     * 2x2x2 小型中心立方体，UV(0,0)，纹理分辨率 16x16
     *
     * @return 层定义
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        // 孢子实体为简单静态模型，无需动画
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, int color) {
        bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
