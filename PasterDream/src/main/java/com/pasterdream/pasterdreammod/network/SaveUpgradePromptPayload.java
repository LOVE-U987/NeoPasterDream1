package com.pasterdream.pasterdreammod.network;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * S2C：旧存档备份/更新提示包。
 * <p>
 * 单机（集成服务器）加载旧版本存档时下发，客户端据此弹出确认界面。
 * 专用服务器不发送本包，改用 OP 系统消息 + 命令。
 *
 * @param worldName     世界名
 * @param backupPath    自动备份目录路径；未备份时为空串
 * @param backupFailed  自动备份是否失败
 * @param targetVersion 目标 ID schema 版本
 */
public record SaveUpgradePromptPayload(String worldName, String backupPath, boolean backupFailed, int targetVersion)
        implements CustomPacketPayload {

    /** 包类型标识 */
    public static final Type<SaveUpgradePromptPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "save_upgrade_prompt"));

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, SaveUpgradePromptPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, SaveUpgradePromptPayload::worldName,
            ByteBufCodecs.STRING_UTF8, SaveUpgradePromptPayload::backupPath,
            ByteBufCodecs.BOOL, SaveUpgradePromptPayload::backupFailed,
            ByteBufCodecs.VAR_INT, SaveUpgradePromptPayload::targetVersion,
            SaveUpgradePromptPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
