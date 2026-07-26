package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.ShadowDeskMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 影之桌 GUI 屏幕（shadow_desk）
 * <p>
 * 176×182 纹理向上偏移 16 像素绘制（与原版 ShadowDeskGuiScreen 一致），
 * 无标题文字。
 */
public class ShadowDeskScreen extends AbstractContainerScreen<ShadowDeskMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/shadow_desk_gui.png");

    /**
     * 构造影之桌 GUI 屏幕
     *
     * @param menu  容器菜单
     * @param inv   玩家背包
     * @param title 标题
     */
    public ShadowDeskScreen(ShadowDeskMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(GUI_TEXTURE, this.leftPos, this.topPos - 16, 0, 0,
                176, 182, 176, 182);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 原版无标题文字
    }
}
