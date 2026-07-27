package com.pasterdream.pasterdreammod.worldgen.tree.decorator;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import com.pasterdream.pasterdreammod.worldgen.tree.DyedreamTreePlacers;

import java.util.function.BiConsumer;

/**
 * 染梦根系装饰器
 * <p>
 * 在树干底部向四周地面延伸染梦木（dyedream_wood）方块，增强树木与地形的融合。
 */
public class DyedreamRootDecorator extends TreeDecorator {

    public static final DyedreamRootDecorator INSTANCE = new DyedreamRootDecorator();
    public static final MapCodec<DyedreamRootDecorator> CODEC = MapCodec.unit(() -> INSTANCE);

    private DyedreamRootDecorator() {}

    @Override
    protected TreeDecoratorType<?> type() {
        return DyedreamTreePlacers.ROOT_DECORATOR.get();
    }

    @Override
    public void place(Context context) {
        LevelSimulatedReader level = context.level();
        RandomSource random = context.random();
        BiConsumer<BlockPos, BlockState> setter = context::setBlock;

        BlockPos base = context.logs().get(context.logs().size() / 2);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (random.nextFloat() < 0.6f) {
                int length = 1 + random.nextInt(3);
                mutable.set(base);
                for (int i = 0; i < length; i++) {
                    mutable.move(dir);
                    if (i > 0) mutable.move(Direction.DOWN);
                    BlockPos ground = findGround(level, mutable);
                    if (ground != null && level.isStateAtPosition(ground.above(), BlockState::isAir)) {
                        setter.accept(ground, com.pasterdream.pasterdreammod.registry.PDBlocks.DYEDREAM_WOOD.get().defaultBlockState()
                                .setValue(RotatedPillarBlock.AXIS, dir.getAxis()));
                    }
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
