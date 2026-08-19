package dev.entropy159.entropylib;

import com.mojang.logging.LogUtils;
import dev.entropy159.entropylib.commands.InfiniteCommand;
import dev.entropy159.entropylib.commands.UnbreakableCommand;
import dev.entropy159.entropylib.config.ClientConfig;
import dev.entropy159.entropylib.mixininterfaces.ConfigValueAddon;
import dev.entropy159.entropylib.registry.EntropyComponents;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

@Mod(EntropyLib.MODID)
public class EntropyLib {
    public static final String MODID = "entropylib";
    public static final Logger LOGGER = LogUtils.getLogger();

    public EntropyLib(IEventBus bus, ModContainer container) {
        NeoForge.EVENT_BUS.register(this);
        bus.addListener(this::setupConfigs);

        EntropyComponents.init(bus);

        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        UnbreakableCommand.register(dispatcher);
        InfiniteCommand.register(dispatcher);
    }

    public void setupConfigs(FMLLoadCompleteEvent event) {
        ModList.get().forEachModContainer((id, container) -> ModConfigs.getModConfigs(id).forEach(config -> {
            if (config.getSpec() instanceof ModConfigSpec spec) {
                spec.getValues().entrySet().forEach(entry -> {
                    if (entry.getRawValue() instanceof ConfigValueAddon<?> addon) {
                        addon.entropylib$setModID(id);
                    }
                });
            }
        }));
    }
}
