package com.vulp.skullsandspirits.tag;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class SASTagCache {

    private static Set<Item> RAW_MEAT_ITEMS = null;

    public static Set<Item> getRawMeatItems() { // Use this as a getter for our tag.
        if (RAW_MEAT_ITEMS == null) {
            RAW_MEAT_ITEMS = BuiltInRegistries.ITEM.getOrCreateTag(TagRegistry.ItemTags.RAW_MEAT).stream().map(Holder::value).collect(Collectors.toUnmodifiableSet());
        }
        return RAW_MEAT_ITEMS;
    }

    public static void invalidateCache() {
        RAW_MEAT_ITEMS = null;
    }

    @SubscribeEvent
    public void onTagsUpdated(TagsUpdatedEvent event) {
        SASTagCache.invalidateCache();
    }

}
