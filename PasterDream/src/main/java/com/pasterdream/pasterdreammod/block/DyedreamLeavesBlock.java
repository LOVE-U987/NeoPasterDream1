package com.pasterdream.pasterdreammod.block;

import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.List;

/**
 * 染梦树叶方块
 * 树苗约 1/18、染梦果约 2/18、木棍小概率；剪刀/精准采集掉自身。
 */
public class DyedreamLeavesBlock extends LeavesBlock {

    public DyedreamLeavesBlock(Properties properties) {
        super(properties);
    }

    public DyedreamLeavesBlock() {
        super(BlockBehaviour.Properties.of()
                .ignitedByLava()
                .sound(SoundType.GRASS)
                .strength(0.01f, 0.1f)
                .noOcclusion()
                .isRedstoneConductor((bs, br, bp) -> false)
                .dynamicShape());
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 20;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>();
        ItemStack tool = params.getParameter(LootContextParams.TOOL);
        ServerLevel level = params.getLevel();

        boolean silkTouch = false;
        int fortune = 0;
        if (tool != null && !tool.isEmpty()) {
            var enchantmentRegistry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            Holder<Enchantment> silkTouchHolder = enchantmentRegistry.getHolderOrThrow(Enchantments.SILK_TOUCH);
            Holder<Enchantment> fortuneHolder = enchantmentRegistry.getHolderOrThrow(Enchantments.FORTUNE);
            silkTouch = tool.getEnchantmentLevel(silkTouchHolder) > 0;
            fortune = tool.getEnchantmentLevel(fortuneHolder);
        }
        boolean shears = tool != null && tool.is(PDItemTags.SHEARS);

        if (silkTouch || shears) {
            drops.add(new ItemStack(this));
            return drops;
        }

        RandomSource random = level.getRandom();
        if (random.nextFloat() < getSaplingChance(fortune)) {
            drops.add(new ItemStack(PDItems.DYEDREAM_SAPLING.get()));
        }
        if (random.nextFloat() < getFruitChance(fortune)) {
            drops.add(new ItemStack(PDItems.DYEDREAM_FRUIT.get()));
        }
        if (random.nextFloat() < getStickChance(fortune)) {
            drops.add(new ItemStack(Items.STICK));
        }
        return drops;
    }

    private float getSaplingChance(int fortune) {
        return switch (fortune) {
            case 0 -> 1 / 18f;
            case 1 -> 0.07f;
            case 2 -> 0.09f;
            case 3 -> 0.125f;
            default -> 0.1667f;
        };
    }

    private float getFruitChance(int fortune) {
        return switch (fortune) {
            case 0 -> 2 / 18f;
            case 1 -> 0.125f;
            case 2 -> 0.1667f;
            case 3 -> 0.2f;
            default -> 0.25f;
        };
    }

    private float getStickChance(int fortune) {
        return switch (fortune) {
            case 0 -> 0.02f;
            case 1 -> 0.022f;
            case 2 -> 0.025f;
            case 3 -> 0.03f;
            default -> 0.04f;
        };
    }
}
