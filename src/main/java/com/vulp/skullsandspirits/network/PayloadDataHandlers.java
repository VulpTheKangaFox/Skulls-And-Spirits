package com.vulp.skullsandspirits.network;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import com.vulp.skullsandspirits.effect.MulesMightEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PayloadDataHandlers {

    public static void handleServerDoubleJump(final DoubleJumpPacketPayload data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (MulesMightEffect.canDoubleJump(player)) {
                MulesMightEffect.doDoubleJump(player);
            }
        }).exceptionally(e -> {
            context.disconnect(Component.translatable(SkullsAndSpirits.MODID + ".networking.failed", e.getMessage()));
            return null;
        });
    }

}
