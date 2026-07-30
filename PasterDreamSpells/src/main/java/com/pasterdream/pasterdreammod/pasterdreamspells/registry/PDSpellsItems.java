package com.pasterdream.pasterdreammod.pasterdreamspells.registry;

import com.pasterdream.pasterdreammod.pasterdreamspells.PasterDreamSpellsMod;
import com.pasterdream.pasterdreammod.pasterdreamspells.entity.projectile.SpellProjectileEntity;
import com.pasterdream.pasterdreammod.pasterdreamspells.item.SpellItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 法术系统物品注册类。
 * <p>
 * 负责注册五种法术卷轴（闪电/剧毒/治疗/狂暴/冰冻），均使用
 * {@link PasterDreamSpellsMod#MOD_ID}（{@code pasterdreamspells}）命名空间。
 *
 * @author PasterDream
 */
public class PDSpellsItems {

    /** 物品注册器 */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PasterDreamSpellsMod.MOD_ID);

    private PDSpellsItems() {
    }

    // ==================== 法术物品（梦境炼药锅炼制产出，还原自原版 PasterDream） ====================

    /**
     * 闪电法术 (lightning_spell)
     * 蓄力后松开发射闪电法术投射物，命中后在 5*5 区域生成 4 次随机落雷。
     */
    public static final DeferredItem<Item> LIGHTNING_SPELL = ITEMS.register("lightning_spell",
            () -> new SpellItem(new Item.Properties().stacksTo(8).rarity(Rarity.COMMON),
                    (level, caster) -> SpellProjectileEntity.shoot(level, caster,
                            SpellProjectileEntity.SpellType.LIGHTNING),
                    "item.pasterdreamspells.lightning_spell.desc0"));

    /**
     * 剧毒法术 (poison_spell)
     * 蓄力后松开发射剧毒法术投射物，命中后对 7*7 区域发动三波剧毒攻势。
     */
    public static final DeferredItem<Item> POISON_SPELL = ITEMS.register("poison_spell",
            () -> new SpellItem(new Item.Properties().stacksTo(8).rarity(Rarity.COMMON),
                    (level, caster) -> SpellProjectileEntity.shoot(level, caster,
                            SpellProjectileEntity.SpellType.POISON),
                    "item.pasterdreamspells.poison_spell.desc0"));

    /**
     * 治疗法术 (healing_spell)
     * 蓄力后松开发射治疗法术投射物，命中后生成治疗立场。
     */
    public static final DeferredItem<Item> HEALING_SPELL = ITEMS.register("healing_spell",
            () -> new SpellItem(new Item.Properties().stacksTo(8).rarity(Rarity.COMMON),
                    (level, caster) -> SpellProjectileEntity.shoot(level, caster,
                            SpellProjectileEntity.SpellType.HEALING),
                    "item.pasterdreamspells.healing_spell.desc0",
                    "item.pasterdreamspells.healing_spell.desc1"));

    /**
     * 狂暴法术 (fury_spell)
     * 蓄力后松开发射狂暴法术投射物，命中后生成狂暴立场。
     */
    public static final DeferredItem<Item> FURY_SPELL = ITEMS.register("fury_spell",
            () -> new SpellItem(new Item.Properties().stacksTo(8).rarity(Rarity.COMMON),
                    (level, caster) -> SpellProjectileEntity.shoot(level, caster,
                            SpellProjectileEntity.SpellType.FURY),
                    "item.pasterdreamspells.fury_spell.desc0",
                    "item.pasterdreamspells.fury_spell.desc1",
                    "item.pasterdreamspells.fury_spell.desc2"));

    /**
     * 冰冻法术 (ice_spell)
     * 蓄力后松开发射冰冻法术投射物，命中后对 7*7 区域发动 5 波冻结。
     */
    public static final DeferredItem<Item> ICE_SPELL = ITEMS.register("ice_spell",
            () -> new SpellItem(new Item.Properties().stacksTo(8).rarity(Rarity.COMMON),
                    (level, caster) -> SpellProjectileEntity.shoot(level, caster,
                            SpellProjectileEntity.SpellType.ICE),
                    "item.pasterdreamspells.ice_spell.desc0"));

    /**
     * 在公共设置阶段执行的额外注册（预留钩子）。
     * <p>
     * 当前法术物品已在静态字段中完成注册，此方法保留以兼容后续 SpellAPI 扩展。
     */
    public static void registerSpells() {
        // 法术物品通过静态字段延迟注册，无需额外操作
    }
}
