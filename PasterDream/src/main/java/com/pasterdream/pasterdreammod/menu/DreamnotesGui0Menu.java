package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.dreamnotes.DreamnotesLogic;
import com.pasterdream.pasterdreammod.registry.PDMenusDreamnotes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 寻梦者笔记阅读 GUI 菜单 (dreamnotes_gui_0)
 * <p>
 * 原版 0 槽容器：仅用于打开客户端屏幕并播放翻书音效。
 * 额外写入 noteId，避免手持切换时页内容错位（向后兼容仅 pos+hand 的缓冲）。
 */
public class DreamnotesGui0Menu extends AbstractContainerMenu {

    public final Level world;
    public final Player entity;
    public final int x;
    public final int y;
    public final int z;
    /** 打开菜单时的笔记 id（0..14），-1 表示未知（回退到手持判定） */
    public final int noteId;

    public DreamnotesGui0Menu(int id, Inventory inv, FriendlyByteBuf extraData) {
        // Defense: spectator's vanilla single-arg openMenu sends an empty buffer (readableBytes == 0),
        // readBlockPos()/readByte() would throw IndexOutOfBoundsException → connection lost.
        this(id, inv,
                extraData != null && extraData.readableBytes() >= 3 ? extraData.readBlockPos() : inv.player.blockPosition(),
                extraData != null && extraData.readableBytes() >= 1 ? extraData.readByte() : (byte) 0,
                extraData != null && extraData.isReadable() ? extraData.readVarInt() : -1);
    }

    public DreamnotesGui0Menu(int id, Inventory inv, BlockPos pos, byte hand, int noteId) {
        super(PDMenusDreamnotes.DREAMNOTES_GUI_0.get(), id);
        this.entity = inv.player;
        this.world = inv.player.level();
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.noteId = noteId;
        // hand 字节保留与原版缓冲布局兼容，菜单侧无需使用
        DreamnotesLogic.playPageTurn(world, x, y, z);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        DreamnotesLogic.playPageTurn(world, x, y, z);
    }
}
