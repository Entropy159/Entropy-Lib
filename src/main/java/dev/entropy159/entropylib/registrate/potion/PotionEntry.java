package dev.entropy159.entropylib.registrate.potion;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;

public class PotionEntry<T extends Potion> extends RegistryEntry<Potion, T> {
    public PotionEntry(AbstractRegistrate<?> owner, DeferredHolder<Potion, T> key) {
        super(owner, key);
    }
}
