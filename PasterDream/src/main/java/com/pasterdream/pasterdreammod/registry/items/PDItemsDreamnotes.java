package com.pasterdream.pasterdreammod.registry.items;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.item.DreamnotesItem;
import com.pasterdream.pasterdreammod.registry.PDItems;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;

/**
 * 寻梦者笔记物品分区注册 (dreamnotes_0..14)。
 * <p>
 * 条目写入共享 {@link PDItems#ITEMS}（与 {@code PDItemsMaterials} 等分区相同模式），
 * 本类由 {@link EventBusSubscriber} 在 MOD 总线扫描时加载，确保 RegisterEvent 前
 * 完成 {@code DeferredItem} 填充。合并时 PDItems re-export / 创造栏挂载见
 * worktree 根 {@code dreamnotes_registry_staging.md}。
 * <p>
 * <b>重要</b>：仅有源文件不够，必须被类加载；本类的 {@code @EventBusSubscriber}
 * 与 {@link #bootstrap()} 即为此准备。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID)
public final class PDItemsDreamnotes {

    public static final DeferredItem<DreamnotesItem> DREAMNOTES_0 = reg(0, List.of(
            "§6开发者名单", "§7展开以阅读笔记内容"));
    public static final DeferredItem<DreamnotesItem> DREAMNOTES_1 = reg(1, List.of(
            "§e染梦裂隙", "§7展开以阅读笔记内容"));
    public static final DeferredItem<DreamnotesItem> DREAMNOTES_2 = reg(2, List.of(
            "§e染梦世界", "§7展开以阅读笔记内容"));
    public static final DeferredItem<DreamnotesItem> DREAMNOTES_3 = reg(3, List.of(
            "§e粉红史莱姆", "§7展开以阅读笔记内容"));
    public static final DeferredItem<DreamnotesItem> DREAMNOTES_4 = reg(4, List.of(
            "§e苍白雪莲", "§7展开以阅读笔记内容"));
    public static final DeferredItem<DreamnotesItem> DREAMNOTES_5 = reg(5, List.of(
            "§e苍白骨针", "§7展开以阅读笔记内容"));
    public static final DeferredItem<DreamnotesItem> DREAMNOTES_6 = reg(6, List.of(
            "§e衍梦肥泥", "§7展开以阅读笔记内容"));
    public static final DeferredItem<DreamnotesItem> DREAMNOTES_7 = reg(7, List.of(
            "§e蓄梦池", "§7展开以阅读笔记内容"));
    public static final DeferredItem<DreamnotesItem> DREAMNOTES_8 = reg(8, List.of(
            "§e阴影中的潜藏者", "§7展开以阅读笔记内容"));
    public static final DeferredItem<DreamnotesItem> DREAMNOTES_9 = reg(9, List.of(
            "§e侵染教堂", "§7展开以阅读笔记内容"));
    public static final DeferredItem<DreamnotesItem> DREAMNOTES_10 = reg(10, List.of(
            "§e沉淀阴影", "§7by §f琴雨梦", "§7展开以阅读笔记内容"));
    public static final DeferredItem<DreamnotesItem> DREAMNOTES_11 = reg(11, List.of(
            "§e阴影游记", "by 琴雨梦", "§7展开以阅读笔记内容"));
    public static final DeferredItem<DreamnotesItem> DREAMNOTES_12 = reg(12, List.of(
            "§e暗影地牢", "by 琴雨梦", "§7展开以阅读笔记内容"));
    public static final DeferredItem<DreamnotesItem> DREAMNOTES_13 = reg(13, List.of(
            "§e恐惧", "by 琴雨梦", "§7展开以阅读笔记内容"));
    public static final DeferredItem<DreamnotesItem> DREAMNOTES_14 = reg(14, List.of(
            "§e无翼鸟也有展翅的梦", "by 琴雨梦", "§7展开以阅读笔记内容"));

    private static DeferredItem<DreamnotesItem> reg(int id, List<String> tips) {
        return PDItems.ITEMS.register("dreamnotes_" + id, () -> new DreamnotesItem(id, tips));
    }

    private PDItemsDreamnotes() {
    }

    /**
     * 空监听：保证类在 common setup 前完成加载。
     *
     * @param event common setup
     */
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        PasterDreamMod.LOGGER.info("[PDItemsDreamnotes] 寻梦者笔记分区已加载 (dreamnotes_0..14)");
    }

    /**
     * 显式触发类加载（供主类 / PDItems staging 接线调用）
     */
    public static void bootstrap() {
        Object unused = DREAMNOTES_0;
    }
}
