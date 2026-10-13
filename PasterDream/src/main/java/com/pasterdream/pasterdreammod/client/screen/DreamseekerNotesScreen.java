package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.api.client.text.NotePagination;
import com.pasterdream.pasterdreammod.api.text.NoteLimits;
import com.pasterdream.pasterdreammod.client.PDClientNoteScreens;
import com.pasterdream.pasterdreammod.client.gui.widget.PaperMultiLineEditBox;
import com.pasterdream.pasterdreammod.client.gui.widget.PaperStyle;
import com.pasterdream.pasterdreammod.network.OpenNotePayload;
import com.pasterdream.pasterdreammod.network.SaveNotePayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 玩家笔记编辑屏幕（无工具栏、无顶部标题）。
 * <p>
 * 共用羊皮卷底图；正文按每页 16 行（0.8f 底图布局需为底部按钮/落款留白，18 行放不下）与阅读态
 * 共用 {@link NotePagination} 分页，正文仍为单个字符串：
 * {@link PaperMultiLineEditBox} 持有完整正文，视图窗口按页对齐到光标所在页（模型 A 单正文自动重排）。
 * 底部为 {@code < 完成 署名 >}：完成保存后关闭，署名打开独立署名子界面。
 * 页码绘制于原顶部标题行位置。
 */
public class DreamseekerNotesScreen extends AbstractPaperScreen {

    /** 保存失败提示色（暗红）。 */
    private static final int NOTICE_COLOR = 0xFF9A302A;

    private final OpenNotePayload payload;
    private String language;
    private String notice = "";
    private String body = "";
    private boolean loaded;
    private NotePagination.Layout layout;
    private int page;
    private boolean layoutDirty;
    private int lastCursor;
    private PaperMultiLineEditBox bodyBox;
    private Button prevButton;
    private Button nextButton;

    /**
     * 构造编辑屏幕。
     *
     * @param payload 打开包（玩家笔记）
     */
    public DreamseekerNotesScreen(OpenNotePayload payload) {
        super(Component.empty());
        this.payload = payload;
    }

    @Override
    protected void initWidgets() {
        this.language = PDClientNoteScreens.currentLanguage();
        if (!this.loaded) {
            boolean zh = "zh_cn".equals(this.language);
            this.body = zh ? this.payload.textZh() : this.payload.textEn();
            this.loaded = true;
        }

        this.bodyBox = new PaperMultiLineEditBox(this.font, this.frame.bodyBoxX(), this.frame.bodyBoxY(),
                this.frame.bodyBoxW(), this.frame.bodyBoxH(),
                Component.empty(), Component.translatable("gui.pasterdream.dreamseeker_notes.body"));
        // 先赋值、再注册监听，避免初始化回调重入。
        this.bodyBox.setValue(this.body);
        this.layout = NotePagination.layout(this.body, this.font.getSplitter());
        this.bodyBox.setValueListener(this::onBodyChanged);
        addRenderableWidget(this.bodyBox);

        this.prevButton = Button.builder(Component.literal("<"), button -> turn(-1))
                .bounds(this.frame.prevButtonX(), this.frame.buttonY(), this.frame.buttonW(), this.frame.buttonH())
                .build();
        this.nextButton = Button.builder(Component.literal(">"), button -> turn(1))
                .bounds(this.frame.nextButtonX(), this.frame.buttonY(), this.frame.buttonW(), this.frame.buttonH())
                .build();
        Button doneButton = Button.builder(Component.translatable("gui.pasterdream.dreamseeker_notes.done"),
                        button -> onClose())
                .bounds(this.frame.doneButtonX(), this.frame.buttonY(), this.frame.midButtonW(), this.frame.buttonH())
                .build();
        Button signButton = Button.builder(Component.translatable("gui.pasterdream.dreamseeker_notes.sign"),
                        button -> openSign())
                .bounds(this.frame.signButtonX(), this.frame.buttonY(), this.frame.midButtonW(), this.frame.buttonH())
                .build();
        addRenderableWidget(this.prevButton);
        addRenderableWidget(doneButton);
        addRenderableWidget(signButton);
        addRenderableWidget(this.nextButton);

        // setValue 将光标置于文末：初始页与光标所在页一致，避免首次按键跳页。
        this.lastCursor = this.bodyBox.textView().pdCursor();
        this.page = this.layout.pageAt(this.lastCursor);
        this.bodyBox.setViewToChar(this.layout.pages().get(this.page).start());
        updateButtons();
    }

    /** 正文变更：只置脏，分页在下一帧合并重算。 */
    private void onBodyChanged(String value) {
        this.body = value;
        this.layoutDirty = true;
    }

    /** 打开独立署名子界面（携带当前正文；取消时返回本编辑器）。 */
    private void openSign() {
        if (this.minecraft == null || this.bodyBox == null) {
            return;
        }
        this.minecraft.setScreen(new DreamseekerNoteSignScreen(this, this.bodyBox.getValue(), this.language));
    }

    @Override
    protected void beforeRender() {
        if (this.layoutDirty) {
            this.layout = NotePagination.layout(this.body, this.font.getSplitter());
            this.layoutDirty = false;
        }
        int cursor = this.bodyBox.textView().pdCursor();
        if (cursor != this.lastCursor) {
            this.lastCursor = cursor;
            this.page = this.layout.pageAt(cursor);
            updateButtons();
        }
        if (this.page >= this.layout.pages().size()) {
            this.page = this.layout.pages().size() - 1;
        }
        // 每帧把视图顶部对齐到当前页页首：输入越界自动建页并跳转即由此实现。
        this.bodyBox.setViewToChar(this.layout.pages().get(this.page).start());
    }

    /** 翻页：只调视图，不改写插入点。 */
    private void turn(int delta) {
        int target = this.page + delta;
        if (target < 0 || target >= this.layout.pages().size()) {
            return;
        }
        this.page = target;
        this.bodyBox.setViewToChar(this.layout.pages().get(target).start());
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN, 0.5f, 1.0f);
        }
        updateButtons();
    }

    /** 依据当前页刷新翻页按钮可用态。 */
    private void updateButtons() {
        int last = this.layout.pages().size() - 1;
        this.prevButton.active = this.page > 0;
        this.nextButton.active = this.page < last;
    }

    @Override
    protected boolean onPaperScrolled(int logicalMouseX, int logicalMouseY, double scrollX, double scrollY) {
        if (scrollY != 0) {
            turn(scrollY > 0 ? -1 : 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 先于正文框消费 PageUp/PageDown，避免被 MultilineTextField 当作光标移动。
        if (keyCode == GLFW.GLFW_KEY_PAGE_UP) {
            turn(-1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_PAGE_DOWN) {
            turn(1);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        if (this.bodyBox == null) {
            super.onClose();
            return;
        }
        String body = this.bodyBox.getValue();
        // 与服务端一致：按 UTF-8 字节上限校验，超限保留界面并提示，避免静默丢失
        if (body.getBytes(StandardCharsets.UTF_8).length > NoteLimits.MAX_BODY_BYTES) {
            this.notice = Component.translatable("gui.pasterdream.dreamseeker_notes.too_long").getString();
            return;
        }
        PacketDistributor.sendToServer(new SaveNotePayload(body, "", "", this.language, false));
        super.onClose();
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 保存失败提示与页码共用顶部标题行位置（提示优先）。
        String text = !this.notice.isEmpty() ? this.notice : pageText();
        if (text.isEmpty()) {
            return;
        }
        int color = !this.notice.isEmpty() ? NOTICE_COLOR : PaperStyle.TEXT_COLOR;
        guiGraphics.drawString(this.font, text,
                (this.frame.designW() - this.font.width(text)) / 2, this.frame.titleY(), color, false);
    }

    /** 当前页码文本（无页时为空）。 */
    private String pageText() {
        List<NotePagination.Page> pages = this.layout.pages();
        return pages.isEmpty() ? "" : (this.page + 1) + "/" + pages.size();
    }
}
