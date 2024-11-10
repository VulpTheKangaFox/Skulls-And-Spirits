package com.vulp.skullsandspirits.datagen.recipe;

import com.vulp.skullsandspirits.crafting.DrainingRecipe;
import net.minecraft.advancements.Criterion;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class DrainingRecipeBuilder implements RecipeBuilder {

    protected final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    @Nullable
    protected String group;

    private final Ingredient ingredient;
    private final ItemStack itemResult;
    private final FluidStack fluidResult;
    private final int drainTime;

    public DrainingRecipeBuilder(Ingredient ingredient, ItemStack itemResult, FluidStack fluidResult, int drainTime) {
        this.ingredient = ingredient;
        this.itemResult = itemResult;
        this.fluidResult = fluidResult;
        this.drainTime = drainTime;
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
        return this.itemResult.getItem();
    }

    public FluidStack getFluidResult() {
        return this.fluidResult;
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation resourceLocation) {
        /*Advancement.Builder advancement = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(resourceLocation))
                .rewards(AdvancementRewards.Builder.recipe(resourceLocation))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancement::addCriterion);*/
        DrainingRecipe recipe = new DrainingRecipe(this.ingredient, this.itemResult, this.fluidResult, this.drainTime);
        recipeOutput.accept(resourceLocation, recipe, null/*advancement.build(resourceLocation.withPrefix("recipes/"))*/);
    }

}
