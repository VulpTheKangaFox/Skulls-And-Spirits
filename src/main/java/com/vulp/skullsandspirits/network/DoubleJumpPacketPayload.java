package com.vulp.skullsandspirits.network;

import com.vulp.skullsandspirits.SkullsAndSpirits;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class DoubleJumpPacketPayload implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<DoubleJumpPacketPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SkullsAndSpirits.MODID, "double_jump"));

    public static final StreamCodec<ByteBuf, DoubleJumpPacketPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {},
            buf -> new DoubleJumpPacketPayload()
        );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}