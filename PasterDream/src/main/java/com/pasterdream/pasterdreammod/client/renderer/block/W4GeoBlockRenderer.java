package com.pasterdream.pasterdreammod.client.renderer.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.client.renderer.DefaultedGeoBlockRenderer;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;

/**
 * W4 波次通用 GeckoLib 方块渲染器（薄壳 → API {@link DefaultedGeoBlockRenderer}）。
 */
public class W4GeoBlockRenderer extends DefaultedGeoBlockRenderer<W4GeoDataBlockEntity> {

    public W4GeoBlockRenderer(String name) {
        super(PasterDreamMod.MOD_ID, name);
    }
}
