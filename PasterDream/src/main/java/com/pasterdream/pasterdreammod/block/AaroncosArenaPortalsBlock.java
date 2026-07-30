package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDBiomes;
import com.pasterdream.pasterdreammod.registry.PDDimensions;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import com.pasterdream.pasterdreammod.world.PortalInfectionData;
import net.minecraft.core.Holder;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
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
 * 触碰时传送至竞技场维度。继承 SlabBlock 实现半砖形状，具有发光效果。
 * <p>
 * 传送条件（原 {@code AaroncosArenaPortalsPr0}）：已完成 {@code achievement_shadow_d_0}
 * 或创造模式；否则提示尚未完成前置进度。
 * <p>
 * 感染效果：传送门周围的地面方块会被转化为灯影之下风格的方块；
 * 若传送门位于 {@code aaroncos_arena_biome} 内，则扩大半径覆盖整个小群系。
 */
public class AaroncosArenaPortalsBlock extends SlabBlock {

    /** 普通模式：感染半径 */
    private static final int NORMAL_RADIUS = 6;
    /** 普通模式：每 tick 最多处理候选数 */
    private static final int NORMAL_CANDIDATES = 10;
    /** 普通模式：调度 tick 间隔 */
    private static final int NORMAL_INTERVAL = 12;

    /** 遗迹群系模式：覆盖整个小群系的大半径（配合缩小的群系参数使用） */
    private static final int BIOME_RADIUS = 48;
    /** 遗迹群系模式：每 tick 处理候选数，保证能在合理时间内覆盖大半径 */
    private static final int BIOME_CANDIDATES = 80;
    /** 遗迹群系模式：调度 tick 间隔，缩短以加快群系级覆盖 */
    private static final int BIOME_INTERVAL = 3;

    /** 单次候选位置的最大尝试次数，避免有机形状边缘导致过多空转 */
    private static final int MAX_ATTEMPTS_MULTIPLIER = 6;

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
     * 每 tick 更新时触发感染效果。
     * 以受控速率将周围方块转化为灯影之下风格，保持动态可见但不过度刷屏。
     *
     * @param state  当前方块状态
     * @param level  当前世界
     * @param pos    方块位置
     * @param random 随机数生成器
     */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isClientSide) {
            infectSurroundingBlocks(level, pos, random);
        }
        level.scheduleTick(pos, this, getInfectionInterval(level, pos));
    }

    /**
     * 方块放置时立即开始感染流程
     */
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            level.scheduleTick(pos, this, getInfectionInterval(serverLevel, pos));
        }
    }

    /**
     * 感染周围方块。
     * 以有机、不规则的范围逐步将地面、水体与植被转化为灯影之下风格；
     * 处于竞技场群系内时使用更大的半径与更短的间隔，以覆盖整个小群系。
     *
     * @param level     服务端世界
     * @param centerPos 传送门位置
     * @param random    随机源
     */
    private void infectSurroundingBlocks(ServerLevel level, BlockPos centerPos, RandomSource random) {
        int radius = getInfectionRadius(level, centerPos);
        int maxCandidates = getInfectionCandidates(level, centerPos);
        int maxAttempts = maxCandidates * MAX_ATTEMPTS_MULTIPLIER;
        int converted = 0;
        int attempts = 0;

        while (converted < maxCandidates && attempts < maxAttempts) {
            attempts++;

            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dy = random.nextInt(3) - 1;
            int dz = random.nextInt(radius * 2 + 1) - radius;

            if (!isWithinOrganicShape(dx, dy, dz, radius, random)) {
                continue;
            }

            BlockPos targetPos = centerPos.offset(dx, dy, dz);
            if (!canInfect(level, targetPos)) {
                continue;
            }

            infectBlock(level, targetPos, random, centerPos);
            converted++;
        }
    }

    /**
     * 根据当前群系返回感染半径。
     * 位于竞技场群系内时使用 {@link #BIOME_RADIUS} 覆盖整个小群系，
     * 否则使用 {@link #NORMAL_RADIUS} 保持普通传送门的小范围效果。
     *
     * @param level 服务端世界
     * @param pos   传送门位置
     * @return 当前应使用的感染半径
     */
    private int getInfectionRadius(ServerLevel level, BlockPos pos) {
        return isInArenaBiome(level, pos) ? BIOME_RADIUS : NORMAL_RADIUS;
    }

    /**
     * 根据当前群系返回每 tick 最大候选处理数。
     *
     * @param level 服务端世界
     * @param pos   传送门位置
     * @return 当前应使用的候选数
     */
    private int getInfectionCandidates(ServerLevel level, BlockPos pos) {
        return isInArenaBiome(level, pos) ? BIOME_CANDIDATES : NORMAL_CANDIDATES;
    }

    /**
     * 根据当前群系返回调度 tick 间隔。
     *
     * @param level 服务端世界
     * @param pos   传送门位置
     * @return 当前应使用的 tick 间隔
     */
    private int getInfectionInterval(ServerLevel level, BlockPos pos) {
        return isInArenaBiome(level, pos) ? BIOME_INTERVAL : NORMAL_INTERVAL;
    }

    /**
     * 判断指定位置是否属于亚伦柯斯竞技场群系。
     *
     * @param level 服务端世界
     * @param pos   待检测位置
     * @return true 若该位置群系为 {@code pasterdream:aaroncos_arena_biome}
     */
    private boolean isInArenaBiome(ServerLevel level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);
        return biome.is(PDBiomes.BIOME_AARONCOS_ARENA);
    }

    /**
     * 判断目标位置是否处于有机的感染形状内。
     * 使用基于坐标的伪噪声叠加随机抖动，使边界呈自然渗出的不规则形态。
     *
     * @param dx     相对 X
     * @param dy     相对 Y
     * @param dz     相对 Z
     * @param radius 当前感染半径
     * @param random 随机源
     * @return true 若该位置位于当前有机边界内
     */
    private boolean isWithinOrganicShape(int dx, int dy, int dz, int radius, RandomSource random) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double noise = Math.sin(dx * 1.7 + dz * 0.5) * 0.6
                + Math.cos(dy * 1.3 + dx * 0.7) * 0.6
                + Math.sin((dx + dz) * 0.9) * 0.4;
        double effectiveRadius = radius + noise * 1.5 + random.nextFloat() * 1.2;
        return distance <= effectiveRadius;
    }

    /**
     * 判断方块是否可以被感染。
     * 包含地面、水源与低矮植被；已转化的方块会跳过以避免重复操作。
     *
     * @param level 服务端世界
     * @param pos   目标位置
     * @return true 若该位置可被转化
     */
    private boolean canInfect(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (isAlreadyInfected(block)) {
            return false;
        }

        if (isInfectableWater(state)) {
            return true;
        }

        if (isInfectablePlant(block)) {
            return true;
        }

        return isInfectableGround(block) && level.isEmptyBlock(pos.above());
    }

    /**
     * 判断方块是否已经被感染为灯影之下风格。
     */
    private boolean isAlreadyInfected(Block block) {
        return block == PDBlocks.SHADOW_BLOCK.get()
                || block == PDBlocks.THICK_SHADOW_BLOCK.get()
                || block == PDBlocks.SHADOW_STONE.get()
                || block == PDBlocks.SHADOW_STONE_BRICK.get()
                || block == PDBlocks.SHADOW_NYLIUM.get()
                || block == PDBlocks.SHADOW_FUNGUS.get()
                || block == PDBlocks.SHADOW_LIQUID.get();
    }

    /**
     * 判断方块类型是否属于可感染的地面方块。
     */
    private boolean isInfectableGround(Block block) {
        return block == net.minecraft.world.level.block.Blocks.GRASS_BLOCK
                || block == net.minecraft.world.level.block.Blocks.DIRT
                || block == net.minecraft.world.level.block.Blocks.STONE
                || block == net.minecraft.world.level.block.Blocks.COBBLESTONE
                || block == net.minecraft.world.level.block.Blocks.GRAVEL
                || block == net.minecraft.world.level.block.Blocks.SAND;
    }

    /**
     * 判断方块是否为可感染的静止水源。
     */
    private boolean isInfectableWater(BlockState state) {
        return state.getFluidState().is(net.minecraft.world.level.material.Fluids.WATER)
                && state.getFluidState().isSource();
    }

    /**
     * 判断方块是否为可感染的低矮植被。
     */
    private boolean isInfectablePlant(Block block) {
        return block == net.minecraft.world.level.block.Blocks.SHORT_GRASS
                || block == net.minecraft.world.level.block.Blocks.TALL_GRASS
                || block == net.minecraft.world.level.block.Blocks.FERN
                || block == net.minecraft.world.level.block.Blocks.LARGE_FERN
                || block == net.minecraft.world.level.block.Blocks.DEAD_BUSH;
    }

    /**
     * 感染单个方块，根据原类型转化为对应的灯影之下风格方块。
     *
     * @param level     服务端世界
     * @param pos       目标位置
     * @param random    随机源
     * @param portalPos 产生本次转化的传送门坐标
     */
    private void infectBlock(ServerLevel level, BlockPos pos, RandomSource random, BlockPos portalPos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (isInfectablePlant(block)) {
            convertPlant(level, pos, random, portalPos);
        } else if (isInfectableWater(state)) {
            recordAndSetBlock(level, portalPos, pos, PDBlocks.SHADOW_LIQUID.get().defaultBlockState());
        } else if (isInfectableGround(block)) {
            convertGround(level, pos, block, random, portalPos);
        }
    }

    /**
     * 将地面方块转化为灯影之下风格。
     * 草方块有几率转为阴影菌丝，以支持植被的灯影化。
     *
     * @param level     服务端世界
     * @param pos       目标位置
     * @param block     原方块
     * @param random    随机源
     * @param portalPos 产生本次转化的传送门坐标
     */
    private void convertGround(ServerLevel level, BlockPos pos, Block block, RandomSource random, BlockPos portalPos) {
        BlockState replacement;

        if (block == net.minecraft.world.level.block.Blocks.GRASS_BLOCK) {
            float roll = random.nextFloat();
            if (roll < 0.15f) {
                replacement = PDBlocks.SHADOW_NYLIUM.get().defaultBlockState();
            } else if (roll < 0.55f) {
                replacement = PDBlocks.SHADOW_BLOCK.get().defaultBlockState();
            } else {
                replacement = PDBlocks.THICK_SHADOW_BLOCK.get().defaultBlockState();
            }
        } else if (block == net.minecraft.world.level.block.Blocks.DIRT) {
            replacement = random.nextBoolean()
                    ? PDBlocks.SHADOW_BLOCK.get().defaultBlockState()
                    : PDBlocks.THICK_SHADOW_BLOCK.get().defaultBlockState();
        } else if (block == net.minecraft.world.level.block.Blocks.STONE) {
            replacement = PDBlocks.SHADOW_STONE.get().defaultBlockState();
        } else if (block == net.minecraft.world.level.block.Blocks.COBBLESTONE) {
            replacement = PDBlocks.SHADOW_STONE_BRICK.get().defaultBlockState();
        } else if (block == net.minecraft.world.level.block.Blocks.GRAVEL) {
            replacement = PDBlocks.SHADOW_BLOCK.get().defaultBlockState();
        } else if (block == net.minecraft.world.level.block.Blocks.SAND) {
            replacement = PDBlocks.THICK_SHADOW_BLOCK.get().defaultBlockState();
        } else {
            return;
        }

        recordAndSetBlock(level, portalPos, pos, replacement);
    }

    /**
     * 将低矮植被转化为灯影之下风格的阴影蘑菇。
     * 若下方地面仍为可转化方块，会先把它转成阴影菌丝以支撑蘑菇。
     *
     * @param level     服务端世界
     * @param pos       植被位置
     * @param random    随机源
     * @param portalPos 产生本次转化的传送门坐标
     */
    private void convertPlant(ServerLevel level, BlockPos pos, RandomSource random, BlockPos portalPos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        Block belowBlock = belowState.getBlock();

        if (isInfectableGround(belowBlock)) {
            recordAndSetBlock(level, portalPos, belowPos, PDBlocks.SHADOW_NYLIUM.get().defaultBlockState());
        } else if (belowBlock == PDBlocks.SHADOW_BLOCK.get() || belowBlock == PDBlocks.THICK_SHADOW_BLOCK.get()) {
            recordAndSetBlock(level, portalPos, belowPos, PDBlocks.SHADOW_NYLIUM.get().defaultBlockState());
        }

        BlockState newBelow = level.getBlockState(belowPos);
        if (newBelow.is(PDBlocks.SHADOW_NYLIUM.get())) {
            recordAndSetBlock(level, portalPos, pos, PDBlocks.SHADOW_FUNGUS.get().defaultBlockState());
        } else {
            level.removeBlock(pos, false);
        }
    }

    /**
     * 先记录原始方块状态，再设置新方块。
     * 这是回滚系统的数据源头，确保每个被转化的位置都能被精确恢复。
     *
     * @param level       服务端世界
     * @param portalPos   产生转化的传送门坐标
     * @param targetPos   被转化的目标坐标
     * @param newState    要设置的新方块状态
     */
    private void recordAndSetBlock(ServerLevel level, BlockPos portalPos, BlockPos targetPos, BlockState newState) {
        BlockState originalState = level.getBlockState(targetPos);
        PortalInfectionData.get(level).recordConversion(portalPos, targetPos, originalState);
        level.setBlock(targetPos, newState, 3);
    }

    /**
     * 当实体进入方块碰撞箱时触发 —— 实现竞技场传送。
     * <p>
     * 需 {@code achievement_shadow_d_0} 或创造；通过后传送并赋予缓降。
     */
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(entity instanceof ServerPlayer player)) {
            return;
        }

        if (level.dimension().equals(PDDimensions.AARONCOS_ARENA_WORLD_LEVEL_KEY)) {
            return;
        }

        if (!player.getAbilities().instabuild && !hasAdvancement(player, "achievement_shadow_d_0")) {
            player.displayClientMessage(
                    Component.translatable("message.pasterdream.aaroncos_arena_portals.locked"), true);
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

    private static boolean hasAdvancement(ServerPlayer player, String name) {
        AdvancementHolder holder = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name));
        return holder != null && player.getAdvancements().getOrStartProgress(holder).isDone();
    }
}
