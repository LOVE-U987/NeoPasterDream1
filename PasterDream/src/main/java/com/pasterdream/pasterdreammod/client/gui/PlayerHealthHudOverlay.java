package com.pasterdream.pasterdreammod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.config.PDClientConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

/**
 * 帕斯特之梦主题血条 HUD 叠加层
 * <p>
 * 移植自原版 {@code PlayerHealthHUD.java}（IGuiOverlay → 1.21.1 {@link LayeredDraw.Layer}），
 * 纹理 {@code textures/screens/paster_widgets.png}（256×256）、坐标与 UV 参数与原版一致：
 * <ul>
 *   <li>锚点：x = 宽/2 - 91，y = 高 - 40（与原版饥饿/血条同排）</li>
 *   <li>底框：UV(0,0) 尺寸 85×10</li>
 *   <li>填充：绘于 (x+1, y+2)，UV x = 1（受击闪烁时 85），
 *       UV y = 10 + 6×状态色（普通 0 / 中毒 1 / 凋零 2），
 *       宽 = 83 × 当前生命/生命上限，高 6</li>
 *   <li>数值文本：「当前生命+伤害吸收/生命上限」去除 ".0" 后缀，居中绘制，
 *       有伤害吸收时黄色否则白色</li>
 *   <li>受击闪烁：无敌帧内生命变化时启动 10/20 gui-tick 计时，每 3 tick 交替高亮</li>
 * </ul>
 * 显示条件：客户端配置 {@code paster health hud} 开启、未隐藏 GUI、
 * 相机实体为玩家且处于可受伤模式（对应原版 shouldDrawSurvivalElements）。
 * 渲染后 {@code Gui.leftHeight += 11} 抬升左侧后续元素（与原版一致）；
 * 原版血条的隐藏由 {@link PDHudEvents} 取消 vanilla player_health 层实现
 * （对应原版 PDHUDEvent 取消 VanillaGuiOverlay.PLAYER_HEALTH）。
 */
public class PlayerHealthHudOverlay implements LayeredDraw.Layer {

    /** 主题血条纹理（自原版 assets 复制，256×256） */
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(
            PasterDreamMod.MOD_ID, "textures/screens/paster_widgets.png");

    /** 文本颜色：白（无伤害吸收，等价原版 java.awt.Color.WHITE.getRGB()） */
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    /** 文本颜色：黄（有伤害吸收，等价原版 java.awt.Color.YELLOW.getRGB()） */
    private static final int COLOR_YELLOW = 0xFFFFFF00;

    /** 上一帧生命值（受击闪烁判定用，与原版实例字段一致） */
    private double playerHealth = 0;
    /** 受击闪烁截止 gui-tick 计数（与原版实例字段一致） */
    private long healthUpdateCounter = 0;

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (!PDClientConfig.PASTER_HEALTH_HUD.get()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui) {
            return;
        }
        // 对应原版 forgeGui.shouldDrawSurvivalElements()：可受伤模式 + 相机实体为玩家
        if (mc.gameMode == null || !mc.gameMode.canHurtPlayer()
                || !(mc.getCameraEntity() instanceof Player player)) {
            return;
        }

        Font font = mc.font;
        int w = guiGraphics.guiWidth();
        int h = guiGraphics.guiHeight();

        int health = Mth.ceil(player.getHealth());
        float playerMaxHealth = player.getMaxHealth();
        float playerOtherHealth = player.getAbsorptionAmount();

        mc.getProfiler().push("pd_health");
        RenderSystem.enableBlend();
        {
            // 受击闪烁：无敌帧内生命变化 → 启动 20（掉血）/10（回血）gui-tick 闪烁计时
            int updateCounter = mc.gui.getGuiTicks();
            boolean highlight = healthUpdateCounter > (long) updateCounter
                    && (healthUpdateCounter - (long) updateCounter) / 3 % 2 == 1;

            if (player.invulnerableTime > 0 && health != playerHealth) {
                healthUpdateCounter = updateCounter + (health < playerHealth ? 20 : 10);
            }

            // 状态色行偏移：中毒 1 / 凋零 2 / 普通 0
            int flagColor = player.hasEffect(MobEffects.POISON) ? 1
                    : player.hasEffect(MobEffects.WITHER) ? 2 : 0;

            playerHealth = health;
            int startCount = highlight ? 84 : 0;
            int x = w / 2 - 91;
            int y = h - 40;

            // 底框与血量填充
            guiGraphics.blit(ICON, x, y, 0, 0, 85, 10);
            guiGraphics.blit(ICON, x + 1, y + 2, startCount + 1, 10 + 6 * flagColor,
                    (int) (83 * (health / playerMaxHealth)), 6);

            // 数值文本（与原版一致：格式化后去除 ".0"）
            String text = String.format("%s/%s", health + playerOtherHealth, playerMaxHealth)
                    .replace(".0", "");
            guiGraphics.drawString(font, text, x + ((85 / 2) - (font.width(text) / 2)), y,
                    playerOtherHealth > 0.0F ? COLOR_YELLOW : COLOR_WHITE);
        }
        // 抬升左侧 HUD 堆叠高度（原版 forgeGui.leftHeight += 11）
        mc.gui.leftHeight += 11;

        RenderSystem.disableBlend();
        mc.getProfiler().pop();
    }
}
