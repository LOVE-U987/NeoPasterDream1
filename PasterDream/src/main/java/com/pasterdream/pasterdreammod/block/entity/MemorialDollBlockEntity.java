package com.pasterdream.pasterdreammod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import com.pasterdream.pasterdreammod.block.MemorialDollBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * 纪念玩偶方块实体基类 (Memorial Doll Block Entity)
 * <p>
 * 存储“被抱住的物品”，并在客户端/服务端之间同步，使方块渲染器能够
 * 根据抱物状态切换模型并渲染手中的物品。
 */
@ParametersAreNonnullByDefault
public abstract class MemorialDollBlockEntity extends BlockEntity implements GeoBlockEntity {

    /**
     * NBT 键：玩偶当前抱住的物品
     */
    private static final String TAG_HELD_ITEM = "HeldItem";

    /**
     * 动画实例缓存
     */
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 当前抱住的物品，空栈表示未抱物
     */
    private ItemStack heldItem = ItemStack.EMPTY;

    /**
     * 构造纪念玩偶方块实体
     *
     * @param type  方块实体类型
     * @param pos   方块位置
     * @param state 方块状态
     */
    protected MemorialDollBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * 判断玩偶当前是否抱着物品
     * <p>
     * 客户端直接从世界中读取方块状态 {@link MemorialDollBlock#HOLDING}，避免依赖 BlockEntity
     * 缓存的方块状态，确保模型切换与物品渲染完全同步；服务端使用内部物品栈判断。
     *
     * @return 是否抱物
     */
    public boolean isHolding() {
        if (this.level != null && this.level.isClientSide) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            if (state.hasProperty(MemorialDollBlock.HOLDING)) {
                return state.getValue(MemorialDollBlock.HOLDING);
            }
        }
        return !this.heldItem.isEmpty();
    }

    /**
     * 获取玩偶当前抱住的物品
     *
     * @return 被抱物品，若为空则返回空栈
     */
    public ItemStack getHeldItem() {
        return this.heldItem.copy();
    }

    /**
     * 设置玩偶抱住的物品并同步到客户端
     * <p>
     * 模型切换由 {@link MemorialDollBlock#HOLDING} 方块状态同步，
     * 被抱物品数据仍通过 NBT 同步给渲染器。
     *
     * @param stack 被抱物品
     */
    public void setHeldItem(ItemStack stack) {
        this.heldItem = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        markUpdated();
    }

    /**
     * 清除抱住的物品并同步到客户端
     * <p>
     * 模型切换由 {@link MemorialDollBlock#HOLDING} 方块状态同步，
     * 被抱物品数据仍通过 NBT 同步给渲染器。
     */
    public void clearHeldItem() {
        if (this.heldItem.isEmpty()) {
            return;
        }
        this.heldItem = ItemStack.EMPTY;
        markUpdated();
    }

    /**
     * 标记方块实体已变更，触发 NBT 同步到客户端
     */
    private void markUpdated() {
        setChanged();
        if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    /**
     * 注册动画控制器 - 静态模型，无动画
     *
     * @param controllers 动画控制器注册器
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // ==================== NBT 与网络同步 ====================

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        // 1.21.1 中 ItemStack.EMPTY.save() 会抛出 IllegalStateException，
        // 因此空物品时不写入 HeldItem 标签；读取时缺失该标签即视为空。
        if (!this.heldItem.isEmpty()) {
            tag.put(TAG_HELD_ITEM, this.heldItem.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(TAG_HELD_ITEM, Tag.TAG_COMPOUND)) {
            this.heldItem = ItemStack.parseOptional(registries, tag.getCompound(TAG_HELD_ITEM));
        } else {
            this.heldItem = ItemStack.EMPTY;
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        // 直接调用 loadAdditional 同步 NBT，避免依赖父类实现细节（1.21 部分路径走 loadWithComponents）
        loadAdditional(tag, registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
