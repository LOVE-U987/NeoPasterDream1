package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDFluids;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * 融梦涌泉流体方块
 * 继承 LiquidBlock，具有发光、无碰撞、无战利品表、爆炸抗性 100 等属性
 * 每 tick（5 游戏刻）产生融梦水晶粒子效果
 */
public class MeltdreamLiquidBlock extends LiquidBlock {

    /**
     * 构造融梦涌泉流体方块
     * 属性：地图色 FIRE、强度 100、发光渲染、无碰撞、液态、可替换
     */
    public MeltdreamLiquidBlock() {
        super(PDFluids.MELTDREAM_LIQUID.get(), BlockBehaviour.Properties.of()
                .mapColor(MapColor.FIRE).strength(100f)
                .hasPostProcess((bs, br, bp) -> true)
                .emissiveRendering((bs, br, bp) -> true)
                .noCollission().noLootTable().liquid()
                .pushReaction(PushReaction.DESTROY)
                .sound(SoundType.EMPTY).replaceable());
    }

    /**
     * 方块放置时调度 tick 更新
     *
     * @param blockstate 当前方块状态
     * @param world      世界实例
     * @param pos        方块位置
     * @param oldState   旧方块状态
     * @param moving     是否因移动触发
     */
    @Override
    public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(blockstate, world, pos, oldState, moving);
        world.scheduleTick(pos, this, 5);
    }

    /**
     * tick 更新：生成融梦水晶粒子并重新调度
     * 粒子生成频率已大幅降低，减少性能开销
     *
     * @param blockstate 当前方块状态
     * @param world      服务端世界实例
     * @param pos        方块位置
     * @param random     随机数源
     */
    @Override
    public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(blockstate, world, pos, random);
        if (PDParticles.MELTDREAM_CRYSTAL_PARTICLE.isBound() && random.nextFloat() < 0.125f) {
            SimpleParticleType particle = PDParticles.MELTDREAM_CRYSTAL_PARTICLE.get();
            world.sendParticles(particle, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5,
                    1, 0.15, 0.15, 0.15, 0.05);
        }
        world.scheduleTick(pos, this, 40);
    }
}
