package dev.kosmx.playerAnim.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ElytraLayer.class)
public class ElytraLayerMixin<T extends LivingEntity, M extends EntityModel<T>> {
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ElytraModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private void inject(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (livingEntity instanceof AbstractClientPlayer) {
            AnimationApplier emote = ((IAnimatedPlayer) livingEntity).playerAnimator_getAnimation();
            if (emote.isActive()) {
                Vec3f translation = emote.get3DTransform("torso", TransformType.POSITION, Vec3f.ZERO);
                Vec3f rotation = emote.get3DTransform("torso", TransformType.ROTATION, Vec3f.ZERO);
                poseStack.translate(translation.getX() / 16, translation.getY() / 16, translation.getZ() / 16);
                poseStack.translate(0.0F, 0.0F, -0.125F);
                poseStack.mulPose((new Quaternionf()).rotateXYZ(rotation.getX(), rotation.getY(), rotation.getZ()));
                IBendHelper.rotateMatrixStack(poseStack, emote.getBend("torso"));
                poseStack.translate(0.0F, 0.0F, 0.125F);
            }
        }
    }
}
