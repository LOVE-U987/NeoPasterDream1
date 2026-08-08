package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.block.entity.SimpleMarkerBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * 冻结之花 (flower_17)
 * <p>
 * 原版 {@code Flower17Block} + {@code Flower17Pr0Procedure}/{@code Flower17Pr1Procedure} 的 1.21.1 移植：
 * 由 {@code flower_16} 雪傀儡解密获得。方块带<b>随机刻</b>，每次随机刻：
 * <ol>
 *   <li>在自身位置播撒雪花粒子；</li>
 *   <li>循环 8 次：随机取周围 {@code (-3..3, -1..0, -3..3)} 的偏移，
 *       若目标为空气则凝结成雪、为水则冻结成冰（偏移暂存于方块实体 NBT，与原版一致）。</li>
 * </ol>
 * 冻结核心见 {@link #freezeAround(ServerLevel, BlockPos, RandomSource)}。
 */
public class DyedreamFlower17Block extends DyedreamFlowerBlock implements EntityBlock {

    /**
     * 构造冻结之花
     *
     * @param properties 方块属性（需含 {@code randomTicks()})
     */
    public DyedreamFlower17Block(Properties properties) {
        super(MobEffects.MOVEMENT_SPEED, 100, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        // 原版提示文本：冻结周围的水源 并在地面凝结成雪
        tooltip.add(Component.literal("\u00A7f\u25AA \u00A79\u51B0\u51BB\u5468\u56F4\u7684\u6C34\u6E90 \u5E76\u5728\u5730\u9762\u51DD\u7ED3\u6210\u96EA"));
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        freezeAround(level, pos, random);
    }

    /**
     * 冻结之花随机刻效果（对应原版 {@code Flower17Pr0Procedure} + 8 次 {@code Flower17Pr1Procedure}）。
     * <p>
     * 播撒雪花粒子后，循环 8 次随机偏移冻结：空气 → 雪、水 → 冰。
     *
     * @param level  服务端世界
     * @param pos    花 17 坐标
     * @param random 随机源（随机刻提供）
     */
    public static void freezeAround(ServerLevel level, BlockPos pos, RandomSource random) {
        level.sendParticles(ParticleTypes.SNOWFLAKE,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 16, 0.5, 0.5, 0.5, 0.05);
        for (int i = 0; i < 8; i++) {
            freezeOnce(level, pos, random);
        }
    }

    /**
     * 单次随机冻结（对应原版 {@code Flower17Pr1Procedure}）：
     * 随机偏移 {@code (-3..3, -1..0, -3..3)} 写入方块实体 NBT，目标为空气 → 雪、水 → 冰。
     *
     * @param level  服务端世界
     * @param pos    花 17 坐标
     * @param random 随机源
     */
    private static void freezeOnce(ServerLevel level, BlockPos pos, RandomSource random) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) {
            return;
        }
        CompoundTag tag = be.getPersistentData();
        int rx = Mth.nextInt(random, -3, 3);
        int ry = Mth.nextInt(random, -1, 0);
        int rz = Mth.nextInt(random, -3, 3);
        tag.putDouble("random_x", rx);
        tag.putDouble("random_y", ry);
        tag.putDouble("random_z", rz);

        BlockPos target = pos.offset(rx, ry, rz);
        BlockState ts = level.getBlockState(target);
        if (ts.isAir()) {
            level.setBlock(target, Blocks.SNOW.defaultBlockState(), 3);
        } else if (ts.is(Blocks.WATER)) {
            level.setBlock(target, Blocks.ICE.defaultBlockState(), 3);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        // 复用通用标记方块实体（仅承载 NBT 数据，用于暂存随机偏移）
        return new SimpleMarkerBlockEntity(PDBlockEntities.FLOWER_17.get(), pos, state);
    }
}
