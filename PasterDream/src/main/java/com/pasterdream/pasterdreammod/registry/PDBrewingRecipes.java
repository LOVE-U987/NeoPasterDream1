package com.pasterdream.pasterdreammod.registry;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.recipe.BasaltBrewingRecipe;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

/**
 * 酿造配方注册类
 * <p>
 * 通过 NeoForge {@link RegisterBrewingRecipesEvent} 注册药水酿造配方——
 * 该事件在 {@code PotionBrewing#bootstrap} 时于游戏总线触发（NeoForge 1.21.1 起
 * 移除了 BREWING_RECIPES 注册表，改为 Builder 收集式注册）。
 * 注册后原版酿造台、JEI 酿造展示、梦境炼药锅通用酿造通道
 * （{@code Level#potionBrewing()} 三件套）三处自动生效，无需额外接入。
 * <p>
 * 挂载方式：{@code @EventBusSubscriber} 注解自动挂载（事件按类型路由至游戏总线，
 * {@code bus} 参数在当前 NeoForge 版本已过时）。
 */
@EventBusSubscriber(modid = PasterDreamMod.MOD_ID)
public class PDBrewingRecipes {

    private PDBrewingRecipes() {
    }

    /**
     * 酿造配方注册事件处理：向 PotionBrewing Builder 注册本模组配方
     *
     * @param event 酿造配方注册事件（携带 Builder 与 RegistryAccess）
     */
    @SubscribeEvent
    public static void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
        // 玄岩药水：粗制药水 + 玄武岩蜗牛壳 → 玄岩药水
        // （移植自原模组 Brewing0BrewingRecipe，1.20.1 → 1.21.1 移植时曾缺失：
        //   JEI 无酿造步骤展示、药水在酿造台与炼药锅中均无法产出）
        event.getBuilder().addRecipe(new BasaltBrewingRecipe());
    }
}
