package com.vulp.skullsandspirits.datagen;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.block.BlockRegistry;
import com.vulp.skullsandspirits.datagen.recipe.DrainingRecipeBuilder;
import com.vulp.skullsandspirits.datagen.recipe.KegRecipeBuilder;
import com.vulp.skullsandspirits.fluid.FluidRegistry;
import com.vulp.skullsandspirits.item.ItemRegistry;
import com.vulp.skullsandspirits.tag.SASTagCache;
import com.vulp.skullsandspirits.tag.TagRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.concurrent.CompletableFuture;

public class SASRecipeProvider extends RecipeProvider {

    public SASRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        Ingredient mushroomIngredient = Ingredient.of(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM, Items.CRIMSON_FUNGUS, Items.WARPED_FUNGUS);
        new KegRecipeBuilder(NonNullList.of(Ingredient.EMPTY,
                mushroomIngredient, mushroomIngredient),
                FluidIngredient.of(Fluids.WATER), 200, Ingredient.of(Items.BOWL),
                new ItemStack(Items.MUSHROOM_STEW),
                300).save(output);

        new KegRecipeBuilder(NonNullList.of(Ingredient.EMPTY,
                Ingredient.of(Blocks.SOUL_SAND, Blocks.SOUL_SOIL), Ingredient.of(Blocks.SMOOTH_BASALT), Ingredient.of(Items.BONE), Ingredient.of(Items.ROTTEN_FLESH)),
                FluidIngredient.of(Fluids.WATER), 250, Ingredient.of(ItemRegistry.MUG.get()),
                new ItemStack(ItemRegistry.GRAVEKEEPERS_BREW.get()),
                6000).save(output);

        new KegRecipeBuilder(NonNullList.of(Ingredient.EMPTY,
                Ingredient.of(Items.ROTTEN_FLESH), Ingredient.of(Items.SWEET_BERRIES), Ingredient.of(Items.NETHER_WART), Ingredient.of(Items.FERMENTED_SPIDER_EYE), Ingredient.of(Items.FERMENTED_SPIDER_EYE)),
                FluidIngredient.of(FluidRegistry.BLOOD.get()), 250, Ingredient.of(ItemRegistry.MUG.get()),
                new ItemStack(ItemRegistry.BLOODWINE.get()),
                6000).save(output);

        new KegRecipeBuilder(NonNullList.of(Ingredient.EMPTY,
                Ingredient.of(Items.SLIME_BALL), Ingredient.of(Items.ROTTEN_FLESH), Ingredient.of(Items.ROTTEN_FLESH), Ingredient.of(Items.SUGAR_CANE), Ingredient.of(Items.SUGAR_CANE)),
                FluidIngredient.of(Fluids.WATER), 250, Ingredient.of(ItemRegistry.MUG.get()),
                new ItemStack(ItemRegistry.ROTTEN_RUM.get()),
                6000).save(output);

        new KegRecipeBuilder(NonNullList.of(Ingredient.EMPTY,
                Ingredient.of(Items.GUNPOWDER), Ingredient.of(Items.GUNPOWDER), Ingredient.of(Items.BLAZE_POWDER), Ingredient.of(Items.HONEYCOMB)),
                FluidIngredient.of(Fluids.LAVA), 250, Ingredient.of(ItemRegistry.MUG.get()),
                new ItemStack(ItemRegistry.INFERNAL_MULE.get()),
                6000).save(output);

        new DrainingRecipeBuilder(Ingredient.of(TagRegistry.ItemTags.RAW_MEAT), new ItemStack(ItemRegistry.JERKY.get()), new FluidStack(FluidRegistry.BLOOD.get(), 200), 60).save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ItemRegistry.MUG.get(), 4)
                .pattern("X ")
                .pattern("O#")
                .pattern("X ")
                .define('X', Items.IRON_INGOT)
                .define('O', ItemTags.PLANKS)
                .define('#', Items.STICK)
                .unlockedBy("has_keg", has(BlockRegistry.SHODDY_KEG.get()))
                .save(output, ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "mug"));
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ItemRegistry.MUG.get(), 4)
                .pattern(" X")
                .pattern("#O")
                .pattern(" X")
                .define('X', Items.IRON_INGOT)
                .define('O', ItemTags.PLANKS)
                .define('#', Items.STICK)
                .unlockedBy("has_keg", has(BlockRegistry.SHODDY_KEG.get()))
                .save(output, ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "mug_variant"));
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, BlockRegistry.SHODDY_KEG.get())
                .pattern("XXX")
                .pattern("OBO")
                .pattern("XXX")
                .define('X', Items.IRON_INGOT)
                .define('O', ItemTags.PLANKS)
                .define('B', Items.BUCKET)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .save(output);
    }

}