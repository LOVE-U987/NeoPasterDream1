package com.pasterdream.pasterdreammod.worldgen.tree.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pasterdream.pasterdreammod.api.worldgen.WorldGenUtils;
import com.pasterdream.pasterdreammod.worldgen.tree.DyedreamTreePlacers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

import java.util.function.BiConsumer;

/**
 * 染梦落叶装饰器
 * <p>
 * 在树干周围地表放置落叶层方块，密度可配置。
 */
public class DyedreamFallenLeavesDecorator extends TreeDecorator {

    public static final MapCodec<DyedreamFallenLeavesDecorator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("probability").forGetter(d -> d.probability),
                    Codec.INT.fieldOf("radius").forGetter(d -> d.radius)
            ).apply(instance, DyedreamFallenLeavesDecorator::new));

    private final float probability;
    private final int radius;

    /**
     * 构造落叶装饰器
     *
     * @param probability 每个位置生成落叶的概率
     * @param radius      落叶覆盖半径
     */
    public DyedreamFallenLeavesDecorator(float probability, int radius) {
        this.probability = probability;
        this.radius = radius;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return DyedreamTreePlacers.FALLEN_LEAVES_DECORATOR.get();
    }

    @Override
    public void place(Context context) {
        LevelSimulatedReader level = context.level();
        RandomSource random = context.random();
        // 包装 setter：越出当前世界生成可写范围的方块位置直接跳过
        //（落叶层半径最大可达 8 格，在区块边缘生成时会跨越到尚未就绪的相邻区块，
        //  触发 "Detected setBlock in a far chunk" 错误刷屏，此处提前过滤）
        BiConsumer<BlockPos, BlockState> setter = (pos, state) -> {
            if (WorldGenUtils.canPlaceInRegion(context.level(), pos)) {
                context.setBlock(pos, state);
            }
        };

        BlockPos base = context.logs().get(context.logs().size() / 2);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) continue;
                if (random.nextFloat() > probability) continue;

                mutable.set(base.getX() + x, base.getY(), base.getZ() + z);
                BlockPos ground = findGround(level, mutable);
                if (ground != null && level.isStateAtPosition(ground.above(), BlockState::isAir)) {
                    setter.accept(ground.above(), com.pasterdream.pasterdreammod.registry.PDBlocks.DYEDREAM_FALLEN_LEAVES.get().defaultBlockState());
                }
            }
        }
    }

    /**
     * 从当前位置向下寻找固体地面
     *
     * @param level 模拟世界读取器
     * @param pos   起始位置（可变）
     * @return 找到的非空气位置，若未找到则返回 null
     */
    private BlockPos findGround(LevelSimulatedReader level, BlockPos.MutableBlockPos pos) {
        for (int i = 0; i < 4; i++) {
            if (!level.isStateAtPosition(pos, BlockState::isAir)) {
                return pos.immutable();
            }
            pos.move(Direction.DOWN);
        }
        return null;
    }
}
