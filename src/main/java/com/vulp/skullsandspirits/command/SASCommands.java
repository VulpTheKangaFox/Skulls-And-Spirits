package com.vulp.skullsandspirits.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.vulp.skullsandspirits.block.GravestoneBlock;
import com.vulp.skullsandspirits.util.DeathInfoHolder;
import com.vulp.skullsandspirits.util.SASUtils;
import com.vulp.skullsandspirits.world.DeathSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SASCommands {

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("sas")
                        .then(
                                Commands.literal("list")
                                        .executes(ctx -> SASCommands.listDeaths(ctx.getSource(), 1))
                                        .then(
                                                Commands.argument("page", IntegerArgumentType.integer(1))
                                                        .executes(ctx -> SASCommands.listDeaths(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "page")))
                                        )
                        )
                        .then(
                                Commands.literal("recover").requires(source -> source.hasPermission(2))
                                        .then(
                                                Commands.argument("target", EntityArgument.player())
                                                        .then(
                                                                Commands.argument("id", IntegerArgumentType.integer(1))
                                                                        .executes(ctx -> SASCommands.recoverDeath(ctx.getSource(), EntityArgument.getPlayer(ctx, "target"), IntegerArgumentType.getInteger(ctx, "id")))
                                                        )
                                        )
                        )
                        .then(
                                Commands.literal("prune").requires(source -> source.hasPermission(2))
                                        .then(
                                                Commands.argument("number", IntegerArgumentType.integer(1))
                                                        .executes(ctx -> SASCommands.pruneDeaths(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "number")))
                                        )
                        )
                        .then(
                                Commands.literal("clear").requires(source -> source.hasPermission(2))
                                        .executes(ctx -> SASCommands.clearDeaths(ctx.getSource()))
                        )
        );
    }

    private static int listDeaths(CommandSourceStack source, int page) {
        MinecraftServer server = source.getServer();
        DeathSavedData deathData = DeathSavedData.getOrCreate(server);
        List<DeathInfoHolder> deaths = deathData.findDeaths(d -> true);
        deaths.sort(Comparator.comparing(DeathInfoHolder::getDeathTime).reversed()); // Sort by recent

        int pageSize = 8;
        int startIndex = (page - 1) * pageSize;
        int totalPages = (int) Math.ceil((double) deaths.size() / pageSize);

        if (startIndex >= deaths.size()) {
            source.sendFailure(Component.literal("Page " + page + " does not exist."));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Listing recorded Gravemarked deaths! (Page " + page + " of " + totalPages + "):"), false);

        for (int i = startIndex; i < Math.min(startIndex + pageSize, deaths.size()); i++) {
            DeathInfoHolder death = deaths.get(i);
            int id = i + 1;
            source.sendSuccess(() -> SASUtils.createFormattedDeathInfoText(id, death, server.overworld(), source.getPlayer()), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int recoverDeath(CommandSourceStack source, ServerPlayer player, int id) {
        DeathSavedData deathData = DeathSavedData.getOrCreate(source.getServer());
        List<DeathInfoHolder> deaths = new ArrayList<>(deathData.getDeaths());
        deaths.sort(Comparator.comparing(DeathInfoHolder::getDeathTime).reversed());

        if (id < 1 || id > deaths.size()) {
            source.sendFailure(Component.literal("Invalid death number."));
            return 0;
        }

        DeathInfoHolder deathToRecover = deaths.get(id - 1);
        GravestoneBlock.autoGrantItemsOnGraveBreak(player, deathToRecover, player.getOnPos().above());
        deathData.setDirty();

        source.sendSuccess(() -> Component.literal("Recovered death #" + id + " for "), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int pruneDeaths(CommandSourceStack source, int count) {
        DeathSavedData deathData = DeathSavedData.getOrCreate(source.getServer());
        List<DeathInfoHolder> deaths = new ArrayList<>(deathData.getDeaths());
        deaths.sort(Comparator.comparing(DeathInfoHolder::getDeathTime)); // Oldest first!

        if (count > deaths.size()) {
            source.sendFailure(Component.literal("Cannot prune " + count + " deaths! Only " + deaths.size() + " available."));
            return 0;
        }

        for (int i = 0; i < count; i++) {
            deathData.removeDeath(deaths.get(i));
        }

        source.sendSuccess(() -> Component.literal("Pruned " + count + " deaths."), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int clearDeaths(CommandSourceStack source) {
        DeathSavedData deathData = DeathSavedData.getOrCreate(source.getServer());
        deathData.clearDeaths();
        source.sendSuccess(() -> Component.literal("Cleared all recorded Gravemarked deaths!"), true);
        return Command.SINGLE_SUCCESS;
    }

}
