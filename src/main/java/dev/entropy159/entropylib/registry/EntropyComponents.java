package dev.entropy159.entropylib.registry;

import com.mojang.serialization.Codec;
import dev.entropy159.entropylib.EntropyLib;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EntropyComponents {
    public static final DeferredRegister.DataComponents REGISTRY = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, EntropyLib.MODID);

    public static final Supplier<DataComponentType<Boolean>> INFINITE = REGISTRY.registerComponentType("infinite", builder -> builder.networkSynchronized(ByteBufCodecs.BOOL).persistent(Codec.BOOL));

    public static void init(IEventBus bus) {
        REGISTRY.register(bus);
    }
}
