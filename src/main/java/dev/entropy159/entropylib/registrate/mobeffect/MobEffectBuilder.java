package dev.entropy159.entropylib.registrate.mobeffect;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import dev.entropy159.entropylib.registrate.EntropyRegistrate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class MobEffectBuilder<T extends MobEffect, P> extends AbstractBuilder<MobEffect, T, P, MobEffectBuilder<T, P>> {
    private final NonNullFunction<ResourceLocation, T> factory;
    private final ResourceLocation id;

    public MobEffectBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<ResourceLocation, T> factory) {
        super(owner, parent, name, callback, BuiltInRegistries.MOB_EFFECT.key());
        this.factory = factory;
        this.id = ResourceLocation.fromNamespaceAndPath(owner.getModid(), name);
    }

    public static <T extends MobEffect, P> MobEffectBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<ResourceLocation, T> factory) {
        return new MobEffectBuilder<>(owner, parent, name, callback, factory).defaultLang();
    }

    @Override
    protected @NotNull T createEntry() {
        return factory.apply(id);
    }

    @Override
    public @NotNull EntropyRegistrate getOwner() {
        return (EntropyRegistrate) super.getOwner();
    }

    @Override
    protected @NotNull RegistryEntry<MobEffect, T> createEntryWrapper(@NotNull DeferredHolder<MobEffect, T> delegate) {
        return new MobEffectEntry<>(getOwner(), delegate);
    }

    @Override
    public @NotNull MobEffectEntry<T> register() {
        return (MobEffectEntry<T>) super.register();
    }

    public MobEffectBuilder<T, P> defaultLang() {
        return lang(t -> "effect." + id.toLanguageKey());
    }

    public MobEffectBuilder<T, P> lang(String name) {
        return lang(t -> "effect." + id.toLanguageKey(), name);
    }
}
