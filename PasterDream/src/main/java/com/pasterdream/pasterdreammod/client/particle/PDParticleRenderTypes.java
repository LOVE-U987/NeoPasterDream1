package com.pasterdream.pasterdreammod.client.particle;

import com.pasterdream.pasterdreammod.api.client.particle.ApiParticleRenderTypes;
import net.minecraft.client.particle.ParticleRenderType;

/**
 * 主模粒子 RenderType 入口（委托 API {@link ApiParticleRenderTypes}）。
 */
public final class PDParticleRenderTypes {

    public static final ParticleRenderType GLOWING_SHEET = ApiParticleRenderTypes.GLOWING_SHEET;

    private PDParticleRenderTypes() {
        throw new UnsupportedOperationException("工具类不可实例化");
    }
}
