package com.vulp.skullsandspirits.util;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DeathInfoHolder {

    public enum GraveStage {
        WAITING, // Data stored but grave not placed yet.
        PLACED, // Grave placed.
        COMPLETE, // Grave lifecycle completed.
        ERROR // Couldn't place grave. (Items will just disperse onto the ground as normal. Pass along an error message and ensure item drops are made invincible for a while to compensate.)
    }

    public enum ErrorReason {
        FAILED_PLACEMENT,
        NO_BLOCK_ENTITY
    }

    private final UUID uuid;
    private final BlockPos deathPos;
    private final LocalDateTime deathTime;
    private GraveStage graveStage;
    private final NonNullList<ItemStack>[] inventories; // 0 = main inventory | 1 = armor | 2 = offhand | 3 = misc
    private int experience;


    public DeathInfoHolder(ServerPlayer player) {
        this(player.getUUID(), player.getOnPos().above(), LocalDateTime.now(), GraveStage.WAITING, convertInventories(player), 0 );
    }

    public DeathInfoHolder(UUID uuid, BlockPos deathPos, LocalDateTime deathTime, GraveStage graveStage, NonNullList<ItemStack>[] inventories, int experience) {
        this.uuid = uuid;
        this.deathPos = deathPos;
        this.deathTime = deathTime;
        this.graveStage = graveStage;
        this.inventories = inventories;
        this.experience = experience;
    }

    public LocalDateTime getDeathTime() {
        return this.deathTime;
    }

    public UUID getUUID() {
        return uuid;
    }

    public BlockPos getDeathPos() {
        return deathPos;
    }

    public NonNullList<ItemStack>[] getInventories() {
        return inventories;
    }

    public NonNullList<ItemStack> getCompiledInventories() {
        NonNullList<ItemStack> combinedList = NonNullList.create();
        for (NonNullList<ItemStack> itemList : this.inventories) {
            combinedList.addAll(itemList);
        }
        return combinedList;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int xp) {
        this.experience = xp;
    }

    public GraveStage getGraveStage() {
        return graveStage;
    }

    public void setGraveStage(GraveStage graveStage) {
        this.graveStage = graveStage;
    }

    private static NonNullList<ItemStack>[] convertInventories(ServerPlayer player) {
        Inventory inventory = player.getInventory();

        NonNullList<ItemStack> main = NonNullList.create();
        NonNullList<ItemStack> armor = NonNullList.create();
        NonNullList<ItemStack> offhand = NonNullList.create();
        NonNullList<ItemStack> misc = NonNullList.create();

        List<ItemStack> checklist = new LinkedList<>();
        checklist.addAll(inventory.items);
        checklist.addAll(inventory.armor);
        checklist.addAll(inventory.offhand);

        for (ItemStack stack : inventory.items) {
            if (!stack.isEmpty()) {
                main.add(stack);
                checklist.remove(stack);
            }
        }
        for (ItemStack stack : inventory.armor) {
            if (!stack.isEmpty()) {
                armor.add(stack);
                checklist.remove(stack);
            }
        }
        for (ItemStack stack : inventory.offhand) {
            if (!stack.isEmpty()) {
                offhand.add(stack);
                checklist.remove(stack);
            }
        }

        checklist.stream().filter(stack -> !stack.isEmpty()).forEach(misc::add);

        return new NonNullList[]{main, armor, offhand, misc};
    }

    public void processDrops(Collection<ItemEntity> itemEntities) {
        LinkedHashSet<ItemStack> droppedItems = itemEntities.stream()
                .filter(Objects::nonNull)
                .map(ItemEntity::getItem)
                .filter(itemStack -> !itemStack.isEmpty())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // Process only the first 3 inventories
        for (int i = 0; i < 3 && i < inventories.length; i++) {
            NonNullList<ItemStack> currentInventory = inventories[i];
            filterInventoryItems(droppedItems, currentInventory);
        }

        // Any remaining drops that are not matched to inventory are added to misc items.
        inventories[3].addAll(droppedItems);
    }

    private void filterInventoryItems(LinkedHashSet<ItemStack> droppedItems, NonNullList<ItemStack> currentInventory) {
        for (int i = 0; i < currentInventory.size(); i++) {
            ItemStack itemStack = currentInventory.get(i);
            if (itemStack.isEmpty()) {
                continue;
            }

            if (droppedItems.contains(itemStack)) {
                droppedItems.remove(itemStack);
            } else {
                currentInventory.set(i, ItemStack.EMPTY);
            }
        }
    }

    public CompoundTag toNBT(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();

        tag.putUUID("uuid", this.uuid);
        tag.putInt("posX", this.deathPos.getX());
        tag.putInt("posY", this.deathPos.getY());
        tag.putInt("posZ", this.deathPos.getZ());
        tag.putString("deathTime", this.deathTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        tag.putString("graveStage", this.graveStage.name());

        // Store inventories (main, armor, offhand, etc.)
        ListTag inventoriesTag = new ListTag();
        for (NonNullList<ItemStack> inventory : this.inventories) {
            ListTag inventoryTag = new ListTag();
            for (ItemStack stack : inventory) {
                if (!stack.isEmpty()) {
                    CompoundTag itemTag = new CompoundTag();
                    inventoryTag.add(stack.save(registries, itemTag));
                }
            }
            inventoriesTag.add(inventoryTag);
        }
        tag.put("inventories", inventoriesTag);  // Store all inventories under "inventories"
        tag.putInt("experience", this.experience);

        return tag;
    }

    public static DeathInfoHolder fromNBT(CompoundTag tag, HolderLookup.Provider registries) {
        UUID uuid = tag.getUUID("uuid");
        BlockPos deathPos = new BlockPos(tag.getInt("posX"), tag.getInt("posY"), tag.getInt("posZ"));
        LocalDateTime deathTime = LocalDateTime.parse(tag.getString("deathTime"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        GraveStage graveStage = GraveStage.valueOf(tag.getString("graveStage"));

        ListTag inventoriesTag = tag.getList("inventories", Tag.TAG_LIST);
        NonNullList<ItemStack>[] inventories = new NonNullList[inventoriesTag.size()];
        for (int i = 0; i < inventoriesTag.size(); i++) {
            ListTag inventoryTag = inventoriesTag.getList(i);  // Get each inventory as a list of item tags
            NonNullList<ItemStack> inventory = NonNullList.withSize(inventoryTag.size(), ItemStack.EMPTY);
            for (int j = 0; j < inventoryTag.size(); j++) {
                CompoundTag itemTag = inventoryTag.getCompound(j);
                inventory.set(j, ItemStack.parseOptional(registries, itemTag));
            }
            inventories[i] = inventory;
        }

        int experience = tag.getInt("experience");

        return new DeathInfoHolder(uuid, deathPos, deathTime, graveStage, inventories, experience);
    }


    public void handleError(ServerPlayer player, Collection<ItemEntity> drops, ErrorReason errorReason) {
        for (ItemEntity drop : drops) {
            drop.setUnlimitedLifetime();
            drop.setInvulnerable(true);
        }
        ExperienceOrb.award(player.serverLevel(), this.deathPos.getCenter(), this.experience);
        MutableComponent message = Component.empty();
        switch (errorReason) {
            case FAILED_PLACEMENT -> message = Component.translatable("message." + SkullsAndSpirits.MODID + ".graveFailedPlacement");
            case NO_BLOCK_ENTITY -> message = Component.translatable("message." + SkullsAndSpirits.MODID + ".graveFailedBlockEntity");
        }
        player.displayClientMessage(message, false);
    }

}
