package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 亚伦柯斯竞技场传送门方块
 * <p>
 * 位于主世界的不可破坏传送门方块，触碰时传送至竞技场维度。
 * 继承 SlabBlock 实现半砖形状，具有发光效果。
 * <p>
 * 传送条件：无条件，任何玩家都可以进入
 * <p>
 * 感染效果：传送门周围的地面方块会被转化为灯影之下风格的方块（阴影方块、厚阴影方块等）
 */
public class AaroncosArenaPortalsBlock extends SlabBlock {

    /** 感染半径 */
    private static final int INFECTION_RADIUS = 3;

    /** 感染概率（每tick） */
    private static final float INFECTION_CHANCE = 0.02f;

    public AaroncosArenaPortalsBlock() {
        super(BlockBehaviour.Properties.of()
                .instrument(NoteBlockInstrument.BASEDRUM)
                .sound(SoundType.GLASS)
                .strength(-1, 3600000)
                .lightLevel(s -> 15)
                .noCollission()
                .noOcclusion()
                .hasPostProcess((bs, br, bp) -> true)
                .emissiveRendering((bs, br, bp) -> true)
                .isRedstoneConductor((bs, br, bp) -> false)
                .dynamicShape());
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }

    /**
     * 每tick更新时触发感染效果
     * 随机将周围的地面方块转化为阴影风格方块
     *
     * @param state       当前方块状态
     * @param level       当前世界
     * @param pos         方块位置
     * @param random      随机数生成器
     */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide && random.nextFloat() < INFECTION_CHANCE) {
            infectSurroundingBlocks(level, pos, random);
        }
        level.scheduleTick(pos, this, 20);
    }

    /**
     * 方块放置时立即开始感染流程
     */
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 0);
        }
    }

    /**
     * 感染周围方块
     * 将草方块、泥土、石头等转化为阴影风格方块
     */
    private void infectSurroundingBlocks(ServerLevel level, BlockPos centerPos, RandomSource random) {
        for (int dx = -INFECTION_RADIUS; dx <= INFECTION_RADIUS; dx++) {
            for (int dz = -INFECTION_RADIUS; dz <= INFECTION_RADIUS; dz++) {
                for (int dy = -1; dy <= 0; dy++) {
                    BlockPos targetPos = centerPos.offset(dx, dy, dz);
                    if (random.nextFloat() < 0.3f && canInfect(level, targetPos)) {
                        infectBlock(level, targetPos);
                    }
                }
            }
        }
    }

    /**
     * 判断方块是否可以被感染
     */
    private boolean canInfect(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        return isInfectableBlock(block) && level.isEmptyBlock(pos.above());
    }

    /**
     * 判断方块类型是否可被感染
     */
    private boolean isInfectableBlock(Block block) {
        return block == net.minecraft.world.level.block.Blocks.GRASS_BLOCK
                || block == net.minecraft.world.level.block.Blocks.DIRT
                || block == net.minecraft.world.level.block.Blocks.STONE
                || block == net.minecraft.world.level.block.Blocks.COBBLESTONE
                || block == net.minecraft.world.level.block.Blocks.GRAVEL
                || block == net.minecraft.world.level.block.Blocks.SAND;
    }

    /**
     * 感染单个方块，转化为阴影风格方块
     */
    private void infectBlock(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        BlockState replacementState;

        if (block == net.minecraft.world.level.block.Blocks.GRASS_BLOCK || block == net.minecraft.world.level.block.Blocks.DIRT) {
            replacementState = RandomSource.create().nextBoolean()
                    ? PDBlocks.SHADOW_BLOCK.get().defaultBlockState()
                    : PDBlocks.THICK_SHADOW_BLOCK.get().defaultBlockState();
        } else if (block == net.minecraft.world.level.block.Blocks.STONE) {
            replacementState = PDBlocks.SHADOW_STONE.get().defaultBlockState();
        } else if (block == net.minecraft.world.level.block.Blocks.COBBLESTONE) {
            replacementState = PDBlocks.SHADOW_STONE_BRICK.get().defaultBlockState();
        } else if (block == net.minecraft.world.level.block.Blocks.GRAVEL) {
            replacementState = PDBlocks.SHADOW_BLOCK.get().defaultBlockState();
        } else if (block == net.minecraft.world.level.block.Blocks.SAND) {
            replacementState = PDBlocks.THICK_SHADOW_BLOCK.get().defaultBlockState();
        } else {
            return;
        }

        level.setBlock(pos, replacementState, 3);
    }

    /**
     * 当实体进入方块碰撞箱时触发 —— 实现竞技场传送
     * <p>
     * 传送条件：无条件，任何玩家都可以进入
     * 传送到目标维度后赋予缓降效果。
     *
     * @param state  当前方块状态
     * @param level  当前世界
     * @param pos    方块位置
     * @param entity 进入方块的实体
     */
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player)) {
            return;
        }

        if (level.dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY)) {
            return;
        }

        ServerLevel targetWorld = player.getServer().getLevel(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY);
        if (targetWorld == null) {
            return;
        }

        DimensionTransition transition = new DimensionTransition(
                targetWorld,
                new net.minecraft.world.phys.Vec3(0.5, 70.0, 0.5),
                entity.getDeltaMovement(),
                entity.getYRot(),
                entity.getXRot(),
                DimensionTransition.PLAY_PORTAL_SOUND
        );

        player.changeDimension(transition);

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 120, 0));
        }
    }
}