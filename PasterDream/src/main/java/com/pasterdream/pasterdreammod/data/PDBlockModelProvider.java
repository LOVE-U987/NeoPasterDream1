package com.pasterdream.pasterdreammod.data;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.data.ApiBlockModelProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * PasterDream 方块模型/状态数据生成器。
 * <p>
 * BlockAPI 驱动的通用生成已上收到 {@link ApiBlockModelProvider}；
 * 主模仅保留命名与未来 {@link #registerExtraStatesAndModels()} 扩展点。
 */
public class PDBlockModelProvider extends ApiBlockModelProvider {

    public PDBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, PasterDreamMod.MOD_ID, existingFileHelper, "PasterDream Block Models");
    }
}
