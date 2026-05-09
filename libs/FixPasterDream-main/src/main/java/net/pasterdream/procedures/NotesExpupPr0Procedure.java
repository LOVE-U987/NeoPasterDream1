package net.pasterdream.procedures;

import net.pasterdream.init.PasterdreamModMobEffects;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.network.chat.Component;

public class NotesExpupPr0Procedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(PasterdreamModMobEffects.EXPUP_BUFF.get(), 3600, 0));
		if (entity instanceof Player _player && !_player.level().isClientSide())
			_player.displayClientMessage(Component.literal(
					"\u4F60\u6536\u5230\u4E86\u7B14\u8BB0\u7684\u542F\u53D1\uFF0C\u968F\u00A7a\u65F6\u95F4\u6D41\u901D\u00A7f\u7F13\u6162\u83B7\u5F97\u00A7e\u5C11\u91CF\u7ECF\u9A8C\u00A7f\uFF0C\u5E76\u5728\u00A7a\u83B7\u53D6\u7ECF\u9A8C\u65F6\u00A7f\u53EF\u4EE5\u00A7e\u5EF6\u957F\u00A7f\u7ECF\u9A8C\u63D0\u5347\u7684\u6548\u679C\u65F6\u95F4"),
					false);
	}
}
