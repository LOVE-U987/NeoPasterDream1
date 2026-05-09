package net.pasterdream.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.pasterdream.init.PasterdreamModItems;

import java.util.function.Supplier;

public class PasterDreamGlassDrinkItem extends PasterDreamFoodItem{
    final private Supplier<Item> Retval;//使用后返还的道具
    public PasterDreamGlassDrinkItem(Properties properties, int num, boolean forced_Display, Supplier<Item> retval) {
        super(properties, num, forced_Display);
        Retval = retval;
    }
    public PasterDreamGlassDrinkItem(Properties properties, int num, boolean forced_Display) {
        this(properties, num, forced_Display, PasterdreamModItems.GLASS_CUP);
    }
    public PasterDreamGlassDrinkItem(Properties properties, int num ,Supplier<Item> retval) {
        super(properties, num,false);
        Retval = retval;
    }
    public PasterDreamGlassDrinkItem(Properties properties, int num) {
        this(properties, num, PasterdreamModItems.GLASS_CUP);
    }
    public PasterDreamGlassDrinkItem(Properties properties , Supplier<Item>retval) {
        super(properties);
        Retval = retval;
    }
    public PasterDreamGlassDrinkItem(Properties properties) {
        this(properties, PasterdreamModItems.GLASS_CUP);
    }
    @Override
    public UseAnim getUseAnimation(ItemStack itemstack) {
        return UseAnim.DRINK;
    }
    @Override
    public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
        super.finishUsingItem(itemstack, world, entity);
        if (itemstack.isEmpty()) {
            return new ItemStack(Retval.get());
        } else {
            if (entity instanceof Player player && !player.getAbilities().instabuild) {
                ItemStack retval = new ItemStack(Retval.get());
                if (!player.getInventory().add(retval))
                    player.drop(retval, false);
            }
            return itemstack;
        }
    }
}
