package com.vulp.skullsandspirits.block;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.fluid.FluidRegistry;
import com.vulp.skullsandspirits.item.ItemRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BlockRegistry {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SkullsAndSpirits.MODID);

    private static final Map<DeferredHolder<Block, Block>, DeferredItem<BlockItem>> BLOCK_ITEM_MAP = new HashMap<>();

    public static final DeferredHolder<Block, Block> SHODDY_KEG = registerBlockWithSimpleItem("shoddy_keg", () -> new KegBlock(BlockBehaviour.Properties.of().strength(1.5F).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredHolder<Block, Block> DRAINING_BASIN = registerBlockWithSimpleItem("draining_basin", () -> new DrainingBasinBlock(BlockBehaviour.Properties.of().strength(1.5F).mapColor(MapColor.TERRACOTTA_RED)));
    public static final DeferredHolder<Block, Block> GRAVESTONE = registerBlockWithSimpleItem("gravestone", () -> new GravestoneBlock(BlockBehaviour.Properties.of().strength(0.75F).mapColor(MapColor.METAL)));

    public static final DeferredHolder<Block, Block> BLOOD = registerBlockWithSimpleItem("blood", () -> new LiquidBlock(FluidRegistry.BLOOD.get(), BlockBehaviour.Properties.of().mapColor(MapColor.FIRE).replaceable().noCollission().randomTicks().strength(100.0F).pushReaction(PushReaction.DESTROY).noLootTable().liquid().sound(SoundType.EMPTY)));

    // Registers a BlockItem with the registered Block.
    private static DeferredBlock<Block> registerBlockWithSimpleItem(String name, Supplier<Block> blockSupplier) {
        DeferredBlock<Block> block = BLOCKS.register(name, blockSupplier);
        BLOCK_ITEM_MAP.put(block, ItemRegistry.ITEMS.registerSimpleBlockItem(name, block));
        return block;
    }

    public static BlockItem getItemForBlock(DeferredHolder<Block, Block> block) {
        return BLOCK_ITEM_MAP.get(block).get();
    }

}
