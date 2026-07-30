package com.pasterdream.pasterdreammod.network;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * S2C：客户端全屏物品展示动画（图腾式 {@code GameRenderer#displayItemActivation}）。
 * <p>
 * 对应原版卡勒卡牌 1–9 / 塞西莉娅加护等在客户端直接调用
 * {@code Minecraft.getInstance().gameRenderer.displayItemActivation(...)}；
 * 新版服务端权威，经本包下发物品 id，由客户端渲染。
 *
 * @param itemId 展示物品的注册表 id（{@link BuiltInRegistries#ITEM} 数字 id）
 */
public record ItemActivationPayload(int itemId) implements CustomPacketPayload {

    /** 包类型标识 */
    public static final Type<ItemActivationPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, "item_activation"));

    /** 网络编解码器 */
    public static final StreamCodec<ByteBuf, ItemActivationPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, ItemActivationPayload::itemId,
                    ItemActivationPayload::new);

    /**
     * 由物品构造包。
     *
     * @param item 要全屏展示的物品
     * @return 包实例
     */
    public static ItemActivationPayload of(Item item) {
        return new ItemActivationPayload(BuiltInRegistries.ITEM.getId(item));
    }

    /**
     * 由物品栈构造包（取栈内物品）。
     *
     * @param stack 物品栈
     * @return 包实例
     */
    public static ItemActivationPayload of(ItemStack stack) {
        return of(stack.getItem());
    }

    /**
     * 解析为物品栈（未知 id 时返回空栈）。
     *
     * @return 展示用物品栈
     */
    public ItemStack asStack() {
        Item item = BuiltInRegistries.ITEM.byId(itemId);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
