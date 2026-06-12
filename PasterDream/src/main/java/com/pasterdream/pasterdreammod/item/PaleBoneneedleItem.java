package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;

/**
 * 苍白骨针 (Pale Bone Needle)
 * <p>
 * 右键使用时，若玩家处于染梦维度，则消耗 1 点生命值将玩家传送回主世界。
 * 一次性消耗品，使用后减少 1 个物品数量。
 * 功能与 {@code DyedreamCrackBlock} 类似，但为单向传送且需付出生命代价。
 */
public class PaleBoneneedleItem extends Item {

    /** 传送冷却时间（tick） */
    private static final int TELEPORT_COOLDOWN = 60;

    /**
     * 构造苍白骨针物品
     *
     * @param properties 物品属性
     */
    public PaleBoneneedleItem(Properties properties) {
        super(properties);
    }

    /**
     * 右键使用苍白骨针：染梦维度 → 主世界单向传送
     * <p>
     * 仅在染梦维度生效，消耗 1 点生命值并消耗 1 个物品，
     * 将玩家传送至主世界的重生点或世界出生点。
     *
     * @param level   当前世界
     * @param player  使用物品的玩家
     * @param usedHand 使用的手
     * @return 交互结果
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);

        // 仅在服务端执行
        if (level.isClientSide()) {
            return InteractionResultHolder.success(stack);
        }

        // 仅在染梦维度生效
        if (!level.dimension().equals(PDDimensions.DYEDREAM_WORLD_LEVEL_KEY)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.fail(stack);
        }

        // 检查传送冷却
        if (serverPlayer.getPortalCooldown() > 0) {
            return InteractionResultHolder.fail(stack);
        }

        // 扣 1 点血量（0.5 颗心），使用魔法伤害类型
        serverPlayer.hurt(serverPlayer.damageSources().magic(), 1.0f);

        // 传送至主世界
        ServerLevel overworld = serverPlayer.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return InteractionResultHolder.fail(stack);
        }

        BlockPos targetPos = findSafePosition(overworld, serverPlayer);

        DimensionTransition transition = new DimensionTransition(
                overworld,
                targetPos.getCenter(),
                serverPlayer.getDeltaMovement(),
                serverPlayer.getYRot(),
                serverPlayer.getXRot(),
                DimensionTransition.PLAY_PORTAL_SOUND
        );

        serverPlayer.changeDimension(transition);
        serverPlayer.setPortalCooldown(TELEPORT_COOLDOWN);

        // 播放使用音效
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BONE_MEAL_USE, SoundSource.PLAYERS, 1.0f, 1.0f);

        // 消耗物品（非创造模式）
        if (!serverPlayer.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return InteractionResultHolder.consume(stack);
    }

    /**
     * 在主世界查找安全的传送位置
     * <p>
     * 优先使用玩家重生点（床），若玩家未设置重生点或重生点不在主世界，
     * 则回退到世界出生点，并从高处向下扫描找到安全地面。
     *
     * @param world  主世界
     * @param player 传送的玩家
     * @return 安全的传送位置
     */
    private static BlockPos findSafePosition(ServerLevel world, ServerPlayer player) {
        BlockPos spawnPos;

        // 优先使用玩家重生点
        if (player.getRespawnPosition() != null && player.getRespawnDimension().equals(world.dimension())) {
            spawnPos = player.getRespawnPosition();
        } else {
            spawnPos = world.getSharedSpawnPos();
        }

        BlockPos.MutableBlockPos checkPos = spawnPos.atY(world.getMaxBuildHeight() - 1).mutable();

        // 从最高处向下扫描，找到第一个非空气方块，然后在其上空 2 格处传送
        for (int y = world.getMaxBuildHeight() - 1; y > world.getMinBuildHeight(); y--) {
            checkPos.setY(y);
            if (!world.getBlockState(checkPos).isAir()) {
                return checkPos.above(2).immutable();
            }
        }

        // 兜底：使用世界出生点上空 3 格
        return spawnPos.above(3);
    }
}
