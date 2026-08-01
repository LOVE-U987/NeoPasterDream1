package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
import dev.kosmx.playerAnim.api.IPlayable;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.IActualAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * playerAnimator 姿势层注册与闪避姿势播放（原版 SetupAnimations + EvasionAnimation）。
 * <p>
 * <b>可选依赖</b>：{@code playeranimator} 在 mods.toml 中为 optional。
 * 本类<strong>不得</strong>使用 {@code @EventBusSubscriber} 无条件订阅——否则在未安装
 * Player Animator 时类加载会解析 {@code IAnimation} 等符号并触发
 * {@link NoClassDefFoundError}。
 * 仅由 {@link #bootstrapIfPresent()} 在模组在场时手动注册。
 * </p>
 * <p>
 * 资源：{@code assets/pasterdream/player_animations/{evasion,none}.json}
 * （Player Animator 2.x 目录为 {@code player_animations}）。
 * 触发：S2C {@link com.pasterdream.pasterdreammod.network.EvasionPosePayload} →
 * {@link #startEvasionPose()} → 客户端 tick 约 28t 后切回 none。
 * </p>
 */
public final class PDPlayerAnimation {

    /** mods.toml / Maven 中的 modId */
    public static final String PLAYER_ANIMATOR_MOD_ID = "playeranimator";

    /** 与原版一致的关联数据键 / 工厂 id */
    public static final ResourceLocation PLAYER_ANIMATION_ID =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "player_animation");

    /** 闪避姿势资源名（path，非文件名） */
    public static final ResourceLocation EVASION_ANIM =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "evasion");

    /** 空姿势（结束闪避时复位） */
    public static final ResourceLocation NONE_ANIM =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "none");

    /** 原版 EvasionAnimationProcedure：tick ≥ 28 时结束 */
    private static final int EVASION_DURATION_TICKS = 28;

    /** 是否已完成 bootstrap（避免重复 registerFactory） */
    private static boolean bootstrapped;

    /** 本地是否正在播放闪避姿势 */
    private static boolean evasionActive;

    /** 本地闪避姿势已进行 tick 数 */
    private static int evasionTick;

    private PDPlayerAnimation() {
    }

    /**
     * 运行时是否可用 Player Animator（未安装时所有入口应 no-op）。
     *
     * @return 模组在场则为 true
     */
    public static boolean isAvailable() {
        return ModList.get().isLoaded(PLAYER_ANIMATOR_MOD_ID);
    }

    /**
     * 若 Player Animator 已安装：注册姿势层工厂 + 客户端 tick。
     * 在 {@link ClientSetup} 的 {@code FMLClientSetupEvent} 中调用（仅客户端）。
     * 未安装时立即返回，且<strong>不会</strong>加载本类中的 playerAnim 符号以外的路径——
     * 调用方须先判断 {@link #isAvailable()} 再引用本方法，或接受本方法入口的 isLoaded 短路
     * （本方法被调用时本类已加载，故仅应在 isAvailable 为 true 时从外部调用）。
     */
    public static void bootstrapIfPresent() {
        if (bootstrapped || !isAvailable()) {
            return;
        }
        bootstrapped = true;
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                PLAYER_ANIMATION_ID, 1000, player -> new ModifierLayer<>());
        NeoForge.EVENT_BUS.addListener(PDPlayerAnimation::onClientTick);
        PDDebugLogger.mainDebug("[PDPlayerAnimation] playeranimator 在场，已注册姿势层与 tick");
    }

    /**
     * 由 S2C {@code EvasionPosePayload} 调用：开始本地闪避姿势序列。
     * 未安装 playeranimator 时 no-op。
     */
    public static void startEvasionPose() {
        if (!isAvailable()) {
            return;
        }
        evasionActive = true;
        evasionTick = 0;
    }

    /**
     * 客户端 tick 末：驱动闪避姿势起停（原版 EvasionAnimationProcedure.onPlayerTick）。
     *
     * @param event 客户端 tick
     */
    private static void onClientTick(ClientTickEvent.Post event) {
        if (!evasionActive) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.isPaused()) {
            return;
        }
        if (evasionTick <= 0) {
            play(player, EVASION_ANIM);
        }
        if (evasionTick >= EVASION_DURATION_TICKS) {
            play(player, NONE_ANIM);
            evasionActive = false;
            evasionTick = 0;
        } else {
            evasionTick++;
        }
    }

    /**
     * 在本地玩家的 ModifierLayer 上设置姿势。
     *
     * @param player 客户端玩家
     * @param animId 姿势资源 id
     */
    @SuppressWarnings("unchecked")
    private static void play(AbstractClientPlayer player, ResourceLocation animId) {
        IAnimation layer = PlayerAnimationAccess.getPlayerAssociatedData(player).get(PLAYER_ANIMATION_ID);
        if (!(layer instanceof ModifierLayer<?> raw)) {
            return;
        }
        ModifierLayer<IAnimation> animation = (ModifierLayer<IAnimation>) raw;
        IPlayable playable = PlayerAnimationRegistry.getAnimation(animId);
        if (playable == null) {
            return;
        }
        IActualAnimation<?> playing = playable.playAnimation();
        animation.setAnimation(playing);
    }
}
