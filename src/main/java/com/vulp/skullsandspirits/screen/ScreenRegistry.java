package com.vulp.skullsandspirits.screen;

import com.vulp.skullsandspirits.inventory.DrainingBasinMenu;
import com.vulp.skullsandspirits.inventory.MenuRegistry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ScreenRegistry {

    @SubscribeEvent
    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.KEG.get(), KegScreen::new);
        event.register(MenuRegistry.DRAINING_BASIN.get(), DrainingBasinScreen::new);
    }

}
