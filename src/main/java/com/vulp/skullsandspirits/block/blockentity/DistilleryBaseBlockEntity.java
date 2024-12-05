package com.vulp.skullsandspirits.block.blockentity;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.block.DistilleryTowerBlock;
import com.vulp.skullsandspirits.inventory.DistilleryMenu;
import com.vulp.skullsandspirits.item.DrinkItem;
import com.vulp.skullsandspirits.util.DrinkTier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DistilleryBaseBlockEntity extends BaseContainerBlockEntity {

	private NonNullList<ItemStack> itemSlots;
	private final ContainerData dataAccess;
	private int tier;
	private final List<Integer> distillTimes;
	private final List<Integer> maxDistillTimes;

	private static int safeGet(List<Integer> list, int index) {
		return index < list.size() ? list.get(index) : 0;
	}

	private static void setSafe(List<Integer> list, int index, int value) {
		while (list.size() <= index) {
			list.add(0);
		}
		list.set(index, value);
	}

	public DistilleryBaseBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockEntityRegistry.DISTILLERY_BASE.get(), pos, blockState);
		this.itemSlots = NonNullList.withSize(9, ItemStack.EMPTY);
		this.distillTimes = new ArrayList<>(Collections.nCopies(5, 0));
		this.maxDistillTimes = new ArrayList<>(Collections.nCopies(5, 0));
		this.dataAccess = new ContainerData() {
			final List<Integer> distillTimes = DistilleryBaseBlockEntity.this.distillTimes;
			final List<Integer> maxDistillTimes = DistilleryBaseBlockEntity.this.maxDistillTimes;

			public int get(int dataID) {
				return switch (dataID) {
					case 0 -> DistilleryBaseBlockEntity.this.tier;
					case 1 -> safeGet(distillTimes, 0);
					case 2 -> safeGet(distillTimes, 1);
					case 3 -> safeGet(distillTimes, 2);
					case 4 -> safeGet(distillTimes, 3);
					case 5 -> safeGet(maxDistillTimes, 0);
					case 6 -> safeGet(maxDistillTimes, 1);
					case 7 -> safeGet(maxDistillTimes, 2);
					case 8 -> safeGet(maxDistillTimes, 3);
					default -> 0;
				};
			}

			public void set(int dataID, int value) {
				switch (dataID) {
					case 0 -> DistilleryBaseBlockEntity.this.tier = value;
					case 1 -> setSafe(distillTimes, 0, value);
					case 2 -> setSafe(distillTimes, 1, value);
					case 3 -> setSafe(distillTimes, 2, value);
					case 4 -> setSafe(distillTimes, 3, value);
					case 5 -> setSafe(maxDistillTimes, 0, value);
					case 6 -> setSafe(maxDistillTimes, 1, value);
					case 7 -> setSafe(maxDistillTimes, 2, value);
					case 8 -> setSafe(maxDistillTimes, 3, value);
				}
			}

			public int getCount() {
				return 9;
			}
		};
	}

	public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
		if (level.isClientSide() || !(blockEntity instanceof DistilleryBaseBlockEntity distillery)) {
			return;
		}
		distillery.tier = state.getValue(DistilleryTowerBlock.TIER).getTier();
		NonNullList<ItemStack> inv = distillery.itemSlots;

		boolean flag = false;

		for (int i = 0; i <= distillery.tier; i++) {
			if (i >= inv.size()) break;

			ItemStack drinkStack = inv.get(i);

			if (drinkStack.getItem() instanceof DrinkItem && DrinkItem.getTier(drinkStack) != DrinkTier.S) {
				ItemStack upgradedDrinkStack = drinkStack.copy();
				DrinkItem.upgrade(upgradedDrinkStack);
				ItemStack outputSlot = inv.get(i + 1);
				int maxStackSize = distillery.getMaxStackSize();

				if (outputSlot.isEmpty() || (ItemStack.isSameItemSameComponents(outputSlot, upgradedDrinkStack)
						&& outputSlot.getCount() + 1 <= Math.min(maxStackSize, outputSlot.getMaxStackSize()))) {

					Integer distillTime = distillery.distillTimes.get(i);
					if (distillTime == 0) {
						distillery.maxDistillTimes.set(i, 200);
					}

					if (distillTime < distillery.maxDistillTimes.get(i)) {
						distillery.distillTimes.set(i, distillTime + 1);
					}

					if (distillTime + 1 == distillery.maxDistillTimes.get(i)) {
						distillery.distillTimes.set(i, 0);
						distillery.maxDistillTimes.set(i, 0);

						if (outputSlot.isEmpty()) {
							upgradedDrinkStack.setCount(1);
							inv.set(i + 1, upgradedDrinkStack);
						} else {
							outputSlot.grow(1);
						}
						drinkStack.shrink(1);
						flag = true;
					}
					continue;
				}
			}
			distillery.distillTimes.set(i, 0);
			distillery.maxDistillTimes.set(i, 0);
		}

		for (int i = distillery.tier + 1; i < distillery.distillTimes.size(); i++) {
			distillery.distillTimes.set(i, 0);
			distillery.maxDistillTimes.set(i, 0);
		}

		if (flag) {
			setChanged(level, pos, state);
		}
	}

	@Override
	protected @NotNull Component getDefaultName() {
		DrinkTier tier = DrinkTier.fromInt(this.tier + 1);
		MutableComponent tierPrefix = Component.translatable("item.skullsandspirits.drink_tier." + tier.getGrade()).withStyle(ChatFormatting.BOLD, DrinkItem.getTierColor(tier, false));
		return tierPrefix.append(" ").append(Component.translatable("container." + SkullsAndSpirits.MODID + ".distillery"));
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.itemSlots;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.itemSlots = items;
	}

	@Override
	protected AbstractContainerMenu createMenu(int i, Inventory player) {
		return new DistilleryMenu(i, player, this, this.dataAccess);
	}

	@Override
	public int getContainerSize() {
		return this.itemSlots.size();
	}

}
