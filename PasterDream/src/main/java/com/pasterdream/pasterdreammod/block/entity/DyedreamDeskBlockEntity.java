package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * 染梦书桌方块实体 (Dyedream Desk Block Entity)
 * 1 格库存（最大堆叠 1），可放置任何物品
 * 实现 MenuProvider 以打开 GUI 菜单
 */
public class DyedreamDeskBlockEntity extends BlockEntity implements MenuProvider {

    /**
     * 1 格库存处理器
     * 最大堆叠数设为 1（与原模组一致，书桌只能放一件展示品）
     */
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, net.minecraft.world.item.ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /**
     * 构造染梦书桌方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public DyedreamDeskBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.DYEDREAM_DESK.get(), pos, state);
    }

    /**
     * 获取库存处理器
     *
     * @return ItemStackHandler 实例（1 格，最大堆叠 1）
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    // ==================== 库存持久化 ====================

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        }
    }

    // ==================== 客户端同步 ====================

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // ==================== GUI 菜单提供者 ====================

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new com.pasterdream.pasterdreammod.menu.DyedreamDeskMenu(id, inventory, this);
    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("container.pasterdream.dyedream_desk");
    }
}
