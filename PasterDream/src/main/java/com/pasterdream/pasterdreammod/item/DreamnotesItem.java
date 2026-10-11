package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.dreamnotes.DreamnotesLogic;
import com.pasterdream.pasterdreammod.network.OpenNotePayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * 寻梦者笔记 (dreamnotes_0..14)
 * <p>
 * 统一数据驱动实现：stacksTo(1)、防火、合成保留自身、右键向客户端下发
 * {@link OpenNotePayload}（客户端从同步注册表解析正文并按 18 行/120px 分页渲染），
 * 并按 noteId 触发原版 Pr0 成就/坐标逻辑；notes_8/9 选中时显示背面坐标。
 */
public class DreamnotesItem extends Item {

    /** 笔记序号 0..14，对应定义 ID dreamnotes_N 与 GUI 页内容。 */
    private final int noteId;
    private final List<String> tooltipKeys;

    /**
     * @param noteId      0..14
     * @param tooltipKeys 悬停描述的语言键列表
     */
    public DreamnotesItem(int noteId, List<String> tooltipKeys) {
        super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.COMMON));
        this.noteId = noteId;
        this.tooltipKeys = List.copyOf(tooltipKeys);
    }

    public int getNoteId() {
        return noteId;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        return itemStack.copyWithCount(1);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        for (String key : tooltipKeys) {
            tooltipComponents.add(Component.translatable(key));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, OpenNotePayload.fixed(noteId));
            DreamnotesLogic.onUse(noteId, level, player, stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        // 原版 notes_8 / notes_9 选中时显示坐标
        if (isSelected && (noteId == 8 || noteId == 9)) {
            DreamnotesLogic.tickSelectedCoords(entity, stack);
        }
    }
}
