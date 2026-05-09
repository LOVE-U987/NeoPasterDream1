package net.pasterdream;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.pasterdream.configuration.PasterdreamConfigClientConfiguration;

import java.awt.*;

public class PlayerHealthHUD implements IGuiOverlay  {
    private static final Minecraft MC = Minecraft.getInstance();
    public static final ResourceLocation ICON = new ResourceLocation(PasterdreamMod.MODID, "textures/screens/paster_widgets.png");

    private double playerHealth = 0;
    private long healthUpdateCounter = 0;

    @Override
    public void render(ForgeGui forgeGui, GuiGraphics guiGraphics, float v, int w, int h) {
        if (!PasterdreamConfigClientConfiguration.PASTER_HEALTH_HUD.get()) return;
        if (!forgeGui.shouldDrawSurvivalElements()) return;

        Font font = forgeGui.getFont();
        Player player = (Player) MC.getCameraEntity();
        if (player == null) return;

        var health = Mth.ceil(player.getHealth());
        var playerMaxHealth = player.getMaxHealth();
        var playerOtherHealth = player.getAbsorptionAmount();

        forgeGui.setupOverlayRenderState(true, false);

        MC.getProfiler().push("pd_health");
        RenderSystem.enableBlend();
        {
            int updateCounter = forgeGui.getGuiTicks();
            boolean highlight = healthUpdateCounter > (long) updateCounter && (healthUpdateCounter - (long) updateCounter) / 3 % 2 == 1;

            if (player.invulnerableTime > 0 && health != playerHealth)
                healthUpdateCounter = updateCounter + (health < playerHealth ? 20 : 10);

            int flagColor = player.hasEffect(MobEffects.POISON) ? 1 : player.hasEffect(MobEffects.WITHER) ? 2 : 0;

            playerHealth = health;
            var startCount = highlight ? 84 : 0;
            int x = w / 2 - 91;
            int y = h - 40;

            /* Render Bar and Health */
            guiGraphics.blit(ICON, x, y, 0, 0, 85, 10);
            guiGraphics.blit(ICON, x + 1, y + 2, startCount + 1, 10 + 6 * flagColor, (int) (83 * (health / playerMaxHealth)), 6);


            /* Render String */
            String text = String.format("%s/%s", health + playerOtherHealth, playerMaxHealth).replace(".0", "");
            guiGraphics.drawString(font, text, x + ((85 / 2) - (font.width(text) / 2)), y, playerOtherHealth > 0.0f ? Color.YELLOW.getRGB() : Color.WHITE.getRGB());
        }
        forgeGui.leftHeight += 11;

        RenderSystem.disableBlend();
        MC.getProfiler().pop();
    }
}
