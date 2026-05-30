package com.pasterdream.pasterdreammod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * 模组指令注册 —— 提供维度刷新等调试/测试功能
 * <p>
 * 指令列表：
 * <ul>
 *   <li>{@code /pasterdream dimension reset <dimension_id>} —— 重置指定维度（踢出玩家、删除 region 文件）</li>
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
                                                    Component.literal("§c用法: /pasterdream dimension reset <dimension_id>"));
                                            return 0;
                                        })
                                )
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
                                            Component.literal("§c用法: /pasterdream bgm <debug|play|list>"));
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
            source.sendFailure(Component.literal("§c维度 " + dimLocation + " 不存在或未加载！"));
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
            source.sendFailure(Component.literal("§c主世界未加载！"));
            return 0;
        }

        for (ServerPlayer player : playersInDim) {
            BlockPos spawnPos = overworld.getSharedSpawnPos();
            player.teleportTo(overworld, spawnPos.getX() + 0.5, spawnPos.getY() + 1, spawnPos.getZ() + 0.5, player.getYRot(), player.getXRot());
            player.sendSystemMessage(Component.literal("§e[PasterDream] 维度 " + dimLocation + " 正在重置，你已被传送回主世界。"));
        }

        Path dimensionPath = server.getWorldPath(LevelResource.ROOT).resolve(dimLocation.getNamespace()).resolve(dimLocation.getPath());
        Path regionPath = dimensionPath.resolve("region");

        if (Files.exists(regionPath)) {
            try {
                server.executeBlocking(() -> targetLevel.save(null, false, false));

                deleteMcaFiles(regionPath);

                int fileCount = countMcaFiles(regionPath);
                source.sendSuccess(() -> Component.literal("§a已重置维度 " + dimLocation + "，删除了 " + fileCount + " 个区域文件。下次进入将重新生成地形！"), true);

                for (ServerPlayer player : playersInDim) {
                    player.sendSystemMessage(Component.literal("§a维度 " + dimLocation + " 已重置，你可以重新进入！"));
                }

                return 1;
            } catch (Exception e) {
                source.sendFailure(Component.literal("§c重置维度时出错: " + e.getMessage()));
                e.printStackTrace();
                return 0;
            }
        } else {
            source.sendSuccess(() -> Component.literal("§e维度 " + dimLocation + " 尚未生成区域数据，无需重置。"), true);
            return 1;
        }
    }

    /**
     * 递归删除目录下所有 .mca 和 .mcc 文件
     *
     * @param path 目录路径
     */
    private static void deleteMcaFiles(Path path) throws IOException {
        if (!Files.exists(path)) return;

        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                String fileName = file.getFileName().toString().toLowerCase();
                if (fileName.endsWith(".mca") || fileName.endsWith(".mcc")) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        System.err.println("无法删除文件: " + file + " - " + e.getMessage());
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 统计目录下的 .mca 文件数量
     *
     * @param path 目录路径
     * @return .mca 文件数量
     */
    private static int countMcaFiles(Path path) throws IOException {
        if (!Files.exists(path)) return 0;

        int[] count = {0};
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.getFileName().toString().toLowerCase().endsWith(".mca")) {
                    count[0]++;
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return count[0];
    }

    // ==================== BGM 调试指令 ====================

    private static final String[] BGM_BIOMES = {
            "dream_meadow", "dream_heath", "dream_taiga", "dream_delta"
    };

    private static final java.util.Map<String, String> BGM_NAMES = java.util.Map.of(
            "dream_meadow", "梦幻草原",
            "dream_heath", "梦幻荒原",
            "dream_taiga", "梦幻雪林",
            "dream_delta", "梦幻三角洲"
    );

    /**
     * 调试指令：检查当前玩家所在位置的群系音乐配置
     */
    private static int bgmDebug(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c此指令只能由玩家执行！"));
            return 0;
        }

        ResourceLocation biomeId = player.level().getBiome(player.blockPosition()).unwrapKey()
                .map(key -> key.location())
                .orElse(null);

        var biome = player.level().getBiome(player.blockPosition()).value();
        var musicOpt = biome.getBackgroundMusic();

        StringBuilder sb = new StringBuilder();
        sb.append("§6=== [PasterDream BGM 调试] ===\n");
        sb.append("§e当前位置: ").append(player.blockPosition().toShortString()).append("\n");
        sb.append("§e当前维度: ").append(player.level().dimension().location()).append("\n");
        sb.append("§e当前群系: ").append(biomeId != null ? biomeId : "§c未知").append("\n");
        sb.append("§e群系温度: ").append(biome.getBaseTemperature()).append("\n");

        if (musicOpt.isPresent()) {
            var music = musicOpt.get();
            sb.append("§a音乐配置: 存在 ✓\n");
            sb.append("  §7Sound: §f").append(music.getEvent().value().getLocation()).append("\n");
            sb.append("  §7MinDelay: §f").append(music.getMinDelay()).append(" tick (").append(music.getMinDelay() / 20).append("s)\n");
            sb.append("  §7MaxDelay: §f").append(music.getMaxDelay()).append(" tick (").append(music.getMaxDelay() / 20).append("s)\n");
            sb.append("  §7ReplaceCurrent: §f").append(music.replaceCurrentMusic()).append("\n");

            PasterDreamMod.LOGGER.info("[BGMDebug] 玩家 {} 在群系 {}，音乐配置: event={}, minDelay={}, maxDelay={}, replace={}",
                    player.getName().getString(), biomeId,
                    music.getEvent().value().getLocation(), music.getMinDelay(), music.getMaxDelay(), music.replaceCurrentMusic());
        } else {
            sb.append("§c音乐配置: 不存在 ✗\n");
            PasterDreamMod.LOGGER.info("[BGMDebug] 玩家 {} 在群系 {}，无音乐配置", player.getName().getString(), biomeId);
        }

        source.sendSuccess(() -> Component.literal(sb.toString()), true);
        return 1;
    }

    /**
     * 调试指令：手动播放指定群系的BGM
     */
    private static int bgmPlay(CommandSourceStack source, String biome) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c此指令只能由玩家执行！"));
            return 0;
        }

        String soundName = "music." + biome;
        ResourceLocation soundLocation = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, soundName);

        var soundEvent = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(soundLocation);

        if (soundEvent == null) {
            source.sendFailure(Component.literal("§c未找到声音事件: " + soundLocation));
            PasterDreamMod.LOGGER.info("[BGMDebug] 尝试播放 BGM 失败: {} 未注册", soundLocation);
            return 0;
        }

        player.playNotifySound(soundEvent, net.minecraft.sounds.SoundSource.MUSIC, 1.0F, 1.0F);

        String displayName = BGM_NAMES.getOrDefault(biome, biome);
        source.sendSuccess(() -> Component.literal("§a正在播放 BGM: " + displayName + " (" + soundLocation + ")"), true);
        PasterDreamMod.LOGGER.info("[BGMDebug] 已为玩家 {} 播放 BGM: {}", player.getName().getString(), soundLocation);
        return 1;
    }

    /**
     * 调试指令：列出所有已注册的BGM声音事件
     */
    private static int bgmList(CommandSourceStack source) {
        StringBuilder sb = new StringBuilder();
        sb.append("§6=== [PasterDream BGM 清单] ===\n");

        int found = 0;
        for (String biome : BGM_BIOMES) {
            String soundName = "music." + biome;
            ResourceLocation soundLocation = ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, soundName);
            var soundEvent = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(soundLocation);

            String displayName = BGM_NAMES.getOrDefault(biome, biome);
            if (soundEvent != null) {
                sb.append("§a✓ ").append(displayName).append(" §7(").append(soundLocation).append(")\n");
                found++;
            } else {
                sb.append("§c✗ ").append(displayName).append(" §7(").append(soundLocation).append(") 未注册\n");
            }
        }
        sb.append("§e已注册: ").append(found).append(" / ").append(BGM_BIOMES.length);

        source.sendSuccess(() -> Component.literal(sb.toString()), true);

        PasterDreamMod.LOGGER.info("[BGMDebug] BGM 清单: {}/{} 已注册", found, BGM_BIOMES.length);
        return 1;
    }

}