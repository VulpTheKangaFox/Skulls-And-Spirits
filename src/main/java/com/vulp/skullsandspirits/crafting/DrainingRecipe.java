package com.vulp.skullsandspirits.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

public class DrainingRecipe implements Recipe<SingleRecipeInput> {

    private final Ingredient ingredient;
    private final ItemStack itemResult;
    private final FluidStack fluidResult;
    private final int drainTime;

    public DrainingRecipe(Ingredient ingredient, ItemStack itemResult, FluidStack fluidResult, int drainTime) {
        this.ingredient = ingredient;
        this.itemResult = itemResult;
        this.fluidResult = fluidResult;
        this.drainTime = drainTime;
    }

    public Ingredient getIngredient() {
        return this.ingredient;
    }

    public ItemStack getItemResult() {
        return this.itemResult;
    }

    public FluidStack getFluidResult() {
        return this.fluidResult;
    }

    public int getDrainTime() {
        return this.drainTime;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.getIngredient().test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return this.itemResult.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.itemResult;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.DRAINING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.DRAINING.get();
    }

}
