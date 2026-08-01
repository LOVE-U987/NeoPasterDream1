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
            player.sendSystemMessage(Component.translatable("message.pasterdream.debug_decor_wand.no_target"));
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
            player.sendSystemMessage(Component.translatable("message.pasterdream.debug_decor_wand.feature_not_found", featureName));
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        boolean success = configuredFeature.value().place(
                serverLevel,
                serverLevel.getChunkSource().getGenerator(),
                serverLevel.getRandom(),
                targetPos
        );

        if (success) {
            player.sendSystemMessage(Component.translatable("message.pasterdream.debug_decor_wand.placed", featureName, targetPos.toShortString()));
        } else {
            player.sendSystemMessage(Component.translatable("message.pasterdream.debug_decor_wand.place_failed", featureName, targetPos.toShortString()));
        }

        player.getCooldowns().addCooldown(this, 5);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
