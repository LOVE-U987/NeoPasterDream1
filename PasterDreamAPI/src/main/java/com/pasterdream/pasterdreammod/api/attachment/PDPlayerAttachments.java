package com.pasterdream.pasterdreammod.api.attachment;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.meltdream.MeltDreamEnergyData;
import com.pasterdream.pasterdreammod.api.san.SanData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

/**
 * PasterDream 玩家数据附件注册中心。
 * <p>
 * 位于 PasterDreamAPI，供主模组及 San/融梦/法术等附属模组共享读取。
 * 注册名保持与旧版 Capability NBT 键兼容，确保旧存档平滑迁移。
 */
public final class PDPlayerAttachments {

    /** 玩家数据附件 DeferredRegister */
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, PasterDreamAPI.DATA_NAMESPACE);

    /**
     * San 值附件。
     * <p>
     * 默认 San 值 100，检查开启；死亡时完整保留数据。
     */
    public static final Supplier<AttachmentType<SanData>> PLAYER_SAN = ATTACHMENT_TYPES.register(
            "player_san",
            () -> AttachmentType.builder(() -> SanData.DEFAULT)
                    .serialize(SanData.CODEC)
                    .copyOnDeath()
                    .build()
    );

    /**
     * 融梦能量附件。
     * <p>
     * 默认能量 0，免消耗计数 0；死亡时完整保留数据。
     */
    public static final Supplier<AttachmentType<MeltDreamEnergyData>> PLAYER_MELTDREAM_ENERGY = ATTACHMENT_TYPES.register(
            "player_meltdream_energy",
            () -> AttachmentType.builder(() -> MeltDreamEnergyData.DEFAULT)
                    .serialize(MeltDreamEnergyData.CODEC)
                    .copyOnDeath()
                    .build()
    );

    private PDPlayerAttachments() {
        throw new UnsupportedOperationException("PDPlayerAttachments 是常量类，不可实例化");
    }
}
