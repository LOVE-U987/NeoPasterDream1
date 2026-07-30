package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.EoulDollBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * EOUL小幽灵玩偶方块 (Eoul Doll Block)
 * <p>
 * 继承 {@link MemorialDollBlock}，绑定 {@link EoulDollBlockEntity}。
 */
public class EoulDollBlock extends MemorialDollBlock {

    /**
     * 方块 MapCodec
     */
    public static final MapCodec<EoulDollBlock> CODEC = simpleCodec(EoulDollBlock::new);

    /**
     * 构造EOUL小幽灵玩偶方块
     *
     * @param properties 方块属性
     */
    public EoulDollBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends MemorialDollBlock> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EoulDollBlockEntity(pos, state);
    }
}
