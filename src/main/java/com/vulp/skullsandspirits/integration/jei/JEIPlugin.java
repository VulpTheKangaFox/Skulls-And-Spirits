package com.vulp.skullsandspirits.integration.jei;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.block.BlockRegistry;
import com.vulp.skullsandspirits.crafting.KegRecipe;
import com.vulp.skullsandspirits.crafting.RecipeRegistry;
import com.vulp.skullsandspirits.inventory.KegMenu;
import com.vulp.skullsandspirits.inventory.MenuRegistry;
import com.vulp.skullsandspirits.screen.KegScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_ID = ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "jei_plugin");
    public static final RecipeType<KegRecipe> KEG_BREWING = RecipeType.create(SkullsAndSpirits.MODID, "keg_brewing", KegRecipe.class);

    public JEIPlugin() {

    }

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new KegRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        RecipeManager manager = level.getRecipeManager();
        List<KegRecipe> list = manager.getAllRecipesFor(RecipeRegistry.KEG_BREWING.get()).stream().map(RecipeHolder::value).toList();
        registration.addRecipes(KEG_BREWING, list);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(KegMenu.class, MenuRegistry.KEG.get(), KEG_BREWING, 0, 7, 9, 36);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(BlockRegistry.SHODDY_KEG.get()), KEG_BREWING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(KegScreen.class, 124, 26, 20, 16, KEG_BREWING);
    }
}
