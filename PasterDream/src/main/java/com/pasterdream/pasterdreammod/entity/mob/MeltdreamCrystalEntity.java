package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.api.entity.anim.ProcedureAnimationHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
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
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

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
public class MeltdreamCrystalEntity extends PathfinderMob implements GeoEntity {

    private static final EntityDataAccessor<Boolean> SHOOT =
            SynchedEntityData.defineId(MeltdreamCrystalEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> ANIMATION =
            SynchedEntityData.defineId(MeltdreamCrystalEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> TEXTURE =
            SynchedEntityData.defineId(MeltdreamCrystalEntity.class, EntityDataSerializers.STRING);

    /** GeckoLib 动画实例缓存 */
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 当前动画标识（用于 procedure 控制器） */
    public String animationprocedure = "empty";

    /** 客户端 procedure 动画处理器 */
    private final ProcedureAnimationHandler procAnim = new ProcedureAnimationHandler();

    /**
     * 构造融梦水晶实体
     *
     * @param entityType 实体类型
     * @param level      世界实例
     */
    public MeltdreamCrystalEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoAi(true);
        this.setPersistenceRequired();
        this.setNoGravity(true);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOT, false);
        builder.define(ANIMATION, "undefined");
        builder.define(TEXTURE, "meltdream_crystal_entity");
    }

    /**
     * 设置纹理
     *
     * @param texture 纹理名称
     */
    public void setTexture(String texture) {
        this.entityData.set(TEXTURE, texture);
    }

    /**
     * 获取纹理名称
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
     * 设置同步动画，同时赋值 animationprocedure 以触发 procedure 控制器
     *
     * @param animation 动画名称
     */
    public void setAnimation(String animation) {
        this.entityData.set(ANIMATION, animation);
        this.animationprocedure = animation;
    }

    // ==================== NBT 持久化 ====================

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

    // ==================== 属性 ====================

    /**
     * 创建融梦水晶实体的属性
     * 移速0, 生命2, 护甲0, 攻击伤害0, 跟随范围0, 飞行速度0
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2)
                .add(Attributes.MOVEMENT_SPEED, 0)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 0)
                .add(Attributes.FOLLOW_RANGE, 0)
                .add(Attributes.FLYING_SPEED, 0);
    }

    // ==================== 生存行为 ====================

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    // 注意：Minecraft 1.21.1 中 canBreatheUnderwater() 为 final，无法覆盖。
    // 溺水伤害已在 hurt() 中免疫，且 baseTick() 保持氧气始终满，等效实现水下呼吸。

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    // ==================== 受伤/免疫 ====================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.IN_FIRE)) return false;
        if (source.is(DamageTypes.ON_FIRE)) return false;
        if (source.is(DamageTypes.LAVA)) return false;
        if (source.is(DamageTypes.ARROW)) return false;
        if (source.is(DamageTypes.PLAYER_ATTACK)) return false;
        if (source.is(DamageTypes.INDIRECT_MAGIC)) return false;
        if (source.is(DamageTypes.FALL)) return false;
        if (source.is(DamageTypes.CACTUS)) return false;
        if (source.is(DamageTypes.DROWN)) return false;
        if (source.is(DamageTypes.LIGHTNING_BOLT)) return false;
        if (source.is(DamageTypes.EXPLOSION)) return false;
        if (source.is(DamageTypes.PLAYER_EXPLOSION)) return false;
        if (source.is(DamageTypes.TRIDENT)) return false;
        if (source.is(DamageTypes.FALLING_ANVIL)) return false;
        if (source.is(DamageTypes.DRAGON_BREATH)) return false;
        if (source.is(DamageTypes.WITHER)) return false;
        if (source.is(DamageTypes.WITHER_SKULL)) return false;

        return super.hurt(source, amount);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
        }
    }

    // ==================== 音效 ====================

    @Override
    public void playStepSound(BlockPos pos, BlockState blockIn) {
        // 飞行实体无需步声音效
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

    // ==================== 交互 ====================

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel) level();
        Vec3 pos = this.position();

        // 播放紫水晶破碎声音
        serverLevel.playSound(null, this.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_BREAK, this.getSoundSource(), 1.0f, 1.0f);

        // 生成大量融梦水晶粒子
        if (PDParticles.MELTDREAM_CRYSTAL_PARTICLE.particleType() != null) {
            serverLevel.sendParticles((SimpleParticleType) PDParticles.MELTDREAM_CRYSTAL_PARTICLE.particleType(),
                    pos.x, pos.y + 0.9, pos.z,
                    50, 0.5, 0.5, 0.5, 0.1);
        }

        this.remove(RemovalReason.DISCARDED);

        // 延迟 3 tick 后掉落融梦水晶碎片
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

    // ==================== 每 tick 更新 ====================

    @Override
    public void baseTick() {
        super.baseTick();

        // 水下呼吸（无限氧气）
        if (this.getAirSupply() < this.getMaxAirSupply()) {
            this.setAirSupply(this.getMaxAirSupply());
        }

        // 服务端每 tick 生成融梦水晶粒子
        if (level() instanceof ServerLevel serverLevel) {
            Vec3 pos = this.position();
            if (PDParticles.MELTDREAM_CRYSTAL_PARTICLE.particleType() != null) {
                serverLevel.sendParticles((SimpleParticleType) PDParticles.MELTDREAM_CRYSTAL_PARTICLE.particleType(),
                        pos.x, pos.y + 0.7, pos.z,
                        1, 0.1, 0.1, 0.1, 0.01);
            }
        }
    }

    // ==================== 死亡掉落 ====================

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        Item meltdreamCrystal = BuiltInRegistries.ITEM.get(
                ResourceLocation.parse("pasterdream:meltdream_crystal_0"));
        if (meltdreamCrystal != Items.AIR) {
            this.spawnAtLocation(new ItemStack(meltdreamCrystal));
        }
    }

    // ==================== GeckoLib 动画 ====================

    private PlayState movementPredicate(AnimationState<MeltdreamCrystalEntity> state) {
        if (state.isMoving()) {
            return state.setAndContinue(RawAnimation.begin().thenLoop("walk"));
        }
        return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
    }

    private PlayState procedurePredicate(AnimationState<MeltdreamCrystalEntity> state) {
        return procAnim.predicate(state,
                level().isClientSide(),
                this::getSyncedAnimation,
                () -> setAnimation("empty"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 0, this::movementPredicate));
        controllers.add(new AnimationController<>(this, "procedure", 0, this::procedurePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}