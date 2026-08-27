package dev.entropy159.entropylib.mixin;

import dev.entropy159.entropylib.config.ServerConfig;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantCommand.class)
public class EnchantCommandMixin {
    @Redirect(method = "enchant", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;getMaxLevel()I"))
    private static int uncap(Enchantment instance) {
        return ServerConfig.UNCAP_ENCHANT_COMMAND.get() ? 255 : instance.getMaxLevel();
    }
}
