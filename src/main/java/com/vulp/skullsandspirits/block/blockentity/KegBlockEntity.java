package com.vulp.skullsandspirits.block.blockentity;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.crafting.KegRecipe;
import com.vulp.skullsandspirits.crafting.RecipeRegistry;
import com.vulp.skullsandspirits.crafting.input.KegRecipeInput;
import com.vulp.skullsandspirits.inventory.KegMenu;
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
import net.minecraft.world.item.crafting.*;
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
import java.util.List;

public class KegBlockEntity extends BaseContainerBlockEntity {

    // Slot configuration to help me remember:
    // Ingredients - 0 to 5
    // Bucket - 6
    // Vessel - 7
    // Output - 8

    private NonNullList<ItemStack> itemSlots;
    private final FluidTank fluidTank;
    private final ContainerData dataAccess;
    private final RecipeType<? extends KegRecipe> recipeType;
    private final RecipeManager.CachedCheck<KegRecipeInput, ? extends KegRecipe> quickCheck;
    public int brewTime;
    public int maxBrewTime;

    public KegBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.SHODDY_KEG.get(), pos, blockState);
        this.itemSlots = NonNullList.withSize(9, ItemStack.EMPTY);
        this.fluidTank = new FluidTank(1000) {
            @Override
            protected void onContentsChanged() {
                setChanged();
            }
        };
        this.dataAccess = new ContainerData() {
            public int get(int dataID) {
                return switch (dataID) {
                    case 0 -> KegBlockEntity.this.brewTime;
                    case 1 -> KegBlockEntity.this.maxBrewTime;
                    case 2 -> BuiltInRegistries.FLUID.getId(fluidTank.getFluid().getFluid());
                    case 3 -> fluidTank.getFluidAmount();
                    default -> 0;
                };
            }

            public void set(int dataID, int value) {
                switch (dataID) {
                    case 0 -> KegBlockEntity.this.brewTime = value;
                    case 1 -> KegBlockEntity.this.maxBrewTime = value;
                    case 2 -> fluidTank.setFluid(new FluidStack(BuiltInRegistries.FLUID.byId(value), fluidTank.getFluidAmount()));
                    case 3 -> fluidTank.setFluid(new FluidStack(fluidTank.getFluid().getFluid(), fluidTank.getFluidAmount()));
                }

            }

            public int getCount() {
                return 4;
            }
        };
        this.recipeType = RecipeRegistry.KEG_BREWING.get();
        this.quickCheck = RecipeManager.createCheck(recipeType);
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (level.isClientSide() || !(blockEntity instanceof KegBlockEntity kegBlockEntity)) {
            return;
        }

        // Checks to make sure inputs exist first.
        if (kegBlockEntity.hasItemInputs()) {
            int maxStackSize = kegBlockEntity.getMaxStackSize();
            KegRecipeInput input = createRecipeInput(kegBlockEntity);
            RecipeHolder<? extends KegRecipe> recipeHolder = kegBlockEntity.quickCheck.getRecipeFor(input, level).orElse(null);
            RegistryAccess registryAccess = level.registryAccess();
            NonNullList<ItemStack> inv = kegBlockEntity.itemSlots;
            if (canBrew(registryAccess, recipeHolder, input, inv, maxStackSize, kegBlockEntity)) {
                if (kegBlockEntity.maxBrewTime == 0) {
                    kegBlockEntity.maxBrewTime = getBrewTime(level, kegBlockEntity);
                }
                kegBlockEntity.brewTime++;
                if (kegBlockEntity.brewTime == kegBlockEntity.maxBrewTime) {
                    kegBlockEntity.brewTime = 0;
                    kegBlockEntity.maxBrewTime = getBrewTime(level, kegBlockEntity);
                    // Create recipe outputs.
                    ItemStack recipeStack = recipeHolder.value().assemble(input, registryAccess);
                    ItemStack outputSlot = inv.get(8);
                    if (outputSlot.isEmpty()) {
                        inv.set(8, recipeStack.copy());
                    } else if (ItemStack.isSameItemSameComponents(recipeStack, outputSlot)) {
                        outputSlot.grow(recipeStack.getCount());
                    }

                    // Shrink ingredients and drain fluid.
                    for (int i = 0; i < 8; i++) {
                        if (i != 6) {
                            inv.get(i).shrink(1);
                        }
                    }
                    kegBlockEntity.fluidTank.drain(recipeHolder.value().getFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
                    setChanged(level, pos, state);
                }
            } else {
                kegBlockEntity.brewTime = 0;
                kegBlockEntity.maxBrewTime = 0;
            }

        }

        // Fluid input logic:
        ItemStack bucketSlotItem = kegBlockEntity.getItemSlots().get(6);
        if (bucketSlotItem.isEmpty()) {
            return;
        }
        IFluidHandlerItem fluidHandlerItem = bucketSlotItem.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerItem == null) {
            return;
        }
        int drainAmount = Math.min(kegBlockEntity.fluidTank.getSpace(), 1000);
        FluidStack simulatedDrain = fluidHandlerItem.drain(drainAmount, IFluidHandler.FluidAction.SIMULATE);
        if (!kegBlockEntity.fluidTank.isFluidValid(simulatedDrain) || simulatedDrain.isEmpty()) {
            return;
        }
        FluidStack drainedFluid = fluidHandlerItem.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
        kegBlockEntity.fluidTank.fill(drainedFluid, IFluidHandler.FluidAction.EXECUTE);
        kegBlockEntity.setItem(6, fluidHandlerItem.getContainer());
    }

    // Checks to determine if the nonnull inputs are even present.
    private boolean hasItemInputs() {
        return !this.itemSlots.get(7).isEmpty() && this.itemSlots.subList(0, 6).stream().anyMatch(stack -> !stack.isEmpty());
    }

    // Lets us quickly check if the block is brewing or not.
    private boolean isBrewing() {
        return this.brewTime > 0;
    }

    // Determines if the keg can craft an item.
    private static boolean canBrew(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipe, KegRecipeInput recipeInput, NonNullList<ItemStack> inventory, int maxStackSize, KegBlockEntity blockEntity) {
        if (recipe != null) {
            ItemStack itemstack = ((RecipeHolder<? extends KegRecipe>) recipe).value().assemble(recipeInput, registryAccess);
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack outputStack = inventory.get(8);
                if (outputStack.isEmpty()) {
                    return true;
                } else if (!ItemStack.isSameItemSameComponents(outputStack, itemstack)) {
                    return false;
                } else {
                    return outputStack.getCount() + itemstack.getCount() <= maxStackSize && outputStack.getCount() + itemstack.getCount() <= outputStack.getMaxStackSize() || outputStack.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // We're respecting the Neo fix that a furnace would have used.
                }
            }
        } else {
            return false;
        }
    }

    private static int getBrewTime(Level level, KegBlockEntity blockEntity) {
        KegRecipeInput recipeInput = createRecipeInput(blockEntity);
        return blockEntity.quickCheck.getRecipeFor(recipeInput, level).map(recipeHolder -> recipeHolder.value().getBrewTime()).orElse(0);
    }

    private static KegRecipeInput createRecipeInput(KegBlockEntity blockEntity) {
        List<ItemStack> items = blockEntity.itemSlots.subList(0, 6);
        ItemStack vesselItem = blockEntity.itemSlots.get(7);
        return new KegRecipeInput(items, blockEntity.fluidTank.getFluid(), blockEntity.fluidTank.getFluidAmount(), vesselItem);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container." + SkullsAndSpirits.MODID + ".shoddy_keg");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.itemSlots;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemList) {
        this.itemSlots = itemList;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        ItemStack itemstack = this.itemSlots.get(index);
        boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameComponents(itemstack, stack);
        this.itemSlots.set(index, stack);
        stack.limitSize(this.getMaxStackSize(stack));
        if ((index < 6 || index == 7) && !flag) {
            this.maxBrewTime = getBrewTime(this.level, this);
            this.brewTime = 0;
            this.setChanged();
        }
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory player) {
        return new KegMenu(i, player, this, this.dataAccess);
    }

    @Override
    public int getContainerSize() {
        return this.itemSlots.size();
    }

    public FluidTank getFluidTank() {
        return this.fluidTank;
    }

    public NonNullList<ItemStack> getItemSlots() {
        return itemSlots;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.brewTime = tag.getInt("BrewTime");
        this.maxBrewTime = tag.getInt("MaxBrewTime");
        ContainerHelper.loadAllItems(tag, this.itemSlots, registries);
        getFluidTank().readFromNBT(registries, tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("BrewTime", this.brewTime);
        tag.putInt("MaxBrewTime", this.maxBrewTime);
        ContainerHelper.saveAllItems(tag, this.itemSlots, registries);
        getFluidTank().writeToNBT(registries, tag);
    }

}
