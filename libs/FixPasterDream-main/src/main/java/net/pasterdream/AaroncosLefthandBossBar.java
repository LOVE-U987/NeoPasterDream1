package net.pasterdream;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.pasterdream.utils.AmarokUtil;

@OnlyIn(Dist.CLIENT)
public class AaroncosLefthandBossBar {
    private static final Minecraft MC = Minecraft.getInstance();
    public static final ResourceLocation ICON = new ResourceLocation(PasterdreamMod.MODID, "textures/screens/aaroncos_hand_hp.png");

    public static final IGuiOverlay guiOverlay = (gui, maxStack, partialTick, width, height) -> {
        if (MC.player != null && !MC.options.hideGui) {
            gui.setupOverlayRenderState(true, false);
            randomBarHandler(gui, width, height, maxStack);
        }
    };

    public static void randomBarHandler(ForgeGui gui, int x, int y, GuiGraphics maxStack) {
        var player = MC.player;
        assert player != null;
        var nbt = player.getPersistentData();
        float hpL = nbt.getFloat("left_hand_hp");
        float hpR = nbt.getFloat("right_hand_hp");
        boolean flagL = hpL > 0.01f;
        boolean flagR = hpR > 0.01f;
        if (!flagL && !flagR) return;

        MC.getProfiler().push("aaroncos_lefthand_boss_bar");

        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ICON);
        var xBase = x / 2;
        var yBase = 12;

        if (flagL) {
            int baseX = xBase - 56;
            maxStack.blit(ICON, baseX - 62, yBase + 1, 0, 0, 116, 21);
            maxStack.blit(ICON, baseX - 55, yBase + 7, 7, 38, 7 + Mth.floor(106 * hpL), 5);
        }

        if (flagR) {
            maxStack.blit(ICON, xBase,     yBase + 1, 0, 64, 244, 21);
            maxStack.blit(ICON, xBase + 3, yBase + 7, 3, 102, 3 + Mth.floor(106 * hpR), 106);
        }

        maxStack.drawString(MC.font, "§l亚伦柯斯之触", xBase - 30, yBase - 7, -1);

        RenderSystem.disableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ICON);
        MC.getProfiler().pop();
    }
}