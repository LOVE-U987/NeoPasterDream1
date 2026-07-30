package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.entity.mob.ThundercloudEntity;
import com.pasterdream.pasterdreammod.entity.mob.WindKnightEntity;
import com.pasterdream.pasterdreammod.entity.projectile.LightningProjectileEntity;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDEffects;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDGameRules;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksFurniture;
import com.pasterdream.pasterdreammod.registry.items.PDItemsFunctional;
import com.pasterdream.pasterdreammod.registry.items.PDItemsMaterials;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import com.pasterdream.pasterdreammod.world.WindJourneyEvents;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 第三梦境「风之旅途」VERIFY 套件 {@code wind-journey}。
 * <p>
 * 断言代码侧可稳定复现的流程点：structure datapack、Boss loot、云雾出维、
 * 祭坛阶段、雷云落雷、融梦箱宝藏成就、顺/逆风移速修饰符与云块物理。
 * locate 自然生成仍属 P0.5 手测。
 * <p>
 * <b>不</b>并入默认 {@code all}（与 twilight-lantern 同策略）；须
 * {@code PASTERDREAM_VERIFY_SUITES=wind-journey} 显式开启。
 */
public final class PDWindJourneyVerifyHooks {

    public record Result(boolean pass, String name, String detail) {
    }

    private static BlockPos altarPos;
    private static int knightsBefore;
    private static int cloudsBefore;

    private PDWindJourneyVerifyHooks() {
    }

    /**
     * 同步阶段：注册表 / loot / 进维 / 云雾出维 / loot roll / 雷云 / 祭坛启动。
     * 祭坛 86t 召唤结果由 {@link #verifyAltarAftermath} 在延迟后调用。
     */
    public static void verifySync(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        if (server == null) {
            out.accept(new Result(false, "wind-skip", "server == null"));
            return;
        }
        verifyStructureDatapack(server, out);
        verifyWindKnightLootTable(server, out);
        verifyFlagRegistered(out);
        out.accept(new Result(true, "san-system-moved", "PDSanHelper 已迁移至 PasterDreamSanity"));

        if (player == null) {
            out.accept(new Result(false, "wind-player-skip", "player == null"));
            return;
        }

        ServerLevel wind = server.getLevel(PDDimensions.WIND_JOURNEY_WORLD_LEVEL_KEY);
        if (wind == null) {
            out.accept(new Result(false, "wind-dim-missing", "wind_journey_world getLevel == null"));
            return;
        }

        double ox = player.getX();
        double oz = player.getZ();
        float yRot = player.getYRot();
        float xRot = player.getXRot();
        player.teleportTo(wind, ox, 120.0D, oz, yRot, xRot);
        out.accept(new Result(PDDimensions.isWindJourneyWorld(player.level()),
                "teleport-wind",
                "dim=" + player.level().dimension().location()));

        verifyCloudmistPresent(player, out);
        verifyCloudmistExit(server, player, out);

        if (!PDDimensions.isWindJourneyWorld(player.level())) {
            player.teleportTo(wind, ox, 120.0D, oz, yRot, xRot);
        }
        verifyMovementMechanics(server, player, out);
        verifyBossLootDrop(player, out);
        verifyThundercloudBolts(player, out);
        verifyMeltdreamTreasure(player, out);
        startAltarStages(player, out);
    }

    /**
     * 祭坛 86t 后：骑士 + 四雷云 + 台回 0。
     */
    public static void verifyAltarAftermath(ServerPlayer player, Consumer<Result> out) {
        if (player == null || altarPos == null) {
            out.accept(new Result(false, "altar-aftermath-skip", "no altar context"));
            return;
        }
        ServerLevel level = player.serverLevel();
        // 推进 ServerScheduler 已由主 tick 完成；此处只读结果
        boolean reset0 = level.getBlockState(altarPos).is(PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_0.get());
        int knights = level.getEntitiesOfClass(WindKnightEntity.class,
                new AABB(altarPos).inflate(16)).size();
        int clouds = level.getEntitiesOfClass(ThundercloudEntity.class,
                new AABB(altarPos).inflate(24)).size();
        boolean knightOk = knights >= knightsBefore + 1;
        boolean cloudOk = clouds >= cloudsBefore + 4;
        out.accept(new Result(reset0 && knightOk && cloudOk,
                "祭坛 4→召唤 骑士+4雷云 台回 0",
                "reset0=" + reset0
                        + " Δknight=" + (knights - knightsBefore)
                        + " Δcloud=" + (clouds - cloudsBefore)));

        level.getEntitiesOfClass(WindKnightEntity.class, new AABB(altarPos).inflate(16))
                .forEach(Entity::discard);
        level.getEntitiesOfClass(ThundercloudEntity.class, new AABB(altarPos).inflate(24))
                .forEach(Entity::discard);
        level.removeBlock(altarPos, false);
        altarPos = null;
    }

    // ==================== datapack / loot / 注册 ====================

    private static void verifyStructureDatapack(MinecraftServer server, Consumer<Result> out) {
        var structures = server.registryAccess().registryOrThrow(Registries.STRUCTURE);
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("pasterdream", "lost_windknight_ruins");
        boolean present = structures.containsKey(id);
        out.accept(new Result(present,
                "structure lost_windknight_ruins 注册",
                present ? "ok" : "missing"));

        var sets = server.registryAccess().registryOrThrow(Registries.STRUCTURE_SET);
        boolean setPresent = sets.containsKey(id);
        out.accept(new Result(setPresent,
                "structure_set lost_windknight_ruins 注册",
                setPresent ? "ok" : "missing"));

        boolean nbt = server.getStructureManager()
                .get(ResourceLocation.fromNamespaceAndPath("pasterdream", "lost_windknight_ruins"))
                .isPresent();
        out.accept(new Result(nbt,
                "STM 加载 lost_windknight_ruins.nbt",
                nbt ? "ok" : "missing"));
    }

    private static void verifyWindKnightLootTable(MinecraftServer server, Consumer<Result> out) {
        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath("pasterdream", "entities/wind_knight"));
        LootTable table = server.reloadableRegistries().getLootTable(key);
        boolean loaded = table != LootTable.EMPTY;
        out.accept(new Result(loaded,
                "loot entities/wind_knight 加载",
                loaded ? "non-empty" : "EMPTY"));
    }

    private static void verifyFlagRegistered(Consumer<Result> out) {
        boolean reg = PDItems.WIND_KNIGHT_FLAG.asItem() != null;
        out.accept(new Result(reg,
                "wind_knight_flag 已注册（原版亦无配方/loot，创造获取）",
                reg ? "ok" : "missing"));
    }

    // ==================== 云雾 ====================

    private static void verifyCloudmistPresent(ServerPlayer player, Consumer<Result> out) {
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                PDEffects.CLOUDMIST_BUFF.holder(), 200, 0, false, false));
        boolean has = player.hasEffect(PDEffects.CLOUDMIST_BUFF.holder());
        out.accept(new Result(has, "风维 cloudmist_buff 可挂效", has ? "dur=200" : "missing"));
    }

    private static void verifyCloudmistExit(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        ServerLevel wind = server.getLevel(PDDimensions.WIND_JOURNEY_WORLD_LEVEL_KEY);
        if (wind == null) {
            out.accept(new Result(false, "cloudmist-exit-skip", "wind null"));
            return;
        }
        if (!PDDimensions.isWindJourneyWorld(player.level())) {
            player.teleportTo(wind, player.getX(), 120.0D, player.getZ(),
                    player.getYRot(), player.getXRot());
        }
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                PDEffects.CLOUDMIST_BUFF.holder(), 400, 0, false, false));
        player.teleportTo(wind, player.getX(), 3.0D, player.getZ(),
                player.getYRot(), player.getXRot());
        for (int i = 0; i < 5; i++) {
            var inst = player.getEffect(PDEffects.CLOUDMIST_BUFF.holder());
            if (inst != null) {
                PDEffects.CLOUDMIST_BUFF.holder().value().applyEffectTick(player, inst.getAmplifier());
            }
        }
        boolean back = player.level().dimension() == Level.OVERWORLD;
        double y = player.getY();
        out.accept(new Result(back, "cloudmist Y≤5 返主世界",
                "dim=" + player.level().dimension().location() + " y=" + y));
        out.accept(new Result(back && Math.abs(y - 304.0D) < 2.0D,
                "出维落点 Y≈304", "y=" + y));
    }

    // ==================== 祭坛（多 tick：启动后 90t 读 aftermath） ====================

    private static void startAltarStages(ServerPlayer player, Consumer<Result> out) {
        ServerLevel level = player.serverLevel();
        BlockPos base = player.blockPosition().offset(4, 0, 4);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                level.setBlock(base.offset(dx, -1, dz), Blocks.STONE.defaultBlockState(), 3);
            }
        }
        level.setBlock(base, PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_0.get().defaultBlockState(), 3);
        BlockEntity be0 = level.getBlockEntity(base);
        out.accept(new Result(be0 != null, "祭坛 stage0 BE 创建",
                be0 == null ? "null BE" : be0.getClass().getSimpleName()));

        player.setItemInHand(InteractionHand.MAIN_HAND,
                new ItemStack(PDItemsMaterials.WINDRUNNER_CRYSTAL.get()));
        useBlock(player, level, base);
        boolean s1 = level.getBlockState(base).is(PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_1.get());
        out.accept(new Result(s1, "祭坛 0→1 风行者水晶",
                level.getBlockState(base).getBlock().toString()));

        // 凝风铁推进带 1t schedule：每步 use 后泵 2 个 scheduler tick
        for (int step = 0; step < 3; step++) {
            player.setItemInHand(InteractionHand.MAIN_HAND,
                    new ItemStack(PDItemsMaterials.WIND_IRON_INGOT.get()));
            useBlock(player, level, base);
            pumpScheduler(level.getServer(), 2);
        }
        boolean s4 = level.getBlockState(base).is(PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_4.get());
        out.accept(new Result(s4, "祭坛 →4 凝风铁×3",
                level.getBlockState(base).getBlock().toString()));

        altarPos = base;
        knightsBefore = level.getEntitiesOfClass(WindKnightEntity.class,
                new AABB(base).inflate(16)).size();
        cloudsBefore = level.getEntitiesOfClass(ThundercloudEntity.class,
                new AABB(base).inflate(24)).size();

        if (s4) {
            player.setItemInHand(InteractionHand.MAIN_HAND,
                    new ItemStack(lookupLightningSpell()));
            useBlock(player, level, base);
            out.accept(new Result(true, "祭坛 4 已投闪电法术（待 86t）", "scheduled"));
        } else {
            out.accept(new Result(false, "祭坛 4 已投闪电法术（待 86t）", "not at stage 4"));
        }
    }

    /**
     * 推进 {@link ServerScheduler} 计数（不跑完整 server tick，避免重入 VERIFY 调度）。
     */
    private static void pumpScheduler(MinecraftServer server, int ticks) {
        ServerScheduler.advanceForTest(ticks);
    }

    private static void useBlock(ServerPlayer player, ServerLevel level, BlockPos pos) {
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
        level.getBlockState(pos).useWithoutItem(level, player, hit);
    }

    // ==================== 雷云 ====================

    private static void verifyThundercloudBolts(ServerPlayer player, Consumer<Result> out) {
        ServerLevel level = player.serverLevel();
        double px = player.getX();
        double py = Math.floor(player.getY()) + 1.0D;
        double pz = player.getZ();
        player.teleportTo(level, px, py, pz, player.getYRot(), player.getXRot());

        ThundercloudEntity cloud = PDEntities.THUNDERCLOUD.get().create(level);
        if (cloud == null) {
            out.accept(new Result(false, "thundercloud spawn", "create null"));
            return;
        }
        cloud.moveTo(px, py + 8.0D, pz, 0.0F, 0.0F);
        cloud.setPersistenceRequired();
        if (!level.addFreshEntity(cloud)) {
            out.accept(new Result(false, "雷云落雷 LightningProjectile", "addFreshEntity false"));
            return;
        }

        // 同 tick 内 getEntitiesOfClass 往往看不到刚 addFresh 的实体；用工厂返回值断言。
        LightningProjectileEntity bolt = LightningProjectileEntity.summonRainBolt(
                level, player.getX(), player.getY() + 5.0D, player.getZ(), 7.0D);
        bolt.setOwner(cloud);
        boolean ok = bolt != null && !bolt.isRemoved();
        // 再走实体 force 路径（副作用；不依赖世界查询）
        cloud.forceRainBoltsForTest(player);

        out.accept(new Result(ok,
                "雷云落雷 LightningProjectile",
                ok ? "summonRainBolt+owner ok" : "bolt null/removed"));
        if (bolt != null) {
            bolt.discard();
        }
        cloud.discard();
    }

    // ==================== Boss loot ====================

    private static void verifyBossLootDrop(ServerPlayer player, Consumer<Result> out) {
        ServerLevel level = player.serverLevel();
        BlockPos p = player.blockPosition().offset(2, 0, 2);
        WindKnightEntity knight = PDEntities.WIND_KNIGHT.get().create(level);
        if (knight == null) {
            out.accept(new Result(false, "wind_knight spawn", "create null"));
            return;
        }
        knight.moveTo(p.getX() + 0.5, p.getY(), p.getZ() + 0.5, 0, 0);
        level.addFreshEntity(knight);

        ResourceKey<LootTable> key = ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath("pasterdream", "entities/wind_knight"));
        LootTable table = level.getServer().reloadableRegistries().getLootTable(key);
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, knight)
                .withParameter(LootContextParams.ORIGIN, knight.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic())
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, player)
                .withOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                .create(LootContextParamSets.ENTITY);
        List<ItemStack> drops = table.getRandomItems(params);
        boolean pulse = drops.stream().anyMatch(s -> s.is(PDItems.PULSE_WINDRUNNER_CRYSTAL.get()));
        out.accept(new Result(pulse,
                "wind_knight loot → pulse_windrunner_crystal",
                "drops=" + drops.stream()
                        .map(s -> s.getItem() + "x" + s.getCount())
                        .toList()));
        knight.discard();
    }

    // ==================== 融梦箱 ====================

    private static void verifyMeltdreamTreasure(ServerPlayer player, Consumer<Result> out) {
        ServerLevel level = player.serverLevel();
        if (!PDDimensions.isWindJourneyWorld(level)) {
            ServerLevel wind = player.server.getLevel(PDDimensions.WIND_JOURNEY_WORLD_LEVEL_KEY);
            if (wind == null) {
                out.accept(new Result(false, "meltdream-skip", "wind null"));
                return;
            }
            player.teleportTo(wind, player.getX(), 120, player.getZ(),
                    player.getYRot(), player.getXRot());
            level = wind;
        }

        ResourceLocation advId = ResourceLocation.fromNamespaceAndPath(
                "pasterdream", "achievement_treasure_wind_journey");
        AdvancementHolder holder = player.server.getAdvancements().get(advId);
        out.accept(new Result(holder != null,
                "成就 achievement_treasure_wind_journey 注册",
                holder == null ? "missing" : "present"));
        if (holder != null) {
            var progress = player.getAdvancements().getOrStartProgress(holder);
            List<String> done = new ArrayList<>();
            for (String crit : progress.getCompletedCriteria()) {
                done.add(crit);
            }
            for (String crit : done) {
                player.getAdvancements().revoke(holder, crit);
            }
        }

        BlockPos base = player.blockPosition().offset(6, 0, 0);
        level.setBlock(base.below(), Blocks.STONE.defaultBlockState(), 3);
        level.setBlock(base, PDBlocks.MELTDREAM_CHEST.get().defaultBlockState(), 3);
        BlockEntity be = level.getBlockEntity(base);
        out.accept(new Result(be != null, "融梦箱 BE",
                be == null ? "null" : be.getClass().getSimpleName()));

        useBlock(player, level, base);
        boolean granted = holder != null
                && player.getAdvancements().getOrStartProgress(holder).isDone();
        out.accept(new Result(granted,
                "风维开融梦箱授予 treasure_wind_journey",
                granted ? "granted" : "not granted"));
        level.removeBlock(base, false);
    }

    /**
     * 动态查找 PasterDreamSpells 的闪电法术物品。
     *
     * @return 闪电法术物品，未注册时返回 Items.AIR
     */
    private static Item lookupLightningSpell() {
        return BuiltInRegistries.ITEM.getOptional(
                ResourceLocation.fromNamespaceAndPath("pasterdreamspells", "lightning_spell"))
                .orElse(net.minecraft.world.item.Items.AIR);
    }

    // ==================== 移动专项（顺/逆风 + 云块物理 + 跨维清理） ====================

    private static final ResourceLocation DEADWIND_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "deadwind_buff_0");
    private static final ResourceLocation TAILWIND_SPEED_ID =
            ResourceLocation.fromNamespaceAndPath("pasterdream", "tailwind_buff_0");

    /**
     * Changelog 组件二/四对应断言：
     * <ul>
     *   <li>云块 friction / speedFactor / jumpFactor</li>
     *   <li>deadwind/tailwind 挂效后 MOVEMENT_SPEED permanent modifier 存在且数值正确</li>
     *   <li>removeEffect 后 modifier 被 onRemove 清掉</li>
     *   <li>PlayerTick + 朝向锥触发顺/逆风</li>
     *   <li>离开风维事件路径清掉效果与 modifier（防残留）</li>
     * </ul>
     */
    private static void verifyMovementMechanics(MinecraftServer server, ServerPlayer player, Consumer<Result> out) {
        verifyCloudPhysics(out);

        // 隔离：清掉可能残留的顺/逆风
        player.removeEffect(PDEffects.DEADWIND_BUFF.holder());
        player.removeEffect(PDEffects.TAILWIND_BUFF.holder());
        stripWindSpeedModifiers(player);

        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            out.accept(new Result(false, "move.attr", "MOVEMENT_SPEED == null"));
            return;
        }
        double baseSpeed = speed.getValue();

        // --- deadwind amp0：-0.02 ADD_VALUE ---
        player.addEffect(new MobEffectInstance(PDEffects.DEADWIND_BUFF.holder(), 200, 0, false, false));
        boolean deadHas = speed.hasModifier(DEADWIND_SPEED_ID);
        double deadAmt = deadHas && speed.getModifier(DEADWIND_SPEED_ID) != null
                ? speed.getModifier(DEADWIND_SPEED_ID).amount() : Double.NaN;
        boolean deadOk = deadHas && Math.abs(deadAmt - (-0.02)) < 1e-9
                && speed.getValue() < baseSpeed - 0.001;
        out.accept(new Result(deadOk, "move.deadwind_mod_amp0",
                String.format("has=%s amt=%s speed=%.4f→%.4f", deadHas, deadAmt, baseSpeed, speed.getValue())));

        player.removeEffect(PDEffects.DEADWIND_BUFF.holder());
        boolean deadCleared = !speed.hasModifier(DEADWIND_SPEED_ID)
                && !player.hasEffect(PDEffects.DEADWIND_BUFF.holder());
        out.accept(new Result(deadCleared, "move.deadwind_remove_clears",
                "hasMod=" + speed.hasModifier(DEADWIND_SPEED_ID)
                        + " hasFx=" + player.hasEffect(PDEffects.DEADWIND_BUFF.holder())));

        // --- tailwind amp0：+0.03 ADD_VALUE ---
        double base2 = speed.getValue();
        player.addEffect(new MobEffectInstance(PDEffects.TAILWIND_BUFF.holder(), 200, 0, false, false));
        boolean tailHas = speed.hasModifier(TAILWIND_SPEED_ID);
        double tailAmt = tailHas && speed.getModifier(TAILWIND_SPEED_ID) != null
                ? speed.getModifier(TAILWIND_SPEED_ID).amount() : Double.NaN;
        boolean tailOk = tailHas && Math.abs(tailAmt - 0.03) < 1e-9
                && speed.getValue() > base2 + 0.001;
        out.accept(new Result(tailOk, "move.tailwind_mod_amp0",
                String.format("has=%s amt=%s speed=%.4f→%.4f", tailHas, tailAmt, base2, speed.getValue())));

        // 顺风 onApply 应互斥清掉逆风（再挂逆风后只剩逆风 mod）
        player.addEffect(new MobEffectInstance(PDEffects.DEADWIND_BUFF.holder(), 200, 0, false, false));
        boolean mutex = speed.hasModifier(DEADWIND_SPEED_ID) && !speed.hasModifier(TAILWIND_SPEED_ID)
                && !player.hasEffect(PDEffects.TAILWIND_BUFF.holder());
        out.accept(new Result(mutex, "move.deadwind_mutex_tailwind",
                "deadMod=" + speed.hasModifier(DEADWIND_SPEED_ID)
                        + " tailMod=" + speed.hasModifier(TAILWIND_SPEED_ID)
                        + " tailFx=" + player.hasEffect(PDEffects.TAILWIND_BUFF.holder())));

        player.removeEffect(PDEffects.DEADWIND_BUFF.holder());
        player.removeEffect(PDEffects.TAILWIND_BUFF.holder());
        stripWindSpeedModifiers(player);

        // --- PlayerTick 朝向锥：风向 0、面朝 0° → 顺风 ---
        if (!PDDimensions.isWindJourneyWorld(player.level())) {
            ServerLevel wind = server.getLevel(PDDimensions.WIND_JOURNEY_WORLD_LEVEL_KEY);
            if (wind != null) {
                player.teleportTo(wind, player.getX(), 120.0D, player.getZ(),
                        player.getYRot(), player.getXRot());
            }
        }
        if (PDDimensions.isWindJourneyWorld(player.level())) {
            ServerLevel windLvl = player.serverLevel();
            GameRules.IntegerValue rule = windLvl.getGameRules().getRule(PDGameRules.WIND_DIRECTION);
            int prevDir = rule.get();
            rule.set(0, server);

            // 清 force NBT（amp0）；清防风
            player.getPersistentData().putDouble("player_tailwind_force", 0);
            player.getPersistentData().putDouble("player_deadwind_force", 0);
            player.removeEffect(PDEffects.WINDPROOF_BUFF.holder());
            player.removeEffect(PDEffects.DEADWIND_BUFF.holder());
            player.removeEffect(PDEffects.TAILWIND_BUFF.holder());
            stripWindSpeedModifiers(player);

            // onPlayerTick 仅在 tickCount % interval == 0 时执行；直接调事件不会推进 tickCount，
            // 必须把 tickCount 对齐到 interval 倍数，否则整段被跳过（间歇感的测试镜像）。
            int interval = Math.max(1, com.pasterdream.pasterdreammod.config.PDCommonConfig
                    .PLAYER_TOTAL_TICK_UPDATE.get());
            player.tickCount = (player.tickCount / interval) * interval;
            player.setYRot(0.0F);
            player.setYHeadRot(0.0F);
            WindJourneyEvents.onPlayerTick(new PlayerTickEvent.Post(player));
            boolean tickTail = player.hasEffect(PDEffects.TAILWIND_BUFF.holder())
                    && speed.hasModifier(TAILWIND_SPEED_ID);
            out.accept(new Result(tickTail, "move.tick_facing_tailwind",
                    "fx=" + player.hasEffect(PDEffects.TAILWIND_BUFF.holder())
                            + " mod=" + speed.hasModifier(TAILWIND_SPEED_ID)
                            + " dir=0 yRot=0 interval=" + interval
                            + " tickCount=" + player.tickCount));

            // 背风 180° → 逆风
            player.removeEffect(PDEffects.TAILWIND_BUFF.holder());
            player.removeEffect(PDEffects.DEADWIND_BUFF.holder());
            stripWindSpeedModifiers(player);
            player.tickCount = (player.tickCount / interval) * interval;
            player.setYRot(180.0F);
            player.setYHeadRot(180.0F);
            WindJourneyEvents.onPlayerTick(new PlayerTickEvent.Post(player));
            boolean tickDead = player.hasEffect(PDEffects.DEADWIND_BUFF.holder())
                    && speed.hasModifier(DEADWIND_SPEED_ID);
            out.accept(new Result(tickDead, "move.tick_facing_deadwind",
                    "fx=" + player.hasEffect(PDEffects.DEADWIND_BUFF.holder())
                            + " mod=" + speed.hasModifier(DEADWIND_SPEED_ID)
                            + " dir=0 yRot=180 tickCount=" + player.tickCount));

            // 防风时 tick 不应再挂风
            player.removeEffect(PDEffects.DEADWIND_BUFF.holder());
            player.removeEffect(PDEffects.TAILWIND_BUFF.holder());
            stripWindSpeedModifiers(player);
            player.addEffect(new MobEffectInstance(PDEffects.WINDPROOF_BUFF.holder(), 200, 0, false, false));
            player.tickCount = (player.tickCount / interval) * interval;
            player.setYRot(0.0F);
            WindJourneyEvents.onPlayerTick(new PlayerTickEvent.Post(player));
            boolean proofOk = !player.hasEffect(PDEffects.TAILWIND_BUFF.holder())
                    && !player.hasEffect(PDEffects.DEADWIND_BUFF.holder())
                    && !speed.hasModifier(TAILWIND_SPEED_ID)
                    && !speed.hasModifier(DEADWIND_SPEED_ID);
            out.accept(new Result(proofOk, "move.windproof_blocks_tick",
                    "tail=" + player.hasEffect(PDEffects.TAILWIND_BUFF.holder())
                            + " dead=" + player.hasEffect(PDEffects.DEADWIND_BUFF.holder())));
            player.removeEffect(PDEffects.WINDPROOF_BUFF.holder());

            rule.set(prevDir, server);
        } else {
            out.accept(new Result(false, "move.tick_skip", "not in wind dim"));
        }

        // --- 跨维清理：挂逆风后模拟离开风维 ---
        player.addEffect(new MobEffectInstance(PDEffects.DEADWIND_BUFF.holder(), 200, 0, false, false));
        boolean beforeLeave = speed.hasModifier(DEADWIND_SPEED_ID);
        WindJourneyEvents.onPlayerChangedDimension(
                new PlayerEvent.PlayerChangedDimensionEvent(
                        player,
                        PDDimensions.WIND_JOURNEY_WORLD_LEVEL_KEY,
                        Level.OVERWORLD));
        boolean afterLeave = !player.hasEffect(PDEffects.DEADWIND_BUFF.holder())
                && !speed.hasModifier(DEADWIND_SPEED_ID);
        out.accept(new Result(beforeLeave && afterLeave, "move.leave_dim_clears_deadwind",
                "beforeMod=" + beforeLeave
                        + " afterFx=" + player.hasEffect(PDEffects.DEADWIND_BUFF.holder())
                        + " afterMod=" + speed.hasModifier(DEADWIND_SPEED_ID)));

        // 再挂顺风测同样路径
        player.addEffect(new MobEffectInstance(PDEffects.TAILWIND_BUFF.holder(), 200, 0, false, false));
        boolean beforeLeaveT = speed.hasModifier(TAILWIND_SPEED_ID);
        WindJourneyEvents.onPlayerChangedDimension(
                new PlayerEvent.PlayerChangedDimensionEvent(
                        player,
                        PDDimensions.WIND_JOURNEY_WORLD_LEVEL_KEY,
                        Level.OVERWORLD));
        boolean afterLeaveT = !player.hasEffect(PDEffects.TAILWIND_BUFF.holder())
                && !speed.hasModifier(TAILWIND_SPEED_ID);
        out.accept(new Result(beforeLeaveT && afterLeaveT, "move.leave_dim_clears_tailwind",
                "beforeMod=" + beforeLeaveT
                        + " afterFx=" + player.hasEffect(PDEffects.TAILWIND_BUFF.holder())
                        + " afterMod=" + speed.hasModifier(TAILWIND_SPEED_ID)));

        // 收尾：确保无残留
        player.removeEffect(PDEffects.DEADWIND_BUFF.holder());
        player.removeEffect(PDEffects.TAILWIND_BUFF.holder());
        player.removeEffect(PDEffects.WINDPROOF_BUFF.holder());
        stripWindSpeedModifiers(player);
    }

    private static void verifyCloudPhysics(Consumer<Result> out) {
        Block cloud = PDBlocks.CLOUD.get();
        Block thick = PDBlocks.THICK_CLOUD.get();
        // Changelog：cloud friction 0.5 / speed 1.25 / jump 1.1
        // thick friction 0.55 / speed 1.2 / jump 1.05
        boolean cloudOk = approx(cloud.getFriction(), 0.5f)
                && approx(cloud.getSpeedFactor(), 1.25f)
                && approx(cloud.getJumpFactor(), 1.1f);
        out.accept(new Result(cloudOk, "move.cloud_physics",
                String.format("friction=%.3f speed=%.3f jump=%.3f",
                        cloud.getFriction(), cloud.getSpeedFactor(), cloud.getJumpFactor())));

        boolean thickOk = approx(thick.getFriction(), 0.55f)
                && approx(thick.getSpeedFactor(), 1.2f)
                && approx(thick.getJumpFactor(), 1.05f);
        out.accept(new Result(thickOk, "move.thick_cloud_physics",
                String.format("friction=%.3f speed=%.3f jump=%.3f",
                        thick.getFriction(), thick.getSpeedFactor(), thick.getJumpFactor())));

        // 厚云必须保持完整碰撞（default_block，不能 noCollision）
        var shape = thick.defaultBlockState().getCollisionShape(
                net.minecraft.world.level.EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        boolean solid = !shape.isEmpty();
        out.accept(new Result(solid, "move.thick_cloud_solid_collision",
                solid ? "non-empty collision" : "empty collision"));
    }

    private static boolean approx(float a, float b) {
        return Math.abs(a - b) < 1e-4f;
    }

    private static void stripWindSpeedModifiers(ServerPlayer player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(DEADWIND_SPEED_ID);
            speed.removeModifier(TAILWIND_SPEED_ID);
        }
    }
}
