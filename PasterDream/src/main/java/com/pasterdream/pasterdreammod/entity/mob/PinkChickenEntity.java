package com.pasterdream.pasterdreammod.entity.mob;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * 粉色鸡 (Pink Chicken) — 粉色的原版鸡
 * <p>
 * 行为与原版 {@link net.minecraft.world.entity.animal.Chicken} 完全一致：
 * <ul>
 *   <li>地面行走，被动动物，可繁殖</li>
 *   <li>食用种子类食物（CHICKEN_FOOD 标签）繁殖</li>
 *   <li>定时下粉蛋（6000-11999 ticks），保存进度</li>
 *   <li>缓降（下落速度 × 0.6）</li>
 *   <li>受火/受伤时恐慌逃跑</li>
 *   <li>完整翅膀动画</li>
 *   <li>Chicken Jockey 支持（小僵尸骑乘）</li>
 * </ul>
 * <p>
 * 渲染：使用原版 ChickenModel + 自定义粉色纹理
 */
public class PinkChickenEntity extends Animal {

    /** 粉蛋物品 */
    private static final Item PINK_EGG = BuiltInRegistries.ITEM.get(
            ResourceLocation.parse("pasterdream:pinkegg"));

    /** 繁殖食物：染梦果 */
    private static final Item DYEDREAM_FRUIT = BuiltInRegistries.ITEM.get(
            ResourceLocation.parse("pasterdream:dyedream_fruit"));

    // ── 翅膀动画字段 ──
    public float flap;
    public float flapSpeed;
    public float oFlapSpeed;
    public float oFlap;
    public float flapping = 1.0F;
    private float nextFlap = 1.0F;

    /** 下蛋计时器（tick），与原版一致：6000~11999 ticks */
    public int eggTime = this.random.nextInt(6000) + 6000;

    /** Chicken Jockey 标记 */
    public boolean isChickenJockey;

    public PinkChickenEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    // ==================== 属性 ====================

    /**
     * 创建粉色鸡的属性，与原版鸡完全一致
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    // ==================== AI 目标 ====================

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0,
                Ingredient.of(DYEDREAM_FRUIT), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    // ==================== 繁殖 ====================

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.getItem() == DYEDREAM_FRUIT;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob parent) {
        return (AgeableMob) this.getType().create(level);
    }

    // ==================== 音效 ====================

    @Override
    public SoundEvent getAmbientSound() {
        return SoundEvents.CHICKEN_AMBIENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.CHICKEN_HURT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.CHICKEN_DEATH;
    }

    @Override
    public void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    // ==================== 每 tick 更新 ====================

    /**
     * 原版鸡的核心 tick 逻辑：翅膀动画 + 缓降 + 下蛋
     */
    @Override
    public void aiStep() {
        super.aiStep();

        // ── 翅膀动画 ──
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed += (this.onGround() ? -1.0F : 4.0F) * 0.3F;
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
        if (!this.onGround() && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }
        this.flapping *= 0.9F;
        Vec3 delta = this.getDeltaMovement();
        if (!this.onGround() && delta.y < 0.0) {
            this.setDeltaMovement(delta.multiply(1.0, 0.6, 1.0));
        }
        this.flap += this.flapping * 2.0F;

        // ── 下蛋（服务端，仅成年且非 Jockey）──
        if (!this.level().isClientSide && this.isAlive() && !this.isBaby()
                && !this.isChickenJockey && --this.eggTime <= 0) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F,
                    (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            if (PINK_EGG != Items.AIR) {
                ItemEntity egg = new ItemEntity(level(), getX(), getY(), getZ(),
                        new ItemStack(PINK_EGG));
                egg.setPickUpDelay(10);
                level().addFreshEntity(egg);
            }
            this.gameEvent(GameEvent.ENTITY_PLACE);
            this.eggTime = this.random.nextInt(6000) + 6000;
        }
    }

    // ==================== 翅膀扇动 ====================

    @Override
    protected boolean isFlapping() {
        return this.flyDist > this.nextFlap;
    }

    @Override
    protected void onFlap() {
        this.nextFlap = this.flyDist + this.flapSpeed / 2.0F;
    }

    // ==================== Chicken Jockey ====================

    public boolean isChickenJockey() {
        return this.isChickenJockey;
    }

    public void setChickenJockey(boolean jockey) {
        this.isChickenJockey = jockey;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return this.isChickenJockey();
    }

    @Override
    protected int getBaseExperienceReward() {
        return this.isChickenJockey() ? 10 : super.getBaseExperienceReward();
    }

    @Override
    protected void positionRider(Entity rider, Entity.MoveFunction callback) {
        super.positionRider(rider, callback);
        if (rider instanceof LivingEntity living) {
            living.yBodyRot = this.yBodyRot;
        }
    }

    // ==================== NBT 持久化 ====================

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.isChickenJockey = tag.getBoolean("IsChickenJockey");
        if (tag.contains("EggLayTime")) {
            this.eggTime = tag.getInt("EggLayTime");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsChickenJockey", this.isChickenJockey);
        tag.putInt("EggLayTime", this.eggTime);
    }
}
