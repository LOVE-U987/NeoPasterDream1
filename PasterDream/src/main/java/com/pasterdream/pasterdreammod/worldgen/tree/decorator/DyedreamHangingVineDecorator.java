package com.pasterdream.pasterdreammod.worldgen.tree.decorator;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import com.pasterdream.pasterdreammod.api.worldgen.WorldGenUtils;
import com.pasterdream.pasterdreammod.worldgen.tree.DyedreamTreePlacers;

import java.util.function.BiConsumer;

/**
 * 染梦垂藤装饰器
 * <p>
 * 从树冠边缘向下延伸垂挂藤蔓，适合垂泪树与发光树。
 */
public class DyedreamHangingVineDecorator extends TreeDecorator {

    public static final DyedreamHangingVineDecorator INSTANCE = new DyedreamHangingVineDecorator();
    public static final MapCodec<DyedreamHangingVineDecorator> CODEC = MapCodec.unit(() -> INSTANCE);

    private DyedreamHangingVineDecorator() {}

    @Override
    protected TreeDecoratorType<?> type() {
        return DyedreamTreePlacers.HANGING_VINE_DECORATOR.get();
    }

    @Override
    public void place(Context context) {
        LevelSimulatedReader level = context.level();
        RandomSource random = context.random();
        // 包装 setter：越出当前世界生成可写范围的方块位置直接跳过
        //（垂藤在区块边缘生成时会跨越到尚未就绪的相邻区块，触发
        //  "Detected setBlock in a far chunk" 错误刷屏，此处提前过滤）
        BiConsumer<BlockPos, BlockState> setter = (pos, state) -> {
            if (WorldGenUtils.canPlaceInRegion(context.level(), pos)) {
                context.setBlock(pos, state);
            }
        };

        for (BlockPos leafPos : context.leaves()) {
            if (random.nextFloat() < 0.08f) {
                BlockPos below = leafPos.below();
                if (level.isStateAtPosition(below, BlockState::isAir)) {
                    int length = 1 + random.nextInt(4);
                    for (int i = 0; i < length; i++) {
                        BlockPos vinePos = below.below(i);
                        if (level.isStateAtPosition(vinePos, BlockState::isAir)) {
                            BlockState state = com.pasterdream.pasterdreammod.registry.PDBlocks.DYEDREAM_HANGING_VINE.get().defaultBlockState()
                                    .setValue(VineBlock.UP, i == 0);
                            setter.accept(vinePos, state);
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }
}
