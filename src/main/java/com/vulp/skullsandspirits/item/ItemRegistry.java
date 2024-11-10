package com.vulp.skullsandspirits.item;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.effect.EffectRegistry;
import com.vulp.skullsandspirits.fluid.FluidRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SkullsAndSpirits.MODID);

    public static final DeferredItem<Item> MUG = ITEMS.register("mug", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GRAVEKEEPERS_BREW = ITEMS.register("gravekeepers_brew", () -> new DrinkItem(4, 4, () -> ItemRegistry.MUG,
            new MobEffectInstance[]{new MobEffectInstance(EffectRegistry.GRAVEMARKED, 54000)}, new int[]{9710107, 15745318}, new Item.Properties()));
    public static final DeferredItem<Item> BLOODWINE = ITEMS.register("bloodwine", () -> new DrinkItem(4, 4, () -> ItemRegistry.MUG,
            new MobEffectInstance[]{new MobEffectInstance(EffectRegistry.BLOODTHIRSTY, 24000)}, new int[]{7739673, 14357793}, new Item.Properties()));
    public static final DeferredItem<Item> ROTTEN_RUM = ITEMS.register("rotten_rum", () -> new DrinkItem(4, 4, () -> ItemRegistry.MUG,
            new MobEffectInstance[]{new MobEffectInstance(EffectRegistry.ROTTEN_BRAWN, 24000)}, new int[]{4621068, 11071506}, new Item.Properties()));
    public static final DeferredItem<Item> INFERNAL_MULE = ITEMS.register("infernal_mule", () -> new DrinkItem(4, 4, () -> ItemRegistry.MUG,
            new MobEffectInstance[]{new MobEffectInstance(EffectRegistry.MULES_MIGHT, 72000)}, new int[]{6892550, 16754706}, new Item.Properties()));

    public static final DeferredItem<Item> BLOOD_BUCKET = ITEMS.register("blood_bucket", () -> new BucketItem(FluidRegistry.BLOOD.get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final DeferredItem<Item> JERKY = ITEMS.register("jerky", () -> new Item(new Item.Properties().food(FoodProps.JERKY)));

    // We store food properties here because where else are we really going to use them?
    private static class FoodProps {

        public static final FoodProperties JERKY = new FoodProperties.Builder().nutrition(2).saturationModifier(0.8F).fast().build();

    }

}
