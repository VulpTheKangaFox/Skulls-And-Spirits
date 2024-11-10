package com.vulp.skullsandspirits.tag;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TagRegistry {

    public static class ItemTags {

        public static final TagKey<Item> RAW_MEAT = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "raw_meat"));

    }

    public static class BlockTags {

    }

}
