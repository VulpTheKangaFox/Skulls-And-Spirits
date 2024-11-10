package com.vulp.skullsandspirits.crafting.serializer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.vulp.skullsandspirits.crafting.DrainingRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

public class DrainingRecipeSerializer  implements RecipeSerializer<DrainingRecipe> {

    public static final MapCodec<DrainingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(DrainingRecipe::getIngredient),
            ItemStack.CODEC.fieldOf("item_result").forGetter(DrainingRecipe::getItemResult),
            FluidStack.CODEC.fieldOf("fluid_ingredient").forGetter(DrainingRecipe::getFluidResult),
            Codec.INT.fieldOf("brewing_time").forGetter(DrainingRecipe::getDrainTime)
    ).apply(inst, DrainingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, DrainingRecipe> STREAM_CODEC = StreamCodec.of(DrainingRecipeSerializer::toNetwork, DrainingRecipeSerializer::fromNetwork);

    private static DrainingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        ItemStack itemResult = ItemStack.STREAM_CODEC.decode(buffer);
        FluidStack fluidResult = FluidStack.STREAM_CODEC.decode(buffer);
        int dryTime = buffer.readVarInt();
        return new DrainingRecipe(ingredient, itemResult, fluidResult, dryTime);
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, DrainingRecipe recipe) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getIngredient());
        ItemStack.STREAM_CODEC.encode(buffer, recipe.getItemResult());
        FluidStack.STREAM_CODEC.encode(buffer, recipe.getFluidResult());
        buffer.writeVarInt(recipe.getDrainTime());
    }

    @Override
    public MapCodec<DrainingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, DrainingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
