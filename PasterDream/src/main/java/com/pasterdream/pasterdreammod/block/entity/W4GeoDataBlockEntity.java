package com.pasterdream.pasterdreammod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * W4 波次通用 GeckoLib 数据方块实体
 * <p>
 * 忠实还原原版 MCreator TileEntity 的双控制器动画模式：
 * <ul>
 *   <li>{@code controller}：方块状态 {@code animation} 属性为 0 时循环播放动画 "0"；</li>
 *   <li>{@code procedurecontroller}：{@code animation} 属性非 0 时单次播放对应编号动画，
 *       播完把属性重置回 0。</li>
 * </ul>
 * 服务 birds_nest、broken_shadow_dungeon_protal、desert_hero_tomb、
 * 三种玻璃罐、guard_crystal、shadow_brazier、shadow_dungeon_portal、
 * shadow_trap_0、twilight_lantern、wind_knight_spawnblock_0..4 等
 * ENTITYBLOCK_ANIMATED 方块，具体类型由构造参数区分。
 */
public class W4GeoDataBlockEntity extends W4DataBlockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 构造通用 GeckoLib 数据方块实体
     *
     * @param type  方块实体类型
     * @param pos   方块位置
     * @param state 方块状态
     */
    public W4GeoDataBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /** 读取方块状态上 animation 属性的当前值（无属性时为 0） */
    private int animationValue() {
        BlockState state = getBlockState();
        if (state.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty prop) {
            return state.getValue(prop);
        }
        return 0;
    }

    /**
     * 空闲控制器谓词 —— animation==0 时循环播放动画 "0"
     *
     * @param event 动画状态
     * @return 播放状态
     */
    private PlayState idlePredicate(AnimationState<W4GeoDataBlockEntity> event) {
        if (animationValue() == 0) {
            return event.setAndContinue(RawAnimation.begin().thenLoop("0"));
        }
        return PlayState.STOP;
    }

    /**
     * 触发控制器谓词 —— animation!=0 时单次播放编号动画，播完重置属性
     *
     * @param event 动画状态
     * @return 播放状态
     */
    private PlayState procedurePredicate(AnimationState<W4GeoDataBlockEntity> event) {
        int anim = animationValue();
        String name = String.valueOf(anim);
        if (anim != 0 && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
            event.getController().setAnimation(RawAnimation.begin().thenPlay(name));
            if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
                if (level != null
                        && getBlockState().getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty prop) {
                    level.setBlock(getBlockPos(), getBlockState().setValue(prop, 0), 3);
                }
                event.getController().forceAnimationReset();
            }
        } else if (anim == 0) {
            return PlayState.STOP;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::idlePredicate));
        controllers.add(new AnimationController<>(this, "procedurecontroller", 0, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
