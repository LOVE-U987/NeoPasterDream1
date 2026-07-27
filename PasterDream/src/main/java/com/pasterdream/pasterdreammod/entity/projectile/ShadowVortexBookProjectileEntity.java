package com.pasterdream.pasterdreammod.entity.projectile;

import com.pasterdream.pasterdreammod.attachment.PDAttachments;
import com.pasterdream.pasterdreammod.item.WandSupport;
import com.pasterdream.pasterdreammod.registry.PDAttributes;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

/**
 * 暗影旋涡法球 (shadow_vortex_book_projectile)
 * <p>
 * 还原自原版 ShadowVortexBookProjectileEntity：
 * <ul>
 *   <li>飞行拖尾：大烟雾 + 烟雾（ShadowVortexBookPr2Procedure）</li>
 *   <li>命中方块：暗影石粒子 + 烟雾（ShadowVortexBookPr3Procedure）</li>
 *   <li>命中实体（ShadowVortexBookPr4Procedure）：同款粒子；若施法者处于潜行——
 *       全部法术物品冷却 240、在落点空气处展开友方暗影旋涡方块（friendly=true）并播放
 *       pasterdream:shadow_vortex 音效、法术强度不足 9 时按 (9-强度) 追加消耗融梦能量/San</li>
 *   <li>默认弹道参数：动能 0.7、伤害 8，发射音效 pasterdream:shadow_vortex_book</li>
 * </ul>
 */
public class ShadowVortexBookProjectileEntity extends AbstractWandProjectileEntity {

    /** 渲染物品缓存（魔法石） */
    private ItemStack cachedItem = ItemStack.EMPTY;

    public ShadowVortexBookProjectileEntity(EntityType<? extends ShadowVortexBookProjectileEntity> type, Level level) {
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
        // 原版 ShadowVortexBookPr2Procedure：大烟雾（客户端）+ 烟雾（服务端广播）
        this.level().addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY(), this.getZ(), 0, 0, 0);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    this.getX(), this.getY(), this.getZ(), 4, 0.15, 0.15, 0.15, 0);
        }
    }

    /**
     * 命中粒子（原版 Pr3/Pr4 共通）：暗影石粒子 + 烟雾各 5 个
     */
    private static void impactParticles(ServerLevel serverLevel, double x, double y, double z) {
        serverLevel.sendParticles((SimpleParticleType) PDParticles.SHADOW_STONE_PARTICLE.particleType(),
                x, y, z, 5, 0.1, 0.1, 0.1, 0.01);
        serverLevel.sendParticles(ParticleTypes.SMOKE, x, y, z, 5, 0.1, 0.1, 0.1, 0.01);
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        BlockPos pos = hitResult.getBlockPos();
        if (this.level() instanceof ServerLevel serverLevel) {
            impactParticles(serverLevel, pos.getX(), pos.getY(), pos.getZ());
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
        impactParticles(serverLevel, x, y, z);
        if (this.getOwner() == null || !this.getOwner().isShiftKeyDown()) {
            return;
        }
        // 潜行命中：展开暗影旋涡
        if (this.getOwner() instanceof Player owner) {
            WandSupport.applyTaggedCooldown(owner, WandSupport.MAGIC_TAG, 240);
        }
        BlockPos pos = BlockPos.containing(x, y, z);
        if (serverLevel.getBlockState(pos).getBlock() == Blocks.AIR) {
            serverLevel.setBlock(pos, PDBlocks.SHADOW_VORTEX.get().defaultBlockState(), 3);
            // 与原版一致：标记为友方旋涡（persistentData friendly=true）并同步方块更新
            BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
            BlockState state = serverLevel.getBlockState(pos);
            if (blockEntity != null) {
                blockEntity.getPersistentData().putBoolean("friendly", true);
            }
            serverLevel.sendBlockUpdated(pos, state, state, 3);
            serverLevel.playSound(null, pos, PDSounds.SHADOW_VORTEX.get(), SoundSource.BLOCKS, 1, 1);
        }
        // 法术强度不足 9：追加消耗理智（融梦能量已剥离至附属 mod）
        if (this.getOwner() instanceof Player owner) {
            AttributeInstance magicPower = owner.getAttribute(PDAttributes.MAGICPOWER);
            if (magicPower != null) {
                double power = magicPower.getValue();
                if (power < 9) {
                    PDAttachments.addPlayerSanWithCheck(owner, -0.02 * (9 - power));
                }
            }
        }
    }

    /**
     * 沿视线发射（原版默认参数：动能 0.7、伤害 8、击退 1）
     */
    public static ShadowVortexBookProjectileEntity shoot(Level level, LivingEntity shooter, RandomSource random) {
        ShadowVortexBookProjectileEntity projectile =
                new ShadowVortexBookProjectileEntity(PDEntities.SHADOW_VORTEX_BOOK_PROJECTILE.get(), level);
        configureShot(projectile, level, shooter, random, 0.7f, 8, PDSounds.SHADOW_VORTEX_BOOK.get());
        return projectile;
    }
}
