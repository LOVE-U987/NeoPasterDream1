package net.pasterdream.procedures;

import net.pasterdream.network.PasterdreamModVariables;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

public class WorkshopGrindInlay0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		if (!world.isClientSide()) {
			BlockPos _bp = BlockPos.containing(x, y, z);
			BlockEntity _blockEntity = world.getBlockEntity(_bp);
			BlockState _bs = world.getBlockState(_bp);
			if (_blockEntity != null)
				_blockEntity.getPersistentData().putDouble("number", (Mth.nextInt(RandomSource.create(), 1, 2)));
			if (world instanceof Level _level)
				_level.sendBlockUpdated(_bp, _bs, _bs, 3);
		}
		if (PasterdreamModVariables.weapon_workshop_item.is(ItemTags.create(new ResourceLocation("pasterdream:paster_weapon")))) {
			if (new Object() {
				public double getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return blockEntity.getPersistentData().getDouble(tag);
					return -1;
				}
			}.getValue(world, BlockPos.containing(x, y, z), "number") == 1) {
				PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().putBoolean("paster_attack_damage", true);
				PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().putDouble("paster_attack_damage_number",
						(PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().getDouble("paster_attack_damage_number") + Mth.nextInt(RandomSource.create(), -5, 10)));
			}
			if (new Object() {
				public double getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return blockEntity.getPersistentData().getDouble(tag);
					return -1;
				}
			}.getValue(world, BlockPos.containing(x, y, z), "number") == 2) {
				PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().putBoolean("paster_attack_speed", true);
				PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().putDouble("paster_attack_speed_number",
						(PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().getDouble("paster_attack_speed_number") + Mth.nextInt(RandomSource.create(), -3, 7)));
			}
		}
		if (PasterdreamModVariables.weapon_workshop_item.is(ItemTags.create(new ResourceLocation("pasterdream:paster_tool")))) {
			if (new Object() {
				public double getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return blockEntity.getPersistentData().getDouble(tag);
					return -1;
				}
			}.getValue(world, BlockPos.containing(x, y, z), "number") == 1) {
				PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().putBoolean("paster_movement_speed", true);
				PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().putDouble("paster_movement_speed_number",
						(PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().getDouble("paster_movement_speed_number") + Mth.nextInt(RandomSource.create(), -5, 20)));
			}
			if (new Object() {
				public double getValue(LevelAccessor world, BlockPos pos, String tag) {
					BlockEntity blockEntity = world.getBlockEntity(pos);
					if (blockEntity != null)
						return blockEntity.getPersistentData().getDouble(tag);
					return -1;
				}
			}.getValue(world, BlockPos.containing(x, y, z), "number") == 2) {
				PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().putBoolean("paster_luck", true);
				PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().putDouble("paster_luck_number",
						(PasterdreamModVariables.weapon_workshop_item.getOrCreateTag().getDouble("paster_luck_number") + Mth.nextInt(RandomSource.create(), -2, 5)));
			}
		}
	}
}
