package net.pasterdream.procedures;

import net.pasterdream.capability.SanCapability;
import net.pasterdream.world.inventory.ShadowSelectEndMenu;
import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraftforge.network.NetworkHooks;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;

import io.netty.buffer.Unpooled;

public class TrueShadowBedPr0Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity)
			_entity.swing(InteractionHand.MAIN_HAND, true);
		if ((world instanceof Level _lvl1 && _lvl1.isDay()) == false || world.getLevelData().isThundering() == true) {
			if ((world.getBlockState(BlockPos.containing(x, y + 2, z))).getBlock() == PasterdreamModBlocks.TWILIGHT_LANTERN.get()) {
				if ((new Object() {
					public boolean getValue(LevelAccessor world, BlockPos pos, String tag) {
						BlockEntity blockEntity = world.getBlockEntity(pos);
						if (blockEntity != null)
							return blockEntity.getPersistentData().getBoolean(tag);
						return false;
					}
				}.getValue(world, BlockPos.containing(x, y + 2, z), "key")) == true && entity instanceof ServerPlayer _plr6 && _plr6.level() instanceof ServerLevel
						&& _plr6.getAdvancements().getOrStartProgress(_plr6.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_hide_9"))).isDone()) {
                    SanCapability.addPlayerSanWithCheck(_plr6, -10);
                    WorldSpawnPr1Procedure.execute(world, entity);
				}
			}
		} else {
			if (entity instanceof Player _player && !_player.level().isClientSide())
				_player.displayClientMessage(Component.literal("\u4F60\u53EA\u80FD\u5728\u591C\u665A\u6216\u96F7\u66B4\u4E2D\u5165\u7720"), true);
		}
		if ((world instanceof Level _lvl ? _lvl.dimension() : Level.OVERWORLD) == (ResourceKey.create(Registries.DIMENSION, new ResourceLocation("pasterdream:lamp_shadow_world")))) {
			if (entity instanceof ServerPlayer _plr15 && _plr15.level() instanceof ServerLevel
					&& _plr15.getAdvancements().getOrStartProgress(_plr15.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_npc_5"))).isDone() && !(entity instanceof ServerPlayer _plr16
							&& _plr16.level() instanceof ServerLevel && _plr16.getAdvancements().getOrStartProgress(_plr16.server.getAdvancements().getAdvancement(new ResourceLocation("pasterdream:achievement_shadow_d_0"))).isDone())) {
				if (entity instanceof ServerPlayer _ent) {
					BlockPos _bpos = BlockPos.containing(x, y, z);
					NetworkHooks.openScreen((ServerPlayer) _ent, new MenuProvider() {
						@Override
						public Component getDisplayName() {
							return Component.literal("ShadowSelectEnd");
						}

						@Override
						public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
							return new ShadowSelectEndMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
						}
					}, _bpos);
				}
			}
		}
	}
}
