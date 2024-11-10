package com.vulp.skullsandspirits.util;

import com.mojang.authlib.GameProfile;
import com.vulp.skullsandspirits.SkullsAndSpirits;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class SASUtils {

    @Nullable
    public static String getPlayerNameFromUUID(ServerLevel serverLevel, UUID uuid, Player player) {
        GameProfileCache profileCache = serverLevel.getServer().getProfileCache();

        if (profileCache != null) {
            return profileCache.get(uuid)
                    .map(GameProfile::getName)
                    .orElseGet(() -> {
                        player.displayClientMessage(
                                Component.translatable("message." + SkullsAndSpirits.MODID + ".playerNameNotFound")
                                        .withStyle(ChatFormatting.RED),
                                false
                        );
                        return null;
                    });
        } else {
            player.displayClientMessage(
                    Component.translatable("message." + SkullsAndSpirits.MODID + ".gameProfileCacheNull")
                            .withStyle(ChatFormatting.RED),
                    false
            );
            return null;
        }
    }

    public static boolean isMouseInsideArea(int x, int y, int width, int height, double mouseX, double mouseY) {
        return x <= mouseX && mouseX < x + width && y <= mouseY && mouseY < y + height;
    }

    public static String ticksToFormattedTime(int ticks) {
        int totalSeconds = ticks / 20;

        if (totalSeconds >= 3600) {
            // Format as HH:MM:SS
            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            // Format as MM:SS
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public static String ticksToFormattedTimeCompact(int ticks, String separator) {
        int totalSeconds = ticks / 20;

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder formattedTime = new StringBuilder();

        if (hours > 0) {
            formattedTime.append(hours).append("h");
        }
        if (minutes > 0) {
            if (formattedTime.length() > 0) {
                formattedTime.append(separator);
            }
            formattedTime.append(minutes).append("m");
        }
        if (seconds > 0 || (hours == 0 && minutes == 0)) {
            if (formattedTime.length() > 0) {
                formattedTime.append(separator);
            }
            formattedTime.append(seconds).append("s");
        }

        return formattedTime.toString();
    }

    public static MutableComponent createFormattedDeathInfoText(int ID, DeathInfoHolder deathInfo, ServerLevel serverLevel, Player player) {
        String playerName = SASUtils.getPlayerNameFromUUID(serverLevel, deathInfo.getUUID(), player);

        MutableComponent formattedText = Component.empty()
                .append(Component.literal("ID: ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(String.valueOf(ID)).withStyle(ChatFormatting.LIGHT_PURPLE))
                .append(Component.literal(" | ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("Time: ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(deathInfo.getDeathTime().format(DateTimeFormatter.ofPattern("dd-MM-yy HH:mm"))).withStyle(ChatFormatting.YELLOW))
                .append(Component.literal(" | ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("Player: ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(playerName != null ? playerName : "Unknown").withStyle(ChatFormatting.BLUE))
                .append(Component.literal(" | ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal("Stage: ").withStyle(ChatFormatting.WHITE));

        ChatFormatting stageColor;
        switch (deathInfo.getGraveStage()) {
            case WAITING -> stageColor = ChatFormatting.GOLD;
            case PLACED -> stageColor = ChatFormatting.GREEN;
            case COMPLETE -> stageColor = ChatFormatting.DARK_GRAY;
            case ERROR -> stageColor = ChatFormatting.RED;
            default -> stageColor = ChatFormatting.GRAY;
        }
        formattedText.append(Component.literal(deathInfo.getGraveStage().name()).withStyle(stageColor));
        return formattedText;
    }


}
