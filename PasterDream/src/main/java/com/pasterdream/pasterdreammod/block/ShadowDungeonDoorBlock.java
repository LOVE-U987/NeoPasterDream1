package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.registry.blocks.PDBlocksDungeon;
import com.pasterdream.pasterdreammod.registry.items.PDItemsMaterials;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * 暗影地牢门方块 —— 对齐原版 {@code ShadowDungeonDoorPr0/1/2}。
 * <p>
 * door_0/1 为薄板状（水平中间），door_2/3 为整高薄墙（Z 轴中间）。
 * 中心门（0 / 2）放置时补全 8 邻格填充门；右键开门；破坏时级联拆除。
 */
public class ShadowDungeonDoorBlock extends Block {

    /** 下层门（door0）同 y 平面 3×3 偏移（含中心） */
    private static final int[][] LOWER_OFFSETS = {
            {0, 0, 0},
            {1, 0, 1}, {-1, 0, 1}, {-1, 0, -1}, {1, 0, -1},
            {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}
    };

    /** 上层门（door2）x/y 平面 3×3 偏移（含中心） */
    private static final int[][] UPPER_OFFSETS = {
            {0, 0, 0},
            {1, 1, 0}, {-1, 1, 0}, {-1, -1, 0}, {1, -1, 0},
            {1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}
    };

    private final VoxelShape shape;

    public ShadowDungeonDoorBlock(BlockBehaviour.Properties properties, VoxelShape shape) {
        super(properties);
        this.shape = shape;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                         @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return shape;
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                        @NotNull BlockState oldState, boolean moving) {
        super.onPlace(state, level, pos, oldState, moving);
        if (level.isClientSide || state.getBlock() == oldState.getBlock()) {
            return;
        }
        Block self = state.getBlock();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if (self == PDBlocksDungeon.SHADOW_DUNGEON_DOOR_0.get()) {
            BlockState fill = PDBlocksDungeon.SHADOW_DUNGEON_DOOR_1.get().defaultBlockState();
            level.setBlock(BlockPos.containing(x + 1, y, z + 1), fill, 3);
            level.setBlock(BlockPos.containing(x + 1, y, z - 1), fill, 3);
            level.setBlock(BlockPos.containing(x - 1, y, z - 1), fill, 3);
            level.setBlock(BlockPos.containing(x - 1, y, z + 1), fill, 3);
            level.setBlock(BlockPos.containing(x + 1, y, z), fill, 3);
            level.setBlock(BlockPos.containing(x - 1, y, z), fill, 3);
            level.setBlock(BlockPos.containing(x, y, z + 1), fill, 3);
            level.setBlock(BlockPos.containing(x, y, z - 1), fill, 3);
        } else if (self == PDBlocksDungeon.SHADOWDUNGEONDOOR_2.get()) {
            BlockState fill = PDBlocksDungeon.SHADOWDUNGEONDOOR_3.get().defaultBlockState();
            level.setBlock(BlockPos.containing(x + 1, y + 1, z), fill, 3);
            level.setBlock(BlockPos.containing(x + 1, y - 1, z), fill, 3);
            level.setBlock(BlockPos.containing(x - 1, y - 1, z), fill, 3);
            level.setBlock(BlockPos.containing(x - 1, y + 1, z), fill, 3);
            level.setBlock(BlockPos.containing(x + 1, y, z), fill, 3);
            level.setBlock(BlockPos.containing(x - 1, y, z), fill, 3);
            level.setBlock(BlockPos.containing(x, y + 1, z), fill, 3);
            level.setBlock(BlockPos.containing(x, y - 1, z), fill, 3);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player,
                                       boolean willHarvest, FluidState fluid) {
        boolean isLower = state.getBlock() == PDBlocksDungeon.SHADOW_DUNGEON_DOOR_0.get();
        boolean isUpperCenter = state.getBlock() == PDBlocksDungeon.SHADOWDUNGEONDOOR_2.get();
        boolean retval = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (!level.isClientSide && (isLower || isUpperCenter)) {
            playDoorSound(level, pos);
            destroyDoorGroup(level, pos, isLower);
        }
        return retval;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
                                                          @NotNull BlockPos pos, @NotNull Player player,
                                                          @NotNull BlockHitResult hitResult) {
        Block self = state.getBlock();
        boolean isLower = self == PDBlocksDungeon.SHADOW_DUNGEON_DOOR_0.get();
        boolean isUpper = self == PDBlocksDungeon.SHADOWDUNGEONDOOR_2.get();
        if (!isLower && !isUpper) {
            return InteractionResult.PASS;
        }

        playDoorSound(level, pos);

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (isLower) {
            if (player.getMainHandItem().is(PDItemsMaterials.SHADOW_DUNGEON_KEY.get())) {
                if (!player.getAbilities().instabuild) {
                    player.getMainHandItem().shrink(1);
                }
                destroyDoorGroup(level, pos, true);
            } else {
                player.displayClientMessage(
                        Component.literal("需要在本层寻找暗影地牢钥匙以打开大门"), true);
            }
        } else {
            if (player instanceof ServerPlayer sp && hasAdvancement(sp, "achievement_shadow_npc_5")) {
                destroyDoorGroup(level, pos, false);
            } else {
                player.displayClientMessage(Component.literal("大门紧闭不开"), true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private static void destroyDoorGroup(Level level, BlockPos center, boolean lower) {
        int[][] offsets = lower ? LOWER_OFFSETS : UPPER_OFFSETS;
        for (int[] o : offsets) {
            level.destroyBlock(center.offset(o[0], o[1], o[2]), false);
        }
    }

    private static void playDoorSound(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            level.playSound(null, pos, PDSounds.SHADOW_DOOR.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    private static boolean hasAdvancement(ServerPlayer player, String name) {
        AdvancementHolder holder = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, name));
        return holder != null && player.getAdvancements().getOrStartProgress(holder).isDone();
    }
}
