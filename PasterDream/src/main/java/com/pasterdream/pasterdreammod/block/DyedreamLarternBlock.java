package com.pasterdream.pasterdreammod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

/**
 * 染梦水晶灯方块。
 * <p>发出 15 级光照的玻璃质感装饰方块，具有自发光、不遮挡视线、非红石导体等特性。</p>
 */
public class DyedreamLarternBlock extends Block {

    /**
     * 创建染梦水晶灯方块实例。
     *
     * @param properties 方块行为属性
     */
    public DyedreamLarternBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    /**
     * 获取染梦水晶灯的默认方块属性。
     * <p>与原模组保持一致：玻璃音效、硬度 0.3、15 级光照、需要镐类工具、无遮挡、自发光、非红石导体。</p>
     *
     * @return 配置好的方块属性
     */
    public static BlockBehaviour.Properties larternProps() {
        return BlockBehaviour.Properties.of()
                .instrument(NoteBlockInstrument.HAT)
                .sound(SoundType.GLASS)
                .strength(0.3F)
                .lightLevel(s -> 15)
                .requiresCorrectToolForDrops()
                .noOcclusion()
                .hasPostProcess((bs, br, bp) -> true)
                .emissiveRendering((bs, br, bp) -> true)
                .isRedstoneConductor((bs, br, bp) -> false);
    }

    /**
     * 获取该方块阻挡光照的程度。
     *
     * @param state 当前方块状态
     * @param worldIn 世界读取接口
     * @param pos 方块位置
     * @return 阻挡光照值，恒为 15（完全透光）
     */
    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 15;
    }

    /**
     * 获取方块的视觉碰撞箱。
     *
     * @param state 当前方块状态
     * @param world 世界读取接口
     * @param pos 方块位置
     * @param context 碰撞上下文
     * @return 空碰撞箱，不遮挡后方方块
     */
    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    /**
     * 获取方块破坏时的掉落物。
     * <p>优先使用战利品表；若战利品表未返回任何物品，则回退为掉落自身，避免 requiresCorrectToolForDrops 导致空手破坏时无任何掉落。</p>
     *
     * @param state 当前方块状态
     * @param builder 战利品参数构建器
     * @return 掉落物列表
     */
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!drops.isEmpty()) {
            return drops;
        }
        return List.of(new ItemStack(this));
    }
}
