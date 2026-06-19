package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
 * 暗影图腾 (Shadow Tune Totem) — 大型地面敌对生物（静止）
 * <p>
 * 行为：
 * - 无 AI，静止不动
 * - 免疫火焰、药水、摔落、凋零伤害
 * - 始终播放 idle 动画，死亡时播放 death 动画
 * <p>
 * 注意：Geo 模型文件名为 shadow_rune_totem.geo.json，与注册名不一致，渲染器需自定义模型路径。
 * 渲染：GeckoLib 动画实体
 */
public class ShadowTuneTotemEntity extends Monster implements GeoEntity {

    private static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(ShadowTuneTotemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(ShadowTuneTotemEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(ShadowTuneTotemEntity.class, EntityDataSerializers.STRING);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 过程动画名称（"empty" 表示无过程动画） */
    public String animationprocedure = "empty";

    /** 客户端 procedure 动画处理器 */
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();

    // ==================== 自毁炸弹技能 ====================
    /** 技能倒计时 tick（从 spawn 开始计数），-1 表示未触发或已结束 */
    private int skillTick = -1;
    /** 是否已触发技能（防止 chunk 重载时重复触发） */
    private boolean skillTriggered = false;

    /**
     * 构造暗影图腾实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public ShadowTuneTotemEntity(EntityType<? extends Monster> type, Level level) {
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
        builder.define(TEXTURE, "shadow_rune_totem");
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
     * 创建暗影图腾实体的属性
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40)
                .add(Attributes.ARMOR, 5)
                .add(Attributes.ATTACK_DAMAGE, 0)
                .add(Attributes.MOVEMENT_SPEED, 0)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.KNOCKBACK_RESISTANCE, 10);
    }


    // ======================== 音效 ========================

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.DEEPSLATE_BRICKS_BREAK;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_EXPLODE.value();
    }

    // ======================== 受伤/免疫 ========================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE)) return false;
        if (source.is(DamageTypes.ON_FIRE)) return false;
        if (source.is(DamageTypes.LAVA)) return false;
        if (source.is(DamageTypes.FALL)) return false;
        if (source.is(DamageTypes.WITHER)) return false;
        if (source.is(DamageTypes.WITHER_SKULL)) return false;
        if (source.is(DamageTypes.INDIRECT_MAGIC)) return false;
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
        // 首次生成触发自毁炸弹
        if (!this.level().isClientSide() && !skillTriggered) {
            skillTriggered = true;
            this.triggerSelfDestruct(this.level(), this.getX(), this.getY(), this.getZ());
        }
        // 自毁炸弹倒计时
        if (!this.level().isClientSide() && skillTick >= 0) {
            skillTick++;
            executeSkillTick();
        }
    }

    // ==================== 自毁炸弹技能实现 ====================

    /**
     * 触发自毁炸弹技能（在 finalizeSpawn 中调用）
     * 原 ShadowTuneTotemPr0Procedure 逻辑
     */
    public void triggerSelfDestruct(LevelAccessor world, double x, double y, double z) {
        if (this.level().isClientSide()) return;

        // 立即：向 64 格内玩家广播"暗影符文塔正在蓄能"
        broadcastToNearbyPlayers(64, Component.literal("§5暗影符文塔正在蓄能"));
        // 启动倒计时
        skillTick = 0;
    }

    /**
     * 每 tick 执行的技能逻辑
     */
    private void executeSkillTick() {
        if (skillTick > 497) {
            // 超过总时长 → 结束
            skillTick = -1;
            return;
        }

        // 300 tick (15s): 广播即将爆破
        if (skillTick == 300) {
            broadcastToNearbyPlayers(64, Component.literal("§4暗影符文塔即将发生爆破"));
        }

        // 400 tick (20s): 播放 skill 动画 + 音效
        if (skillTick == 400) {
            this.setAnimation("skill");
            // 播放暗影蓄能音效
            this.level().playSound(null, this.blockPosition(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 2.0F, 0.7F);
        }

        // 482 tick (~24s): 执行爆炸
        if (skillTick == 482) {
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();

            // 播放爆炸音效
            this.level().playSound(null, this.blockPosition(),
                    SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 4.0F, 1.0F);

            // 对 99 格内非特殊/非暗影实体施加暗影效果 + 爆炸
            AABB aabb = new AABB(new Vec3(x, y, z), new Vec3(x, y, z)).inflate(99 / 2d);
            List<Entity> targets = this.level().getEntitiesOfClass(Entity.class, aabb, e -> true)
                    .stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(this)))
                    .toList();
            for (Entity target : targets) {
                if (!target.getType().is(TagKey.create(Registries.ENTITY_TYPE,
                        ResourceLocation.fromNamespaceAndPath("pasterdream", "special_entity_tag")))
                        && !target.getType().is(TagKey.create(Registries.ENTITY_TYPE,
                        ResourceLocation.fromNamespaceAndPath("pasterdream", "shadow_mob")))) {
                    if (target instanceof LivingEntity living) {
                        living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 200, 0));
                    }
                    // 在每个目标位置产生爆炸
                    this.level().explode(null, target.getX(), target.getY(), target.getZ(),
                            5, Level.ExplosionInteraction.MOB);
                }
            }

            // 释放暗影石粒子 + 烟雾
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles((SimpleParticleType) PDParticles.SHADOW_STONE_PARTICLE.particleType(),
                        x, y, z, 128, 1, 4, 1, 0.1);
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        x, y, z, 128, 1, 4, 1, 0.1);
                // 造成伤害并自毁（延迟 15 tick）
                serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                        x, y, z, 1, 0, 0, 0, 0);
            }
        }

        // 497 tick (~25s): 自毁
        if (skillTick == 497) {
            if (this.isAlive()) {
                this.hurt(this.damageSources().generic(), this.getHealth() * 2);
                this.discard();
            }
        }
    }

    /**
     * 向附近玩家广播消息
     *
     * @param range   检测半径
     * @param message 消息内容
     */
    private void broadcastToNearbyPlayers(double range, Component message) {
        AABB aabb = new AABB(this.getX(), this.getY(), this.getZ(),
                this.getX(), this.getY(), this.getZ()).inflate(range / 2d);
        for (Player player : this.level().getEntitiesOfClass(Player.class, aabb)) {
            player.displayClientMessage(message, true);
        }
    }

    // ======================== 死亡处理 ========================

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
     * 暗影图腾存活时播放 idle，死亡时播放 death
     */
    private PlayState movementPredicate(AnimationState<ShadowTuneTotemEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            if (this.isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("death"));
            }
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    /**
     * 过程动画控制器（用于触发一次性动画）
     */
    private PlayState procedurePredicate(AnimationState<ShadowTuneTotemEntity> state) {
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