package com.pasterdream.pasterdreammod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * {@code MultilineTextField.StringView} 访问器 Mixin —— 以 {@code int} 暴露行区间端点。
 * <p>
 * {@code StringView} 为 {@code protected} 嵌套 record，跨包无法引用其类型；
 * 通过 {@code targets} 字符串定位并以 {@code @Accessor} 读取 record 组件，供
 * {@link MultilineTextFieldViewMixin} 桥接为 {@code PDMultilineTextView}。
 */
@Mixin(targets = "net.minecraft.client.gui.components.MultilineTextField$StringView")
public interface StringViewAccessor {

    /**
     * 行起始字符下标（含）。
     *
     * @return 起始下标
     */
    @Accessor("beginIndex")
    int pasterdream$beginIndex();

    /**
     * 行结束字符下标（不含）。
     *
     * @return 结束下标
     */
    @Accessor("endIndex")
    int pasterdream$endIndex();
}
