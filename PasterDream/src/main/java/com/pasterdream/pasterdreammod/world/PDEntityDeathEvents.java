package com.pasterdream.pasterdreammod.world;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 实体死亡挂钩：对齐原版 {@code EntityDeathPr0} + {@code SculkHeartPr0}。
 * <ul>
 *   <li>Warden → 附近玩家（需 achievement_start、未 hide_7）授 hide_7 + 文案/效果；无 silentsdelight 时掉 sculk_heart</li>
 *   <li>ElderGuardian → elder_guardian_scale</li>
 * </ul>
 */
public final class PDEntityDeathEvents {

    private PDEntityDeathEvents() {
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        if (level.isClientSide()) {
            return;
        }
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        if (entity instanceof Warden) {
            grantSculkHeartMemory(level, x, y, z);
            if (!ModList.get().isLoaded("silentsdelight") && level instanceof ServerLevel server) {
                ItemEntity drop = new ItemEntity(server, x, y, z, new ItemStack(PDItems.SCULK_HEART.get()));
                drop.setPickUpDelay(10);
                server.addFreshEntity(drop);
            }
        }
        if (entity instanceof ElderGuardian && level instanceof ServerLevel server) {
            ItemEntity drop = new ItemEntity(server, x, y, z,
                    new ItemStack(PDItems.ELDER_GUARDIAN_SCALE.get()));
            drop.setPickUpDelay(10);
            server.addFreshEntity(drop);
        }
    }

    /**
     * 原 SculkHeartPr0：32 格 AABB 内玩家，已有 start 且未 hide_7。
     */
    private static void grantSculkHeartMemory(Level level, double x, double y, double z) {
        AABB box = new AABB(x, y, z, x, y, z).inflate(16.0);
        for (Player player : level.getEntitiesOfClass(Player.class, box)) {
            if (!(player instanceof ServerPlayer sp)) {
                continue;
            }
            if (!isDone(sp, "achievement_start") || isDone(sp, "achievement_hide_7")) {
                continue;
            }
            if (!awardAll(sp, "achievement_hide_7")) {
                continue;
            }
            sp.displayClientMessage(Component.literal(
                    "§5你眼前的事物突然模糊了起来，黑暗的迷雾慢慢的覆盖了你的瞳孔。"), false);
            sp.displayClientMessage(Component.literal(
                    "§5脑海里闪过一些不属于自己的记忆，随之感受到了强烈的疲惫感。"), false);
            sp.displayClientMessage(Component.literal(
                    "§5你回想到了曾经也有过类似的感觉，也许应该躺到床上休息一下..."), false);
            sp.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 140, 0, false, false));
            ServerScheduler.schedule(20, () -> {
                if (!sp.isAlive() || sp.level().isClientSide()) {
                    return;
                }
                sp.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0, false, false));
                sp.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false));
                sp.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false));
            });
            PDDebugLogger.mainDebug("[EntityDeath] granted hide_7 to {} near {}",
                    sp.getGameProfile().getName(), BlockPos.containing(x, y, z).toShortString());
        }
    }

    private static boolean isDone(ServerPlayer player, String path) {
        AdvancementHolder holder = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path));
        return holder != null && player.getAdvancements().getOrStartProgress(holder).isDone();
    }

    private static boolean awardAll(ServerPlayer player, String path) {
        AdvancementHolder holder = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path));
        if (holder == null) {
            return false;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        if (progress.isDone()) {
            return false;
        }
        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(holder, criterion);
        }
        return progress.isDone();
    }
}
