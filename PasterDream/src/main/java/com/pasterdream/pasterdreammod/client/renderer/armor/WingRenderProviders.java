package com.pasterdream.pasterdreammod.client.renderer.armor;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.function.Supplier;

/**
 * 翅膀盔甲渲染器提供者（客户端专属）。
 * <p>
 * 由 common 物品类的 {@code createGeoRenderer} 经反射调用（仅客户端物理环境触发），
 * 保证专用服加载物品类时不会连带加载任何 {@code net.minecraft.client.*} 依赖，
 * 从而规避 DS 启动阶段的 NoClassDefFoundError 风险。
 */
public final class WingRenderProviders {

    private WingRenderProviders() {
        throw new UnsupportedOperationException("WingRenderProviders 是工具类，不可实例化");
    }

    /** 天使之翼渲染器提供者 */
    public static GeoRenderProvider angelWing() {
        return armorProvider(() -> new AngelWingArmorRenderer());
    }

    /** 机械之翼渲染器提供者 */
    public static GeoRenderProvider machineWing() {
        return armorProvider(() -> new MachineWingArmorRenderer());
    }

    /** 遗落之翼渲染器提供者 */
    public static GeoRenderProvider forsakensWing() {
        return armorProvider(() -> new ForsakensWingArmorRenderer());
    }

    /**
     * 构造延迟初始化的盔甲渲染器提供者（渲染时才实例化渲染器）。
     *
     * @param factory 渲染器工厂
     * @return 懒加载的 {@link GeoRenderProvider}
     */
    private static GeoRenderProvider armorProvider(Supplier<GeoArmorRenderer<?>> factory) {
        return new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <E extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
                    E livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<E> original) {
                if (this.renderer == null) {
                    this.renderer = factory.get();
                }
                return this.renderer;
            }
        };
    }
}
