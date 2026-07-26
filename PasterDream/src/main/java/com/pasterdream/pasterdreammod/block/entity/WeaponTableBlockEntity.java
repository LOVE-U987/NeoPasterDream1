package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 精铸工作台方块实体 (Weapon Table Block Entity)
 * <p>
 * 仅承担 GeckoLib 3D 模型渲染（空闲动画 "0"）。
 * 原版 TileEntity 携带 9 格 RandomizableContainer 库存，但该方块没有任何
 * GUI/掉落内容物逻辑，库存实际不可达，故新版不再保留（见移植报告）。
 */
public class WeaponTableBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 构造精铸工作台方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public WeaponTableBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.WEAPON_TABLE.get(), pos, state);
    }

    /**
     * 动画谓词：ANIMATION 为 0 时循环空闲动画 "0"（动画文件仅含 "0"）
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState predicate(AnimationState<WeaponTableBlockEntity> state) {
        int anim = 0;
        if (getBlockState().getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty prop) {
            anim = getBlockState().getValue(prop);
        }
        if (anim == 0) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("0"));
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
