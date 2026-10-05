package com.pasterdream.pasterdreammod.api.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

/**
 * 笔记正文的 Markdown 子集解析：转换为原版 {@link Component}/{@link Style} 渲染。
 * <p>
 * 支持斜体 {@code *x*}、删除线 {@code --x--}、下划线 {@code __x__}（可相互嵌套组合）、
 * 命名色 {@code <name>…</name>} 与真彩 {@code <#RRGGBB>…</#RRGGBB>}；{@code \} 转义；
 * 未闭合标记按字面输出；{@code §} 传统格式码原样透传由原版管线解析。
 * <p>
 * 状态在一页内持续，换行不会清除样式。本类仅依赖 common 类型，不含任何客户端引用。
 */
public final class NoteText {

    /** 颜色/闭合标签体的最大长度（含 {@code #RRGGBB} 与命名色）。 */
    private static final int MAX_TAG_LENGTH = 12;

    private NoteText() {
    }

    /** 解析结果片段：保留来源范围，分页时不能从标记或代理字符中间切开。 */
    public record Token(int start, int end, String text, Style style) {
    }

    /**
     * 解析整段 Markdown 正文为可渲染组件。
     *
     * @param source Markdown 原文
     * @return 渲染组件
     */
    public static Component render(String source) {
        return components(tokens(source));
    }

    /**
     * 按来源范围渲染（显示页继承源文前方状态，但不把补偿标记写回源文）。
     *
     * @param source 完整原文
     * @param from   起始字符下标（含）
     * @param to     结束字符下标（不含）
     * @return 该范围的渲染组件
     */
    public static Component renderRange(String source, int from, int to) {
        return renderRange(tokens(source), from, to);
    }

    /**
     * 按来源范围渲染（复用已解析 token）。
     *
     * @param tokens 已解析的 token 列表
     * @param from   起始字符下标（含）
     * @param to     结束字符下标（不含）
     * @return 该范围的渲染组件
     */
    public static Component renderRange(List<Token> tokens, int from, int to) {
        return components(tokens.stream().filter(t -> t.start() >= from && t.end() <= to).toList());
    }

    /**
     * 原码编辑的虚拟前缀：只含零宽的原版格式码，用于续页/局部渲染补齐样式。
     *
     * @param source 完整原文
     * @param until  截止字符下标（不含）
     * @return 生效中的原版格式码前缀（如 {@code §l§o}）
     */
    public static String legacyPrefix(String source, int until) {
        State state = new State();
        for (int i = 0; i + 1 < Math.min(until, source.length()); i++) {
            if (source.charAt(i) != '§') {
                continue;
            }
            ChatFormatting format = ChatFormatting.getByCode(source.charAt(++i));
            if (format == ChatFormatting.RESET) {
                state.legacy = Style.EMPTY;
            } else if (format != null) {
                state.legacy = state.legacy.applyLegacyFormat(format);
            }
        }
        return state.prefix();
    }

    /**
     * 将原文逐字符解析为带样式的 token（保留来源范围）。
     *
     * @param source Markdown 原文
     * @return token 列表
     */
    public static List<Token> tokens(String source) {
        List<Token> result = new ArrayList<>();
        State state = new State();
        for (int i = 0; i < source.length(); ) {
            int end = consumeMarker(source, i, state);
            if (end != i) {
                result.add(new Token(i, end, "", state.style()));
            } else if (source.charAt(i) == '\\' && i + 1 < source.length() && isMarker(source.charAt(i + 1))) {
                end = i + 2;
                result.add(new Token(i, end, source.substring(i + 1, end), state.style()));
            } else {
                end = i + Character.charCount(source.codePointAt(i));
                result.add(new Token(i, end, source.substring(i, end), state.style()));
            }
            i = end;
        }
        return result;
    }

    /**
     * 自动续页只补当前状态前缀；普通翻页不调用此方法。
     *
     * @param source 完整原文
     * @param until  截止字符下标（不含）
     * @return 未闭合样式前缀（Markdown 标记 + 活动 {@code §} 代码）
     */
    public static String continuationPrefix(String source, int until) {
        State state = new State();
        for (int i = 0; i < Math.min(until, source.length()); ) {
            int end = consumeMarker(source, i, state);
            if (end != i) {
                i = end;
            } else if (source.charAt(i) == '\\' && i + 1 < source.length() && isMarker(source.charAt(i + 1))) {
                i += 2;
            } else {
                i += Character.charCount(source.codePointAt(i));
            }
        }
        return state.prefix();
    }

    /**
     * 将提议的切分点左移到最近的 token 边界，避免从标记/代理字符中间切开。
     *
     * @param source   完整原文
     * @param proposed 提议字符下标
     * @return 安全边界（不超过 proposed）
     */
    public static int safeBoundary(String source, int proposed) {
        int boundary = 0;
        for (Token token : tokens(source)) {
            if (token.end() > proposed) {
                break;
            }
            boundary = token.end();
        }
        return boundary;
    }

    /**
     * 编辑态保留 Markdown 源码，只把传统格式码转换为显式 Style。
     *
     * @param source 完整原文
     * @param from   起始字符下标（含）
     * @param to     结束字符下标（不含）
     * @return 该范围的渲染组件
     */
    public static Component renderLegacyRange(String source, int from, int to) {
        List<Token> result = new ArrayList<>();
        Style style = Style.EMPTY;
        for (int i = 0; i < Math.min(to, source.length()); ) {
            if (source.charAt(i) == '§' && i + 1 < source.length()) {
                ChatFormatting format = ChatFormatting.getByCode(source.charAt(i + 1));
                if (format != null) {
                    style = format == ChatFormatting.RESET ? Style.EMPTY : style.applyLegacyFormat(format);
                }
                i += 2;
            } else {
                int end = i + Character.charCount(source.codePointAt(i));
                if (i >= from && end <= to) {
                    result.add(new Token(i, end, source.substring(i, end), style));
                }
                i = end;
            }
        }
        return components(result);
    }

    /** 将相邻同一样式的 token 合并为最小数量的字面量组件。 */
    private static Component components(List<Token> tokens) {
        MutableComponent root = Component.empty();
        StringBuilder buffer = new StringBuilder();
        Style current = Style.EMPTY;
        for (Token token : tokens) {
            if (token.text().isEmpty()) {
                continue;
            }
            if (!current.equals(token.style())) {
                if (!buffer.isEmpty()) {
                    root.append(Component.literal(buffer.toString()).setStyle(current));
                }
                buffer.setLength(0);
                current = token.style();
            }
            buffer.append(token.text());
        }
        if (!buffer.isEmpty()) {
            root.append(Component.literal(buffer.toString()).setStyle(current));
        }
        return root;
    }

    /**
     * 消费位于 {@code i} 的标记或格式码，更新状态。
     *
     * @return 消费结束下标；{@code i} 表示未消费（普通字符）
     */
    private static int consumeMarker(String source, int i, State state) {
        char c = source.charAt(i);
        if (c == '§' && i + 1 < source.length()) {
            ChatFormatting format = ChatFormatting.getByCode(source.charAt(i + 1));
            if (format == ChatFormatting.RESET) {
                state.reset();
            } else if (format != null) {
                state.legacy = state.legacy.applyLegacyFormat(format);
            }
            return i + 2;
        }
        if (c == '*') {
            state.italic = !state.italic;
            return i + 1;
        }
        if (i + 1 < source.length() && source.startsWith("--", i)) {
            state.strike = !state.strike;
            return i + 2;
        }
        if (i + 1 < source.length() && source.startsWith("__", i)) {
            state.underline = !state.underline;
            return i + 2;
        }
        if (c == '<') {
            int end = source.indexOf('>', i + 1);
            if (end < 0 || end - i > MAX_TAG_LENGTH) {
                return i;
            }
            String body = source.substring(i + 1, end).trim();
            if (body.startsWith("/")) {
                String name = body.substring(1).trim();
                if (name.isEmpty() || color(name) != null) {
                    if (!state.colors.isEmpty()) {
                        state.colors.removeLast();
                    }
                    return end + 1;
                }
            } else {
                Integer color = color(body);
                if (color != null) {
                    state.colors.addLast(color);
                    return end + 1;
                }
            }
        }
        return i;
    }

    /** 命名色或 {@code #RRGGBB} 真彩；未知返回 null。 */
    private static Integer color(String name) {
        if (name.matches("#[0-9a-fA-F]{6}")) {
            return Integer.parseInt(name.substring(1), 16);
        }
        ChatFormatting format = ChatFormatting.getByName(name.toLowerCase(Locale.ROOT));
        return format == null ? null : format.getColor();
    }

    /** 可被 {@code \} 转义的标记字符。 */
    private static boolean isMarker(char c) {
        return c == '*' || c == '-' || c == '_' || c == '\\' || c == '<';
    }

    /** 行内样式状态机。 */
    private static final class State {
        private Style legacy = Style.EMPTY;
        private final Deque<Integer> colors = new ArrayDeque<>();
        private boolean italic;
        private boolean strike;
        private boolean underline;

        private Style style() {
            Style result = legacy.withItalic(italic || legacy.isItalic())
                    .withStrikethrough(strike || legacy.isStrikethrough())
                    .withUnderlined(underline || legacy.isUnderlined());
            return colors.isEmpty() ? result : result.withColor(colors.peekLast());
        }

        private void reset() {
            legacy = Style.EMPTY;
            colors.clear();
            italic = strike = underline = false;
        }

        /** 生成续页/局部渲染所需的未闭合样式前缀。 */
        private String prefix() {
            StringBuilder out = new StringBuilder();
            if (legacy.getColor() != null) {
                for (ChatFormatting format : ChatFormatting.values()) {
                    if (format.getColor() != null && format.getColor().intValue() == legacy.getColor().getValue()) {
                        out.append(format);
                        break;
                    }
                }
            }
            if (legacy.isObfuscated()) {
                out.append("§k");
            }
            if (legacy.isBold()) {
                out.append("§l");
            }
            if (legacy.isStrikethrough()) {
                out.append("§m");
            }
            if (legacy.isUnderlined()) {
                out.append("§n");
            }
            if (legacy.isItalic()) {
                out.append("§o");
            }
            if (italic) {
                out.append('*');
            }
            if (strike) {
                out.append("--");
            }
            if (underline) {
                out.append("__");
            }
            for (int color : colors) {
                out.append(String.format(Locale.ROOT, "<#%06X>", color));
            }
            return out.toString();
        }
    }
}
