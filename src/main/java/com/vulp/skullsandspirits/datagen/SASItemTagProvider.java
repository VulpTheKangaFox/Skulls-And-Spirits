package com.vulp.skullsandspirits.datagen;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.tag.TagRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class SASItemTagProvider extends ItemTagsProvider {

    public SASItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, SkullsAndSpirits.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(TagRegistry.ItemTags.RAW_MEAT).add(Items.PORKCHOP, Items.CHICKEN, Items.BEEF, Items.MUTTON, Items.RABBIT, Items.SALMON, Items.COD, Items.TROPICAL_FISH);
    }
}