package com.pasterdream.pasterdreammod.worldgen.structure;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.Optional;

/**
 * 主世界染梦裂隙结构 —— 受配置 {@code DYEDREAM_CRACK_GENERATE} 控制。
 * <p>
 * 当「染梦裂隙自然生成」配置关闭时，该结构不生成（返回空）；
 * 开启时委托内部 jigsaw 结构在 Y=32 天空生成裂隙浮岛。
 * <p>
 * 由于 {@link JigsawStructure} 为 final 类，本类采用<b>组合</b>方式包装
 * 一个 JigsawStructure 委托实例完成实际生成逻辑（同 {@link AaroncosArenaPortalStructure}）。
 */
public final class DyedreamCrackStructure extends Structure {

    /** 序列化编解码器：复用 JigsawStructure 的字段结构，解码为本类实例 */
    public static final MapCodec<DyedreamCrackStructure> CODEC =
            JigsawStructure.CODEC.xmap(DyedreamCrackStructure::new, DyedreamCrackStructure::unwrap);

    /** 被包装的原版 jigsaw 结构实例（负责实际生成逻辑） */
    private final JigsawStructure delegate;

    /**
     * 包装构造函数。
     *
     * @param delegate 解码出的 jigsaw 结构实例
     */
    private DyedreamCrackStructure(JigsawStructure delegate) {
        super(delegate.getModifiedStructureSettings());
        this.delegate = delegate;
    }

    /**
     * 拆包（CODEC 编码用）。
     *
     * @param structure 本类实例
     * @return 被包装的 jigsaw 结构实例
     */
    private static JigsawStructure unwrap(DyedreamCrackStructure structure) {
        return structure.delegate;
    }

    /**
     * 生成点判定：配置关闭时不生成裂隙；开启时委托 jigsaw 结构实际生成。
     *
     * @param context 结构生成上下文
     * @return 生成桩；配置关闭时为空
     */
    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        if (!Boolean.TRUE.equals(PDCommonConfig.DYEDREAM_CRACK_GENERATE.get())) {
            return Optional.empty();
        }
        return delegate.findGenerationPoint(context);
    }

    /**
     * 返回本结构的类型（沿用被包装的 jigsaw 类型）。
     *
     * @return StructureType
     */
    @Override
    public StructureType<?> type() {
        return delegate.type();
    }
}
