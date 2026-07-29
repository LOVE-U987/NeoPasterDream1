package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import com.pasterdream.pasterdreammod.entity.SpellEffects;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.PDEntities;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksFurniture;
import com.pasterdream.pasterdreammod.registry.items.PDItemsMaterials;
import com.pasterdream.pasterdreammod.registry.items.PDItemsFunctional;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * 风之骑士唤醒台（wind_knight_spawnblock_0..4）
 * <p>
 * 忠实还原原版 {@code WindKnightSpawnblockNBlock + WindKnightSpawnblockPr0Procedure}：
 * 五阶段拼装傀儡——0 号嵌入风行者水晶 → 1/2/3 号各用一块凝风铁锭组装躯干/手臂/头颅 →
 * 4 号使用闪电法术（消耗 lightning_spell），86 tick 后召唤风之骑士 + 四朵雷雨云，
 * 台座重置回 0 号。
 * <p>
 * 属性：不可破坏、石质音效、noOcclusion、光通透；GeckoLib 渲染；
 * 形状 (0,0,0,16,8,16)。
 */
public class WindKnightSpawnblockBlock extends BaseEntityBlock {

    public static final MapCodec<WindKnightSpawnblockBlock> CODEC =
            simpleCodec(p -> new WindKnightSpawnblockBlock(0, p));

    /** MCreator 动画状态属性（0..1） */
    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 1);

    /** 阶段编号（0..4） */
    private final int stage;

    /**
     * 构造风之骑士唤醒台
     *
     * @param stage      阶段编号（0..4）
     * @param properties 方块属性
     */
    public WindKnightSpawnblockBlock(int stage, Properties properties) {
        super(properties);
        this.stage = stage;
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
        builder.add(ANIMATION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState();
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
                PDBlockEntitiesFurniture.WIND_KNIGHT_SPAWNBLOCKS.get(stage).get(), pos, state);
    }

    // ==================== 右键交互（原 WindKnightSpawnblockPr0Procedure） ====================

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        interact(level, pos, player);
        return InteractionResult.SUCCESS;
    }

    /**
     * 阶段推进交互（忠实原 procedure，逐阶段判断当前方块）
     *
     * @param level  世界
     * @param pos    位置
     * @param player 玩家
     */
    private static void interact(Level level, BlockPos pos, Player player) {
        BlockState current = level.getBlockState(pos);

        if (current.getBlock() == PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_0.get()) {
            if (player.getMainHandItem().getItem() == PDItemsMaterials.WINDRUNNER_CRYSTAL.get()) {
                replaceKeepingProperties(level, pos, PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_1.get());
                if (!level.isClientSide()) {
                    level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_PLACE, SoundSource.BLOCKS, 1, 1);
                }
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SCRAPE,
                            pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 8, 0.5, 0.5, 0.5, 0.1);
                }
                player.getMainHandItem().shrink(1);
            } else {
                message(player, "需要嵌入 [风行者水晶] ");
            }
        }

        if (level.getBlockState(pos).getBlock() == PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_1.get()) {
            advanceWithIron(level, pos, player, PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_2.get(), 12, 0.7,
                    "需要 [凝风铁锭] 组装躯干");
        }
        if (level.getBlockState(pos).getBlock() == PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_2.get()) {
            advanceWithIron(level, pos, player, PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_3.get(), 20, 0.9,
                    "需要 [凝风铁锭] 组装手臂");
        }
        if (level.getBlockState(pos).getBlock() == PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_3.get()) {
            advanceWithIron(level, pos, player, PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_4.get(), 24, 0.9,
                    "需要 [凝风铁锭] 组装头颅");
        }

        if (level.getBlockState(pos).getBlock() == PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_4.get()) {
            if (player.getMainHandItem().getItem() == PDItemsFunctional.LIGHTNING_SPELL.get()) {
                player.getMainHandItem().shrink(1);
                if (level instanceof ServerLevel serverLevel) {
                    SpellEffects.lightning(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                }
                ServerScheduler.schedule(86, () -> {
                    if (level.getBlockState(pos).getBlock() != PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_4.get()) {
                        return;
                    }
                    replaceKeepingProperties(level, pos, PDBlocksFurniture.WIND_KNIGHT_SPAWNBLOCK_0.get());
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
                    }
                    if (!level.isClientSide()) {
                        level.playSound(null, pos, PDSounds.SHADOW_DOOR.get(), SoundSource.MASTER, 1, 1);
                    }
                });
            } else {
                message(player, "盛有闪电的魔药才能唤醒傀儡");
            }
        }
    }

    /** 用凝风铁锭推进一个阶段（音效 + 粒子 + 1 tick 后替换方块） */
    private static void advanceWithIron(Level level, BlockPos pos, Player player,
                                        Block next, int particleCount, double spread, String failMessage) {
        if (player.getMainHandItem().getItem() == PDItemsMaterials.WIND_IRON_INGOT.get()) {
            if (!level.isClientSide()) {
                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 1, 1);
            }
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SCRAPE,
                        pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                        particleCount, spread, spread == 0.7 ? 0.7 : 1, spread, 0.1);
            }
            player.getMainHandItem().shrink(1);
            ServerScheduler.schedule(1, () -> replaceKeepingProperties(level, pos, next));
        } else {
            message(player, failMessage);
        }
    }

    /** 替换方块并尽量保留旧状态属性（原 procedure 的属性拷贝块） */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void replaceKeepingProperties(Level level, BlockPos pos, Block next) {
        BlockState newState = next.defaultBlockState();
        BlockState oldState = level.getBlockState(pos);
        for (Entry<Property<?>, Comparable<?>> entry : oldState.getValues().entrySet()) {
            Property property = newState.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
            if (property != null && newState.getValue(property) != null) {
                try {
                    newState = newState.setValue(property, (Comparable) entry.getValue());
                } catch (Exception ignored) {
                }
            }
        }
        level.setBlock(pos, newState, 3);
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
}
