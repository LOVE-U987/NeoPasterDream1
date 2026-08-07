package com.pasterdream.pasterdreammod.api.effect.cutscene;

import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 过场动画内嵌屏幕特效时间轴 —— tick → 特效列表
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code CutsceneScreenEffectData}
 * 设计思路（独立实现，非复制）。在过场动画的指定 tick 触发屏幕特效
 * （与 {@code ScreenEffectPayload} 同用特效类型注册表）。
 */
public class CutsceneScreenEffectData {

    /** 时间轴：tick → 特效描述列表 */
    private final Map<Integer, List<TimelineEffect>> effects = new HashMap<>();

    /**
     * 在指定 tick 注册一个屏幕特效
     *
     * @param tick     触发 tick
     * @param typeId   特效类型注册 id（String）
     * @param dataNbt  特效数据序列化（NBT 字节数组，由数据 StreamCodec 编码）
     * @param inTime   渐入 tick 数
     * @param stayTime 持续 tick 数
     * @param outTime  渐出 tick 数
     */
    public void put(int tick, String typeId, byte[] dataNbt, int inTime, int stayTime, int outTime) {
        effects.computeIfAbsent(tick, k -> new ArrayList<>())
                .add(new TimelineEffect(typeId, dataNbt, inTime, stayTime, outTime));
    }

    /**
     * 获取指定 tick 的全部特效（无则空列表）
     *
     * @param tick 触发 tick
     * @return 特效描述列表
     */
    public List<TimelineEffect> get(int tick) {
        return effects.getOrDefault(tick, List.of());
    }

    /**
     * 时间轴是否为空
     *
     * @return 空返回 {@code true}
     */
    public boolean isEmpty() {
        return effects.isEmpty();
    }

    /**
     * 用另一份时间轴数据替换当前内容（用于反序列化后加载）
     *
     * @param other 来源时间轴
     */
    public void replaceWith(CutsceneScreenEffectData other) {
        this.effects.clear();
        this.effects.putAll(other.effects);
    }

    /**
     * 序列化到 NBT
     *
     * @return CompoundTag
     */
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("size", effects.size());
        int idx = 0;
        for (Map.Entry<Integer, List<TimelineEffect>> entry : effects.entrySet()) {
            CompoundTag tickTag = new CompoundTag();
            tickTag.putInt("tick", entry.getKey());
            tickTag.putInt("count", entry.getValue().size());
            for (int i = 0; i < entry.getValue().size(); i++) {
                TimelineEffect e = entry.getValue().get(i);
                CompoundTag eTag = new CompoundTag();
                eTag.putString("typeId", e.typeId());
                eTag.putByteArray("data", e.dataNbt());
                eTag.putInt("in", e.inTime());
                eTag.putInt("stay", e.stayTime());
                eTag.putInt("out", e.outTime());
                tickTag.put("e" + i, eTag);
            }
            tag.put("t" + idx++, tickTag);
        }
        return tag;
    }

    /**
     * 从 NBT 反序列化
     *
     * @param tag CompoundTag
     * @return 时间轴数据
     */
    public static CutsceneScreenEffectData fromTag(CompoundTag tag) {
        CutsceneScreenEffectData data = new CutsceneScreenEffectData();
        int size = tag.getInt("size");
        for (int idx = 0; idx < size; idx++) {
            CompoundTag tickTag = tag.getCompound("t" + idx);
            int tick = tickTag.getInt("tick");
            int count = tickTag.getInt("count");
            for (int i = 0; i < count; i++) {
                CompoundTag eTag = tickTag.getCompound("e" + i);
                data.put(tick, eTag.getString("typeId"), eTag.getByteArray("data"),
                        eTag.getInt("in"), eTag.getInt("stay"), eTag.getInt("out"));
            }
        }
        return data;
    }

    /**
     * 时间轴特效描述
     *
     * @param typeId   特效类型注册 id
     * @param dataNbt  特效数据序列化字节
     * @param inTime   渐入 tick 数
     * @param stayTime 持续 tick 数
     * @param outTime  渐出 tick 数
     */
    public record TimelineEffect(String typeId, byte[] dataNbt, int inTime, int stayTime, int outTime) {
    }
}
