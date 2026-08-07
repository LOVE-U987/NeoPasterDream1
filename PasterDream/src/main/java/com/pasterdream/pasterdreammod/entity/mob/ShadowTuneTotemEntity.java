package com.pasterdream.pasterdreammod.entity.mob;

import com.pasterdream.pasterdreammod.api.effect.atmosphere.AtmosphereEffectAPI;
import com.pasterdream.pasterdreammod.api.effect.impact.ImpactFrame;
import com.pasterdream.pasterdreammod.api.effect.impact.ImpactFrameAPI;
import com.pasterdream.pasterdreammod.api.effect.particle.ParticleEmitterAPI;
import com.pasterdream.pasterdreammod.api.effect.particle.ParticleEmitterData;
import com.pasterdream.pasterdreammod.api.effect.particle.processors.CircleSpawnProcessor;
import com.pasterdream.pasterdreammod.api.effect.shake.ScreenShakeAPI;
import com.pasterdream.pasterdreammod.api.effect.shake.ScreenShakeData;
import com.pasterdream.pasterdreammod.api.entity.damage.ConfigurableImmunityEntity;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.*;

import java.util.Comparator;
import java.util.List;

/**
 * 暗影图腾 (Shadow Tune Totem) — 终结技召唤物（50 HP，需玩家尽快打掉）
 * <p>
 * 行为：
 * - 无 AI，静止不动
 * - 免疫火焰、药水、摔落、凋零伤害
 * - 始终播放 idle 动画，死亡时播放 death 动画
 * - 生成后进入倒计时，倒计时结束对 50 格内造成 2500 点魔法伤害的巨型爆炸
 * - 玩家需在爆炸前摧毁图腾（50 HP）以规避毁灭性伤害
 * <p>
 * 注意：Geo 模型文件名为 shadow_rune_totem.geo.json，与注册名不一致，渲染器需自定义模型路径。
 * 渲染：GeckoLib 动画实体
 */
public class ShadowTuneTotemEntity extends ConfigurableImmunityEntity {

    // ==================== 自毁炸弹技能 ====================
    /** 技能倒计时 tick（从 spawn 开始计数），-1 表示未触发或已结束 */
    private int skillTick = -1;
    /** 是否已触发技能（防止 chunk 重载时重复触发） */
    private boolean skillTriggered = false;

    /**
     * 构造暗影图腾实体
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public ShadowTuneTotemEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.xpReward = 0;
        this.setNoAi(true);
    }

    /**
     * 返回默认纹理名称
     *
     * @return 默认纹理名
     */
    @Override
    protected String getDefaultTexture() {
        return "shadow_rune_totem";
    }

    // ======================== 属性 ========================

    /**
     * 创建暗影图腾实体的属性
     *
     * @return 属性构造器
     */
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50)
                .add(Attributes.ARMOR, 5)
                .add(Attributes.ATTACK_DAMAGE, 0)
                .add(Attributes.MOVEMENT_SPEED, 0)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.KNOCKBACK_RESISTANCE, 10);
    }


    // ======================== 音效 ========================

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.DEEPSLATE_BRICKS_BREAK;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.GENERIC_EXPLODE.value();
    }

    // ======================== 受伤/免疫 ========================
    // 伤害免疫逻辑已统一由 ConfigurableImmunityEntity + EntityImmunitySetup 管理
    // 配置位置: EntityImmunitySetup.setupAllImmunities() -> SHADOW_TUNE_TOTEM

    // ======================== 每 tick 更新 ========================

    @Override
    public void baseTick() {
        super.baseTick();
        this.refreshDimensions();
        // 首次生成触发自毁炸弹
        if (!this.level().isClientSide() && !skillTriggered) {
            skillTriggered = true;
            this.triggerSelfDestruct(this.level(), this.getX(), this.getY(), this.getZ());
        }
        // 自毁炸弹倒计时（图腾死亡后立即停止，玩家打掉图腾即可规避爆炸）
        if (!this.level().isClientSide() && skillTick >= 0 && this.isAlive()) {
            skillTick++;
            executeSkillTick();
        }
    }

    // ==================== 自毁炸弹技能实现 ====================

    /**
     * 触发自毁炸弹技能（在 finalizeSpawn 中调用）
     * 原 ShadowTuneTotemPr0Procedure 逻辑
     *
     * @param world 世界访问器
     * @param x     触发 x 坐标
     * @param y     触发 y 坐标
     * @param z     触发 z 坐标
     */
    public void triggerSelfDestruct(LevelAccessor world, double x, double y, double z) {
        if (this.level().isClientSide()) return;

        // 启动倒计时。
        // 提示已由右手终结技释放时的 finale_warning 统一给出（含打图腾指令），
        // 此处不再重复广播 charging，避免动作栏提示被覆盖、玩家看不清。
        skillTick = 0;
    }

    /**
     * 每 tick 执行的技能逻辑
     */
    private void executeSkillTick() {
        if (skillTick > 497) {
            // 超过总时长 → 结束
            skillTick = -1;
            return;
        }

        // 300 tick (15s): 广播即将爆破 + 开始缓慢降低四周亮度（暗化铺垫）
        if (skillTick == 300) {
            broadcastToNearbyPlayers(64, Component.translatable("message.pasterdream.shadow_tune.detonation_soon"));
            // 低强度暗化起步：客户端 AtmosphereHandler 阻尼插值（0.15/tick）缓慢爬升，
            // 覆盖到爆炸后（300+200 > 497 自毁），让玩家先感知"危机逼近"再转入全黑
            if (this.level() instanceof ServerLevel sl) {
                AtmosphereEffectAPI.darken(sl, this.position(), 99.0, 0.3f, 200);
            }
        }

        // 400 tick (20s): 播放 skill 动画 + 音效 + 充能光柱粒子（持续到爆炸前）
        if (skillTick == 400) {
            this.setAnimation("skill");
            // 播放暗影蓄能音效
            this.level().playSound(null, this.blockPosition(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 2.0F, 0.7F);
            // 充能粒子发射器：图腾位置暗影石喷射（82 tick ≈ 充能时长，5/tick）
            if (this.level() instanceof ServerLevel sl) {
                ParticleEmitterAPI.spawn(sl, this.position(), 99.0,
                        ParticleEmitterData.builder((net.minecraft.core.particles.SimpleParticleType) PDParticles.SHADOW_STONE_PARTICLE.particleType())
                                .position(this.position().add(0, 1.5, 0))
                                .lifetime(82)
                                .particlesPerTick(5)
                                .processor(new CircleSpawnProcessor(1.5f))
                                .build());
                // 黑场蓄力铺垫：爆炸动画开始瞬间将亮度快速压到极低（0.95 近乎全黑），
                // 覆盖 300t 的缓慢暗化，营造"即将爆炸"的极致压迫感
                AtmosphereEffectAPI.darken(sl, this.position(), 99.0, 0.95f, 82);
            }
        }

        // 482 tick (~24s): 执行爆炸 —— 半径 50 格内造成 2500 点魔法伤害
        if (skillTick == 482) {
            double x = this.getX();
            double y = this.getY();
            double z = this.getZ();

            // 播放爆炸音效
            this.level().playSound(null, this.blockPosition(),
                    SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 4.0F, 1.0F);

            // 对 50 格内非特殊/非暗影实体造成 2500 魔法伤害
            AABB aabb = new AABB(new Vec3(x, y, z), new Vec3(x, y, z)).inflate(50 / 2d);
            List<Entity> targets = this.level().getEntitiesOfClass(Entity.class, aabb, e -> true)
                    .stream().sorted(Comparator.comparingDouble(e -> e.distanceToSqr(this)))
                    .toList();
            for (Entity target : targets) {
                if (!target.getType().is(TagKey.create(Registries.ENTITY_TYPE,
                        ResourceLocation.fromNamespaceAndPath("pasterdream", "special_entity_tag")))
                        && !target.getType().is(TagKey.create(Registries.ENTITY_TYPE,
                        ResourceLocation.fromNamespaceAndPath("pasterdream", "shadow_mob")))
                        && !(target instanceof Player player && player.isCreative())) {
                    target.hurt(this.damageSources().magic(), 2500.0F);
                }
            }

            // 7.5 格破坏地形爆炸（范围减半，由原 15 格调整为 7.5 格）：
            // BLOCK 交互不受 mobGriefing 限制必然破坏方块；
            // 自定义 ExplosionDamageCalculator 关闭爆炸自身对实体的伤害/击退，避免与上方 2500 魔法伤害叠加
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.explode(this,
                        Explosion.getDefaultDamageSource(serverLevel, this),
                        new ExplosionDamageCalculator() {
                            @Override
                            public boolean shouldDamageEntity(Explosion explosion, Entity entity) {
                                return false;
                            }
                        },
                        x, y, z,
                        7.5f, false, Level.ExplosionInteraction.BLOCK);

                // 持续 5 秒（100 tick）的爆炸粒子发射器：暗影石 + 烟雾混合，
                // 以 CircleSpawnProcessor 向四周缓慢扩散（方向向上、速度 0.3~1.5、半径 6）
                ParticleEmitterAPI.spawn(serverLevel, this.position(), 99.0,
                        ParticleEmitterData.builder((SimpleParticleType) PDParticles.SHADOW_STONE_PARTICLE.particleType())
                                .addParticle(ParticleTypes.SMOKE)
                                .position(this.position())
                                .lifetime(100)
                                .particlesPerTick(6)
                                .processor(new CircleSpawnProcessor(new Vec3(0, 1, 0), 0.3f, 1.5f, 6.0f))
                                .build());
                // 爆炸核心闪光（一次性，vanilla 爆炸指示器）
                serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                        x, y, z, 1, 0, 0, 0, 0);

                // 图腾爆炸：终结技高潮 —— 黑白闪（黑场/白场交替各 3 tick）+ 屏幕晃动 + 暗化峰值
                ImpactFrameAPI.sendImpactFrames(serverLevel, this.position(), 99.0,
                        new ImpactFrame(0.3f, 0.05f, 3, true),   // 黑场
                        new ImpactFrame(0.3f, 0.05f, 3, false),  // 白场
                        new ImpactFrame(0.3f, 0.05f, 3, true),   // 黑场
                        new ImpactFrame(0.3f, 0.05f, 3, false)); // 白场
                // 屏幕晃动：总时长 6 秒（120 tick），in 极短快速达峰、stay 短暂维持、
                // out 长时递减——强度由最高到低随时间衰减；amplitude 0.35 为高强度
                ScreenShakeAPI.sendShake(serverLevel, this.position(), 99.0,
                        ScreenShakeData.builder()
                                .inTime(4).stayTime(8).outTime(108)
                                .amplitude(0.35f).frequency(1.5f)
                                .build());
                AtmosphereEffectAPI.darken(serverLevel, this.position(), 99.0, 0.95f, 100);

                // 50 格爆炸范围扩散粒子：从中心向四周生成多层同心圆环，
                // 让 50 格内的玩家都能直观看到爆炸波及范围（与伤害半径一致）
                for (int radius = 10; radius <= 50; radius += 10) {
                    int ringCount = 24;
                    for (int i = 0; i < ringCount; i++) {
                        double angle = Math.toRadians(i * (360.0 / ringCount));
                        double px = x + Math.cos(angle) * radius;
                        double pz = z + Math.sin(angle) * radius;
                        serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                                px, y + 0.5, pz, 1, 0, 0, 0, 0);
                    }
                }
            }
        }

        // 497 tick (~25s): 自毁
        if (skillTick == 497) {
            if (this.isAlive()) {
                this.hurt(this.damageSources().generic(), this.getHealth() * 2);
                this.discard();
            }
        }
    }

    /**
     * 向附近玩家广播消息
     *
     * @param range   检测半径
     * @param message 消息内容
     */
    private void broadcastToNearbyPlayers(double range, Component message) {
        AABB aabb = new AABB(this.getX(), this.getY(), this.getZ(),
                this.getX(), this.getY(), this.getZ()).inflate(range / 2d);
        for (Player player : this.level().getEntitiesOfClass(Player.class, aabb)) {
            player.displayClientMessage(message, true);
        }
    }

    // ======================== 死亡处理 ========================

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            this.remove(RemovalReason.KILLED);
        }
    }

    // ======================== GeckoLib 动画 ========================

    /**
     * 移动状态动画控制器
     * 暗影图腾存活时播放 idle，死亡时播放 death
     *
     * @param state 动画状态
     * @return 播放状态
     */
    private PlayState movementPredicate(AnimationState<ShadowTuneTotemEntity> state) {
        if (this.getSyncedAnimation().equals("empty")) {
            if (this.isDeadOrDying()) {
                return state.setAndContinue(RawAnimation.begin().thenPlay("death"));
            }
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
