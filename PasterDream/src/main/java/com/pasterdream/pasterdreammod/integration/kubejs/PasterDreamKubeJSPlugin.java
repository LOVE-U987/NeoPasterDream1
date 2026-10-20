package com.pasterdream.pasterdreammod.integration.kubejs;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDRecipeTypes;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;

/**
 * PasterDream 的 KubeJS 插件入口
 * <p>
 * 由 {@code kubejs.plugins.txt} 声明，KubeJS 加载时自动实例化。
 * 注册自定义配方 schema（暗影高炉 / 精铸工坊），
 * 玩家可在 {@code ServerEvents.recipes} 中通过
 * {@code event.recipes.pasterdream.<type>} 自定义配方。
 */
public class PasterDreamKubeJSPlugin implements KubeJSPlugin {

    @Override
    public void init() {
        PasterDreamMod.LOGGER.info("[PasterDream] KubeJS 插件已初始化");
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(PasterDreamKubeJSEvents.GROUP);
    }

    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry registry) {
        // 暗影高炉：event.recipes.pasterdream.shadow_blasting(...)
        registry.register(PDRecipeTypes.SHADOW_BLASTING.getId(), PDRecipeSchemas.SHADOW_BLASTING);
        // 精铸工坊：event.recipes.pasterdream.weapon_forge(...)
        registry.register(PDRecipeTypes.WEAPON_FORGE.getId(), PDRecipeSchemas.WEAPON_FORGE);
        // 梦境炼药锅：event.recipes.pasterdream.dream_brewing(...)
        registry.register(PDRecipeTypes.DREAM_BREWING.getId(), PDRecipeSchemas.DREAM_BREWING);
    }
}
