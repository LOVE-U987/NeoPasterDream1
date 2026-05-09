package net.pasterdream;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MultiBlock {
    public static final MultiBlock INITIALIZE = new MultiBlock();
    public boolean checkMultiBlock(Map<Integer, List<Block>> maps, Player player, Level world, BlockPos basePos, int ox, int oz, Direction face, Block target, int maxY) {
        if (maps.size() < maxY) return false;
        Processor processor = new Processor(maps);

        switch (face) {
            case EAST -> {
                var originPos = new BlockPos(basePos.getX() + ox, basePos.getY(), basePos.getZ() - oz);
                for (int y = 0; y < maxY; y++) {
                    int i = 0;
                    for (int x = 0; x > -5; x--)
                        for (int z = 0; z < 5; z++) {
                            if (!processor.checkBlockInMap(world, new BlockPos(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z), y, i))
                                return false;
                            i++;
                        }
                }

                for (int y = 0; y < maxY; y++) for (int x = 0; x > -5; x--) for (int z = 0; z < 5; z++) {
                    var pos0 = new BlockPos(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z);
                    world.destroyBlock(pos0, false);
                    world.setBlockAndUpdate(basePos, Objects.requireNonNull(target.getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, player.getMainHandItem(),
                            BlockHitResult.miss(player.position(), face, basePos)))));
                }
            }

            case SOUTH -> {
                var originPos = new BlockPos(basePos.getX() + oz, basePos.getY(), basePos.getZ() + ox);
                for (int y = 0; y < maxY; y++) {
                    int i = 0;
                    for (int z = 0; z > -5; z--)
                        for (int x = 0; x > -5; x--) {
                            if (!processor.checkBlockInMap(world, new BlockPos(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z), y, i))
                                return false;
                            i++;
                        }
                }

                for (int y = 0; y < maxY; y++) for (int z = 0; z > -5; z--) for (int x = 0; x > -5; x--) {
                    var pos0 = new BlockPos(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z);
                    world.destroyBlock(pos0, false);
                    world.setBlockAndUpdate(basePos, Objects.requireNonNull(target.getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, player.getMainHandItem(),
                            BlockHitResult.miss(player.position(), face, basePos)))));
                }
            }

            case WEST -> {
                var originPos = new BlockPos(basePos.getX() - ox, basePos.getY(), basePos.getZ() + oz);
                for (int y = 0; y < maxY; y++) {
                    int i = 0;
                    for (int x = 0; x < 5; x++)
                        for (int z = 0; z > -5; z--) {
                            if (!processor.checkBlockInMap(world, new BlockPos(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z), y, i))
                                return false;
                            i++;
                        }
                }

                for (int y = 0; y < maxY; y++) for (int x = 0; x < 5; x++) for (int z = 0; z > -5; z--) {
                    var pos0 = new BlockPos(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z);
                    world.destroyBlock(pos0, false);
                    world.setBlockAndUpdate(basePos, Objects.requireNonNull(target.getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, player.getMainHandItem(),
                            BlockHitResult.miss(player.position(), face, basePos)))));
                }
            }

            case NORTH -> {
                var originPos = new BlockPos(basePos.getX() - oz, basePos.getY(), basePos.getZ() - ox);
                for (int y = 0; y < maxY; y++) {
                    int i = 0;
                    for (int z = 0; z < 5; z++)
                        for (int x = 0; x < 5; x++) {
                            if (!processor.checkBlockInMap(world, new BlockPos(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z), y, i))
                                return false;
                            i++;
                        }
                }

                for (int y = 0; y < maxY; y++) for (int z = 0; z < 5; z++) for (int x = 0; x < 5; x++) {
                    var pos0 = new BlockPos(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z);
                    world.destroyBlock(pos0, false);
                    world.setBlockAndUpdate(basePos, Objects.requireNonNull(target.getStateForPlacement(new BlockPlaceContext(player, InteractionHand.MAIN_HAND, player.getMainHandItem(),
                            BlockHitResult.miss(player.position(), face, basePos)))));
                }
            }
        }

        return true;
    }

    public void setMultiBlock(Map<Integer, List<Block>> map, Player player, Level world, BlockPos pos, int ox, int oz, Direction face, int maxY) {
        Processor processor = new Processor(map);
        switch (face) {
            case EAST -> {
                var originPos = new BlockPos(pos.getX() + ox, pos.getY(), pos.getZ() - oz);
                for (int y = 0; y < maxY; y++) {
                    int i = 0;
                    for (int x = 0; x > -5; x--) for (int z = 0; z < 5; z++) {
                        processor.setBlockInMap(world, player, new BlockPos(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z), face, y, i);
                        i++;
                    }
                }
            }

            case SOUTH -> {
                var originPos = new BlockPos(pos.getX() + oz, pos.getY(), pos.getZ() + ox);
                for (int y = 0; y < maxY; y++) {
                    int i = 0;
                    for (int z = 0; z > -5; z--) for (int x = 0; x > -5; x--) {
                        processor.setBlockInMap(world, player, new BlockPos(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z), face, y, i);
                        i++;
                    }
                }
            }

            case WEST -> {
                var originPos = new BlockPos(pos.getX() - ox, pos.getY(), pos.getZ() + oz);
                for (int y = 0; y < maxY; y++) {
                    int i = 0;
                    for (int x = 0; x < 5; x++) for (int z = 0; z > -5; z--) {
                        processor.setBlockInMap(world, player, new BlockPos(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z), face, y, i);
                        i++;
                    }
                }
            }

            case NORTH -> {
                var originPos = new BlockPos(pos.getX() - oz, pos.getY(), pos.getZ() - ox);
                for (int y = 0; y < maxY; y++) {
                    int i = 0;
                    for (int z = 0; z < 5; z++) for (int x = 0; x < 5; x++) {
                        processor.setBlockInMap(world, player, new BlockPos(originPos.getX() + x, originPos.getY() + y, originPos.getZ() + z), face, y, i);
                        i++;
                    }
                }
            }
        }
    }

    public class Processor {
        Map<Integer, List<Block>> maps;
        public Processor(Map<Integer, List<Block>> maps) {
            this.maps = maps;
        }

        public boolean checkBlockInMap(Level world, BlockPos pos, int y, int size) {
            var block = maps.get(y).get(size);
            if (block == null || block == Blocks.AIR) return true;
            else return world.getBlockState(pos).is(block);
        }

        public void setBlockInMap(Level world, Player player, BlockPos pos, Direction face, int y, int size) {
            var block = this.maps.get(y).get(size);
            if (block == null || block == Blocks.AIR) return;
            world.setBlockAndUpdate(pos, Objects.requireNonNull(block.getStateForPlacement(
                    new BlockPlaceContext(player, InteractionHand.MAIN_HAND, player.getMainHandItem(),
                            BlockHitResult.miss(player.position(), face, pos)))
            ));
        }
    }
}



