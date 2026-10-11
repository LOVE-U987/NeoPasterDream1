package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.api.text.NoteLimits;
import com.pasterdream.pasterdreammod.client.gui.widget.PaperEditBox;
import com.pasterdream.pasterdreammod.client.gui.widget.PaperMultiLineEditBox;
import com.pasterdream.pasterdreammod.network.OpenNotePayload;
import com.pasterdream.pasterdreammod.network.SaveNotePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * 可编辑寻梦者笔记编辑屏幕（无工具栏）。
 * <p>
 * 共用羊皮卷底图；正文可滚动输入，标题可编辑；关闭时按当前客户端语言回写
 * {@link SaveNotePayload}。阅读态由 {@link DreamnoteScreen} 负责分页渲染。
 * 底图与控件位置由 {@link NoteGuiLayout} 统一缩放定位；正文框为纸面透明自绘控件，名称框为深色输入框。
 */
public class DreamseekerNotesScreen extends AbstractPaperScreen {

    /** 保存失败提示色（暗红）。 */
    private static final int NOTICE_COLOR = 0xFF9A302A;

    private final OpenNotePayload payload;
    private String language;
    private String notice = "";
    private PaperMultiLineEditBox bodyBox;
    private PaperEditBox nameBox;

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
        String body = zh ? this.payload.textZh() : this.payload.textEn();
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
        this.bodyBox.setValue(body);
        addRenderableWidget(this.bodyBox);
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
        if (!this.notice.isEmpty()) {
            guiGraphics.drawString(this.font, this.notice,
                    this.frame.bodyX(), this.frame.designH() - 14, NOTICE_COLOR, false);
        }
    }
}
