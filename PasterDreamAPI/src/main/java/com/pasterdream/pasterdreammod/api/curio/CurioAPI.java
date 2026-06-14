package com.pasterdream.pasterdreammod.api.curio;

import com.pasterdream.pasterdreammod.api.PasterDreamAPI;
import com.pasterdream.pasterdreammod.api.curio.model.CurioSlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.*;
import java.util.function.Supplier;

/**
 * 饰品注册 API（Curio API）—— 采用 Facade 模式 + Builder 模式，
 * 提供一站式饰品注册、属性修饰器设置及身体渲染配置功能。
 * <p>
 * 搭配 Curios API 使用，自动管理饰品在对应槽位的注册和标签绑定。
 * </p>
 * <p>
 * 使用示例：
 * <pre>{@code
 * // ====== 注册带属性加成的戒指 ======
 * CurioAPI.create("ember_ring")
 *     .slot(CurioSlot.RING)
 *     .stacksTo(1)
 *     .rarity(Rarity.EPIC)
 *     .attribute("minecraft:generic.attack_damage",
 *           "a1b2c3d4-e5f6-7890-abcd-ef1234567890", 2.0,
 *           AttributeModifier.Operation.ADD_VALUE)
 *     .tooltip("§c烈焰之戒", "§7攻击力 +2")
 *     .register();
 *
 * // ====== 注册带身体渲染的头部饰品 ======
 * CurioAPI.create("mystic_crown")
 *     .slot(CurioSlot.HEAD)
 *     .stacksTo(1)
 *     .rarity(Rarity.RARE)
 *     .tooltip("§b神秘皇冠")
 *     .renderBuiltin()  // 启用 Curios 默认身体渲染
 *     .register();
 * }</pre>
 * <p>
 * 注意：需要在 {@code PasterDreamMod} 构造函数中注册到事件总线：
 * <pre>{@code
 * CurioAPI.REGISTRY.register(modEventBus);
 * }</pre>
 * 身体渲染注册需在客户端初始化时调用 {@link #registerClientRenderers()}。
 * </p>
 */
public final class CurioAPI {

    /** 饰品专用注册器 */
    public static final DeferredRegister.Items REGISTRY =
            DeferredRegister.createItems(PasterDreamAPI.MOD_ID);

    /** 渲染器注册信息缓存：item_full_name -> render_type */
    static final Map<String, String> RENDERER_REGISTRY = new LinkedHashMap<>();

    /** 渲染器供应商缓存：item_full_name -> Supplier<ICurioRenderer>
     *  用于在客户端初始化时调用 CuriosRendererRegistry.register() */
    static final Map<String, Supplier<ICurioRenderer>> RENDERER_SUPPLIERS = new LinkedHashMap<>();

    /** 已注册的饰品列表（按注册顺序） */
    static final List<CurioRegistration> REGISTERED_CURIOS = new ArrayList<>();

    private CurioAPI() {}

    /**
     * 创建饰品构建器。
     *
     * @param name 注册名（不含命名空间，如 "ember_ring"）
     * @return 饰品构建器
     */
    public static CurioBuilder create(String name) {
        Objects.requireNonNull(name, "[CurioAPI] name 不能为空");
        return new CurioBuilder(REGISTRY, name);
    }

    /**
     * 获取所有已注册饰品的信息。
     *
     * @return 已注册饰品列表（不可修改）
     */
    public static List<CurioRegistration> getRegisteredCurios() {
        return Collections.unmodifiableList(REGISTERED_CURIOS);
    }

    /**
     * 获取所有已配置了渲染器的饰品供应商映射。
     *
     * @return item_full_name -> Supplier<ICurioRenderer>
     */
    public static Map<String, Supplier<ICurioRenderer>> getRendererSuppliers() {
        return Collections.unmodifiableMap(RENDERER_SUPPLIERS);
    }

    /**
     * 通过注册名查找已注册的饰品。
     *
     * @param name 注册名（不含命名空间）
     * @return 饰品注册信息，未找到时返回 empty
     */
    public static Optional<CurioRegistration> getCurio(String name) {
        return REGISTERED_CURIOS.stream()
                .filter(r -> r.name().equals(name))
                .findFirst();
    }

    /**
     * 注册所有已配置的身体渲染器。
     * <p>
     * <b>客户端专用：</b>请在客户端初始化事件中调用此方法，
     * 对应于 {@link top.theillusivec4.curios.api.client.CuriosRendererRegistry#register(Item, Supplier)} 的调用时机。
     * </p>
     * <pre>{@code
     * // 在客户端初始化时调用
     * CurioAPI.registerClientRenderers();
     * }</pre>
     */
    public static void registerClientRenderers() {
        // 委托给客户端处理器
        if (clientBridge != null) {
            clientBridge.registerAll();
        }
    }

    // ======================== 内部数据结构 ========================

    /**
     * 饰品注册信息 —— 记录每个已注册饰品的关键元数据
     *
     * @param name     注册名（不含命名空间）
     * @param fullName 完整注册名（含命名空间）
     * @param slotId   槽位标识
     * @param item     注册的物品实例
     * @param renderType 渲染类型（"none"、"builtin"、"custom"）
     */
    public record CurioRegistration(
            String name,
            String fullName,
            String slotId,
            Item item,
            String renderType
    ) {}

    // ======================== 内部桥接接口 ========================

    /**
     * 客户端渲染注册桥接接口。
     * <p>
     * 实现类需要在客户端初始化时注册到 {@link #setClientBridge(CurioClientBridge)}，
     * 以确保 {@link #registerClientRenderers()} 能够正确调用客户端代码。
     * </p>
     * <p>
     * 一般不需要手动调用，由 {@link CurioClientHandler} 自动完成。
     * </p>
     */
    public interface CurioClientBridge {
        /** 注册所有挂起的身体渲染器 */
        void registerAll();
    }

    private static CurioClientBridge clientBridge;

    /**
     * 设置客户端桥接实现（仅供内部使用）。
     *
     * @param bridge 客户端桥接实现
     */
    public static void setClientBridge(CurioClientBridge bridge) {
        clientBridge = bridge;
    }

    /**
     * 获取槽位对应的标签文件中的物品列表。
     *
     * @param slotId 槽位标识
     * @return 该槽位下所有已注册物品的完整注册名列表
     */
    public static List<String> getItemsBySlot(String slotId) {
        return REGISTERED_CURIOS.stream()
                .filter(r -> r.slotId().equals(slotId))
                .map(CurioRegistration::fullName)
                .toList();
    }
}