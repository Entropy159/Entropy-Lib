package dev.entropy159.entropylib.registrate;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.entropy159.entropylib.registrate.mobeffect.MobEffectBuilder;
import dev.entropy159.entropylib.registrate.potion.PotionBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.brewing.PotionBrewEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;

public class EntropyRegistrate extends AbstractRegistrate<EntropyRegistrate> {
    private static final Logger log = LogManager.getLogger(EntropyRegistrate.class);

    private final HashMap<PotionBuilder<? extends Potion, ?>, Tuple<Holder<Potion>, Item>> potionMixes = new HashMap<>();
    private final HashMap<PotionBuilder<? extends Potion, ?>, Integer> potionColors = new HashMap<>();

    protected EntropyRegistrate(String modid) {
        super(modid);
    }

    public static @NotNull EntropyRegistrate create(String modID) {
        var ret = new EntropyRegistrate(modID);
        ret.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
        Optional<IEventBus> modEventBus = ModList.get().getModContainerById(modID).map(ModContainer::getEventBus);
        modEventBus.ifPresentOrElse(ret::registerEventListeners, () -> {
            String message = "# [EntropyRegistrate] Failed to register eventListeners for mod " + modID + ", This should be reported to this mod's dev #";
            StringBuilder hashtags = new StringBuilder().repeat("#", message.length());
            log.fatal(hashtags.toString());
            log.fatal(message);
            log.fatal(hashtags.toString());
        });
        return ret;
    }

    @Override
    public @NotNull EntropyRegistrate registerEventListeners(@NotNull IEventBus bus) {
        super.registerEventListeners(bus);
        NeoForge.EVENT_BUS.addListener(this::registerBrewingRecipes);
        NeoForge.EVENT_BUS.addListener(this::modifyBrewingOutput);
        return self();
    }

    public PotionBuilder<Potion, EntropyRegistrate> potion(String name, Supplier<MobEffectInstance[]> effects) {
        return potion(name, id -> new Potion(id.getPath(), effects.get()));
    }

    public <T extends Potion> PotionBuilder<T, EntropyRegistrate> potion(NonNullFunction<ResourceLocation, T> factory) {
        return potion(self(), factory);
    }

    public <T extends Potion> PotionBuilder<T, EntropyRegistrate> potion(String name, NonNullFunction<ResourceLocation, T> factory) {
        return potion(self(), name, factory);
    }

    public <T extends Potion, P> PotionBuilder<T, P> potion(P parent, NonNullFunction<ResourceLocation, T> factory) {
        return potion(parent, currentName(), factory);
    }

    public <T extends Potion, P> PotionBuilder<T, P> potion(P parent, String name, NonNullFunction<ResourceLocation, T> factory) {
        return entry(name, callback -> PotionBuilder.create(this, parent, name, callback, factory));
    }

    protected void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        potionMixes.forEach((potion, mix) -> event.getBuilder().addMix(mix.getA(), mix.getB(), potion.get()));
    }

    public <T extends Potion, P> void addPotionMix(PotionBuilder<T, P> builder, Holder<Potion> base, Item ingredient) {
        potionMixes.put(builder, new Tuple<>(base, ingredient));
    }

    protected void modifyBrewingOutput(PotionBrewEvent.Post event) {
        potionColors.forEach((potion, color) -> {
            for (int i = 0; i < event.getLength(); i++) {
                ItemStack stack = event.getItem(i);
                var contents = stack.get(DataComponents.POTION_CONTENTS);
                if (contents != null) {
                    if (contents.is(potion.get())) {
                        var newContents = new PotionContents(contents.potion(), Optional.of(potionColors.get(potion)), contents.customEffects());
                        stack.set(DataComponents.POTION_CONTENTS, newContents);
                    }
                }
            }
        });
    }

    public <T extends Potion, P> void addPotionColor(PotionBuilder<T, P> builder, int color) {
        potionColors.put(builder, color);
    }

    public <T extends MobEffect> MobEffectBuilder<T, EntropyRegistrate> mobEffect(NonNullSupplier<T> factory) {
        return mobEffect(self(), factory);
    }

    public <T extends MobEffect> MobEffectBuilder<T, EntropyRegistrate> mobEffect(String name, NonNullSupplier<T> factory) {
        return mobEffect(self(), name, factory);
    }

    public <T extends MobEffect, P> MobEffectBuilder<T, P> mobEffect(P parent, NonNullSupplier<T> factory) {
        return mobEffect(parent, currentName(), factory);
    }

    public <T extends MobEffect, P> MobEffectBuilder<T, P> mobEffect(P parent, String name, NonNullSupplier<T> factory) {
        return entry(name, callback -> MobEffectBuilder.create(this, parent, name, callback, id -> factory.get()));
    }

    public void configLang(ModConfigSpec.ConfigValue<?> value, String translation) {
        ModConfigSpec.ValueSpec spec = value.getSpec();
        String langKey = spec.getTranslationKey();
        if (langKey == null) {
            langKey = value.getPath().stream().reduce(getModid() + ".configuration", (a, b) -> a + "." + b);
        }
        addRawLang(langKey, translation);
        String comment = spec.getComment();
        if (comment != null) {
            addRawLang(langKey + ".tooltip", comment);
        }
    }

    public void configLang(String key, String translation) {
        addRawLang(getModid() + ".configuration." + key, translation);
    }
}
