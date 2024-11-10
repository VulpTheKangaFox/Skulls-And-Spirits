package com.vulp.skullsandspirits.datagen;

import com.vulp.skullsandspirits.block.BlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class SASBlockLootSubProvider extends BlockLootSubProvider {

    public SASBlockLootSubProvider(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BlockRegistry.BLOCKS.getEntries()
                .stream()
                .map(e -> (Block) e.value())
                .toList();
    }

    @Override
    protected void generate() {
        add(BlockRegistry.GRAVESTONE.get(), noDrop());
        dropSelf(BlockRegistry.SHODDY_KEG.get());
        dropSelf(BlockRegistry.DRAINING_BASIN.get());
    }
}