package com.pasterdream.pasterdreammod.network;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 调试变体水晶 —— 变体选择同步包（C2S）
 * <p>
 * 玩家手持变体水晶「潜行 + 滚轮」切换变体时，客户端改完本地物品组件后发本包，
 * 由服务端把同一个变体序号写入该槽位物品，保证放置时两端选中项一致。
 * <p>
 * 属调试工具，按 API 边界规则留在主模（调试/测试代码不上收 API）。
 *
 * @param slot    玩家物品栏槽位（含快捷栏 0~8）
 * @param variant 选中的变体序号（服务端会按变体总数取模校验）
 */
public record DebugWandVariantPayload(int slot, int variant) implements CustomPacketPayload {

    /** 包类型 ID */
    public static final CustomPacketPayload.Type<DebugWandVariantPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
                    PasterDreamMod.MOD_ID, "debug_wand_variant"));

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, DebugWandVariantPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, DebugWandVariantPayload::slot,
            ByteBufCodecs.VAR_INT, DebugWandVariantPayload::variant,
            DebugWandVariantPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
