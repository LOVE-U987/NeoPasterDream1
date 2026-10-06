package com.pasterdream.pasterdreammod.mixin;

import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * {@code MultiLineEditBox} 访问器 Mixin —— 暴露原版 private 的 {@code textField}。
 * <p>
 * {@code PaperMultiLineEditBox} 在保留 IMBlocker 识别（基类仍为 {@code MultiLineEditBox}）的同时，
 * 需读取内部 {@code MultilineTextField} 以自绘正文；该字段为 private 且无访问器，故经本接口桥接，
 * 再转为 {@code PDMultilineTextView} 使用。
 */
@Mixin(MultiLineEditBox.class)
public interface MultiLineEditBoxAccessor {

    /**
     * 取得内部文本状态。
     *
     * @return 原版 {@code MultilineTextField}
     */
    @Accessor("textField")
    MultilineTextField pasterdream$getTextField();
}
