package com.pasterdream.pasterdreammod.client.screen;

import com.pasterdream.pasterdreammod.network.SaveUpgradeConfirmPayload;
import com.pasterdream.pasterdreammod.network.SaveUpgradePromptPayload;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

import java.io.File;

/**
 * 旧存档备份/更新提示界面（仅客户端）。
 * <p>
 * 按钮：
 * <ul>
 *   <li>已备份并继续：发送 C2S 确认包，服务端写入 ID schema 标记。</li>
 *   <li>打开备份目录：在系统文件管理器中打开自动备份目录（单机）。</li>
 *   <li>退出世界：断开当前连接。</li>
 * </ul>
 */
public class SaveUpgradePromptScreen extends Screen {

    /** 行高 */
    private static final int LINE_HEIGHT = 12;

    /** 提示数据 */
    private final SaveUpgradePromptPayload payload;

    /**
     * 构造提示界面。
     *
     * @param payload 提示数据
     */
    public SaveUpgradePromptScreen(SaveUpgradePromptPayload payload) {
        super(Component.translatable("pasterdream.save_compat.screen.title"));
        this.payload = payload;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int buttonY = this.height / 2 + 48;
        int halfWidth = 150;
        int gap = 10;

        this.addRenderableWidget(Button.builder(
                        Component.translatable("pasterdream.save_compat.screen.confirm"),
                        button -> {
                            PacketDistributor.sendToServer(new SaveUpgradeConfirmPayload());
                            this.onClose();
                        })
                .bounds(centerX - halfWidth - gap / 2, buttonY, halfWidth, 20)
                .build());

        this.addRenderableWidget(Button.builder(
                        Component.translatable("pasterdream.save_compat.screen.later"),
                        button -> this.onClose())
                .bounds(centerX + gap / 2, buttonY, halfWidth, 20)
                .build());

        Button openButton = Button.builder(
                        Component.translatable("pasterdream.save_compat.screen.open_backup"),
                        button -> openBackupDirectory())
                .bounds(centerX - halfWidth - gap / 2, buttonY + 24, halfWidth, 20)
                .build();
        openButton.active = !this.payload.backupPath().isEmpty() && !this.payload.backupFailed();
        this.addRenderableWidget(openButton);

        this.addRenderableWidget(Button.builder(
                        Component.translatable("pasterdream.save_compat.screen.disconnect"),
                        button -> this.disconnectWorld())
                .bounds(centerX + gap / 2, buttonY + 24, halfWidth, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;
        int y = this.height / 2 - 70;

        guiGraphics.drawCenteredString(this.font, this.title, centerX, y, 0xFFD700);
        y += LINE_HEIGHT * 2;

        guiGraphics.drawCenteredString(this.font,
                Component.translatable("pasterdream.save_compat.screen.world", this.payload.worldName()),
                centerX, y, 0xFFFFFF);
        y += LINE_HEIGHT;
        guiGraphics.drawCenteredString(this.font,
                Component.translatable("pasterdream.save_compat.screen.advice"),
                centerX, y, 0xFFFFFF);
        y += LINE_HEIGHT;

        Component backupLine;
        if (this.payload.backupFailed()) {
            backupLine = Component.translatable("pasterdream.save_compat.screen.backup_failed");
        } else if (!this.payload.backupPath().isEmpty()) {
            backupLine = Component.translatable("pasterdream.save_compat.screen.backup_path", this.payload.backupPath());
        } else {
            backupLine = Component.translatable("pasterdream.save_compat.screen.backup_none");
        }
        guiGraphics.drawCenteredString(this.font, backupLine, centerX, y, 0xFFFFFF);
        y += LINE_HEIGHT;
        guiGraphics.drawCenteredString(this.font,
                Component.translatable("pasterdream.save_compat.screen.note"),
                centerX, y, 0xAAAAAA);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    /**
     * 在系统文件管理器中打开备份目录。
     */
    private void openBackupDirectory() {
        if (this.payload.backupPath().isEmpty() || this.payload.backupFailed()) {
            return;
        }
        Util.getPlatform().openFile(new File(this.payload.backupPath()));
    }

    /**
     * 断开当前世界连接。
     */
    private void disconnectWorld() {
        if (this.minecraft != null && this.minecraft.getConnection() != null) {
            this.minecraft.getConnection().getConnection()
                    .disconnect(Component.translatable("pasterdream.save_compat.screen.disconnect"));
        }
    }
}
