package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.WorkshopCauldeonBlockEntity;
import com.pasterdream.pasterdreammod.util.PasterItemData;
import com.pasterdream.pasterdreammod.util.WeaponWorkshopVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * 工坊冷却盆方块 (Workshop Cauldeon)
 * <p>
 * 武器工坊群卫星工位之一：外观由精铸工坊核心统一渲染。
 * 淬火工序（原版 WorkshopCauldeonPr0Procedure）：手持工序 2（待淬火）
 * 的原胚右键 → 冷却盆 BE 随机强化（{@code applyQuenchInlay}）→
 * 熄灭音效 + 水滴/气泡/烟雾粒子 → 工序置 3、改名"未完成原胚（待打磨）"→
 * 强化后的原胚弹出为掉落物（拾取延迟 20、永不消失），清空主手。
 * 手持不符时在快捷栏上方提示用途。
 */
public class WorkshopCauldeonBlock extends BaseEntityBlock {

    public static final MapCodec<WorkshopCauldeonBlock> CODEC = simpleCodec(WorkshopCauldeonBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    /** 原胚物品标签（pasterdream:embryo_items） */
    private static final TagKey<Item> EMBRYO_ITEMS =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "embryo_items"));

    /**
     * 构造工坊冷却盆方块
     *
     * @param properties 方块属性
     */
    public WorkshopCauldeonBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        // 占位 JSON 模型（air 纹理），实际外观由精铸工坊核心渲染
        return RenderShape.MODEL;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        // 与原版一致：完全阻挡光线
        return 15;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        // 原版逐朝向碰撞箱
        Direction facing = state.getValue(FACING);
        if (facing == Direction.NORTH) return box(1, 3, 2, 9, 15, 15);
        if (facing == Direction.EAST) return box(1, 3, 1, 14, 15, 9);
        if (facing == Direction.WEST) return box(2, 3, 7, 15, 15, 15);
        return box(7, 3, 1, 15, 15, 14);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WorkshopCauldeonBlockEntity(pos, state);
    }

    // ==================== 淬火交互（原版 WorkshopCauldeonPr0Procedure） ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ItemStack hand = player.getMainHandItem();
        if (!hand.is(EMBRYO_ITEMS) || PasterItemData.getDouble(hand, "process") != 2) {
            player.displayClientMessage(Component.translatable("message.pasterdream.workshop_cauldeon.cool_down"), true);
            return InteractionResult.SUCCESS;
        }
        // 原版把主手整组原胚交给全局暂存并原地强化，随后整组弹出为掉落物
        ItemStack stash = hand.copy();
        WeaponWorkshopVariables.weaponWorkshopItem = stash;
        if (level.getBlockEntity(pos) instanceof WorkshopCauldeonBlockEntity cauldeon) {
            cauldeon.applyQuenchInlay(stash);
        }
        level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.NEUTRAL, 1.0f, 1.0f);
        if (level instanceof ServerLevel serverLevel) {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            serverLevel.sendParticles(ParticleTypes.DRIPPING_WATER, x + 0.5, y + 1, z + 0.5, 7, 0.3, 0.5, 0.3, 0.05);
            serverLevel.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, x + 0.5, y + 1, z + 0.5, 6, 0.3, 0.5, 0.3, 0.05);
            serverLevel.sendParticles(ParticleTypes.SMOKE, x + 0.5, y + 1, z + 0.5, 3, 0.3, 0.5, 0.3, 0.05);
        }
        PasterItemData.putDouble(stash, "process", 3);
        stash.set(DataComponents.CUSTOM_NAME, Component.translatable("message.pasterdream.embryo.pending_polish"));
        if (level instanceof ServerLevel serverLevel) {
            ItemEntity drop = new ItemEntity(serverLevel,
                    pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, stash);
            drop.setPickUpDelay(20);
            drop.setUnlimitedLifetime();
            serverLevel.addFreshEntity(drop);
        }
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.getInventory().setChanged();
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }
}
