package net.tropicraft.core.common.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.tropicraft.Tropicraft;
import net.tropicraft.core.common.encyclopedia.TropicalBookData;

public record ClientboundUpdateEncyclopediaPacket(TropicalBookData data) implements CustomPacketPayload {
    public static final Type<ClientboundUpdateEncyclopediaPacket> TYPE = new Type<>(Tropicraft.id("update_encyclopedia_data"));
    public static final StreamCodec<ByteBuf, ClientboundUpdateEncyclopediaPacket> STREAM_CODEC = StreamCodec.composite(
            TropicalBookData.STREAM_CODEC, ClientboundUpdateEncyclopediaPacket::data,
            ClientboundUpdateEncyclopediaPacket::new
    );
    public static void handle(ClientboundUpdateEncyclopediaPacket packet, IPayloadContext ctx) {
        TropicalBookData data = Minecraft.getInstance().player.getData(TropicalBookData.ATTACHMENT);
        data.copyFrom(packet.data);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
