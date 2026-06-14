package com.pasterdream.pasterdreammod.entity.mob;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import org.jetbrains.annotations.Nullable;

/**
 * 粉色鸡 (Pink Chicken) — 染梦世界会飞的小鸡
 * <p>
 * 行为：
 * - 被动动物，继承自 {@link Animal}，可繁殖
 * - 使用 {@link FlyingMoveControl} 实现悬停飞行
 * - 定期下粉蛋（原 PinkChickenPr0Procedure）
 * - 食用染梦果（dyedream_fruit）繁殖
 * <p>
 * 渲染：使用原版 ChickenModel + 自定义粉色纹理
 */
public class PinkChickenEntity extends Animal {

    /** 食物：染梦果（通过 BuiltInRegistries 获取，兼容新项目尚未注册该物品的情况） */
    private static final Item DYEDREAM_FRUIT = BuiltInRegistries.ITEM.get(
            ResourceLocation.parse("pasterdream:dyedream_fruit"));

    /** 粉蛋物品（用于掉落逻辑，暂为留空引用） */
    private static final Item PINK_EGG = BuiltInRegistries.ITEM.get(
            ResourceLocation.parse("pasterdream:pink_egg"));

    /**
     * 构造粉色鸡实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public PinkChickenEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        // 使用飞行移动控制器，实现空中悬停
        this.moveControl = new FlyingMoveControl(this, 20, true);
        // 设置飞行导航
        this.navigation = new FlyingPathNavigation(this, level);
        // 避免寻路时绕开水体
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        // 始终无重力
        this.setNoGravity(true);
        this.xpReward = 1;
    }

    /**
     * 创建粉色鸡的属性
     * 5 血量、0.3 行走速度、0.3 飞行速度
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FLYING_SPEED, 0.3);
    }

    // ==================== AI ====================

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(2, new FollowParentGoal(this, 1.0));
        // 3D 飞行随机游荡——配合 FlyingPathNavigation 在三维空间中寻路
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0, 60));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
    }

    // ==================== 飞行导航 ====================

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        nav.setCanPassDoors(true);
        return nav;
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

    // ==================== 受伤/免疫 ====================

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 免疫摔落伤害
        if (source.is(DamageTypes.FALL)) return false;
        return super.hurt(source, amount);
    }

    // ==================== 每 tick 更新 ====================

    @Override
    public void baseTick() {
        super.baseTick();
        if (!level().isClientSide()) {
            tryLayPinkEgg();
        }
    }

    /**
     * 尝试下粉蛋逻辑（原 PinkChickenPr0Procedure）
     * <p>
     * 每 tick 有 1/12000 概率在脚下生成粉蛋实体（掉落物形式），
     * 延迟拾取 10 tick。若粉蛋物品尚未注册则跳过。
     */
    private void tryLayPinkEgg() {
        if (this.random.nextInt(12000) == 0 && this.isAlive()) {
            if (PINK_EGG != Items.AIR) {
                ItemEntity egg = new ItemEntity(level(), getX(), getY(), getZ(),
                        new ItemStack(PINK_EGG));
                egg.setPickUpDelay(10);
                level().addFreshEntity(egg);
            }
        }
    }
}