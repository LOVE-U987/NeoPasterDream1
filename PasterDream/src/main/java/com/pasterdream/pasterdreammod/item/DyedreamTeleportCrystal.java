package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.worldgen.PDTeleportLanding;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

            if (level.dimension().equals(PDDimensions.DYEDREAM_WORLD_LEVEL_KEY)) {
                // 在染梦维度 → 回主世界（重生点/出生点安全降落）
                targetDimension = Level.OVERWORLD;
            } else {
                // 主世界/其他维度 → 去染梦维度（出生在 (0,0) 原点裂隙旁）
                targetDimension = PDDimensions.DYEDREAM_WORLD_LEVEL_KEY;
            }

            ServerLevel targetWorld = serverPlayer.getServer().getLevel(targetDimension);
            if (targetWorld != null) {
                // 查找安全传送位置（与裂隙行为一致）
                BlockPos safePos;
                if (targetDimension.equals(PDDimensions.DYEDREAM_WORLD_LEVEL_KEY)) {
                    safePos = PDTeleportLanding.findDyedreamOriginArrival(targetWorld);
                } else {
                    safePos = PDTeleportLanding.findSafeRespawnLanding(targetWorld, serverPlayer);
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
}