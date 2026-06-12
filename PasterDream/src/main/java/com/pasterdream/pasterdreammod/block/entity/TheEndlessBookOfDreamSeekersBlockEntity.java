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
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 寻梦者的永恒书卷方块实体 (The Endless Book of Dream Seekers Block Entity)
 * 1 格库存 + GeckoLib 动画 + GUI 菜单提供者
 * 简化版本：仅含物品展示槽，无导入功能
 */
public class TheEndlessBookOfDreamSeekersBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 1 格库存处理器
     * 用于展示单个物品
     */
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /**
     * 构造寻梦者的永恒书卷方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public TheEndlessBookOfDreamSeekersBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.THE_ENDLESS_BOOK_OF_DREAM_SEEKERS.get(), pos, state);
    }

    /**
     * 获取库存处理器
     *
     * @return ItemStackHandler 实例（1 格）
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    // ==================== GeckoLib 动画 ====================

    /**
     * 书卷动画谓词 - 持续循环播放动画
     */
    private PlayState predicate(AnimationState<TheEndlessBookOfDreamSeekersBlockEntity> state) {
        state.getController().setAnimation(RawAnimation.begin().thenLoop("0"));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
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
        return new com.pasterdream.pasterdreammod.menu.TheEndlessBookOfDreamSeekersMenu(id, inventory, this);
    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("container.pasterdream.the_endless_book_of_dream_seekers");
    }
}
