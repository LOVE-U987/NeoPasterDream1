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

package top.theillusivec4.curios.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosDataProvider;
import top.theillusivec4.curios.api.type.data.IEntitiesData;
import top.theillusivec4.curios.api.type.data.ISlotData;
import top.theillusivec4.curios.common.data.EntitiesData;
import top.theillusivec4.curios.common.data.SlotData;

@Mixin(value = CuriosDataProvider.class, remap = false)
public class MixinCuriosDataProvider {

  @Inject(at = @At("HEAD"), method = "createSlotData", cancellable = true)
  private static void curios$createSlotData(CallbackInfoReturnable<ISlotData> cir) {
    cir.setReturnValue(new SlotData());
  }

  @Inject(at = @At("HEAD"), method = "createEntitiesData", cancellable = true)
  private static void curios$createEntitiesData(CallbackInfoReturnable<IEntitiesData> cir) {
    cir.setReturnValue(new EntitiesData());
  }
}
