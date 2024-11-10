package com.vulp.skullsandspirits.datagen;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.block.BlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class SASBlockTagProvider extends BlockTagsProvider {

    public SASBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SkullsAndSpirits.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_AXE).add(
                BlockRegistry.SHODDY_KEG.get(),
                BlockRegistry.DRAINING_BASIN.get()
        );
    }

}
