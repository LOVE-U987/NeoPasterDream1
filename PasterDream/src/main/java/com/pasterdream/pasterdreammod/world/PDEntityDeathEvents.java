package com.pasterdream.pasterdreammod.world;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDAdvancements;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.damagesource.CombatEntry;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * 实体死亡挂钩：对齐原版 {@code EntityDeathPr0} + {@code SculkHeartPr0} + 自定义诙谐死亡提示。
 * <ul>
 *   <li>Warden → 附近玩家（需 achievement_start、未 hide_7）授 hide_7 + 文案/效果；无 silentsdelight 时掉 sculk_heart</li>
 *   <li>ElderGuardian → elder_guardian_scale</li>
 *   <li>模组怪物击杀玩家 → 随机选取一条诙谐自定义死亡消息，替换原版死亡提示</li>
 * </ul>
 */
public final class PDEntityDeathEvents {

    private PDEntityDeathEvents() {
    }

    /**
     * 怪物类型 → 死亡消息语言键列表（随机选取一条）的映射表。
     * 消息使用 {@code death.pasterdream.<entity_registry_name>.<0..n>} 格式，
     * 每条消息的 {@code %1$s} 即为玩家名。
     */
    private static final Map<EntityType<?>, List<String>> DEATH_MESSAGE_KEYS = Map.ofEntries(
            // ====== 灯影世界 ======
            Map.entry(EntityType.byString("pasterdream:shadow_golem").orElse(null), List.of(
                    "death.pasterdream.shadow_golem.0",
                    "death.pasterdream.shadow_golem.1")),
            Map.entry(EntityType.byString("pasterdream:shadow_ghost").orElse(null), List.of(
                    "death.pasterdream.shadow_ghost.0",
                    "death.pasterdream.shadow_ghost.1")),
            Map.entry(EntityType.byString("pasterdream:shadow_squeal_ghost").orElse(null), List.of(
                    "death.pasterdream.shadow_squeal_ghost.0",
                    "death.pasterdream.shadow_squeal_ghost.1")),
            Map.entry(EntityType.byString("pasterdream:shadow_squeal_ghost_0").orElse(null), List.of(
                    "death.pasterdream.shadow_squeal_ghost.0",
                    "death.pasterdream.shadow_squeal_ghost.1")),
            Map.entry(EntityType.byString("pasterdream:shadow_hand").orElse(null), List.of(
                    "death.pasterdream.shadow_hand.0",
                    "death.pasterdream.shadow_hand.1")),
            Map.entry(EntityType.byString("pasterdream:shadow_tune_totem").orElse(null), List.of(
                    "death.pasterdream.shadow_tune_totem.0",
                    "death.pasterdream.shadow_tune_totem.1")),
            // ====== 恐怖尖喙家族 ======
            Map.entry(EntityType.byString("pasterdream:terrorbeak").orElse(null), List.of(
                    "death.pasterdream.terrorbeak.0",
                    "death.pasterdream.terrorbeak.1")),
            Map.entry(EntityType.byString("pasterdream:crazy_terrorbeak").orElse(null), List.of(
                    "death.pasterdream.crazy_terrorbeak.0",
                    "death.pasterdream.crazy_terrorbeak.1")),
            Map.entry(EntityType.byString("pasterdream:weakeness_terrorbeak").orElse(null), List.of(
                    "death.pasterdream.weakeness_terrorbeak.0",
                    "death.pasterdream.weakeness_terrorbeak.1")),
            // ====== 骨翼 ======
            Map.entry(EntityType.byString("pasterdream:bone_wing").orElse(null), List.of(
                    "death.pasterdream.bone_wing.0",
                    "death.pasterdream.bone_wing.1")),
            Map.entry(EntityType.byString("pasterdream:ash_bone_wing").orElse(null), List.of(
                    "death.pasterdream.ash_bone_wing.0",
                    "death.pasterdream.ash_bone_wing.1")),
            // ====== 雷云家族 ======
            Map.entry(EntityType.byString("pasterdream:thundercloud").orElse(null), List.of(
                    "death.pasterdream.thundercloud.0",
                    "death.pasterdream.thundercloud.1")),
            Map.entry(EntityType.byString("pasterdream:highvoltage").orElse(null), List.of(
                    "death.pasterdream.highvoltage.0",
                    "death.pasterdream.highvoltage.1")),
            // ====== 风之旅途 ======
            Map.entry(EntityType.byString("pasterdream:wind_knight").orElse(null), List.of(
                    "death.pasterdream.wind_knight.0",
                    "death.pasterdream.wind_knight.1")),
            // ====== 黑甲虫家族 ======
            Map.entry(EntityType.byString("pasterdream:black_beetle").orElse(null), List.of(
                    "death.pasterdream.black_beetle.0",
                    "death.pasterdream.black_beetle.1")),
            Map.entry(EntityType.byString("pasterdream:black_beetle_mother").orElse(null), List.of(
                    "death.pasterdream.black_beetle_mother.0",
                    "death.pasterdream.black_beetle_mother.1")),
            // ====== 其他敌对 ======
            Map.entry(EntityType.byString("pasterdream:fox_fire").orElse(null), List.of(
                    "death.pasterdream.fox_fire.0",
                    "death.pasterdream.fox_fire.1")),
            Map.entry(EntityType.byString("pasterdream:shaking_crystal").orElse(null), List.of(
                    "death.pasterdream.shaking_crystal.0",
                    "death.pasterdream.shaking_crystal.1")),
            Map.entry(EntityType.byString("pasterdream:small_stone_spirit").orElse(null), List.of(
                    "death.pasterdream.small_stone_spirit.0",
                    "death.pasterdream.small_stone_spirit.1")),
            Map.entry(EntityType.byString("pasterdream:meltdream_crystal").orElse(null), List.of(
                    "death.pasterdream.meltdream_crystal.0",
                    "death.pasterdream.meltdream_crystal.1")),
            Map.entry(EntityType.byString("pasterdream:spore_entity").orElse(null), List.of(
                    "death.pasterdream.spore_entity.0",
                    "death.pasterdream.spore_entity.1")),
            // ====== BOSS ======
            Map.entry(EntityType.byString("pasterdream:aaroncos_lefthand_0").orElse(null), List.of(
                    "death.pasterdream.aaroncos.0",
                    "death.pasterdream.aaroncos.1")),
            Map.entry(EntityType.byString("pasterdream:aaroncos_righthand_0").orElse(null), List.of(
                    "death.pasterdream.aaroncos.0",
                    "death.pasterdream.aaroncos.1"))
    );

    /**
     * CombatTracker 内部的 entries 列表，用于注入自定义死亡消息。
     */
    private static final Field COMBAT_ENTRIES_FIELD;

    static {
        try {
            COMBAT_ENTRIES_FIELD = CombatTracker.class.getDeclaredField("entries");
            COMBAT_ENTRIES_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("无法找到 CombatTracker.entries 字段", e);
        }
    }

    /**
     * 包装 DamageSource，使其返回指定的死亡消息文本。
     * 用于覆盖原版“玩家被 X 杀死了”的提示。
     */
    private static class CustomMessageDamageSource extends DamageSource {
        private final Component message;

        /**
         * @param original 原始伤害源，保留其 DamageType
         * @param message  要显示的自定义死亡消息
         */
        public CustomMessageDamageSource(DamageSource original, Component message) {
            super(original.typeHolder());
            this.message = message;
        }

        @Override
        public Component getLocalizedDeathMessage(LivingEntity entity) {
            return this.message;
        }
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

        // —— 原版生物特殊掉落 ——
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

        // —— 玩家被模组怪物击杀 → 用自定义消息替换原版死亡消息 ——
        if (entity instanceof ServerPlayer serverPlayer) {
            Entity killer = event.getSource().getEntity();
            if (killer != null) {
                List<String> keys = DEATH_MESSAGE_KEYS.get(killer.getType());
                if (keys != null && !keys.isEmpty()) {
                    int idx = serverPlayer.getRandom().nextInt(keys.size());
                    Component msg = Component.translatable(keys.get(idx), serverPlayer.getDisplayName());
                    overrideDeathMessage(serverPlayer, event.getSource(), msg);
                }
            }
        }
    }

    /**
     * 将玩家的 CombatTracker 替换为只包含一条自定义消息的记录，
     * 从而让原版死亡广播直接显示自定义文本。
     *
     * @param player   死亡玩家
     * @param original 原始伤害源，用于保留 DamageType
     * @param message  要显示的死亡消息
     */
    @SuppressWarnings("unchecked")
    private static void overrideDeathMessage(ServerPlayer player, DamageSource original, Component message) {
        CombatTracker tracker = player.getCombatTracker();
        try {
            List<CombatEntry> entries = (List<CombatEntry>) COMBAT_ENTRIES_FIELD.get(tracker);
            entries.clear();
            entries.add(new CombatEntry(
                    new CustomMessageDamageSource(original, message),
                    0.0F, null, 0.0F));
        } catch (IllegalAccessException e) {
            PDDebugLogger.mainDebug("[EntityDeath] 无法替换死亡消息: {}", e.getMessage());
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
            sp.displayClientMessage(Component.translatable(
                    "message.pasterdream.entity_death.blur.1"), false);
            sp.displayClientMessage(Component.translatable(
                    "message.pasterdream.entity_death.blur.2"), false);
            sp.displayClientMessage(Component.translatable(
                    "message.pasterdream.entity_death.blur.3"), false);
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
        if (!PDAdvancements.isAdvancementLocked(player, ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path))) {
            return true;
        }
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
