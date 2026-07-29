package com.pasterdream.pasterdreammod.entity.projectile;

import com.pasterdream.pasterdreammod.api.entity.projectile.AbstractWandProjectileEntity;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 草莓甜心音符弹 (strawberry_heart_projectile)
 * <p>
 * 还原自原版 StrawberryHeartProjectEntity（注册名经 renames 映射为 strawberry_heart_projectile）：
 * <ul>
 *   <li>飞行拖尾：爱心 + 尘埃粒子（StrawberryHeartProjectPr0Procedure）</li>
 *   <li>默认弹道参数：动能 2、伤害 1，发射音效 block.note_block.guitar</li>
 * </ul>
 */
public class StrawberryHeartProjectileEntity extends AbstractWandProjectileEntity {

    /** 渲染物品缓存（魔法石） */
    private ItemStack cachedItem = ItemStack.EMPTY;

    public StrawberryHeartProjectileEntity(EntityType<? extends StrawberryHeartProjectileEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected ItemStack projectileItem() {
        if (cachedItem == null || cachedItem.isEmpty()) {
            cachedItem = new ItemStack(PDItems.MAGIC_STONE.get());
        }
        return cachedItem;
    }

    @Override
    protected void onTickEffect() {
        // 原版 StrawberryHeartProjectPr0Procedure：爱心（y-0.4）+ 尘埃粒子
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.HEART,
                    this.getX(), this.getY() - 0.4, this.getZ(), 1, 0.1, 0.1, 0.1, 0.05);
            serverLevel.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                    this.getX(), this.getY(), this.getZ(), 3, 0.1, 0.1, 0.1, 0.1);
        }
    }

    /**
     * 沿视线发射（原版默认参数：动能 2、伤害 1、击退 0）
     */
    public static StrawberryHeartProjectileEntity shoot(Level level, LivingEntity shooter, RandomSource random) {
        StrawberryHeartProjectileEntity projectile =
                new StrawberryHeartProjectileEntity(PDEntities.STRAWBERRY_HEART_PROJECTILE.get(), level);
        configureShot(projectile, level, shooter, random, 2f, 1, SoundEvents.NOTE_BLOCK_GUITAR.value());
        return projectile;
    }
}
