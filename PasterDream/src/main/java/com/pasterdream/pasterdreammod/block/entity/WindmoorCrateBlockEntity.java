package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.menu.WindmoorCrateMenu;
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
 * 风泊木箱方块实体（windmoor_crate）
 * <p>
 * 15 格库存（5×3），实现 MenuProvider 供右键打开 GUI；
 * 另通过 {@link W4DataBlockEntity} 的数据键 new_loots 支持调试模式下的
 * 战利品刷新粒子提示（原 WindmoorCratePr0/Pr1）。
 */
public class WindmoorCrateBlockEntity extends W4DataBlockEntity implements MenuProvider {

    /** 15 格库存 */
    private final ItemStackHandler itemHandler = new ItemStackHandler(15) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /**
     * 构造风泊木箱方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public WindmoorCrateBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntitiesFurniture.WINDMOOR_CRATE.get(), pos, state);
    }

    /**
     * 获取库存处理器
     *
     * @return 15 格 ItemStackHandler
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
        return new WindmoorCrateMenu(id, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pasterdream.windmoor_crate");
    }
}
