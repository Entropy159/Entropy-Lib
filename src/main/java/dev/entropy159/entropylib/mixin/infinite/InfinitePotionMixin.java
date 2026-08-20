package dev.entropy159.entropylib.mixin.infinite;

import com.llamalad7.mixinextras.sugar.Local;
import dev.entropy159.entropylib.registry.EntropyComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PotionItem.class)
public class InfinitePotionMixin {
    @Redirect(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;hasInfiniteMaterials()Z"))
    private boolean noGlassBottle(Player instance, @Local(argsOnly = true) ItemStack stack) {
        return instance.hasInfiniteMaterials() || stack.getOrDefault(EntropyComponents.INFINITE, false);
    }
}
