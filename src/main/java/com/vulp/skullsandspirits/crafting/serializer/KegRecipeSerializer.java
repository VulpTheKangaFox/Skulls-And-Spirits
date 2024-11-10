package com.vulp.skullsandspirits.crafting.serializer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vulp.skullsandspirits.crafting.KegRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.TagFluidIngredient;

public class KegRecipeSerializer implements RecipeSerializer<KegRecipe> {

    public static final MapCodec<KegRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.listOf().fieldOf("item_ingredients").flatXmap((ingredients) -> {
                Ingredient[] ingredientArray = ingredients.toArray(Ingredient[]::new);
                if (ingredientArray.length == 0) {
                    return DataResult.error(() -> "No ingredients for keg recipe");
                } else {
                    return ingredientArray.length > 6 ? DataResult.error(() -> "Too many ingredients for keg recipe. It supports a maximum of 6") : DataResult.success(NonNullList.of(Ingredient.EMPTY, ingredientArray));
                }
            }, DataResult::success).forGetter(KegRecipe::getIngredients),
            FluidIngredient.CODEC.fieldOf("fluid_ingredient").forGetter(KegRecipe::getFluidIngredient),
            Codec.INT.fieldOf("fluid_amount").forGetter(KegRecipe::getFluidAmount),
            Ingredient.CODEC_NONEMPTY.fieldOf("vessel_ingredient").forGetter(KegRecipe::getVesselIngredient),
            ItemStack.SINGLE_ITEM_CODEC.fieldOf("result").forGetter(KegRecipe::getResult),
            Codec.INT.fieldOf("brewing_time").forGetter(KegRecipe::getBrewTime)
    ).apply(inst, KegRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, KegRecipe> STREAM_CODEC = StreamCodec.of(KegRecipeSerializer::toNetwork, KegRecipeSerializer::fromNetwork);

    private static KegRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        int i = buffer.readVarInt();
        NonNullList<Ingredient> itemIngredients = NonNullList.withSize(i, Ingredient.EMPTY);
        itemIngredients.replaceAll((ingredient) -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
        FluidIngredient fluidIngredient = FluidIngredient.STREAM_CODEC.decode(buffer);
        int fluidAmount = buffer.readVarInt();
        Ingredient vesselIngredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        ItemStack result = ItemStack.STREAM_CODEC.decode(buffer);
        int brewTime = buffer.readVarInt();
        return new KegRecipe(itemIngredients, fluidIngredient, fluidAmount, vesselIngredient, result, brewTime);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, KegRecipe recipe) {
        buffer.writeVarInt(recipe.getIngredients().size());
        for (Ingredient ingredient : recipe.getIngredients()) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
        }
        FluidIngredient.STREAM_CODEC.encode(buffer, recipe.getFluidIngredient());
        buffer.writeVarInt(recipe.getFluidAmount());
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getVesselIngredient());
        ItemStack.STREAM_CODEC.encode(buffer, recipe.getResult());
        buffer.writeVarInt(recipe.getBrewTime());
    }

    @Override
    public MapCodec<KegRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, KegRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
