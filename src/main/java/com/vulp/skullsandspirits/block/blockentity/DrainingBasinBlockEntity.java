package com.vulp.skullsandspirits.block.blockentity;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.crafting.DrainingRecipe;
import com.vulp.skullsandspirits.crafting.RecipeRegistry;
import com.vulp.skullsandspirits.inventory.DrainingBasinMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DrainingBasinBlockEntity extends BaseContainerBlockEntity {

    // Slot configuration to help me remember:
    // Ingredient - 0
    // Output - 1
    // Bucket - 2

    private NonNullList<ItemStack> itemSlots;
    private final FluidTank fluidTank;
    private final ContainerData dataAccess;
    private final RecipeType<? extends DrainingRecipe> recipeType;
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends DrainingRecipe> quickCheck;
    public int drainTime;
    public int maxDrainTime;

    public DrainingBasinBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.DRAINING_BASIN.get(), pos, blockState);
        this.itemSlots = NonNullList.withSize(9, ItemStack.EMPTY);

        this.fluidTank = new FluidTank(2000) {
            @Override
            protected void onContentsChanged() {
                setChanged();
            }
        };

        this.dataAccess = new ContainerData() {
            public int get(int dataID) {
                return switch (dataID) {
                    case 0 -> DrainingBasinBlockEntity.this.drainTime;
                    case 1 -> DrainingBasinBlockEntity.this.maxDrainTime;
                    case 2 -> BuiltInRegistries.FLUID.getId(fluidTank.getFluid().getFluid());
                    case 3 -> fluidTank.getFluidAmount();
                    default -> 0;
                };
            }
            public void set(int dataID, int value) {
                switch (dataID) {
                    case 0 -> DrainingBasinBlockEntity.this.drainTime = value;
                    case 1 -> DrainingBasinBlockEntity.this.maxDrainTime = value;
                    case 2 -> fluidTank.setFluid(new FluidStack(BuiltInRegistries.FLUID.byId(value), fluidTank.getFluidAmount()));
                    case 3 -> fluidTank.setFluid(new FluidStack(fluidTank.getFluid().getFluid(), fluidTank.getFluidAmount()));
                }

            }
            public int getCount() {
                return 4;
            }
        };

        this.recipeType = RecipeRegistry.DRAINING.get();
        this.quickCheck = RecipeManager.createCheck(recipeType);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (level.isClientSide() || !(blockEntity instanceof DrainingBasinBlockEntity basinBlockEntity)) {
            return;
        }
        if (basinBlockEntity.hasItemInputs()) {
            int maxStackSize = basinBlockEntity.getMaxStackSize();
            SingleRecipeInput input = createRecipeInput(basinBlockEntity);
            RecipeHolder<? extends DrainingRecipe> recipeHolder = basinBlockEntity.quickCheck.getRecipeFor(input, level).orElse(null);
            RegistryAccess registryAccess = level.registryAccess();
            NonNullList<ItemStack> inv = basinBlockEntity.itemSlots;
            if (canDrain(registryAccess, recipeHolder, input, inv, maxStackSize, basinBlockEntity)) {
                if (basinBlockEntity.maxDrainTime == 0) {
                    basinBlockEntity.maxDrainTime = getDrainTime(level, basinBlockEntity);
                }
                basinBlockEntity.drainTime++;
                if (basinBlockEntity.drainTime == basinBlockEntity.maxDrainTime) {
                    basinBlockEntity.drainTime = 0;
                    basinBlockEntity.maxDrainTime = getDrainTime(level, basinBlockEntity);
                    ItemStack recipeStack = recipeHolder.value().assemble(input, registryAccess);
                    ItemStack outputSlot = inv.get(1);
                    FluidStack recipeFluidStack = recipeHolder.value().getFluidResult();
                    FluidTank tank = basinBlockEntity.fluidTank;
                    if (outputSlot.isEmpty()) {
                        inv.set(1, recipeStack.copy());
                    } else if (ItemStack.isSameItemSameComponents(recipeStack, outputSlot)) {
                        outputSlot.grow(recipeStack.getCount());
                    }
                    if (tank.isEmpty()) {
                        tank.setFluid(recipeFluidStack.copy());
                    } else if (FluidStack.isSameFluidSameComponents(recipeFluidStack, tank.getFluid())) {
                        tank.fill(recipeFluidStack.copy(), IFluidHandler.FluidAction.EXECUTE);
                    }
                    inv.get(0).shrink(1);
                    setChanged(level, pos, state);
                }
            } else {
                basinBlockEntity.drainTime = 0;
                basinBlockEntity.maxDrainTime = 0;
            }
        }

        // Fluid output logic:
        ItemStack bucketSlotItem = basinBlockEntity.getItem(2);
        if (bucketSlotItem.isEmpty()) {
            return;
        }

        IFluidHandlerItem fluidHandlerItem = bucketSlotItem.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerItem == null) {
            return;
        }

        FluidStack availableFluid = basinBlockEntity.fluidTank.getFluid();
        if (availableFluid.isEmpty()) {
            return;
        }

        int fillAmount = Math.min(availableFluid.getAmount(), 1000);
        FluidStack simulatedFill = basinBlockEntity.fluidTank.drain(fillAmount, IFluidHandler.FluidAction.SIMULATE);
        int acceptedAmount = fluidHandlerItem.fill(simulatedFill, IFluidHandler.FluidAction.SIMULATE);

        if (acceptedAmount <= 0) {
            return;
        }

        FluidStack drainedFluid = basinBlockEntity.fluidTank.drain(acceptedAmount, IFluidHandler.FluidAction.EXECUTE);
        fluidHandlerItem.fill(drainedFluid, IFluidHandler.FluidAction.EXECUTE);
        basinBlockEntity.setItem(2, fluidHandlerItem.getContainer());
    }

    private static boolean canDrain(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipe, SingleRecipeInput recipeInput, NonNullList<ItemStack> inventory, int maxStackSize, DrainingBasinBlockEntity blockEntity) {
        if (recipe == null) {
            return false;
        }
        DrainingRecipe drainingRecipe = ((RecipeHolder<? extends DrainingRecipe>) recipe).value();
        ItemStack itemStack = drainingRecipe.assemble(recipeInput, registryAccess);
        FluidStack fluidStack = drainingRecipe.getFluidResult();

        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack outputStack = inventory.get(1);
        FluidTank outputTank = blockEntity.fluidTank;
        boolean stackPass = outputStack.isEmpty() ||
                (ItemStack.isSameItemSameComponents(outputStack, itemStack) &&
                        outputStack.getCount() + itemStack.getCount() <= Math.min(maxStackSize, outputStack.getMaxStackSize()));
        boolean tankPass = outputTank.isEmpty() ||
                (FluidStack.isSameFluidSameComponents(outputTank.getFluid(), fluidStack) &&
                        outputTank.getSpace() >= fluidStack.getAmount());

        return stackPass && tankPass;
    }

    private static int getDrainTime(Level level, DrainingBasinBlockEntity blockEntity) {
        SingleRecipeInput recipeInput = createRecipeInput(blockEntity);
        return blockEntity.quickCheck.getRecipeFor(recipeInput, level).map(recipeHolder -> recipeHolder.value().getDrainTime()).orElse(0);
    }

    private boolean hasItemInputs() {
        return !this.itemSlots.get(0).isEmpty();
    }

    private static SingleRecipeInput createRecipeInput(DrainingBasinBlockEntity blockEntity) {
        return new SingleRecipeInput(blockEntity.itemSlots.get(0));
    }


    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container." + SkullsAndSpirits.MODID + ".draining_basin");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.itemSlots;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        ItemStack itemstack = this.itemSlots.get(index);
        boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, stack);
        this.itemSlots.set(index, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        if (index == 0 && !flag) {
            this.maxDrainTime = getDrainTime(this.level, this);
            this.drainTime = 0;
            this.setChanged();
        }
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemList) {
        this.itemSlots = itemList;
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory player) {
        return new DrainingBasinMenu(i, player, this, this.dataAccess);
    }

    @Override
    public int getContainerSize() {
        return this.itemSlots.size();
    }

    public FluidTank getFluidTank() {
        return this.fluidTank;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.drainTime = tag.getInt("DrainTime");
        this.maxDrainTime = tag.getInt("MaxBrewTime");
        ContainerHelper.loadAllItems(tag, this.itemSlots, registries);
        this.fluidTank.readFromNBT(registries, tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("DrainTime", this.drainTime);
        tag.putInt("MaxDrainTime", this.maxDrainTime);
        ContainerHelper.saveAllItems(tag, this.itemSlots, registries);
        this.fluidTank.writeToNBT(registries, tag);
    }

}
