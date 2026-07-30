package com.pasterdream.pasterdreammod.pasterdreammeltdream.registry;

import com.pasterdream.pasterdreammod.api.curio.CurioAPI;
import com.pasterdream.pasterdreammod.api.curio.model.CurioSlot;
import com.pasterdream.pasterdreammod.pasterdreammeltdream.PasterDreamMeltDreamMod;
import com.pasterdream.pasterdreammod.pasterdreammeltdream.item.MeltdreamEnergy0RingItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 融梦能量系统物品注册类。
 * <p>
 * 负责注册融梦水晶、融梦精华、融梦能量戒指等融梦相关物品。
 *
 * @author PasterDream
 */
public class PDMeltDreamItems {

    /** 物品注册器 */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PasterDreamMeltDreamMod.MOD_ID);

    /** 融梦光环戒指 (meltdream_energy_0_ring) */
    public static final DeferredItem<Item> MELTDREAM_ENERGY_0_RING =
            CurioAPI.create("meltdream_energy_0_ring").slot(CurioSlot.RING)
                    .withItemClass(MeltdreamEnergy0RingItem::new).register();

    private PDMeltDreamItems() {
    }
}
