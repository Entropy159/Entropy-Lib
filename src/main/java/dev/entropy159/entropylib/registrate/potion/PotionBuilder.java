package dev.entropy159.entropylib.registrate.potion;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.entropy159.entropylib.registrate.EntropyRegistrate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;

public class PotionBuilder<T extends Potion, P> extends AbstractBuilder<Potion, T, P, PotionBuilder<T, P>> {
    private final NonNullFunction<ResourceLocation, T> factory;
    private final ResourceLocation id;

    public PotionBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<ResourceLocation, T> factory) {
        super(owner, parent, name, callback, BuiltInRegistries.POTION.key());
        this.factory = factory;
        this.id = ResourceLocation.fromNamespaceAndPath(owner.getModid(), name);
    }

    public static <T extends Potion, P> PotionBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, NonNullFunction<ResourceLocation, T> factory) {
        return new PotionBuilder<>(owner, parent, name, callback, factory).defaultLang();
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
    protected @NotNull RegistryEntry<Potion, T> createEntryWrapper(@NotNull DeferredHolder<Potion, T> delegate) {
        return new PotionEntry<>(getOwner(), delegate);
    }

    @Override
    public @NotNull PotionEntry<T> register() {
        return (PotionEntry<T>) super.register();
    }

    public PotionBuilder<T, P> defaultLang() {
        lang((p, c) -> p.getAutomaticName(c, getRegistryKey()));
        return this;
    }

    public PotionBuilder<T, P> lang(String name) {
        lang((p, c) -> name);
        return this;
    }

    private void lang(NonNullBiFunction<RegistrateLangProvider, NonNullSupplier<T>, String> name) {
        setData(ProviderType.LANG, (ctx, prov) -> {
            prov.add("item.minecraft.potion.effect." + id.getPath(), "Potion of " + name.apply(prov, ctx::get));
            prov.add("item.minecraft.splash_potion.effect." + id.getPath(), "Splash Potion of " + name.apply(prov, ctx::get));
            prov.add("item.minecraft.lingering_potion.effect." + id.getPath(), "Lingering Potion of " + name.apply(prov, ctx::get));
            prov.add("item.minecraft.tipped_arrow.effect." + id.getPath(), "Arrow of " + name.apply(prov, ctx::get));
        });
    }

    public PotionBuilder<T, P> recipe(Holder<Potion> base, Item ingredient) {
        getOwner().addPotionMix(this, base, ingredient);
        return this;
    }

    public PotionBuilder<T, P> recipe(Item ingredient) {
        return recipe(Potions.AWKWARD, ingredient);
    }

    public PotionBuilder<T, P> color(int color) {
        getOwner().addPotionColor(this, color);
        return this;
    }
}
