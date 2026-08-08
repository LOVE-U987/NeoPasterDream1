package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;

/**
 * 雪傀儡解密之花 (flower_16)
 * <p>
 * 原版 {@code Flower16Block} + {@code Flower16Pr0Procedure} 的 1.21.1 移植：
 * 双格花下方 3 格处放有染梦书桌，且四方位 5 格呈花阵布局
 * （{@code crop_0a} / {@code dyedream_sapling} / {@code flower_14} / {@code flower_9}），
 * 同时 9 格内存在雪傀儡与悦灵时，右键可触发解密：
 * 献祭最近的雪傀儡与悦灵 → 四方位花阵销毁 → 花 16 变花 17（冻结之花）→ 书桌消失
 * → 雪花/雪球粒子 + 梦境音效。
 * <p>
 * 解密核心见 {@link #tryDecrypt(Level, BlockPos)}。
 */
public class DyedreamFlower16Block extends DyedreamFlowerBlock {

    /**
     * 构造雪傀儡解密之花
     *
     * @param properties 方块属性
     */
    public DyedreamFlower16Block(Properties properties) {
        super(MobEffects.MOVEMENT_SPEED, 0, properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            tryDecrypt(level, pos);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            tryDecrypt(level, pos);
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * 雪傀儡解密核心逻辑（对应原版 {@code Flower16Pr0Procedure}）。
     * <p>
     * 校验顺序与参数与原版逐项一致：
     * <ol>
     *   <li>点击格下方 3 格为染梦书桌；</li>
     *   <li>四方位 5 格为 {@code crop_0a}/{@code dyedream_sapling}/{@code flower_14}/{@code flower_9}；</li>
     *   <li>9 格内同时存在雪傀儡与悦灵；</li>
     *   <li>献祭 15 格内最近的雪傀儡与悦灵 → 四方位花阵销毁 → 花 16 变花 17 → 书桌消失；</li>
     *   <li>雪花/雪球粒子 + 梦境音效 {@code dream0}。</li>
     * </ol>
     *
     * @param level 世界（服务端）
     * @param pos   被点击的花 16 坐标
     * @return 是否成功触发解密
     */
    public static boolean tryDecrypt(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return false;
        }
        // ① 下方 3 格为染梦书桌
        if (!level.getBlockState(pos.below(3)).is(PDBlocks.DYEDREAM_DESK.get())) {
            return false;
        }
        // ② 四方位 5 格花阵校验（坐标与原版 Flower16Pr0Procedure 一致）
        if (!isBlock(level, pos.offset(0, 0, 5), PDBlocks.CROP_0A.get())
                || !isBlock(level, pos.offset(0, 0, -5), PDBlocks.DYEDREAM_SAPLING.get())
                || !isBlock(level, pos.offset(5, 0, 0), PDBlocks.FLOWER_14.get())
                || !isBlock(level, pos.offset(-5, 0, 0), PDBlocks.FLOWER_9.get())) {
            return false;
        }
        // ③ 9 格内同时存在雪傀儡与悦灵
        AABB box9 = AABB.ofSize(Vec3.atCenterOf(pos), 9, 9, 9);
        if (level.getEntitiesOfClass(SnowGolem.class, box9).isEmpty()
                || level.getEntitiesOfClass(Allay.class, box9).isEmpty()) {
            return false;
        }

        // ④ 献祭 15 格内最近的雪傀儡与悦灵
        AABB box15 = AABB.ofSize(Vec3.atCenterOf(pos), 15, 15, 15);
        Vec3 center = Vec3.atCenterOf(pos);
        Entity allay = level.getEntitiesOfClass(Allay.class, box15).stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(center)))
                .orElse(null);
        if (allay != null) {
            allay.discard();
        }
        Entity golem = level.getEntitiesOfClass(SnowGolem.class, box15).stream()
                .min(Comparator.comparingDouble(e -> e.distanceToSqr(center)))
                .orElse(null);
        if (golem != null) {
            golem.discard();
        }

        // ⑤ 四方位花阵销毁
        level.destroyBlock(pos.offset(0, 0, -5), false);
        level.destroyBlock(pos.offset(0, 0, 5), false);
        level.destroyBlock(pos.offset(5, 0, 0), false);
        level.destroyBlock(pos.offset(-5, 0, 0), false);
        // 花 16 → 花 17（冻结之花）
        level.setBlock(pos, PDBlocks.FLOWER_17.get().defaultBlockState(), 3);
        // 书桌消失
        level.destroyBlock(pos.below(3), false);

        // 雪花/雪球粒子
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 16, 0.5, 0.5, 0.5, 0.05);
            serverLevel.sendParticles(ParticleTypes.ITEM_SNOWBALL,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 16, 0.5, 0.5, 0.5, 0.05);
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 64, 2, 1, 2, 0.1);
        }
        // 声音：梦境音效 dream0
        level.playSound(null, pos, PDSounds.DREAM0.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
        return true;
    }

    /**
     * 判断指定位置是否为目标方块
     *
     * @param level 世界
     * @param pos   目标坐标
     * @param block 期望方块
     * @return 匹配返回 true
     */
    private static boolean isBlock(Level level, BlockPos pos, Block block) {
        return level.getBlockState(pos).is(block);
    }
}
