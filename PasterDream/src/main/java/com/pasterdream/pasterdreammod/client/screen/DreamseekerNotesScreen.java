package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.text.NoteLimits;
import com.pasterdream.pasterdreammod.network.OpenNotePayload;
import com.pasterdream.pasterdreammod.network.SaveNotePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * 可编辑寻梦者笔记编辑屏幕（无工具栏）。
 * <p>
 * 共用羊皮卷底图；正文可滚动输入，标题可编辑；关闭时按当前客户端语言回写
 * {@link SaveNotePayload}。阅读态由 {@link DreamnoteScreen} 负责分页渲染。
 */
public class DreamseekerNotesScreen extends Screen {

    private static final ResourceLocation PAPER = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "textures/screens/dreamnote_paper.png");
    private static final int PAPER_W = 200;
    private static final int PAPER_H = 264;
    private static final int PAPER_TEX_W = 775;
    private static final int PAPER_TEX_H = 1024;
    private static final int BODY_X = 40;
    private static final int BODY_Y = 48;
    private static final int BODY_W = NoteLimits.WIDTH + 8;

    private final OpenNotePayload payload;
    private String language;
    private String notice = "";
    private int x0;
    private int y0;
    private MultiLineEditBox bodyBox;
    private EditBox nameBox;

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
    protected void init() {
        super.init();
        this.x0 = (this.width - PAPER_W) / 2;
        this.y0 = (this.height - PAPER_H) / 2;
        this.language = currentLanguage();
        boolean zh = "zh_cn".equals(this.language);
        String body = zh ? this.payload.textZh() : this.payload.textEn();
        String name = zh ? this.payload.nameZh() : this.payload.nameEn();

        this.nameBox = new EditBox(this.font, this.x0 + BODY_X, this.y0 + 22, NoteLimits.WIDTH, 16,
                Component.translatable("gui.pasterdream.dreamseeker_notes.name"));
        this.nameBox.setMaxLength(128);
        this.nameBox.setValue(name);
        addRenderableWidget(this.nameBox);

        this.bodyBox = new MultiLineEditBox(this.font, this.x0 + BODY_X, this.y0 + BODY_Y, BODY_W, 162,
                Component.empty(), Component.translatable("gui.pasterdream.dreamseeker_notes.body"));
        this.bodyBox.setCharacterLimit(NoteLimits.MAX_BODY_BYTES);
        this.bodyBox.setValue(body);
        addRenderableWidget(this.bodyBox);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (!this.notice.isEmpty()) {
            guiGraphics.drawString(this.font, this.notice,
                    this.x0 + 8, this.y0 + PAPER_H - 14, 0xFF9A302A, false);
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(PAPER, this.x0, this.y0, PAPER_W, PAPER_H, 0.0f, 0.0f,
                PAPER_TEX_W, PAPER_TEX_H, PAPER_TEX_W, PAPER_TEX_H);
    }
}
