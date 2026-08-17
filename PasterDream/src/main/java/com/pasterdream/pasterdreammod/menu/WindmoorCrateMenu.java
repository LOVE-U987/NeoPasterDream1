package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.api.menu.SimpleContainerMenu;
import com.pasterdream.pasterdreammod.block.entity.WindmoorCrateBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDMenusFurniture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * 风泊木箱 GUI 容器菜单（windmoor_crate）
 * <p>
 * 与原版 WindmoorCrateGuiMenu 布局一致：
 * 15 格（5×3，x=43..115，y=23/41/59）+ 玩家区下移 30 像素。
 */
public class WindmoorCrateMenu extends SimpleContainerMenu {

    private final BlockEntity blockEntity;

    /**
     * 网络工厂构造（客户端）
     *
     * @param id        容器 ID
     * @param inv       玩家背包
     * @param extraData 附加数据（方块坐标）
     */
    public WindmoorCrateMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
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
     * @param blockEntity 风泊木箱方块实体
     */
    public WindmoorCrateMenu(int id, Inventory inv, BlockEntity blockEntity) {
        super(PDMenusFurniture.WINDMOOR_CRATE.get(), id, 15);
        this.blockEntity = blockEntity;
        bindBlockEntity(blockEntity);

        IItemHandler handler = blockEntity instanceof WindmoorCrateBlockEntity crate
                ? crate.getItemHandler() : new ItemStackHandler(15);

        addContainerGrid(handler, 5, 3, 43, 23);
        addPlayerInventory(inv, 114);
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }
}
