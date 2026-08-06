package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * 完整的暗影地牢传送门核心。
 * <p>
 * 由 {@link BrokenShadowDungeonProtalBlock} 修复后替换而来，固定使用
 * {@code shadow_dungeon_portal} 模型资源。
 * <p>
 * 携带地牢冷却计时：进入地牢后 {@code cd=true}，每 20 tick 递增 {@code time}，
 * 达 1800 tick（90 秒）后冷却结束；期间右键提示剩余时间（由
 * {@link BrokenShadowDungeonProtalBlock#handleFixedInteraction} 读取）。
 */
public class ShadowDungeonPortalBlock extends BaseEntityBlock {

    public static final MapCodec<ShadowDungeonPortalBlock> CODEC = simpleCodec(ShadowDungeonPortalBlock::new);

    public ShadowDungeonPortalBlock(Properties properties) {
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

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return box(3, 3, 3, 13, 13, 13);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new W4GeoDataBlockEntity(PDBlockEntitiesFurniture.SHADOW_DUNGEON_PORTAL.get(), pos, state);
    }

    // ==================== tick 冷却（原 ShadowDungeonPortalBlock 逻辑） ====================

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        level.scheduleTick(pos, this, 20);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (W4DataBlockEntity.getBooleanAt(level, pos, "cd")) {
            double time = W4DataBlockEntity.getDoubleAt(level, pos, "time") + 1;
            W4DataBlockEntity.putDoubleAt(level, pos, "time", time);
            if (time >= 1800) {
                W4DataBlockEntity.putBooleanAt(level, pos, "cd", false);
                W4DataBlockEntity.putDoubleAt(level, pos, "time", 0);
            }
        }
        level.scheduleTick(pos, this, 20);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        // 复用 BrokenShadowDungeonProtalBlock 的已修复交互逻辑
        return BrokenShadowDungeonProtalBlock.handleFixedInteraction(level, pos, player);
    }
}
