package dev.entropy159.entropylib.network.toClient;

import dev.entropy159.entropylib.EntropyLib;
import dev.entropy159.entropylib.client.EntropyLibClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record InstantTeleportPacket(int id, Vec3 pos) implements CustomPacketPayload {
    public static final Type<InstantTeleportPacket> TYPE = new Type<>(EntropyLib.id("instant_tp"));
    public static final StreamCodec<ByteBuf, InstantTeleportPacket> STREAM_CODEC = StreamCodec.of((buf, val) -> {
        ByteBufCodecs.INT.encode(buf, val.id);
        ByteBufCodecs.DOUBLE.encode(buf, val.pos.x);
        ByteBufCodecs.DOUBLE.encode(buf, val.pos.y);
        ByteBufCodecs.DOUBLE.encode(buf, val.pos.z);
    }, buf -> new InstantTeleportPacket(ByteBufCodecs.INT.decode(buf), new Vec3(ByteBufCodecs.DOUBLE.decode(buf), ByteBufCodecs.DOUBLE.decode(buf), ByteBufCodecs.DOUBLE.decode(buf))));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        EntropyLibClient.UNLERP_ENTITIES.put(id, pos);
    }
}
