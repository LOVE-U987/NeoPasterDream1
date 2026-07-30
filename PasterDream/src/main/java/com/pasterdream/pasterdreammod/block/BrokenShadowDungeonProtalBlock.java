package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksDungeon;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksFurniture;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksVegetation;
import com.pasterdream.pasterdreammod.registry.items.PDItemsMaterials;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * 暗影地牢传送门（broken_shadow_dungeon_protal，保留原版拼写）
 * <p>
 * 同一方块通过 animation 属性控制状态：
 * <ul>
 *   <li>animation=0（破损）：初始状态，需修复；</li>
 *   <li>animation=1（修复中）：播放修复动画；</li>
 *   <li>animation=2（已修复）：可交互的地牢传送门（替换原 shadow_dungeon_portal）。</li>
 * </ul>
 * 修复后不再替换为另一个方块，而是保留此方块并切换纹理/模型/动画。
 */
public class BrokenShadowDungeonProtalBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final MapCodec<BrokenShadowDungeonProtalBlock> CODEC =
            simpleCodec(BrokenShadowDungeonProtalBlock::new);

    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 2);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    /** 五层地牢的层配置：随机上界 / 模板名表 / Y 偏移 */
    private record Layer(int range, int yOffset, String[] templates) {
    }

    private static final Layer[] LAYERS = {
            new Layer(6, -28, new String[]{"shadow_dungeon_0", "shadow_dungeon_1", "shadow_dungeon_2",
                    "shadow_dungeon_3", "shadow_dungeon_4", "shadow_dungeon_5"}),
            new Layer(4, -37, new String[]{"shadow_dungeon_10", "shadow_dungeon_11",
                    "shadow_dungeon_12", "shadow_dungeon_13"}),
            new Layer(3, -46, new String[]{"shadow_dungeon_7", "shadow_dungeon_8", "shadow_dungeon_9"}),
            new Layer(2, -55, new String[]{"shadow_dungeon_6", "shadow_dungeon_14"}),
            new Layer(2, -64, new String[]{"shadow_dungeon_npc_0", "shadow_dungeon_npc_1"}),
    };

    /**
     * 构造暗影地牢传送门方块
     *
     * @param properties 方块属性
     */
    public BrokenShadowDungeonProtalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return box(3, 3, 3, 13, 13, 13);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ANIMATION, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean water = context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, water);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!drops.isEmpty()) {
            return drops;
        }
        return Collections.singletonList(new ItemStack(this));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.literal("§7手持 §e黑金属 §7和 §e影灯 §7修复核心"));
    }

    // ==================== tick 冷却（原 ShadowDungeonPortalBlock） ====================

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        level.scheduleTick(pos, this, 20);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (W4DataBlockEntity.getBooleanAt(level, pos, "cd")) {
            double time = W4DataBlockEntity.getDoubleAt(level, pos, "time") + 1;
            W4DataBlockEntity.putDoubleAt(level, pos, "time", time);
            if (time >= 1800) {
                W4DataBlockEntity.putBooleanAt(level, pos, "cd", false);
                W4DataBlockEntity.putDoubleAt(level, pos, "time", 0);
            }
        }
        level.scheduleTick(pos, this, 20);
    }

    // ==================== 右键交互 ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        // ===== 破损状态修复逻辑（原 BrokenShadowDungeonProtalBlock 逻辑） =====
        if (pos.getY() <= 20) {
            if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("地牢的结构被破坏，传送门没有反应"), false);
            }
            return InteractionResult.SUCCESS;
        }

        if (player.getAbilities().instabuild) {
            startRepair(level, pos, player);
            return InteractionResult.SUCCESS;
        }

        if (hasAdvancement(player, "achievement_hide_14")) {
            boolean lightMainMetalOff =
                    player.getMainHandItem().getItem() == PDBlocksVegetation.SHADOW_LIGHT_0.get().asItem()
                            && player.getOffhandItem().getItem() == PDItemsMaterials.BLACKMETAL_INGOT.get();
            boolean metalMainLightOff =
                    player.getMainHandItem().getItem() == PDItemsMaterials.BLACKMETAL_INGOT.get()
                            && player.getOffhandItem().getItem() == PDBlocksVegetation.SHADOW_LIGHT_0.get().asItem();
            if (lightMainMetalOff || metalMainLightOff) {
                startRepair(level, pos, player);
                ItemStack metal = new ItemStack(PDItemsMaterials.BLACKMETAL_INGOT.get());
                player.getInventory().clearOrCountMatchingItems(
                        s -> metal.getItem() == s.getItem(), 1, player.inventoryMenu.getCraftSlots());
                ItemStack light = new ItemStack(PDBlocksVegetation.SHADOW_LIGHT_0.get());
                player.getInventory().clearOrCountMatchingItems(
                        s -> light.getItem() == s.getItem(), 1, player.inventoryMenu.getCraftSlots());
            } else if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("双手持§e黑金属§f和§e影灯§f以修复核心"), true);
            }
        } else if (!player.level().isClientSide()) {
            player.displayClientMessage(
                    Component.literal("缺少知识 你还不理解这个核心的工作原理和修复方法"), true);
        }
        return InteractionResult.SUCCESS;
    }

    /** 修复演出 + 20 tick 后替换为完整暗影地牢传送门 */
    private static void startRepair(Level level, BlockPos pos, Player player) {
        // 修复演出：animation=1 + smithing_table 音效 + 末地烛粒子
        setAnimation(level, pos, 1);
        if (!level.isClientSide()) {
            level.playSound(null, pos, SoundEvents.SMITHING_TABLE_USE, SoundSource.NEUTRAL, 1, 1);
        }
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 24, 1, 1, 1, 0.3);
        }

        // 20 tick 后按原模组方式替换为完整核心方块
        ServerScheduler.schedule(20, () -> {
            level.setBlock(pos, PDBlocksFurniture.SHADOW_DUNGEON_PORTAL.get().defaultBlockState(), 3);
            if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("核心已修复"), true);
            }
        });
    }

    /** 已修复状态的交互逻辑：出口传送 + 地牢生成（原 ShadowDungeonPortalBlock.useWithoutItem） */
    public static InteractionResult handleFixedInteraction(Level level, BlockPos pos, Player player) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        if (player.getMainHandItem().getItem() == PDItemsMaterials.TABITEM_1.get()) {
            W4DataBlockEntity.putBooleanAt(level, pos, "exit", true);
            if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("已设置为出口！"), false);
            }
            return InteractionResult.SUCCESS;
        }

        if (W4DataBlockEntity.getBooleanAt(level, pos, "exit")) {
            // 出口传送：倒计时后传送到地表
            if (!level.isClientSide()) {
                level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 2, 1);
            }
            setAnimation(level, pos, 0);
            setAnimation(level, pos, 1);
            countdownTeleport(level, pos, x + 0.5, y + 66, z + 2.5, false);
            return InteractionResult.SUCCESS;
        }

        boolean structureIntact =
                level.getBlockState(pos.offset(0, -2, 0)).getBlock() == PDBlocksDungeon.SHADOW_DUNGEON_BLOCK_0.get()
                        && level.getBlockState(pos.offset(1, -2, 0)).getBlock() == PDBlocksDungeon.SHADOW_DUNGEON_BLOCK_1.get();
        if (structureIntact) {
            if (!W4DataBlockEntity.getBooleanAt(level, pos, "cd")) {
                generateDungeon(level, pos);
                if (!level.isClientSide()) {
                    level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.NEUTRAL, 2, 1);
                }
                setAnimation(level, pos, 0);
                setAnimation(level, pos, 1);
                W4DataBlockEntity.putBooleanAt(level, pos, "cd", true);
                countdownTeleport(level, pos, x - 3.5, y - 19, z + 0.5, true);
                ServerScheduler.schedule(60, () -> {
                    setAnimation(level, pos, 0);
                    if (!level.isClientSide()) {
                        level.playSound(null, BlockPos.containing(x, y - 19, z),
                                SoundEvents.END_PORTAL_SPAWN, SoundSource.NEUTRAL, 1, 1);
                    }
                });
            } else if (!player.level().isClientSide()) {
                long remain = (long) (1800 - W4DataBlockEntity.getDoubleAt(level, pos, "time"));
                player.displayClientMessage(
                        Component.literal("地牢刷新冷却中 剩余：" + remain + "秒"), true);
            }
        } else if (!player.level().isClientSide()) {
            player.displayClientMessage(Component.literal("遗迹结构不完整"), false);
        }
        return InteractionResult.SUCCESS;
    }

    // ==================== 地牢生成与传送（原 ShadowDungeonPortalBlock） ====================

    /** 8 格内玩家 3 秒倒计时传送（进入地牢时附带成就链授予） */
    private static void countdownTeleport(Level level, BlockPos pos,
                                          double tx, double ty, double tz, boolean enterDungeon) {
        Vec3 center = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        for (Entity entity : level.getEntitiesOfClass(Entity.class,
                new AABB(center, center).inflate(8 / 2d), e -> true)) {
            if (!(entity instanceof Player)) {
                continue;
            }
            message(entity, "传送倒计时：3");
            ServerScheduler.schedule(20, () -> message(entity, "传送倒计时：2"));
            ServerScheduler.schedule(40, () -> message(entity, "传送倒计时：1"));
            if (enterDungeon) {
                ServerScheduler.schedule(60, () -> {
                    if (entity instanceof ServerPlayer sp
                            && hasAdvancement(sp, "achievement_shadow_b_0")
                            && !hasAdvancement(sp, "achievement_shadow_c_0")) {
                        awardAdvancement(sp, "achievement_shadow_c_0");
                    }
                    ServerScheduler.schedule(1, () -> teleport(entity, tx, ty, tz));
                });
            } else {
                ServerScheduler.schedule(60, () -> teleport(entity, tx, ty, tz));
            }
        }
    }

    /** 生成五层随机地牢（原 ShadowDungeonPortalBlock.generateDungeon） */
    private static void generateDungeon(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        int[] rolls = new int[LAYERS.length];
        for (int i = 0; i < LAYERS.length; i++) {
            rolls[i] = Mth.nextInt(RandomSource.create(), 1, LAYERS[i].range());
            W4DataBlockEntity.putDoubleAt(level, pos, "layer" + (i + 1), rolls[i]);
        }
        // 逐层门
        level.setBlock(BlockPos.containing(x, y - 29, z), PDBlocksDungeon.SHADOW_DUNGEON_DOOR_0.get().defaultBlockState(), 3);
        level.setBlock(BlockPos.containing(x, y - 38, z), PDBlocksDungeon.SHADOW_DUNGEON_DOOR_0.get().defaultBlockState(), 3);
        level.setBlock(BlockPos.containing(x, y - 47, z), PDBlocksDungeon.SHADOW_DUNGEON_DOOR_0.get().defaultBlockState(), 3);
        level.setBlock(BlockPos.containing(x, y - 56, z), PDBlocksDungeon.SHADOW_DUNGEON_DOOR_0.get().defaultBlockState(), 3);
        level.setBlock(BlockPos.containing(x, y - 65, z), PDBlocksDungeon.SHADOW_DUNGEON_DOOR_0.get().defaultBlockState(), 3);
        level.setBlock(BlockPos.containing(x, y - 67, z + 3), PDBlocksDungeon.SHADOWDUNGEONDOOR_2.get().defaultBlockState(), 3);
        // 清场
        Vec3 center = new Vec3(x, y - 42, z);
        for (Entity entity : level.getEntitiesOfClass(Entity.class,
                new AABB(center, center).inflate(44 / 2d), e -> true)) {
            if (!entity.level().isClientSide()) {
                entity.discard();
            }
        }
        // 分帧放置各层结构
        for (int i = 0; i < LAYERS.length; i++) {
            Layer layer = LAYERS[i];
            String template = layer.templates()[rolls[i] - 1];
            ServerScheduler.schedule(i + 1, () -> placeTemplate(serverLevel,
                    BlockPos.containing(x - 11, y + layer.yOffset(), z - 11), template));
        }
    }

    private static void placeTemplate(ServerLevel level, BlockPos target, String name) {
        StructureTemplate template = level.getStructureManager().getOrCreate(
                ResourceLocation.fromNamespaceAndPath("pasterdream", name));
        if (template != null) {
            template.placeInWorld(level, target, target,
                    new StructurePlaceSettings()
                            .setRotation(Rotation.NONE)
                            .setMirror(Mirror.NONE)
                            .setIgnoreEntities(false),
                    level.random, 3);
        }
    }

    private static void teleport(Entity entity, double x, double y, double z) {
        entity.teleportTo(x, y, z);
        if (entity instanceof ServerPlayer sp) {
            sp.connection.teleport(x, y, z, entity.getYRot(), entity.getXRot());
        }
    }

    private static void message(Entity entity, String text) {
        if (entity instanceof Player player && !player.level().isClientSide()) {
            player.displayClientMessage(Component.literal(text), true);
        }
    }

    private static boolean hasAdvancement(Player player, String name) {
        if (!(player instanceof ServerPlayer serverPlayer) || !(serverPlayer.level() instanceof ServerLevel)) {
            return false;
        }
        AdvancementHolder holder = serverPlayer.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath("pasterdream", name));
        return holder != null && serverPlayer.getAdvancements().getOrStartProgress(holder).isDone();
    }

    private static void awardAdvancement(ServerPlayer player, String name) {
        AdvancementHolder holder = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath("pasterdream", name));
        if (holder == null) {
            return;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        if (!progress.isDone()) {
            for (String criteria : progress.getRemainingCriteria()) {
                player.getAdvancements().award(holder, criteria);
            }
        }
    }

    /** 设置 animation 属性 */
    public static void setAnimation(Level level, BlockPos pos, int value) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty prop
                && prop.getPossibleValues().contains(value)) {
            level.setBlock(pos, state.setValue(prop, value), 3);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new W4GeoDataBlockEntity(PDBlockEntitiesFurniture.BROKEN_SHADOW_DUNGEON_PROTAL.get(), pos, state);
    }
}
