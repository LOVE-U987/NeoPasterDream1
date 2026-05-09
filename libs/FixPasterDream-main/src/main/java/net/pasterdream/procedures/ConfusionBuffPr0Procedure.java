package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModMobEffects;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Mth;

public class ConfusionBuffPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof Player == true) {
			if ((entity instanceof LivingEntity _livEnt && _livEnt.hasEffect(PasterdreamModMobEffects.CONFUSION_BUFF.get()) ? _livEnt.getEffect(PasterdreamModMobEffects.CONFUSION_BUFF.get()).getAmplifier() : 0) == 0) {
				{
					Entity _ent = entity;
					_ent.setYRot((float) (entity.getYRot() + Mth.nextDouble(RandomSource.create(), -1, 1)));
					_ent.setXRot((float) (entity.getXRot() + Mth.nextDouble(RandomSource.create(), -1, 1)));
					_ent.setYBodyRot(_ent.getYRot());
					_ent.setYHeadRot(_ent.getYRot());
					_ent.yRotO = _ent.getYRot();
					_ent.xRotO = _ent.getXRot();
					if (_ent instanceof LivingEntity _entity) {
						_entity.yBodyRotO = _entity.getYRot();
						_entity.yHeadRotO = _entity.getYRot();
					}
				}
			} else {
				{
					Entity _ent = entity;
					_ent.setYRot((float) (entity.getYRot() + Mth.nextDouble(RandomSource.create(), -2, 2)));
					_ent.setXRot((float) (entity.getXRot() + Mth.nextDouble(RandomSource.create(), -2, 2)));
					_ent.setYBodyRot(_ent.getYRot());
					_ent.setYHeadRot(_ent.getYRot());
					_ent.yRotO = _ent.getYRot();
					_ent.xRotO = _ent.getXRot();
					if (_ent instanceof LivingEntity _entity) {
						_entity.yBodyRotO = _entity.getYRot();
						_entity.yHeadRotO = _entity.getYRot();
					}
				}
			}
		} else {
			{
				Entity _ent = entity;
				_ent.setYRot((float) Mth.nextDouble(RandomSource.create(), -180, 180));
				_ent.setXRot((float) Mth.nextDouble(RandomSource.create(), -90, 90));
				_ent.setYBodyRot(_ent.getYRot());
				_ent.setYHeadRot(_ent.getYRot());
				_ent.yRotO = _ent.getYRot();
				_ent.xRotO = _ent.getXRot();
				if (_ent instanceof LivingEntity _entity) {
					_entity.yBodyRotO = _entity.getYRot();
					_entity.yHeadRotO = _entity.getYRot();
				}
			}
			entity.setDeltaMovement(new Vec3(0, (-1), 0));
		}
	}
}
