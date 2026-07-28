package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.api.entity.base.GeckoLibMonsterEntity;
import com.pasterdream.pasterdreammod.entity.projectile.LightningProjectileEntity;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.*;

/**
 * 高压雷云 (Highvoltage Thundercloud) — 飞行敌对生物
 * <p>
 * 行为（对齐原版 {@code HighvoltageThundercloudPr0}/{@code Pr1}）：
 * <ul>
 *   <li>飞行游荡，无重力；免疫火焰、摔落、闪电</li>
 *   <li>baseTick：约 2.5% 概率对脚下 24 格内玩家落 6 道雷（伤害 10）</li>
 *   <li>受伤：约 50% 概率立刻再落一轮（伤害 10）</li>
 * </ul>
 */
public class HighvoltageThundercloudEntity extends GeckoLibMonsterEntity {

    /** 落雷基础伤害（原版 HighvoltageThundercloudPr0/Pr1） */
    private static final double BOLT_DAMAGE = 10.0D;

    /**
     * 构造高压雷云实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public HighvoltageThundercloudEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 22;
        this.moveControl = new FlyingMoveControl(this, 10, true);
        this.setNoGravity(true);
    }

    @Override
    protected String getDefaultTexture() {
        return "highvoltage_thundercloud";
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.MOVEMENT_SPEED, 0.15)
                .add(Attributes.FLYING_SPEED, 0.15)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.4, 20) {
            @Override
            protected Vec3 getPosition() {
                RandomSource random = HighvoltageThundercloudEntity.this.getRandom();
                double dirX = HighvoltageThundercloudEntity.this.getX() + ((random.nextFloat() * 2 - 1) * 16);
                double dirY = HighvoltageThundercloudEntity.this.getY() + ((random.nextFloat() * 2 - 1) * 16);
                double dirZ = HighvoltageThundercloudEntity.this.getZ() + ((random.nextFloat() * 2 - 1) * 16);
                return new Vec3(dirX, dirY, dirZ);
            }
        });
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        // 飞行实体无需步声音效
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.GENERIC_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvent.createVariableRangeEvent(
                net.minecraft.resources.ResourceLocation.withDefaultNamespace("block.candle.extinguish"));
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        // 飞行生物，不处理摔落检测
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 原版 HighvoltageThundercloudPr1：受伤 50% 落雷反击（伤害 10）
        if (!this.level().isClientSide()) {
            tryRainBolts(0.5D, BOLT_DAMAGE, false);
        }
        if (source.is(DamageTypes.IN_FIRE)) return false;
        if (source.is(DamageTypes.ON_FIRE)) return false;
        if (source.is(DamageTypes.LAVA)) return false;
        if (source.is(DamageTypes.LIGHTNING_BOLT)) return false;
        return super.hurt(source, amount);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.setNoGravity(true);
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
        // 原版 HighvoltageThundercloudPr0：每 tick 2.5% 落雷
        if (!this.level().isClientSide()) {
            tryRainBolts(0.025D, BOLT_DAMAGE, true);
        }
    }

    @Override
    public void setNoGravity(boolean ignored) {
        super.setNoGravity(true);
    }

    /**
     * VERIFY / 调试：强制落一轮雷（chance=1）。
     */
    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    public void forceRainBoltsForTest() {
        tryRainBolts(1.0D, BOLT_DAMAGE, false);
    }

    /**
     * 对附近玩家落 6 道竖直雷（对齐 Pr0/Pr1）。
     *
     * @param chance    触发概率
     * @param damage    单支落雷伤害
     * @param ambientFx 是否播放云体环境粒子
     */
    private void tryRainBolts(double chance, double damage, boolean ambientFx) {
        if (!(this.level() instanceof ServerLevel world)) {
            return;
        }
        if (this.random.nextDouble() > chance) {
            return;
        }
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        AABB probe = AABB.ofSize(new Vec3(x, y - 10.0D, z), 24.0D, 24.0D, 24.0D);
        if (world.getEntitiesOfClass(Player.class, probe, e -> true).isEmpty()) {
            return;
        }
        Player player = world.getEntitiesOfClass(Player.class,
                        AABB.ofSize(new Vec3(x, y - 5.0D, z), 24.0D, 24.0D, 24.0D), e -> true)
                .stream()
                .min((a, b) -> Double.compare(a.distanceToSqr(x, y - 5.0D, z), b.distanceToSqr(x, y - 5.0D, z)))
                .orElse(null);
        if (player == null || !player.isAlive()) {
            return;
        }

        RandomSource rng = this.getRandom();
        for (int i = 0; i < 6; i++) {
            double bx = 0.1D * Mth.nextDouble(rng, -6.0D, 6.0D) + player.getX();
            double by = player.getY() + 5.0D;
            double bz = 0.1D * Mth.nextDouble(rng, -6.0D, 6.0D) + player.getZ();
            LightningProjectileEntity bolt =
                    LightningProjectileEntity.summonRainBolt(world, bx, by, bz, damage);
            bolt.setOwner(this);
        }

        world.playSound(null, player.blockPosition(), PDSounds.THUNDERCLOUD_ATTACK.get(),
                SoundSource.MASTER, 0.6F, 1.0F);
        world.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                player.getX(), player.getY(), player.getZ(), 16, 0.4, 0.2, 0.4, 0.004);
        this.clearFire();
        if (ambientFx) {
            world.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 1, 0.6, 0.3, 0.6, 0.004);
            world.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, x, y, z, 1, 0.6, 0.3, 0.6, 0.004);
        }
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 10) {
            this.remove(RemovalReason.KILLED);
        }
    }

    private PlayState movementPredicate(AnimationState<HighvoltageThundercloudEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
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
