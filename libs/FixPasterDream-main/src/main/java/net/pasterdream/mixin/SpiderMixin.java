package net.pasterdream.mixin;

import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.pasterdream.utils.AvoidPerfumeGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Spider.class)
public class SpiderMixin {
    @Inject(method = "registerGoals()V", at = @At("HEAD"))
    private void goals(CallbackInfo ci) {
        var thiz = (Spider)(Object) this;
        thiz.goalSelector.addGoal(3, new AvoidPerfumeGoal(thiz));
    }
}
