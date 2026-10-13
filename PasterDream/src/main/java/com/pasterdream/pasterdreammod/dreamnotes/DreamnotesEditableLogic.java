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
 * 玩家笔记的服务端保存语义。
 * <p>
 * 校验发送者主手为 {@link DreamseekerNotesItem}、语言白名单与字节上限后，写入物品
 * {@code CUSTOM_DATA} 的**当前语言键**（禁止跨语言写入）。署名（{@code signed=true}）时
 * 标题 trim 后为空则丢弃该次署名；标题/作者超长截断。
 */
public final class DreamnotesEditableLogic {

    /** 标题字符上限。 */
    private static final int MAX_NAME_CHARS = 128;
    /** 作者字符上限。 */
    private static final int MAX_AUTHOR_CHARS = 64;

    private DreamnotesEditableLogic() {
    }

    /**
     * 保存当前语言字段（正文；署名时含标题/作者/署名标记）。
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
        boolean zh = "zh_cn".equals(language);
        String name = payload.name() == null ? "" : payload.name();
        String author = payload.author() == null ? "" : payload.author();
        boolean signed = payload.signed() && !name.trim().isEmpty();
        if (name.length() > MAX_NAME_CHARS) {
            name = name.substring(0, MAX_NAME_CHARS);
        }
        if (author.length() > MAX_AUTHOR_CHARS) {
            author = author.substring(0, MAX_AUTHOR_CHARS);
        }
        final String savedBody = body;
        final String savedName = name;
        final String savedAuthor = author;
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putString(zh ? DreamseekerNotesItem.KEY_TEXT : DreamseekerNotesItem.KEY_TEXT_EN, savedBody);
            if (signed) {
                tag.putString(zh ? DreamseekerNotesItem.KEY_NAME : DreamseekerNotesItem.KEY_NAME_EN, savedName);
                tag.putString(zh ? DreamseekerNotesItem.KEY_AUTHOR : DreamseekerNotesItem.KEY_AUTHOR_EN, savedAuthor);
                tag.putBoolean(zh ? DreamseekerNotesItem.KEY_SIGNED : DreamseekerNotesItem.KEY_SIGNED_EN, true);
            }
        });
        // 同步物品 NBT 到客户端，使署名后的 tooltip 立即可见。
        player.containerMenu.broadcastChanges();
    }
}
