package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksMisc;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * 干裂粘土层·湿润（claypan_1）
 * <p>
 * 忠实还原原版 {@code Claypan1Block + Claypan1Pr0Procedure}：
 * 200 tick 周期计时，露天时 BE 数据 time 累加（白天且不下雨时额外 +1），
 * time ≥ 240 后转化为干透的 claypan_2。
 * 形状 (0,0,0,16,3,16)，0.5 强度需正确工具。
 */
public class Claypan1Block extends Block implements EntityBlock {

    /**
     * 构造干裂粘土层方块
     *
     * @param properties 方块属性
     */
    public Claypan1Block(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return box(0, 0, 0, 16, 3, 16);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!drops.isEmpty()) {
            return drops;
        }
        return Collections.singletonList(new ItemStack(this));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        level.scheduleTick(pos, this, 200);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        // 原 Claypan1Pr0Procedure
        if (level.canSeeSkyFromBelowWater(pos)) {
            if (W4DataBlockEntity.getDoubleAt(level, pos, "time") >= 240) {
                level.setBlock(pos, PDBlocksMisc.CLAYPAN_2.get().defaultBlockState(), 3);
                return;
            }
            W4DataBlockEntity.putDoubleAt(level, pos, "time",
                    W4DataBlockEntity.getDoubleAt(level, pos, "time") + 1);
            if (level.isDay() && !level.getLevelData().isRaining()) {
                W4DataBlockEntity.putDoubleAt(level, pos, "time",
                        W4DataBlockEntity.getDoubleAt(level, pos, "time") + 1);
            }
        }
        level.scheduleTick(pos, this, 200);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new W4DataBlockEntity(PDBlockEntitiesFurniture.CLAYPAN_1.get(), pos, state);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int eventId, int eventParam) {
        super.triggerEvent(state, level, pos, eventId, eventParam);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventId, eventParam);
    }
}
