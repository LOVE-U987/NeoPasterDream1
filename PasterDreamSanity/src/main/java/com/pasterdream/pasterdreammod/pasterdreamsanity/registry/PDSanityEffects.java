package com.pasterdream.pasterdreammod.pasterdreamsanity.registry;

import com.pasterdream.pasterdreammod.api.attribute.APIAttributes;
import com.pasterdream.pasterdreammod.api.effect.MobEffectAPI;
import com.pasterdream.pasterdreammod.api.effect.MobEffectResult;
import com.pasterdream.pasterdreammod.api.san.SanHelper;
import com.pasterdream.pasterdreammod.pasterdreamsanity.PasterDreamSanityMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.InstantenousMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

/**
 * San 值系统状态效果注册类。
 * <p>
 * 负责注册疯狂、恍惚、不振、振奋等 San 相关效果。
 * 这些效果从 PasterDream 主模组迁移而来，避免 PasterDreamSanity 反向依赖主模组。
 *
 * @author PasterDream
 */
public class PDSanityEffects {

    /** 状态效果注册器 */
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, PasterDreamSanityMod.MOD_ID);

    // ==================== A. 纯属性修饰符型 ====================

    /**
     * 振奋 (cheerup_buff)。
     * <p>
     * 有益效果，San 值高于阈值时获得：瞬身术冷却 -0.1、移速 +5%、攻速 +0.05、战技冷却 -0.1。
     */
    public static final DeferredHolder<MobEffect, MobEffect> CHEERUP_BUFF =
            MobEffectAPI.REGISTRY.register("cheerup_buff",
                    () -> new MobEffect(MobEffectCategory.BENEFICIAL, 0xFFFF7E7A) {
                    }
                            .addAttributeModifier(APIAttributes.TELEPORTATIONCD, modifierId("cheerup_buff_0"),
                                    -0.1, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.MOVEMENT_SPEED, modifierId("cheerup_buff_1"),
                                    0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                            .addAttributeModifier(Attributes.ATTACK_SPEED, modifierId("cheerup_buff_2"),
                                    0.05, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(APIAttributes.SKILLCD, modifierId("cheerup_buff_3"),
                                    -0.1, AttributeModifier.Operation.ADD_VALUE));

    // ==================== B. 混合型（修饰符 + 回调） ====================

    /**
     * 疯狂 (insand_buff)。
     * <p>
     * 有害效果，San 过低时获得。修饰符包含瞬身术冷却 +2、移速 -30%、攻速 -10%、攻击力 -2、
     * 战技冷却 +1、实体/方块交互距离 -0.2；每 tick 按等级触发画面抖动与恐怖生物幻觉。
     * 幻觉生物通过运行时注册表查找，避免对主模组实体的编译依赖。
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
                            .addAttributeModifier(APIAttributes.TELEPORTATIONCD, modifierId("insand_buff_0"),
                                    2, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.MOVEMENT_SPEED, modifierId("insand_buff_1"),
                                    -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                            .addAttributeModifier(Attributes.ATTACK_SPEED, modifierId("insand_buff_2"),
                                    -0.1, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                            .addAttributeModifier(Attributes.ATTACK_DAMAGE, modifierId("insand_buff_3"),
                                    -2, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(APIAttributes.SKILLCD, modifierId("insand_buff_4"),
                                    1, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.ENTITY_INTERACTION_RANGE, modifierId("insand_buff_5"),
                                    -0.2, AttributeModifier.Operation.ADD_VALUE)
                            .addAttributeModifier(Attributes.BLOCK_INTERACTION_RANGE, modifierId("insand_buff_6"),
                                    -0.2, AttributeModifier.Operation.ADD_VALUE));

    /**
     * 恍惚 (trance_buff)。
     * <p>
     * 有害效果，仅对玩家生效时基础值：瞬身冷却 +1、移速 -0.02、攻速 -0.2、攻击力 -1、战技冷却 +0.5；
     * 移除时对称撤销。
     */
    public static final MobEffectResult TRANCE_BUFF =
            MobEffectAPI.createEffect("trance_buff")
                    .harmful()
                    .color(0xFFBC726D)
                    .onApply((entity, amplifier) -> tranceShift(entity, 1))
                    .onRemove((entity, amplifier) -> tranceShift(entity, -1))
                    .build();

    /**
     * 不振 (lethargy_buff)。
     * <p>
     * 有害效果，仅对玩家生效时基础值：瞬身冷却 +0.5、移速 -0.01、攻速 -0.1、战技冷却 +0.2；
     * 移除时对称撤销。
     */
    public static final MobEffectResult LETHARGY_BUFF =
            MobEffectAPI.createEffect("lethargy_buff")
                    .harmful()
                    .color(0xFFD3A2A2)
                    .onApply((entity, amplifier) -> lethargyShift(entity, 1))
                    .onRemove((entity, amplifier) -> lethargyShift(entity, -1))
                    .build();

    /**
     * 精神回复 (san_increase)。
     * <p>
     * 有益效果 0x98FB98。瞬时生效：San 值 +((amplifier &amp; 0xff) + 1)。
     * 已在主模块 PDItemsFoods 中被银狐棉花糖引用。
     */
    public static final DeferredHolder<MobEffect, MobEffect> SAN_INCREASE =
            MobEffectAPI.REGISTRY.register("san_increase",
                    () -> new InstantenousMobEffect(MobEffectCategory.BENEFICIAL, 0x98FB98) {
                        @Override
                        public boolean applyEffectTick(net.minecraft.world.entity.LivingEntity entity, int amplifier) {
                            if (entity instanceof net.minecraft.server.level.ServerPlayer pl) {
                                SanHelper.addPlayerSanWithCheck(pl, (amplifier & 0xff) + 1);
                            }
                            return true;
                        }
                    });

    /**
     * 精神损伤 (san_decrease)。
     * <p>
     * 有害效果 0x9B4400。瞬时生效：San 值 -((amplifier &amp; 0xff) + 1)。
     * 对照原版 {@code SanVaryMobEffect(false)}；语言键已在主模 assets 中。
     */
    public static final DeferredHolder<MobEffect, MobEffect> SAN_DECREASE =
            MobEffectAPI.REGISTRY.register("san_decrease",
                    () -> new InstantenousMobEffect(MobEffectCategory.HARMFUL, 0x9B4400) {
                        @Override
                        public boolean applyEffectTick(net.minecraft.world.entity.LivingEntity entity, int amplifier) {
                            if (entity instanceof net.minecraft.server.level.ServerPlayer pl) {
                                SanHelper.addPlayerSanWithCheck(pl, -(amplifier & 0xff) - 1);
                            }
                            return true;
                        }
                    });

    private PDSanityEffects() {
    }

    /**
     * 生成 pasterdream 命名空间下的属性修饰符 ID。
     *
     * @param path 路径
     * @return 修饰符 ID
     */
    private static ResourceLocation modifierId(String path) {
        return ResourceLocation.fromNamespaceAndPath("pasterdream", path);
    }

    /**
     * 修改属性基础值。
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

    /** 恍惚基础值批量修改（sign=+1 生效 / -1 撤销） */
    private static void tranceShift(LivingEntity entity, int sign) {
        if (entity instanceof Player) {
            shiftBaseValue(entity, APIAttributes.TELEPORTATIONCD, sign);
            shiftBaseValue(entity, Attributes.MOVEMENT_SPEED, -0.02 * sign);
            shiftBaseValue(entity, Attributes.ATTACK_SPEED, -0.2 * sign);
            shiftBaseValue(entity, Attributes.ATTACK_DAMAGE, -sign);
            shiftBaseValue(entity, APIAttributes.SKILLCD, 0.5 * sign);
        }
    }

    /** 不振基础值批量修改（sign=+1 生效 / -1 撤销） */
    private static void lethargyShift(LivingEntity entity, int sign) {
        if (entity instanceof Player) {
            shiftBaseValue(entity, APIAttributes.TELEPORTATIONCD, 0.5 * sign);
            shiftBaseValue(entity, Attributes.MOVEMENT_SPEED, -0.01 * sign);
            shiftBaseValue(entity, Attributes.ATTACK_SPEED, -0.1 * sign);
            shiftBaseValue(entity, APIAttributes.SKILLCD, 0.2 * sign);
        }
    }

    // ---------- insand_buff tick 逻辑 ----------

    /** 疯狂 tick：画面抖动 + 恐怖生物幻觉召唤 */
    private static void insandTick(LivingEntity entity) {
        if (entity.level().isClientSide() || !entity.isAlive() || !(entity instanceof Player)) {
            return;
        }
        int amplifier = entity.hasEffect(INSAND_BUFF)
                ? entity.getEffect(INSAND_BUFF).getAmplifier() : 0;
        double x = entity.getX(), y = entity.getY(), z = entity.getZ();
        ServerLevel level = entity.level() instanceof ServerLevel sl ? sl : null;
        if (level == null) {
            return;
        }
        if (amplifier == 0) {
            jitterRotation(entity, 0.5, 0.5);
            if (Math.random() < 0.005) {
                if (Math.random() < 0.3) {
                    spawnHallucination(level, "terrorbeak", x, y, z, true);
                }
                if (Math.random() < 0.5) {
                    spawnHallucination(level, "shadow_hand", x, y, z, false);
                }
            }
        } else if (amplifier == 1) {
            jitterRotation(entity, 1, 1);
            if (Math.random() < 0.01) {
                if (Math.random() < 0.25) {
                    spawnHallucination(level, "terrorbeak", x, y, z, true);
                }
                if (Math.random() < 0.5) {
                    spawnHallucination(level, "shadow_hand", x, y, z, false);
                }
            }
        } else if (amplifier == 2) {
            jitterRotation(entity, 3, 2);
            if (Math.random() < 0.025) {
                if (hasCurioEquipped(entity, "white_flower_body") && entity.getHealth() > 1) {
                    entity.hurt(entity.damageSources().fellOutOfWorld(), 1);
                }
                if (Math.random() < 0.05) {
                    spawnHallucination(level, "terrorbeak", x, y, z, true);
                }
                if (Math.random() < 0.03) {
                    spawnHallucination(level, "crazy_terrorbeak", x, y, z, true);
                }
                if (Math.random() < 0.5) {
                    spawnHallucination(level, "shadow_hand", x, y, z, false);
                }
            }
        }
    }

    /** 视角抖动 */
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
     * 召唤幻觉生物。
     *
     * @param level      服务端世界
     * @param entityId   实体注册 ID（不含命名空间，默认 pasterdream）
     * @param x          目标 X
     * @param y          目标 Y
     * @param z          目标 Z
     * @param checkAbove true 检查 y+2 处为空气
     */
    private static void spawnHallucination(ServerLevel level, String entityId, double x, double y, double z, boolean checkAbove) {
        Optional<EntityType<?>> optionalType = BuiltInRegistries.ENTITY_TYPE.getOptional(
                ResourceLocation.fromNamespaceAndPath("pasterdream", entityId));
        if (optionalType.isEmpty()) {
            return;
        }
        EntityType<?> type = optionalType.get();
        BlockPos checkPos = BlockPos.containing(x, checkAbove ? y + 2 : y, z);
        if (level.getBlockState(checkPos).is(Blocks.AIR)) {
            Entity spawned = type.spawn(level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
            if (spawned != null) {
                spawned.setYRot(level.getRandom().nextFloat() * 360F);
            }
        }
    }

    /**
     * 判断实体是否装备了指定 Curios 饰品。
     *
     * @param entity 目标实体
     * @param itemId 物品注册 ID（不含命名空间，默认 pasterdream）
     * @return 是否装备
     */
    private static boolean hasCurioEquipped(LivingEntity entity, String itemId) {
        Optional<net.minecraft.world.item.Item> optionalItem = BuiltInRegistries.ITEM.getOptional(
                ResourceLocation.fromNamespaceAndPath("pasterdream", itemId));
        if (optionalItem.isEmpty()) {
            return false;
        }
        net.minecraft.world.item.Item item = optionalItem.get();
        return top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(entity)
                .map(handler -> handler.findFirstCurio(item).isPresent())
                .orElse(false);
    }

    /**
     * 清除并重新施加反胃效果，模拟画面抖动。
     *
     * @param entity 目标实体
     */
    @SuppressWarnings("unused")
    private static void refreshNausea(LivingEntity entity) {
        entity.removeEffect(MobEffects.CONFUSION);
        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false, false));
    }
}
