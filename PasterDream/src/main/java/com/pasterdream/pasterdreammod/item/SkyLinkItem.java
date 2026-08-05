package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.client.sky.PlayerSkyLinkData;
import com.pasterdream.pasterdreammod.client.sky.SkyboxRenderer;
import com.pasterdream.pasterdreammod.client.sky.math.SkyPoint;
import com.pasterdream.pasterdreammod.client.sky.render.SkyGeometry;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

/**
 * 星空枕 (memento_item_08) 物品类 —— 在天空上创建"连线星体"
 * <p>
 * 仅夜晚可用（白天提示晚上再用）。操作方式：
 * <ul>
 *   <li>右键：在视线方向生成一颗连线星体（按创建顺序依次连接成开放链）</li>
 *   <li>左键：移除对准的已创建星体（{@link #tryRemoveStarAt}，由客户端事件调用）</li>
 *   <li>数量达到上限（默认 8，可配置 {@link PDCommonConfig#SKYLINK_MAX_STARS}）后不再创建</li>
 * </ul>
 * 数据仅存客户端（{@link PlayerSkyLinkData}），是纯视觉功能，不跨存档持久化。
 * <p>
 * ⚠️ 坐标换算：渲染时天空球被 PoseStack 旋转（先 X(-90°) 再 Y(skyAngle)），
 * 此处需把"世界空间视线方向"逆变换回"天空球局部坐标"再存储，
 * 否则创建出来的星体会偏离玩家实际看到的方位。
 */
public class SkyLinkItem extends Item {

    /** 取消星体的角距离阈值（天空球半径 100 下的直线距离） */
    private static final float REMOVE_THRESHOLD = 6.0F;
    /** 视线世界空间 Y 分量下限（低于此值视为看向脚下，不创建） */
    private static final float MIN_LOOK_Y = -0.35F;

    /**
     * @param properties 物品属性
     */
    public SkyLinkItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            handleSkyLink(player);
        }
        // 纯客户端视觉功能，服务端无需动作；返回 PASS 仅触发挥臂动画，不进入使用状态
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        // 使用描述 + 获取方式（宝箱开出）
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.memento_item_08.effect"));
        tooltipComponents.add(Component.translatable("tooltip.pasterdream.memento_item_08.source"));
    }

    /**
     * 客户端处理连线星体创建（右键）
     *
     * @param player 玩家
     */
    private static void handleSkyLink(Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        // 仅夜晚可用：白天提示晚上再用
        if (!SkyboxRenderer.isNight()) {
            mc.player.displayClientMessage(Component.translatable("message.pasterdream.skylink.night_only"), true);
            mc.player.playSound(SoundEvents.NOTE_BLOCK_BASS.value(), 0.6F, 0.7F);
            return;
        }
        // 同步配置上限（配置文件可能被热修改）
        PlayerSkyLinkData.MAX_STARS = Math.max(1, PDCommonConfig.SKYLINK_MAX_STARS.get());
        LocalPlayer localPlayer = mc.player;

        // 相机视线方向（世界空间）
        Vec3 look = localPlayer.getLookAngle();
        float lookX = (float) look.x;
        float lookY = (float) look.y;
        float lookZ = (float) look.z;

        // 视线过低（看向脚下）时提示并忽略
        if (lookY < MIN_LOOK_Y) {
            localPlayer.displayClientMessage(Component.translatable("message.pasterdream.skylink.look_up"), true);
            return;
        }

        // 天空旋转角（渲染时 PoseStack 先 X(-90°) 再 Y(skyAngle)，世界=局部的正变换）
        float timeOfDay = mc.level.getTimeOfDay(1.0F);
        float skyAngle = timeOfDay * 360.0F;
        float rad = (float) Math.toRadians(skyAngle);
        float cosA = Mth.cos(rad);
        float sinA = Mth.sin(rad);

        // 世界 → 天空局部（逆变换 = Y(-A)·X(90°)·look）
        // 渲染正变换: X(-90°)·Y(A)·v（矩阵右乘，先 Y 后 X）
        // 故逆变换: 先 X(90°): (a,b,c)->(a,-c,b)，再 Y(-A): (x,z)->(x·cosA-z·sinA, x·sinA+z·cosA)
        float sx = lookX * cosA - lookY * sinA;
        float sy = -lookZ;
        float sz = lookX * sinA + lookY * cosA;
        float length = Mth.sqrt(sx * sx + sy * sy + sz * sz);
        if (length < 0.001F) {
            return;
        }
        // 局部方向 → 天空球 yaw/pitch（与 SkyGeometry.point 约定一致）
        float yaw = (float) Math.atan2(sx, sz);
        float pitch = (float) Math.asin(Mth.clamp(sy / length, -1.0F, 1.0F));
        SkyPoint point = SkyGeometry.point(yaw, pitch);

        UUID uuid = localPlayer.getUUID();

        // 创建新星体（达到上限则提示）；移除已改为左键（见 {@link #tryRemoveStarAt}）
        if (PlayerSkyLinkData.addStar(uuid, point)) {
            int count = PlayerSkyLinkData.getStars(uuid).size();
            localPlayer.displayClientMessage(
                    Component.translatable("message.pasterdream.skylink.added", count, PlayerSkyLinkData.MAX_STARS), true);
            localPlayer.playSound(SoundEvents.AMETHYST_BLOCK_CHIME, 0.8F, 1.0F);
            // 首次创建星体 → 授予织星成就（客户端安全提交到服务端线程）
            SkyboxRenderer.awardClient("achievement_skylink");
        } else {
            localPlayer.displayClientMessage(
                    Component.translatable("message.pasterdream.skylink.full", PlayerSkyLinkData.MAX_STARS), true);
            localPlayer.playSound(SoundEvents.NOTE_BLOCK_BASS.value(), 0.6F, 0.7F);
        }
    }

    /**
     * 左键移除：尝试移除玩家视线方向对准的连线星体（客户端事件调用）
     * <p>
     * 与创建共用同一坐标换算；仅夜晚可用；移除成功返回 true（调用方可取消
     * 方块破坏等默认行为）。
     *
     * @return 是否移除了某颗星体
     */
    public static boolean tryRemoveStarAt() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return false;
        }
        // 仅夜晚可用：白天提示晚上再用
        if (!SkyboxRenderer.isNight()) {
            mc.player.displayClientMessage(Component.translatable("message.pasterdream.skylink.night_only"), true);
            return false;
        }
        LocalPlayer localPlayer = mc.player;
        // 相机视线方向（世界空间）
        Vec3 look = localPlayer.getLookAngle();
        float lookX = (float) look.x;
        float lookY = (float) look.y;
        float lookZ = (float) look.z;
        // 天空旋转角（与创建/渲染约定一致）
        float timeOfDay = mc.level.getTimeOfDay(1.0F);
        float rad = (float) Math.toRadians(timeOfDay * 360.0F);
        float cosA = Mth.cos(rad);
        float sinA = Mth.sin(rad);
        float sx = lookX * cosA - lookY * sinA;
        float sy = -lookZ;
        float sz = lookX * sinA + lookY * cosA;
        float length = Mth.sqrt(sx * sx + sy * sy + sz * sz);
        if (length < 0.001F) {
            return false;
        }
        float yaw = (float) Math.atan2(sx, sz);
        float pitch = (float) Math.asin(Mth.clamp(sy / length, -1.0F, 1.0F));
        SkyPoint point = SkyGeometry.point(yaw, pitch);
        UUID uuid = localPlayer.getUUID();
        if (PlayerSkyLinkData.removeStarNear(uuid, point, REMOVE_THRESHOLD)) {
            localPlayer.displayClientMessage(Component.translatable("message.pasterdream.skylink.removed"), true);
            localPlayer.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.6F, 1.2F);
            return true;
        }
        return false;
    }
}
