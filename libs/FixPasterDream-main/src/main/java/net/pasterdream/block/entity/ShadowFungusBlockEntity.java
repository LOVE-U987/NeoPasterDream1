package net.pasterdream.block.entity;

import net.pasterdream.init.PasterdreamModBlockEntities;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;

public class ShadowFungusBlockEntity extends BlockEntity {
	public ShadowFungusBlockEntity(BlockPos pos, BlockState state) {
		super(PasterdreamModBlockEntities.SHADOW_FUNGUS.get(), pos, state);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithFullMetadata();
	}
}
