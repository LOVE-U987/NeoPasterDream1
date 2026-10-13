package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.client.gui.widget.PaperEditBox;
import com.pasterdream.pasterdreammod.network.SaveNotePayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 玩家笔记署名子界面（仿原版 {@code BookSignScreen}）。
 * <p>
 * 独立于编辑器的羊皮卷屏幕：上方标题输入框、下方作者输入框（预填玩家名，可改），底部
 * {@code 署名 / 取消}。确认后按当前语言发送 {@link SaveNotePayload}（{@code signed=true}）并关闭回游戏；
 * 取消则返回原编辑器（正文由编辑器 {@code loaded} 状态保留）。
 */
public class DreamseekerNoteSignScreen extends AbstractPaperScreen {

    /** 提示色（暗红）。 */
    private static final int NOTICE_COLOR = 0xFF9A302A;
    /** 标题字符上限。 */
    private static final int MAX_TITLE_CHARS = 128;
    /** 作者字符上限。 */
    private static final int MAX_AUTHOR_CHARS = 64;

    private final DreamseekerNotesScreen editor;
    private final String body;
    private final String language;
    private String notice = "";
    private PaperEditBox titleBox;
    private PaperEditBox authorBox;

    /**
     * 构造署名子界面。
     *
     * @param editor   发起署名、取消时返回的编辑器
     * @param body     待定稿的当前语言正文
     * @param language 当前语言键（{@code zh_cn}/{@code en_us}）
     */
    public DreamseekerNoteSignScreen(DreamseekerNotesScreen editor, String body, String language) {
        super(Component.empty());
        this.editor = editor;
        this.body = body;
        this.language = language;
    }

    @Override
    protected void initWidgets() {
        this.titleBox = new PaperEditBox(this.font, this.frame.nameX(), this.frame.nameY(),
                this.frame.nameW(), this.frame.nameH(),
                Component.translatable("gui.pasterdream.dreamseeker_notes.sign_title"));
        this.titleBox.setMaxLength(MAX_TITLE_CHARS);
        addRenderableWidget(this.titleBox);

        this.authorBox = new PaperEditBox(this.font, this.frame.nameX(), this.frame.bodyY(),
                this.frame.nameW(), this.frame.nameH(),
                Component.translatable("gui.pasterdream.dreamseeker_notes.sign_author"));
        this.authorBox.setMaxLength(MAX_AUTHOR_CHARS);
        if (this.minecraft != null && this.minecraft.player != null) {
            this.authorBox.setValue(this.minecraft.player.getName().getString());
        }
        addRenderableWidget(this.authorBox);

        Button signButton = Button.builder(Component.translatable("gui.pasterdream.dreamseeker_notes.sign"),
                        button -> sign())
                .bounds(this.frame.signButtonX(), this.frame.buttonY(), this.frame.midButtonW(), this.frame.buttonH())
                .build();
        Button cancelButton = Button.builder(Component.translatable("gui.cancel"), button -> cancel())
                .bounds(this.frame.doneButtonX(), this.frame.buttonY(), this.frame.midButtonW(), this.frame.buttonH())
                .build();
        addRenderableWidget(signButton);
        addRenderableWidget(cancelButton);
    }

    /** 确认署名：标题非空校验后发送定稿包并关闭回游戏。 */
    private void sign() {
        String title = this.titleBox.getValue().trim();
        if (title.isEmpty()) {
            this.notice = Component.translatable("gui.pasterdream.dreamseeker_notes.sign_empty_title").getString();
            return;
        }
        PacketDistributor.sendToServer(new SaveNotePayload(this.body, title,
                this.authorBox.getValue().trim(), this.language, true));
        if (this.minecraft != null) {
            this.minecraft.setScreen(null);
        }
    }

    /** 取消：返回原编辑器。 */
    private void cancel() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.editor);
        }
    }

    @Override
    public void onClose() {
        // Esc 等同取消，返回编辑器。
        cancel();
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!this.notice.isEmpty()) {
            guiGraphics.drawString(this.font, this.notice,
                    (this.frame.designW() - this.font.width(this.notice)) / 2, this.frame.titleY(),
                    NOTICE_COLOR, false);
        }
    }
}
