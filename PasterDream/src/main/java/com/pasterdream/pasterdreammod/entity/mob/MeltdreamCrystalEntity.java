package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.entity.damage.ConfigurableImmunityEntity;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

/**
 * 融梦水晶实体 (Meltdream Crystal) — 漂浮的梦境水晶
 * <p>
 * 行为：
 * - 静止漂浮的装饰性实体，使用飞行移动控制
 * - 每 tick 发出融梦水晶粒子
 * - 右键点击后消失并掉落融梦水晶碎片
 * - 死亡时掉落融梦水晶碎片
 * - 多种伤害类型免疫（含玩家直接攻击）
 * - 水下呼吸，免疫流体推动
 * <p>
 * 渲染：GeckoLib 动画实体，含 idle/movement/procedure 动画
 */
public class MeltdreamCrystalEntity extends ConfigurableImmunityEntity {

    public MeltdreamCrystalEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoAi(true);
        this.setPersistenceRequired();
        this.setNoGravity(true);
    }

    @Override
    protected String getDefaultTexture() {
        return "meltdream_crystal_entity";
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2)
                .add(Attributes.MOVEMENT_SPEED, 0)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 0)
                .add(Attributes.FOLLOW_RANGE, 0)
                .add(Attributes.FLYING_SPEED, 0);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.AMETHYST_BLOCK_CHIME;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.AMETHYST_BLOCK_BREAK;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.AMETHYST_BLOCK_BREAK;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel) level();
        Vec3 pos = this.position();

        serverLevel.playSound(null, this.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_BREAK, this.getSoundSource(), 1.0f, 1.0f);

        if (PDParticles.MELTDREAM_CRYSTAL_PARTICLE.particleType() != null) {
            serverLevel.sendParticles((SimpleParticleType) PDParticles.MELTDREAM_CRYSTAL_PARTICLE.particleType(),
                    pos.x, pos.y + 0.9, pos.z,
                    50, 0.5, 0.5, 0.5, 0.1);
        }

        this.remove(RemovalReason.DISCARDED);

        serverLevel.getServer().tell(new TickTask(
                serverLevel.getServer().getTickCount() + 3,
                () -> {
                    Item meltdreamCrystal = BuiltInRegistries.ITEM.get(
                            ResourceLocation.parse("pasterdream:meltdream_crystal_0"));
                    if (meltdreamCrystal != Items.AIR) {
                        ItemStack drop = new ItemStack(meltdreamCrystal);
                        ItemEntity item = new ItemEntity(serverLevel, pos.x, pos.y, pos.z, drop);
                        serverLevel.addFreshEntity(item);
                    }
                }
        ));

        return InteractionResult.SUCCESS;
    }

    @Override
    public void baseTick() {
        super.baseTick();

        if (this.getAirSupply() < this.getMaxAirSupply()) {
            this.setAirSupply(this.getMaxAirSupply());
        }

        if (level() instanceof ServerLevel serverLevel) {
            Vec3 pos = this.position();
            if (PDParticles.MELTDREAM_CRYSTAL_PARTICLE.particleType() != null) {
                serverLevel.sendParticles((SimpleParticleType) PDParticles.MELTDREAM_CRYSTAL_PARTICLE.particleType(),
                        pos.x, pos.y + 0.7, pos.z,
                        1, 0.1, 0.1, 0.1, 0.01);
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        Item meltdreamCrystal = BuiltInRegistries.ITEM.get(
                ResourceLocation.parse("pasterdream:meltdream_crystal_0"));
        if (meltdreamCrystal != Items.AIR) {
            this.spawnAtLocation(new ItemStack(meltdreamCrystal));
        }
    }

    private PlayState movementPredicate(AnimationState<MeltdreamCrystalEntity> state) {
        if (state.isMoving()) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
        }
        return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        super.registerControllers(controllers);
        controllers.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
    }
}