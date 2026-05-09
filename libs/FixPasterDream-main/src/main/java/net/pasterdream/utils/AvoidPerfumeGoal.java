package net.pasterdream.utils;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.pasterdream.init.PasterdreamModMobEffects;

public class AvoidPerfumeGoal extends AvoidEntityGoal<Player> {
    public AvoidPerfumeGoal(PathfinderMob mob) {
        super(mob, Player.class, 6.0F, 1.0D, 1.2D);
    }

    @Override
    public boolean canUse() {
        this.toAvoid = this.mob.level().getNearestPlayer(this.mob, this.maxDist);
        if (this.toAvoid == null) {
            return false;
        } else if (toAvoid.hasEffect(PasterdreamModMobEffects.DYEDREAM_PERFUME_BUFF.get())) {
            Vec3 vec3 = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.toAvoid.position());
            if (vec3 == null) {
                return false;
            } else if (this.toAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < this.toAvoid.distanceToSqr(this.mob)) {
                return false;
            } else {
                this.path = this.pathNav.createPath(vec3.x, vec3.y, vec3.z, 0);
                return this.path != null;
            }
        }

        return false;
    }
}
