package com.pasterdream.pasterdreammod.api.menu;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.menu.builder.MenuBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.pasterdream.pasterdreammod.api.util.PDDebugLogger;
/**
 * 菜单类型注册 API —— 集中管理 MenuType 的注册与查询
 * <p>
 * 采用 Facade 模式 + Builder 模式设计，与 {@link com.pasterdream.pasterdreammod.api.block.BlockAPI} 风格一致。
 * <p>
 * 使用示例：
 * <pre>{@code
 * MenuAPI.register("shadow_chest", () -> IMenuTypeExtension.create(ShadowChestMenu::new));
 *
 * // 或使用 Builder
 * MenuAPI.createMenu("shadow_chest")
 *     .factory(ShadowChestMenu::new)
 *     .build();
 * }</pre>
 */
public final class MenuAPI {

    /**
     * API 专属的菜单类型注册器。
     * 需要在 {@code PasterDreamMod} 构造函数中通过 {@link #registerAll(IEventBus)} 注册到事件总线。
     */
    public static final DeferredRegister<MenuType<?>> REGISTRY =
            DeferredRegister.create(Registries.MENU, PasterDreamAPI.DATA_NAMESPACE);

    /** 已注册菜单类型的 DeferredHolder 缓存 */
    private static final Map<String, DeferredHolder<MenuType<?>, ? extends MenuType<?>>> HOLDERS =
            new HashMap<>();

    private MenuAPI() {
        throw new UnsupportedOperationException("MenuAPI 是纯静态门面类，不可实例化");
    }

    /**
     * 注册一个菜单类型。
     *
     * @param name     菜单注册名（snake_case 格式）
     * @param supplier 菜单类型 Supplier
     * @param <T>      菜单类型参数
     * @return 注册后的 DeferredHolder
     */
    public static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> register(
            String name, Supplier<MenuType<T>> supplier) {
        DeferredHolder<MenuType<?>, MenuType<T>> holder = REGISTRY.register(name, supplier);
        HOLDERS.put(name, holder);
        PDDebugLogger.apiDebug("[MenuAPI] 已注册菜单: {}", name);
        return holder;
    }

    /**
     * 创建一个菜单构建器。
     *
     * @param name 菜单注册名
     * @param <T>  菜单类型参数
     * @return {@link MenuBuilder} 实例
     */
    public static <T extends AbstractContainerMenu> MenuBuilder<T> createMenu(String name) {
        return new MenuBuilder<>(name);
    }

    /**
     * 根据注册名查询菜单类型。
     *
     * @param name 菜单注册名
     * @return {@link MenuType}，未找到返回 null
     */
    @Nullable
    public static MenuType<?> getMenuType(String name) {
        DeferredHolder<MenuType<?>, ? extends MenuType<?>> holder = HOLDERS.get(name);
        return holder != null ? holder.get() : null;
    }

    /**
     * 根据注册名查询菜单类型的 DeferredHolder。
     *
     * @param name 菜单注册名
     * @return DeferredHolder，未找到返回 null
     */
    @Nullable
    public static DeferredHolder<MenuType<?>, ? extends MenuType<?>> getHolder(String name) {
        return HOLDERS.get(name);
    }

    /**
     * 获取所有已注册菜单类型的不可变视图。
     *
     * @return 注册名到 DeferredHolder 的映射
     */
    public static Map<String, DeferredHolder<MenuType<?>, ? extends MenuType<?>>> getRegisteredMenus() {
        return Collections.unmodifiableMap(HOLDERS);
    }

    /**
     * 将菜单类型注册器注册到模组事件总线。
     *
     * @param modEventBus NeoForge 模组事件总线
     */
    public static void registerAll(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
        PDDebugLogger.apiDebug("[MenuAPI] 已注册菜单 DeferredRegister 到事件总线");
    }

    /**
     * 重置所有静态缓存，供测试使用。
     * <p>
     * 清空已缓存的菜单类型 DeferredHolder，使每次测试都在干净的缓存状态下运行。
     * 注意：此方法不会取消 DeferredRegister 中的已注册菜单类型，仅清除 API 层面的缓存数据。
     */
    public static void resetForTesting() {
        HOLDERS.clear();
    }
}
