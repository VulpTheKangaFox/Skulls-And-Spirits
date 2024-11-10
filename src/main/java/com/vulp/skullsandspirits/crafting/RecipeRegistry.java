package com.vulp.skullsandspirits.crafting;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.crafting.serializer.DrainingRecipeSerializer;
import com.vulp.skullsandspirits.crafting.serializer.KegRecipeSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class RecipeRegistry {

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, SkullsAndSpirits.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, SkullsAndSpirits.MODID);

    public static final Supplier<RecipeType<KegRecipe>> KEG_BREWING = RECIPE_TYPES.register("keg_brewing", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "keg_brewing")));
    public static final Supplier<RecipeType<DrainingRecipe>> DRAINING = RECIPE_TYPES.register("draining", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "draining")));

    public static final Supplier<RecipeSerializer<KegRecipe>> KEG_BREWING_SERIALIZER = RECIPE_SERIALIZERS.register("keg_brewing", KegRecipeSerializer::new);
    public static final Supplier<RecipeSerializer<DrainingRecipe>> DRAINING_SERIALIZER = RECIPE_SERIALIZERS.register("draining", DrainingRecipeSerializer::new);

}
