package com.vulp.skullsandspirits.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.inventory.KegMenu;
import com.vulp.skullsandspirits.screen.renderer.FluidTankRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.Optional;

public class KegScreen extends AbstractContainerScreen<KegMenu> {

    private static final ResourceLocation KEG_SPRITE_LOCATION = ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "textures/gui/shoddy_keg.png");
    private FluidTankRenderer fluidRenderer;

    public KegScreen(KegMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 188;
        this.inventoryLabelX += 6;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        this.fluidRenderer = new FluidTankRenderer(1000, 16, 52, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(0,0,105);
        guiGraphics.blit(KEG_SPRITE_LOCATION, i + 24, j + 17, 188, 0, 16, 52);
        poseStack.popPose();
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        if (x > i + 22 && y > j + 15 && x < i + 41 && y < j + 70) {
            guiGraphics.renderTooltip(this.font, this.fluidRenderer.getTooltip(this.menu.getFluid()), Optional.empty(), x, y);
        }
        if (x > i + 148 && y > j + 47 && x < i + 172 && y < j + 73) {
            Component timeTooltipComponent = this.menu.getTimeTooltipComponent();
            if (timeTooltipComponent == null) {
                return;
            }
            guiGraphics.renderTooltip(this.font, List.of(timeTooltipComponent), Optional.empty(), x, y);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(KEG_SPRITE_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (this.menu.getSlot(6).hasItem()) {
            guiGraphics.blit(KEG_SPRITE_LOCATION, i + 45, j + 52, 188, 52, 18, 18);
        }
        if (this.menu.getSlot(7).hasItem()) {
            guiGraphics.blit(KEG_SPRITE_LOCATION, i + 85, j + 57, 188, 70, 18, 18);
        }
        this.fluidRenderer.render(guiGraphics.pose(), i + 24, j + 17, this.menu.getFluid());
        guiGraphics.blit(KEG_SPRITE_LOCATION, i + 123, j + 26, 188, 88, Mth.ceil(this.menu.getBrewProgress() * 22.0F), 16);
        int brewBubbles = (int) (this.menu.getBrewTimeElapsed() % 26);
        guiGraphics.blit(KEG_SPRITE_LOCATION, i + 149, j + 48 + 25 - brewBubbles, 188, 104 + 25 - brewBubbles, 23, brewBubbles);
    }

}
