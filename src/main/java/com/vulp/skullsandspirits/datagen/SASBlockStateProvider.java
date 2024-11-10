package com.vulp.skullsandspirits.datagen;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.block.BlockRegistry;
import com.vulp.skullsandspirits.block.KegBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class SASBlockStateProvider extends BlockStateProvider {

    public SASBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SkullsAndSpirits.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        getVariantBuilder(BlockRegistry.SHODDY_KEG.get()).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(!state.getValue(KegBlock.STACKED) ? models().getExistingFile(modLoc("block/shoddy_keg")) : models().getExistingFile(modLoc("block/shoddy_keg_stacked")))
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .build());

        getVariantBuilder(BlockRegistry.GRAVESTONE.get()).forAllStates(state -> ConfiguredModel.builder()
                .modelFile(models().getExistingFile(modLoc("block/gravestone")))
                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                .build());

        simpleBlock(BlockRegistry.DRAINING_BASIN.get(), models().getExistingFile(modLoc("block/draining_basin")));
    }

}
