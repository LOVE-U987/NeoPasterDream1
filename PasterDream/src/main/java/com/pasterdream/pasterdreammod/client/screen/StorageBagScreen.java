package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.StorageBagMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 储物袋 GUI 屏幕：小袋 176×166 / 高级袋 176×200。
 */
public class StorageBagScreen extends AbstractContainerScreen<StorageBagMenu> {

    private static final ResourceLocation TEX_SMALL =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/screens/storage_bag_gui_.png");
    private static final ResourceLocation TEX_LARGE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "textures/screens/storage_bag_0_gui_.png");

    public StorageBagScreen(StorageBagMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = menu.isAdvanced() ? 200 : 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        ResourceLocation tex = menu.isAdvanced() ? TEX_LARGE : TEX_SMALL;
        guiGraphics.blit(tex, this.leftPos, this.topPos, 0, 0,
                this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
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
