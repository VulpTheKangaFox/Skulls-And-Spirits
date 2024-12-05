package com.vulp.skullsandspirits.inventory;

import com.vulp.skullsandspirits.inventory.slot.SASSlots;
import com.vulp.skullsandspirits.item.DrinkItem;
import com.vulp.skullsandspirits.util.DrinkTier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class DistilleryMenu extends AbstractContainerMenu {

	private final Container distillery;
	private final ContainerData distilleryData;

	public DistilleryMenu(int id, Inventory playerInventory) {
		this(id, playerInventory, new SimpleContainer(5), new SimpleContainerData(9));
	}

	public DistilleryMenu(int id, Inventory playerInventory, Container distilleryContainer, ContainerData distilleryData) {
		super(MenuRegistry.DISTILLERY.get(), id);
		checkContainerSize(distilleryContainer, 5);
		checkContainerDataCount(distilleryData, 9);
		this.distillery = distilleryContainer;
		this.distilleryData = distilleryData;
		int tier = this.distilleryData.get(0);
		for (int j = 0; j < 5; ++j) {
			int k = 8 + j * 37;
			if (j <= tier) {
				this.addSlot(new SASSlots.DrinkItemSlot(distilleryContainer, DrinkTier.fromInt(j), j, k, 36));
			} else {
				this.addSlot(new SASSlots.SimpleResultSlot(distilleryContainer, j, k, 36));
			}
		}
		this.addDataSlots(distilleryData);
		for(int k = 0; k < 3; ++k) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInventory, j + k * 9 + 9, 10 + j * 18, 84 + k * 18));
			}
		}
		for(int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(playerInventory, k, 10 + k * 18, 142));
		}
	}

	public int[] getDistilTimes() {
		int[] distilTimes = new int[4];
		for (int i = 0; i < 4; i++) {
			distilTimes[i] = this.distilleryData.get(i + 1);
		}
		return distilTimes;
	}

	public int[] getMaxDistilTimes() {
		int[] maxDistilTimes = new int[4];
		for (int i = 0; i < 4; i++) {
			maxDistilTimes[i] = this.distilleryData.get(i + 5);
		}
		return maxDistilTimes;
	}

	public int getTier() {
		return this.distilleryData.get(0);
	}

	// TODO: Get this sorted. Employ GPT if you gotta.
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();

			// Check if the slot is one of the 5 drink slots
			if (index >= 0 && index < 5) {
				// Attempt to move item from drink slots to player inventory (index 5 and beyond)
				if (!this.moveItemStackTo(itemstack1, 5, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(itemstack1, itemstack);
			} else {
				// Handle moving items from player inventory to drink slots
				int tierLimit = this.distilleryData.get(0); // Get the allowed tier limit
				if (itemstack1.getItem() instanceof DrinkItem) {
					DrinkTier itemTier = DrinkItem.getTier(itemstack1);
					boolean moved = false;

					for (int i = 0; i <= tierLimit; i++) {
						Slot targetSlot = this.slots.get(i);
						if (targetSlot instanceof SASSlots.DrinkItemSlot drinkSlot && drinkSlot.tier == itemTier) {
							if (this.moveItemStackTo(itemstack1, i, i + 1, false)) {
								moved = true;
								break;
							}
						}
					}

					if (!moved) {
						return ItemStack.EMPTY;
					}
				} else {
					// Attempt to move non-drink items to player inventory or other valid slots
					if (index < this.slots.size() - 36) {
						if (!this.moveItemStackTo(itemstack1, this.slots.size() - 36, this.slots.size(), false)) {
							return ItemStack.EMPTY;
						}
					} else if (!this.moveItemStackTo(itemstack1, 5, this.slots.size() - 36, false)) {
						return ItemStack.EMPTY;
					}
				}
			}

			if (itemstack1.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
		}

		return itemstack;
	}

	@Override
	public boolean stillValid(Player player) {
		return this.distillery.stillValid(player);
	}

}
