package net.pasterdream.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.communication.ChannelEventTracker;
import net.pasterdream.communication.MeltDreamEnergyDataMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MeltDreamEnergyCapability {
    double MeltDreamEnergy;
    int NoNeedConsume;
    public static final String NBT_KEY_MELT_DREAM_ENERGY_VALUE = "pasterdreammeltdreamenergyvalue";
    public static final String NBT_KEY_NO_NEED_CONSUME_VALUE = "pasterdreammeltdreamenergynoneedconsume";
    public static final int CommandNoNeedConsume = 100000000;//指令用的
    public MeltDreamEnergyCapability()
    {
        MeltDreamEnergy = 0;
        NoNeedConsume = 0;
    }
    public MeltDreamEnergyCapability(MeltDreamEnergyCapability meltDreamEnergyCapability)
    {
        MeltDreamEnergy = meltDreamEnergyCapability.MeltDreamEnergy;
        NoNeedConsume = meltDreamEnergyCapability.NoNeedConsume;
    }
    public void saveNBTData(CompoundTag tag) {
        tag.putDouble(NBT_KEY_MELT_DREAM_ENERGY_VALUE, MeltDreamEnergy);
        tag.putInt(NBT_KEY_NO_NEED_CONSUME_VALUE,NoNeedConsume);
    }
    public void loadNBTData(CompoundTag tag) {
        MeltDreamEnergy = tag.getDouble(NBT_KEY_MELT_DREAM_ENERGY_VALUE);
        NoNeedConsume = tag.getInt(NBT_KEY_NO_NEED_CONSUME_VALUE);
    }
    public double getMeltDreamEnergy()
    {
        return MeltDreamEnergy;
    }
    public void setMeltDreamEnergy(double value) {
        MeltDreamEnergy = Math.max(0,Math.min(value, 100));
    }
    public void addMeltDreamEnergy(double value) {
        MeltDreamEnergy = Math.max(0,Math.min(MeltDreamEnergy + value, 100));
    }
    public boolean getNoNeedConsume()//为了方便多个相同道具进行维护进行的处理
    {
        return NoNeedConsume > 0;
    }
    public void setNoNeedConsume(boolean noNeedConsume)
    {
        NoNeedConsume = Math.max(0,NoNeedConsume + (noNeedConsume?1:-1));
    }
    public int getNoNeedConsumeValue()
    {
        return NoNeedConsume;
    }
    public void setNoNeedConsumeValue(int noNeedConsume)
    {
        NoNeedConsume = Math.max(0,noNeedConsume);
    }
    public boolean getNoNeedConsumeValueByCommand()
    {
        return NoNeedConsume >= CommandNoNeedConsume;
    }
    public void setNoNeedConsumeValueByCommand(boolean flag)
    {
        if(NoNeedConsume >= CommandNoNeedConsume && !flag)
            NoNeedConsume = NoNeedConsume - CommandNoNeedConsume;
        else if (NoNeedConsume < CommandNoNeedConsume && flag)
            NoNeedConsume = NoNeedConsume + CommandNoNeedConsume;
    }
    public void setMeltDreamEnergyCapability(MeltDreamEnergyCapability cap) {
        MeltDreamEnergy = cap.MeltDreamEnergy;
        NoNeedConsume = cap.NoNeedConsume;
    }
    public static void init()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, RegisterCapabilitiesEvent.class, MeltDreamEnergyCapability::register);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, MeltDreamEnergyCapability::attachCapability);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.Clone.class, MeltDreamEnergyCapability::playerClone);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerRespawnEvent.class, MeltDreamEnergyCapability::playerRespawn);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerChangedDimensionEvent.class, MeltDreamEnergyCapability::playerChangeDimension);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerLoggedInEvent.class, MeltDreamEnergyCapability::playerLoggedIn);
    }
    private static void register(RegisterCapabilitiesEvent event) {
        event.register(Provider.class);
    }
    public static void setPlayerMeltDreamEnergy(Player player,double value)
    {
        if(player instanceof ServerPlayer sp)
        {
            sp.getCapability(Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).ifPresent(cap ->{
                cap.setMeltDreamEnergy(value);
                sync(sp);
            });
        }
    }
    public static void setPlayerMeltDreamEnergyNoNeedConsume(Player player,boolean value)
    {
        if(player instanceof ServerPlayer sp)
        {
            sp.getCapability(Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).ifPresent(cap ->{
                cap.setNoNeedConsume(value);
                sync(sp);
            });
        }
    }
    public static void setPlayerMeltDreamEnergyNoNeedConsumeByCommand(Player player,boolean value)
    {
        if(player instanceof ServerPlayer sp)
        {
            sp.getCapability(Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).ifPresent(cap ->{
                cap.setNoNeedConsumeValueByCommand(value);
                sync(sp);
            });
        }
    }
    public static void addPlayerMeltDreamEnergy(Player player,double value)
    {
        if(player instanceof ServerPlayer sp)
        {
            sp.getCapability(Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).ifPresent(cap ->{
                cap.addMeltDreamEnergy(value);
                sync(sp);
            });
        }
    }
    //返回值为true时，则消耗成功
    public static boolean consumePlayerMeltDreamEnergy(Player player,double value)
    {
        if(player instanceof ServerPlayer sp)
        {
            return sp.getCapability(Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).map(cap ->{
                if(cap.getNoNeedConsume() || sp.isCreative())
                    return true;
                if(cap.MeltDreamEnergy > value)
                {
                    cap.addMeltDreamEnergy(-value);
                    sync(sp);
                    return true;
                }
                return false;
            }).orElse(false);
        }
        return player.getCapability(Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).map(cap ->{
            if(cap.getNoNeedConsume() || player.isCreative())
                return true;
            return cap.MeltDreamEnergy > value;
        }).orElse(false);//如果是客户端，只检查
    }
    public static void playerClone(PlayerEvent.Clone event) {
        if(event.getEntity() instanceof ServerPlayer sp)
        {
            Player original = event.getOriginal();
            original.reviveCaps();
            original.getCapability(Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).ifPresent((oldData) -> {
                event.getEntity().getCapability(Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).ifPresent(
                        (newData) -> newData.setMeltDreamEnergyCapability(oldData));
            });
            original.invalidateCaps();
            sync(event.getEntity());
        }
    }
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof Player player)
        {
            if(!player.getCapability(Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).isPresent())
            {
                event.addCapability(new ResourceLocation(PasterdreamMod.MODID, Provider.NBT_KEY_MELTDREAMENERGY),new Provider());
            }
        }
    }
    public static void sync(Player player) {
        if(player instanceof ServerPlayer sp)
        {
            player.getCapability(Provider.PLAYER_MELTDREAMENERGY_CAPABILITY).ifPresent(
                    (data) -> {
                        ChannelEventTracker.sendToPlayer(new MeltDreamEnergyDataMessage (data),sp);
                    });
        }
    }
    private static void playerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        sync(event.getEntity());
    }

    private static void playerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        sync(event.getEntity());
    }

    private static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        sync(event.getEntity());
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag>
    {
        public static final String NBT_KEY_MELTDREAMENERGY ="pasterdreammeltdreamenergy";
        private MeltDreamEnergyCapability meltdreamenergy;
        public static final Capability<MeltDreamEnergyCapability> PLAYER_MELTDREAMENERGY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
        private final LazyOptional<MeltDreamEnergyCapability> playerMeltDreamEnergyLazyOptional = LazyOptional.of(() -> meltdreamenergy);
        public Provider() {
            this.meltdreamenergy = new MeltDreamEnergyCapability();
        }
        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
            return PLAYER_MELTDREAMENERGY_CAPABILITY.orEmpty(cap, playerMeltDreamEnergyLazyOptional);
        }
        @Override
        public CompoundTag serializeNBT() {
            var tag = new CompoundTag();
            meltdreamenergy.saveNBTData(tag);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag compoundTag) {
            meltdreamenergy.loadNBTData(compoundTag);
        }
    }
}
