package com.vulp.skullsandspirits.effect;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EffectRegistry {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, SkullsAndSpirits.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> BREW_SICKNESS = EFFECTS.register("brew_sickness", () -> new BrewSicknessEffect(MobEffectCategory.HARMFUL, 0x202C23));
    public static final DeferredHolder<MobEffect, MobEffect> GRAVEMARKED = EFFECTS.register("gravemarked", () -> new SASEffect(MobEffectCategory.BENEFICIAL, 0xE5462D));
    public static final DeferredHolder<MobEffect, MobEffect> BLOODTHIRSTY = EFFECTS.register("bloodthirsty", () -> new SASEffect(MobEffectCategory.BENEFICIAL, 0xF71C1A));
    public static final DeferredHolder<MobEffect, MobEffect> ROTTEN_BRAWN = EFFECTS.register("rotten_brawn", () -> new RottenBrawnEffect(MobEffectCategory.BENEFICIAL, 0x5BB71A)
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "effect.rotten_brawn"), 3.0, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.MAX_HEALTH, ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "effect.rotten_brawn"), 10.0, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.SCALE, ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "effect.rotten_brawn"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> MULES_MIGHT = EFFECTS.register("mules_might", () -> new MulesMightEffect(MobEffectCategory.BENEFICIAL, 0xFFB705)
            .addAttributeModifier(Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "effect.mules_might"), 0.1, AttributeModifier.Operation.ADD_VALUE)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "effect.mules_might"), 0.2F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
            .addAttributeModifier(Attributes.SAFE_FALL_DISTANCE, ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "effect.mules_might"), 3F, AttributeModifier.Operation.ADD_VALUE));

}
