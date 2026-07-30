package com.pasterdream.pasterdreammod.pasterdreamsanity.registry;

import com.pasterdream.pasterdreammod.api.curio.CurioAPI;
import com.pasterdream.pasterdreammod.api.curio.model.CurioSlot;
import com.pasterdream.pasterdreammod.pasterdreamsanity.PasterDreamSanityMod;
import com.pasterdream.pasterdreammod.pasterdreamsanity.item.WhiteFlowerBodyItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * San 值系统物品注册类。
 * <p>
 * 负责注册白花胸针、镇静剂等 San 相关物品。
 *
 * @author PasterDream
 */
public class PDSanityItems {

    /** 物品注册器 */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PasterDreamSanityMod.MOD_ID);

    /** 白花胸针 (white_flower_body) */
    public static final DeferredItem<Item> WHITE_FLOWER_BODY =
            CurioAPI.create("white_flower_body").slot(CurioSlot.BODY)
                    .withItemClass(WhiteFlowerBodyItem::new).register();

    private PDSanityItems() {
    }
}
