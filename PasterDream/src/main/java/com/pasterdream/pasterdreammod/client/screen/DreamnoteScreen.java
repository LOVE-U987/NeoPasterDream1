package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.client.text.NotePagination;
import com.pasterdream.pasterdreammod.api.text.NoteText;
import com.pasterdream.pasterdreammod.dreamnotes.NoteDefinition;
import com.pasterdream.pasterdreammod.dreamnotes.PDNoteRegistry;
import com.pasterdream.pasterdreammod.network.OpenNotePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Locale;

/**
 * 寻梦者笔记阅读屏幕（固定叙事笔记与可编辑笔记共用）。
 * <p>
 * 共用羊皮卷底图，正文经 Markdown 渲染并按 18 行/120px 自动分页；无工具栏。
 * 固定笔记从同步注册表按 noteId 解析正文/标题，可编辑笔记直接使用打开包中的双语内容。
 */
public class DreamnoteScreen extends Screen {

    /** 羊皮卷底图。 */
    private static final ResourceLocation PAPER = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "textures/screens/dreamnote_paper.png");
    /** 底图绘制宽。 */
    private static final int PAPER_W = 200;
    /** 底图绘制高（775:1024 等比缩放）。 */
    private static final int PAPER_H = 264;
    /** 底图原始宽。 */
    private static final int PAPER_TEX_W = 775;
    /** 底图原始高。 */
    private static final int PAPER_TEX_H = 1024;

    private static final int BODY_X = 40;
    private static final int BODY_Y = 48;
    private static final int BODY_W = NotePagination.WIDTH;
    private static final int TEXT_COLOR = 0xFF2B2118;

    private final OpenNotePayload payload;
    private String body = "";
    private String title = "";
    private NotePagination.Layout layout;
    private int page;
    private int x0;
    private int y0;
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
    protected void init() {
        super.init();
        this.x0 = (this.width - PAPER_W) / 2;
        this.y0 = (this.height - PAPER_H) / 2;
        resolveContent(currentLanguage());
        try {
            this.layout = NotePagination.layout(this.body, this.font.getSplitter());
        } catch (IllegalArgumentException exception) {
            this.layout = new NotePagination.Layout(this.body,
                    List.of(new NotePagination.Page(0, this.body.length(), this.body.length())));
        }
        this.page = 0;
        this.prevButton = Button.builder(Component.literal("<"), button -> turn(-1))
                .bounds(this.x0 + 24, this.y0 + 232, 20, 18).build();
        this.nextButton = Button.builder(Component.literal(">"), button -> turn(1))
                .bounds(this.x0 + PAPER_W - 44, this.y0 + 232, 20, 18).build();
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
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(PAPER, this.x0, this.y0, PAPER_W, PAPER_H, 0.0f, 0.0f,
                PAPER_TEX_W, PAPER_TEX_H, PAPER_TEX_W, PAPER_TEX_H);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, this.title,
                this.x0 + (PAPER_W - this.font.width(this.title)) / 2, this.y0 + 22, TEXT_COLOR, false);
        if (!this.layout.pages().isEmpty()) {
            NotePagination.Page current = this.layout.pages().get(this.page);
            guiGraphics.drawWordWrap(this.font, NoteText.renderRange(this.body, current.start(), current.visibleEnd()),
                    this.x0 + BODY_X, this.y0 + BODY_Y, BODY_W, TEXT_COLOR);
        }
        String pageText = (this.page + 1) + "/" + this.layout.pages().size();
        guiGraphics.drawString(this.font, pageText,
                this.x0 + (PAPER_W - this.font.width(pageText)) / 2, this.y0 + 239, TEXT_COLOR, false);
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY != 0) {
            turn(scrollY > 0 ? -1 : 1);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }
}
