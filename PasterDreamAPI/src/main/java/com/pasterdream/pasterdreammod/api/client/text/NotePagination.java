package com.pasterdream.pasterdreammod.api.client.text;

import com.google.gson.Gson;
import com.pasterdream.pasterdreammod.api.text.NoteLimits;
import com.pasterdream.pasterdreammod.api.text.NoteText;
import net.minecraft.client.StringSplitter;
import net.minecraft.network.chat.Style;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 笔记正文的固定行数自动分页（客户端）。
 * <p>
 * 连续源文与显示页分离：编辑器、固定叙事笔记与可编辑笔记共用同一分页结果。
 * 折行宽度 {@link #WIDTH}=120、每页 {@link #LINES}=18 可视行（PD2 为 19，此处有意少 1 行）。
 * 软承接页保存时无分隔合并回上一元素，存储格式不变。
 * <p>
 * 依赖客户端 {@link StringSplitter}，仅客户端可用。
 */
@OnlyIn(Dist.CLIENT)
public final class NotePagination {

    /** 折行宽度（像素，与编辑框文本宽一致）。 */
    public static final int WIDTH = NoteLimits.WIDTH;
    /** 每页可视行数。 */
    public static final int LINES = NoteLimits.LINES;
    /** 保存时的最大页数。 */
    public static final int MAX_PAGES = NoteLimits.MAX_PAGES;
    /** 正文（JSON 字符串）最大字节数。 */
    public static final int MAX_JSON_BYTES = NoteLimits.MAX_BODY_BYTES;

    private static final int MAX_READ_PAGES = 2048;
    private static final Gson GSON = new Gson();

    private NotePagination() {
    }

    /**
     * 显示页边界。
     * <p>
     * {@code end} 包含页边界的换行，{@code visibleEnd} 排除该换行；源文字符始终完整保留。
     *
     * @param start      起始字符下标（含）
     * @param end        结束字符下标（不含，含边界换行）
     * @param visibleEnd 可见结束下标（不含，排除边界换行）
     */
    public record Page(int start, int end, int visibleEnd) {
    }

    /**
     * 分页结果。
     *
     * @param body  完整正文
     * @param pages 显示页列表
     */
    public record Layout(String body, List<Page> pages) {
        public Layout {
            pages = List.copyOf(pages);
        }

        /**
         * 光标字符下标所在的页序号。
         *
         * @param cursor 光标下标
         * @return 页序号（0-based）
         */
        public int pageAt(int cursor) {
            for (int i = 0; i < pages.size() - 1; i++) {
                if (cursor < pages.get(i).end()) {
                    return i;
                }
            }
            return pages.size() - 1;
        }
    }

    /**
     * 旧数组的页边界作为换行读取；新笔记保存为只有一个正文元素的数组。
     *
     * @param pages 存储页数组
     * @return 归一化换行后的完整正文
     */
    public static String joinStored(List<String> pages) {
        return String.join("\n", pages).replace("\r\n", "\n").replace('\r', '\n');
    }

    /**
     * 校验正文容量上限（字节）。
     *
     * @param body 完整正文
     * @throws IllegalArgumentException 超出 {@link #MAX_JSON_BYTES}
     */
    public static void validateBody(String body) {
        if (body.length() > MAX_JSON_BYTES
                || GSON.toJson(List.of(body)).getBytes(StandardCharsets.UTF_8).length > MAX_JSON_BYTES) {
            throw new IllegalArgumentException("笔记总容量已满，本次修改未应用");
        }
    }

    /**
     * 编辑态分页：校验容量与页数后返回布局。
     *
     * @param body     完整正文
     * @param splitter 字体折行器
     * @return 分页布局
     * @throws IllegalArgumentException 超容量或超页数
     */
    public static Layout edited(String body, StringSplitter splitter) {
        validateBody(body);
        Layout result = layout(body, splitter);
        if (result.pages().size() > MAX_PAGES) {
            throw new IllegalArgumentException("笔记最多支持 " + MAX_PAGES + " 页，本次修改未应用");
        }
        return result;
    }

    /**
     * 只读态分页（不校验页数上限，仅受读取页数硬上限保护）。
     *
     * @param body     完整正文
     * @param splitter 字体折行器
     * @return 分页布局
     */
    public static Layout layout(String body, StringSplitter splitter) {
        List<NoteText.Token> tokens = NoteText.tokens(body);
        List<Page> pages = new ArrayList<>();
        int start = 0;
        while (true) {
            String prefix = NoteText.legacyPrefix(body, start);
            String remaining = prefix + body.substring(start);
            List<Integer> ends = new ArrayList<>();
            splitter.splitLines(remaining, WIDTH, Style.EMPTY, true, (style, begin, end) -> ends.add(end));
            int rawCount = ends.size() + (body.isEmpty() || remaining.endsWith("\n") ? 1 : 0);
            int visibleCount = splitter.splitLines(NoteText.renderRange(tokens, start, body.length()), WIDTH, Style.EMPTY).size();
            if (rawCount <= LINES && visibleCount <= LINES) {
                pages.add(new Page(start, body.length(), body.length()));
                return new Layout(body, pages);
            }
            int end = ends.size() >= LINES ? start + ends.get(LINES - 1) - prefix.length() : body.length();
            end = boundary(tokens, end);
            int visibleEnd;
            while (true) {
                if (end <= start) {
                    throw new IllegalArgumentException("此处格式标记无法分页，请缩短标记或调整换行");
                }
                visibleEnd = body.charAt(end - 1) == '\n' ? end - 1 : end;
                if (fits(body, tokens, start, visibleEnd, splitter)) {
                    break;
                }
                end = boundary(tokens, end - 1);
            }
            pages.add(new Page(start, end, visibleEnd));
            if (pages.size() >= MAX_READ_PAGES) {
                throw new IllegalArgumentException("笔记显示页数超过读取上限");
            }
            start = end;
        }
    }

    /**
     * 判断单页是否仍在行数上限内。
     *
     * @param body     完整正文
     * @param page     页边界
     * @param splitter 字体折行器
     * @return true 表示放得下
     */
    public static boolean fits(String body, Page page, StringSplitter splitter) {
        return fits(body, NoteText.tokens(body), page.start(), page.visibleEnd(), splitter);
    }

    private static boolean fits(String body, List<NoteText.Token> tokens, int start, int end,
                                StringSplitter splitter) {
        String raw = NoteText.legacyPrefix(body, start) + body.substring(start, end);
        int rawCount = splitter.splitLines(raw, WIDTH, Style.EMPTY).size();
        if (raw.isEmpty() || raw.endsWith("\n")) {
            rawCount++;
        }
        return rawCount <= LINES
                && splitter.splitLines(NoteText.renderRange(tokens, start, end), WIDTH, Style.EMPTY).size() <= LINES;
    }

    /** 提议切分点左移到最近的 token 边界（不越过起点）。 */
    private static int boundary(List<NoteText.Token> tokens, int proposed) {
        int low = 0;
        int high = tokens.size();
        while (low < high) {
            int middle = (low + high) >>> 1;
            if (tokens.get(middle).end() <= proposed) {
                low = middle + 1;
            } else {
                high = middle;
            }
        }
        return low == 0 ? 0 : tokens.get(low - 1).end();
    }
}
