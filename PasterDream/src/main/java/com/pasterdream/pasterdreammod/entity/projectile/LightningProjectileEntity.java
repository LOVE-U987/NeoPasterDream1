package com.pasterdream.pasterdreammod.entity.projectile;

import com.pasterdream.pasterdreammod.api.entity.projectile.AbstractWandProjectileEntity;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 闪电投射物 (lightning_projectile)
 * <p>
 * 还原自原版 LightningProjectileEntity：雷云/高压雷云召唤的天罚落雷，
 * 也保留原版“沿视线发射”的静态工厂。
 * <ul>
 *   <li>飞行拖尾：闪电粒子 + 电火花（LightningProjectilePr0Procedure）</li>
 *   <li>默认弹道参数：动能 1、伤害 5、无击退，发射音效 thundercloud_attack</li>
 *   <li>雷云攻击版本由 {@link #summonRainBolt} 生成：竖直下落、伤害 7/10、穿透 1</li>
 * </ul>
 */
public class LightningProjectileEntity extends AbstractWandProjectileEntity {

    /** 渲染物品缓存（夜明蝶 lightning_item，延迟创建避免注册期取值） */
    private ItemStack cachedItem = ItemStack.EMPTY;

    public LightningProjectileEntity(EntityType<? extends LightningProjectileEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected ItemStack projectileItem() {
        if (cachedItem == null || cachedItem.isEmpty()) {
            cachedItem = new ItemStack(PDItems.LIGHTNING_ITEM.get());
        }
        return cachedItem;
    }

    @Override
    protected void onTickEffect() {
        // 原版 LightningProjectilePr0Procedure：闪电粒子 + 电火花（客户端渲染）
        this.level().addParticle((SimpleParticleType) PDParticles.LIGHTNING_PARTICLE.particleType(),
                this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        this.level().addParticle(ParticleTypes.ELECTRIC_SPARK,
                this.getX(), this.getY(), this.getZ(), 0, 0, 0);
    }

    /**
     * 沿视线发射（原版默认参数：动能 1、伤害 5、击退 0）
     */
    public static LightningProjectileEntity shoot(Level level, LivingEntity shooter, RandomSource random) {
        LightningProjectileEntity projectile =
                new LightningProjectileEntity(PDEntities.LIGHTNING_PROJECTILE.get(), level);
        configureShot(projectile, level, shooter, random, 1f, 5, PDSounds.THUNDERCLOUD_ATTACK.get());
        return projectile;
    }

    /**
     * 生成一支雷云落雷（原版 ThundercloudPr0/Pr1 等 procedure 的散射落雷）
     * <p>
     * 竖直向下（shoot(0,-1,0)，速度 1、散布 0），静音、穿透 1。
     *
     * @param level  服务端世界
     * @param x      生成点 X
     * @param y      生成点 Y
     * @param z      生成点 Z
     * @param damage 基础伤害（雷云 7 / 高压雷云 10）
     * @return 已加入世界的落雷投射物
     */
    public static LightningProjectileEntity summonRainBolt(Level level, double x, double y, double z, double damage) {
        LightningProjectileEntity bolt =
                new LightningProjectileEntity(PDEntities.LIGHTNING_PROJECTILE.get(), level);
        bolt.setBaseDamage(damage);
        bolt.setSilent(true);
        bolt.setPierceCount((byte) 1);
        bolt.setPos(x, y, z);
        bolt.shoot(0, -1, 0, 1, 0);
        level.addFreshEntity(bolt);
        return bolt;
    }

    /**
     * 朝目标发射（原版 shoot(entity, target) 版本，伤害 5）
     */
    public static LightningProjectileEntity shoot(LivingEntity shooter, LivingEntity target) {
        LightningProjectileEntity projectile =
                new LightningProjectileEntity(PDEntities.LIGHTNING_PROJECTILE.get(), shooter.level());
        projectile.setOwner(shooter);
        projectile.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
        double dx = target.getX() - shooter.getX();
        double dy = target.getY() + target.getEyeHeight() - 1.1;
        double dz = target.getZ() - shooter.getZ();
        projectile.shoot(dx, dy - projectile.getY() + Math.hypot(dx, dz) * 0.2F, dz, 1f * 2, 12.0F);
        projectile.setSilent(true);
        projectile.setBaseDamage(5);
        projectile.setCritArrow(false);
        shooter.level().addFreshEntity(projectile);
        shooter.level().playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                PDSounds.THUNDERCLOUD_ATTACK.get(), SoundSource.PLAYERS, 1,
                1f / (shooter.getRandom().nextFloat() * 0.5f + 1));
        return projectile;
    }
}
