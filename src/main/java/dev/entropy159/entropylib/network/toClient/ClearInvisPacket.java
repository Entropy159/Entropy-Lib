package dev.entropy159.entropylib.network.toClient;

import dev.entropy159.entropylib.EntropyLib;
import dev.entropy159.entropylib.client.EntropyLibClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ClearInvisPacket(int entityID) implements CustomPacketPayload {
    public static final Type<ClearInvisPacket> TYPE = new Type<>(EntropyLib.id("clear_invis"));
    public static final StreamCodec<ByteBuf, ClearInvisPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, ClearInvisPacket::entityID, ClearInvisPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        EntropyLibClient.INVIS_MAP.remove(entityID);
    }
}
