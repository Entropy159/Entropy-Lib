package dev.entropy159.entropylib.client;

import dev.entropy159.entropylib.EntropyLib;
import dev.entropy159.entropylib.client.util.render.FBOManager;
import dev.entropy159.entropylib.client.util.render.RenderTypeUtil;
import dev.entropy159.entropylib.util.InvisEffect;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.HashMap;

@Mod(value = EntropyLib.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = EntropyLib.MODID, value = Dist.CLIENT)
public class EntropyLibClient {
    public static HashMap<Integer, Vec3> UNLERP_ENTITIES = new HashMap<>();

    public EntropyLibClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void clientSetup(FMLLoadCompleteEvent event) {
        event.enqueueWork(FBOManager::init);
    }

    @SubscribeEvent
    static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (event.getEntity().getActiveEffects().stream().anyMatch(effect -> effect.getEffect().value() instanceof InvisEffect invis && invis.isFull())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    static void renderTypes(RegisterShadersEvent event) {
        RenderTypeUtil.register(event);
    }

    @SubscribeEvent
    static void renderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_SKY) {
            FBOManager.renderAll(event.getPartialTick().getGameTimeDeltaPartialTick(true));
        }
    }
}
