package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;

import java.util.Comparator;
import java.util.List;

/**
 * 震动水晶 (Shaking Crystal) — 地面敌对生物（静止）
 * <p>
 * 行为：
 * - 无 AI，静止不动
 * - 免疫绝大多数伤害类型（火焰、箭矢、玩家攻击、药水、摔落等）
 * - 始终播放 spawn 动画
 * <p>
 * 渲染：GeckoLib 动画实体
 */
public class ShakingCrystalEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(ShakingCrystalEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(ShakingCrystalEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(ShakingCrystalEntity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();

    /** 过程动画名称（"empty" 表示无过程动画） */
    public String animationprocedure = "empty";

    // ==================== 自毁技能 ====================
    /** 技能倒计时 tick */
    private int skillTick = -1;
    /** 是否已触发技能 */
    private boolean skillTriggered = false;

    /**
     * 构造震动水晶实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public ShakingCrystalEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 0;
        this.setNoAi(true);
    }

    // ======================== 同步数据 ========================

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "shaking_crystal");
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

    // ======================== 属性 ========================

    /**
     * 创建震动水晶实体的属性
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.MOVEMENT_SPEED, 0)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.KNOCKBACK_RESISTANCE, 100);
    }


    // ======================== 音效 ========================

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.GENERIC_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_DEATH;
    }

    // ======================== 受伤/免疫 ========================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE)) return false;
        if (source.is(DamageTypes.ON_FIRE)) return false;
        if (source.is(DamageTypes.LAVA)) return false;
        if (source.is(DamageTypes.ARROW)) return false;
        if (source.is(DamageTypes.THROWN)) return false;
        if (source.is(DamageTypes.PLAYER_ATTACK)) return false;
        if (source.is(DamageTypes.INDIRECT_MAGIC)) return false;
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
        return super.hurt(source, amount);
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

    // ======================== 每 tick 更新 ========================

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
        // 首次生成触发自毁技能
        if (!this.level().isClientSide() && !skillTriggered) {
            skillTriggered = true;
            this.triggerSelfDestruct();
        }
        // 服务端自毁倒计时
        if (!this.level().isClientSide() && skillTick >= 0) {
            skillTick++;
            executeSkillTick();
        }
    }

    // ==================== 自毁技能实现 ====================

    /**
     * 触发自毁技能（在 onAddedToWorld 中调用）
     * 原 ShakingCrystalPr0Procedure：播放 spawn 动画 → 粒子效果 → 混乱 → 自毁
     */
    public void triggerSelfDestruct() {
        if (this.level().isClientSide()) return;
        // 播放 spawn 动画
        this.setAnimation("spawn");
        // 播放水晶碎裂音效
        this.level().playSound(null, this.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.HOSTILE, 1.5F, 0.8F);
        skillTick = 0;
    }

    /**
     * 每 tick 执行的技能逻辑
     */
    private void executeSkillTick() {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();

        // tick 0: 立即生成 7 个爆炸粒子
        if (skillTick == 0 && this.level() instanceof ServerLevel sl) {
            for (int i = 0; i < 7; i++) {
                sl.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 1, 0.7, 0.5, 0.7, 0.01);
            }
        }

        // tick 7: 暗影石粒子 + 烟雾（地面）
        if (skillTick == 7 && this.level() instanceof ServerLevel sl) {
            sl.sendParticles((SimpleParticleType) PDParticles.SHADOW_STONE_PARTICLE.particleType(), x, y, z, 64, 1, 0.2, 1, 0.02);
            sl.sendParticles((SimpleParticleType) PDParticles.SHADOW_STONE_PARTICLE.particleType(), x, y, z, 16, 0.5, 0.2, 0.5, 0.02);
            sl.sendParticles(ParticleTypes.SMOKE, x, y, z, 64, 1, 0.5, 1, 0.01);
        }

        // tick 15: 暗影石粒子 + 烟雾（y+1 高度）
        if (skillTick == 15 && this.level() instanceof ServerLevel sl) {
            sl.sendParticles((SimpleParticleType) PDParticles.SHADOW_STONE_PARTICLE.particleType(), x, y + 1, z, 64, 1, 1, 1, 0.02);
            sl.sendParticles((SimpleParticleType) PDParticles.SHADOW_STONE_PARTICLE.particleType(), x, y + 1, z, 16, 0.5, 1, 0.5, 0.02);
            sl.sendParticles(ParticleTypes.SMOKE, x, y + 1, z, 64, 1, 1, 1, 0.01);

            // 对 9 格内非特殊实体施加混乱效果
            AABB aabb = new AABB(new Vec3(x, y, z), new Vec3(x, y, z)).inflate(9 / 2d);
            List<Entity> targets = this.level().getEntitiesOfClass(Entity.class, aabb, e -> true)
                    .stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(this)))
                    .toList();
            for (Entity target : targets) {
                if (!target.getType().is(TagKey.create(Registries.ENTITY_TYPE,
                        ResourceLocation.fromNamespaceAndPath("pasterdream", "special_entity_tag")))) {
                    if (target instanceof LivingEntity living) {
                        living.addEffect(new MobEffectInstance(PDEffects.CONFUSION_BUFF.holder(), 20, 1));
                    }
                }
            }
        }

        // tick 50: 自毁
        if (skillTick == 50) {
            this.discard();
            skillTick = -1;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
    }

    // ======================== 死亡处理 ========================

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 1) {
            this.remove(RemovalReason.KILLED);
        }
    }

    // ======================== GeckoLib 动画 ========================

    /**
     * 移动状态动画控制器
     * 震动水晶始终播放 spawn 循环动画
     */
    private PlayState movementPredicate(AnimationState<ShakingCrystalEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("spawn"));
        }
        return PlayState.STOP;
    }

    /**
     * 过程动画控制器（用于触发一次性动画）
     */
    private PlayState procedurePredicate(AnimationState<ShakingCrystalEntity> state) {
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