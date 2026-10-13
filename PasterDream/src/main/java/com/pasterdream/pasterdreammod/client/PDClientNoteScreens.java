package com.pasterdream.pasterdreammod.client;

import com.pasterdream.pasterdreammod.client.screen.DreamnoteScreen;
import com.pasterdream.pasterdreammod.client.screen.DreamseekerNotesScreen;
import com.pasterdream.pasterdreammod.network.OpenNotePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.Locale;

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
     * <p>
     * 路由：固定笔记（{@code noteId>=0}）→ 阅读屏；玩家笔记按当前语言是否已署名 → 阅读屏 / 编辑屏。
     *
     * @param payload 打开包
     */
    public static void openNote(OpenNotePayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        Screen screen;
        if (payload.noteId() >= 0) {
            screen = new DreamnoteScreen(payload);
        } else {
            boolean signed = "zh_cn".equals(currentLanguage()) ? payload.signedZh() : payload.signedEn();
            screen = signed ? new DreamnoteScreen(payload) : new DreamseekerNotesScreen(payload);
        }
        minecraft.setScreen(screen);
    }

    /**
     * 当前客户端语言键（归一为 {@code zh_cn} / {@code en_us}）。
     * <p>
     * 注意：common 侧 {@code NoteLangBridge} 经反射按字符串调用本方法，改名时须同步该桥。
     *
     * @return 语言键
     */
    public static String currentLanguage() {
        String code = Minecraft.getInstance().getLanguageManager().getSelected();
        return code != null && code.toLowerCase(Locale.ROOT).startsWith("zh") ? "zh_cn" : "en_us";
    }
}
