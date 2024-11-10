package com.vulp.skullsandspirits;

import com.mojang.logging.LogUtils;
import com.vulp.skullsandspirits.block.BlockRegistry;
import com.vulp.skullsandspirits.block.blockentity.BlockEntityRegistry;
import com.vulp.skullsandspirits.command.SASCommands;
import com.vulp.skullsandspirits.crafting.RecipeRegistry;
import com.vulp.skullsandspirits.datagen.DataGenHandler;
import com.vulp.skullsandspirits.effect.EffectRegistry;
import com.vulp.skullsandspirits.event.SASClientEventHandler;
import com.vulp.skullsandspirits.event.SASEventHandler;
import com.vulp.skullsandspirits.fluid.FluidRegistry;
import com.vulp.skullsandspirits.fluid.FluidTypeRegistry;
import com.vulp.skullsandspirits.inventory.MenuRegistry;
import com.vulp.skullsandspirits.item.ItemRegistry;
import com.vulp.skullsandspirits.network.SASPayloads;
import com.vulp.skullsandspirits.screen.ScreenRegistry;
import com.vulp.skullsandspirits.tag.SASTagCache;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.function.Supplier;

@Mod(SkullsAndSpirits.MODID)
public class SkullsAndSpirits {

    public static final String MODID = "skullsandspirits";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final Supplier<CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("skullsandspirits", () -> CreativeModeTab.builder()
            .title(Component.translatable("item_group." + MODID))
            .icon(() -> new ItemStack(ItemRegistry.MUG.get()))
            .displayItems((params, output) -> {
                output.accept(BlockRegistry.GRAVESTONE.get());
                output.accept(BlockRegistry.SHODDY_KEG.get());
                output.accept(BlockRegistry.DRAINING_BASIN.get());
                output.accept(ItemRegistry.MUG.get());
                output.accept(ItemRegistry.GRAVEKEEPERS_BREW.get());
                output.accept(ItemRegistry.BLOODWINE.get());
                output.accept(ItemRegistry.ROTTEN_RUM.get());
                output.accept(ItemRegistry.INFERNAL_MULE.get());
                output.accept(ItemRegistry.BLOOD_BUCKET.get());
                output.accept(ItemRegistry.JERKY.get());
            }).build()
    );

    public SkullsAndSpirits(IEventBus modEventBus, ModContainer modContainer) {
        BlockRegistry.BLOCKS.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        BlockEntityRegistry.BLOCK_ENTITY_TYPES.register(modEventBus);
        MenuRegistry.MENUS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        RecipeRegistry.RECIPE_TYPES.register(modEventBus);
        RecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        EffectRegistry.EFFECTS.register(modEventBus);
        FluidRegistry.FLUIDS.register(modEventBus);
        FluidTypeRegistry.FLUID_TYPES.register(modEventBus);

        modEventBus.register(new ScreenRegistry());
        modEventBus.register(new DataGenHandler());
        modEventBus.register(new SASPayloads());
        modEventBus.register(new FluidTypeRegistry());
        NeoForge.EVENT_BUS.register(new SASCommands());
        NeoForge.EVENT_BUS.register(new SASEventHandler());
        NeoForge.EVENT_BUS.register(new SASTagCache());

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        modEventBus.addListener(this::clientSetup);
    }

    @SubscribeEvent
    private void clientSetup(final FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new SASClientEventHandler());
    }

}
