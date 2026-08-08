package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksColdDomain;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;

/**
 * 冷域雪地草坪方块（雪地草坪）
 * <p>
 * 继承原版 {@link SpreadingSnowyDirtBlock}（草方块基类），具备原版草方块的核心行为：
 * <ul>
 *   <li>上方被雪覆盖时自动切换为 snowy 状态（侧面变纯雪纹理，由 blockstate JSON 控制）</li>
 *   <li>可沿随机刻向四周蔓延到冷域泥土（{@link PDBlocksColdDomain#COLD_DOMAIN_DIRT}）</li>
 *   <li>上方光照不足/被遮挡/被水覆盖时，退化为冷域泥土</li>
 * </ul>
 * 区别于原版：退化与蔓延的目标方块均为冷域泥土，而非原版泥土。
 */
public class SnowyColdDomainGrassBlock extends SpreadingSnowyDirtBlock {

    /** 方块编解码器（供方块状态系统序列化） */
    public static final MapCodec<SnowyColdDomainGrassBlock> CODEC = simpleCodec(SnowyColdDomainGrassBlock::new);

    /**
     * @param properties 方块属性（复制自原版草方块）
     */
    public SnowyColdDomainGrassBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends SpreadingSnowyDirtBlock> codec() {
        return CODEC;
    }

    /**
     * 判断方块上方环境是否允许草存活（复制原版 {@code canBeGrass} 逻辑：
     * 上方为单层雪允许；上方为流体不允许；否则依赖光照）。
     *
     * @param state 当前方块状态
     * @param level 世界读取器
     * @param pos   方块位置
     * @return 允许存活返回 true
     */
    private static boolean canBeGrass(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);
        if (aboveState.is(Blocks.SNOW) && aboveState.getValue(SnowLayerBlock.LAYERS) == 1) {
            return true;
        } else if (aboveState.getFluidState().getAmount() == 8) {
            return false;
        } else {
            int i = LightEngine.getLightBlockInto(
                    level, state, pos, aboveState, above, Direction.UP, aboveState.getLightBlock(level, above));
            return i < level.getMaxLightLevel();
        }
    }

    /**
     * 判断目标位置上方是否可长草（复制原版 {@code canPropagate} 逻辑）。
     *
     * @param state 目标位置方块状态
     * @param level 世界读取器
     * @param pos   目标位置
     * @return 可蔓延返回 true
     */
    private static boolean canPropagate(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos above = pos.above();
        return canBeGrass(state, level, pos) && !level.getFluidState(above).is(FluidTags.WATER);
    }

    /**
     * 随机刻逻辑：光照不足/被水覆盖时退化为冷域泥土，光照充足时向四周蔓延。
     * <p>
     * 完全覆写原版逻辑以替换目标方块（原版写死 {@code minecraft:dirt}，
     * 冷域版本应使用 {@code cold_domain_dirt}）。
     *
     * @param state  当前方块状态
     * @param level  服务端世界
     * @param pos    当前方块位置
     * @param random 随机源
     */
    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canBeGrass(state, level, pos)) {
            if (!level.isAreaLoaded(pos, 1)) {
                return; // 防止检查相邻区块光照时加载未就绪区块
            }
            // 退化为冷域泥土
            level.setBlockAndUpdate(pos, PDBlocksColdDomain.COLD_DOMAIN_DIRT.get().defaultBlockState());
        } else {
            if (!level.isAreaLoaded(pos, 3)) {
                return;
            }
            if (level.getMaxLocalRawBrightness(pos.above()) >= 9) {
                BlockState grassState = this.defaultBlockState();
                for (int i = 0; i < 4; i++) {
                    BlockPos target = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                    BlockState targetState = level.getBlockState(target);
                    // 目标为冷域泥土且上方环境允许长草 → 蔓延并同步雪覆盖状态
                    if (targetState.is(PDBlocksColdDomain.COLD_DOMAIN_DIRT.get())
                            && canPropagate(targetState, level, target)) {
                        level.setBlockAndUpdate(target, grassState.setValue(
                                SNOWY, level.getBlockState(target.above()).is(Blocks.SNOW)));
                    }
                }
            }
        }
    }
}
