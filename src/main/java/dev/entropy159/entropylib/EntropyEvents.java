package dev.entropy159.entropylib;

import dev.entropy159.entropylib.network.toClient.ClearInvisPacket;
import dev.entropy159.entropylib.registry.EntropyComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class EntropyEvents {
    @SubscribeEvent
    public static void tooltips(ItemTooltipEvent event) {
        if (event.getItemStack().getOrDefault(EntropyComponents.INFINITE, false)) {
            event.getToolTip().add(Component.literal("Infinite").withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        PacketDistributor.sendToAllPlayers(new ClearInvisPacket(event.getEntity().getId()));
    }
}
