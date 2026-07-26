package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.api.entity.base.GeckoLibMobEntity;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * 大地之刃剑气 (terrasword_wave)
 * <p>
 * 还原自原版 TerraswordWaveEntity（GeckoLib 飞行“剑气”实体，非 AbstractArrow）：
 * <ul>
 *   <li>由大地之刃战技（TerraSwordPr0Procedure）挥出，初速为玩家视线方向 ×2</li>
 *   <li>生成时获得隐藏的云雾效果 25 tick 作为寿命计时器（TerraswordWavePr1Procedure），
 *       云雾消退后剑气即消散</li>
 *   <li>每 tick（TerraswordWavePr0Procedure）：孢子 + 剑气粒子拖尾，对直径 2.5 范围内
 *       非玩家且不带 special_entity_tag 的实体造成 2 + paster_atk*0.7 点玩家攻击类伤害；
 *       造成伤害后进入“死亡”状态——横扫粒子 ×2 + 龙息爆炸音效并消散</li>
 *   <li>免疫火焰/箭矢/玩家直接攻击/药水/摔落/仙人掌/溺水/雷击/爆炸/三叉戟/铁砧/龙息/凋零</li>
 *   <li>动画：idle 循环 + 战技段位一次性动画 "1"/"2"/"3"（由剑技程序 setAnimation 触发）</li>
 * </ul>
 */
public class TerraswordWaveEntity extends GeckoLibMobEntity {

    /** 特殊实体标签（剑气不会伤害的实体） */
    private static final TagKey<EntityType<?>> SPECIAL_ENTITY_TAG = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath("pasterdream", "special_entity_tag"));

    public TerraswordWaveEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 0;
        this.setNoAi(false);
        this.moveControl = new FlyingMoveControl(this, 10, true);
    }

    @Override
    protected String getDefaultTexture() {
        return "terrasword_wave";
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FlyingPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // 与原版一致：无任何 AI 目标，仅按初速直线飞行
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        // 与原版一致：不计坠落
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 原版剑气的全套免疫清单
        if (source.is(DamageTypes.IN_FIRE)
                || source.getDirectEntity() instanceof AbstractArrow
                || source.getDirectEntity() instanceof Player
                || source.getDirectEntity() instanceof ThrownPotion
                || source.getDirectEntity() instanceof AreaEffectCloud
                || source.is(DamageTypes.FALL)
                || source.is(DamageTypes.CACTUS)
                || source.is(DamageTypes.DROWN)
                || source.is(DamageTypes.LIGHTNING_BOLT)
                || source.is(DamageTypes.EXPLOSION)
                || source.is(DamageTypes.TRIDENT)
                || source.is(DamageTypes.FALLING_ANVIL)
                || source.is(DamageTypes.DRAGON_BREATH)
                || source.is(DamageTypes.WITHER)
                || source.is(DamageTypes.WITHER_SKULL)) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean fireImmune() {
        // 原版 EntityType 带 fireImmune()；本项目 EntityAPI 未暴露该开关，改由实体覆写
        return true;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData);
        // 原版 TerraswordWavePr1Procedure：隐藏云雾效果 25 tick 作为剑气寿命
        this.addEffect(new MobEffectInstance(PDEffects.CLOUDMIST_BUFF.holder(), 25, 0, false, false));
        return data;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        waveTick();
        this.refreshDimensions();
    }

    /**
     * 原版 TerraswordWavePr0Procedure：粒子拖尾 + 直径 2.5 范围伤害 + 寿命/死亡判定
     */
    private void waveTick() {
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles((SimpleParticleType) PDParticles.SPORE_PARTICLE.particleType(),
                    x, y, z, 3, 0.2, 0.2, 0.2, 0.1);
            serverLevel.sendParticles((SimpleParticleType) PDParticles.TERRASWORD_WAVE_PARTICLE.particleType(),
                    x, y, z, 3, 0.2, 0.5, 0.2, 0.1);
            Vec3 center = new Vec3(x, y, z);
            for (Entity target : serverLevel.getEntitiesOfClass(Entity.class,
                    new AABB(center, center).inflate(2.5 / 2d), e -> true)) {
                if (!(target instanceof Player) && !target.getType().is(SPECIAL_ENTITY_TAG)
                        && target != this) {
                    // 2 + paster_atk*0.7 点“玩家攻击”类伤害（paster_atk 由大地之刃战技写入）
                    target.hurt(new DamageSource(serverLevel.registryAccess()
                                    .registryOrThrow(Registries.DAMAGE_TYPE)
                                    .getHolderOrThrow(DamageTypes.PLAYER_ATTACK)),
                            (float) (2 + this.getPersistentData().getDouble("paster_atk") * 0.7));
                    this.getPersistentData().putBoolean("death", true);
                }
            }
        }
        // 云雾（寿命）消退后消散
        if (!this.hasEffect(PDEffects.CLOUDMIST_BUFF.holder()) && !this.level().isClientSide()) {
            this.discard();
        }
        // 命中后的“死亡”演出：横扫粒子 ×2 + 龙息爆炸音效
        if (this.getPersistentData().getBoolean("death") && !this.getPersistentData().getBoolean("switch")) {
            this.getPersistentData().putBoolean("switch", true);
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, x, y, z, 1, 0.5, 0.5, 0.5, 0.1);
                serverLevel.playSound(null, BlockPos.containing(x, y, z),
                        SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 0.7f, 1.0f);
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, x, y, z, 1, 0.5, 0.5, 0.5, 0.1);
            }
            if (!this.level().isClientSide()) {
                this.discard();
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.updateSwingTime();
        this.setNoGravity(true);
    }

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

    @Override
    protected void tickDeath() {
        // 与原版一致：20 tick 后移除
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
        }
    }

    /**
     * 剑气 idle 循环动画（原版 movementPredicate：animationprocedure 为空时循环 idle）
     */
    private PlayState movementPredicate(AnimationState<TerraswordWaveEntity> state) {
        if ("empty".equals(this.animationprocedure)) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }
        return PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(this, "movement", 4, this::movementPredicate));
    }

    /**
     * 创建大地之刃剑气的属性（与原版逐项一致）
     *
     * @return 属性构建器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.FOLLOW_RANGE, 64)
                .add(Attributes.KNOCKBACK_RESISTANCE, 10)
                .add(Attributes.ATTACK_KNOCKBACK, 10)
                .add(Attributes.FLYING_SPEED, 0.5);
    }
}
