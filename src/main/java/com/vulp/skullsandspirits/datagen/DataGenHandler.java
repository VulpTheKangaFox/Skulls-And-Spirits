package com.vulp.skullsandspirits.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DataGenHandler {

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new SASRecipeProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new SASBlockStateProvider(output, existingFileHelper));
        generator.addProvider(event.includeServer(), new SASItemModelProvider(output, existingFileHelper));
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(), List.of(new LootTableProvider.SubProviderEntry(SASBlockLootSubProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
        BlockTagsProvider blockTagsProvider = new SASBlockTagProvider(output, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new SASItemTagProvider(output, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new SASDataMapProvider(output, lookupProvider));
    }

}