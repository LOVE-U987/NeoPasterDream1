package com.pasterdream.pasterdreammod.mixin;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 染梦维度流体生成修正 Mixin —— 移除世界底部原版硬编码岩浆层
 * <p>
 * 原版 {@link NoiseBasedChunkGenerator#createFluidPicker} 将岩浆水位硬编码为
 * Y=-54（逻辑等价于 {@code y < min(-54, seaLevel) ? LAVA : WATER}），
 * 任何 min_y 低于 -54 的维度其世界最底层（染梦维度为 Y-64~-55 约 10 格）
 * 都会强制生成岩浆，surface_rule 无法覆盖。
 * <p>
 * 本 Mixin 仅对「默认方块为方解石」的维度生效（{@code pasterdream:dyedream_world}
 * 噪声设置的特征，项目中唯一），将其流体选择替换为「低于海平面一律默认流体
 * （水）」，实现「地下岩浆改为地下河」；其余维度保持原版行为不受影响。
 */
@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {

    /**
     * 拦截 createFluidPicker，对染梦维度返回无岩浆的流体选择器
     * <p>
     * 原版返回的流体选择器在 y &lt; -54 时给出岩浆状态；此处直接返回
     * 「水位 = 海平面、流体 = 默认流体（水）」的恒定状态，由
     * {@link Aquifer.FluidStatus#at} 自行按高度判定水/空气。
     *
     * @param settings 噪声生成设置（染梦维度 default_block 为方解石）
     * @param cir      回调返回值（可取消并替换）
     */
    @Inject(method = "createFluidPicker", at = @At("HEAD"), cancellable = true)
    private static void pasterdream$dyedreamNoLava(NoiseGeneratorSettings settings, CallbackInfoReturnable<Aquifer.FluidPicker> cir) {
        if (settings.defaultBlock().is(Blocks.CALCITE)) {
            Aquifer.FluidStatus water = new Aquifer.FluidStatus(settings.seaLevel(), settings.defaultFluid());
            cir.setReturnValue((x, y, z) -> water);
        }
    }
}
