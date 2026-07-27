package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.entity.projectile.MoltengoldWandProjectileEntity;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDParticles;
import com.pasterdream.pasterdreammod.util.ServerScheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 唤星裂隙（starcall_crack）
 * <p>
 * 原版亦为<strong>限时方块</strong>（非实体）：唤星者/『亚勒兹』法球在露天命中时
 * 以一定概率置于落点 y+11。行为对齐原版 {@code StarcallCrackBlock} + Pr0/Pr1：
 * <ul>
 *   <li>放置后每 10 tick（0.5s）下射炙焰金杖法球（伤害 5、动能 1、散布 8），
 *       并播熔岩粒子与龙息爆炸音；y−10 处有液体时不射</li>
 *   <li>存活 200 tick（10s）后若仍为本方块则变为空气</li>
 *   <li>不可破坏（strength −1 / 极高爆破抗性）、无碰撞、发光 15</li>
 * </ul>
 */
public class StarcallCrackBlock extends Block {

    /** 水平扩展碰撞/轮廓箱（原版 box(-10,0,-10,26,1,26)） */
    private static final VoxelShape SHAPE = Block.box(-10.0D, 0.0D, -10.0D, 26.0D, 1.0D, 26.0D);

    /** tick 间隔：下射与粒子节奏 */
    private static final int TICK_INTERVAL = 10;

    /** 生命周期（tick） */
    private static final int LIFETIME_TICKS = 200;

    /**
     * 构造唤星裂隙：玄武岩音效、不可破坏、全亮、无碰撞无遮挡、自发光。
     */
    public StarcallCrackBlock() {
        super(BlockBehaviour.Properties.of()
                .instrument(NoteBlockInstrument.HAT)
                .sound(SoundType.BASALT)
                .strength(-1.0F, 3600000.0F)
                .lightLevel(s -> 15)
                .noCollission()
                .noOcclusion()
                .hasPostProcess((bs, br, bp) -> true)
                .emissiveRendering((bs, br, bp) -> true)
                .isRedstoneConductor((bs, br, bp) -> false));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /**
     * 放置：启动 10tick 节奏 + 200tick 后自毁 + 唤星粒子爆发。
     */
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        if (level.isClientSide()) {
            return;
        }
        level.scheduleTick(pos, this, TICK_INTERVAL);
        // 原版 StarcallCrackPr1：queueServerWork(200) 后若仍是本方块则清除
        final BlockPos locked = pos.immutable();
        ServerScheduler.schedule(LIFETIME_TICKS, () -> {
            if (level instanceof ServerLevel server
                    && server.getBlockState(locked).is(this)) {
                server.removeBlock(locked, false);
            }
        });
        if (level instanceof ServerLevel server) {
            server.sendParticles(
                    (SimpleParticleType) PDParticles.STARCALL_PARTICLE.particleType(),
                    locked.getX(), locked.getY(), locked.getZ(),
                    8, 3.0D, 3.0D, 3.0D, 0.1D);
        }
    }

    /**
     * 周期 tick：熔岩粒子、龙息音、向下射炙焰金法球，并重排下一拍。
     */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        rainFireball(level, pos);
        if (level.getBlockState(pos).is(this)) {
            level.scheduleTick(pos, this, TICK_INTERVAL);
        }
    }

    /**
     * 原版 StarcallCrackPr0：粒子 + 音效 +（y−10 非液体时）下射法球。
     */
    private static void rainFireball(ServerLevel level, BlockPos pos) {
        double cx = pos.getX() + 0.5D;
        double cy = pos.getY() + 0.5D;
        double cz = pos.getZ() + 0.5D;

        level.sendParticles(ParticleTypes.LAVA, cx, pos.getY() - 0.1D, cz, 3, 1.0D, 0.1D, 1.0D, 0.1D);
        level.playSound(null, pos, SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 0.6F, 1.0F);

        // 原版：y-10 流体对应方块不是 LiquidBlock 才发射
        if (!level.getFluidState(pos.below(10)).isEmpty()) {
            return;
        }

        MoltengoldWandProjectileEntity projectile =
                new MoltengoldWandProjectileEntity(PDEntities.MOLTENGOLD_WAND_PROJECTILE.get(), level);
        projectile.setBaseDamage(5.0D);
        projectile.setSilent(true);
        // 1.21 无 setKnockback；原版 knockback=1 在此版本由命中默认处理
        projectile.setPos(cx, cy, cz);
        projectile.shoot(0.0D, -1.0D, 0.0D, 1.0F, 8.0F);
        level.addFreshEntity(projectile);
    }
}
