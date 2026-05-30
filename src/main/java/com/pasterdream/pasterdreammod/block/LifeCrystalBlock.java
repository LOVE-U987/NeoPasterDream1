package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.LifeCrystalBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.Nullable;
import java.util.List;

/**
 * 生命水晶方块 - 使用 GeckoLib 动画的交互式光源方块
 *
 * 功能：
 * 1. 右键吸收 → 40 tick 倒计时 → 爱心粒子 → 破碎消失
 * 2. 永久增加玩家 2 点最大生命值
 * 3. 空闲时散发环境粒子
 * 4. 支持 FACING / WATERLOGGED 属性
 * 5. 发光等级 12
 */
public class LifeCrystalBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final MapCodec<LifeCrystalBlock> CODEC = simpleCodec(LifeCrystalBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final ResourceLocation MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "life_crystal");
    private static final AttributeModifier LIFE_CRYSTAL_MODIFIER =
            new AttributeModifier(MODIFIER_ID, 2, AttributeModifier.Operation.ADD_VALUE);

    // 根据 FACING 方向的碰撞箱 - 晶体为扁平形状
    private static final VoxelShape SHAPE_NORTH = Block.box(3, 2, 5, 13, 14, 11);
    private static final VoxelShape SHAPE_EAST = Block.box(5, 2, 3, 11, 14, 13);

    public LifeCrystalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ANIMATION, 0)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST, WEST -> SHAPE_EAST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ANIMATION, WATERLOGGED);
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

    // ==================== 方块实体 ====================

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new LifeCrystalBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, PDBlockEntities.LIFE_CRYSTAL.get(),
                (lvl, pos, st, be) -> be.serverTick(lvl, pos, st));
    }

    // ==================== 右键交互 ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        // 检查玩家是否已经吸收过生命水晶
        if (player instanceof LivingEntity livingEntity) {
            AttributeInstance attr = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            if (attr != null && attr.hasModifier(MODIFIER_ID)) {
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("你已经吸收过生命水晶了！"),
                        false);
                return InteractionResult.SUCCESS;
            }
        }

        // 设置动画状态为 1（激活旋转）
        level.setBlock(pos, state.setValue(ANIMATION, 1), 3);

        // 播放水晶嗡鸣音效
        level.playSound(null, pos,
                SoundEvent.createVariableRangeEvent(
                        ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "life_crystal")),
                SoundSource.BLOCKS, 1.0f, 1.0f);

        // 启动方块实体的吸收计时器
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof LifeCrystalBlockEntity crystal) {
            crystal.startUse(player);
        }

        return InteractionResult.CONSUME;
    }

    // ==================== 客户端环境粒子 ====================

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        // 空闲时随机散发荧光粒子（15% 概率）
        if (state.getValue(ANIMATION) == 0 && random.nextFloat() < 0.15f) {
            double x = pos.getX() + 0.3 + random.nextDouble() * 0.4;
            double y = pos.getY() + 0.5 + random.nextDouble() * 0.5;
            double z = pos.getZ() + 0.3 + random.nextDouble() * 0.4;
            level.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.02, 0);
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(this));
    }
}