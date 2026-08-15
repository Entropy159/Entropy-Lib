package dev.entropy159.entropylib.network.toClient;

import dev.entropy159.entropylib.EntropyLib;
import dev.entropy159.entropylib.client.EntropyLibClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public record InvisPacket(int entityID, ResourceLocation effectID, boolean enabled,
                          boolean isFull) implements CustomPacketPayload {
    public static final Type<InvisPacket> TYPE = new Type<>(EntropyLib.id("invis"));
    public static final StreamCodec<ByteBuf, InvisPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT, InvisPacket::entityID, ResourceLocation.STREAM_CODEC, InvisPacket::effectID, ByteBufCodecs.BOOL, InvisPacket::enabled, ByteBufCodecs.BOOL, InvisPacket::isFull, InvisPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        var map = EntropyLibClient.INVIS_MAP.computeIfAbsent(entityID, id -> new HashMap<>());
        if (enabled) {
            map.put(effectID, isFull);
        } else {
            map.remove(effectID);
        }
    }
}
