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
import net.minecraft.world.item.ItemStack;
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
 * 2 格库存 + GeckoLib 动画 + GUI 菜单提供者。
 * <p>
 * 槽位语义（对照原版 withSize(2)）：
 * <ul>
 *   <li>槽 0：展示槽（仅由导入写入，GUI/自动化不可放入）；</li>
 *   <li>槽 1：导入槽（玩家放入待导入物品）。</li>
 * </ul>
 * 导入按钮语义等价原版 {@code TheEndlessBookOfDreamSeekersPr5}：复制槽 1 → 槽 0 并清空导入槽。
 */
public class TheEndlessBookOfDreamSeekersBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    /** 展示槽（原版 index 0） */
    public static final int SLOT_DISPLAY = 0;
    /** 导入槽（原版 index 1） */
    public static final int SLOT_IMPORT = 1;
    /** 槽位数 */
    public static final int SLOT_COUNT = 2;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 2 格库存：0 展示 / 1 导入；最大堆叠 1（原版 getMaxStackSize=1）。
     * 展示槽 {@link #isItemValid} 返回 false，仅 {@link #importFromSlot} 可写入。
     */
    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                BlockState state = getBlockState();
                level.sendBlockUpdated(worldPosition, state, state, 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            // 原版 canPlaceItem(0)=false，展示槽不可由 GUI/漏斗放入
            return slot != SLOT_DISPLAY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
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
     * @return ItemStackHandler 实例（2 格）
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    /**
     * 导入：将导入槽（1）复制到展示槽（0）并清空导入槽。
     * 等价原版 TheEndlessBookOfDreamSeekersPr5（原版延迟 1 tick 清槽，此处同步执行更稳妥）。
     *
     * @return 是否执行了导入（导入槽非空时为 true）
     */
    public boolean importFromSlot() {
        ItemStack importStack = itemHandler.getStackInSlot(SLOT_IMPORT);
        if (importStack.isEmpty()) {
            return false;
        }
        ItemStack display = importStack.copy();
        display.setCount(1);
        // setStackInSlot 绕过 isItemValid，可写入展示槽
        itemHandler.setStackInSlot(SLOT_DISPLAY, display);
        itemHandler.setStackInSlot(SLOT_IMPORT, ItemStack.EMPTY);
        return true;
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
        // 兼容旧档（仅 1 槽）：deserialize 可能把 Size 改回 1，安全扩到 2 并保留原槽 0
        if (itemHandler.getSlots() != SLOT_COUNT) {
            int oldSlots = itemHandler.getSlots();
            ItemStack[] preserved = new ItemStack[oldSlots];
            for (int i = 0; i < oldSlots; i++) {
                preserved[i] = itemHandler.getStackInSlot(i).copy();
            }
            itemHandler.setSize(SLOT_COUNT);
            for (int i = 0; i < Math.min(oldSlots, SLOT_COUNT); i++) {
                itemHandler.setStackInSlot(i, preserved[i]);
            }
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
