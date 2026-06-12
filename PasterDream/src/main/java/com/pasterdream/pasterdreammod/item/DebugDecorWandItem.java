package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class DebugDecorWandItem extends Item {
    private final String featureName;

    public DebugDecorWandItem(Properties properties, String featureName) {
        super(properties);
        this.featureName = featureName;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        ServerLevel serverLevel = (ServerLevel) level;

        BlockHitResult blockHitResult;
        HitResult hitResult = player.pick(200.0D, 0.0F, false);
        if (hitResult.getType() == HitResult.Type.MISS) {
            player.sendSystemMessage(Component.literal("§c没有瞄准任何方块！"));
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        if (hitResult instanceof BlockHitResult) {
            blockHitResult = (BlockHitResult) hitResult;
        } else {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        BlockPos targetPos = blockHitResult.getBlockPos().relative(blockHitResult.getDirection());

        ResourceKey<ConfiguredFeature<?, ?>> featureKey = ResourceKey.create(
                Registries.CONFIGURED_FEATURE,
                ResourceLocation.parse(PasterDreamMod.MOD_ID + ":" + featureName)
        );

        var configuredFeature = serverLevel.registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE)
                .getHolder(featureKey)
                .orElse(null);

        if (configuredFeature == null) {
            player.sendSystemMessage(Component.literal("§c未找到特征: " + featureName));
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        boolean success = configuredFeature.value().place(
                serverLevel,
                serverLevel.getChunkSource().getGenerator(),
                serverLevel.getRandom(),
                targetPos
        );

        if (success) {
            player.sendSystemMessage(Component.literal("§a已放置: §f" + featureName + " §a于 " + targetPos.toShortString()));
        } else {
            player.sendSystemMessage(Component.literal("§e放置失败: §f" + featureName + " §e于 " + targetPos.toShortString()));
        }

        player.getCooldowns().addCooldown(this, 5);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
