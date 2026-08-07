package com.pasterdream.pasterdreammod.api.effect.particle.processors;

import com.pasterdream.pasterdreammod.api.effect.particle.EmitterProcessor;
import com.pasterdream.pasterdreammod.api.effect.particle.EmitterProcessorType;
import com.pasterdream.pasterdreammod.api.effect.particle.ParticleEmitterData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * 绑定实体处理器 —— 发射器跟随实体位置移动
 * <p>
 * 借鉴开源模组 FDLib（作者 FINDERFEED）的 {@code BoundToEntityProcessor} 设计思路
 * （独立实现，非复制）。发射器持有实体的网络 id，客户端每 tick 把发射位置
 * 同步到该实体当前位置（可加垂直偏移）。
 * <p>
 * 注意：本处理器仅记录实体 id 与偏移参数，实际"跟随"逻辑在客户端
 * {@code ParticleEmitter} 中经 {@link #tickEmitter(Object)} 的反射调用消费。
 */
public class BoundToEntityProcessor implements EmitterProcessor<BoundToEntityProcessor> {

    /** 处理器类型实例 */
    public static final Type TYPE = new Type();

    private final int entityId;
    private final float yOffset;

    /**
     * 构造绑定实体处理器
     *
     * @param entityId 实体网络 id（服务端 {@code entity.getId()}）
     * @param yOffset  发射位置相对实体位置的垂直偏移
     */
    public BoundToEntityProcessor(int entityId, float yOffset) {
        this.entityId = entityId;
        this.yOffset = yOffset;
    }

    /**
     * 构造绑定实体处理器（无垂直偏移）
     *
     * @param entityId 实体网络 id
     */
    public BoundToEntityProcessor(int entityId) {
        this(entityId, 0.0f);
    }

    @Override
    public void tickEmitter(ParticleEmitterData emitter) {
        // 客户端实现层通过反射把发射位置同步到实体位置（见 api/client/effect/particle）
    }

    @Override
    public EmitterProcessorType<BoundToEntityProcessor> type() {
        return TYPE;
    }

    /**
     * 获取实体网络 id
     *
     * @return 实体 id
     */
    public int entityId() {
        return entityId;
    }

    /**
     * 获取垂直偏移
     *
     * @return y 偏移
     */
    public float yOffset() {
        return yOffset;
    }

    /**
     * 绑定实体处理器类型
     */
    public static class Type implements EmitterProcessorType<BoundToEntityProcessor> {

        /** 类型唯一 id */
        public static final ResourceLocation ID = ResourceLocation.parse("pasterdream:bound_to_entity_processor");

        /** 编解码器 */
        public static final StreamCodec<FriendlyByteBuf, BoundToEntityProcessor> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_INT, v -> v.entityId,
                        ByteBufCodecs.FLOAT, v -> v.yOffset,
                        BoundToEntityProcessor::new
                );

        @Override
        public StreamCodec<FriendlyByteBuf, BoundToEntityProcessor> codec() {
            return STREAM_CODEC;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }
}
