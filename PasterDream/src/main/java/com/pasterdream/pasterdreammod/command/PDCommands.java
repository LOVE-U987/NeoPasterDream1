package com.pasterdream.pasterdreammod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.util.DimensionRegionHelper;
import com.pasterdream.pasterdreammod.world.PDAaroncosArenaSpawnData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 模组指令注册 —— 提供维度刷新等调试/测试功能
 * <p>
 * 指令列表：
 * <ul>
 *   <li>{@code /pasterdream dimension reset <dimension_id>} —— 重置指定维度（踢出玩家、删除 region 文件）</li>
 *   <li>{@code /pasterdream arena locate} —— 显示主世界竞技场遗迹坐标与距离（遗迹为手动放置，/locate 无法定位）</li>
 *   <li>{@code /pasterdream arena tp} —— 传送到主世界竞技场遗迹附近</li>
 * </ul>
 */
public class PDCommands {

    /**
     * 注册所有指令 —— 监听 RegisterCommandsEvent
     *
     * @param event 指令注册事件
     */
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("pasterdream")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("dimension")
                                .then(Commands.literal("reset")
                                        .then(Commands.argument("dimension_id", StringArgumentType.word())
                                                .executes(context -> {
                                                    String dimId = StringArgumentType.getString(context, "dimension_id");
                                                    return resetDimension(context.getSource(), dimId);
                                                })
                                        )
                                        .executes(context -> {
                                            context.getSource().sendFailure(
                                                    Component.translatable("message.pasterdream.command.usage_dimension_reset"));
                                            return 0;
                                        })
                                )
                        )
                        .then(Commands.literal("arena")
                                .then(Commands.literal("locate")
                                        .executes(context -> arenaLocate(context.getSource()))
                                )
                                .then(Commands.literal("tp")
                                        .executes(context -> arenaTp(context.getSource()))
                                )
                                .executes(context -> {
                                    context.getSource().sendFailure(
                                            Component.translatable("message.pasterdream.command.usage_arena"));
                                    return 0;
                                })
                        )
                        .then(Commands.literal("bgm")
                                .then(Commands.literal("debug")
                                        .executes(context -> bgmDebug(context.getSource()))
                                )
                                .then(Commands.literal("play")
                                        .then(Commands.argument("biome", StringArgumentType.word())
                                                .executes(context -> {
                                                    String biome = StringArgumentType.getString(context, "biome");
                                                    return bgmPlay(context.getSource(), biome);
                                                })
                                        )
                                )
                                .then(Commands.literal("list")
                                        .executes(context -> bgmList(context.getSource()))
                                )
                                .executes(context -> {
                                    context.getSource().sendFailure(
                                            Component.translatable("message.pasterdream.command.usage_bgm"));
                                    return 0;
                                })
                        )
        );
    }

    /**
     * 重置指定维度的逻辑：
     * <ol>
     *   <li>将维度内所有玩家传送回主世界出生点</li>
     *   <li>删除该维度的 region 文件（.mca）</li>
     *   <li>下次玩家进入时自动重新生成地形</li>
     * </ol>
     *
     * @param source      指令来源
     * @param dimensionId 维度 ID（如 "pasterdream:dyedream_world"）
     * @return 操作结果状态码
     */
    private static int resetDimension(CommandSourceStack source, String dimensionId) {
        MinecraftServer server = source.getServer();

        ResourceLocation dimLocation;
        if (dimensionId.contains(":")) {
            dimLocation = ResourceLocation.parse(dimensionId);
        } else {
            dimLocation = ResourceLocation.fromNamespaceAndPath(dimensionId, dimensionId);
        }

        ResourceKey<Level> dimKey = ResourceKey.create(Registries.DIMENSION, dimLocation);
        ServerLevel targetLevel = server.getLevel(dimKey);

        if (targetLevel == null) {
            source.sendFailure(Component.translatable("message.pasterdream.command.dimension_not_found", dimLocation));
            return 0;
        }

        List<ServerPlayer> playersInDim = new ArrayList<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.level().dimension().equals(dimKey)) {
                playersInDim.add(player);
            }
        }

        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            source.sendFailure(Component.translatable("message.pasterdream.command.overworld_not_loaded"));
            return 0;
        }

        for (ServerPlayer player : playersInDim) {
            BlockPos spawnPos = overworld.getSharedSpawnPos();
            player.teleportTo(overworld, spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5, player.getYRot(), player.getXRot());
            player.sendSystemMessage(Component.translatable("message.pasterdream.command.dimension_reset_teleport", dimLocation));
        }

        Path regionPath = DimensionRegionHelper.regionDirectory(server, dimLocation);

        if (Files.exists(regionPath)) {
            try {
                server.executeBlocking(() -> targetLevel.save(null, false, false));

                int fileCount = DimensionRegionHelper.deleteRegionChunkFiles(
                        regionPath,
                        file -> PasterDreamMod.LOGGER.warn("[PDCommands] 无法删除区域文件: {}", file));
                source.sendSuccess(() -> Component.translatable("message.pasterdream.command.dimension_reset_success", dimLocation, fileCount), true);

                for (ServerPlayer player : playersInDim) {
                    player.sendSystemMessage(Component.translatable("message.pasterdream.command.dimension_reset_reenter", dimLocation));
                }

                return 1;
            } catch (IOException | CompletionException e) {
                source.sendFailure(Component.translatable("message.pasterdream.command.dimension_reset_error", e.getMessage()));
                PasterDreamMod.LOGGER.error("[PDCommands] 重置维度 {} 时发生 IO/执行异常", dimLocation, e);
                return 0;
            } catch (Exception e) {
                // 兜底：捕获未预期异常，防止命令执行中断影响服务端稳定性
                source.sendFailure(Component.translatable("message.pasterdream.command.dimension_reset_error", e.getMessage()));
                PasterDreamMod.LOGGER.error("[PDCommands] 重置维度 {} 时发生未预期异常", dimLocation, e);
                return 0;
            }
        } else {
            source.sendSuccess(() -> Component.translatable("message.pasterdream.command.dimension_no_region", dimLocation), true);
            return 1;
        }
    }

    // ==================== 竞技场遗迹定位指令 ====================

    /**
     * 获取主世界竞技场遗迹中心（未生成时返回 null）。
     *
     * @param source 指令来源
     * @return 遗迹中心坐标；主世界未加载或尚未生成时返回 null
     */
    private static BlockPos getArenaCenter(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            source.sendFailure(Component.translatable("message.pasterdream.command.overworld_not_loaded"));
            return null;
        }
        return PDAaroncosArenaSpawnData.get(overworld).getCenter();
    }

    /**
     * 定位指令：显示主世界竞技场遗迹坐标与距离。
     * <p>
     * 遗迹由结构集正常随机生成且每世界仅一座（非 /locate 可搜的常规登记结构时用本指令），
     * 坐标直接从存档记录读取。
     *
     * @param source 指令来源
     * @return 操作结果状态码
     */
    private static int arenaLocate(CommandSourceStack source) {
        BlockPos center = getArenaCenter(source);
        if (center == null) {
            source.sendFailure(Component.translatable("message.pasterdream.command.arena_not_generated"));
            return 0;
        }

        if (source.getEntity() instanceof ServerPlayer player) {
            int dist = (int) Math.sqrt(player.blockPosition().distSqr(center));
            source.sendSuccess(() -> Component.translatable("message.pasterdream.command.arena_locate_player",
                    center.getX(), center.getY(), center.getZ(), dist), true);
        } else {
            source.sendSuccess(() -> Component.translatable("message.pasterdream.command.arena_locate",
                    center.getX(), center.getY(), center.getZ()), true);
        }
        return 1;
    }

    /**
     * 传送指令：把玩家传送到主世界竞技场遗迹附近的地表。
     *
     * @param source 指令来源
     * @return 操作结果状态码
     */
    private static int arenaTp(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("message.pasterdream.command.player_only"));
            return 0;
        }
        BlockPos center = getArenaCenter(source);
        if (center == null) {
            source.sendFailure(Component.translatable("message.pasterdream.command.arena_not_generated"));
            return 0;
        }

        MinecraftServer server = source.getServer();
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        // 预生成遗迹所在 chunk，确保能取得正确地表高度
        overworld.getChunk(center.getX() >> 4, center.getZ() >> 4, ChunkStatus.FULL, true);
        int surfaceY = overworld.getHeight(Heightmap.Types.WORLD_SURFACE, center.getX(), center.getZ());

        player.teleportTo(overworld, center.getX() + 0.5, surfaceY + 2, center.getZ() + 0.5, player.getYRot(), player.getXRot());
        source.sendSuccess(() -> Component.translatable("message.pasterdream.command.arena_tp_success"), true);
        return 1;
    }

    // ==================== BGM 调试指令 ====================

    private static final String[] BGM_BIOMES = {
            "dream_meadow", "dream_heath", "dream_taiga", "dream_delta"
    };

    /** 群系 BGM 显示名 → 语言键（值改经语言文件本地化，避免中英文本写死在代码里） */
    private static final java.util.Map<String, String> BGM_KEYS = java.util.Map.of(
            "dream_meadow", "message.pasterdream.command.bgm_name.dream_meadow",
            "dream_heath", "message.pasterdream.command.bgm_name.dream_heath",
            "dream_taiga", "message.pasterdream.command.bgm_name.dream_taiga",
            "dream_delta", "message.pasterdream.command.bgm_name.dream_delta"
    );

    /**
     * 调试指令：检查当前玩家所在位置的群系音乐配置
     */
    private static int bgmDebug(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("message.pasterdream.command.player_only"));
            return 0;
        }

        ResourceLocation biomeId = player.level().getBiome(player.blockPosition()).unwrapKey()
                .map(key -> key.location())
                .orElse(null);

        var biome = player.level().getBiome(player.blockPosition()).value();
        var musicOpt = biome.getBackgroundMusic();

        MutableComponent msg = Component.translatable("message.pasterdream.command.bgm_debug_header")
                .append(Component.literal("\n"))
                .append(Component.translatable("message.pasterdream.command.bgm_debug_pos", player.blockPosition().toShortString()))
                .append(Component.literal("\n"))
                .append(Component.translatable("message.pasterdream.command.bgm_debug_dim", player.level().dimension().location()))
                .append(Component.literal("\n"))
                .append(Component.translatable("message.pasterdream.command.bgm_debug_biome",
                        biomeId != null ? biomeId : Component.translatable("message.pasterdream.command.unknown")))
                .append(Component.literal("\n"))
                .append(Component.translatable("message.pasterdream.command.bgm_debug_temp", biome.getBaseTemperature()));

        if (musicOpt.isPresent()) {
            var music = musicOpt.get();
            msg.append(Component.literal("\n"))
                    .append(Component.translatable("message.pasterdream.command.bgm_debug_music_exists"))
                    .append(Component.literal("\n"))
                    .append(Component.translatable("message.pasterdream.command.bgm_debug_music_sound", music.getEvent().value().getLocation()))
                    .append(Component.literal("\n"))
                    .append(Component.translatable("message.pasterdream.command.bgm_debug_music_min_delay", music.getMinDelay(), music.getMinDelay() / 20))
                    .append(Component.literal("\n"))
                    .append(Component.translatable("message.pasterdream.command.bgm_debug_music_max_delay", music.getMaxDelay(), music.getMaxDelay() / 20))
                    .append(Component.literal("\n"))
                    .append(Component.translatable("message.pasterdream.command.bgm_debug_music_replace", music.replaceCurrentMusic()));

            PDDebugLogger.mainInfo("[BGMDebug] 玩家 {} 在群系 {}，音乐配置: event={}, minDelay={}, maxDelay={}, replace={}",
                    player.getName().getString(), biomeId,
                    music.getEvent().value().getLocation(), music.getMinDelay(), music.getMaxDelay(), music.replaceCurrentMusic());
        } else {
            msg.append(Component.literal("\n"))
                    .append(Component.translatable("message.pasterdream.command.bgm_debug_music_missing"));
            PDDebugLogger.mainInfo("[BGMDebug] 玩家 {} 在群系 {}，无音乐配置", player.getName().getString(), biomeId);
        }

        source.sendSuccess(() -> msg, true);
        return 1;
    }

    /**
     * 调试指令：手动播放指定群系的BGM
     */
    private static int bgmPlay(CommandSourceStack source, String biome) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.translatable("message.pasterdream.command.player_only"));
            return 0;
        }

        String soundName = "music." + biome;
        ResourceLocation soundLocation = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, soundName);

        var soundEvent = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(soundLocation);

        if (soundEvent == null) {
            source.sendFailure(Component.translatable("message.pasterdream.command.bgm_sound_not_found", soundLocation));
            PDDebugLogger.mainInfo("[BGMDebug] 尝试播放 BGM 失败: {} 未注册", soundLocation);
            return 0;
        }

        player.playNotifySound(soundEvent, net.minecraft.sounds.SoundSource.MUSIC, 1.0F, 1.0F);

        Component displayName = BGM_KEYS.containsKey(biome)
                ? Component.translatable(BGM_KEYS.get(biome))
                : Component.literal(biome);
        source.sendSuccess(() -> Component.translatable("message.pasterdream.command.bgm_play", displayName, soundLocation), true);
        PDDebugLogger.mainInfo("[BGMDebug] 已为玩家 {} 播放 BGM: {}", player.getName().getString(), soundLocation);
        return 1;
    }

    /**
     * 调试指令：列出所有已注册的BGM声音事件
     */
    private static int bgmList(CommandSourceStack source) {
        MutableComponent msg = Component.translatable("message.pasterdream.command.bgm_list_header");

        int found = 0;
        for (String biome : BGM_BIOMES) {
            String soundName = "music." + biome;
            ResourceLocation soundLocation = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, soundName);
            var soundEvent = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(soundLocation);

            Component displayName = Component.translatable(BGM_KEYS.get(biome));
            if (soundEvent != null) {
                msg.append(Component.literal("\n"))
                        .append(Component.translatable("message.pasterdream.command.bgm_list_registered", displayName, soundLocation));
                found++;
            } else {
                msg.append(Component.literal("\n"))
                        .append(Component.translatable("message.pasterdream.command.bgm_list_missing", displayName, soundLocation));
            }
        }
        msg.append(Component.literal("\n"))
                .append(Component.translatable("message.pasterdream.command.bgm_list_summary", found, BGM_BIOMES.length));

        source.sendSuccess(() -> msg, true);

        PDDebugLogger.mainInfo("[BGMDebug] BGM 清单: {}/{} 已注册", found, BGM_BIOMES.length);
        return 1;
    }

}