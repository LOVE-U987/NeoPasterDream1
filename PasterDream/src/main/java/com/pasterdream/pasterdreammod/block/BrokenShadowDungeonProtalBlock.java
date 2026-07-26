package com.pasterdream.pasterdreammod.block;

import com.mojang.serialization.MapCodec;
import com.pasterdream.pasterdreammod.block.entity.W4GeoDataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntitiesFurniture;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksFurniture;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksVegetation;
import com.pasterdream.pasterdreammod.registry.items.PDItemsMaterials;
import com.pasterdream.pasterdreammod.util.ServerScheduler;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
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

/**
 * 破损的暗影地牢传送门（broken_shadow_dungeon_protal，保留原版拼写）
 * <p>
 * 忠实还原原版 {@code BrokenShadowDungeonProtalBlock + BrokenShadowDungeonProtalPr0}：
 * <ul>
 *   <li>y ≤ 20：结构被破坏，无法修复（聊天提示）；</li>
 *   <li>创造模式：无条件修复——animation=1 + smithing_table 音效 + END_ROD 粒子，
 *       20 tick 后替换为 shadow_dungeon_portal；</li>
 *   <li>生存模式：需 achievement_hide_14 达成，且双手分别持影灯（shadow_light_0）
 *       与黑金属锭——消耗各 1 后修复；否则给出对应引导提示。</li>
 * </ul>
 * 不可破坏、石质音效、GeckoLib 渲染，形状 (3,3,3,13,13,13)。
 */
public class BrokenShadowDungeonProtalBlock extends BaseEntityBlock {

    public static final MapCodec<BrokenShadowDungeonProtalBlock> CODEC =
            simpleCodec(BrokenShadowDungeonProtalBlock::new);

    public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 2);

    /**
     * 构造破损传送门方块
     *
     * @param properties 方块属性
     */
    public BrokenShadowDungeonProtalBlock(Properties properties) {
        super(properties);
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

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hitResult) {
        if (pos.getY() <= 20) {
            if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("地牢的结构被破坏，传送门没有反应"), false);
            }
            return InteractionResult.SUCCESS;
        }

        if (player.getAbilities().instabuild) {
            repairEffects(level, pos);
            if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("创造模式：核心无条件修复"), false);
            }
            scheduleRepair(level, pos, player);
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
                repairEffects(level, pos);
                ItemStack metal = new ItemStack(PDItemsMaterials.BLACKMETAL_INGOT.get());
                player.getInventory().clearOrCountMatchingItems(
                        s -> metal.getItem() == s.getItem(), 1, player.inventoryMenu.getCraftSlots());
                ItemStack light = new ItemStack(PDBlocksVegetation.SHADOW_LIGHT_0.get());
                player.getInventory().clearOrCountMatchingItems(
                        s -> light.getItem() == s.getItem(), 1, player.inventoryMenu.getCraftSlots());
                scheduleRepair(level, pos, player);
            } else if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("双手持§e黑金属§f和§e影灯§f以修复核心"), true);
            }
        } else if (!player.level().isClientSide()) {
            player.displayClientMessage(
                    Component.literal("缺少知识 你还不理解这个核心的工作原理和修复方法"), true);
        }
        return InteractionResult.SUCCESS;
    }

    /** 修复演出：animation=1 + smithing_table 音效 + 末地烛粒子 */
    private static void repairEffects(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty prop
                && prop.getPossibleValues().contains(1)) {
            level.setBlock(pos, state.setValue(prop, 1), 3);
        }
        if (!level.isClientSide()) {
            level.playSound(null, pos, SoundEvents.SMITHING_TABLE_USE, SoundSource.NEUTRAL, 1, 1);
        }
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 24, 1, 1, 1, 0.3);
        }
    }

    /** 20 tick 后替换为完好传送门并提示 */
    private static void scheduleRepair(Level level, BlockPos pos, Player player) {
        ServerScheduler.schedule(20, () -> {
            level.setBlock(pos, PDBlocksFurniture.SHADOW_DUNGEON_PORTAL.get().defaultBlockState(), 3);
            if (!player.level().isClientSide()) {
                player.displayClientMessage(Component.literal("核心已修复"), true);
            }
        });
    }

    /** 成就完成度查询（缺失时降级 false） */
    private static boolean hasAdvancement(Player player, String name) {
        if (!(player instanceof ServerPlayer serverPlayer) || !(serverPlayer.level() instanceof ServerLevel)) {
            return false;
        }
        AdvancementHolder holder = serverPlayer.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath("pasterdream", name));
        return holder != null && serverPlayer.getAdvancements().getOrStartProgress(holder).isDone();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new W4GeoDataBlockEntity(PDBlockEntitiesFurniture.BROKEN_SHADOW_DUNGEON_PROTAL.get(), pos, state);
    }
}
