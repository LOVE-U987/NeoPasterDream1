package com.pasterdream.pasterdreammod.client;

import com.google.common.collect.Lists;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.config.PDClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 加载界面 tips —— 对齐原版 {@code ClientEvent#drawScreen}。
 * <p>
 * 在连接服务器 / 加载世界 / 进度界面底部绘制一条随机帕斯特之梦小贴士，
 * 由配置 {@code loading gui tips}（默认 true）控制开关。
 * <p>
 * 独立 {@link EventBusSubscriber}，不修改共享的 {@code PDClientEvents.java}。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID, value = Dist.CLIENT)
public final class PDLoadingTipsClientEvents {

    private PDLoadingTipsClientEvents() {
    }

    /** 当前显示的 tips 文案（跨界面缓存，避免每帧重抽） */
    private static String tip = "";

    /** 帕斯特之梦小贴士文案（沿用原版 ClientEvent 的 TIPS 列表） */
    private static final List<String> TIPS = Lists.newArrayList(
            "默认按[C]键使用瞬身术！",
            "瞬身术有一段极短的回避无敌帧，利用好它！",
            "融梦能量条和精神值条的位置可以在配置文件调整！",
            "厚翅甲虫会后空翻，前提是你得给它取个特别的名字！",
            "幸运值是真实有用的！",
            "海岸会刷新一些渔民小屋",
            "在特定的群系和环境下，可以在海洋里钓出来一些深海的宝藏！",
            "遗迹可不会把箱子摆到特别明显的位置...你应该仔细寻找！",
            "有什么不懂的就去查MC百科吧！",
            "我会一直更新这个模组！直到...",
            "反馈模组bug请给开发者看崩溃/游戏日志！",
            "拜托朋友，开心起来  你真的很棒！",
            "珍惜那些爱你的人！",
            "去试试魔法金属吧！",
            "去试试极光幽境吧！",
            "咩咩狼的尾巴有多长？",
            "想来一起开发帕斯特之梦吗？",
            "想不想在帕斯特里留下自己的遗迹建筑呢？来试试看吧！",
            "琴雨梦是我的赛博亲女儿！",
            "琴雨梦敲可爱！",
            "幼幼紫也敲可爱！",
            "生日是2002/11/28！"
    );

    /** 显示 tips 的屏幕类型集合（与原版一致：连接/加载/进度界面） */
    private static final Set<Class<? extends Screen>> SCREENS = Set.of(
            ConnectScreen.class,
            LevelLoadingScreen.class,
            ProgressScreen.class
    );

    /**
     * 屏幕渲染后绘制 tips（仅当配置开启且在目标屏幕时）。
     *
     * @param event 屏幕渲染事件
     */
    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        // 配置关闭时不注册任何绘制
        if (!Boolean.TRUE.equals(PDClientConfig.LOADING_GUI_TIPS.get())) {
            tip = "";
            return;
        }
        Screen screen = event.getScreen();
        if (!SCREENS.contains(screen.getClass())) {
            tip = "";
            return;
        }
        if (tip.isEmpty()) {
            tip = TIPS.get(new Random().nextInt(TIPS.size()));
        }
        GuiGraphics graphics = event.getGuiGraphics();
        Font font = Minecraft.getInstance().font;
        int height = screen.height - 20;
        graphics.drawString(font, tip, 10, height, 0xFFFFFFFF);
    }
}
