package net.tropicraft.core.common.network.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.tropicraft.Tropicraft;
import net.tropicraft.core.common.encyclopedia.TropicalBookData;

import java.util.Map;
import java.util.Set;

public record ClientboundUpdateEncyclopediaPacket(Map<String, String> data) implements CustomPacketPayload {
    public static final Type<ClientboundUpdateEncyclopediaPacket> TYPE = new Type<>(Tropicraft.id("update_encyclopedia_data"));
    //private final List<EncyclopediaHolder> added
    public static final StreamCodec<ByteBuf, ClientboundUpdateEncyclopediaPacket> STREAM_CODEC = StreamCodec.composite(
            TropicalBookData.MAP_STREAM_CODEC, ClientboundUpdateEncyclopediaPacket::data,
            ClientboundUpdateEncyclopediaPacket::new
    );
    public static void handle(ClientboundUpdateEncyclopediaPacket packet, IPayloadContext ctx) {
        TropicalBookData data = Minecraft.getInstance().player.getData(TropicalBookData.ATTACHMENT);
        data.copyFrom(packet.data);
    }

    private void write(FriendlyByteBuf buf) {

    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
