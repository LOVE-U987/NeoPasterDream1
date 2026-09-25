package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.MeltdreamChestBlockEntity;
import com.pasterdream.pasterdreammod.api.doll.DollAPI;
import com.pasterdream.pasterdreammod.api.meltdream.MeltDreamEnergyAPI;
import com.pasterdream.pasterdreammod.api.meltdream.MeltDreamEnergyConfigRegistry;
import com.pasterdream.pasterdreammod.config.MeltdreamChestLootConfig;
import com.pasterdream.pasterdreammod.config.PDCommonConfig;
import com.pasterdream.pasterdreammod.registry.PDAdvancements;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDItemTags;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
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

    /** 染梦世界战利品表（隐藏默认，可由数据包覆盖） */
    private static final ResourceKey<LootTable> LOOT_TABLE_DYEDREAM = lootKey("chests/loots_meltdream_chest_0");
    /** 风之旅途 / 灯影世界战利品表（隐藏默认，可由数据包覆盖） */
    private static final ResourceKey<LootTable> LOOT_TABLE_DREAM_OTHER = lootKey("chests/loots_meltdream_chest_1");

    /** 品质附加战利品表（水晶/纪念品；数据包可覆盖） */
    private static final ResourceKey<LootTable> BONUS_COMMON = lootKey("chests/loots_meltdream_chest_bonus_common");
    private static final ResourceKey<LootTable> BONUS_RARE = lootKey("chests/loots_meltdream_chest_bonus_rare");
    private static final ResourceKey<LootTable> BONUS_LEGENDARY = lootKey("chests/loots_meltdream_chest_bonus_legendary");

    /** 隐藏兜底战利品表（非梦境维度 / 主表缺失 / 自定义池全无效；数据包可覆盖） */
    private static final ResourceKey<LootTable> FALLBACK_COMMON = lootKey("chests/loots_meltdream_chest_fallback_common");
    private static final ResourceKey<LootTable> FALLBACK_RARE = lootKey("chests/loots_meltdream_chest_fallback_rare");
    private static final ResourceKey<LootTable> FALLBACK_LEGENDARY = lootKey("chests/loots_meltdream_chest_fallback_legendary");

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
     *   <li>随机决定品质（普通50%/稀有30%/传说20%），仅用于动画/音效与附加档位</li>
     *   <li>按解析优先级填充战利品（默认原版战利品表；自定义开关开启时走配置池）</li>
     *   <li>播放对应品质的音效</li>
     *   <li>设置方块 animation 属性 → GeckoLib 播放开启动画</li>
     *   <li>记录该玩家的冷却时间</li>
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

        // 2. 随机决定品质（仅决定动画/音效，以及自定义模式下的池档位）
        int quality = selectQuality(level.random);

        // 3. 填入战利品（默认走原版战利品表；自定义开关开启时走配置池）
        populateLoot(chest.getItemHandler(), (ServerLevel) level, pos, player, quality);

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

        // 8. 融梦能量奖励（原版 MeltdreamChestPr0：开箱 +2 融梦能量）
        //    系统启用时按「chest generation multiplier」倍率折算（默认 1.0 → +2）
        if (MeltDreamEnergyConfigRegistry.get().enabled().get()) {
            double multiplier = MeltDreamEnergyConfigRegistry.get().chestGenerationMultiplier().get();
            MeltDreamEnergyAPI.addEnergy(player, 2.0 * multiplier);
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

    // ==================== 战利品填充 ====================

    /**
     * 按解析优先级填充战利品：
     * <ol>
     *   <li>自定义开关开启且玩家池有效：基础填池 + 附加</li>
     *   <li>梦境维度主表有效：基础填表 + 附加</li>
     *   <li>非梦境 / 主表缺失或无效 / 自定义池全无效：兜底表 + 附加</li>
     * </ol>
     *
     * @param handler 存货处理器
     * @param level   服务端世界
     * @param pos     方块位置
     * @param player  开箱玩家
     * @param quality 品质（1=普通, 2=稀有, 3=传说）
     */
    private static void populateLoot(ItemStackHandler handler, ServerLevel level, BlockPos pos, Player player, int quality) {
        if (PDCommonConfig.MELTDREAM_CHEST_CUSTOM_LOOT_ENABLED.get()) {
            MeltdreamChestLootConfig.LootEntry[] pool = MeltdreamChestLootConfig.getCustomLoot(quality);
            if (pool.length > 0) {
                fillBaseFromPool(handler, pool, level.random, quality);
                applyQualityExtras(handler, level, pos, player, quality);
                return;
            }
            PasterDreamMod.LOGGER.warn("[MeltdreamChest] 自定义物品池为空或全部无效，回退兜底表");
        }
        ResourceKey<LootTable> baseKey = dimensionLootTable(level);
        LootTable base = baseKey != null ? getTable(level, baseKey) : null;
        if (base != null) {
            fillFromLootTable(handler, base, level, pos, player);
        } else {
            if (baseKey != null) {
                PasterDreamMod.LOGGER.warn("[MeltdreamChest] 维度战利品表缺失或无效：{}，回退兜底表", baseKey.location());
            }
            ResourceKey<LootTable> fallbackKey = fallbackLootTable(quality);
            LootTable fallback = getTable(level, fallbackKey);
            if (fallback != null) {
                fillFromLootTable(handler, fallback, level, pos, player);
            } else {
                PasterDreamMod.LOGGER.warn("[MeltdreamChest] 兜底战利品表缺失或无效：{}", fallbackKey.location());
                clearHandler(handler);
            }
        }
        applyQualityExtras(handler, level, pos, player, quality);
    }

    /**
     * 按维度选择战利品表：染梦 → {@code _0}；风旅/灯影 → {@code _1}；其它维度 → null（走兜底表）。
     *
     * @param level 服务端世界
     * @return 战利品表键；非梦境维度返回 null
     */
    @Nullable
    private static ResourceKey<LootTable> dimensionLootTable(ServerLevel level) {
        if (PDDimensions.isDyedreamWorld(level)) {
            return LOOT_TABLE_DYEDREAM;
        }
        if (PDDimensions.isWindJourneyWorld(level) || PDDimensions.isLampShadowWorld(level)) {
            return LOOT_TABLE_DREAM_OTHER;
        }
        return null;
    }

    /**
     * 从配置池填入基础物品（品质相关件数，保持与旧 fillItems 布局一致）：
     * 普通 8 件（0..7）、稀有 7 件（1..7，slot 0 留给唱片）、传说 8 件（0..7）。
     *
     * @param handler 存货处理器
     * @param pool    玩家配置物品池
     * @param random  随机数源
     * @param quality 品质
     */
    private static void fillBaseFromPool(ItemStackHandler handler, MeltdreamChestLootConfig.LootEntry[] pool, RandomSource random, int quality) {
        clearHandler(handler);
        // 稀有档：slot 0 预留给唱片；普通/传说填 0..7
        for (int i = (quality == 2 ? 1 : 0); i <= 7; i++) {
            handler.setStackInSlot(i, rollFromPool(pool, random));
        }
    }

    /**
     * 从战利品表顺序填入基础物品（0..n-1，n≤8），其余槽位清空。
     *
     * @param handler 存货处理器
     * @param table   战利品表
     * @param level   服务端世界
     * @param pos     方块位置
     * @param player  开箱玩家
     */
    private static void fillFromLootTable(ItemStackHandler handler, LootTable table, ServerLevel level, BlockPos pos, Player player) {
        clearHandler(handler);
        List<ItemStack> base = rollTable(table, level, pos, player);
        int n = Math.min(base.size(), 8);
        if (base.size() > 8) {
            PasterDreamMod.LOGGER.warn("[MeltdreamChest] 战利品表产出 {} 件，超过 8 件已截断", base.size());
        }
        for (int i = 0; i < n; i++) {
            handler.setStackInSlot(i, base.get(i));
        }
    }

    /**
     * 解析战利品表为物品列表（CHEST 参数集，过滤空栈）。
     *
     * @param table  战利品表
     * @param level  服务端世界
     * @param pos    方块位置
     * @param player 开箱玩家
     * @return 非空物品列表
     */
    private static List<ItemStack> rollTable(LootTable table, ServerLevel level, BlockPos pos, Player player) {
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withLuck(player.getLuck())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.CHEST);
        List<ItemStack> out = new ArrayList<>();
        for (ItemStack stack : table.getRandomItems(params)) {
            if (!stack.isEmpty()) {
                out.add(stack);
            }
        }
        return out;
    }

    /**
     * 在基础掉落之上叠加移植版附加内容：唱片/玩偶（代码，标签/API 驱动）+ 水晶/纪念品（附加表）。
     *
     * @param handler 存货处理器
     * @param level   服务端世界
     * @param pos     方块位置
     * @param player  开箱玩家
     * @param quality 品质
     */
    private static void applyQualityExtras(ItemStackHandler handler, ServerLevel level, BlockPos pos, Player player, int quality) {
        RandomSource random = level.random;
        if (quality == 2) {
            applyRareExtras(handler, player, random);
        } else if (quality == 3) {
            applyLegendaryExtras(handler, player, random);
        }
        applyBonus(handler, level, pos, player, quality);
        normalizeCrystal(handler);
    }

    /**
     * 稀有档附加：slot 0 唱片 + 50% 玩偶（放首个空槽）。
     */
    private static void applyRareExtras(ItemStackHandler handler, Player player, RandomSource random) {
        placeDiscAtSlotZero(handler, player, random);
        if (random.nextFloat() < 0.5f) {
            putInFirstEmpty(handler, rollDoll(player, random));
        }
    }

    /**
     * 传说档附加：玩偶（基础件 ≥2 时替换一槽保留至少一件，否则放空槽）。
     */
    private static void applyLegendaryExtras(ItemStackHandler handler, Player player, RandomSource random) {
        int baseCount = countLeadingFilled(handler);
        if (baseCount >= 2) {
            handler.setStackInSlot(random.nextInt(baseCount), rollDoll(player, random));
        } else {
            putInFirstEmpty(handler, rollDoll(player, random));
        }
    }

    /**
     * 应用品质附加表：水晶标签项归位 slot 8，其余（纪念品）放首个空槽。
     *
     * @param handler 存货处理器
     * @param level   服务端世界
     * @param pos     方块位置
     * @param player  开箱玩家
     * @param quality 品质
     */
    private static void applyBonus(ItemStackHandler handler, ServerLevel level, BlockPos pos, Player player, int quality) {
        LootTable bonus = getTable(level, bonusLootTable(quality));
        if (bonus == null) {
            return;
        }
        for (ItemStack stack : rollTable(bonus, level, pos, player)) {
            if (stack.is(PDItemTags.MELTDREAM_CHEST_CRYSTAL)) {
                handler.setStackInSlot(8, stack);
            } else {
                putInFirstEmpty(handler, stack);
            }
        }
    }

    /**
     * 放置唱片：slot 0 为空则直接放入（池路径）；否则基础整体右移 1 位（表路径，slot 8 溢出项被覆盖）。
     */
    private static void placeDiscAtSlotZero(ItemStackHandler handler, Player player, RandomSource random) {
        ItemStack disc = rollDisc(player, random);
        if (disc.isEmpty()) {
            return;
        }
        if (!handler.getStackInSlot(0).isEmpty()) {
            for (int i = 7; i >= 0; i--) {
                handler.setStackInSlot(i + 1, handler.getStackInSlot(i));
            }
        }
        handler.setStackInSlot(0, disc);
    }

    /**
     * 将 handler 内首个水晶标签项与 slot 8 交换（确保水晶实体分支可达；slot 8 已是水晶则跳过）。
     */
    private static void normalizeCrystal(ItemStackHandler handler) {
        for (int i = 0; i < 9; i++) {
            if (i == 8) {
                continue;
            }
            if (handler.getStackInSlot(i).is(PDItemTags.MELTDREAM_CHEST_CRYSTAL)) {
                ItemStack crystal = handler.getStackInSlot(i);
                handler.setStackInSlot(i, handler.getStackInSlot(8));
                handler.setStackInSlot(8, crystal);
                return;
            }
        }
    }

    /**
     * 将物品放入首个空槽；无空槽（或物品为空）则不操作。
     */
    private static void putInFirstEmpty(ItemStackHandler handler, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        int slot = firstEmptySlot(handler);
        if (slot >= 0) {
            handler.setStackInSlot(slot, stack);
        }
    }

    /**
     * 查找首个空槽位。
     *
     * @param handler 存货处理器
     * @return 首个空槽下标；无空槽返回 -1
     */
    private static int firstEmptySlot(ItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 统计从 slot 0 起连续已填充的槽数。
     */
    private static int countLeadingFilled(ItemStackHandler handler) {
        int n = 0;
        while (n < 9 && !handler.getStackInSlot(n).isEmpty()) {
            n++;
        }
        return n;
    }

    /**
     * 清空全部槽位。
     */
    private static void clearHandler(ItemStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            handler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    /**
     * 获取战利品表；缺失或解析为 {@link LootTable#EMPTY} 时返回 null（附加表空池返回非 null 空表）。
     *
     * @param level 服务端世界
     * @param key   战利品表键
     * @return 有效战利品表；缺失/EMPTY 返回 null
     */
    @Nullable
    private static LootTable getTable(ServerLevel level, ResourceKey<LootTable> key) {
        LootTable table = level.getServer().reloadableRegistries().getLootTable(key);
        return (table == null || table == LootTable.EMPTY) ? null : table;
    }

    /**
     * 品质 → 附加表键。
     */
    private static ResourceKey<LootTable> bonusLootTable(int quality) {
        return switch (quality) {
            case 2 -> BONUS_RARE;
            case 3 -> BONUS_LEGENDARY;
            default -> BONUS_COMMON;
        };
    }

    /**
     * 品质 → 兜底表键。
     */
    private static ResourceKey<LootTable> fallbackLootTable(int quality) {
        return switch (quality) {
            case 2 -> FALLBACK_RARE;
            case 3 -> FALLBACK_LEGENDARY;
            default -> FALLBACK_COMMON;
        };
    }

    /**
     * 构造战利品表键（命名空间固定为 pasterdream）。
     */
    private static ResourceKey<LootTable> lootKey(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE,
                ResourceLocation.fromNamespaceAndPath("pasterdream", path));
    }

    /**
     * 从音乐唱片标签中随机选取一张 —— 优先选玩家尚未拥有的，若全部拥有则全池随机。
     * <p>标签为空（数据包移除）时返回空栈并告警。</p>
     *
     * @param player 打开宝箱的玩家
     * @param random 随机数源
     * @return 选中的唱片 ItemStack
     */
    private static ItemStack rollDisc(Player player, RandomSource random) {
        List<Item> allDiscs = new ArrayList<>();
        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(PDItemTags.MUSIC_DISCS)) {
            allDiscs.add(holder.value());
        }
        if (allDiscs.isEmpty()) {
            PasterDreamMod.LOGGER.warn("[MeltdreamChest] 唱片标签 {} 为空，跳过唱片掉落", PDItemTags.MUSIC_DISCS.location());
            return ItemStack.EMPTY;
        }
        List<Item> unowned = allDiscs.stream()
                .filter(disc -> player.getInventory().countItem(disc) <= 0)
                .toList();
        List<Item> pool = unowned.isEmpty() ? allDiscs : unowned;
        return new ItemStack(pool.get(random.nextInt(pool.size())));
    }

    /**
     * 从 DollAPI 战利品池随机选取一个玩偶 —— 优先选玩家尚未拥有的，若全部拥有则全池随机。
     * <p>池为空（数据包/注册异常）时返回空栈并告警。</p>
     *
     * @param player 打开宝箱的玩家
     * @param random 随机数源
     * @return 选中的玩偶 ItemStack
     */
    private static ItemStack rollDoll(Player player, RandomSource random) {
        List<Item> allDolls = DollAPI.getLootItems();
        if (allDolls.isEmpty()) {
            PasterDreamMod.LOGGER.warn("[MeltdreamChest] DollAPI 玩偶战利品池为空，跳过玩偶掉落");
            return ItemStack.EMPTY;
        }
        List<Item> unowned = allDolls.stream()
                .filter(doll -> player.getInventory().countItem(doll) <= 0)
                .toList();
        List<Item> pool = unowned.isEmpty() ? allDolls : unowned;
        return new ItemStack(pool.get(random.nextInt(pool.size())));
    }

    /**
     * 从一个物品池中按权重随机抽取一个物品。
     *
     * @param pool   物品池
     * @param random 随机数源
     * @return 选中的物品（副本）；池为空返回空栈
     */
    private static ItemStack rollFromPool(MeltdreamChestLootConfig.LootEntry[] pool, RandomSource random) {
        if (pool == null || pool.length == 0) {
            return ItemStack.EMPTY;
        }
        int totalWeight = 0;
        for (MeltdreamChestLootConfig.LootEntry entry : pool) {
            totalWeight += entry.weight();
        }
        if (totalWeight <= 0) {
            return pool[0].stack().copy();
        }
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
