package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.menu.ShadowDeskMenu;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * 影之桌方块实体（shadow_desk）
 * <p>
 * 1 格展示库存（与原版 ShadowDeskGuiMenu 的 ItemStackHandler(1) 一致），
 * 实现 MenuProvider 供右键打开 GUI。
 */
public class ShadowDeskBlockEntity extends W4DataBlockEntity implements MenuProvider {

    /** 1 格库存 */
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /**
     * 构造影之桌方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public ShadowDeskBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntitiesFurniture.SHADOW_DESK.get(), pos, state);
    }

    /**
     * 获取库存处理器
     *
     * @return 1 格 ItemStackHandler
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

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

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ShadowDeskMenu(id, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pasterdream.shadow_desk");
    }
}
