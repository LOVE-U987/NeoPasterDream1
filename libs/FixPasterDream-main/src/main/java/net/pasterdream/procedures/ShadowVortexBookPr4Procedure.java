package net.pasterdream.procedures;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.pasterdream.capability.MeltDreamEnergyCapability;
import net.pasterdream.capability.SanCapability;
import net.pasterdream.init.PasterdreamModParticleTypes;
import net.pasterdream.init.PasterdreamModBlocks;
import net.pasterdream.init.PasterdreamModAttributes;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.ItemTags;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import java.util.concurrent.atomic.AtomicReference;

public class ShadowVortexBookPr4Procedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity sourceentity) {
		if (sourceentity == null)
			return;
		if (world instanceof ServerLevel _level)
			_level.sendParticles((SimpleParticleType) (PasterdreamModParticleTypes.SHADOW_STONE_PARTICLE.get()), x, y, z, 5, 0.1, 0.1, 0.1, 0.01);
		if (world instanceof ServerLevel _level)
			_level.sendParticles(ParticleTypes.SMOKE, x, y, z, 5, 0.1, 0.1, 0.1, 0.01);
		if (sourceentity.isShiftKeyDown()) {
			{
				AtomicReference<IItemHandler> _iitemhandlerref = new AtomicReference<>();
				sourceentity.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(_iitemhandlerref::set);
				if (_iitemhandlerref.get() != null) {
					for (int _idx = 0; _idx < _iitemhandlerref.get().getSlots(); _idx++) {
						ItemStack itemstackiterator = _iitemhandlerref.get().getStackInSlot(_idx).copy();
						if (itemstackiterator.is(ItemTags.create(new ResourceLocation("pasterdream:magic")))) {
							if (sourceentity instanceof Player _player)
								_player.getCooldowns().addCooldown(itemstackiterator.getItem(), 240);
						}
					}
				}
			}
			if ((world.getBlockState(BlockPos.containing(x, y, z))).getBlock() == Blocks.AIR) {
				world.setBlock(BlockPos.containing(x, y, z), PasterdreamModBlocks.SHADOW_VORTEX.get().defaultBlockState(), 3);
				if (!world.isClientSide()) {
					BlockPos _bp = BlockPos.containing(x, y, z);
					BlockEntity _blockEntity = world.getBlockEntity(_bp);
					BlockState _bs = world.getBlockState(_bp);
					if (_blockEntity != null)
						_blockEntity.getPersistentData().putBoolean("friendly", true);
					if (world instanceof Level _level)
						_level.sendBlockUpdated(_bp, _bs, _bs, 3);
				}
				if (world instanceof Level _level) {
					if (!_level.isClientSide()) {
						_level.playSound(null, BlockPos.containing(x, y, z), ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:shadow_vortex")), SoundSource.BLOCKS, 1, 1);
					} else {
						_level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("pasterdream:shadow_vortex")), SoundSource.BLOCKS, 1, 1, false);
					}
				}
			}
            if(sourceentity instanceof Player pl)
            {
                AttributeInstance instance = pl.getAttribute(PasterdreamModAttributes.MAGICPOWER.get());
                if(instance != null)
                {
                    double magic_power = instance.getValue();
                    if(magic_power < 9)
                    {
                        MeltDreamEnergyCapability.consumePlayerMeltDreamEnergy(pl,0.01 * (9 - magic_power));
                        SanCapability.addPlayerSanWithCheck(pl, -0.02 * (9 - magic_power));
                    }
                }
            }
		}
	}
}
