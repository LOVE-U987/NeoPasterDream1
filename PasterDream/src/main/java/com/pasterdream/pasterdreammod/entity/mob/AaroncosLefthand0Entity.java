package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.effect.ghost.GhostEffectAPI;
import com.pasterdream.pasterdreammod.registry.PDArenaBossManager;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 亚伦柯斯左手 (Aaroncos Lefthand 0) — BOSS 级飞行敌对生物
 * <p>
 * 行为：
 * <ul>
 *   <li>飞行 BOSS，500HP，免疫击退和火焰伤害</li>
 *   <li>技能循环：冲刺(3次) → 重击，受击触发剑雨技能</li>
 *   <li>HP &lt; 100 时触发鲜血锁链（召唤暗影之手 + 玩家减益）</li>
 *   <li>粉色 BOSS 血条</li>
 * </ul>
 * <p>
 * 动画：
 * <ul>
 *   <li>movement: idle / walk / fly / death</li>
 *   <li>attacking: 触发式攻击动画</li>
 *   <li>procedure: 技能动画（skill_sprint / skill_hit / skill_sword）</li>
 * </ul>
 *
 * @see AaroncosHandEntity
 */
public class AaroncosLefthand0Entity extends AaroncosHandEntity {

    /** 剑雨技能各段攻击的触发时刻（tick，相对技能开始），提取为常量避免每次触发重复创建数组 */
    private static final int[] SWORD_RAIN_HIT_TICKS = {57, 70, 83, 88, 95, 105, 112};

    /**
     * 构造亚伦柯斯左手实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public AaroncosLefthand0Entity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    /**
     * 获取默认纹理名称
     *
     * @return 纹理名称
     */
    @Override
    protected String getDefaultTexture() {
        return "aaroncos_lefthand_0";
    }

    // ======================== 子类差异实现 ========================

    /**
     * 获取手名称，用于日志输出。
     *
     * @return 手名称
     */
    @Override
    protected String getHandName() {
        return "AaroncosLefthand0";
    }

    /**
     * 获取召唤动画持续 tick 数。
     *
     * @return 召唤动画时长
     */
    @Override
    protected int getSpawnAnimationTicks() {
        return 80;
    }

    /**
     * 召唤动画完成后的回调。
     *
     * @param serverLevel 当前服务端世界
     */
    @Override
    protected void onSpawnAnimationComplete(ServerLevel serverLevel) {
        if (serverLevel.dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY)) {
            PDArenaBossManager.onSpawnAnimationComplete(serverLevel);
        }
        PDDebugLogger.mainDebug("[AaroncosLefthand0] 召唤动画完成，BOSS 激活");
    }

    /**
     * 死亡时的维度相关回调。
     *
     * @param serverLevel 当前服务端世界
     */
    @Override
    protected void onHandDeath(ServerLevel serverLevel) {
        if (serverLevel.dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY)) {
            PDArenaBossManager.onLeftHandDeath(serverLevel);
        }
    }

    /**
     * 受击时触发的技能。
     */
    @Override
    protected void onHurtTriggerSkill() {
        triggerSwordSkill();
    }

    /**
     * 保存左手专属 NBT 数据。
     *
     * @param compound 待写入的 CompoundTag
     */
    @Override
    protected void saveHandSpecificData(CompoundTag compound) {
        CompoundTag data = this.getPersistentData();
        compound.putInt("AaroncosSprint", data.getInt("AaroncosSprint"));
        compound.putInt("AaroncosHit", data.getInt("AaroncosHit"));
        compound.putInt("AaroncosSword", data.getInt("AaroncosSword"));
    }

    /**
     * 读取左手专属 NBT 数据。
     *
     * @param compound 待读取的 CompoundTag
     */
    @Override
    protected void readHandSpecificData(CompoundTag compound) {
        CompoundTag data = this.getPersistentData();
        if (compound.contains("AaroncosSprint")) {
            data.putInt("AaroncosSprint", compound.getInt("AaroncosSprint"));
        }
        if (compound.contains("AaroncosHit")) {
            data.putInt("AaroncosHit", compound.getInt("AaroncosHit"));
        }
        if (compound.contains("AaroncosSword")) {
            data.putInt("AaroncosSword", compound.getInt("AaroncosSword"));
        }
    }

    /**
     * 获取近战 AI 移动速度倍率。
     *
     * @return 速度倍率
     */
    @Override
    protected double getMeleeAttackSpeed() {
        return 1.2;
    }

    // ======================== 属性 ========================

    /**
     * 创建亚伦柯斯左手实体属性（BOSS 级：500HP/20攻/10甲）
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500)
                .add(Attributes.ARMOR, 10)
                .add(Attributes.ATTACK_DAMAGE, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FLYING_SPEED, 0.25);
    }

    // ======================== 技能系统 ========================

    /**
     * 技能循环调度 —— 使用 PersistentData 管理技能状态
     * <p>
     * 技能循环逻辑（原 AaroncosLefthandSkillProcedure）：
     * <ul>
     *   <li>冲刺(3次) → skill_hit 累计</li>
     *   <li>skill_hit == 3 → 触发重击技能</li>
     * </ul>
     * <p>
     * 召唤状态下技能系统被禁用。
     */
    @Override
    protected void tickSkillCycle() {
        // 召唤状态下禁用技能
        if (isSummoning()) return;

        CompoundTag data = this.getPersistentData();
        boolean sw = data.getBoolean("AaroncosSwitch");
        int skill = data.getInt("AaroncosSkill");

        if (!sw || skill == 1) return;

        // 只对仇恨目标释放技能：无目标不放（脱战/和平模式站桩）
        LivingEntity target = getCombatTarget();
        if (target == null) return;

        int sprint = data.getInt("AaroncosSprint");
        int hit = data.getInt("AaroncosHit");

        // 近战技能：目标近身（<12 格）才释放冲刺/重击
        if (this.distanceToSqr(target) < 12.0 * 12.0) {
            // 冲刺阶段：sprint 不为 1 且不为 3 时触发
            if (sprint != 1 && sprint != 3) {
                data.putInt("AaroncosSkill", 1);
                data.putInt("AaroncosSprint", 1);
                executeSprintSkill();
                return;
            }

            // 重击阶段：skill_hit == 3 时触发
            if (hit == 3) {
                data.putInt("AaroncosSkill", 1);
                data.putInt("AaroncosHit", 4);
                executeHitSkill();
            }
        }
    }

    /**
     * 执行冲刺技能 —— 播放冲刺动画 + 音效 + 冲锋伤害
     */
    private void executeSprintSkill() {
        CompoundTag data = this.getPersistentData();
        this.setAnimation("skill_sprint");

        // 5 tick 后播放音效（剑波 + 石裂）
        queueTask(5, () -> {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SWORD_WAVE.get(), SoundSource.HOSTILE, 1.2F, 1.0F);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.STONE_BREAK_0.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        });

        // 16 tick 后锁定当前攻击目标（敌人）并冲锋
        queueTask(16, () -> {
            // 技能方向锁定 BOSS 当前攻击目标（敌人），而非最近玩家；
            // 无目标时不强行转向。
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                this.lookAt(target, 360.0F, 360.0F);
                // 关键：getLookAngle() 基于 yRot，Mob.lookAt 只更新 yHeadRot，
                // 必须同步 yRot/yBodyRot，否则冲锋方向仍用旧身体朝向导致打空
                this.setYRot(this.getYHeadRot());
                this.setYBodyRot(this.getYHeadRot());
                Vec3 look = this.getLookAngle();
                this.setDeltaMovement(look.x * 2.8, look.y - 0.2, look.z * 2.8);

                // 冲锋过程开启残影拖尾（半透明虚影跟随 BOSS 位移）
                if (this.level() instanceof ServerLevel sl) {
                    GhostEffectAPI.startGhostTrail(sl, this.position(), 99.0,
                            this.getId(), 24, 40);
                }
            }
        });

        // 17 tick 后爆炸粒子 + 范围伤害
        queueTask(17, () -> {
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        16, 1, 1, 1, 0.2);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0F, 1.0F);
            this.hurtNearbyPlayers(6.0, 7.0F);
        });

        // 24 tick 后二次爆炸
        queueTask(24, () -> {
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        16, 1, 1, 1, 0.2);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0F, 1.0F);
            this.hurtNearbyPlayers(6.0, 7.0F);
        });

        // 20 tick 后递增 skill_hit
        queueTask(20, () -> data.putInt("AaroncosHit", data.getInt("AaroncosHit") + 1));

        // 40 tick 后解锁技能状态
        queueTask(40, () -> data.putInt("AaroncosSkill", 0));

        // 120 tick 后重置 sprint 计数
        queueTask(120, () -> {
            if (data.getInt("AaroncosSprint") == 1) {
                data.putInt("AaroncosSprint", 0);
            }
        });
    }

    /**
     * 执行重击技能 —— 播放重击动画 + 三段式冲击波
     * <p>
     * 三段 AoE 范围递增（15/19/23 格），伤害递增（6/7/8 点），
     * 每段对范围内非暗影系 LivingEntity 造伤害并附加 CONFUSION 10 tick。
     */
    private void executeHitSkill() {
        CompoundTag data = this.getPersistentData();
        this.setAnimation("skill_hit");

        // 第一段重击（10 tick 后起跳，19 tick 后落地爆炸）—— 15 格 AoE / 6 点伤害
        queueTask(10, () -> this.setDeltaMovement(new Vec3(0, 2, 0)));
        queueTask(15, () -> this.setDeltaMovement(new Vec3(0, -10, 0)));
        queueTask(19, () -> {
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        12, 1, 1, 1, 0.5);
                sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                        64, 2, 1, 2, 0.5);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SWORD1.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            this.hurtNearbyLivingWithConfusion(15.0, 6.0F, 10);
            this.pushNearbyPlayers(0, 0.5, 0);
        });

        // 第二段重击（21 tick 后）—— 19 格 AoE / 7 点伤害
        queueTask(21, () -> this.setDeltaMovement(new Vec3(0, 3, 0)));
        queueTask(27, () -> this.setDeltaMovement(new Vec3(0, -10, 0)));
        queueTask(30, () -> {
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        16, 2, 1, 2, 0.5);
                sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                        128, 3, 1, 3, 0.5);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SWORD1.get(), SoundSource.HOSTILE, 1.1F, 1.0F);
            this.hurtNearbyLivingWithConfusion(19.0, 7.0F, 10);
            this.pushNearbyPlayers(0, 1.0, 0);
        });

        // 第三段重击（42 tick 后）—— 23 格 AoE / 8 点伤害
        queueTask(42, () -> this.setDeltaMovement(new Vec3(0, 4, 0)));
        queueTask(48, () -> this.setDeltaMovement(new Vec3(0, -10, 0)));
        queueTask(53, () -> {
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        24, 3, 1, 3, 0.5);
                sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                        192, 4, 1, 4, 0.5);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SWORD_WAVE.get(), SoundSource.HOSTILE, 1.2F, 1.0F);
            this.hurtNearbyLivingWithConfusion(23.0, 8.0F, 10);
            this.pushNearbyPlayers(0, 1.5, 0);
        });

        // 100 tick 后重置技能和 hit 计数
        queueTask(100, () -> {
            data.putInt("AaroncosSkill", 0);
            data.putInt("AaroncosHit", 0);
        });
    }

    /**
     * 受击触发的剑雨技能 —— 播放剑雨动画 + 范围混乱 + 暗影石粒子 + 多段 AoE 伤害
     * <p>
     * 行为：
     * <ul>
     *   <li>初始阶段：自身抗性提升、播放 SHADOW_SWORD 与 STONE_BREAK 音效</li>
     *   <li>初始 AoE 混乱：对 30 格内非暗影 LivingEntity 施加 CONFUSION 60 tick</li>
     *   <li>多段剑雨（57/70/83/88/95/105/112 tick）：粒子 + 16 格 AoE 8 点伤害 + SWORD_WAVE 音效</li>
     *   <li>对玩家额外施加 CONFUSION 20 tick</li>
     * </ul>
     */
    private void triggerSwordSkill() {
        CompoundTag data = this.getPersistentData();
        int skill = data.getInt("AaroncosSkill");
        boolean sw = data.getBoolean("AaroncosSwitch");
        int sword = data.getInt("AaroncosSword");

        if (skill == 0 && sw && sword != 1 && this.getHealth() > 1) {
            data.putInt("AaroncosSkill", 1);
            data.putInt("AaroncosSword", 1);

            this.setAnimation("skill_sword");

            // 抗性提升 + 下坠
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120, 1, false, false));
            this.setDeltaMovement(new Vec3(0, -2, 0));

            // 音效（剑雨触发 + 石裂）
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SHADOW_SWORD.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.STONE_BREAK.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

            // 初始 AoE 混乱效果（30 格）—— 对非暗影 LivingEntity（排除创造玩家）施加 CONFUSION 60 tick
            List<LivingEntity> initialEntities = this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(30.0),
                    e -> e != this && e.isAlive() && !e.getType().is(SHADOW_MOB_TAG)
                            && !(e instanceof Player p && p.isCreative()));
            for (LivingEntity entity : initialEntities) {
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false));
            }

            // 15 tick 后粒子 + 缓慢
            queueTask(15, () -> {
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 4, false, false));
                if (this.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                            128, 1, 2, 1, 0.5);
                }
            });

            // 25 tick 后二次粒子
            queueTask(25, () -> {
                if (this.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                            128, 1, 2, 1, 0.5);
                }
            });

            // 57 tick 后开始多段剑雨 —— 粒子 + 16 格 AoE 8 点伤害 + 音效
            for (int t : SWORD_RAIN_HIT_TICKS) {
                queueTask(t, () -> {
                    if (this.level() instanceof ServerLevel sl) {
                        double px = this.getX() + (this.getRandom().nextFloat() - 0.5) * 6;
                        double pz = this.getZ() + (this.getRandom().nextFloat() - 0.5) * 6;
                        sl.sendParticles(ParticleTypes.SWEEP_ATTACK, px, this.getY() - 1, pz,
                                1, 0, 0, 0, 0);
                    }

                    // 16 格 AoE 伤害判定（排除自身、暗影系友军与创造玩家）
                    List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class,
                            this.getBoundingBox().inflate(16.0),
                            e -> e != this && e.isAlive() && !e.getType().is(SHADOW_MOB_TAG)
                                    && !(e instanceof Player p && p.isCreative()));
                    for (LivingEntity entity : entities) {
                        entity.hurt(this.damageSources().generic(), 8.0F);
                        // 对玩家额外施加 CONFUSION 20 tick
                        if (entity instanceof Player player) {
                            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20, 0, false, false));
                        }
                    }

                    // 播放剑波音效
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                            PDSounds.SWORD_WAVE.get(), SoundSource.HOSTILE, 1.0F, 1.2F);
                });
            }

            // 140 tick 后解锁技能
            queueTask(140, () -> data.putInt("AaroncosSkill", 0));
            // 420 tick 后重置剑雨冷却
            queueTask(420, () -> data.putInt("AaroncosSword", 0));
        }
    }
}
