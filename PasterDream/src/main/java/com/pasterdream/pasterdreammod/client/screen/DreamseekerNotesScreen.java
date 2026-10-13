package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.api.client.text.NotePagination;
import com.pasterdream.pasterdreammod.api.text.NoteLimits;
import com.pasterdream.pasterdreammod.client.gui.widget.PaperEditBox;
import com.pasterdream.pasterdreammod.client.gui.widget.PaperMultiLineEditBox;
import com.pasterdream.pasterdreammod.client.gui.widget.PaperStyle;
import com.pasterdream.pasterdreammod.network.OpenNotePayload;
import com.pasterdream.pasterdreammod.network.SaveNotePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 * 可编辑寻梦者笔记编辑屏幕（无工具栏）。
 * <p>
 * 共用羊皮卷底图；正文按 18 行/120px 与阅读态共用 {@link NotePagination} 分页，但**正文仍为单个字符串**：
 * {@link PaperMultiLineEditBox} 持有完整正文，视图窗口按页对齐到光标所在页（模型 A 单正文自动重排）。
 * 输入使光标越过当前页末行时，重排产生下一页并自动跳转；翻页只调视图、不改写插入点。
 * 标题可编辑；关闭时按当前客户端语言回写 {@link SaveNotePayload}。
 */
public class DreamseekerNotesScreen extends AbstractPaperScreen {

    /** 保存失败提示色（暗红）。 */
    private static final int NOTICE_COLOR = 0xFF9A302A;

    private final OpenNotePayload payload;
    private String language;
    private String notice = "";
    private String body = "";
    private NotePagination.Layout layout;
    private int page;
    private boolean layoutDirty;
    private int lastCursor;
    private PaperMultiLineEditBox bodyBox;
    private PaperEditBox nameBox;
    private Button prevButton;
    private Button nextButton;

    /**
     * 构造编辑屏幕。
     *
     * @param payload 打开包（可编辑）
     */
    public DreamseekerNotesScreen(OpenNotePayload payload) {
        super(Component.empty());
        this.payload = payload;
    }

    /** 当前客户端语言对应的数据语言键。 */
    private static String currentLanguage() {
        String code = Minecraft.getInstance().getLanguageManager().getSelected();
        return code != null && code.toLowerCase(Locale.ROOT).startsWith("zh") ? "zh_cn" : "en_us";
    }

    @Override
    protected void initWidgets() {
        this.language = currentLanguage();
        boolean zh = "zh_cn".equals(this.language);
        this.body = zh ? this.payload.textZh() : this.payload.textEn();
        String name = zh ? this.payload.nameZh() : this.payload.nameEn();

        this.nameBox = new PaperEditBox(this.font, this.frame.nameX(), this.frame.nameY(),
                this.frame.nameW(), this.frame.nameH(),
                Component.translatable("gui.pasterdream.dreamseeker_notes.name"));
        this.nameBox.setMaxLength(128);
        this.nameBox.setValue(name);
        addRenderableWidget(this.nameBox);

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
        addRenderableWidget(this.prevButton);
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
        if (this.bodyBox == null || this.nameBox == null) {
            super.onClose();
            return;
        }
        String body = this.bodyBox.getValue();
        // 与服务端一致：按 UTF-8 字节上限校验，超限保留界面并提示，避免静默丢失
        if (body.getBytes(StandardCharsets.UTF_8).length > NoteLimits.MAX_BODY_BYTES) {
            this.notice = Component.translatable("gui.pasterdream.dreamseeker_notes.too_long").getString();
            return;
        }
        PacketDistributor.sendToServer(new SaveNotePayload(body, this.nameBox.getValue(), this.language));
        super.onClose();
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 保存失败提示占用页码位（居中，与左右按钮横向不交叠）；无提示时才画页码。
        if (!this.notice.isEmpty()) {
            guiGraphics.drawString(this.font, this.notice,
                    (this.frame.designW() - this.font.width(this.notice)) / 2, this.frame.footerY(),
                    NOTICE_COLOR, false);
            return;
        }
        List<NotePagination.Page> pages = this.layout.pages();
        if (pages.isEmpty()) {
            return;
        }
        String pageText = (this.page + 1) + "/" + pages.size();
        guiGraphics.drawString(this.font, pageText,
                (this.frame.designW() - this.font.width(pageText)) / 2, this.frame.footerY(), PaperStyle.TEXT_COLOR, false);
    }
}
