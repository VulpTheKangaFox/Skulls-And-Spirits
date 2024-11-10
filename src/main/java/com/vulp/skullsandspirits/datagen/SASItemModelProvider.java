package com.vulp.skullsandspirits.datagen;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.block.BlockRegistry;
import com.vulp.skullsandspirits.item.ItemRegistry;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class SASItemModelProvider extends ItemModelProvider {

    public SASItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, SkullsAndSpirits.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent("shoddy_keg", modLoc("block/shoddy_keg"));
        withExistingParent("draining_basin", modLoc("block/draining_basin"));
        basicItem(ItemRegistry.MUG.get());
        basicItem(ItemRegistry.GRAVEKEEPERS_BREW.get());
        basicItem(ItemRegistry.BLOODWINE.get());
        basicItem(ItemRegistry.ROTTEN_RUM.get());
        basicItem(ItemRegistry.INFERNAL_MULE.get());
        basicItem(ItemRegistry.JERKY.get());
    }
}