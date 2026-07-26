package com.pasterdream.pasterdreammod.entity.mob;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

/**
 * 狂暴法术立场 (Fury Spell Field)
 * <p>
 * 狂暴法术命中后生成的视觉立场实体，还原自原版 FurySpellEntityEntity：
 * <ul>
 *   <li>纯展示实体（非生物），存在 90 tick 后自动消失</li>
 *   <li>不可推动、免疫火焰、无碰撞交互</li>
 *   <li>循环播放 "0" 动画（GeckoLib，2 倍缩放半透明渲染）</li>
 * </ul>
 * 增益脉冲逻辑不在本实体内——由 {@link com.pasterdream.pasterdreammod.entity.SpellEffects#fury}
 * 的定时任务驱动（与原版 procedure 结构一致）。
 */
public class FurySpellFieldEntity extends Entity implements GeoEntity {

    /** 立场存在时长（tick，与原版一致） */
    private static final int LIFETIME_TICKS = 90;

    /** 循环动画 "0"（见 animations/entity/fury_spell_entity.animation.json） */
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("0");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /**
     * 构造狂暴法术立场
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public FurySpellFieldEntity(EntityType<? extends FurySpellFieldEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    @Override
    public void tick() {
        super.tick();
        // 90 tick 后自动消散（与原版 FurySpellEntityPr0Procedure 一致）
        if (!this.level().isClientSide() && this.tickCount >= LIFETIME_TICKS) {
            this.discard();
        }
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // 无需同步数据
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // 短生命周期展示实体，无需持久化（重载后由 tickCount 重新计时）
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        // 无需持久化
    }

    // ==================== GeckoLib 动画 ====================

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0,
                state -> state.setAndContinue(IDLE_ANIM)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
