package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.WorkshopBlastMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 工坊锻炉 GUI 屏幕 (Workshop Blast Screen)
 * 布局完全还原原版 WorkshopBlastGuiScreen：176×196 背景纹理，
 * 储罐岩浆 ≥1000mB 时整幅叠绘满罐贴图 workshop_blast_gui_0.png
 * （原版 WorkshopBlastGuiPr0 条件贴图）。无按钮交互。
 */
public class WorkshopBlastScreen extends AbstractContainerScreen<WorkshopBlastMenu> {

    /** GUI 背景纹理（原版 176×196） */
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/workshop_blast_gui.png");

    /** 满罐叠加贴图（岩浆 ≥1000mB 时整幅叠绘） */
    private static final ResourceLocation GUI_FULL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/workshop_blast_gui_0.png");

    /** 满罐贴图显示门槛（mB，等于一次煅烧的耗量） */
    private static final int FULL_THRESHOLD_MB = 1000;

    /**
     * 构造工坊锻炉 GUI 屏幕
     *
     * @param menu  容器菜单
     * @param inv   玩家库存
     * @param title 标题文本
     */
    public WorkshopBlastScreen(WorkshopBlastMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 196;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(GUI_TEXTURE,
                this.leftPos, this.topPos,
                0, 0,
                this.imageWidth, this.imageHeight,
                this.imageWidth, this.imageHeight);

        if (this.menu.getFluidAmount() >= FULL_THRESHOLD_MB) {
            guiGraphics.blit(GUI_FULL_TEXTURE,
                    this.leftPos, this.topPos,
                    0, 0,
                    this.imageWidth, this.imageHeight,
                    this.imageWidth, this.imageHeight);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 与原版一致：不绘制标题/背包标签
    }
}
