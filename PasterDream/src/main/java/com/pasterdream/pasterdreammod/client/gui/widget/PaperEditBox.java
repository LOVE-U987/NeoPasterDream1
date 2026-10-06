package com.pasterdream.pasterdreammod.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * 名称输入框：整块深色底框（vanilla {@code EditBox} 底框）+ 浅色文字，字号与正文一致。
 * <p>
 * 深色底框上用深棕文字不可读，故文字改用 {@link PaperStyle#NAME_TEXT_COLOR} 浅色；字号沿用传入字体，
 * 与 {@link PaperMultiLineEditBox} 正文同为默认字体/行高。
 */
public class PaperEditBox extends EditBox {

    /**
     * 构造名称输入框。
     *
     * @param font    字体
     * @param x       左缘 X（逻辑坐标）
     * @param y       顶部 Y（逻辑坐标）
     * @param width   宽（逻辑坐标）
     * @param height  高（逻辑坐标）
     * @param message 提示文本
     */
    public PaperEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        this.setTextColor(PaperStyle.NAME_TEXT_COLOR);
        this.setTextColorUneditable(PaperStyle.NAME_TEXT_COLOR);
    }
}
