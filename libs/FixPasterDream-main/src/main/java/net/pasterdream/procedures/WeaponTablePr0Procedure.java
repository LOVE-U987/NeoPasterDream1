package net.pasterdream.procedures;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.MultiBlock;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.Map;

public class WeaponTablePr0Procedure {
        static Map<Integer, List<Block>> map = Maps.newHashMap();

        private static void initMultiBlockMaps() {
                map.put(0, Lists.newArrayList(
                        // 0 - 4
                        null,
                        null,
                        null,
                        null,
                        null,

                        // 5 - 9
                        Blocks.STONE_BRICKS,
                        Blocks.STONE_BRICKS,
                        Blocks.STONE_BRICKS,
                        Blocks.STONE_BRICKS,
                        Blocks.STONE_BRICKS,

                        // 10 - 14
                        Blocks.STONE_BRICKS,
                        Blocks.DEEPSLATE_TILES,
                        Blocks.DEEPSLATE_TILES,
                        Blocks.DEEPSLATE_TILES,
                        Blocks.STONE_BRICKS,

                        // 15 - 19
                        Blocks.STONE_BRICKS,
                        Blocks.STONE_BRICKS,
                        Blocks.STONE_BRICKS,
                        Blocks.STONE_BRICKS,
                        Blocks.STONE_BRICKS,
                        
                        // 20 - 24
                        null,
                        null,
                        null,
                        null,
                        null
                ));
                map.put(1, Lists.newArrayList(
                        // 0 - 4
                        null,
                        null,
                        null,
                        null,
                        null,

                        // 5 - 9
                        Blocks.POLISHED_BLACKSTONE_BRICKS,
                        Blocks.POLISHED_BLACKSTONE_BRICKS,
                        null,
                        PasterdreamModBlocks.WEAPON_TABLE.get(),
                        Blocks.CAULDRON,

                        // 10 - 14
                        Blocks.POLISHED_BLACKSTONE_BRICKS,
                        Blocks.POLISHED_BLACKSTONE_BRICKS,
                        null,
                        null,
                        null,

                        // 15 - 19
                        Blocks.GRINDSTONE,
                        null,
                        null,
                        null,
                        Blocks.ANVIL,

                        // 20 - 24
                        null,
                        null,
                        null,
                        null,
                        null
                ));
                map.put(2, Lists.newArrayList(
                        // 0 - 4
                        null,
                        null,
                        null,
                        null,
                        null,

                        // 5 - 9
                        Blocks.BRICKS,
                        Blocks.BLAST_FURNACE,
                        null,
                        null,
                        null,

                        // 10 - 14
                        Blocks.BRICKS,
                        Blocks.BLAST_FURNACE,
                        null,
                        null,
                        null,

                        // 15 - 19
                        null,
                        null,
                        null,
                        null,
                        null,

                        // 20 - 24
                        null,
                        null,
                        null,
                        null,
                        null
                ));
                map.put(3, Lists.newArrayList(
                        // 0 - 4
                        null,
                        null,
                        null,
                        null,
                        null,

                        // 5 - 9
                        Blocks.MUD_BRICK_SLAB,
                        Blocks.MUD_BRICK_SLAB,
                        null,
                        null,
                        null,

                        // 10 - 14
                        Blocks.MUD_BRICK_SLAB,
                        Blocks.MUD_BRICK_SLAB,
                        null,
                        null,
                        null,

                        // 15 - 19
                        null,
                        null,
                        null,
                        null,
                        null,

                        // 20 - 24
                        null,
                        null,
                        null,
                        null,
                        null
                ));
        }

        public static void execute(Level world, double x, double y, double z, Entity entity) {
		if (entity == null || world.isClientSide() || !(entity instanceof Player player)) return;

        if (player.getMainHandItem().getItem() == PasterdreamModItems.BLUEPRINT_1.get()) {
            // 检查结构
            if (map.isEmpty()) initMultiBlockMaps(); // 因为是静态类所以无需反复导入，检查不空即可
            var isBuilding = MultiBlock.INITIALIZE.checkMultiBlock(map, (Player) entity, world, new BlockPos(Mth.floor(x), Mth.floor(y - 1), Mth.floor(z)), 1, 3, entity.getDirection(), Blocks.AIR, 4);
            if (isBuilding) {
                WeaponWorkshopPr0Procedure.execute(world, player, x, (y + 1), z);
            }
            else
                player.displayClientMessage(Component.literal("\u591A\u65B9\u5757\u7ED3\u6784\u4E0D\u5B8C\u6574"), (true));
		} else
            player.displayClientMessage(Component.literal("\u7F3A\u5C11\u84DD\u56FE \u8BF7\u624B\u6301\u84DD\u56FE\u70B9\u51FB\u6838\u5FC3"), (true));
	}
}


