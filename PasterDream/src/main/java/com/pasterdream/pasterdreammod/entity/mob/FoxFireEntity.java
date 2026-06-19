package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;

import java.util.List;

/**
 * 狐火 (FoxFire) —— 染梦世界中飘浮的神秘火焰精灵
 * <p>
 * 行为要点：
 * <ul>
 *   <li>中立环境实体，继承 PathfinderMob + GeoEntity</li>
 *   <li>setNoAi(true) — 完全静止，无任何 AI 行为</li>
 *   <li>不可推动，免疫几乎所有伤害类型</li>
 *   <li>生成后 400 tick（约 20 秒）自动消散</li>
 *   <li>每 tick 散发狐火粒子，对范围内实体施加效果</li>
 * </ul>
 * <p>
 * 渲染：GeckoLib 动画实体，默认纹理 "fox_fire"
 */
public class FoxFireEntity extends PathfinderMob implements GeoEntity {

    /** 射击状态同步标记（兼容动画系统） */
    public static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(FoxFireEntity.class, EntityDataSerializers.BOOLEAN);
    /** 当前播放动画名称同步标记 */
    public static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(FoxFireEntity.class, EntityDataSerializers.STRING);
    /** 纹理名称同步标记（默认 "fox_fire"） */
    public static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(FoxFireEntity.class, EntityDataSerializers.STRING);

    /** GeckoLib 动画实例缓存 */
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();

    /** 过程动画名称（"empty" 表示无过程动画） */
    public String animationprocedure = "empty";

    /**
     * 构造狐火实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public FoxFireEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 0;
        setNoAi(true);
    }

    // ======================== 同步数据 ========================

    /**
     * 定义同步实体数据
     */
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "fox_fire");
    }

    /**
     * 设置纹理名称
     *
     * @param texture 纹理名称
     */
    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    /**
     * 获取当前纹理名称
     *
     * @return 纹理名称
     */
    public String getTexture() {
        return this.entityData.get(TEXTURE);
    }

    /**
     * 获取同步的动画名称
     *
     * @return 动画名称
     */
    public String getSyncedAnimation() {
        return this.entityData.get(ANIMATION);
    }

    /**
     * 设置同步的动画名称
     *
     * @param animation 动画名称
     */
    public void setAnimation(String animation) {
        this.entityData.set(ANIMATION, animation);
    }

    // ======================== NBT 持久化 ========================

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Texture", this.getTexture());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Texture")) {
            this.setTexture(compound.getString("Texture"));
        }
    }

    // ======================== 属性 ========================

    /**
     * 创建狐火的属性
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2.0)
                .add(Attributes.ARMOR, 0.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    // ======================== AI 目标 ========================

    @Override
    protected void registerGoals() {
        // 狐火无任何 AI 目标，完全静止
    }

    // ======================== 物理碰撞 ========================

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entityIn) {
        // 不可推动，空实现
    }

    @Override
    protected void pushEntities() {
        // 不可推动，空实现
    }

    // ======================== 受伤/免疫 ========================

    /**
     * 狐火免疫几乎所有伤害类型
     *
     * @param source 伤害来源
     * @param amount 伤害量
     * @return 是否受到伤害
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE)) return false;
        if (source.is(DamageTypes.ON_FIRE)) return false;
        if (source.is(DamageTypes.ARROW)) return false;
        if (source.is(DamageTypes.PLAYER_ATTACK)) return false;
        if (source.is(DamageTypes.FALL)) return false;
        if (source.is(DamageTypes.CACTUS)) return false;
        if (source.is(DamageTypes.DROWN)) return false;
        if (source.is(DamageTypes.LIGHTNING_BOLT)) return false;
        if (source.is(DamageTypes.EXPLOSION)) return false;
        if (source.is(DamageTypes.TRIDENT)) return false;
        if (source.is(DamageTypes.FALLING_ANVIL)) return false;
        if (source.is(DamageTypes.DRAGON_BREATH)) return false;
        if (source.is(DamageTypes.WITHER)) return false;
        if (source.is(DamageTypes.WITHER_SKULL)) return false;
        if (source.is(DamageTypes.THROWN)) return false;
        if (source.is(DamageTypes.INDIRECT_MAGIC)) return false;
        return super.hurt(source, amount);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        // 不处理摔落检测
    }

    // ======================== 生成时初始化 ========================

    /**
     * 生成时设置旋转角度为 0，播放狐火音效
     */
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty,
                                        MobSpawnType reason, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData retval = super.finalizeSpawn(world, difficulty, reason, livingdata);

        // 将所有旋转角度归零
        this.setYRot(0);
        this.setXRot(0);
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.yBodyRotO = this.getYRot();
        this.yHeadRotO = this.getYRot();

        // 播放狐火环境音效
        if (!this.level().isClientSide()) {
            this.level().playSound(null, BlockPos.containing(this.getX(), this.getY(), this.getZ()),
                    PDSounds.FOX_FIRE.get(), SoundSource.NEUTRAL, 1.0f, 1.0f);
        }

        return retval;
    }

    // ======================== 每 tick 行为 ========================

    /**
     * 每 tick 逻辑：
     * <ul>
     *   <li>计时器达到 400 时销毁</li>
     *   <li>否则计时器 +1，散发狐火粒子</li>
     *   <li>对范围内 Mob 施加缓慢效果（TODO: VulnerabilityBuff 待移植）</li>
     *   <li>对范围内 Player 施加生命恢复效果</li>
     * </ul>
     */
    @Override
    public void baseTick() {
        super.baseTick();
        // 计时器逻辑
        double time = this.getPersistentData().getDouble("time");
        if (time >= 400) {
            if (!this.level().isClientSide()) {
                // 消散时：爆发大量狐火粒子 + 播放熄灭音效
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            (SimpleParticleType) PDParticles.FOX_FIRE_0_PARTICLE.particleType(),
                            this.getX(), this.getY(), this.getZ(),
                            30, 3.0, 1.0, 3.0, 0.5
                    );
                    serverLevel.sendParticles(
                            (SimpleParticleType) PDParticles.FOX_FIRE_1_PARTICLE.particleType(),
                            this.getX(), this.getY(), this.getZ(),
                            30, 3.0, 1.0, 3.0, 0.5
                    );
                }
                // 播放火焰熄灭音效
                this.level().playSound(null, this.blockPosition(),
                        SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 0.8f, 1.2f);
                this.discard();
            }
        } else {
            this.getPersistentData().putDouble("time", time + 1);

            // 每 tick 散发狐火粒子（服务端）
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        (SimpleParticleType) PDParticles.FOX_FIRE_0_PARTICLE.particleType(),
                        this.getX(), this.getY(), this.getZ(),
                        5, 5.0, 0.15, 5.0, 1.0
                );
                serverLevel.sendParticles(
                        (SimpleParticleType) PDParticles.FOX_FIRE_1_PARTICLE.particleType(),
                        this.getX(), this.getY(), this.getZ(),
                        5, 5.0, 0.15, 5.0, 1.0
                );
            }

            // 检测半径 6 内的实体
            Vec3 center = new Vec3(this.getX(), this.getY(), this.getZ());
            AABB aabb = new AABB(center, center).inflate(6.0 / 2.0);
            List<Entity> entities = this.level().getEntitiesOfClass(Entity.class, aabb, e -> true);

            // TODO: 原模组使用 VulnerabilityBuff（自定制效果）和 special_entity_tag（实体类型标签），
            //  这两者在新项目中暂未移植。暂时使用 WEAKNESS 作为 VulnerabilityBuff 的占位替代。
            for (Entity entity : entities) {
                if (entity instanceof Mob mob) {
                    // 对非特殊的 Mob 施加缓慢效果
                    // TODO: 原逻辑会检查 !entity.getType().is(special_entity_tag)，此处暂省略该检查
                    if (mob instanceof LivingEntity living) {
                        living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
                        // TODO: VulnerabilityBuff (20 tick, 1级) — 待移植后替换下方占位
                        // living.addEffect(new MobEffectInstance(PasterdreamModMobEffects.VULNERABILITY_BUFF.get(), 20, 1));
                    }
                } else if (entity instanceof Player player) {
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
                }
            }
        }

        this.refreshDimensions();
    }

    // ======================== 音效 ========================

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        // 狐火无脚步声
    }

    // ======================== 死亡处理 ========================

    /**
     * 死亡后 20 tick 移除实体
     */
    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
        }
    }

    // ======================== GeckoLib 动画 ========================

    /**
     * 移动状态动画控制器
     * 狐火始终播放 idle 循环动画
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState movementPredicate(AnimationState<FoxFireEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    /**
     * 过程动画控制器（用于触发一次性动画）
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState procedurePredicate(AnimationState<FoxFireEntity> state) {
        return procAnim.predicate(state,
                level().isClientSide(),
                this::getSyncedAnimation,
                () -> setAnimation("empty"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
        controllers.add(new AnimationController<>(this, "procedure", 4, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}