package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 暗影漩涡方块实体
 * <p>
 * BOSS 右手涡流技能生成的临时方块实体，使用 GeckoLib 渲染旋转漩涡动画。
 * 放置后持续约 5 秒（100 tick）自动消失。
 * 对应方块 {@link com.pasterdream.pasterdreammod.registry.PDBlocks#SHADOW_VORTEX}。
 */
public class ShadowVortexBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 漩涡存活时间（tick），约 5 秒 */
    private static final int LIFETIME = 100;

    /** 已存在的 tick 数 */
    private int age = 0;

    /**
     * 构造暗影漩涡方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public ShadowVortexBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.SHADOW_VORTEX.get(), pos, state);
    }

    /**
     * 客户端 tick —— 用于动画和计时
     * <p>
     * 由方块的 tick 方法调用，累加 age，到达生命周期后移除方块。
     */
    public void tick() {
        if (level == null) return;

        age++;

        // 到达生命周期后移除方块（服务端执行）
        if (!level.isClientSide && age >= LIFETIME && level instanceof ServerLevel serverLevel) {
            serverLevel.removeBlock(worldPosition, false);
        }
    }

    /**
     * 动画谓词 —— 循环播放漩涡动画
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState idlePredicate(AnimationState<ShadowVortexBlockEntity> state) {
        return state.setAndContinue(RawAnimation.begin().thenLoop("0"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "vortex", 0, this::idlePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
