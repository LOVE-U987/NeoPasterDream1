package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
 * 梦境炼药锅方块实体 (Dream Cauldron Block Entity)
 * 集成 GeckoLib 动画 + 4 格库存（3 输入 + 1 输出）+ GUI 菜单提供者
 *
 * 槽位说明：
 * - 索引 0-2：输入槽（炼药材料）
 * - 索引 3：输出槽（成品法术书）
 *
 * 动画说明：
 * - animation = 0：空闲循环
 * - animation = 1：搅拌/炼药动画（触发式）
 */
public class DreamCauldronBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 4 格库存处理器（3 输入 + 1 输出） */
    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /**
     * 构造梦境炼药锅方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public DreamCauldronBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.DREAM_CAULDRON.get(), pos, state);
    }

    /**
     * 获取库存处理器
     *
     * @return ItemStackHandler 实例（4 格：0-2 输入，3 输出）
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    // ==================== GeckoLib 动画 ====================

    /**
     * 空闲动画控制器
     * 持续播放空闲循环动画
     */
    private PlayState idlePredicate(AnimationState<DreamCauldronBlockEntity> state) {
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::idlePredicate));
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
        return new com.pasterdream.pasterdreammod.menu.DreamCauldronMenu(id, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.pasterdream.dream_cauldron");
    }
}
