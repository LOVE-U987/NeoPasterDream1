package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 蓄梦池方块实体 (Dream Accumulator Block Entity)
 * 使用 GeckoLib 实现动画效果
 *
 * 动画说明：
 * - move 骨骼：上下浮动动画（4秒循环）
 * - bone2 骨骼：360度旋转动画（4秒一圈）
 */
public class DreamAccumulatorBlockEntity extends BlockEntity implements GeoBlockEntity {

    /**
     * GeckoLib 动画实例缓存
     */
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 构造蓄梦池方块实体
     *
     * @param pos 方块位置
     * @param state 方块状态
     */
    public DreamAccumulatorBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.DREAM_ACCUMULATOR.get(), pos, state);
    }

    /**
     * 注册动画控制器
     * 定义动画播放逻辑
     *
     * @param controllers 动画控制器注册器
     */
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            // 循环播放动画 "0"（对应动画文件中的 "0" 动画）
            state.setAnimation(RawAnimation.begin().thenLoop("0"));
            return PlayState.CONTINUE;
        }));
    }

    /**
     * 获取动画实例缓存
     *
     * @return AnimatableInstanceCache 实例
     */
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
