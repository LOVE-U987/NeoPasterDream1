package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;

/**
 * 染梦传送水晶 —— 在任意维度使用即可往返染梦世界
 * <p>
 * 主世界使用 → 传送到染梦维度世界出生点
 * 染梦维度使用 → 传送回主世界世界出生点
 * 使用后产生 20 秒（400 tick）冷却时间
 */
public class DyedreamTeleportCrystal extends Item {

    private static final int COOLDOWN_TICKS = 400;

    public DyedreamTeleportCrystal(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            ResourceKey<Level> targetDimension;
            BlockPos targetPos;

            if (level.dimension().equals(PDDimensions.DYEDREAM_WORLD_LEVEL_KEY)) {
                // 在染梦维度 → 回主世界
                targetDimension = Level.OVERWORLD;
                targetPos = level.getSharedSpawnPos();
            } else {
                // 主世界/其他维度 → 去染梦维度
                targetDimension = PDDimensions.DYEDREAM_WORLD_LEVEL_KEY;
                targetPos = level.getSharedSpawnPos();
            }

            ServerLevel targetWorld = serverPlayer.getServer().getLevel(targetDimension);
            if (targetWorld != null) {
                // 查找安全传送位置
                BlockPos safePos = findSafePosition(targetWorld, targetPos);
                if (safePos == null) {
                    safePos = targetWorld.getSharedSpawnPos();
                }

                DimensionTransition transition = new DimensionTransition(
                        targetWorld,
                        safePos.getCenter(),
                        player.getDeltaMovement(),
                        player.getYRot(),
                        player.getXRot(),
                        DimensionTransition.PLAY_PORTAL_SOUND
                );
                serverPlayer.changeDimension(transition);

                // 消耗物品（生存模式）
                if (!player.isCreative()) {
                    stack.shrink(1);
                }

                // 设置冷却时间
                serverPlayer.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    /**
     * 在目标维度查找安全的地面传送位置 —— 先扫描出目标 XZ 的最高地表方块，
     * 然后在其上空 2 格处传送，避免穿地
     *
     * @param world 目标世界
     * @param startPos 起始搜索位置（取 XZ 坐标）
     * @return 安全位置，如果找不到则返回目标维度出生点上空
     */
    private BlockPos findSafePosition(ServerLevel world, BlockPos startPos) {
        BlockPos.MutableBlockPos checkPos = startPos.atY(world.getMaxBuildHeight() - 1).mutable();

        // 从最高处向下扫描，找到第一个非空气方块（即地面），然后在其上空 2 格处传送
        for (int y = world.getMaxBuildHeight() - 1; y > world.getMinBuildHeight(); y--) {
            checkPos.setY(y);
            BlockState blockAt = world.getBlockState(checkPos);
            if (!blockAt.isAir()) {
                return checkPos.above(2).immutable();
            }
        }

        // 兜底：使用世界出生点，并尝试上空 3 格
        BlockPos spawnPos = world.getSharedSpawnPos();
        BlockPos safePos = findFirstAirFromTop(world, spawnPos);
        return safePos != null ? safePos : spawnPos.above(3);
    }

    /**
     * 从最高处向下扫描，找到第一个空气方块位置
     */
    private BlockPos findFirstAirFromTop(ServerLevel world, BlockPos startPos) {
        BlockPos.MutableBlockPos checkPos = startPos.atY(world.getMaxBuildHeight() - 1).mutable();
        for (int y = world.getMaxBuildHeight() - 1; y > world.getMinBuildHeight(); y--) {
            checkPos.setY(y);
            if (world.getBlockState(checkPos).isAir()) {
                return checkPos.above(2).immutable();
            }
        }
        return null;
    }
}