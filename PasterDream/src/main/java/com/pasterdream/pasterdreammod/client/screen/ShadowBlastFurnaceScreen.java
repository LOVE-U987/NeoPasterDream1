package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.menu.ShadowBlastFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 暗影高炉 GUI 屏幕 (Shadow Blast Furnace Screen)
 * 布局完全还原原版 ShadowBlastFurnaceGuiScreen：176×216 背景
 * （纹理图集 236×216，右侧 60 像素为进度/液体覆盖图）：
 * <ul>
 *   <li>冶炼进度：自 (68, 71) 起自上而下生长，UV(176, 0)，宽 38、满高 34；</li>
 *   <li>液体柱：自 (99, 46) 区域自下而上生长，UV(176, 34+...)，宽 10、满高 35。</li>
 * </ul>
 * 进度与液量经菜单 DataSlot 同步（原版直接读客户端 BE 能力，语义一致）。
 */
public class ShadowBlastFurnaceScreen extends AbstractContainerScreen<ShadowBlastFurnaceMenu> {

    /** GUI 背景纹理图集（原版 236×216） */
    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID,
                    "textures/screens/shadow_blast_furnace_gui.png");

    /** 纹理图集宽度 */
    private static final int ATLAS_WIDTH = 236;
    /** 纹理图集高度 */
    private static final int ATLAS_HEIGHT = 216;

    /**
     * 构造暗影高炉 GUI 屏幕
     *
     * @param menu  容器菜单
     * @param inv   玩家库存
     * @param title 标题文本
     */
    public ShadowBlastFurnaceScreen(ShadowBlastFurnaceMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 216;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(GUI_TEXTURE, this.leftPos, this.topPos, 0, 0,
                176, 216, ATLAS_WIDTH, ATLAS_HEIGHT);
        // 冶炼进度（原版：38 宽，按进度比例向下生长，最高 34 像素）
        int need = this.menu.getNeedBlastingTime();
        int now = this.menu.getBlastingTime();
        if (need > 0) {
            int height = (int) Math.nextUp(34.0 * now / need);
            guiGraphics.blit(GUI_TEXTURE, this.leftPos + 68, this.topPos + 71,
                    176, 0, 38, height, ATLAS_WIDTH, ATLAS_HEIGHT);
        }
        // 暗影液体柱（原版：10 宽，按液量比例自下而上生长，最高 35 像素）
        int capacity = this.menu.getFluidCapacity();
        int amount = this.menu.getFluidAmount();
        if (capacity > 0 && amount > 0) {
            int height = (int) Math.nextUp(35.0 * amount / capacity);
            guiGraphics.blit(GUI_TEXTURE, this.leftPos + 99, this.topPos + 46 + 35 - height,
                    176, 34 + 35 - height, 10, height, ATLAS_WIDTH, ATLAS_HEIGHT);
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
