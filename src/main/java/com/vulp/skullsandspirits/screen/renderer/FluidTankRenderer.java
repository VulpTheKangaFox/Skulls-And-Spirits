package com.vulp.skullsandspirits.screen.renderer;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import org.joml.Matrix4f;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Thank you mezz! Credit goes to https://github.com/mezz/JustEnoughItems for the code I heavily referenced here.
public class FluidTankRenderer {

    private final int maxFluidAmount;
    private final int width;
    private final int height;
    private final boolean sideways;

    public FluidTankRenderer(int maxFluidAmount, int width, int height, boolean sideways) {
        Preconditions.checkArgument(maxFluidAmount > 0, "Max fluid amount must be greater than 0!");
        Preconditions.checkArgument(width > 0, "Max width must be greater than 0!");
        Preconditions.checkArgument(height > 0, "Max height must be greater than 0!");
        this.maxFluidAmount = maxFluidAmount;
        this.width = width;
        this.height = height;
        this.sideways = sideways;
    }

    public void render(PoseStack poseStack, int x, int y, FluidStack fluidStack) {
        RenderSystem.enableBlend();
        poseStack.pushPose();
        {
            poseStack.translate(x, y, 0);
            drawFluidTank(poseStack, this.width, this.height, fluidStack, this.sideways);
        }
        poseStack.popPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    public void drawFluidTank(PoseStack poseStack, final int x, final int y, FluidStack fluidStack, boolean sideways) {
        if (fluidStack.isEmpty()) {
            return;
        }

        int fluidAmount = fluidStack.getAmount();
        int scaledAmount = (fluidAmount * (sideways ? this.width : this.height)) / this.maxFluidAmount;

        if (fluidAmount > 0 && scaledAmount < 1) {
            scaledAmount = 1;
        }
        if (sideways) {
            if (scaledAmount > this.width) {
                scaledAmount = this.width;
            }
        } else {
            if (scaledAmount > this.height) {
                scaledAmount = this.height;
            }
        }

        IClientFluidTypeExtensions fluidClientInfo = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation fluidLocation = fluidClientInfo.getStillTexture(fluidStack);
        TextureAtlasSprite fluidSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidLocation);
        int fluidTint = fluidClientInfo.getTintColor(fluidStack);

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        Matrix4f matrix = poseStack.last().pose();

        float red = (fluidTint >> 16 & 0xFF) / 255.0F;
        float green = (fluidTint >> 8 & 0xFF) / 255.0F;
        float blue = (fluidTint & 0xFF) / 255.0F;
        float alpha = ((fluidTint >> 24) & 0xFF) / 255F;
        RenderSystem.setShaderColor(red, green, blue, alpha);

        final int xTileAmount = sideways ? scaledAmount / 16 : x / 16;
        final int yTileAmount = sideways ? y / 16 : scaledAmount / 16;
        final int xTileRemainder = sideways ? scaledAmount - (xTileAmount * 16) : x - (xTileAmount * 16);
        final int yTileRemainder = sideways ? y - (yTileAmount * 16) : scaledAmount - (yTileAmount * 16);

        for (int xTile = 0; xTile <= xTileAmount; xTile++) {
            for (int yTile = 0; yTile <= yTileAmount; yTile++) {
                int width = xTile == xTileAmount ? xTileRemainder : 16;
                int height = yTile == yTileAmount ? yTileRemainder : 16;

                int finalX = xTile * 16;
                int finalY = y - ((yTile + 1) * 16);

                if (width > 0 && height > 0) {
                    int maskTop = 16 - height;
                    int maskRight = 16 - width;

                    float uMin = fluidSprite.getU0();
                    float uMax = fluidSprite.getU1();
                    float vMin = fluidSprite.getV0();
                    float vMax = fluidSprite.getV1();

                    if (sideways) {
                        uMax = uMax - (maskRight / 16F * (uMax - uMin));
                        vMax = vMax - (maskTop / 16F * (vMax - vMin));
                    } else {
                        uMax = uMax - (maskRight / 16F * (uMax - uMin));
                        vMax = vMax - (maskTop / 16F * (vMax - vMin));
                    }

                    RenderSystem.setShader(GameRenderer::getPositionTexShader);

                    Tesselator tessellator = Tesselator.getInstance();
                    BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                    bufferBuilder.addVertex(matrix, finalX, finalY + 16, 100).setUv(uMin, vMax);
                    bufferBuilder.addVertex(matrix, finalX + 16 - maskRight, finalY + 16, 100).setUv(uMax, vMax);
                    bufferBuilder.addVertex(matrix, finalX + 16 - maskRight, finalY + maskTop, 100).setUv(uMax, vMin);
                    bufferBuilder.addVertex(matrix, finalX, finalY + maskTop, 100).setUv(uMin, vMin);
                    BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
                }
            }
        }
    }

    public List<Component> getTooltip(FluidStack fluidStack) {
        List<Component> tooltip = new ArrayList<>();
        if (fluidStack.isEmpty()) {
            return tooltip;
        }
        final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
        Component fluidName = fluidStack.getHoverName();
        tooltip.add(fluidName);
        tooltip.add(Component.literal(numberFormat.format((fluidStack.getAmount() * 1000L) / FluidType.BUCKET_VOLUME) + "/" + this.maxFluidAmount + "mB").withStyle(ChatFormatting.GRAY));
        return tooltip;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
