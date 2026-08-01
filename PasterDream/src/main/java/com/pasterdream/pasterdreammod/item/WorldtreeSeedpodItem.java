package com.pasterdream.pasterdreammod.item;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.api.dimension.APIDimensions;
import com.pasterdream.pasterdreammod.api.meltdream.MeltDreamEnergyAPI;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

/**
 * Worldtree Seedpod Item (Curio Item)
 * <p>
 * 佩戴后检测条件：染梦世界 + y>160 + 白天(1000~13000) + 露天 + 脚下为染梦世界树叶<br>
 * 满足时每 tick：融梦能量 +0.1、饱和度消耗 +0.4（即描述所述 +360/h）。
 * <p>
 * 还原自原版 WorldtreeSeedpodItem + WorldtreeSeedpodPr1Procedure。
 */
public class WorldtreeSeedpodItem extends Item implements ICurioItem {

    public WorldtreeSeedpodItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON));
    }

    @Override
    public void appendHoverText(ItemStack itemstack, Item.TooltipContext context, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, context, list, flag);
        list.add(Component.translatable("tooltip.pasterdream.worldtree_seedpod.quality"));
        list.add(Component.translatable("tooltip.pasterdream.worldtree_seedpod.effect_1"));
        list.add(Component.translatable("tooltip.pasterdream.worldtree_seedpod.effect_2"));
        list.add(Component.translatable("tooltip.pasterdream.worldtree_seedpod.effect_3"));
        list.add(Component.translatable("tooltip.pasterdream.worldtree_seedpod.flavor_1"));
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) return;
        Level level = player.level();
        if (level.isClientSide) return;

        // 节流：与原版一致，每 20 tick（≈1 秒）执行一次完整检查
        if (player.tickCount % 20 != 0) return;

        // 取原版精确坐标（与原版 WorldtreeSeedpodPr0Procedure 一致）
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();
        BlockPos pos = BlockPos.containing(x, y, z);

        // 条件 1：染梦世界
        if (!APIDimensions.isDyedreamWorld(level)) {
            PasterDreamMod.LOGGER.trace("[WorldtreeSeedpod] skip: not dyedream world, dim={}", level.dimension().location());
            return;
        }

        // 条件 2：露天（与原版 canSeeSkyFromBelowWater 一致）
        if (!level.canSeeSkyFromBelowWater(pos)) {
            PasterDreamMod.LOGGER.trace("[WorldtreeSeedpod] skip: not open sky at {}", pos);
            return;
        }

        // 条件 3：白天（与原版 isDay() 一致）
        if (!level.isDay()) {
            PasterDreamMod.LOGGER.trace("[WorldtreeSeedpod] skip: not daytime");
            return;
        }

        // 条件 4：高度 y > 160（原版使用 entity.getY() 精确 double 比较）
        if (!(y > 160)) {
            PasterDreamMod.LOGGER.trace("[WorldtreeSeedpod] skip: y={} <= 160", y);
            return;
        }

        // 条件 5：脚下为染梦世界树叶（与原版一致，检测 y-1 和 y-2 两格）
        BlockState below1 = level.getBlockState(BlockPos.containing(x, y - 1, z));
        BlockState below2 = level.getBlockState(BlockPos.containing(x, y - 2, z));
        if (!below1.is(PDBlocks.DYEDREAM_WORLDTREE_LEAVES.get())
                && !below2.is(PDBlocks.DYEDREAM_WORLDTREE_LEAVES.get())) {
            PasterDreamMod.LOGGER.trace("[WorldtreeSeedpod] skip: below blocks are {} (y-1) and {} (y-2), want dyedream_worldtree_leaves",
                    below1.getBlock(), below2.getBlock());
            return;
        }

        PasterDreamMod.LOGGER.trace("[WorldtreeSeedpod] PASS: adding energy + exhaustion");
        MeltDreamEnergyAPI.addEnergy(player, 0.1);
        player.getFoodData().addExhaustion(0.4f);
    }

}
