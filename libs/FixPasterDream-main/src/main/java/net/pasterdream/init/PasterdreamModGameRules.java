
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.pasterdream.init;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.level.GameRules;
import net.pasterdream.communication.ChannelEventTracker;
import net.pasterdream.communication.SanDataMessage;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class PasterdreamModGameRules {
	public static final GameRules.Key<GameRules.IntegerValue> RANDOMCOORDX = GameRules.register("randomCoordX", GameRules.Category.SPAWNING, GameRules.IntegerValue.create(0));
	public static final GameRules.Key<GameRules.IntegerValue> RANDOMCOORDZ = GameRules.register("randomCoordZ", GameRules.Category.SPAWNING, GameRules.IntegerValue.create(0));
	public static final GameRules.Key<GameRules.BooleanValue> PASTERDREAMDEBUGMODE = GameRules.register("pasterdreamDebugmode", GameRules.Category.MISC, GameRules.BooleanValue.create(false));
	public static final GameRules.Key<GameRules.IntegerValue> PASTERDREAM_WIND_DIRECTION = GameRules.register("pasterdreamWindDirection", GameRules.Category.MISC, GameRules.IntegerValue.create(0));
    public static final GameRules.Key<GameRules.BooleanValue> SANCHECKSYSTEM = GameRules.register("pasterdreamSanSystem", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true,(server, rule) -> {
        for(ServerPlayer serverplayer : server.getPlayerList().getPlayers()) {
            ChannelEventTracker.sendToPlayer(new SanDataMessage(rule.get()),serverplayer);
        }
    }));
    public static final GameRules.Key<GameRules.IntegerValue> STARTSANONREVIVE = GameRules.register("pasterdreamStartSanOnRevive", GameRules.Category.PLAYER, GameRules.IntegerValue.create(90));
    //每多少tick执行一次san回复，总量保持不变，仅影响对应函数执行频率
    public static final GameRules.Key<GameRules.IntegerValue> SANVARIABILITYPERTICK = GameRules.register("pasterdreamSanVariabilityPerTick", GameRules.Category.PLAYER, GameRules.IntegerValue.create(5));
}
