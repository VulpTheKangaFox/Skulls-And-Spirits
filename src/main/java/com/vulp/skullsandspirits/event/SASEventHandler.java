package com.vulp.skullsandspirits.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.vulp.skullsandspirits.block.BlockRegistry;
import com.vulp.skullsandspirits.block.GravestoneBlock;
import com.vulp.skullsandspirits.block.blockentity.GravestoneBlockEntity;
import com.vulp.skullsandspirits.effect.EffectRegistry;
import com.vulp.skullsandspirits.effect.MulesMightEffect;
import com.vulp.skullsandspirits.util.DeathInfoHolder;
import com.vulp.skullsandspirits.world.DeathSavedData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.Collection;

public class SASEventHandler {

    @SubscribeEvent
    public void onAttackEntity(LivingDamageEvent.Post event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player && player.hasEffect(EffectRegistry.BLOODTHIRSTY) && player.getHealth() < player.getMaxHealth()) {
            player.heal(event.getNewDamage() * 0.3f);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) { // Handles the death info so it loads when our world loads.
        MinecraftServer server = event.getServer();
        DeathSavedData.getOrCreate(server);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel) || level.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).get()) {
            return;
        }
        if (entity instanceof ServerPlayer player && player.hasEffect(EffectRegistry.GRAVEMARKED)) {
            DeathSavedData deathSavedData = DeathSavedData.getOrCreate(serverLevel.getServer());
            deathSavedData.addDeath(new DeathInfoHolder(player));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.hasEffect(EffectRegistry.GRAVEMARKED)) {
            DeathInfoHolder awaitingDeath = DeathSavedData.findLatestAwaitingDeathForPlayer(player.getUUID());
            if (awaitingDeath != null) {
                ServerLevel level = player.serverLevel();
                Collection<ItemEntity> drops = event.getDrops();
                awaitingDeath.processDrops(drops);
                BlockPos gravePos = GravestoneBlockEntity.calculatePlacement(level, awaitingDeath.getDeathPos());
                if (gravePos == null) {
                    awaitingDeath.setGraveStage(DeathInfoHolder.GraveStage.ERROR);
                    awaitingDeath.handleError(player, drops, DeathInfoHolder.ErrorReason.FAILED_PLACEMENT);
                } else {
                    level.setBlockAndUpdate(gravePos, GravestoneBlock.setRandomFacing(BlockRegistry.GRAVESTONE.get().defaultBlockState()).setValue(GravestoneBlock.FLOATING, Block.canSupportCenter(level, gravePos.below(), Direction.UP)));
                    if (level.getBlockEntity(gravePos) instanceof GravestoneBlockEntity blockEntity) {
                        blockEntity.setDeathInfoHolder(awaitingDeath);
                        drops.clear();
                        awaitingDeath.setGraveStage(DeathInfoHolder.GraveStage.PLACED);
                    } else {
                        awaitingDeath.setGraveStage(DeathInfoHolder.GraveStage.ERROR);
                        awaitingDeath.handleError(player, drops, DeathInfoHolder.ErrorReason.NO_BLOCK_ENTITY);
                    }
                }
                DeathSavedData.getOrCreate(level.getServer()).setDirty();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.hasEffect(EffectRegistry.GRAVEMARKED)) {
            DeathInfoHolder awaitingDeath = DeathSavedData.findLatestAwaitingDeathForPlayer(player.getUUID());
            if (awaitingDeath != null) {
                awaitingDeath.setExperience(event.getDroppedExperience());
                event.setCanceled(true);
            }
        }
    }

}