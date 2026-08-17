package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.api.menu.SimpleContainerMenu;
import com.pasterdream.pasterdreammod.block.entity.ShadowDeskBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDMenusFurniture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/**
 * 影之桌 GUI 容器菜单（shadow_desk）
 * <p>
 * 与原版 ShadowDeskGuiMenu 布局一致：
 * 1 格展示槽 (79,26) + 玩家背包（y=84 起）+ 快捷栏（y=142）。
 */
public class ShadowDeskMenu extends SimpleContainerMenu {

    private final BlockEntity blockEntity;

    /**
     * 网络工厂构造（客户端）
     *
     * @param id        容器 ID
     * @param inv       玩家背包
     * @param extraData 附加数据（方块坐标）
     */
    public ShadowDeskMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        // Defense: spectator's vanilla single-arg openMenu sends an empty buffer (readableBytes == 0),
        // readBlockPos() would throw IndexOutOfBoundsException → connection lost.
        this(id, inv, extraData != null && extraData.readableBytes() >= 8
                ? inv.player.level().getBlockEntity(extraData.readBlockPos()) : null);
    }

    /**
     * 服务端构造
     *
     * @param id          容器 ID
     * @param inv         玩家背包
     * @param blockEntity 影之桌方块实体
     */
    public ShadowDeskMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenusFurniture.SHADOW_DESK.get(), id, 1);
        this.blockEntity = blockEntity;
        bindBlockEntity(blockEntity);

        IItemHandler handler = blockEntity instanceof ShadowDeskBlockEntity desk
                ? desk.getItemHandler() : new ItemStackHandler(1);

        this.addSlot(new SlotItemHandler(handler, 0, 79, 26));
        addPlayerInventory(inv, 84);
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
