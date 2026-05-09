package net.pasterdream;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.pasterdream.capability.SanCapability;
import net.pasterdream.init.PasterdreamModAttributes;

@OnlyIn(Dist.CLIENT)
public class LoseMind {
    private static final Minecraft MC = Minecraft.getInstance();
    public static final ResourceLocation ICON = new ResourceLocation(PasterdreamMod.MODID, "textures/screens/lose_mind_gui.png");

    public static final IGuiOverlay guiOverlay = (gui, maxStack, partialTick, width, height) -> {
        if (!(MC.player.getVehicle() instanceof LivingEntity) && !Minecraft.getInstance().options.hideGui) {
            gui.setupOverlayRenderState(true, false);
            randomBarHandler(gui, width, height, maxStack); 
        }
    };

    public static void randomBarHandler(ForgeGui gui, int x, int y, GuiGraphics maxStack) {
        var player = MC.player;
        if(!SanCapability.IsSanCheckSystem())return;
        MC.getProfiler().push("lose_mind_gui");
        var amount = player.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).map(SanCapability::getSanValue).orElse(100.0);

        var nbt = player.getPersistentData();
        float mind;

        if (nbt.contains("mind")) {
            mind = nbt.getFloat("mind");
        } else mind = 0.0F;

        if (amount <= 20.0D) {
            if (mind < 1.0F) mind += 0.005F;
        } else if (mind > 0.0F) mind -= 0.01F;

        nbt.putFloat("mind", mind);

        if (mind > 0.01F) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, mind);
            RenderSystem.setShaderTexture(0, ICON);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(0.0D, y, -90.0D).uv(0.0F, 1.0F).endVertex();
            bufferbuilder.vertex(x, y, -90.0D).uv(1.0F, 1.0F).endVertex();
            bufferbuilder.vertex(x, 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
            bufferbuilder.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();
            tesselator.end();
            // RenderSystem.depthMask(true);
            // RenderSystem.enableDepthTest();
            // RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, mind);
        }
    }
}