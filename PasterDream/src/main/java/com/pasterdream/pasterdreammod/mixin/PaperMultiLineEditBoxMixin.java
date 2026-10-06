package com.pasterdream.pasterdreammod.mixin;

import com.pasterdream.pasterdreammod.client.gui.widget.PaperMultiLineEditBox;
import com.pasterdream.pasterdreammod.client.gui.widget.PaperStyle;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * MultiLineEditBox 正文渲染混合注入 —— 仅纸面输入框去投影阴影并套用纸面正文色
 * <p>
 * 原版 {@code renderContents} 硬编码 5 参数 {@code drawString}（默认 {@code dropShadow = true}）
 * 与浅色正文色，子类无法改写；此处 {@code @Redirect} 覆盖其正文文本绘制。
 * <p>
 * {@code @Redirect} 作用于全部 {@link MultiLineEditBox} 实例，而原版举报界面
 * （如 {@code AbstractReportScreen}/{@code NameReportScreen}/{@code SkinReportScreen} 的评论框）
 * 也使用该类；因此按实例分流：仅 {@link PaperMultiLineEditBox} 走纸面分支，
 * 其余保持原参数委托原方法，避免污染原版深色输入框。
 */
@Mixin(MultiLineEditBox.class)
public class PaperMultiLineEditBoxMixin {

    /**
     * 正文文本绘制分流：纸面框去阴影 + 深棕字色，其余维持原样
     *
     * @param guiGraphics 绘制上下文（被调用方）
     * @param font        字体
     * @param text        文本
     * @param x           左缘 X
     * @param y           顶部 Y
     * @param color       原字色
     * @return 绘制结果
     */
    @Redirect(method = "renderContents", require = 1,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I"))
    private int pasterdream$paperText(GuiGraphics guiGraphics, Font font, String text,
                                      int x, int y, int color) {
        if (!PaperMultiLineEditBox.class.isInstance(this)) {
            return guiGraphics.drawString(font, text, x, y, color, true);
        }
        return guiGraphics.drawString(font, text, x, y, PaperStyle.TEXT_COLOR, false);
    }
}
