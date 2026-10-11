package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.GoldenFoxSculptureBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * 狐狸雕像方块 (Golden Fox Sculpture)
 * <p>
 * GeckoLib 静态雕像；支持 FACING / WATERLOGGED。
 * 右键（原版 {@code GoldenFoxSculpturePr0}）：四角 (±9,0,±9) 均为
 * {@code flower_12}（迷梦冶梦莲）下半、主手荧光浆果、且日时
 * {@code dayTime % 24000 ∈ [0,450]} 时消耗浆果与五块，召唤金色狐狸。
 */
public class GoldenFoxSculptureBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final MapCodec<GoldenFoxSculptureBlock> CODEC = simpleCodec(GoldenFoxSculptureBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /** 四角花相对雕像的水平偏移（原版 ±9） */
    public static final int RITUAL_OFFSET = 9;

    /** 日时窗口上限（含）：0…450 ≈ 日出后一小段 */
    public static final long RITUAL_DAYTIME_MAX = 450L;

    // 雕像碰撞箱：中等尺寸，居中对齐
    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 16, 12);

    /**
     * 构造狐狸雕像方块
     *
     * @param properties 方块属性
     */
    public GoldenFoxSculptureBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    // ==================== 水浸支持 ====================

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }
        return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }

    // ==================== 右键仪式（GoldenFoxSculpturePr0） ====================

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        tryActivateRitual(level, pos, player);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        tryActivateRitual(level, pos, player);
        return InteractionResult.SUCCESS;
    }

    /**
     * 校验四角迷梦冶梦莲 + 荧光浆果 + 日出窗口，成功则召唤金狐。
     *
     * @param level  世界（服务端）
     * @param pos    雕像坐标
     * @param player 交互玩家
     * @return 是否成功召唤
     */
    public static boolean tryActivateRitual(Level level, BlockPos pos, Player player) {
        if (level.isClientSide || player == null) {
            return false;
        }
        ItemStack main = player.getMainHandItem();
        long tod = level.getDayTime() % 24000L;
        boolean flowersOk = hasRitualFlowers(level, pos);
        boolean berryOk = main.is(Items.GLOW_BERRIES);
        boolean timeOk = tod >= 0L && tod <= RITUAL_DAYTIME_MAX;
        if (!flowersOk || !berryOk || !timeOk) {
            if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.translatable("message.pasterdream.golden_fox.no_reaction"), false);
            }
            return false;
        }

        main.shrink(1);
        int o = RITUAL_OFFSET;
        level.destroyBlock(pos.offset(o, 0, o), false);
        level.destroyBlock(pos.offset(-o, 0, -o), false);
        level.destroyBlock(pos.offset(o, 0, -o), false);
        level.destroyBlock(pos.offset(-o, 0, o), false);
        level.destroyBlock(pos, false);

        if (level instanceof ServerLevel serverLevel) {
            Entity fox = PDEntities.GOLDEN_FOX.get().spawn(
                    serverLevel, pos.offset(0, 0, 0), MobSpawnType.MOB_SUMMONED);
            if (fox != null) {
                fox.setYRot(level.getRandom().nextFloat() * 360.0F);
            }
            double cx = pos.getX() + 0.5;
            double cy = pos.getY() + 0.2;
            double cz = pos.getZ() + 0.5;
            sendOptionalParticles(serverLevel, "healing_spell_particle", cx, cy, cz, 12);
            sendOptionalParticles(serverLevel, "yellow_smoke_particle", cx, cy, cz, 12);
        }
        level.playSound(null, pos, SoundEvents.FOX_AMBIENT, SoundSource.MASTER, 1.2F, 1.0F);
        level.playSound(null, pos, PDSounds.DING_0.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        return true;
    }

    /**
     * 四角是否均为 flower_12（迷梦冶梦莲；双格植物任一 half 的 block 匹配即可）。
     */
    public static boolean hasRitualFlowers(Level level, BlockPos center) {
        int o = RITUAL_OFFSET;
        return isFlower12(level, center.offset(o, 0, o))
                && isFlower12(level, center.offset(-o, 0, -o))
                && isFlower12(level, center.offset(o, 0, -o))
                && isFlower12(level, center.offset(-o, 0, o));
    }

    private static boolean isFlower12(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(PDBlocks.FLOWER_12.get());
    }

    // ==================== 方块实体 ====================

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GoldenFoxSculptureBlockEntity(pos, state);
    }

    /**
     * 向附属法术模组粒子做可选发送；未装载 PasterDreamSpells 时静默跳过。
     */
    private static void sendOptionalParticles(ServerLevel level, String path,
                                              double x, double y, double z, int count) {
        BuiltInRegistries.PARTICLE_TYPE.getOptional(
                ResourceLocation.fromNamespaceAndPath("pasterdreamspells", path)
        ).ifPresent(type -> {
            if (type instanceof SimpleParticleType simple) {
                level.sendParticles(simple, x, y, z, count, 0.5, 0.4, 0.5, 0.1);
            }
        });
    }
}
