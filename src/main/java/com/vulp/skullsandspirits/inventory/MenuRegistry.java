package com.vulp.skullsandspirits.inventory;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MenuRegistry {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, SkullsAndSpirits.MODID);

    public static final Supplier<MenuType<KegMenu>> KEG = MENUS.register("shoddy_keg_menu", () -> new MenuType<>(KegMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<DrainingBasinMenu>> DRAINING_BASIN = MENUS.register("draining_basin_menu", () -> new MenuType<>(DrainingBasinMenu::new, FeatureFlags.DEFAULT_FLAGS));

}
