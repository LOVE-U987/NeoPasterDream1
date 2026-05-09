package net.pasterdream;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@OnlyIn(Dist.CLIENT)
public class CloudmistHud {
    private static final Minecraft MC = Minecraft.getInstance();
    public static final ResourceLocation ICON = new ResourceLocation(PasterdreamMod.MODID, "textures/screens/cloudmist_hud.png");

    public static final IGuiOverlay guiOverlay = (gui, maxStack, partialTick, width, height) -> {
        if (!(MC.player.getVehicle() instanceof LivingEntity) && !Minecraft.getInstance().options.hideGui) {
            gui.setupOverlayRenderState(true, false);
            randomBarHandler(gui, width, height, maxStack);
        }
    };

    public static void randomBarHandler(ForgeGui gui, int x, int y, GuiGraphics maxStack) {
        var player = MC.player;
        MC.getProfiler().push("couldmist_hud");

        var nbt = player.getPersistentData();
        double mind;

        if (nbt.contains("cloudmist_percent")) {
            mind = nbt.getDouble("cloudmist_percent") / 100;
        } else mind = 0.0d;

        if (mind > 0.01d) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, (float) mind);
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
