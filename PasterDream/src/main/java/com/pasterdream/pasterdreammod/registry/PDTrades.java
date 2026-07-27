package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

/**
 * 村民 / 流浪商人交易（对齐原版 {@code PasterdreamModTrades} 共 7 条）。
 * <ul>
 *   <li>流浪商人 generic×5：丛林孢子、作物1/4、四叶草饰品、酵母</li>
 *   <li>工具匠 Lv5：羊皮纸+28 绿宝石 → 蓝图1</li>
 *   <li>农夫 Lv2：1 绿宝石 → 酵母</li>
 * </ul>
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID)
public final class PDTrades {

    private PDTrades() {
    }

    /**
     * 注册流浪商人通用交易（原版 5 条）。
     *
     * @param event 流浪商人交易事件
     */
    @SubscribeEvent
    public static void onWandererTrades(WandererTradesEvent event) {
        event.getGenericTrades().add(new BasicItemListing(
                new ItemStack(Items.EMERALD, 5),
                new ItemStack(PDItems.JUNGLE_SPORE.get()),
                10, 5, 0.05f));
        event.getGenericTrades().add(new BasicItemListing(
                new ItemStack(Items.EMERALD, 5),
                new ItemStack(PDItems.CROP_1A.get(), 2),
                10, 5, 0.05f));
        event.getGenericTrades().add(new BasicItemListing(
                new ItemStack(Items.EMERALD, 5),
                new ItemStack(PDItems.CROP_4A.get(), 2),
                10, 5, 0.05f));
        event.getGenericTrades().add(new BasicItemListing(
                new ItemStack(Items.EMERALD, 40),
                new ItemStack(PDItems.FOURLEAF_CLOVER_CURIO.get()),
                10, 5, 0.05f));
        event.getGenericTrades().add(new BasicItemListing(
                new ItemStack(Items.EMERALD, 2),
                new ItemStack(PDItems.YEAST.get()),
                10, 5, 0.05f));
    }

    /**
     * 注册村民职业交易（原版工具匠 + 农夫共 2 条）。
     *
     * @param event 村民交易事件
     */
    @SubscribeEvent
    public static void onVillagerTrades(VillagerTradesEvent event) {
        if (event.getType() == VillagerProfession.TOOLSMITH) {
            // 双价：羊皮纸 + 28 绿宝石 → 蓝图1
            event.getTrades().get(5).add(new BasicItemListing(
                    new ItemStack(PDItems.PERGAMYN.get()),
                    new ItemStack(Items.EMERALD, 28),
                    new ItemStack(PDItems.BLUEPRINT_1.get()),
                    10, 5, 0.05f));
        }
        if (event.getType() == VillagerProfession.FARMER) {
            event.getTrades().get(2).add(new BasicItemListing(
                    new ItemStack(Items.EMERALD),
                    new ItemStack(PDItems.YEAST.get()),
                    10, 5, 0.05f));
        }
    }
}
