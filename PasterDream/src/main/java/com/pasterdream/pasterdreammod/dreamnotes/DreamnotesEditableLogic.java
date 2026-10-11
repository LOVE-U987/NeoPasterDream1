package com.pasterdream.pasterdreammod.dreamnotes;

import com.pasterdream.pasterdreammod.api.text.NoteLimits;
import com.pasterdream.pasterdreammod.item.DreamseekerNotesItem;
import com.pasterdream.pasterdreammod.network.SaveNotePayload;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.nio.charset.StandardCharsets;

/**
 * 可编辑笔记的服务端保存语义。
 * <p>
 * 校验发送者主手为 {@link DreamseekerNotesItem}、语言白名单与字节上限后，写入物品
 * {@code CUSTOM_DATA} 对应语言字段。
 */
public final class DreamnotesEditableLogic {

    /** 标题字符上限。 */
    private static final int MAX_NAME_CHARS = 128;

    private DreamnotesEditableLogic() {
    }

    /**
     * 保存可编辑笔记当前语言字段。
     *
     * @param player  发送者
     * @param payload 保存包
     */
    public static void save(Player player, SaveNotePayload payload) {
        if (player == null || payload == null) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof DreamseekerNotesItem)) {
            return;
        }
        String language = payload.languageId();
        if (!"zh_cn".equals(language) && !"en_us".equals(language)) {
            return;
        }
        String body = payload.body() == null ? "" : payload.body();
        if (body.getBytes(StandardCharsets.UTF_8).length > NoteLimits.MAX_BODY_BYTES) {
            return;
        }
        String name = payload.name() == null ? "" : payload.name();
        if (name.length() > MAX_NAME_CHARS) {
            name = name.substring(0, MAX_NAME_CHARS);
        }
        boolean zh = "zh_cn".equals(language);
        final String savedName = name;
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putString(zh ? DreamseekerNotesItem.KEY_TEXT : DreamseekerNotesItem.KEY_TEXT_EN, body);
            tag.putString(zh ? DreamseekerNotesItem.KEY_NAME : DreamseekerNotesItem.KEY_NAME_EN, savedName);
        });
    }
}
