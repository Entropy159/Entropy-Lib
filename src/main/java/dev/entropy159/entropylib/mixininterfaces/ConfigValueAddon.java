package dev.entropy159.entropylib.mixininterfaces;

import net.neoforged.neoforge.common.ModConfigSpec;

public interface ConfigValueAddon<T> {
    default String entropylib$getModID() {
        return null;
    }

    default void entropylib$setModID(String modID) {

    }

    default ModConfigSpec entropylib$getSpec() {
        return null;
    }
}
