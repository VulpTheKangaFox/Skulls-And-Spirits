package com.vulp.skullsandspirits.inventory.slot;

import com.vulp.skullsandspirits.inventory.KegMenu;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;

public class SASSlots {

    public static class SimpleResultSlot extends Slot {

        public SimpleResultSlot(Container container, int slot, int xPosition, int yPosition) {
            super(container, slot, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

    }

    public static class FluidItemSlot extends Slot {

        public FluidItemSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getCapability(Capabilities.FluidHandler.ITEM) != null;
        }

    }

}