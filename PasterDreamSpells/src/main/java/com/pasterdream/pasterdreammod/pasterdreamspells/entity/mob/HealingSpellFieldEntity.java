package com.pasterdream.pasterdreammod.pasterdreamspells.entity.mob;

import com.pasterdream.pasterdreammod.pasterdreamspells.registry.PDSpellsParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

/**
 * 治疗法术立场 (Healing Spell Field)
 * <p>
 * 治疗法术命中后生成的治疗立场实体，还原自原版 HealingSpellEntityEntity：
 * <ul>
 *   <li>存在 400 tick（20 秒），期间每 tick 为 5*5 区域内的玩家/和平生物
     *       回复最大生命值的 1/400（即每秒 5%）</li>
 *   <li>每 tick 散发治疗/黄色烟雾粒子</li>
 *   <li>立场实体为无敌状态，不会受到任何伤害</li>
 *   <li>无 AI、不移动，循环播放 "idle" 动画（GeckoLib，半透明渲染）</li>
 * </ul>
 *
 * @author PasterDream
 */
public class HealingSpellFieldEntity extends PathfinderMob implements GeoEntity {

    /** 立场存在时长（tick，与原版一致） */
    private static final int LIFETIME_TICKS = 400;

    /** 治疗范围半径（5*5 区域） */
    private static final double HEAL_RADIUS = 5 / 2d;

    /** 每 tick 回复比例分母（maxHealth / 400 = 每秒 5%） */
    private static final float HEAL_FRACTION_DENOMINATOR = 400f;

    /** 循环动画 "idle"（见 animations/entity/healing_spell.animation.json） */
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    /** 剩余存在时间（tick），持久化保存 */
    private int lifeTicks = LIFETIME_TICKS;

    /**
     * 构造治疗法术立场
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public HealingSpellFieldEntity(EntityType<? extends HealingSpellFieldEntity> type, Level level) {
        super(type, level);
    }

    /**
     * 构建实体属性（数值与原版一致）
     *
     * @return 属性构建器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0)
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.FOLLOW_RANGE, 16);
    }

    @Override
    protected void registerGoals() {
        // 立场无任何 AI 行为
    }

    /**
     * 治疗立场无敌，免疫所有伤害源
     *
     * @param source 伤害源
     * @return 始终为 true
     */
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    @Override
    public void baseTick() {
        super.baseTick();
        if (this.level().isClientSide()) {
            return;
        }
        // 寿命结束消散（与原版 HealingSpellEntityPr1Procedure 一致）
        if (--lifeTicks <= 0) {
            this.discard();
            return;
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(PDSpellsParticles.HEALING_SPELL_PARTICLE.holder().get(),
                    this.getX(), this.getY() - 0.5, this.getZ(), 3, 1.7, 0.5, 1.7, 0.05);
            serverLevel.sendParticles(PDSpellsParticles.YELLOW_SMOKE_PARTICLE.holder().get(),
                    this.getX(), this.getY() + 0.1, this.getZ(), 2, 1.7, 0.5, 1.7, 0.05);
            healNearby();
        }
    }

    /**
     * 为立场范围内的玩家与和平生物每 tick 回复最大生命值的 1/400
     * <p>
     * 查询用以自身包围盒为基准的 {@code getBoundingBox().inflate(2.5)}（vanilla
     * 区域效果实体惯用形态）而非以脚底坐标为中心的零体积盒，避免立场模型
     * 下沉/嵌地时判定区间整体偏移。
     */
    private void healNearby() {
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(HEAL_RADIUS),
                living -> living != this && (living instanceof PathfinderMob || living instanceof Player));
        for (LivingEntity living : targets) {
            living.setHealth(living.getHealth() + living.getMaxHealth() / HEAL_FRACTION_DENOMINATOR);
        }
    }

    // ==================== 持久化 ====================

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("LifeTicks", lifeTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("LifeTicks")) {
            lifeTicks = tag.getInt("LifeTicks");
        }
    }

    // ==================== GeckoLib 动画 ====================

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0,
                state -> state.setAndContinue(IDLE_ANIM)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
