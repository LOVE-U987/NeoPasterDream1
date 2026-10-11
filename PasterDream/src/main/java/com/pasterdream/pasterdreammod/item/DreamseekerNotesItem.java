package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.network.OpenNotePayload;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 可编辑寻梦者笔记 (dreamseeker_notes)
 * <p>
 * 玩家可写双语笔记：正文/标题按语言存于 {@code CUSTOM_DATA}
 * （{@code text}/{@code text_en} 与 {@code name}/{@code name_en}）。
 * 右键向客户端下发 {@link OpenNotePayload}（可编辑），客户端按当前语言进入编辑器。
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

    public DreamseekerNotesItem() {
        super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.COMMON));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            PacketDistributor.sendToPlayer(serverPlayer, OpenNotePayload.editable(
                    tag.getString(KEY_TEXT),
                    tag.getString(KEY_TEXT_EN),
                    tag.getString(KEY_NAME),
                    tag.getString(KEY_NAME_EN)));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
