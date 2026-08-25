package dev.entropy159.entropylib.mixin.infinite;

import dev.entropy159.entropylib.registry.EntropyComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SpawnEggItem.class)
public class InfiniteSpawnEggMixin {
    @Redirect(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void infinite(ItemStack instance, int decrement) {
        if (!instance.getOrDefault(EntropyComponents.INFINITE, false)) {
            instance.shrink(decrement);
        }
    }
}
