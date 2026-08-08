package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.MeltdreamChestBlockEntity;
import com.pasterdream.pasterdreammod.api.doll.DollAPI;
import com.pasterdream.pasterdreammod.api.doll.DollResult;
import com.pasterdream.pasterdreammod.config.MeltdreamChestLootConfig;
import com.pasterdream.pasterdreammod.registry.PDAdvancements;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 融梦水晶箱方块 - 使用 GeckoLib 动画的三级随机宝藏箱
 *
 * animation 属性说明：
 * - 0：未激活/可交互状态（盖子闭合，此状态下才响应右键开箱）
 * - 1：普通品质开启动画
 * - 2：稀有品质开启动画
 * - 3：传说品质开启动画
 *
 * 右键点击时随机决定品质，播放对应动画，
 * 动画结束后通过 tick 调度替换为 meltdream_chest_open
 */
public class MeltdreamChestBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final MapCodec<MeltdreamChestBlock> CODEC = simpleCodec(MeltdreamChestBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 3);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /** 各品质等级的动画播放时长（tick），用于调度弹出阶段 */
    public static final int[] ANIMATION_DURATIONS = {0, 70, 90, 110};

    private static final VoxelShape SHAPE_NORTH = Block.box(1, 0, 1, 15, 14, 15);
    private static final VoxelShape SHAPE_EAST = Block.box(1, 0, 1, 15, 14, 15);

    /**
     * 构造融梦水晶箱方块
     *
     * @param properties 方块属性
     */
    public MeltdreamChestBlock(Properties properties) {
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
        return SHAPE_NORTH;
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
        return new MeltdreamChestBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // 客户端无 tick 逻辑（动画由 GeckoLib 驱动），不注册 ticker
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, PDBlockEntities.MELTDREAM_CHEST.get(),
                MeltdreamChestBlockEntity::serverTick);
    }

    // ==================== 右键交互 ====================

    /**
     * 右键点击融梦水晶箱时的完整流程：
     * <ol>
     *   <li>检查玩家冷却（每人独立）</li>
     *   <li>随机决定品质（普通50%/稀有30%/传说20%）</li>
     *   <li>向箱子填入 8 个随机战利品 + 最后 1 个融梦水晶</li>
     *   <li>播放对应品质的音效</li>
     *   <li>设置方块 animation 属性 → GeckoLib 播放开启动画</li>
     *   <li>记录该玩家的冷却时间（1 分钟）</li>
     *   <li>状态机由 {@link MeltdreamChestBlockEntity#serverTick} 接管</li>
     * </ol>
     */
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (state.getValue(ANIMATION) != 0) return InteractionResult.CONSUME;

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof MeltdreamChestBlockEntity chest)) return InteractionResult.PASS;

        // 1. 检查冷却
        if (!chest.canOpen(player)) {
            // 冷却中提示
            if (player instanceof ServerPlayer sp) {
                sp.displayClientMessage(
                        net.minecraft.network.chat.Component.translatable("message.pasterdream.meltdream_chest.cooldown"),
                        true
                );
            }
            return InteractionResult.CONSUME;
        }

        // 2. 随机决定品质
        int quality = selectQuality(level.random);
        MeltdreamChestLootConfig.LootEntry[] pool = switch (quality) {
            case 2 -> getRareLoot();
            case 3 -> getLegendaryLoot();
            default -> getCommonLoot();
        };

        // 3. 填入物品（根据品质决定掉落策略）
        fillItems(chest.getItemHandler(), pool, level.random, player, quality);

        // 4. 播放音效
        SoundEvent sound = quality == 3 ? PDSounds.MELTDREAM_CHEST.get() : PDSounds.MELTDREAM_CHEST_0.get();
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.5f, 1.0f);

        // 5. 设置动画状态（同类型方块，BlockEntity 维持不变）
        level.setBlock(pos, state.setValue(ANIMATION, quality), 3);

        // 6. 设置冷却 + 初始化状态机（chest 实例未变）
        chest.setCooldown(player);
        chest.initOpening(player, quality);

        // 7. 宝藏成就（原版 MeltdreamChestPr0：先 treasure_start，再按维度）
        if (player instanceof ServerPlayer sp) {
            awardTreasureAdvancements(sp, level);
        }

        return InteractionResult.CONSUME;
    }

    /**
     * 开箱时授予宝藏树成就（原版 MeltdreamChestPr0Procedure）。
     * <ol>
     *   <li>始终尝试 {@code achievement_treasure_start}</li>
     *   <li>染梦维度 → {@code achievement_treasure_dyedream}</li>
     *   <li>风旅维度 → {@code achievement_treasure_wind_journey}</li>
     * </ol>
     *
     * @param player 开箱玩家
     * @param level  箱子所在维度
     */
    private static void awardTreasureAdvancements(ServerPlayer player, Level level) {
        PDAdvancements.award(player, PDAdvancements.TREASURE_START);
        if (PDDimensions.isWindJourneyWorld(level)) {
            PDAdvancements.award(player, PDAdvancements.TREASURE_WIND_JOURNEY);
        } else if (PDDimensions.isDyedreamWorld(level)) {
            PDAdvancements.award(player, PDAdvancements.TREASURE_DYEDREAM);
        }
    }

    /**
     * 随机选择宝箱品质等级
     * <p>概率分布：普通 50% → 稀有 30% → 传说 20%</p>
     *
     * @param random 随机数源
     * @return 品质等级 (1=普通, 2=稀有, 3=传说)
     */
    private static int selectQuality(net.minecraft.util.RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.50f) return 1;
        if (roll < 0.80f) return 2;
        return 3;
    }

    /**
     * 向存货中填入战利品 —— 根据品质决定掉落策略
     * <ul>
     *   <li>普通品质 (1)：8 个随机食物，第 9 格空（不放水晶）</li>
     *   <li>稀有品质 (2)：第 1 格唱片 + 第 2~7 格随机稀有材料，第 9 格空（不放水晶），50% 概率额外掉一个玩偶</li>
     *   <li>传说品质 (3)：前 8 格随机传说物品（其中 1 格替换为玩偶），第 9 格固定 1 个融梦水晶碎片</li>
     * </ul>
     *
     * @param handler 存货处理器（9 格）
     * @param pool    物品池数组
     * @param random  随机数源
     * @param player  打开宝箱的玩家（用于判断唱片拥有情况）
     * @param quality 品质等级（1=普通, 2=稀有, 3=传说）
     */
    private static void fillItems(ItemStackHandler handler, MeltdreamChestLootConfig.LootEntry[] pool, RandomSource random, Player player, int quality) {
        if (quality == 3) {
            // 传说品质：第 9 格固定 1 个融梦水晶碎片（弹出时生成水晶实体），前 8 格掉落传说稀有度物品
            for (int i = 0; i < 8; i++) {
                handler.setStackInSlot(i, rollFromPool(pool, random));
            }
            handler.setStackInSlot(8, new ItemStack(PDItems.MELTDREAM_CRYSTAL_0.get()));
            // 额外：将前 8 格中的随机 1 格替换为玩偶（优先玩家未拥有的）
            int dollSlot = random.nextInt(8);
            handler.setStackInSlot(dollSlot, rollDoll(player, random));
        } else if (quality == 1) {
            // 普通品质：8 个随机食物，不放融梦水晶碎片
            for (int i = 0; i < 8; i++) {
                handler.setStackInSlot(i, rollFromPool(pool, random));
            }
            handler.setStackInSlot(8, ItemStack.EMPTY);
        } else {
            // 稀有品质：第 1 格唱片 + 第 2~7 格随机稀有材料，不放融梦水晶碎片
            handler.setStackInSlot(0, rollDisc(player, random));
            for (int i = 1; i < 8; i++) {
                handler.setStackInSlot(i, rollFromPool(pool, random));
            }
            handler.setStackInSlot(8, ItemStack.EMPTY);
            // 额外：稀有品质有 50% 概率额外掉落一个玩偶（优先玩家未拥有的）
            if (random.nextFloat() < 0.5f) {
                int slot = 1 + random.nextInt(8);
                handler.setStackInSlot(slot, rollDoll(player, random));
            }
        }
    }

    /**
     * 从所有 13 张唱片中随机选取一张 —— 优先选玩家尚未拥有的，
     * 若全部拥有则全池随机
     *
     * @param player 打开宝箱的玩家
     * @param random 随机数源
     * @return 选中的唱片 ItemStack
     */
    private static ItemStack rollDisc(Player player, net.minecraft.util.RandomSource random) {
        List<Item> allDiscs = List.of(
                PDItems.SWEETDREAM_DISC.get(),
                PDItems.SNOWFALLDREAM_DISC.get(),
                PDItems.AARONCOS_DISC.get(),
                PDItems.DYEDREAM_WORLD_DISC.get(),
                PDItems.WIND_JOURNEY_DISC.get(),
                PDItems.WIND_JOURNEY_1_DISC.get(),
                PDItems.WIND_JOURNEY_DEPARTURE_DISC.get(),
                PDItems.WIND_JOURNEY_MIDSUMMER_DISC.get(),
                PDItems.DREAM_MEADOW_DISC.get(),
                PDItems.DREAM_MEADOW_DAISY_DISC.get(),
                PDItems.DREAM_HEATH_DISC.get(),
                PDItems.DREAM_TAIGA_DISC.get(),
                PDItems.DREAM_DELTA_DISC.get()
        );
        // 筛选玩家背包中未拥有的唱片
        List<Item> unowned = allDiscs.stream()
                .filter(disc -> player.getInventory().countItem(disc) <= 0)
                .toList();
        List<Item> pool2 = unowned.isEmpty() ? allDiscs : unowned;
        return new ItemStack(pool2.get(random.nextInt(pool2.size())));
    }

    /**
     * 从所有玩偶/雕像中随机选取一个 —— 优先选玩家尚未拥有的，
     * 若全部拥有则回退全池随机（与唱片 {@link #rollDisc} 逻辑完全一致）。
     * <p>包含原版注册玩偶与 {@link DollAPI} 动态注册的自定义玩偶。</p>
     *
     * @param player 打开宝箱的玩家
     * @param random 随机数源
     * @return 选中的玩偶 ItemStack
     */
    private static ItemStack rollDoll(Player player, net.minecraft.util.RandomSource random) {
        List<Item> allDolls = getAllDolls();
        // 筛选玩家背包中未拥有的玩偶，全部拥有则回退全池随机
        List<Item> unowned = allDolls.stream()
                .filter(doll -> player.getInventory().countItem(doll) <= 0)
                .toList();
        List<Item> pool = unowned.isEmpty() ? allDolls : unowned;
        return new ItemStack(pool.get(random.nextInt(pool.size())));
    }

    /**
     * 获取所有可掉落的玩偶/雕像物品列表。
     * <p>合并 PDItems 中注册的旧玩偶与 {@link DollAPI} 注册的自定义玩偶。</p>
     *
     * @return 玩偶物品列表
     */
    private static List<Item> getAllDolls() {
        List<Item> dolls = new ArrayList<>();
        // 原版注册的旧玩偶/雕像
        dolls.add(PDItems.QIN_DOLL_0.get());
        dolls.add(PDItems.LITTLE_PURPLE_DOLL_0.get());
        dolls.add(PDItems.GOLDEN_FOX_SCULPTURE.get());
        dolls.add(PDItems.LOVE_U_DOLL.get());
        dolls.add(PDItems.EOUL_DOLL.get());
        // DollAPI 动态注册的自定义玩偶（phantom_daze、mini_beixu_doll、wuyu_doll 等）
        for (DollResult result : DollAPI.getRegistrations()) {
            dolls.add(result.item().get());
        }
        return dolls;
    }

    // ==================== 刻调度（已移除 — 由 BlockEntity.serverTick 接管） ====================

    /**
     * 获取普通品质物品池 —— 从 {@link MeltdreamChestLootConfig} 读取（懒加载与回退逻辑内聚在配置类）。
     * <p>普通品质战利品默认只掉落各种简单食物。</p>
     *
     * @return 普通品质物品池数组
     */
    private static MeltdreamChestLootConfig.LootEntry[] getCommonLoot() {
        return MeltdreamChestLootConfig.getCommonLoot();
    }

    /**
     * 获取稀有品质物品池 —— 染梦高级材料与中级装备（懒加载）。
     *
     * @return 稀有品质物品池数组
     */
    private static MeltdreamChestLootConfig.LootEntry[] getRareLoot() {
        return MeltdreamChestLootConfig.getRareLoot();
    }

    /**
     * 获取传说品质物品池 —— 染梦维度顶级装备与稀有材料（懒加载）。
     *
     * @return 传说品质物品池数组
     */
    private static MeltdreamChestLootConfig.LootEntry[] getLegendaryLoot() {
        return MeltdreamChestLootConfig.getLegendaryLoot();
    }

    /**
     * 从一个物品池中按权重随机抽取一个物品
     *
     * @param pool   物品池
     * @param random 随机数源
     * @return 选中的物品（副本）
     */
    private static ItemStack rollFromPool(MeltdreamChestLootConfig.LootEntry[] pool, net.minecraft.util.RandomSource random) {
        int totalWeight = 0;
        for (MeltdreamChestLootConfig.LootEntry entry : pool) totalWeight += entry.weight();
        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        for (MeltdreamChestLootConfig.LootEntry entry : pool) {
            cumulative += entry.weight();
            if (roll < cumulative) {
                return entry.stack().copy();
            }
        }
        return pool[0].stack().copy();
    }

    // ==================== 方块破坏时掉落物品 ====================

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MeltdreamChestBlockEntity chest) {
                for (int i = 0; i < chest.getItemHandler().getSlots(); i++) {
                    ItemStack stack = chest.getItemHandler().getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        popResource(level, pos, stack);
                    }
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        ItemStack stack = new ItemStack(this);
        // 优先使用战利品上下文携带的方块实体（玩家挖掘路径）
        BlockEntity be = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        MeltdreamChestBlockEntity chest = (be instanceof MeltdreamChestBlockEntity c) ? c : null;
        if (chest == null) {
            // 兜底：TNT 等爆炸破坏路径可能未携带 BLOCK_ENTITY，通过 ORIGIN 从世界重新获取方块实体
            Vec3 origin = params.getOptionalParameter(LootContextParams.ORIGIN);
            if (origin != null && params.getLevel() instanceof ServerLevel serverLevel) {
                BlockEntity worldBe = serverLevel.getBlockEntity(BlockPos.containing(origin));
                if (worldBe instanceof MeltdreamChestBlockEntity chestFromWorld) {
                    chest = chestFromWorld;
                }
            }
        }
        if (chest != null) {
            CompoundTag tag = new CompoundTag();
            chest.saveCooldownsToTag(tag);
            if (!tag.isEmpty()) {
                stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
            }
        }
        return List.of(stack);
    }
}
