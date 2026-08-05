package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import com.pasterdream.pasterdreammod.entity.SpellEffects;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.registry.items.PDItemsMaterials;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * 风之骑士唤醒台（wind_knight_spawnblock）
 * <p>
 * 忠实还原原版 {@code WindKnightSpawnblockNBlock + WindKnightSpawnblockPr0Procedure}，
 * 但将原版 5 个独立方块（_0.._4）合并为单一方块，由方块数据 {@link #STAGE} 决定拼装样式：
 * stage 0 嵌入风行者水晶 → 1/2/3 各用一块凝风铁锭组装躯干/手臂/头颅 →
 * stage 4 使用闪电法术（消耗 lightning_spell），86 tick 后召唤风之骑士 + 四朵雷雨云，
 * 台座重置回 stage 0。
 * <p>
 * 属性：不可破坏、石质音效、noOcclusion、光通透；GeckoLib 渲染（按 STAGE 切换模型）；
 * 放置朝向 {@link #FACING}，渲染随朝向旋转；形状 (0,0,0,16,8,16)。
 */
public class WindKnightSpawnblockBlock extends BaseEntityBlock {

    /** 阶段属性（0..4）：由方块数据决定拼装样式 */
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 4);

    /** MCreator 动画状态属性（0..1） */
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);

    /** 水平朝向（放置方向） */
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public static final MapCodec<WindKnightSpawnblockBlock> CODEC = simpleCodec(WindKnightSpawnblockBlock::new);

    /**
     * 构造风之骑士唤醒台
     *
     * @param properties 方块属性
     */
    public WindKnightSpawnblockBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(STAGE, 0)
                .setValue(ANIMATION, 0)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    /**
     * 从方块状态解析阶段（0..4）
     *
     * @param state 方块状态
     * @return 阶段编号，无 STAGE 属性时返回 0
     */
    public static int stageOf(BlockState state) {
        return state.hasProperty(STAGE) ? state.getValue(STAGE) : 0;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return box(0, 0, 0, 16, 8, 16);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE, ANIMATION, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (!drops.isEmpty()) {
            return drops;
        }
        return Collections.singletonList(new ItemStack(this));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new W4GeoDataBlockEntity(
                PDBlockEntitiesFurniture.WIND_KNIGHT_SPAWNBLOCK.get(), pos, state);
    }

    // ==================== 右键交互（原 WindKnightSpawnblockPr0Procedure） ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        // 客户端只回报成功；换块 / 扣物 / ServerScheduler 仅服务端。
        // ServerScheduler 为 JVM 静态队列，集成端客户端若 schedule 会把 ClientLevel 任务塞进服务端 tick。
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        interact(level, pos, player);
        return InteractionResult.SUCCESS;
    }

    /**
     * 阶段推进交互（忠实原 procedure，逐阶段判断当前方块；仅服务端）
     *
     * @param level  世界
     * @param pos    位置
     * @param player 玩家
     */
    private static void interact(Level level, BlockPos pos, Player player) {
        int stage = stageOf(level.getBlockState(pos));

        switch (stage) {
            case 0 -> {
                if (player.getMainHandItem().getItem() == PDItemsMaterials.WINDRUNNER_CRYSTAL.get()) {
                    setStage(level, pos, 1);
                    level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.BLOCKS, 1, 1);
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.SCRAPE,
                                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 8, 0.5, 0.5, 0.5, 0.1);
                    }
                    player.getMainHandItem().shrink(1);
                } else {
                    message(player, "需要嵌入 [风行者水晶] ");
                }
            }
            case 1 -> advanceWithIron(level, pos, player, 2, 12, 0.7,
                    "需要 [凝风铁锭] 组装躯干");
            case 2 -> advanceWithIron(level, pos, player, 3, 20, 0.9,
                    "需要 [凝风铁锭] 组装手臂");
            case 3 -> advanceWithIron(level, pos, player, 4, 24, 0.9,
                    "需要 [凝风铁锭] 组装头颅");
            case 4 -> {
                if (player.getMainHandItem().getItem() == lookupLightningSpell()) {
                    player.getMainHandItem().shrink(1);
                    if (level instanceof ServerLevel serverLevel) {
                        SpellEffects.lightning(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    }
                    ServerScheduler.schedule(86, () -> {
                        if (stageOf(level.getBlockState(pos)) != 4) {
                            return;
                        }
                        setStage(level, pos, 0);
                        if (level instanceof ServerLevel serverLevel) {
                            spawnAt(serverLevel, PDEntities.WIND_KNIGHT.get(),
                                    pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                            spawnAt(serverLevel, PDEntities.THUNDERCLOUD.get(),
                                    pos.getX() + 6.5, pos.getY() + 8, pos.getZ() + 6.5);
                            spawnAt(serverLevel, PDEntities.THUNDERCLOUD.get(),
                                    pos.getX() - 6.5, pos.getY() + 8, pos.getZ() + 6.5);
                            spawnAt(serverLevel, PDEntities.THUNDERCLOUD.get(),
                                    pos.getX() + 6.5, pos.getY() + 8, pos.getZ() - 6.5);
                            spawnAt(serverLevel, PDEntities.THUNDERCLOUD.get(),
                                    pos.getX() - 6.5, pos.getY() + 8, pos.getZ() - 6.5);
                            level.playSound(null, pos, PDSounds.SHADOW_DOOR.get(), SoundSource.MASTER, 1, 1);
                        }
                    });
                } else {
                    message(player, "盛有闪电的魔药才能唤醒傀儡");
                }
            }
            default -> {
            }
        }
    }

    /** 用凝风铁锭推进一个阶段（音效 + 粒子 + 1 tick 后写入新阶段；仅服务端） */
    private static void advanceWithIron(Level level, BlockPos pos, Player player,
                                        int nextStage, int particleCount, double spread, String failMessage) {
        if (player.getMainHandItem().getItem() == PDItemsMaterials.WIND_IRON_INGOT.get()) {
            level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1, 1);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SCRAPE,
                        pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                        particleCount, spread, spread == 0.7 ? 0.7 : 1, spread, 0.1);
            }
            player.getMainHandItem().shrink(1);
            ServerScheduler.schedule(1, () -> setStage(level, pos, nextStage));
        } else {
            message(player, failMessage);
        }
    }

    /**
     * 写入新阶段，保留其余属性（FACING/ANIMATION）（仅服务端）。
     * <p>
     * 取代原版“替换成另一个方块”的实现——样式由方块数据 STAGE 决定，方块本体不变。
     */
    private static void setStage(Level level, BlockPos pos, int stage) {
        BlockState state = level.getBlockState(pos);
        if (state.hasProperty(STAGE)) {
            level.setBlock(pos, state.setValue(STAGE, stage), 3);
        }
    }

    /**
     * 在指定位置生成实体并随机朝向。
     * <p>
     * 不用 {@code EntityType#spawn(BlockPos)}：其对悬空/碰撞位可能返回 null
     *（祭坛伴生雷云 y+8 实测曾 Δcloud=0，骑士仍成功）。改为 create + moveTo + addFresh。
     */
    private static void spawnAt(ServerLevel level, net.minecraft.world.entity.EntityType<?> type,
                                double x, double y, double z) {
        Entity entity = type.create(level);
        if (entity == null) {
            return;
        }
        float yRot = level.getRandom().nextFloat() * 360F;
        entity.moveTo(x, y, z, yRot, 0.0F);
        if (entity instanceof Mob mob) {
            mob.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()),
                    MobSpawnType.MOB_SUMMONED, null);
            mob.setPersistenceRequired();
        }
        level.addFreshEntity(entity);
    }

    /** 向玩家发送快捷栏提示（仅服务端） */
    private static void message(Player player, String text) {
        if (!player.level().isClientSide()) {
            player.displayClientMessage(Component.literal(text), true);
        }
    }

    /**
     * 动态查找 PasterDreamSpells 的闪电法术物品。
     *
     * @return 闪电法术物品，未注册时返回 Items.AIR
     */
    private static Item lookupLightningSpell() {
        return BuiltInRegistries.ITEM.getOptional(
                ResourceLocation.fromNamespaceAndPath("pasterdreamspells", "lightning_spell"))
                .orElse(net.minecraft.world.item.Items.AIR);
    }
}
