package dev.entropy159.entropylib.client;

import dev.entropy159.entropylib.EntropyLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.HashMap;

@Mod(value = EntropyLib.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = EntropyLib.MODID, value = Dist.CLIENT)
public class EntropyLibClient {
    public static HashMap<Integer, HashMap<ResourceLocation, Boolean>> INVIS_MAP = new HashMap<>();
    public static HashMap<Integer, Vec3> UNLERP_ENTITIES = new HashMap<>();

    public EntropyLibClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (INVIS_MAP.getOrDefault(event.getEntity().getId(), new HashMap<>()).values().stream().anyMatch(b -> b)) {
            event.setCanceled(true);
        }
    }
}
