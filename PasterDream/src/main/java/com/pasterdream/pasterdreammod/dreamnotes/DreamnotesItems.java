package com.pasterdream.pasterdreammod.dreamnotes;

import com.pasterdream.pasterdreammod.item.DreamnotesItem;
import com.pasterdream.pasterdreammod.registry.items.PDItemsDreamnotes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 笔记物品按 id 索引（供 GUI If 判定与验证钩子使用）。
 */
public final class DreamnotesItems {

    private DreamnotesItems() {
    }

    @SuppressWarnings("unchecked")
    private static final List<DeferredItem<? extends Item>> ALL = List.of(
            PDItemsDreamnotes.DREAMNOTES_0,
            PDItemsDreamnotes.DREAMNOTES_1,
            PDItemsDreamnotes.DREAMNOTES_2,
            PDItemsDreamnotes.DREAMNOTES_3,
            PDItemsDreamnotes.DREAMNOTES_4,
            PDItemsDreamnotes.DREAMNOTES_5,
            PDItemsDreamnotes.DREAMNOTES_6,
            PDItemsDreamnotes.DREAMNOTES_7,
            PDItemsDreamnotes.DREAMNOTES_8,
            PDItemsDreamnotes.DREAMNOTES_9,
            PDItemsDreamnotes.DREAMNOTES_10,
            PDItemsDreamnotes.DREAMNOTES_11,
            PDItemsDreamnotes.DREAMNOTES_12,
            PDItemsDreamnotes.DREAMNOTES_13,
            PDItemsDreamnotes.DREAMNOTES_14
    );

    public static List<DeferredItem<? extends Item>> all() {
        return ALL;
    }

    @Nullable
    public static Item byId(int noteId) {
        if (noteId < 0 || noteId >= ALL.size()) {
            return null;
        }
        return ALL.get(noteId).get();
    }

    public static int count() {
        return ALL.size();
    }

    /** 从物品实例反查 noteId，非笔记返回 -1。 */
    public static int idOf(Item item) {
        if (item instanceof DreamnotesItem notes) {
            return notes.getNoteId();
        }
        for (int i = 0; i < ALL.size(); i++) {
            if (ALL.get(i).get() == item) {
                return i;
            }
        }
        return -1;
    }
}
