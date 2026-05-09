package net.pasterdream.block.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;
import net.pasterdream.PasterdreamMod;
import net.pasterdream.block.ShadowBlastFurnaceBlock;
import net.pasterdream.init.PasterdreamModItems;
import net.pasterdream.init.PasterdreamModRecipeType;
import net.pasterdream.recipes.dark_smithing.ShadowBlastFurnaceRecipe;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.GeoBlockEntity;

import net.pasterdream.world.inventory.ShadowBlastFurnaceGuiMenu;
import net.pasterdream.init.PasterdreamModFluids;
import net.pasterdream.init.PasterdreamModBlockEntities;

import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.Capability;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

import java.util.Optional;
import java.util.stream.IntStream;

import io.netty.buffer.Unpooled;

public class ShadowBlastFurnaceTileEntity extends BaseContainerBlockEntity implements GeoBlockEntity, WorldlyContainer {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private NonNullList<ItemStack> stacks = NonNullList.withSize(6, ItemStack.EMPTY);
    private final FluidTank fluidTank = new FluidTank(9000, fs -> fs.getFluid().isSame(PasterdreamModFluids.SHADOW_LIQUID.get())) {
        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();
            inventoryChanged();
        }
    };
    /*
    i = 0 输入
    i = 1 噩梦燃料位
    i = 2 产物
    i = 3 副产物
    i = 4 放阴影燃料桶 --todo 用tag判断
    i = 5 阴影燃料桶输出
    */
	private final LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.values());
    private ResourceLocation workRecipeID;
    private int blastingTime;
	public ShadowBlastFurnaceTileEntity(BlockPos pos, BlockState state) {
		super(PasterdreamModBlockEntities.SHADOW_BLAST_FURNACE.get(), pos, state);
	}
    @Nullable
    public ResourceLocation getWorkRecipeID() {
        return workRecipeID;
    }
    public int getBlastingTime(){
        return blastingTime;
    }
    public int getNeedBlastingTime(){
        if(workRecipeID == null) return 0;
        Optional<? extends Recipe<?>> recipe = level
                .getRecipeManager()
                .byKey(workRecipeID);
        return recipe.map(cap->{
            if(cap instanceof ShadowBlastFurnaceRecipe SBFRecipe){
                return SBFRecipe.getBlastingTick();
            }
            return 0;
        }).orElse(0);
    }
    private PlayState predicate(AnimationState event) {
		String animationprocedure = ("" + ((this.getBlockState()).getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty _getip1 ? (this.getBlockState()).getValue(_getip1) : 0));
		if (animationprocedure.equals("0")) {
			return event.setAndContinue(RawAnimation.begin().thenLoop(animationprocedure));
		}
		return PlayState.STOP;
	}

	private PlayState procedurePredicate(AnimationState event) {
		String animationprocedure = ("" + ((this.getBlockState()).getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty _getip1 ? (this.getBlockState()).getValue(_getip1) : 0));
		if (!animationprocedure.equals("0")
                && event.getController().getAnimationState() == AnimationController.State.STOPPED) {
			event.getController().setAnimation(RawAnimation.begin().thenPlay(animationprocedure));
			if (event.getController().getAnimationState() == AnimationController.State.STOPPED) {
				if (this.getBlockState().getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty _integerProp)
					level.setBlock(this.getBlockPos(), this.getBlockState().setValue(_integerProp, 0), 3);
				event.getController().forceAnimationReset();
			}
		} else if (animationprocedure.equals("0")) {
			return PlayState.STOP;
		}
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>(this, "controller", 0, this::predicate));
		data.add(new AnimationController<>(this, "procedurecontroller", 0, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		ContainerHelper.loadAllItems(compound, this.stacks);
		if (compound.get("fluidTank") instanceof CompoundTag compoundTag)
			fluidTank.readFromNBT(compoundTag);
        String RecipeId = compound.getString("workingRecipe");
        if(RecipeId.isEmpty())
            this.workRecipeID = null;
        else
            this.workRecipeID = new ResourceLocation(RecipeId);
        this.blastingTime = compound.getInt("blastingTime");
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
        ContainerHelper.saveAllItems(compound, this.stacks);
		compound.put("fluidTank", fluidTank.writeToNBT(new CompoundTag()));
        if(this.workRecipeID != null)
            compound.putString("workingRecipe",this.workRecipeID.toString());
        else
            compound.putString("workingRecipe","");
        compound.putInt("blastingTime",this.blastingTime);
	}

    public static void animationTick(Level level, BlockPos pos, BlockState state, ShadowBlastFurnaceTileEntity shadowblastfurnace) {
        if (state.getValue(ShadowBlastFurnaceBlock.WORKING)) {
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();
            addParticles(ParticleTypes.LARGE_SMOKE,level,x + 0.5,y + 2,z + 0.5, 7 ,0,3,0,0.15);
            addParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,level,x + 0.5,y + 2,z + 0.5, 4 ,0.05,3,0.05,0.15);
            level.playLocalSound(x, y, z, ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.blastfurnace.fire_crackle")), SoundSource.BLOCKS, 2, 1, false);
        }
    }
    private static <T extends ParticleOptions> void addParticles(T particleType,Level level, double x, double y, double z, int count, double XDist, double YDist, double ZDist, double maxspeed) {
        RandomSource random = level.random;
        double fx = random.nextGaussian() * XDist;
        double fy = random.nextGaussian() * YDist;
        double fz = random.nextGaussian() * ZDist;
        double vx = random.nextGaussian() * maxspeed;
        double vy = random.nextGaussian() * maxspeed;
        double vz = random.nextGaussian() * maxspeed;
        level.addParticle(particleType, x + fx, y + fy, z + fz, vx, vy, vz);
    }
    public static void blastingTick(Level level, BlockPos pos, BlockState state, ShadowBlastFurnaceTileEntity shadowblastfurnace) {
        boolean updateInventory = false;
        //输入液体燃料
        if(shadowblastfurnace.fluidTank.getSpace() > 0) {
            ItemStack input = shadowblastfurnace.getItem(4).copy().split(1);
            ItemStack output = shadowblastfurnace.getItem(5);
            FluidStack fluidStack = input
                    .getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
                    .map(cap->
                            cap.drain(new FluidStack(PasterdreamModFluids.SHADOW_LIQUID.get(),shadowblastfurnace.fluidTank.getSpace()),IFluidHandler.FluidAction.SIMULATE)).orElse(FluidStack.EMPTY);
            ItemStack result = input.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
                    .map(cap->{
                        cap.drain(fluidStack,IFluidHandler.FluidAction.EXECUTE);
                        return cap.getContainer();
                    }).orElse(input);
            //检查能否成功装入
            boolean flag1 = shadowblastfurnace.fluidTank.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE) > 0;
            //输出是不是empty
            boolean flag2 = result.isEmpty();
            //输出槽是不是没满且与当前输出一致
            boolean flag3 = ItemStack.isSameItemSameTags(result,output) && output.getCount() + result.getCount() <= output.getMaxStackSize();
            //输出槽是不是空的
            boolean flag4 = output.isEmpty();
            if(flag1 && (flag2 || flag3 || flag4)) {
                updateInventory = true;
                shadowblastfurnace.fluidTank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                shadowblastfurnace.getItem(4).shrink(1);
                if(!flag2) {
                    if(flag3) {
                        output.grow(result.getCount());
                    }
                    else {
                        shadowblastfurnace.stacks.set(5, result);
                    }
                }
            }
        }
        //生产
        if(shadowblastfurnace.workRecipeID != null)
        {
            Optional<? extends Recipe<?>> recipe = level
                    .getRecipeManager()
                    .byKey(shadowblastfurnace.workRecipeID);
            //原配方还存在且还能匹配的上
            if(recipe.map(cap-> cap instanceof ShadowBlastFurnaceRecipe).orElse(false)) {
                ShadowBlastFurnaceRecipe recipe1 = (ShadowBlastFurnaceRecipe) recipe.get();
                shadowblastfurnace.blastingTime = Math.min(recipe1.getBlastingTick(),shadowblastfurnace.blastingTime + 1);
                updateInventory = true;
                //完成了
                if(shadowblastfurnace.blastingTime == recipe1.getBlastingTick())
                {
                    //检查主产物和副产物，均被阻塞时卡住
                    ItemStack Result = recipe1.getResultItem(level.registryAccess()).copy();
                    ItemStack byResult = recipe1.getByResultItem(level.registryAccess()).copy();
                    ItemStack output = shadowblastfurnace.getItem(2);
                    ItemStack byoutput = shadowblastfurnace.getItem(3);
                    boolean flag1 = Result.isEmpty() || output.isEmpty() || (ItemStack.isSameItem(Result,output) && output.getCount() + Result.getCount() <= Result.getMaxStackSize());
                    boolean flag2 = byResult.isEmpty() || byoutput.isEmpty() || (ItemStack.isSameItem(byResult,byoutput) && byoutput.getCount() + byResult.getCount() <= byResult.getMaxStackSize());
                    if(flag1 && flag2)
                    {
                        shadowblastfurnace.workRecipeID = null;
                        if(!Result.isEmpty()) {
                            if(output.isEmpty()) {
                                shadowblastfurnace.setItem(2,Result.copy());
                            }
                            else{
                                output.grow(Result.getCount());
                            }
                        }
                        if(!byResult.isEmpty()){
                            if(level.random.nextDouble() <= recipe1.getByoutput_probability()){
                                if(byoutput.isEmpty()) {
                                    shadowblastfurnace.setItem(3,byResult.copy());
                                }
                                else{
                                    byoutput.grow(byResult.getCount());
                                }
                            }
                        }
                    }
                }
            } else{
                shadowblastfurnace.workRecipeID = null;
            }
        }
        if(shadowblastfurnace.workRecipeID == null)
        {
            //燃烧时间归零
            shadowblastfurnace.blastingTime = 0;
            //处理下一次配方更新，不支持空烧
            if(!shadowblastfurnace.getItem(0).isEmpty())
            {
                Optional<ShadowBlastFurnaceRecipe> recipe = shadowblastfurnace.findNewMatchingRecipe();
                if(recipe.isPresent())
                {
                    ShadowBlastFurnaceRecipe SBFRecipe = recipe.get();
                    ItemStack inputFuel = shadowblastfurnace.getItem(1);
                    boolean flag1 = inputFuel.is(PasterdreamModItems.NIGHTMARE_FUEL.get()) && inputFuel.getCount() >= SBFRecipe.getSpendFuel();
                    boolean flag2 = shadowblastfurnace.fluidTank.getFluidAmount() >= SBFRecipe.getSpendFluidFuel();
                    if(flag1 && flag2)
                    {
                        updateInventory = true;
                        shadowblastfurnace.removeItem(1,SBFRecipe.getSpendFuel());
                        shadowblastfurnace.removeItem(0,1);
                        shadowblastfurnace.fluidTank.drain(SBFRecipe.getSpendFluidFuel(), IFluidHandler.FluidAction.EXECUTE);
                        shadowblastfurnace.workRecipeID = SBFRecipe.getId();
                    }
                }
            }
        }
        //更新方块状态
        if((shadowblastfurnace.workRecipeID == null) == state.getValue(ShadowBlastFurnaceBlock.WORKING)){
            updateInventory = true;
            BlockState newState = state.setValue(ShadowBlastFurnaceBlock.WORKING,!(shadowblastfurnace.workRecipeID == null));
            level.setBlock(pos,newState,Block.UPDATE_ALL);
        }
        if(updateInventory)
        {
            shadowblastfurnace.inventoryChanged();
        }
    }
    private Optional<ShadowBlastFurnaceRecipe> findNewMatchingRecipe() {
        if (level == null) return Optional.empty();
        return level
                .getRecipeManager()
                .getRecipeFor(PasterdreamModRecipeType.SHADOW_BLAST_FURNACE.get(), this, level);
    }
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithFullMetadata();
	}

	@Override
	public int getContainerSize() {
		return stacks.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.stacks)
			if (!itemstack.isEmpty())
				return false;
		return true;
	}

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.stacks.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack stack = this.stacks.get(slot);
        ItemStack result = stack.split(amount);
        inventoryChanged();
        return result;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = this.stacks.get(slot);
        this.stacks.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack itemStack) {
        this.stacks.set(slot, itemStack);
        inventoryChanged();
    }

    @Override
	public @NotNull Component getDefaultName() {
		return Component.literal("shadow_blast_furnace");
	}

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return new ShadowBlastFurnaceGuiMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(this.worldPosition));
	}

	@Override
	public Component getDisplayName() {
		return Component.literal("Shadow Blast Furnace");
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		if(index == 2 || index == 3 || index == 5)return false;
        return true;
	}

	@Override
	public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        if(side == Direction.DOWN)return new int[]{2, 3, 5};
        else if(side == Direction.UP)return new int[]{0};
        else if(side == Direction.NORTH)return new int[]{4};
		return new int[]{1};
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction direction) {
		return this.canPlaceItem(index, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return true;
	}

    private void inventoryChanged()
    {
        super.setChanged();
        if(level != null)
        {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove && facing != null && capability == ForgeCapabilities.ITEM_HANDLER)
			return handlers[facing.ordinal()].cast();
		if (!this.remove && capability == ForgeCapabilities.FLUID_HANDLER)
			return LazyOptional.of(() -> fluidTank).cast();
		return super.getCapability(capability, facing);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		for (LazyOptional<? extends IItemHandler> handler : handlers)
			handler.invalidate();
	}

    @Override
    public void clearContent() {

    }
}
