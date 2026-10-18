package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.DreamCauldronMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 梦境炼药锅 GUI 屏幕 (Dream Cauldron Screen)
 * 类原版酿造台的自动酿造界面：196×196 背景纹理 + 酿造进度条 + 液体量文本。
 *
 * 纹理资源：
 * - dream_cauldron_gui.png：背景纹理（196×196，原版素材）
 *
 * 界面元素（相对于 GUI 纹理左上角）：
 * - 酿造进度条：位置 (78, 71)，尺寸 (38, 15)——原合成按钮区域，酿造中显示
 * - 液体量文本：位置 (170, 43)，显示储罐中融梦液体毫桶数
 *
 * 酿造为自动触发：材料放入即由服务端判定启动，GUI 保持打开并实时显示进度。
 */
public class DreamCauldronScreen extends AbstractContainerScreen<DreamCauldronMenu> {

    /** GUI 背景纹理（原版 196×196） */
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/dream_cauldron_gui.png");

    /** 酿造进度条区域（相对于 GUI 纹理，原合成按钮位置） */
    private static final int PROGRESS_BAR_X = 78;
    private static final int PROGRESS_BAR_Y = 71;
    private static final int PROGRESS_BAR_WIDTH = 38;
    private static final int PROGRESS_BAR_HEIGHT = 15;

    /** 进度条底色（半透明深色，标识进度条区域） */
    private static final int PROGRESS_BAR_BG_COLOR = 0x66000000;

    /** 进度条填充色（梦之紫） */
    private static final int PROGRESS_BAR_FILL_COLOR = 0xFF9370DB;

    /** 液体量文本位置与颜色（原版值） */
    private static final int FLUID_TEXT_X = 170;
    private static final int FLUID_TEXT_Y = 43;
    private static final int FLUID_TEXT_COLOR = -26887;

    /**
     * 构造梦境炼药锅 GUI 屏幕
     *
     * @param menu  容器菜单
     * @param inv   玩家库存
     * @param title 标题文本
     */
    public DreamCauldronScreen(DreamCauldronMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 196;
        this.imageHeight = 196;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // 绘制背景纹理，纹理尺寸 196×196
        guiGraphics.blit(GUI_TEXTURE,
                this.leftPos, this.topPos,
                0, 0,
                this.imageWidth, this.imageHeight,
                this.imageWidth, this.imageHeight);

        // 绘制酿造进度条（酿造中才显示）
        renderBrewProgressBar(guiGraphics);
    }

    /**
     * 绘制酿造进度条：底色为整块半透明深色，
     * 前景按「已酿造时长 / 总时长」比例从左向右填充梦之紫
     *
     * @param guiGraphics 绘制上下文
     */
    private void renderBrewProgressBar(GuiGraphics guiGraphics) {
        int duration = this.menu.getBrewDuration();
        if (duration <= 0) {
            return;
        }
        int elapsed = Math.min(this.menu.getBrewTimeElapsed(), duration);
        int x = this.leftPos + PROGRESS_BAR_X;
        int y = this.topPos + PROGRESS_BAR_Y;
        int fillWidth = (int) ((long) elapsed * PROGRESS_BAR_WIDTH / duration);
        guiGraphics.fill(x, y, x + PROGRESS_BAR_WIDTH, y + PROGRESS_BAR_HEIGHT,
                PROGRESS_BAR_BG_COLOR);
        if (fillWidth > 0) {
            guiGraphics.fill(x, y, x + fillWidth, y + PROGRESS_BAR_HEIGHT,
                    PROGRESS_BAR_FILL_COLOR);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 与原版一致：不绘制标题/背包标签，仅绘制储罐液体量文本
        guiGraphics.drawString(this.font,
                this.menu.getFluidAmount() + "mb",
                FLUID_TEXT_X, FLUID_TEXT_Y, FLUID_TEXT_COLOR, false);
    }
}
