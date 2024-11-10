package com.vulp.skullsandspirits.integration.jei;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.block.BlockRegistry;
import com.vulp.skullsandspirits.crafting.KegRecipe;
import com.vulp.skullsandspirits.crafting.RecipeRegistry;
import com.vulp.skullsandspirits.util.SASUtils;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class KegRecipeCategory implements IRecipeCategory<KegRecipe> {

    protected final IDrawableAnimated bubbles;
    private final Component title;
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable gauge;

    public KegRecipeCategory(IGuiHelper helper) {
        this.title = Component.translatable("jei." + SkullsAndSpirits.MODID + ".keg_brewing");
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "textures/gui/jei/keg_brewing.png");
        this.background = helper.createDrawable(texture, 0, 0, 134, 59);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(BlockRegistry.SHODDY_KEG.get()));
        this.bubbles = helper.drawableBuilder(texture, 134, 0, 23, 25).buildAnimated(200, IDrawableAnimated.StartDirection.BOTTOM, false);
        this.gauge = helper.createDrawable(texture, 157, 0, 16, 55);
    }


    @Override
    public RecipeType<KegRecipe> getRecipeType() {
        return JEIPlugin.KEG_BREWING;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, KegRecipe recipe, IFocusGroup focuses) {
        NonNullList<Ingredient> recipeIngredients = recipe.getIngredients();
        FluidIngredient fluidIngredient = recipe.getFluidIngredient();
        int fluidAmount = recipe.getFluidAmount();
        Ingredient vesselIngredient = recipe.getVesselIngredient();
        ItemStack resultStack = recipe.getResult();

        if (!fluidIngredient.isEmpty()) {
            IRecipeSlotBuilder fluidSlotBuilder = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 1, 2);
            for (FluidStack stack : fluidIngredient.getStacks()) {
                fluidSlotBuilder.addFluidStack(stack.getFluid(), fluidAmount);
            }
            fluidSlotBuilder.setFluidRenderer(1000, false, 16, 55); // Capacity remains 1000 just for now.
        }

        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 3; ++j) {
                int slot = j + i * 3;
                if (slot > recipeIngredients.size() - 1) {
                    break;
                }
                builder.addSlot(RecipeIngredientRole.INPUT, 37 + j * 18, 1 + i * 18).addItemStacks(Arrays.asList(recipeIngredients.get(slot).getItems())); // Ingredient slots
            }
        }
        builder.addSlot(RecipeIngredientRole.INPUT, 55, 42).addItemStacks(Arrays.asList(vesselIngredient.getItems())); // Vessel slot
        builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 9).addItemStack(resultStack); // Output slot
    }

    @Override
    public void draw(KegRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        bubbles.draw(guiGraphics, 110, 32);
        gauge.draw(guiGraphics, 1, 2);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, KegRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
        if (SASUtils.isMouseInsideArea(110, 32, 23, 25, mouseX, mouseY)) {
            tooltip.add(Component.literal(SASUtils.ticksToFormattedTimeCompact(recipe.getBrewTime(), "")));
        }
    }

}
