package com.vulp.skullsandspirits.crafting.input;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.HashSet;
import java.util.List;

public class KegRecipeInput implements RecipeInput {

    private final List<ItemStack> items;
    private final FluidStack fluids;
    private final int fluidAmount;
    private final ItemStack vesselItem;

    public KegRecipeInput(List<ItemStack> items, FluidStack fluids, int fluidAmount, ItemStack vesselItem) {
        this.items = items;
        this.fluids = fluids;
        this.fluidAmount = fluidAmount;
        this.vesselItem = vesselItem;
    }


    @Override
    public ItemStack getItem(int i) {
        return this.items.get(i);
    }

    public FluidStack getFluid() { // I don't know if this will work.
        return this.fluids;
    }

    public int getFluidAmount() {
        return this.fluidAmount;
    }

    public ItemStack getVesselItem() {
        return this.vesselItem;
    }

    @Override
    public int size() {
        return this.items.size();
    }

    public List<ItemStack> getAllItems() {
        return this.items;
    }

}
