package com.pasterdream.pasterdreammod.mixin;

import com.pasterdream.pasterdreammod.client.gui.widget.PDMultilineTextView;
import net.minecraft.client.gui.components.MultilineTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

/**
 * {@code MultilineTextField} 视图 Mixin —— 桥接 {@link PDMultilineTextView} 暴露内部行/光标/选区。
 * <p>
 * 原版 {@code getLineView(int)} 返回 {@code protected StringView}，跨包不可引用；改为 {@code @Shadow}
 * 私有的 {@code displayLines} 列表，元素经 {@link StringViewAccessor} 读取区间端点。
 * 光标与选区取自私有字段 {@code cursor}/{@code selectCursor}，与 {@code hasSelection()}
 * （{@code selectCursor != cursor}）、{@code getSelected()}（{@code [min, max]}）口径一致。
 */
@Mixin(MultilineTextField.class)
public abstract class MultilineTextFieldViewMixin implements PDMultilineTextView {

    @Shadow
    private List<StringViewAccessor> displayLines;

    @Shadow
    private int cursor;

    @Shadow
    private int selectCursor;

    @Shadow
    public abstract String value();

    @Override
    public int pdLineCount() {
        return this.displayLines.size();
    }

    @Override
    public int pdLineBegin(int index) {
        return ((StringViewAccessor) this.displayLines.get(index)).pasterdream$beginIndex();
    }

    @Override
    public int pdLineEnd(int index) {
        return ((StringViewAccessor) this.displayLines.get(index)).pasterdream$endIndex();
    }

    @Override
    public int pdCursor() {
        return this.cursor;
    }

    @Override
    public String pdValue() {
        return this.value();
    }

    @Override
    public boolean pdHasSelection() {
        return this.selectCursor != this.cursor;
    }

    @Override
    public int pdSelectionBegin() {
        return Math.min(this.selectCursor, this.cursor);
    }

    @Override
    public int pdSelectionEnd() {
        return Math.max(this.selectCursor, this.cursor);
    }
}
