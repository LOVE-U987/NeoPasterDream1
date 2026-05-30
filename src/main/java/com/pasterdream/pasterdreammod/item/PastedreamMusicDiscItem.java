package com.pasterdream.pasterdreammod.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

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
 */
public class PastedreamMusicDiscItem extends Item {

    private static final Properties DEFAULT_PROPERTIES = new Item.Properties()
            .stacksTo(1)
            .rarity(Rarity.RARE);

    /**
     * 构造音乐唱片物品
     *
     * @param modId        模组 ID（如 "pasterdream"）
     * @param registryName 唱片物品注册名（如 "sweetdream_disc"）
     * @param songId       对应的 jukebox_song JSON 文件名（如 "sweetdream"）
     */
    public PastedreamMusicDiscItem(String modId, String registryName, String songId) {
        super(DEFAULT_PROPERTIES.jukeboxPlayable(
                ResourceKey.create(Registries.JUKEBOX_SONG,
                        ResourceLocation.fromNamespaceAndPath(modId, songId))));
    }
}
