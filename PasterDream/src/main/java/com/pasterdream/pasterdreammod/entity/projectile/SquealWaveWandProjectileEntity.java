package com.pasterdream.pasterdreammod.entity.projectile;

import com.pasterdream.pasterdreammod.api.entity.projectile.AbstractWandProjectileEntity;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * 魂啸法杖音波 (squeal_wave_wand_projectile)
 * <p>
 * 还原自原版 SquealWaveWandProjectileEntity：
 * <ul>
 *   <li>飞行拖尾：魂啸粒子 + 幽匿之魂（SquealWaveWandPr0Procedure）</li>
 *   <li>命中实体（SquealWaveWandPr3Procedure）：灵魂逃逸音效；
 *       施法者法术强度 ≥1 时对直径 3 范围造成 2+法术强度+1.5 点魔法伤害并爆发灵魂粒子；
 *       佩戴幽灵面具时 10% 概率在 1 tick 后召唤怨魂（并驯服附近怨魂——原版为 TamableAnimal 检查，
 *       本项目怨魂未实现驯服接口时该分支自然跳过，保持原语义）</li>
 *   <li>默认弹道参数：动能 1.3、伤害 4，发射音效 pasterdream:squeal_wave</li>
 * </ul>
 */
public class SquealWaveWandProjectileEntity extends AbstractWandProjectileEntity {

    /** 特殊实体标签（免疫 AoE 的实体） */
    private static final TagKey<EntityType<?>> SPECIAL_ENTITY_TAG = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath("pasterdream", "special_entity_tag"));

    /** 渲染物品缓存（魔法石） */
    private ItemStack cachedItem = ItemStack.EMPTY;

    public SquealWaveWandProjectileEntity(EntityType<? extends SquealWaveWandProjectileEntity> type, Level level) {
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
        // 原版 SquealWaveWandPr0Procedure：魂啸粒子（客户端）+ 幽匿之魂（服务端广播）
        this.level().addParticle((SimpleParticleType) PDParticles.SQUEAL_WAVE_PARTICLE.particleType(),
                this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL,
                    this.getX(), this.getY(), this.getZ(), 2, 0.1, 0.1, 0.1, 0);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        Entity hitEntity = hitResult.getEntity();
        // 原版 SquealWaveWandPr3Procedure：灵魂逃逸音效（0.5）
        serverLevel.playSound(null, BlockPos.containing(x, y, z),
                SoundEvents.SOUL_ESCAPE.value(), SoundSource.NEUTRAL, 0.5f, 1.0f);
        if (!(this.getOwner() instanceof Player owner)) {
            return;
        }
        AttributeInstance magicPower = owner.getAttribute(PDAttributes.MAGICPOWER);
        if (magicPower != null && magicPower.getBaseValue() >= 1) {
            // 直径 3 的范围伤害：2 + 法术强度 + 1.5 点魔法伤害
            Vec3 center = new Vec3(x, y, z);
            for (Entity around : serverLevel.getEntitiesOfClass(Entity.class,
                    new AABB(center, center).inflate(3 / 2d), e -> true)) {
                // 与原版逐字一致：此处判断的是被命中实体（而非迭代实体）的特殊实体标签
                if (!hitEntity.getType().is(SPECIAL_ENTITY_TAG)) {
                    around.hurt(serverLevel.damageSources().magic(),
                            (float) (2 + magicPower.getBaseValue() + 1.5));
                }
            }
            serverLevel.sendParticles((SimpleParticleType) PDParticles.SOUL_PARTICLE.particleType(),
                    x, y, z, 16, 0.7, 0.5, 0.7, 0);
            serverLevel.sendParticles(ParticleTypes.SOUL, x, y, z, 16, 0.7, 0.5, 0.7, 0);
        }
        // 幽灵面具：10% 概率召唤怨魂
        boolean hasGhostFace = CuriosApi.getCuriosInventory(owner)
                .map(inv -> inv.findFirstCurio(PDItems.GHOST_FACE_HEAD.get()).isPresent())
                .orElse(false);
        if (hasGhostFace && Math.random() <= 0.1) {
            ServerScheduler.schedule(1, () -> {
                Entity summoned = PDEntities.FRIENDLY_GHOST.get().spawn(serverLevel,
                        BlockPos.containing(x, y + 1, z), MobSpawnType.MOB_SUMMONED);
                if (summoned != null) {
                    summoned.setYRot(1);
                    summoned.setYBodyRot(1);
                    summoned.setYHeadRot(1);
                }
                // 原版：驯服直径 5 范围内的怨魂（仅当实体实现 TamableAnimal）
                Vec3 center = new Vec3(x, y, z);
                for (Entity around : serverLevel.getEntitiesOfClass(Entity.class,
                        new AABB(center, center).inflate(5 / 2d), e -> true)) {
                    if (around.getType() == PDEntities.FRIENDLY_GHOST.get()
                            && around instanceof TamableAnimal tamable) {
                        tamable.tame(owner);
                    }
                }
            });
        }
    }

    /**
     * 沿视线发射（原版默认参数：动能 1.3、伤害 4、击退 0）
     */
    public static SquealWaveWandProjectileEntity shoot(Level level, LivingEntity shooter, RandomSource random) {
        SquealWaveWandProjectileEntity projectile =
                new SquealWaveWandProjectileEntity(PDEntities.SQUEAL_WAVE_WAND_PROJECTILE.get(), level);
        configureShot(projectile, level, shooter, random, 1.3f, 4, PDSounds.SQUEAL_WAVE.get());
        return projectile;
    }
}
