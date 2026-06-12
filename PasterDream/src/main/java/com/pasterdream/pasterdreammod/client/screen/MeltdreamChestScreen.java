package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.MeltdreamChestMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 融梦水晶箱 GUI 屏幕 (Meltdream Chest Screen)
 *
 * 渲染 176×182 的箱子界面纹理
 * 背景纹理使用 meltdream_chest_0.png（位于 textures/screens/ 目录）
 * y 偏移为 1 像素以使纹理与槽位对齐
 */
public class MeltdreamChestScreen extends AbstractContainerScreen<MeltdreamChestMenu> {

    /** GUI 纹理路径 */
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/meltdream_chest_0.png");

    /**
     * 构造融梦水晶箱 GUI 屏幕
     *
     * @param menu  容器菜单
     * @param inv   玩家库存
     * @param title 标题文本
     */
    public MeltdreamChestScreen(MeltdreamChestMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 182;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // 绘制背景纹理
        guiGraphics.blit(GUI_TEXTURE,
                this.leftPos, this.topPos,
                0, 0,
                this.imageWidth, this.imageHeight,
                this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 不渲染标题文字（原模组风格，无文字）
    }
}
