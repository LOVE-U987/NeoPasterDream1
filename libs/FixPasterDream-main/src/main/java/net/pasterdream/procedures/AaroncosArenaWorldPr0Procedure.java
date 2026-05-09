package net.pasterdream.procedures;

import net.pasterdream.PasterdreamMod;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Comparator;

public class AaroncosArenaWorldPr0Procedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		if ((world instanceof Level _lvl ? _lvl.dimension() : Level.OVERWORLD) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:aaroncos_arena_world")))) {
			if (world.players().size() < 2) {
				if (world instanceof ServerLevel _serverworld) {
					StructureTemplate template = _serverworld.getStructureManager().getOrCreate(new ResourceLocation("pasterdream", "aaroncos_arena"));
					if (template != null) {
						template.placeInWorld(_serverworld, new BlockPos(-35, 0, -35), new BlockPos(-35, 0, -35), new StructurePlaceSettings().setRotation(Rotation.NONE).setMirror(Mirror.NONE).setIgnoreEntities(false), _serverworld.random, 3);
					}
				}
				{
					final Vec3 _center = new Vec3(0, 50, 0);
					List<Entity> _entfound = world.getEntitiesOfClass(Entity.class, new AABB(_center, _center).inflate(99 / 2d), e -> true).stream().sorted(Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_center))).toList();
					for (Entity entityiterator : _entfound) {
						if (!(entityiterator instanceof Player)) {
							if (!entityiterator.level().isClientSide())
								entityiterator.discard();
						}
					}
				}
			}
			{
				Entity _ent = entity;
				_ent.teleportTo(0, 70, 0);
				if (_ent instanceof ServerPlayer _serverPlayer)
					_serverPlayer.connection.teleport(0, 70, 0, _ent.getYRot(), _ent.getXRot());
			}
			PasterdreamMod.queueServerWork(1, () -> {
				{
					Entity _ent = entity;
					_ent.teleportTo(0, 70, 0);
					if (_ent instanceof ServerPlayer _serverPlayer)
						_serverPlayer.connection.teleport(0, 70, 0, _ent.getYRot(), _ent.getXRot());
				}
			});
		}
	}
}
