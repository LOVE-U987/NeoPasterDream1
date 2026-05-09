package net.pasterdream.event;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "pasterdream")
public class BlocksEvent {
    public static final TagKey<Block> BLOCK_CAN_SHEARS = TagKey.create(Registries.BLOCK, new ResourceLocation("pasterdream", "mineable/shears"));

    @SubscribeEvent
    public static void onBlockBreakByPlayerEvent(BlockEvent.BreakEvent event) {
        final var player = event.getPlayer();
        final var world = (Level) event.getLevel();
        final var block = world.getBlockState(event.getPos());

        if (player.isCreative()) return;
        if (!block.is(BLOCK_CAN_SHEARS) || !player.getMainHandItem().is(Items.SHEARS)) return;
        final var entity = new ItemEntity(world, event.getPos().getX() + 0.5, event.getPos().getY() + 0.5, event.getPos().getZ() + 0.5, new ItemStack(block.getBlock()));
        world.addFreshEntity(entity);
    }
}
