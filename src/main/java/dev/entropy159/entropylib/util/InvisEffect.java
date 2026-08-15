package dev.entropy159.entropylib.util;

import dev.entropy159.entropylib.network.toClient.InvisPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;

public interface InvisEffect {
    boolean isFull();

    ResourceLocation id();

    default void update(Entity entity, boolean enabled) {
        PacketDistributor.sendToAllPlayers(new InvisPacket(entity.getId(), id(), enabled, isFull()));
    }
}
