package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.dreamnotes.DreamnotesLogic;
import com.pasterdream.pasterdreammod.menu.DreamnotesGui0Menu;
import com.pasterdream.pasterdreammod.registry.PDMenusDreamnotes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 寻梦者笔记 (dreamnotes_0..14)
 * <p>
 * 统一实现：stacksTo(1)、防火、合成保留自身、右键打开 {@link DreamnotesGui0Menu}，
 * 并按 noteId 触发原版 Pr0 成就/坐标逻辑；notes_8/9 选中时显示背面坐标。
 * <p>
 * 原版 MCreator 库存 Capability 对应 0 槽 GUI，无实际容器用途，1.21 移植省略。
 */
public class DreamnotesItem extends Item {

    /** 笔记序号 0..14，对应 GUI 页纹理 xun_meng_zhe_bi_ji__gui{N} */
    private final int noteId;
    private final List<Component> tooltips;

    /**
     * @param noteId   0..14
     * @param tooltips 悬停描述行（已含格式码）
     */
    public DreamnotesItem(int noteId, List<String> tooltips) {
        super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.COMMON));
        this.noteId = noteId;
        this.tooltips = tooltips.stream().map(s -> (Component) Component.literal(s)).toList();
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
        tooltipComponents.addAll(tooltips);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal("Dreamnotes " + noteId);
                }

                @Override
                public AbstractContainerMenu createMenu(int id, Inventory inventory, Player p) {
                    return new DreamnotesGui0Menu(id, inventory, p.blockPosition(),
                            hand == InteractionHand.MAIN_HAND ? (byte) 0 : (byte) 1, noteId);
                }
            }, buf -> {
                buf.writeBlockPos(player.blockPosition());
                buf.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
                buf.writeVarInt(noteId);
            });
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
