package com.vulp.skullsandspirits.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

// TODO: Make sure the double jump does not trigger while swimming, flying, etc.
public class MulesMightEffect extends SASEffect {

    public static String DOUBLE_JUMP_TAG = "SASCanDoubleJump";

    protected MulesMightEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    public static boolean canDoubleJump(Player player) {
        // Ensure the player is airborne, has the effect, and has permission to double jump
        return !player.onGround() && player.hasEffect(EffectRegistry.MULES_MIGHT) && player.getPersistentData().getBoolean(DOUBLE_JUMP_TAG);
    }

    // TODO: Make effect scale with amplifier, an additional air-jump added with each tier.
    public static void doDoubleJump(Player player) {
        player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z); // Reset Y velocity so that there's no variation in the secondary jump distance.
        player.setDeltaMovement(player.getDeltaMovement().add(0, 0.5, 0));
        player.resetFallDistance();
        // Clear the tag to prevent further double jumps until the player returns to the ground.
        player.getPersistentData().putBoolean(DOUBLE_JUMP_TAG, false);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        if (livingEntity instanceof Player player) {
            // Reset double jump availability when the player is on the ground
            if (player.onGround()) {
                player.getPersistentData().putBoolean(DOUBLE_JUMP_TAG, true);
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}