package com.pasterdream.pasterdreammod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 丛林孢子植株方块。
 * <p>
 * 参考原版蘑菇（{@code MushroomBlock}）的自然蔓延：随机刻以 1/25 概率尝试把自身复制到
 * 附近空位（9×3×9 范围内同类上限 5，±1 范围尝试 4 次）。与原版的差异是「忽略亮度」：
 * {@link FlowerBlock} 继承自 {@code BushBlock}，其 {@code canSurvive} 不含亮度检查，
 * 故蔓延在明亮环境下同样成立。
 * <p>
 * 骨粉（{@link BonemealableBlock}）立即催生一次同样的蔓延，不做巨型化。
 */
public class JungleSporePlantBlock extends FlowerBlock implements BonemealableBlock {

    /** 随机刻蔓延概率分母（对应原版 1/25） */
    private static final int SPREAD_CHANCE = 25;

    /** 9×3×9 范围内同类植株数量上限（对应原版 5） */
    private static final int CROWD_LIMIT = 5;

    /** 目标点随机尝试次数（对应原版 4） */
    private static final int SPREAD_TRIES = 4;

    /**
     * @param effect     触碰 / 迷之炖菜效果
     * @param seconds    效果持续秒数
     * @param properties 方块属性（需含 {@code randomTicks()}）
     */
    public JungleSporePlantBlock(Holder<MobEffect> effect, float seconds, BlockBehaviour.Properties properties) {
        super(effect, seconds, properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(SPREAD_CHANCE) == 0) {
            spread(state, level, pos, random);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        spread(state, level, pos, random);
    }

    /**
     * 一次蔓延尝试：复刻原版 {@code MushroomBlock.randomTick} 的扩散算法（无亮度限制）。
     *
     * @param state  当前植株状态
     * @param level  服务端世界
     * @param origin 起始位置
     * @param random 随机源
     */
    private void spread(BlockState state, ServerLevel level, BlockPos origin, RandomSource random) {
        int remaining = CROWD_LIMIT;
        for (BlockPos scan : BlockPos.betweenClosed(origin.offset(-4, -1, -4), origin.offset(4, 1, 4))) {
            if (level.getBlockState(scan).is(this) && --remaining <= 0) {
                return;
            }
        }

        BlockPos cursor = origin;
        BlockPos target = origin.offset(random.nextInt(3) - 1,
                random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
        for (int i = 0; i < SPREAD_TRIES; i++) {
            if (level.isEmptyBlock(target) && state.canSurvive(level, target)) {
                cursor = target;
            }
            target = cursor.offset(random.nextInt(3) - 1,
                    random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
        }
        if (level.isEmptyBlock(target) && state.canSurvive(level, target)) {
            level.setBlock(target, state, 2);
        }
    }
}
