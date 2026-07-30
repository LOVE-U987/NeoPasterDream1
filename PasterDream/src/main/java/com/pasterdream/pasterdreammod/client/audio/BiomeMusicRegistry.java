package com.pasterdream.pasterdreammod.client.audio;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.audio.BiomeMusicTable;

/**
 * PD 主模群系音乐表：在 API {@link BiomeMusicTable} 上固定命名空间为 {@link PasterDreamMod#MOD_ID}。
 * <p>
 * 具体 biome → 曲目与维度白名单仍由 {@link ModMusicManager#initializeDefaultBiomeMusic()} /
 * {@link com.pasterdream.pasterdreammod.client.PDClientEvents} 填写。
 */
public class BiomeMusicRegistry extends BiomeMusicTable {

    public BiomeMusicRegistry() {
        super(PasterDreamMod.MOD_ID);
    }
}
