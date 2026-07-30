package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.api.effect.MobEffectResult;
import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.entity.mob.WeakenessTerrorbeakEntity;
import com.pasterdream.pasterdreammod.registry.items.PDItemsArmor;
import com.pasterdream.pasterdreammod.registry.items.PDItemsCurios;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * 状态效果（BUFF/DEBUFF）注册类
 * <p>
 * 使用 {@link MobEffectAPI} 的 Facade+Builder 模式注册，
 * 支持链式配置分类、颜色、着色器、粒子、回调等。
 * <p>
 * 实现方式说明（对应原模组 potion/*.java + procedures/*.java）：
 * <ul>
 *   <li><b>纯属性修饰符型</b>：直接 {@code MobEffectAPI.REGISTRY.register} + vanilla
 *       {@link MobEffect#addAttributeModifier}（效果生效自动施加、过期自动移除，随等级 ×(amplifier+1) 缩放，
 *       与原版 1.20.1 行为一致）。</li>
 *   <li><b>tick/回调逻辑型</b>：{@link MobEffectAPI#createEffect} 链式 Builder；
 *       onTick 由 {@code PasterDreamEffect#applyEffectTick} 每 tick 驱动，
 *       onApply/onRemove 由 {@link PDEffectEvents} 监听 NeoForge
 *       {@code MobEffectEvent.Added/Remove/Expired} 统一派发。</li>
 *   <li><b>混合型</b>（修饰符 + 回调）：直接注册匿名子类，按需重写
 *       {@code applyEffectTick} / {@code onEffectAdded}。</li>
 * </ul>
 * 颜色规则：原版十进制负数颜色 → {@code 0xFF} 前缀十六进制（N &amp; 0xFFFFFFFF），
 * 原版正数颜色保持原值。
 *
 * @see MobEffectAPI
 * @see PDEffectEvents
 * @see com.pasterdream.pasterdreammod.api.effect.builder.MobEffectBuilder
 * @see com.pasterdream.pasterdreammod.api.effect.base.PasterDreamEffect
 */
public class PDEffects {

    /** 经验提升效果触发概率（每 tick） */
    private static final int EXPUP_CHANCE = 10;
    /** 经验提升效果随机范围上限 */
    private static final int EXPUP_DENOMINATOR = 1000;

    // ==================== 染梦维度核心效果 ====================

    /**
     * 梦境祝福 (dreamwish_buff)
     * <p>
     * 粉红色有益效果，进入染梦维度时自动获取。
     * 纯标记效果，无附加逻辑。
     */
    public static final MobEffectResult DREAMWISH_BUFF =
            MobEffectAPI.createEffect("dreamwish_buff")
                    .beneficial()
                    .color(0xFFFA8CE6)
                    .build();

    /**
     * 染梦附魔 (dyedreamup_buff)
     * <p>
     * 亮粉色有益效果，染梦维度的通用增幅状态。
     * 纯标记效果，无附加逻辑。
     */
    public static final MobEffectResult DYEDREAMUP_BUFF =
            MobEffectAPI.createEffect("dyedreamup_buff")
                    .beneficial()
                    .color(0xFFFF80B2)
                    .build();

    /**
     * 染梦香水 (dyedream_perfume_buff)
     * <p>
     * 米白色有益效果，使用染梦香水后获得。
     * 纯标记效果，无附加逻辑。
     */
    public static final MobEffectResult DYEDREAM_PERFUME_BUFF =
            MobEffectAPI.createEffect("dyedream_perfume_buff")
                    .beneficial()
                    .color(0xFFEACDBD)
                    .build();

    // ==================== 工具类效果 ====================

    /**
     * 经验提升 (expup_buff)
     * <p>
     * 淡紫色有益效果，每 tick 有 1/1000 概率给予 1 点经验。
     * 演示 {@link MobEffectAPI} 的 onTick 回调用法。
     */
    public static final MobEffectResult EXPUP_BUFF =
            MobEffectAPI.createEffect("expup_buff")
                    .beneficial()
                    .color(0xFFABABD5)
                    .onTick((entity, amplifier) -> {
                        // 每 tick 概率给 1 点经验（使用实体自身随机源保证世界种子一致性）
                        if (Mth.nextInt(entity.getRandom(), 1, EXPUP_DENOMINATOR) <= EXPUP_CHANCE) {
                            if (entity instanceof net.minecraft.world.entity.player.Player player) {
                                player.giveExperiencePoints(1);
                            }
                        }
                    })
                    .build();

    /**
     * 菊茶效果 (goldenrod_tea_buff)
     * <p>
     * 暖橙色有益效果，饮用黄金菊茶后获得。
     * 每 tick 自动消除饥饿和反胃效果。
     */
    public static final MobEffectResult GOLDENROD_TEA_BUFF =
            MobEffectAPI.createEffect("goldenrod_tea_buff")
                    .beneficial()
                    .color(0xFFFF9F6A)
                    .onTick((entity, amplifier) -> {
                        // 每 tick 移除饥饿和反胃效果（原版逻辑）
                        entity.removeEffect(MobEffects.HUNGER);
                        entity.removeEffect(MobEffects.CONFUSION);
                    })
                    .build();

    // ==================== 防风效果 ====================

    /**
     * 防风效果 (windproof_buff)
     * <p>
     * 淡蓝色有益效果，水母系列食物提供。
     * 纯标记效果，无 tick 逻辑，可抵御风系 debuff 或环境风阻（待实装）。
     */
    public static final MobEffectResult WINDPROOF_BUFF =
            MobEffectAPI.createEffect("windproof_buff")
                    .beneficial()
                    .color(0xBBBBF6)
                    .build();

    // ==================== 烹饪增益效果 ====================

    /**
     * 烹饪增益 (cook_buff)
     * 玫红色有益效果：SAN_VARIABILITY +1.2（原版 CookBuffMobEffect）。
     */
    public static final DeferredHolder<MobEffect, MobEffect> COOK_BUFF =
            MobEffectAPI.REGISTRY.register("cook_buff",
                    () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xEE3373) {
                    }
                            .addAttributeModifier(PDAttributes.SAN_VARIABILITY, modifierId("cook_buff_0"),
                                    1.2, AttributeModifier.Operation.ADD_VALUE));

    // ==================== 实体技能相关状态效果 ====================

    /**
     * 混乱效果 (confusion_buff)
     * <p>
     * 深紫色有害效果，由恐怖尖喙咆哮、震动水晶等技能施加。
     * 每 tick 施加原版反胃（屏幕抖动）和缓慢效果，模拟眩晕/致盲的实战效果。
     */
    public static final MobEffectResult CONFUSION_BUFF =
            MobEffectAPI.createEffect("confusion_buff")
                    .harmful()
                    .color(0xFF4A0080)
                    .onTick((entity, amplifier) -> {
                        // 施加反胃效果——屏幕抖动（刷新持续防止自然消失）
                        entity.addEffect(new MobEffectInstance(
                                MobEffects.CONFUSION, 100, 0, false, false, false));
                        // 施加缓慢效果——限制移动
                        entity.addEffect(new MobEffectInstance(
                                MobEffects.MOVEMENT_SLOWDOWN, 100, 1, false, false, false));
                    })
                    .build();

    /**
     * 暗影沉默效果 (shadow_silence_buff)
     * 暗紫色有害效果，被沉默的实体无法使用技能。
     */
    public static final MobEffectResult SHADOW_SILENCE_BUFF =
            MobEffectAPI.createEffect("shadow_silence_buff")
                    .harmful()
                    .color(0xFF2A0040)
                    .build();

    /**
     * 压迫效果 (oppression_buff)
     * 深红色有害效果：SAN_VARIABILITY -9.6；牛奶不可清除（原版 getCurativeItems 空列表）。
     */
    public static final DeferredHolder<MobEffect, MobEffect> OPPRESSION_BUFF =
            MobEffectAPI.REGISTRY.register("oppression_buff",
                    () -> new MobEffect(MobEffectCategory.HARMFUL, 0xFF800000) {
                    }
                            .addAttributeModifier(PDAttributes.SAN_VARIABILITY, modifierId("oppression_buff_0"),
                                    -9.6, AttributeModifier.Operation.ADD_VALUE));

    // ==================== 法术效果（还原自原版法术模块） ====================
    // 说明：这两个效果的核心是属性修饰符，使用 vanilla 原生的
    // MobEffect.addAttributeModifier（随效果自动施加/过期自动移除，无需回调），
    // 因此直接在 MobEffectAPI.REGISTRY 上注册，而非经过 Builder。

    /**
     * 狂暴法术增益 (fury_spell_buff)
     * 紫红色有益效果：攻击力 +4、攻击速度 +3、移动速度 +0.05、
     * SKILLCD/TELEPORTATIONCD -0.3（与原版 FurySpellBuffPr0 一致）。
     */
    public static final DeferredHolder<MobEffect, MobEffect> FURY_SPELL_BUFF =
            MobEffectAPI.REGISTRY.register("fury_spell_buff",
                    () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFFB655EC) {
                    }
                            .addAttributeModifier(Attributes.ATTACK_DAMAGE,
                                    ResourceLocation.fromNamespaceAndPath("pasterdream", "fury_spell_buff_0"),
                                    4, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                                    ResourceLocation.fromNamespaceAndPath("pasterdream", "fury_spell_buff_1"),
                                    0.05, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.ATTACK_SPEED,
                                    ResourceLocation.fromNamespaceAndPath("pasterdream", "fury_spell_buff_2"),
                                    3, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(PDAttributes.SKILLCD, modifierId("fury_spell_buff_3"),
                                    -0.3, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(PDAttributes.TELEPORTATIONCD, modifierId("fury_spell_buff_4"),
                                    -0.3, AttributeModifier.Operation.ADD_VALUE));

    /**
     * 冰冻法术减益 (ice_spell_buff)
     * 冰蓝色有害效果：移动速度 -1（完全定身）、攻击力 -100（数值与原版一致）。
     */
    public static final DeferredHolder<MobEffect, MobEffect> ICE_SPELL_BUFF =
            MobEffectAPI.REGISTRY.register("ice_spell_buff",
                    () -> new MobEffect(MobEffectCategory.HARMFUL, 0xFFB8ECF6) {
                    }
                            .addAttributeModifier(Attributes.MOVEMENT_SPEED,
                                    ResourceLocation.fromNamespaceAndPath("pasterdream", "ice_spell_buff_0"),
                                    -1, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.ATTACK_DAMAGE,
                                    ResourceLocation.fromNamespaceAndPath("pasterdream", "ice_spell_buff_1"),
                                    -100, AttributeModifier.Operation.ADD_VALUE));

    // ════════════════════════════════════════════════════════════════════
    // 以下为 2026-07-26 批量还原的 34 个状态效果
    // 对照原版 init/PasterdreamModMobEffects.java + potion/*.java + procedures/
    // ════════════════════════════════════════════════════════════════════

    // ==================== A. 纯属性修饰符型（vanilla addAttributeModifier） ====================

    /**
     * 振奋 (cheerup_buff) — 原版 CheerupBuffMobEffect
     * <p>
     * 有益 0xFFFF7E7A（原 -33158）。San 值高于阈值时获得：
     * 瞬身术冷却 -0.1、移速 +0.05、攻速 +0.05、战技冷却 -0.1（均为 ADD_VALUE，对应旧 ADDITION）。
     */
    public static final DeferredHolder<MobEffect, MobEffect> CHEERUP_BUFF =
            MobEffectAPI.REGISTRY.register("cheerup_buff",
                    () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFFFF7E7A) {
                    }
                            .addAttributeModifier(PDAttributes.TELEPORTATIONCD, modifierId("cheerup_buff_0"),
                                    -0.1, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.MOVEMENT_SPEED, modifierId("cheerup_buff_1"),
                                    0.05, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.ATTACK_SPEED, modifierId("cheerup_buff_2"),
                                    0.05, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(PDAttributes.SKILLCD, modifierId("cheerup_buff_3"),
                                    -0.1, AttributeModifier.Operation.ADD_VALUE));

    /**
     * 反击 (counterattack_buff) — 原版 CounterattackBuffMobEffect
     * <p>
     * 有益 0xFF8EADD5（原 -7426603）。回避成功且装备反击系饰品时获得：
     * 攻击力 +3、战技倍率 +0.5。
     */
    public static final DeferredHolder<MobEffect, MobEffect> COUNTERATTACK_BUFF =
            MobEffectAPI.REGISTRY.register("counterattack_buff",
                    () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFF8EADD5) {
                    }
                            .addAttributeModifier(Attributes.ATTACK_DAMAGE, modifierId("counterattack_buff_0"),
                                    3, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(PDAttributes.SKILLMULTIPLIER, modifierId("counterattack_buff_1"),
                                    0.5, AttributeModifier.Operation.ADD_VALUE));

    // ==================== B. 混合型（修饰符 + 回调，直接注册匿名子类） ====================

    /**
     * 漂泊旅者的染梦竖琴 (dreamharp_of_wanderer_buff) — 原版 DreamharpOfWandererBuffMobEffect
     * <p>
     * 有益 0xFFE279B2（原 -1934926）。修饰符：san 变化率 +2.4、最大生命 +4、
     * 移速 +1%（ADD_MULTIPLIED_BASE，对应旧 MULTIPLY_BASE）；
     * 生效时立即治疗 max(4 &lt;&lt; (amplifier+1), 0) 点生命（原版在 addAttributeModifiers 中执行，
     * 此处经 vanilla onEffectAdded 钩子在效果首次生效时执行）。
     */
    public static final DeferredHolder<MobEffect, MobEffect> DREAMHARP_OF_WANDERER_BUFF =
            MobEffectAPI.REGISTRY.register("dreamharp_of_wanderer_buff",
                    () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFFE279B2) {
                        @Override
                        public void onEffectAdded(LivingEntity entity, int amplifier) {
                            super.onEffectAdded(entity, amplifier);
                            // 原版：入场治疗（等级越高治疗越多）
                            entity.heal(Math.max(4 << (amplifier + 1), 0));
                        }
                    }
                            .addAttributeModifier(PDAttributes.SAN_VARIABILITY, modifierId("dreamharp_of_wanderer_buff_0"),
                                    2.4, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.MAX_HEALTH, modifierId("dreamharp_of_wanderer_buff_1"),
                                    4, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.MOVEMENT_SPEED, modifierId("dreamharp_of_wanderer_buff_2"),
                                    0.01, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    /**
     * 疯狂 (insand_buff) — 原版 InsandBuffMobEffect + InsandBuffPr0Procedure
     * <p>
     * 有害 0xFF1F0505（原 -14744315）。San 过低时获得。修饰符：
     * 瞬身术冷却 +2、移速 -30%、攻速 -10%（ADD_MULTIPLIED_BASE）、攻击力 -2、战技冷却 +1、
     * 实体/方块交互距离 -0.2（对应旧 Forge ENTITY_REACH/BLOCK_REACH）。
     * 每 tick 按等级触发画面抖动与恐怖生物幻觉（见 {@link #insandTick}）。
     */
    public static final DeferredHolder<MobEffect, MobEffect> INSAND_BUFF =
            MobEffectAPI.REGISTRY.register("insand_buff",
                    () -> new MobEffect(MobEffectCategory.HARMFUL, 0xFF1F0505) {
                        @Override
                        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
                            insandTick(entity);
                            return true;
                        }

                        @Override
                        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
                            return true;
                        }
                    }
                            .addAttributeModifier(PDAttributes.TELEPORTATIONCD, modifierId("insand_buff_0"),
                                    2, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.MOVEMENT_SPEED, modifierId("insand_buff_1"),
                                    -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                            .addAttributeModifier(Attributes.ATTACK_SPEED, modifierId("insand_buff_2"),
                                    -0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                            .addAttributeModifier(Attributes.ATTACK_DAMAGE, modifierId("insand_buff_3"),
                                    -2, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(PDAttributes.SKILLCD, modifierId("insand_buff_4"),
                                    1, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, modifierId("insand_buff_5"),
                                    -0.2, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, modifierId("insand_buff_6"),
                                    -0.2, AttributeModifier.Operation.ADD_VALUE));

    /**
     * 休憩 (rest_buff) — 原版 RestBuffMobEffect(fix=1)
     * <p>
     * 有益 0xFFC4E6FF（原 -3873025）。San 变化率 +0.6/分。
     * 每 tick 依据露天/光照状态与 {@link #REST_BUFF_IN_DARK} 互相转换（原版逻辑原样保留）。
     */
    public static final DeferredHolder<MobEffect, MobEffect> REST_BUFF =
            MobEffectAPI.REGISTRY.register("rest_buff",
                    () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFFC4E6FF) {
                        @Override
                        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
                            restBuffSwapTick(entity);
                            return true;
                        }

                        @Override
                        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
                            return true;
                        }
                    }
                            .addAttributeModifier(PDAttributes.SAN_VARIABILITY, modifierId("rest_buff_0"),
                                    0.6, AttributeModifier.Operation.ADD_VALUE));

    /**
     * 休憩·暗处 (rest_buff_in_dark) — 原版 RestBuffMobEffect(fix=2)
     * <p>
     * 有益 0xFFC4E6FF（与 rest_buff 同色）。San 变化率 +1.2/分（0.6×2）。
     * 与 {@link #REST_BUFF} 共用互换 tick 逻辑。
     */
    public static final DeferredHolder<MobEffect, MobEffect> REST_BUFF_IN_DARK =
            MobEffectAPI.REGISTRY.register("rest_buff_in_dark",
                    () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFFC4E6FF) {
                        @Override
                        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
                            restBuffSwapTick(entity);
                            return true;
                        }

                        @Override
                        public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
                            return true;
                        }
                    }
                            .addAttributeModifier(PDAttributes.SAN_VARIABILITY, modifierId("rest_buff_in_dark_0"),
                                    1.2, AttributeModifier.Operation.ADD_VALUE));

    // ==================== C. 瞬时效果型（InstantenousMobEffect，作用于玩家附件数据） ====================

    /**
     * 精神回复 (san_increase) — 原版 SanVaryMobEffect(true)
     * 有益 0xADFF2F。瞬时生效：San +((amplifier &amp; 0xff) + 1)。
     */
    public static final DeferredHolder<MobEffect, MobEffect> SAN_INCREASE =
            MobEffectAPI.REGISTRY.register("san_increase",
                    () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 0xADFF2F) {
                        @Override
                        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
                            if (entity instanceof ServerPlayer pl) {
                                PDAttachments.addPlayerSanWithCheck(pl, (amplifier & 0xff) + 1);
                            }
                            return true;
                        }
                    });

    /**
     * 精神损伤 (san_decrease) — 原版 SanVaryMobEffect(false)
     * 有害 0x9B4400。瞬时生效：San -((amplifier &amp; 0xff) + 1)。
     */
    public static final DeferredHolder<MobEffect, MobEffect> SAN_DECREASE =
            MobEffectAPI.REGISTRY.register("san_decrease",
                    () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0x9B4400) {
                        @Override
                        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
                            if (entity instanceof ServerPlayer pl) {
                                PDAttachments.addPlayerSanWithCheck(pl, -(amplifier & 0xff) - 1);
                            }
                            return true;
                        }
                    });

    /**
     * 融梦能量增加 (melt_dream_energy_increase) — 原版 MeltDreamEnergyVaryMobEffect(true)
     * 有益 0xADFF2F。瞬时生效：融梦能量 +((amplifier &amp; 0xff) + 1)。
     */
    public static final DeferredHolder<MobEffect, MobEffect> MELT_DREAM_ENERGY_INCREASE =
            MobEffectAPI.REGISTRY.register("melt_dream_energy_increase",
                    () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 0xADFF2F) {
                        @Override
                        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
                            if (entity instanceof ServerPlayer pl) {
                                PDAttachments.addPlayerMeltDreamEnergy(pl, (amplifier & 0xff) + 1);
                            }
                            return true;
                        }
                    });

    /**
     * 融梦能量减少 (melt_dream_energy_decrease) — 原版 MeltDreamEnergyVaryMobEffect(false)
     * 有害 0x9B4400。瞬时生效：融梦能量 -((amplifier &amp; 0xff) + 1)。
     */
    public static final DeferredHolder<MobEffect, MobEffect> MELT_DREAM_ENERGY_DECREASE =
            MobEffectAPI.REGISTRY.register("melt_dream_energy_decrease",
                    () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0x9B4400) {
                        @Override
                        public boolean applyEffectTick(LivingEntity entity, int amplifier) {
                            if (entity instanceof ServerPlayer pl) {
                                PDAttachments.addPlayerMeltDreamEnergy(pl, -(amplifier & 0xff) - 1);
                            }
                            return true;
                        }
                    });

    // ==================== D. Builder 型（tick / 生效 / 移除回调，经 PDEffectEvents 派发） ====================

    /**
     * 束缚 (bind_buff) — 原版 BindBuffMobEffect
     * <p>
     * 有害 0xFFA1B0B8（原 -6180680）。纯标记效果。
     * 原版源码中 BindBuffPr0/Pr1Procedure 为孤儿代码（无任何调用点），故不移植。
     */
    public static final MobEffectResult BIND_BUFF =
            MobEffectAPI.createEffect("bind_buff")
                    .harmful()
                    .color(0xFFA1B0B8)
                    .build();

    /**
     * 啵啵鸡的华丽飞羽 (boboji_buff) — 原版 BobojiBuffMobEffect
     * <p>
     * 有益 0xFFB8EA5D（原 -4658595）。装备啵啵鸡饰品使用瞬身术时获得：
     * 生效时播放鹦鹉音效并给予周围玩家短暂回避（Pt0），每 tick 生成孢子+尘埃粒子拖尾（Pr0）。
     * 隐藏 GUI 图标由 client/PDEffectClientExtensions 接线。
     */
    public static final MobEffectResult BOBOJI_BUFF =
            MobEffectAPI.createEffect("boboji_buff")
                    .beneficial()
                    .color(0xFFB8EA5D)
                    .onApply((entity, amplifier) -> bobojiApply(entity))
                    .onTick((entity, amplifier) -> bobojiTick(entity))
                    .build();

    /**
     * 云雾 (cloudmist_buff) — 原版 CloudmistBuffMobEffect + CloudmistBuffPr0Procedure
     * <p>
     * 中性 0xFFD6FCFF（原 -2687745）。风之旅途维度坠落机制：
     * 每 tick 记录云雾浓度百分比（HUD 用），低于 y=5 时传送回主世界重生点上空 y=304。
     */
    public static final MobEffectResult CLOUDMIST_BUFF =
            MobEffectAPI.createEffect("cloudmist_buff")
                    .neutral()
                    .color(0xFFD6FCFF)
                    .onTick((entity, amplifier) -> cloudmistTick(entity))
                    .build();

    /**
     * 逆风 (deadwind_buff) — 原版 DeadwindBuffMobEffect + DeadwindBuffPr0/Pr1Procedure
     * <p>
     * 有害 0xFFE6916B（原 -1666709）。生效时移除顺风并按等级施加永久修饰符
     * （移速 -0.02/-0.03、瞬身距离 -0.3/-0.5、瞬身冷却 ×+150%/+200%），移除时撤销。
     * 修饰符 ID 沿用原版命名 deadwind_buff_0/1/2。
     */
    public static final MobEffectResult DEADWIND_BUFF =
            MobEffectAPI.createEffect("deadwind_buff")
                    .harmful()
                    .color(0xFFE6916B)
                    .onApply(PDEffects::deadwindApply)
                    .onRemove(PDEffects::deadwindRemove)
                    .build();

    /**
     * 染梦守护 (dyedream_armor_buff) — 原版 DyedreamArmorBuffMobEffect
     * <p>
     * 有益 0xFFFFFFFF（原 -1）。染梦套装效果：每 tick 补挂生命提升（HealthboostArmorPr0），
     * 效果移除时若未穿全套染梦护甲则撤销生命提升（ArmorBuffPr0）。
     */
    public static final MobEffectResult DYEDREAM_ARMOR_BUFF =
            MobEffectAPI.createEffect("dyedream_armor_buff")
                    .beneficial()
                    .color(0xFFFFFFFF)
                    .onTick((entity, amplifier) -> healthboostArmorTick(entity))
                    .onRemove((entity, amplifier) -> armorBuffRemove(entity,
                            PDItemsArmor.DYEDREAM_ARMOR_HELMET.get(), PDItemsArmor.DYEDREAM_ARMOR_CHESTPLATE.get(),
                            PDItemsArmor.DYEDREAM_ARMOR_LEGGINGS.get(), PDItemsArmor.DYEDREAM_ARMOR_BOOTS.get()))
                    .build();

    /**
     * 回避 (evasion_buff) — 原版 EvasionBuffMobEffect
     * <p>
     * 有益 0xFFD5FFFC（原 -2752516）。持有时受到的下一次伤害被完全回避
     * （闪避判定与冲刺位移逻辑见 {@link PDEffectEvents#onLivingDamagePre}）。
     */
    public static final MobEffectResult EVASION_BUFF =
            MobEffectAPI.createEffect("evasion_buff")
                    .beneficial()
                    .color(0xFFD5FFFC)
                    .build();

    /**
     * 回避衣装 (evasion_cloak_buff) — 原版 EvasionCloakBuffMobEffect + EvasionCloakBuffPr0Procedure
     * <p>
     * 有益 0xFFABEFFC（原 -5509124）。回避斗篷激活状态：每 tick 生成银辉+尘埃粒子。
     */
    public static final MobEffectResult EVASION_CLOAK_BUFF =
            MobEffectAPI.createEffect("evasion_cloak_buff")
                    .beneficial()
                    .color(0xFFABEFFC)
                    .onTick((entity, amplifier) -> {
                        if (entity.level() instanceof ServerLevel level) {
                            level.sendParticles((SimpleParticleType) PDParticles.SILVER_PARTICLE.particleType(),
                                    entity.getX(), entity.getY() + 1, entity.getZ(), 4, 0.4, 0.5, 0.4, 0.01);
                            level.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                                    entity.getX(), entity.getY() + 1, entity.getZ(), 4, 0.4, 0.5, 0.4, 0.01);
                        }
                    })
                    .build();

    /**
     * 怒气爆发 (flareup_buff) — 原版 FlareupBuffMobEffect + FlareupBuffPr0/Pr1Procedure
     * <p>
     * 有益 0xFFFF8547（原 -31417）。生效时基础值：攻击力 +3、攻速 +0.2、
     * 战技倍率 +0.5、战技冷却 -0.1；移除时对称撤销（原版即直接修改属性基础值）。
     */
    public static final MobEffectResult FLAREUP_BUFF =
            MobEffectAPI.createEffect("flareup_buff")
                    .beneficial()
                    .color(0xFFFF8547)
                    .onApply((entity, amplifier) -> flareupShift(entity, 1))
                    .onRemove((entity, amplifier) -> flareupShift(entity, -1))
                    .build();

    /**
     * 迷梦 (fondillusion_buff) — 原版 FondillusionBuffMobEffect + FondillusionBuffPr0Procedure
     * <p>
     * 有益 0xFFCB86F7（原 -3438857）。主世界 y260~310 高空的云雾浓度记录；
     * y≥306 且完成 achievement_b_0 与 achievement_hide_16 成就时传送至风之旅途维度。
     */
    public static final MobEffectResult FONDILLUSION_BUFF =
            MobEffectAPI.createEffect("fondillusion_buff")
                    .beneficial()
                    .color(0xFFCB86F7)
                    .onTick((entity, amplifier) -> fondillusionTick(entity))
                    .build();

    /**
     * 圣杯 (grail_buff) — 原版 GrailBuffMobEffect + GrailBuffPr0/Pr1/Pr2Procedure
     * <p>
     * 有益 0xFFFFE73B（原 -6341）。生效时最大生命基础值 +20，
     * 每 tick 净化反胃/失明/饥饿，移除时最大生命基础值 -20。
     */
    public static final MobEffectResult GRAIL_BUFF =
            MobEffectAPI.createEffect("grail_buff")
                    .beneficial()
                    .color(0xFFFFE73B)
                    .onApply((entity, amplifier) -> shiftBaseValue(entity, Attributes.MAX_HEALTH, 20))
                    .onTick((entity, amplifier) -> {
                        entity.removeEffect(MobEffects.CONFUSION);
                        entity.removeEffect(MobEffects.BLINDNESS);
                        entity.removeEffect(MobEffects.HUNGER);
                    })
                    .onRemove((entity, amplifier) -> shiftBaseValue(entity, Attributes.MAX_HEALTH, -20))
                    .build();

    /**
     * 禁止改造 (guard_block_buff) — 原版 GuardBlockBuffMobEffect + GuardBlockBuffPr0/Pr1Procedure
     * <p>
     * 有益 0xFF50006C（原 -11534228）。守卫方块领域内：生效切换冒险模式，
     * 移除恢复生存模式。不可被牛奶清除（见 {@link PDEffectEvents}）。
     */
    public static final MobEffectResult GUARD_BLOCK_BUFF =
            MobEffectAPI.createEffect("guard_block_buff")
                    .beneficial()
                    .color(0xFF50006C)
                    .onApply((entity, amplifier) -> {
                        if (entity instanceof ServerPlayer player) {
                            player.setGameMode(GameType.ADVENTURE);
                        }
                    })
                    .onRemove((entity, amplifier) -> {
                        if (entity instanceof ServerPlayer player) {
                            player.setGameMode(GameType.SURVIVAL);
                        }
                    })
                    .build();

    /**
     * 不振 (lethargy_buff) — 原版 LethargyBuffMobEffect + LethargyBuffPr0/Pr1Procedure
     * <p>
     * 有害 0xFFD3A2A2（原 -2907486）。仅对玩家生效时基础值：
     * 瞬身冷却 +0.5、移速 -0.01、攻速 -0.1、战技冷却 +0.2；移除时对称撤销。
     */
    public static final MobEffectResult LETHARGY_BUFF =
            MobEffectAPI.createEffect("lethargy_buff")
                    .harmful()
                    .color(0xFFD3A2A2)
                    .onApply((entity, amplifier) -> lethargyShift(entity, 1))
                    .onRemove((entity, amplifier) -> lethargyShift(entity, -1))
                    .build();

    /**
     * 机械之翼 (machine_wing_effect) — 原版 MachineWingEffectMobEffect + MachineWingEffectPr0/Pr1Procedure
     * <p>
     * 有益 0xFF374467（原 -13155225）。生效时开启玩家飞行，移除时关闭。
     * 隐藏 GUI 图标由 client/PDEffectClientExtensions 接线。
     */
    public static final MobEffectResult MACHINE_WING_EFFECT =
            MobEffectAPI.createEffect("machine_wing_effect")
                    .beneficial()
                    .color(0xFF374467)
                    .onApply((entity, amplifier) -> {
                        if (entity instanceof Player player) {
                            // mayfly 才能在生存模式起飞；flying 仅表示当前是否在飞
                            player.getAbilities().mayfly = true;
                            player.getAbilities().flying = true;
                            player.onUpdateAbilities();
                        }
                    })
                    .onRemove((entity, amplifier) -> {
                        if (entity instanceof Player player
                                && !player.isCreative() && !player.isSpectator()) {
                            player.getAbilities().mayfly = false;
                            player.getAbilities().flying = false;
                            player.onUpdateAbilities();
                        }
                    })
                    .build();

    /**
     * 寻梦者的祈愿 (memento_buff) — 原版 MementoBuffMobEffect + MementoBuffPr0/Pr1Procedure
     * <p>
     * 有益 0xFFFFDCF6（原 -8970）。生效时幸运属性基础值 +10。
     * 【原版 Bug 修正】原版 MementoBuffPr1（移除回调）同样是 +10，会导致幸运值随
     * 每次获得/失去效果无限增长；此处按对称语义还原为移除时 -10。
     */
    public static final MobEffectResult MEMENTO_BUFF =
            MobEffectAPI.createEffect("memento_buff")
                    .beneficial()
                    .color(0xFFFFDCF6)
                    .onApply((entity, amplifier) -> shiftBaseValue(entity, PDAttributes.LUCK, 10))
                    .onRemove((entity, amplifier) -> shiftBaseValue(entity, PDAttributes.LUCK, -10))
                    .build();

    /**
     * 高速反射 (rapid_reaction) — 原版 RapidReactionMobEffect + RapidReactionPr0/Pr1Procedure
     * <p>
     * 有益 0xFF71DCE2（原 -9315102）。生效时瞬身术冷却基础值 -0.2，移除时 +0.2。
     */
    public static final MobEffectResult RAPID_REACTION =
            MobEffectAPI.createEffect("rapid_reaction")
                    .beneficial()
                    .color(0xFF71DCE2)
                    .onApply((entity, amplifier) -> shiftBaseValue(entity, PDAttributes.TELEPORTATIONCD, -0.2))
                    .onRemove((entity, amplifier) -> shiftBaseValue(entity, PDAttributes.TELEPORTATIONCD, 0.2))
                    .build();

    /**
     * 行动抑制 (restrainmove_block_buff) — 原版 RestrainmoveBlockBuffMobEffect + RestrainmoveBlockBuffPr0Procedure
     * <p>
     * 有益（原版定义）0xFF8E0606（原 -7469562）。抑制方块领域内：
     * 每 tick 移除跳跃提升并强制关闭玩家飞行。不可被牛奶清除。
     */
    public static final MobEffectResult RESTRAINMOVE_BLOCK_BUFF =
            MobEffectAPI.createEffect("restrainmove_block_buff")
                    .beneficial()
                    .color(0xFF8E0606)
                    .onTick((entity, amplifier) -> {
                        entity.removeEffect(MobEffects.JUMP);
                        if (entity instanceof Player player) {
                            player.getAbilities().flying = false;
                            player.onUpdateAbilities();
                        }
                    })
                    .build();

    /**
     * 幽匿回响 (sculk_armor_buff) — 原版 SculkArmorBuffMobEffect
     * <p>
     * 有益 0xFFFFFFFF（原 -1）。幽匿套装效果：每 tick 补挂生命提升，
     * 效果移除时若未穿全套幽匿护甲则撤销生命提升（ArmorBuffPr1）。
     */
    public static final MobEffectResult SCULK_ARMOR_BUFF =
            MobEffectAPI.createEffect("sculk_armor_buff")
                    .beneficial()
                    .color(0xFFFFFFFF)
                    .onTick((entity, amplifier) -> healthboostArmorTick(entity))
                    .onRemove((entity, amplifier) -> armorBuffRemove(entity,
                            PDItemsArmor.SCULK_ARMOR_HELMET.get(), PDItemsArmor.SCULK_ARMOR_CHESTPLATE.get(),
                            PDItemsArmor.SCULK_ARMOR_LEGGINGS.get(), PDItemsArmor.SCULK_ARMOR_BOOTS.get()))
                    .build();

    /**
     * 暗影窥视 (shadow_spyon_buff) — 原版 ShadowSpyonBuffMobEffect + ShadowIntrudePr0Procedure
     * <p>
     * 有害 0xFF333333（原 -13421773）。主世界夜晚黑暗处随机触发"暗影入侵"事件：
     * 黑暗侵蚀提示 → 周期性召唤虚弱恐怖尖喙与暗影之手 → 白天或清剿后平息并授予成就。
     * 不可被牛奶清除。
     */
    public static final MobEffectResult SHADOW_SPYON_BUFF =
            MobEffectAPI.createEffect("shadow_spyon_buff")
                    .harmful()
                    .color(0xFF333333)
                    .onTick((entity, amplifier) -> shadowIntrudeTick(entity))
                    .build();

    /**
     * 雪下的誓言 (snow_vow_buff) — 原版 SnowVowBuffMobEffect + SnowVowBuffPr0/Pr1/Pr2Procedure
     * <p>
     * 有益 0xFF8ABED3（原 -7684397）。生效时幸运基础值 +3、
     * 每 tick 清除着火与冰冻状态、移除时幸运 -3。
     */
    public static final MobEffectResult SNOW_VOW_BUFF =
            MobEffectAPI.createEffect("snow_vow_buff")
                    .beneficial()
                    .color(0xFF8ABED3)
                    .onApply((entity, amplifier) -> shiftBaseValue(entity, PDAttributes.LUCK, 3))
                    .onTick((entity, amplifier) -> {
                        entity.clearFire();
                        entity.setTicksFrozen(0);
                    })
                    .onRemove((entity, amplifier) -> shiftBaseValue(entity, PDAttributes.LUCK, -3))
                    .build();

    /**
     * 顺风 (tailwind_buff) — 原版 TailwindBuffMobEffect + TailwindBuffPr0/Pr1Procedure
     * <p>
     * 有益 0xFF93D9BE（原 -7087682）。生效时移除逆风并按等级施加永久修饰符
     * （移速 +0.03/+0.04、瞬身距离 +1/+1.5、瞬身冷却 ×+70%/+60%），移除时撤销。
     * 修饰符 ID 沿用原版命名 tailwind_buff_0/1/2。
     * 隐藏 GUI 图标由 client/PDEffectClientExtensions 接线。
     */
    public static final MobEffectResult TAILWIND_BUFF =
            MobEffectAPI.createEffect("tailwind_buff")
                    .beneficial()
                    .color(0xFF93D9BE)
                    .onApply(PDEffects::tailwindApply)
                    .onRemove(PDEffects::tailwindRemove)
                    .build();

    /**
     * 瞬身术 (teleportation_buff) — 原版 TeleportationBuffMobEffect
     * <p>
     * 有益 0xFFFFFFFF（原 -1）。瞬身术冷却标记效果（传送键位系统还原后接入）。
     * 不可被牛奶清除。
     */
    public static final MobEffectResult TELEPORTATION_BUFF =
            MobEffectAPI.createEffect("teleportation_buff")
                    .beneficial()
                    .color(0xFFFFFFFF)
                    .build();

    /**
     * 恍惚 (trance_buff) — 原版 TranceBuffMobEffect + TranceBuffPr0/Pr1Procedure
     * <p>
     * 有害 0xFFBC726D（原 -4427155）。仅对玩家生效时基础值：
     * 瞬身冷却 +1、移速 -0.02、攻速 -0.2、攻击力 -1、战技冷却 +0.5；移除时对称撤销。
     */
    public static final MobEffectResult TRANCE_BUFF =
            MobEffectAPI.createEffect("trance_buff")
                    .harmful()
                    .color(0xFFBC726D)
                    .onApply((entity, amplifier) -> tranceShift(entity, 1))
                    .onRemove((entity, amplifier) -> tranceShift(entity, -1))
                    .build();

    /**
     * 转身衣装 (turnback_cloak_buff) — 原版 TurnbackCloakBuffMobEffect + TurnbackCloakBuffPr0Procedure
     * <p>
     * 有益 0xFFFFFFFF（原 -1）。转身斗篷激活状态：每 tick 生成金辉粒子 + 尘埃粒子
     * （与原版 TurnbackCloakBuffPr0Procedure 双粒子逐参数一致）。
     */
    public static final MobEffectResult TURNBACK_CLOAK_BUFF =
            MobEffectAPI.createEffect("turnback_cloak_buff")
                    .beneficial()
                    .color(0xFFFFFFFF)
                    .onTick((entity, amplifier) -> {
                        if (entity.level() instanceof ServerLevel level) {
                            level.sendParticles((SimpleParticleType) PDParticles.GOLDEN_PARTICLE.particleType(),
                                    entity.getX(), entity.getY() + 1, entity.getZ(), 4, 0.4, 0.5, 0.4, 0.01);
                            level.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                                    entity.getX(), entity.getY() + 1, entity.getZ(), 4, 0.4, 0.5, 0.4, 0.01);
                        }
                    })
                    .build();

    /**
     * 易伤 (vulnerability_buff) — 原版 VulnerabilityBuffMobEffect
     * <p>
     * 有害 0xFFA23333（原 -6147277）。受到的伤害改为 原伤害 × 0.1 × 等级
     * （伤害修改逻辑见 {@link PDEffectEvents#onLivingDamagePre}）。
     */
    public static final MobEffectResult VULNERABILITY_BUFF =
            MobEffectAPI.createEffect("vulnerability_buff")
                    .harmful()
                    .color(0xFFA23333)
                    .build();

    // ════════════════════════════════════════════════════════════════════
    // 私有工具与 procedure 还原逻辑
    // ════════════════════════════════════════════════════════════════════

    /** 风之旅途维度键（防御性：getLevel 为空时键比较恒 false，不进维） */
    private static final ResourceKey<Level> WIND_JOURNEY_WORLD =
            ResourceKey.create(Registries.DIMENSION,
                    ResourceLocation.fromNamespaceAndPath("pasterdream", "wind_journey_world"));

    /** 生成 pasterdream 命名空间下的属性修饰符 ID */
    private static ResourceLocation modifierId(String path) {
        return ResourceLocation.fromNamespaceAndPath("pasterdream", path);
    }

    /**
     * 修改属性基础值（对应原版 procedure 的 setBaseValue(getBaseValue() ± N) 写法）
     *
     * @param entity    目标实体
     * @param attribute 属性
     * @param delta     变化量
     */
    private static void shiftBaseValue(LivingEntity entity,
                                       net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
                                       double delta) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(instance.getBaseValue() + delta);
        }
    }

    /** 判断实体是否装备了指定 Curios 饰品（对应原版 CuriosApi.findEquippedCurio） */
    private static boolean hasCurioEquipped(LivingEntity entity, Item item) {
        return CuriosApi.getCuriosInventory(entity)
                .map(handler -> handler.findFirstCurio(item).isPresent())
                .orElse(false);
    }

    /** 若属性实例上没有同 ID 修饰符则添加永久修饰符（对应原版 hasModifier + addPermanentModifier） */
    private static void addPermanentIfAbsent(LivingEntity entity,
                                             net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
                                             ResourceLocation id, double amount, AttributeModifier.Operation op) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null && !instance.hasModifier(id)) {
            instance.addPermanentModifier(new AttributeModifier(id, amount, op));
        }
    }

    /** 按 ID 移除永久修饰符（对应原版 removePermanentModifier） */
    private static void removePermanent(LivingEntity entity,
                                        net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute,
                                        ResourceLocation id) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(id);
        }
    }

    // ---------- boboji_buff（BobojiBuffPt0/Pr0Procedure） ----------

    /** 啵啵鸡效果生效：鹦鹉音效 + 给周围玩家（或自身）短暂回避 */
    private static void bobojiApply(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }
        double x = entity.getX(), y = entity.getY(), z = entity.getZ();
        BlockPos pos = BlockPos.containing(x, y, z);
        level.playSound(null, pos, SoundEvents.PARROT_FLY, SoundSource.NEUTRAL, 1, 1);
        level.playSound(null, pos, SoundEvents.PARROT_AMBIENT, SoundSource.NEUTRAL, 1, 1);
        Vec3 center = new Vec3(x, y, z);
        // 原版：15×15×15 范围内存在玩家 → 给 7.5 半径内所有玩家回避 10t；否则给自身回避 5t
        if (!level.getEntitiesOfClass(Player.class, AABB.ofSize(center, 15, 15, 15), e -> true).isEmpty()) {
            for (Player player : level.getEntitiesOfClass(Player.class,
                    new AABB(center, center).inflate(15 / 2d), e -> true)) {
                player.addEffect(new MobEffectInstance(EVASION_BUFF.holder(), 10, 0, false, false));
            }
        } else {
            entity.addEffect(new MobEffectInstance(EVASION_BUFF.holder(), 5, 0, false, false));
        }
    }

    /** 啵啵鸡效果 tick：孢子 + 尘埃粒子拖尾 */
    private static void bobojiTick(LivingEntity entity) {
        if (entity.level() instanceof ServerLevel level) {
            level.sendParticles((SimpleParticleType) PDParticles.SPORE_PARTICLE.particleType(),
                    entity.getX(), entity.getY(), entity.getZ(), 4, 0.4, 0.8, 0.4, 0.1);
            level.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                    entity.getX(), entity.getY(), entity.getZ(), 4, 0.4, 0.8, 0.4, 0.1);
        }
    }

    // ---------- cloudmist_buff（CloudmistBuffPr0Procedure） ----------

    /** 云雾 tick：风之旅途坠落检测与云雾浓度记录 */
    private static void cloudmistTick(LivingEntity entity) {
        if (entity.level().isClientSide) {
            return;
        }
        if (entity.level().dimension().equals(WIND_JOURNEY_WORLD)) {
            if (entity.getY() > 0 && entity.getY() <= 50) {
                // HUD 云雾浓度百分比（原版 CloudmistHud 读取）
                entity.getPersistentData().putDouble("cloudmist_percent", (50 - entity.getY()) * 2);
                if (entity.getY() <= 5) {
                    // 跌破云层：传送回主世界重生点上空 y=304
                    if (entity instanceof ServerPlayer player) {
                        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
                        if (overworld != null && player.level().dimension() != Level.OVERWORLD) {
                            // 1.21.1 teleportTo(ServerLevel,...) 自带能力/效果同步，
                            // 等价原版 WIN_GAME + teleportTo + 手动补包序列
                            player.teleportTo(overworld, player.getX(), player.getY(), player.getZ(),
                                    player.getYRot(), player.getXRot());
                        }
                        double tx;
                        double tz;
                        BlockPos respawn = player.getRespawnPosition();
                        if (player.getRespawnDimension().equals(player.level().dimension()) && respawn != null) {
                            tx = respawn.getX();
                            tz = respawn.getZ();
                        } else {
                            BlockPos spawn = player.serverLevel().getSharedSpawnPos();
                            tx = spawn.getX();
                            tz = spawn.getZ();
                        }
                        player.connection.teleport(tx, 304, tz, player.getYRot(), player.getXRot());
                    } else {
                        entity.teleportTo(0, 304, 0);
                    }
                }
            } else {
                entity.getPersistentData().putDouble("cloudmist_percent", 0);
            }
        }
        if (!(entity.level().dimension() == Level.OVERWORLD
                || entity.level().dimension().equals(WIND_JOURNEY_WORLD))) {
            entity.getPersistentData().putDouble("cloudmist_percent", 0);
        }
    }

    // ---------- deadwind_buff / tailwind_buff（Deadwind/TailwindBuffPr0/Pr1Procedure） ----------

    /** 逆风生效：移除顺风 + 按等级施加永久修饰符 */
    private static void deadwindApply(LivingEntity entity, Integer amplifier) {
        entity.removeEffect(TAILWIND_BUFF.holder());
        // 先摘旧 modifier，再 add：amp 0↔1 或 force 切换时 addPermanentIfAbsent 不会卡住旧数值
        deadwindRemove(entity, amplifier);
        if (amplifier == 0) {
            addPermanentIfAbsent(entity, Attributes.MOVEMENT_SPEED, modifierId("deadwind_buff_0"),
                    -0.02, AttributeModifier.Operation.ADD_VALUE);
            addPermanentIfAbsent(entity, PDAttributes.TELEPORTATIONRANGE, modifierId("deadwind_buff_1"),
                    -0.3, AttributeModifier.Operation.ADD_VALUE);
            addPermanentIfAbsent(entity, PDAttributes.TELEPORTATIONCD, modifierId("deadwind_buff_2"),
                    1.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        } else {
            addPermanentIfAbsent(entity, Attributes.MOVEMENT_SPEED, modifierId("deadwind_buff_0"),
                    -0.03, AttributeModifier.Operation.ADD_VALUE);
            addPermanentIfAbsent(entity, PDAttributes.TELEPORTATIONRANGE, modifierId("deadwind_buff_1"),
                    -0.5, AttributeModifier.Operation.ADD_VALUE);
            addPermanentIfAbsent(entity, PDAttributes.TELEPORTATIONCD, modifierId("deadwind_buff_2"),
                    2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
    }

    /** 逆风移除：撤销永久修饰符（按 ID 移除，与施加时数值无关） */
    private static void deadwindRemove(LivingEntity entity, Integer amplifier) {
        removePermanent(entity, Attributes.MOVEMENT_SPEED, modifierId("deadwind_buff_0"));
        removePermanent(entity, PDAttributes.TELEPORTATIONRANGE, modifierId("deadwind_buff_1"));
        removePermanent(entity, PDAttributes.TELEPORTATIONCD, modifierId("deadwind_buff_2"));
    }

    /** 顺风生效：移除逆风 + 按等级施加永久修饰符 */
    private static void tailwindApply(LivingEntity entity, Integer amplifier) {
        entity.removeEffect(DEADWIND_BUFF.holder());
        // 先摘旧 modifier，再 add：amp 0↔1 或纸飞机 force 切换时保证数值刷新
        tailwindRemove(entity, amplifier);
        if (amplifier == 0) {
            addPermanentIfAbsent(entity, Attributes.MOVEMENT_SPEED, modifierId("tailwind_buff_0"),
                    0.03, AttributeModifier.Operation.ADD_VALUE);
            addPermanentIfAbsent(entity, PDAttributes.TELEPORTATIONRANGE, modifierId("tailwind_buff_1"),
                    1, AttributeModifier.Operation.ADD_VALUE);
            addPermanentIfAbsent(entity, PDAttributes.TELEPORTATIONCD, modifierId("tailwind_buff_2"),
                    0.7, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        } else {
            addPermanentIfAbsent(entity, Attributes.MOVEMENT_SPEED, modifierId("tailwind_buff_0"),
                    0.04, AttributeModifier.Operation.ADD_VALUE);
            addPermanentIfAbsent(entity, PDAttributes.TELEPORTATIONRANGE, modifierId("tailwind_buff_1"),
                    1.5, AttributeModifier.Operation.ADD_VALUE);
            addPermanentIfAbsent(entity, PDAttributes.TELEPORTATIONCD, modifierId("tailwind_buff_2"),
                    0.6, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
    }

    /** 顺风移除：撤销永久修饰符 */
    private static void tailwindRemove(LivingEntity entity, Integer amplifier) {
        removePermanent(entity, Attributes.MOVEMENT_SPEED, modifierId("tailwind_buff_0"));
        removePermanent(entity, PDAttributes.TELEPORTATIONRANGE, modifierId("tailwind_buff_1"));
        removePermanent(entity, PDAttributes.TELEPORTATIONCD, modifierId("tailwind_buff_2"));
    }

    // ---------- 套装生命提升（HealthboostArmorPr0 / ArmorBuffPr0/Pr1Procedure） ----------

    /** 每 tick 若无生命提升则补挂 1200000t 的隐形生命提升 */
    private static void healthboostArmorTick(LivingEntity entity) {
        if (!entity.level().isClientSide && !entity.hasEffect(MobEffects.HEALTH_BOOST)) {
            entity.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 1200000, 0, false, false));
        }
    }

    /** 套装效果移除：若未穿指定全套护甲则撤销生命提升 */
    private static void armorBuffRemove(LivingEntity entity, Item helmet, Item chestplate, Item leggings, Item boots) {
        ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = entity.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = entity.getItemBySlot(EquipmentSlot.FEET);
        if (!(head.getItem() == helmet && chest.getItem() == chestplate
                && legs.getItem() == leggings && feet.getItem() == boots)) {
            entity.removeEffect(MobEffects.HEALTH_BOOST);
        }
    }

    // ---------- flareup / lethargy / trance（基础值批量增减） ----------

    /** 怒气爆发基础值批量修改（sign=+1 生效 / -1 撤销） */
    private static void flareupShift(LivingEntity entity, int sign) {
        shiftBaseValue(entity, Attributes.ATTACK_DAMAGE, 3 * sign);
        shiftBaseValue(entity, Attributes.ATTACK_SPEED, 0.2 * sign);
        shiftBaseValue(entity, PDAttributes.SKILLMULTIPLIER, 0.5 * sign);
        shiftBaseValue(entity, PDAttributes.SKILLCD, -0.1 * sign);
    }

    /** 不振基础值批量修改（仅玩家，sign=+1 生效 / -1 撤销） */
    private static void lethargyShift(LivingEntity entity, int sign) {
        if (entity instanceof Player) {
            shiftBaseValue(entity, PDAttributes.TELEPORTATIONCD, 0.5 * sign);
            shiftBaseValue(entity, Attributes.MOVEMENT_SPEED, -0.01 * sign);
            shiftBaseValue(entity, Attributes.ATTACK_SPEED, -0.1 * sign);
            shiftBaseValue(entity, PDAttributes.SKILLCD, 0.2 * sign);
        }
    }

    /** 恍惚基础值批量修改（仅玩家，sign=+1 生效 / -1 撤销） */
    private static void tranceShift(LivingEntity entity, int sign) {
        if (entity instanceof Player) {
            shiftBaseValue(entity, PDAttributes.TELEPORTATIONCD, sign);
            shiftBaseValue(entity, Attributes.MOVEMENT_SPEED, -0.02 * sign);
            shiftBaseValue(entity, Attributes.ATTACK_SPEED, -0.2 * sign);
            shiftBaseValue(entity, Attributes.ATTACK_DAMAGE, -sign);
            shiftBaseValue(entity, PDAttributes.SKILLCD, 0.5 * sign);
        }
    }

    // ---------- fondillusion_buff（FondillusionBuffPr0Procedure） ----------

    /** 迷梦 tick：主世界高空云雾浓度 + 达成条件后传送风之旅途 */
    private static void fondillusionTick(LivingEntity entity) {
        if (entity.level().isClientSide || !(entity instanceof Player)) {
            return;
        }
        if (entity.level().dimension() == Level.OVERWORLD) {
            if (entity.getY() > 260 && entity.getY() <= 310) {
                entity.getPersistentData().putDouble("cloudmist_percent", (entity.getY() - 260) * 2);
                if (entity.getY() >= 306 && entity instanceof ServerPlayer player
                        && isAdvancementDone(player, "achievement_b_0")
                        && isAdvancementDone(player, "achievement_hide_16")) {
                    ServerLevel windJourney = player.server.getLevel(WIND_JOURNEY_WORLD);
                    // 防御性：维度未加载时 getLevel 为 null，静默跳过
                    if (windJourney != null && player.level().dimension() != WIND_JOURNEY_WORLD) {
                        // 对齐原版 FondillusionBuffPr0 / 灯影床：WIN_GAME + teleport + 能力/效果 + 1032
                        player.connection.send(new ClientboundGameEventPacket(
                                ClientboundGameEventPacket.WIN_GAME, 0));
                        player.teleportTo(windJourney, player.getX(), player.getY(), player.getZ(),
                                player.getYRot(), player.getXRot());
                        player.connection.send(new ClientboundPlayerAbilitiesPacket(player.getAbilities()));
                        for (MobEffectInstance effect : player.getActiveEffects()) {
                            player.connection.send(new ClientboundUpdateMobEffectPacket(
                                    player.getId(), effect, false));
                        }
                        player.connection.send(new ClientboundLevelEventPacket(
                                1032, BlockPos.ZERO, 0, false));
                    }
                }
            } else {
                entity.getPersistentData().putDouble("cloudmist_percent", 0);
            }
        }
        if (!(entity.level().dimension() == Level.OVERWORLD
                || entity.level().dimension().equals(WIND_JOURNEY_WORLD))) {
            entity.getPersistentData().putDouble("cloudmist_percent", 0);
        }
    }

    /** 判断玩家指定成就是否已完成（成就未注册时视为未完成） */
    private static boolean isAdvancementDone(ServerPlayer player, String path) {
        AdvancementHolder advancement = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath("pasterdream", path));
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    // ---------- insand_buff（InsandBuffPr0Procedure） ----------

    /** 疯狂 tick：画面抖动 + 恐怖生物幻觉召唤（数值/概率与原版一致） */
    private static void insandTick(LivingEntity entity) {
        if (entity.level().isClientSide || !entity.isAlive() || !(entity instanceof Player)) {
            return;
        }
        int amplifier = entity.hasEffect(INSAND_BUFF)
                ? entity.getEffect(INSAND_BUFF).getAmplifier() : 0;
        double x = entity.getX(), y = entity.getY(), z = entity.getZ();
        ServerLevel level = entity.level() instanceof ServerLevel sl ? sl : null;
        if (level == null) {
            return;
        }
        // 画面抖动受 LOW_SAN_PICTURE_JITTER 控制（默认 true）；幻觉召唤不受此配置影响
        boolean pictureJitter = Boolean.TRUE.equals(PDCommonConfig.LOW_SAN_PICTURE_JITTER.get());
        if (amplifier == 0) {
            if (pictureJitter) {
                jitterRotation(entity, 0.5, 0.5);
            }
            if (Math.random() < 0.005) {
                if (Math.random() < 0.3) {
                    spawnHallucination(level, PDEntities.TERRORBEAK.get(), x, y, z, true);
                }
                if (Math.random() < 0.5) {
                    spawnHallucination(level, PDEntities.SHADOW_HAND.get(), x, y, z, false);
                }
            }
        } else if (amplifier == 1) {
            if (pictureJitter) {
                jitterRotation(entity, 1, 1);
            }
            if (Math.random() < 0.01) {
                if (Math.random() < 0.25) {
                    spawnHallucination(level, PDEntities.TERRORBEAK.get(), x, y, z, true);
                }
                if (Math.random() < 0.5) {
                    spawnHallucination(level, PDEntities.SHADOW_HAND.get(), x, y, z, false);
                }
            }
        } else if (amplifier == 2) {
            if (pictureJitter) {
                jitterRotation(entity, 3, 2);
            }
            if (Math.random() < 0.025) {
                // 装备退化之躯时自损 1 点（虚空伤害，血量>1 才触发）
                if (hasCurioEquipped(entity, PDItemsCurios.DEGENERATE_BODYS.get())
                        && entity.getHealth() > 1) {
                    entity.hurt(entity.damageSources().fellOutOfWorld(), 1);
                }
                if (Math.random() < 0.05) {
                    spawnHallucination(level, PDEntities.TERRORBEAK.get(), x, y, z, true);
                }
                if (Math.random() < 0.03) {
                    spawnHallucination(level, PDEntities.CRAZY_TERRORBEAK.get(), x, y, z, true);
                }
                if (Math.random() < 0.5) {
                    spawnHallucination(level, PDEntities.SHADOW_HAND.get(), x, y, z, false);
                }
            }
        }
    }

    /** 视角抖动（对应原版 MCreator 旋转抖动模板） */
    private static void jitterRotation(LivingEntity entity, double yawRange, double pitchRange) {
        entity.setYRot((float) (entity.getYRot() + Mth.nextDouble(RandomSource.create(), -yawRange, yawRange)));
        entity.setXRot((float) (entity.getXRot() + Mth.nextDouble(RandomSource.create(), -pitchRange, pitchRange)));
        entity.setYBodyRot(entity.getYRot());
        entity.setYHeadRot(entity.getYRot());
        entity.yRotO = entity.getYRot();
        entity.xRotO = entity.getXRot();
        entity.yBodyRotO = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();
    }

    /**
     * 召唤幻觉生物（对应原版空气检测 + MOB_SUMMONED 生成）
     *
     * @param checkAbove true 检查 y+2 处为空气（尖喙类），false 检查脚下坐标处为空气（暗影之手）
     */
    private static void spawnHallucination(ServerLevel level,
                                           net.minecraft.world.entity.EntityType<?> type,
                                           double x, double y, double z, boolean checkAbove) {
        BlockPos checkPos = BlockPos.containing(x, checkAbove ? y + 2 : y, z);
        if (level.getBlockState(checkPos).is(Blocks.AIR)) {
            net.minecraft.world.entity.Entity spawned =
                    type.spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
            if (spawned != null) {
                spawned.setYRot(level.getRandom().nextFloat() * 360F);
            }
        }
    }

    // ---------- rest_buff / rest_buff_in_dark（RestBuffMobEffect.applyEffectTick） ----------

    /**
     * 休憩效果互换 tick（原版逻辑原样保留）：
     * 露天或光照 &lt; 7 → 暗处变体转普通变体；否则普通变体转暗处变体。
     * tick 中增删效果由 vanilla tickEffects 的 CME 捕获兜底（与原版 1.20.1 相同机制）。
     */
    private static void restBuffSwapTick(LivingEntity entity) {
        if (entity.level().isClientSide) {
            return;
        }
        Level world = entity.level();
        BlockPos pos = BlockPos.containing(entity.getX(), entity.getY(), entity.getZ());
        MobEffectInstance oldInstance;
        MobEffectInstance newInstance = null;
        if (world.canSeeSkyFromBelowWater(pos) || world.getMaxLocalRawBrightness(pos) < 7) {
            oldInstance = entity.getEffect(REST_BUFF_IN_DARK);
            if (oldInstance != null) {
                newInstance = new MobEffectInstance(REST_BUFF, oldInstance.getDuration(),
                        oldInstance.getAmplifier(), oldInstance.isAmbient(),
                        oldInstance.isVisible(), oldInstance.showIcon());
            }
        } else {
            oldInstance = entity.getEffect(REST_BUFF);
            if (oldInstance != null) {
                newInstance = new MobEffectInstance(REST_BUFF_IN_DARK, oldInstance.getDuration(),
                        oldInstance.getAmplifier(), oldInstance.isAmbient(),
                        oldInstance.isVisible(), oldInstance.showIcon());
            }
        }
        if (oldInstance != null) {
            entity.removeEffect(oldInstance.getEffect());
            entity.addEffect(newInstance);
        }
    }

    // ---------- shadow_spyon_buff（ShadowIntrudePr0Procedure） ----------

    /** 暗影窥视 tick：暗影入侵事件状态机（文案/概率/数值与原版一致） */
    private static void shadowIntrudeTick(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }
        double x = entity.getX(), y = entity.getY(), z = entity.getZ();
        var data = entity.getPersistentData();
        if (!level.isDay() && level.dimension() == Level.OVERWORLD) {
            if (!data.getBoolean("shadow_intrude")) {
                // 阶段一：黑暗处低概率触发入侵
                if (level.getMaxLocalRawBrightness(BlockPos.containing(x, y, z)) <= 7 && Math.random() < 0.0005) {
                    data.putBoolean("shadow_intrude", true);
                    data.putBoolean("shadow_intrude_end", false);
                    if (entity instanceof Player player) {
                        player.displayClientMessage(Component.literal("§5你感到一丝凉意，眼前被笼罩了一层黑雾"), false);
                        player.displayClientMessage(Component.literal("§5附近的的影子开始蠕动"), false);
                    }
                    entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0, false, false));
                    level.playSound(null, BlockPos.containing(x, y, z), PDSounds.SHADOW_DOOR.get(),
                            SoundSource.NEUTRAL, 1, 1);
                }
            } else if (Math.random() < 0.02 && !data.getBoolean("shadow_intrude_end")) {
                // 阶段二：周期性在附近随机位置召唤暗影生物
                data.putDouble("nearly_random_coord_x", Mth.nextInt(RandomSource.create(), -9, 9));
                data.putDouble("nearly_random_coord_z", Mth.nextInt(RandomSource.create(), -9, 9));
                double rx = x + data.getDouble("nearly_random_coord_x");
                double rz = z + data.getDouble("nearly_random_coord_z");
                if (level.isEmptyBlock(BlockPos.containing(rx, y + 2, rz))
                        && level.isEmptyBlock(BlockPos.containing(rx, y + 1, rz))
                        && !level.isEmptyBlock(BlockPos.containing(rx, y - 1, rz))) {
                    var terrorbeak = PDEntities.WEAKENESS_TERRORBEAK.get()
                            .spawn(level, BlockPos.containing(rx, y, rz), MobSpawnType.MOB_SUMMONED);
                    if (terrorbeak != null) {
                        terrorbeak.setYRot(level.getRandom().nextFloat() * 360F);
                    }
                    var hand = PDEntities.SHADOW_HAND.get()
                            .spawn(level, BlockPos.containing(rx, y, rz), MobSpawnType.MOB_SUMMONED);
                    if (hand != null) {
                        hand.setYRot(level.getRandom().nextFloat() * 360F);
                    }
                    data.putDouble("shadow_intrude_number", data.getDouble("shadow_intrude_number") + 1);
                    if (data.getDouble("shadow_intrude_number") > 4 && Math.random() >= 0.5) {
                        data.putBoolean("shadow_intrude_end", true);
                    }
                    entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0));
                }
            }
            // 阶段三：入侵结束且 32 格内无虚弱尖喙 → 平息
            if (data.getBoolean("shadow_intrude_end") && data.getBoolean("shadow_intrude")
                    && level.getEntitiesOfClass(WeakenessTerrorbeakEntity.class,
                            AABB.ofSize(new Vec3(x, y, z), 32, 32, 32), e -> true).isEmpty()) {
                shadowIntrudeCalm(entity, data);
            }
        }
        // 白天强制平息
        if (level.isDay() && data.getBoolean("shadow_intrude")) {
            shadowIntrudeCalm(entity, data);
        }
    }

    /** 暗影入侵平息：清除状态 + 首次授予成就并提示 */
    private static void shadowIntrudeCalm(LivingEntity entity, net.minecraft.nbt.CompoundTag data) {
        data.putBoolean("shadow_intrude", false);
        data.putBoolean("shadow_intrude_end", false);
        if (entity instanceof Player player && !player.level().isClientSide) {
            player.displayClientMessage(Component.literal("§5影子归于平息..."), false);
        }
        if (entity instanceof ServerPlayer player && !isAdvancementDone(player, "achievement_shadow_npc_3")) {
            AdvancementHolder advancement = player.server.getAdvancements()
                    .get(ResourceLocation.fromNamespaceAndPath("pasterdream", "achievement_shadow_npc_3"));
            if (advancement != null) {
                AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
                if (!progress.isDone()) {
                    for (String criteria : progress.getRemainingCriteria()) {
                        player.getAdvancements().award(advancement, criteria);
                    }
                }
            }
            player.displayClientMessage(Component.literal("§7你对这些暗影生物会来到这里感到疑惑"), false);
            player.displayClientMessage(Component.literal("§7或许我们应该再去找一次无名..."), false);
            entity.removeEffect(SHADOW_SPYON_BUFF.holder());
        }
    }
}
