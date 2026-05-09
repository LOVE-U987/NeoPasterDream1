package net.pasterdream.procedures;

import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;

public class DreamTrainStructurePr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z) {
		world.setBlock(BlockPos.containing(x, y, z), Blocks.AIR.defaultBlockState(), 3);
		if (world instanceof ServerLevel _serverworld) {
			StructureTemplate template = _serverworld.getStructureManager().getOrCreate(new ResourceLocation("pasterdream", "dream_train"));
			if (template != null) {
				template.placeInWorld(_serverworld, BlockPos.containing(x, y, z), BlockPos.containing(x, y, z), new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false), _serverworld.random, 3);
			}
		}
		if (!world.isClientSide() && world.getServer() != null)
			world.getServer().getPlayerList().broadcastSystemMessage(Component.literal("\u4E00\u8F86\u9010\u68A6\u5217\u8F66\u547C\u5578\u800C\u8FC7..."), false);
		if (Math.random() < 0.5) {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(("\u5217\u8F66\u5411\u6211\u4EEC\u66B4\u9732\u4E86\u4E00\u4E2A\u4F4D\u7F6E\u4FE1\u606F... \u00A7aX\uFF1A" + new java.text.DecimalFormat("#").format(x))),
						false);
		} else {
			if (!world.isClientSide() && world.getServer() != null)
				world.getServer().getPlayerList().broadcastSystemMessage(Component.literal(("\u5217\u8F66\u5411\u6211\u4EEC\u66B4\u9732\u4E86\u4E00\u4E2A\u4F4D\u7F6E\u4FE1\u606F... \u00A7aZ\uFF1A" + new java.text.DecimalFormat("#").format(z))),
						false);
		}
	}
}
