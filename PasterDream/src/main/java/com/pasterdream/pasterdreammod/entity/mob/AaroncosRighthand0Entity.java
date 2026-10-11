package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.effect.atmosphere.AtmosphereEffectAPI;
import com.pasterdream.pasterdreammod.api.effect.particle.ParticleEmitterAPI;
import com.pasterdream.pasterdreammod.api.effect.particle.ParticleEmitterData;
import com.pasterdream.pasterdreammod.api.effect.particle.processors.CircleSpawnProcessor;
import com.pasterdream.pasterdreammod.entity.projectile.ShadowMagicballEntity;
import com.pasterdream.pasterdreammod.registry.PDArenaBossManager;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 亚伦柯斯右手 (Aaroncos Righthand 0) — BOSS 级飞行敌对生物
 * <p>
 * 行为：
 * <ul>
 *   <li>飞行 BOSS，500HP，免疫击退和火焰伤害</li>
 *   <li>技能循环：魔法弹(3次) → 涡流；终结技「调音图腾」在双 BOSS 低血量/单侧死亡时全场释放一次</li>
 *   <li>HP &lt; 本体血量 1/3 时触发狂暴（鲜血锁链，玩家减益）</li>
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
        PDDebugLogger.mainDebug("[AaroncosRighthand0] 召唤动画完成，BOSS 激活");
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
     * <p>
     * 调音图腾已改为终结技（仅在双 BOSS 低血量 / 单侧死亡时由技能循环释放），
     * 受击不再触发，故此处为空实现。
     */
    @Override
    protected void onHurtTriggerSkill() {
        // 终结技由 tickSkillCycle 按血量条件释放，受击不触发
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
        compound.putBoolean("AaroncosTuneTotemFinale", data.getBoolean("AaroncosTuneTotemFinale"));
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
        if (compound.contains("AaroncosTuneTotemFinale")) {
            data.putBoolean("AaroncosTuneTotemFinale", compound.getBoolean("AaroncosTuneTotemFinale"));
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

    /**
     * 获取首选战斗距离 —— 右手为远程手，飞行追踪 AI 会维持约 10 格输出距离，
     * 避免贴脸后卡在近战普攻不放远程技能（魔法弹 / 涡流 / 终结技）。
     *
     * @return 10.0（格）
     */
    @Override
    protected double getPreferredCombatRange() {
        return 10.0;
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
     *   <li>终结技：双 BOSS 血量均低于 1/5 或单侧死亡时 → 触发调音图腾（全场仅一次）</li>
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

        // 终结技优先判定：调音图腾是低血量范围演出（50 格爆炸 + 全屏特效），
        // 不依赖仇恨目标或攻击距离——贴脸肉搏 / 玩家风筝脱战短暂失去目标时，
        // 只要血量条件满足都应释放，避免「玩家贴脸输出把 BOSS 打死却从没见大招」。
        // 原实现把该判定锁在 distanceToSqr > 36 的远程分支内，贴脸时永不被评估。
        if (tryTriggerTuneTotemFinale()) {
            return;
        }

        // 只对仇恨目标释放技能：无目标不放（脱战/和平模式站桩）
        LivingEntity target = getCombatTarget();
        if (target == null) return;

        int magicball = data.getInt("AaroncosMagicball");
        int vortex = data.getInt("AaroncosVortex");

        // 远程技能：目标稍远（>6 格）才释放（远程手保持距离输出）
        if (this.distanceToSqr(target) > 6.0 * 6.0) {
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

        // 5 tick 后播放蓄力音效（SKILL0 + STONE_BREAK_0）+ 蓄力光团粒子发射器
        queueTask(5, () -> {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.SKILL0.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    PDSounds.STONE_BREAK_0.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

            // 蓄力光团：手部灵魂火焰粒子持续喷射（30 tick 覆盖蓄力过程）
            if (this.level() instanceof ServerLevel sl) {
                ParticleEmitterAPI.spawn(sl, this.position(), 99.0,
                        ParticleEmitterData.builder(ParticleTypes.SOUL_FIRE_FLAME)
                                .position(this.position().add(0, 1.5, 0))
                                .lifetime(30)
                                .particlesPerTick(3)
                                .processor(new CircleSpawnProcessor(0.5f))
                                .build());
            }
        });

        // 35 tick 后锁定当前攻击目标（敌人）+ 爆炸效果 + 发射魔法弹
        queueTask(35, () -> {
            // 技能方向锁定 BOSS 当前攻击目标（敌人），而非最近玩家；
            // 无目标时不强行转向，保持当前朝向发射。
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                this.lookAt(target, 360.0F, 360.0F);
                // 关键：getLookAngle() 基于 yRot，Mob.lookAt 只更新 yHeadRot，
                // 必须同步 yRot/yBodyRot，否则弹道方向仍用旧身体朝向导致打空
                this.setYRot(this.getYHeadRot());
                this.setYBodyRot(this.getYHeadRot());
            }

            // 爆炸粒子（发射瞬间不再触发全屏打击帧，避免频繁黑白闪）
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
                    // 设置发射者，飞弹不会把 BOSS 本体误判为目标而原地爆炸
                    magicball.setOwner(this);
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

            // 64 格内可攻击玩家（排除创造模式）受到涡流影响
            AABB box = this.getBoundingBox().inflate(64.0);
            this.level().getEntitiesOfClass(Player.class, box, this::isAttackablePlayer)
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
     * 终结技：调音图腾 —— 全场仅释放一次的巨大爆炸前置
     * <p>
     * 释放条件（全部满足，且全场未释放过）：
     * <ul>
     *   <li>释放者（右手）当前血量低于自身最大血量的 <b>1/3</b>（强制前置）</li>
     *   <li>另一只手（左手）已死亡，或左手血量低于其最大血量的 1/5</li>
     * </ul>
     * 左手先死但右手血量未达标时不会释放，右手血量降至 1/3 后仍可正常补释放。
     * 效果：
     * <ul>
     *   <li>0 tick：触发 skill_tunetotem 动画 + 抗性提升 + 下坠 + 范围混乱</li>
     *   <li>0 tick：向场内玩家广播高危提示（伤害极高，需尽快打掉图腾）</li>
     *   <li>21 tick：召唤 ShadowTuneTotemEntity（50 HP / 50 格 2500 魔法伤害爆炸）+ 后跳</li>
     *   <li>120 tick：解锁技能状态</li>
     * </ul>
     *
     * @return 是否已释放终结技（true 时技能循环应直接返回）
     */
    private boolean tryTriggerTuneTotemFinale() {
        CompoundTag data = this.getPersistentData();
        if (data.getBoolean("AaroncosTuneTotemFinale")) {
            return false;
        }
        if (this.getHealth() <= 1 || this.level().isClientSide()) {
            return false;
        }

        // 强制前置：释放者（右手）自身血量必须低于最大血量的 1/3。
        // 条件不满足时直接返回且不写任何标记，避免「左手先死但右手血量未达标」时
        // 只评估一次就放弃——后续右手血量降到 1/3 仍可再次评估并释放。
        if (this.getHealth() > this.getMaxHealth() / 3f) {
            return false;
        }

        // 查询场内另一只手（左手）—— 竞技场半径约 99 格
        List<AaroncosLefthand0Entity> lefts = this.level().getEntitiesOfClass(
                AaroncosLefthand0Entity.class, this.getBoundingBox().inflate(99.0));
        AaroncosLefthand0Entity left = lefts.isEmpty() ? null : lefts.get(0);

        // 任一侧死亡 → 可释放
        boolean partnerDead = left == null || !left.isAlive();
        // 左手也低于自身最大血量 1/5 → 可释放
        boolean partnerLow = left != null && left.getHealth() <= left.getMaxHealth() / 5f;

        if (!partnerDead && !partnerLow) {
            return false;
        }

        // —— 释放终结技 ——
        data.putBoolean("AaroncosTuneTotemFinale", true);
        data.putInt("AaroncosSkill", 1);

        this.setAnimation("skill_tunetotem");

        // 抗性提升 + 下坠
        this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 1, false, false));
        this.setDeltaMovement(new Vec3(0, -2, 0));

        // 向 64 格内玩家广播高危提示
        AABB warningBox = this.getBoundingBox().inflate(64.0);
        this.level().getEntitiesOfClass(Player.class, warningBox, Entity::isAlive)
                .forEach(p -> p.displayClientMessage(
                        Component.translatable("message.pasterdream.shadow_tune.finale_warning"),
                        true));

        // 音效（SKILL2 + STONE_BREAK）+ 释放铺垫：短促暗化氛围（黑场闪白与晃动留给图腾爆炸高潮）
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                PDSounds.SKILL2.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                PDSounds.STONE_BREAK.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

        if (this.level() instanceof ServerLevel sl) {
            AtmosphereEffectAPI.darken(sl, this.position(), 99.0, 0.7f, 80);
        }

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
        return true;
    }
}
