package com.vulp.skullsandspirits.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.inventory.DrainingBasinMenu;
import com.vulp.skullsandspirits.screen.renderer.FluidTankRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class DrainingBasinScreen extends AbstractContainerScreen<DrainingBasinMenu> {

    private static final ResourceLocation BASIN_SPRITE_LOCATION = ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "textures/gui/draining_basin.png");
    private FluidTankRenderer fluidRenderer;

    public DrainingBasinScreen(DrainingBasinMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.fluidRenderer = new FluidTankRenderer(2000, 58, 16, true);
    }


    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (x > i + 89 && y > j + 21 && x < i + 149 && y < j + 39) {
            guiGraphics.renderTooltip(this.font, this.fluidRenderer.getTooltip(this.menu.getFluid()), Optional.empty(), x, y);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0,0,105);
        guiGraphics.blit(BASIN_SPRITE_LOCATION, i + 90, j + 22, 0, 166, 58, 16);
        poseStack.popPose();
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BASIN_SPRITE_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (this.menu.getSlot(2).hasItem()) {
            guiGraphics.blit(BASIN_SPRITE_LOCATION, i + 131, j + 45, 176, 38, 18, 18);
        }
        this.fluidRenderer.render(guiGraphics.pose(), i + 90, j + 22, this.menu.getFluid());
        guiGraphics.blit(BASIN_SPRITE_LOCATION, i + 49, j + 23, 176, 0, Mth.ceil(this.menu.getDrainProgress() * 36.0F), 38);
    }

}
