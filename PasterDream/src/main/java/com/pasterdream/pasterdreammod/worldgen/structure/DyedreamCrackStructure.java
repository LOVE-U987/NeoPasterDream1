package com.pasterdream.pasterdreammod.worldgen.structure;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.Optional;

/**
<<<<<<< HEAD
 * 主世界染梦裂隙结构（struct_dyedream_crack_1，Y=32 空中浮岛）。
 * <p>
 * 自然生成受配置 {@link PDCommonConfig#DYEDREAM_CRACK_GENERATE} 控制。
 * <p>
 * <b>设计动机</b>：NeoForge 加载时序为 mod 构造 → {@code RegisterEvent}（DeferredRegister
 * 冻结）→ 配置加载（{@code ModConfigEvent.Loading}）。因此在 mod 构造阶段无法安全读取
 * 配置值来决定"是否注册类型"，而注册后又无法撤销。故本类采用与
 * {@link AaroncosArenaPortalStructure} 相同的<b>组合</b>方案（{@link JigsawStructure} 为
 * final 类不可继承）：StructureType 无条件注册，配置判断下沉到<b>生成阶段</b>——
 * 覆写 {@link #findGenerationPoint}，配置关闭时直接返回空，等价于"不生成"。
 * 此时配置早已加载完成，{@code ConfigValue.get()} 安全。
 * <p>
 * 效果与旧实现一致：配置关闭时主世界天空不再生成裂隙浮岛；
 * 结构 JSON / structure_set 始终可解析，不会产生数据包报错。
=======
 * 主世界染梦裂隙结构 —— 受配置 {@code DYEDREAM_CRACK_GENERATE} 控制。
 * <p>
 * 当「染梦裂隙自然生成」配置关闭时，该结构不生成（返回空）；
 * 开启时委托内部 jigsaw 结构在 Y=32 天空生成裂隙浮岛。
 * <p>
 * 由于 {@link JigsawStructure} 为 final 类，本类采用<b>组合</b>方式包装
 * 一个 JigsawStructure 委托实例完成实际生成逻辑（同 {@link AaroncosArenaPortalStructure}）。
>>>>>>> 78e76f9ac8db1d4e43a7f83c4aca5b827277f1db
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
<<<<<<< HEAD
     * 生成点判定：配置关闭时禁止生成，否则委托 jigsaw 结构实际生成。
=======
     * 生成点判定：配置关闭时不生成裂隙；开启时委托 jigsaw 结构实际生成。
>>>>>>> 78e76f9ac8db1d4e43a7f83c4aca5b827277f1db
     *
     * @param context 结构生成上下文
     * @return 生成桩；配置关闭时为空
     */
    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
<<<<<<< HEAD
        // 配置关闭（dyedream crack generate=false）时跳过生成。
        // 此处为世界生成阶段，配置已加载，ConfigValue.get() 安全。
        if (!PDCommonConfig.DYEDREAM_CRACK_GENERATE.get()) {
=======
        if (!Boolean.TRUE.equals(PDCommonConfig.DYEDREAM_CRACK_GENERATE.get())) {
>>>>>>> 78e76f9ac8db1d4e43a7f83c4aca5b827277f1db
            return Optional.empty();
        }
        return delegate.findGenerationPoint(context);
    }

    /**
     * 返回本结构的类型（沿用被包装的 jigsaw 类型）。
     *
<<<<<<< HEAD
     * @return 结构类型
=======
     * @return StructureType
>>>>>>> 78e76f9ac8db1d4e43a7f83c4aca5b827277f1db
     */
    @Override
    public StructureType<?> type() {
        return delegate.type();
    }
}
