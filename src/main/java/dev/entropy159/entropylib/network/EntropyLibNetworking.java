package dev.entropy159.entropylib.network;

import dev.entropy159.entropylib.network.toClient.ClearInvisPacket;
import dev.entropy159.entropylib.network.toClient.InstantTeleportPacket;
import dev.entropy159.entropylib.network.toClient.InvisPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber
public class EntropyLibNetworking {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        registrar.playToClient(InvisPacket.TYPE, InvisPacket.STREAM_CODEC, InvisPacket::handle);
        registrar.playToClient(ClearInvisPacket.TYPE, ClearInvisPacket.STREAM_CODEC, ClearInvisPacket::handle);
        registrar.playToClient(InstantTeleportPacket.TYPE, InstantTeleportPacket.STREAM_CODEC, InstantTeleportPacket::handle);
    }
}
