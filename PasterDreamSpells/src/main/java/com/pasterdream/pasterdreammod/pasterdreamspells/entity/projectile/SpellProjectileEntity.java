package com.pasterdream.pasterdreammod.pasterdreamspells.entity.projectile;

import com.pasterdream.pasterdreammod.pasterdreamspells.effect.SpellEffects;
import com.pasterdream.pasterdreammod.pasterdreamspells.registry.PDSpellsEntities;
import com.pasterdream.pasterdreammod.pasterdreamspells.registry.PDSpellsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

/**
 * 法术投射物 (Spell Projectile)
 * <p>
 * 五种法术（闪电/剧毒/治疗/狂暴/冰冻）共用的投射物实体，
 * 还原自原版 PasterDream 的 XxxSpellProjectileEntity 系列：
 * <ul>
 *   <li>弓箭弹道（AbstractArrow），发射初速 0.8，无伤害、无击退、静音、非暴击</li>
 *   <li>命中实体/方块后触发对应法术效果（{@link SpellEffects}）并消失</li>
 *   <li>落地不可拾取（原版效果触发后立即 discard，从无拾取机会）</li>
 *   <li>以飞行中的法术物品贴图渲染（{@link ItemSupplier} + ThrownItemRenderer）</li>
 * </ul>
 * 具体法术类型由实体类型决定（{@link SpellType}），五种类型各自注册 EntityType。
 *
 * @author PasterDream
 */
public class SpellProjectileEntity extends AbstractArrow implements ItemSupplier {

    /** 发射初速（原版 shoot power 0.4f * 2） */
    private static final float SHOOT_POWER = 0.4f * 2;

    /**
     * 法术类型：绑定实体类型、物品外观与命中效果
     */
    public enum SpellType {
        /** 闪电法术：5*5 区域 4 次随机落雷 */
        LIGHTNING(() -> PDSpellsEntities.LIGHTNING_SPELL_PROJECTILE.get(),
                () -> PDSpellsItems.LIGHTNING_SPELL.get(), SpellEffects::lightning),
        /** 剧毒法术：7*7 区域三波剧毒攻势 */
        POISON(() -> PDSpellsEntities.POISON_SPELL_PROJECTILE.get(),
                () -> PDSpellsItems.POISON_SPELL.get(), SpellEffects::poison),
        /** 治疗法术：生成治疗立场 */
        HEALING(() -> PDSpellsEntities.HEALING_SPELL_PROJECTILE.get(),
                () -> PDSpellsItems.HEALING_SPELL.get(), SpellEffects::healing),
        /** 狂暴法术：生成狂暴立场 */
        FURY(() -> PDSpellsEntities.FURY_SPELL_PROJECTILE.get(),
                () -> PDSpellsItems.FURY_SPELL.get(), SpellEffects::fury),
        /** 冰冻法术：7*7 区域 5 波冻结 */
        ICE(() -> PDSpellsEntities.ICE_SPELL_PROJECTILE.get(),
                () -> PDSpellsItems.ICE_SPELL.get(), SpellEffects::ice);

        /**
         * 命中效果回调（服务端）
         */
        @FunctionalInterface
        public interface ImpactEffect {
            /**
             * 在命中点触发法术效果
             *
             * @param level 服务端世界
             * @param x     命中点 X
             * @param y     命中点 Y
             * @param z     命中点 Z
             */
            void trigger(ServerLevel level, double x, double y, double z);
        }

        private final Supplier<EntityType<SpellProjectileEntity>> entityType;
        private final Supplier<Item> item;
        private final ImpactEffect impact;

        SpellType(Supplier<EntityType<SpellProjectileEntity>> entityType,
                  Supplier<Item> item, ImpactEffect impact) {
            this.entityType = entityType;
            this.item = item;
            this.impact = impact;
        }
    }

    /** 本投射物的法术类型（按实体类型懒解析） */
    private SpellType spellType;

    /**
     * 构造法术投射物（注册工厂使用）
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    public SpellProjectileEntity(EntityType<? extends SpellProjectileEntity> type, Level level) {
        super(type, level);
        this.pickup = Pickup.DISALLOWED;
        this.setSilent(true);
        this.setCritArrow(false);
        this.setBaseDamage(0);
    }

    /**
     * 解析本实体对应的法术类型
     *
     * @return 法术类型
     */
    private SpellType resolveSpellType() {
        if (spellType == null) {
            for (SpellType type : SpellType.values()) {
                if (type.entityType.get() == this.getType()) {
                    spellType = type;
                    break;
                }
            }
        }
        return spellType;
    }

    @Override
    public ItemStack getItem() {
        SpellType type = resolveSpellType();
        return type != null ? new ItemStack(type.item.get()) : ItemStack.EMPTY;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return getItem();
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        triggerImpact(this.getX(), this.getY(), this.getZ());
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        BlockPos pos = hitResult.getBlockPos();
        // 与原版一致：方块命中以方块整数坐标为效果中心
        triggerImpact(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * 触发命中效果并移除投射物（服务端）
     */
    private void triggerImpact(double x, double y, double z) {
        if (this.level() instanceof ServerLevel serverLevel) {
            SpellType type = resolveSpellType();
            if (type != null) {
                type.impact.trigger(serverLevel, x, y, z);
            }
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        // 与原版一致：落地即消失（正常情况下命中时已触发效果并 discard，此为兜底）
        if (this.inGround) {
            this.discard();
        }
    }

    /**
     * 由施法者沿视线方向发射法术投射物（还原原版 shoot() 静态工厂的行为）
     *
     * @param level  服务端世界
     * @param caster 施法者
     * @param type   法术类型
     * @return 已加入世界的投射物实体
     */
    public static SpellProjectileEntity shoot(ServerLevel level, LivingEntity caster, SpellType type) {
        SpellProjectileEntity projectile = new SpellProjectileEntity(type.entityType.get(), level);
        projectile.setOwner(caster);
        projectile.setPos(caster.getX(), caster.getEyeY() - 0.1, caster.getZ());
        Vec3 view = caster.getViewVector(1);
        projectile.shoot(view.x, view.y, view.z, SHOOT_POWER, 0);
        level.addFreshEntity(projectile);
        // 原版发射音效与音调公式：1 / (rand*0.5+1) + power/2
        level.playSound(null, caster.getX(), caster.getY(), caster.getZ(),
                SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 1.0f,
                1f / (level.getRandom().nextFloat() * 0.5f + 1) + 0.4f / 2);
        return projectile;
    }
}
