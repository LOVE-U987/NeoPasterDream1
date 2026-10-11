package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * 羊皮卷风格屏幕基类：统一处理底图缩放、控件定位与输入坐标反变换。
 * <p>
 * 坐标体系：控件一律使用 {@link NoteGuiLayout.Frame} 的**逻辑坐标**添加与渲染；
 * 底图、控件、文本在同一个 {@code PoseStack} 缩放坐标系内绘制，屏幕尺寸变化时由
 * {@code Screen.init() -> initWidgets()} 重算布局。
 * <p>
 * 渲染骨架不调用 {@code super.render()}：因为本版本 {@code Screen.render} 自身会先调用
 * {@code renderBackground} 再渲染 {@code renderables}，若在缩放 pose 内调用会重复且缩放暗化，
 * 故此处显式调用一次 {@code renderBackground}（屏幕坐标），再手动在缩放 pose 内渲染控件。
 */
public abstract class AbstractPaperScreen extends Screen {

    /** 共用羊皮卷底图。 */
    protected static final ResourceLocation PAPER = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "textures/screens/dreamnote_paper.png");

    /** 当前布局（逻辑坐标系）。 */
    protected NoteGuiLayout.Frame frame;

    /**
     * 构造屏幕。
     *
     * @param title 屏幕标题
     */
    protected AbstractPaperScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        this.frame = NoteGuiLayout.compute(this.width, this.height);
        initWidgets();
    }

    /** 子类按 {@link #frame} 的逻辑坐标添加控件。 */
    protected abstract void initWidgets();

    /** 子类在缩放坐标系内绘制文本（鼠标坐标为逻辑坐标）。 */
    protected abstract void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);

    /**
     * 绘制共用底图（基类统一实现，子类无需覆写）。
     *
     * @param guiGraphics 绘制上下文
     */
    protected void drawPaper(GuiGraphics guiGraphics) {
        guiGraphics.blit(PAPER, 0, 0, frame.designW(), frame.designH(), 0.0f, 0.0f,
                NoteGuiLayout.TEX_W, NoteGuiLayout.TEX_H, NoteGuiLayout.TEX_W, NoteGuiLayout.TEX_H);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(frame.originX(), frame.originY(), 0);
        guiGraphics.pose().scale(frame.scale(), frame.scale(), 1.0f);
        drawPaper(guiGraphics);
        int logicalMouseX = frame.toLogicalX(mouseX);
        int logicalMouseY = frame.toLogicalY(mouseY);
        for (Renderable renderable : this.renderables) {
            renderable.render(guiGraphics, logicalMouseX, logicalMouseY, partialTick);
        }
        renderContent(guiGraphics, logicalMouseX, logicalMouseY, partialTick);
        guiGraphics.pose().popPose();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(frame.toLogicalX(mouseX), frame.toLogicalY(mouseY), button);
    }

    @Override
    public final boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(frame.toLogicalX(mouseX), frame.toLogicalY(mouseY), button);
    }

    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(frame.toLogicalX(mouseX), frame.toLogicalY(mouseY), button,
                dragX / frame.scale(), dragY / frame.scale());
    }

    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int logicalMouseX = frame.toLogicalX(mouseX);
        int logicalMouseY = frame.toLogicalY(mouseY);
        if (onPaperScrolled(logicalMouseX, logicalMouseY, scrollX, scrollY)) {
            return true;
        }
        return super.mouseScrolled(logicalMouseX, logicalMouseY, scrollX, scrollY);
    }

    @Override
    public final void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(frame.toLogicalX(mouseX), frame.toLogicalY(mouseY));
    }

    /**
     * 滚轮钩子（逻辑坐标）。默认不消费，交回控件处理（如正文编辑框滚动）。
     *
     * @param logicalMouseX 逻辑鼠标 X
     * @param logicalMouseY 逻辑鼠标 Y
     * @param scrollX       横向滚动量
     * @param scrollY       纵向滚动量
     * @return true 表示已消费
     */
    protected boolean onPaperScrolled(int logicalMouseX, int logicalMouseY, double scrollX, double scrollY) {
        return false;
    }
}
