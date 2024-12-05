package com.vulp.skullsandspirits.inventory;

import com.vulp.skullsandspirits.crafting.RecipeRegistry;
import com.vulp.skullsandspirits.inventory.slot.SASSlots;
import com.vulp.skullsandspirits.util.SASUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;

public class KegMenu extends AbstractContainerMenu {

    private static HashSet<Item> KNOWN_VESSELS = new HashSet<>();

    private final Container keg;
    private final ContainerData kegData;
    private Fluid currentFluid;
    private int lastFluidID;

    public KegMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(9), new SimpleContainerData(4));
    }

    public KegMenu(int id, Inventory playerInventory, Container kegContainer, ContainerData kegData) {
        super(MenuRegistry.KEG.get(), id);
        checkContainerSize(kegContainer, 9);
        checkContainerDataCount(kegData, 4);
        this.keg = kegContainer;
        this.kegData = kegData;
        KNOWN_VESSELS = getValidVesselItems(playerInventory.player.level());
        for (int k = 0; k < 2; ++k) {
            for (int j = 0; j < 3; ++j) {
                this.addSlot(new Slot(kegContainer, j + k * 3, 68 + j * 18, 17 + k * 18));
            }
        }
        this.addSlot(new SASSlots.FluidItemSlot(kegContainer, 6, 46, 53));
        this.addSlot(new VesselSlot(kegContainer, 7, 86, 58));
        this.addSlot(new SASSlots.SimpleResultSlot(kegContainer, 8, 152, 25));

        this.addDataSlots(kegData);
        for(int k = 0; k < 3; ++k) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + k * 9 + 9, 14 + j * 18, 84 + k * 18));
            }
        }
        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 14 + k * 18, 142));
        }
    }

    public FluidStack getFluid() {
        int fluidID = this.kegData.get(2);
        if (this.lastFluidID != fluidID || this.currentFluid == null) {
            this.currentFluid = BuiltInRegistries.FLUID.byId(fluidID);
            this.lastFluidID = fluidID;
        }
        return new FluidStack(currentFluid, this.kegData.get(3));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == 8) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index >= 0 && index <= 5) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == 6) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == 7) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (index >= 9 && index < 45) {
                    if (itemstack.getCapability(Capabilities.FluidHandler.ITEM) != null) {
                        if (!this.moveItemStackTo(itemstack1, 6, 7, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                    if (this.isVessel(itemstack)) {
                        if (!this.moveItemStackTo(itemstack1, 7, 8, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                    if (!this.moveItemStackTo(itemstack1, 0, 6, false)) {
                        if (index < 36) {
                            if (!this.moveItemStackTo(itemstack1, 36, 45, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else {
                            if (!this.moveItemStackTo(itemstack1, 9, 36, false)) {
                                return ItemStack.EMPTY;
                            }
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

    protected boolean isVessel(ItemStack stack) {
        return KNOWN_VESSELS.contains(stack.getItem());
    }

    @Override
    public boolean stillValid(Player player) {
        return this.keg.stillValid(player);
    }

    public float getBrewProgress() {
        int i = this.kegData.get(0);
        int j = this.kegData.get(1);
        return j != 0 && i != 0 ? Mth.clamp((float)i / (float)j, 0.0F, 1.0F) : 0.0F;
    }

    public float getBrewTimeElapsed() {
        return this.kegData.get(0);
    }

    @Nullable
    public Component getTimeTooltipComponent() {
        int elapsedTicks = this.kegData.get(0);
        int maxTicks = this.kegData.get(1);

        if (maxTicks == 0) {
            return null;
        }

        String elapsedTime = SASUtils.ticksToFormattedTimeCompact(maxTicks - elapsedTicks, " ");
        return Component.literal(elapsedTime);
    }

    private static HashSet<Item> getValidVesselItems(Level level) {
        HashSet<Item> vessels = new HashSet<>();
        level.getRecipeManager().getAllRecipesFor(RecipeRegistry.KEG_BREWING.get()).forEach(recipeHolder -> vessels.addAll(Arrays.stream(recipeHolder.value().getVesselIngredient().getItems()).map(ItemStack::getItem).toList()));
        return vessels;
    }

    static class VesselSlot extends Slot {

        public VesselSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return KNOWN_VESSELS.contains(stack.getItem());
        }

    }

}
