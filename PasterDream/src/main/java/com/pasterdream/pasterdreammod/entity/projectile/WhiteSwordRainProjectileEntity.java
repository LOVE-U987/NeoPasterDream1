package com.pasterdream.pasterdreammod.entity.projectile;

import com.pasterdream.pasterdreammod.api.entity.projectile.AbstractWandProjectileEntity;
import com.pasterdream.pasterdreammod.entity.mob.AaroncosLefthand0Entity;
import com.pasterdream.pasterdreammod.entity.mob.AaroncosRighthand0Entity;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/**
 * 白厄剑雨光剑 (white_sword_rain_projectile)
 * <p>
 * 还原自原版 WhiteSwordRainProjectileEntity：
 * <ul>
 *   <li>飞行拖尾：末地烛（向下）+ 尘埃粒子（WhiteSwordRainPr0Procedure）</li>
 *   <li>命中实体（WhiteSwordRainPr1Procedure）：亚伦柯斯之触 6% / 暗影生物 12% 概率
 *       施加暗影沉默 200 tick；所有命中目标附加束缚 40 tick</li>
 *   <li>默认弹道参数：动能 1.1、伤害 4，无发射音效</li>
 * </ul>
 */
public class WhiteSwordRainProjectileEntity extends AbstractWandProjectileEntity {

    /** 暗影生物标签 */
    private static final TagKey<EntityType<?>> SHADOW_MOB_TAG = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath("pasterdream", "shadow_mob"));

    /** 渲染物品缓存（白厄剑雨物品） */
    private ItemStack cachedItem = ItemStack.EMPTY;

    public WhiteSwordRainProjectileEntity(EntityType<? extends WhiteSwordRainProjectileEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected ItemStack projectileItem() {
        if (cachedItem == null || cachedItem.isEmpty()) {
            cachedItem = new ItemStack(PDItems.WHITE_SWORD_RAIN.get());
        }
        return cachedItem;
    }

    @Override
    protected void onTickEffect() {
        // 原版 WhiteSwordRainPr0Procedure：末地烛拖尾（客户端，向下速度）+ 尘埃粒子（服务端广播）
        this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 0, -1, 0);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                    this.getX(), this.getY(), this.getZ(), 2, 0.1, 0, 0.1, 0.1);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        // 原版 WhiteSwordRainPr1Procedure：暗影沉默概率 + 束缚
        Entity entity = hitResult.getEntity();
        if (!(entity instanceof LivingEntity living) || living.level().isClientSide()) {
            return;
        }
        if (entity instanceof AaroncosLefthand0Entity || entity instanceof AaroncosRighthand0Entity) {
            if (Math.random() <= 0.06) {
                living.addEffect(new MobEffectInstance(PDEffects.SHADOW_SILENCE_BUFF.holder(), 200, 0));
            }
        } else if (entity.getType().is(SHADOW_MOB_TAG) && Math.random() <= 0.12) {
            living.addEffect(new MobEffectInstance(PDEffects.SHADOW_SILENCE_BUFF.holder(), 200, 0));
        }
        living.addEffect(new MobEffectInstance(PDEffects.BIND_BUFF.holder(), 40, 0));
    }

    /**
     * 沿视线发射（原版默认参数：动能 1.1、伤害 4、击退 0，无音效）
     */
    public static WhiteSwordRainProjectileEntity shoot(Level level, LivingEntity shooter, RandomSource random) {
        WhiteSwordRainProjectileEntity projectile =
                new WhiteSwordRainProjectileEntity(PDEntities.WHITE_SWORD_RAIN_PROJECTILE.get(), level);
        configureShot(projectile, level, shooter, random, 1.1f, 4, null);
        return projectile;
    }

    /**
     * 生成一支白色灾厄剑技的下落光剑（原版 WhiteSwordPr0Procedure 的剑雨条目）
     * <p>
     * 竖直下落（shoot(0,-1,0)，速度 1、散布 0.1），穿透 1、静音。
     *
     * @param level   服务端世界
     * @param shooter 施法者（作为伤害归属）
     * @param x       生成点 X
     * @param y       生成点 Y
     * @param z       生成点 Z
     * @param damage  基础伤害（3 + 0.4*攻击力）
     * @return 已加入世界的光剑投射物
     */
    public static WhiteSwordRainProjectileEntity summonRainSword(ServerLevel level, LivingEntity shooter,
                                                                 double x, double y, double z, float damage) {
        WhiteSwordRainProjectileEntity sword =
                new WhiteSwordRainProjectileEntity(PDEntities.WHITE_SWORD_RAIN_PROJECTILE.get(), level);
        sword.setOwner(shooter);
        sword.setBaseDamage(damage);
        sword.setSilent(true);
        sword.setPierceCount((byte) 1);
        sword.setPos(x, y, z);
        sword.shoot(0, -1, 0, 1, 0.1f);
        level.addFreshEntity(sword);
        return sword;
    }
}
