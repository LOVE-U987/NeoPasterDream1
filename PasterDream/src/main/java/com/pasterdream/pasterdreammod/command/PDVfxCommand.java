package com.pasterdream.pasterdreammod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pasterdream.pasterdreammod.api.effect.atmosphere.AtmosphereEffectAPI;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CameraPos;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CurveType;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CutsceneAPI;
import com.pasterdream.pasterdreammod.api.effect.cutscene.CutsceneData;
import com.pasterdream.pasterdreammod.api.effect.cutscene.EasingType;
import com.pasterdream.pasterdreammod.api.effect.ghost.GhostEffectAPI;
import com.pasterdream.pasterdreammod.api.effect.impact.ImpactFrame;
import com.pasterdream.pasterdreammod.api.effect.impact.ImpactFrameAPI;
import com.pasterdream.pasterdreammod.api.effect.particle.ParticleEmitterAPI;
import com.pasterdream.pasterdreammod.api.effect.particle.ParticleEmitterData;
import com.pasterdream.pasterdreammod.api.effect.particle.processors.CircleSpawnProcessor;
import com.pasterdream.pasterdreammod.api.effect.screen.ScreenEffectAPI;
import com.pasterdream.pasterdreammod.api.effect.screen.instances.ScreenColorData;
import com.pasterdream.pasterdreammod.api.effect.shake.ScreenShakeAPI;
import com.pasterdream.pasterdreammod.api.effect.shake.ScreenShakeData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * 特效调试命令 —— 手动触发各特效系统，便于运行验证
 * <p>
 * 用法：
 * <ul>
 *   <li>{@code /pasterdream vfx impact} —— 打击帧（灰闪）</li>
 *   <li>{@code /pasterdream vfx screen} —— 屏幕特效（纯色渐入渐出）</li>
 *   <li>{@code /pasterdream vfx particle} —— 粒子发射器（灵魂粒子圆喷）</li>
 *   <li>{@code /pasterdream vfx cutscene} —— 过场动画（相机环绕玩家）</li>
 * </ul>
 * 由 {@code PasterDreamMod} 构造器 {@code NeoForge.EVENT_BUS.addListener(PDVfxCommand::register)}
 * 注册到游戏总线。
 */
public final class PDVfxCommand {

    private PDVfxCommand() {
    }

    /**
     * 注册命令
     *
     * @param event 命令注册事件
     */
    public static void register(net.neoforged.neoforge.event.RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("pasterdream")
                .then(Commands.literal("vfx")
                        .then(Commands.literal("impact").executes(ctx -> runImpact(ctx.getSource())))
                        .then(Commands.literal("screen").executes(ctx -> runScreen(ctx.getSource())))
                        .then(Commands.literal("particle").executes(ctx -> runParticle(ctx.getSource())))
                        .then(Commands.literal("cutscene").executes(ctx -> runCutscene(ctx.getSource())))
                        .then(Commands.literal("ghost").executes(ctx -> runGhost(ctx.getSource())))
                        .then(Commands.literal("atmosphere").executes(ctx -> runAtmosphere(ctx.getSource())))
                        .then(Commands.literal("shake").executes(ctx -> runShake(ctx.getSource())))
                ));
    }

    /** 打击帧测试 */
    private static int runImpact(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (player.level() instanceof ServerLevel level) {
            ImpactFrameAPI.sendImpactFrames(level, player.position(), 80.0,
                    new ImpactFrame(0.45f, 0.03f, 6, false));
            source.sendSuccess(() -> Component.literal("[VFX] 已触发打击帧"), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    /** 屏幕特效测试 */
    private static int runScreen(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (player.level() instanceof ServerLevel level) {
            ScreenEffectAPI.sendScreenEffectToPlayers(level, player.position(), 80.0,
                    ScreenColorData.TYPE, new ScreenColorData(0x55000000), 10, 30, 20);
            source.sendSuccess(() -> Component.literal("[VFX] 已触发屏幕特效（纯色）"), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    /** 粒子发射器测试 */
    private static int runParticle(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (player.level() instanceof ServerLevel level) {
            ParticleEmitterAPI.spawn(level, player.position(), 80.0,
                    ParticleEmitterData.builder(ParticleTypes.SOUL)
                            .position(player.position().add(0, 1.5, 0))
                            .lifetime(40)
                            .particlesPerTick(8)
                            .processor(new CircleSpawnProcessor(2.0f))
                            .build());
            source.sendSuccess(() -> Component.literal("[VFX] 已触发粒子发射器"), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    /** 过场动画测试 */
    private static int runCutscene(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (player.level() instanceof ServerLevel level) {
            Vec3 center = player.position();
            CutsceneAPI.startCutsceneForPlayers(level, center, 80.0,
                    CutsceneData.create()
                            .time(100)
                            .moveCurveType(CurveType.CATMULLROM)
                            .timeEasing(EasingType.SMOOTHSTEP)
                            .addCameraPos(CameraPos.of(center.add(0, 3, 6), center))    // 前
                            .addCameraPos(CameraPos.of(center.add(6, 8, 0), center))    // 右
                            .addCameraPos(CameraPos.of(center.add(0, 3, -6), center))   // 后
                            .addCameraPos(CameraPos.of(center.add(-6, 8, 0), center))); // 左
            source.sendSuccess(() -> Component.literal("[VFX] 已触发过场动画"), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    /** 残影测试（玩家自身残影拖尾） */
    private static int runGhost(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (player.level() instanceof ServerLevel level) {
            GhostEffectAPI.startGhostTrail(level, player.position(), 80.0,
                    player.getId(), 40, 50);
            source.sendSuccess(() -> Component.literal("[VFX] 已触发残影拖尾"), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    /** 雾色/暗化测试（血色雾） */
    private static int runAtmosphere(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (player.level() instanceof ServerLevel level) {
            AtmosphereEffectAPI.bloodFog(level, player.position(), 80.0, 0.9f, 120);
            source.sendSuccess(() -> Component.literal("[VFX] 已触发血色雾氛围"), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    /** 屏幕晃动测试 */
    private static int runShake(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        if (player.level() instanceof ServerLevel level) {
            ScreenShakeAPI.sendShake(level, player.position(), 80.0,
                    ScreenShakeData.builder()
                            .inTime(2).stayTime(8).outTime(14)
                            .amplitude(0.15f).frequency(1.0f)
                            .build());
            source.sendSuccess(() -> Component.literal("[VFX] 已触发屏幕晃动"), true);
        }
        return Command.SINGLE_SUCCESS;
    }
}
