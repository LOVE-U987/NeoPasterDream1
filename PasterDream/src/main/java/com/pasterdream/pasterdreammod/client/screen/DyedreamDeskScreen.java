package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.DyedreamDeskMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 染梦书桌 GUI 屏幕 (Dyedream Desk Screen)
 * 渲染 176×182 的 GUI 纹理（比标准 166 高 16px，向上偏移绘制）
 * 与原模组的 DyedreamDeskGuiScreen 功能一致：
 * - 不渲染标题文字（原版风格）
 * - 纹理尺寸 176×182，绘制位置向上偏移 16 像素
 * - 单个物品槽居中偏下
 */
public class DyedreamDeskScreen extends AbstractContainerScreen<DyedreamDeskMenu> {

    /**
     * GUI 纹理路径
     * 原版纹理尺寸：176×182（比标准多 16px 高度，用于扩展显示区域）
     */
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/dyedream_desk_gui_0.png");

    /**
     * 构造染梦书桌 GUI 屏幕
     *
     * @param menu  容器菜单
     * @param inv   玩家库存
     * @param title 标题文本
     */
    public DyedreamDeskScreen(DyedreamDeskMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // 绘制背景纹理，纹理尺寸 176×182
        // 向上偏移 16 像素绘制，使 GUI 在垂直方向上有更多显示空间
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
        // 不渲染标题文字（原模组风格，无文字标签）
    }
}
