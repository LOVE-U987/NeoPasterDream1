/*
 * Copyright (c) 2018-2024 C4
 *
 * This file is part of Curios, a mod made for Minecraft.
 *
 * Curios is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Curios is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Curios.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package top.theillusivec4.curios.platform;

import java.util.Map;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.platform.services.ICuriosPlatform;

public class NeoForgeCurios implements ICuriosPlatform {

  @Override
  public Map<String, ISlotType> getItemStackSlots(ItemStack stack,
                                                  @Nullable LivingEntity livingEntity) {
    return livingEntity != null ? CuriosApi.getItemStackSlots(stack, livingEntity) :
        CuriosApi.getItemStackSlots(stack, true);
  }

  @Override
  public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity livingEntity) {
    return stack.makesPiglinsNeutral(livingEntity);
  }

  @Override
  public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity livingEntity) {
    return stack.canWalkOnPowderedSnow(livingEntity);
  }

  @Override
  public boolean isEnderMask(ItemStack stack, Player player, EnderMan enderMan) {
    return stack.isEnderMask(player, enderMan);
  }
}
