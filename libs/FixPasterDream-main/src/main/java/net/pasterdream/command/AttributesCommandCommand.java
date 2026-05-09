
package net.pasterdream.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.pasterdream.capability.MeltDreamEnergyCapability;
import net.pasterdream.capability.SanCapability;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraft.commands.Commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;

@Mod.EventBusSubscriber
public class AttributesCommandCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("pasterdream")
                .requires(s -> s.hasPermission(4))
                .then(Commands.literal("san")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.literal("set")
                                        .then(Commands.argument("san_value", DoubleArgumentType.doubleArg(0,100))
                                                .executes(arguments -> {
                                                    if(SanCapability.IsSanCheckSystem())
                                                    {
                                                        ServerPlayer sp = EntityArgument.getPlayer(arguments, "player");
                                                        SanCapability.setPlayerSanWithCheck(sp,DoubleArgumentType.getDouble(arguments, "san_value"));
                                                        sp.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).ifPresent(cap -> {
                                                            arguments.getSource().sendSuccess(() -> Component.literal(String.format(Component.translatable("command.pasterdream.san.get").getString(),sp.getName().getString(), cap.getSanValue()))
                                                                    ,false);
                                                        });
                                                    }
                                                    else{
                                                        arguments.getSource().sendFailure(Component.translatable("command.pasterdream.san.fail"));
                                                    }
                                                    return 0;
                                                })
                                        )
                                )
                                .then(Commands.literal("add")
                                        .then(Commands.argument("san_value", DoubleArgumentType.doubleArg(-100,100))
                                                .executes(arguments -> {
                                                    if(SanCapability.IsSanCheckSystem())
                                                    {
                                                        ServerPlayer sp = EntityArgument.getPlayer(arguments, "player");
                                                        SanCapability.addPlayerSanWithCheck(sp,DoubleArgumentType.getDouble(arguments, "san_value"));
                                                        sp.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).ifPresent(cap -> {
                                                            arguments.getSource().sendSuccess(() -> Component.literal(String.format(Component.translatable("command.pasterdream.san.get").getString(),sp.getName().getString(), cap.getSanValue()))
                                                                    ,false);
                                                        });
                                                    }
                                                    else{
                                                        arguments.getSource().sendFailure(Component.translatable("command.pasterdream.san.fail"));
                                                    }
                                                    return 0;
                                                })
                                        )
                                )
                                .then(Commands.literal("get")
                                            .executes(arguments -> {
                                                if(SanCapability.IsSanCheckSystem())
                                                {
                                                    ServerPlayer sp = EntityArgument.getPlayer(arguments, "player");
                                                    sp.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).ifPresent(cap -> {
                                                        arguments.getSource().sendSuccess(() -> Component.literal(String.format(Component.translatable("command.pasterdream.san.get").getString(),sp.getName().getString(), cap.getSanValue()))
                                                                ,false);
                                                    });
                                                }
                                                else{
                                                    arguments.getSource().sendFailure(Component.translatable("command.pasterdream.san.fail"));
                                                }
                                                return 0;
                                            })
                                )
                        )
                )
                .then(Commands.literal("meltdreamenergy")
                        .then(Commands.literal("no_need_consume")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.literal("set")
                                                .then(Commands.argument("flag", BoolArgumentType.bool())
                                                        .executes( arguments ->{
                                                            ServerPlayer sp = EntityArgument.getPlayer(arguments, "player");
                                                            boolean flag = BoolArgumentType.getBool(arguments,"flag");
                                                            MeltDreamEnergyCapability.setPlayerMeltDreamEnergyNoNeedConsumeByCommand(sp,flag);
                                                            sp.getCapability(MeltDreamEnergyCapability.Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).ifPresent(cap->{
                                                                if(!cap.getNoNeedConsume())
                                                                {
                                                                    arguments.getSource().sendSuccess(() -> Component.literal(String.format(Component.translatable("command.pasterdream.meltdreamenergy.no_need_consume.get.0").getString(),sp.getName().getString()))
                                                                            ,false);
                                                                }
                                                                else{
                                                                    if(cap.getNoNeedConsumeValueByCommand())
                                                                    {
                                                                        arguments.getSource().sendSuccess(() -> Component.literal(String.format(Component.translatable("command.pasterdream.meltdreamenergy.no_need_consume.get.2").getString(),sp.getName().getString()))
                                                                                ,false);
                                                                    }
                                                                    else{
                                                                        arguments.getSource().sendSuccess(() -> Component.literal(String.format(Component.translatable("command.pasterdream.meltdreamenergy.no_need_consume.get.1").getString(),sp.getName().getString()))
                                                                                ,false);
                                                                    }
                                                                }
                                                            });
                                                            return 0;
                                                        })))
                                        .then(Commands.literal("get")
                                                .executes( arguments -> {
                                                    ServerPlayer sp = EntityArgument.getPlayer(arguments, "player");
                                                    sp.getCapability(MeltDreamEnergyCapability.Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).ifPresent(cap -> {
                                                        if (!cap.getNoNeedConsume()) {
                                                            arguments.getSource().sendSuccess(() -> Component.literal(String.format(Component.translatable("command.pasterdream.meltdreamenergy.no_need_consume.get.0").getString(), sp.getName().getString()))
                                                                    , false);
                                                        } else {
                                                            if (cap.getNoNeedConsumeValueByCommand()) {
                                                                arguments.getSource().sendSuccess(() -> Component.literal(String.format(Component.translatable("command.pasterdream.meltdreamenergy.no_need_consume.get.2").getString(), sp.getName().getString()))
                                                                        , false);
                                                            } else {
                                                                arguments.getSource().sendSuccess(() -> Component.literal(String.format(Component.translatable("command.pasterdream.meltdreamenergy.no_need_consume.get.1").getString(), sp.getName().getString()))
                                                                        , false);
                                                            }
                                                        }
                                                    });
                                                    return 0;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("meltdreamenergy")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.literal("set")
                                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                                .executes( arguments ->{
                                                                    ServerPlayer sp = EntityArgument.getPlayer(arguments, "player");
                                                                    double value = DoubleArgumentType.getDouble(arguments,"value");
                                                                    MeltDreamEnergyCapability.setPlayerMeltDreamEnergy(sp,value);
                                                                    sp.getCapability(MeltDreamEnergyCapability.Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).ifPresent(cap -> {
                                                                        arguments.getSource().sendSuccess(() -> Component.literal(String.format(Component.translatable("command.pasterdream.meltdreamenergy.meltdreamenergy.get").getString(),sp.getName().getString(), cap.getMeltDreamEnergy()))
                                                                                ,false);
                                                                    });
                                                                    return 0;
                                                                })))
                                                .then(Commands.literal("add")
                                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                                .executes( arguments ->{
                                                                    ServerPlayer sp = EntityArgument.getPlayer(arguments, "player");
                                                                    double value = DoubleArgumentType.getDouble(arguments,"value");
                                                                    MeltDreamEnergyCapability.addPlayerMeltDreamEnergy(sp,value);
                                                                    sp.getCapability(MeltDreamEnergyCapability.Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).ifPresent(cap -> {
                                                                        arguments.getSource().sendSuccess(() -> Component.literal(String.format(Component.translatable("command.pasterdream.meltdreamenergy.meltdreamenergy.get").getString(),sp.getName().getString(), cap.getMeltDreamEnergy()))
                                                                                ,false);
                                                                    });
                                                                    return 0;
                                                                })))
                                                .then(Commands.literal("get")
                                                        .executes( arguments -> {
                                                            ServerPlayer sp = EntityArgument.getPlayer(arguments, "player");
                                                            sp.getCapability(MeltDreamEnergyCapability.Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).ifPresent(cap -> {
                                                                arguments.getSource().sendSuccess(() -> Component.literal(String.format(Component.translatable("command.pasterdream.meltdreamenergy.meltdreamenergy.get").getString(),sp.getName().getString(), cap.getMeltDreamEnergy()))
                                                                        ,false);
                                                            });
                                                            return 0;
                                                        })
                                                )
                                        )
                        )
                )
        );
	}
}
