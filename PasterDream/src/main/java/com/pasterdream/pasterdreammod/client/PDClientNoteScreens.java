package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.client.screen.DreamnoteScreen;
import com.pasterdream.pasterdreammod.client.screen.DreamseekerNotesScreen;
import com.pasterdream.pasterdreammod.network.OpenNotePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/**
 * 笔记界面的客户端落地桥（仅客户端加载）。
 * <p>
 * {@code PDNetwork} 经反射转发至此，避免 common 类静态引用 {@link Screen}/{@link Minecraft}。
 */
public final class PDClientNoteScreens {

    private PDClientNoteScreens() {
    }

    /**
     * 打开笔记阅读/编辑界面。
     *
     * @param payload 打开包
     */
    public static void openNote(OpenNotePayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen = payload.editable() ? new DreamseekerNotesScreen(payload) : new DreamnoteScreen(payload);
        minecraft.setScreen(screen);
    }
}
