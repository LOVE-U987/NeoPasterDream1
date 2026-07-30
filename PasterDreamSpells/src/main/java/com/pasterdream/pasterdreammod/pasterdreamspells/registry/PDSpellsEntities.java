package com.pasterdream.pasterdreammod.pasterdreamspells.registry;

import com.pasterdream.pasterdreammod.pasterdreamspells.PasterDreamSpellsMod;
import com.pasterdream.pasterdreammod.pasterdreamspells.entity.mob.FurySpellFieldEntity;
import com.pasterdream.pasterdreammod.pasterdreamspells.entity.mob.HealingSpellFieldEntity;
import com.pasterdream.pasterdreammod.pasterdreamspells.entity.projectile.SpellProjectileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 法术系统实体注册类。
 * <p>
 * 负责注册治疗法术场域、狂暴法术场域以及五种法术投射物。
 * 所有实体均使用 {@link PasterDreamSpellsMod#MOD_ID}（{@code pasterdreamspells}）命名空间。
 *
 * @author PasterDream
 */
public class PDSpellsEntities {

    /** 实体类型注册器 */
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, PasterDreamSpellsMod.MOD_ID);

    /**
     * 闪电法术投射物 (lightning_spell_projectile)
     * 由闪电法术物品蓄力施放，命中后在 5*5 区域生成 4 次随机落雷。
     */
    public static final Supplier<EntityType<SpellProjectileEntity>> LIGHTNING_SPELL_PROJECTILE =
            ENTITY_TYPES.register("lightning_spell_projectile",
                    () -> EntityType.Builder.<SpellProjectileEntity>of(SpellProjectileEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("lightning_spell_projectile"));

    /**
     * 剧毒法术投射物 (poison_spell_projectile)
     * 命中后对 7*7 区域发动三波剧毒攻势。
     */
    public static final Supplier<EntityType<SpellProjectileEntity>> POISON_SPELL_PROJECTILE =
            ENTITY_TYPES.register("poison_spell_projectile",
                    () -> EntityType.Builder.<SpellProjectileEntity>of(SpellProjectileEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("poison_spell_projectile"));

    /**
     * 治疗法术投射物 (healing_spell_projectile)
     * 命中后生成治疗立场实体。
     */
    public static final Supplier<EntityType<SpellProjectileEntity>> HEALING_SPELL_PROJECTILE =
            ENTITY_TYPES.register("healing_spell_projectile",
                    () -> EntityType.Builder.<SpellProjectileEntity>of(SpellProjectileEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("healing_spell_projectile"));

    /**
     * 狂暴法术投射物 (fury_spell_projectile)
     * 命中后生成狂暴立场实体。
     */
    public static final Supplier<EntityType<SpellProjectileEntity>> FURY_SPELL_PROJECTILE =
            ENTITY_TYPES.register("fury_spell_projectile",
                    () -> EntityType.Builder.<SpellProjectileEntity>of(SpellProjectileEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("fury_spell_projectile"));

    /**
     * 冰冻法术投射物 (ice_spell_projectile)
     * 命中后对 7*7 区域发动 5 波冻结。
     */
    public static final Supplier<EntityType<SpellProjectileEntity>> ICE_SPELL_PROJECTILE =
            ENTITY_TYPES.register("ice_spell_projectile",
                    () -> EntityType.Builder.<SpellProjectileEntity>of(SpellProjectileEntity::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("ice_spell_projectile"));

    /**
     * 狂暴法术立场 (fury_spell_entity)
     * 纯展示立场实体，90 tick 后消散。
     */
    public static final Supplier<EntityType<FurySpellFieldEntity>> FURY_SPELL_ENTITY =
            ENTITY_TYPES.register("fury_spell_entity",
                    () -> EntityType.Builder.<FurySpellFieldEntity>of(FurySpellFieldEntity::new, MobCategory.MISC)
                            .sized(2f, 0.1f)
                            .fireImmune()
                            .clientTrackingRange(64 / 16)
                            .updateInterval(3)
                            .build("fury_spell_entity"));

    /**
     * 治疗法术立场 (healing_spell_entity)
     * 无敌的治疗立场实体，400 tick 内每 tick 治疗 5*5 范围。
     */
    public static final Supplier<EntityType<HealingSpellFieldEntity>> HEALING_SPELL_ENTITY =
            ENTITY_TYPES.register("healing_spell_entity",
                    () -> EntityType.Builder.<HealingSpellFieldEntity>of(HealingSpellFieldEntity::new, MobCategory.MONSTER)
                            .sized(0.1f, 0.1f)
                            .fireImmune()
                            .clientTrackingRange(64 / 16)
                            .updateInterval(3)
                            .build("healing_spell_entity"));

    private PDSpellsEntities() {
        throw new UnsupportedOperationException("PDSpellsEntities 是注册类，不可实例化");
    }
}
