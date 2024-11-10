package com.vulp.skullsandspirits.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

// We needed to create a child effect because MobEffect isn't public.
public class SASEffect extends MobEffect {

    protected SASEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

}
