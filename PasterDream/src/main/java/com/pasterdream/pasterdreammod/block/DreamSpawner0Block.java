package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.DreamSpawner0BlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.registry.PDGameRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 构梦刷怪笼方块 (Dream Spawner 0)
 * <p>
 * 带方块实体的"真"刷怪机（与纯装饰的 {@link DreamSpawner1Block} 相对）：
 * <ul>
 *   <li>手持刷怪蛋右键写入生物种类（原版 DreamSpawner0Pr2）；</li>
 *   <li>放置时初始化侦测半径 16 与首刷标记（原版 DreamSpawner0Pr1）；</li>
 *   <li>每 10 tick（原版 DreamSpawner0Pr0，调试模式游戏规则关闭时）：侦测半径内
 *       有玩家 → 首次立即生成一只；此后剩余批量次数 &gt; 0 时每次 10% 概率
 *       生成并扣减，批量耗尽后退化为构梦刷怪笼 1 号（纯装饰）。</li>
 * </ul>
 * 不掉落任何物品（与原版 getDrops 返回 AIR 一致）。
 */
public class DreamSpawner0Block extends Block implements EntityBlock {

    public static final MapCodec<DreamSpawner0Block> CODEC = simpleCodec(DreamSpawner0Block::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    /**
     * 构造构梦刷怪笼方块
     *
     * @param properties 方块属性
     */
    public DreamSpawner0Block(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    protected MapCodec<? extends Block> codec() {
        return CODEC;
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

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        // 与原版一致：不掉落任何物品
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!drops.isEmpty()) {
            return drops;
        }
        return List.of();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DreamSpawner0BlockEntity(pos, state);
    }

    // ==================== 放置初始化（原版 DreamSpawner0Pr1） ====================

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        level.scheduleTick(pos, this, 10);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof DreamSpawner0BlockEntity spawner) {
            spawner.setFirstSpawned(false);
            spawner.setPlayerRange(16);
            spawner.setSpawnNumber(0);
        }
    }

    // ==================== 周期刷怪（原版 DreamSpawner0Pr0，每 10 tick） ====================

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        spawnTick(level, pos);
        level.scheduleTick(pos, this, 10);
    }

    /**
     * 刷怪周期逻辑（原版 DreamSpawner0Pr0Procedure）
     *
     * @param level 服务端世界
     * @param pos   方块位置
     */
    private void spawnTick(ServerLevel level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof DreamSpawner0BlockEntity spawner)) {
            return;
        }
        // 调试模式游戏规则开启时不刷怪
        if (level.getGameRules().getBoolean(PDGameRules.PASTERDREAM_DEBUG_MODE)) {
            return;
        }
        double range = spawner.getPlayerRange();
        boolean playerNearby = !level.getEntitiesOfClass(Player.class,
                AABB.ofSize(new Vec3(pos.getX(), pos.getY(), pos.getZ()), range, range, range), e -> true).isEmpty();
        if (!playerNearby) {
            return;
        }
        ItemStack egg = spawner.getEgg();
        if (egg.isEmpty() || !(egg.getItem() instanceof SpawnEggItem spawnEgg)) {
            return;
        }
        if (!spawner.isFirstSpawned()) {
            // 首次侦测到玩家：立即生成一只
            spawner.setFirstSpawned(true);
            spawnMob(level, pos, spawnEgg, egg);
        } else if (spawner.getSpawnNumber() >= 1) {
            // 批量生成：每次 10% 概率生成并扣减
            if (level.random.nextDouble() < 0.1) {
                spawner.setSpawnNumber(spawner.getSpawnNumber() - 1);
                spawnMob(level, pos, spawnEgg, egg);
            }
        } else {
            // 批量耗尽：退化为纯装饰的 1 号刷怪笼
            level.setBlock(pos, PDBlocks.DREAM_SPAWNER_1.get().defaultBlockState(), 3);
        }
    }

    /**
     * 按刷怪蛋种类生成生物并播放烟雾/火焰粒子
     *
     * @param level    服务端世界
     * @param pos      方块位置
     * @param spawnEgg 刷怪蛋物品
     * @param eggStack 刷怪蛋物品栈（保留自定义数据）
     */
    private static void spawnMob(ServerLevel level, BlockPos pos, SpawnEggItem spawnEgg, ItemStack eggStack) {
        EntityType<?> type = spawnEgg.getType(eggStack);
        type.spawn(level, ItemStack.EMPTY, null, pos, MobSpawnType.SPAWN_EGG, true, true);
        level.sendParticles(ParticleTypes.SMOKE,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0);
        level.sendParticles(ParticleTypes.FLAME,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.5, 0.5, 0.5, 0);
    }

    // ==================== 右键写入刷怪蛋（原版 DreamSpawner0Pr2） ====================

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return ItemInteractionResult.SUCCESS;
        }
        if (stack.getItem() instanceof SpawnEggItem
                && level.getBlockEntity(pos) instanceof DreamSpawner0BlockEntity spawner) {
            // 与原版一致：整组写入（不消耗玩家手中的刷怪蛋）
            spawner.setEgg(stack.copy());
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, level, pos, eventID, eventParam);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }
}
