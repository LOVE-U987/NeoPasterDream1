package com.pasterdream.pasterdreammod.block.entity;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 亚伦柯斯之手生成激活方块实体
 * <p>
 * 使用 GeckoLib 渲染 3D 模型和动画，对应方块
 * {@link com.pasterdream.pasterdreammod.registry.PDBlocks#AARONCOSHANDSPAWNBLOCK}。
 * 空闲状态下持续播放漂浮/脉动动画。
 */
public class AaroncosHandSpawnBlockEntity extends BlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 构造亚伦柯斯之手生成激活方块实体
     *
     * @param pos   方块位置
     * @param state 方块状态
     */
    public AaroncosHandSpawnBlockEntity(BlockPos pos, BlockState state) {
        super(PDBlockEntities.AARONCOS_HAND_SPAWN_BLOCK.get(), pos, state);
    }

    /**
     * 空闲动画谓词 - 循环播放待机动画
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState idlePredicate(AnimationState<AaroncosHandSpawnBlockEntity> state) {
        return state.setAndContinue(RawAnimation.begin().thenLoop("0"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 0, this::idlePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
