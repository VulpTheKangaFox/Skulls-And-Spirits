package com.vulp.skullsandspirits.inventory.slot;

import com.vulp.skullsandspirits.component.DataComponentRegistry;
import com.vulp.skullsandspirits.item.DrinkItem;
import com.vulp.skullsandspirits.util.DrinkTier;
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

    public static class DrinkItemSlot extends Slot {

        private final DrinkTier tier;

        public DrinkItemSlot(Container container, DrinkTier tier, int slot, int x, int y) {
            super(container, slot, x, y);
            this.tier = tier;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            // Check if the item is a DrinkItem and has the correct tier
            if (stack.getItem() instanceof DrinkItem) {
                DrinkTier stackTier = DrinkItem.getTier(stack);
                return stackTier == this.tier; // Ensure it matches this slot's tier
            }
            return false; // Not valid if not a DrinkItem
        }

    }

}