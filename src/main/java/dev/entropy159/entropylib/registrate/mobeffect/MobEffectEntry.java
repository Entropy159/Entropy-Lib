package dev.entropy159.entropylib.registrate.mobeffect;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MobEffectEntry<T extends MobEffect> extends RegistryEntry<MobEffect, T> {
    public MobEffectEntry(AbstractRegistrate<?> owner, DeferredHolder<MobEffect, T> key) {
        super(owner, key);
    }
}
