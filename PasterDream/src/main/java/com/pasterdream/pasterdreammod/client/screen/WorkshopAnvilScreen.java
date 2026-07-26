package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.WorkshopAnvilMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 工坊铁砧 GUI 屏幕 (Workshop Anvil Screen)
 * 布局完全还原原版 WorkshopAnvilGuiScreen：176×216 背景纹理 +
 * "开始锻造"按钮 + 5 个数字图片按钮 + 目标数字指示灯 + 积分标签。
 * <ul>
 *   <li>指示灯：目标数字为 k 时在 (24+27(k-1), 14) 绘制 anvil_button_1.png（18×18）；</li>
 *   <li>数字按钮：位于 (24+27(k-1), 32)，18×36 图集（v=0 常态 / v=18 悬停），
 *       原版为 ImageButton，1.21 改为手绘 + {@code mouseClicked} 命中判定
 *       （与 DreamCauldronScreen 模式一致）；</li>
 *   <li>积分标签：位置 (78, 68)，颜色 -12829636（原版 WorkshopAnvilPr2 文本）。</li>
 * </ul>
 * 全部按钮经 vanilla {@code handleInventoryButtonClick} 通道发送
 * （0 = 开始锻造，1-5 = 数字按钮，等价原版 WorkshopAnvilGuiButtonMessage）。
 */
public class WorkshopAnvilScreen extends AbstractContainerScreen<WorkshopAnvilMenu> {

    /** GUI 背景纹理（原版 176×216） */
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/workshop_anvil_gui.png");

    /** 目标数字指示灯纹理（18×18） */
    private static final ResourceLocation INDICATOR_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/anvil_button_1.png");

    /** 数字按钮图集（18×36：v=0 常态帧，v=18 悬停帧），索引 0-4 对应按钮 1-5 */
    private static final ResourceLocation[] NUMBER_BUTTON_TEXTURES = {
            buttonTexture("imagebutton_anvil_button_0"),
            buttonTexture("imagebutton_anvil_button_01"),
            buttonTexture("imagebutton_anvil_button_02"),
            buttonTexture("imagebutton_anvil_button_03"),
            buttonTexture("imagebutton_anvil_button_04")
    };

    /** 数字按钮/指示灯的横向起点与步距（原版 24 起步、间隔 27） */
    private static final int BUTTON_START_X = 24;
    private static final int BUTTON_STEP_X = 27;
    /** 指示灯纵坐标 */
    private static final int INDICATOR_Y = 14;
    /** 数字按钮纵坐标与尺寸 */
    private static final int BUTTON_Y = 32;
    private static final int BUTTON_SIZE = 18;

    /** 积分标签位置与颜色（原版值） */
    private static final int SCORE_TEXT_X = 78;
    private static final int SCORE_TEXT_Y = 68;
    private static final int SCORE_TEXT_COLOR = -12829636;

    /**
     * 构造工坊铁砧 GUI 屏幕
     *
     * @param menu  容器菜单
     * @param inv   玩家库存
     * @param title 标题文本
     */
    public WorkshopAnvilScreen(WorkshopAnvilMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 216;
    }

    /**
     * 构造数字按钮图集纹理路径
     *
     * @param name 图集文件名
     * @return 纹理资源路径
     */
    private static ResourceLocation buttonTexture(String name) {
        return ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                "textures/screens/atlas/" + name + ".png");
    }

    @Override
    public void init() {
        super.init();
        // "开始锻造"按钮（原版 bounds(leftPos+62, topPos+83, 51, 20)）
        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.pasterdream.workshop_anvil_gui.button_start"),
                        button -> sendButton(WorkshopAnvilMenu.BUTTON_START))
                .bounds(this.leftPos + 62, this.topPos + 83, 51, 20)
                .build());
    }

    /**
     * 经 vanilla 通道发送菜单按钮点击
     *
     * @param id 按钮 ID
     */
    private void sendButton(int id) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(GUI_TEXTURE,
                this.leftPos, this.topPos,
                0, 0,
                this.imageWidth, this.imageHeight,
                this.imageWidth, this.imageHeight);

        // 目标数字指示灯（原版 NbtNumber1-5 条件贴图）
        int number = this.menu.getNumber();
        if (number >= 1 && number <= 5) {
            guiGraphics.blit(INDICATOR_TEXTURE,
                    this.leftPos + BUTTON_START_X + BUTTON_STEP_X * (number - 1), this.topPos + INDICATOR_Y,
                    0, 0, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE);
        }

        // 5 个数字按钮（18×36 图集：悬停时使用下半帧）
        for (int i = 0; i < 5; i++) {
            int x = BUTTON_START_X + BUTTON_STEP_X * i;
            boolean hover = isHoveringArea(x, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE, mouseX, mouseY);
            guiGraphics.blit(NUMBER_BUTTON_TEXTURES[i],
                    this.leftPos + x, this.topPos + BUTTON_Y,
                    0, hover ? BUTTON_SIZE : 0,
                    BUTTON_SIZE, BUTTON_SIZE,
                    BUTTON_SIZE, BUTTON_SIZE * 2);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 与原版一致：仅绘制积分文本（WorkshopAnvilPr2Procedure 的返回值）
        guiGraphics.drawString(this.font,
                "积分 " + new java.text.DecimalFormat("#.#").format(this.menu.getScore()),
                SCORE_TEXT_X, SCORE_TEXT_Y, SCORE_TEXT_COLOR, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (int i = 0; i < 5; i++) {
                int x = BUTTON_START_X + BUTTON_STEP_X * i;
                if (isHoveringArea(x, BUTTON_Y, BUTTON_SIZE, BUTTON_SIZE, (int) mouseX, (int) mouseY)) {
                    // 数字按钮 1-5（等价原版 WorkshopAnvilGuiButtonMessage 1-5）
                    sendButton(i + 1);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /**
     * 判断鼠标是否悬浮在指定区域内（相对 GUI 左上角）
     *
     * @param x      区域左上角 x
     * @param y      区域左上角 y
     * @param w      区域宽度
     * @param h      区域高度
     * @param mouseX 鼠标屏幕 x
     * @param mouseY 鼠标屏幕 y
     * @return 是否悬浮在区域内
     */
    private boolean isHoveringArea(int x, int y, int w, int h, int mouseX, int mouseY) {
        int guiX = mouseX - this.leftPos;
        int guiY = mouseY - this.topPos;
        return guiX >= x && guiX < x + w && guiY >= y && guiY < y + h;
    }
}
