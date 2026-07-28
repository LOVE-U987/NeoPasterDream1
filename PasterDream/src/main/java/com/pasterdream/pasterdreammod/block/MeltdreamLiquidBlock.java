package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.item.TurnPaleCeciliaItem;
import com.pasterdream.pasterdreammod.registry.PDFluids;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
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
 * <p>
 * 失色塞西莉娅的加护掉落物进入源格时转化为塞西莉娅的加护。
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
     * 方块放置时调度粒子方块 tick
     * <p>
     * 注意：super.onPlace() 调度的是流体扩散 tick，与本方块的粒子方块 tick 相互独立；
     * 液体蔓延导致液位变化时 onPlace 会被反复触发，这里用 hasScheduledTick 去重，
     * 避免同一位置叠加出多条并行的粒子 tick 链
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
        if (!world.getBlockTicks().hasScheduledTick(pos, this)) {
            world.scheduleTick(pos, this, 5);
        }
    }

    /**
     * 掉落物进入融梦涌泉源：失色塞西莉娅 → 塞西莉娅的加护。
     */
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (level.isClientSide() || !(entity instanceof ItemEntity itemEntity)) {
            return;
        }
        if (!level.getFluidState(pos).isSource()) {
            return;
        }
        TurnPaleCeciliaItem.tryRestoreItemEntity(itemEntity);
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
        // 粒子类型在注册阶段已完成注册，运行期不可能为 null，无需判空；
        // 通过 holder() 直接取得 SimpleParticleType 引用，类型安全，无需强制转换
        if (random.nextFloat() < 0.125f) {
            SimpleParticleType particle = PDParticles.MELTDREAM_CRYSTAL_PARTICLE.holder().get();
            world.sendParticles(particle, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5,
                    1, 0.15, 0.15, 0.15, 0.05);
        }
        world.scheduleTick(pos, this, 40);
    }
}
