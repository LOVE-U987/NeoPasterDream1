package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlockEntities;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

/**
 * 阴影蘑菇 (shadow_fungus)
 * <p>
 * 阴影维度地表植被，只能种植在阴影菌丝上。骨粉（Pr0，30%）与 randomTick（Pr1，6%）
 * 在头顶留空时放置 {@code shadow_fungustree_0..7} 结构。
 * 还原自原版 {@code ShadowFungusBlock} + Pr0/Pr1。
 */
public class ShadowFungusBlock extends FlowerBlock implements EntityBlock, BonemealableBlock {

    private static final String TAG_NUMBER = "number";
    private static final String[] TREE_STRUCTURES = {
            "shadow_fungustree_0",
            "shadow_fungustree_1",
            "shadow_fungustree_2",
            "shadow_fungustree_3",
            "shadow_fungustree_4",
            "shadow_fungustree_5",
            "shadow_fungustree_6",
            "shadow_fungustree_7"
    };

    public ShadowFungusBlock(BlockBehaviour.Properties properties) {
        super(MobEffects.MOVEMENT_SPEED, 0, properties);
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .randomTicks()
                .sound(SoundType.FUNGUS)
                .instabreak()
                .noCollission()
                .offsetType(BlockBehaviour.OffsetType.NONE)
                .pushReaction(PushReaction.DESTROY);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(PDBlocks.SHADOW_NYLIUM.get());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new W4DataBlockEntity(PDBlockEntities.SHADOW_FUNGUS.get(), pos, state);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 原版 tick() + randomTicks → 1.21 randomTick；生长概率 6%（Pr1）
        tryGrow(level, pos, 0.06);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        // 骨粉生长概率 30%（Pr0）
        tryGrow(level, pos, 0.3);
    }

    /**
     * 随机写入 number∈[1,8]，检查 y+1/3/5/7/9 为空后放置对应菌树结构。
     *
     * @param chance 成功生长概率（骨粉 0.3 / 随机刻 0.06）
     */
    private static void tryGrow(ServerLevel level, BlockPos pos, double chance) {
        int number = Mth.nextInt(level.getRandom(), 1, 8);
        W4DataBlockEntity.putDoubleAt(level, pos, TAG_NUMBER, number);

        if (level.getRandom().nextDouble() >= chance) {
            return;
        }
        if (!hasVerticalClearance(level, pos)) {
            return;
        }

        int index = Mth.clamp(number, 1, 8) - 1;
        placeTree(level, pos, TREE_STRUCTURES[index]);
    }

    /** 原版检查 y+1/3/5/7/9 均为空气 */
    private static boolean hasVerticalClearance(ServerLevel level, BlockPos pos) {
        for (int dy : new int[]{1, 3, 5, 7, 9}) {
            if (!level.getBlockState(pos.above(dy)).isAir()) {
                return false;
            }
        }
        return true;
    }

    private static void placeTree(ServerLevel level, BlockPos fungusPos, String structureName) {
        StructureTemplate template = level.getStructureManager().getOrCreate(
                ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, structureName));
        BlockPos origin = fungusPos.offset(-2, 0, -2);
        template.placeInWorld(level, origin, origin,
                new StructurePlaceSettings()
                        .setRotation(Rotation.NONE)
                        .setMirror(Mirror.NONE)
                        .setIgnoreEntities(false),
                level.random, 3);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        super.triggerEvent(state, level, pos, id, param);
        BlockEntity be = level.getBlockEntity(pos);
        return be != null && be.triggerEvent(id, param);
    }
}
