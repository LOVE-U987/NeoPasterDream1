package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.dreamnotes.NoteLangBridge;
import com.pasterdream.pasterdreammod.network.OpenNotePayload;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * 可编辑寻梦者笔记 (dreamseeker_notes)
 * <p>
 * 玩家可写双语笔记：正文/标题/作者/署名标记按语言存于 {@code CUSTOM_DATA}
 * （{@code text}/{@code text_en}、{@code name}/{@code name_en}、{@code author}/{@code author_en}、
 * {@code signed}/{@code signed_en}）。右键向客户端下发 {@link OpenNotePayload}（玩家笔记），
 * 客户端按当前语言与署名状态进入阅读或编辑界面。
 */
public class DreamseekerNotesItem extends Item {

    /** 中文正文字段。 */
    public static final String KEY_TEXT = "text";
    /** 英文正文字段。 */
    public static final String KEY_TEXT_EN = "text_en";
    /** 中文标题字段。 */
    public static final String KEY_NAME = "name";
    /** 英文标题字段。 */
    public static final String KEY_NAME_EN = "name_en";
    /** 中文作者字段。 */
    public static final String KEY_AUTHOR = "author";
    /** 英文作者字段。 */
    public static final String KEY_AUTHOR_EN = "author_en";
    /** 中文署名标记。 */
    public static final String KEY_SIGNED = "signed";
    /** 英文署名标记。 */
    public static final String KEY_SIGNED_EN = "signed_en";

    public DreamseekerNotesItem() {
        super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.COMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            PacketDistributor.sendToPlayer(serverPlayer, OpenNotePayload.player(
                    tag.getString(KEY_TEXT),
                    tag.getString(KEY_TEXT_EN),
                    tag.getString(KEY_AUTHOR),
                    tag.getString(KEY_AUTHOR_EN),
                    tag.getBoolean(KEY_SIGNED),
                    tag.getBoolean(KEY_SIGNED_EN)));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
                                TooltipFlag tooltipFlag) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        boolean zh = "zh_cn".equals(NoteLangBridge.current());
        if (tag.getBoolean(zh ? KEY_SIGNED : KEY_SIGNED_EN)) {
            String title = tag.getString(zh ? KEY_NAME : KEY_NAME_EN);
            String author = tag.getString(zh ? KEY_AUTHOR : KEY_AUTHOR_EN);
            if (!title.isEmpty()) {
                tooltipComponents.add(Component.literal(title));
            }
            if (!author.isEmpty()) {
                tooltipComponents.add(Component.translatable(
                        "gui.pasterdream.dreamseeker_notes.by_author", author));
            }
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
