package net.pasterdream.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.configuration.PasterdreamConfigClientConfiguration;
import net.pasterdream.init.PasterdreamModItems;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Mod.EventBusSubscriber(modid = PasterdreamMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvent {
    @SubscribeEvent
    public static void clientSetupEvent(FMLClientSetupEvent event) {
        if (PasterdreamConfigClientConfiguration.LOADING_GUI_TIPS.get())
            MinecraftForge.EVENT_BUS.addListener(ClientEvent::drawScreen);
        event.enqueueWork(ClientEvent::work);
    }

    public static void drawScreen(ScreenEvent.Render.Post event) {
        drawTip(event.getGuiGraphics(), event.getScreen());
    }

    private static void work() {
        ItemPropertiesRegister();
    }

    static String Tip = "";
    static final List<String> TIPS = Lists.newArrayList(
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
    static final Set<Class<? extends Screen>> SCREENS = Sets.newHashSet(
            ConnectScreen.class,
            LevelLoadingScreen.class,
            ProgressScreen.class
    );
    private static void drawTip(GuiGraphics graphics, Screen screen) {
        if (!SCREENS.contains(screen.getClass())) {
            Tip = "";
            return;
        }

        if (Tip.isEmpty()) {
            Random random = new Random();
            Tip = TIPS.get(random.nextInt(TIPS.size()));
        }

        final Font font = Minecraft.getInstance().font;
        int height = screen.height - 20;

        graphics.drawString(font, Tip, 10, height, Color.WHITE.getRGB());
        graphics.drawString(font, "PasterDream Tip:", 10, height - 10, Color.YELLOW.getRGB());
    }

    private static void ItemPropertiesRegister() {
        ItemProperties.register(
                PasterdreamModItems.STAR_WISH_ROD.get(),
                new ResourceLocation(PasterdreamMod.MODID, "cast"),
                (itemStack, clientWorld, livingEntity, ic) -> {
                    if (itemStack.getOrCreateTag().getBoolean("cast")) return 1.0F;
                    else return 0.0F;
                }
        );
    }
}
