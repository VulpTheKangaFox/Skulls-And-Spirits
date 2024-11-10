package com.vulp.skullsandspirits.effect;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;

import java.util.Set;

public class BrewSicknessEffect extends MobEffect {

    protected BrewSicknessEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplifier) {
        return amplifier > 1 && tickCount % 10 == 0;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        MobEffectInstance effect = entity.getEffect(EffectRegistry.BREW_SICKNESS);
        if (effect != null) {
            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, effect.getDuration(), amplifier - 2));
        }
        return true;
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {
        // This shouldn't be curable by default.
    }
}
