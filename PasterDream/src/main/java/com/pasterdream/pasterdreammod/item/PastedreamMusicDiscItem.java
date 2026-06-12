package com.pasterdream.pasterdreammod.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

/**
 * PasterDream 通用音乐唱片物品类
 * <p>
 * 适配 NeoForge 1.21.1（Minecraft 1.21）的新唱片系统。
 * <p>
 * 在 1.21 中 {@code RecordItem} 已被移除，唱片改用数据驱动方式：
 * <ul>
 *   <li>物品通过 {@link Item.Properties#jukeboxPlayable} 组件关联唱片歌曲</li>
 *   <li>歌曲定义在 {@code data/modid/jukebox_song/} 目录下的 JSON 中</li>
 *   <li>音乐 SoundEvent 由 sound_event 字段指向声音定义</li>
 * </ul>
 * <p>
 * 本类封装了注册唱片物品所需的基本属性（堆叠 1、稀有度 RARE），
 * 并提供从注册名自动构建 {@link ResourceKey<JukeboxSong>} 的能力。
 * 按住 Shift 可展开查看曲名、作者等元数据信息。
 */
public class PastedreamMusicDiscItem extends Item {

    private static final Properties DEFAULT_PROPERTIES = new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE);

    /** 曲名 */
    @Nullable
    private final String trackTitle;
    /** 作者 */
    @Nullable
    private final String trackArtist;
    /** 专辑 */
    @Nullable
    private final String trackAlbum;

    /**
     * 构造音乐唱片物品（无元数据版本）
     *
     * @param modId        模组 ID（如 "pasterdream"）
     * @param registryName 唱片物品注册名（如 "sweetdream_disc"）
     * @param songId       对应的 jukebox_song JSON 文件名（如 "sweetdream"）
     */
    public PastedreamMusicDiscItem(String modId, String registryName, String songId) {
        this(modId, registryName, songId, null, null, null);
    }

    /**
     * 构造音乐唱片物品（含元数据版本）
     *
     * @param modId        模组 ID（如 "pasterdream"）
     * @param registryName 唱片物品注册名（如 "sweetdream_disc"）
     * @param songId       对应的 jukebox_song JSON 文件名（如 "sweetdream"）
     * @param title        曲名（可为 null）
     * @param artist       作者（可为 null）
     * @param album        专辑（可为 null）
     */
    public PastedreamMusicDiscItem(String modId, String registryName, String songId,
                                   @Nullable String title, @Nullable String artist, @Nullable String album) {
        super(DEFAULT_PROPERTIES.jukeboxPlayable(
                ResourceKey.create(Registries.JUKEBOX_SONG,
                        ResourceLocation.fromNamespaceAndPath(modId, songId))));
        this.trackTitle = title;
        this.trackArtist = artist;
        this.trackAlbum = album;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        if (Screen.hasShiftDown()) {
            // Shift 展开模式：显示详细元数据
            if (trackTitle != null) {
                tooltip.add(Component.literal("§7曲名: §f" + trackTitle));
            }
            if (trackArtist != null) {
                tooltip.add(Component.literal("§7作者: §f" + trackArtist));
            }
            if (trackAlbum != null) {
                tooltip.add(Component.literal("§7专辑: §f" + trackAlbum));
            }
        } else {
            // 非 Shift 模式：提示按 Shift 查看更多
            if (trackTitle != null || trackArtist != null || trackAlbum != null) {
                tooltip.add(Component.literal("§7按住 §eShift §7查看唱片信息"));
            }
        }
    }
}
