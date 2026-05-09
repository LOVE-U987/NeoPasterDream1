package net.pasterdream.capability;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.pasterdream.communication.ChannelEventTracker;
import net.pasterdream.communication.SanDataMessage;
import net.pasterdream.init.PasterdreamModGameRules;
import net.pasterdream.init.PasterdreamModMobEffects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.pasterdream.PasterdreamMod;


public class SanCapability {
    double SanValue;
    boolean SanCheck;
    public static final String NBT_KEY_SAN_VALUE = "pasterdreamsanvalue";
    public static final String NBT_KEY_SAN_CHECK = "pasterdreamsancheck";

    public SanCapability()
    {
        SanValue = 100;
        SanCheck = true;
    }
    public SanCapability(double sanValue, boolean sanCheck)
    {
        SanValue = sanValue;
        SanCheck = sanCheck;
    }
    public void saveNBTData(CompoundTag tag) {
        tag.putDouble(NBT_KEY_SAN_VALUE, SanValue);
        tag.putBoolean(NBT_KEY_SAN_CHECK, SanCheck);
    }
    public void loadNBTData(CompoundTag tag) {
        SanValue = tag.getDouble(NBT_KEY_SAN_VALUE);
        SanCheck = tag.getBoolean(NBT_KEY_SAN_CHECK);
    }
    public double getSanValue()
    {
        return SanValue;
    }
    public boolean getSanCheck()
    {
        return SanCheck;
    }
    public void setSanValue(double sanValue) {
        SanValue = Math.max(0,Math.min(sanValue, 100));
    }
    public void addSanValue(double sanValue) {
        SanValue = Math.max(0,Math.min(SanValue + sanValue, 100));
    }
    public void setSanCheck(boolean sanCheck) {
        SanCheck = sanCheck;
    }
    public void setSan(SanCapability san) {
        SanValue = san.SanValue;
        SanCheck = san.SanCheck;
    }
    public static void init()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(EventPriority.NORMAL, false, RegisterCapabilitiesEvent.class, SanCapability::register);
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, SanCapability::attachCapability);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.Clone.class, SanCapability::playerClone);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerRespawnEvent.class, SanCapability::playerRespawn);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerChangedDimensionEvent.class, SanCapability::playerChangeDimension);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerEvent.PlayerLoggedInEvent.class, SanCapability::playerLoggedIn);
    }
    private static void register(RegisterCapabilitiesEvent event) {
        event.register(Provider.class);
    }
    public static boolean checkSanEffect(MobEffect effect)
    {
        return effect == PasterdreamModMobEffects.SAN_INCREASE.get() || effect == PasterdreamModMobEffects.SAN_DECREASE.get();
    }
    //--todo 双端分离
    public static boolean IsSanCheckSystem()//判断是否应该启用SanCheck，双端可用
    {
        boolean flag = false;
        if (EffectiveSide.get().isClient()) {
            Minecraft client = Minecraft.getInstance();
            if(client.player != null)
            {
                flag = client.player.getCapability(SanCapability.Provider.PLAYER_SAN_CAPABILITY).map(SanCapability::getSanCheck).orElse(false);
            }
        } else {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            flag = server.getGameRules().getBoolean(PasterdreamModGameRules.SANCHECKSYSTEM);
        }
        return flag;
    }
    public static void setPlayerSanWithCheck(Player player,double san)
    {
        if(player instanceof ServerPlayer sp && sp.level().getGameRules().getBoolean(PasterdreamModGameRules.SANCHECKSYSTEM))
        {
            setPlayerSan(player,san);
        }
    }
    public static void addPlayerSanWithCheck(Player player,double san)
    {
        if(player instanceof ServerPlayer sp && sp.level().getGameRules().getBoolean(PasterdreamModGameRules.SANCHECKSYSTEM))
        {
            addPlayerSan(player,san);
        }
    }
    private static void setPlayerSan(Player player,double san)
    {
        if(player instanceof ServerPlayer sp)
        {
            sp.getCapability(Provider.PLAYER_SAN_CAPABILITY).ifPresent( cap ->{
                cap.setSanValue(san);
                syncOnlyValue(sp);
            });
        }
    }
    private static void addPlayerSan(Player player,double san)
    {
        if(player instanceof ServerPlayer sp)
        {
            sp.getCapability(Provider.PLAYER_SAN_CAPABILITY).ifPresent( cap ->{
                cap.addSanValue(san);
                syncOnlyValue(sp);
            });
        }
    }
    public static void playerClone(PlayerEvent.Clone event) {
        if(event.getEntity() instanceof ServerPlayer sp)
        {
            if(event.isWasDeath())
            {
                sp.getCapability(Provider.PLAYER_SAN_CAPABILITY).ifPresent( cap ->{
                    cap.setSanValue(sp.level().getGameRules().getInt(PasterdreamModGameRules.STARTSANONREVIVE));
                    cap.setSanCheck(sp.level().getGameRules().getBoolean(PasterdreamModGameRules.SANCHECKSYSTEM));
                });
            }
            else
            {
                Player original = event.getOriginal();
                original.reviveCaps();
                original.getCapability(Provider.PLAYER_SAN_CAPABILITY).ifPresent((oldData) -> {
                    event.getEntity().getCapability(Provider.PLAYER_SAN_CAPABILITY).ifPresent(
                            (newData) -> newData.setSan(oldData));
                });
                original.invalidateCaps();
            }
            sync(event.getEntity());
        }
    }
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof Player player)
        {
            if(!player.getCapability(Provider.PLAYER_SAN_CAPABILITY).isPresent())
            {
                event.addCapability(new ResourceLocation(PasterdreamMod.MODID, Provider.NBT_KEY_SAN),new Provider());
            }
        }
    }
    public static void syncOnlyValue(Player player) {
        if(player instanceof ServerPlayer sp)
        {
            player.getCapability(Provider.PLAYER_SAN_CAPABILITY).ifPresent(
                    (data) -> {
                        ChannelEventTracker.sendToPlayer(new SanDataMessage(data.SanValue),sp);
                    });
        }
    }
    public static void syncOnlyCheck(Player player) {
        if(player instanceof ServerPlayer sp)
        {
            player.getCapability(Provider.PLAYER_SAN_CAPABILITY).ifPresent(
                    (data) -> {
                        ChannelEventTracker.sendToPlayer(new SanDataMessage(sp.level().getGameRules().getBoolean(PasterdreamModGameRules.SANCHECKSYSTEM)),sp);
                    });
        }
    }
    public static void sync(Player player) {
        if(player instanceof ServerPlayer sp)
        {
            player.getCapability(Provider.PLAYER_SAN_CAPABILITY).ifPresent(
                    (data) -> {
                        data.setSanCheck(sp.level().getGameRules().getBoolean(PasterdreamModGameRules.SANCHECKSYSTEM));
                        ChannelEventTracker.sendToPlayer(new SanDataMessage(data),sp);
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
        public static final String NBT_KEY_SAN ="pasterdreamsan";
        private SanCapability san;
        public static final Capability<SanCapability> PLAYER_SAN_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
        private final LazyOptional<SanCapability> playerSanLazyOptional = LazyOptional.of(() -> san);
        public Provider() {
            this.san = new SanCapability();
        }
        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction direction) {
            return PLAYER_SAN_CAPABILITY.orEmpty(cap, playerSanLazyOptional);
        }
        @Override
        public CompoundTag serializeNBT() {
            var tag = new CompoundTag();
            san.saveNBTData(tag);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag compoundTag) {
            san.loadNBTData(compoundTag);
        }
    }
}
