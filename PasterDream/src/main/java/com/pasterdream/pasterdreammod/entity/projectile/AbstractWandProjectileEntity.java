package com.pasterdream.pasterdreammod.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * 法杖武器投射物公共基类 (Abstract Wand Projectile)
 * <p>
 * 还原自原版 PasterDream 的 MCreator 弹射物模板（XxxProjectileEntity 系列），
 * 8 个法杖/法术书投射物共用的行为骨架：
 * <ul>
 *   <li>继承 {@link AbstractArrow} 并实现 {@link ItemSupplier}，以指定物品贴图渲染（ThrownItemRenderer）</li>
 *   <li>{@link #doPostHurtEffects} 命中后扣回目标身上的箭矢计数（原版模板行为，避免箭插模型）</li>
 *   <li>每 tick 调用 {@link #onTickEffect()} 播放拖尾粒子；落地（inGround）即消失</li>
 *   <li>静态发射工具 {@link #configureShot}：沿视线方向发射，静音、非暴击，
 *       速度 power*2、散布 0（与原版 shoot() 静态工厂逐参数一致）</li>
 * </ul>
 * 注意：1.21.1 中 {@code AbstractArrow#setKnockback} 已被移除，原版各弹射物的击退等级
 * （0/1）不再显式设置，与本项目既有弹射物移植（BoneWingFireBall 等）保持同一处理。
 */
public abstract class AbstractWandProjectileEntity extends AbstractArrow implements ItemSupplier {

    /**
     * 构造法杖投射物（注册工厂使用）
     *
     * @param type  实体类型
     * @param level 世界实例
     */
    protected AbstractWandProjectileEntity(EntityType<? extends AbstractWandProjectileEntity> type, Level level) {
        super(type, level);
    }

    /**
     * 获取渲染/拾取所用的物品（子类返回各自的 PROJECTILE_ITEM）
     *
     * @return 渲染物品栈
     */
    protected abstract ItemStack projectileItem();

    @Override
    public ItemStack getItem() {
        return projectileItem();
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return projectileItem();
    }

    @Override
    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        // 原版模板：命中后目标不显示插箭（箭矢计数 -1）
        entity.setArrowCount(entity.getArrowCount() - 1);
    }

    /**
     * 每 tick 的拖尾效果（默认无，子类按原版 procedure 覆写）
     */
    protected void onTickEffect() {
    }

    /**
     * 设置箭矢穿透等级（等价原版 1.20.1 的 {@code setPierceLevel}）
     * <p>
     * 1.21.1 中 {@code AbstractArrow#setPierceLevel} 已改为 private（穿透等级仅由
     * 发射武器的穿透附魔写入）；本项目不引入 AccessTransformer，改经存档 NBT 的
     * {@code PierceLevel} 键往返一次完成写入——先完整导出当前附加存档数据，改写
     * 穿透等级后再读回，除穿透外所有字段等值还原，语义与原版调用一致。
     *
     * @param level 穿透等级（可额外穿过的实体数）
     */
    protected void setPierceCount(byte level) {
        CompoundTag tag = new CompoundTag();
        this.addAdditionalSaveData(tag);
        tag.putByte("PierceLevel", level);
        this.readAdditionalSaveData(tag);
    }

    @Override
    public void tick() {
        super.tick();
        onTickEffect();
        // 原版模板：落地即消失
        if (this.inGround) {
            this.discard();
        }
    }

    /**
     * 按原版 shoot() 静态工厂配置一次沿视线的发射（发射者眼位 -0.1，速度 power*2）
     *
     * @param projectile 待发射的投射物
     * @param level      世界
     * @param shooter    发射者
     * @param random     随机源（用于音调公式）
     * @param power      动能（原版 tooltip 中的“法球动能”）
     * @param damage     基础伤害
     * @param sound      发射音效（null 表示不播放，如白色剑雨）
     */
    protected static void configureShot(AbstractWandProjectileEntity projectile, Level level,
                                        LivingEntity shooter, RandomSource random,
                                        float power, double damage, @Nullable SoundEvent sound) {
        projectile.setOwner(shooter);
        projectile.setPos(shooter.getX(), shooter.getEyeY() - 0.1, shooter.getZ());
        Vec3 view = shooter.getViewVector(1);
        projectile.shoot(view.x, view.y, view.z, power * 2, 0);
        projectile.setSilent(true);
        projectile.setCritArrow(false);
        projectile.setBaseDamage(damage);
        level.addFreshEntity(projectile);
        if (sound != null) {
            // 原版发射音调公式：1 / (rand*0.5+1) + power/2
            level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(),
                    sound, SoundSource.PLAYERS, 1.0f,
                    1f / (random.nextFloat() * 0.5f + 1) + power / 2);
        }
    }
}
