package com.vulp.skullsandspirits.world;

import com.vulp.skullsandspirits.util.DeathInfoHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DeathSavedData extends SavedData {

    private static TreeSet<DeathInfoHolder> GRAVEMARK_DEATHS = new TreeSet<>(
            Comparator.comparing(DeathInfoHolder::getDeathTime) // Ensures that deaths are stored in chronological order.
    );

    public static DeathSavedData getOrCreate(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(DeathSavedData::new, DeathSavedData::load),
                "sas_gravemarked_data"
        );
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        // Sort the deaths based on timestamp before saving
        List<DeathInfoHolder> gravemarkDeaths = GRAVEMARK_DEATHS.stream().toList();
        for (int i = 0; i < gravemarkDeaths.size(); i++) {
            tag.put("gravemark_" + (i + 1), gravemarkDeaths.get(i).toNBT(registries));
        }
        return tag;
    }

    public static DeathSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        DeathSavedData data = new DeathSavedData();

        for (String key : tag.getAllKeys()) {
            if (key.startsWith("gravemark_")) {
                CompoundTag gravemarkTag = tag.getCompound(key);
                DeathInfoHolder deathInfo = DeathInfoHolder.fromNBT(gravemarkTag, registries);
                GRAVEMARK_DEATHS.add(deathInfo);  // TreeSet automatically orders them by death time I hope.
            }
        }

        return data;
    }

    @Nullable
    public static DeathInfoHolder findLatestAwaitingDeathForPlayer(UUID playerUUID) {
        NavigableSet<DeathInfoHolder> descendingDeaths = GRAVEMARK_DEATHS.descendingSet();
        for (DeathInfoHolder deathInfo : descendingDeaths) {
            if (deathInfo.getUUID().equals(playerUUID) && deathInfo.getGraveStage() == DeathInfoHolder.GraveStage.WAITING) {
                return deathInfo;
            }
        }
        return null;
    }

    public List<DeathInfoHolder> findDeaths(Predicate<DeathInfoHolder> filter) {
        return GRAVEMARK_DEATHS.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public boolean addDeath(DeathInfoHolder deathInfo) {
        boolean added = GRAVEMARK_DEATHS.add(deathInfo);
        if (added) {
            this.setDirty();
        }
        return added;
    }

    public boolean removeDeath(DeathInfoHolder deathInfo) {
        boolean removed = GRAVEMARK_DEATHS.remove(deathInfo);
        if (removed) {
            this.setDirty();
        }
        return removed;
    }

    public void clearDeaths() {
        GRAVEMARK_DEATHS.clear();
        this.setDirty();
    }

    public Set<DeathInfoHolder> getDeaths() {
        return Collections.unmodifiableSet(GRAVEMARK_DEATHS);
    }

}