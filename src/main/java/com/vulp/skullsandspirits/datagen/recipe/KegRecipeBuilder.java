package com.vulp.skullsandspirits.datagen.recipe;

import com.vulp.skullsandspirits.crafting.KegRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KegRecipeBuilder implements RecipeBuilder {

    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    @Nullable protected String group;

    private final NonNullList<Ingredient> itemIngredients;
    private final FluidIngredient fluidIngredient;
    private final int fluidAmount;
    private final Ingredient vesselIngredient;
    private final ItemStack result;
    private final int brewTime;

    public KegRecipeBuilder(NonNullList<Ingredient> itemIngredients, FluidIngredient fluidIngredient, int fluidAmount, Ingredient vesselIngredient, ItemStack result, int brewTime) {
        this.itemIngredients = itemIngredients;
        this.fluidIngredient = fluidIngredient;
        this.fluidAmount = fluidAmount;
        this.vesselIngredient = vesselIngredient;
        this.result = result;
        this.brewTime = brewTime;
    }

    @Override
    public RecipeBuilder unlockedBy(String s, Criterion<?> criterion) {
        this.criteria.put(s, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        /*Advancement.Builder advancement = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation))
                .rewards(AdvancementRewards.Builder.recipe(resourceLocation))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);*/
        KegRecipe recipe = new KegRecipe(this.itemIngredients, this.fluidIngredient, this.fluidAmount, this.vesselIngredient, this.result, this.brewTime);
        recipeOutput.accept(resourceLocation, recipe, null/*advancement.build(resourceLocation.withPrefix("recipes/"))*/);
    }

}
