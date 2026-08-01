package com.pasterdream.pasterdreammod.api.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 结构成书本地化器 (Structure Written Book Localizer)
 * <p>
 * 将结构 NBT 预置的中文 {@code written_book} 重建为「可翻译组件」形式：
 * 书页使用 {@code Component.translatable}，由 {@code book.pasterdream.<slug>.page.N}
 * 语言键驱动，随游戏语言在中文/英文间自动切换；物品显示名同样本地化。
 * <p>
 * 说明：成书标题字段（{@link WrittenBookContent#title()}）为纯字符串，无法翻译，
 * 故通过 {@link DataComponents#CUSTOM_NAME} 本地化物品栏名称；打开书的 GUI 标题
 * 仍显示原始标题字符串。
 */
public final class BookLocalization {

    /** 中文书名 → 语言键前缀（完整键为 book.pasterdream.<slug>.* ） */
    private static final Map<String, String> TITLE_TO_SLUG = new HashMap<>();

    static {
        TITLE_TO_SLUG.put("气泡生态球", "bubble_ecosphere");
        TITLE_TO_SLUG.put("染梦水晶球", "dream_crystal_ball");
        TITLE_TO_SLUG.put("探求秘辛", "seeking_secrets");
        TITLE_TO_SLUG.put("染梦教堂日记 其一", "dream_church_1");
        TITLE_TO_SLUG.put("染梦讲堂 其二", "dream_church_2");
        TITLE_TO_SLUG.put("染梦教堂 其三", "dream_church_3");
        TITLE_TO_SLUG.put("梦境漂泊", "dream_drifting");
        TITLE_TO_SLUG.put("祈愿树", "wishing_tree");
        TITLE_TO_SLUG.put("染梦游记 其三", "dream_travels_3");
        TITLE_TO_SLUG.put("染梦实验室", "dream_laboratory");
        TITLE_TO_SLUG.put("温暖的“寒风”", "warm_cold_wind");
        TITLE_TO_SLUG.put("代达罗斯之翼与浮空岛", "daedalus_wings");
        TITLE_TO_SLUG.put("染梦游记 其一", "dream_travels_1");
        TITLE_TO_SLUG.put("染梦游记 其二", "dream_travels_2");
        TITLE_TO_SLUG.put("染梦世界树", "dream_worldtree");
        TITLE_TO_SLUG.put("落叶归根 裂荚归冠", "falling_leaves");
        TITLE_TO_SLUG.put("花园解密", "garden_decryption");
        TITLE_TO_SLUG.put("破风的骑士", "windbreaking_knight");
        TITLE_TO_SLUG.put("融梦涌泉井", "meltdream_well");
        TITLE_TO_SLUG.put("粉顶蘑菇屋", "pink_mushroom_house");
        TITLE_TO_SLUG.put("被阴影浸染的字迹潦草的笔记", "shadow_hand_note");
        TITLE_TO_SLUG.put("关于黑暗之手的随笔", "shadow_hand_essay");
        TITLE_TO_SLUG.put("与黑色双手的决战和败北", "shadow_hand_defeat");
        TITLE_TO_SLUG.put("研究笔记-黑金属", "black_metal_research");
        TITLE_TO_SLUG.put("阴影小木屋", "shadow_cabin");
        TITLE_TO_SLUG.put("灯影游记 其二", "shadow_travels_2");
        TITLE_TO_SLUG.put("黑暗之地", "land_of_darkness");
        TITLE_TO_SLUG.put("阴影地下工作室", "shadow_workshop");
        TITLE_TO_SLUG.put("浮空岛日记", "floating_island_diary");
        TITLE_TO_SLUG.put("来往于梦", "between_dreams");
        TITLE_TO_SLUG.put("清晨的新风", "morning_breeze");
    }

    private BookLocalization() {
    }

    /**
     * 将 {@code written_book} 的书页重建为可翻译组件；未知书原样保留。
     *
     * @param stack 目标成书（修改其书页与显示名）
     */
    public static void localize(ItemStack stack) {
        if (!stack.is(Items.WRITTEN_BOOK)) {
            return;
        }
        WrittenBookContent content = stack.get(DataComponents.WRITTEN_BOOK_CONTENT);
        if (content == null) {
            return;
        }
        String slug = TITLE_TO_SLUG.get(content.title().raw());
        if (slug == null) {
            return;
        }
        int pageCount = content.pages().size();
        List<Filterable<Component>> pages = new ArrayList<>(pageCount);
        for (int i = 0; i < pageCount; i++) {
            pages.add(Filterable.passThrough(
                    Component.translatable("book.pasterdream." + slug + ".page." + i)));
        }
        // resolved=false：客户端打开书时按语言键重新渲染
        stack.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(
                content.title(), content.author(), content.generation(), pages, false));
        // 物品栏/手持名称本地化
        stack.set(DataComponents.CUSTOM_NAME,
                Component.translatable("book.pasterdream." + slug + ".title"));
    }
}
