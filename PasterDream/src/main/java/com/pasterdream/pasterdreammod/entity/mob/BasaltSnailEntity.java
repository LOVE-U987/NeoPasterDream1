package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.api.entity.base.GeckoLibMobEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * 玄武岩蜗牛 (Basalt Snail) —— 游荡在染梦世界的中性生物！
 * <p>
 * 行为要点：
 * <ul>
 *   <li>中性生物，继承 {@link PathfinderMob}</li>
 *   <li>地面移动，不会主动攻击玩家</li>
 *   <li>受到攻击时：5tick 延迟后施加抗性提升 IV + 缓慢 V，并播放 "inoutshell" 动画</li>
 *   <li>免疫火焰和溺水伤害</li>
 * </ul>
 * <p>
 * 渲染：GeckoLib 动画实体，默认纹理 "basalt_snail"
 */
public class BasaltSnailEntity extends GeckoLibMobEntity {

    /** 受伤效果延迟计数器（受伤后 5tick 施加药水效果） */
    private int hurtEffectDelay = 0;

    /**
     * 构造玄武岩蜗牛实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public BasaltSnailEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 2;
    }

    /**
     * 返回默认纹理名称
     *
     * @return 默认纹理 "basalt_snail"
     */
    @Override
    protected String getDefaultTexture() {
        return "basalt_snail";
    }

    // ======================== 同步数据 ========================

    /**
     * 定义同步实体数据
     *
     * @param builder 同步数据构建器
     */
    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    // ======================== 属性 ========================

    /**
     * 创建玄武岩蜗牛的属性
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.1)
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.ATTACK_DAMAGE, 0.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    // ======================== AI 目标 ========================

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 目标选择器：仅反击（不主动攻击）
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        // 行为目标：浮水 → 近战反击 → 随机散步 → 随机张望
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    // ======================== 受伤/免疫 ========================

    /**
     * 玄武岩蜗牛免疫以下伤害类型：
     * <ul>
     *   <li>火焰（火/岩浆）</li>
     *   <li>溺水</li>
     * </ul>
     * <p>
     * 受伤时 5tick 延迟后施加抗性提升 IV + 缓慢 V，并播放 "inoutshell" 动画
     *
     * @param source 伤害来源
     * @param amount 伤害值
     * @return 是否受到伤害
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE)) return false;
        if (source.is(DamageTypes.DROWN)) return false;
        boolean result = super.hurt(source, amount);
        if (result) {
            // 触发 "inoutshell" 动画
            this.setAnimation("inoutshell");
            // 播放缩壳音效（石头碎裂声）
            this.level().playSound(null, this.blockPosition(),
                    SoundEvents.STONE_BREAK, SoundSource.NEUTRAL, 1.0f, 1.0f);
            // 设定 5tick 延迟后施加药水效果
            this.hurtEffectDelay = 5;
        }
        return result;
    }

    // ======================== 逻辑更新 ========================

    @Override
    public void tick() {
        super.tick();
        this.updateSwingTime();
        // 处理受伤延迟药水效果
        if (this.hurtEffectDelay > 0) {
            this.hurtEffectDelay--;
            if (this.hurtEffectDelay == 0 && this.isAlive()) {
                // 抗性提升 IV (等级 3) + 缓慢 V (等级 4)，持续 170tick
                this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 170, 3));
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 170, 4));
            }
        }
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        // 蜗牛爬行脚步声（可自定义）
    }

    // ======================== 尺寸刷新 ========================

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
    }

    // ======================== 死亡处理 ========================

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
            this.dropExperience(this.getLastHurtByMob());
        }
    }

    // ======================== GeckoLib 动画 ========================

    /**
     * 移动状态动画控制器
     * <ul>
     *   <li>移动时循环播放 "walk"</li>
     *   <li>静止时循环播放 "idle"</li>
     * </ul>
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState movementPredicate(AnimationState<BasaltSnailEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            if (state.isMoving() || !(state.getLimbSwingAmount() > -0.15F && state.getLimbSwingAmount() < 0.15F)) {
                return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
    }
}
