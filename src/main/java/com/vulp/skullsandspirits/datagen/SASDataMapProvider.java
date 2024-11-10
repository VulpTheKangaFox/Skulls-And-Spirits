package com.vulp.skullsandspirits.datagen;

import com.vulp.skullsandspirits.block.BlockRegistry;
import com.vulp.skullsandspirits.item.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

public class SASDataMapProvider extends DataMapProvider {

    protected SASDataMapProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(packOutput, lookupProvider);
    }

    @Override
    protected void gather() {
        this.builder(NeoForgeDataMaps.FURNACE_FUELS)
                .add(BlockRegistry.SHODDY_KEG.getId(), new FurnaceFuel(400), false)
                .add(BlockRegistry.DRAINING_BASIN.getId(), new FurnaceFuel(400), false)
                .add(ItemRegistry.MUG.getId(), new FurnaceFuel(200), false);
    }
}
