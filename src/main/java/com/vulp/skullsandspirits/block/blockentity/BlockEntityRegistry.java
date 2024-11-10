package com.vulp.skullsandspirits.block.blockentity;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.block.BlockRegistry;
import com.vulp.skullsandspirits.block.GravestoneBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockEntityRegistry {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SkullsAndSpirits.MODID);

    public static final Supplier<BlockEntityType<KegBlockEntity>> SHODDY_KEG = BLOCK_ENTITY_TYPES.register("shoddy_keg", () -> BlockEntityType.Builder.of(KegBlockEntity::new, BlockRegistry.SHODDY_KEG.get()).build(null));
    public static final Supplier<BlockEntityType<DrainingBasinBlockEntity>> DRAINING_BASIN = BLOCK_ENTITY_TYPES.register("draining_basin", () -> BlockEntityType.Builder.of(DrainingBasinBlockEntity::new, BlockRegistry.DRAINING_BASIN.get()).build(null));
    public static final Supplier<BlockEntityType<GravestoneBlockEntity>> GRAVESTONE = BLOCK_ENTITY_TYPES.register("gravestone", () -> BlockEntityType.Builder.of(GravestoneBlockEntity::new, BlockRegistry.GRAVESTONE.get()).build(null));

}
