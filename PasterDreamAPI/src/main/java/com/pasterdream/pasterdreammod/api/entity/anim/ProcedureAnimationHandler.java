package com.pasterdream.pasterdreammod.api.entity.anim;

import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.function.Supplier;

/**
 * Procedure 动画处理器 —— 封装标准的 procedure 动画控制器逻辑
 * <p>
 * GeckoLib 的 procedure 动画控制器用于播放由服务端触发的"一次性"动画
 * （如技能、咆哮、受击等），与 movement/attacking 等持续性动画控制器协同工作。
 * <p>
 * 每个实体应创建自己的 {@code ProcedureAnimationHandler} 实例，
 * 并在 {@code procedurePredicate} 中委托调用 {@link #predicate} 方法，
 * 避免手写重复且容易出错的条件逻辑。
 * <p>
 * 使用示例：
 * <pre>{@code
 * public class MyEntity extends Monster implements GeoEntity {
 *     // 创建处理器实例
 *     private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();
 *
 *     // ... 其他代码 ...
 *
 *     // procedure 动画控制器回调
 *     private PlayState procedurePredicate(AnimationState<MyEntity> state) {
 *         return procAnim.predicate(state,
 *                 level().isClientSide(),
 *                 this::getSyncedAnimation,
 *                 () -> setAnimation("empty"));
 *     }
 *
 *     // 注册控制器
 *     public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
 *         controllers.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
 *         // ... 其他控制器 ...
 *     }
 * }
 * }</pre>
 *
 * <h3>工作原理</h3>
 * <ol>
 *   <li><b>服务端触发</b>：实体代码调用 {@code setAnimation("skill_name")} 更新同步数据</li>
 *   <li><b>数据同步</b>：Minecraft 网络同步将 {@code ANIMATION} data parameter 同步到客户端</li>
 *   <li><b>客户端播放</b>：此 handler 检测到新动画 → 通过 GeckoLib 播放一次</li>
 *   <li><b>自动重置</b>：动画播完后自动将同步数据重置为 "empty"，恢复 movement 控制器</li>
 * </ol>
 *
 * @see com.pasterdream.pasterdreammod.api.entity.skill.EntitySkillManager
 */
public class ProcedureAnimationHandler {

    /** 当前正在播放的 procedure 动画名称，防止重复触发 */
    private String currentlyPlaying = "empty";

    /**
     * procedure 动画控制器回调 - 标准实现
     * <p>
     * 完整的动画播放生命周期：
     * <ul>
     *   <li>检查是否在客户端（服务端不播动画）</li>
     *   <li>读取同步动画数据，与 currentlyPlaying 比较</li>
     *   <li>新动画 → 通过 {@link AnimationController#setAnimation} 播放一次</li>
     *   <li>动画播放中 → 返回 {@link PlayState#CONTINUE} 保持播放</li>
     *   <li>动画播完 → 重置 currentlyPlaying 和同步数据为 "empty"</li>
     * </ul>
     *
     * @param <T>             实体类型（需实现 GeoEntity）
     * @param state           动画状态
     * @param isClientSide    是否在客户端侧（由实体传入 {@code level().isClientSide()}）
     * @param syncedAnimSupplier 同步动画名称提供者（实体 {@code getSyncedAnimation()} 方法引用）
     * @param setEmptyAnim    重置动画为 empty 的回调（实体 {@code () -> setAnimation("empty")} 方法引用）
     * @return {@link PlayState#CONTINUE} 或 {@link PlayState#STOP}
     */
    public <T extends software.bernie.geckolib.animatable.GeoEntity> PlayState predicate(
            AnimationState<T> state,
            boolean isClientSide,
            Supplier<String> syncedAnimSupplier,
            Runnable setEmptyAnim) {

        // 只在客户端播放 procedure 动画
        if (!isClientSide) {
            return PlayState.STOP;
        }

        String anim = syncedAnimSupplier.get();

        // 检测到新动画 → 开始播放
        if (!anim.equals("empty") && !anim.equals(currentlyPlaying)) {
            currentlyPlaying = anim;
            state.getController().setAnimation(RawAnimation.begin().thenPlay(anim));
            return PlayState.CONTINUE;
        }

        // 有动画正在播放 → 等待播完
        if (!currentlyPlaying.equals("empty")) {
            if (state.getController().getAnimationState() == AnimationController.State.STOPPED) {
                // 动画播放完毕 → 清理状态
                currentlyPlaying = "empty";
                setEmptyAnim.run();
                return PlayState.STOP;
            }
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    /**
     * 重置处理器状态
     * <p>
     * 在实体死亡或需要强制重置动画时调用。
     */
    public void reset() {
        this.currentlyPlaying = "empty";
    }

    /**
     * 获取当前正在播放的动画名称
     *
     * @return 动画名称，无正在播放的动画返回 "empty"
     */
    public String getCurrentlyPlaying() {
        return currentlyPlaying;
    }
}