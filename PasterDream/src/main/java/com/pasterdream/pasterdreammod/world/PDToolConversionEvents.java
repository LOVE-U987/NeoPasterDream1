package com.pasterdream.pasterdreammod.world;

import com.pasterdream.pasterdreammod.registry.PDBlocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * 工具与方块交互转换事件（锄头开垦 / 斧头剥皮）。
 * <p>
 * NeoForge 的锄头/斧头在右键方块时会调用 {@code BlockState.getToolModifiedState}
 * 触发 {@link BlockEvent.BlockToolModificationEvent}，本类在此将染梦/冷域专属方块
 * 接入工具转换：
 * <ul>
 *   <li><b>锄头开垦</b>（{@link ItemAbilities#HOE_TILL}）：染梦草方块 / 染梦泥土 → 染梦耕地</li>
 *   <li><b>斧头剥皮</b>（{@link ItemAbilities#AXE_STRIP}）：冷域木头 → 去皮冷域木头（保留轴向）</li>
 * </ul>
 * 音效、粒子、耐久消耗由原版工具逻辑自动处理。
 */
public final class PDToolConversionEvents {

    private PDToolConversionEvents() {
    }

    /**
     * 工具修改方块事件处理
     *
     * @param event 工具修改方块事件
     */
    @SubscribeEvent
    public static void onBlockToolModification(BlockEvent.BlockToolModificationEvent event) {
        var ability = event.getItemAbility();
        if (ability == null) {
            return;
        }
        BlockState original = event.getState();

        if (ability.equals(ItemAbilities.HOE_TILL)) {
            // 锄头开垦：染梦草方块 / 染梦泥土 → 染梦耕地
            if (original.is(PDBlocks.DYEDREAM_GRASS.get())
                    || original.is(PDBlocks.DYEDREAM_DIRT.get())) {
                event.setFinalState(PDBlocks.DYEDREAM_FARMLAND.get().defaultBlockState());
            }
        } else if (ability.equals(ItemAbilities.AXE_STRIP)) {
            // 斧头剥皮：冷域木头 → 去皮冷域木头（保留轴向）
            if (original.is(PDBlocks.COLD_DOMAIN_LOG.get())) {
                event.setFinalState(PDBlocks.STRIPPED_COLD_DOMAIN_LOG.get().defaultBlockState()
                        .setValue(RotatedPillarBlock.AXIS, original.getValue(RotatedPillarBlock.AXIS)));
            }
        }
    }
}
