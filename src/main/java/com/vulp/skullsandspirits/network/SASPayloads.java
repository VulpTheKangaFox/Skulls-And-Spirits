package com.vulp.skullsandspirits.network;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class SASPayloads {

    @SubscribeEvent
    public void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(DoubleJumpPacketPayload.TYPE, DoubleJumpPacketPayload.STREAM_CODEC, PayloadDataHandlers::handleServerDoubleJump);
    }

}
