package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 娇小琴雨梦玩偶方块实体 (Qym Doll 0 Block Entity)
 * 静态装饰物，无动画，仅用于 GeckoLib 3D 模型渲染
 */
public class QymDoll0BlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 构造娇小琴雨梦玩偶方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public QymDoll0BlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.QIN_DOLL_0.get(), pos, state);
    }

    /**
     * 注册动画控制器 - 静态模型，无动画
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> PlayState.STOP));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
