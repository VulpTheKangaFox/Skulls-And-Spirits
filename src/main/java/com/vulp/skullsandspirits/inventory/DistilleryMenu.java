package com.vulp.skullsandspirits.inventory;

import com.vulp.skullsandspirits.inventory.slot.SASSlots;
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

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return null;
	}

	@Override
	public boolean stillValid(Player player) {
		return this.distillery.stillValid(player);
	}

}
