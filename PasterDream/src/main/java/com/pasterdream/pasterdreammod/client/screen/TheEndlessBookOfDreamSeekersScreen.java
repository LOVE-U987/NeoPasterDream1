package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.TheEndlessBookOfDreamSeekersMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 寻梦者的永恒书卷 GUI 屏幕 (The Endless Book of Dream Seekers Screen)
 * 渲染 176×182 的 GUI 纹理，向上偏移 16 像素绘制
 * 简化版本：无导入按钮，仅含物品展示槽和玩家背包
 */
public class TheEndlessBookOfDreamSeekersScreen extends AbstractContainerScreen<TheEndlessBookOfDreamSeekersMenu> {

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/the_endless_book_of_dream_seekers_gui.png");

    /**
     * 构造寻梦者的永恒书卷 GUI 屏幕
     *
     * @param menu  容器菜单
     * @param inv   玩家库存
     * @param title 标题文本
     */
    public TheEndlessBookOfDreamSeekersScreen(TheEndlessBookOfDreamSeekersMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(GUI_TEXTURE,
                this.leftPos, this.topPos - 16,
                0, 0,
                176, 182,
                176, 182);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }
}
