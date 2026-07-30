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
        list.add(Component.literal("\u54C1\u8D28\uFF1A\u00A7d\u53F2\u8BD7 \u2605\u2605\u2605\u2605\u2605\u2605"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u5728\u67D3\u68A6\u4E16\u754C \u9732\u5929\u73AF\u5883 \u8EAB\u5904\u9AD8\u5EA6y>160 \u767D\u5929 \u811A\u4E0B\u4E3A\u67D3\u68A6\u4E16\u754C\u6811\u53F6"));
        list.add(Component.literal("\u00A77\u25AA \u00A74\u6301\u7EED\u964D\u4F4E\u9971\u98DF\u5EA6"));
        list.add(Component.literal("\u00A77\u25AA \u00A79\u878D\u68A6\u80FD\u91CF+360/h"));
        list.add(Component.literal("\u00A77\u00A7o-- \u843D\u53F6\u5F52\u6839 \u88C2\u835A\u5F52\u51A0"));
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
