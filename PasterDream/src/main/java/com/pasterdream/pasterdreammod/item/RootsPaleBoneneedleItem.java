package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 溯源苍白骨针（roots_pale_boneneedle）。
 * <p>
 * 还原自原版 RootsPaleBoneneedleItem + RootsPaleBoneneedlePr0/Pr1Procedure：
 * <ul>
 *   <li>潜行右击方块：记录该坐标为标记点（存入物品自定义数据），冷却 1 秒；</li>
 *   <li>右键使用（已设标记）：受 1 点伤害，若身处帕斯特之梦维度
 *       （染梦世界 / 影灯世界 / 亚伦柯斯竞技场），1 秒后传送回主世界的标记坐标，冷却 5 秒；</li>
 *   <li>右键使用（未设标记）：行为同苍白骨针 —— 受 1 点伤害并返回主世界重生点
 *       （无重生点则回世界出生点），同时授予成就 achievement_b_2。</li>
 * </ul>
 * 与消耗型的苍白骨针不同，本物品可无限重复使用（不消耗）。
 */
public class RootsPaleBoneneedleItem extends Item {

    /** 传送音效 pasterdream:dream0（音效键由并行任务统一并入 sounds.json，ogg 已随本任务复制） */
    private static final SoundEvent DREAM0_SOUND =
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("pasterdream", "dream0"));

    /** 自定义数据键：是否已设置标记（与原版 NBT 键一致） */
    private static final String TAG_SWITCH = "switch";

    public RootsPaleBoneneedleItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.pasterdream.roots_pale_boneneedle.desc"));
        tooltip.add(Component.translatable("tooltip.pasterdream.roots_pale_boneneedle.set_mark"));
        tooltip.add(Component.translatable("tooltip.pasterdream.roots_pale_boneneedle.default_respawn"));
    }

    /** 潜行右击方块：记录标记坐标 */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (player == null || !player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        BlockPos pos = context.getClickedPos();
        if (level instanceof ServerLevel serverLevel) {
            ItemStack stack = context.getItemInHand();
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
                tag.putBoolean(TAG_SWITCH, true);
                tag.putDouble("x", pos.getX());
                tag.putDouble("y", pos.getY());
                tag.putDouble("z", pos.getZ());
            });
            serverLevel.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                    pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 64, 1, 1, 1, 0.15);
            player.displayClientMessage(Component.translatable("tooltip.pasterdream.roots_pale_boneneedle.mark_set"), true);
            serverLevel.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1.0f, 1.0f);
            player.getCooldowns().addCooldown(this, 20);
        }
        return InteractionResult.SUCCESS;
    }

    /** 右键使用：从帕斯特之梦维度返回主世界（标记点或重生点） */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.success(stack);
        }
        CompoundTag data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        boolean marked = data.getBoolean(TAG_SWITCH);

        // 与原版一致：使用即受 1 点伤害并挥手
        player.swing(InteractionHand.MAIN_HAND, true);
        player.hurt(serverLevel.damageSources().generic(), 1);

        // 仅在帕斯特之梦三维度内可触发返回主世界
        if (!isPasterDreamDimension(serverLevel)) {
            return InteractionResultHolder.success(stack);
        }

        serverLevel.sendParticles((SimpleParticleType) PDParticles.DUST_0_PARTICLE.particleType(),
                player.getX(), player.getY(), player.getZ(), 64, 0.1, 1, 0.1, 0.2);
        serverLevel.playSound(null, BlockPos.containing(player.getX(), player.getY(), player.getZ()),
                DREAM0_SOUND, SoundSource.NEUTRAL, 0.5f, 1.0f);

        // 延迟 1 秒（20 tick）后执行传送（等价原版 queueServerWork(20)）
        double markX = data.getDouble("x");
        double markY = data.getDouble("y");
        double markZ = data.getDouble("z");
        ServerScheduler.schedule(20, () -> {
            if (!serverPlayer.isAlive()) {
                return;
            }
            ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
            if (overworld == null) {
                return;
            }
            if (!marked) {
                // 未设标记：授予成就并返回重生点（原版 PaleBoneneedlePr0 分支）
                grantAdvancement(serverPlayer, "achievement_b_2");
            }
            Vec3 target = marked
                    ? new Vec3(markX, markY, markZ)
                    : respawnTarget(overworld, serverPlayer);
            DimensionTransition transition = new DimensionTransition(
                    overworld, target, serverPlayer.getDeltaMovement(),
                    serverPlayer.getYRot(), serverPlayer.getXRot(),
                    DimensionTransition.PLAY_PORTAL_SOUND);
            serverPlayer.changeDimension(transition);
            serverPlayer.getCooldowns().addCooldown(this, 100);
        });
        return InteractionResultHolder.success(stack);
    }

    /** 判定是否处于帕斯特之梦三维度（染梦 / 影灯 / 亚伦柯斯竞技场，与原版判定一致） */
    private static boolean isPasterDreamDimension(ServerLevel level) {
        return level.dimension().equals(PDDimensions.DYEDREAM_WORLD_LEVEL_KEY)
                || level.dimension().equals(PDDimensions.LAMP_SHADOW_WORLD_LEVEL_KEY)
                || level.dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
    }

    /** 计算主世界返回坐标：优先玩家重生点，否则世界出生点（与原版 fallback 一致） */
    private static Vec3 respawnTarget(ServerLevel overworld, ServerPlayer player) {
        if (player.getRespawnPosition() != null && overworld.dimension().equals(player.getRespawnDimension())) {
            return Vec3.atBottomCenterOf(player.getRespawnPosition());
        }
        return Vec3.atBottomCenterOf(overworld.getSharedSpawnPos());
    }

    /** 授予指定 pasterdream 命名空间成就（成就不存在时静默跳过） */
    private static void grantAdvancement(ServerPlayer player, String path) {
        AdvancementHolder advancement = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath("pasterdream", path));
        if (advancement == null) {
            return;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (!progress.isDone()) {
            for (String criterion : progress.getRemainingCriteria()) {
                player.getAdvancements().award(advancement, criterion);
            }
        }
    }
}
