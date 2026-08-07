package com.pasterdream.pasterdreammod.api.effect.particle.processors;

import com.pasterdream.pasterdreammod.api.effect.particle.EmitterProcessor;
import com.pasterdream.pasterdreammod.api.effect.particle.EmitterProcessorType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * 空处理器 —— 不施加任何行为，粒子在发射位置以默认速度生成
 * <p>
 * 作为发射器数据的默认处理器，提供最小的多态编解码骨架。
 */
public class EmptyEmitterProcessor implements EmitterProcessor<EmptyEmitterProcessor> {

    /** 处理器类型实例 */
    public static final Type TYPE = new Type();

    @Override
    public EmitterProcessorType<EmptyEmitterProcessor> type() {
        return TYPE;
    }

    /**
     * 空处理器类型
     */
    public static class Type implements EmitterProcessorType<EmptyEmitterProcessor> {

        /** 类型唯一 id */
        public static final ResourceLocation ID = ResourceLocation.parse("pasterdream:empty_emitter_processor");

        /** 编解码器（无字段，unit codec） */
        public static final StreamCodec<FriendlyByteBuf, EmptyEmitterProcessor> STREAM_CODEC =
                StreamCodec.unit(new EmptyEmitterProcessor());

        @Override
        public StreamCodec<FriendlyByteBuf, EmptyEmitterProcessor> codec() {
            return STREAM_CODEC;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }
}
