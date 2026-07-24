package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.entity.projectile.ShadowMagicballEntity;
import com.pasterdream.pasterdreammod.registry.PDArenaBossManager;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 亚伦柯斯右手 (Aaroncos Righthand 0) — BOSS 级飞行敌对生物
 * <p>
 * 行为：
 * <ul>
 *   <li>飞行 BOSS，500HP，免疫击退和火焰伤害</li>
 *   <li>技能循环：魔法弹(3次) → 涡流，受击触发调音图腾召唤</li>
 *   <li>HP &lt; 100 时触发鲜血锁链（玩家减益）</li>
 *   <li>粉色 BOSS 血条</li>
 * </ul>
 * <p>
 * 动画：
 * <ul>
 *   <li>movement: idle / walk / fly / death</li>
 *   <li>attacking: 触发式攻击动画</li>
 *   <li>procedure: 技能动画（skill_magicball / skill_vortex / skill_tunetotem）</li>
 * </ul>
 *
 * @see AaroncosHandEntity
 */
public class AaroncosRighthand0Entity extends AaroncosHandEntity {

    /**
     * 构造亚伦柯斯右手实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public AaroncosRighthand0Entity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    /**
     * 获取默认纹理名称
     *
     * @return 纹理名称
     */
    @Override
    protected String getDefaultTexture() {
        return "aaroncos_righthand_0";
    }

    // ======================== 子类差异实现 ========================

    /**
     * 获取手名称，用于日志输出。
     *
     * @return 手名称
     */
    @Override
    protected String getHandName() {
        return "AaroncosRighthand0";
    }

    /**
     * 获取召唤动画持续 tick 数。
     *
     * @return 召唤动画时长
     */
    @Override
    protected int getSpawnAnimationTicks() {
        return 40;
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
        PasterDreamMod.LOGGER.debug("[AaroncosRighthand0] 召唤动画完成，BOSS 激活");
    }

    /**
     * 死亡时的维度相关回调。
     *
     * @param serverLevel 当前服务端世界
     */
    @Override
    protected void onHandDeath(ServerLevel serverLevel) {
        if (serverLevel.dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY)) {
            PDArenaBossManager.onRightHandDeath(serverLevel);
        }
    }

    /**
     * 受击时触发的技能。
     */
    @Override
    protected void onHurtTriggerSkill() {
        triggerTuneTotemSkill();
    }

    /**
     * 保存右手专属 NBT 数据。
     *
     * @param compound 待写入的 CompoundTag
     */
    @Override
    protected void saveHandSpecificData(CompoundTag compound) {
        CompoundTag data = this.getPersistentData();
        compound.putInt("AaroncosMagicball", data.getInt("AaroncosMagicball"));
        compound.putInt("AaroncosVortex", data.getInt("AaroncosVortex"));
        compound.putInt("AaroncosTuneTotem", data.getInt("AaroncosTuneTotem"));
    }

    /**
     * 读取右手专属 NBT 数据。
     *
     * @param compound 待读取的 CompoundTag
     */
    @Override
    protected void readHandSpecificData(CompoundTag compound) {
        CompoundTag data = this.getPersistentData();
        if (compound.contains("AaroncosMagicball")) {
            data.putInt("AaroncosMagicball", compound.getInt("AaroncosMagicball"));
        }
        if (compound.contains("AaroncosVortex")) {
            data.putInt("AaroncosVortex", compound.getInt("AaroncosVortex"));
        }
        if (compound.contains("AaroncosTuneTotem")) {
            data.putInt("AaroncosTuneTotem", compound.getInt("AaroncosTuneTotem"));
        }
    }

    /**
     * 获取近战 AI 移动速度倍率。
     *
     * @return 速度倍率
     */
    @Override
    protected double getMeleeAttackSpeed() {
        return 1.0;
    }

    // ======================== 属性 ========================

    /**
     * 创建亚伦柯斯右手实体属性（BOSS 级：500HP/18攻/4甲）
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500)
                .add(Attributes.ARMOR, 4)
                .add(Attributes.ATTACK_DAMAGE, 18)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.FOLLOW_RANGE, 32)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FLYING_SPEED, 0.4);
    }

    // ======================== 技能系统 ========================

    /**
     * 技能循环调度 —— 使用 PersistentData 管理技能状态
     * <p>
     * 技能循环逻辑（原 AaroncosRighthandSkillProcedure）：
     * <ul>
     *   <li>魔法弹(3次, 间隔约20+40 tick) → vortex 累计</li>
     *   <li>vortex == 3 → 触发涡流技能</li>
     *   <li>受击 → 触发调音图腾技能</li>
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

        int magicball = data.getInt("AaroncosMagicball");
        int vortex = data.getInt("AaroncosVortex");

        // 魔法弹阶段：magicball 不为 1 且不为 3 时触发
        if (magicball != 1 && magicball != 3) {
            data.putInt("AaroncosSkill", 1);
            data.putInt("AaroncosMagicball", 1);
            executeMagicballSkill();
            return;
        }

        // 涡流阶段：vortex == 3 时触发
        if (vortex == 3) {
            data.putInt("AaroncosSkill", 1);
            data.putInt("AaroncosVortex", 4);
            executeVortexSkill();
        }
    }

    /**
     * 执行魔法弹技能 —— 播放魔法弹动画 + 音效 + 发射魔法弹
     * <p>
     * 流程：
     * <ul>
     *   <li>0 tick：触发 skill_magicball 动画</li>
     *   <li>5 tick：播放蓄力音效（SKILL0 + STONE_BREAK_0）</li>
     *   <li>35 tick：锁定最近玩家 + 爆炸粒子 + 发射 ShadowMagicballEntity 弹幕</li>
     *   <li>20 tick：累加 vortex 计数</li>
     *   <li>40 tick：解锁技能状态</li>
     *   <li>90 tick：重置 magicball 计数</li>
     * </ul>
     */
    private void executeMagicballSkill() {
        CompoundTag data = this.getPersistentData();
        this.setAnimation("skill_magicball");

        // 5 tick 后播放蓄力音效（SKILL0 + STONE_BREAK_0）
        queueTask(5, () -> {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SKILL0.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.STONE_BREAK_0.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        });

        // 35 tick 后锁定最近玩家 + 爆炸效果 + 发射魔法弹
        queueTask(35, () -> {
            // 锁定最近玩家
            Player nearest = this.level().getNearestPlayer(this, 64.0);
            if (nearest != null) {
                this.lookAt(nearest, 360.0F, 360.0F);
            }

            // 爆炸粒子
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        16, 1, 1, 1, 0.2);
                sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                        64, 1, 1, 1, 0.2);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SKILL1.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

            // 发射方向上的粒子（保留视觉余韵）
            Vec3 look = this.getLookAngle();
            double tx = this.getX() + look.x * 1.5;
            double ty = this.getY() + look.y;
            double tz = this.getZ() + look.z * 1.5;

            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, tx, ty, tz,
                        8, 0.3, 0.3, 0.3, 0.1);
            }

            // 实际生成 ShadowMagicballEntity 弹幕实体
            if (this.level() instanceof ServerLevel sl) {
                ShadowMagicballEntity magicball = PDEntities.SHADOW_MAGICBALL.get().create(sl);
                if (magicball != null) {
                    magicball.moveTo(this.getX() + look.x * 1.5,
                            this.getY() + look.y * 1.5,
                            this.getZ() + look.z * 1.5);
                    magicball.setDeltaMovement(look.x * 3, look.y * 2, look.z * 3);
                    sl.addFreshEntity(magicball);
                }
            }
        });

        // 20 tick 后递增 vortex 计数
        queueTask(20, () -> data.putInt("AaroncosVortex", data.getInt("AaroncosVortex") + 1));

        // 40 tick 后解锁技能状态
        queueTask(40, () -> data.putInt("AaroncosSkill", 0));

        // 90 tick 后重置 magicball 计数
        queueTask(90, () -> {
            if (data.getInt("AaroncosMagicball") == 1) {
                data.putInt("AaroncosMagicball", 0);
            }
        });
    }

    /**
     * 执行涡流技能 —— 播放涡流动画 + 范围减益 + 暗影漩涡方块
     * <p>
     * 流程：
     * <ul>
     *   <li>0 tick：触发 skill_vortex 动画 + 下坠 + 缓慢效果</li>
     *   <li>42 tick：涡流爆发，生成暗影漩涡方块（每个玩家脚下 + BOSS 脚下） + 伤害 + 减益 + 音效</li>
     *   <li>120 tick：重置技能和 vortex 计数</li>
     * </ul>
     */
    private void executeVortexSkill() {
        CompoundTag data = this.getPersistentData();
        this.setAnimation("skill_vortex");

        // 下坠
        this.setDeltaMovement(new Vec3(0, -5, 0));
        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 4, false, false));

        // 42 tick 后涡流爆发：范围伤害 + 粒子 + 玩家减益 + 暗影漩涡方块
        queueTask(42, () -> {
            if (this.level() instanceof ServerLevel sl) {
                sl.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(),
                        24, 2, 1, 2, 0.3);
                sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                        128, 2, 1, 2, 0.5);

                // 在 BOSS 位置生成暗影漩涡方块
                BlockPos bossPos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
                sl.setBlockAndUpdate(bossPos, PDBlocks.SHADOW_VORTEX.get().defaultBlockState());
            }

            // 64 格内玩家受到涡流影响
            AABB box = this.getBoundingBox().inflate(64.0);
            this.level().getEntitiesOfClass(Player.class, box, Entity::isAlive)
                    .forEach(p -> {
                        p.hurt(this.damageSources().mobAttack(this), 4.0F);
                        p.setDeltaMovement(new Vec3(0, 0.2, 0));
                        // 附加缓慢效果
                        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 2, false, false));

                        // 在玩家位置生成暗影漩涡方块
                        if (this.level() instanceof ServerLevel sl) {
                            BlockPos playerPos = BlockPos.containing(p.getX(), p.getY(), p.getZ());
                            sl.setBlockAndUpdate(playerPos, PDBlocks.SHADOW_VORTEX.get().defaultBlockState());
                        }
                    });

            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SHADOW_VORTEX.get(), SoundSource.HOSTILE, 0.8F, 1.0F);
        });

        // 120 tick 后重置技能和 vortex 计数
        queueTask(120, () -> {
            data.putInt("AaroncosSkill", 0);
            data.putInt("AaroncosVortex", 0);
        });
    }

    /**
     * 受击触发的调音图腾技能 —— 播放调音图腾动画 + 召唤调音图腾 + 范围减益
     * <p>
     * 流程：
     * <ul>
     *   <li>0 tick：触发 skill_tunetotem 动画 + 抗性提升 + 下坠 + 范围混乱 + STONE_BREAK/SKILL2 音效</li>
     *   <li>10 tick：自身缓慢 + 粒子</li>
     *   <li>15 tick：二次粒子</li>
     *   <li>21 tick：召唤 ShadowTuneTotemEntity + 后跳</li>
     *   <li>120 tick：解锁技能状态</li>
     *   <li>600 tick：重置调音图腾冷却</li>
     * </ul>
     */
    private void triggerTuneTotemSkill() {
        CompoundTag data = this.getPersistentData();
        int skill = data.getInt("AaroncosSkill");
        boolean sw = data.getBoolean("AaroncosSwitch");
        int totem = data.getInt("AaroncosTuneTotem");

        if (skill == 0 && sw && totem != 1 && this.getHealth() > 1) {
            data.putInt("AaroncosSkill", 1);
            data.putInt("AaroncosTuneTotem", 1);

            this.setAnimation("skill_tunetotem");

            // 抗性提升 + 下坠
            this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 1, false, false));
            this.setDeltaMovement(new Vec3(0, -2, 0));

            // 初始 AoE 混乱效果：15 格内非暗影标签 LivingEntity 施加 CONFUSION 60 tick
            List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(15.0),
                    e -> e != this && e.isAlive() && !e.getType().is(SHADOW_MOB_TAG));
            for (LivingEntity entity : entities) {
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false));
            }

            // 音效（SKILL2 + STONE_BREAK）
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SKILL2.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.STONE_BREAK.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

            // 10t 后粒子 + 自身缓慢
            queueTask(10, () -> {
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 4, false, false));
                if (this.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                            32, 1, 0, 1, 0.5);
                }
            });

            // 15t 后二次粒子
            queueTask(15, () -> {
                if (this.level() instanceof ServerLevel sl) {
                    sl.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(),
                            32, 1, 0, 1, 0.5);
                }
            });

            // 21t 后召唤调音图腾 + 后跳
            queueTask(21, () -> {
                // 召唤 ShadowTuneTotemEntity（前方 2 格）
                if (this.level() instanceof ServerLevel serverLevel) {
                    ShadowTuneTotemEntity tuneTotem = PDEntities.SHADOW_TUNE_TOTEM.get().create(serverLevel);
                    if (tuneTotem != null) {
                        tuneTotem.moveTo(this.getX() + this.getLookAngle().x * 2,
                                this.getY(),
                                this.getZ() + this.getLookAngle().z * 2);
                        tuneTotem.setYRot(this.getRandom().nextFloat() * 360.0F);
                        serverLevel.addFreshEntity(tuneTotem);
                    }
                }
                // 后跳
                Vec3 look = this.getLookAngle();
                this.setDeltaMovement(new Vec3(look.x * (-1), 0, look.z * (-1)));
            });

            // 120 tick 后解锁技能
            queueTask(120, () -> data.putInt("AaroncosSkill", 0));
            // 600 tick 后重置调音图腾冷却
            queueTask(600, () -> data.putInt("AaroncosTuneTotem", 0));
        }
    }
}