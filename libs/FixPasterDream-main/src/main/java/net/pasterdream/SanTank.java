package net.pasterdream;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.pasterdream.capability.SanCapability;
import net.pasterdream.init.PasterdreamModAttributes;
import net.pasterdream.configuration.PasterdreamConfigClientConfiguration;

import net.pasterdream.utils.AmarokUtil;

@OnlyIn(Dist.CLIENT)
public class SanTank {
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
        if (!SanCapability.IsSanCheckSystem())return;
        if (PasterdreamConfigClientConfiguration.STEALTH_DISPLAY_ATTRIBUTE_HUD.get() && !player.isShiftKeyDown()) return;
        MC.getProfiler().push("san_bar");
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, MeltdreamenergyTank.ICON);
        var xBase = x + PasterdreamConfigClientConfiguration.SAN_TANK_XBASE.get().intValue();
        var yBase = y + PasterdreamConfigClientConfiguration.SAN_TANK_YBASE.get().intValue();
        int amount = (int) Math.round(player.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).map(SanCapability::getSanValue).orElse(100.0));
        float smallAmout = (float) 20 / 100;
        maxStack.blit(ICON, xBase, yBase,0,32,32,32);
        maxStack.blit(ICON, xBase, yBase + 6, 0, 70, 32, 20 - Math.round(amount * smallAmout));
        AttributeInstance instance = player.getAttribute(PasterdreamModAttributes.SAN_VARIABILITY.get());
        if(instance != null)
        {
            boolean flag = Math.abs(instance.getValue()) < 5;
            if(instance.getValue() > 0)
            {
                maxStack.blit(ICON, xBase + 18 + (flag ? 3 : 0), yBase + 16 + (flag ? 4 : 0),flag ? 7 : 14,flag ? 8 : 16,32,32,14,16,256, 256);
            }
            else if(instance.getValue() < 0){
                maxStack.blit(ICON, xBase + 18 + (flag ? 3 : 0), yBase + 16 + (flag ? 4 : 0),flag ? 7 : 14,flag ? 8 : 16,32,48,14,16,256, 256);
            }
        }
        if (player.isShiftKeyDown()) maxStack.drawString(MC.font, amount + "/100", xBase + 4, yBase - 4, -1);
        RenderSystem.disableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ICON);
        MC.getProfiler().pop();
    }
}