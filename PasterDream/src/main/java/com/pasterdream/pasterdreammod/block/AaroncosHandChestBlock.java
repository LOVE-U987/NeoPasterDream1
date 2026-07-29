package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.AaroncosHandChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * 亚伦柯斯之触战利品箱方块
 * <p>
 * GeckoLib 实体方块；右键开启掉落（原版 {@code AaroncosHandChestPr0}），
 * 非容器 GUI。
 */
public class AaroncosHandChestBlock extends BaseEntityBlock {

    public static final MapCodec<AaroncosHandChestBlock> CODEC = simpleCodec(AaroncosHandChestBlock::new);

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

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                 Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof AaroncosHandChestBlockEntity chest && chest.tryOpen(player)) {
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
