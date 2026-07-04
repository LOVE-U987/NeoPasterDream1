package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.AaroncosHandChestBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 亚伦柯斯之触战利品箱方块
 * <p>
 * 使用 GeckoLib 渲染 3D 模型和动画的装饰方块，
 * 对应方块实体 {@link AaroncosHandChestBlockEntity}。
 */
public class AaroncosHandChestBlock extends BaseEntityBlock {

    public static final MapCodec<AaroncosHandChestBlock> CODEC = simpleCodec(AaroncosHandChestBlock::new);

    /**
     * 构造亚伦柯斯之触战利品箱方块
     *
     * @param properties 方块属性
     */
    public AaroncosHandChestBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AaroncosHandChestBlockEntity(pos, state);
    }
}
