package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.api.doll.DollAPI;
import com.pasterdream.pasterdreammod.api.doll.DollConfig;
import com.pasterdream.pasterdreammod.block.entity.DollBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 通用玩偶方块
 * <p>
 * 所有通过 {@link DollAPI} 注册的玩偶共用此类。根据配置决定是否启用抱物逻辑，
 * 并根据方块实例查找对应的方块实体类型。
 */
public class DollBlock extends MemorialDollBlock {

    /**
     * 方块 MapCodec
     */
    public static final MapCodec<DollBlock> CODEC = simpleCodec(DollBlock::new);

    /**
     * 构造通用玩偶方块
     *
     * @param properties 方块属性
     */
    public DollBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends MemorialDollBlock> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        Optional<DeferredHolder<BlockEntityType<?>, BlockEntityType<DollBlockEntity>>> holder = DollAPI.getBlockEntityHolder(this);
        if (holder.isEmpty()) {
            return null;
        }
        return new DollBlockEntity(pos, state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        DollConfig config = DollAPI.getConfig(this).orElse(null);
        if (config == null || !config.canHoldItems()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
