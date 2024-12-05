package com.vulp.skullsandspirits.screen;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.inventory.DistilleryMenu;
import com.vulp.skullsandspirits.item.DrinkItem;
import com.vulp.skullsandspirits.util.DrinkTier;
import com.vulp.skullsandspirits.util.SASUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.List;
import java.util.Optional;

public class DistilleryScreen extends AbstractContainerScreen<DistilleryMenu> {

	private static final ResourceLocation DISTILLERY_SPRITE_LOCATION = ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "textures/gui/distillery.png");

	public DistilleryScreen(DistilleryMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.imageWidth = 180;
		this.inventoryLabelX += 1;
	}

	@Override
	protected void init() {
		super.init();
		this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		super.renderTooltip(guiGraphics, mouseX, mouseY);

		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;

		for (int k = 0; k < 4; k++) {
			int regionX = i + 26 + k * 37;
			int regionY = j + 37;
			int regionWidth = 17;
			int regionHeight = 14;

			if (SASUtils.isMouseInsideArea(regionX, regionY, regionWidth, regionHeight, mouseX, mouseY)) {
				if (this.menu.getTier() >= k) {
					int time = this.menu.getDistilTimes()[k];
					int maxTime = this.menu.getMaxDistilTimes()[k];
					if (maxTime == 0) {
						return;
					}
					String timeString = SASUtils.ticksToFormattedTimeCompact(maxTime - time, " ");
					guiGraphics.renderTooltip(this.font, List.of(Component.literal(timeString)), Optional.empty(), mouseX, mouseY);
				} else {
					DrinkTier drinkTier = DrinkTier.fromInt(k + 1);
					Component cannotUpgradeComponent = Component.translatable("container.skullsandspirits.distillery.cannot_upgrade", Component.literal(drinkTier.getGrade().toUpperCase()).withStyle(DrinkItem.getTierColor(drinkTier, true), ChatFormatting.BOLD, ChatFormatting.UNDERLINE));

					guiGraphics.renderTooltip(this.font, List.of(cannotUpgradeComponent), Optional.empty(), mouseX, mouseY);
				}
			}
		}
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		int i = (this.width - this.imageWidth) / 2;
		int j = (this.height - this.imageHeight) / 2;
		guiGraphics.blit(DISTILLERY_SPRITE_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
		for (int k = 0; k < 4; k++) {
			if (this.menu.getTier() >= k) {
				Slot slot = this.menu.getSlot(k);
				if (slot.hasItem()) {
					int time = this.menu.getDistilTimes()[k];
					int maxTime = this.menu.getMaxDistilTimes()[k];
					int arrow = Mth.ceil(maxTime != 0 && time != 0 ? Mth.clamp((float)time / (float)maxTime, 0.0F, 1.0F) * 15.0F : 0.0F);
					guiGraphics.blit(DISTILLERY_SPRITE_LOCATION, i + 27 + k * 37, j + 38, 180, 0, arrow, 12);
				}
			} else {
				guiGraphics.blit(DISTILLERY_SPRITE_LOCATION, i + 27 + k * 37, j + 38, 180, 12, 15, 12);
			}
		}
	}

}