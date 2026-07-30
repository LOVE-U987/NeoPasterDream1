package com.pasterdream.pasterdreammod.menu;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.registry.PDItems;
import com.pasterdream.pasterdreammod.registry.PDMenus;
import com.pasterdream.pasterdreammod.registry.PDSounds;
import com.pasterdream.pasterdreammod.api.util.ServerScheduler;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

/**
 * 影之抉择 GUI 容器菜单 (Shadow Select End Menu)
 * <p>
 * 无槽位的纯选择界面（原版 ShadowSelectEndMenu，320×200 背景 + 两个图片按钮）。
 * 原版由真影之床（TrueShadowBedPr0Procedure）在服务端打开；真影之床属后续波次，
 * 落地后经 {@code serverPlayer.openMenu(provider, pos)} 打开本菜单即可。
 * <p>
 * 两个按钮经 vanilla {@code clickMenuButton} 通道触发
 * （等价原版 ShadowSelectEndButtonMessage 按钮 0/1 语义）：
 * <ul>
 *   <li>按钮 0"黑暗"（原版 ShadowSelectEndPr0）：授予 achievement_shadow_d_0 与
 *       achievement_talent_shadow 成就 + shadow_door 音效 + 赠送暗影剑柄 +
 *       四段延迟旁白（80/140/200/260 tick）；</li>
 *   <li>按钮 1"光明"（原版 ShadowSelectEndPr1）：授予 achievement_shadow_d_0 与
 *       achievement_talent_light 成就 + shadow_door 音效 + 赠送白水晶与提示。</li>
 * </ul>
 */
public class ShadowSelectEndMenu extends AbstractContainerMenu {

    /** "黑暗"按钮的菜单按钮 ID（原版按钮 0） */
    public static final int BUTTON_DARK = 0;
    /** "光明"按钮的菜单按钮 ID（原版按钮 1） */
    public static final int BUTTON_LIGHT = 1;

    /**
     * 构造影之抉择菜单（从网络缓冲区接收；缓冲区携带触发位置，仅用于音效定位）
     *
     * @param id        容器 ID
     * @param inv       玩家库存
     * @param extraData 包含 BlockPos 的网络缓冲区（可为空缓冲）
     */
    public ShadowSelectEndMenu(int id, Inventory inv, net.minecraft.network.FriendlyByteBuf extraData) {
        this(id, inv);
        if (extraData != null && extraData.readableBytes() >= 8) {
            extraData.readBlockPos();
        }
    }

    /**
     * 构造影之抉择菜单
     *
     * @param id  容器 ID
     * @param inv 玩家库存
     */
    public ShadowSelectEndMenu(int id, Inventory inv) {
        super(PDMenus.SHADOW_SELECT_END.get(), id);
    }

    /**
     * 处理 GUI 按钮点击（服务端调用）
     *
     * @param player 点击按钮的玩家
     * @param id     按钮 ID（{@link #BUTTON_DARK} / {@link #BUTTON_LIGHT}）
     * @return 是否处理了该按钮
     */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return id == BUTTON_DARK || id == BUTTON_LIGHT;
        }
        if (id == BUTTON_DARK) {
            chooseDark(serverPlayer);
            return true;
        }
        if (id == BUTTON_LIGHT) {
            chooseLight(serverPlayer);
            return true;
        }
        return false;
    }

    /**
     * "黑暗"结局选择（原版 ShadowSelectEndPr0Procedure）
     *
     * @param player 服务端玩家
     */
    private static void chooseDark(ServerPlayer player) {
        player.closeContainer();
        grantAdvancement(player, "achievement_shadow_d_0");
        grantAdvancement(player, "achievement_talent_shadow");
        playShadowDoor(player);
        giveItem(player, new ItemStack(PDItems.SHADOW_HILT.get().asItem()));
        ServerScheduler.schedule(80, () ->
                player.displayClientMessage(Component.literal("无名：看来你已经做出了选择"), false));
        ServerScheduler.schedule(140, () ->
                player.displayClientMessage(Component.literal("无名：如果你想战胜祂 阻止祂 接纳祂 还是融入祂"), false));
        ServerScheduler.schedule(200, () ->
                player.displayClientMessage(Component.literal("无名：就请在这片灯影中寻找吧 寻找亚伦柯斯双手的眼睛"), false));
        ServerScheduler.schedule(260, () ->
                player.displayClientMessage(Component.literal("§a在此维度中寻找四根手指组成的类似手的遗迹，并进入中间的传送门"), false));
    }

    /**
     * "光明"结局选择（原版 ShadowSelectEndPr1Procedure）
     *
     * @param player 服务端玩家
     */
    private static void chooseLight(ServerPlayer player) {
        player.closeContainer();
        grantAdvancement(player, "achievement_shadow_d_0");
        grantAdvancement(player, "achievement_talent_light");
        playShadowDoor(player);
        player.displayClientMessage(Component.literal("§7一块闪耀着白光的水晶出现在了你的手里"), false);
        giveItem(player, new ItemStack(PDItems.WHITE_CRYSTAL.get().asItem()));
    }

    /** 在玩家位置播放暗影门音效 */
    private static void playShadowDoor(ServerPlayer player) {
        player.serverLevel().playSound(null, BlockPos.containing(player.getX(), player.getY(), player.getZ()),
                PDSounds.SHADOW_DOOR.get(), SoundSource.NEUTRAL, 1, 1);
    }

    /** 把物品放入玩家背包，放不下则掉落（原版 ItemHandlerHelper.giveItemToPlayer 语义） */
    private static void giveItem(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    /**
     * 授予成就的全部条件（原版 award remaining criteria 语义）；
     * 防御性：holder 缺失时打调试日志并跳过（datapack 缺失/命名错误时不崩）
     *
     * @param player 服务端玩家
     * @param path   成就注册路径
     */
    private static void grantAdvancement(ServerPlayer player, String path) {
        AdvancementHolder holder = player.server.getAdvancements()
                .get(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path));
        if (holder == null) {
            PasterDreamMod.LOGGER.debug("[ShadowSelectEnd] 成就 {} 未注册，跳过授予", path);
            return;
        }
        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(holder);
        if (!progress.isDone()) {
            for (String criterion : progress.getRemainingCriteria()) {
                player.getAdvancements().award(holder, criterion);
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
