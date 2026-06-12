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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 影之箱方块实体 (Shadow Chest Block Entity)
 * 集成 GeckoLib 动画 + 15 格库存系统 + GUI 菜单提供者
 *
 * 动画说明：
 * - animation = 0：空闲状态（盖闭合，模型保持绑定姿势）
 * - animation = 1：开盖动画（骨骼 "top" 旋转 + 滑动，hold_on_last_frame）
 *   播放完毕后自动重置 animation 为 0
 */
public class ShadowChestBlockEntity extends BlockEntity implements GeoBlockEntity, MenuProvider {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final String ANIM_PROPERTY = "animation";

    /** 缓存动画属性值，避免每帧查询 block state */
    private int cachedAnimation = 0;

    /**
     * 15 格库存处理器
     * 对应 GUI 中 5×3 的物品槽位网格
     */
    private final ItemStackHandler itemHandler = new ItemStackHandler(15) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /**
     * 构造影之箱方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public ShadowChestBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.SHADOW_CHEST.get(), pos, state);
        updateCachedAnimation(state);
    }

    /**
     * 获取库存处理器
     *
     * @return ItemStackHandler 实例（15 格）
     */
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    // ==================== GeckoLib 动画 ====================

    /**
     * 动画控制器 1：空闲状态循环
     * 当 animation = 0 时持续循环
     */
    private PlayState idlePredicate(software.bernie.geckolib.animation.AnimationState<ShadowChestBlockEntity> state) {
        int anim = getAnimationProperty();
        if (anim == 0) {
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    /**
     * 动画控制器 2：触发式动画
     * 当 animation = 1 时播放开盖动画，完成后自动重置为 0
     */
    private PlayState openPredicate(AnimationState<ShadowChestBlockEntity> state) {
        int anim = getAnimationProperty();
        if (anim != 0 && state.getController().getAnimationState() == AnimationController.State.STOPPED) {
            state.getController().setAnimation(RawAnimation.begin().thenPlay(String.valueOf(anim)));
            if (state.getController().getAnimationState() == AnimationController.State.STOPPED) {
                resetAnimationProperty();
                state.getController().forceAnimationReset();
            }
        } else if (anim == 0) {
            return PlayState.STOP;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::idlePredicate));
        controllers.add(new AnimationController<>(this, "procedurecontroller", 0, this::openPredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    /**
     * 更新缓存的动画属性值
     * 从方块状态中提取 animation 属性并缓存
     */
    private void updateCachedAnimation(BlockState state) {
        if (state.getBlock().getStateDefinition().getProperty(ANIM_PROPERTY) instanceof IntegerProperty prop) {
            cachedAnimation = state.getValue(prop);
        } else {
            cachedAnimation = 0;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null) {
            updateCachedAnimation(getBlockState());
        }
    }

    @Override
    public void setBlockState(BlockState state) {
        super.setBlockState(state);
        updateCachedAnimation(state);
    }

    /**
     * 获取当前动画属性值（使用缓存，避免每帧查询 block state）
     *
     * @return animation 属性值（0 或 1）
     */
    private int getAnimationProperty() {
        return cachedAnimation;
    }

    /**
     * 重置动画属性为 0（空闲）
     */
    private void resetAnimationProperty() {
        if (level == null) return;
        BlockState state = level.getBlockState(worldPosition);
        if (state.getBlock().getStateDefinition().getProperty(ANIM_PROPERTY) instanceof IntegerProperty prop) {
            level.setBlock(worldPosition, state.setValue(prop, 0), 3);
        }
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
        return new com.pasterdream.pasterdreammod.menu.ShadowChestMenu(id, inventory, this);
    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("container.pasterdream.shadow_chest");
    }
}