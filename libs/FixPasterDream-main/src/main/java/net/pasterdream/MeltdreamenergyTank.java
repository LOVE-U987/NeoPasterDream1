package net.pasterdream;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.pasterdream.capability.MeltDreamEnergyCapability;
import net.pasterdream.configuration.PasterdreamConfigClientConfiguration;


@OnlyIn(Dist.CLIENT)
public class MeltdreamenergyTank {
    private static final Minecraft MC = Minecraft.getInstance();
    public static final ResourceLocation ICON = new ResourceLocation(PasterdreamMod.MODID, "textures/screens/pasterdream_hud.png");

    public static final IGuiOverlay guiOverlay = (gui, maxStack, partialTick, width, height) -> {
        if (!(MC.player.getVehicle() instanceof LivingEntity) && !Minecraft.getInstance().options.hideGui) {
            gui.setupOverlayRenderState(true, false);
            randomBarHandler(gui, width, height, maxStack); 
        }
    };

    public static void randomBarHandler(ForgeGui gui, int x, int y, GuiGraphics maxStack) {
        var player = MC.player;
        if (!player.getCapability(MeltDreamEnergyCapability.Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).isPresent())return;
        if (PasterdreamConfigClientConfiguration.STEALTH_DISPLAY_ATTRIBUTE_HUD.get() && !player.isShiftKeyDown()) return;
        MC.getProfiler().push("meltdreamenergy_bar");
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ICON);
        var xBase = PasterdreamConfigClientConfiguration.MELTDREAMENERGY_TANK_XBASE.get().intValue();
        var yBase = y + PasterdreamConfigClientConfiguration.MELTDREAMENERGY_TANK_YBASE.get().intValue();
        int amount = (int) Math.round(player.getCapability(MeltDreamEnergyCapability.Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).map(MeltDreamEnergyCapability::getMeltDreamEnergy).orElse(0.0));
        float smallAmout = (float) 66 / 100;
        maxStack.blit(ICON, xBase, yBase,0,0,80,15);
        maxStack.blit(ICON, xBase, yBase, 0, 16, 11 + Math.round(smallAmout * amount), 15);
        if (player.isShiftKeyDown()) maxStack.drawString(MC.font, amount + "/100", xBase + 33, yBase - 5, -1);
        RenderSystem.disableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ICON);
        MC.getProfiler().pop();
    }
}