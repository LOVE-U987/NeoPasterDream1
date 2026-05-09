
package net.pasterdream.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.pasterdream.init.PasterdreamModItems;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DyedreamHammerItem extends PickaxeItem {
	public DyedreamHammerItem() {
		super(new Tier() {
			public int getUses() {
				return 6570;
			}

			public float getSpeed() {
				return 3f;
			}

			public float getAttackDamageBonus() {
				return 10f;
			}

			public int getLevel() {
				return 4;
			}

			public int getEnchantmentValue() {
				return 22;
			}

			public Ingredient getRepairIngredient() {
				return Ingredient.of(new ItemStack(PasterdreamModItems.DYEDREAM_INGOT.get()));
			}
		}, 0, -3.3f, new Item.Properties());
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
		list.add(Component.literal("\u00A7f\u25AA \u00A79\u8303\u56F4\u6316\u6398 3*3*3"));
	}

	@Override
	public boolean mineBlock(ItemStack item, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
		if (!world.isClientSide && state.getDestroySpeed(world, pos) != 0.0F) {
			Set<BlockPos> posSet = new HashSet<>();
			for (int y = -1; y <= 1; ++y) for (int x = -1; x <= 1; ++x) for (int z = -1; z <= 1; ++z) {
				BlockPos pos1 = new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
				if (world.getBlockState(pos1).equals(state) && item.isCorrectToolForDrops(world.getBlockState(pos1)) && !pos1.equals(pos)) {
					var childBlockState = world.getBlockState(pos1);
					childBlockState.spawnAfterBreak((ServerLevel) world, pos1, item, true);
					List<ItemStack> drops = Block.getDrops(childBlockState, (ServerLevel) world, pos1, world.getBlockEntity(pos1), entity, item);
					drops.forEach(e -> Block.popResourceFromFace(world, pos1, ((BlockHitResult) entity.pick(20D, 1F, false)).getDirection(), e));

					world.setBlockAndUpdate(pos1, Blocks.AIR.defaultBlockState());
					if (world.getBlockEntity(pos1) != null) world.removeBlockEntity(pos1);

					posSet.add(pos1);
				}
			}

			item.hurtAndBreak(posSet.size() + 1, entity, (e) -> {
				e.broadcastBreakEvent(EquipmentSlot.MAINHAND);
			});
		}
		return true;
	}
}
