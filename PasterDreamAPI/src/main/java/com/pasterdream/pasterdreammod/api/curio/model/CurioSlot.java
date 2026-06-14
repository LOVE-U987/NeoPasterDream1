package com.pasterdream.pasterdreammod.api.curio.model;

/**
 * 饰品槽位枚举 —— 对应 Curios API 的标准槽位类型
 * <p>
 * 使用 {@link #getSlotId()} 获取 Curios API 识别的槽位标识字符串。
 * </p>
 *
 * <pre>{@code
 * CurioSlot.RING.getSlotId()  → "ring"
 * CurioSlot.NECKLACE.getSlotId() → "necklace"
 * }</pre>
 */
public enum CurioSlot {

    /** 头部（头饰、帽子、面具等） */
    HEAD("head"),
    /** 项链（项链、吊坠等） */
    NECKLACE("necklace"),
    /** 背部（披风、翅膀等） */
    BACK("back"),
    /** 身体（胸甲、躯干饰品等） */
    BODY("body"),
    /** 戒指（可装备多个） */
    RING("ring"),
    /** 腰带 */
    BELT("belt"),
    /** 护符（通用类饰品、勋章等） */
    CHARM("charm"),
    /** 手镯 */
    BRACELET("bracelet"),
    /** 手部（手套等） */
    HANDS("hands"),
    /** 通用饰品槽 */
    CURIO("curio");

    private final String slotId;

    /**
     * @param slotId Curios API 识别的槽位标识
     */
    CurioSlot(String slotId) {
        this.slotId = slotId;
    }

    /**
     * 获取 Curios API 识别的槽位标识字符串。
     *
     * @return 槽位标识，如 "ring"、"necklace"
     */
    public String getSlotId() {
        return slotId;
    }

    /**
     * 根据槽位标识字符串查找对应的枚举值。
     *
     * @param slotId 槽位标识字符串
     * @return 对应的 {@link CurioSlot} 枚举，未匹配时返回 {@link #CURIO}
     */
    public static CurioSlot fromSlotId(String slotId) {
        for (CurioSlot slot : values()) {
            if (slot.slotId.equals(slotId)) {
                return slot;
            }
        }
        return CURIO;
    }
}