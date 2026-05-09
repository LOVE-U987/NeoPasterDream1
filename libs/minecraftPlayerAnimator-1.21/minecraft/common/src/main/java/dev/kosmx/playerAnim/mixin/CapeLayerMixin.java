package dev.kosmx.playerAnim.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeLayer.class)
public abstract class CapeLayerMixin extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public CapeLayerMixin(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
        super(renderLayerParent);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/PlayerModel;renderCloak(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, AbstractClientPlayer abstractClientPlayer, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        AnimationApplier emote = ((IAnimatedPlayer) abstractClientPlayer).playerAnimator_getAnimation();
        if (emote.isActive()) {
            ModelPart torso = this.getParentModel().body;
            Pair<Float, Float> torsoBend = emote.getBend("torso");
            Pair<Float, Float> bodyBend = emote.getBend("body");
            Pair<Float, Float> bend = new Pair<>(torsoBend.getLeft() + bodyBend.getLeft(), torsoBend.getRight() + bodyBend.getRight());

            poseStack.translate(torso.x / 16, torso.y / 16, torso.z / 16);
            poseStack.mulPose((new Quaternionf()).rotateXYZ(torso.xRot, torso.yRot, torso.zRot));
            IBendHelper.rotateMatrixStack(poseStack, torsoBend);
            poseStack.translate(0.0F, 0.0F, 0.125F);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));

            ModelPart cape = ((PlayerModelAccessor)this.getParentModel()).getCloak();
            cape.x = 0;
            cape.y = 0;
            cape.z = 0;
            cape.xRot = 0;
            cape.yRot = 0;
            cape.zRot = 0;

            IBendHelper.INSTANCE.bend(cape, bend);
        }
        else {
            IBendHelper.INSTANCE.bend(((PlayerModelAccessor)this.getParentModel()).getCloak(), null);
        }
    }

    @WrapWithCondition(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V"))
    private boolean mulPose(PoseStack instance, Quaternionf quaternionf, @Local(argsOnly = true) AbstractClientPlayer abstractClientPlayer) {
        AnimationApplier emote = ((IAnimatedPlayer) abstractClientPlayer).playerAnimator_getAnimation();
        return !emote.isActive();
    }

    @WrapWithCondition(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/player/AbstractClientPlayer;FFFFFF)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    private boolean translate(PoseStack instance, float f, float g, float h, @Local(argsOnly = true) AbstractClientPlayer abstractClientPlayer) {
        AnimationApplier emote = ((IAnimatedPlayer) abstractClientPlayer).playerAnimator_getAnimation();
        return !emote.isActive();
    }
}
