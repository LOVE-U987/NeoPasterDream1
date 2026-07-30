package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.PasterDreamMod;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * playerAnimator 姿势层注册与闪避姿势播放（原版 SetupAnimations + EvasionAnimation）。
 * <p>
 * 资源：{@code assets/pasterdream/player_animation/{evasion,none}.json}。
 * 触发：服务端下发 {@link com.pasterdream.pasterdreammod.network.EvasionPosePayload} 后，
 * {@link #startEvasionPose()} 写入本地标志，由客户端 tick 播放约 28 tick 后切回 none。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public final class PDPlayerAnimation {

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

    /** 本地是否正在播放闪避姿势 */
    private static boolean evasionActive;

    /** 本地闪避姿势已进行 tick 数 */
    private static int evasionTick;

    private PDPlayerAnimation() {
    }

    /**
     * 客户端初始化：注册玩家姿势层工厂（原版 SetupAnimationsProcedure.onClientSetup）。
     *
     * @param event 客户端 setup
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                PLAYER_ANIMATION_ID, 1000, player -> new ModifierLayer<>());
    }

    /**
     * 由 S2C {@code EvasionPosePayload} 调用：开始本地闪避姿势序列。
     */
    public static void startEvasionPose() {
        evasionActive = true;
        evasionTick = 0;
    }

    /**
     * 客户端 tick 末：驱动闪避姿势起停（原版 EvasionAnimationProcedure.onPlayerTick）。
     *
     * @param event 客户端 tick
     */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
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
