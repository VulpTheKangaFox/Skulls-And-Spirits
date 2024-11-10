package com.vulp.skullsandspirits.crafting;

import com.vulp.skullsandspirits.crafting.input.KegRecipeInput;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.RecipeMatcher;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.*;
import java.util.stream.Collectors;

public class KegRecipe implements Recipe<KegRecipeInput> {

    private final NonNullList<Ingredient> ingredients;
    private final FluidIngredient fluidIngredient;
    private final int fluidAmount;
    private final Ingredient vesselIngredient;
    private final int brewTime;
    private final ItemStack result;

    public KegRecipe(NonNullList<Ingredient> ingredients, FluidIngredient fluidIngredient, int fluidAmount, Ingredient vesselIngredient, ItemStack result, int brewTime) {
        this.ingredients = ingredients;
        this.fluidIngredient = fluidIngredient;
        this.fluidAmount = fluidAmount;
        this.vesselIngredient = vesselIngredient;
        this.result = result;
        this.brewTime = brewTime;
    }

    @Override
    public boolean matches(KegRecipeInput input, Level level) {
        if (input.getAllItems().stream().filter(itemStack -> !itemStack.isEmpty()).toList().size() != this.ingredients.size() || !this.fluidIngredient.test(input.getFluid()) || this.fluidAmount > input.getFluidAmount() || !this.vesselIngredient.test(input.getVesselItem())) {
            return false;
        } else {
            ArrayList<ItemStack> nonEmptyItems = new ArrayList<>(input.size());
            for (ItemStack item : input.getAllItems()) {
                if (!item.isEmpty()) {
                    nonEmptyItems.add(item);
                }
            }
            return RecipeMatcher.findMatches(nonEmptyItems, this.ingredients) != null;
        }
    }

    @Override
    public ItemStack assemble(KegRecipeInput kegRecipeInput, HolderLookup.Provider provider) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.KEG_BREWING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.KEG_BREWING.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public FluidIngredient getFluidIngredient() {
        return this.fluidIngredient;
    }

    public int getFluidAmount() {
        return this.fluidAmount;
    }

    public Ingredient getVesselIngredient() {
        return this.vesselIngredient;
    }

    public int getBrewTime() {
        return this.brewTime;
    }

    public ItemStack getResult() {
        return this.result;
    }

}
