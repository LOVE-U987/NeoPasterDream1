package com.pasterdream.pasterdreammod.api.client.sky;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 天空盒注册表 —— 集中管理全部天空内容条目
 * <p>
 * 两条来源：
 * <ul>
 *   <li><b>代码注册</b>：{@link #register}，由模组在客户端初始化时调用</li>
 *   <li><b>数据条目</b>：{@link #replaceDataEntries}，由数据包重载监听器
 *       （{@code data/<namespace>/skyboxes/*.json}）填充，支持运行时重载</li>
 * </ul>
 * <p>
 * 渲染器通过 {@link #entries()} 获取合并后按 {@link SkyContent#priority()}
 * 升序排列的全部条目。
 */
public final class SkyboxRegistry {

    /** 代码注册的条目 */
    private static final List<SkyboxEntry> ENTRIES = new ArrayList<>();

    /** 数据包注册的条目（重载时整体替换） */
    private static final List<SkyboxEntry> DATA_ENTRIES = new ArrayList<>();

    private SkyboxRegistry() {
    }

    /**
     * 代码注册一条天空内容（默认淡入速度 0.08，权重 0）
     *
     * @param content   内容
     * @param condition 激活条件
     */
    public static void register(SkyContent content, SkyCondition condition) {
        register(content, condition, 0.08F);
    }

    /**
     * 代码注册一条天空内容（自定义淡入速度，权重 0）
     *
     * @param content   内容
     * @param condition 激活条件
     * @param fadeSpeed 淡入淡出速度
     */
    public static void register(SkyContent content, SkyCondition condition, float fadeSpeed) {
        ENTRIES.add(new SkyboxEntry(content, condition, fadeSpeed, 0));
        sort(ENTRIES);
    }

    /**
     * 整体替换数据条目（由数据包重载监听器调用）
     *
     * @param entries 新数据条目列表
     */
    public static void replaceDataEntries(List<SkyboxEntry> entries) {
        DATA_ENTRIES.clear();
        DATA_ENTRIES.addAll(entries);
        sort(DATA_ENTRIES);
    }

    /**
     * 获取合并后的全部条目（代码 + 数据），按优先级升序排列
     *
     * @return 只读条目列表
     */
    public static List<SkyboxEntry> entries() {
        List<SkyboxEntry> entries = new ArrayList<>(ENTRIES.size() + DATA_ENTRIES.size());
        entries.addAll(ENTRIES);
        entries.addAll(DATA_ENTRIES);
        sort(entries);
        return List.copyOf(entries);
    }

    /**
     * 按内容优先级升序排序
     *
     * @param entries 待排序列表
     */
    private static void sort(List<SkyboxEntry> entries) {
        entries.sort(Comparator.comparingInt(entry -> entry.content().priority()));
    }
}
