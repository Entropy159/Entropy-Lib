package dev.entropy159.entropylib.mixin;

import dev.entropy159.entropylib.mixininterfaces.ConfigValueAddon;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ModConfigSpec.ConfigValue.class)
public abstract class ConfigValueMixin<T> implements ConfigValueAddon<T> {
    @Shadow
    private @Nullable ModConfigSpec spec;
    @Unique
    private String entropylib$modID = null;

    @Override
    public String entropylib$getModID() {
        return entropylib$modID;
    }

    @Override
    public void entropylib$setModID(String modID) {
        entropylib$modID = modID;
    }

    @Override
    public @Nullable ModConfigSpec entropylib$getSpec() {
        return spec;
    }
}
