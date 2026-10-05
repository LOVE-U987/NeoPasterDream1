package com.pasterdream.pasterdreammod.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/**
 * 纸面风格单行输入框：隐藏 vanilla 深色底框，使用深棕文字，叠于羊皮卷上。
 */
public class PaperEditBox extends EditBox {

    /**
     * 构造纸面风格输入框。
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
        this.setBordered(false);
        this.setTextColor(PaperStyle.TEXT_COLOR);
        this.setTextColorUneditable(PaperStyle.TEXT_COLOR);
    }
}
