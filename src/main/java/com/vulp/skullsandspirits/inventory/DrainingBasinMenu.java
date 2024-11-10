package com.vulp.skullsandspirits.inventory;

import com.vulp.skullsandspirits.inventory.slot.SASSlots;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;

public class DrainingBasinMenu extends AbstractContainerMenu {

    private final Container basin;
    private final ContainerData basinData;
    private Fluid currentFluid;
    private int lastFluidID;

    public DrainingBasinMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(3), new SimpleContainerData(4));
    }

    public DrainingBasinMenu(int id, Inventory playerInventory, Container basinContainer, ContainerData basinData) {
        super(MenuRegistry.DRAINING_BASIN.get(), id);
        checkContainerSize(basinContainer, 3);
        checkContainerDataCount(basinData, 4);
        this.basin = basinContainer;
        this.basinData = basinData;
        this.addSlot(new Slot(basinContainer, 0, 28, 34));
        this.addSlot(new SASSlots.SimpleResultSlot(basinContainer, 1, 90, 46));
        this.addSlot(new SASSlots.FluidItemSlot(basinContainer, 2, 132, 46));
        this.addDataSlots(basinData);
        for(int k = 0; k < 3; ++k) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + k * 9 + 9, 8 + j * 18, 84 + k * 18));
            }
        }
        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    public FluidStack getFluid() {
        int fluidID = this.basinData.get(2);
        if (this.lastFluidID != fluidID || this.currentFluid == null) {
            this.currentFluid = BuiltInRegistries.FLUID.byId(fluidID);
            this.lastFluidID = fluidID;
        }
        return new FluidStack(currentFluid, this.basinData.get(3));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index == 1) {
                if (!this.moveItemStackTo(itemstack1, 4, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index == 0) {
                if (!this.moveItemStackTo(itemstack1, 4, 39, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == 2) {
                if (!this.moveItemStackTo(itemstack1, 4, 39, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 3 && index < 39) {
                if (itemstack.getCapability(Capabilities.FluidHandler.ITEM) != null) {
                    if (!this.moveItemStackTo(itemstack1, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                    if (index < 30) {
                        if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        if (!this.moveItemStackTo(itemstack1, 3, 30, false)) {
                            return ItemStack.EMPTY;
                        }
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
        return this.basin.stillValid(player);
    }

    public float getDrainProgress() {
        int i = this.basinData.get(0);
        int j = this.basinData.get(1);
        return j != 0 && i != 0 ? Mth.clamp((float)i / (float)j, 0.0F, 1.0F) : 0.0F;
    }

    public float getDrainTimeElapsed() {
        return this.basinData.get(0);
    }

}
