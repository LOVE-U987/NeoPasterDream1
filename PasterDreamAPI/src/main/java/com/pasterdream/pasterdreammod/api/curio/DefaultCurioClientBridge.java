package com.pasterdream.pasterdreammod.api.curio;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Curio 客户端渲染注册的默认桥接：遍历 {@link CurioAPI#getRendererSuppliers()} 写入 Curios。
 * <p>
 * 主模可包一层加日志，或直接 {@code CurioAPI.setClientBridge(new DefaultCurioClientBridge())}。
 */
public class DefaultCurioClientBridge implements CurioAPI.CurioClientBridge {

    private final Consumer<String> onRegistered;
    private final Consumer<String> onMissingItem;

    public DefaultCurioClientBridge() {
        this(name -> {}, name -> {});
    }

    /**
     * @param onRegistered  成功注册时回调 fullName（可接 LOGGER.debug）
     * @param onMissingItem 物品未找到时回调 fullName（可接 LOGGER.warn）
     */
    public DefaultCurioClientBridge(Consumer<String> onRegistered, Consumer<String> onMissingItem) {
        this.onRegistered = onRegistered != null ? onRegistered : name -> {};
        this.onMissingItem = onMissingItem != null ? onMissingItem : name -> {};
    }

    @Override
    @SuppressWarnings("unchecked")
    public void registerAll() {
        for (var entry : CurioAPI.getRendererSuppliers().entrySet()) {
            String fullName = entry.getKey();
            ResourceLocation id = ResourceLocation.parse(fullName);
            Item item = BuiltInRegistries.ITEM.get(id);
            if (item != Items.AIR) {
                CuriosRendererRegistry.register(item, (Supplier<ICurioRenderer>) entry.getValue());
                onRegistered.accept(fullName);
            } else {
                onMissingItem.accept(fullName);
            }
        }
    }
}
