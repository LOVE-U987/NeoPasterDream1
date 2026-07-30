package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.LoveUDollBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * LOVE_U拉乌酱玩偶方块 (Love U Doll Block)
 * <p>
 * 继承 {@link MemorialDollBlock}，绑定 {@link LoveUDollBlockEntity}。
 */
public class LoveUDollBlock extends MemorialDollBlock {

    /**
     * 方块 MapCodec
     */
    public static final MapCodec<LoveUDollBlock> CODEC = simpleCodec(LoveUDollBlock::new);

    /**
     * 构造LOVE_U拉乌酱玩偶方块
     *
     * @param properties 方块属性
     */
    public LoveUDollBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends MemorialDollBlock> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LoveUDollBlockEntity(pos, state);
    }
}
