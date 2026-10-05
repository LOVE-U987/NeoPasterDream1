package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.api.client.text.NotePagination;
import com.pasterdream.pasterdreammod.api.text.NoteText;
import com.pasterdream.pasterdreammod.client.gui.widget.PaperStyle;
import com.pasterdream.pasterdreammod.dreamnotes.NoteDefinition;
import com.pasterdream.pasterdreammod.dreamnotes.PDNoteRegistry;
import com.pasterdream.pasterdreammod.network.OpenNotePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Locale;

/**
 * 寻梦者笔记阅读屏幕（固定叙事笔记与可编辑笔记共用）。
 * <p>
 * 共用羊皮卷底图，正文经 Markdown 渲染并按 18 行/120px 自动分页；无工具栏。
 * 固定笔记从同步注册表按 noteId 解析正文/标题，可编辑笔记直接使用打开包中的双语内容。
 * 底图与文字位置均由 {@link NoteGuiLayout} 统一缩放定位。
 */
public class DreamnoteScreen extends AbstractPaperScreen {

    /** 正文文字色（深棕，取自纸面共享配色）。 */
    private static final int TEXT_COLOR = PaperStyle.TEXT_COLOR;

    private final OpenNotePayload payload;
    private String body = "";
    private String title = "";
    private NotePagination.Layout layout;
    private int page;
    private Button prevButton;
    private Button nextButton;

    /**
     * 构造笔记屏幕。
     *
     * @param payload 打开包
     */
    public DreamnoteScreen(OpenNotePayload payload) {
        super(Component.empty());
        this.payload = payload;
    }

    @Override
    protected void initWidgets() {
        resolveContent(currentLanguage());
        try {
            this.layout = NotePagination.layout(this.body, this.font.getSplitter());
        } catch (IllegalArgumentException exception) {
            this.layout = new NotePagination.Layout(this.body,
                    List.of(new NotePagination.Page(0, this.body.length(), this.body.length())));
        }
        this.page = 0;
        this.prevButton = Button.builder(Component.literal("<"), button -> turn(-1))
                .bounds(this.frame.prevButtonX(), this.frame.buttonY(), this.frame.buttonW(), this.frame.buttonH())
                .build();
        this.nextButton = Button.builder(Component.literal(">"), button -> turn(1))
                .bounds(this.frame.nextButtonX(), this.frame.buttonY(), this.frame.buttonW(), this.frame.buttonH())
                .build();
        addRenderableWidget(this.prevButton);
        addRenderableWidget(this.nextButton);
        updateButtons();
    }

    /** 当前客户端语言对应的数据语言键。 */
    private static String currentLanguage() {
        String code = Minecraft.getInstance().getLanguageManager().getSelected();
        return code != null && code.toLowerCase(Locale.ROOT).startsWith("zh") ? "zh_cn" : "en_us";
    }

    /** 解析正文与标题：固定笔记读注册表，可编辑笔记读包内双语字段。 */
    private void resolveContent(String language) {
        if (this.payload.noteId() >= 0) {
            NoteDefinition definition = PDNoteRegistry.get(this.minecraft == null ? null : this.minecraft.level,
                    PDNoteRegistry.definitionId(this.payload.noteId()));
            if (definition != null) {
                this.title = definition.titleOr(language);
                this.body = definition.bodyOr(language);
            }
        } else {
            boolean zh = "zh_cn".equals(language);
            this.title = zh ? this.payload.nameZh() : this.payload.nameEn();
            this.body = zh ? this.payload.textZh() : this.payload.textEn();
        }
    }

    /** 翻页。 */
    private void turn(int delta) {
        int target = this.page + delta;
        if (target < 0 || target >= this.layout.pages().size()) {
            return;
        }
        this.page = target;
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
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawString(this.font, this.title,
                (this.frame.designW() - this.font.width(this.title)) / 2, this.frame.titleY(), TEXT_COLOR, false);
        if (!this.layout.pages().isEmpty()) {
            NotePagination.Page current = this.layout.pages().get(this.page);
            guiGraphics.drawWordWrap(this.font, NoteText.renderRange(this.body, current.start(), current.visibleEnd()),
                    this.frame.bodyX(), this.frame.bodyY(), this.frame.bodyW(), TEXT_COLOR);
        }
        String pageText = (this.page + 1) + "/" + this.layout.pages().size();
        guiGraphics.drawString(this.font, pageText,
                (this.frame.designW() - this.font.width(pageText)) / 2, this.frame.footerY(), TEXT_COLOR, false);
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
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            turn(-1);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            turn(1);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
