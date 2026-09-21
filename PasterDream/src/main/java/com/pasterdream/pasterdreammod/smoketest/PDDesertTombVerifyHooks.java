package com.pasterdream.pasterdreammod.smoketest;

import com.pasterdream.pasterdreammod.PasterDreamMod;
import com.pasterdream.pasterdreammod.block.entity.W4DataBlockEntity;
import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

/**
 * 荒漠英雄之墓升级流程校验钩子。
 * <p>
 * 覆盖阶段 3：携附魔『朔漠大剑』(desert_sword) 交互，升级为『沉荆门』朔漠
 * (true_desert_sword) 后附魔不得丢失。供 {@link PDPortingVerifyTest} 调用。
 */
public final class PDDesertTombVerifyHooks {

    /** 单条断言结果 */
    public record Result(boolean pass, String name, String detail) {
    }

    private PDDesertTombVerifyHooks() {
    }

    /**
     * 运行荒漠英雄之墓升级校验
     *
     * @param player 服务端玩家（主手携带附魔剑）
     * @param out    断言输出
     */
    public static void verify(ServerPlayer player, Consumer<Result> out) {
        if (player == null) {
            out.accept(new Result(false, "荒漠之墓升级：玩家为空", "player == null"));
            return;
        }
        ServerLevel level = player.serverLevel();
        BlockPos pos = player.blockPosition().offset(3, 0, 0);
        level.getChunk(pos);

        Holder<Enchantment> sharpness = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SHARPNESS);

        try {
            level.setBlock(pos, PDBlocks.DESERT_HERO_TOMB.get().defaultBlockState(), 3);
            if (level.getBlockEntity(pos) == null) {
                out.accept(new Result(false, "荒漠之墓 BE 已创建", "blockEntity == null"));
                return;
            }

            // 阶段 3：携剑再访
            W4DataBlockEntity.putDoubleAt(level, pos, "number", 3);

            ItemStack sword = new ItemStack(item("desert_sword"));
            sword.enchant(sharpness, 5);
            player.setItemInHand(InteractionHand.MAIN_HAND, sword);

            BlockState state = level.getBlockState(pos);
            BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
            state.useWithoutItem(level, player, hit);

            ItemStack upgraded = player.getMainHandItem();
            boolean isTrueSword = upgraded.is(item("true_desert_sword"));
            int sharp = EnchantmentHelper.getEnchantmentsForCrafting(upgraded).getLevel(sharpness);
            out.accept(new Result(isTrueSword && sharp == 5,
                    "荒漠之墓升级保留附魔",
                    "trueSword=" + isTrueSword + " sharpness=" + sharp));
        } finally {
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            level.removeBlock(pos, false);
        }
    }

    private static Item item(String path) {
        return BuiltInRegistries.ITEM
                .getOptional(ResourceLocation.fromNamespaceAndPath(PasterDreamMod.MOD_ID, path))
                .orElse(Items.AIR);
    }
}
