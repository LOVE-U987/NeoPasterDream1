
package net.pasterdream.item;

import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.pasterdream.procedures.BlueprintPr0Procedure;
import net.pasterdream.world.inventory.BlueprintGui0Menu;

import java.util.List;

public class BlueprintItem extends Item {
    private final ResourceLocation blueprintId;
    private final int description_num;
	public BlueprintItem(String resourceLocation,int num) {
		super(new Properties().stacksTo(1).rarity(Rarity.COMMON));
        blueprintId = new ResourceLocation(resourceLocation);
        description_num = num;
	}
    public ResourceLocation getBlueprintId()
    {
        return blueprintId;
    }
    public BlueprintItem(String resourceLocation) {
        this(resourceLocation,0);
    }
	@Override
	public void appendHoverText(ItemStack itemstack, Level world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, world, list, flag);
        list.add(Component.translatable("item.pasterdream.blueprint.0").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("blueprint.pasterdream."+blueprintId.toLanguageKey()).withStyle(ChatFormatting.AQUA));
        list.add(Component.translatable("item.pasterdream.blueprint.1").withStyle(ChatFormatting.GRAY));
        for(int i = 0;i < description_num; ++i)
            list.add(Component.translatable(this.getDescriptionId() + ".describe."+ i).withStyle(ChatFormatting.GRAY));
    }

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player entity, InteractionHand hand) {
		if (entity instanceof ServerPlayer serverPlayer) {
			NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
				@Override
				public Component getDisplayName() {
					return Component.literal("Blueprint " + blueprintId.toString());
				}

				@Override
				public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
                    //这部分数据给服务端处理
                    packetBuffer.writeResourceLocation(blueprintId);
                    packetBuffer.writeBlockPos(entity.blockPosition());
                    packetBuffer.writeBoolean(hand == InteractionHand.MAIN_HAND);
					return new BlueprintGui0Menu(id, inventory, packetBuffer);
				}
			}, buf -> {
                //这部分数据给客户端处理
                buf.writeResourceLocation(blueprintId);
                buf.writeBlockPos(entity.blockPosition());
                buf.writeBoolean(hand == InteractionHand.MAIN_HAND);
            });
		}
		BlueprintPr0Procedure.execute(world, entity.getX(), entity.getY(), entity.getZ(), entity);
		return InteractionResultHolder.success(entity.getItemInHand(hand));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		BlueprintPr0Procedure.execute(context.getLevel(), context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ(), context.getPlayer());
		return InteractionResult.SUCCESS;
	}
}
